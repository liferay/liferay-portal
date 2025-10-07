/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.site.cms.site.initializer.constants.BulkActionExecutionStatusConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Ivica Cardic
 */
public class ViewBulkActionTaskReportSuccessfulItemsDisplayContext {

	public ViewBulkActionTaskReportSuccessfulItemsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		ObjectEntry objectEntry = (ObjectEntry)_httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (objectEntry == null) {
			return null;
		}

		return StringBundler.concat(
			"/o/cms/bulk-action-task-items?filter=executionStatus eq '",
			BulkActionExecutionStatusConstants.COMPLETED,
			"' and r_bulkActionTaskToBulkActionTaskItems_c_bulkActionTaskId ",
			"eq '", objectEntry.getObjectEntryId(), "'");
	}

	private final HttpServletRequest _httpServletRequest;

}