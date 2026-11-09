/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.util;

import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

/**
 * @author Jhosseph Gonzalez
 */
public class WorkflowTaskSearchURLUtil {

	public static String getSearchURL() {
		return "/o/search/v1.0/search?emptySearch=true&entryClassNames=" +
			KaleoTaskInstanceToken.class.getName();
	}

}