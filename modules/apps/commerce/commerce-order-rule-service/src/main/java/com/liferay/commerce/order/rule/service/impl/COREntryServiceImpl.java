/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.impl;

import com.liferay.commerce.order.rule.constants.COREntryActionKeys;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.base.COREntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

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
		"json.web.service.context.path=COREntry"
	},
	service = AopService.class
)
public class COREntryServiceImpl extends COREntryServiceBaseImpl {

	@Override
	public COREntry addCOREntry(
			String externalReferenceCode, boolean active, String description,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String name, int priority, String type,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_corEntryModelResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, COREntryActionKeys.ADD_COR_ENTRY);

		return corEntryLocalService.addCOREntry(
			externalReferenceCode, getUserId(), active, description,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, name, priority, type, typeSettings, serviceContext);
	}

	@Override
	public COREntry deleteCOREntry(long corEntryId) throws PortalException {
		_corEntryModelResourcePermission.check(
			getPermissionChecker(), corEntryId, ActionKeys.DELETE);

		return corEntryLocalService.deleteCOREntry(corEntryId);
	}

	@Override
	public COREntry fetchCOREntry(long corEntryId) throws PortalException {
		COREntry corEntry = corEntryPersistence.fetchByPrimaryKey(corEntryId);

		if (corEntry != null) {
			_corEntryModelResourcePermission.check(
				getPermissionChecker(), corEntry, ActionKeys.VIEW);
		}

		return corEntry;
	}

	@Override
	public COREntry fetchCOREntryByExternalReferenceCode(
			long companyId, String externalReferenceCode)
		throws PortalException {

		COREntry corEntry = corEntryPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (corEntry != null) {
			_corEntryModelResourcePermission.check(
				getPermissionChecker(), corEntry, ActionKeys.VIEW);
		}

		return corEntry;
	}

	@Override
	public List<COREntry> getCOREntries(
			long companyId, boolean active, int start, int end)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_corEntryModelResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, COREntryActionKeys.VIEW_COR_ENTRIES);

		return corEntryLocalService.getCOREntries(
			companyId, active, start, end);
	}

	@Override
	public List<COREntry> getCOREntries(
			long companyId, boolean active, String type, int start, int end)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_corEntryModelResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, COREntryActionKeys.VIEW_COR_ENTRIES);

		return corEntryPersistence.findByC_A_LikeType(
			companyId, active, type, start, end);
	}

	@Override
	public List<COREntry> getCOREntries(
			long companyId, String type, int start, int end)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_corEntryModelResourcePermission.getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null, COREntryActionKeys.VIEW_COR_ENTRIES);

		return corEntryLocalService.getCOREntries(companyId, type, start, end);
	}

	@Override
	public COREntry getCOREntry(long corEntryId) throws PortalException {
		_corEntryModelResourcePermission.check(
			getPermissionChecker(), corEntryId, ActionKeys.VIEW);

		return corEntryPersistence.findByPrimaryKey(corEntryId);
	}

	@Override
	public COREntry updateCOREntry(
			long corEntryId, boolean active, String description,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String name, int priority, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		_corEntryModelResourcePermission.check(
			getPermissionChecker(), corEntryId, ActionKeys.UPDATE);

		return corEntryLocalService.updateCOREntry(
			getUserId(), corEntryId, active, description, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, name,
			priority, typeSettings, serviceContext);
	}

	@Override
	public COREntry updateCOREntryExternalReferenceCode(
			String externalReferenceCode, long corEntryId)
		throws PortalException {

		_corEntryModelResourcePermission.check(
			getPermissionChecker(), corEntryId, ActionKeys.UPDATE);

		return corEntryLocalService.updateCOREntryExternalReferenceCode(
			externalReferenceCode, corEntryId);
	}

	@Override
	public COREntry updateCOREntryTypeSettings(
			long corEntryId, String typeSettings)
		throws PortalException {

		_corEntryModelResourcePermission.check(
			getPermissionChecker(), corEntryId, ActionKeys.UPDATE);

		return corEntryLocalService.updateCOREntryTypeSettings(
			corEntryId, typeSettings);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntry)"
	)
	private ModelResourcePermission<COREntry> _corEntryModelResourcePermission;

}