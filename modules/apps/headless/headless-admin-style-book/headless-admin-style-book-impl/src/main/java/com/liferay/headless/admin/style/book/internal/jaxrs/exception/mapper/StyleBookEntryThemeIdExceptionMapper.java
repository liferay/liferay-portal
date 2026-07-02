/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.style.book.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.style.book.exception.StyleBookEntryThemeIdException;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Style.Book)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Style.Book.StyleBookEntryThemeIdExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class StyleBookEntryThemeIdExceptionMapper
	extends BaseExceptionMapper<StyleBookEntryThemeIdException> {

	@Override
	protected Problem getProblem(
		StyleBookEntryThemeIdException styleBookEntryThemeIdException) {

		return new Problem(styleBookEntryThemeIdException);
	}

}