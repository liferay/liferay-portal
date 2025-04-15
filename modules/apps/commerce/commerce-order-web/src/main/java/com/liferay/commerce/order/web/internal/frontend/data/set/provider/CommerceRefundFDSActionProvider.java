/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.web.internal.model.Refund;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.REFUNDS,
	service = FDSActionProvider.class
)
public class CommerceRefundFDSActionProvider implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		Refund refund = (Refund)model;

		return DropdownItemListBuilder.add(
			() -> _commercePaymentEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), refund.getId(),
				ActionKeys.VIEW),
			dropdownItem -> {
				dropdownItem.setHref(
					_getRefundEditURL(refund.getId(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, Constants.VIEW));
			}
		).build();
	}

	private PortletURL _getRefundEditURL(
		long commercePaymentEntryId, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CommercePortletKeys.COMMERCE_PAYMENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_payment/edit_commerce_payment_entry"
		).setBackURL(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commercePaymentEntryId", commercePaymentEntryId
		).buildPortletURL();
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry)"
	)
	private ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}