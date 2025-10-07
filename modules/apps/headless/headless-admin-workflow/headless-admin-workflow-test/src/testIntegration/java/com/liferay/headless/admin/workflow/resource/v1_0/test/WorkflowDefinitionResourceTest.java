/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.workflow.client.dto.v1_0.Node;
import com.liferay.headless.admin.workflow.client.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowDefinitionSerDes;
import com.liferay.headless.admin.workflow.resource.v1_0.test.util.WorkflowDefinitionTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.kaleo.definition.util.WorkflowDefinitionContentUtil;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class WorkflowDefinitionResourceTest
	extends BaseWorkflowDefinitionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseWorkflowDefinitionResourceTestCase.setUpClass();

		_workflowDefinition =
			_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
				TestPropsValues.getCompanyId(), "Single Approver");

		_undeployWorkflowDefinition(
			_workflowDefinition.getName(), _workflowDefinition.getVersion());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		String content = _workflowDefinition.getContent();

		_workflowDefinitionManager.deployWorkflowDefinition(
			null, _workflowDefinition.getCompanyId(),
			_workflowDefinition.getUserId(), _workflowDefinition.getTitle(),
			_workflowDefinition.getName(), content.getBytes());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (WorkflowDefinition workflowDefinition :
				_workflowDefinitions.values()) {

			_undeployWorkflowDefinition(
				workflowDefinition.getName(),
				GetterUtil.getInteger(workflowDefinition.getVersion()));
		}

		_workflowDefinitions.clear();
	}

	@Override
	@Test
	public void testDeleteWorkflowDefinitionUndeploy() throws Exception {
		WorkflowDefinition workflowDefinition =
			testPostWorkflowDefinitionSave_addWorkflowDefinition(
				randomWorkflowDefinition());

		assertHttpResponseStatusCode(
			204,
			workflowDefinitionResource.
				deleteWorkflowDefinitionUndeployHttpResponse(
					workflowDefinition.getName(),
					workflowDefinition.getVersion()));

		_workflowDefinitions.remove(workflowDefinition.getName());
	}

	@Override
	@Test
	public void testGetWorkflowDefinitionByName() throws Exception {
		WorkflowDefinition workflowDefinition =
			testPostWorkflowDefinitionDeploy_addWorkflowDefinition(
				randomWorkflowDefinition());

		assertHttpResponseStatusCode(
			200,
			workflowDefinitionResource.getWorkflowDefinitionByNameHttpResponse(
				workflowDefinition.getName(), null, null));
		assertHttpResponseStatusCode(
			200,
			workflowDefinitionResource.getWorkflowDefinitionByNameHttpResponse(
				workflowDefinition.getName(), null, 1));
		assertHttpResponseStatusCode(
			404,
			workflowDefinitionResource.getWorkflowDefinitionByNameHttpResponse(
				workflowDefinition.getName(), null, 2));

		testPostWorkflowDefinitionDeploy_addWorkflowDefinition(
			workflowDefinition);

		assertHttpResponseStatusCode(
			200,
			workflowDefinitionResource.getWorkflowDefinitionByNameHttpResponse(
				workflowDefinition.getName(), null, 2));

		WorkflowDefinition latestWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinitionByName(
				workflowDefinition.getName(), null, null);

		Assert.assertEquals(
			workflowDefinition.getDateCreated(),
			latestWorkflowDefinition.getDateCreated());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteWorkflowDefinitionUndeploy() throws Exception {
		super.testGraphQLDeleteWorkflowDefinitionUndeploy();
	}

	@Override
	@Test
	public void testGraphQLGetWorkflowDefinitionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"workflowDefinitions",
			HashMapBuilder.<String, Object>put(
				"page", 1
			).put(
				"pageSize", 2
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject workflowDefinitionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/workflowDefinitions");

		Assert.assertEquals(0, workflowDefinitionsJSONObject.get("totalCount"));

		WorkflowDefinition workflowDefinition1 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());
		WorkflowDefinition workflowDefinition2 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		workflowDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/workflowDefinitions");

		Assert.assertEquals(2, workflowDefinitionsJSONObject.get("totalCount"));

		assertEqualsIgnoringOrder(
			Arrays.asList(workflowDefinition1, workflowDefinition2),
			Arrays.asList(
				WorkflowDefinitionSerDes.toDTOs(
					workflowDefinitionsJSONObject.getString("items"))));
	}

	@Override
	@Test
	public void testPostWorkflowDefinition() throws Exception {
		super.testPostWorkflowDefinition();

		// The value of "content" is JSON

		JSONObject jsonObject = _getWorkflowDefinitionJSONObject(
			"workflow-definition.json");

		JSONObject workflowDefinitionJSONObject = _postWorkflowDefinition(
			jsonObject);

		JSONAssert.assertEquals(
			jsonObject.getJSONObject(
				"content"
			).toString(),
			WorkflowDefinitionContentUtil.toJSON(
				workflowDefinitionJSONObject.getString("content")),
			true);

		// The value of "content" is a JSON string

		jsonObject = _getWorkflowDefinitionJSONObject(
			"workflow-definition-json-string.json");

		workflowDefinitionJSONObject = _postWorkflowDefinition(jsonObject);

		JSONAssert.assertEquals(
			jsonObject.getString("content"),
			WorkflowDefinitionContentUtil.toJSON(
				workflowDefinitionJSONObject.getString("content")),
			true);
	}

	@Override
	@Test
	public void testPostWorkflowDefinitionSave() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		randomWorkflowDefinition.setNodes(new Node[0]);
		randomWorkflowDefinition.setTransitions(new Transition[0]);

		WorkflowDefinition postWorkflowDefinition =
			testPostWorkflowDefinitionSave_addWorkflowDefinition(
				randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, postWorkflowDefinition);
		assertValid(postWorkflowDefinition);
	}

	@Override
	@Test
	public void testPutWorkflowDefinition() throws Exception {
		WorkflowDefinition postWorkflowDefinition =
			testPutWorkflowDefinition_addWorkflowDefinition();

		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		randomWorkflowDefinition.setName(postWorkflowDefinition.getName());
		randomWorkflowDefinition.setVersion("2");

		WorkflowDefinition putWorkflowDefinition =
			workflowDefinitionResource.putWorkflowDefinition(
				postWorkflowDefinition.getId(), randomWorkflowDefinition);

		_workflowDefinitions.put(
			putWorkflowDefinition.getName(), putWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, putWorkflowDefinition);
		assertValid(putWorkflowDefinition);

		WorkflowDefinition getWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinition(
				putWorkflowDefinition.getId());

		assertEquals(randomWorkflowDefinition, getWorkflowDefinition);
		assertValid(getWorkflowDefinition);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "name", "nodes", "title", "title_i18n", "transitions",
			"version"
		};
	}

	@Override
	protected WorkflowDefinition randomWorkflowDefinition() throws Exception {
		WorkflowDefinition workflowDefinition =
			super.randomWorkflowDefinition();

		workflowDefinition.setActive(true);
		workflowDefinition.setContent(
			WorkflowDefinitionTestUtil.getContent(
				workflowDefinition.getDescription(), "workflow-definition.xml",
				workflowDefinition.getName()));
		workflowDefinition.setNodes(
			new Node[] {
				new Node() {
					{
						label = "Approved";
						name = "approved";
						type = Type.TERMINAL_STATE;
					}
				},
				new Node() {
					{
						label = "Created";
						name = "created";
						type = Type.INITIAL_STATE;
					}
				},
				new Node() {
					{
						label = "Review";
						name = "review";
						type = Type.TASK;
					}
				},
				new Node() {
					{
						label = "Update";
						name = "update";
						type = Type.TASK;
					}
				}
			});
		workflowDefinition.setTitle_i18n(
			HashMapBuilder.put(
				LanguageUtil.getLanguageId(LocaleUtil.US),
				workflowDefinition.getTitle()
			).build());
		workflowDefinition.setTransitions(
			new Transition[] {
				new Transition() {
					{
						label = "Review";
						name = "review";
						sourceNodeName = "created";
						targetNodeName = "review";
					}
				},
				new Transition() {
					{
						label = "Approve";
						name = "approve";
						sourceNodeName = "review";
						targetNodeName = "approved";
					}
				},
				new Transition() {
					{
						label = "Reject";
						name = "reject";
						sourceNodeName = "review";
						targetNodeName = "update";
					}
				},
				new Transition() {
					{
						label = "Resubmit";
						name = "resubmit";
						sourceNodeName = "update";
						targetNodeName = "review";
					}
				}
			});
		workflowDefinition.setVersion("1");

		return workflowDefinition;
	}

	@Override
	protected WorkflowDefinition
			testDeleteWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGetWorkflowDefinitionsPage_addWorkflowDefinition(
			randomWorkflowDefinition());
	}

	@Override
	protected WorkflowDefinition
			testGetWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGetWorkflowDefinitionsPage_addWorkflowDefinition(
			randomWorkflowDefinition());
	}

	@Override
	protected WorkflowDefinition
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		workflowDefinition =
			workflowDefinitionResource.postWorkflowDefinitionDeploy(
				workflowDefinition);

		_workflowDefinitions.put(
			workflowDefinition.getName(), workflowDefinition);

		return workflowDefinition;
	}

	@Override
	protected WorkflowDefinition
			testGraphQLWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGetWorkflowDefinition_addWorkflowDefinition();
	}

	@Override
	protected WorkflowDefinition
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		workflowDefinition =
			workflowDefinitionResource.postWorkflowDefinitionDeploy(
				workflowDefinition);

		_workflowDefinitions.put(
			workflowDefinition.getName(), workflowDefinition);

		return workflowDefinition;
	}

	@Override
	protected WorkflowDefinition
			testPostWorkflowDefinition_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		return testGetWorkflowDefinitionsPage_addWorkflowDefinition(
			workflowDefinition);
	}

	@Override
	protected WorkflowDefinition
			testPostWorkflowDefinitionDeploy_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		return testGetWorkflowDefinitionsPage_addWorkflowDefinition(
			workflowDefinition);
	}

	@Override
	protected WorkflowDefinition
			testPostWorkflowDefinitionSave_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		workflowDefinition.setActive(false);

		workflowDefinition =
			workflowDefinitionResource.postWorkflowDefinitionSave(
				workflowDefinition);

		_workflowDefinitions.put(
			workflowDefinition.getName(), workflowDefinition);

		return workflowDefinition;
	}

	@Override
	protected WorkflowDefinition
			testPostWorkflowDefinitionUpdateActive_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		return testGetWorkflowDefinitionsPage_addWorkflowDefinition(
			workflowDefinition);
	}

	@Override
	protected WorkflowDefinition
			testPutWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGetWorkflowDefinition_addWorkflowDefinition();
	}

	private static void _undeployWorkflowDefinition(
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws Exception {

		int workflowDefinitionsCount =
			_workflowDefinitionManager.getWorkflowDefinitionsCount(
				TestPropsValues.getCompanyId(), workflowDefinitionName);

		if (workflowDefinitionsCount == 0) {
			return;
		}

		_workflowDefinitionManager.updateActive(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowDefinitionName, workflowDefinitionVersion, false);

		_workflowDefinitionManager.undeployWorkflowDefinition(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowDefinitionName, workflowDefinitionVersion);
	}

	private JSONObject _getWorkflowDefinitionJSONObject(String fileName)
		throws Exception {

		return JSONFactoryUtil.createJSONObject(
			StreamUtil.toString(
				getClass().getResourceAsStream(
					StringBundler.concat(
						"/com/liferay/headless/admin/workflow/resource/v1_0",
						"/test/util/dependencies/", fileName))));
	}

	private JSONObject _postWorkflowDefinition(
			JSONObject workflowDefinitionJSONObject)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			workflowDefinitionJSONObject.toString(),
			"headless-admin-workflow/v1.0/workflow-definitions",
			Http.Method.POST);
	}

	private static com.liferay.portal.kernel.workflow.WorkflowDefinition
		_workflowDefinition;

	@Inject
	private static WorkflowDefinitionManager _workflowDefinitionManager;

	private final Map<String, WorkflowDefinition> _workflowDefinitions =
		new HashMap<>();

}