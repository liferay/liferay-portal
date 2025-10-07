/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;

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
						JSONUtil.put("x-filterable", JSONUtil.putAll())
					).put(
						"EntityModelResourceTestEntity2",
						JSONUtil.put("x-filterable", JSONUtil.putAll("id"))
					).put(
						"TestEntity",
						JSONUtil.put(
							"x-filterable",
							JSONUtil.putAll(
								"companyId", "customFields/booleanField",
								"customFields/integerField",
								"customFields/stringField", "dateModified",
								"description", "id", "keywords", "published",
								"statusCode")
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
										JSONUtil.putAll(
											"companyId",
											"customFields/booleanField",
											"customFields/integerField",
											"customFields/stringField",
											"dateModified", "description", "id",
											"keywords", "published",
											"statusCode")))))))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, "test/v1.0/openapi.json", Http.Method.GET
			).toString(),
			false);
	}

}