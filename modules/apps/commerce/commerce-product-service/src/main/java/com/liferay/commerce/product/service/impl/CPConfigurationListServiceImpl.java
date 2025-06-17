/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationListServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.systemevent.SystemEvent;

import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPConfigurationList"
	},
	service = AopService.class
)
public class CPConfigurationListServiceImpl
	extends CPConfigurationListServiceBaseImpl {

	@Override
	public CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpConfigurationListLocalService.addCPConfigurationList(
			externalReferenceCode, getUserId(), groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpConfigurationListLocalService.addOrUpdateCPConfigurationList(
			externalReferenceCode, companyId, getUserId(), groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws PortalException {

		_checkCommerceCatalog(
			cpConfigurationList.getGroupId(), ActionKeys.UPDATE);

		return cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			cpConfigurationListLocalService.getCPConfigurationList(
				cpConfigurationListId);

		_checkCommerceCatalog(
			cpConfigurationList.getGroupId(), ActionKeys.UPDATE);

		return cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList fetchCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			cpConfigurationListLocalService.
				fetchCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (cpConfigurationList != null) {
			_checkCommerceCatalog(
				cpConfigurationList.getGroupId(), ActionKeys.VIEW);
		}

		return cpConfigurationList;
	}

	@Override
	public CPConfigurationList getCPConfigurationList(long cpConfigurationLisId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			cpConfigurationListLocalService.getCPConfigurationList(
				cpConfigurationLisId);

		_checkCommerceCatalog(
			cpConfigurationList.getGroupId(), ActionKeys.VIEW);

		return cpConfigurationList;
	}

	@Override
	public CPConfigurationList getCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			cpConfigurationListLocalService.
				getCPConfigurationListByExternalReferenceCode(
					externalReferenceCode, companyId);

		_checkCommerceCatalog(
			cpConfigurationList.getGroupId(), ActionKeys.VIEW);

		return cpConfigurationList;
	}

	@Override
	public List<CPConfigurationList> getCPConfigurationLists(
			long groupId, long companyId)
		throws PortalException {

		List<CPConfigurationList> cpConfigurationLists =
			cpConfigurationListLocalService.getCPConfigurationLists(
				groupId, companyId);

		Iterator<CPConfigurationList> iterator =
			cpConfigurationLists.iterator();

		while (iterator.hasNext()) {
			CPConfigurationList cpConfigurationList = iterator.next();

			if (!_containsCommerceCatalog(
					cpConfigurationList.getGroupId(), ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		return cpConfigurationLists;
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.VIEW);

		return cpConfigurationListLocalService.getMasterCPConfigurationList(
			groupId);
	}

	@Indexable(type = IndexableType.REINDEX)
	public CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long groupId, long parentCPConfigurationListId,
			boolean masterCPConfigurationList, String name, double priority,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		_checkCommerceCatalog(groupId, ActionKeys.UPDATE);

		return cpConfigurationListLocalService.updateCPConfigurationList(
			externalReferenceCode, cpConfigurationListId, getUserId(), groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	private void _checkCommerceCatalog(long groupId, String actionId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		_commerceCatalogModelResourcePermission.check(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	private boolean _containsCommerceCatalog(long groupId, String actionId)
		throws PortalException {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(groupId);

		if (commerceCatalog == null) {
			throw new PrincipalException();
		}

		return _commerceCatalogModelResourcePermission.contains(
			getPermissionChecker(), commerceCatalog, actionId);
	}

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

}