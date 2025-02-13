/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.jaxrs.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class HeadlessDiscoveryOpenAPIResourceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition1 = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_objectDefinition2 = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Ignore
	@Test
	@TestInfo("LPD-33459")
	public void testGetGlobalOpenAPI() throws Exception {
		List<String> globalOpenAPIPaths = _getPaths(
			HTTPTestUtil.invokeToJSONObject(
				null, "openapi/openapi.json", Http.Method.GET));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, "openapi", Http.Method.GET);

		Map<String, Object> map = jsonObject.toMap();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			for (String openAPIPath : (List<String>)entry.getValue()) {
				for (String path :
						_getPaths(
							HTTPTestUtil.invokeToJSONObject(
								null, _getOpenAPISubpath(openAPIPath),
								Http.Method.GET))) {

					if (path.endsWith("/")) {
						path = path.substring(0, path.lastIndexOf("/"));
					}

					Assert.assertTrue(
						globalOpenAPIPaths.remove(entry.getKey() + path));
				}
			}
		}

		Assert.assertTrue(globalOpenAPIPaths.isEmpty());
	}

	private String _getOpenAPISubpath(String openAPIPath) {
		String openAPISubpath = StringUtil.removeFirst(
			openAPIPath, "http://localhost:8080/o/");

		return StringUtil.replaceLast(openAPISubpath, ".yaml", ".json");
	}

	private List<String> _getPaths(JSONObject openAPIJSONObject) {
		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

		Map<String, Object> map = pathsJSONObject.toMap();

		return new ArrayList<>(map.keySet());
	}

	private ObjectDefinition _publishObjectDefinition(String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}