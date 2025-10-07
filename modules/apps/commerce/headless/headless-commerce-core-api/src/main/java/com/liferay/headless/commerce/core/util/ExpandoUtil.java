/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.util;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Igor Beslic
 */
public class ExpandoUtil {

	public static void updateExpando(
		long companyId, Class<?> clazz, long classPK,
		Map<String, ?> expandoAttributes) {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			companyId, clazz.getName(), classPK);

		Enumeration<String> enumeration = expandoBridge.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (!expandoAttributes.containsKey(attributeName)) {
				continue;
			}

			Object attributeValue = expandoAttributes.get(attributeName);

			if ((ExpandoColumnConstants.DATE == expandoBridge.getAttributeType(
					attributeName)) &&
				(attributeValue.getClass() != Date.class)) {

				expandoBridge.setAttribute(
					attributeName,
					_parseDate((String)expandoAttributes.get(attributeName)));
			}
			else {
				expandoBridge.setAttribute(
					attributeName,
					(Serializable)expandoAttributes.get(attributeName));
			}
		}
	}

	private static Serializable _parseDate(String data) {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		try {
			return dateFormat.parse(data);
		}
		catch (ParseException parseException) {
			throw new IllegalArgumentException(
				"Unable to parse date from " + data, parseException);
		}
	}

}