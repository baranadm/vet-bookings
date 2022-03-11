package pl.baranowski.dev.model;

import pl.baranowski.dev.entity.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AvailableSlotsFinder {
    private final List<Doctor> doctors;
    private final EpochFutureTimeRange timeRange;

    public AvailableSlotsFinder(List<Doctor> doctors, EpochFutureTimeRange timeRange) {
        this.doctors = doctors;
        this.timeRange = timeRange;
    }

    public List<DoctorsFreeSlots> find() {
        List<DoctorsFreeSlots> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            List<Long> availableSlots = freeSlotsForDoctorBetween(doctor, timeRange);
            if (availableSlots.size() > 0) {
                DoctorsFreeSlots doctorsFreeSlots = new DoctorsFreeSlots(doctor, availableSlots);
                result.add(doctorsFreeSlots);
            }
        }
        return result;
    }

    private List<Long> freeSlotsForDoctorBetween(Doctor doctor, EpochFutureTimeRange timeRange) {
        List<Long> topHours = topHoursBetween(timeRange);

        List<Long> result = topHours.stream()
                                    .filter(hour -> doctor.isAvailableAt(hour)) // checks, if doctor is free at the beginning of visit
                                    .filter(hour -> doctor.isAvailableAt(hour + 3600 - 1)) // checks, if doctor is free at the ending of visit (inclusive)
                                    .collect(Collectors.toList());

        return result;
    }

    List<Long> topHoursBetween(EpochFutureTimeRange timeRange) {
        long oneHourInSeconds = 3600;
        long secondsToNextTopHour = (timeRange.getStartInSeconds() % oneHourInSeconds);
        long topHour = (secondsToNextTopHour == 0) ? timeRange.getStartInSeconds() : timeRange.getStartInSeconds() - secondsToNextTopHour + oneHourInSeconds;

        List<Long> result = new ArrayList<>();
        for (; topHour <= timeRange.getEndInSeconds(); topHour += oneHourInSeconds) {
            result.add(topHour);
        }

        return result;
    }


}
