/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_7_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class OAuthClientEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		String authRequestParametersJSON = _randomJSON();
		String tokenRequestParametersJSON = _randomJSON();

		OAuthClientEntry oAuthClientEntry = null;

		try (OpenIdConnectProviderHttpServer openIdConnectProviderHttpServer =
				new OpenIdConnectProviderHttpServer()) {

			oAuthClientEntry =
				_oAuthClientEntryLocalService.addOAuthClientEntry(
					null, TestPropsValues.getUserId(),
					authRequestParametersJSON,
					openIdConnectProviderHttpServer.getURL(), null,
					JSONUtil.put(
						"client_id", RandomTestUtil.randomString()
					).toString(),
					null, OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
					OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
					OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
					tokenRequestParametersJSON);
		}

		DB db = DBManagerUtil.getDB();

		try {
			_testUpgrade(
				authRequestParametersJSON,
				oAuthClientEntry.getOAuthClientEntryId(),
				tokenRequestParametersJSON);

			db.runSQLTemplate(
				"alter table OAuthClientEntry add parametersJSON TEXT null;",
				true);
			db.runSQLTemplate(
				"update OAuthClientEntry set parametersJSON = " +
					"authRequestParametersJSON;",
				true);
			db.runSQLTemplate(
				"alter table OAuthClientEntry drop column " +
					"authRequestParametersJSON;",
				true);
			db.runSQLTemplate(
				"alter table OAuthClientEntry drop column " +
					"tokenRequestParametersJSON;",
				true);
			db.runSQLTemplate(
				"alter_column_type OAuthClientEntry clientId VARCHAR(128) " +
					"null;",
				true);

			_testUpgrade(
				authRequestParametersJSON,
				oAuthClientEntry.getOAuthClientEntryId(), "{}");
		}
		finally {
			try (Connection connection = DataAccess.getConnection()) {
				DBInspector dbInspector = new DBInspector(connection);

				if (dbInspector.hasColumn(
						"OAuthClientEntry", "parametersJSON")) {

					db.runSQLTemplate(
						"alter table OAuthClientEntry drop column " +
							"parametersJSON;",
						true);
					db.runSQLTemplate(
						"alter table OAuthClientEntry add " +
							"authRequestParametersJSON VARCHAR(3999) null;",
						true);
					db.runSQLTemplate(
						"alter table OAuthClientEntry add " +
							"tokenRequestParametersJSON VARCHAR(3999) null;",
						true);
					db.runSQLTemplate(
						"alter_column_type OAuthClientEntry clientId " +
							"VARCHAR(256) null;",
						true);
				}
			}

			_oAuthClientEntryLocalService.deleteOAuthClientEntry(
				oAuthClientEntry.getOAuthClientEntryId());
		}
	}

	private String _randomJSON() {
		return JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		).toString();
	}

	private void _testUpgrade(
			String authRequestParametersJSON, long oAuthClientEntryId,
			String tokenRequestParametersJSON)
		throws Exception {

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.oauth.client.persistence.internal.upgrade.v1_7_0." +
				"OAuthClientEntryUpgradeProcess");

		for (UpgradeStep upgradeStep : upgradeProcess.getUpgradeSteps()) {
			upgradeStep.upgrade();
		}

		_multiVMPool.clear();

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			Assert.assertTrue(
				dbInspector.hasColumnType(
					"OAuthClientEntry", "clientId", "VARCHAR(256) null"));
			Assert.assertFalse(
				dbInspector.hasColumn("OAuthClientEntry", "parametersJSON"));
		}

		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.getOAuthClientEntry(
				oAuthClientEntryId);

		Assert.assertEquals(
			authRequestParametersJSON,
			oAuthClientEntry.getAuthRequestParametersJSON());
		Assert.assertEquals(
			tokenRequestParametersJSON,
			oAuthClientEntry.getTokenRequestParametersJSON());
	}

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.oauth.client.persistence.internal.upgrade.registry.OAuthClientPersistenceServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}