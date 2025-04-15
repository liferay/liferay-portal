/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Converts any {@code InvalidFormatException} to a {@code 400} error.
 *
 * @author Alejandro Hernández
 * @review
 */
public class InvalidFormatExceptionMapper
	extends BaseExceptionMapper<InvalidFormatException> {

	@Override
	protected Problem getProblem(
		InvalidFormatException invalidFormatException) {

		List<JsonMappingException.Reference> references =
			invalidFormatException.getPath();

		StringBundler sb = new StringBundler(references.size() * 2);

		for (JsonMappingException.Reference reference : references) {
			sb.append(reference.getFieldName());
			sb.append(".");
		}

		sb.setIndex(sb.index() - 1);

		Class<?> clazz = invalidFormatException.getTargetType();

		return new Problem(
			invalidFormatException.getLocalizedMessage(),
			Response.Status.BAD_REQUEST,
			StringBundler.concat(
				"Unable to map JSON path \"", sb, "\" with value \"",
				invalidFormatException.getValue(), "\" to class \"",
				clazz.getSimpleName(), "\""),
			InvalidFormatException.class.getName());
	}

}