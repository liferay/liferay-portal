/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.rest.internal.dto.v1_0.converter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.core.UriInfo;

import java.util.Locale;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class UserAccountDTOConverterContext extends DefaultDTOConverterContext {

	public UserAccountDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Map<String, String>> actions,
		DTOConverterRegistry dtoConverterRegistry, long groupId, Object id,
		Locale locale, UriInfo uriInfo, User user) {

		super(
			acceptAllLanguages, actions, dtoConverterRegistry, id, locale,
			uriInfo, user);

		_groupId = groupId;
	}

	public long getGroupId() {
		return _groupId;
	}

	private final long _groupId;

}