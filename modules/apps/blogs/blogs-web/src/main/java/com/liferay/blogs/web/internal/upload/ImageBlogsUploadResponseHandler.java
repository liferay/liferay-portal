/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.upload;

import com.liferay.blogs.configuration.BlogsFileUploadsConfiguration;
import com.liferay.blogs.exception.EntryImageNameException;
import com.liferay.blogs.exception.EntryImageSizeException;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;

/**
 * @author Roberto Díaz
 * @author Alejandro Tardín
 */
public class ImageBlogsUploadResponseHandler implements UploadResponseHandler {

	public ImageBlogsUploadResponseHandler(
		BlogsFileUploadsConfiguration blogsFileUploadsConfiguration,
		ItemSelectorUploadResponseHandler itemSelectorUploadResponseHandler) {

		_blogsFileUploadsConfiguration = blogsFileUploadsConfiguration;
		_itemSelectorUploadResponseHandler = itemSelectorUploadResponseHandler;
	}

	@Override
	public JSONObject onFailure(
			PortletRequest portletRequest, PortalException portalException)
		throws PortalException {

		JSONObject jsonObject = _itemSelectorUploadResponseHandler.onFailure(
			portletRequest, portalException);

		if (portalException instanceof EntryImageNameException ||
			portalException instanceof EntryImageSizeException) {

			int errorType = 0;
			String message = StringPool.BLANK;

			if (portalException instanceof EntryImageNameException) {
				errorType =
					ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
				message = StringUtil.merge(
					_blogsFileUploadsConfiguration.imageExtensions());
			}
			else if (portalException instanceof EntryImageSizeException) {
				errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
			}

			jsonObject.put(
				"error",
				JSONUtil.put(
					"errorType", errorType
				).put(
					"message", message
				));
		}

		return jsonObject;
	}

	@Override
	public JSONObject onSuccess(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
		throws PortalException {

		return _itemSelectorUploadResponseHandler.onSuccess(
			uploadPortletRequest, fileEntry);
	}

	private final BlogsFileUploadsConfiguration _blogsFileUploadsConfiguration;
	private final ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

}