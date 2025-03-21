/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ClientDataRequestHelper;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;

import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
@Component(service = ClientDataRequestHelper.class)
public class ClientDataRequestHelperImpl implements ClientDataRequestHelper {

	@Override
	public Part getPart(String name, Object request, Portlet portlet)
		throws IOException, PortletException {

		UploadRequest uploadRequest = _getUploadRequest(request);

		if (uploadRequest == null) {
			return null;
		}

		Map<String, FileItem[]> multipartParameterMap =
			uploadRequest.getMultipartParameterMap();

		FileItem[] fileItems = multipartParameterMap.get(name);

		if (ArrayUtil.isEmpty(fileItems)) {
			return null;
		}

		return new PartImpl(fileItems[0], portlet.getMultipartLocation());
	}

	@Override
	public Collection<Part> getParts(Object request, Portlet portlet)
		throws IOException, PortletException {

		UploadRequest uploadRequest = _getUploadRequest(request);

		if (uploadRequest == null) {
			return Collections.emptySet();
		}

		List<Part> parts = new ArrayList<>();

		Map<String, FileItem[]> multipartParameterMap =
			uploadRequest.getMultipartParameterMap();

		for (Map.Entry<String, FileItem[]> entry :
				multipartParameterMap.entrySet()) {

			FileItem[] fileItems = entry.getValue();

			for (FileItem fileItem : fileItems) {
				parts.add(
					new PartImpl(fileItem, portlet.getMultipartLocation()));
			}
		}

		return parts;
	}

	private UploadRequest _getUploadRequest(Object request) {
		while (true) {
			if (request instanceof UploadRequest) {
				return (UploadRequest)request;
			}

			if (request instanceof ServletRequestWrapper) {
				ServletRequestWrapper servletRequestWrapper =
					(ServletRequestWrapper)request;

				request = servletRequestWrapper.getRequest();
			}
			else {
				return null;
			}
		}
	}

	private static final class PartImpl implements Part {

		@Override
		public void delete() throws IOException {
			_fileItem.delete();
		}

		@Override
		public String getContentType() {
			return _fileItem.getContentType();
		}

		@Override
		public String getHeader(String name) {
			return _fileItem.getHeader(name);
		}

		@Override
		public Collection<String> getHeaderNames() {
			return _fileItem.getHeaderNames();
		}

		@Override
		public Collection<String> getHeaders(String name) {
			return _fileItem.getHeaders(name);
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return _fileItem.getInputStream();
		}

		@Override
		public String getName() {
			return _fileItem.getFieldName();
		}

		@Override
		public long getSize() {
			return _fileItem.getSize();
		}

		@Override
		public String getSubmittedFileName() {
			return _fileItem.getFileName();
		}

		@Override
		public void write(String fileName) throws IOException {
			if (Validator.isNull(fileName)) {
				throw new IOException("Invalid file name");
			}

			try {
				File file = new File(fileName);

				if (!file.isAbsolute() &&
					Validator.isNotNull(_multipartLocation)) {

					File multipartLocation = new File(_multipartLocation);

					if (multipartLocation.isDirectory()) {
						file = new File(multipartLocation, fileName);
					}
				}

				_fileItem.write(file);
			}
			catch (Exception exception) {
				throw new IOException(exception);
			}
		}

		private PartImpl(FileItem fileItem, String multipartLocation) {
			_fileItem = fileItem;
			_multipartLocation = multipartLocation;
		}

		private final FileItem _fileItem;
		private final String _multipartLocation;

	}

}