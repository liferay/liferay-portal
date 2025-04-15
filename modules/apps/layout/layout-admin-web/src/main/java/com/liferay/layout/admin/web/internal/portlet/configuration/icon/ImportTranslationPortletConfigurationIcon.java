/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.configuration.icon;

/**
 * @author Adolfo Pérez
 */
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
	service = PortletConfigurationIcon.class
)
public class ImportTranslationPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "import-translations");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			return PortletURLBuilder.create(
				_translationURLProvider.getImportTranslationURL(
					_getGroupId(portletRequest),
					_portal.getClassNameId(Layout.class.getName()),
					RequestBackedPortletURLFactoryUtil.create(portletRequest))
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				portletDisplay.getId()
			).setParameter(
				"backURLTitle", portletDisplay.getPortletDisplayName()
			).buildString();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return true;
	}

	private long _getGroupId(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroupId();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TranslationURLProvider _translationURLProvider;

}