/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;
import com.liferay.portal.vulcan.jackson.databind.ser.VulcanPropertyFilter;
import com.liferay.portal.vulcan.util.FieldsUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ivica Cardic
 */
public class ObjectWriterFactory {

	public static ObjectWriter getObjectWriter(List<String> includeFieldNames) {
		ObjectMapper objectMapper =
			ObjectMapperProviderUtil.getBatchEngineObjectMapper();

		SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();

		if (includeFieldNames.isEmpty()) {
			simpleFilterProvider.setFailOnUnknownId(false);
		}
		else {
			Set<String> expandedFieldNames = new HashSet<>();

			for (String fieldName : includeFieldNames) {
				expandedFieldNames.addAll(FieldsUtil.expand(fieldName));
			}

			simpleFilterProvider.addFilter(
				"Liferay.Vulcan",
				VulcanPropertyFilter.of(expandedFieldNames, null));
		}

		return objectMapper.writer(simpleFilterProvider);
	}

}