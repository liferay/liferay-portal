/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.portlet.configuration.icon;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "jakarta.portlet.name=" + KaleoFormsPortletKeys.KALEO_FORMS_ADMIN,
	service = PortletConfigurationIcon.class
)
public class FieldSetsPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.format(
			getLocale(portletRequest), "manage-x",
			_language.get(getLocale(portletRequest), "field-sets"));
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				portletRequest,
				PortletProviderUtil.getPortletId(
					DDMStructure.class.getName(), PortletProvider.Action.VIEW),
				themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/view.jsp"
		).setBackURL(
			themeDisplay.getURLCurrent()
		).setParameter(
			"groupId", themeDisplay.getScopeGroupId()
		).setParameter(
			"refererPortletName", KaleoFormsPortletKeys.KALEO_FORMS_ADMIN
		).setParameter(
			"refererWebDAVToken",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				Portlet portlet = _portletLocalService.getPortletById(
					portletDisplay.getId());

				return WebDAVUtil.getStorageToken(portlet);
			}
		).setParameter(
			"showAncestorScopes", true
		).setParameter(
			"showManageTemplates", true
		).buildString();
	}

	@Override
	public double getWeight() {
		return 102;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		return !user.isGuestUser();
	}

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

}