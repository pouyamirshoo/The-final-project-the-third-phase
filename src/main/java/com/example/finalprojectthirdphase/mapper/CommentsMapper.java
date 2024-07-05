package com.example.finalprojectthirdphase.mapper;

import com.example.finalprojectthirdphase.dto.CommentsReturn;
import com.example.finalprojectthirdphase.dto.CommentsSaveRequest;
import com.example.finalprojectthirdphase.entity.Comments;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = OrderMapper.class)
public interface CommentsMapper {

    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);

    Comments commentsSaveRequestToModel(CommentsSaveRequest request);

    CommentsReturn modelCommentsToSaveResponse(Comments comments);
}
