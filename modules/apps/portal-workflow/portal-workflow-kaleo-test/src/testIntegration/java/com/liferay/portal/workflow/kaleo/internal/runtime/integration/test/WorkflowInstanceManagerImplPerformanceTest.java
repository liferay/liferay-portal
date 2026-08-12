/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.runtime.integration.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Closeable;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Sousa
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class WorkflowInstanceManagerImplPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(Validator.isNull(System.getenv("JENKINS_HOME")));
	}

	@Test
	public void testStartWorkflowInstance() throws Exception {
		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Properties properties = PropertiesUtil.load(
			WorkflowInstanceManagerImplPerformanceTest.class.
				getResourceAsStream(
					"dependencies/workflow-instance-performance.properties"),
			"UTF-8");

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0],
			ServiceContextTestUtil.getServiceContext());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				_createWorkflowDefinitionBytes(
					GetterUtil.getInteger(
						properties.getProperty("workflow.nodes.count"))),
				TestPropsValues.getCompanyId(), null,
				accountEntry.getAccountEntryGroupId(),
				RandomTestUtil.randomString(),
				WorkflowDefinitionConstants.SCOPE_AI, false,
				RandomTestUtil.randomString(), TestPropsValues.getUserId());

		int workflowInstancesThreadCount = GetterUtil.getInteger(
			properties.getProperty("workflow.instances.thread.count"));

		ExecutorService executorService = Executors.newFixedThreadPool(
			workflowInstancesThreadCount);

		int workflowInstancesCount = GetterUtil.getInteger(
			properties.getProperty("workflow.instances.count"));

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		List<Future<Void>> futures = new ArrayList<>(workflowInstancesCount);

		try (Closeable closeable = new PerformanceTimer(
				GetterUtil.getInteger(
					properties.getProperty(
						"workflow.instances.start.max.time")),
				StringBundler.concat(
					"Start ", workflowInstancesCount,
					" workflow instances in parallel across ",
					workflowInstancesThreadCount, " threads"))) {

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			for (int i = 0; i < workflowInstancesCount; i++) {
				futures.add(
					executorService.submit(
						new CompanyInheritableThreadLocalCallable<>(
							() -> {
								PrincipalThreadLocal.setName(user.getUserId());

								PermissionThreadLocal.setPermissionChecker(
									permissionChecker);

								try {
									_workflowInstanceManager.
										startWorkflowInstance(
											TestPropsValues.getCompanyId(),
											accountEntry.
												getAccountEntryGroupId(),
											TestPropsValues.getUserId(),
											workflowDefinition.getName(),
											workflowDefinition.getVersion(),
											null,
											HashMapBuilder.
												<String, Serializable>put(
													WorkflowConstants.
														CONTEXT_SERVICE_CONTEXT,
													new ServiceContext()
												).build(),
											true);
								}
								finally {
									PrincipalThreadLocal.setName(originalName);

									PermissionThreadLocal.setPermissionChecker(
										originalPermissionChecker);
								}

								return null;
							})));
			}

			for (Future<Void> future : futures) {
				future.get();
			}
		}

		executorService.shutdown();

		SystemProperties.set("liferay.mode", liferayMode);
	}

	private void _changeStartWorkflowNodeTarget(
		JSONArray workflowDefinitionChildNodesJSONArray) {

		JSONObject startWorkflowNodeJSONObject =
			workflowDefinitionChildNodesJSONArray.getJSONObject(2);

		JSONArray startWorkflowNodeChildNodesJSONArray =
			startWorkflowNodeJSONObject.getJSONArray("#child-nodes");

		for (int i = 0; i < startWorkflowNodeChildNodesJSONArray.length();
			 i++) {

			JSONObject startWorkflowNodeChildNodeJSONObject =
				startWorkflowNodeChildNodesJSONArray.getJSONObject(i);

			if (!Objects.equals(
					startWorkflowNodeChildNodeJSONObject.getString("#tag-name"),
					"transitions")) {

				continue;
			}

			JSONArray transitionsChildNodesJSONArray =
				startWorkflowNodeChildNodeJSONObject.getJSONArray(
					"#child-nodes");

			JSONObject transitionJSONObject =
				transitionsChildNodesJSONArray.getJSONObject(0);

			JSONArray transitionChildNodesJSONArray =
				transitionJSONObject.getJSONArray("#child-nodes");

			for (int j = 0; j < transitionChildNodesJSONArray.length(); j++) {
				JSONObject transitionChildNodeJSONObject =
					transitionChildNodesJSONArray.getJSONObject(j);

				if (Objects.equals(
						transitionChildNodeJSONObject.getString("#tag-name"),
						"target")) {

					transitionChildNodeJSONObject.put(
						"#value", "workflowNode1");

					return;
				}
			}
		}
	}

	private byte[] _createWorkflowDefinitionBytes(int workflowNodeCount)
		throws Exception {

		ClassLoader classLoader =
			WorkflowInstanceManagerImplPerformanceTest.class.getClassLoader();

		byte[] bytes = FileUtil.getBytes(
			classLoader.getResourceAsStream(
				"com/liferay/portal/workflow/kaleo/dependencies" +
					"/minimal-workflow-definition.json"));

		if (workflowNodeCount <= 0) {
			return bytes;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			new String(bytes));

		JSONArray jsonArray = jsonObject.getJSONArray("#child-nodes");

		_changeStartWorkflowNodeTarget(jsonArray);

		for (int i = 1; i <= workflowNodeCount; i++) {
			String targetWorkflowNodeName = "workflowNode" + (i + 1);

			if (i == workflowNodeCount) {
				targetWorkflowNodeName = "end";
			}

			jsonArray.put(
				_createWorkflowNodeJSONObject(
					"workflowNode" + i, targetWorkflowNodeName));
		}

		String json = jsonObject.toString();

		return json.getBytes();
	}

	private JSONObject _createWorkflowNodeJSONObject(
		String name, String targetNodeName) {

		return JSONUtil.put(
			"#child-nodes",
			JSONUtil.putAll(
				JSONUtil.put(
					"#tag-name", "name"
				).put(
					"#value", name
				),
				JSONUtil.put(
					"#child-nodes",
					JSONUtil.putAll(
						JSONUtil.put(
							"#child-nodes",
							JSONUtil.putAll(
								JSONUtil.put(
									"#tag-name", "name"
								).put(
									"#value", "next"
								),
								JSONUtil.put(
									"#tag-name", "target"
								).put(
									"#value", targetNodeName
								),
								JSONUtil.put(
									"#tag-name", "default"
								).put(
									"#value", "true"
								))
						).put(
							"#tag-name", "transition"
						))
				).put(
					"#tag-name", "transitions"
				))
		).put(
			"#tag-name", "state"
		);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}