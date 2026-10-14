/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;

/**
 * @author Kevin Lee
 */
public class JavaReferenceAnnotationsCheck extends JavaAnnotationsCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String formatAnnotation(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent, String annotation, String indent)
		throws Exception {

		String trimmedAnnotation = StringUtil.trim(annotation);

		if (!(trimmedAnnotation.equals("@Reference") ||
			  trimmedAnnotation.startsWith("@Reference("))) {

			return annotation;
		}

		List<String> importNames = javaClass.getImportNames();

		if (!importNames.contains(
				"org.osgi.service.component.annotations.Reference")) {

			return annotation;
		}

		_checkReferenceMethods(fileName, absolutePath, javaClass);
		_checkTargetAttribute(
			fileName, absolutePath, javaClass, annotation, "target");

		return annotation;
	}

	private void _checkReferenceMethods(
		String fileName, String absolutePath, JavaClass javaClass) {

		if (!isAttributeValue(_CHECK_REFERENCE_METHOD_KEY, absolutePath)) {
			return;
		}

		for (String allowedReferenceMethodFileName :
				getAttributeValues(
					_ALLOWED_REFERENCE_METHOD_FILE_NAMES_KEY, absolutePath)) {

			if (absolutePath.endsWith(allowedReferenceMethodFileName)) {
				return;
			}
		}

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.hasAnnotation("Reference") ||
				!javaTerm.isJavaMethod()) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Do not use @Reference on method ", javaTerm.getName(),
					", use @Reference on field or ServiceTracker",
					"/ServiceTrackerList/ServiceTrackerMap instead"));
		}
	}

	private void _checkTargetAttribute(
			String fileName, String absolutePath, JavaClass javaClass,
			String annotation, String targetAttribute)
		throws Exception {

		String targetAttributeValue = getAnnotationAttributeValue(
			annotation, targetAttribute);

		List<String> ignoreTargetAttributeValues = getAttributeValues(
			_IGNORE_TARGET_ATTRIBUTE_VALUES_KEY, absolutePath);

		if ((targetAttributeValue == null) ||
			ignoreTargetAttributeValues.contains(targetAttributeValue)) {

			return;
		}

		checkComponentName(
			fileName, absolutePath, javaClass, targetAttributeValue, true);
	}

	private static final String _ALLOWED_REFERENCE_METHOD_FILE_NAMES_KEY =
		"allowedReferenceMethodFileNames";

	private static final String _CHECK_REFERENCE_METHOD_KEY =
		"checkReferenceMethod";

	private static final String _IGNORE_TARGET_ATTRIBUTE_VALUES_KEY =
		"ignoreTargetAttributeValues";

}