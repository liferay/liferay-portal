/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayRenderResponse;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;

import java.util.Collection;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 */
public class RenderResponseImpl
	extends MimeResponseImpl implements LiferayRenderResponse {

	@Override
	public String getLifecycle() {
		return PortletRequest.RENDER_PHASE;
	}

	public String getResourceName() {
		return _resourceName;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public boolean getUseDefaultTemplate() {
		if (_useDefaultTemplate == null) {
			Portlet portlet = getPortlet();

			return portlet.isUseDefaultTemplate();
		}

		return _useDefaultTemplate;
	}

	@Override
	public void setNextPossiblePortletModes(
		Collection<? extends PortletMode> portletModes) {
	}

	@Override
	public void setResourceName(String resourceName) {
		_resourceName = resourceName;
	}

	@Override
	public void setTitle(String title) {

		// See LEP-2188

		ThemeDisplay themeDisplay =
			(ThemeDisplay)portletRequestImpl.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String localizedCustomTitle = PortletConfigurationUtil.getPortletTitle(
			portletDisplay.getPortletName(),
			portletDisplay.getPortletPreferences(),
			themeDisplay.getLanguageId());

		if (Validator.isNull(localizedCustomTitle)) {
			String siteDefaultLanguageId = LocaleUtil.toLanguageId(
				themeDisplay.getSiteDefaultLocale());

			localizedCustomTitle = PortletConfigurationUtil.getPortletTitle(
				portletDisplay.getPortletName(),
				portletDisplay.getPortletPreferences(), siteDefaultLanguageId);
		}

		if (portletDisplay.isActive() &&
			Validator.isNull(localizedCustomTitle)) {

			_title = title;
		}
		else {
			_title = localizedCustomTitle;
		}

		portletDisplay.setTitle(_title);
	}

	@Override
	public void setUseDefaultTemplate(Boolean useDefaultTemplate) {
		_useDefaultTemplate = useDefaultTemplate;
	}

	private String _resourceName;
	private String _title;
	private Boolean _useDefaultTemplate;

}