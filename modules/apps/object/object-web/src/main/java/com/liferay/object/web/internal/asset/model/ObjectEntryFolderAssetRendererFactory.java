/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
	service = AssetRendererFactory.class
)
public class ObjectEntryFolderAssetRendererFactory
	extends BaseAssetRendererFactory<ObjectEntryFolder> {

	public static final String TYPE = "object_entry_folder";

	public ObjectEntryFolderAssetRendererFactory() {
		setClassName(ObjectEntryFolder.class.getName());
		setLinkable(true);
		setPortletId(ObjectPortletKeys.OBJECT_DEFINITIONS);
	}

	@Override
	public AssetRenderer<ObjectEntryFolder> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderService.getObjectEntryFolder(classPK);

		if (!FeatureFlagManagerUtil.isEnabled(
				objectEntryFolder.getCompanyId(), "LPD-17564")) {

			return null;
		}

		return new ObjectEntryFolderAssetRenderer(
			_assetDisplayPageFriendlyURLProvider, _depotEntryLocalService,
			objectEntryFolder, this, _portal);
	}

	@Override
	public String getClassName() {
		return ObjectEntryFolder.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "folder";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _objectEntryFolderModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Override
	public boolean isSearchable() {
		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getCompanyId(), "LPD-17564");
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private Portal _portal;

}