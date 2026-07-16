/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.op.connect;

import hudson.Extension;

import hudson.model.Describable;
import hudson.model.Descriptor;

import hudson.util.FormValidation;
import hudson.util.Secret;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jenkins.model.Jenkins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kohsuke.stapler.QueryParameter;

/**
 * @author Michael Hashimoto
 */
@Extension
public class OPConnectDescriptor
	extends Descriptor<OPConnectDescriptor>
	implements Describable<OPConnectDescriptor> {

	public OPConnectDescriptor() {
		super(OPConnectDescriptor.class);

		load();

		refreshSecretValues();
	}

	public FormValidation doTestConnection(
		@QueryParameter("accessToken") String accessToken,
		@QueryParameter("connectURL") String connectURL) {

		Jenkins jenkins = Jenkins.getInstanceOrNull();

		if (jenkins != null) {
			jenkins.checkPermission(Jenkins.ADMINISTER);
		}

		if ((connectURL == null) || connectURL.isEmpty()) {
			return FormValidation.error("Connect URL is required");
		}

		if ((accessToken == null) || accessToken.isEmpty()) {
			accessToken = getAccessToken();
		}

		if ((accessToken == null) || accessToken.isEmpty()) {
			return FormValidation.error("Access Token is required");
		}

		try {
			OPConnectClient opConnectClient = new OPConnectClient(
				accessToken, connectURL);

			List<OPConnectClient.Vault> vaults = opConnectClient.getVaults();

			return FormValidation.ok(
				"Successfully connected to 1Password Connect. Found " +
					vaults.size() + " vaults.");
		}
		catch (IOException ioException) {
			return FormValidation.error(
				"Unable to connect to 1Password Connect: " +
					ioException.getMessage());
		}
	}

	public String getAccessToken() {
		if (_accessToken == null) {
			return null;
		}

		return Secret.toString(_accessToken);
	}

	public String getConnectURL() {
		return _connectURL;
	}

	@Override
	public Descriptor<OPConnectDescriptor> getDescriptor() {
		return this;
	}

	public List<String> getIgnoredValues() {
		return _ignoredValues;
	}

	public String getIgnoredValuesString() {
		if (_ignoredValues == null) {
			return "";
		}

		return String.join("\n", _ignoredValues);
	}

	public String getLastRefreshTimeString() {
		if (_lastRefreshTime == 0) {
			return "Never";
		}

		return String.valueOf(new Date(_lastRefreshTime));
	}

	public long getRefreshIntervalMinutes() {
		return _refreshIntervalMinutes;
	}

	public List<String> getSecretValues() {
		return _secretValues;
	}

	public List<String> getVaultNames() {
		return _vaultNames;
	}

	public List<OPConnectClient.Vault> getVaults() {
		if (!_isConfigured()) {
			return Collections.emptyList();
		}

		try {
			OPConnectClient opConnectClient = new OPConnectClient(
				getAccessToken(), getConnectURL());

			return opConnectClient.getVaults();
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to retrieve 1Password vaults", ioException);
			}

			return Collections.emptyList();
		}
	}

	public boolean hasVaultName(String vaultName) {
		return _vaultNames.contains(vaultName);
	}

	public void refreshSecretValues() {
		if (!_isConfigured() || _vaultNames.isEmpty()) {
			_secretValues = Collections.emptyList();

			_lastRefreshTime = System.currentTimeMillis();

			return;
		}

		try {
			List<String> secretValues = new ArrayList<>();

			OPConnectClient opConnectClient = new OPConnectClient(
				getAccessToken(), getConnectURL());

			for (String vaultName : _vaultNames) {
				OPConnectClient.Vault vault = opConnectClient.getVault(
					vaultName);

				if (vault == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to find 1Password vault named \"" +
								vaultName + "\"");
					}

					continue;
				}

				for (OPConnectClient.Item item : vault.getItems()) {
					for (OPConnectClient.Field field : item.getFields()) {
						secretValues.add(field.getValue());
					}
				}
			}

			_secretValues = secretValues;

			_lastRefreshTime = System.currentTimeMillis();

			if (_log.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();

				sb.append("Successfully connected to 1Password Connect at ");
				sb.append(_connectURL);
				sb.append(" and loaded ");
				sb.append(_secretValues.size());
				sb.append(" values from ");
				sb.append(_vaultNames.size());
				sb.append(" vaults");

				_log.info(sb.toString());
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to retrieve 1Password vault values", ioException);
			}
		}
	}

	public void setAccessToken(String accessToken) {
		_accessToken = Secret.fromString(accessToken);
	}

	public void setConnectURL(String connectURL) {
		_connectURL = connectURL;
	}

	public void setIgnoredValues(List<String> ignoredValues) {
		_ignoredValues = ignoredValues;
	}

	public void setRefreshIntervalMinutes(long refreshIntervalMinutes) {
		_refreshIntervalMinutes = refreshIntervalMinutes;
	}

	public void setVaultNames(List<String> vaultNames) {
		_vaultNames.clear();

		if (vaultNames == null) {
			return;
		}

		_vaultNames.addAll(vaultNames);
	}

	private boolean _isConfigured() {
		String accessToken = getAccessToken();
		String connectURL = getConnectURL();

		if ((accessToken != null) && !accessToken.isEmpty() &&
			(connectURL != null) && !connectURL.isEmpty()) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactory.getLog(
		OPConnectDescriptor.class);

	private Secret _accessToken;
	private String _connectURL;
	private List<String> _ignoredValues = new ArrayList<>();
	private transient volatile long _lastRefreshTime;
	private long _refreshIntervalMinutes = 60;
	private transient volatile List<String> _secretValues =
		Collections.emptyList();
	private final List<String> _vaultNames = new ArrayList<>();

}