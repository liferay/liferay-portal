/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public class AssetRendererUtil {

	public static String getAssetRendererUserFullName(
		AssetRenderer<?> assetRenderer, HttpServletRequest httpServletRequest) {

		long assetRendererUserId = assetRenderer.getUserId();

		if (assetRendererUserId > 0) {
			User user = UserLocalServiceUtil.fetchUser(assetRendererUserId);

			if (user == null) {
				return LanguageUtil.get(httpServletRequest, "deleted-user");
			}

			return user.getFullName();
		}

		return LanguageUtil.get(httpServletRequest, "anonymous");
	}

}