/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.PatcherAccountLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/get_accounts"
	},
	service = MVCResourceCommand.class
)
public class GetAccountsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String accountEntryCode = ParamUtil.getString(
			resourceRequest, "accountEntryCode");

		List<PatcherAccount> patcherAccounts =
			_patcherAccountLocalService.getPatcherAccounts(
				themeDisplay.getCompanyId(), accountEntryCode,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (PatcherAccount patcherAccount : patcherAccounts) {
			jsonArray.put(
				JSONUtil.put(
					"accountEntryCode", patcherAccount.getAccountEntryCode()
				).put(
					"accountEntryId", patcherAccount.getAccountEntryId()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PatcherAccountLocalService _patcherAccountLocalService;

}