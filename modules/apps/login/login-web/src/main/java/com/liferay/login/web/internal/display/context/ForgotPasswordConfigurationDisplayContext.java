/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Istvan Sajtos
 */
public class ForgotPasswordConfigurationDisplayContext {

	public ForgotPasswordConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		PortletPreferences portletPreferences, RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_portletPreferences = portletPreferences;
		_renderRequest = renderRequest;
	}

	public List<TabsItem> getTabsItems() {
		TabsItemList tabsItemList = TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "email-from"));
			}
		).build();

		String emailPasswordSentSubject =
			LocalizationUtil.getLocalizationXmlFromPreferences(
				_portletPreferences, _renderRequest, "emailPasswordSentSubject",
				"preferences", StringPool.BLANK);
		String emailPasswordSentBody =
			LocalizationUtil.getLocalizationXmlFromPreferences(
				_portletPreferences, _renderRequest, "emailPasswordSentBody",
				"preferences", StringPool.BLANK);

		if (Validator.isNotNull(emailPasswordSentSubject) ||
			Validator.isNotNull(emailPasswordSentBody)) {

			tabsItemList.add(
				tabsItem -> tabsItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "password-changed-notification")));
		}

		tabsItemList.add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "password-reset-notification")));

		return tabsItemList;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletPreferences _portletPreferences;
	private final RenderRequest _renderRequest;

}