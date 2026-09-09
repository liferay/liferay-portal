/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.test.util;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

/**
 * @author Gabriel Lima
 */
public class FrontendTokenDefinitionTestUtil {

	public static String getFrontendTokenDefinition(String frontendTokenName) {
		return JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put(
									"defaultValue",
									RandomTestUtil.randomString()
								).put(
									"editorType", "ColorPicker"
								).put(
									"label", RandomTestUtil.randomString()
								).put(
									"mappings",
									JSONUtil.putAll(
										JSONUtil.put(
											"type", "cssVariable"
										).put(
											"value",
											RandomTestUtil.randomString()
										))
								).put(
									"name", frontendTokenName
								).put(
									"type", "String"
								))
						).put(
							"label", RandomTestUtil.randomString()
						).put(
							"name", RandomTestUtil.randomString()
						))
				).put(
					"name", RandomTestUtil.randomString()
				))
		).toString();
	}

}