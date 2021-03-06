package io.stream.com.mappers;

import java.util.Date;

import io.stream.com.models.Comment;
import io.stream.com.models.Movie;
import io.stream.com.models.User;
import io.stream.com.models.dtos.CommentDto;

public class CommentMapper {

    public static Comment map(Movie movie, CommentDto commentDto, User user){ 
        return Comment.builder()
            .body(commentDto.getBody())
            .movie(movie)
            .date(new Date())
            .user(user)
            .build(); 
    }

}
