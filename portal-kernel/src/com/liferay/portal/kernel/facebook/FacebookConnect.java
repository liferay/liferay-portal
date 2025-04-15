/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.facebook;

import com.liferay.portal.kernel.json.JSONObject;

import jakarta.portlet.PortletRequest;

/**
 * @author Wilson Man
 * @author Brian Wing Shun Chan
 * @author Mika Koivisto
 */
public interface FacebookConnect {

	public String getAccessToken(long companyId, String redirect, String code);

	public String getAccessTokenURL(long companyId);

	public String getAppId(long companyId);

	public String getAppSecret(long companyId);

	public String getAuthURL(long companyId);

	public JSONObject getGraphResources(
		long companyId, String path, String accessToken, String fields);

	public String getGraphURL(long companyId);

	public String getProfileImageURL(PortletRequest portletRequest);

	public String getRedirectURL(long companyId);

	public boolean isEnabled(long companyId);

	public boolean isVerifiedAccountRequired(long companyId);

}