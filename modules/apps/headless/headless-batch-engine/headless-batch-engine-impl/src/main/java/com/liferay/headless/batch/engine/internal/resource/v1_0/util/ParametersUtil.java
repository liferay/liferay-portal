/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.internal.resource.v1_0.util;

import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivica Cardic
 */
public class ParametersUtil {

	public static Map<String, Serializable> toParameters(
		UriInfo contextUriInfo, Set<String> ignoredParameters) {

		return HashMapBuilder.<String, Serializable>putAll(
			_toMap(ignoredParameters, contextUriInfo.getPathParameters())
		).putAll(
			_toMap(ignoredParameters, contextUriInfo.getQueryParameters())
		).build();
	}

	private static Map<String, Serializable> _toMap(
		Set<String> ignoredParameters,
		MultivaluedMap<String, String> uriInfoParameters) {

		Map<String, Serializable> parameters = new HashMap<>();

		for (Map.Entry<String, List<String>> entry :
				uriInfoParameters.entrySet()) {

			String key = entry.getKey();

			if (ignoredParameters.contains(key)) {
				continue;
			}

			List<String> values = entry.getValue();

			if (!values.isEmpty()) {
				parameters.put(key, values.get(0));
			}
		}

		return parameters;
	}

}