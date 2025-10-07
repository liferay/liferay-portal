/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.osgi.dto.DTO;

/**
 * @author Dante Wang
 */
public abstract class Registration<S, D extends DTO> {

	public Registration(D dto, S service) {
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

		_readLock = readWriteLock.readLock();

		_writeLock = readWriteLock.writeLock();

		_condition = _writeLock.newCondition();

		_dto = dto;
		_service = service;
	}

	public void addReference() {
		_readLock.lock();

		try {
			referenceCount.incrementAndGet();
		}
		finally {
			_readLock.unlock();
		}
	}

	public void destroy() {
		boolean interrupted = false;

		_writeLock.lock();

		_destroyed = true;

		try {
			while (referenceCount.get() != 0) {
				try {
					_condition.await();
				}
				catch (InterruptedException interruptedException) {
					interrupted = true;

					if (_log.isDebugEnabled()) {
						_log.debug(interruptedException);
					}
				}
			}
		}
		finally {
			_writeLock.unlock();

			if (interrupted) {
				Thread thread = Thread.currentThread();

				thread.interrupt();
			}
		}
	}

	public D getDTO() {
		return _dto;
	}

	public S getService() {
		return _service;
	}

	public void removeReference() {
		_readLock.lock();

		try {
			if ((referenceCount.decrementAndGet() == 0) && _destroyed) {
				_readLock.unlock();
				_writeLock.lock();

				try {
					_condition.signalAll();
				}
				finally {
					_readLock.lock();
					_writeLock.unlock();
				}
			}
		}
		finally {
			_readLock.unlock();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(getDTO());
	}

	protected final AtomicInteger referenceCount = new AtomicInteger();

	private static final Log _log = LogFactoryUtil.getLog(Registration.class);

	private final Condition _condition;
	private volatile boolean _destroyed;
	private final D _dto;
	private final Lock _readLock;
	private final S _service;
	private final Lock _writeLock;

}