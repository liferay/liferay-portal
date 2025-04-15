/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.util;

import com.liferay.document.library.kernel.exception.InvalidFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.util.DL;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelCreateDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelModifiedDateComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelReadCountComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelSizeComparator;
import com.liferay.document.library.kernel.util.comparator.RepositoryModelTitleComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.PortletLayoutFinder;
import com.liferay.portal.kernel.portlet.PortletLayoutFinderRegistryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class DLImpl implements DL {

	@Override
	public int compareVersions(String version1, String version2) {
		int[] splitVersion1 = StringUtil.split(version1, StringPool.PERIOD, 0);
		int[] splitVersion2 = StringUtil.split(version2, StringPool.PERIOD, 0);

		if ((splitVersion1.length != 2) && (splitVersion2.length != 2)) {
			return 0;
		}
		else if (splitVersion1.length != 2) {
			return -1;
		}
		else if (splitVersion2.length != 2) {
			return 1;
		}

		if (splitVersion1[0] > splitVersion2[0]) {
			return 1;
		}
		else if (splitVersion1[0] < splitVersion2[0]) {
			return -1;
		}
		else if (splitVersion1[1] > splitVersion2[1]) {
			return 1;
		}
		else if (splitVersion1[1] < splitVersion2[1]) {
			return -1;
		}

		return 0;
	}

	public void destroy() {
	}

	@Override
	public String getAbsolutePath(
			PortletRequest portletRequest, long rootFolderId, long folderId)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if ((folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) ||
			(rootFolderId == folderId)) {

			return themeDisplay.translate("home");
		}

		Folder folder = DLAppLocalServiceUtil.getFolder(folderId);

		List<Folder> folders = folder.getAncestors();

		if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder rootFolder = DLAppLocalServiceUtil.getFolder(rootFolderId);

			if (!folders.contains(rootFolder)) {
				throw new InvalidFolderException(
					InvalidFolderException.INVALID_ROOT_FOLDER, rootFolderId);
			}

			folders = ListUtil.subList(folders, 0, folders.indexOf(rootFolder));
		}

		Collections.reverse(folders);

		StringBundler sb = new StringBundler((folders.size() * 4) + 5);

		sb.append(themeDisplay.translate("home"));

		for (Folder curFolder : folders) {
			sb.append(StringPool.SPACE);
			sb.append(StringPool.RAQUO_CHAR);
			sb.append(StringPool.SPACE);
			sb.append(curFolder.getName());
		}

		sb.append(StringPool.SPACE);
		sb.append(StringPool.RAQUO_CHAR);
		sb.append(StringPool.SPACE);
		sb.append(folder.getName());

		return sb.toString();
	}

	@Override
	public Set<String> getAllMediaGalleryMimeTypes() {
		return _allMediaGalleryMimeTypes;
	}

	@Override
	public String getDDMStructureKey(DLFileEntryType dlFileEntryType) {
		return getDDMStructureKey(dlFileEntryType.getUuid());
	}

	@Override
	public String getDDMStructureKey(String fileEntryTypeUuid) {
		return _STRUCTURE_KEY_PREFIX +
			StringUtil.toUpperCase(fileEntryTypeUuid);
	}

	@Override
	public String getDeprecatedDDMStructureKey(
		DLFileEntryType dlFileEntryType) {

		return getDeprecatedDDMStructureKey(
			dlFileEntryType.getFileEntryTypeId());
	}

	@Override
	public String getDeprecatedDDMStructureKey(long fileEntryTypeId) {
		return _STRUCTURE_KEY_PREFIX + fileEntryTypeId;
	}

	@Override
	public String getDividedPath(long id) {
		StringBundler sb = new StringBundler(16);

		long dividend = id;

		while ((dividend / _DIVISOR) != 0) {
			sb.append(StringPool.SLASH);
			sb.append(dividend % _DIVISOR);

			dividend = dividend / _DIVISOR;
		}

		sb.append(StringPool.SLASH);
		sb.append(id);

		return sb.toString();
	}

	@Override
	public Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-document")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-document")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-document")
		).put(
			"[$DOCUMENT_STATUS_BY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-updated-the-document")
		).put(
			"[$DOCUMENT_TITLE$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-document-title")
		).put(
			"[$DOCUMENT_TYPE$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-document-type")
		).put(
			"[$DOCUMENT_URL$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-document-url")
		).put(
			"[$DOCUMENT_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-document")
		).put(
			"[$DOCUMENT_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-document")
		).put(
			"[$FOLDER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-folder-in-which-the-document-has-been-added")
		).put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-document")
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient")
		).build();
	}

	@Override
	public Map<String, String> getEmailFromDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-document")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-document")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-document")
		).put(
			"[$DOCUMENT_STATUS_BY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-updated-the-document")
		).put(
			"[$DOCUMENT_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-document")
		).put(
			"[$DOCUMENT_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-document")
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-document")
		).build();
	}

	@Override
	public List<FileEntry> getFileEntries(Hits hits) {
		List<FileEntry> entries = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			long fileEntryId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			try {
				entries.add(DLAppLocalServiceUtil.getFileEntry(fileEntryId));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Documents and Media search index is stale and " +
							"contains file entry " + fileEntryId,
						exception);
				}
			}
		}

		return entries;
	}

	@Override
	public String getFileEntryImage(
		FileEntry fileEntry, ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			"<img src=\"", themeDisplay.getPathThemeImages(),
			"/file_system/small/", fileEntry.getIcon(),
			".png\" style=\"border-width: 0; text-align: left;\">");
	}

	@Override
	public String getFileIcon(String extension) {
		if (!_fileIcons.contains(extension)) {
			extension = _DEFAULT_FILE_ICON;
		}

		return extension;
	}

	@Override
	public String getFileIconCssClass(String extension) {
		return "documents-and-media";
	}

	@Override
	public String getGenericName(String extension) {
		String genericName = _genericNames.get(extension);

		if (genericName == null) {
			genericName = _DEFAULT_GENERIC_NAME;
		}

		return genericName;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             com.liferay.document.library.util.DLURLHelper#getPreviewURL(
	 *             FileEntry, FileVersion, ThemeDisplay, String)}
	 */
	@Deprecated
	@Override
	public String getPreviewURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString) {

		return getPreviewURL(
			fileEntry, fileVersion, themeDisplay, queryString, true, true);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             com.liferay.document.library.util.DLURLHelper#getPreviewURL(
	 *             FileEntry, FileVersion, ThemeDisplay, String, boolean,
	 *             boolean)}
	 */
	@Deprecated
	@Override
	public String getPreviewURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		StringBundler sb = new StringBundler(15);

		if ((themeDisplay != null) && absoluteURL) {
			sb.append(themeDisplay.getPortalURL());
		}

		sb.append(PortalUtil.getPathContext());
		sb.append("/documents/");
		sb.append(fileEntry.getRepositoryId());
		sb.append(StringPool.SLASH);
		sb.append(fileEntry.getFolderId());
		sb.append(StringPool.SLASH);

		String fileName = fileEntry.getFileName();

		if (fileEntry.isInTrash()) {
			TrashHelper trashHelper = _trashHelperSnapshot.get();

			fileName = trashHelper.getOriginalTitle(fileEntry.getFileName());
		}

		sb.append(URLCodec.encodeURL(HtmlUtil.unescape(fileName)));

		sb.append(StringPool.SLASH);
		sb.append(URLCodec.encodeURL(fileEntry.getUuid()));

		if (appendVersion) {
			sb.append("?version=");
			sb.append(fileVersion.getVersion());
			sb.append("&t=");
		}
		else {
			sb.append("?t=");
		}

		Date modifiedDate = fileVersion.getModifiedDate();

		sb.append(modifiedDate.getTime());

		sb.append(queryString);

		String previewURL = sb.toString();

		if (themeDisplay != null) {
			if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
				previewURL = PortalUtil.addPreservedParameters(
					themeDisplay, previewURL, false, true);
			}

			if (themeDisplay.isAddSessionIdToURL()) {
				previewURL = PortalUtil.getURLWithSessionId(
					previewURL, themeDisplay.getSessionId());
			}
		}

		return previewURL;
	}

	@Override
	public <T> OrderByComparator<T> getRepositoryModelOrderByComparator(
		String orderByCol, String orderByType) {

		return getRepositoryModelOrderByComparator(
			orderByCol, orderByType, false);
	}

	@Override
	public <T> OrderByComparator<T> getRepositoryModelOrderByComparator(
		String orderByCol, String orderByType, boolean orderByModel) {

		boolean orderByAsc = true;

		if (orderByType.equals("desc")) {
			orderByAsc = false;
		}

		OrderByComparator<T> orderByComparator = null;

		if (orderByCol.equals("creationDate")) {
			orderByComparator = new RepositoryModelCreateDateComparator<>(
				orderByAsc, orderByModel);
		}
		else if (orderByCol.equals("downloads")) {
			orderByComparator = new RepositoryModelReadCountComparator<>(
				orderByAsc, orderByModel);
		}
		else if (orderByCol.equals("modifiedDate")) {
			orderByComparator = new RepositoryModelModifiedDateComparator<>(
				orderByAsc, orderByModel);
		}
		else if (orderByCol.equals("size")) {
			orderByComparator = new RepositoryModelSizeComparator<>(
				orderByAsc, orderByModel);
		}
		else if (orderByCol.equals("title")) {
			orderByComparator = new RepositoryModelTitleComparator<>(
				orderByAsc, orderByModel);
		}
		else {
			orderByComparator = new RepositoryModelModifiedDateComparator<>(
				orderByAsc, orderByModel);
		}

		return orderByComparator;
	}

	@Override
	public String getSanitizedFileName(String title, String extension) {
		String fileName = StringUtil.replace(
			title, CharPool.SLASH, CharPool.UNDERLINE);

		if (Validator.isNotNull(extension) &&
			!StringUtil.endsWith(fileName, StringPool.PERIOD + extension)) {

			fileName += StringPool.PERIOD + extension;
		}

		if (fileName.length() > 255) {
			int x = fileName.length() - 1;

			if (Validator.isNotNull(extension)) {
				x = fileName.lastIndexOf(StringPool.PERIOD);
			}

			int y = x - (fileName.length() - 255);

			fileName = fileName.substring(0, y) + fileName.substring(x);
		}

		return fileName;
	}

	@Override
	public String getTempFileId(long id, String version) {
		return getTempFileId(id, version, null);
	}

	@Override
	public String getTempFileId(long id, String version, String languageId) {
		if (Validator.isNull(languageId)) {
			return StringBundler.concat(id, StringPool.PERIOD, version);
		}

		return StringBundler.concat(
			id, StringPool.PERIOD, version, StringPool.PERIOD, languageId);
	}

	@Override
	public String getThumbnailStyle() {
		return getThumbnailStyle(true, 0);
	}

	@Override
	public String getThumbnailStyle(boolean max, int margin) {
		return getThumbnailStyle(
			max, margin,
			PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT),
			PrefsPropsUtil.getInteger(
				PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH));
	}

	@Override
	public String getThumbnailStyle(
		boolean max, int margin, int height, int width) {

		StringBundler sb = new StringBundler(5);

		if (max) {
			sb.append("max-height: ");
		}
		else {
			sb.append("height: ");
		}

		height = height + (2 * margin);

		sb.append(height);

		if (max) {
			sb.append("px; max-width: ");
		}
		else {
			sb.append("px; width: ");
		}

		width = width + (2 * margin);

		sb.append(width);

		sb.append("px;");

		return sb.toString();
	}

	@Override
	public String getTitleWithExtension(FileEntry fileEntry) {
		return getTitleWithExtension(
			fileEntry.getTitle(), fileEntry.getExtension());
	}

	@Override
	public String getTitleWithExtension(String title, String extension) {
		if (Validator.isNotNull(extension)) {
			String periodAndExtension = StringPool.PERIOD.concat(extension);

			if (!title.endsWith(periodAndExtension)) {
				title += periodAndExtension;
			}
		}

		return title;
	}

	@Override
	public String getUniqueFileName(
		long groupId, long folderId, String fileName,
		boolean ignoreDuplicateTitle) {

		String uniqueFileTitle = FileUtil.stripExtension(fileName);

		String extension = FileUtil.getExtension(fileName);

		for (int i = 1;; i++) {
			if ((ignoreDuplicateTitle ||
				 !_existsFileEntryByTitle(
					 groupId, folderId, uniqueFileTitle)) &&
				!_existsFileEntryByFileName(
					groupId, extension, folderId, uniqueFileTitle)) {

				break;
			}

			uniqueFileTitle = FileUtil.appendParentheticalSuffix(
				fileName, String.valueOf(i));
		}

		return getTitleWithExtension(uniqueFileTitle, extension);
	}

	@Override
	public String getUniqueTitle(long groupId, long folderId, String title) {
		String uniqueFileTitle = title;

		int i = 1;

		while (_existsFileEntryByTitle(groupId, folderId, uniqueFileTitle)) {
			uniqueFileTitle = StringUtil.appendParentheticalSuffix(
				title, String.valueOf(i));

			i++;
		}

		return uniqueFileTitle;
	}

	@Override
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, long folderId, long fileEntryTypeId) {

		while (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(
				folderId);

			if (dlFolder == null) {
				return false;
			}

			if (dlFolder.getRestrictionType() !=
					DLFolderConstants.RESTRICTION_TYPE_INHERIT) {

				break;
			}

			folderId = dlFolder.getParentFolderId();
		}

		if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				companyId, groupId, DLFolderConstants.getClassName(), folderId,
				fileEntryTypeId) ||
			WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(
				companyId, groupId, DLFolderConstants.getClassName(), folderId,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isAutoGeneratedDLFileEntryTypeDDMStructureKey(
		String ddmStructureKey) {

		if (ddmStructureKey.startsWith(_STRUCTURE_KEY_PREFIX)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isOfficeExtension(String extension) {
		return ArrayUtil.contains(_MICROSOFT_OFFICE_EXTENSIONS, extension);
	}

	@Override
	public boolean isValidVersion(String version) {
		version = _stripVersionSuffix(version);

		if (version.equals(DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {
			return true;
		}

		String[] versionParts = StringUtil.split(version, StringPool.PERIOD);

		if (versionParts.length != 2) {
			return false;
		}

		if (Validator.isNumber(versionParts[0]) &&
			Validator.isNumber(versionParts[1])) {

			return true;
		}

		return false;
	}

	@Override
	public void startWorkflowInstance(
			long userId, DLFileVersion dlFileVersion, String syncEventType,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext =
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL,
				getEntryURL(dlFileVersion, serviceContext)
			).put(
				"event", syncEventType
			).build();

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			dlFileVersion.getCompanyId(), dlFileVersion.getGroupId(), userId,
			DLFileEntryConstants.getClassName(),
			dlFileVersion.getFileVersionId(), dlFileVersion, serviceContext,
			workflowContext);
	}

	protected String getEntryURL(
			DLFileVersion dlFileVersion, ServiceContext serviceContext)
		throws PortalException {

		if (Objects.equals(serviceContext.getCommand(), Constants.ADD_WEBDAV) ||
			Objects.equals(
				serviceContext.getCommand(), Constants.UPDATE_WEBDAV)) {

			return serviceContext.getPortalURL() +
				serviceContext.getCurrentURL();
		}

		String entryURL = GetterUtil.getString(
			serviceContext.getAttribute("entryURL"));

		if (Validator.isNotNull(entryURL)) {
			return entryURL;
		}

		boolean hasAssetDisplayPage = GetterUtil.getBoolean(
			serviceContext.getAttribute("hasAssetDisplayPage"));

		if (hasAssetDisplayPage) {
			return StringPool.BLANK;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if ((httpServletRequest == null) || (themeDisplay == null)) {
			return StringPool.BLANK;
		}

		PortletURL portletURL = null;

		long plid = serviceContext.getPlid();
		long controlPanelPlid = PortalUtil.getControlPanelPlid(
			serviceContext.getCompanyId());
		String portletId = PortletProviderUtil.getPortletId(
			FileEntry.class.getName(), PortletProvider.Action.VIEW);

		DLFileEntry fileEntry = dlFileVersion.getFileEntry();

		PortletLayoutFinder portletLayoutFinder =
			PortletLayoutFinderRegistryUtil.getPortletLayoutFinder(
				DLFileEntryConstants.getClassName());

		PortletLayoutFinder.Result result = portletLayoutFinder.find(
			themeDisplay, fileEntry.getGroupId());

		if (result != null) {
			portletId = result.getPortletId();
			plid = result.getPlid();
		}

		if ((plid == controlPanelPlid) ||
			(plid == LayoutConstants.DEFAULT_PLID)) {

			portletURL = PortalUtil.getControlPanelPortletURL(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletId, plid,
				PortletRequest.RENDER_PHASE);
		}

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/view_file_entry");
		portletURL.setParameter(
			"fileEntryId", String.valueOf(dlFileVersion.getFileEntryId()));

		return portletURL.toString();
	}

	protected String getImageSrc(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString) {

		return getImageSrc(
			fileEntry, fileVersion, themeDisplay, queryString, true, true);
	}

	protected String getImageSrc(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		String thumbnailSrc = StringPool.BLANK;

		if (Validator.isNotNull(queryString)) {
			thumbnailSrc = getPreviewURL(
				fileEntry, fileVersion, themeDisplay, queryString,
				appendVersion, absoluteURL);
		}

		return thumbnailSrc;
	}

	private static void _populateGenericNamesMap(String genericName) {
		String[] extensions = PropsUtil.getArray(
			PropsKeys.DL_FILE_GENERIC_EXTENSIONS, new Filter(genericName));

		for (String extension : extensions) {
			_genericNames.put(extension, genericName);
		}
	}

	private boolean _existsFileEntryByFileName(
		long groupId, String extension, long folderId, String title) {

		try {
			DLAppLocalServiceUtil.getFileEntryByFileName(
				groupId, folderId, getTitleWithExtension(title, extension));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	private boolean _existsFileEntryByTitle(
		long groupId, long folderId, String uniqueFileTitle) {

		try {
			DLAppLocalServiceUtil.getFileEntry(
				groupId, folderId, uniqueFileTitle);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	private String _stripVersionSuffix(String version) {
		int index = version.indexOf(CharPool.TILDE);

		if (index != -1) {
			return version.substring(0, index);
		}

		if (version.endsWith(".index")) {
			return StringUtil.removeLast(version, ".index");
		}

		return version;
	}

	private static final String _DEFAULT_FILE_ICON = "page";

	private static final String _DEFAULT_GENERIC_NAME = "default";

	private static final long _DIVISOR = 256;

	private static final String[] _MICROSOFT_OFFICE_EXTENSIONS = {
		"accda", "accdb", "accdc", "accde", "accdp", "accdr", "accdt", "accdu",
		"acl", "ade", "adp", "asd", "cnv", "crtx", "doc", "docm", "docx", "dot",
		"dotm", "dotx", "grv", "iaf", "laccdb", "maf", "mam", "maq", "mar",
		"mat", "mda", "mdb", "mde", "mdt", "mdw", "mpd", "mpp", "mpt", "oab",
		"obi", "oft", "olm", "one", "onepkg", "ops", "ost", "pa", "pip", "pot",
		"potm", "potx", "ppa", "ppam", "pps", "ppsm", "ppsx", "ppt", "pptm",
		"pptx", "prf", "pst", "pub", "puz", "rpmsg", "sldm", "sldx", "slk",
		"snp", "svd", "thmx", "vdx", "vrge08message", "vsd", "vss", "vst",
		"vsx", "vtx", "wbk", "wll", "xar", "xl", "xla", "xlam", "xlb", "xlc",
		"xll", "xlm", "xls", "xlsb", "xlsm", "xlsx", "xlt", "xltm", "xltx",
		"xlw", "xsf", "xsn"
	};

	private static final String _STRUCTURE_KEY_PREFIX = "AUTO_";

	private static final Log _log = LogFactoryUtil.getLog(DLImpl.class);

	private static final Set<String> _allMediaGalleryMimeTypes =
		new TreeSet<String>() {
			{
				add(
					ContentTypes.
						APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML);
				addAll(
					SetUtil.fromArray(
						PropsUtil.getArray(
							PropsKeys.DL_FILE_ENTRY_PREVIEW_AUDIO_MIME_TYPES)));
				addAll(
					SetUtil.fromArray(
						PropsUtil.getArray(
							PropsKeys.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES)));
				addAll(
					SetUtil.fromArray(
						PropsUtil.getArray(
							PropsKeys.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES)));
				add(ContentTypes.IMAGE_SVG_XML);
			}
		};

	private static final Set<String> _fileIcons = new HashSet<String>() {
		{
			String[] fileIcons = null;

			try {
				fileIcons = PropsUtil.getArray(PropsKeys.DL_FILE_ICONS);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				fileIcons = new String[] {StringPool.BLANK};
			}

			for (int i = 0; i < fileIcons.length; i++) {

				// Only process non wildcard extensions

				if (!StringPool.STAR.equals(fileIcons[i])) {

					// Strip starting period

					String extension = fileIcons[i];

					if (extension.length() > 0) {
						extension = extension.substring(1);
					}

					add(extension);
				}
			}
		}
	};

	private static final Map<String, String> _genericNames = new HashMap<>();
	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(DLImpl.class, TrashHelper.class);

	static {
		String[] genericNames = PropsUtil.getArray(
			PropsKeys.DL_FILE_GENERIC_NAMES);

		for (String genericName : genericNames) {
			_populateGenericNamesMap(genericName);
		}
	}

}