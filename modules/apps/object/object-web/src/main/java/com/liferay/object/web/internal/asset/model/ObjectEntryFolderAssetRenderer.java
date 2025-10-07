/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Mikel Lorza
 */
public class ObjectEntryFolderAssetRenderer
	extends BaseAssetRenderer<ObjectEntryFolder> {

	public ObjectEntryFolderAssetRenderer(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		DepotEntryLocalService depotEntryLocalService,
		ObjectEntryFolder objectEntryFolder,
		ObjectEntryFolderAssetRendererFactory
			objectEntryFolderAssetRendererFactory,
		Portal portal) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_depotEntryLocalService = depotEntryLocalService;
		_objectEntryFolder = objectEntryFolder;
		_objectEntryFolderAssetRendererFactory =
			objectEntryFolderAssetRendererFactory;
		_portal = portal;
	}

	@Override
	public ObjectEntryFolder getAssetObject() {
		return _objectEntryFolder;
	}

	@Override
	public String getClassName() {
		return ObjectEntryFolder.class.getName();
	}

	@Override
	public long getClassPK() {
		return _objectEntryFolder.getObjectEntryFolderId();
	}

	@Override
	public long getGroupId() {
		return _objectEntryFolder.getGroupId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _objectEntryFolder.getLabel(locale);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws Exception {

		if (themeDisplay == null) {
			return null;
		}

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			_objectEntryFolder.getGroupId());

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return _assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					getClassName(),
					new ClassPKInfoItemIdentifier(getClassPK())),
				themeDisplay);
		}

		return StringBundler.concat(
			themeDisplay.getPortalURL(),
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/view-folder/",
			_portal.getClassNameId(getClassName()), StringPool.SLASH,
			_objectEntryFolder.getObjectEntryFolderId(),
			"?p_l_mode=read&redirect=",
			HtmlUtil.escapeURL(themeDisplay.getURLCurrent()));
	}

	@Override
	public long getUserId() {
		return _objectEntryFolder.getUserId();
	}

	@Override
	public String getUserName() {
		return _objectEntryFolder.getUserName();
	}

	@Override
	public String getUuid() {
		return _objectEntryFolder.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _hasPermission(permissionChecker, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return _hasPermission(permissionChecker, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String template) {

		return false;
	}

	@Override
	public boolean isDisplayable() {
		return false;
	}

	private boolean _hasPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		try {
			return _objectEntryFolderAssetRendererFactory.hasPermission(
				permissionChecker, _objectEntryFolder.getObjectEntryFolderId(),
				actionId);
		}
		catch (PortalException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final ObjectEntryFolder _objectEntryFolder;
	private final ObjectEntryFolderAssetRendererFactory
		_objectEntryFolderAssetRendererFactory;
	private final Portal _portal;

}