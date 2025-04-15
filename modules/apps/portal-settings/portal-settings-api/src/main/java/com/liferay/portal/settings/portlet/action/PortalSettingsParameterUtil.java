/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.portlet.action;

import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

/**
 * @author Michael C. Han
 */
public class PortalSettingsParameterUtil {

	public static boolean getBoolean(
		ActionRequest actionRequest,
		PortalSettingsFormContributor portalSettingsFormContributor,
		String name) {

		return ParamUtil.getBoolean(
			actionRequest,
			portalSettingsFormContributor.getParameterNamespace() + name);
	}

	public static String getString(
		ActionRequest actionRequest,
		PortalSettingsFormContributor portalSettingsFormContributor,
		String name) {

		return ParamUtil.getString(
			actionRequest,
			portalSettingsFormContributor.getParameterNamespace() + name);
	}

}