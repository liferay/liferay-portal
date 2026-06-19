/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.jaxrs.writer.interceptor;

import com.liferay.headless.data.masking.engine.DataMaskingEngine;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.Priority;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=*)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Data.Masking.WriterInterceptor"
	},
	scope = ServiceScope.PROTOTYPE, service = WriterInterceptor.class
)
@Priority(Priorities.USER - 100)
public class DataMaskingWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		if (!_isRedactableMediaType(writerInterceptorContext.getMediaType())) {
			writerInterceptorContext.proceed();

			return;
		}

		List<String> dataMaskExternalReferenceCodes =
			_getDataMaskExternalReferenceCodes(
				_httpServletRequest.getHeader("X-Liferay-Data-Masks"));

		if (ListUtil.isEmpty(dataMaskExternalReferenceCodes)) {
			writerInterceptorContext.proceed();

			return;
		}

		long companyId = _portal.getCompanyId(_httpServletRequest);

		OutputStream originalOutputStream =
			writerInterceptorContext.getOutputStream();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		writerInterceptorContext.setOutputStream(byteArrayOutputStream);

		try {
			writerInterceptorContext.proceed();

			Charset charset = _getCharset(
				writerInterceptorContext.getMediaType());

			String body = byteArrayOutputStream.toString(charset);

			String redactedBody = _dataMaskingEngine.redact(
				companyId, dataMaskExternalReferenceCodes, body);

			originalOutputStream.write(redactedBody.getBytes(charset));
		}
		finally {
			writerInterceptorContext.setOutputStream(originalOutputStream);
		}
	}

	private Charset _getCharset(MediaType mediaType) {
		if (mediaType == null) {
			return StandardCharsets.UTF_8;
		}

		Map<String, String> parameters = mediaType.getParameters();

		String charset = parameters.get(MediaType.CHARSET_PARAMETER);

		if (Validator.isNull(charset)) {
			return StandardCharsets.UTF_8;
		}

		return Charset.forName(charset);
	}

	private List<String> _getDataMaskExternalReferenceCodes(
		String headerValue) {

		if (Validator.isNull(headerValue)) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			Arrays.asList(StringUtil.split(headerValue, ',')),
			token -> {
				String trimmedString = token.trim();

				if (trimmedString.isEmpty()) {
					return null;
				}

				return trimmedString;
			});
	}

	private boolean _isRedactableMediaType(MediaType mediaType) {
		if ((mediaType != null) &&
			(mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE) ||
			 mediaType.isCompatible(MediaType.TEXT_PLAIN_TYPE))) {

			return true;
		}

		return false;
	}

	@Reference
	private DataMaskingEngine _dataMaskingEngine;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private Portal _portal;

}