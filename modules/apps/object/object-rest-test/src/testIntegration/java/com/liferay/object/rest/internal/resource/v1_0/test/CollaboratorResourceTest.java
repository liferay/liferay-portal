/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class CollaboratorResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinition = _getObjectDefinition();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-48130")
	public void testDeleteObjectEntryCollaboratorByEmailAddress()
		throws Exception {

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.DELETE);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Ticket ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.DELETE);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				ticket.getTicketId(), 0, 0,
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		User user1 = _addUser();

		emailAddress = user1.getEmailAddress();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.DELETE);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		_addUser(emailAddress);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.DELETE);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				ticket.getTicketId(), 0, 0,
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		long classNameId = _classNameLocalService.getClassNameId(
			objectEntry.getModelClassName());

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			_role, _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.UPDATE);
		RoleTestUtil.addResourcePermission(
			_role, _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		String password = RandomTestUtil.randomString();

		user1 = _addUser();

		User user2 = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		user2.setEmailAddressVerified(true);

		user2 = _userLocalService.updateUser(user2);

		_users.add(user2);

		_userLocalService.addRoleUser(_role.getRoleId(), user2.getUserId());

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user1.getUserId(),
			classNameId, objectEntry.getObjectEntryId(),
			objectEntry.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			new ServiceContext());

		String endpoint1 = StringBundler.concat(
			_objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntry.getObjectEntryId(), "/collaborators/by-email-address/",
			user1.getEmailAddress());

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() -> {
				JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
					null, endpoint1, Http.Method.DELETE);

				Assert.assertEquals(
					"NOT_FOUND", jsonObject.getString("status"));
			}
		);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(), classNameId,
				objectEntry.getObjectEntryId()));

		_sharingEntryLocalService.deleteSharingEntry(sharingEntry);

		_sharingEntryLocalService.addSharingEntry(
			null, user2.getUserId(), 0, 0, user1.getUserId(), classNameId,
			objectEntry.getObjectEntryId(), objectEntry.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			new ServiceContext());

		HTTPTestUtil.invokeToJSONObject(null, endpoint1, Http.Method.DELETE);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(), classNameId,
				objectEntry.getObjectEntryId()));

		_sharingEntryLocalService.addSharingEntry(
			null, user2.getUserId(), 0, 0, user1.getUserId(), classNameId,
			objectEntry.getObjectEntryId(), objectEntry.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			new ServiceContext());

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() -> HTTPTestUtil.invokeToJSONObject(
				null, endpoint1, Http.Method.DELETE)
		);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(), classNameId,
				objectEntry.getObjectEntryId()));

		_sharingEntryLocalService.deleteSharingEntry(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(), classNameId,
				objectEntry.getObjectEntryId()));

		objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), user2.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user2.getUserId()));

		_sharingEntryLocalService.addSharingEntry(
			null, user2.getUserId(), 0, 0, user1.getUserId(), classNameId,
			objectEntry.getObjectEntryId(), objectEntry.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			new ServiceContext());

		String endpoint2 = StringBundler.concat(
			_objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntry.getObjectEntryId(), "/collaborators/by-email-address/",
			user1.getEmailAddress());

		HTTPTestUtil.customize(
		).withCredentials(
			user2.getEmailAddress(), password
		).apply(
			() -> HTTPTestUtil.invokeToJSONObject(
				null, endpoint2, Http.Method.DELETE)
		);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(), classNameId,
				objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Test
	@TestInfo("LPD-48130")
	public void testDeleteObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _addUser();

		_assertDeleteObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/User/",
				user.getUserId()),
			user);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/Email/",
				user.getUserId()),
			Http.Method.DELETE);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testDeleteScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress()
		throws Exception {

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Ticket ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.DELETE);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testDeleteScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _addUser();

		_assertDeleteObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/User/", user.getUserId()),
			user);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/Email/", user.getUserId()),
			Http.Method.DELETE);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testGetObjectEntryCollaboratorByEmailAddress()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.GET);

		Assert.assertEquals("Email", jsonObject.getString("type"));
		Assert.assertEquals(emailAddress, jsonObject.getString("emailAddress"));

		Ticket ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		_sharingEntryLocalService.deleteSharingEntry(
			_sharingEntryLocalService.fetchSharingEntry(
				ticket.getTicketId(), 0, 0,
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		_ticketLocalService.deleteTicket(ticket.getTicketId());

		objectEntry = _addObjectEntry();

		User user = _addUser();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", user.getEmailAddress()),
			Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(user.getEmailAddress())
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", user.getEmailAddress()),
			Http.Method.GET);

		Assert.assertEquals("User", jsonObject.getString("type"));
		Assert.assertEquals(
			String.valueOf(user.getUserId()), jsonObject.getString("id"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testGetObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _addUser();

		_assertGetObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/User/",
				user.getUserId()),
			user);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators/by-type/Email/",
				user.getUserId()),
			Http.Method.GET);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testGetObjectEntryCollaboratorsPage() throws Exception {
		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.GET);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject3, collaboratorJSONObject2,
				collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				emailAddress
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.PUT);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.GET);

		Assert.assertEquals(4, jsonObject.getInt("totalCount"));

		Ticket ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		_ticketLocalService.deleteTicket(ticket.getTicketId());

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.GET);

		Assert.assertEquals(3, jsonObject.getInt("totalCount"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testGetScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress()
		throws Exception {

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.GET);

		Assert.assertEquals("Email", jsonObject.getString("type"));
		Assert.assertEquals(emailAddress, jsonObject.getString("emailAddress"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testGetScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		User user = _addUser();

		_assertGetObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/User/", user.getUserId()),
			user);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/Email/", user.getUserId()),
			Http.Method.GET);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	@Test
	public void testGetScopeScopeKeyByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(), "/collaborators"),
			Http.Method.GET);

		_assertEquals(
			JSONUtil.putAll(collaboratorJSONObject2, collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testPostObjectEntryCollaboratorsPage() throws Exception {
		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject3, collaboratorJSONObject2,
				collaboratorJSONObject1),
			jsonObject1.getJSONArray("items"));

		objectEntry = _addObjectEntry();

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject("not-an-email")
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.getString("status"));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				JSONUtil.put(
					"actionIds",
					JSONUtil.put(SharingEntryAction.VIEW.getActionId())
				).put(
					"share", true
				).put(
					"type", "Email"
				)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.getString("status"));

		String emailAddress1 =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				JSONUtil.put(
					"actionIds", JSONUtil.put("INVALID_ACTION")
				).put(
					"emailAddress", emailAddress1
				).put(
					"share", true
				).put(
					"type", "Email"
				)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress1));

		jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(
					emailAddress1, SharingEntryAction.UPDATE,
					SharingEntryAction.VIEW)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject1.getString("status"));

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress1));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress1)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Ticket ticket1 = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress1);

		Assert.assertNotNull(ticket1);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				ticket1.getTicketId(), 0, 0,
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(
					StringUtil.toUpperCase(emailAddress1))
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), objectEntry.getModelClassName(),
			objectEntry.getObjectEntryId(),
			TicketConstants.TYPE_INVITE_COLLABORATOR);

		Assert.assertEquals(tickets.toString(), 1, tickets.size());

		Ticket ticket2 = tickets.get(0);

		Assert.assertEquals(ticket1.getTicketId(), ticket2.getTicketId());

		User user1 = _addUser(emailAddress1);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(emailAddress1)
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress1));
		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user1.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		User user2 = _addUser();

		objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(user2.getEmailAddress())
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNull(
			"No ticket should be created when the email resolves to an " +
				"existing user",
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				user2.getEmailAddress()));

		Assert.assertNotNull(
			"A User-type sharing entry must exist for the resolved user",
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user2.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		String emailAddress2 =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		jsonObject1 = _getEmailCollaboratorJSONObject(emailAddress2);

		JSONObject jsonObject2 = _getUserCollaboratorJSONObject();
		JSONObject jsonObject3 = _getUserGroupCollaboratorJSONObject();

		objectEntry = _addObjectEntry();

		JSONObject jsonObject5 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				jsonObject1, jsonObject2, jsonObject3
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		JSONArray jsonArray = jsonObject5.getJSONArray("items");

		Assert.assertEquals(jsonArray.toString(), 3, jsonArray.length());

		Assert.assertNotNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress2));

		String emailAddress3 =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";
		String emailAddress4 =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		User user3 = _addUser();

		objectEntry = _addObjectEntry();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				_getEmailCollaboratorJSONObject(emailAddress3),
				_getEmailCollaboratorJSONObject(emailAddress4),
				_getEmailCollaboratorJSONObject(user3.getEmailAddress())
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNotNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress3));
		Assert.assertNotNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress4));
		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user3.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_getEmailCollaboratorJSONObject(user3.getEmailAddress())
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(), "/collaborators"),
			Http.Method.POST);

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress3));
		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress4));
		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user3.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testPostScopeScopeKeyByExternalReferenceCodeCollaboratorsPage()
		throws Exception {

		JSONObject collaboratorJSONObject1 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject2 =
			_getUserGroupCollaboratorJSONObject();
		JSONObject collaboratorJSONObject3 = _getUserCollaboratorJSONObject();
		JSONObject collaboratorJSONObject4 =
			_getUserGroupCollaboratorJSONObject();
		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.putAll(
				collaboratorJSONObject1, collaboratorJSONObject2,
				collaboratorJSONObject3, collaboratorJSONObject4
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(), "/collaborators"),
			Http.Method.POST);

		_assertEquals(
			JSONUtil.putAll(
				collaboratorJSONObject4, collaboratorJSONObject3,
				collaboratorJSONObject2, collaboratorJSONObject1),
			jsonObject.getJSONArray("items"));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testPutObjectEntryCollaboratorByEmailAddress()
		throws Exception {

		User user = _addUser();

		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				user.getEmailAddress(), SharingEntryAction.UPDATE,
				SharingEntryAction.VIEW
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", user.getEmailAddress()),
			Http.Method.PUT);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				user.getEmailAddress()
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", user.getEmailAddress()),
			Http.Method.PUT);

		Assert.assertEquals("User", jsonObject.getString("type"));
		Assert.assertEquals(
			String.valueOf(user.getUserId()), jsonObject.getString("id"));

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				0, 0, user.getUserId(),
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				user.getEmailAddress()));

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		objectEntry = _addObjectEntry();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				emailAddress, SharingEntryAction.UPDATE, SharingEntryAction.VIEW
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.PUT);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));

		Assert.assertNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				emailAddress
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.PUT);

		Assert.assertEquals("Email", jsonObject.getString("type"));
		Assert.assertEquals(emailAddress, jsonObject.getString("emailAddress"));

		Ticket ticket = _fetchTicketByEmailAddress(
			objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
			emailAddress);

		Assert.assertNotNull(ticket);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntry(
				ticket.getTicketId(), 0, 0,
				_classNameLocalService.getClassNameId(
					objectEntry.getModelClassName()),
				objectEntry.getObjectEntryId()));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testPutObjectEntryCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		UserGroup userGroup = _addUserGroup();

		_assertPutObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), StringPool.SLASH,
				objectEntry.getObjectEntryId(),
				"/collaborators/by-type/UserGroup/",
				userGroup.getUserGroupId()),
			userGroup);
	}

	@Test
	@TestInfo("LPD-48130")
	public void testPutScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress()
		throws Exception {

		String emailAddress =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) +
				"@liferay.com";

		ObjectEntry objectEntry = _addObjectEntry();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getEmailCollaboratorJSONObject(
				emailAddress
			).toString(),
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-email-address/", emailAddress),
			Http.Method.PUT);

		Assert.assertEquals("Email", jsonObject.getString("type"));
		Assert.assertEquals(emailAddress, jsonObject.getString("emailAddress"));

		Assert.assertNotNull(
			_fetchTicketByEmailAddress(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				emailAddress));
	}

	@Test
	@TestInfo("LPD-48130")
	public void testPutScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		UserGroup userGroup = _addUserGroup();

		_assertPutObjectEntryCollaborator(
			StringBundler.concat(
				_objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				objectEntry.getExternalReferenceCode(),
				"/collaborators/by-type/UserGroup/",
				userGroup.getUserGroupId()),
			userGroup);
	}

	private static ObjectDefinition _getObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).indexed(
					true
				).indexedAsKeyword(
					true
				).name(
					"title"
				).localized(
					false
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE, TestPropsValues.getUserId());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		return user;
	}

	private User _addUser(String emailAddress) throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			StringPool.BLANK, emailAddress, RandomTestUtil.randomString(),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		_users.add(user);

		return user;
	}

	private UserGroup _addUserGroup() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_userGroups.add(userGroup);

		return userGroup;
	}

	private void _assertDeleteObjectEntryCollaborator(
			String endpoint, User user)
		throws Exception {

		JSONObject jsonObject1 = _getUserCollaboratorJSONObject(user);

		_assertEquals(
			jsonObject1,
			HTTPTestUtil.invokeToJSONObject(
				jsonObject1.toString(), endpoint, Http.Method.PUT));

		HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.DELETE);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		Assert.assertEquals("NOT_FOUND", jsonObject2.getString("status"));
	}

	private void _assertEquals(
		JSONArray actualJSONArray, JSONArray expectedJSONArray) {

		Assert.assertEquals(
			expectedJSONArray.length(), actualJSONArray.length());

		for (int i = 0; i < expectedJSONArray.length(); i++) {
			_assertEquals(
				actualJSONArray.getJSONObject(i),
				expectedJSONArray.getJSONObject(i));
		}
	}

	private void _assertEquals(JSONObject jsonObject1, JSONObject jsonObject2) {
		Assert.assertTrue(_equals(jsonObject1, jsonObject2));
	}

	private void _assertGetObjectEntryCollaborator(String endpoint, User user)
		throws Exception {

		JSONObject jsonObject = _getUserCollaboratorJSONObject(user);

		HTTPTestUtil.invokeToJSONObject(
			jsonObject.toString(), endpoint, Http.Method.PUT);

		_assertEquals(
			jsonObject,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET));
	}

	private void _assertPutObjectEntryCollaborator(
			String endpoint, UserGroup userGroup)
		throws Exception {

		JSONObject jsonObject = _getUserGroupCollaboratorJSONObject(userGroup);

		HTTPTestUtil.invokeToJSONObject(
			jsonObject.toString(), endpoint, Http.Method.PUT);

		_assertEquals(
			jsonObject,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.GET));
	}

	private boolean _equals(JSONObject jsonObject1, JSONObject jsonObject2) {
		for (String assertFieldName : _ASSERT_FIELD_NAMES) {
			if (Objects.equals(assertFieldName, "actionIds")) {
				if (!JSONUtil.equals(
						jsonObject1.getJSONArray("actionIds"),
						jsonObject2.getJSONArray("actionIds"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "externalReferenceCode")) {
				if (!StringUtil.equals(
						jsonObject1.getString("externalReferenceCode"),
						jsonObject2.getString("externalReferenceCode"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "name")) {
				if (!StringUtil.equals(
						jsonObject1.getString("name"),
						jsonObject2.getString("name"))) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "share")) {
				if (jsonObject1.getBoolean("share") != jsonObject2.getBoolean(
						"share")) {

					return false;
				}

				continue;
			}

			if (Objects.equals(assertFieldName, "type") &&
				!StringUtil.equals(
					jsonObject1.getString("type"),
					jsonObject2.getString("type"))) {

				return false;
			}
		}

		return true;
	}

	private Ticket _fetchTicketByEmailAddress(
			String className, long classPK, String emailAddress)
		throws Exception {

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), className, classPK,
			TicketConstants.TYPE_INVITE_COLLABORATOR);

		for (Ticket ticket : tickets) {
			if (Objects.equals(emailAddress, ticket.getEmailAddress())) {
				return ticket;
			}
		}

		return null;
	}

	private JSONObject _getEmailCollaboratorJSONObject(String emailAddress) {
		return _getEmailCollaboratorJSONObject(
			emailAddress, SharingEntryAction.VIEW);
	}

	private JSONObject _getEmailCollaboratorJSONObject(
		String emailAddress, SharingEntryAction... sharingEntryActions) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (SharingEntryAction sharingEntryAction : sharingEntryActions) {
			jsonArray.put(sharingEntryAction.getActionId());
		}

		return JSONUtil.put(
			"actionIds", jsonArray
		).put(
			"emailAddress", emailAddress
		).put(
			"share", true
		).put(
			"type", "Email"
		);
	}

	private JSONObject _getUserCollaboratorJSONObject() throws Exception {
		return _getUserCollaboratorJSONObject(_addUser());
	}

	private JSONObject _getUserCollaboratorJSONObject(User user) {
		return JSONUtil.put(
			"actionIds", JSONUtil.put(SharingEntryAction.VIEW.getActionId())
		).put(
			"externalReferenceCode", user.getExternalReferenceCode()
		).put(
			"id", user.getUserId()
		).put(
			"name", user.getFullName()
		).put(
			"share", true
		).put(
			"type", "User"
		);
	}

	private JSONObject _getUserGroupCollaboratorJSONObject() throws Exception {
		return _getUserGroupCollaboratorJSONObject(_addUserGroup());
	}

	private JSONObject _getUserGroupCollaboratorJSONObject(
		UserGroup userGroup) {

		return JSONUtil.put(
			"actionIds", JSONUtil.put(SharingEntryAction.VIEW.getActionId())
		).put(
			"externalReferenceCode", userGroup.getExternalReferenceCode()
		).put(
			"id", userGroup.getUserGroupId()
		).put(
			"name", userGroup.getName()
		).put(
			"share", true
		).put(
			"type", "UserGroup"
		);
	}

	private static final String[] _ASSERT_FIELD_NAMES = {
		"actionIds", "externalReferenceCode", "name", "share", "type"
	};

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private List<User> _users = new ArrayList<>();

}