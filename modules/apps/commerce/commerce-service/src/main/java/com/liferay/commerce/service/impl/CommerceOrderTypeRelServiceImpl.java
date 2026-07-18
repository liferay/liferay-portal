/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.commerce.service.base.CommerceOrderTypeRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceOrderTypeRel"
	},
	service = AopService.class
)
public class CommerceOrderTypeRelServiceImpl
	extends CommerceOrderTypeRelServiceBaseImpl {

	@Override
	public CommerceOrderTypeRel addCommerceOrderTypeRel(
			String className, long classPK, long commerceOrderTypeId,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.UPDATE);

		return commerceOrderTypeRelLocalService.addCommerceOrderTypeRel(
			getUserId(), className, classPK, commerceOrderTypeId,
			serviceContext);
	}

	@Override
	public CommerceOrderTypeRel deleteCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		CommerceOrderTypeRel commerceOrderTypeRel =
			commerceOrderTypeRelLocalService.getCommerceOrderTypeRel(
				commerceOrderTypeRelId);

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(),
			commerceOrderTypeRel.getCommerceOrderTypeId(), ActionKeys.UPDATE);

		return commerceOrderTypeRelLocalService.deleteCommerceOrderTypeRel(
			commerceOrderTypeRelId);
	}

	@Override
	public void deleteCommerceOrderTypeRels(
			String className, long commerceOrderTypeId)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.UPDATE);

		commerceOrderTypeRelLocalService.deleteCommerceOrderTypeRels(
			className, commerceOrderTypeId);
	}

	@Override
	public List<CommerceOrderTypeRel> getCommerceOrderTypeCommerceChannelRels(
			long commerceOrderTypeId, String keywords, int start, int end)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.VIEW);

		return commerceOrderTypeRelLocalService.
			getCommerceOrderTypeCommerceChannelRels(
				commerceOrderTypeId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceChannelRelsCount(
			long commerceOrderTypeId, String keywords)
		throws PortalException {

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(), commerceOrderTypeId, ActionKeys.VIEW);

		return commerceOrderTypeRelLocalService.
			getCommerceOrderTypeCommerceChannelRelsCount(
				commerceOrderTypeId, keywords);
	}

	@Override
	public CommerceOrderTypeRel getCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		CommerceOrderTypeRel commerceOrderTypeRel =
			commerceOrderTypeRelLocalService.getCommerceOrderTypeRel(
				commerceOrderTypeRelId);

		_commerceOrderTypeModelResourcePermission.check(
			getPermissionChecker(),
			commerceOrderTypeRel.getCommerceOrderTypeId(), ActionKeys.VIEW);

		return commerceOrderTypeRelLocalService.getCommerceOrderTypeRel(
			commerceOrderTypeRelId);
	}

	@Override
	public List<CommerceOrderTypeRel> getCommerceOrderTypeRels(
			String className, long classPK, int start, int end,
			OrderByComparator<CommerceOrderTypeRel> orderByComparator)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceOrderTypeModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceOrderActionKeys.VIEW_COMMERCE_ORDER_TYPES);

		return commerceOrderTypeRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK, start,
			end, orderByComparator);
	}

	@Override
	public int getCommerceOrderTypeRelsCount(String className, long classPK)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceOrderTypeModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceOrderActionKeys.VIEW_COMMERCE_ORDER_TYPES);

		return commerceOrderTypeRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderType)"
	)
	private ModelResourcePermission<CommerceOrderType>
		_commerceOrderTypeModelResourcePermission;

}