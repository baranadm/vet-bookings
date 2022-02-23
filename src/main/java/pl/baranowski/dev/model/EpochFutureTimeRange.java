package pl.baranowski.dev.model;

import pl.baranowski.dev.exception.InvalidEpochTimeException;

public class EpochFutureTimeRange {

	private final Long startInSeconds;
	private final Long endInSeconds;
	
	public EpochFutureTimeRange(Long startInSeconds, Long endInSeconds) throws InvalidEpochTimeException {
		this.startInSeconds = startInSeconds;
		this.endInSeconds = endInSeconds;
		validate();
	}
	
	// TODO test
	public static EpochFutureTimeRange fromStrings(String startInSeconds, String endInSeconds) throws InvalidEpochTimeException {
		long start, end;
		try {
			start = Long.decode(startInSeconds);
			end = Long.decode(endInSeconds);
		} catch (Exception e) {
			throw new InvalidEpochTimeException("EpochTime values should contain olny digits.");
		}
		EpochFutureTimeRange timeRange = new EpochFutureTimeRange(start, end);
		return timeRange;
	}

	private void validate() throws InvalidEpochTimeException {
		throwIfEpochsAreNotInSeconds();
		throwIfStartBeforeNow();
		throwIfStartAfterEnd();
	}

	private void throwIfEpochsAreNotInSeconds() throws InvalidEpochTimeException {
		if(!isInSeconds(startInSeconds)) {
			throw new InvalidEpochTimeException("Epoch start time has to be in seconds.");
		}
		if(!isInSeconds(endInSeconds)) {
			throw new InvalidEpochTimeException("Epoch end time has to be in seconds.");
		}
	}

	private boolean isInSeconds(Long epoch) {
		return epoch.toString().length() == 10;
	}

	private void throwIfStartBeforeNow() throws InvalidEpochTimeException {
		if(startInSeconds < System.currentTimeMillis()/1000) {
			throw new InvalidEpochTimeException("Searching request not valid: epoch start should be later than now.");
		}
	}

	private void throwIfStartAfterEnd() throws InvalidEpochTimeException {
		if(startInSeconds >= endInSeconds) {
			throw new InvalidEpochTimeException("Searching request not valid: epoch start should be less than epoch end.");
		}
	}

	public Long getStartInSeconds() {
		return startInSeconds;
	}

	public Long getEndInSeconds() {
		return endInSeconds;
	}

	
}
