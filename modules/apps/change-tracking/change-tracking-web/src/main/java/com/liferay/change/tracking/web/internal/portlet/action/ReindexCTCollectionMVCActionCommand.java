/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/reindex_ct_collection"
	},
	service = MVCActionCommand.class
)
public class ReindexCTCollectionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			actionRequest, "ctCollectionId");

		if (ctCollectionId > 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			CTCollectionPermission.check(
				themeDisplay.getPermissionChecker(), ctCollectionId,
				ActionKeys.UPDATE);

			List<CTEntry> ctEntries =
				_ctEntryLocalService.getCTCollectionCTEntries(ctCollectionId);

			_indexer.reindex(ctEntries);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			hideDefaultSuccessMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference(
		target = "(indexer.class.name=com.liferay.change.tracking.model.CTEntry)"
	)
	private Indexer<CTEntry> _indexer;

}