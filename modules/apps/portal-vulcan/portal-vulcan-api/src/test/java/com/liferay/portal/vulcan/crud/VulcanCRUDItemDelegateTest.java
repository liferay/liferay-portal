/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.crud;

import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carlos Correa
 */
public class VulcanCRUDItemDelegateTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_log = ReflectionTestUtil.getFieldValue(
			VulcanCRUDItemDelegate.class, "log");

		ReflectionTestUtil.setFieldValue(
			VulcanCRUDItemDelegate.class, "log", new TestLogWrapper(_log));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ReflectionTestUtil.setFieldValue(
			VulcanCRUDItemDelegate.class, "log", _log);
	}

	@After
	public void tearDown() throws Exception {
		_debugMessages.clear();
		_debugThrowables.clear();
		_errorMessages.clear();
		_errorThrowables.clear();
	}

	@Test
	public void testFetchItem() {
		Object object = Mockito.mock(Object.class);

		VulcanCRUDItemDelegate<Object> vulcanCRUDItemDelegate =
			new TestVulcanCRUDItemDelegate(object);

		Assert.assertSame(
			object,
			vulcanCRUDItemDelegate.fetchItem(RandomTestUtil.randomLong()));

		Assert.assertTrue(_debugMessages.isEmpty());
		Assert.assertTrue(_debugThrowables.isEmpty());
		Assert.assertTrue(_errorMessages.isEmpty());
		Assert.assertTrue(_errorThrowables.isEmpty());
	}

	@Test
	public void testFetchItemWithException() {
		Exception exception = new Exception();

		VulcanCRUDItemDelegate<Object> vulcanCRUDItemDelegate =
			new TestVulcanCRUDItemDelegate(exception);

		long id = RandomTestUtil.randomLong();

		Assert.assertNull(vulcanCRUDItemDelegate.fetchItem(id));

		Assert.assertTrue(_debugMessages.isEmpty());
		Assert.assertTrue(_debugThrowables.isEmpty());
		Assert.assertEquals(
			_errorMessages.toString(), 1, _errorMessages.size());
		Assert.assertEquals(
			"Unable to find item with id " + id, _errorMessages.get(0));
		Assert.assertEquals(
			_errorThrowables.toString(), 1, _errorThrowables.size());
		Assert.assertSame(exception, _errorThrowables.get(0));
	}

	@Test
	public void testFetchItemWithNoSuchModelException() {
		NoSuchModelException noSuchModelException = new NoSuchModelException();

		VulcanCRUDItemDelegate<Object> vulcanCRUDItemDelegate =
			new TestVulcanCRUDItemDelegate(noSuchModelException);

		long id = RandomTestUtil.randomLong();

		Assert.assertNull(vulcanCRUDItemDelegate.fetchItem(id));

		Assert.assertEquals(
			_debugMessages.toString(), 1, _debugMessages.size());
		Assert.assertEquals(
			"Unable to find item with id " + id, _debugMessages.get(0));
		Assert.assertEquals(
			_debugThrowables.toString(), 1, _debugThrowables.size());
		Assert.assertSame(noSuchModelException, _debugThrowables.get(0));
		Assert.assertTrue(_errorMessages.isEmpty());
		Assert.assertTrue(_errorThrowables.isEmpty());
	}

	@Test
	public void testFetchItemWithNotFoundException() {
		NotFoundException notFoundException = new NotFoundException();

		VulcanCRUDItemDelegate<Object> vulcanCRUDItemDelegate =
			new TestVulcanCRUDItemDelegate(notFoundException);

		long id = RandomTestUtil.randomLong();

		Assert.assertNull(vulcanCRUDItemDelegate.fetchItem(id));

		Assert.assertEquals(
			_debugMessages.toString(), 1, _debugMessages.size());
		Assert.assertEquals(
			"Unable to find item with id " + id, _debugMessages.get(0));
		Assert.assertEquals(
			_debugThrowables.toString(), 1, _debugThrowables.size());
		Assert.assertSame(notFoundException, _debugThrowables.get(0));
		Assert.assertTrue(_errorMessages.isEmpty());
		Assert.assertTrue(_errorThrowables.isEmpty());
	}

	private static Log _log;

	private static final List<Object> _debugMessages = new ArrayList<>();
	private static final List<Throwable> _debugThrowables = new ArrayList<>();
	private static final List<Object> _errorMessages = new ArrayList<>();
	private static final List<Throwable> _errorThrowables = new ArrayList<>();

	private static class TestLogWrapper extends LogWrapper {

		public TestLogWrapper(Log log) {
			super(log);
		}

		@Override
		public void debug(Object msg, Throwable throwable) {
			_debugMessages.add((String)msg);
			_debugThrowables.add(throwable);
		}

		@Override
		public void error(Object msg, Throwable throwable) {
			_errorMessages.add((String)msg);
			_errorThrowables.add(throwable);
		}

		@Override
		public boolean isDebugEnabled() {
			return true;
		}

	}

	private static class TestVulcanCRUDItemDelegate
		implements VulcanCRUDItemDelegate<Object> {

		public TestVulcanCRUDItemDelegate(Exception exception) {
			_exception = exception;
		}

		public TestVulcanCRUDItemDelegate(Object item) {
			_item = item;
		}

		@Override
		public Object getItem(Long id) throws Exception {
			if (_exception != null) {
				throw _exception;
			}

			return _item;
		}

		private Exception _exception;
		private Object _item;

	}

}