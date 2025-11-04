package ru.david.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.david.NauJava.entity.Transfer;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "transfers")
public interface TransferRepository extends CrudRepository<Transfer, Long> {
    List<Transfer> findByDate(LocalDateTime date);
}
