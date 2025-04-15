/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBFolderPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Marco Galluzzi
 */
public class KBFolderAssetRenderer
	extends BaseAssetRenderer<KBFolder> implements TrashRenderer {

	public KBFolderAssetRenderer(KBFolder kbFolder, TrashHelper trashHelper) {
		_kbFolder = kbFolder;
		_trashHelper = trashHelper;
	}

	@Override
	public KBFolder getAssetObject() {
		return _kbFolder;
	}

	@Override
	public String getClassName() {
		return KBFolder.class.getName();
	}

	@Override
	public long getClassPK() {
		return _kbFolder.getKbFolderId();
	}

	@Override
	public long getGroupId() {
		return _kbFolder.getGroupId();
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<KBFolder> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _kbFolder.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _kbFolder.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		if (_trashHelper == null) {
			return _kbFolder.getName();
		}

		return _trashHelper.getOriginalTitle(_kbFolder.getName());
	}

	@Override
	public String getType() {
		return KBFolderAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		Group group = GroupLocalServiceUtil.fetchGroup(_kbFolder.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group,
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/admin/common/edit_kb_folder.jsp"
		).setParameter(
			"kbFolderId", _kbFolder.getKbFolderId()
		).buildPortletURL();
	}

	@Override
	public long getUserId() {
		return _kbFolder.getUserId();
	}

	@Override
	public String getUserName() {
		return _kbFolder.getUserName();
	}

	@Override
	public String getUuid() {
		return _kbFolder.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return KBFolderPermission.contains(
			permissionChecker, _kbFolder, KBActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return KBFolderPermission.contains(
			permissionChecker, _kbFolder, KBActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final KBFolder _kbFolder;
	private final TrashHelper _trashHelper;

}