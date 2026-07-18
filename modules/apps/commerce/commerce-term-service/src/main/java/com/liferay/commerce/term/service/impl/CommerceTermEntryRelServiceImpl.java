/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.impl;

import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.base.CommerceTermEntryRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceTermEntryRel"
	},
	service = AopService.class
)
public class CommerceTermEntryRelServiceImpl
	extends CommerceTermEntryRelServiceBaseImpl {

	@Override
	public CommerceTermEntryRel addCommerceTermEntryRel(
			String className, long classPK, long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.UPDATE);

		return commerceTermEntryRelLocalService.addCommerceTermEntryRel(
			getUserId(), className, classPK, commerceTermEntryId);
	}

	@Override
	public void deleteCommerceTermEntryRel(long commerceTermEntryRelId)
		throws PortalException {

		CommerceTermEntryRel commerceTermEntryRel =
			commerceTermEntryRelPersistence.findByPrimaryKey(
				commerceTermEntryRelId);

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(),
			commerceTermEntryRel.getCommerceTermEntryId(), ActionKeys.UPDATE);

		commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
			commerceTermEntryRel);
	}

	@Override
	public void deleteCommerceTermEntryRels(
			String className, long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.UPDATE);

		commerceTermEntryRelLocalService.deleteCommerceTermEntryRels(
			className, commerceTermEntryId);
	}

	@Override
	public void deleteCommerceTermEntryRelsByCommerceTermEntryId(
			long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.UPDATE);

		commerceTermEntryRelLocalService.deleteCommerceTermEntryRels(
			commerceTermEntryId);
	}

	@Override
	public CommerceTermEntryRel fetchCommerceTermEntryRel(
			String className, long classPK, long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceTermEntryId);
	}

	@Override
	public List<CommerceTermEntryRel> getCommerceOrderTypeCommerceTermEntryRels(
			long commerceTermEntryId, String keywords, int start, int end)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelLocalService.
			getCommerceOrderTypeCommerceTermEntryRels(
				commerceTermEntryId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceTermEntryRelsCount(
			long commerceTermEntryId, String keywords)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelLocalService.
			getCommerceOrderTypeCommerceTermEntryRelsCount(
				commerceTermEntryId, keywords);
	}

	@Override
	public CommerceTermEntryRel getCommerceTermEntryRel(
			long commerceTermEntryRelId)
		throws PortalException {

		CommerceTermEntryRel commerceTermEntryRel =
			commerceTermEntryRelPersistence.findByPrimaryKey(
				commerceTermEntryRelId);

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(),
			commerceTermEntryRel.getCommerceTermEntryId(), ActionKeys.VIEW);

		return commerceTermEntryRel;
	}

	@Override
	public List<CommerceTermEntryRel> getCommerceTermEntryRels(
			long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelPersistence.findByCommerceTermEntryId(
			commerceTermEntryId);
	}

	@Override
	public List<CommerceTermEntryRel> getCommerceTermEntryRels(
			long commerceTermEntryId, int start, int end,
			OrderByComparator<CommerceTermEntryRel> orderByComparator)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelPersistence.findByCommerceTermEntryId(
			commerceTermEntryId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTermEntryRelsCount(long commerceTermEntryId)
		throws PortalException {

		_commerceTermEntryModelResourcePermission.check(
			getPermissionChecker(), commerceTermEntryId, ActionKeys.VIEW);

		return commerceTermEntryRelPersistence.countByCommerceTermEntryId(
			commerceTermEntryId);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.term.model.CommerceTermEntry)"
	)
	private ModelResourcePermission<CommerceTermEntry>
		_commerceTermEntryModelResourcePermission;

}