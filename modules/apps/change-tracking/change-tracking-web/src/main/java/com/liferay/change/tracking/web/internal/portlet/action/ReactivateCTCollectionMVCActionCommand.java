/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noor Najjar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/reactivate_ct_collection"
	},
	service = MVCActionCommand.class
)
public class ReactivateCTCollectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = _companyLocalService.getCompany(
			themeDisplay.getCompanyId());

		CTSchemaVersion latestCTSchemaVersion =
			_ctSchemaVersionLocalService.getLatestCTSchemaVersion(
				company.getCompanyId());

		long ctCollectionId = ParamUtil.getLong(
			actionRequest, "ctCollectionId");

		CTCollection expiredCTCollection =
			_ctCollectionLocalService.getCTCollection(ctCollectionId);

		expiredCTCollection.setSchemaVersionId(
			latestCTSchemaVersion.getSchemaVersionId());
		expiredCTCollection.setStatus(WorkflowConstants.STATUS_DRAFT);

		_ctCollectionLocalService.updateCTCollection(expiredCTCollection);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

}