/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.io;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Xiangyue Cai
 */
public class SerializableObjectWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testEquals() throws Exception {
		Assert.assertNotEquals(
			_testSerializableObjectWrapper, _TEST_SERIALIZABLE);
		Assert.assertNotEquals(
			_testSerializableObjectWrapper,
			new SerializableObjectWrapper(_ANOTHER_TEST_SERIALIZABLE));

		Assert.assertEquals(
			_testSerializableObjectWrapper, _testSerializableObjectWrapper);
		Assert.assertEquals(
			_testSerializableObjectWrapper,
			new SerializableObjectWrapper(_TEST_SERIALIZABLE));
		Assert.assertEquals(
			_testSerializableObjectWrapper,
			_cloneBySerialization(_testSerializableObjectWrapper));
		Assert.assertEquals(
			_cloneBySerialization(_testSerializableObjectWrapper),
			_testSerializableObjectWrapper);
		Assert.assertEquals(
			_cloneBySerialization(_testSerializableObjectWrapper),
			_cloneBySerialization(_testSerializableObjectWrapper));
	}

	@Test
	public void testHashCode() throws Exception {
		SerializableObjectWrapper anotherTestSerializableObjectWrapper =
			new SerializableObjectWrapper(_ANOTHER_TEST_SERIALIZABLE);

		Assert.assertNotEquals(
			_testSerializableObjectWrapper.hashCode(),
			anotherTestSerializableObjectWrapper.hashCode());

		SerializableObjectWrapper testSerializableObjectWrapper =
			new SerializableObjectWrapper(_TEST_SERIALIZABLE);

		Assert.assertEquals(
			_testSerializableObjectWrapper.hashCode(),
			testSerializableObjectWrapper.hashCode());

		SerializableObjectWrapper cloneSerializableObjectWrapper =
			_cloneBySerialization(_testSerializableObjectWrapper);

		Assert.assertEquals(
			_testSerializableObjectWrapper.hashCode(),
			cloneSerializableObjectWrapper.hashCode());
	}

	@Test
	public void testUnwrap() throws Exception {
		Assert.assertEquals(
			_TEST_SERIALIZABLE,
			SerializableObjectWrapper.unwrap(_testSerializableObjectWrapper));

		Assert.assertEquals(
			_TEST_SERIALIZABLE,
			SerializableObjectWrapper.unwrap(
				_cloneBySerialization(_testSerializableObjectWrapper)));

		Assert.assertEquals(
			_TEST_SERIALIZABLE,
			SerializableObjectWrapper.unwrap(_TEST_SERIALIZABLE));
	}

	@Test
	public void testWithBrokenClassLoader() throws Exception {
		ClassLoaderPool.unregister(ClassLoaderPool.class.getClassLoader());

		ClassNotFoundException classNotFoundException =
			new ClassNotFoundException();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				new ClassLoader() {

					@Override
					public Class<?> loadClass(String name)
						throws ClassNotFoundException {

						if (name.equals(TestSerializable.class.getName())) {
							throw classNotFoundException;
						}

						return super.loadClass(name);
					}

				});
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SerializableObjectWrapper.class.getName(),
				LoggerTestUtil.ALL)) {

			// Test unwrap

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertNull(
				SerializableObjectWrapper.unwrap(
					_cloneBySerialization(_testSerializableObjectWrapper)));

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to deserialize object", logEntry.getMessage());
			Assert.assertSame(classNotFoundException, logEntry.getThrowable());
		}
	}

	private SerializableObjectWrapper _cloneBySerialization(
			SerializableObjectWrapper serializableObjectWrapper)
		throws Exception {

		try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream()) {

			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					unsyncByteArrayOutputStream)) {

				objectOutputStream.writeObject(serializableObjectWrapper);
			}

			try (UnsyncByteArrayInputStream unsyncByteArrayInputStream =
					new UnsyncByteArrayInputStream(
						unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
						unsyncByteArrayOutputStream.size());
				ObjectInputStream objectInputStream = new ObjectInputStream(
					unsyncByteArrayInputStream)) {

				return (SerializableObjectWrapper)
					objectInputStream.readObject();
			}
		}
	}

	private static final TestSerializable _ANOTHER_TEST_SERIALIZABLE =
		new TestSerializable("_ANOTHER_TEST_SERIALIZABLE");

	private static final TestSerializable _TEST_SERIALIZABLE =
		new TestSerializable("_TEST_SERIALIZABLE");

	private final SerializableObjectWrapper _testSerializableObjectWrapper =
		new SerializableObjectWrapper(_TEST_SERIALIZABLE);

	private static class TestSerializable implements Serializable {

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof TestSerializable)) {
				return false;
			}

			TestSerializable testSerializable = (TestSerializable)object;

			return Objects.equals(_name, testSerializable._name);
		}

		@Override
		public int hashCode() {
			return _name.hashCode();
		}

		private TestSerializable(String name) {
			_name = name;
		}

		private final String _name;

	}

}