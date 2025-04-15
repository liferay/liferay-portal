/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.display.context;

import com.liferay.layout.set.prototype.configuration.LayoutSetPrototypeConfiguration;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Tamas Molnar
 */
public class PropagationMessageDisplayContext {

	public PropagationMessageDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public Map<String, Object> getData() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
				group.getClassPK());

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		boolean readyForPropagation = GetterUtil.getBoolean(
			settingsUnicodeProperties.getProperty("readyForPropagation"));

		return HashMapBuilder.<String, Object>put(
			"enableDisablePropagationURL",
			() -> PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					_httpServletRequest,
					LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"updateLayoutSetPrototypeAction"
			).setRedirect(
				PortalUtil.getLayoutURL(themeDisplay)
			).setParameter(
				"layoutSetPrototypeId",
				layoutSetPrototype.getLayoutSetPrototypeId()
			).setParameter(
				"readyForPropagation", !readyForPropagation
			).buildString()
		).put(
			"portletNamespace",
			PortalUtil.getPortletNamespace(
				LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE)
		).put(
			"readyForPropagation", readyForPropagation
		).put(
			"triggerPropagation",
			isTriggerPropagation(themeDisplay.getCompanyId())
		).build();
	}

	protected boolean isTriggerPropagation(long companyId) {
		try {
			LayoutSetPrototypeConfiguration layoutSetPrototypeConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					LayoutSetPrototypeConfiguration.class, companyId);

			return layoutSetPrototypeConfiguration.triggerPropagation();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PropagationMessageDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;

}