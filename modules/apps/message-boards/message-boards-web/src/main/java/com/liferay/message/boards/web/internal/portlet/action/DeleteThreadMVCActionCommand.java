/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.exception.LockedThreadException;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBThreadService;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Deepak Gothe
 * @author Sergio González
 * @author Zsolt Berentey
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/delete_thread"
	},
	service = MVCActionCommand.class
)
public class DeleteThreadMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteThreads(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteThreads(actionRequest, true);
			}
		}
		catch (LockedThreadException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/message_boards/error.jsp");
		}
	}

	private void _deleteThreads(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long[] deleteThreadIds = null;

		long threadId = ParamUtil.getLong(actionRequest, "threadId");

		if (threadId > 0) {
			deleteThreadIds = new long[] {threadId};
		}
		else {
			deleteThreadIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "threadIds"), 0L);
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteThreadId : deleteThreadIds) {
			if (moveToTrash) {
				MBThread thread = _mbThreadService.moveThreadToTrash(
					deleteThreadId);

				trashedModels.add(thread);
			}
			else {
				_mbThreadService.deleteThread(deleteThreadId);
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
	private MBThreadService _mbThreadService;

}