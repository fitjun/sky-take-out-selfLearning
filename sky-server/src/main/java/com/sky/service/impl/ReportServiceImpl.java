package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private WorkSpaceService workSpaceService;
    @Override
    @Transactional
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> days = new ArrayList<>();
        List<Double> turnovers = new ArrayList<>();
        days.add(begin);
        while (!begin.equals(end)){
            //计算其中有哪天及每一天营业额
            begin = begin.plusDays(1);
            days.add(begin);
        }
        days.forEach(day->{
            LocalDateTime DayStart = LocalDateTime.of(day,LocalTime.MIN);
            LocalDateTime DayEnd = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("start",DayStart);
            map.put("end",DayEnd);
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.sumTurnover(map);
            turnover = turnover==null?0.0:turnover;
            turnovers.add(turnover);
        });
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(StringUtils.join(days,","));
        turnoverReportVO.setTurnoverList(StringUtils.join(turnovers,","));
        return turnoverReportVO;
    }

    @Override
    @Transactional
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> days = new ArrayList<>();
        List<Integer> all = new ArrayList<>();
        List<Integer> newUser = new ArrayList<>();
        days.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            days.add(begin);
        }
        days.forEach(day->{
            LocalDateTime dayStart = LocalDateTime.of(day,LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime",dayEnd);
            Integer AllUser = userMapper.CountUser(map);
            map.put("startTime",dayStart);
            Integer dayUser = userMapper.CountUser(map);
            all.add(AllUser);
            newUser.add(dayUser);
        });
        return UserReportVO.builder()
                .dateList(StringUtils.join(days,","))
                .newUserList(StringUtils.join(newUser,","))
                .totalUserList(StringUtils.join(all,","))
                .build();
    }

    @Override
    @Transactional
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //LocalDate不要时分秒，要时分秒就是LocalDateTime
        List<LocalDate> days = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();
        List<Integer> validOrderCounts = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount=0;
        Double orderCompletionRate = 0.0;
        days.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            days.add(begin);
        }
        for (int i = 0; i < days.size(); i++) {
            LocalDate day = days.get(i);
            LocalDateTime startTime = LocalDateTime.of(day,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime",endTime);
            map.put("startTime",startTime);
            //总数不传状态，有效订单再传
            Integer Daytotal = orderMapper.countOrders(map);
            orderCounts.add(Daytotal);
            map.put("status",5);
            Integer DayValid = orderMapper.countOrders(map);
            validOrderCounts.add(DayValid);
            totalOrderCount+=Daytotal;
            validOrderCount+=DayValid;
        }
        //除0错误处理
        if (validOrderCount!=0){
            orderCompletionRate = validOrderCount.doubleValue()/totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(days,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCountList(StringUtils.join(orderCounts,","))
                .validOrderCountList(StringUtils.join(validOrderCounts,","))
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    @Transactional
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);
        Map map = new HashMap();
        map.put("startTime",beginTime);
        map.put("endTime",endTime);
        List<GoodsSalesDTO> salesDTOS = orderMapper.findGoodSales(map);
        for (GoodsSalesDTO salesDTO : salesDTOS) {
            nameList.add(salesDTO.getName());
            numberList.add(salesDTO.getNumber());
        }
//        while (nameList.size()<10) {
//            nameList.add(null);
//            numberList.add(null);
//        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    @Override
    public void export(HttpServletResponse response) {
        LocalDate beginDay = LocalDate.now().minusDays(30);
        LocalDate endDay = LocalDate.now().minusDays(1);
        //方法需要精确到时分秒,所以做一次转换  概览数据
        LocalDateTime start = LocalDateTime.of(beginDay,LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDay,LocalTime.MAX);
        BusinessDataVO businessDataVO = workSpaceService.businessData(start,end);
        //读取报表模板
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间:"+beginDay+"至"+endDay);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            int startRow = 7;
            BusinessDataVO businessDataVO1;
            //明细数据，查询每天的数据即可,还是哪个方法
            while (!beginDay.equals(endDay.plusDays(1))){
                LocalDateTime startTime = LocalDateTime.of(beginDay,LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.of(beginDay,LocalTime.MAX);
                businessDataVO1 = workSpaceService.businessData(startTime, endTime);
                row = sheet.getRow(startRow);
                row.getCell(1).setCellValue(beginDay.toString());
                row.getCell(2).setCellValue(businessDataVO1.getTurnover());
                row.getCell(3).setCellValue(businessDataVO1.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO1.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO1.getNewUsers());
                beginDay = beginDay.plusDays(1);
                startRow+=1;
            }
            //通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
            excel.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
