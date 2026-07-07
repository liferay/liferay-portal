/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.node.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carolina Barbosa
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class AIHubAgentNodeExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				FileUtil.getBytes(
					classLoader.getResourceAsStream(
						"com/liferay/portal/workflow/kaleo/dependencies" +
							"/ai-hub-agent-workflow-definition.json")),
				TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			(Class<WorkflowHandler<?>>)(Class<?>)WorkflowHandler.class,
			(WorkflowHandler)ProxyUtil.newProxyInstance(
				WorkflowHandler.class.getClassLoader(),
				new Class<?>[] {WorkflowHandler.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getClassName")) {
						return clazz.getName();
					}

					if (Objects.equals(
							method.getName(), "getWorkflowDefinitionLink")) {

						return _workflowDefinitionLinkLocalService.
							updateWorkflowDefinitionLink(
								TestPropsValues.getUserId(),
								TestPropsValues.getCompanyId(), 0,
								clazz.getName(), 0, 0,
								_workflowDefinition.getName(), 1);
					}

					if (Objects.equals(
							method.getName(), "startWorkflowInstance")) {

						_workflowInstanceLinkLocalService.startWorkflowInstance(
							TestPropsValues.getCompanyId(), 0, (Long)args[2],
							clazz.getName(), 1,
							(Map<String, Serializable>)args[5]);
					}

					return null;
				}),
			HashMapDictionaryBuilder.put(
				"model.class.name=", clazz.getName()
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDoExecute() throws Exception {
		List<Callable<?>> callables = new ArrayList<>();
		Class<?> clazz = getClass();
		List<Http.Options> optionsList = new ArrayList<>();

		try (AutoCloseable autoCloseable1 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_nodeExecutor, "_http", _getHttp(optionsList));
			AutoCloseable autoCloseable2 =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					_nodeExecutor, "_noticeableExecutorService",
					_getNoticeableExecutorService(callables))) {

			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				TestPropsValues.getCompanyId(), 0, TestPropsValues.getUserId(),
				clazz.getName(), 1, null, new ServiceContext());

			WorkflowInstanceLink workflowInstanceLink =
				_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
					TestPropsValues.getCompanyId(), 0, clazz.getName(), 1);

			WorkflowInstance workflowInstance =
				_workflowInstanceManager.getWorkflowInstance(
					TestPropsValues.getCompanyId(),
					workflowInstanceLink.getWorkflowInstanceId());

			Assert.assertFalse(workflowInstance.isComplete());

			Assert.assertEquals(callables.toString(), 1, callables.size());

			Callable<?> callable = callables.get(0);

			callable.call();

			workflowInstance = _workflowInstanceManager.getWorkflowInstance(
				TestPropsValues.getCompanyId(),
				workflowInstanceLink.getWorkflowInstanceId());

			Assert.assertTrue(workflowInstance.isComplete());

			Map<String, Serializable> workflowContext =
				workflowInstance.getWorkflowContext();

			Assert.assertEquals(_OUTPUT, workflowContext.get("output"));

			Http.Options options = optionsList.get(0);

			Assert.assertEquals(
				"Bearer " + _ACCESS_TOKEN,
				options.getHeader(HttpHeaders.AUTHORIZATION));
			Assert.assertEquals(
				_USER_TOKEN,
				options.getHeader("Liferay-AI-Hub-Cell-On-Behalf-Of"));

			Http.Body body = options.getBody();

			JSONObject bodyJSONObject = JSONFactoryUtil.createJSONObject(
				body.getContent());

			Assert.assertEquals(
				"L_FIX_SPELLING_AND_GRAMMAR",
				bodyJSONObject.getString(
					"agentDefinitionExternalReferenceCode"));
			Assert.assertFalse(bodyJSONObject.getBoolean("asynchronous"));
			Assert.assertTrue(bodyJSONObject.has("context"));
		}
	}

	private Http _getHttp(List<Http.Options> optionsList) {
		return (Http)ProxyUtil.newProxyInstance(
			Http.class.getClassLoader(), new Class<?>[] {Http.class},
			(proxy, method, args) -> {
				if (!Objects.equals(method.getName(), "URLtoString")) {
					return null;
				}

				Http.Options options = (Http.Options)args[0];

				Http.Response response = options.getResponse();

				response.setResponseCode(200);

				if (StringUtil.endsWith(
						options.getLocation(),
						_SERVICE_URL + "/o/ai-hub/v1.0/agent-instances")) {

					optionsList.add(options);

					return JSONUtil.put(
						"output", _OUTPUT
					).toString();
				}

				if (StringUtil.endsWith(
						options.getLocation(),
						"/o/ai-hub-cell/v1.0/authorization-tokens")) {

					return JSONUtil.put(
						"accessToken", _ACCESS_TOKEN
					).put(
						"serviceURL", _SERVICE_URL
					).put(
						"userToken", _USER_TOKEN
					).toString();
				}

				return null;
			});
	}

	private NoticeableExecutorService _getNoticeableExecutorService(
		List<Callable<?>> callables) {

		return (NoticeableExecutorService)ProxyUtil.newProxyInstance(
			NoticeableExecutorService.class.getClassLoader(),
			new Class<?>[] {NoticeableExecutorService.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "submit")) {
					callables.add((Callable<?>)args[0]);
				}

				return null;
			});
	}

	private static final String _ACCESS_TOKEN = RandomTestUtil.randomString();

	private static final String _OUTPUT = RandomTestUtil.randomString();

	private static final String _SERVICE_URL = RandomTestUtil.randomString();

	private static final String _USER_TOKEN = RandomTestUtil.randomString();

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.runtime.internal.node.AIHubAgentNodeExecutor"
	)
	private NodeExecutor _nodeExecutor;

	private ServiceRegistration<WorkflowHandler<?>> _serviceRegistration;
	private WorkflowDefinition _workflowDefinition;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}