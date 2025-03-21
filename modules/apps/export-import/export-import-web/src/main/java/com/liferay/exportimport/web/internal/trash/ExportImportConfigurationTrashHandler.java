/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.trash;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.BaseTrashHandler;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Levente Hudák
 */
@Component(
	property = "model.class.name=com.liferay.exportimport.kernel.model.ExportImportConfiguration",
	service = TrashHandler.class
)
public class ExportImportConfigurationTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_exportImportConfigurationLocalService.deleteExportImportConfiguration(
			classPK);
	}

	@Override
	public String getClassName() {
		return ExportImportConfiguration.class.getName();
	}

	@Override
	public String getRestoreMessage(
		PortletRequest portletRequest, long classPK) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.translate("export-import-template");
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _exportImportConfigurationLocalService.
			fetchExportImportConfiguration(classPK);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		ExportImportConfigurationTrashRenderer
			exportImportConfigurationTrashRenderer =
				new ExportImportConfigurationTrashRenderer(
					_exportImportConfigurationLocalService.
						getExportImportConfiguration(classPK));

		exportImportConfigurationTrashRenderer.setServletContext(
			_servletContext);

		return exportImportConfigurationTrashRenderer;
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_exportImportConfigurationLocalService.
			restoreExportImportConfigurationFromTrash(userId, classPK);
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				classPK);

		return GroupPermissionUtil.contains(
			permissionChecker,
			_groupLocalService.getGroup(exportImportConfiguration.getGroupId()),
			actionId);
	}

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.exportimport.web)")
	private ServletContext _servletContext;

}