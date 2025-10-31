/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.test.rule.ExpectedDBType;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedMultipleLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import java.time.Duration;
import java.time.Instant;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class RedirectNotFoundEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddOrUpdateRedirectNotFoundEntry() {
		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url");

		Assert.assertEquals(1, redirectNotFoundEntry.getRequestCount());
	}

	@ExpectedMultipleLogs(
		expectedMultipleLogs = {
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedLog = "Application exception overridden by commit exception",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = DefaultTransactionExecutor.class
			),
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Batch failure",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "DB2 SQL Error: SQLCODE=-803",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.HYPERSONIC,
						expectedLog = "integrity constraint violation",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Duplicate entry '",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Duplicate entry '",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.ORACLE,
						expectedLog = "ORA-00001: unique constraint",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "Batch entry",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "duplicate key",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.SQLSERVER,
						expectedLog = "Cannot insert duplicate key row",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = SqlExceptionHelper.class
			)
		}
	)
	@Test
	public void testAddOrUpdateRedirectNotFoundEntryConcurrently()
		throws Exception {

		try (AutoCloseable autoCloseable =
				_registerRedirectNotFoundEntryModelListener()) {

			RedirectNotFoundEntry redirectNotFoundEntry =
				_addOrUpdateRedirectNotFoundEntry("url");

			Assert.assertEquals(2, redirectNotFoundEntry.getRequestCount());
		}
	}

	@Test
	public void testAddOrUpdateRedirectNotFoundEntryWithDifferentURL() {
		RedirectNotFoundEntry redirectNotFoundEntry1 =
			_addOrUpdateRedirectNotFoundEntry("url1");
		RedirectNotFoundEntry redirectNotFoundEntry2 =
			_addOrUpdateRedirectNotFoundEntry("url2");

		Assert.assertNotEquals(redirectNotFoundEntry1, redirectNotFoundEntry2);

		Assert.assertEquals(1, redirectNotFoundEntry1.getRequestCount());
		Assert.assertEquals(1, redirectNotFoundEntry2.getRequestCount());
	}

	@Test
	public void testAddOrUpdateRedirectNotFoundEntryWithExistingEntry() {
		RedirectNotFoundEntry redirectNotFoundEntry1 =
			_addOrUpdateRedirectNotFoundEntry("url");
		RedirectNotFoundEntry redirectNotFoundEntry2 =
			_addOrUpdateRedirectNotFoundEntry("url");

		Assert.assertEquals(redirectNotFoundEntry1, redirectNotFoundEntry2);
		Assert.assertEquals(2, redirectNotFoundEntry2.getRequestCount());
	}

	@Test
	public void testAddOrUpdateRedirectNotFoundEntryWithNullURL() {
		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry(null);

		Assert.assertEquals(1, redirectNotFoundEntry.getRequestCount());
	}

	@Test
	public void testGetRedirectNotFoundEntries() {
		_addOrUpdateRedirectNotFoundEntry("url1");
		_addOrUpdateRedirectNotFoundEntry("url2");
		_addOrUpdateRedirectNotFoundEntry("url3");

		List<RedirectNotFoundEntry> redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 3,
			redirectNotFoundEntries.size());
	}

	@Test
	public void testGetRedirectNotFoundEntriesReturnsActiveEntries() {
		RedirectNotFoundEntry activeRedirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url1", false);

		_addOrUpdateRedirectNotFoundEntry("url2", true);

		List<RedirectNotFoundEntry> activeRedirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), false, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			activeRedirectNotFoundEntries.toString(), 1,
			activeRedirectNotFoundEntries.size());
		Assert.assertEquals(
			activeRedirectNotFoundEntry, activeRedirectNotFoundEntries.get(0));
	}

	@Test
	public void testGetRedirectNotFoundEntriesReturnsAllEntries() {
		RedirectNotFoundEntry activeRedirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url1", false);
		RedirectNotFoundEntry ignoredRedirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url2", true);

		List<RedirectNotFoundEntry> allRedirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), null, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			allRedirectNotFoundEntries.toString(), 2,
			allRedirectNotFoundEntries.size());

		Assert.assertEquals(
			activeRedirectNotFoundEntry, allRedirectNotFoundEntries.get(0));

		Assert.assertEquals(
			ignoredRedirectNotFoundEntry, allRedirectNotFoundEntries.get(1));
	}

	@Test
	public void testGetRedirectNotFoundEntriesReturnsIgnoredEntries() {
		_addOrUpdateRedirectNotFoundEntry("url1", false);

		RedirectNotFoundEntry ignoredRedirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url2", true);

		List<RedirectNotFoundEntry> ignoredRedirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			ignoredRedirectNotFoundEntries.toString(), 1,
			ignoredRedirectNotFoundEntries.size());

		Assert.assertEquals(
			ignoredRedirectNotFoundEntry,
			ignoredRedirectNotFoundEntries.get(0));
	}

	@Test
	public void testGetRedirectNotFoundEntriesWithMinimumModifiedDate() {
		Instant instant = Instant.now();

		Date minModifiedDate = Date.from(instant.minus(Duration.ofDays(5)));

		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url1", new Date());

		_addOrUpdateRedirectNotFoundEntry(
			"url2", Date.from(instant.minus(Duration.ofDays(6))));
		_addOrUpdateRedirectNotFoundEntry(
			"url3", Date.from(instant.minus(Duration.ofDays(7))));

		List<RedirectNotFoundEntry> redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), minModifiedDate, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 1,
			redirectNotFoundEntries.size());

		Assert.assertEquals(
			redirectNotFoundEntry, redirectNotFoundEntries.get(0));
	}

	@Test
	public void testUpdateRedirectNotFoundEntryChangesTheIgnoredFlag()
		throws Exception {

		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry("url", false);

		List<RedirectNotFoundEntry> redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), false, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 1,
			redirectNotFoundEntries.size());

		Assert.assertEquals(
			redirectNotFoundEntry, redirectNotFoundEntries.get(0));

		_redirectNotFoundEntryLocalService.updateRedirectNotFoundEntry(
			redirectNotFoundEntry.getRedirectNotFoundEntryId(), true);

		redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 1,
			redirectNotFoundEntries.size());

		Assert.assertEquals(
			redirectNotFoundEntry, redirectNotFoundEntries.get(0));
	}

	private RedirectNotFoundEntry _addOrUpdateRedirectNotFoundEntry(
		String url) {

		RedirectNotFoundEntry redirectNotFoundEntry =
			_redirectNotFoundEntryLocalService.addOrUpdateRedirectNotFoundEntry(
				_group, url);

		if (redirectNotFoundEntry != null) {
			_redirectNotFoundEntries.add(redirectNotFoundEntry);
		}

		return redirectNotFoundEntry;
	}

	private RedirectNotFoundEntry _addOrUpdateRedirectNotFoundEntry(
		String url, Boolean ignored) {

		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry(url);

		redirectNotFoundEntry.setIgnored(ignored);

		return _redirectNotFoundEntryLocalService.updateRedirectNotFoundEntry(
			redirectNotFoundEntry);
	}

	private RedirectNotFoundEntry _addOrUpdateRedirectNotFoundEntry(
		String url, Date date) {

		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry(url);

		redirectNotFoundEntry.setModifiedDate(date);

		return _redirectNotFoundEntryLocalService.updateRedirectNotFoundEntry(
			redirectNotFoundEntry);
	}

	private AutoCloseable _registerRedirectNotFoundEntryModelListener() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				(Class<ModelListener<RedirectNotFoundEntry>>)
					(Class<?>)ModelListener.class,
				new BaseModelListener<RedirectNotFoundEntry>() {

					@Override
					public void onBeforeCreate(
						RedirectNotFoundEntry redirectNotFoundEntry) {

						if (!_executed) {
							_executed = true;

							try {
								TransactionInvokerUtil.invoke(
									TransactionConfig.Factory.create(
										Propagation.REQUIRES_NEW,
										new Class<?>[] {Exception.class}),
									() -> {
										_addOrUpdateRedirectNotFoundEntry(
											"url");

										return null;
									});
							}
							catch (Throwable throwable) {
								throw new SystemException(throwable);
							}
						}
					}

					private boolean _executed;

				},
				new HashMapDictionary<>());

		return serviceRegistration::unregister;
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final Set<RedirectNotFoundEntry> _redirectNotFoundEntries =
		new HashSet<>();

	@Inject
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

}