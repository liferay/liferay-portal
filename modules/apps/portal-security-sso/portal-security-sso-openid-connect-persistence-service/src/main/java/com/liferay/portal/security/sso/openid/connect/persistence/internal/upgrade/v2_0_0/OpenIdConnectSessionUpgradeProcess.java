/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.internal.upgrade.v2_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;

import java.security.MessageDigest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Arthur Chan
 */
public class OpenIdConnectSessionUpgradeProcess extends UpgradeProcess {

	public OpenIdConnectSessionUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select openIdConnectSessionId, configurationPid from " +
					"OpenIdConnectSession");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long openIdConnectSessionId = resultSet.getLong(
					"openIdConnectSessionId");
				String configurationPid = resultSet.getString(
					"configurationPid");

				try {
					_upgradeOpenIdConnectSession(
						openIdConnectSessionId, configurationPid);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					runSQL(
						"delete from OpenIdConnectSession where " +
							"openIdConnectSessionId = " +
								openIdConnectSessionId);
				}
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"OpenIdConnectSession", "configurationPid", "providerName")
		};
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"OpenIdConnectSession",
				"authServerWellKnownURI VARCHAR(256) null",
				"clientId VARCHAR(256) null")
		};
	}

	private String _generateLocalWellKnownURI(
			String issuer, String tokenEndpoint)
		throws Exception {

		URI issuerURI = URI.create(issuer);
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");

		return StringBundler.concat(
			issuerURI.getScheme(), "://", issuerURI.getAuthority(),
			"/.well-known/openid-configuration", issuerURI.getPath(), '/',
			Base64.encodeToURL(messageDigest.digest(tokenEndpoint.getBytes())),
			"/local");
	}

	private void _upgradeOpenIdConnectSession(
			long openIdConnectSessionId, String configurationPid)
		throws Exception {

		Configuration configuration = _configurationAdmin.getConfiguration(
			configurationPid, "?");

		Dictionary<String, ?> properties = configuration.getProperties();

		String discoveryEndpoint = GetterUtil.getString(
			properties.get("discoveryEndPoint"));

		if (Validator.isNull(discoveryEndpoint)) {
			discoveryEndpoint = _generateLocalWellKnownURI(
				GetterUtil.getString(properties.get("issuerURL")),
				GetterUtil.getString(properties.get("tokenEndPoint")));
		}

		String openIdConnectClientId = GetterUtil.getString(
			properties.get("openIdConnectClientId"));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update OpenIdConnectSession set authServerWellKnownURI = ?, " +
					"clientId = ? WHERE openIdConnectSessionId = ?")) {

			preparedStatement.setString(1, discoveryEndpoint);
			preparedStatement.setString(2, openIdConnectClientId);
			preparedStatement.setLong(3, openIdConnectSessionId);

			preparedStatement.execute();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectSessionUpgradeProcess.class);

	private final ConfigurationAdmin _configurationAdmin;

}