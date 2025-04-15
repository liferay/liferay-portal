/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.groovy.script.use;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

/**
 * @author Feliphe Marinho
 */
public class ObjectDefinitionGroovyScriptUseSourceURLFactory {

	public static String create(
			Company company, long objectDefinitionId, Portal portal,
			String screenNavigationCategoryKey)
		throws PortalException {

		String url = StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		url = HttpComponentsUtil.addParameter(
			url, "p_p_id", ObjectPortletKeys.OBJECT_DEFINITIONS);
		url = HttpComponentsUtil.addParameter(url, "p_p_lifecycle", "0");
		url = HttpComponentsUtil.addParameter(
			url, "p_p_state", WindowState.MAXIMIZED.toString());
		url = HttpComponentsUtil.addParameter(
			url, "p_p_mode", PortletMode.VIEW.toString());

		String namespace = portal.getPortletNamespace(
			ObjectPortletKeys.OBJECT_DEFINITIONS);

		url = HttpComponentsUtil.addParameter(
			url, namespace + "mvcRenderCommandName",
			"/object_definitions/edit_object_definition");
		url = HttpComponentsUtil.addParameter(
			url, namespace + "objectDefinitionId", objectDefinitionId);

		return HttpComponentsUtil.addParameter(
			url, namespace + "screenNavigationCategoryKey",
			screenNavigationCategoryKey);
	}

}