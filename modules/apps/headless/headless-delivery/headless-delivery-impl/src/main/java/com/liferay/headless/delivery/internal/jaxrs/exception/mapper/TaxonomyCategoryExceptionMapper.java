/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code AssetCategoryException} to a {@code 400} error.
 *
 * @author Javier de Arcos
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.TaxonomyCategoryExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class TaxonomyCategoryExceptionMapper
	extends BaseExceptionMapper<AssetCategoryException> {

	@Override
	protected Problem getProblem(
		AssetCategoryException assetCategoryException) {

		String errorMessage = StringPool.BLANK;

		String vocabularyName = StringPool.BLANK;

		AssetVocabulary assetVocabulary =
			assetCategoryException.getVocabulary();

		if (assetVocabulary != null) {
			vocabularyName = assetVocabulary.getName();
		}

		if (assetCategoryException.getType() ==
				AssetCategoryException.AT_LEAST_ONE_CATEGORY) {

			errorMessage =
				"Select at least one taxonomy category for " + vocabularyName;
		}
		else if (assetCategoryException.getType() ==
					AssetCategoryException.TOO_MANY_CATEGORIES) {

			errorMessage =
				"Unable to select more than one taxonomy category for " +
					vocabularyName;
		}

		return new Problem(Response.Status.BAD_REQUEST, errorMessage);
	}

}