/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.web.internal.display.context.CommerceOrderAttachmentsDisplayContext;
import com.liferay.commerce.order.web.internal.display.context.CommerceOrderEditDisplayContext;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = "screen.navigation.entry.order:Integer=45",
	service = ScreenNavigationEntry.class
)
public class CommerceOrderAttachmentsScreenNavigationEntry
	extends CommerceOrderAttachmentsScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceOrder> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommerceOrderEditDisplayContext commerceOrderEditDisplayContext =
			(CommerceOrderEditDisplayContext)httpServletRequest.getAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT);

		CommerceOrder commerceOrder =
			commerceOrderEditDisplayContext.getCommerceOrder();

		CommerceOrderAttachmentsDisplayContext
			commerceOrderAttachmentsDisplayContext =
				new CommerceOrderAttachmentsDisplayContext(
					commerceOrder, _commerceOrderAttachmentLocalService,
					_commerceOrderAttachmentModelResourcePermission,
					_commerceOrderModelResourcePermission, _dsRequestManager,
					httpServletRequest, _language, _portal, _portletURLFactory,
					_userLocalService);

		httpServletRequest.setAttribute(
			CommerceOrderAttachmentsDisplayContext.class.getName(),
			commerceOrderAttachmentsDisplayContext);

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/commerce_order/attachments.jsp");
	}

	@Reference
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderAttachment)"
	)
	private ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private UserLocalService _userLocalService;

}