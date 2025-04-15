/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.internal.exportimport.portlet.preferences.processor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Lourdes Fernández Besada
 */
public class ExportImportPortletPreferencesProcessorUtil {

	public static String getDisplayStyle(
		Portlet portlet, PortletPreferences portletPreferences) {

		try {
			if (Validator.isNotNull(portlet.getTemplateHandlerInstance())) {
				return portletPreferences.getValue("displayStyle", null);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static long getDisplayStyleGroupId(
		long companyId, Portlet portlet,
		PortletPreferences portletPreferences) {

		try {
			if (Validator.isNull(portlet.getTemplateHandlerInstance())) {
				return 0;
			}

			String displayStyleGroupExternalReferenceCode =
				portletPreferences.getValue(
					"displayStyleGroupExternalReferenceCode", null);

			if (Validator.isNull(displayStyleGroupExternalReferenceCode)) {
				return 0;
			}

			Group group =
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					displayStyleGroupExternalReferenceCode, companyId);

			if (group != null) {
				return group.getGroupId();
			}

			return 0;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportPortletPreferencesProcessorUtil.class);

}