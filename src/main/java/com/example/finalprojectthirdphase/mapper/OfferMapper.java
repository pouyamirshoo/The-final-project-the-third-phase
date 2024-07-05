package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.OfferReturn;
import com.example.finalprojectthirdphase.dto.OfferSaveRequest;
import com.example.finalprojectthirdphase.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ExpertMapper.class, OrderMapper.class})
public interface OfferMapper {

    OfferMapper INSTANCE = Mappers.getMapper(OfferMapper.class);

    Offer offerSaveRequestToModel(OfferSaveRequest request);

    OfferReturn modelOfferToSaveResponse(Offer offer);

    List<OfferReturn> listOfferToSaveResponse(List<Offer> offers);
}
