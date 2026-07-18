/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.base.CommerceChannelRelServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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
		"json.web.service.context.path=CommerceChannelRel"
	},
	service = AopService.class
)
public class CommerceChannelRelServiceImpl
	extends CommerceChannelRelServiceBaseImpl {

	@Override
	public CommerceChannelRel addCommerceChannelRel(
			String className, long classPK, long commerceChannelId,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.UPDATE);

		return commerceChannelRelLocalService.addCommerceChannelRel(
			className, classPK, commerceChannelId, serviceContext);
	}

	@Override
	public List<CommerceChannelRel> addCommerceChannelRels(
			String className, long[] classPKs, long commerceChannelId,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.UPDATE);

		return commerceChannelRelLocalService.addCommerceChannelRels(
			className, classPKs, commerceChannelId, serviceContext);
	}

	@Override
	public void deleteCommerceChannelRel(long commerceChannelRelId)
		throws PortalException {

		CommerceChannelRel commerceChannelRel =
			commerceChannelRelPersistence.findByPrimaryKey(
				commerceChannelRelId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelRel.getCommerceChannelId(),
			ActionKeys.UPDATE);

		commerceChannelRelLocalService.deleteCommerceChannelRel(
			commerceChannelRelId);
	}

	@Override
	public void deleteCommerceChannelRels(String className, long classPK)
		throws PortalException {

		List<CommerceChannelRel> commerceChannelRels =
			commerceChannelRelLocalService.getCommerceChannelRels(
				className, classPK, StringPool.BLANK, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (CommerceChannelRel commerceChannelRel : commerceChannelRels) {
			commerceChannelRelLocalService.deleteCommerceChannelRel(
				commerceChannelRel.getCommerceChannelRelId());
		}
	}

	@Override
	public CommerceChannelRel fetchCommerceChannelRel(
			String className, long classPK, long commerceChannelId)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceChannelId);
	}

	@Override
	public CommerceChannelRel getCommerceChannelRel(long commerceChannelRelId)
		throws PortalException {

		CommerceChannelRel commerceChannelRel =
			commerceChannelRelPersistence.findByPrimaryKey(
				commerceChannelRelId);

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelRel.getCommerceChannelId(),
			ActionKeys.VIEW);

		return commerceChannelRel;
	}

	@Override
	public List<CommerceChannelRel> getCommerceChannelRels(
			long commerceChannelId, int start, int end,
			OrderByComparator<CommerceChannelRel> orderByComparator)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelPersistence.findByCommerceChannelId(
			commerceChannelId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceChannelRel> getCommerceChannelRels(
			String className, long classPK, String name, int start, int end)
		throws PortalException {

		if (className.equals(CPDefinition.class.getName())) {
			_cpDefinitionModelResourcePermission.check(
				getPermissionChecker(), classPK, ActionKeys.VIEW);
		}

		return commerceChannelRelFinder.findByC_C(
			className, classPK, name, start, end, true);
	}

	@Override
	public int getCommerceChannelRelsCount(long commerceChannelId)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelPersistence.countByCommerceChannelId(
			commerceChannelId);
	}

	@Override
	public int getCommerceChannelRelsCount(String className, long classPK)
		throws PortalException {

		return commerceChannelRelService.getCommerceChannelRelsCount(
			className, classPK, StringPool.BLANK);
	}

	@Override
	public int getCommerceChannelRelsCount(
			String className, long classPK, String name)
		throws PortalException {

		if (className.equals(CPDefinition.class.getName())) {
			_cpDefinitionModelResourcePermission.check(
				getPermissionChecker(), classPK, ActionKeys.VIEW);
		}

		return commerceChannelRelFinder.countByC_C(
			className, classPK, name, true);
	}

	@Override
	public List<CommerceChannelRel> getCommerceCurrencyCommerceChannelRels(
			long commerceChannelId, String name, int start, int end)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelLocalService.
			getCommerceCurrencyCommerceChannelRels(
				commerceChannelId, name, start, end);
	}

	@Override
	public int getCommerceCurrencyCommerceChannelRelsCount(
			long commerceChannelId, String name)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelLocalService.
			getCommerceCurrencyCommerceChannelRelsCount(
				commerceChannelId, name);
	}

	@Override
	public List<CommerceChannelRel> getCountryCommerceChannelRels(
			long commerceChannelId, String name, int start, int end)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelLocalService.getCountryCommerceChannelRels(
			commerceChannelId, name, start, end);
	}

	@Override
	public int getCountryCommerceChannelRelsCount(
			long commerceChannelId, String name)
		throws PortalException {

		_commerceChannelModelResourcePermission.check(
			getPermissionChecker(), commerceChannelId, ActionKeys.VIEW);

		return commerceChannelRelLocalService.
			getCountryCommerceChannelRelsCount(commerceChannelId, name);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

}