package pl.baranowski.dev.model;

import org.junit.jupiter.api.Test;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class EpochFutureTimeRangeTest {

    @Test
    void new_whenValidInput_shouldCreate() throws InvalidEpochTimeException {
        //given
        long start = LocalDateTime.of(2030, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
        //then
        assertNotNull(timeRange);
    }

    @Test
    void new_whenStartBeforeNow_shouldThrow() {
        //given
        long start = LocalDateTime.of(2020, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        //then
        assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end));
    }

    @Test
    void new_whenStartAfterEnd_shouldThrow() {
        //given
        long start = LocalDateTime.of(2040, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        //then
        assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end));
    }

    @Test
    void new_whenStartEqualsEnd_shouldThrow() {
        //given
        long start = LocalDateTime.of(2040, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        //then
        assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, start));
    }

    @Test
    void new_whenStartIsNotInSeconds_shouldThrow() {
        //given
        long start = LocalDateTime.of(2025, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC) * 1000;
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        //then
        assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end));
    }

    @Test
    void new_whenEndIsNotInSeconds_shouldThrow() {
        //given
        long start = LocalDateTime.of(2025, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC) * 1000;
        //when
        //then
        assertThrows(InvalidEpochTimeException.class, () -> new EpochFutureTimeRange(start, end));
    }

    @Test
    void getStartInSeconds() throws InvalidEpochTimeException {
        //given
        long start = LocalDateTime.of(2030, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
        //then
        assertEquals(start, timeRange.getStartInSeconds());
    }

    @Test
    void getEndInSeconds() throws InvalidEpochTimeException {
        //given
        long start = LocalDateTime.of(2030, 1, 1, 12, 0,0).toEpochSecond(ZoneOffset.UTC);
        long end = LocalDateTime.of(2030, 1, 1, 13, 0,0).toEpochSecond(ZoneOffset.UTC);
        //when
        EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
        //then
        assertEquals(end, timeRange.getEndInSeconds());
    }
}