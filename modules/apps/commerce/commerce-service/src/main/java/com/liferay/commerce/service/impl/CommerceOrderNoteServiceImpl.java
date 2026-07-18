/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderNote;
import com.liferay.commerce.service.base.CommerceOrderNoteServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrderNote"
	},
	service = AopService.class
)
public class CommerceOrderNoteServiceImpl
	extends CommerceOrderNoteServiceBaseImpl {

	@Override
	public CommerceOrderNote addCommerceOrderNote(
			long commerceOrderId, String content, boolean restricted,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES;

		if (restricted) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, actionId);

		return commerceOrderNoteLocalService.addCommerceOrderNote(
			commerceOrderId, content, restricted, serviceContext);
	}

	@Override
	public CommerceOrderNote addOrUpdateCommerceOrderNote(
			String externalReferenceCode, long commerceOrderNoteId,
			long commerceOrderId, String content, boolean restricted,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES;

		CommerceOrderNote commerceOrderNote = null;

		if (commerceOrderNoteId > 0) {
			commerceOrderNote = commerceOrderNotePersistence.fetchByPrimaryKey(
				commerceOrderNoteId);
		}
		else {
			commerceOrderNote =
				commerceOrderNoteLocalService.
					fetchCommerceOrderNoteByExternalReferenceCode(
						externalReferenceCode, serviceContext.getCompanyId());
		}

		if (((commerceOrderNote != null) && commerceOrderNote.isRestricted()) ||
			restricted) {

			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, actionId);

		return commerceOrderNoteLocalService.addOrUpdateCommerceOrderNote(
			externalReferenceCode, commerceOrderNoteId, commerceOrderId,
			content, restricted, serviceContext);
	}

	@Override
	public void deleteCommerceOrderNote(long commerceOrderNoteId)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			commerceOrderNotePersistence.findByPrimaryKey(commerceOrderNoteId);

		String actionId = CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES;

		if (commerceOrderNote.isRestricted()) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderNote.getCommerceOrderId(),
			actionId);

		commerceOrderNoteLocalService.deleteCommerceOrderNote(
			commerceOrderNote);
	}

	@Override
	public CommerceOrderNote fetchCommerceOrderNote(long commerceOrderNoteId)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			commerceOrderNotePersistence.fetchByPrimaryKey(commerceOrderNoteId);

		_checkCommerceOrderNotePermissions(commerceOrderNote);

		return commerceOrderNote;
	}

	@Override
	public CommerceOrderNote fetchCommerceOrderNoteByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			commerceOrderNoteLocalService.
				fetchCommerceOrderNoteByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceOrderNote != null) {
			_commerceOrderModelResourcePermission.check(
				getPermissionChecker(), commerceOrderNote.getCommerceOrderId(),
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES);
		}

		return commerceOrderNote;
	}

	@Override
	public CommerceOrderNote getCommerceOrderNote(long commerceOrderNoteId)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			commerceOrderNotePersistence.findByPrimaryKey(commerceOrderNoteId);

		_checkCommerceOrderNotePermissions(commerceOrderNote);

		return commerceOrderNote;
	}

	@Override
	public List<CommerceOrderNote> getCommerceOrderNotes(
			long commerceOrderId, boolean restricted)
		throws PortalException {

		String actionId = ActionKeys.VIEW;

		if (restricted) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, actionId);

		return commerceOrderNoteLocalService.getCommerceOrderNotes(
			commerceOrderId, restricted);
	}

	@Override
	public List<CommerceOrderNote> getCommerceOrderNotes(
			long commerceOrderId, boolean restricted, int start, int end)
		throws PortalException {

		String actionId = ActionKeys.VIEW;

		if (restricted) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, actionId);

		return commerceOrderNotePersistence.findByC_R(
			commerceOrderId, restricted, start, end);
	}

	@Override
	public List<CommerceOrderNote> getCommerceOrderNotes(
			long commerceOrderId, int start, int end)
		throws PortalException {

		if (_commerceOrderModelResourcePermission.contains(
				getPermissionChecker(), commerceOrderId,
				CommerceOrderActionKeys.
					MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES)) {

			return commerceOrderNotePersistence.findByCommerceOrderId(
				commerceOrderId, start, end);
		}

		return commerceOrderNoteService.getCommerceOrderNotes(
			commerceOrderId, false, start, end);
	}

	@Override
	public int getCommerceOrderNotesCount(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId,
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES);

		return commerceOrderNotePersistence.countByCommerceOrderId(
			commerceOrderId);
	}

	@Override
	public int getCommerceOrderNotesCount(
			long commerceOrderId, boolean restricted)
		throws PortalException {

		String actionId = ActionKeys.VIEW;

		if (restricted) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, actionId);

		return commerceOrderNotePersistence.countByC_R(
			commerceOrderId, restricted);
	}

	@Override
	public CommerceOrderNote updateCommerceOrderNote(
			long commerceOrderNoteId, String content, boolean restricted)
		throws PortalException {

		CommerceOrderNote commerceOrderNote =
			commerceOrderNotePersistence.findByPrimaryKey(commerceOrderNoteId);

		String actionId = CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES;

		if (commerceOrderNote.isRestricted() || restricted) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderNote.getCommerceOrderId(),
			actionId);

		return commerceOrderNoteLocalService.updateCommerceOrderNote(
			commerceOrderNote.getCommerceOrderNoteId(), content, restricted);
	}

	private void _checkCommerceOrderNotePermissions(
			CommerceOrderNote commerceOrderNote)
		throws PortalException {

		if (commerceOrderNote == null) {
			return;
		}

		String actionId = ActionKeys.VIEW;

		if (commerceOrderNote.isRestricted()) {
			actionId =
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES;
		}

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderNote.getCommerceOrderId(),
			actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

}