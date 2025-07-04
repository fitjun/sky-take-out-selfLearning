package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.properties.ShopProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.DistanceUtil;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderStatistusVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShopProperties shopProperties;
    @Autowired
    private WebSocketServer webSocketServer;
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO order) {
        //异常判断 购物车为空、地址为空
        AddressBook addressBook = new AddressBook();
        addressBook.setId(order.getAddressBookId());
        List<AddressBook> addressBook1 = addressBookMapper.findAddressBook(addressBook);
        if (addressBook1 == null || addressBook1.isEmpty()) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> shoppingCart1 = shoppingCartMapper.findShoppingCart(shoppingCart);
        if (shoppingCart1 == null || shoppingCart1.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //取出数据，创建订单对象、订单细节对象
        addressBook=addressBook1.get(0);


        //判断是否在店铺五公里内
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        String address1 = shopProperties.getAddress();
        String ak = shopProperties.getAK();
        log.info("地址：{}，ak:{}",address,ak);
        DistanceUtil distanceUtil = new DistanceUtil();
        try {
            boolean within5Km = distanceUtil.isWithin5Km(ak, address1, address);
            if(!within5Km){
                throw new AddressBookBusinessException("地址与商家距离过远，请选择其他店铺");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Orders o = new Orders();
        BeanUtils.copyProperties(order, o);
        o.setNumber(String.valueOf(System.currentTimeMillis()));
        o.setStatus(Orders.PENDING_PAYMENT);
        o.setUserId(currentId);
        o.setOrderTime(LocalDateTime.now());
        o.setPayStatus(Orders.UN_PAID);
        o.setPhone(addressBook.getPhone());
        o.setConsignee(addressBook.getConsignee());
        o.setAddress(address);
        orderMapper.insert(o);
        List<OrderDetail> ol = new ArrayList<>();
        for (ShoppingCart sc : shoppingCart1) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(sc, orderDetail);
            orderDetail.setOrderId(o.getId());
            ol.add(orderDetail);
        }
        orderMapper.insertOrderDetail(ol);
        //成功后删除购物车
        shoppingCartMapper.clean(currentId);
        OrderSubmitVO  orderSubmitVO = OrderSubmitVO.builder()
                .id(o.getId())
                .orderNumber(o.getNumber())
                .orderTime(o.getOrderTime())
                .orderAmount(o.getAmount())
                .build();
        return orderSubmitVO;
    }

    @Override
    public LocalDateTime pay(OrdersPaymentDTO ordersDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);
        orders.setNumber(ordersDTO.getOrderNumber());
        orders.setPayStatus(Orders.PAID);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayMethod(ordersDTO.getPayMethod());
        orders.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(orders);
        Orders o = orderMapper.findOrderByIdOrNumber(null, orders.getNumber());
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",o.getId());
        map.put("content","订单号:"+o.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        return LocalDateTime.now().plusMinutes(30);
    }

    @Override
    public PageResult OrderHistory(Integer page, Integer pageSize, Integer status) {
        Long currentId = BaseContext.getCurrentId();
        PageHelper.startPage(page,pageSize);
        OrdersConditionSearchDTO o = new OrdersConditionSearchDTO();
        o.setUserId(currentId);
        o.setStatus(status);
        Page<OrderVO> orders = orderMapper.findOrder(o);
        List<OrderVO> orderlist = orders.getResult();
        orderlist.forEach(orderVO -> {
            List<OrderDetail> details = orderMapper.findOrderDetailByOrderId(orderVO.getId());
            orderVO.setOrderDetailList(details);
        });
        return new PageResult(orders.getTotal(),orders.getResult());
    }

    @Override
    public OrderDetailVO orderDetail(Long id) {
        Orders orders = orderMapper.findOrderByIdOrNumber(id,null);
        orders.setUserId(BaseContext.getCurrentId());
        List<OrderDetail> details = orderMapper.findOrderDetailByOrderId(id);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(orders,orderDetailVO);
        orderDetailVO.setOrderDetailList(details);
        return orderDetailVO;
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.cancel(orders);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> details = orderMapper.findOrderDetailByOrderId(id);
        //菜品重新加入购物车，旧订单不用管、让其生成新订单，旧的留着做订单历史
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        details.forEach(orderDetail -> {
            ShoppingCart sc = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,sc);
            sc.setCreateTime(LocalDateTime.now());
            sc.setUserId(BaseContext.getCurrentId());
            shoppingCarts.add(sc);
        });
        shoppingCartMapper.addAll(shoppingCarts);
    }

    @Override
    public PageResult ConditionSearch(Integer page, Integer pageSize, LocalDateTime beginTime, LocalDateTime endTime, String number, String phone, Integer status) {
        PageHelper.startPage(page,pageSize);
        OrdersConditionSearchDTO orders = new OrdersConditionSearchDTO();
        orders.setBeginTime(beginTime);
        orders.setEndTime(endTime);
        orders.setPhone(phone);
        orders.setNumber(number);
        orders.setStatus(status);
        Page<OrderVO> order = orderMapper.findOrder(orders);
        return new PageResult(order.getTotal(),order.getResult());
    }

    @Override
    public OrderStatistusVO orderStatics() {
        List<OrderStatusDTO> orderStatusDTO = orderMapper.StaticCount();
        OrderStatistusVO statistusVO = new OrderStatistusVO();
        orderStatusDTO.forEach(o->{
            if(Integer.valueOf(o.getStatus())==2){
                statistusVO.setToBeConfirmed(o.getNum());
            } else if (Integer.valueOf(o.getStatus())==3) {
                statistusVO.setConfirmed(o.getNum());
            } else if (Integer.valueOf(o.getStatus())==4) {
                statistusVO.setDeliveryInProgress(o.getNum());
            }
        });
        return statistusVO;
    }

    @Override
    public void confirm(Long id) {
        Orders o = new Orders();
        o.setId(id);
        o.setStatus(Orders.CONFIRMED);
        orderMapper.update(o);
    }

    @Override
    public void rejection(OrdersRejectionDTO rejectionDTO) {
        Orders o = new Orders();
        o.setId(rejectionDTO.getId());
        o.setRejectionReason(rejectionDTO.getRejectionReason());
        o.setStatus(7);
        orderMapper.update(o);
    }

    @Override
    public void delivery(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders o = new Orders();
        o.setId(id);
        o.setStatus(Orders.COMPLETED);
        orderMapper.update(o);
    }

    @Override
    public void reminder(Long id) {
        Orders o = orderMapper.findOrderByIdOrNumber(id,null);
        if(o==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号:"+o.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
}
