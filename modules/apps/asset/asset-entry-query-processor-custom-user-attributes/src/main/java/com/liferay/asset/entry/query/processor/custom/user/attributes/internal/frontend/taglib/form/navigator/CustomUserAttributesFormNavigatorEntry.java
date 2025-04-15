/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.query.processor.custom.user.attributes.internal.frontend.taglib.form.navigator;

import com.liferay.asset.publisher.constants.AssetPublisherConstants;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "form.navigator.entry.order:Integer=300",
	service = FormNavigatorEntry.class
)
public class CustomUserAttributesFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<Object> {

	@Override
	public String getCategoryKey() {
		return AssetPublisherConstants.CATEGORY_KEY_ASSET_SELECTION;
	}

	@Override
	public String getFormNavigatorId() {
		return AssetPublisherConstants.FORM_NAVIGATOR_ID_CONFIGURATION;
	}

	@Override
	public String getKey() {
		return "custom-user-attributes";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "custom-user-attributes");
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isDeprecated() {
		return true;
	}

	@Override
	public boolean isVisible(User user, Object object) {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-39304")) {
			return false;
		}

		if (FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-13311") &&
			_isDynamicAssetSelection()) {

			return true;
		}

		return false;
	}

	@Override
	protected String getJspPath() {
		return "/custom_user_attributes.jsp";
	}

	private boolean _isDynamicAssetSelection() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(
				themeDisplay.getLayout(), portletDisplay.getPortletResource());

		String selectionStyle = GetterUtil.getString(
			portletPreferences.getValue("selectionStyle", null));

		return Objects.equals(selectionStyle, "dynamic");
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.asset.entry.query.processor.custom.user.attributes)"
	)
	private ServletContext _servletContext;

}