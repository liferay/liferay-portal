/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.license.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.license.LicenseInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Amos Fong
 */
public class LicenseManagerUtil {

	public static void checkFreeTier() {
		if (isFreeTier()) {
			throw new UnsupportedOperationException();
		}
	}

	public static void checkLicense(String productId) {
		_licenseManager.checkLicense(productId);
	}

	public static Date getAppExpirationDate(App app) {
		return _licenseManager.getAppExpirationDate(app);
	}

	public static List<Map<String, String>> getClusterLicenseProperties(
		String clusterNodeId) {

		return _licenseManager.getClusterLicenseProperties(clusterNodeId);
	}

	public static String getHostName() {
		return _licenseManager.getHostName();
	}

	public static Set<String> getIpAddresses() {
		return _licenseManager.getIpAddresses();
	}

	public static LicenseInfo getLicenseInfo(String productId) {
		return _licenseManager.getLicenseInfo(productId);
	}

	public static LicenseManager getLicenseManager() {
		return _licenseManager;
	}

	public static List<Map<String, String>> getLicenseProperties() {
		return _licenseManager.getLicenseProperties();
	}

	public static Map<String, String> getLicenseProperties(String productId) {
		return _licenseManager.getLicenseProperties(productId);
	}

	public static int getLicenseState(Map<String, String> licenseProperties) {
		return _licenseManager.getLicenseState(licenseProperties);
	}

	public static int getLicenseState(String productId) {
		return _licenseManager.getLicenseState(productId);
	}

	public static Set<String> getMacAddresses() {
		return _licenseManager.getMacAddresses();
	}

	public static boolean isAppEnabled(App app) {
		return _licenseManager.isAppEnabled(app);
	}

	public static boolean isFreeTier() {
		return _licenseManager.isFreeTier();
	}

	public static void registerLicense(JSONObject jsonObject) throws Exception {
		_licenseManager.registerLicense(jsonObject);
	}

	public void setLicenseManager(LicenseManager licenseManager) {
		_licenseManager = licenseManager;
	}

	private static LicenseManager _licenseManager;

}