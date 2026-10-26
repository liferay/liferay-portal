/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.jaxrs.exception.mapper;

import com.liferay.depot.exception.DepotEntryGroupRelToGroupException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Converts any {@code MustAcceptDepotEntryType} to a {@code 400} error.
 *
 * @author Javier Moral
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Asset.Library)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Asset.Library.ConnectedSiteMustAcceptDepotEntryTypeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ConnectedSiteMustAcceptDepotEntryTypeExceptionMapper
	extends BaseExceptionMapper
		<DepotEntryGroupRelToGroupException.MustAcceptDepotEntryType> {

	@Override
	protected Problem getProblem(
		DepotEntryGroupRelToGroupException.MustAcceptDepotEntryType
			mustAcceptDepotEntryType) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.get(
				_acceptLanguage.getPreferredLocale(),
				"this-connection-is-not-allowed"));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}