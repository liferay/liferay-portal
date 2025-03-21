/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.document.library.asset.DLFileEntryDDMFormValuesReader;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.capabilities.CommentCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Sergio González
 * @author Zsolt Berentey
 */
public class DLFileEntryAssetRenderer
	extends BaseJSPAssetRenderer<FileEntry> implements TrashRenderer {

	public DLFileEntryAssetRenderer(
		FileEntry fileEntry, FileVersion fileVersion,
		DLFileEntryLocalService dlFileEntryLocalService,
		TrashHelper trashHelper, DLURLHelper dlURLHelper) {

		_fileEntry = fileEntry;
		_fileVersion = fileVersion;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_trashHelper = trashHelper;
		_dlURLHelper = dlURLHelper;
	}

	@Override
	public FileEntry getAssetObject() {
		return _fileEntry;
	}

	@Override
	public String getClassName() {
		return DLFileEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		String version = _fileVersion.getVersion();

		if (!_fileVersion.isApproved() && _fileVersion.isDraft() &&
			!_fileVersion.isPending() &&
			!version.equals(DLFileEntryConstants.VERSION_DEFAULT)) {

			return _fileVersion.getFileVersionId();
		}

		return _fileEntry.getFileEntryId();
	}

	@Override
	public DDMFormValuesReader getDDMFormValuesReader() {
		return new DLFileEntryDDMFormValuesReader(_fileEntry, _fileVersion);
	}

	@Override
	public String getDiscussionPath() {
		if (PropsValues.DL_FILE_ENTRY_COMMENTS_ENABLED) {
			return "edit_file_entry_discussion";
		}

		return null;
	}

	@Override
	public long getGroupId() {
		return _fileEntry.getGroupId();
	}

	@Override
	public String getIconCssClass() {
		return _fileEntry.getIconCssClass();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/document_library/asset/file_entry_" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getNewName(String oldName, String token) {
		String extension = FileUtil.getExtension(oldName);

		if (Validator.isNull(extension)) {
			return super.getNewName(oldName, token);
		}

		int index = oldName.lastIndexOf(CharPool.PERIOD);

		return StringBundler.concat(
			oldName.substring(0, index), StringPool.SPACE, token,
			StringPool.PERIOD, extension);
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<FileEntry> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _fileVersion.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _fileEntry.getDescription();
	}

	@Override
	public String[] getSupportedConversions() {
		return DocumentConversionUtil.getConversions(_fileEntry.getExtension());
	}

	@Override
	public String getThumbnailPath(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String thumbnailSrc = _dlURLHelper.getThumbnailSrc(
			_fileEntry, themeDisplay);

		if (Validator.isNotNull(thumbnailSrc)) {
			return thumbnailSrc;
		}

		return super.getThumbnailPath(portletRequest);
	}

	@Override
	public String getTitle(Locale locale) {
		String title = null;

		if (getAssetRendererType() == AssetRendererFactory.TYPE_LATEST) {
			title = _fileVersion.getTitle();
		}
		else {
			title = _fileEntry.getTitle();
		}

		if (_trashHelper == null) {
			return title;
		}

		return _trashHelper.getOriginalTitle(title);
	}

	@Override
	public String getType() {
		return DLFileEntryAssetRendererFactory.TYPE;
	}

	@Override
	public String getURLDownload(ThemeDisplay themeDisplay) {
		return _dlURLHelper.getDownloadURL(
			_fileEntry, _fileVersion, themeDisplay, StringPool.BLANK);
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.create(
			_getPortletURL(liferayPortletRequest)
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).setParameter(
			"fileEntryId", _fileEntry.getFileEntryId()
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLExport(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
		).setActionName(
			"/document_library/get_file"
		).setParameter(
			"folderId", _fileEntry.getFolderId()
		).setParameter(
			"groupId", _fileEntry.getRepositoryId()
		).setParameter(
			"title", _fileEntry.getTitle()
		).buildPortletURL();
	}

	@Override
	public String getURLImagePreview(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _dlURLHelper.getImagePreviewURL(
			_fileEntry, _fileVersion, themeDisplay);
	}

	@Override
	public String getURLShare(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _dlURLHelper.getPreviewURL(
			_fileEntry, _fileVersion, themeDisplay, StringPool.BLANK, false,
			true);
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<FileEntry> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/document_library/view_file_entry"
		).setParameter(
			"fileEntryId", _fileEntry.getFileEntryId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws PortalException {

		if (_assetDisplayPageFriendlyURLProvider != null) {
			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						FileEntry.class.getName(),
						new ClassPKInfoItemIdentifier(getClassPK())),
					themeDisplay);

			if (Validator.isNotNull(friendlyURL)) {
				return friendlyURL;
			}
		}

		if (!_hasViewInContextGroupLayout(
				themeDisplay, _fileEntry.getGroupId())) {

			return null;
		}

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect,
			"/document_library/find_file_entry", "fileEntryId",
			_fileEntry.getFileEntryId());
	}

	@Override
	public long getUserId() {
		return _fileEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _fileEntry.getUserName();
	}

	@Override
	public String getUuid() {
		return _fileEntry.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DLFileEntryPermission.contains(
			permissionChecker, _fileEntry.getFileEntryId(), ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DLFileEntryPermission.contains(
			permissionChecker, _fileEntry.getFileEntryId(), ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY, _fileEntry);

		String version = ParamUtil.getString(httpServletRequest, "version");

		if ((getAssetRendererType() == AssetRendererFactory.TYPE_LATEST) ||
			Validator.isNotNull(version)) {

			if ((_fileEntry != null) && Validator.isNotNull(version)) {
				_fileVersion = _fileEntry.getFileVersion(version);
			}

			httpServletRequest.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_VERSION, _fileVersion);
		}
		else {
			httpServletRequest.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_VERSION,
				_fileEntry.getFileVersion());
		}

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isCategorizable(long groupId) {
		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			getClassPK());

		if ((dlFileEntry == null) ||
			(dlFileEntry.getRepositoryId() != groupId)) {

			return false;
		}

		return super.isCategorizable(groupId);
	}

	@Override
	public boolean isCommentable() {
		if (super.isCommentable()) {
			return _fileEntry.isRepositoryCapabilityProvided(
				CommentCapability.class);
		}

		return false;
	}

	@Override
	public boolean isConvertible() {
		return true;
	}

	@Override
	public boolean isPrintable() {
		return false;
	}

	public void setAssetDisplayPageFriendlyURLProvider(
		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
	}

	private PortletURL _getPortletURL(
		LiferayPortletRequest liferayPortletRequest) {

		Group group = GroupLocalServiceUtil.fetchGroup(_fileEntry.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortalUtil.getControlPanelPortletURL(
			liferayPortletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
			0, 0, PortletRequest.RENDER_PHASE);
	}

	private boolean _hasViewInContextGroupLayout(
		ThemeDisplay themeDisplay, long groupId) {

		try {
			PortletLayoutFinder portletLayoutFinder =
				PortletLayoutFinderRegistryUtil.getPortletLayoutFinder(
					DLFileEntryConstants.getClassName());

			if (portletLayoutFinder == null) {
				return false;
			}

			PortletLayoutFinder.Result result = portletLayoutFinder.find(
				themeDisplay, groupId);

			if (result == null) {
				return false;
			}

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryAssetRenderer.class);

	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final DLURLHelper _dlURLHelper;
	private final FileEntry _fileEntry;
	private FileVersion _fileVersion;
	private final TrashHelper _trashHelper;

}