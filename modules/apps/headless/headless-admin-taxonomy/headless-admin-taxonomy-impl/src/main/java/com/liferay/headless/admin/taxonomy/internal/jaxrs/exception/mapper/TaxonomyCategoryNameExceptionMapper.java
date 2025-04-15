/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.jaxrs.exception.mapper;

import com.liferay.asset.kernel.exception.AssetCategoryNameException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code DuplicateTagException} to a {@code 409} error.
 *
 * @author Víctor Galán
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Taxonomy)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Taxonomy.TaxonomyCategoryNameExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class TaxonomyCategoryNameExceptionMapper
	extends BaseExceptionMapper<AssetCategoryNameException> {

	@Override
	protected Problem getProblem(
		AssetCategoryNameException assetCategoryNameException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			StringUtil.replace(
				assetCategoryNameException.getMessage(), "category",
				"taxonomy category"));
	}

}