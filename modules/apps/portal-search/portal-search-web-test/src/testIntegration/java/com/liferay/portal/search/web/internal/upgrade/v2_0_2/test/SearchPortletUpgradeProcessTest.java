/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v2_0_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class SearchPortletUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_db = DBManagerUtil.getDB();
		_group = GroupTestUtil.addGroup();

		_setUpData();
		_setUpSearchPortletUpgradeProcess();
	}

	@Test
	public void testUpgrade() throws Exception {
		String sql1 = StringBundler.concat(
			"select count(*) from Layout where typeSettings like '%",
			_PORTLET_ID, "%'");
		String sql2 = StringBundler.concat(
			"select count(*) from Portlet where portletId = '", _PORTLET_ID,
			"'");
		String sql3 = StringBundler.concat(
			"select count(*) from PortletPreferences where portletId like '",
			_PORTLET_ID, "%'");
		String sql4 = StringBundler.concat(
			"select count(*) from ResourceAction where name = '", _PORTLET_ID,
			"'");
		String sql5 = StringBundler.concat(
			"select count(*) from ResourcePermission where name = '",
			_PORTLET_ID, "'");

		Assert.assertEquals(sql1, 3, _count(sql1));
		Assert.assertEquals(sql2, 1, _count(sql2));
		Assert.assertEquals(sql3, 1, _count(sql3));
		Assert.assertEquals(sql3, 1, _count(sql4));
		Assert.assertEquals(sql3, 1, _count(sql4));

		_searchPortletUpgradeProcess.upgrade();

		Assert.assertEquals(sql1, 0, _count(sql1));
		Assert.assertEquals(sql2, 0, _count(sql2));
		Assert.assertEquals(sql3, 0, _count(sql3));
		Assert.assertEquals(sql3, 0, _count(sql4));
		Assert.assertEquals(sql3, 0, _count(sql5));
	}

	private void _addLayouts() throws Exception {
		LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.COLUMN_PREFIX + 1,
				StringBundler.concat(
					_PORTLET_ID, "_INSTANCE_", RandomTestUtil.randomString())
			).buildString());

		LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.COLUMN_PREFIX + 1,
				StringBundler.concat(
					RandomTestUtil.randomString(), ",", _PORTLET_ID,
					"_INSTANCE_", RandomTestUtil.randomString())
			).buildString());

		LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.COLUMN_PREFIX + 2,
				StringBundler.concat(
					RandomTestUtil.randomString(), ",", _PORTLET_ID,
					"_INSTANCE_", RandomTestUtil.randomString(), ",",
					RandomTestUtil.randomString())
			).buildString());
	}

	private int _count(String sql) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	private void _setUpData() throws Exception {
		_addLayouts();

		_db.runSQL(
			StringBundler.concat(
				"insert into Portlet (id_, portletId) values (",
				RandomTestUtil.randomInt(), ", '", _PORTLET_ID, "')"));
		_db.runSQL(
			StringBundler.concat(
				"insert into PortletPreferences (ctCollectionId, portletId, ",
				"portletPreferencesId) values (", RandomTestUtil.randomInt(),
				", '", _PORTLET_ID, "', ", RandomTestUtil.randomInt(), ")"));
		_db.runSQL(
			StringBundler.concat(
				"insert into ResourceAction (name, resourceActionId) values ('",
				_PORTLET_ID, "', ", RandomTestUtil.randomInt(), ")"));
		_db.runSQL(
			StringBundler.concat(
				"insert into ResourcePermission (ctCollectionId, name, ",
				"resourcePermissionId) values (", RandomTestUtil.randomInt(),
				", '", _PORTLET_ID, "', ", RandomTestUtil.randomInt(), ")"));
	}

	private void _setUpSearchPortletUpgradeProcess() {
		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						Class<?> clazz = upgradeStep.getClass();

						if (!Objects.equals(clazz.getName(), _CLASS_NAME)) {
							continue;
						}

						_searchPortletUpgradeProcess =
							(UpgradeProcess)upgradeStep;
					}
				}

			});
	}

	private static final String _CLASS_NAME =
		"com.liferay.portal.search.web.internal.upgrade.v2_0_2." +
			"SearchPortletUpgradeProcess";

	private static final String _PORTLET_ID =
		"com_liferay_portal_search_web_date_facet_portlet_DateFacetPortlet";

	private DB _db;

	@DeleteAfterTestRun
	private Group _group;

	private UpgradeProcess _searchPortletUpgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.portal.search.web.internal.upgrade.registry.SearchWebUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}