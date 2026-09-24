/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shuyang Zhou
 */
public class InvalidationSequence {

	public long getSequence() {
		return _sequence.get();
	}

	public boolean invalidate(String regionName, long sequence) {
		AtomicLong invalidationSequence = _invalidationSequences.get(
			regionName);

		if (invalidationSequence == null) {
			invalidationSequence = _invalidationSequences.computeIfAbsent(
				regionName, key -> new AtomicLong());
		}

		long previousInvalidationSequence = invalidationSequence.getAndUpdate(
			curInvalidationSequence -> _sequence.incrementAndGet());

		if (previousInvalidationSequence > sequence) {
			return true;
		}

		return false;
	}

	public void publish(
		String regionName, long sequence, Runnable publishRunnable,
		Runnable withdrawRunnable) {

		if (_isInvalidatedAfter(regionName, sequence)) {
			return;
		}

		publishRunnable.run();

		if (_isInvalidatedAfter(regionName, sequence)) {
			withdrawRunnable.run();
		}
	}

	private boolean _isInvalidatedAfter(String regionName, long sequence) {
		AtomicLong invalidationSequence = _invalidationSequences.get(
			regionName);

		if ((invalidationSequence == null) ||
			(invalidationSequence.get() <= sequence)) {

			return false;
		}

		return true;
	}

	private final Map<String, AtomicLong> _invalidationSequences =
		new ConcurrentHashMap<>();
	private final AtomicLong _sequence = new AtomicLong();

}