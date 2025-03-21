/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.core.UriInfo;

import java.util.Locale;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class LinkedProductDTOConverterContext
	extends DefaultDTOConverterContext {

	public LinkedProductDTOConverterContext(
		boolean acceptAllLanguages, long accountId,
		Map<String, Map<String, String>> actions, long channelId,
		DTOConverterRegistry dtoConverterRegistry, Object id, Locale locale,
		String productTypeName, UriInfo uriInfo, User user) {

		super(
			acceptAllLanguages, actions, dtoConverterRegistry, id, locale,
			uriInfo, user);

		_accountId = accountId;
		_channelId = channelId;
		_productTypeName = productTypeName;
	}

	public long getAccountId() {
		return _accountId;
	}

	public long getChannelId() {
		return _channelId;
	}

	public String getProductTypeName() {
		return _productTypeName;
	}

	private final long _accountId;
	private final long _channelId;
	private final String _productTypeName;

}