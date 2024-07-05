package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.OrderReturn;
import com.example.finalprojectthirdphase.dto.OrderSaveRequest;
import com.example.finalprojectthirdphase.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {CustomerMapper.class, SubDutyMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);


    Order orderSaveRequestToModel(OrderSaveRequest request);

    OrderReturn modelOrderToSaveResponse(Order order);

    List<OrderReturn> listOrderToSaveResponse(List<Order> orders);
}
