/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFileEntryPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.capabilities.CommentCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.text.Format;

import java.util.List;
import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class DLViewFileEntryDisplayContext {

	public DLViewFileEntryDisplayContext(
		DLAdminDisplayContext dlAdminDisplayContext,
		DLDisplayContextProvider dlDisplayContextProvider,
		HttpServletRequest httpServletRequest, Language language, Portal portal,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_dlAdminDisplayContext = dlAdminDisplayContext;
		_dlDisplayContextProvider = dlDisplayContextProvider;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletResponse = portal.getHttpServletResponse(renderResponse);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_dlRequestHelper = new DLRequestHelper(_httpServletRequest);

		_dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
			_dlRequestHelper);
	}

	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.getActionDropdownItems();
	}

	public String getDiscussionClassName() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.getDiscussionClassName();
	}

	public long getDiscussionClassPK() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.getDiscussionClassPK();
	}

	public long getDiscussionUserId() throws PortalException {
		FileEntry fileEntry = getFileEntry();

		return _portal.getValidUserId(
			fileEntry.getCompanyId(), fileEntry.getUserId());
	}

	public String getDocumentTitle() throws PortalException {
		if (_documentTitle != null) {
			return _documentTitle;
		}

		FileVersion fileVersion = getFileVersion();

		if (!isVersionSpecific()) {
			_documentTitle = fileVersion.getTitle();
		}
		else {
			_documentTitle = StringBundler.concat(
				fileVersion.getTitle(), StringPool.SPACE,
				StringPool.OPEN_PARENTHESIS,
				_language.get(_httpServletRequest, "version"), StringPool.SPACE,
				fileVersion.getVersion(), StringPool.CLOSE_PARENTHESIS);
		}

		return _documentTitle;
	}

	public FileEntry getFileEntry() {
		if (_fileEntry != null) {
			return _fileEntry;
		}

		_fileEntry = (FileEntry)_renderRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

		return _fileEntry;
	}

	public FileVersion getFileVersion() throws PortalException {
		if (_fileVersion != null) {
			return _fileVersion;
		}

		_fileVersion = (FileVersion)_renderRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);

		if (_fileVersion != null) {
			return _fileVersion;
		}

		FileEntry fileEntry = getFileEntry();

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		User user = permissionChecker.getUser();

		if ((_themeDisplay.getUserId() == fileEntry.getUserId()) ||
			permissionChecker.isContentReviewer(
				user.getCompanyId(), _themeDisplay.getScopeGroupId()) ||
			DLFileEntryPermission.contains(
				permissionChecker, fileEntry, ActionKeys.UPDATE)) {

			_fileVersion = fileEntry.getLatestFileVersion();
		}
		else {
			_fileVersion = fileEntry.getFileVersion();
		}

		return _fileVersion;
	}

	public String getLockInfoDisplayType() {
		FileEntry fileEntry = getFileEntry();

		if (!fileEntry.hasLock()) {
			return "danger";
		}

		return "info";
	}

	public String getLockInfoMessage(Locale locale) {
		FileEntry fileEntry = getFileEntry();

		if (!fileEntry.hasLock()) {
			Lock lock = _getLock();

			Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
				locale, _themeDisplay.getTimeZone());

			return _language.format(
				_httpServletRequest,
				"you-cannot-modify-this-document-because-it-was-locked-by-x-" +
					"on-x",
				new Object[] {
					HtmlUtil.escape(
						_portal.getUserName(
							lock.getUserId(),
							String.valueOf(lock.getUserId()))),
					dateTimeFormat.format(lock.getCreateDate())
				},
				false);
		}

		Lock lock = _getLock();

		if (lock.isNeverExpires()) {
			return _language.get(
				_httpServletRequest,
				"you-now-have-an-indefinite-lock-on-this-document");
		}

		return _language.format(
			_httpServletRequest, "you-now-have-a-lock-on-this-document",
			_language.getTimeDescription(
				_httpServletRequest,
				DLFileEntryConstants.LOCK_EXPIRATION_TIME));
	}

	public String getRedirect() throws PortalException {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = _portal.escapeRedirect(
			ParamUtil.getString(_renderRequest, "redirect"));

		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			long parentFolderId = _getParentFolderId();

			String mvcRenderCommandName = "/document_library/view";

			if (parentFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				mvcRenderCommandName = "/document_library/view_folder";
			}

			_redirect = PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				mvcRenderCommandName
			).setParameter(
				"folderId", parentFolderId
			).buildString();
		}

		return _redirect;
	}

	public boolean isDownloadLinkVisible() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.isDownloadLinkVisible();
	}

	public boolean isEnableDiscussionRatings() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		return dlPortletInstanceSettings.isEnableCommentRatings();
	}

	public boolean isShared() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.isShared();
	}

	public boolean isSharingLinkVisible() throws PortalException {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		return dlViewFileVersionDisplayContext.isSharingLinkVisible();
	}

	public boolean isShowComments() {
		boolean showComments = ParamUtil.getBoolean(
			_httpServletRequest, "showComments", true);

		FileEntry fileEntry = getFileEntry();

		if (showComments &&
			fileEntry.isRepositoryCapabilityProvided(CommentCapability.class)) {

			return true;
		}

		return false;
	}

	public boolean isShowHeader() {
		boolean showHeader = ParamUtil.getBoolean(
			_httpServletRequest, "showHeader", true);

		FileEntry fileEntry = getFileEntry();

		if (showHeader && (fileEntry.getFolder() != null)) {
			return true;
		}

		return false;
	}

	public boolean isShowLockInfo() throws PortalException {
		if (_getLock() == null) {
			return false;
		}

		return DLFileEntryPermission.contains(
			_themeDisplay.getPermissionChecker(), getFileEntry(),
			ActionKeys.UPDATE);
	}

	public boolean isShowVersionDetails() {
		if (_dlPortletInstanceSettingsHelper.isShowActions() &&
			_dlAdminDisplayContext.isVersioningStrategyOverridable()) {

			return true;
		}

		return false;
	}

	public boolean isVersionSpecific() {
		if (_versionSpecific != null) {
			return _versionSpecific;
		}

		FileVersion fileVersion = (FileVersion)_renderRequest.getAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);

		if (fileVersion == null) {
			_versionSpecific = false;
		}
		else {
			_versionSpecific = true;
		}

		return _versionSpecific;
	}

	public void renderPreview(PageContext pageContext) throws Exception {
		DLViewFileVersionDisplayContext dlViewFileVersionDisplayContext =
			_getDLViewFileVersionDisplayContext();

		PortalIncludeUtil.include(
			pageContext, dlViewFileVersionDisplayContext::renderPreview);
	}

	private DLViewFileVersionDisplayContext
			_getDLViewFileVersionDisplayContext()
		throws PortalException {

		if (_dlViewFileVersionDisplayContext != null) {
			return _dlViewFileVersionDisplayContext;
		}

		_dlViewFileVersionDisplayContext =
			_dlDisplayContextProvider.getDLViewFileVersionDisplayContext(
				_httpServletRequest, _httpServletResponse, getFileVersion());

		return _dlViewFileVersionDisplayContext;
	}

	private Lock _getLock() {
		if (_lock != null) {
			return _lock;
		}

		FileEntry fileEntry = getFileEntry();

		_lock = fileEntry.getLock();

		return _lock;
	}

	private long _getParentFolderId() throws PortalException {
		FileEntry fileEntry = getFileEntry();

		if (DLFolderPermission.contains(
				_themeDisplay.getPermissionChecker(), fileEntry.getGroupId(),
				fileEntry.getFolderId(), ActionKeys.VIEW)) {

			return fileEntry.getFolderId();
		}

		return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	private final DLAdminDisplayContext _dlAdminDisplayContext;
	private final DLDisplayContextProvider _dlDisplayContextProvider;
	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private final DLRequestHelper _dlRequestHelper;
	private DLViewFileVersionDisplayContext _dlViewFileVersionDisplayContext;
	private String _documentTitle;
	private FileEntry _fileEntry;
	private FileVersion _fileVersion;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final Language _language;
	private Lock _lock;
	private final Portal _portal;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private Boolean _versionSpecific;

}