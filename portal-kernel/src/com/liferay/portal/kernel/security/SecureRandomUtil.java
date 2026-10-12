/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.io.BigEndianCodec;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SystemProperties;

import java.security.SecureRandom;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Shuyang Zhou
 */
public class SecureRandomUtil {

	public static boolean nextBoolean() {
		byte b = nextByte();

		if (b < 0) {
			return false;
		}

		return true;
	}

	public static byte nextByte() {
		while (true) {
			Buffer buffer = _readBufferAtomicReference.get();

			int index = buffer.getAndAdd(1);

			if (index < _BUFFER_SIZE) {
				return buffer._bytes[index];
			}

			_swap(buffer);
		}
	}

	public static double nextDouble() {
		while (true) {
			Buffer buffer = _readBufferAtomicReference.get();

			int index = buffer.getAndAdd(8);

			if ((index + 7) < _BUFFER_SIZE) {
				return BigEndianCodec.getDouble(buffer._bytes, index);
			}

			_swap(buffer);
		}
	}

	public static float nextFloat() {
		while (true) {
			Buffer buffer = _readBufferAtomicReference.get();

			int index = buffer.getAndAdd(4);

			if ((index + 3) < _BUFFER_SIZE) {
				return BigEndianCodec.getFloat(buffer._bytes, index);
			}

			_swap(buffer);
		}
	}

	public static int nextInt() {
		while (true) {
			Buffer buffer = _readBufferAtomicReference.get();

			int index = buffer.getAndAdd(4);

			if ((index + 3) < _BUFFER_SIZE) {
				return BigEndianCodec.getInt(buffer._bytes, index);
			}

			_swap(buffer);
		}
	}

	public static long nextLong() {
		while (true) {
			Buffer buffer = _readBufferAtomicReference.get();

			int index = buffer.getAndAdd(8);

			if ((index + 7) < _BUFFER_SIZE) {
				return BigEndianCodec.getLong(buffer._bytes, index);
			}

			_swap(buffer);
		}
	}

	private static Buffer _get(Future<Buffer> future) {
		boolean interrupted = false;

		try {
			while (true) {
				try {
					return future.get();
				}
				catch (ExecutionException executionException) {
					return null;
				}
				catch (InterruptedException interruptedException) {
					interrupted = true;
				}
			}
		}
		finally {
			if (interrupted) {
				Thread currentThread = Thread.currentThread();

				currentThread.interrupt();
			}
		}
	}

	private static void _swap(Buffer buffer) {
		Buffer nextBuffer = _get(buffer._nextBufferFuture);

		if (nextBuffer == null) {
			nextBuffer = new Buffer();
		}

		if (_readBufferAtomicReference.compareAndSet(buffer, nextBuffer)) {
			buffer._nextReadBufferFuture.set(nextBuffer);
		}
	}

	private static final int _BUFFER_SIZE;

	private static final int _MIN_BUFFER_SIZE = 1024;

	private static final Random _random = new SecureRandom();
	private static final AtomicReference<Buffer> _readBufferAtomicReference;

	private static class Buffer {

		public int getAndAdd(int delta) {
			return _index.getAndAdd(delta);
		}

		private Buffer() {
			_random.nextBytes(_bytes);
		}

		private final byte[] _bytes = new byte[_BUFFER_SIZE];
		private final AtomicInteger _index = new AtomicInteger();
		private final DefaultNoticeableFuture<Buffer> _nextBufferFuture =
			new DefaultNoticeableFuture<>();
		private final DefaultNoticeableFuture<Buffer> _nextReadBufferFuture =
			new DefaultNoticeableFuture<>();

	}

	private static class PrefetchThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					_writeBuffer._nextBufferFuture.set(new Buffer());
				}
				catch (Throwable throwable) {
					_writeBuffer._nextBufferFuture.setException(throwable);
				}

				_writeBuffer = _get(_writeBuffer._nextReadBufferFuture);
			}
		}

		private PrefetchThread(Buffer writeBuffer) {
			super("SecureRandomUtil Prefetch Thread");

			_writeBuffer = writeBuffer;
		}

		private Buffer _writeBuffer;

	}

	static {
		int bufferSize = GetterUtil.getInteger(
			SystemProperties.get(
				SecureRandomUtil.class.getName() + ".buffer.size"));

		if (bufferSize < _MIN_BUFFER_SIZE) {
			bufferSize = _MIN_BUFFER_SIZE;
		}

		_BUFFER_SIZE = bufferSize;

		Buffer buffer = new Buffer();

		_readBufferAtomicReference = new AtomicReference<>(buffer);

		Thread thread = new PrefetchThread(buffer);

		thread.setContextClassLoader(null);
		thread.setDaemon(true);

		thread.start();
	}

}