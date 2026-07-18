/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.service.base.CommerceOrderAttachmentServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrderAttachment"
	},
	service = AopService.class
)
public class CommerceOrderAttachmentServiceImpl
	extends CommerceOrderAttachmentServiceBaseImpl {

	@Override
	public CommerceOrderAttachment addCommerceOrderAttachment(
			long commerceOrderId, double priority, boolean restricted,
			String title, String type, String fileName, InputStream inputStream)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_ATTACHMENT);
		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		return commerceOrderAttachmentLocalService.addCommerceOrderAttachment(
			null, getUserId(), commerceOrderId, priority, restricted, title,
			type, fileName, inputStream);
	}

	@Override
	public void deleteCommerceOrderAttachment(long commerceOrderAttachmentId)
		throws PortalException {

		_commerceOrderAttachmentModelResourcePermission.check(
			getPermissionChecker(), commerceOrderAttachmentId,
			ActionKeys.DELETE);

		commerceOrderAttachmentLocalService.deleteCommerceOrderAttachment(
			commerceOrderAttachmentId);
	}

	@Override
	public CommerceOrderAttachment fetchCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException {

		CommerceOrderAttachment commerceOrderAttachment =
			commerceOrderAttachmentPersistence.fetchByPrimaryKey(
				commerceOrderAttachmentId);

		if (commerceOrderAttachment == null) {
			return null;
		}

		_commerceOrderAttachmentModelResourcePermission.check(
			getPermissionChecker(), commerceOrderAttachment, ActionKeys.VIEW);

		return commerceOrderAttachment;
	}

	@Override
	public CommerceOrderAttachment
			fetchCommerceOrderAttachmentByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceOrderAttachment commerceOrderAttachment =
			commerceOrderAttachmentLocalService.
				fetchCommerceOrderAttachmentByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceOrderAttachment == null) {
			return null;
		}

		_commerceOrderAttachmentModelResourcePermission.check(
			getPermissionChecker(), commerceOrderAttachment, ActionKeys.VIEW);

		return commerceOrderAttachment;
	}

	@Override
	public CommerceOrderAttachment getCommerceOrderAttachment(
			long commerceOrderAttachmentId)
		throws PortalException {

		_commerceOrderAttachmentModelResourcePermission.check(
			getPermissionChecker(), commerceOrderAttachmentId, ActionKeys.VIEW);

		return commerceOrderAttachmentPersistence.findByPrimaryKey(
			commerceOrderAttachmentId);
	}

	@Override
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
			long commerceOrderId, boolean restricted, int start, int end,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		if (restricted) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
		}

		return commerceOrderAttachmentPersistence.findByC_R(
			commerceOrderId, restricted, start, end, orderByComparator);
	}

	@Override
	public List<CommerceOrderAttachment> getCommerceOrderAttachments(
			long commerceOrderId, int start, int end,
			OrderByComparator<CommerceOrderAttachment> orderByComparator)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		if (_commerceOrderModelResourcePermission.contains(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS)) {

			return commerceOrderAttachmentLocalService.
				getCommerceOrderAttachments(
					commerceOrderId, start, end, orderByComparator);
		}

		return commerceOrderAttachmentPersistence.findByC_R(
			commerceOrderId, false, start, end, orderByComparator);
	}

	@Override
	public int getCommerceOrderAttachmentsCount(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		if (_commerceOrderModelResourcePermission.contains(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS)) {

			return commerceOrderAttachmentLocalService.
				getCommerceOrderAttachmentsCount(commerceOrderId);
		}

		return commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentsCount(commerceOrderId, false);
	}

	@Override
	public int getCommerceOrderAttachmentsCount(
			long commerceOrderId, boolean restricted)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		if (restricted) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.
					VIEW_RESTRICTED_COMMERCE_ORDER_ATTACHMENTS);
		}

		return commerceOrderAttachmentLocalService.
			getCommerceOrderAttachmentsCount(commerceOrderId, restricted);
	}

	@Override
	public CommerceOrderAttachment updateCommerceOrderAttachment(
			long commerceOrderAttachmentId, double priority, boolean restricted,
			String title, String type)
		throws PortalException {

		_commerceOrderAttachmentModelResourcePermission.check(
			getPermissionChecker(), commerceOrderAttachmentId,
			ActionKeys.UPDATE);

		return commerceOrderAttachmentLocalService.
			updateCommerceOrderAttachment(
				commerceOrderAttachmentId, priority, restricted, title, type);
	}

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

}