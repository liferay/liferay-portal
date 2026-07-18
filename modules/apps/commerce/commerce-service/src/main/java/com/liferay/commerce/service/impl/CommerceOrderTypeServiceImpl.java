/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.base.CommerceOrderTypeServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrderType"
	},
	service = AopService.class
)
public class CommerceOrderTypeServiceImpl
	extends CommerceOrderTypeServiceBaseImpl {

	@Override
	public CommerceOrderType addCommerceOrderType(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int displayOrder,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceOrderTypeModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceOrderActionKeys.ADD_COMMERCE_ORDER_TYPE);

		return commerceOrderTypeLocalService.addCommerceOrderType(
			externalReferenceCode, getUserId(), nameMap, descriptionMap, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, displayOrder, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public CommerceOrderType deleteCommerceOrderType(long commerceOrderTypeId)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.DELETE);

		return commerceOrderTypeLocalService.deleteCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public CommerceOrderType fetchCommerceOrderType(long commerceOrderTypeId)
		throws PortalException {

		CommerceOrderType commerceOrderType =
			commerceOrderTypePersistence.fetchByPrimaryKey(commerceOrderTypeId);

		if (commerceOrderType != null) {
			_commerceOrderTypeModelResourcePermission.check(
				getPermissionChecker(), commerceOrderTypeId, ActionKeys.VIEW);
		}

		return commerceOrderType;
	}

	@Override
	public CommerceOrderType fetchCommerceOrderTypeByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceOrderType commerceOrderType =
			commerceOrderTypeLocalService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceOrderType != null) {
			_commerceOrderTypeModelResourcePermission.check(
				getPermissionChecker(), commerceOrderType, ActionKeys.VIEW);
		}

		return commerceOrderType;
	}

	@Override
	public CommerceOrderType getCommerceOrderType(long commerceOrderTypeId)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.VIEW);

		return commerceOrderTypePersistence.findByPrimaryKey(
			commerceOrderTypeId);
	}

	@Override
	public List<CommerceOrderType> getCommerceOrderTypes(
			String className, long classPK, boolean active, int start, int end)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		return commerceOrderTypeLocalService.getCommerceOrderTypes(
			permissionChecker.getCompanyId(), className, classPK, active, start,
			end);
	}

	@Override
	public int getCommerceOrderTypesCount(
			String className, long classPK, boolean active)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		return commerceOrderTypeLocalService.getCommerceOrderTypesCount(
			permissionChecker.getCompanyId(), className, classPK, active);
	}

	@Override
	public CommerceOrderType updateCommerceOrderType(
			String externalReferenceCode, long commerceOrderTypeId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int displayOrder, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.UPDATE);

		return commerceOrderTypeLocalService.updateCommerceOrderType(
			externalReferenceCode, getUserId(), commerceOrderTypeId, nameMap,
			descriptionMap, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, displayOrder,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommerceOrderType updateCommerceOrderTypeExternalReferenceCode(
			String externalReferenceCode, long commerceOrderTypeId)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.UPDATE);

		return commerceOrderTypeLocalService.
			updateCommerceOrderTypeExternalReferenceCode(
				externalReferenceCode, commerceOrderTypeId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderType)"
	)
	private ModelResourcePermission<CommerceOrderType>
		_commerceOrderTypeModelResourcePermission;

}