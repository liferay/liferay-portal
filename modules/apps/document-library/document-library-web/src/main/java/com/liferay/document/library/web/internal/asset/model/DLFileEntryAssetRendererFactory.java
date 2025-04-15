/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.document.library.asset.model.DLFileEntryClassTypeReader;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
	service = AssetRendererFactory.class
)
public class DLFileEntryAssetRendererFactory
	extends BaseAssetRendererFactory<FileEntry> {

	public static final String TYPE = "document";

	public DLFileEntryAssetRendererFactory() {
		setLinkable(true);
		setPortletId(DLPortletKeys.DOCUMENT_LIBRARY);
		setSearchable(true);
		setSupportsClassTypes(true);
	}

	@Override
	public AssetRenderer<FileEntry> getAssetRenderer(long classPK, int type)
		throws PortalException {

		FileEntry fileEntry = null;
		FileVersion fileVersion = null;

		try {
			fileEntry = _dlAppLocalService.getFileEntry(classPK);

			if (type == TYPE_LATEST) {
				fileVersion = fileEntry.getLatestFileVersion();
			}
			else if (type == TYPE_LATEST_APPROVED) {
				fileVersion = fileEntry.getFileVersion();
			}
			else {
				throw new IllegalArgumentException(
					"Unknown asset renderer type " + type);
			}
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileEntryException);
			}

			fileVersion = _dlAppLocalService.getFileVersion(classPK);

			fileEntry = fileVersion.getFileEntry();
		}

		DLFileEntryAssetRenderer dlFileEntryAssetRenderer =
			new DLFileEntryAssetRenderer(
				fileEntry, fileVersion, _dlFileEntryLocalService, _trashHelper,
				_dlURLHelper);

		dlFileEntryAssetRenderer.setAssetDisplayPageFriendlyURLProvider(
			_assetDisplayPageFriendlyURLProvider);
		dlFileEntryAssetRenderer.setAssetRendererType(type);

		return dlFileEntryAssetRenderer;
	}

	@Override
	public String getClassName() {
		return DLFileEntry.class.getName();
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		return new DLFileEntryClassTypeReader();
	}

	@Override
	public String getIconCssClass() {
		return "documents-and-media";
	}

	@Override
	public String getSubtypeTitle(Locale locale) {
		return _language.get(locale, "type");
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getTypeName(Locale locale, long subtypeId) {
		try {
			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.getFileEntryType(subtypeId);

			return dlFileEntryType.getName(locale);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return super.getTypeName(locale, subtypeId);
		}
	}

	@Override
	public PortletURL getURLAdd(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		Group group = getGroup(liferayPortletRequest);

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).setCMD(
			Constants.ADD
		).setParameter(
			"fileEntryTypeId",
			() -> {
				long fileEntryTypeId =
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT;

				if (classTypeId >= 0) {
					fileEntryTypeId = classTypeId;
				}

				return fileEntryTypeId;
			}
		).setParameter(
			"folderId", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID
		).setParameter(
			"groupId", group.getGroupId()
		).setParameter(
			"showMountFolder", false
		).setParameter(
			"showSelectFolder", true
		).buildPortletURL();
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
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		if ((classTypeId > 0) &&
			!_dlFileEntryTypeModelResourcePermission.contains(
				permissionChecker, classTypeId, ActionKeys.VIEW)) {

			return false;
		}

		return _portletResourcePermission.contains(
			permissionChecker, groupId, ActionKeys.ADD_DOCUMENT);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _fileEntryModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryAssetRendererFactory.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.document.library.kernel.model.DLFileEntryType)"
	)
	private ModelResourcePermission<DLFileEntryType>
		_dlFileEntryTypeModelResourcePermission;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(target = "(resource.name=" + DLConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}