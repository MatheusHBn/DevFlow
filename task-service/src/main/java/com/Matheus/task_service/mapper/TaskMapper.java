package com.Matheus.task_service.mapper;

import com.Matheus.task_service.domain.Task;
import com.Matheus.task_service.dto.TaskRequest;
import com.Matheus.task_service.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TaskRequest request, @MappingTarget Task task);

    Task toEntity(TaskRequest request);

    TaskResponse toResponse(Task task);
    List<TaskResponse> toResponseList(List<Task> tasks);

}
