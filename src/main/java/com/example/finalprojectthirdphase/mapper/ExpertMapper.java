package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.ExpertReturn;
import com.example.finalprojectthirdphase.dto.ExpertSaveRequest;
import com.example.finalprojectthirdphase.entity.Expert;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ExpertMapper {

    ExpertMapper INSTANCE = Mappers.getMapper(ExpertMapper.class);

    Expert expertSaveRequestToModel(ExpertSaveRequest expertSaveRequest);

    ExpertReturn modelExpertToSaveResponse(Expert expert);

    List<ExpertReturn> listExpertToSaveResponse(List<Expert> experts);

}
