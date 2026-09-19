/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.impl;

import com.liferay.commerce.pricing.exception.DuplicateCommercePricingClassCPDefinitionRelException;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.base.CommercePricingClassCPDefinitionRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "model.class.name=com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel",
	service = AopService.class
)
public class CommercePricingClassCPDefinitionRelLocalServiceImpl
	extends CommercePricingClassCPDefinitionRelLocalServiceBaseImpl {

	@Override
	public CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId,
				ServiceContext serviceContext)
		throws PortalException {

		// Commerce pricing class cp definition rel

		User user = _userLocalService.getUser(serviceContext.getUserId());

		_validate(commercePricingClassId, cpDefinitionId);

		long commercePricingClassCPDefinitionRelId =
			counterLocalService.increment();

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				commercePricingClassCPDefinitionRelPersistence.create(
					commercePricingClassCPDefinitionRelId);

		commercePricingClassCPDefinitionRel.setCompanyId(user.getCompanyId());
		commercePricingClassCPDefinitionRel.setUserId(user.getUserId());
		commercePricingClassCPDefinitionRel.setUserName(user.getFullName());
		commercePricingClassCPDefinitionRel.setCommercePricingClassId(
			commercePricingClassId);
		commercePricingClassCPDefinitionRel.setCPDefinitionId(cpDefinitionId);

		return commercePricingClassCPDefinitionRelPersistence.update(
			commercePricingClassCPDefinitionRel);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				CommercePricingClassCPDefinitionRel
					commercePricingClassCPDefinitionRel)
		throws PortalException {

		return commercePricingClassCPDefinitionRelPersistence.remove(
			commercePricingClassCPDefinitionRel);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws PortalException {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				commercePricingClassCPDefinitionRelPersistence.findByPrimaryKey(
					commercePricingClassCPDefinitionRelId);

		return commercePricingClassCPDefinitionRelLocalService.
			deleteCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRel);
	}

	@Override
	public void deleteCommercePricingClassCPDefinitionRels(
			long commercePricingClassId)
		throws PortalException {

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				commercePricingClassCPDefinitionRelPersistence.
					findByCommercePricingClassId(commercePricingClassId);

		for (CommercePricingClassCPDefinitionRel
				commercePricingClassCPDefinitionRel :
					commercePricingClassCPDefinitionRels) {

			commercePricingClassCPDefinitionRelLocalService.
				deleteCommercePricingClassCPDefinitionRel(
					commercePricingClassCPDefinitionRel);
		}
	}

	@Override
	public CommercePricingClassCPDefinitionRel
		fetchCommercePricingClassCPDefinitionRel(
			long commercePricingClassId, long cpDefinitionId) {

		return commercePricingClassCPDefinitionRelPersistence.fetchByC_C(
			commercePricingClassId, cpDefinitionId);
	}

	@Override
	public long[] getCPDefinitionIds(long commercePricingClassId) {
		return ListUtil.toLongArray(
			commercePricingClassCPDefinitionRelPersistence.
				findByCommercePricingClassId(commercePricingClassId),
			CommercePricingClassCPDefinitionRel::getCPDefinitionId);
	}

	@Override
	public List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassByCPDefinitionId(long cpDefinitionId) {

		return commercePricingClassCPDefinitionRelPersistence.
			findByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassCPDefinitionRels(long commercePricingClassId) {

		return commercePricingClassCPDefinitionRelPersistence.
			findByCommercePricingClassId(commercePricingClassId);
	}

	@Override
	public List<CommercePricingClassCPDefinitionRel>
		getCommercePricingClassCPDefinitionRels(
			long commercePricingClassId, int start, int end,
			OrderByComparator<CommercePricingClassCPDefinitionRel>
				orderByComparator) {

		return commercePricingClassCPDefinitionRelPersistence.
			findByCommercePricingClassId(
				commercePricingClassId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId) {

		return commercePricingClassCPDefinitionRelPersistence.
			countByCommercePricingClassId(commercePricingClassId);
	}

	@Override
	public int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId, String name, String languageId) {

		return commercePricingClassCPDefinitionRelFinder.
			countByCommercePricingClassId(
				commercePricingClassId, name, languageId);
	}

	@Override
	public List<CommercePricingClassCPDefinitionRel>
		searchByCommercePricingClassId(
			long commercePricingClassId, String name, String languageId,
			int start, int end) {

		return commercePricingClassCPDefinitionRelFinder.
			findByCommercePricingClassId(
				commercePricingClassId, name, languageId, start, end);
	}

	private void _validate(long commercePricingClassId, long cpDefinitionId)
		throws PortalException {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				commercePricingClassCPDefinitionRelPersistence.fetchByC_C(
					commercePricingClassId, cpDefinitionId);

		if (commercePricingClassCPDefinitionRel != null) {
			throw new DuplicateCommercePricingClassCPDefinitionRelException();
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}