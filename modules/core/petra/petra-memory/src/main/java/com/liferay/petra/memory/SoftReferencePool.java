/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.memory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Shuyang Zhou
 */
public class SoftReferencePool<V, P> {

	public static final int DEFAULT_IDLE_SIZE = 8;

	public SoftReferencePool(PoolAction<V, P> poolAction) {
		this(poolAction, DEFAULT_IDLE_SIZE);
	}

	public SoftReferencePool(PoolAction<V, P> poolAction, int maxIdleSize) {
		this(poolAction, maxIdleSize, true);
	}

	public SoftReferencePool(
		PoolAction<V, P> poolAction, int maxIdleSize, boolean useWeakCounter) {

		_poolAction = poolAction;
		_maxIdleSize = maxIdleSize;

		_useWeakCounter = useWeakCounter;

		if (_useWeakCounter) {
			_weakCounter = new AtomicInteger();
		}
		else {
			_weakCounter = null;
		}
	}

	public V borrowObject(P parameter) {
		while (true) {
			SoftReference<? extends V> softReference = _softReferences.poll();

			if (softReference == null) {
				return _poolAction.onCreate(parameter);
			}
			else if (_useWeakCounter) {
				_weakCounter.getAndDecrement();
			}

			V value = softReference.get();

			if (value != null) {
				return _poolAction.onBorrow(value, parameter);
			}
		}
	}

	public void returnObject(V value) {
		if (_getCount() < _maxIdleSize) {
			SoftReference<V> softReference = new SoftReference<>(
				value, _referenceQueue);

			_poolAction.onReturn(value);

			_softReferences.offer(softReference);

			if (_useWeakCounter) {
				_weakCounter.getAndIncrement();
			}
		}
		else {
			while (_getCount() > _maxIdleSize) {
				if ((_softReferences.poll() == null) || !_useWeakCounter) {
					continue;
				}

				_weakCounter.getAndDecrement();
			}
		}

		SoftReference<? extends V> softReference = null;

		while (true) {
			softReference = (SoftReference<? extends V>)_referenceQueue.poll();

			if (softReference == null) {
				break;
			}

			if (!_softReferences.remove(softReference) || !_useWeakCounter) {
				continue;
			}

			_weakCounter.getAndDecrement();
		}
	}

	private int _getCount() {
		if (_useWeakCounter) {
			return _weakCounter.get();
		}

		return _softReferences.size();
	}

	private final int _maxIdleSize;
	private final PoolAction<V, P> _poolAction;
	private final ReferenceQueue<V> _referenceQueue = new ReferenceQueue<>();
	private final Queue<SoftReference<? extends V>> _softReferences =
		new ConcurrentLinkedQueue<>();
	private final boolean _useWeakCounter;
	private final AtomicInteger _weakCounter;

}