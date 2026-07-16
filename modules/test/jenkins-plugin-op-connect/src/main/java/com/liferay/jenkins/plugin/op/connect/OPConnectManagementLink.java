/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.op.connect;

import hudson.Extension;

import hudson.model.ManagementLink;

import hudson.security.Permission;

import jakarta.servlet.ServletException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;

import org.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Michael Hashimoto
 */
@Extension
public class OPConnectManagementLink extends ManagementLink {

	public void doOpConnectConfiguration(
			StaplerRequest staplerRequest, StaplerResponse staplerResponse)
		throws IOException, ServletException {

		OPConnectDescriptor opConnectDescriptor = getOpConnectDescriptor();

		if (opConnectDescriptor == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(
			staplerRequest.getParameter("json"));

		String accessToken = jsonObject.optString("accessToken");

		if (accessToken.isEmpty()) {
			accessToken = opConnectDescriptor.getAccessToken();
		}
		else {
			opConnectDescriptor.setAccessToken(accessToken);
		}

		String connectURL = jsonObject.getString("connectURL");

		opConnectDescriptor.setConnectURL(connectURL);

		List<String> vaultNames = new ArrayList<>();

		for (String key : jsonObject.keySet()) {
			if (key.startsWith(_VAULT_NAME_PREFIX) &&
				jsonObject.optBoolean(key)) {

				vaultNames.add(key.substring(_VAULT_NAME_PREFIX.length()));
			}
		}

		if (!jsonObject.optBoolean("vaultsRendered")) {
			try {
				OPConnectClient opConnectClient = new OPConnectClient(
					accessToken, connectURL);

				for (OPConnectClient.Vault vault :
						opConnectClient.getVaults()) {

					vaultNames.add(vault.getName());
				}
			}
			catch (IOException ioException) {
			}
		}

		opConnectDescriptor.setVaultNames(vaultNames);

		List<String> ignoredValues = new ArrayList<>();

		String ignoredValuesString = jsonObject.optString("ignoredValues");

		for (String ignoredValue : ignoredValuesString.split("\\r?\\n")) {
			ignoredValue = ignoredValue.trim();

			if (!ignoredValue.isEmpty()) {
				ignoredValues.add(ignoredValue);
			}
		}

		opConnectDescriptor.setIgnoredValues(ignoredValues);

		opConnectDescriptor.setRefreshIntervalMinutes(
			jsonObject.optLong("refreshIntervalMinutes", 60));

		opConnectDescriptor.save();

		opConnectDescriptor.refreshSecretValues();

		Jenkins jenkins = Jenkins.getInstanceOrNull();

		if (jenkins != null) {
			staplerResponse.sendRedirect(jenkins.getRootUrl() + getUrlName());
		}
	}

	@Override
	public Category getCategory() {
		return Category.SECURITY;
	}

	@Override
	public String getDescription() {
		return "Connect to a 1Password Connect server and hide the values of " +
			"a chosen vault in build logs.";
	}

	@Override
	public String getDisplayName() {
		return "1Password Connect";
	}

	@Override
	public String getIconFileName() {
		return "clipboard.png";
	}

	public OPConnectDescriptor getOpConnectDescriptor() {
		Jenkins jenkins = Jenkins.getInstanceOrNull();

		if (jenkins == null) {
			return null;
		}

		return jenkins.getDescriptorByType(OPConnectDescriptor.class);
	}

	@Override
	public Permission getRequiredPermission() {
		return Jenkins.ADMINISTER;
	}

	@Override
	public String getUrlName() {
		return "op-connect-configuration";
	}

	private static final String _VAULT_NAME_PREFIX = "vault:";

}