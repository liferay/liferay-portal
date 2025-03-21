/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.Provider;

/**
 * @author João Pedro Carvalho Malta
 */
@Provider
public class UnsupportedOperationExceptionMapper
	extends BaseExceptionMapper<UnsupportedOperationException> {

	@Override
	protected Problem getProblem(
		UnsupportedOperationException unsupportedOperationException) {

		return new Problem(unsupportedOperationException);
	}

}