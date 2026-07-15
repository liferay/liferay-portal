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
public interface LicenseManager {

	public static final int STATE_ABSENT = 1;

	public static final int STATE_EXPIRED = 2;

	public static final int STATE_GOOD = 3;

	public static final int STATE_INACTIVE = 4;

	public static final int STATE_INVALID = 5;

	public static final int STATE_OVERLOAD = 6;

	public void checkLicense(String productId);

	public Date getAppExpirationDate(App app);

	public List<Map<String, String>> getClusterLicenseProperties(
		String clusterNodeId);

	public String getHostName();

	public Set<String> getIpAddresses();

	public LicenseInfo getLicenseInfo(String productId);

	public List<Map<String, String>> getLicenseProperties();

	public Map<String, String> getLicenseProperties(String productId);

	public int getLicenseState(Map<String, String> licenseProperties);

	public int getLicenseState(String productId);

	public Set<String> getMacAddresses();

	public boolean isAppEnabled(App app);

	public boolean isFreeTier();

	public void registerLicense(JSONObject jsonObject) throws Exception;

}