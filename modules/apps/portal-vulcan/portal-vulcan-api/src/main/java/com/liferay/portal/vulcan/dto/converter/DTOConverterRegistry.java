/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.converter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
public interface DTOConverterRegistry {

	public default Set<String> getDTOClassNames() {
		return new HashSet<>();
	}

	public DTOConverter<?, ?> getDTOConverter(String dtoClassName);

	public default DTOConverter<?, ?> getDTOConverter(
		String dtoClassName, String type) {

		return getDTOConverter(dtoClassName);
	}

	public DTOConverter<?, ?> getDTOConverter(
		String applicationName, String dtoClassName, String version);

}