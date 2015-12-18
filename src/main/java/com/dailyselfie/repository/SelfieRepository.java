package com.dailyselfie.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface SelfieRepository extends CrudRepository<SelfieRecord, Long> {

    Collection<SelfieRecord> findByName(String name);

}