/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.portlet.action;

import com.liferay.commerce.notification.exception.NoSuchNotificationQueueEntryException;
import com.liferay.commerce.notification.service.CommerceNotificationQueueEntryService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_notification_queue_entry"
	},
	service = MVCActionCommand.class
)
public class EditCommerceNotificationQueueEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceNotificationQueues(actionRequest);
			}
			else if (cmd.equals("resend")) {
				_resendCommerceNotificationQueueEntry(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNotificationQueueEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceNotificationQueues(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceNotificationQueueEntryIds = null;

		long commerceNotificationQueueEntryId = ParamUtil.getLong(
			actionRequest, "commerceNotificationQueueEntryId");

		if (commerceNotificationQueueEntryId > 0) {
			deleteCommerceNotificationQueueEntryIds = new long[] {
				commerceNotificationQueueEntryId
			};
		}
		else {
			deleteCommerceNotificationQueueEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceNotificationQueueEntryIds"),
				0L);
		}

		for (long deleteCommerceNotificationQueueEntryId :
				deleteCommerceNotificationQueueEntryIds) {

			_commerceNotificationQueueEntryService.
				deleteCommerceNotificationQueueEntry(
					deleteCommerceNotificationQueueEntryId);
		}
	}

	private void _resendCommerceNotificationQueueEntry(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceNotificationQueueEntryId = ParamUtil.getLong(
			actionRequest, "commerceNotificationQueueEntryId");

		_commerceNotificationQueueEntryService.
			resendCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	@Reference
	private CommerceNotificationQueueEntryService
		_commerceNotificationQueueEntryService;

}