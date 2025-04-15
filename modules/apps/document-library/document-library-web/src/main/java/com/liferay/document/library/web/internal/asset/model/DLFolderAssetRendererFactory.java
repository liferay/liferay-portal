/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
	service = AssetRendererFactory.class
)
public class DLFolderAssetRendererFactory
	extends BaseAssetRendererFactory<Folder> {

	public static final String TYPE = "document_folder";

	public DLFolderAssetRendererFactory() {
		setCategorizable(false);
		setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<Folder> getAssetRenderer(long classPK, int type)
		throws PortalException {

		DLFolderAssetRenderer dlFolderAssetRenderer = new DLFolderAssetRenderer(
			_dlAppLocalService.getFolder(classPK), _trashHelper);

		dlFolderAssetRenderer.setAssetRendererType(type);

		return dlFolderAssetRenderer;
	}

	@Override
	public String getClassName() {
		return DLFolder.class.getName();
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
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				DLPortletKeys.DOCUMENT_LIBRARY, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		}
		catch (WindowStateException windowStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(windowStateException);
			}
		}

		return liferayPortletURL;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _folderModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFolderAssetRendererFactory.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}