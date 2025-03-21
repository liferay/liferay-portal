/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 */
public class UploadPortletRequestImpl
	extends HttpServletRequestWrapper implements UploadPortletRequest {

	public UploadPortletRequestImpl(
		UploadServletRequest uploadServletRequest,
		PortletRequest portletRequest, String namespace) {

		super(uploadServletRequest);

		_uploadServletRequest = uploadServletRequest;
		_portletRequest = portletRequest;
		_namespace = namespace;
	}

	@Override
	public void cleanUp() {
		_uploadServletRequest.cleanUp();
	}

	@Override
	public String getContentType(String name) {
		String contentType = _uploadServletRequest.getContentType(
			_namespace.concat(name));

		if (contentType == null) {
			contentType = _uploadServletRequest.getContentType(name);
		}

		if (Validator.isNull(contentType) ||
			contentType.equals(ContentTypes.APPLICATION_OCTET_STREAM)) {

			File file = getFile(name);

			if (file != null) {
				contentType = MimeTypesUtil.getContentType(file);
			}
		}

		return contentType;
	}

	@Override
	public File getFile(String name) {
		return getFile(name, false);
	}

	@Override
	public File getFile(String name, boolean forceCreate) {
		File file = _uploadServletRequest.getFile(
			_namespace.concat(name), forceCreate);

		if (file == null) {
			file = _uploadServletRequest.getFile(name, forceCreate);
		}

		return file;
	}

	@Override
	public InputStream getFileAsStream(String name) throws IOException {
		return getFileAsStream(name, true);
	}

	@Override
	public InputStream getFileAsStream(String name, boolean deleteOnClose)
		throws IOException {

		InputStream inputStream = _uploadServletRequest.getFileAsStream(
			_namespace.concat(name), deleteOnClose);

		if (inputStream == null) {
			inputStream = _uploadServletRequest.getFileAsStream(
				name, deleteOnClose);
		}

		return inputStream;
	}

	@Override
	public String getFileName(String name) {
		String fileName = _uploadServletRequest.getFileName(
			_namespace.concat(name));

		if (fileName == null) {
			fileName = _uploadServletRequest.getFileName(name);
		}

		return fileName;
	}

	@Override
	public String[] getFileNames(String name) {
		String[] fileNames = _uploadServletRequest.getFileNames(
			_namespace.concat(name));

		if (fileNames == null) {
			fileNames = _uploadServletRequest.getFileNames(name);
		}

		return fileNames;
	}

	@Override
	public File[] getFiles(String name) {
		File[] files = _uploadServletRequest.getFiles(_namespace.concat(name));

		if (files == null) {
			files = _uploadServletRequest.getFiles(name);
		}

		return files;
	}

	@Override
	public InputStream[] getFilesAsStream(String name) throws IOException {
		return getFilesAsStream(name, true);
	}

	@Override
	public InputStream[] getFilesAsStream(String name, boolean deleteOnClose)
		throws IOException {

		InputStream[] inputStreams = _uploadServletRequest.getFilesAsStream(
			_namespace.concat(name), deleteOnClose);

		if (inputStreams == null) {
			inputStreams = _uploadServletRequest.getFilesAsStream(
				name, deleteOnClose);
		}

		return inputStreams;
	}

	@Override
	public String getFullFileName(String name) {
		String fullFileName = _uploadServletRequest.getFullFileName(
			_namespace.concat(name));

		if (fullFileName == null) {
			fullFileName = _uploadServletRequest.getFullFileName(name);
		}

		return fullFileName;
	}

	@Override
	public Map<String, FileItem[]> getMultipartParameterMap() {
		if (!(_uploadServletRequest instanceof UploadServletRequestImpl)) {
			return Collections.emptyMap();
		}

		Map<String, FileItem[]> map = new HashMap<>();

		UploadServletRequestImpl uploadServletRequestImpl =
			(UploadServletRequestImpl)_uploadServletRequest;

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequestImpl.getMultipartParameterMap();

		for (Map.Entry<String, FileItem[]> entry :
				multipartParameterMap.entrySet()) {

			String name = entry.getKey();
			FileItem[] fileItems = entry.getValue();

			if (name.startsWith(_namespace)) {
				map.put(name.substring(_namespace.length()), fileItems);
			}
			else {
				map.put(name, fileItems);
			}
		}

		return map;
	}

	@Override
	public String getParameter(String name) {
		String parameter = _uploadServletRequest.getParameter(
			_namespace.concat(name));

		if (parameter == null) {
			parameter = _uploadServletRequest.getParameter(name);
		}

		return parameter;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<>();

		Enumeration<String> enumeration = getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			map.put(name, getParameterValues(name));
		}

		return map;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		List<String> parameterNames = new ArrayList<>();

		Enumeration<String> enumeration =
			_uploadServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (name.startsWith(_namespace)) {
				parameterNames.add(name.substring(_namespace.length()));
			}
			else {
				parameterNames.add(name);
			}
		}

		return Collections.enumeration(parameterNames);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] parameterValues = _uploadServletRequest.getParameterValues(
			_namespace.concat(name));

		if (parameterValues == null) {
			parameterValues = _uploadServletRequest.getParameterValues(name);
		}

		return parameterValues;
	}

	@Override
	public PortletRequest getPortletRequest() {
		return _portletRequest;
	}

	@Override
	public Map<String, List<String>> getRegularParameterMap() {
		if (!(_uploadServletRequest instanceof UploadServletRequestImpl)) {
			return Collections.emptyMap();
		}

		Map<String, List<String>> map = new HashMap<>();

		UploadServletRequestImpl uploadServletRequestImpl =
			(UploadServletRequestImpl)_uploadServletRequest;

		Map<String, List<String>> regularParameterMap =
			uploadServletRequestImpl.getRegularParameterMap();

		for (Map.Entry<String, List<String>> entry :
				regularParameterMap.entrySet()) {

			String name = entry.getKey();
			List<String> parameters = entry.getValue();

			if (name.startsWith(_namespace)) {
				map.put(name.substring(_namespace.length()), parameters);
			}
			else {
				map.put(name, parameters);
			}
		}

		return map;
	}

	@Override
	public Long getSize(String name) {
		Long size = _uploadServletRequest.getSize(_namespace.concat(name));

		if (size == null) {
			size = _uploadServletRequest.getSize(name);
		}

		if (size == null) {
			return Long.valueOf(0);
		}

		return size;
	}

	@Override
	public Boolean isFormField(String name) {
		Boolean formField = _uploadServletRequest.isFormField(
			_namespace.concat(name));

		if (formField == null) {
			formField = _uploadServletRequest.isFormField(name);
		}

		if (formField == null) {
			return true;
		}

		return formField.booleanValue();
	}

	private final String _namespace;
	private final PortletRequest _portletRequest;
	private final UploadServletRequest _uploadServletRequest;

}