/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.impl;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.base.AssetDisplayPageEntryServiceBaseImpl;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"json.web.service.context.name=asset",
		"json.web.service.context.path=AssetDisplayPageEntry"
	},
	service = AopService.class
)
public class AssetDisplayPageEntryServiceImpl
	extends AssetDisplayPageEntryServiceBaseImpl {

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, int type,
			ServiceContext serviceContext)
		throws Exception {

		_checkPermissions(
			_portal.getClassName(classNameId), classPK, ActionKeys.UPDATE);

		return assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			getUserId(), groupId, classNameId, classPK,
			layoutPageTemplateEntryId, type, serviceContext);
	}

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, ServiceContext serviceContext)
		throws Exception {

		_checkPermissions(
			_portal.getClassName(classNameId), classPK, ActionKeys.UPDATE);

		return assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			getUserId(), groupId, classNameId, classPK,
			layoutPageTemplateEntryId, serviceContext);
	}

	@Override
	public void deleteAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws Exception {

		_checkPermissions(
			_portal.getClassName(classNameId), classPK, ActionKeys.DELETE);

		assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
			groupId, classNameId, classPK);
	}

	@Override
	public AssetDisplayPageEntry fetchAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws Exception {

		_checkPermissions(
			_portal.getClassName(classNameId), classPK, ActionKeys.VIEW);

		return assetDisplayPageEntryPersistence.fetchByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		if (!_hasLayoutPageTemplateEntryViewPermission(
				layoutPageTemplateEntryId)) {

			return Collections.emptyList();
		}

		return ListUtil.filter(
			assetDisplayPageEntryLocalService.getAssetDisplayPageEntries(
				classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate, start, end, orderByComparator),
			this::_hasAssetViewPermission);
	}

	@Override
	public List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		if (!_hasLayoutPageTemplateEntryViewPermission(
				layoutPageTemplateEntryId)) {

			return Collections.emptyList();
		}

		return ListUtil.filter(
			assetDisplayPageEntryLocalService.
				getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
					layoutPageTemplateEntryId),
			this::_hasAssetViewPermission);
	}

	@Override
	public List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		if (!_hasLayoutPageTemplateEntryViewPermission(
				layoutPageTemplateEntryId)) {

			return Collections.emptyList();
		}

		return ListUtil.filter(
			assetDisplayPageEntryLocalService.
				getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
					layoutPageTemplateEntryId, start, end, orderByComparator),
			this::_hasAssetViewPermission);
	}

	@Override
	public int getAssetDisplayPageEntriesCount(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		if (!_hasLayoutPageTemplateEntryViewPermission(
				layoutPageTemplateEntryId)) {

			return 0;
		}

		return assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesCount(
				classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate);
	}

	@Override
	public int getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		if (!_hasLayoutPageTemplateEntryViewPermission(
				layoutPageTemplateEntryId)) {

			return 0;
		}

		return assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	@Override
	public AssetDisplayPageEntry updateAssetDisplayPageEntry(
			long assetDisplayPageEntryId, long layoutPageTemplateEntryId,
			int type)
		throws Exception {

		AssetDisplayPageEntry assetDisplayPageEntry =
			assetDisplayPageEntryPersistence.fetchByPrimaryKey(
				assetDisplayPageEntryId);

		_checkPermissions(
			assetDisplayPageEntry.getClassName(),
			assetDisplayPageEntry.getClassPK(), ActionKeys.UPDATE);

		return assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntryId, layoutPageTemplateEntryId, type);
	}

	private void _checkPermissions(
			String className, long classPK, String actionId)
		throws Exception {

		if (!_hasPermission(className, classPK, actionId)) {
			throw new PrincipalException();
		}
	}

	private boolean _hasAssetViewPermission(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		try {
			return _hasPermission(
				assetDisplayPageEntry.getClassName(),
				assetDisplayPageEntry.getClassPK(), ActionKeys.VIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private boolean _hasLayoutPageTemplateEntryViewPermission(
		long layoutPageTemplateEntryId) {

		try {
			return _layoutPageTemplateEntryModelResourcePermission.contains(
				getPermissionChecker(), layoutPageTemplateEntryId,
				ActionKeys.VIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private boolean _hasPermission(
			String className, long classPK, String actionId)
		throws Exception {

		InfoItemPermissionProvider infoItemPermissionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemPermissionProvider.class, className);

		if (infoItemPermissionProvider != null) {
			return infoItemPermissionProvider.hasPermission(
				getPermissionChecker(),
				new InfoItemReference(className, classPK), actionId);
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		return assetRendererFactory.hasPermission(
			getPermissionChecker(), classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetDisplayPageEntryServiceImpl.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry)"
	)
	private ModelResourcePermission<LayoutPageTemplateEntry>
		_layoutPageTemplateEntryModelResourcePermission;

	@Reference
	private Portal _portal;

}