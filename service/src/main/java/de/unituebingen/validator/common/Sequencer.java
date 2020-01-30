package de.unituebingen.validator.common;

import java.util.concurrent.atomic.AtomicLong;

public final class Sequencer {
	
	private static final AtomicLong sequenceNumber = new AtomicLong(0);

	public static long getSequence () {
		return sequenceNumber.getAndIncrement();
	}
}
