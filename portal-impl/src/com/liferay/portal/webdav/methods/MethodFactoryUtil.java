/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.util.PropsUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class MethodFactoryUtil {

	public static Method create(HttpServletRequest httpServletRequest)
		throws WebDAVException {

		String method = httpServletRequest.getMethod();

		Map<String, Object> methods = MethodHolder._methods;

		Method methodImpl = (Method)methods.get(StringUtil.toUpperCase(method));

		if (methodImpl == null) {
			throw new WebDAVException(
				"Method " + method + " is not implemented");
		}

		return methodImpl;
	}

	private static class MethodHolder {

		private static final Map<String, Object> _methods =
			new HashMap<String, Object>() {
				{
					try {
						for (String methodName :
								Method.SUPPORTED_METHOD_NAMES) {

							String defaultClassName = methodName.substring(1);

							defaultClassName = StringUtil.toLowerCase(
								defaultClassName);
							defaultClassName =
								methodName.substring(0, 1) + defaultClassName;
							defaultClassName =
								"com.liferay.portal.webdav.methods." +
									defaultClassName + "MethodImpl";

							String className = GetterUtil.getString(
								PropsUtil.get(
									MethodFactoryUtil.class.getName() + "." +
										methodName),
								defaultClassName);

							put(
								methodName,
								InstanceFactory.newInstance(
									MethodFactoryUtil.class.getClassLoader(),
									className));
						}
					}
					catch (Exception exception) {
						throw new ExceptionInInitializerError(exception);
					}
				}
			};

	}

}