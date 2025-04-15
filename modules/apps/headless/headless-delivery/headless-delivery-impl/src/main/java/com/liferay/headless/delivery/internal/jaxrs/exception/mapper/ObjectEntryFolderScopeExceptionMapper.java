/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectEntryFolderScopeException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code ObjectEntryFolderScopeException} to a {@code 409} error.
 *
 * @author Alicia García
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.ObjectEntryFolderScopeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectEntryFolderScopeExceptionMapper
	extends BaseExceptionMapper<ObjectEntryFolderScopeException> {

	@Override
	protected Problem getProblem(
		ObjectEntryFolderScopeException objectEntryFolderScopeException) {

		return new Problem(
			Response.Status.CONFLICT,
			objectEntryFolderScopeException.getMessage());
	}

}