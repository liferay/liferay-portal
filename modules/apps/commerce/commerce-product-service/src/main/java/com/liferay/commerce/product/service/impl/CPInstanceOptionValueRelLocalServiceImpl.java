/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.service.base.CPInstanceOptionValueRelLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The implementation of the cp instance option value rel local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * <code>com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService</code>
 * interface. <p> This is a local service. Methods of this service will not have
 * security checks based on the propagated JAAS credentials because this service
 * can only be accessed from within the same VM.
 * </p>
 *
 * @author Igor Beslic
 * @see    CPInstanceOptionValueRelLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPInstanceOptionValueRel",
	service = AopService.class
)
public class CPInstanceOptionValueRelLocalServiceImpl
	extends CPInstanceOptionValueRelLocalServiceBaseImpl {

	@Override
	public CPInstanceOptionValueRel addCPInstanceOptionValueRel(
			long groupId, long companyId, long userId,
			long cpDefinitionOptionRelId, long cpDefinitionOptionValueRelId,
			long cpInstanceId)
		throws PortalException {

		long cpInstanceOptionValueRelId = counterLocalService.increment();

		CPInstanceOptionValueRel cpInstanceOptionValueRel =
			cpInstanceOptionValueRelPersistence.create(
				cpInstanceOptionValueRelId);

		cpInstanceOptionValueRel.setGroupId(groupId);
		cpInstanceOptionValueRel.setCompanyId(companyId);
		cpInstanceOptionValueRel.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		cpInstanceOptionValueRel.setUserName(user.getFullName());

		Date createDate = new Date();

		cpInstanceOptionValueRel.setCreateDate(createDate);
		cpInstanceOptionValueRel.setModifiedDate(createDate);

		cpInstanceOptionValueRel.setCPDefinitionOptionRelId(
			cpDefinitionOptionRelId);
		cpInstanceOptionValueRel.setCPDefinitionOptionValueRelId(
			cpDefinitionOptionValueRelId);
		cpInstanceOptionValueRel.setCPInstanceId(cpInstanceId);

		return cpInstanceOptionValueRelPersistence.update(
			cpInstanceOptionValueRel);
	}

	@Override
	public List<CPInstanceOptionValueRel>
		getCPDefinitionCPInstanceOptionValueRels(long cpDefinitionId) {

		return cpInstanceOptionValueRelFinder.findByCPDefinitionId(
			cpDefinitionId,
			new QueryDefinition<>(WorkflowConstants.STATUS_APPROVED));
	}

	@Override
	public List<CPInstanceOptionValueRel>
		getCPDefinitionOptionRelCPInstanceOptionValueRels(
			long cpDefinitionOptionRelId) {

		return cpInstanceOptionValueRelPersistence.
			findByCPDefinitionOptionRelId(cpDefinitionOptionRelId);
	}

	@Override
	public List<CPInstanceOptionValueRel>
		getCPInstanceCPInstanceOptionValueRels(long cpInstanceId) {

		return cpInstanceOptionValueRelPersistence.findByCPInstanceId(
			cpInstanceId);
	}

	@Override
	public List<CPInstanceOptionValueRel>
		getCPInstanceCPInstanceOptionValueRels(
			long cpDefinitionOptionRelId, long cpInstanceId) {

		return cpInstanceOptionValueRelPersistence.findByCDORI_CII(
			cpDefinitionOptionRelId, cpInstanceId);
	}

	@Override
	public boolean hasCPInstanceCPDefinitionOptionRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		int countByCPDefinitionOptionRelIdCPInstanceId =
			cpInstanceOptionValueRelPersistence.countByCDORI_CII(
				cpDefinitionOptionRelId, cpInstanceId);

		if (countByCPDefinitionOptionRelIdCPInstanceId > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasCPInstanceCPDefinitionOptionValueRel(
		long cpDefinitionOptionValueRelId, long cpInstanceId) {

		int countByCPDefinitionOptionValueRelIdCPInstanceId =
			cpInstanceOptionValueRelPersistence.countByCDOVRI_CII(
				cpDefinitionOptionValueRelId, cpInstanceId);

		if (countByCPDefinitionOptionValueRelIdCPInstanceId > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasCPInstanceOptionValueRel(long cpInstanceId) {
		int countByCPInstanceId =
			cpInstanceOptionValueRelPersistence.countByCPInstanceId(
				cpInstanceId);

		if (countByCPInstanceId > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean matchesCPDefinitionOptionRels(
		long cpDefinitionId, long cpInstanceId) {

		List<CPDefinitionOptionRel> cpDefinitionCPDefinitionOptionRels =
			_cpDefinitionOptionRelPersistence.findByC_SC(cpDefinitionId, true);

		List<CPInstanceOptionValueRel> cpInstanceCPInstanceOptionValueRels =
			cpInstanceOptionValueRelLocalService.
				getCPInstanceCPInstanceOptionValueRels(cpInstanceId);

		if (cpDefinitionCPDefinitionOptionRels.size() !=
				cpInstanceCPInstanceOptionValueRels.size()) {

			return false;
		}

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionCPDefinitionOptionRels) {

			boolean matched = false;

			for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
					cpInstanceCPInstanceOptionValueRels) {

				if (cpDefinitionOptionRel.getCPDefinitionOptionRelId() ==
						cpInstanceOptionValueRel.getCPDefinitionOptionRelId()) {

					matched = true;

					break;
				}
			}

			if (!matched) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels) {

		List<CPInstanceOptionValueRel> cpInstanceCPInstanceOptionValueRels =
			cpInstanceOptionValueRelPersistence.findByCPInstanceId(
				cpInstanceId);

		if (cpInstanceOptionValueRels.size() !=
				cpInstanceCPInstanceOptionValueRels.size()) {

			return false;
		}

		int matchCount = 0;

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpInstanceOptionValueRels) {

			for (CPInstanceOptionValueRel currCPInstanceOptionValueRel :
					cpInstanceCPInstanceOptionValueRels) {

				if ((cpInstanceOptionValueRel.getCPDefinitionOptionRelId() !=
						currCPInstanceOptionValueRel.
							getCPDefinitionOptionRelId()) ||
					(cpInstanceOptionValueRel.getCPDefinitionOptionRelId() !=
						currCPInstanceOptionValueRel.
							getCPDefinitionOptionRelId())) {

					continue;
				}

				matchCount++;
			}
		}

		if (cpInstanceOptionValueRels.size() == matchCount) {
			return true;
		}

		return false;
	}

	@Override
	public boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		Map<Long, List<Long>>
			cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds) {

		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			cpInstanceOptionValueRelPersistence.findByCPInstanceId(
				cpInstanceId);

		for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
				cpInstanceOptionValueRels) {

			if (!cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds.
					containsKey(
						cpInstanceOptionValueRel.
							getCPDefinitionOptionRelId())) {

				return false;
			}

			List<Long> cpDefinitionOptionValueIds =
				cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds.get(
					cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

			if (!cpDefinitionOptionValueIds.contains(
					cpInstanceOptionValueRel.
						getCPDefinitionOptionValueRelId())) {

				return false;
			}
		}

		return true;
	}

	@Override
	public void updateCPInstanceOptionValueRels(
			long groupId, long companyId, long userId, long cpInstanceId,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds)
		throws PortalException {

		Set<Long> cpDefinitionOptionRelIds =
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.keySet();

		for (Long cpDefinitionOptionRelId : cpDefinitionOptionRelIds) {
			List<Long> cpDefinitionOptionValueRelIds =
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds.get(
					cpDefinitionOptionRelId);

			for (Long cpDefinitionOptionValueRelId :
					cpDefinitionOptionValueRelIds) {

				cpInstanceOptionValueRelLocalService.
					addCPInstanceOptionValueRel(
						groupId, companyId, userId, cpDefinitionOptionRelId,
						cpDefinitionOptionValueRelId, cpInstanceId);
			}
		}
	}

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private UserLocalService _userLocalService;

}