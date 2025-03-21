/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Ambrín Chaudhary
 */
public class MBEditMessageDisplayContext {

	public MBEditMessageDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, MBMessage message,
		TrashHelper trashHelper) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_message = message;
		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getMBPortletComponentContext() throws Exception {
		long messageId = BeanParamUtil.getLong(
			_message, _liferayPortletRequest, "messageId");

		Map<String, Object> taglibContext = HashMapBuilder.<String, Object>put(
			"constants",
			HashMapBuilder.<String, Object>put(
				"ACTION_PUBLISH", WorkflowConstants.ACTION_PUBLISH
			).put(
				"ACTION_SAVE_DRAFT", WorkflowConstants.ACTION_SAVE_DRAFT
			).put(
				"CMD", Constants.CMD
			).build()
		).put(
			"currentAction",
			(_message == null) ? Constants.ADD : Constants.UPDATE
		).put(
			"messageId", (_message != null) ? messageId : null
		).put(
			"rootNodeId",
			_liferayPortletResponse.getNamespace() + "mbEditPageContainer"
		).put(
			"trashEnabled",
			() -> _trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId())
		).build();

		if (_message != null) {
			ResourceURL getAttachmentsURL =
				_liferayPortletResponse.createResourceURL();

			getAttachmentsURL.setParameter(
				"messageId", String.valueOf(_message.getMessageId()));
			getAttachmentsURL.setResourceID("/message_boards/get_attachments");

			taglibContext.put(
				"getAttachmentsURL", getAttachmentsURL.toString());

			taglibContext.put(
				"viewTrashAttachmentsURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/message_boards/view_deleted_message_attachments"
				).setRedirect(
					PortletURLUtil.getCurrent(
						_liferayPortletRequest, _liferayPortletResponse)
				).setParameter(
					"messageId", _message.getMessageId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
		}

		return taglibContext;
	}

	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private MBMessage _message;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}