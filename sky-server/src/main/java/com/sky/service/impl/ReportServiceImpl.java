package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Override
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
}
