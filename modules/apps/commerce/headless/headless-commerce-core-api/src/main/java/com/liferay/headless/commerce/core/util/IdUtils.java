/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author Zoltán Takács
 */
public class IdUtils {

	public static String getExternalReferenceCodeFromId(String id) {
		if ((id != null) && (id.length() >= 5) && id.startsWith("ext-")) {
			return id.substring(4);
		}

		throw new ClientErrorException(
			"Unable to parse {Id} parameter:" + id, Response.Status.CONFLICT);
	}

	public static boolean isLocalPK(String id) {
		try {
			NumberFormat numberFormat = NumberFormat.getInstance();

			numberFormat.parse(id);
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException);
			}

			return false;
		}

		return true;
	}

	private IdUtils() {
	}

	private static final Log _log = LogFactoryUtil.getLog(IdUtils.class);

}