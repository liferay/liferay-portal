/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.portlet;

import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.wish.list.constants.CommerceWishListConstants;
import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.commerce.wish.list.util.CommerceWishListHttpHelper;
import com.liferay.commerce.wish.list.web.internal.display.context.CommerceWishListDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-my-commerce-wish-lists",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Wish Lists",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/my_wish_lists/view.jsp",
		"jakarta.portlet.name=" + CommerceWishListPortletKeys.MY_COMMERCE_WISH_LISTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.supported-public-render-parameter=commerceWishListId",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MyCommerceWishListsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CommerceWishListDisplayContext commerceWishListDisplayContext =
			new CommerceWishListDisplayContext(
				commerceProductPriceCalculation, _commerceProductViewPermission,
				commerceWishListHttpHelper, commerceWishListItemService,
				commerceWishListService, cpDefinitionHelper, cpInstanceHelper,
				portal.getHttpServletRequest(renderRequest),
				_portletResourcePermission);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commerceWishListDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected CommerceProductPriceCalculation commerceProductPriceCalculation;

	@Reference
	protected CommerceWishListHttpHelper commerceWishListHttpHelper;

	@Reference
	protected CommerceWishListItemService commerceWishListItemService;

	@Reference
	protected CommerceWishListService commerceWishListService;

	@Reference
	protected CPDefinitionHelper cpDefinitionHelper;

	@Reference
	protected CPInstanceHelper cpInstanceHelper;

	@Reference
	protected Portal portal;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference(
		target = "(resource.name=" + CommerceWishListConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}