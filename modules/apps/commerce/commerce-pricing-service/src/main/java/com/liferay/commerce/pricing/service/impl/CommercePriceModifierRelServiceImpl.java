/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.base.CommercePriceModifierRelServiceBaseImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierPersistence;
import com.liferay.portal.aop.AopService;
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
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePriceModifierRel"
	},
	service = AopService.class
)
public class CommercePriceModifierRelServiceImpl
	extends CommercePriceModifierRelServiceBaseImpl {

	@Override
	public CommercePriceModifierRel addCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierPersistence.findByPrimaryKey(
				commercePriceModifierId);

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.UPDATE);

		return commercePriceModifierRelLocalService.addCommercePriceModifierRel(
			commercePriceModifierId, className, classPK, serviceContext);
	}

	@Override
	public void deleteCommercePriceModifierRel(long commercePriceModifierRelId)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.UPDATE);

		commercePriceModifierRelLocalService.deleteCommercePriceModifierRel(
			commercePriceModifierRel);
	}

	@Override
	public CommercePriceModifierRel fetchCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.fetchByCPM_CN_CPK(
				commercePriceModifierId,
				_classNameLocalService.getClassNameId(className), classPK);

		if (commercePriceModifierRel == null) {
			return null;
		}

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRel;
	}

	@Override
	public List<CommercePriceModifierRel>
		getCategoriesCommercePriceModifierRels(
			long commercePriceModifierId, String name, int start, int end) {

		return commercePriceModifierRelFinder.
			findCategoriesByCommercePriceModifierId(
				commercePriceModifierId, name, start, end, true);
	}

	@Override
	public int getCategoriesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name) {

		return commercePriceModifierRelFinder.
			countCategoriesByCommercePriceModifierId(
				commercePriceModifierId, name, true);
	}

	@Override
	public long[] getClassPKs(long commercePriceModifierRelId, String className)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRelLocalService.getClassPKs(
			commercePriceModifierRelId, className);
	}

	@Override
	public CommercePriceModifierRel getCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRel;
	}

	@Override
	public List<CommercePriceModifierRel> getCommercePriceModifierRels(
			long commercePriceModifierRelId, String className)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRelLocalService.
			getCommercePriceModifierRels(commercePriceModifierRelId, className);
	}

	@Override
	public List<CommercePriceModifierRel> getCommercePriceModifierRels(
			long commercePriceModifierRelId, String className, int start,
			int end,
			OrderByComparator<CommercePriceModifierRel> orderByComparator)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRelLocalService.
			getCommercePriceModifierRels(
				commercePriceModifierRelId, className, start, end,
				orderByComparator);
	}

	@Override
	public int getCommercePriceModifierRelsCount(
			long commercePriceModifierRelId, String className)
		throws PortalException {

		CommercePriceModifierRel commercePriceModifierRel =
			commercePriceModifierRelPersistence.findByPrimaryKey(
				commercePriceModifierRelId);

		CommercePriceModifier commercePriceModifier =
			commercePriceModifierRel.getCommercePriceModifier();

		_commercePriceListModelResourcePermission.check(
			getPermissionChecker(),
			commercePriceModifier.getCommercePriceListId(), ActionKeys.VIEW);

		return commercePriceModifierRelLocalService.
			getCommercePriceModifierRelsCount(
				commercePriceModifierRelId, className);
	}

	@Override
	public List<CommercePriceModifierRel> getCommercePriceModifiersRels(
		String className, long classPK) {

		return commercePriceModifierRelPersistence.findByCN_CPK(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<CommercePriceModifierRel>
		getCommercePricingClassesCommercePriceModifierRels(
			long commercePriceModifierId, String title, int start, int end) {

		return commercePriceModifierRelFinder.
			findPricingClassesByCommercePriceModifierId(
				commercePriceModifierId, title, start, end, true);
	}

	@Override
	public int getCommercePricingClassesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String title) {

		return commercePriceModifierRelFinder.
			countPricingClassesByCommercePriceModifierId(
				commercePriceModifierId, title, true);
	}

	@Override
	public List<CommercePriceModifierRel>
		getCPDefinitionsCommercePriceModifierRels(
			long commercePriceModifierId, String name, String languageId,
			int start, int end) {

		return commercePriceModifierRelFinder.
			findCPDefinitionsByCommercePriceModifierId(
				commercePriceModifierId, name, languageId, start, end, true);
	}

	@Override
	public int getCPDefinitionsCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name, String languageId) {

		return commercePriceModifierRelFinder.
			countCPDefinitionsByCommercePriceModifierId(
				commercePriceModifierId, languageId, name, true);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

	@Reference
	private CommercePriceModifierPersistence _commercePriceModifierPersistence;

}