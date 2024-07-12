package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.AdminReturn;
import com.example.finalprojectthirdphase.dto.AdminSaveRequest;
import com.example.finalprojectthirdphase.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {

    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    Admin adminSaveRequestToModel(AdminSaveRequest adminSaveRequest);

    AdminReturn modelAdminToSaveResponse(Admin admin);
}
