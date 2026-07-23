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
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
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

		_cmpProjectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", TestPropsValues.getCompanyId());
		_cmpProjectObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());
		_cmpTaskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", TestPropsValues.getCompanyId());
		_cmpTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());

		ObjectDefinition cmsBasicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_cmsBasicWebContentObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				depotEntry.getGroupId(), TestPropsValues.getUserId(),
				cmsBasicWebContentObjectDefinition.getObjectDefinitionId(), 0,
				null,
				Collections.singletonMap(
					"title_i18n",
					(Serializable)RandomTestUtil.randomLanguageIdStringMap()),
				ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testPostCMPProjectLinkObjectEntry() throws Exception {

		// Link basic web content in a project

		_testPostCMPProjectLinkObjectEntry();

		// Link the same basic web content in a different project

		_testPostCMPProjectLinkObjectEntry();
	}

	@Test
	public void testPostCMPProjectObjectEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_PROJECT);

		Assert.assertEquals(
			409,
			HTTPTestUtil.invokeToHttpCode(
				null,
				_cmpProjectObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));

		JSONObject cmpProjectObjectEntryJSONObject =
			_postCMPProjectObjectEntry();

		depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			cmpProjectObjectEntryJSONObject.getLong("scopeId"));

		Assert.assertEquals(DepotConstants.TYPE_PROJECT, depotEntry.getType());
	}

	@Test
	public void testPostCMPTaskLinkObjectEntry() throws Exception {

		// Link basic web content in a task

		_testPostCMPTaskLinkObjectEntry();

		// Link the same basic web content in a different task

		_testPostCMPTaskLinkObjectEntry();
	}

	@Test
	public void testPostCMPTaskObjectEntry() throws Exception {
		JSONObject cmpProjectObjectEntryJSONObject =
			_postCMPProjectObjectEntry();
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_PROJECT);

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"r_cmpProjectToCMPTasks_c_cmpProjectId",
					cmpProjectObjectEntryJSONObject.getLong("id")
				).put(
					"title", RandomTestUtil.randomString()
				).toString(),
				_cmpTaskObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));
		Assert.assertEquals(
			404,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					"r_cmpProjectToCMPTasks_c_cmpProjectERC",
					cmpProjectObjectEntryJSONObject.getString(
						"externalReferenceCode")
				).put(
					"title", RandomTestUtil.randomString()
				).toString(),
				_cmpTaskObjectDefinition.getRESTContextPath() + "/scopes/" +
					depotEntry.getGroupId(),
				Http.Method.POST));

		JSONObject cmpTaskObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"r_cmpProjectToCMPTasks_c_cmpProjectERC",
					cmpProjectObjectEntryJSONObject.getString(
						"externalReferenceCode")
				).put(
					"title", RandomTestUtil.randomString()
				).toString(),
				_cmpTaskObjectDefinition.getRESTContextPath() + "/scopes/" +
					cmpProjectObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST);

		Assert.assertEquals(
			cmpProjectObjectEntryJSONObject.getLong("id"),
			cmpTaskObjectEntryJSONObject.getLong(
				"r_cmpProjectToCMPTasks_c_cmpProjectId"));
		Assert.assertEquals(
			cmpProjectObjectEntryJSONObject.getLong("scopeId"),
			cmpTaskObjectEntryJSONObject.getLong("scopeId"));
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), type,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private JSONObject _getCMSBasicWebContentObjectEntryJSONObject() {
		return JSONUtil.put(
			"classExternalReferenceCode",
			_cmsBasicWebContentObjectEntry.getExternalReferenceCode()
		).put(
			"className", _cmsBasicWebContentObjectEntry.getModelClassName()
		).put(
			"groupExternalReferenceCode",
			() -> {
				Group group = _groupLocalService.getGroup(
					_cmsBasicWebContentObjectEntry.getGroupId());

				return group.getExternalReferenceCode();
			}
		);
	}

	private JSONObject _postCMPProjectObjectEntry() throws Exception {
		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"title", RandomTestUtil.randomString()
			).toString(),
			_cmpProjectObjectDefinition.getRESTContextPath(), Http.Method.POST);
	}

	private JSONObject _postCMPTaskObjectEntry() throws Exception {
		JSONObject cmpProjectObjectEntryJSONObject =
			_postCMPProjectObjectEntry();

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"r_cmpProjectToCMPTasks_c_cmpProjectERC",
				cmpProjectObjectEntryJSONObject.getString(
					"externalReferenceCode")
			).put(
				"title", RandomTestUtil.randomString()
			).toString(),
			_cmpTaskObjectDefinition.getRESTContextPath() + "/scopes/" +
				cmpProjectObjectEntryJSONObject.getLong("scopeId"),
			Http.Method.POST);
	}

	private void _testPostCMPProjectLinkObjectEntry() throws Exception {
		JSONObject bodyJSONObject =
			_getCMSBasicWebContentObjectEntryJSONObject();

		JSONObject cmpProjectObjectEntryJSONObject =
			_postCMPProjectObjectEntry();

		bodyJSONObject.put(
			"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
			cmpProjectObjectEntryJSONObject.getLong("id"));

		JSONObject cmpProjectLinkObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				bodyJSONObject.toString(),
				_cmpProjectLinkObjectDefinition.getRESTContextPath() +
					"/scopes/" +
						cmpProjectObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST);

		Assert.assertEquals(
			bodyJSONObject.getString("classExternalReferenceCode"),
			cmpProjectLinkObjectEntryJSONObject.getString(
				"classExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getString("className"),
			cmpProjectLinkObjectEntryJSONObject.getString("className"));
		Assert.assertEquals(
			bodyJSONObject.getString("groupExternalReferenceCode"),
			cmpProjectLinkObjectEntryJSONObject.getString(
				"groupExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getLong(
				"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"),
			cmpProjectLinkObjectEntryJSONObject.getLong(
				"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				bodyJSONObject.toString(),
				_cmpProjectLinkObjectDefinition.getRESTContextPath() +
					"/scopes/" +
						cmpProjectObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST));
	}

	private void _testPostCMPTaskLinkObjectEntry() throws Exception {
		JSONObject bodyJSONObject =
			_getCMSBasicWebContentObjectEntryJSONObject();

		JSONObject cmpTaskObjectEntryJSONObject = _postCMPTaskObjectEntry();

		bodyJSONObject.put(
			"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
			cmpTaskObjectEntryJSONObject.getLong("id"));

		JSONObject cmpTaskLinkObjectEntryJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				bodyJSONObject.toString(),
				_cmpTaskLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					cmpTaskObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST);

		Assert.assertEquals(
			bodyJSONObject.getString("classExternalReferenceCode"),
			cmpTaskLinkObjectEntryJSONObject.getString(
				"classExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getString("className"),
			cmpTaskLinkObjectEntryJSONObject.getString("className"));
		Assert.assertEquals(
			bodyJSONObject.getString("groupExternalReferenceCode"),
			cmpTaskLinkObjectEntryJSONObject.getString(
				"groupExternalReferenceCode"));
		Assert.assertEquals(
			bodyJSONObject.getLong("r_cmpTaskToCMPTaskLinks_c_cmpTaskId"),
			cmpTaskLinkObjectEntryJSONObject.getLong(
				"r_cmpTaskToCMPTaskLinks_c_cmpTaskId"));

		Assert.assertEquals(
			400,
			HTTPTestUtil.invokeToHttpCode(
				bodyJSONObject.toString(),
				_cmpTaskLinkObjectDefinition.getRESTContextPath() + "/scopes/" +
					cmpTaskObjectEntryJSONObject.getLong("scopeId"),
				Http.Method.POST));
	}

	private ObjectDefinition _cmpProjectLinkObjectDefinition;
	private ObjectDefinition _cmpProjectObjectDefinition;
	private ObjectDefinition _cmpTaskLinkObjectDefinition;
	private ObjectDefinition _cmpTaskObjectDefinition;
	private ObjectEntry _cmsBasicWebContentObjectEntry;

	@DeleteAfterTestRun
	private List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}