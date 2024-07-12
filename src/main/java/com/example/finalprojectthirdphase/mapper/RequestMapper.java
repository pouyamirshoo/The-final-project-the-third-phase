package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.RequestReturn;
import com.example.finalprojectthirdphase.dto.RequestSaveRequest;
import com.example.finalprojectthirdphase.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SubDutyMapper.class, ExpertMapper.class})
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    Request requestSaveRequestToModel(RequestSaveRequest request);

    RequestReturn modelRequestToSaveResponse(Request request);
}
