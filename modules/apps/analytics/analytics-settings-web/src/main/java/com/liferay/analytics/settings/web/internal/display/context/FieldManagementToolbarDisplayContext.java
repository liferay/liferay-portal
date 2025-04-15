/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rachael Koestartyo
 */
public class FieldManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public FieldManagementToolbarDisplayContext(
		FieldDisplayContext fieldDisplayContext,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			fieldDisplayContext.getFieldSearch());

		_fieldDisplayContext = fieldDisplayContext;
	}

	@Override
	public String getComponentId() {
		if (StringUtil.equalsIgnoreCase(
				_fieldDisplayContext.getMVCRenderCommandName(),
				"/analytics_settings/edit_synced_contacts_fields")) {

			return "contactsFieldsManagementToolbar";
		}

		return "usersFieldsManagementToolbar";
	}

	@Override
	public String getSearchContainerId() {
		if (StringUtil.equalsIgnoreCase(
				_fieldDisplayContext.getMVCRenderCommandName(),
				"/analytics_settings/edit_synced_contacts_fields")) {

			return "selectContactsFields";
		}

		return "selectUsersFields";
	}

	private final FieldDisplayContext _fieldDisplayContext;

}