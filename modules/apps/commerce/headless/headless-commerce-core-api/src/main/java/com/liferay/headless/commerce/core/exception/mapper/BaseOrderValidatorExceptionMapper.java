/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.exception.mapper;

import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

/**
 * @author Andrea Sbarra
 */
@Provider
public class BaseOrderValidatorExceptionMapper
	extends BaseExceptionMapper<CommerceOrderValidatorException> {

	@Override
	public Response toResponse(
		CommerceOrderValidatorException commerceOrderValidatorException) {

		return super.toResponse(commerceOrderValidatorException);
	}

	@Override
	protected Problem getProblem(
		CommerceOrderValidatorException commerceOrderValidatorException) {

		List<CommerceOrderValidatorResult> commerceOrderValidatorResults =
			commerceOrderValidatorException.getCommerceOrderValidatorResults();

		StringBundler sb = new StringBundler(
			commerceOrderValidatorResults.size() * 2);

		for (CommerceOrderValidatorResult commerceOrderValidatorResult :
				commerceOrderValidatorResults) {

			if (commerceOrderValidatorResult.hasMessageResult()) {
				sb.append(commerceOrderValidatorResult.getLocalizedMessage());
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return new Problem(
			sb.toString(), Response.Status.BAD_REQUEST,
			CommerceOrderValidatorException.class.getSimpleName(),
			CommerceOrderValidatorException.class.getSimpleName());
	}

}