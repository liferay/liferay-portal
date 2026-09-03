/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.base.CProductLocalServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ethan Bustad
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CProduct",
	service = AopService.class
)
public class CProductLocalServiceImpl extends CProductLocalServiceBaseImpl {

	@Override
	public CProduct addCProduct(
			String externalReferenceCode, long userId, long groupId,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CProduct cProduct = cProductLocalService.createCProduct(
			counterLocalService.increment());

		cProduct.setExternalReferenceCode(externalReferenceCode);
		cProduct.setGroupId(groupId);
		cProduct.setCompanyId(user.getCompanyId());
		cProduct.setUserId(user.getUserId());
		cProduct.setUserName(user.getFullName());
		cProduct.setLatestVersion(1);

		return cProductPersistence.update(cProduct);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CProduct deleteCProduct(CProduct cProduct) throws PortalException {
		cProduct = cProductPersistence.remove(cProduct);

		_cpDefinitionLinkLocalService.deleteCPDefinitionLinksByCProductId(
			cProduct.getCProductId());

		return cProduct;
	}

	@Override
	public CProduct deleteCProduct(long cProductId) throws PortalException {
		CProduct cProduct = cProductPersistence.findByPrimaryKey(cProductId);

		return cProductLocalService.deleteCProduct(cProduct);
	}

	@Override
	public CProduct getCProductByCPInstanceUuid(String cpInstanceUuid)
		throws PortalException {

		CPInstance cpInstance =
			cpInstancePersistence.fetchByCPInstanceUuid_First(
				cpInstanceUuid, null);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		return cpDefinition.getCProduct();
	}

	@Override
	public int increment(long cProductId) throws PortalException {
		CProduct cProduct = cProductPersistence.findByPrimaryKey(cProductId);

		cProduct.setLatestVersion(cProduct.getLatestVersion() + 1);

		cProduct = cProductPersistence.update(cProduct);

		return cProduct.getLatestVersion();
	}

	@Override
	public CProduct updateCProductExternalReferenceCode(
			String externalReferenceCode, long cProductId)
		throws PortalException {

		CProduct cProduct = cProductPersistence.findByPrimaryKey(cProductId);

		cProduct.setExternalReferenceCode(externalReferenceCode);

		cProduct = cProductPersistence.update(cProduct);

		_reindexCPDefinition(cProduct.getPublishedCPDefinitionId());

		return cProduct;
	}

	@Override
	public CProduct updatePublishedCPDefinitionId(
			long cProductId, long publishedCPDefinitionId)
		throws PortalException {

		CProduct cProduct = cProductPersistence.findByPrimaryKey(cProductId);

		long originalPublishedCPDefinitionId =
			cProduct.getPublishedCPDefinitionId();

		if (originalPublishedCPDefinitionId == publishedCPDefinitionId) {
			return cProduct;
		}

		cProduct.setPublishedCPDefinitionId(publishedCPDefinitionId);

		cProduct = cProductPersistence.update(cProduct);

		_reindexCPDefinition(originalPublishedCPDefinitionId);
		_reindexCPDefinition(publishedCPDefinitionId);

		return cProduct;
	}

	@Reference
	protected CPInstancePersistence cpInstancePersistence;

	private void _reindexCPDefinition(long cpDefinitionId)
		throws PortalException {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
	}

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private UserLocalService _userLocalService;

}