/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.DuplicateCPSpecificationOptionListTypeDefinitionRelException;
import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.commerce.product.service.base.CPSpecificationOptionListTypeDefinitionRelLocalServiceBaseImpl;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel",
	service = AopService.class
)
public class CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl
	extends CPSpecificationOptionListTypeDefinitionRelLocalServiceBaseImpl {

	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			addCPSpecificationOptionListTypeDefinitionRel(
				long cpSpecificationOptionId, long listTypeDefinitionId)
		throws PortalException {

		if (hasCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionId, listTypeDefinitionId)) {

			throw new DuplicateCPSpecificationOptionListTypeDefinitionRelException();
		}

		_listTypeDefinitionLocalService.getListTypeDefinition(
			listTypeDefinitionId);

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel =
				createCPSpecificationOptionListTypeDefinitionRel(
					counterLocalService.increment());

		cpSpecificationOptionListTypeDefinitionRel.setCPSpecificationOptionId(
			cpSpecificationOptionId);
		cpSpecificationOptionListTypeDefinitionRel.setListTypeDefinitionId(
			listTypeDefinitionId);

		return updateCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionListTypeDefinitionRel);
	}

	@Override
	public void deleteCPSpecificationOptionListTypeDefinitionRel(
			long cpSpecificationOptionId, long listTypeDefinitionId)
		throws PortalException {

		cpSpecificationOptionListTypeDefinitionRelPersistence.removeByC_L(
			cpSpecificationOptionId, listTypeDefinitionId);
	}

	@Override
	public void deleteCPSpecificationOptionListTypeDefinitionRels(
		long cpSpecificationOptionId) {

		cpSpecificationOptionListTypeDefinitionRelPersistence.
			removeByCPSpecificationOptionId(cpSpecificationOptionId);
	}

	@Override
	public List<CPSpecificationOptionListTypeDefinitionRel>
		getCPSpecificationOptionListTypeDefinitionRels(
			long cpSpecificationOptionId) {

		return cpSpecificationOptionListTypeDefinitionRelPersistence.
			findByCPSpecificationOptionId(cpSpecificationOptionId);
	}

	@Override
	public int getCPSpecificationOptionListTypeDefinitionRelsCount(
		long listTypeDefinitionId) {

		return cpSpecificationOptionListTypeDefinitionRelPersistence.
			countByListTypeDefinitionId(listTypeDefinitionId);
	}

	@Override
	public boolean hasCPSpecificationOptionListTypeDefinitionRel(
		long cpSpecificationOptionId, long listTypeDefinitionId) {

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel =
				cpSpecificationOptionListTypeDefinitionRelPersistence.
					fetchByC_L(cpSpecificationOptionId, listTypeDefinitionId);

		if (cpSpecificationOptionListTypeDefinitionRel != null) {
			return true;
		}

		return false;
	}

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

}