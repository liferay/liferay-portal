/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.jaxrs.exception.mapper;

import com.liferay.list.type.exception.DuplicateListTypeEntryException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nathaly Gomes
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.List.Type)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.List.Type.DuplicateListTypeEntryExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateListTypeEntryExceptionMapper
	extends BaseExceptionMapper<DuplicateListTypeEntryException> {

	@Override
	protected Problem getProblem(
		DuplicateListTypeEntryException duplicateListTypeEntryException) {

		return new Problem(duplicateListTypeEntryException);
	}

}