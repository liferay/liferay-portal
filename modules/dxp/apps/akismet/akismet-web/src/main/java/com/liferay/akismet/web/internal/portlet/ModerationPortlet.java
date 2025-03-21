/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.akismet.web.internal.portlet;

import com.liferay.akismet.client.AkismetClient;
import com.liferay.akismet.web.internal.constants.ModerationPortletKeys;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.exception.RequiredMessageException;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jamie
 */
@Component(
	enabled = false,
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"jakarta.portlet.display-name=Spam Moderation",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ModerationPortletKeys.MODERATION,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ModerationPortlet extends MVCPortlet {

	public void deleteMBMessages(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] mbMessageIds = ParamUtil.getLongValues(
			actionRequest, "deleteMBMessageIds");

		for (long mbMessageId : mbMessageIds) {
			_mbMessageService.deleteMessage(mbMessageId);
		}
	}

	public void markNotSpamMBMessages(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] mbMessageIds = ParamUtil.getLongValues(
			actionRequest, "notSpamMBMessageIds");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		for (long mbMessageId : mbMessageIds) {
			MBMessage mbMessage = _mbMessageLocalService.updateStatus(
				themeDisplay.getUserId(), mbMessageId,
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<>());

			_akismetClient.submitHam(mbMessage);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof NoSuchMessageException ||
			throwable instanceof PrincipalException ||
			throwable instanceof RequiredMessageException) {

			return true;
		}

		return false;
	}

	@Reference
	private AkismetClient _akismetClient;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private MBMessageService _mbMessageService;

}