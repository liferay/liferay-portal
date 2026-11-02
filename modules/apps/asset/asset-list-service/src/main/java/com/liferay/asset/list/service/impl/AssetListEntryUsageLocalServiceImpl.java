/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.constants.AssetListEntryUsageConstants;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.service.base.AssetListEntryUsageLocalServiceBaseImpl;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntryUsage",
	service = AopService.class
)
public class AssetListEntryUsageLocalServiceImpl
	extends AssetListEntryUsageLocalServiceBaseImpl {

	@Override
	public AssetListEntryUsage addAssetListEntryUsage(
			long userId, long groupId, long classNameId, String containerKey,
			long containerType, String key, long plid,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long assetListEntryUsageId = counterLocalService.increment();

		AssetListEntryUsage assetListEntryUsage =
			assetListEntryUsagePersistence.create(assetListEntryUsageId);

		assetListEntryUsage.setUuid(serviceContext.getUuid());
		assetListEntryUsage.setGroupId(groupId);
		assetListEntryUsage.setCompanyId(user.getCompanyId());
		assetListEntryUsage.setUserId(userId);
		assetListEntryUsage.setUserName(user.getFullName());
		assetListEntryUsage.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		assetListEntryUsage.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		assetListEntryUsage.setClassNameId(classNameId);
		assetListEntryUsage.setContainerKey(containerKey);
		assetListEntryUsage.setContainerType(containerType);
		assetListEntryUsage.setKey(key);
		assetListEntryUsage.setPlid(plid);
		assetListEntryUsage.setType(_getType(plid));

		return assetListEntryUsagePersistence.update(assetListEntryUsage);
	}

	@Override
	public void deleteAssetListEntryUsages(long containerType, long plid) {
		assetListEntryUsagePersistence.removeByCT_P(containerType, plid);
	}

	@Override
	public void deleteAssetListEntryUsages(
		String containerKey, long containerType, long plid) {

		assetListEntryUsagePersistence.removeByCK_CT_P(
			containerKey, containerType, plid);
	}

	@Override
	public List<AssetListEntryUsage> getAssetEntryListUsagesByPlid(long plid) {
		return assetListEntryUsagePersistence.findByPlid(plid);
	}

	@Override
	public List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key) {

		return assetListEntryUsagePersistence.findByG_C_K(
			groupId, classNameId, key);
	}

	@Override
	public List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key, int type, int start,
		int end, OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return assetListEntryUsagePersistence.findByG_C_K_T(
			groupId, classNameId, key, type, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key, int start, int end,
		OrderByComparator<AssetListEntryUsage> orderByComparator) {

		return assetListEntryUsagePersistence.findByG_C_K(
			groupId, classNameId, key, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntryUsage> getAssetListEntryUsages(
		String containerKey, long containerType, long plid) {

		return assetListEntryUsagePersistence.findByCK_CT_P(
			containerKey, containerType, plid);
	}

	@Override
	public int getAssetListEntryUsagesCount(
		long groupId, long classNameId, String key) {

		return assetListEntryUsagePersistence.countByG_C_K(
			groupId, classNameId, key);
	}

	@Override
	public int getAssetListEntryUsagesCount(
		long groupId, long classNameId, String key, int type) {

		return assetListEntryUsagePersistence.countByG_C_K_T(
			groupId, classNameId, key, type);
	}

	@Override
	public int getCompanyAssetListEntryUsagesCount(
		long companyId, long classNameId, String key) {

		return assetListEntryUsagePersistence.countByC_C_K(
			companyId, classNameId, key);
	}

	private int _getType(long plid) {
		if (plid <= 0) {
			return AssetListEntryUsageConstants.TYPE_DEFAULT;
		}

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return AssetListEntryUsageConstants.TYPE_DEFAULT;
		}

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry == null) {
			return AssetListEntryUsageConstants.TYPE_LAYOUT;
		}

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			return AssetListEntryUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE;
		}

		return AssetListEntryUsageConstants.TYPE_PAGE_TEMPLATE;
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}