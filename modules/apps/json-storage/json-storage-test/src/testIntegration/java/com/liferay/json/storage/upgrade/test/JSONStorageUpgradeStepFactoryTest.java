/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.json.storage.service.JSONStorageEntryLocalService;
import com.liferay.json.storage.upgrade.JSONStorageUpgradeStepFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class JSONStorageUpgradeStepFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"create table JSONStorageUpgrade (jsonStorageUpgradeId LONG ",
				"not null, ctCollectionId LONG not null, companyId LONG, ",
				"jsonString VARCHAR(75), primary key (jsonStorageUpgradeId, ",
				"ctCollectionId))"));

		db.runSQL(
			StringBundler.concat(
				"insert into JSONStorageUpgrade values (", _CLASS_PK, ", 0, ",
				TestPropsValues.getCompanyId(), ", '", _JSON_STRING, "')"));

		_className = _classNameLocalService.getClassName(
			JSONStorageUpgradeStepFactoryTest.class.getName());
	}

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("drop table JSONStorageUpgrade");

		_jsonStorageEntryLocalService.deleteJSONStorageEntries(
			_className.getClassNameId(), _CLASS_PK);
	}

	@Test
	public void testJSONStoreUpgradeStepFactory() throws Exception {
		UpgradeProcess upgradeProcess =
			(UpgradeProcess)_jsonStorageUpgradeStepFactory.createUpgradeStep(
				JSONStorageUpgradeStepFactoryTest.class, "JSONStorageUpgrade",
				"jsonStorageUpgradeId", "jsonString");

		upgradeProcess.upgrade();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertFalse(
				dbInspector.hasColumn("JSONStorageUpgrade", "jsonString"));
		}

		JSONAssert.assertEquals(
			_JSON_STRING,
			_jsonStorageEntryLocalService.getJSON(
				_className.getClassNameId(), _CLASS_PK),
			true);
	}

	private static final long _CLASS_PK = 1;

	private static final String _JSON_STRING =
		"{\"array\": [1, 2], \"object\": {\"key\": \"value\"}}";

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static JSONStorageEntryLocalService _jsonStorageEntryLocalService;

	@Inject
	private static JSONStorageUpgradeStepFactory _jsonStorageUpgradeStepFactory;

	@DeleteAfterTestRun
	private ClassName _className;

}