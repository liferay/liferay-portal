/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public interface ServletFileUpload {

	public List<FileItem> parseRequest(
			HttpServletRequest httpServletRequest, String location,
			int fileSizeThreshold)
		throws UploadException;

}