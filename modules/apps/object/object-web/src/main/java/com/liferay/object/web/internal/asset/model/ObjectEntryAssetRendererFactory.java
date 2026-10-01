/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.web.internal.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class ObjectEntryAssetRendererFactory
	extends BaseAssetRendererFactory<ObjectEntry> {

	public ObjectEntryAssetRendererFactory(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		DLAppLocalService dlAppLocalService, DLURLHelper dlURLHelper,
		ObjectDefinition objectDefinition,
		ObjectEntryDisplayContextFactory objectEntryDisplayContextFactory,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService,
		ServletContext servletContext) {

		setClassName(objectDefinition.getClassName());
		setSearchable(true);
		setPortletId(objectDefinition.getPortletId());

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_dlAppLocalService = dlAppLocalService;
		_dlURLHelper = dlURLHelper;
		_objectDefinition = objectDefinition;
		_objectEntryDisplayContextFactory = objectEntryDisplayContextFactory;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryService = objectEntryService;
		_objectFieldLocalService = objectFieldLocalService;
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
				_assetDisplayPageFriendlyURLProvider, _dlAppLocalService,
				_dlURLHelper, _objectDefinition,
				_objectEntryLocalService.getObjectEntry(classPK),
				_objectEntryDisplayContextFactory, _objectEntryService,
				_objectFieldLocalService);

		objectEntryAssetRenderer.setServletContext(_servletContext);

		return objectEntryAssetRenderer;
	}

	@Override
	public String getIconCssClass() {
		if (!_objectDefinition.isCMS()) {
			return StringPool.BLANK;
		}

		return _icons.getOrDefault(
			_objectDefinition.getExternalReferenceCode(), "forms");
	}

	@Override
	public String getType() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getTypeName(Locale locale) {
		String typeName = super.getTypeName(locale);

		if (_objectDefinition.isCMS()) {
			typeName = StringUtil.appendParentheticalSuffix(typeName, "CMS");
		}

		return typeName;
	}

	@Override
	public boolean hasPermission(
		PermissionChecker permissionChecker, long classPK, String actionId) {

		if (Objects.equals(actionId, ActionKeys.DOWNLOAD) &&
			_objectDefinition.isCMS()) {

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				_objectDefinition.getObjectDefinitionId(), "file");

			if ((objectField != null) &&
				objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				actionId = objectField.getAttachmentDownloadActionKey();
			}
		}

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

	private static final Map<String, String> _icons = HashMapBuilder.put(
		"L_CMS_BASIC_DOCUMENT", "documents-and-media"
	).put(
		"L_CMS_BASIC_WEB_CONTENT", "forms"
	).put(
		"L_CMS_BLOG", "blogs"
	).put(
		"L_CMS_EXTERNAL_VIDEO", "video"
	).put(
		"L_CMS_VOCABULARY", "vocabulary"
	).put(
		"L_CONTENTS", "web-content"
	).put(
		"L_FILES", "document-default"
	).build();

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DLAppLocalService _dlAppLocalService;
	private final DLURLHelper _dlURLHelper;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryDisplayContextFactory
		_objectEntryDisplayContextFactory;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ServletContext _servletContext;

}