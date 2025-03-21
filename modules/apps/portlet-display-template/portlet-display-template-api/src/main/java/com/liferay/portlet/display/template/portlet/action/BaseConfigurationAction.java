/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ReadOnlyException;

/**
 * @author Lourdes Fernández Besada
 */
public abstract class BaseConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	protected void postProcess(
			long companyId, PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group group = GroupLocalServiceUtil.fetchGroup(
			themeDisplay.getCompanyId(),
			getParameter(portletRequest, "displayStyleGroupKey"));

		try {
			if ((group != null) &&
				(group.getGroupId() != themeDisplay.getScopeGroupId())) {

				portletPreferences.setValue(
					"displayStyleGroupExternalReferenceCode",
					group.getExternalReferenceCode());
			}
			else {
				portletPreferences.reset(
					"displayStyleGroupExternalReferenceCode");
			}
		}
		catch (ReadOnlyException readOnlyException) {
			throw new SystemException(readOnlyException);
		}
	}

}