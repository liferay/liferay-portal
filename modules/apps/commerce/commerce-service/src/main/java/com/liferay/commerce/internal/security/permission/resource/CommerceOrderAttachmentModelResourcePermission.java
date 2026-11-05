/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.security.permission.resource;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceOrderAttachment",
	service = ModelResourcePermission.class
)
public class CommerceOrderAttachmentModelResourcePermission
	implements ModelResourcePermission<CommerceOrderAttachment> {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			CommerceOrderAttachment commerceOrderAttachment, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, commerceOrderAttachment, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommerceOrderAttachment.class.getName(),
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long commerceOrderAttachmentId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, commerceOrderAttachmentId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommerceOrderAttachment.class.getName(),
				commerceOrderAttachmentId, actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			CommerceOrderAttachment commerceOrderAttachment, String actionId)
		throws PortalException {

		if (!_commerceOrderModelResourcePermission.contains(
				permissionChecker, commerceOrderAttachment.getCommerceOrderId(),
				ActionKeys.VIEW)) {

			return false;
		}

		if (commerceOrderAttachment.isRestricted()) {
			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commerceOrderAttachment.getCommerceOrderId());

			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			PortletResourcePermission portletResourcePermission =
				_commerceOrderModelResourcePermission.
					getPortletResourcePermission();

			if (!portletResourcePermission.contains(
					permissionChecker, accountEntry.getAccountEntryGroupId(),
					CommerceOrderActionKeys.
						VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS)) {

				return false;
			}
		}

		if (permissionChecker.hasPermission(
				null, CommerceOrderAttachment.class.getName(), 0, actionId) ||
			permissionChecker.hasOwnerPermission(
				commerceOrderAttachment.getCompanyId(),
				CommerceOrderAttachment.class.getName(),
				commerceOrderAttachment.getCommerceOrderAttachmentId(),
				commerceOrderAttachment.getUserId(), actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			commerceOrderAttachment.getGroupId(),
			CommerceOrderAttachment.class.getName(),
			commerceOrderAttachment.getCommerceOrderAttachmentId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long commerceOrderAttachmentId,
			String actionId)
		throws PortalException {

		CommerceOrderAttachment commerceOrderAttachment =
			_commerceOrderAttachmentLocalService.fetchCommerceOrderAttachment(
				commerceOrderAttachmentId);

		if (commerceOrderAttachment == null) {
			return false;
		}

		return contains(permissionChecker, commerceOrderAttachment, actionId);
	}

	@Override
	public String getModelName() {
		return CommerceOrderAttachment.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _commerceOrderModelResourcePermission.
			getPortletResourcePermission();
	}

	@Reference
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

}