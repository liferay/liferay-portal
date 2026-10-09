/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.xml;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.xml.Element;

import java.lang.reflect.Method;

import java.util.List;

/**
 * @author Charles May
 */
public class BeanToXMLUtil {

	public static void addBean(Object object, Element parentEl) {
		Class<?> clazz = object.getClass();

		Element el = parentEl.addElement(
			getClassNameWithoutPackage(clazz.getName()));

		addFields(object, el);
	}

	public static void addFields(Object object, Element parentEl) {
		Class<?> clazz = object.getClass();

		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			String methodName = method.getName();

			if (!methodName.startsWith("get") ||
				methodName.equals("getClass")) {

				continue;
			}

			String memberName = StringUtil.removeSubstring(methodName, "get");

			memberName = TextFormatter.format(memberName, TextFormatter.I);
			memberName = TextFormatter.format(memberName, TextFormatter.K);

			try {
				Object returnValue = method.invoke(object, new Object[0]);

				if (returnValue instanceof List<?>) {
					List<Object> list = (List<Object>)returnValue;

					Element listEl = parentEl.addElement(memberName);

					for (Object curObject : list) {
						addBean(curObject, listEl);
					}
				}
				else {
					_add(parentEl, memberName, returnValue.toString());
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	public static String getClassNameWithoutPackage(String className) {
		String[] classNameArray = StringUtil.split(className, CharPool.PERIOD);

		String classNameWithoutPackage =
			classNameArray[classNameArray.length - 1];

		classNameWithoutPackage = TextFormatter.format(
			classNameWithoutPackage, TextFormatter.I);
		classNameWithoutPackage = TextFormatter.format(
			classNameWithoutPackage, TextFormatter.K);

		return classNameWithoutPackage;
	}

	private static Element _add(Element element, String name, String text) {
		Element childElement = element.addElement(name);

		childElement.addText(GetterUtil.getString(text));

		return childElement;
	}

	private static final Log _log = LogFactoryUtil.getLog(BeanToXMLUtil.class);

}