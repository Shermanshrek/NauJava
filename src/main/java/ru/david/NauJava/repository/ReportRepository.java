package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.david.NauJava.entity.Report;

public interface ReportRepository extends CrudRepository<Report, Long> {
}