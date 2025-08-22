package com.clinic.booking.repository;

import com.clinic.booking.entity.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
}
