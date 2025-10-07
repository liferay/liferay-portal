/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.bnd.util;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.SwappableSecurityManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.util.ReflectionUtilTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class ConfigurableUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testBigString() {
		_testBigString(65535);
		_testBigString(65536);
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testClassInitializationFailure() throws Exception {
		SecurityException securityException = new SecurityException();

		try (SwappableSecurityManager swappableSecurityManager =
				ReflectionUtilTestUtil.throwForSuppressAccessChecks(
					securityException)) {

			Class.forName(ConfigurableUtil.class.getName());

			Assert.fail();
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(securityException, eiie.getCause());
		}
	}

	@AdviseWith(adviceClasses = ConfigurableUtilAdvice.class)
	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testConcurrentCreateConfigurable() throws Exception {
		Callable<TestConfiguration> callable =
			() -> ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				Collections.singletonMap(
					"testReqiredString", "testReqiredString"));

		FutureTask<TestConfiguration> futureTask1 = new FutureTask<>(callable);
		FutureTask<TestConfiguration> futureTask2 = new FutureTask<>(callable);

		Thread thread1 = new Thread(
			futureTask1, "Configurable Util Test Thread 1");
		Thread thread2 = new Thread(
			futureTask2, "Configurable Util Test Thread 2");

		thread1.start();
		thread2.start();

		ConfigurableUtilAdvice.waitUntilBlock();

		ConfigurableUtilAdvice.unblock();

		_assertTestConfiguration(futureTask1.get(), "testReqiredString");
		_assertTestConfiguration(futureTask2.get(), "testReqiredString");
	}

	@Test
	public void testCreateConfigurable() {

		// Test dictionary

		_assertTestConfiguration(
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				HashMapDictionaryBuilder.put(
					"testReqiredString", "testReqiredString1"
				).build()),
			"testReqiredString1");

		// Test map

		_assertTestConfiguration(
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				Collections.singletonMap(
					"testReqiredString", "testReqiredString2")),
			"testReqiredString2");
	}

	@Test
	public void testMisc() {

		// Exception

		try {
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class, Collections.emptyMap());

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertEquals(
				"Unable to create snapshot class for " +
					TestConfiguration.class,
				runtimeException.getMessage());

			Throwable throwable = runtimeException.getCause();

			throwable = throwable.getCause();

			Assert.assertTrue(throwable instanceof IllegalStateException);
			Assert.assertEquals(
				"Attribute is required but not set testReqiredString",
				throwable.getMessage());
		}

		// Constructor

		new ConfigurableUtil();
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testOverride() {

		// Test dictionary override

		PropsUtil.set(
			"configuration.override." + TestConfiguration.class.getName() +
				"_testReqiredString",
			"\"testReqiredString3\"");

		_assertTestConfiguration(
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				HashMapDictionaryBuilder.put(
					"testReqiredString", "testReqiredString1"
				).build()),
			"testReqiredString3");

		// Test map override

		_assertTestConfiguration(
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				Collections.singletonMap(
					"testReqiredString", "testReqiredString2")),
			"testReqiredString3");
	}

	@Test
	public void testSyntheticMethod() {
		TestSyntheticMethodConfiguration testSyntheticMethodConfiguration =
			ConfigurableUtil.createConfigurable(
				TestSyntheticMethodConfiguration.class, Collections.emptyMap());

		Assert.assertEquals(
			"test_string", testSyntheticMethodConfiguration.testString());
	}

	@Aspect
	public static class ConfigurableUtilAdvice {

		public static void unblock() {
			_blockingCountDownLatch.countDown();
		}

		public static void waitUntilBlock() throws InterruptedException {
			_waitingCountDownLatch.await();
		}

		@Around(
			"execution(private * com.liferay.portal.configuration.metatype." +
				"bnd.util.ConfigurableUtil._generateSnapshotClassData(..))"
		)
		public Object generateSnapshotClassData(
				ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {

			_waitingCountDownLatch.countDown();

			_blockingCountDownLatch.await();

			return proceedingJoinPoint.proceed();
		}

		private static final CountDownLatch _blockingCountDownLatch =
			new CountDownLatch(1);
		private static final CountDownLatch _waitingCountDownLatch =
			new CountDownLatch(2);

	}

	public static class TestClass {

		public TestClass(String name) {
			_name = name;
		}

		public String getName() {
			return _name;
		}

		private final String _name;

	}

	private void _assertTestConfiguration(
		TestConfiguration testConfiguration, String requiredString) {

		Assert.assertTrue(testConfiguration.testBoolean());
		Assert.assertEquals(1, testConfiguration.testByte());
		Assert.assertEquals(1.0, testConfiguration.testDouble(), 0);
		Assert.assertEquals(TestEnum.TEST_VALUE, testConfiguration.testEnum());
		Assert.assertEquals(1.0, testConfiguration.testFloat(), 0);
		Assert.assertEquals(100, testConfiguration.testInt());
		Assert.assertNull(testConfiguration.testNullResult());
		Assert.assertEquals(
			requiredString, testConfiguration.testReqiredString());
		Assert.assertEquals(1, testConfiguration.testShort());
		Assert.assertEquals("test_string", testConfiguration.testString());
		Assert.assertArrayEquals(
			new String[] {"test_string_1", "test_string_2"},
			testConfiguration.testStringArray());
		Assert.assertArrayEquals(
			new String[0],
			testConfiguration.testStringArrayWithEmptyStringAsDefault());
		Assert.assertArrayEquals(
			new String[] {"a=b", "c=d,e=f"},
			testConfiguration.testStringEscapeMultiValuedAttribute());
		Assert.assertEquals(
			"a=b,c=d\\,e=f",
			testConfiguration.testStringEscapeSingleValuedAttribute());

		TestClass testClass = testConfiguration.testClass();

		Assert.assertEquals("test.class", testClass.getName());
	}

	private void _testBigString(int length) {
		StringBundler sb = new StringBundler(length);

		for (int i = 0; i < length; i++) {
			sb.append(CharPool.LOWER_CASE_A);
		}

		String bigString = sb.toString();

		_assertTestConfiguration(
			ConfigurableUtil.createConfigurable(
				TestConfiguration.class,
				Collections.singletonMap("testReqiredString", bigString)),
			bigString);
	}

	private interface TestConfiguration {

		@Meta.AD(deflt = "true", required = false)
		public boolean testBoolean();

		@Meta.AD(deflt = "1", required = false)
		public byte testByte();

		@Meta.AD(deflt = "test.class", required = false)
		public TestClass testClass();

		@Meta.AD(deflt = "1.0", required = false)
		public double testDouble();

		@Meta.AD(deflt = "TEST_VALUE", required = false)
		public TestEnum testEnum();

		@Meta.AD(deflt = "1.0", required = false)
		public float testFloat();

		@Meta.AD(deflt = "100", required = false)
		public int testInt();

		@Meta.AD(deflt = "100", required = false)
		public long testLong();

		@Meta.AD(required = false)
		public String testNullResult();

		@Meta.AD(required = true)
		public String testReqiredString();

		@Meta.AD(deflt = "1", required = false)
		public short testShort();

		@Meta.AD(deflt = "test_string", required = false)
		public String testString();

		@Meta.AD(deflt = "test_string_1|test_string_2", required = false)
		public String[] testStringArray();

		@Meta.AD(deflt = "", required = false)
		public String[] testStringArrayWithEmptyStringAsDefault();

		@Meta.AD(deflt = "a=b,c=d\\,e=f", required = false)
		public String[] testStringEscapeMultiValuedAttribute();

		@Meta.AD(deflt = "a=b,c=d\\,e=f", required = false)
		public String testStringEscapeSingleValuedAttribute();

	}

	private enum TestEnum {

		TEST_VALUE

	}

	private interface TestSyntheticMethodConfiguration {

		@Meta.AD(deflt = "test_string", required = false)
		public String testString();

		public Runnable runnable = () -> {
		};

	}

}