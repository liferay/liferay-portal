/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action;

import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.web.internal.constants.NotificationWebKeys;
import com.liferay.notification.web.internal.display.context.ViewNotificationTemplatesDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		"mvc.command.name=/notification_templates/edit_notification_template"
	},
	service = MVCRenderCommand.class
)
public class EditNotificationTemplateMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			NotificationWebKeys.NOTIFICATION_TEMPLATE_TYPE,
			ParamUtil.getString(renderRequest, "notificationTemplateType"));
		renderRequest.setAttribute(
			NotificationWebKeys.NOTIFICATION_TEMPLATES,
			_notificationTemplateLocalService.fetchNotificationTemplate(
				ParamUtil.getLong(renderRequest, "notificationTemplateId")));
		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ViewNotificationTemplatesDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_notificationTemplateModelResourcePermission));

		return "/notification_templates/edit_notification_template.jsp";
	}

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.notification.model.NotificationTemplate)"
	)
	private ModelResourcePermission<NotificationTemplate>
		_notificationTemplateModelResourcePermission;

	@Reference
	private Portal _portal;

}