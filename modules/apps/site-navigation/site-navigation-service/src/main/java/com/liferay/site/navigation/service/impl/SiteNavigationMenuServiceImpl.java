/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.navigation.constants.SiteNavigationActionKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.base.SiteNavigationMenuServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"json.web.service.context.name=sitenavigation",
		"json.web.service.context.path=SiteNavigationMenu"
	},
	service = AopService.class
)
public class SiteNavigationMenuServiceImpl
	extends SiteNavigationMenuServiceBaseImpl {

	@Override
	public SiteNavigationMenu addSiteNavigationMenu(
			String externalReferenceCode, long groupId, String name, int type,
			boolean auto, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		return siteNavigationMenuLocalService.addSiteNavigationMenu(
			externalReferenceCode, getUserId(), groupId, name, type, auto,
			serviceContext);
	}

	@Override
	public SiteNavigationMenu addSiteNavigationMenu(
			String externalReferenceCode, long groupId, String name, int type,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		return siteNavigationMenuLocalService.addSiteNavigationMenu(
			externalReferenceCode, getUserId(), groupId, name, type,
			serviceContext);
	}

	@Override
	public SiteNavigationMenu addSiteNavigationMenu(
			String externalReferenceCode, long groupId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			SiteNavigationActionKeys.ADD_SITE_NAVIGATION_MENU);

		return siteNavigationMenuLocalService.addSiteNavigationMenu(
			externalReferenceCode, getUserId(), groupId, name, serviceContext);
	}

	@Override
	public SiteNavigationMenu deleteSiteNavigationMenu(
			long siteNavigationMenuId)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.DELETE);

		return siteNavigationMenuLocalService.deleteSiteNavigationMenu(
			siteNavigationMenuId);
	}

	@Override
	public SiteNavigationMenu deleteSiteNavigationMenu(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SiteNavigationMenu siteNavigationMenu =
			siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode, groupId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenu, ActionKeys.DELETE);

		return siteNavigationMenuLocalService.deleteSiteNavigationMenu(
			siteNavigationMenu);
	}

	@Override
	public SiteNavigationMenu fetchSiteNavigationMenu(long siteNavigationMenuId)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.VIEW);

		return siteNavigationMenuPersistence.fetchByPrimaryKey(
			siteNavigationMenuId);
	}

	@Override
	public SiteNavigationMenu getSiteNavigationMenuByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SiteNavigationMenu siteNavigationMenu =
			siteNavigationMenuLocalService.
				getSiteNavigationMenuByExternalReferenceCode(
					externalReferenceCode, groupId);

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenu, ActionKeys.VIEW);

		return siteNavigationMenu;
	}

	@Override
	public List<SiteNavigationMenu> getSiteNavigationMenus(long groupId) {
		return siteNavigationMenuPersistence.filterFindByGroupId(groupId);
	}

	@Override
	public List<SiteNavigationMenu> getSiteNavigationMenus(
		long groupId, int start, int end,
		OrderByComparator<SiteNavigationMenu> orderByComparator) {

		return getSiteNavigationMenus(
			new long[] {groupId}, start, end, orderByComparator);
	}

	@Override
	public List<SiteNavigationMenu> getSiteNavigationMenus(
		long groupId, String keywords, int start, int end,
		OrderByComparator<SiteNavigationMenu> orderByComparator) {

		return getSiteNavigationMenus(
			new long[] {groupId}, keywords, start, end, orderByComparator);
	}

	@Override
	public List<SiteNavigationMenu> getSiteNavigationMenus(
		long[] groupIds, int start, int end,
		OrderByComparator<SiteNavigationMenu> orderByComparator) {

		return siteNavigationMenuPersistence.filterFindByGroupId(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public List<SiteNavigationMenu> getSiteNavigationMenus(
		long[] groupIds, String keywords, int start, int end,
		OrderByComparator<SiteNavigationMenu> orderByComparator) {

		return siteNavigationMenuPersistence.filterFindByG_LikeN(
			groupIds,
			_customSQL.keywords(keywords, false, WildcardMode.SURROUND)[0],
			start, end, orderByComparator);
	}

	@Override
	public int getSiteNavigationMenusCount(long groupId) {
		return getSiteNavigationMenusCount(new long[] {groupId});
	}

	@Override
	public int getSiteNavigationMenusCount(long groupId, String keywords) {
		return getSiteNavigationMenusCount(new long[] {groupId}, keywords);
	}

	@Override
	public int getSiteNavigationMenusCount(long[] groupIds) {
		return siteNavigationMenuPersistence.filterCountByGroupId(groupIds);
	}

	@Override
	public int getSiteNavigationMenusCount(long[] groupIds, String keywords) {
		return siteNavigationMenuPersistence.filterCountByG_LikeN(
			groupIds,
			_customSQL.keywords(keywords, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public SiteNavigationMenu updateSiteNavigationMenu(
			long siteNavigationMenuId, int type, boolean auto,
			ServiceContext serviceContext)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.UPDATE);

		return siteNavigationMenuLocalService.updateSiteNavigationMenu(
			getUserId(), siteNavigationMenuId, type, auto, serviceContext);
	}

	@Override
	public SiteNavigationMenu updateSiteNavigationMenu(
			long siteNavigationMenuId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		_siteNavigationMenuModelResourcePermission.check(
			getPermissionChecker(), siteNavigationMenuId, ActionKeys.UPDATE);

		return siteNavigationMenuLocalService.updateSiteNavigationMenu(
			getUserId(), siteNavigationMenuId, name, serviceContext);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference(
		target = "(resource.name=" + SiteNavigationConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.site.navigation.model.SiteNavigationMenu)"
	)
	private ModelResourcePermission<SiteNavigationMenu>
		_siteNavigationMenuModelResourcePermission;

}