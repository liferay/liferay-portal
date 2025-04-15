/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectEntryFolderNameException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code ObjectEntryFolderNameException} to a {@code 400} or
 * {@code 409} error.
 *
 * @author Alicia García
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.ObjectEntryFolderNameExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectEntryFolderNameExceptionMapper
	extends BaseExceptionMapper<ObjectEntryFolderNameException> {

	@Override
	protected Problem getProblem(
		ObjectEntryFolderNameException objectEntryFolderNameException) {

		if (objectEntryFolderNameException instanceof
				ObjectEntryFolderNameException.MustNotBeDuplicate) {

			return new Problem(
				Response.Status.CONFLICT,
				objectEntryFolderNameException.getMessage());
		}

		if (objectEntryFolderNameException instanceof
				ObjectEntryFolderNameException.MustNotBeNull) {

			return new Problem(objectEntryFolderNameException);
		}

		return new Problem(objectEntryFolderNameException);
	}

}