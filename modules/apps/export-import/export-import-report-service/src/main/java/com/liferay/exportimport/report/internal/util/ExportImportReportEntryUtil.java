/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.util;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

/**
 * @author Petteri Karttunen
 */
public class ExportImportReportEntryUtil {

	public static int getOrigin() {
		if (BatchEngineThreadLocal.isBatchImportInProcess()) {
			return ExportImportReportEntryConstants.ORIGIN_BATCH;
		}

		return ExportImportReportEntryConstants.ORIGIN_STAGING;
	}

	public static String getScope(Group group) {
		if ((group == null) || group.isCompany()) {
			return ObjectDefinitionConstants.SCOPE_COMPANY;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isCompanyGroup(group)) {
			return ObjectDefinitionConstants.SCOPE_COMPANY;
		}

		return ObjectDefinitionConstants.SCOPE_SITE;
	}

	public static String getScope(long groupId) {
		if (groupId == 0) {
			return ObjectDefinitionConstants.SCOPE_COMPANY;
		}

		return getScope(GroupLocalServiceUtil.fetchGroup(groupId));
	}

	public static String getScopeKey(Group group) {
		if (group == null) {
			return null;
		}

		return group.getExternalReferenceCode();
	}

	public static String getScopeKey(long groupId) {
		if (groupId == 0) {
			return null;
		}

		return getScopeKey(GroupLocalServiceUtil.fetchGroup(groupId));
	}

}