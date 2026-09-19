/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.UniqueFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.UniqueFinderEntryPersistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class UniqueFinderEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@BeforeClass
	public static void setUpClass() {
		Object uniquePersistenceFinder = ReflectionTestUtil.getFieldValue(
			_uniqueFinderEntryPersistence, "_uniquePersistenceFinderByName");

		_fetchPath = ReflectionTestUtil.getFieldValue(
			uniquePersistenceFinder, "_fetchPath");

		_fetchPath.touch();
	}

	@Test
	public void testFetchByNameWithDuplicateResults() {
		String name = RandomTestUtil.randomString();

		Date modifiedDate = new Date();

		UniqueFinderEntry uniqueFinderEntry = _addUniqueFinderEntry(
			modifiedDate, name);

		_addUniqueFinderEntry(new Date(modifiedDate.getTime() - 1000), name);

		_finderCache.removeResult(_fetchPath, new Object[] {name});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				UniquePersistenceFinder.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				uniqueFinderEntry,
				_uniqueFinderEntryPersistence.fetchByName(name));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message, message.contains("returned more than one result"));

			logEntries.clear();

			Assert.assertEquals(
				uniqueFinderEntry,
				_uniqueFinderEntryPersistence.fetchByName(name));

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	@Test
	public void testFetchByNameWithFailingSession() {
		String name = RandomTestUtil.randomString();

		Assert.assertNull(_uniqueFinderEntryPersistence.fetchByName(name));

		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			_uniqueFinderEntryPersistence, "_sessionFactory");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BasePersistenceImpl.class.getName(), LoggerTestUtil.OFF)) {

			ReflectionTestUtil.setFieldValue(
				_uniqueFinderEntryPersistence, "_sessionFactory",
				ProxyUtil.newProxyInstance(
					SessionFactory.class.getClassLoader(),
					new Class<?>[] {SessionFactory.class},
					(proxy, method, arguments) -> {
						String methodName = method.getName();

						if (methodName.equals("openSession")) {
							throw new RuntimeException(
								"Unable to open session");
						}

						return method.invoke(sessionFactory, arguments);
					}));

			Assert.assertNull(_uniqueFinderEntryPersistence.fetchByName(name));

			_finderCache.removeResult(_fetchPath, new Object[] {name});

			try {
				_uniqueFinderEntryPersistence.fetchByName(name);

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_uniqueFinderEntryPersistence, "_sessionFactory",
				sessionFactory);
		}
	}

	@Test
	public void testFetchByNameWithStaleEntityInCache() {
		String name = RandomTestUtil.randomString();

		UniqueFinderEntry uniqueFinderEntry = _addUniqueFinderEntry(
			new Date(), name);

		_assertCacheResult(name, uniqueFinderEntry);

		UniqueFinderEntry staleUniqueFinderEntry = _addUniqueFinderEntry(
			new Date(), RandomTestUtil.randomString());

		_finderCache.putResult(
			_fetchPath, new Object[] {name}, staleUniqueFinderEntry);

		_assertCacheResult(name, staleUniqueFinderEntry);

		Assert.assertEquals(
			uniqueFinderEntry, _uniqueFinderEntryPersistence.fetchByName(name));

		_assertCacheResult(name, uniqueFinderEntry);
	}

	@Test
	public void testFetchByNameWithoutCache() {
		String name = RandomTestUtil.randomString();

		_assertCacheResult(name, null);

		Assert.assertNull(
			_uniqueFinderEntryPersistence.fetchByName(name, false));

		_assertCacheResult(name, null);
	}

	private UniqueFinderEntry _addUniqueFinderEntry(
		Date modifiedDate, String name) {

		UniqueFinderEntry uniqueFinderEntry =
			_uniqueFinderEntryPersistence.create(RandomTestUtil.nextLong());

		uniqueFinderEntry.setModifiedDate(modifiedDate);
		uniqueFinderEntry.setName(name);

		uniqueFinderEntry = _uniqueFinderEntryPersistence.update(
			uniqueFinderEntry);

		_uniqueFinderEntries.add(uniqueFinderEntry);

		return uniqueFinderEntry;
	}

	private void _assertCacheResult(
		String name, UniqueFinderEntry uniqueFinderEntry) {

		Assert.assertEquals(
			uniqueFinderEntry,
			_finderCache.getResult(
				_fetchPath, new Object[] {name},
				_uniqueFinderEntryPersistence));
	}

	private static FinderPath _fetchPath;

	@Inject
	private static UniqueFinderEntryPersistence _uniqueFinderEntryPersistence;

	@Inject
	private FinderCache _finderCache;

	@DeleteAfterTestRun
	private List<UniqueFinderEntry> _uniqueFinderEntries = new ArrayList<>();

}