/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.selenium;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Calum Ragan
 */
public class LiferaySeleniumMethod {

	public LiferaySeleniumMethod(Method seleniumMethod) {
		_method = seleniumMethod;
	}

	public Method getMethod() {
		return _method;
	}

	public String getMethodName() {
		return _method.getName();
	}

	public int getParameterCount() {
		return _method.getParameterCount();
	}

	public List<String> getParameterNames() {
		String[] parameterNames = _liferaySeleniumMethodNames.get(
			getMethodName());

		if (parameterNames != null) {
			return Arrays.asList(parameterNames);
		}

		int parameterCount = getParameterCount();

		if (parameterCount == 0) {
			return Collections.emptyList();
		}

		return _defaultParameterNames.subList(0, parameterCount);
	}

	public Class<?>[] getParameterTypes() {
		return _method.getParameterTypes();
	}

	private static final List<String> _defaultParameterNames = Arrays.asList(
		"locator1", "value1", "locator2");
	private static final List<String> _javaScriptMethodNames = Arrays.asList(
		"assertJavaScript", "executeJavaScript", "getJavaScriptResult",
		"waitForJavaScript", "waitForJavaScriptNoError", "verifyJavaScript");
	private static final Map<String, String[]> _liferaySeleniumMethodNames =
		new HashMap<String, String[]>() {
			{
				put(
					"assertAttributeValue",
					new String[] {"locator1", "value1", "value2"});
				put(
					"assertCSSValue",
					new String[] {"locator1", "locator2", "value1"});
				put("assertEmailBody", new String[] {"value1", "value2"});
				put("assertEmailSubject", new String[] {"value1", "value2"});
				put(
					"assertNotSelectedLabel",
					new String[] {"value1", "value2"});
				put("assertPrompt", new String[] {"value1", "value2"});
				put("connectToEmailAccount", new String[] {"value1", "value2"});
				put(
					"dragAndDropToObject",
					new String[] {"locator1", "locator2"});
				put(
					"dragAtAndDrop",
					new String[] {"locator1", "value1", "value2"});
				put("executeCDPCommand", new String[] {"value1", "value2"});
				put(
					"ocularAssertElementImage",
					new String[] {"locator1", "value1", "value2"});
				put("openWindow", new String[] {"value1", "value2"});
				put("replyToEmail", new String[] {"value1", "value2"});
				put("sendEmail", new String[] {"value1", "value2", "value3"});
				put("waitForPopUp", new String[] {"value1", "value2"});
			}
		};
	private static final List<String> _singleValueMethodNames = Arrays.asList(
		"assertAlert", "assertAlertText", "assertConfirmation",
		"assertConsoleTextNotPresent", "assertConsoleTextPresent",
		"assertHTMLSourceTextNotPresent", "assertHTMLSourceTextPresent",
		"assertJavaScriptErrors", "assertNotAlert", "assertLocation",
		"assertNotLocation", "assertPartialConfirmation",
		"assertPartialLocation", "assertTextNotPresent", "assertTextPresent",
		"getConfirmation", "getEmailBody", "getEmailSubject", "getEval",
		"isConsoleTextNotPresent", "isConsoleTextPresent",
		"isHTMLSourceTextPresent", "isTestName", "isTextNotPresent",
		"isTextPresent", "open", "pause", "runScript", "scrollBy",
		"setWindowSize", "selectPopUp", "selectWindow", "typeAlert",
		"typeScreen", "waitForConfirmation", "waitForConsoleTextNotPresent",
		"waitForConsoleTextPresent", "waitForTextNotPresent",
		"waitForTextPresent");

	static {
		for (String methodName : _javaScriptMethodNames) {
			_liferaySeleniumMethodNames.put(
				methodName, new String[] {"value1", "value2", "value3"});
		}

		for (String methodName : _singleValueMethodNames) {
			_liferaySeleniumMethodNames.put(
				methodName, new String[] {"value1"});
		}
	}

	private final Method _method;

}