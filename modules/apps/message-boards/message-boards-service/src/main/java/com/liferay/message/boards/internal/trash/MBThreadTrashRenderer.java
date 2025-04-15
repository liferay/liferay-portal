/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.trash;

import com.liferay.asset.constants.AssetWebKeys;
import com.liferay.asset.util.AssetHelper;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.MBTreeWalker;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseJSPTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Zsolt Berentey
 */
public class MBThreadTrashRenderer extends BaseJSPTrashRenderer {

	public static final String TYPE = "message_thread";

	public MBThreadTrashRenderer(MBThread thread, AssetHelper assetHelper)
		throws PortalException {

		_thread = thread;
		_assetHelper = assetHelper;

		_rootMessage = MBMessageLocalServiceUtil.getMBMessage(
			thread.getRootMessageId());
	}

	@Override
	public String getClassName() {
		return MBThread.class.getName();
	}

	@Override
	public long getClassPK() {
		return _thread.getPrimaryKey();
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/message_boards/view_message_content.jsp";
	}

	@Override
	public String getPortletId() {
		return MBPortletKeys.MESSAGE_BOARDS;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return HtmlUtil.stripHtml(_rootMessage.getSubject());
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			AssetWebKeys.ASSET_HELPER, _assetHelper);

		MBMessageDisplay messageDisplay =
			MBMessageServiceUtil.getMessageDisplay(
				_rootMessage.getMessageId(), WorkflowConstants.STATUS_IN_TRASH);

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_MESSAGE_DISPLAY, messageDisplay);

		MBTreeWalker treeWalker = messageDisplay.getTreeWalker();

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER, treeWalker);

		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY,
			messageDisplay.getCategory());
		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE,
			treeWalker.getRoot());
		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, Integer.valueOf(0));
		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.FALSE);
		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, _rootMessage);
		httpServletRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD,
			messageDisplay.getThread());

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final AssetHelper _assetHelper;
	private final MBMessage _rootMessage;
	private final MBThread _thread;

}