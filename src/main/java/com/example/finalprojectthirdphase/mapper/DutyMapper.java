package com.example.finalprojectthirdphase.mapper;


import com.example.finalprojectthirdphase.dto.DutySaveRequest;
import com.example.finalprojectthirdphase.dto.DutyReturn;
import com.example.finalprojectthirdphase.entity.Duty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DutyMapper {

    DutyMapper INSTANCE = Mappers.getMapper(DutyMapper.class);

    Duty dutySaveRequestToModel(DutySaveRequest dutySaveRequest);

    DutyReturn modelDutyToSaveResponse(Duty duty);

    List<DutyReturn> listDutyToSaveResponse(List<Duty> duties);
}
