/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search;

import com.liferay.portal.kernel.dao.db.DBManager;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brian I. Kim
 */
public class UserBulkReindexerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_dbManager = Mockito.mock(DBManager.class);

		DBManagerUtil.setDBManager(_dbManager);
	}

	@After
	public void tearDown() {
		DBManagerUtil.setDBManager(null);
	}

	@Test
	public void testReindex() {
		_assertReindex(3, 65400, 1, 1);
		_assertReindex(3, 65400, 3, 1);
		_assertReindex(3, 65400, 4, 2);
		_assertReindex(3, 65400, 10, 4);
		_assertReindex(Integer.MAX_VALUE, 3, 1, 1);
		_assertReindex(Integer.MAX_VALUE, 3, 3, 1);
		_assertReindex(Integer.MAX_VALUE, 3, 4, 2);
		_assertReindex(Integer.MAX_VALUE, 3, 10, 4);
	}

	private void _assertReindex(
		int dbInMaxParameters, int dbMaxParameters, int size, int times) {

		Mockito.when(
			_dbManager.getDBInMaxParameters()
		).thenReturn(
			dbInMaxParameters
		);

		Mockito.when(
			_dbManager.getDBMaxParameters()
		).thenReturn(
			dbMaxParameters
		);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			Mockito.mock(IndexableActionableDynamicQuery.class);

		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class);

		Mockito.when(
			userLocalService.getIndexableActionableDynamicQuery()
		).thenReturn(
			indexableActionableDynamicQuery
		);

		UserBulkReindexer userBulkReindexer = new UserBulkReindexer();

		ReflectionTestUtil.setFieldValue(
			userBulkReindexer, "userLocalService", userLocalService);

		userBulkReindexer.reindex(
			RandomTestUtil.randomLong(),
			Collections.nCopies(size, RandomTestUtil.randomLong()));

		Mockito.verify(
			indexableActionableDynamicQuery, Mockito.times(times)
		).performActions();
	}

	private DBManager _dbManager;

}