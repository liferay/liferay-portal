/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.security.permission.resource;

import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.service.CommercePaymentEntryAuditLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import jakarta.annotation.Resource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntryAudit",
	service = ModelResourcePermission.class
)
public class CommercePaymentEntryAuditModelResourcePermission
	implements ModelResourcePermission<CommercePaymentEntryAudit> {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			CommercePaymentEntryAudit commercePaymentEntryAudit,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, commercePaymentEntryAudit, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntryAudit.class.getName(),
				commercePaymentEntryAudit.getCommercePaymentEntryId(),
				actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker,
			long commercePaymentEntryAuditId, String actionId)
		throws PortalException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			_commercePaymentEntryAuditLocalService.getCommercePaymentEntryAudit(
				commercePaymentEntryAuditId);

		if (!contains(permissionChecker, commercePaymentEntryAudit, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntry.class.getName(),
				commercePaymentEntryAudit.getCommercePaymentEntryId(),
				actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			CommercePaymentEntryAudit commercePaymentEntryAudit,
			String actionId)
		throws PortalException {

		return _commercePaymentEntryModelResourcePermission.contains(
			permissionChecker,
			commercePaymentEntryAudit.getCommercePaymentEntryId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			long commercePaymentEntryAuditId, String actionId)
		throws PortalException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			_commercePaymentEntryAuditLocalService.getCommercePaymentEntryAudit(
				commercePaymentEntryAuditId);

		return _commercePaymentEntryModelResourcePermission.contains(
			permissionChecker,
			commercePaymentEntryAudit.getCommercePaymentEntryId(), actionId);
	}

	@Override
	public String getModelName() {
		return null;
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return null;
	}

	@Resource
	private CommercePaymentEntryAuditLocalService
		_commercePaymentEntryAuditLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry)"
	)
	private ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;

}