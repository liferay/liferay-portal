/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.content.web.internal.portlet.action;

import com.liferay.commerce.address.content.web.internal.display.context.CommerceAddressDisplayContext;
import com.liferay.commerce.address.content.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.exception.NoSuchAddressException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ADDRESS_CONTENT,
		"mvc.command.name=/commerce_address_content/edit_commerce_address"
	},
	service = MVCRenderCommand.class
)
public class EditCommerceAddressMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CommerceAddressDisplayContext commerceAddressDisplayContext =
				new CommerceAddressDisplayContext(
					_actionHelper, _commerceAccountHelper,
					_commerceAddressService, _countryService,
					_groupLocalService,
					_portal.getHttpServletRequest(renderRequest),
					_regionService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, commerceAddressDisplayContext);

			_setCommerceAddressRequestAttribute(renderRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchAddressException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/edit_commerce_address.jsp";
	}

	private void _setCommerceAddressRequestAttribute(
			RenderRequest renderRequest)
		throws PortalException {

		CommerceAddress commerceAddress = _actionHelper.getCommerceAddress(
			renderRequest);

		renderRequest.setAttribute(
			CommerceWebKeys.COMMERCE_ADDRESS, commerceAddress);
	}

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CountryService _countryService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RegionService _regionService;

}