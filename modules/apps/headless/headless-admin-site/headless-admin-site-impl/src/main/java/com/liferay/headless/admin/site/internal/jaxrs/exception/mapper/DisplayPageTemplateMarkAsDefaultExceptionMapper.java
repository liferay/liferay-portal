/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.jaxrs.exception.mapper;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryDefaultTemplateException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Site)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Site.DisplayPageTemplateMarkAsDefaultExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class DisplayPageTemplateMarkAsDefaultExceptionMapper
	extends BaseExceptionMapper
		<LayoutPageTemplateEntryDefaultTemplateException> {

	@Override
	protected Problem getProblem(
		LayoutPageTemplateEntryDefaultTemplateException
			layoutPageTemplateEntryDefaultTemplateException) {

		String name = "display page template";

		if (Objects.equals(
				layoutPageTemplateEntryDefaultTemplateException.getType(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			name = "master page";
		}

		return new Problem(
			Response.Status.CONFLICT,
			StringUtil.replace(
				layoutPageTemplateEntryDefaultTemplateException.getMessage(),
				"layout page template entry", name));
	}

}