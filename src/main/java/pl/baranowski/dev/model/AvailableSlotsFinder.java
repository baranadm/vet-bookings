package pl.baranowski.dev.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.baranowski.dev.entity.Doctor;

public class AvailableSlotsFinder {
	private final List<Doctor> doctors;
	private final EpochFutureTimeRange timeRange;
	
	public AvailableSlotsFinder(List<Doctor> doctors, EpochFutureTimeRange timeRange) {
		this.doctors = doctors;
		this.timeRange = timeRange;
	}
	
	public List<AvailableSlotsAtTheDoctor> find() {
		List<AvailableSlotsAtTheDoctor> result = new ArrayList<>();
		for(Doctor doctor: doctors) {
			AvailableSlotsAtTheDoctor availableSlotsAtTheDoctor = findAvailableSpotsAtTheDoctor(doctor);
			result.add(availableSlotsAtTheDoctor);
		}
		return result;
	}

	private AvailableSlotsAtTheDoctor findAvailableSpotsAtTheDoctor(Doctor doctor) {
		List<Long> availableSlots = findDoctorsAvailableTimesBetweenTimeRange(doctor, timeRange);
		AvailableSlotsAtTheDoctor availableSlotsAtTheDoctor = new AvailableSlotsAtTheDoctor(doctor, availableSlots);
		return availableSlotsAtTheDoctor;
	}

	private List<Long> findDoctorsAvailableTimesBetweenTimeRange(Doctor doctor, EpochFutureTimeRange timeRange) {
		List<Long> topHours = topHoursBetween(timeRange);
		
		List<Long> result = topHours.stream()
				.filter(hour -> doctor.isAvailableAt(hour)) // checks only beginning of visit
				.collect(Collectors.toList());
		
		return result;
	}

	List<Long> topHoursBetween(EpochFutureTimeRange timeRange) {
		long oneHourInSeconds = 3600;
		long secondsToNextTopHour = (timeRange.getStartInSeconds() % oneHourInSeconds);
		long topHour = (secondsToNextTopHour == 0) ? timeRange.getStartInSeconds() : timeRange.getStartInSeconds() - secondsToNextTopHour + oneHourInSeconds;
		
		List<Long> result = new ArrayList<>();
		for(; topHour <= timeRange.getEndInSeconds(); topHour += oneHourInSeconds) {
			result.add(topHour);
		}
		
		return result;
	}
	

	
}
