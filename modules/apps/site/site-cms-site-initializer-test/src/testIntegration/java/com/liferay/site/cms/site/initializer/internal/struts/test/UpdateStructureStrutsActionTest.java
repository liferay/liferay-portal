/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class UpdateStructureStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-77022")
	public void testExecute() throws Exception {
		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition1 =
				ObjectDefinitionTestUtil.publishObjectDefinition();
		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition2 =
				ObjectDefinitionTestUtil.publishObjectDefinition();

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship =
				_objectRelationshipLocalService.addObjectRelationship(
					null, TestPropsValues.getUserId(),
					serviceBuilderObjectDefinition1.getObjectDefinitionId(),
					serviceBuilderObjectDefinition2.getObjectDefinitionId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null,
					true,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		Assert.assertTrue(serviceBuilderObjectRelationship.isEdge());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"deletedObjectRelationships",
			JSONUtil.putAll(
				JSONUtil.put(
					"objectDefinitionERC",
					serviceBuilderObjectDefinition1.getExternalReferenceCode()
				).put(
					"objectRelationshipERC",
					serviceBuilderObjectRelationship.getExternalReferenceCode()
				)
			).toString());

		ObjectDefinition dtoObjectDefinition = _getObjectDefinition(
			serviceBuilderObjectDefinition1.getExternalReferenceCode());

		mockHttpServletRequest.setParameter(
			"objectDefinition", dtoObjectDefinition.toString());

		mockHttpServletRequest.setParameter("objectRelationships", "[]");
		mockHttpServletRequest.setParameter(
			"repeatableGroupObjectDefinitions", "[]");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_updateStructureStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		Assert.assertNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship.getObjectRelationshipId()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			serviceBuilderObjectDefinition1.getObjectDefinitionId());
		_objectDefinitionLocalService.deleteObjectDefinition(
			serviceBuilderObjectDefinition2.getObjectDefinitionId());
	}

	@Test
	@TestInfo("LPD-99742")
	public void testExecuteDeletesObjectRelationships() throws Exception {
		_serviceBuilderObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_serviceBuilderObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_serviceBuilderObjectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship1 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_serviceBuilderObjectDefinition1,
					_serviceBuilderObjectDefinition3,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
					StringUtil.randomId(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship2 = _addEdgeObjectRelationship(
				_serviceBuilderObjectDefinition1,
				_serviceBuilderObjectDefinition3);

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship3 = _addEdgeObjectRelationship(
				_serviceBuilderObjectDefinition3,
				_serviceBuilderObjectDefinition2);

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship4 =
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_serviceBuilderObjectDefinition1,
					_serviceBuilderObjectDefinition3,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
					StringUtil.randomId(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		String name = StringUtil.randomId();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				_serviceBuilderObjectDefinition3, TestPropsValues.getUser());

		mockHttpServletRequest.setParameter(
			"objectRelationships",
			JSONUtil.putAll(
				JSONUtil.put(
					"deletionType",
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE
				).put(
					"name", name
				).put(
					"objectDefinitionExternalReferenceCode1",
					_serviceBuilderObjectDefinition2.getExternalReferenceCode()
				).put(
					"objectDefinitionExternalReferenceCode2",
					_serviceBuilderObjectDefinition3.getExternalReferenceCode()
				).put(
					"type", ObjectRelationshipConstants.TYPE_ONE_TO_MANY
				)
			).toString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_updateStructureStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		Assert.assertNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship1.getObjectRelationshipId()));

		Assert.assertNotNull(
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectDefinitionId(
					_serviceBuilderObjectDefinition3.getObjectDefinitionId(),
					name));
		Assert.assertNotNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship2.getObjectRelationshipId()));
		Assert.assertNotNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship4.getObjectRelationshipId()));
		Assert.assertNotNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship3.getObjectRelationshipId()));

		_deleteEdgeObjectRelationship(serviceBuilderObjectRelationship2);
		_deleteEdgeObjectRelationship(serviceBuilderObjectRelationship3);
	}

	@Test
	@TestInfo("LPP-65252")
	public void testExecuteDeletesObjectRelationshipsFromRepeatableObjectDefinitions()
		throws Exception {

		_serviceBuilderObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_serviceBuilderObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_serviceBuilderObjectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		String externalReferenceCode = StringUtil.randomId();
		String name = StringUtil.randomId();

		_objectRelationshipLocalService.addObjectRelationship(
			externalReferenceCode, TestPropsValues.getUserId(),
			_serviceBuilderObjectDefinition3.getObjectDefinitionId(),
			_serviceBuilderObjectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, null, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				_serviceBuilderObjectDefinition1, TestPropsValues.getUser());

		mockHttpServletRequest.setParameter(
			"objectRelationships",
			JSONUtil.putAll(
				JSONUtil.put(
					"deletionType",
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"name", name
				).put(
					"objectDefinitionExternalReferenceCode1",
					_serviceBuilderObjectDefinition3.getExternalReferenceCode()
				).put(
					"objectDefinitionExternalReferenceCode2",
					_serviceBuilderObjectDefinition2.getExternalReferenceCode()
				).put(
					"type", ObjectRelationshipConstants.TYPE_ONE_TO_MANY
				)
			).toString());
		mockHttpServletRequest.setParameter(
			"repeatableGroupObjectDefinitions",
			JSONUtil.putAll(
				_jsonFactory.createJSONObject(
					String.valueOf(
						_getObjectDefinition(
							_serviceBuilderObjectDefinition2.
								getExternalReferenceCode())))
			).toString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_updateStructureStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectDefinitionId(
						_serviceBuilderObjectDefinition3.
							getObjectDefinitionId(),
						name);

		Assert.assertEquals(
			_serviceBuilderObjectDefinition2.getObjectDefinitionId(),
			serviceBuilderObjectRelationship.getObjectDefinitionId2());
	}

	@Test
	@TestInfo("LPD-92696")
	public void testExecuteDoesNotDeleteObjectRelationships() throws Exception {
		_serviceBuilderObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_serviceBuilderObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship =
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_serviceBuilderObjectDefinition1,
					_serviceBuilderObjectDefinition2);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_updateStructureStrutsAction.execute(
			_getMockHttpServletRequest(
				_serviceBuilderObjectDefinition1, TestPropsValues.getUser()),
			mockHttpServletResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(jsonObject.toString(), 0, jsonObject.length());

		Assert.assertNotNull(
			_objectFieldLocalService.fetchObjectField(
				serviceBuilderObjectRelationship.getObjectFieldId2()));
		Assert.assertNotNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship.getObjectRelationshipId()));
	}

	@Test
	@TestInfo("LPD-102138")
	public void testExecuteDoesNotDeleteUnauthorizedObjectRelationships()
		throws Exception {

		_serviceBuilderObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_serviceBuilderObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_serviceBuilderObjectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship = _addEdgeObjectRelationship(
				_serviceBuilderObjectDefinition2,
				_serviceBuilderObjectDefinition3);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			com.liferay.object.model.ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				_serviceBuilderObjectDefinition1.getObjectDefinitionId()),
			_role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_serviceBuilderObjectDefinition1, _user);

		mockHttpServletRequest.setParameter(
			"deletedObjectRelationships",
			JSONUtil.putAll(
				JSONUtil.put(
					"objectDefinitionERC",
					_serviceBuilderObjectDefinition2.getExternalReferenceCode()
				).put(
					"objectRelationshipERC",
					serviceBuilderObjectRelationship.getExternalReferenceCode()
				)
			).toString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_updateStructureStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			jsonObject.toString(), "PrincipalException.MustHavePermission",
			jsonObject.getString("type"));

		Assert.assertNotNull(
			_objectRelationshipLocalService.fetchObjectRelationship(
				serviceBuilderObjectRelationship.getObjectRelationshipId()));

		_deleteEdgeObjectRelationship(serviceBuilderObjectRelationship);
	}

	private com.liferay.object.model.ObjectRelationship
			_addEdgeObjectRelationship(
				com.liferay.object.model.ObjectDefinition
					serviceBuilderObjectDefinition1,
				com.liferay.object.model.ObjectDefinition
					serviceBuilderObjectDefinition2)
		throws Exception {

		com.liferay.object.model.ObjectRelationship
			serviceBuilderObjectRelationship =
				_objectRelationshipLocalService.addObjectRelationship(
					null, TestPropsValues.getUserId(),
					serviceBuilderObjectDefinition1.getObjectDefinitionId(),
					serviceBuilderObjectDefinition2.getObjectDefinitionId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null,
					true,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		Assert.assertTrue(serviceBuilderObjectRelationship.isEdge());

		return serviceBuilderObjectRelationship;
	}

	private void _deleteEdgeObjectRelationship(
			com.liferay.object.model.ObjectRelationship
				serviceBuilderObjectRelationship)
		throws Exception {

		com.liferay.object.model.ObjectRelationship
			updatedServiceBuilderObjectRelationship =
				_objectRelationshipLocalService.updateObjectRelationship(
					serviceBuilderObjectRelationship.getExternalReferenceCode(),
					serviceBuilderObjectRelationship.getObjectRelationshipId(),
					serviceBuilderObjectRelationship.
						getParameterObjectFieldId(),
					serviceBuilderObjectRelationship.getDeletionType(), null,
					false, serviceBuilderObjectRelationship.getLabelMap(),
					null);

		_objectRelationshipLocalService.deleteObjectRelationship(
			updatedServiceBuilderObjectRelationship);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition,
			User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"objectDefinition",
			_getObjectDefinitionJSON(
				serviceBuilderObjectDefinition.getObjectDefinitionId()));

		return mockHttpServletRequest;
	}

	private ObjectDefinition _getObjectDefinition(String externalReferenceCode)
		throws Exception {

		ObjectDefinitionResource objectDefinitionResource =
			_objectDefinitionResourceFactory.create(
			).user(
				TestPropsValues.getUser()
			).build();

		ObjectDefinition objectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode);

		objectDefinition.setObjectRelationships((ObjectRelationship[])null);

		return objectDefinition;
	}

	private String _getObjectDefinitionJSON(long objectDefinitionId)
		throws Exception {

		ObjectDefinitionResource objectDefinitionResource =
			_objectDefinitionResourceFactory.create(
			).user(
				TestPropsValues.getUser()
			).build();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			String.valueOf(
				objectDefinitionResource.getObjectDefinition(
					objectDefinitionId)));

		jsonObject.put("objectRelationships", _jsonFactory.createJSONArray());

		return jsonObject.toString();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private com.liferay.object.model.ObjectDefinition
		_serviceBuilderObjectDefinition1;

	@DeleteAfterTestRun
	private com.liferay.object.model.ObjectDefinition
		_serviceBuilderObjectDefinition2;

	@DeleteAfterTestRun
	private com.liferay.object.model.ObjectDefinition
		_serviceBuilderObjectDefinition3;

	@Inject(filter = "path=/cms/update-structure")
	private StrutsAction _updateStructureStrutsAction;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}