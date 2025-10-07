/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.impl;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.navigation.exception.InvalidSiteNavigationMenuItemOrderException;
import com.liferay.site.navigation.exception.InvalidSiteNavigationMenuItemTypeException;
import com.liferay.site.navigation.exception.SiteNavigationMenuItemNameException;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.model.SiteNavigationMenuItemTable;
import com.liferay.site.navigation.service.base.SiteNavigationMenuItemLocalServiceBaseImpl;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuPersistence;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;
import com.liferay.site.navigation.util.comparator.SiteNavigationMenuItemOrderComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.site.navigation.model.SiteNavigationMenuItem",
	service = AopService.class
)
public class SiteNavigationMenuItemLocalServiceImpl
	extends SiteNavigationMenuItemLocalServiceBaseImpl {

	@Override
	public SiteNavigationMenuItem addOrUpdateSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings, ServiceContext serviceContext)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (siteNavigationMenuItem == null) {
			siteNavigationMenuItem = addSiteNavigationMenuItem(
				externalReferenceCode, userId, groupId, siteNavigationMenuId,
				parentSiteNavigationMenuItemId, type,
				siteNavigationMenuItemPersistence.countByS_P(
					siteNavigationMenuId, parentSiteNavigationMenuItemId),
				typeSettings, serviceContext);
		}
		else {
			siteNavigationMenuItem = updateSiteNavigationMenuItem(
				userId, siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				groupId, siteNavigationMenuId, parentSiteNavigationMenuItemId,
				type, siteNavigationMenuItem.getOrder(), typeSettings);
		}

		return siteNavigationMenuItem;
	}

	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				type);

		if (siteNavigationMenuItemType == null) {
			throw new InvalidSiteNavigationMenuItemTypeException(type);
		}

		String name = siteNavigationMenuItemType.getName(typeSettings);

		_validateName(name);

		User user = _userLocalService.getUser(userId);

		long siteNavigationMenuItemId = counterLocalService.increment();

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.create(siteNavigationMenuItemId);

		siteNavigationMenuItem.setUuid(serviceContext.getUuid());
		siteNavigationMenuItem.setExternalReferenceCode(externalReferenceCode);
		siteNavigationMenuItem.setGroupId(groupId);
		siteNavigationMenuItem.setCompanyId(user.getCompanyId());
		siteNavigationMenuItem.setUserId(userId);
		siteNavigationMenuItem.setUserName(user.getFullName());
		siteNavigationMenuItem.setSiteNavigationMenuId(siteNavigationMenuId);
		siteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
		siteNavigationMenuItem.setName(name);
		siteNavigationMenuItem.setType(type);
		siteNavigationMenuItem.setTypeSettings(typeSettings);
		siteNavigationMenuItem.setOrder(order);
		siteNavigationMenuItem.setExpandoBridgeAttributes(serviceContext);

		return siteNavigationMenuItemPersistence.update(siteNavigationMenuItem);
	}

	@Override
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		int siteNavigationMenuItemCount =
			siteNavigationMenuItemPersistence.countByS_P(
				siteNavigationMenuId, parentSiteNavigationMenuItemId);

		return addSiteNavigationMenuItem(
			externalReferenceCode, userId, groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, type, siteNavigationMenuItemCount,
			typeSettings, serviceContext);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException {

		return siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItemId, false);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId, boolean deleteChildren)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			getSiteNavigationMenuItem(siteNavigationMenuItemId);

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			getSiteNavigationMenuItems(
				siteNavigationMenuItem.getSiteNavigationMenuId(),
				siteNavigationMenuItemId);

		List<SiteNavigationMenuItem> siblingsSiteNavigationMenuItems =
			getSiteNavigationMenuItems(
				siteNavigationMenuItem.getSiteNavigationMenuId(),
				siteNavigationMenuItem.getParentSiteNavigationMenuItemId());

		int siblingOrderOffset = siteNavigationMenuItems.size();

		if (deleteChildren) {
			siblingOrderOffset = 0;
		}

		for (SiteNavigationMenuItem siblingSiteNavigationMenuItem :
				siblingsSiteNavigationMenuItems) {

			if (siblingSiteNavigationMenuItem.getOrder() <=
					siteNavigationMenuItem.getOrder()) {

				continue;
			}

			siblingSiteNavigationMenuItem.setOrder(
				siblingOrderOffset + siblingSiteNavigationMenuItem.getOrder() -
					1);

			siteNavigationMenuItemPersistence.update(
				siblingSiteNavigationMenuItem);
		}

		for (int i = 0; i < siteNavigationMenuItems.size(); i++) {
			SiteNavigationMenuItem childSiteNavigationMenuItem =
				siteNavigationMenuItems.get(i);

			if (deleteChildren) {
				siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
					childSiteNavigationMenuItem.getSiteNavigationMenuItemId(),
					true);

				continue;
			}

			childSiteNavigationMenuItem.setParentSiteNavigationMenuItemId(
				siteNavigationMenuItem.getParentSiteNavigationMenuItemId());
			childSiteNavigationMenuItem.setOrder(
				siteNavigationMenuItem.getOrder() + i);

			siteNavigationMenuItemPersistence.update(
				childSiteNavigationMenuItem);
		}

		return siteNavigationMenuItemLocalService.deleteSiteNavigationMenuItem(
			siteNavigationMenuItem);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return siteNavigationMenuItemPersistence.remove(siteNavigationMenuItem);
	}

	@Override
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByERC_G(
				externalReferenceCode, groupId);

		return deleteSiteNavigationMenuItem(siteNavigationMenuItem);
	}

	@Override
	public void deleteSiteNavigationMenuItems(long siteNavigationMenuId) {
		siteNavigationMenuItemPersistence.removeBySiteNavigationMenuId(
			siteNavigationMenuId);
	}

	@Override
	public void deleteSiteNavigationMenuItemsByGroupId(long groupId) {
		List<SiteNavigationMenu> siteNavigationMenus =
			_siteNavigationMenuPersistence.findByGroupId(groupId);

		for (SiteNavigationMenu siteNavigationMenu : siteNavigationMenus) {
			siteNavigationMenuItemPersistence.removeBySiteNavigationMenuId(
				siteNavigationMenu.getSiteNavigationMenuId());
		}
	}

	@Override
	public List<Long> getParentSiteNavigationMenuItemIds(
		long siteNavigationMenuId, String typeSettingsKeyword) {

		return siteNavigationMenuItemPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				SiteNavigationMenuItemTable.INSTANCE.
					parentSiteNavigationMenuItemId
			).from(
				SiteNavigationMenuItemTable.INSTANCE
			).where(
				SiteNavigationMenuItemTable.INSTANCE.siteNavigationMenuId.eq(
					siteNavigationMenuId
				).and(
					SiteNavigationMenuItemTable.INSTANCE.typeSettings.like(
						typeSettingsKeyword)
				)
			));
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId) {

		return siteNavigationMenuItemPersistence.findBySiteNavigationMenuId(
			siteNavigationMenuId);
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return siteNavigationMenuItemPersistence.findByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			SiteNavigationMenuItemOrderComparator.getInstance(true));
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return siteNavigationMenuItemPersistence.findBySiteNavigationMenuId(
			siteNavigationMenuId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		String type) {

		return siteNavigationMenuItemPersistence.findByType(type);
	}

	@Override
	public int getSiteNavigationMenuItemsCount(long siteNavigationMenuId) {
		return siteNavigationMenuItemPersistence.countBySiteNavigationMenuId(
			siteNavigationMenuId);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, long parentSiteNavigationMenuItemId,
			int order)
		throws PortalException {

		// Site navigation menu item

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.fetchByPrimaryKey(
				siteNavigationMenuItemId);

		_validate(
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			siteNavigationMenuItemId, parentSiteNavigationMenuItemId);

		long oldParentSiteNavigationMenuItemId =
			siteNavigationMenuItem.getParentSiteNavigationMenuItemId();
		int oldOrder = siteNavigationMenuItem.getOrder();

		siteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
		siteNavigationMenuItem.setOrder(order);

		siteNavigationMenuItem = siteNavigationMenuItemPersistence.update(
			siteNavigationMenuItem);

		// Child site navigation menu item

		int newOrder = 0;

		List<SiteNavigationMenuItem> children = getSiteNavigationMenuItems(
			siteNavigationMenuItem.getSiteNavigationMenuId(),
			parentSiteNavigationMenuItemId);

		for (SiteNavigationMenuItem child : children) {
			if (newOrder == order) {
				newOrder++;
			}

			if (child.getSiteNavigationMenuItemId() ==
					siteNavigationMenuItemId) {

				continue;
			}

			child.setOrder(newOrder++);

			siteNavigationMenuItemPersistence.update(child);
		}

		if (parentSiteNavigationMenuItemId !=
				oldParentSiteNavigationMenuItemId) {

			List<SiteNavigationMenuItem> oldChildren =
				getSiteNavigationMenuItems(
					siteNavigationMenuItem.getSiteNavigationMenuId(),
					oldParentSiteNavigationMenuItemId);

			for (SiteNavigationMenuItem oldChild : oldChildren) {
				if (oldChild.getOrder() <= oldOrder) {
					continue;
				}

				oldChild.setOrder(oldChild.getOrder() - 1);

				siteNavigationMenuItemPersistence.update(oldChild);
			}
		}

		return siteNavigationMenuItem;
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.findByPrimaryKey(
				siteNavigationMenuItemId);

		siteNavigationMenuItem.setGroupId(groupId);
		siteNavigationMenuItem.setUserId(userId);
		siteNavigationMenuItem.setSiteNavigationMenuId(siteNavigationMenuId);
		siteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
		siteNavigationMenuItem.setType(type);
		siteNavigationMenuItem.setTypeSettings(typeSettings);
		siteNavigationMenuItem.setOrder(order);

		return siteNavigationMenuItemPersistence.update(siteNavigationMenuItem);
	}

	@Override
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		SiteNavigationMenuItem siteNavigationMenuItem =
			siteNavigationMenuItemPersistence.fetchByPrimaryKey(
				siteNavigationMenuItemId);

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem.getType());

		if (siteNavigationMenuItemType == null) {
			throw new InvalidSiteNavigationMenuItemTypeException(
				siteNavigationMenuItem.getType());
		}

		User user = _userLocalService.getUser(userId);

		String name = siteNavigationMenuItemType.getName(typeSettings);

		_validateName(name);

		_validateLayout(typeSettings);

		siteNavigationMenuItem.setUserId(userId);
		siteNavigationMenuItem.setUserName(user.getFullName());
		siteNavigationMenuItem.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		siteNavigationMenuItem.setName(name);
		siteNavigationMenuItem.setTypeSettings(typeSettings);
		siteNavigationMenuItem.setExpandoBridgeAttributes(serviceContext);

		return siteNavigationMenuItemPersistence.update(siteNavigationMenuItem);
	}

	private void _validate(
			long siteNavigationMenuId, long siteNavigationMenuItemId,
			long parentSiteNavigationMenuItemId)
		throws PortalException {

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			getSiteNavigationMenuItems(
				siteNavigationMenuId, siteNavigationMenuItemId);

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			siteNavigationMenuItemId =
				siteNavigationMenuItem.getSiteNavigationMenuItemId();

			if (siteNavigationMenuItemId == parentSiteNavigationMenuItemId) {
				throw new InvalidSiteNavigationMenuItemOrderException();
			}

			_validate(
				siteNavigationMenuId, siteNavigationMenuItemId,
				parentSiteNavigationMenuItemId);
		}
	}

	private void _validateLayout(String typeSettings) throws PortalException {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).build();

		String layoutUuid = typeSettingsUnicodeProperties.getProperty(
			"layoutUuid");

		if (Validator.isNull(layoutUuid)) {
			return;
		}

		long groupId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.getProperty("groupId"));
		boolean privateLayout = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("privateLayout"));

		_layoutService.getLayoutByUuidAndGroupId(
			layoutUuid, groupId, privateLayout);
	}

	private void _validateName(String name) throws PortalException {
		if (name == null) {
			return;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			SiteNavigationMenuItem.class.getName(), "name");

		if (name.length() > maxLength) {
			throw new SiteNavigationMenuItemNameException(
				"Maximum length of name exceeded");
		}
	}

	@Reference
	private LayoutService _layoutService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuPersistence _siteNavigationMenuPersistence;

	@Reference
	private UserLocalService _userLocalService;

}