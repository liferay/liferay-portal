/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.instance.lifecycle.test;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.headless.builder.test.util.APIApplicationTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Carlos Correa
 */
@FeatureFlag("LPS-178642")
public class APIApplicationPublisherPortalInstanceLifecycleListenerTest
	extends BaseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		Class<?> clazz = _modelListener.getClass();

		ComponentDescriptionDTO modelListenerComponentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				FrameworkUtil.getBundle(clazz), clazz.getName());

		clazz = _portalInstanceLifecycleListener.getClass();

		ComponentDescriptionDTO
			portalInstanceLifecycleListenerComponentDescriptionDTO =
				_serviceComponentRuntime.getComponentDescriptionDTO(
					FrameworkUtil.getBundle(clazz), clazz.getName());

		String baseURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
		String path =
			StringPool.FORWARD_SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString());

		try {
			_disableComponentDescriptionDTO(
				modelListenerComponentDescriptionDTO);

			_disableComponentDescriptionDTO(
				portalInstanceLifecycleListenerComponentDescriptionDTO);

			String externalReferenceCode = RandomTestUtil.randomString();

			_addAPIApplication(baseURL, externalReferenceCode, path);

			APIApplicationTestUtil.assertNotDeployedAPIApplication(baseURL);

			_enableComponentDescriptionDTO(
				portalInstanceLifecycleListenerComponentDescriptionDTO);

			APIApplicationTestUtil.assertDeployedAPIApplication(baseURL);

			Assert.assertTrue(
				HTTPTestUtil.invokeToJSONObject(
					null, "c/" + baseURL + "/openapi.json", Http.Method.GET
				).getJSONObject(
					"paths"
				).has(
					path
				));
		}
		finally {
			_enableComponentDescriptionDTO(
				modelListenerComponentDescriptionDTO);

			_enableComponentDescriptionDTO(
				portalInstanceLifecycleListenerComponentDescriptionDTO);
		}
	}

	private void _addAPIApplication(
			String baseURL, String externalReferenceCode, String path)
		throws Exception {

		String apiEndpointExternalReferenceCode = RandomTestUtil.randomString();
		String apiSchemaExternalReferenceCode = RandomTestUtil.randomString();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"apiApplicationToAPIEndpoints",
				JSONUtil.put(
					JSONUtil.put(
						"description", "description"
					).put(
						"externalReferenceCode",
						apiEndpointExternalReferenceCode
					).put(
						"httpMethod", "get"
					).put(
						"name", "name"
					).put(
						"path", path
					).put(
						"retrieveType",
						APIApplication.Endpoint.RetrieveType.COLLECTION.
							getValue()
					).put(
						"scope",
						APIApplication.Endpoint.Scope.COMPANY.getValue()
					))
			).put(
				"apiApplicationToAPISchemas",
				JSONUtil.put(
					JSONUtil.put(
						"apiSchemaToAPIProperties",
						JSONUtil.put(
							JSONUtil.put(
								"description", "description"
							).put(
								"name", "name"
							).put(
								"objectFieldERC", "APPLICATION_STATUS"
							))
					).put(
						"description", "description"
					).put(
						"externalReferenceCode", apiSchemaExternalReferenceCode
					).put(
						"mainObjectDefinitionERC", "L_API_APPLICATION"
					).put(
						"name", "name"
					))
			).put(
				"applicationStatus", "published"
			).put(
				"baseURL", baseURL
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			"headless-builder/applications", Http.Method.POST);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/requestAPISchemaToAPIEndpoints/",
				apiEndpointExternalReferenceCode),
			Http.Method.PUT);
		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"headless-builder/schemas/by-external-reference-code/",
				apiSchemaExternalReferenceCode,
				"/responseAPISchemaToAPIEndpoints/",
				apiEndpointExternalReferenceCode),
			Http.Method.PUT);
	}

	private void _disableComponentDescriptionDTO(
			ComponentDescriptionDTO componentDescriptionDTO)
		throws Exception {

		if (!_serviceComponentRuntime.isComponentEnabled(
				componentDescriptionDTO)) {

			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private void _enableComponentDescriptionDTO(
			ComponentDescriptionDTO componentDescriptionDTO)
		throws Exception {

		if (_serviceComponentRuntime.isComponentEnabled(
				componentDescriptionDTO)) {

			return;
		}

		Promise<Void> promise = _serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	@Inject(
		filter = "component.name=com.liferay.headless.builder.internal.model.listener.APIApplicationPublisherObjectEntryModelListener"
	)
	private ModelListener<ObjectEntry> _modelListener;

	@Inject(
		filter = "component.name=com.liferay.headless.builder.internal.instance.lifecycle.APIApplicationPublisherPortalInstanceLifecycleListener"
	)
	private PortalInstanceLifecycleListener _portalInstanceLifecycleListener;

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}