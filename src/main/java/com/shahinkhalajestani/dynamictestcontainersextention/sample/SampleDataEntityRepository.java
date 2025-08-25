package com.shahinkhalajestani.dynamictestcontainersextention.sample;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleDataEntityRepository extends JpaRepository<SampleDataEntity, Long> {
    // Additional query methods can be defined here if needed
}

