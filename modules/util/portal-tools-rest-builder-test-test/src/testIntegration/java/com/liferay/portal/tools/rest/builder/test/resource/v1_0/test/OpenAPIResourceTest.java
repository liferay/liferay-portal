/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class OpenAPIResourceTest {

	@Test
	public void testGetOpenAPI() throws Exception {
		JSONAssert.assertEquals(
			JSONUtil.put(
				"components",
				JSONUtil.put(
					"schemas",
					JSONUtil.put(
						"EntityModelResourceTestEntity1",
						JSONUtil.put("x-filterable", Collections.emptyMap())
					).put(
						"EntityModelResourceTestEntity2",
						JSONUtil.put(
							"x-filterable",
							JSONUtil.put("id", JSONUtil.put("type", "id")))
					).put(
						"TestEntity",
						JSONUtil.put(
							"x-filterable",
							JSONUtil.put(
								"companyId", JSONUtil.put("type", "id")
							).put(
								"customFields/booleanField",
								JSONUtil.put("type", "boolean")
							).put(
								"customFields/integerField",
								JSONUtil.put("type", "integer")
							).put(
								"customFields/stringField",
								JSONUtil.put("type", "string")
							).put(
								"dateModified",
								JSONUtil.put("type", "date_time")
							).put(
								"description", JSONUtil.put("type", "string")
							).put(
								"id", JSONUtil.put("type", "id")
							).put(
								"keywords",
								JSONUtil.put(
									"items", "string"
								).put(
									"type", "array"
								)
							).put(
								"published", JSONUtil.put("type", "boolean")
							).put(
								"statusCode",
								JSONUtil.put(
									"items", "integer"
								).put(
									"type", "array"
								)
							)
						).put(
							"x-test", true
						)
					))
			).put(
				"paths",
				JSONUtil.put(
					"/v1.0/test-entities",
					JSONUtil.put(
						"get",
						JSONUtil.put(
							"parameters",
							JSONUtil.putAll(
								JSONUtil.put(
									"schema",
									JSONUtil.put(
										"x-filterable",
										JSONUtil.put(
											"companyId",
											JSONUtil.put("type", "id")
										).put(
											"customFields/booleanField",
											JSONUtil.put("type", "boolean")
										).put(
											"customFields/integerField",
											JSONUtil.put("type", "integer")
										).put(
											"customFields/stringField",
											JSONUtil.put("type", "string")
										).put(
											"dateModified",
											JSONUtil.put("type", "date_time")
										).put(
											"description",
											JSONUtil.put("type", "string")
										).put(
											"id", JSONUtil.put("type", "id")
										).put(
											"keywords",
											JSONUtil.put(
												"items", "string"
											).put(
												"type", "array"
											)
										).put(
											"published",
											JSONUtil.put("type", "boolean")
										).put(
											"statusCode",
											JSONUtil.put(
												"items", "integer"
											).put(
												"type", "array"
											)
										)))))))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, "test/v1.0/openapi.json", Http.Method.GET
			).toString(),
			false);
	}

}