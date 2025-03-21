/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.jaxrs.exception.mapper;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Site)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Site.DisplayPageTemplateFolderKeyExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class DisplayPageTemplateFolderKeyExceptionMapper
	extends BaseExceptionMapper
		<LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException> {

	@Override
	protected Problem getProblem(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException) {

		String name = "page template set";

		if (Objects.equals(
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
					getType(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE)) {

			name = "display page template folder";
		}

		return new Problem(
			Response.Status.CONFLICT,
			StringUtil.replace(
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
					getMessage(),
				new String[] {
					"Layout page template collection key",
					"layout page template collection key",
					"layout page template collection"
				},
				new String[] {"Key", "key", name}));
	}

}