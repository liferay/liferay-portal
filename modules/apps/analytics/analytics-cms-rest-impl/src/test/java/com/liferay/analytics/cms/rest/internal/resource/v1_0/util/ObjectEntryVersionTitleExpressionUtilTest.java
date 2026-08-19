/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0.util;

import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManager;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Clob;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class ObjectEntryVersionTitleExpressionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_originalLanguage = LanguageUtil.getLanguage();

		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.isAvailableLocale(Mockito.any(Locale.class))
		).thenReturn(
			true
		);

		languageUtil.setLanguage(language);
	}

	@After
	public void tearDown() {
		DBManagerUtil.reset();

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_originalLanguage);
	}

	@Test
	public void testGetLocalizedTitleExpressionDB2() {
		_setUpDBType(DBType.DB2);

		Assert.assertEquals(
			"JSON_VALUE(ObjectEntryVersion.content, " +
				"'$.properties.title_i18n.en_US')",
			_toSQL(
				ObjectEntryVersionTitleExpressionUtil.
					getLocalizedTitleExpression("en_US")));
	}

	@Test
	public void testGetLocalizedTitleExpressionInvalidLanguageId() {
		_setUpDBType(DBType.ORACLE);

		Assert.assertEquals(
			"NULL",
			_toSQL(
				ObjectEntryVersionTitleExpressionUtil.
					getLocalizedTitleExpression("invalid")));
	}

	@Test
	public void testGetLocalizedTitleExpressionOracle() {
		_setUpDBType(DBType.ORACLE);

		Assert.assertEquals(
			"JSON_VALUE(ObjectEntryVersion.content, " +
				"'$.properties.title_i18n.en_US')",
			_toSQL(
				ObjectEntryVersionTitleExpressionUtil.
					getLocalizedTitleExpression("en_US")));
	}

	@Test
	public void testGetLocalizedTitleExpressionSQLServer() {
		_setUpDBType(DBType.SQLSERVER);

		Assert.assertEquals(
			"JSON_VALUE(ObjectEntryVersion.content, " +
				"'$.properties.title_i18n.en_US')",
			_toSQL(
				ObjectEntryVersionTitleExpressionUtil.
					getLocalizedTitleExpression("en_US")));
	}

	@Test
	public void testGetTitleExpressionDB2() {
		_setUpDBType(DBType.DB2);

		Assert.assertEquals(
			"JSON_VALUE(ObjectEntryVersion.content, '$.properties.title')",
			_toSQL(ObjectEntryVersionTitleExpressionUtil.getTitleExpression()));
	}

	@Test
	public void testGetTitleExpressionOracle() {
		_setUpDBType(DBType.ORACLE);

		Assert.assertEquals(
			"JSON_VALUE(ObjectEntryVersion.content, '$.properties.title')",
			_toSQL(ObjectEntryVersionTitleExpressionUtil.getTitleExpression()));
	}

	private void _setUpDBType(DBType dbType) {
		DBManager dbManager = Mockito.mock(DBManager.class);

		DB db = Mockito.mock(DB.class);

		Mockito.when(
			db.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			dbManager.getDB()
		).thenReturn(
			db
		);

		DBManagerUtil.setDBManager(dbManager);
	}

	private String _toSQL(Expression<Clob> expression) {
		StringBundler sb = new StringBundler();

		expression.toSQL(sb::append, null);

		return sb.toString();
	}

	private Language _originalLanguage;

}