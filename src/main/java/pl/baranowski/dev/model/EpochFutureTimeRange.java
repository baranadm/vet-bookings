package pl.baranowski.dev.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.baranowski.dev.exception.epoch.InvalidEpochTimeException;

public class EpochFutureTimeRange {
	private static final Logger LOGGER = LoggerFactory.getLogger(EpochFutureTimeRange.class);
    private final Long startInSeconds;
    private final Long endInSeconds;

    public EpochFutureTimeRange(Long startInSeconds, Long endInSeconds) throws InvalidEpochTimeException {
        LOGGER.debug("Trying to create TimeRange for values: startInSeconds='{}', endInSeconds='{}'", startInSeconds, endInSeconds);
		this.startInSeconds = startInSeconds;
        this.endInSeconds = endInSeconds;
        validate();
		LOGGER.debug("Validated successfully, creating...");
    }

    private void validate() throws InvalidEpochTimeException {
        throwIfEpochsAreNotInSeconds();
        throwIfStartBeforeNow();
        throwIfStartAfterEnd();
    }

    private void throwIfEpochsAreNotInSeconds() throws InvalidEpochTimeException {
        if (!isInSeconds(startInSeconds)) {
            throw new InvalidEpochTimeException("Epoch start time has to be in seconds.");
        }
        if (!isInSeconds(endInSeconds)) {
            throw new InvalidEpochTimeException("Epoch end time has to be in seconds.");
        }
    }

    private boolean isInSeconds(Long epoch) {
        return epoch.toString().length() == 10;
    }

    private void throwIfStartBeforeNow() throws InvalidEpochTimeException {
        if (startInSeconds < System.currentTimeMillis() / 1000) {
            throw new InvalidEpochTimeException("Searching request not valid: epoch start should be later than now.");
        }
    }

    private void throwIfStartAfterEnd() throws InvalidEpochTimeException {
        if (startInSeconds >= endInSeconds) {
            throw new InvalidEpochTimeException(
                    "Searching request not valid: epoch start should be less than epoch end.");
        }
    }

    public Long getStartInSeconds() {
		LOGGER.debug("getStartInSeconds(): returning {}", startInSeconds);
        return startInSeconds;
    }

    public Long getEndInSeconds() {
		LOGGER.debug("getEndInSeconds(): returning {}", endInSeconds);
        return endInSeconds;
    }


}
