/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.taglib.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.sharing.service.SharingEntryLocalServiceUtil;

/**
 * @author Alejandro Tardín
 */
public class CollaboratorsUtil {

	public static JSONObject getCollaboratorsJSONObject(
		long classNameId, long classPK, ThemeDisplay themeDisplay) {

		return JSONUtil.put(
			"collaborators",
			_getSharingEntryToUsersJSONArray(classPK, classNameId, themeDisplay)
		).put(
			"owner", _getUserJSONObject(classNameId, classPK, themeDisplay)
		).put(
			"total",
			SharingEntryLocalServiceUtil.getSharingEntriesCount(
				classNameId, classPK)
		);
	}

	private static AssetRenderer<?> _getAssetRenderer(
		long classNameId, long classPK) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameId);

		try {
			return assetRendererFactory.getAssetRenderer(classPK);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static String _getDisplayURL(ThemeDisplay themeDisplay, User user) {
		try {
			if ((user == null) || user.isGuestUser()) {
				return StringPool.BLANK;
			}

			return user.getDisplayURL(themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static String _getPortraitURL(
		ThemeDisplay themeDisplay, User user) {

		try {
			if (user.getPortraitId() == 0) {
				return null;
			}

			return user.getPortraitURL(themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static JSONArray _getSharingEntryToUsersJSONArray(
		long classPK, long classNameId, ThemeDisplay themeDisplay) {

		return JSONUtil.toJSONArray(
			SharingEntryLocalServiceUtil.getSharingEntries(
				classNameId, classPK, 0, 4, null),
			sharingEntry -> {
				User user = UserLocalServiceUtil.fetchUserById(
					sharingEntry.getToUserId());

				if (user == null) {
					return null;
				}

				return _getUserJSONObject(user, themeDisplay);
			},
			_log);
	}

	private static JSONObject _getUserJSONObject(
		long classNameId, long classPK, ThemeDisplay themeDisplay) {

		AssetRenderer<?> assetRenderer = _getAssetRenderer(
			classNameId, classPK);

		if (assetRenderer == null) {
			return JSONUtil.put(
				"fullName",
				LanguageUtil.get(themeDisplay.getLocale(), "deleted-user")
			).put(
				"userId", 0
			);
		}

		User user = UserLocalServiceUtil.fetchUser(assetRenderer.getUserId());

		if (user != null) {
			return _getUserJSONObject(user, themeDisplay);
		}

		return JSONUtil.put(
			"fullName", assetRenderer.getUserName()
		).put(
			"userId", assetRenderer.getUserId()
		);
	}

	private static JSONObject _getUserJSONObject(
		User user, ThemeDisplay themeDisplay) {

		return JSONUtil.put(
			"displayURL", _getDisplayURL(themeDisplay, user)
		).put(
			"fullName", user.getFullName()
		).put(
			"portraitURL", _getPortraitURL(themeDisplay, user)
		).put(
			"userId", user.getUserId()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CollaboratorsUtil.class);

}