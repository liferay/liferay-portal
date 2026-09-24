/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.cache;

import com.liferay.petra.lang.HashUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Shuyang Zhou
 */
public class InvalidationSequence {

	public long getSequence() {
		return _sequence.get();
	}

	public boolean invalidate(
		String regionName, long sequence, boolean removeAll,
		Collection<Serializable> keys) {

		AtomicLong writeSequence = _getAtomicLong(_writeSequences, regionName);

		long previousWriteSequence = writeSequence.getAndUpdate(
			curWriteSequence -> _sequence.incrementAndGet());

		long stamp = _sequence.incrementAndGet();

		if (removeAll) {
			_wipe(regionName, stamp);
		}
		else {
			for (Serializable key : keys) {
				_invalidateKey(regionName, key, stamp);
			}
		}

		if (previousWriteSequence > sequence) {
			return true;
		}

		return false;
	}

	public void publish(
		String regionName, long sequence, Runnable publishRunnable,
		Runnable withdrawRunnable) {

		if (_isWrittenAfter(regionName, sequence)) {
			return;
		}

		publishRunnable.run();

		if (_isWrittenAfter(regionName, sequence)) {
			withdrawRunnable.run();
		}
	}

	public boolean publishKey(
		String regionName, Serializable key, long sequence,
		Runnable publishRunnable, Runnable withdrawRunnable) {

		int index = _getIndex(regionName, key);

		if (_isKeyInvalidatedAfter(regionName, index, sequence)) {
			return false;
		}

		publishRunnable.run();

		if (_isKeyInvalidatedAfter(regionName, index, sequence)) {
			withdrawRunnable.run();

			return false;
		}

		return true;
	}

	private AtomicLong _getAtomicLong(
		Map<String, AtomicLong> atomicLongs, String regionName) {

		AtomicLong atomicLong = atomicLongs.get(regionName);

		if (atomicLong == null) {
			atomicLong = atomicLongs.computeIfAbsent(
				regionName, curRegionName -> new AtomicLong());
		}

		return atomicLong;
	}

	private int _getIndex(String regionName, Serializable key) {
		int hash = HashUtil.hash(regionName.hashCode(), key);

		return (hash ^ (hash >>> 16)) & (_KEY_SEQUENCES_SIZE - 1);
	}

	private void _invalidateKey(
		String regionName, Serializable key, long stamp) {

		_keySequences.accumulateAndGet(
			_getIndex(regionName, key), stamp, Math::max);
	}

	private boolean _isKeyInvalidatedAfter(
		String regionName, int index, long sequence) {

		if (_keySequences.get(index) > sequence) {
			return true;
		}

		AtomicLong wipeSequence = _wipeSequences.get(regionName);

		if ((wipeSequence != null) && (wipeSequence.get() > sequence)) {
			return true;
		}

		return false;
	}

	private boolean _isWrittenAfter(String regionName, long sequence) {
		AtomicLong writeSequence = _writeSequences.get(regionName);

		if ((writeSequence == null) || (writeSequence.get() <= sequence)) {
			return false;
		}

		return true;
	}

	private void _wipe(String regionName, long stamp) {
		AtomicLong wipeSequence = _getAtomicLong(_wipeSequences, regionName);

		wipeSequence.accumulateAndGet(stamp, Math::max);
	}

	private static final int _KEY_SEQUENCES_SIZE = 1 << 16;

	private final AtomicLongArray _keySequences = new AtomicLongArray(
		_KEY_SEQUENCES_SIZE);
	private final AtomicLong _sequence = new AtomicLong();
	private final Map<String, AtomicLong> _wipeSequences =
		new ConcurrentHashMap<>();
	private final Map<String, AtomicLong> _writeSequences =
		new ConcurrentHashMap<>();

}