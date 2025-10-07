/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class SXPBlueprintCollectionProviderUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPS-129412")
	public void testUpgrade() throws Exception {
		Assume.assumeFalse(FeatureFlagManagerUtil.isEnabled("LPS-129412"));

		_setFeatureFlag(false);

		try {
			String configuration = _readJSON("configuration");

			_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
				null, TestPropsValues.getUserId(), configuration,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				StringPool.BLANK, _OLD_SCHEMA_VERSION,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator,
				"com.liferay.search.experiences.internal.upgrade.v3_2_0." +
					"SXPBlueprintCollectionProviderUpgradeProcess");

			upgradeProcess.upgrade();

			_multiVMPool.clear();

			_sxpBlueprint = _sxpBlueprintLocalService.fetchSXPBlueprint(
				_sxpBlueprint.getSXPBlueprintId());

			JSONAssert.assertEquals(
				configuration, _sxpBlueprint.getConfigurationJSON(),
				JSONCompareMode.STRICT);
			Assert.assertEquals(
				_NEW_SCHEMA_VERSION, _sxpBlueprint.getSchemaVersion());
		}
		finally {
			_unsetFeatureFlag(false);
		}
	}

	@FeatureFlag("LPS-129412")
	@Test
	@TestInfo("LPS-129412")
	public void testUpgradeWithFF() throws Exception {
		Assume.assumeTrue(FeatureFlagManagerUtil.isEnabled("LPS-129412"));

		_setFeatureFlag(true);

		try {
			String originalConfiguration = _readJSON("configuration");

			_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
				null, TestPropsValues.getUserId(), originalConfiguration,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				StringPool.BLANK, _OLD_SCHEMA_VERSION,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator,
				"com.liferay.search.experiences.internal.upgrade.v3_2_0." +
					"SXPBlueprintCollectionProviderUpgradeProcess");

			upgradeProcess.upgrade();

			_multiVMPool.clear();

			_sxpBlueprint = _sxpBlueprintLocalService.fetchSXPBlueprint(
				_sxpBlueprint.getSXPBlueprintId());

			JSONObject configurationJSONObject =
				JSONFactoryUtil.createJSONObject(
					_sxpBlueprint.getConfigurationJSON());

			JSONObject generalConfigurationJSONObject =
				configurationJSONObject.getJSONObject("generalConfiguration");

			Assert.assertTrue(
				generalConfigurationJSONObject.getBoolean(
					"collectionProvider"));
			Assert.assertEquals(
				"com.liferay.asset.kernel.model.AssetEntry",
				generalConfigurationJSONObject.getString(
					"collectionProviderType"));
			Assert.assertTrue(
				generalConfigurationJSONObject.getBoolean(
					"legacyAssetCollectionProvider"));

			Assert.assertEquals(
				_NEW_SCHEMA_VERSION, _sxpBlueprint.getSchemaVersion());
		}
		finally {
			_unsetFeatureFlag(true);
		}
	}

	private void _createNewPortalPreferenceValue(
			boolean enabled, Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into PortalPreferenceValue (",
					"portalPreferenceValueId, companyId, key_, namespace, ",
					"smallValue) VALUES (?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, _group.getCompanyId());
			preparedStatement.setString(3, _FEATURE_FLAG_KEY);
			preparedStatement.setString(
				4, "com.liferay.portal.kernel.feature.flag.FeatureFlag");
			preparedStatement.setString(5, Boolean.toString(enabled));

			preparedStatement.executeUpdate();
		}
	}

	private String _readJSON(String name) {
		return StringUtil.read(
			_clazz,
			StringBundler.concat(
				"dependencies/", _clazz.getSimpleName(), StringPool.PERIOD,
				name, ".json"));
	}

	private void _setFeatureFlag(boolean enabled) throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			String selectSQL =
				"select portalPreferenceValueId, smallValue FROM " +
					"PortalPreferenceValue WHERE key_ = ? AND companyId = ?";

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(selectSQL)) {

				preparedStatement.setString(1, _FEATURE_FLAG_KEY);
				preparedStatement.setLong(2, _group.getCompanyId());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						_initialFeatureFlagExists = true;
						_portalPreferenceValueId = resultSet.getLong(
							"portalPreferenceValueId");
						_initialFeatureFlagValue = resultSet.getBoolean(
							"smallValue");
					}
				}
			}

			if (_initialFeatureFlagExists) {
				_updatePortalPreferenceValue(enabled, connection);
			}
			else {
				_createNewPortalPreferenceValue(enabled, connection);
			}
		}
	}

	private void _unsetFeatureFlag(boolean enabled) throws Exception {
		if (_initialFeatureFlagExists &&
			(_initialFeatureFlagValue == enabled)) {

			return;
		}

		try (Connection connection = DataAccess.getConnection()) {
			if (_initialFeatureFlagExists) {
				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"update PortalPreferenceValue set smallValue = ? " +
								"where portalPreferenceValueId = ?")) {

					preparedStatement.setString(
						1, Boolean.toString(_initialFeatureFlagValue));
					preparedStatement.setLong(2, _portalPreferenceValueId);

					preparedStatement.executeUpdate();
				}
			}
			else {
				String deleteSQL =
					"delete from PortalPreferenceValue where " +
						"portalPreferenceValueId = ?";

				try (PreparedStatement preparedStatement =
						connection.prepareStatement(deleteSQL)) {

					preparedStatement.setLong(1, 0);

					preparedStatement.executeUpdate();
				}
			}
		}
	}

	private void _updatePortalPreferenceValue(
			boolean enabled, Connection connection)
		throws SQLException {

		if (_initialFeatureFlagValue != enabled) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update PortalPreferenceValue set smallValue = ? " +
							"where portalPreferenceValueId = ?")) {

				preparedStatement.setString(1, Boolean.toString(enabled));
				preparedStatement.setLong(2, _portalPreferenceValueId);

				preparedStatement.executeUpdate();
			}
		}
	}

	private static final String _FEATURE_FLAG_KEY = "LPS-129412";

	private static final String _NEW_SCHEMA_VERSION = "1.2";

	private static final String _OLD_SCHEMA_VERSION = "1.1";

	@Inject(
		filter = "(&(component.name=com.liferay.search.experiences.internal.upgrade.registry.SXPServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private final Class<?> _clazz = getClass();

	@DeleteAfterTestRun
	private Group _group;

	private boolean _initialFeatureFlagExists;
	private boolean _initialFeatureFlagValue;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _portalPreferenceValueId;

	@DeleteAfterTestRun
	private SXPBlueprint _sxpBlueprint;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}