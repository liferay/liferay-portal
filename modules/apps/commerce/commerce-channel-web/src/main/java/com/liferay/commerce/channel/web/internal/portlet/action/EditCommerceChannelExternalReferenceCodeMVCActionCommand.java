/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_channel_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceChannelExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceOrderExternalReferenceCode(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceChannelExternalReferenceCodeException ||
				exception instanceof NoSuchChannelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _updateCommerceOrderExternalReferenceCode(
			ActionRequest actionRequest)
		throws Exception {

		long commerceChannelId = ParamUtil.getLong(
			actionRequest, "commerceChannelId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commerceChannelService.updateCommerceChannelExternalReferenceCode(
			externalReferenceCode, commerceChannelId);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

}