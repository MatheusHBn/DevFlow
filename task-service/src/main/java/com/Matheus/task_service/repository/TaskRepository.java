package com.Matheus.task_service.repository;

import com.Matheus.task_service.domain.StatusTask;
import com.Matheus.task_service.domain.Task;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(StatusTask status);
}
