/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.constants.MBThreadConstants;
import com.liferay.message.boards.exception.MessageBodyException;
import com.liferay.message.boards.exception.MessageSubjectException;
import com.liferay.message.boards.exception.NoSuchThreadException;
import com.liferay.message.boards.exception.RequiredMessageException;
import com.liferay.message.boards.exception.SplitThreadException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.service.MBThreadService;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.web.internal.util.MBRequestUtil;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.InputStream;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/split_thread"
	},
	service = MVCActionCommand.class
)
public class SplitThreadMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_splitThread(actionRequest, actionResponse);
		}
		catch (PrincipalException | RequiredMessageException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/message_boards/error.jsp");
		}
		catch (MessageBodyException | MessageSubjectException |
			   NoSuchThreadException | SplitThreadException exception) {

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private void _splitThread(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		String splitThreadSubject = ParamUtil.getString(
			actionRequest, "splitThreadSubject");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			MBThread.class.getName(), actionRequest);

		MBMessage message = _mbMessageLocalService.getMessage(messageId);

		MBThread newThread = _mbThreadService.splitThread(
			messageId, splitThreadSubject, serviceContext);

		boolean addExplanationPost = ParamUtil.getBoolean(
			actionRequest, "addExplanationPost");

		if (addExplanationPost) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long oldParentMessageId = message.getParentMessageId();
			String subject = ParamUtil.getString(actionRequest, "subject");
			String body = ParamUtil.getString(actionRequest, "body");

			MBGroupServiceSettings mbGroupServiceSettings =
				MBRequestUtil.getMBGroupServiceSettings(
					_portal.getHttpServletRequest(actionRequest),
					themeDisplay.getScopeGroupId());

			String layoutFullURL = _portal.getLayoutFullURL(themeDisplay);

			String newThreadURL =
				layoutFullURL + "/-/message_boards/view_message/" +
					message.getMessageId();

			body = StringUtil.replace(
				body, MBThreadConstants.NEW_THREAD_URL, newThreadURL);

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			_mbMessageService.addMessage(
				oldParentMessageId, subject, body,
				mbGroupServiceSettings.getMessageFormat(),
				Collections.<ObjectValuePair<String, InputStream>>emptyList(),
				false, MBThreadConstants.PRIORITY_NOT_GIVEN,
				message.isAllowPingbacks(), serviceContext);
		}

		LiferayActionResponse liferayActionResponse =
			(LiferayActionResponse)actionResponse;

		actionResponse.sendRedirect(
			PortletURLBuilder.createRenderURL(
				liferayActionResponse
			).setMVCRenderCommandName(
				"/message_boards/view_message"
			).setParameter(
				"messageId", newThread.getRootMessageId()
			).buildString());
	}

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private MBThreadService _mbThreadService;

	@Reference
	private Portal _portal;

}