/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.web.internal.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

/**
 * @author Feliphe Marinho
 */
public class ObjectEntryAssetRendererFactory
	extends BaseAssetRendererFactory<ObjectEntry> {

	public ObjectEntryAssetRendererFactory(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ObjectDefinition objectDefinition,
		ObjectEntryDisplayContextFactory objectEntryDisplayContextFactory,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryService objectEntryService, ServletContext servletContext) {

		setClassName(objectDefinition.getClassName());
		setSearchable(true);
		setPortletId(objectDefinition.getPortletId());

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_objectDefinition = objectDefinition;
		_objectEntryDisplayContextFactory = objectEntryDisplayContextFactory;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryService = objectEntryService;
		_servletContext = servletContext;
	}

	@Override
	public AssetRenderer<ObjectEntry> getAssetRenderer(long classPK, int type)
		throws PortalException {

		if (!_objectDefinition.isDefaultStorageType()) {
			return null;
		}

		ObjectEntryAssetRenderer objectEntryAssetRenderer =
			new ObjectEntryAssetRenderer(
				_assetDisplayPageFriendlyURLProvider, _objectDefinition,
				_objectEntryLocalService.getObjectEntry(classPK),
				_objectEntryDisplayContextFactory, _objectEntryService);

		objectEntryAssetRenderer.setServletContext(_servletContext);

		return objectEntryAssetRenderer;
	}

	@Override
	public String getIconCssClass() {
		return StringPool.BLANK;
	}

	@Override
	public String getType() {
		return _objectDefinition.getClassName();
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		try {
			return ObjectDefinitionResourcePermissionUtil.
				hasModelResourcePermission(
					_objectDefinition, classPK, _objectEntryService, actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean isActive(long companyId) {
		if (_objectDefinition.getCompanyId() == companyId) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isSelectable() {
		return !StringUtil.equals(
			_objectDefinition.getScope(),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryAssetRendererFactory.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryDisplayContextFactory
		_objectEntryDisplayContextFactory;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryService _objectEntryService;
	private final ServletContext _servletContext;

}