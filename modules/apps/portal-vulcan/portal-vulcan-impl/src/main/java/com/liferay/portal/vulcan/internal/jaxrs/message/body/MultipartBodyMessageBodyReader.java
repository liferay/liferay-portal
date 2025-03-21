/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.message.body;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.internal.multipart.MultipartUtil;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import java.io.InputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.util.Streams;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;

/**
 * @author Alejandro Hernández
 * @author Javier Gamarra
 */
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Provider
public class MultipartBodyMessageBodyReader
	implements MessageBodyReader<MultipartBody> {

	@Override
	public boolean isReadable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return true;
	}

	@Override
	public MultipartBody readFrom(
		Class<MultipartBody> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType, MultivaluedMap<String, String> multivaluedMap,
		InputStream inputStream) {

		Message message = JAXRSUtils.getCurrentMessage();

		UploadServletRequest uploadServletRequest = _getUploadServletRequest(
			(HttpServletRequest)message.getContextualProperty("HTTP.REQUEST"));

		if (uploadServletRequest == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Upload servlet request is null");
			}

			return null;
		}

		UploadException uploadException =
			(UploadException)uploadServletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			throw new BadRequestException(
				"Please enter a file with a valid file size no larger than " +
					DLValidatorUtil.getMaxAllowableSize(0, null),
				uploadException);
		}

		Map<String, BinaryFile> binaryFiles = new HashMap<>();
		Map<String, String> values = new HashMap<>();

		try {
			Collection<Part> parts = uploadServletRequest.getParts();

			if ((parts != null) && !parts.isEmpty()) {
				for (Part part : parts) {
					String fileName = MultipartUtil.getFileName(part);

					if (fileName == null) {
						values.put(
							part.getName(),
							Streams.asString(part.getInputStream()));
					}
					else {
						binaryFiles.put(
							part.getName(),
							new BinaryFile(
								part.getContentType(), fileName,
								part.getInputStream(), part.getSize()));
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (binaryFiles.isEmpty() && values.isEmpty()) {
			try {
				Map<String, FileItem[]> multipartParameterMap =
					uploadServletRequest.getMultipartParameterMap();

				for (Map.Entry<String, FileItem[]> entry :
						multipartParameterMap.entrySet()) {

					FileItem fileItem = entry.getValue()[0];

					binaryFiles.put(
						entry.getKey(),
						new BinaryFile(
							fileItem.getContentType(), fileItem.getFileName(),
							fileItem.getInputStream(), fileItem.getSize()));
				}

				Map<String, List<String>> regularParameterMap =
					uploadServletRequest.getRegularParameterMap();

				for (Map.Entry<String, List<String>> entry :
						regularParameterMap.entrySet()) {

					List<String> parameterValues = entry.getValue();

					values.put(entry.getKey(), parameterValues.get(0));
				}
			}
			catch (Exception exception) {
				throw new BadRequestException(
					"Request body is not a valid multipart form", exception);
			}
		}

		ContextResolver<ObjectMapper> contextResolver =
			_providers.getContextResolver(
				ObjectMapper.class, MediaType.MULTIPART_FORM_DATA_TYPE);

		return MultipartBody.of(
			binaryFiles, contextResolver::getContext, values);
	}

	private UploadServletRequest _getUploadServletRequest(
		ServletRequest servletRequest) {

		while (servletRequest instanceof ServletRequestWrapper) {
			if (servletRequest instanceof UploadServletRequest) {
				return (UploadServletRequest)servletRequest;
			}

			ServletRequestWrapper servletRequestWrapper =
				(ServletRequestWrapper)servletRequest;

			servletRequest = servletRequestWrapper.getRequest();
		}

		if (servletRequest instanceof UploadServletRequest) {
			return (UploadServletRequest)servletRequest;
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultipartBodyMessageBodyReader.class);

	@Context
	private ObjectMapper _objectMapper;

	@Context
	private Providers _providers;

}