/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

/**
 * @author Leonardo Barros
 */
public class ObjectMapperUtil {

	public static <T> T readValue(Class<?> clazz, Object object) {
		try {
			return readValue(clazz, _objectMapper.writeValueAsString(object));
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonProcessingException);
			}

			return null;
		}
	}

	public static <T> T readValue(Class<?> clazz, String json) {
		try {
			return (T)_objectMapper.readValue(json, clazz);
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonProcessingException);
			}

			return null;
		}
	}

	public static <T> T unsafeReadValue(Class<?> clazz, String json) {
		try {
			return (T)_objectMapper.readValue(json, clazz);
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException(jsonProcessingException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectMapperUtil.class);

	private static final ObjectMapper _objectMapper =
		ObjectMapperProviderUtil.getObjectMapper();

}