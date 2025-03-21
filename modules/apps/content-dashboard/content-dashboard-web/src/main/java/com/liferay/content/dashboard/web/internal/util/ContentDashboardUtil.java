/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.util;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardConstants;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

/**
 * @author Alejandro Tardín
 * @author Yurena Cabrera
 */
public class ContentDashboardUtil {

	public static long[] getAssetVocabularyIds(RenderRequest renderRequest) {
		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String[] assetVocabularyIds = portletPreferences.getValues(
			"assetVocabularyIds", new String[0]);

		if (ArrayUtil.isNotEmpty(assetVocabularyIds)) {
			return GetterUtil.getLongValues(assetVocabularyIds);
		}

		long[] defaultAssetVocabularyIds = _getDefaultAssetVocabularyIds(
			renderRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			portletPreferences.setValues(
				"assetVocabularyIds",
				ArrayUtil.toStringArray(defaultAssetVocabularyIds));

			PortletPreferencesLocalServiceUtil.updatePreferences(
				themeDisplay.getUserId(), PortletKeys.PREFS_OWNER_TYPE_USER, 0,
				ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
				portletPreferences);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return defaultAssetVocabularyIds;
	}

	private static long[] _getDefaultAssetVocabularyIds(
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetVocabulary audienceAssetVocabulary =
			AssetVocabularyLocalServiceUtil.fetchGroupVocabulary(
				themeDisplay.getCompanyGroupId(),
				ContentDashboardConstants.DefaultInternalAssetVocabularyName.
					AUDIENCE.toString());
		AssetVocabulary stageAssetVocabulary =
			AssetVocabularyLocalServiceUtil.fetchGroupVocabulary(
				themeDisplay.getCompanyGroupId(),
				ContentDashboardConstants.DefaultInternalAssetVocabularyName.
					STAGE.toString());

		return new long[] {
			audienceAssetVocabulary.getVocabularyId(),
			stageAssetVocabulary.getVocabularyId()
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardUtil.class);

}