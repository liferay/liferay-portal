/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectActionParametersException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.ObjectActionParametersExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectActionParametersExceptionMapper
	extends BaseExceptionMapper<ObjectActionParametersException> {

	@Override
	protected Problem getProblem(
		ObjectActionParametersException objectActionParametersException) {

		String detail = objectActionParametersException.getMessage();

		if (detail == null) {
			detail = String.valueOf(
				_toJSONArray(objectActionParametersException.getMessageKeys()));
		}

		return new Problem(
			detail, Response.Status.BAD_REQUEST, null,
			ObjectActionParametersException.class.getName());
	}

	private JSONArray _toJSONArray(Map<String, Object> messageKeys) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (Map.Entry<String, Object> entry : messageKeys.entrySet()) {
			if (entry.getValue() instanceof Map) {
				jsonArray.put(
					JSONUtil.put(
						"fieldName", entry.getKey()
					).put(
						"messages",
						_toJSONArray((Map<String, Object>)entry.getValue())
					));

				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"fieldName", entry.getKey()
				).put(
					"message",
					_language.get(
						_acceptLanguage.getPreferredLocale(),
						(String)entry.getValue())
				));
		}

		return jsonArray;
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}