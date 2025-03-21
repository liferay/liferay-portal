/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.exception.LockedThreadException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryService;
import com.liferay.message.boards.service.MBThreadService;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/edit_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteEntries(actionRequest, false);
			}
			else if (cmd.equals(Constants.LOCK)) {
				lockThreads(actionRequest);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteEntries(actionRequest, true);
			}
			else if (cmd.equals(Constants.UNLOCK)) {
				unlockThreads(actionRequest);
			}
		}
		catch (LockedThreadException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/message_boards/error.jsp");
		}
	}

	protected void lockThreads(ActionRequest actionRequest) throws Exception {
		long[] threadIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsMBThread");

		for (long threadId : threadIds) {
			_mbThreadService.lockThread(threadId);
		}
	}

	protected void unlockThreads(ActionRequest actionRequest) throws Exception {
		long[] threadIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsMBThread");

		for (long threadId : threadIds) {
			_mbThreadService.unlockThread(threadId);
		}
	}

	private void _deleteEntries(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] threadIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsMBThread");

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long threadId : threadIds) {
			if (moveToTrash) {
				MBThread thread = _mbThreadService.moveThreadToTrash(threadId);

				trashedModels.add(thread);
			}
			else {
				_mbThreadService.deleteThread(threadId);
			}
		}

		long[] categoryIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsMBCategory");

		for (long categoryId : categoryIds) {
			if (moveToTrash) {
				MBCategory category = _mbCategoryService.moveCategoryToTrash(
					categoryId);

				trashedModels.add(category);
			}
			else {
				_mbCategoryService.deleteCategory(
					themeDisplay.getScopeGroupId(), categoryId);
			}
		}

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	@Reference
	private MBCategoryService _mbCategoryService;

	@Reference
	private MBThreadService _mbThreadService;

}