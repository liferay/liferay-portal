/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.openapi.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.openapi.v1_0.ObjectEntryOpenAPIResource;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryOpenAPIResourceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag("LPD-6233")
	@Test
	public void testGetOpenAPI() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_testGetOpenAPI(false, objectDefinition, "/");

		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"assignee"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		_testGetOpenAPI(true, objectDefinition, "/");

		objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new AssigneeObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"assignee"
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);

		_testGetOpenAPI(true, objectDefinition, "/scopes/{scopeKey}");
	}

	private void _testGetOpenAPI(
			boolean hasAssigneeUserExternalReferenceCodeParameter,
			ObjectDefinition objectDefinition, String path)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryOpenAPIResourceTest.class);

		try (ServiceTrackerMap<String, ObjectEntryOpenAPIResource>
				serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
					bundle.getBundleContext(), ObjectEntryOpenAPIResource.class,
					"openapi.resource.key")) {

			ObjectEntryOpenAPIResource objectEntryOpenAPIResource =
				serviceTrackerMap.getService(objectDefinition.getName());

			Response response = objectEntryOpenAPIResource.getOpenAPI(
				null, "json", null);

			OpenAPI openAPI = (OpenAPI)response.getEntity();

			Paths paths = openAPI.getPaths();

			PathItem pathItem = paths.get(path);

			Operation operation = pathItem.getGet();

			Assert.assertEquals(
				hasAssigneeUserExternalReferenceCodeParameter,
				ListUtil.exists(
					operation.getParameters(),
					parameter -> StringUtil.equals(
						parameter.getName(),
						"assigneeUserExternalReferenceCode")));
		}
	}

}