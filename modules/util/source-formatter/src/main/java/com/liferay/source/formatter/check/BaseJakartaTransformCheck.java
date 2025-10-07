/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Alan Huang
 */
public abstract class BaseJakartaTransformCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!isValidExtension(fileName)) {
			return content;
		}

		return format(fileName, absolutePath, content);
	}

	protected abstract String format(
			String fileName, String absolutePath, String content)
		throws Exception;

	protected abstract String[] getValidExtensions();

	protected boolean isValidExtension(String fileName) {
		for (String extension : getValidExtensions()) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	protected String replace(String value) {
		for (Map.Entry<String, String> entry : _replacementDashMap.entrySet()) {
			value = StringUtil.replace(value, entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, String> entry : _replacementDotMap.entrySet()) {
			value = StringUtil.replace(value, entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, String> entry :
				_replacementSlashMap.entrySet()) {

			value = StringUtil.replace(value, entry.getKey(), entry.getValue());
		}

		return StringUtil.replace(value, "JAVAX_", "JAKARTA_");
	}

	protected String replaceTaglibURIs(String content) {
		return StringUtil.replace(
			content,
			new String[] {
				"http://java.sun.com/jsp/jstl/core",
				"http://java.sun.com/jsp/jstl/fmt",
				"http://java.sun.com/jsp/jstl/functions",
				"http://java.sun.com/jsp/jstl/sql",
				"http://java.sun.com/jsp/jstl/xml"
			},
			new String[] {
				"jakarta.tags.core", "jakarta.tags.fmt",
				"jakarta.tags.functions", "jakarta.tags.sql", "jakarta.tags.xml"
			});
	}

	private static final Set<String> _preservedSubpackageNames = new HashSet<>(
		Arrays.asList("annotation.processing", "transaction.xa"));
	private static final Map<String, String> _replacementDashMap =
		new LinkedHashMap<>();
	private static final Map<String, String> _replacementDotMap =
		new LinkedHashMap<>();
	private static final Map<String, String> _replacementSlashMap =
		new LinkedHashMap<>();
	private static final Set<String> _subpackageNames = new HashSet<>(
		Arrays.asList(
			"activation", "annotation", "batch", "decorator", "ejb", "el",
			"enterprise", "faces", "inject", "interceptor", "jms", "json",
			"jws", "mail", "mvc", "persistence", "portlet", "resource",
			"security.auth.message", "security.enterprise", "security.jacc",
			"servlet", "transaction", "validation", "websocket", "ws.rs",
			"xml.bind", "xml.soap", "xml.ws"));

	static {
		_subpackageNames.forEach(
			subpackageName -> {
				String javaxPackage = "javax." + subpackageName;
				String jakartaPackage = "jakarta." + subpackageName;

				_replacementDashMap.put(
					StringUtil.replace(javaxPackage, '.', '-'),
					StringUtil.replace(jakartaPackage, '.', '-'));
				_replacementDotMap.put(javaxPackage, jakartaPackage);
				_replacementSlashMap.put(
					StringUtil.replace(javaxPackage, '.', '/'),
					StringUtil.replace(jakartaPackage, '.', '/'));
			});

		// Order matters, preserved subpackage names need to be put into
		// replacement map later

		_preservedSubpackageNames.forEach(
			preservedSubpackageName -> {
				String preservedJavaxPackage =
					"javax." + preservedSubpackageName;
				String preservedJakartaPackage =
					"jakarta." + preservedSubpackageName;

				_replacementDashMap.put(
					StringUtil.replace(preservedJakartaPackage, '.', '-'),
					StringUtil.replace(preservedJavaxPackage, '.', '-'));
				_replacementDotMap.put(
					preservedJakartaPackage, preservedJavaxPackage);
				_replacementSlashMap.put(
					StringUtil.replace(preservedJakartaPackage, '.', '/'),
					StringUtil.replace(preservedJavaxPackage, '.', '/'));
			});

		_replacementDashMap.put(
			"X-JAVAX-PORTLET-NAMESPACED-RESPONSE",
			"X-JAKARTA-PORTLET-NAMESPACED-RESPONSE");
	}

}