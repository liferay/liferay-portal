/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class ObjectEntryResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(ObjectEntryResourceTest.class);

		ObjectDefinition basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_basicWebContentObjectEntry = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			basicWebContentObjectDefinition.getObjectDefinitionId(), 0, null,
			Collections.singletonMap(
				"title_i18n",
				(Serializable)RandomTestUtil.randomLanguageIdStringMap()),
			ServiceContextTestUtil.getServiceContext());

		_projectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", TestPropsValues.getCompanyId());
		_projectObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());
		_taskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", TestPropsValues.getCompanyId());
		_taskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testPostProjectLinkObjectEntry() throws Exception {

		// Link basic web content in a project

		_testPostProjectLinkObjectEntry();

		// Link the same basic web content in a different project

		_testPostProjectLinkObjectEntry();
	}

	@Test
	public void testPostProjectObjectEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_PROJECT);

		Assert.assertEquals(
			409,
			HTTPTestUtil.invokeToHttpCode(
				null,
				_projectObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));

		JSONObject projectObjectEntryJSONObject = _postProjectObjectEntry();

		depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			projectObjectEntryJSONObject.getLong("scopeId"));

		Assert.assertEquals(DepotConstants.TYPE_PROJECT, depotEntry.getType());
	}

	@Test
	public void testPostTaskLinkObjectEntry() throws Exception {

		// Link basic web content in a task

		_testPostTaskLinkObjectEntry();

		// Link the same basic web content in a different task

		_testPostTaskLinkObjectEntry();
	}

	@Test
	public void testPostTaskObjectEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_PROJECT);
		JSONObject projectObjectEntryJSONObject = _postProjectObjectEntry();

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"r_cmpProjectToCMPTasks_c_cmpProjectId",
					projectObjectEntryJSONObject.getLong("id")
				).put(
					"title", RandomTestUtil.randomString()
				).toString(),
				_taskObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));
		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"r_cmpProjectToCMPTasks_c_cmpProjectERC",
					projectObjectEntryJSONObject.getString(
						"externalReferenceCode")
				).put(
					"title", RandomTestUtil.randomString()
				).toString(),
				_taskObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));

		JSONObject taskObjectEntryJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"r_cmpProjectToCMPTasks_c_cmpProjectERC",
				projectObjectEntryJSONObject.getString("externalReferenceCode")
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			_taskObjectDefinition.getRESTContextPath() + "/scopes/" +
				projectObjectEntryJSONObject.getLong("scopeId"),
			Http.Method.POST);

		Assert.assertEquals(
			projectObjectEntryJSONObject.getLong("id"),
			taskObjectEntryJSONObject.getLong(
				"r_cmpProjectToCMPTasks_c_cmpProjectId"));
		Assert.assertEquals(
			projectObjectEntryJSONObject.getLong("scopeId"),
			taskObjectEntryJSONObject.getLong("scopeId"));
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), type,
			ServiceContextTestUtil.getServiceContext());
	}

	private JSONObject _getBasicWebContentObjectEntryJSONObject() {
		return JSONUtil.put(
			"classExternalReferenceCode",
			_basicWebContentObjectEntry.getExternalReferenceCode()
		).put(
			"className", _basicWebContentObjectEntry.getModelClassName()
		).put(
			"groupExternalReferenceCode",
			() -> {
				Group group = _groupLocalService.getGroup(
					_basicWebContentObjectEntry.getGroupId());

				return group.getExternalReferenceCode();
			}
		);
	}

	private JSONObject _postProjectObjectEntry() throws Exception {
		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"title", RandomTestUtil.randomString()
			).toString(),
			_projectObjectDefinition.getRESTContextPath(), Http.Method.POST);
	}

	private JSONObject _postTaskObjectEntry() throws Exception {
		JSONObject projectObjectEntryJSONObject = _postProjectObjectEntry();

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"r_cmpProjectToCMPTasks_c_cmpProjectERC",
				projectObjectEntryJSONObject.getString("externalReferenceCode")
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			_taskObjectDefinition.getRESTContextPath() + "/scopes/" +
				projectObjectEntryJSONObject.getLong("scopeId"),
			Http.Method.POST);
	}

	private void _testPostProjectLinkObjectEntry() throws Exception {
		JSONObject bodyJSONObject = _getBasicWebContentObjectEntryJSONObject();

		JSONObject projectObjectEntryJSONObject = _postProjectObjectEntry();

		bodyJSONObject.put(
			"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
			projectObjectEntryJSONObject.getLong("id"));

		JSONObject projectLinkObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				bodyJSONObject.toString(),
				_projectLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					projectObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST);

		Assert.assertEquals(
			bodyJSONObject.getString("classExternalReferenceCode"),
			projectLinkObjectEntryJSONObject.getString(
				"classExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getString("className"),
			projectLinkObjectEntryJSONObject.getString("className"));
		Assert.assertEquals(
			bodyJSONObject.getString("groupExternalReferenceCode"),
			projectLinkObjectEntryJSONObject.getString(
				"groupExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getLong(
				"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"),
			projectLinkObjectEntryJSONObject.getLong(
				"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				bodyJSONObject.toString(),
				_projectLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					projectObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST));
	}

	private void _testPostTaskLinkObjectEntry() throws Exception {
		JSONObject bodyJSONObject = _getBasicWebContentObjectEntryJSONObject();

		JSONObject taskObjectEntryJSONObject = _postTaskObjectEntry();

		bodyJSONObject.put(
			"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
			taskObjectEntryJSONObject.getLong("id"));

		JSONObject taskLinkObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				bodyJSONObject.toString(),
				_taskLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					taskObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST);

		Assert.assertEquals(
			bodyJSONObject.getString("classExternalReferenceCode"),
			taskLinkObjectEntryJSONObject.getString(
				"classExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getString("className"),
			taskLinkObjectEntryJSONObject.getString("className"));
		Assert.assertEquals(
			bodyJSONObject.getString("groupExternalReferenceCode"),
			taskLinkObjectEntryJSONObject.getString(
				"groupExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getLong("r_cmpTaskToCMPTaskLinks_c_cmpTaskId"),
			taskLinkObjectEntryJSONObject.getLong(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId"));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				bodyJSONObject.toString(),
				_taskLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					taskObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST));
	}

	private ObjectEntry _basicWebContentObjectEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectDefinition _projectLinkObjectDefinition;
	private ObjectDefinition _projectObjectDefinition;
	private ObjectDefinition _taskLinkObjectDefinition;
	private ObjectDefinition _taskObjectDefinition;

}