package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.SubDutyReturn;
import com.example.finalprojectthirdphase.dto.SubDutySaveRequest;
import com.example.finalprojectthirdphase.entity.SubDuty;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = DutyMapper.class)
public interface SubDutyMapper {

    SubDutyMapper INSTANCE = Mappers.getMapper(SubDutyMapper.class);

    SubDuty subDutySaveRequestToModel(SubDutySaveRequest SubDutySaveRequest);

    SubDutyReturn modelSubDutyToSaveResponse(SubDuty subDuty);

    List<SubDutyReturn> listSubDutyToSaveResponse(List<SubDuty> subDuties);
}
