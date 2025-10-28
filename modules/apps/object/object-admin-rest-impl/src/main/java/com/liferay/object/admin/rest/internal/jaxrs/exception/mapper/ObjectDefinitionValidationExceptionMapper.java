/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectDefinitionValidationException;
import com.liferay.object.exception.ObjectDefinitionValidationException.ValidationError;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Farias
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.ObjectDefinitionValidationExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectDefinitionValidationExceptionMapper
	extends BaseExceptionMapper<ObjectDefinitionValidationException> {

	@Override
	protected Problem getProblem(
		ObjectDefinitionValidationException
			objectDefinitionValidationException) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (ValidationError validationError :
				objectDefinitionValidationException.getValidationErrors()) {

			jsonArray.put(
				JSONUtil.put(
					"entryClassName", validationError.getEntryClassName()
				).put(
					"entryKey", validationError.getEntryKey()
				).put(
					"errorMessage", validationError.getErrorMessage()
				).put(
					"exceptionClassName",
					validationError.getExceptionClassName()
				).put(
					"propertyName", validationError.getPropertyName()
				).put(
					"propertyValue", validationError.getPropertyValue()
				));
		}

		return new Problem(
			jsonArray.toString(), Response.Status.BAD_REQUEST, null,
			ObjectDefinitionValidationException.class.getName());
	}

	@Reference
	private JSONFactory _jsonFactory;

}