/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.base.SiteNavigationMenuItemServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"json.web.service.context.name=sitenavigation",
		"json.web.service.context.path=SiteNavigationMenuItem"
	},
	service = AopService.class
)
public class SiteNavigationMenuItemServiceImpl
	extends SiteNavigationMenuItemServiceBaseImpl {

	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			externalReferenceCode, getUserId(), groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, typeSettings, serviceContext);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByPrimaryKey(
				siteNavigationMenuItemId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(),
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId, boolean deleteChildren)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByPrimaryKey(
				siteNavigationMenuItemId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(),
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId, deleteChildren);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemLocalService.
				getSiteNavigationMenuItemByExternalReferenceCode(
					externalReferenceCode, groupId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(),
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteSiteNavigationMenuItems(long siteNavigationMenuId)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.UPDATE);

		siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItems(
			siteNavigationMenuId);
	}

	@Override
	public List<Long> getParentSiteNavigationMenuItemIds(
			long siteNavigationMenuId, String typeSettingsKeyword)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.VIEW);

		return siteNavigationMenuItemLocalService.
			getParentSiteNavigationMenuItemIds(
				siteNavigationMenuId, typeSettingsKeyword);
	}

	@Override
	public SiteNavigationMenuItem
			getSiteNavigationMenuItemByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemLocalService.
				getSiteNavigationMenuItemByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (siteNavigationMenuItem != null) {
			_siteNavigationMenuModelResourcePermission.check(
				getPermissionChecker(),
				siteNavigationMenuItem.getSiteNavigationMenuId(),
				ActionKeys.VIEW);
		}

		return siteNavigationMenuItem;
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
			long siteNavigationMenuId)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.VIEW);

		return siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId);
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.VIEW);

		return siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
			long siteNavigationMenuId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.VIEW);

		return siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
			siteNavigationMenuId, orderByComparator);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, long parentSiteNavigationMenuItemId,
			int order)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByPrimaryKey(
				siteNavigationMenuItemId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(),
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			siteNavigationMenuItemId, parentSiteNavigationMenuItemId, order);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByPrimaryKey(
				siteNavigationMenuItemId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(),
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
			getUserId(), siteNavigationMenuItemId, typeSettings,
			serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.site.navigation.model.SiteNavigationMenu)"
	)
	private ModelResourcePermission<SiteNavigationMenu>
		_siteNavigationMenuModelResourcePermission;

}