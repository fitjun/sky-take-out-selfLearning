package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            map.put("dayEnd",dayEnd);
            Integer AllUser = userMapper.CountUser(map);
            map.put("dayStart",dayStart);
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
        map.put("beginTime",beginTime);
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
}
