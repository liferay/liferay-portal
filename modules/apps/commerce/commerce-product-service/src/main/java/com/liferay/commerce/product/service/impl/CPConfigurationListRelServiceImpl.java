/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationListRelServiceBaseImpl;
import com.liferay.commerce.product.service.persistence.CPConfigurationListPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPConfigurationListRel"
	},
	service = AopService.class
)
public class CPConfigurationListRelServiceImpl
	extends CPConfigurationListRelServiceBaseImpl {

	@Override
	public CPConfigurationListRel addCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.UPDATE);

		return cpConfigurationListRelLocalService.addCPConfigurationListRel(
			getUserId(), className, classPK, cpConfigurationListId);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPConfigurationListRel deleteCPConfigurationListRel(
			CPConfigurationListRel cpConfigurationListRel)
		throws PortalException {

		_checkCommerceCatalog(
			cpConfigurationListRel.getCPConfigurationListId(),
			ActionKeys.UPDATE);

		return cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
			cpConfigurationListRel);
	}

	@Override
	public CPConfigurationListRel deleteCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException {

		CPConfigurationListRel cpConfigurationListRel =
			cpConfigurationListRelPersistence.findByPrimaryKey(
				cpConfigurationListRelId);

		_checkCommerceCatalog(
			cpConfigurationListRel.getCPConfigurationListId(),
			ActionKeys.UPDATE);

		return cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
			cpConfigurationListRelId);
	}

	@Override
	public void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.UPDATE);

		cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			cpConfigurationListId);
	}

	@Override
	public void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.UPDATE);

		cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	@Override
	public CPConfigurationListRel fetchCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getAccountEntryCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getAccountEntryCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getAccountEntryCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public List<CPConfigurationListRel> getAccountGroupCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getAccountGroupCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getAccountGroupCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public List<CPConfigurationListRel>
			getCommerceOrderTypeCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getCommerceOrderTypeCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getCommerceOrderTypeCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public CPConfigurationListRel getCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException {

		return cpConfigurationListRelPersistence.findByPrimaryKey(
			cpConfigurationListRelId);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelPersistence.findByCPConfigurationListId(
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelPersistence.findByCPConfigurationListId(
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.getCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public int getCPConfigurationListRelsCount(long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getCPConfigurationListRelsCount(cpConfigurationListId);
	}

	@Override
	public int getCPConfigurationListRelsCount(
			String className, long cpConfigurationListId)
		throws PortalException {

		_checkCommerceCatalog(cpConfigurationListId, ActionKeys.VIEW);

		return cpConfigurationListRelLocalService.
			getCPConfigurationListRelsCount(className, cpConfigurationListId);
	}

	private void _checkCommerceCatalog(
			long cpConfigurationListId, String actionId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListPersistence.findByPrimaryKey(
				cpConfigurationListId);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				cpConfigurationList.getGroupId());

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CPConfigurationListPersistence _cpConfigurationListPersistence;

}