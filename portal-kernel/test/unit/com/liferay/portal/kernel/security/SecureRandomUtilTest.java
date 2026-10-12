/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;

import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class SecureRandomUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

	@Before
	public void setUp() {
		System.setProperty(_KEY_BUFFER_SIZE, "2048");
	}

	@After
	public void tearDown() {
		System.clearProperty(_KEY_BUFFER_SIZE);
	}

	@NewEnv(type = NewEnv.Type.NONE)
	@Test
	public void testConstructor() {
		new SecureRandomUtil();
	}

	@Test
	public void testHighConcurrency() throws Exception {
		AtomicInteger duplicateCounter = new AtomicInteger();
		List<Future<Void>> list = new ArrayList<>();
		Set<String> values = ConcurrentHashMap.newKeySet();

		Runtime runtime = Runtime.getRuntime();

		int processors = runtime.availableProcessors();

		ExecutorService executorService = Executors.newFixedThreadPool(
			processors);

		for (int i = 0; i < (processors * 10000); i++) {
			list.add(
				executorService.submit(
					() -> {
						if (!values.add(
								SecureRandomUtil.nextLong() + "-" +
									SecureRandomUtil.nextLong())) {

							duplicateCounter.incrementAndGet();
						}

						return null;
					}));
		}

		for (Future<Void> future : list) {
			future.get();
		}

		executorService.shutdownNow();

		Assert.assertEquals(0, duplicateCounter.get());
	}

	@Test
	public void testInitialization() {
		System.setProperty(_KEY_BUFFER_SIZE, "10");

		Assert.assertEquals(
			Integer.valueOf(1024),
			ReflectionTestUtil.getFieldValue(
				SecureRandomUtil.class, "_BUFFER_SIZE"));

		byte[] bytes = ReflectionTestUtil.getFieldValue(getBuffer(), "_bytes");

		Assert.assertEquals(Arrays.toString(bytes), 1024, bytes.length);
	}

	@Test
	public void testNextBoolean() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 4096; i++) {
			byte b = (byte)i;

			if (b < 0) {
				Assert.assertFalse(SecureRandomUtil.nextBoolean());
			}
			else {
				Assert.assertTrue(SecureRandomUtil.nextBoolean());
			}
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testNextByte() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 4096; i++) {
			Assert.assertEquals((byte)i, SecureRandomUtil.nextByte());
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testNextDouble() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 512; i++) {
			byte b = (byte)(i * 8);

			byte[] bytes = new byte[8];

			for (int j = 0; j < 8; j++) {
				bytes[j] = (byte)(b + j);
			}

			Assert.assertEquals(
				BigEndianCodec.getDouble(bytes, 0),
				SecureRandomUtil.nextDouble(), 0);
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testNextFloat() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 1024; i++) {
			byte b = (byte)(i * 4);

			byte[] bytes = new byte[4];

			for (int j = 0; j < 4; j++) {
				bytes[j] = (byte)(b + j);
			}

			Assert.assertEquals(
				BigEndianCodec.getFloat(bytes, 0), SecureRandomUtil.nextFloat(),
				0);
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testNextInt() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 1024; i++) {
			byte b = (byte)(i * 4);

			byte[] bytes = new byte[4];

			for (int j = 0; j < 4; j++) {
				bytes[j] = (byte)(b + j);
			}

			Assert.assertEquals(
				BigEndianCodec.getInt(bytes, 0), SecureRandomUtil.nextInt());
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testNextLong() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		for (int i = 0; i < 512; i++) {
			byte b = (byte)(i * 8);

			byte[] bytes = new byte[8];

			for (int j = 0; j < 8; j++) {
				bytes[j] = (byte)(b + j);
			}

			Assert.assertEquals(
				BigEndianCodec.getLong(bytes, 0), SecureRandomUtil.nextLong());
		}

		getNextBuffer(getBuffer());

		Assert.assertEquals(6144, counter.get());
	}

	@Test
	public void testSwapWithCancelledPrefetch() throws Exception {
		Future<?> nextBufferFuture = ReflectionTestUtil.getFieldValue(
			getNextBuffer(getBuffer()), "_nextBufferFuture");

		nextBufferFuture.cancel(false);

		for (int i = 0; i < 512; i++) {
			SecureRandomUtil.nextLong();
		}

		try {
			SecureRandomUtil.nextLong();

			Assert.fail();
		}
		catch (CancellationException cancellationException) {
		}
	}

	@Test
	public void testSwapWithFailedPrefetch() throws Exception {
		Object buffer = getNextBuffer(getBuffer());

		Random random = ReflectionTestUtil.getFieldValue(
			SecureRandomUtil.class, "_random");
		RuntimeException runtimeException = new RuntimeException();

		ReflectionTestUtil.setFieldValue(
			SecureRandomUtil.class, "_random",
			new Random() {

				@Override
				public void nextBytes(byte[] bytes) {
					ReflectionTestUtil.setFieldValue(
						SecureRandomUtil.class, "_random", random);

					throw runtimeException;
				}

			});

		for (int i = 0; i < 513; i++) {
			SecureRandomUtil.nextLong();
		}

		Future<?> nextBufferFuture = ReflectionTestUtil.getFieldValue(
			buffer, "_nextBufferFuture");

		try {
			nextBufferFuture.get();

			Assert.fail();
		}
		catch (ExecutionException executionException) {
			Assert.assertSame(runtimeException, executionException.getCause());
		}

		Future<?> nextReadBufferFuture = ReflectionTestUtil.getFieldValue(
			buffer, "_nextReadBufferFuture");

		Object nextBuffer = nextReadBufferFuture.get();

		Assert.assertSame(nextBuffer, getBuffer());
		Assert.assertNotNull(getNextBuffer(nextBuffer));
	}

	@Test
	public void testSwapWithInterrupt() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		FutureTask<Boolean> futureTask = new FutureTask<>(
			() -> {
				for (int i = 0; i < 255; i++) {
					SecureRandomUtil.nextLong();
				}

				Thread currentThread = Thread.currentThread();

				currentThread.interrupt();

				SecureRandomUtil.nextLong();

				return currentThread.isInterrupted();
			});

		Thread thread = new Thread(futureTask);

		synchronized (counter) {
			for (int i = 0; i < 257; i++) {
				SecureRandomUtil.nextLong();
			}

			thread.start();

			while (thread.getState() != Thread.State.WAITING);
		}

		thread.join();

		Assert.assertTrue(futureTask.get());
	}

	@Test
	public void testSwapWithPendingPrefetch() throws Exception {
		AtomicInteger counter = installPredictableRandom();

		Object buffer = getNextBuffer(getBuffer());

		FutureTask<Long> futureTask = new FutureTask<>(
			() -> {
				for (int i = 0; i < 255; i++) {
					SecureRandomUtil.nextLong();
				}

				return SecureRandomUtil.nextLong();
			});

		Thread thread = new Thread(futureTask);

		synchronized (counter) {
			for (int i = 0; i < 257; i++) {
				SecureRandomUtil.nextLong();
			}

			thread.start();

			while (thread.getState() != Thread.State.WAITING);
		}

		thread.join();

		byte[] bytes = ReflectionTestUtil.getFieldValue(
			getNextBuffer(buffer), "_bytes");

		Assert.assertEquals(
			BigEndianCodec.getLong(bytes, 0), (long)futureTask.get());
	}

	@Test
	public void testSwapWithStaleBuffer() {
		Object buffer = getBuffer();

		for (int i = 0; i < 257; i++) {
			SecureRandomUtil.nextLong();
		}

		Object nextBuffer = getBuffer();

		ReflectionTestUtil.invoke(
			SecureRandomUtil.class, "_swap", new Class<?>[] {buffer.getClass()},
			buffer);

		Assert.assertSame(nextBuffer, getBuffer());
	}

	protected Object getBuffer() {
		AtomicReference<?> readBufferAtomicReference =
			ReflectionTestUtil.getFieldValue(
				SecureRandomUtil.class, "_readBufferAtomicReference");

		return readBufferAtomicReference.get();
	}

	protected Object getNextBuffer(Object buffer) throws Exception {
		Future<?> nextBufferFuture = ReflectionTestUtil.getFieldValue(
			buffer, "_nextBufferFuture");

		return nextBufferFuture.get();
	}

	protected AtomicInteger installPredictableRandom() throws Exception {
		Object buffer = getBuffer();

		Object nextBuffer = getNextBuffer(buffer);

		PredictableRandom predictableRandom = new PredictableRandom();

		ReflectionTestUtil.setFieldValue(
			SecureRandomUtil.class, "_random", predictableRandom);

		predictableRandom.nextBytes(
			ReflectionTestUtil.getFieldValue(buffer, "_bytes"));
		predictableRandom.nextBytes(
			ReflectionTestUtil.getFieldValue(nextBuffer, "_bytes"));

		return predictableRandom._counter;
	}

	private static final String _KEY_BUFFER_SIZE =
		SecureRandomUtil.class.getName() + ".buffer.size";

	private static class PredictableRandom extends SecureRandom {

		@Override
		public void nextBytes(byte[] bytes) {
			synchronized (_counter) {
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = (byte)_counter.getAndIncrement();
				}
			}
		}

		private static final long serialVersionUID = 1L;

		private final AtomicInteger _counter = new AtomicInteger();

	}

}