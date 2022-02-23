package pl.baranowski.dev.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import pl.baranowski.dev.exception.InvalidEpochTimeException;
import pl.baranowski.dev.model.EpochFutureTimeRange;

class VisitServiceUnitTest {

	long mondayH10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault()).toEpochSecond();
	
	private VisitService underTest;
	
	public VisitServiceUnitTest() {
		underTest = new VisitService();
	}
	
	@Test
	void topHoursBetween_validInput() throws InvalidEpochTimeException {
		long start = mondayH10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = mondayH10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
		assertEquals(23, underTest.topHoursBetween(timeRange).size());
	}
	
	@Test
	void topHoursBetween_startAfterEnd_shouldThrow() throws InvalidEpochTimeException {
		long start = mondayH10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = mondayH10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(end, start));
	}
	
	@Test
	void topHoursBetween_startNotInSeconds_shouldThrow() throws InvalidEpochTimeException {
		long start = mondayH10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = mondayH10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start*10, end));
	}
	
	@Test
	void topHoursBetween_endNotInSeconds_shouldThrow() throws InvalidEpochTimeException {
		long start = mondayH10Y2100 + 30*60; // 2100-01-25 10:30:00;
		long end = mondayH10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end*10));
	}
	
	@Test
	void topHoursBetween_startBeforeNow_shouldThrow() throws InvalidEpochTimeException {
		long now = System.currentTimeMillis() / 1000;
		long end = mondayH10Y2100 + 24*60*60 - 30*60; // 2100-01-26 9:30:00;
		assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(now - 1, end));
	}

}
