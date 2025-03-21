/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.frontend.taglib.form.navigator;

import com.liferay.asset.publisher.constants.AssetPublisherConstants;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseConfigurationFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<Object> {

	@Override
	public String getCategoryKey() {
		return StringPool.BLANK;
	}

	@Override
	public String getFormNavigatorId() {
		return AssetPublisherConstants.FORM_NAVIGATOR_ID_CONFIGURATION;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	protected String getSelectionStyle() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(
				themeDisplay.getLayout(), portletDisplay.getPortletResource());

		return GetterUtil.getString(
			portletPreferences.getValue("selectionStyle", null),
			AssetPublisherSelectionStyleConfigurationUtil.
				defaultSelectionStyle());
	}

	protected boolean isAssetListSelection() {
		return Objects.equals(
			getSelectionStyle(),
			AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST);
	}

	protected boolean isDynamicAssetSelection() {
		return Objects.equals(
			getSelectionStyle(),
			AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC);
	}

	protected boolean isManualSelection() {
		return Objects.equals(
			getSelectionStyle(),
			AssetPublisherSelectionStyleConstants.TYPE_MANUAL);
	}

}