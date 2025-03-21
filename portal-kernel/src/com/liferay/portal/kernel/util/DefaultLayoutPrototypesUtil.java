/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class DefaultLayoutPrototypesUtil {

	public static Layout addLayout(
			LayoutSet layoutSet, String nameKey, String friendlyURL,
			String layoutTemplateId)
		throws Exception {

		Group group = layoutSet.getGroup();

		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			nameMap.put(locale, LanguageUtil.get(locale, nameKey));
		}

		Layout layout = LayoutLocalServiceUtil.addLayout(
			null, group.getCreatorUserId(), group.getGroupId(),
			layoutSet.isPrivateLayout(),
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			null, LayoutConstants.TYPE_PORTLET, StringPool.BLANK, false,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), friendlyURL
			).build(),
			new ServiceContext());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(0, layoutTemplateId, false);

		return layout;
	}

	public static String addPortletId(
			Layout layout, String portletId, String columnId)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		portletId = layoutTypePortlet.addPortletId(
			0, portletId, columnId, -1, false);

		updateLayout(layout);

		addResourcePermissions(layout, portletId);

		return portletId;
	}

	public static PortletPreferences updatePortletSetup(
			Layout layout, String portletId, Map<String, String> preferences)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layout, portletId);

		for (Map.Entry<String, String> entry : preferences.entrySet()) {
			portletPreferences.setValue(entry.getKey(), entry.getValue());
		}

		portletPreferences.store();

		return portletPreferences;
	}

	protected static void addResourcePermissions(
			Layout layout, String portletId)
		throws Exception {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			layout.getCompanyId(), portletId);

		PortalUtil.addPortletDefaultResource(
			layout.getCompanyId(), layout, portlet);
	}

	protected static void updateLayout(Layout layout) throws Exception {
		LayoutLocalServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

}