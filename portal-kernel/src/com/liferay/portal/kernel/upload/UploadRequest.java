/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upload;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 */
public interface UploadRequest extends HttpServletRequest {

	public void cleanUp();

	public String getContentType(String name);

	public File getFile(String name);

	public File getFile(String name, boolean forceCreate);

	public InputStream getFileAsStream(String name) throws IOException;

	public InputStream getFileAsStream(String name, boolean deleteOnClose)
		throws IOException;

	public String getFileName(String name);

	public String[] getFileNames(String name);

	public File[] getFiles(String name);

	public InputStream[] getFilesAsStream(String name) throws IOException;

	public InputStream[] getFilesAsStream(String name, boolean deleteOnClose)
		throws IOException;

	public String getFullFileName(String name);

	public Map<String, FileItem[]> getMultipartParameterMap();

	public Map<String, List<String>> getRegularParameterMap();

	public Long getSize(String name);

	public Boolean isFormField(String name);

}