/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Converts any {@code UnrecognizedPropertyException} to a {@code 400} error.
 *
 * @author Alejandro Hernández
 * @review
 */
public class UnrecognizedPropertyExceptionMapper
	extends BaseExceptionMapper<UnrecognizedPropertyException> {

	@Override
	protected Problem getProblem(
		UnrecognizedPropertyException unrecognizedPropertyException) {

		StringBundler sb = new StringBundler();

		List<JsonMappingException.Reference> references =
			unrecognizedPropertyException.getPath();

		for (int i = 0; i < references.size(); i++) {
			JsonMappingException.Reference reference = references.get(i);

			Object object = reference.getFrom();

			Class<?> clazz = object.getClass();

			sb.append(
				StringBundler.concat(
					"The property \"", reference.getFieldName(),
					"\" is not defined in ", clazz.getSimpleName(), "."));

			if ((i + 1) < references.size()) {
				sb.append(" ");
			}
		}

		return new Problem(Response.Status.BAD_REQUEST, sb.toString());
	}

}