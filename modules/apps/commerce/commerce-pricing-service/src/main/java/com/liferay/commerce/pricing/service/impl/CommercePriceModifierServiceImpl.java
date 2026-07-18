/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.base.CommercePriceModifierServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceModifier"
	},
	service = AopService.class
)
public class CommercePriceModifierServiceImpl
	extends CommercePriceModifierServiceBaseImpl {

	@Override
	public CommercePriceModifier addCommercePriceModifier(
			String externalReferenceCode, long groupId,
			long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCommercePriceModifierPermission(commercePriceListId, 0);

		return commercePriceModifierLocalService.addCommercePriceModifier(
			externalReferenceCode, groupId, commercePriceListId, title, target,
			modifierAmount, modifierType, priority, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommercePriceModifier addOrUpdateCommercePriceModifier(
			String externalReferenceCode, long commercePriceModifierId,
			long groupId, long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.fetchByPrimaryKey(
				commercePriceModifierId);

		if ((commercePriceModifier == null) &&
			Validator.isNotNull(externalReferenceCode)) {

			commercePriceModifier =
				commercePriceModifierLocalService.
					fetchCommercePriceModifierByExternalReferenceCode(
						externalReferenceCode, serviceContext.getCompanyId());
		}

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		if ((commercePriceModifier != null) &&
			(commercePriceModifier.getCommercePriceListId() !=
				commercePriceListId)) {

			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceModifier.getCommercePriceListId(),
				ActionKeys.UPDATE);
		}

		return commercePriceModifierLocalService.
			addOrUpdateCommercePriceModifier(
				externalReferenceCode, commercePriceModifierId, groupId,
				commercePriceListId, title, target, modifierAmount,
				modifierType, priority, active, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, serviceContext);
	}

	@Override
	public CommercePriceModifier deleteCommercePriceModifier(
			long commercePriceModifierId)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.UPDATE);

		return commercePriceModifierLocalService.deleteCommercePriceModifier(
			commercePriceModifier);
	}

	@Override
	public CommercePriceModifier fetchCommercePriceModifier(
			long commercePriceModifierId)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.fetchByPrimaryKey(
				commercePriceModifierId);

		if (commercePriceModifier != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceModifier.getCommercePriceListId(),
				ActionKeys.VIEW);
		}

		return commercePriceModifier;
	}

	@Override
	public CommercePriceModifier
			fetchCommercePriceModifierByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierLocalService.
				fetchCommercePriceModifierByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commercePriceModifier != null) {
			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceModifier.getCommercePriceListId(),
				ActionKeys.VIEW);
		}

		return commercePriceModifier;
	}

	@Override
	public CommercePriceModifier getCommercePriceModifier(
			long commercePriceModifierId)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifier;
	}

	@Override
	public List<CommercePriceModifier> getCommercePriceModifiers(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceModifier> orderByComparator)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceModifierPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceModifiersCount(long commercePriceListId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.VIEW);

		return commercePriceModifierPersistence.countByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public CommercePriceModifier updateCommercePriceModifier(
			long commercePriceModifierId, long groupId,
			long commercePriceListId, String title, String target,
			BigDecimal modifierAmount, String modifierType, double priority,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCommercePriceModifierPermission(
			commercePriceListId, commercePriceModifierId);

		return commercePriceModifierLocalService.updateCommercePriceModifier(
			commercePriceModifierId, groupId, commercePriceListId, title,
			target, modifierAmount, modifierType, priority, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	private void _checkCommercePriceModifierPermission(
			long commercePriceListId, long commercePriceModifierId)
		throws PortalException {

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(), commercePriceListId, ActionKeys.UPDATE);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierPersistence.fetchByPrimaryKey(
				commercePriceModifierId);

		if ((commercePriceModifier != null) &&
			(commercePriceModifier.getCommercePriceListId() !=
				commercePriceListId)) {

			_commercePriceListModelResourcePermission.check(
				getPermissionChecker(),
				commercePriceModifier.getCommercePriceListId(),
				ActionKeys.UPDATE);
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}