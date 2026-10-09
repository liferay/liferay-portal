/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class JavaSignatureParametersCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		if (javaTerm.hasAnnotation("Deprecated")) {
			return javaTerm.getContent();
		}

		JavaSignature signature = javaTerm.getSignature();

		List<JavaParameter> parameters = signature.getParameters();

		if (parameters.size() > 1) {
			_checkParameterOrder(
				fileName, parameters, javaTerm.getLineNumber(),
				"HttpServletRequest", "HttpServletResponse",
				"LiferayPortletRequest", "LiferayPortletResponse",
				"RenderRequest", "RenderResponse");
			_checkParameterOrder(
				fileName, parameters, javaTerm.getLineNumber(),
				"PortletRequest", "PortletResponse");
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD};
	}

	private void _checkParameterOrder(
		String fileName, List<JavaParameter> parameters, int lineNumber,
		String... typeNames) {

		Map<String, Integer> indexMap = new HashMap<>();

		for (String typeName : typeNames) {
			for (int i = 0; i < parameters.size(); i++) {
				JavaParameter javaParameter = parameters.get(i);

				if (!typeName.equals(javaParameter.getParameterType())) {
					continue;
				}

				for (Map.Entry<String, Integer> entry : indexMap.entrySet()) {
					if (i >= entry.getValue()) {
						continue;
					}

					addMessage(
						fileName,
						StringBundler.concat(
							"Parameter of type \"", entry.getKey(),
							"\" should come before parameter of type \"",
							typeName, "\""),
						lineNumber);
				}

				indexMap.put(typeName, i);
			}
		}
	}

}