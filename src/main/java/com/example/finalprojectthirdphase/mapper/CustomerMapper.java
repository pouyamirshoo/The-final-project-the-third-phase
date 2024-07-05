package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.CustomerSaveRequest;
import com.example.finalprojectthirdphase.dto.CustomerReturn;
import com.example.finalprojectthirdphase.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer customerSaveRequestToModel(CustomerSaveRequest customerSaveRequest);

    CustomerReturn modelCustomerToSaveResponse(Customer customer);

    List<CustomerReturn> listCustomerToSaveResponse(List<Customer> customers);
}
