/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.servlet;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.cms.site.initializer.internal.servlet.DownloadObjectEntryFolderCMSServlet",
		"osgi.http.whiteboard.servlet.pattern=/cms/download-folder/*",
		"servlet.init.httpMethods=GET,POST"
	},
	service = Servlet.class
)
public class DownloadObjectEntryFolderCMSServlet extends BaseCMSServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_downloadObjectEntryFolder(httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_downloadBulkAction(httpServletRequest, httpServletResponse);
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	private void _downloadBulkAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		JSONObject jsonObject = null;

		try {
			jsonObject = _jsonFactory.createJSONObject(
				StreamUtil.toString(
					httpServletRequest.getInputStream(), StringPool.UTF8));
		}
		catch (JSONException jsonException) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn(jsonException);
			}

			return;
		}

		if (!StringUtil.equalsIgnoreCase(
				"DownloadBulkAction", jsonObject.getString("type"))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn("Type is not \"DownloadBulkAction\"");
			}

			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		JSONObject selectionScopeJSONObject = jsonObject.getJSONObject(
			"selectionScope");

		if ((selectionScopeJSONObject != null) &&
			selectionScopeJSONObject.getBoolean("selectAll")) {

			_selectAllDownload(httpServletRequest, themeDisplay, zipWriter);
		}
		else {
			JSONArray jsonArray = jsonObject.getJSONArray("bulkActionItems");

			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);

				_zipEntry(
					jsonObject.getString("className"),
					jsonObject.getLong("classPK"), themeDisplay, zipWriter);
			}
		}

		try (InputStream inputStream = new FileInputStream(
				zipWriter.getFile())) {

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse,
				"download-" + Time.getTimestamp() + ".zip", inputStream, 0,
				ContentTypes.APPLICATION_ZIP,
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		finally {
			File file = zipWriter.getFile();

			if ((file != null) && file.exists()) {
				file.delete();
			}
		}
	}

	private void _downloadObjectEntryFolder(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException {

		String[] pathArray = StringUtil.split(
			_getObjectEntryFolderPath(httpServletRequest), StringPool.SLASH);

		long classNameId = portal.getClassNameId(ObjectEntryFolder.class);

		if (classNameId != GetterUtil.getLong(pathArray[0])) {
			throw new RuntimeException();
		}

		long objectEntryFolderId = GetterUtil.getLong(pathArray[1]);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try {
			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderService.getObjectEntryFolder(
					objectEntryFolderId);

			String zipFileName = objectEntryFolder.getName() + ".zip";

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_zipObjectEntryFolder(
				objectEntryFolder.getGroupId(), objectEntryFolderId,
				objectEntryFolder.getName(), themeDisplay, zipWriter);

			try (InputStream inputStream = new FileInputStream(
					zipWriter.getFile())) {

				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, zipFileName,
					inputStream, 0, ContentTypes.APPLICATION_ZIP,
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
			}
		}
		finally {
			File file = zipWriter.getFile();

			if ((file != null) && file.exists()) {
				file.delete();
			}
		}
	}

	private String _getObjectEntryFolderPath(
		HttpServletRequest httpServletRequest) {

		String requestURI = httpServletRequest.getRequestURI();

		String path =
			httpServletRequest.getContextPath() +
				httpServletRequest.getServletPath();

		return requestURI.substring(path.length() + 1);
	}

	private void _selectAllDownload(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			ZipWriter zipWriter)
		throws IOException, PortalException {

		SearchHits searchHits = getSelectAllSearchHits(httpServletRequest);

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			_zipEntry(
				document.getString("entryClassName"),
				document.getLong("entryClassPK"), themeDisplay, zipWriter);
		}
	}

	private void _zipEntry(
			String className, long classPK, ThemeDisplay themeDisplay,
			ZipWriter zipWriter)
		throws IOException, PortalException {

		if (StringUtil.equalsIgnoreCase(
				className, ObjectEntryFolder.class.getName())) {

			ObjectEntryFolder objectEntryFolder = null;

			try {
				objectEntryFolder =
					_objectEntryFolderService.getObjectEntryFolder(classPK);
			}
			catch (PrincipalException principalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(principalException);
				}

				return;
			}

			_zipObjectEntryFolder(
				objectEntryFolder.getGroupId(), classPK,
				objectEntryFolder.getName(), themeDisplay, zipWriter);
		}
		else {
			_zipObjectEntry(
				_objectEntryLocalService.getObjectEntry(classPK),
				StringPool.SLASH, themeDisplay.getPermissionChecker(),
				zipWriter);
		}
	}

	private void _zipObjectEntry(
			ObjectEntry objectEntry, String path,
			PermissionChecker permissionChecker, ZipWriter zipWriter)
		throws IOException, PortalException {

		ObjectField attachmentObjectField = null;
		long fileEntryId = 0;
		Map<String, Serializable> values = objectEntry.getValues();

		List<ObjectField> attachmentObjectFields =
			_objectFieldLocalService.getObjectFieldsByBusinessType(
				objectEntry.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		for (ObjectField objectField : attachmentObjectFields) {
			long candidateFileEntryId = GetterUtil.getLong(
				values.get(objectField.getName()));

			if (candidateFileEntryId != 0) {
				attachmentObjectField = objectField;
				fileEntryId = candidateFileEntryId;

				break;
			}
		}

		if (fileEntryId == 0) {
			return;
		}

		ModelResourcePermission<ObjectEntry>
			objectEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					objectEntry.getModelClassName());

		if (objectEntryModelResourcePermission == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No model resource permission registered for " +
						objectEntry.getModelClassName());
			}

			return;
		}

		if (!objectEntryModelResourcePermission.contains(
				permissionChecker, objectEntry, ActionKeys.VIEW) ||
			!objectEntryModelResourcePermission.contains(
				permissionChecker, objectEntry,
				attachmentObjectField.getAttachmentDownloadActionKey())) {

			return;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		if (fileEntry.containsPermission(
				permissionChecker, ActionKeys.DOWNLOAD)) {

			zipWriter.addEntry(
				path + StringPool.SLASH + fileEntry.getFileName(),
				fileEntry.getContentStream());
		}
	}

	private void _zipObjectEntryFolder(
			long groupId, long objectEntryFolderId, String path,
			ThemeDisplay themeDisplay, ZipWriter zipWriter)
		throws IOException, PortalException {

		List<ObjectEntry> objectEntryFolderObjectEntries =
			_objectEntryLocalService.getObjectEntryFolderObjectEntries(
				groupId, objectEntryFolderId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (ObjectEntry objectEntry : objectEntryFolderObjectEntries) {
			_zipObjectEntry(
				objectEntry, path, themeDisplay.getPermissionChecker(),
				zipWriter);
		}

		List<ObjectEntryFolder> objectEntryFolders =
			_objectEntryFolderLocalService.getObjectEntryFolders(
				groupId, themeDisplay.getCompanyId(), objectEntryFolderId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ObjectEntryFolder objectEntryFolder : objectEntryFolders) {
			if (_objectEntryFolderModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), objectEntryFolder,
					ActionKeys.VIEW)) {

				_zipObjectEntryFolder(
					groupId, objectEntryFolder.getObjectEntryFolderId(),
					StringBundler.concat(
						path, StringPool.SLASH, objectEntryFolder.getName()),
					themeDisplay, zipWriter);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DownloadObjectEntryFolderCMSServlet.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}