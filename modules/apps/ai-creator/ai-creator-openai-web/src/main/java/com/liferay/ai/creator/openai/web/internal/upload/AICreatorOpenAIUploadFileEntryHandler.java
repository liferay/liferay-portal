/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.upload;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.upload.UploadFileEntryHandler;

import jakarta.portlet.PortletRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class AICreatorOpenAIUploadFileEntryHandler
	implements UploadFileEntryHandler {

	public AICreatorOpenAIUploadFileEntryHandler(DLAppService dlAppService) {
		_dlAppService = dlAppService;
	}

	@Override
	public FileEntry upload(UploadPortletRequest uploadPortletRequest)
		throws IOException, PortalException {

		PortletRequest portletRequest =
			uploadPortletRequest.getPortletRequest();

		String urlPath = ParamUtil.getString(portletRequest, "urlPath");

		if (Validator.isNull(urlPath)) {
			return null;
		}

		URL url = new URL(urlPath);

		File file = null;

		try (InputStream inputStream = new BufferedInputStream(
				url.openStream())) {

			long repositoryId = GetterUtil.getLong(
				ParamUtil.getLong(portletRequest, "repositoryId"));

			long folderId = GetterUtil.getLong(
				ParamUtil.getLong(portletRequest, "folderId"),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			Date now = new Date();

			String mimeType = URLConnection.guessContentTypeFromStream(
				inputStream);

			Set<String> extensions = MimeTypesUtil.getExtensions(mimeType);

			String extension = StringPool.BLANK;

			if (!extensions.isEmpty()) {
				Iterator<String> iterator = extensions.iterator();

				extension = iterator.next();
			}

			String title = StringBundler.concat(
				"AI-image-", now.getTime(), extension);

			file = FileUtil.createTempFile(inputStream);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				FileEntry.class.getName(), portletRequest);

			serviceContext.setAttribute(
				"fileEntryTypeId",
				GetterUtil.getLong(
					ParamUtil.getLong(portletRequest, "fileEntryTypeId")));

			return _dlAppService.addFileEntry(
				null, repositoryId, folderId, title, mimeType, title, title,
				null, null, file, null, null, null, serviceContext);
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	private final DLAppService _dlAppService;

}