/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code LayoutFriendlyURLsException} to a {@code 400} error.
 *
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Delivery)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Delivery.SitePageFriendlyURLsExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class SitePageFriendlyURLsExceptionMapper
	extends BaseExceptionMapper<LayoutFriendlyURLsException> {

	@Override
	protected Problem getProblem(
		LayoutFriendlyURLsException layoutFriendlyURLsException) {

		return new Problem(layoutFriendlyURLsException);
	}

}