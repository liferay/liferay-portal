/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

/**
 * @author Veronica Gonzalez
 */
public class TasksSectionUtil {

	public static String getFDSName(long companyId) {
		ObjectDefinition cmpProjectObjectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", companyId);

		if (cmpProjectObjectDefinition == null) {
			return CMSSiteInitializerFDSNames.TASKS_SECTION;
		}

		return "com.liferay.content.marketing.platform.web-workflow-tasks";
	}

}