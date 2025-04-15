/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.multipart;

import jakarta.servlet.http.Part;

import jakarta.ws.rs.core.HttpHeaders;

import java.lang.reflect.Parameter;

/**
 * @author Javier Gamarra
 */
public class MultipartUtil {

	public static String getFileName(Part part) {
		String header = part.getHeader(HttpHeaders.CONTENT_DISPOSITION);

		if (header == null) {
			return part.getName();
		}

		String string = "filename=\"";

		int index = header.indexOf(string);

		if (index == -1) {
			return null;
		}

		return header.substring(index + string.length(), header.length() - 1);
	}

	public static boolean isMultipartBody(Parameter parameter) {
		Class<?> clazz = parameter.getType();

		String typeName = clazz.getTypeName();

		return typeName.contains("MultipartBody");
	}

}