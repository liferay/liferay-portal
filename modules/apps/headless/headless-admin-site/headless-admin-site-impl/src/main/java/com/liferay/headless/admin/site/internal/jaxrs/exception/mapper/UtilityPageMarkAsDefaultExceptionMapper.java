/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.jaxrs.exception.mapper;

import com.liferay.layout.utility.page.exception.DefaultLayoutUtilityPageEntryException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Site)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Site.DefaultUtilityPageExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class UtilityPageMarkAsDefaultExceptionMapper
	extends BaseExceptionMapper<DefaultLayoutUtilityPageEntryException> {

	@Override
	protected Problem getProblem(
		DefaultLayoutUtilityPageEntryException
			defaultLayoutUtilityPageEntryException) {

		return new Problem(defaultLayoutUtilityPageEntryException);
	}

}