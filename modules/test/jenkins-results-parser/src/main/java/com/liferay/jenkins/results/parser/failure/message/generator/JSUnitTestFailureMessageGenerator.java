/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.Dom4JUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

/**
 * @author Brittney Nguyen
 */
public class JSUnitTestFailureMessageGenerator
	extends BaseFailureMessageGenerator {

	@Override
	public String getMessage(String consoleText) {
		Matcher packageFailureMatcher = _packageFailurePattern.matcher(
			consoleText);

		if (!packageFailureMatcher.find()) {
			return null;
		}

		List<String> packageFailureList = new ArrayList<>();

		while (packageFailureMatcher.find()) {
			String packageFailure = packageFailureMatcher.group(0);

			if (!packageFailureList.contains(packageFailure)) {
				packageFailureList.add(packageFailure);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Build completed with ");
		sb.append(packageFailureList.size());
		sb.append(" failures");

		for (int i = 0;
			 i < Math.min(packageFailureList.size(), _FAILURE_ITEMS_MAX); i++) {

			sb.append("\n* ");
			sb.append(packageFailureList.get(i));
		}

		return sb.toString();
	}

	@Override
	public Element getMessageElement(String consoleText) {
		Matcher packageFailureMatcher = _packageFailurePattern.matcher(
			consoleText);

		if (!packageFailureMatcher.find()) {
			return null;
		}

		List<Element> elementList = new ArrayList<>();

		List<String> packageFailureList = new ArrayList<>();

		while (packageFailureMatcher.find()) {
			String packageFailure = packageFailureMatcher.group(0);

			Element element = new DefaultElement("element");

			element.addText(packageFailure);

			if ((element != null) &&
				!packageFailureList.contains(packageFailure)) {

				elementList.add(element);
				packageFailureList.add(packageFailure);
			}
		}

		return Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"b", null,
				"Build completed with " + packageFailureList.size() +
					" failures."),
			Dom4JUtil.getOrderedListElement(elementList, _FAILURE_ITEMS_MAX));
	}

	private static final int _FAILURE_ITEMS_MAX = 7;

	private static final Pattern _packageFailurePattern = Pattern.compile(
		"Execution failed for task '[\\D]+:packageRunTest'");

}