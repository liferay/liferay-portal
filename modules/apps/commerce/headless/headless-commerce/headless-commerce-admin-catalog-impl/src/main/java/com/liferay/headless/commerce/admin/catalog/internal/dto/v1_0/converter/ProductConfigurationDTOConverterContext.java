/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.core.UriInfo;

import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class ProductConfigurationDTOConverterContext
	extends DefaultDTOConverterContext {

	public ProductConfigurationDTOConverterContext(
		boolean acceptAllLanguages, Map<String, Map<String, String>> actions,
		long cpConfigurationEntryId, DTOConverterRegistry dtoConverterRegistry,
		Object id, Locale locale, boolean showDifferences, UriInfo uriInfo,
		User user) {

		super(
			acceptAllLanguages, actions, dtoConverterRegistry, id, locale,
			uriInfo, user);

		_cpConfigurationEntryId = cpConfigurationEntryId;
		_showDifferences = showDifferences;
	}

	public long getCPConfigurationEntryId() {
		return _cpConfigurationEntryId;
	}

	public boolean getShowDifferences() {
		return _showDifferences;
	}

	private final long _cpConfigurationEntryId;
	private final boolean _showDifferences;

}