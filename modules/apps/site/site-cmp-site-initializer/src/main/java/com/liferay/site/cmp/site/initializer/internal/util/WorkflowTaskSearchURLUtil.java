/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

/**
 * @author Jhosseph Gonzalez
 */
public class WorkflowTaskSearchURLUtil {

	public static String getSearchURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
			KaleoTaskInstanceToken.class.getName(),
			"&filter=cmpTaskObjectEntryIds/any(x:x gt 0)");
	}

}