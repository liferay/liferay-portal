/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.SessionCustomizer;
import com.liferay.portal.kernel.dao.orm.SessionWrapper;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jeff Wu
 */
@RunWith(Arquillian.class)
public class ColumnNameEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_columnNameEntryPersistence = ColumnNameEntryUtil.getPersistence();
	}

	@Test
	public void test() throws Exception {
		ColumnNameEntry columnNameEntry1 = _addColumnNameEntry();
		ColumnNameEntry columnNameEntry2 = _addColumnNameEntry();

		Set<Serializable> primaryKeys = new HashSet<>();

		primaryKeys.add(columnNameEntry1.getPrimaryKey());
		primaryKeys.add(columnNameEntry2.getPrimaryKey());

		List<String> queryStrings = new ArrayList<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<SessionCustomizer> serviceRegistration =
			bundleContext.registerService(
				SessionCustomizer.class,
				session -> new SessionWrapper(session) {

					@Override
					public Query createQuery(String queryString)
						throws ORMException {

						queryStrings.add(queryString);

						return super.createQuery(queryString);
					}

				},
				null);

		Map<Serializable, ColumnNameEntry> columnNameEntries = null;

		try {
			_columnNameEntryPersistence.clearCache();

			columnNameEntries = _columnNameEntryPersistence.fetchByPrimaryKeys(
				primaryKeys);
		}
		finally {
			serviceRegistration.unregister();
		}

		Assert.assertEquals(queryStrings.toString(), 1, queryStrings.size());

		Assert.assertEquals(
			StringBundler.concat(
				"SELECT columnNameEntry FROM ColumnNameEntry columnNameEntry ",
				"WHERE columnNameEntry.columnNameEntryId IN (",
				columnNameEntry1.getPrimaryKey(), ",",
				columnNameEntry2.getPrimaryKey(), ")"),
			queryStrings.get(0));

		Assert.assertEquals(
			columnNameEntry1,
			columnNameEntries.get(columnNameEntry1.getPrimaryKey()));
		Assert.assertEquals(
			columnNameEntry2,
			columnNameEntries.get(columnNameEntry2.getPrimaryKey()));
		Assert.assertEquals(
			columnNameEntries.toString(), 2, columnNameEntries.size());
	}

	private ColumnNameEntry _addColumnNameEntry() throws Exception {
		ColumnNameEntry columnNameEntry = _columnNameEntryPersistence.create(
			RandomTestUtil.nextLong());

		columnNameEntry.setName(RandomTestUtil.randomString());

		return _columnNameEntryPersistence.update(columnNameEntry);
	}

	private ColumnNameEntryPersistence _columnNameEntryPersistence;

}