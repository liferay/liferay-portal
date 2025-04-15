/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.internal.jaxrs.exception.mapper;

import com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryIdException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Channel)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Commerce.Admin.Channel.DuplicateChannelAccountIdExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateChannelAccountIdExceptionMapper
	extends BaseExceptionMapper
		<DuplicateCommerceChannelAccountEntryIdException> {

	@Override
	protected Problem getProblem(
		DuplicateCommerceChannelAccountEntryIdException
			duplicateCommerceChannelAccountEntryIdException) {

		return new Problem(
			Response.Status.CONFLICT,
			StringUtil.replace(
				duplicateCommerceChannelAccountEntryIdException.getMessage(),
				new String[] {"account entry ID", "commerce channel"},
				new String[] {"account ID", "channel"}));
	}

}