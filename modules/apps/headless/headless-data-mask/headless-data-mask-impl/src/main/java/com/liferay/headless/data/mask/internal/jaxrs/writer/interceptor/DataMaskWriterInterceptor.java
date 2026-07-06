/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.jaxrs.writer.interceptor;

import com.liferay.headless.data.mask.internal.engine.DataMaskEngineUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
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
import java.io.Serializable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
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
		"osgi.jaxrs.name=Liferay.Headless.Data.Mask.WriterInterceptor"
	},
	scope = ServiceScope.PROTOTYPE, service = WriterInterceptor.class
)
@Priority(Priorities.USER - 100)
public class DataMaskWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext)
		throws IOException {

		MediaType mediaType = writerInterceptorContext.getMediaType();

		if ((mediaType == null) ||
			(!mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE) &&
			 !mediaType.isCompatible(MediaType.TEXT_PLAIN_TYPE))) {

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

		List<ObjectEntry> objectEntries = _getObjectEntries(
			_portal.getCompanyId(_httpServletRequest),
			dataMaskExternalReferenceCodes);

		if (ListUtil.isEmpty(objectEntries)) {
			writerInterceptorContext.proceed();

			return;
		}

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

			originalOutputStream.write(
				_redact(
					objectEntries, body
				).getBytes(
					charset
				));
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

	private List<ObjectEntry> _getObjectEntries(
		long companyId, List<String> dataMaskExternalReferenceCodes) {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311")) {
			return Collections.emptyList();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_MASK", companyId);

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		List<ObjectEntry> objectEntries = new ArrayList<>();

		for (String dataMaskExternalReferenceCode :
				dataMaskExternalReferenceCodes) {

			if (Validator.isNull(dataMaskExternalReferenceCode)) {
				continue;
			}

			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				dataMaskExternalReferenceCode, 0,
				objectDefinition.getObjectDefinitionId());

			if (objectEntry == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No data mask was resolved for external reference ",
							"code \"", dataMaskExternalReferenceCode, "\""));
				}

				continue;
			}

			objectEntries.add(objectEntry);
		}

		return objectEntries;
	}

	private String _redact(List<ObjectEntry> objectEntries, String text) {
		for (ObjectEntry objectEntry : objectEntries) {
			Map<String, Serializable> values = objectEntry.getValues();

			String detectionRegex = MapUtil.getString(values, "detectionRegex");
			String replacementValue = MapUtil.getString(
				values, "replacementValue");

			if (Validator.isNull(detectionRegex) ||
				Validator.isNull(replacementValue)) {

				continue;
			}

			try {
				text = DataMaskEngineUtil.redact(
					detectionRegex,
					MapUtil.getString(values, "replacementRegex"),
					replacementValue, text);
			}
			catch (RuntimeException runtimeException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to apply data mask \"",
							MapUtil.getString(values, "name"), "\": ",
							runtimeException.getMessage()));
				}
			}
		}

		return text;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataMaskWriterInterceptor.class);

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}