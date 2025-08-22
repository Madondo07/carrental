package com.clinic.booking.controller;

import com.clinic.booking.entity.Patient;
import com.clinic.booking.repository.PatientRepository;
import com.clinic.booking.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        // Save patient first
        Patient saved = patientRepository.save(patient);
        // Send email receipt if email is present and is a CPUT org address
        if (saved.getEmail() != null && saved.getEmail().endsWith("@mycput.ac.za")) {
            // Try to extract studentId from email if not present
            String studentId = null;
            if (saved.getEmail().matches("^\\d{9}@mycput.ac.za$")) {
                studentId = saved.getEmail().substring(0, 9);
            } else if (saved.getClass().getDeclaredFields() != null) {
                try {
                    java.lang.reflect.Field f = saved.getClass().getDeclaredField("studentId");
                    f.setAccessible(true);
                    Object val = f.get(saved);
                    if (val != null)
                        studentId = val.toString();
                } catch (Exception ignore) {
                }
            }
            emailService.sendAppointmentReceipt(
                    saved.getEmail(),
                    saved.getName(),
                    studentId != null ? studentId : "N/A",
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getStatus() != null ? saved.getStatus() : "Waiting");
        }
        return saved;
    }

    // Update patient (e.g., status or other fields)
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        return patientRepository.findById(id).map(patient -> {
            patient.setName(updatedPatient.getName());
            patient.setEmail(updatedPatient.getEmail());
            patient.setPhone(updatedPatient.getPhone());
            patient.setStatus(updatedPatient.getStatus());
            return patientRepository.save(patient);
        }).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    // Delete patient
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
    }
}
