/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Converts any {@code JsonMappingException} to a {@code 400} error.
 *
 * @author Alejandro Hernández
 * @review
 */
public class JsonMappingExceptionMapper
	extends BaseExceptionMapper<JsonMappingException> {

	@Override
	protected Problem getProblem(JsonMappingException jsonMappingException) {
		List<JsonMappingException.Reference> references =
			jsonMappingException.getPath();

		if (references.isEmpty()) {
			return new Problem(
				Response.Status.BAD_REQUEST, "Unable to map JSON path");
		}

		StringBundler sb = new StringBundler(references.size() * 2);

		for (JsonMappingException.Reference reference : references) {
			sb.append(reference.getFieldName());
			sb.append(".");
		}

		sb.setIndex(sb.index() - 1);

		return new Problem(
			Response.Status.BAD_REQUEST,
			"Unable to map JSON path: " + sb.toString());
	}

}