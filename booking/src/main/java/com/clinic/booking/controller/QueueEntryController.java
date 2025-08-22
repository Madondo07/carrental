package com.clinic.booking.controller;

import com.clinic.booking.entity.QueueEntry;
import com.clinic.booking.repository.QueueEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "*")
public class QueueEntryController {
    @Autowired
    private QueueEntryRepository queueEntryRepository;

    @GetMapping
    public List<QueueEntry> getAllQueueEntries() {
        return queueEntryRepository.findAll();
    }

    @PostMapping
    public QueueEntry addQueueEntry(@RequestBody QueueEntry entry) {
        entry.setId(null); // Ensure new entry
        return queueEntryRepository.save(entry);
    }

    @PutMapping("/{id}")
    public QueueEntry updateQueueEntry(@PathVariable Long id, @RequestBody QueueEntry entry) {
        Optional<QueueEntry> existing = queueEntryRepository.findById(id);
        if (existing.isPresent()) {
            QueueEntry q = existing.get();
            q.setName(entry.getName());
            q.setStudentId(entry.getStudentId());
            q.setEmail(entry.getEmail());
            q.setPhone(entry.getPhone());
            q.setResidence(entry.getResidence());
            q.setCondition(entry.getCondition());
            q.setStatus(entry.getStatus());
            return queueEntryRepository.save(q);
        } else {
            throw new RuntimeException("Queue entry not found");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteQueueEntry(@PathVariable Long id) {
        queueEntryRepository.deleteById(id);
    }
}
