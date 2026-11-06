/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.helper;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.rest.internal.security.permission.WorkspacePermission;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class IndividualSegmentHelperTest {

	@Before
	public void setUp() {
		Mockito.when(
			_faroProject.getGroupId()
		).thenReturn(
			_GROUP_ID_WORKSPACE
		);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				Mockito.anyLong(), Mockito.eq(_GROUP_ID_CHANNEL))
		).thenReturn(
			Collections.emptyList()
		);

		WorkspacePermission workspacePermission = new WorkspacePermission();

		ReflectionTestUtil.setFieldValue(
			_individualSegmentHelper, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentHelper, "_faroChannelLocalService",
			_faroChannelLocalService);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentHelper, "_workspacePermission",
			workspacePermission);
		ReflectionTestUtil.setFieldValue(
			workspacePermission, "_faroUserLocalService",
			_faroUserLocalService);
		ReflectionTestUtil.setFieldValue(
			workspacePermission, "_roleLocalService", _roleLocalService);
		ReflectionTestUtil.setFieldValue(
			workspacePermission, "_userGroupRoleLocalService",
			_userGroupRoleLocalService);

		_mockFaroChannel(
			_CHANNEL_ID_ALL_USERS, FaroChannelConstants.PERMISSION_ALL_USERS);
		_mockFaroChannel(
			_CHANNEL_ID_SELECT_USERS,
			FaroChannelConstants.PERMISSION_SELECT_USERS);

		_mockFaroUser(RoleConstants.SITE_ADMINISTRATOR, _USER_ID_ADMINISTRATOR);
		_mockFaroUser(RoleConstants.SITE_MEMBER, _USER_ID_MEMBER);

		_mockIndividualSegment(
			RandomTestUtil.randomString(),
			_INDIVIDUAL_SEGMENT_ID_UNKNOWN_CHANNEL);
		_mockIndividualSegment(
			_CHANNEL_ID_ALL_USERS, _INDIVIDUAL_SEGMENT_ID_ALL_USERS);
		_mockIndividualSegment(
			_CHANNEL_ID_SELECT_USERS, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS);
		_mockIndividualSegment(null, _INDIVIDUAL_SEGMENT_ID_NO_CHANNEL);

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);
	}

	@Test
	public void testGetIndividualSegment() {
		_setPermissionChecker(_USER_ID_MEMBER);

		_assertGetIndividualSegment(_INDIVIDUAL_SEGMENT_ID_ALL_USERS);
		_assertGetIndividualSegment(_INDIVIDUAL_SEGMENT_ID_NO_CHANNEL);
	}

	@Test
	public void testGetIndividualSegmentWithEngineException() {
		Mockito.when(
			_contactsEngineClient.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_BAD_REQUEST, false)
		).thenThrow(
			new FaroEngineClientException(RandomTestUtil.randomString(), 400)
		);

		_assertNotFoundException(_INDIVIDUAL_SEGMENT_ID_BAD_REQUEST);

		Mockito.when(
			_contactsEngineClient.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_NOT_FOUND, false)
		).thenThrow(
			new NoSuchEntryException(RandomTestUtil.randomString())
		);

		_assertNotFoundException(_INDIVIDUAL_SEGMENT_ID_NOT_FOUND);

		FaroEngineClientException faroEngineClientException1 =
			new FaroEngineClientException(RandomTestUtil.randomString(), 500);

		Mockito.when(
			_contactsEngineClient.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_SERVER_ERROR, false)
		).thenThrow(
			faroEngineClientException1
		);

		try {
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_SERVER_ERROR);

			Assert.fail();
		}
		catch (FaroEngineClientException faroEngineClientException2) {
			Assert.assertSame(
				faroEngineClientException1, faroEngineClientException2);
		}
	}

	@Test
	public void testGetIndividualSegmentWithSelectUsersChannel() {
		_setPermissionChecker(_USER_ID_ADMINISTRATOR);

		_assertGetIndividualSegment(_INDIVIDUAL_SEGMENT_ID_SELECT_USERS);

		_setPermissionChecker(_USER_ID_MEMBER);

		try {
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS);

			Assert.fail();
		}
		catch (ForbiddenException forbiddenException) {
			String expectedTitle = StringBundler.concat(
				"Unable to access the individual segment with ID ",
				_INDIVIDUAL_SEGMENT_ID_SELECT_USERS,
				". Call \"getWorkspaceGroupChannelIndividualSegmentsPage\" to ",
				"list the individual segments you can use.");

			Assert.assertEquals(expectedTitle, forbiddenException.getMessage());

			Response response = forbiddenException.getResponse();

			Assert.assertEquals(
				MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
			Assert.assertEquals(
				Response.Status.FORBIDDEN.getStatusCode(),
				response.getStatus());

			Problem problem = (Problem)response.getEntity();

			Assert.assertEquals(Response.Status.FORBIDDEN, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
			Assert.assertNull(problem.getType());
		}

		_setPermissionChecker(_USER_ID_OMNIADMIN);

		_assertGetIndividualSegment(_INDIVIDUAL_SEGMENT_ID_SELECT_USERS);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				_USER_ID_MEMBER, _GROUP_ID_CHANNEL)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		_setPermissionChecker(_USER_ID_MEMBER);

		_assertGetIndividualSegment(_INDIVIDUAL_SEGMENT_ID_SELECT_USERS);
	}

	@Test
	public void testGetIndividualSegmentWithUnknownChannel() {
		_setPermissionChecker(_USER_ID_OMNIADMIN);

		_assertNotFoundException(_INDIVIDUAL_SEGMENT_ID_UNKNOWN_CHANNEL);
	}

	private void _assertGetIndividualSegment(String individualSegmentId) {
		IndividualSegment individualSegment =
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, individualSegmentId);

		Assert.assertEquals(individualSegmentId, individualSegment.getId());
	}

	private void _assertNotFoundException(String individualSegmentId) {
		try {
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, individualSegmentId);

			Assert.fail();
		}
		catch (NotFoundException notFoundException) {
			String expectedTitle =
				"No individual segment was found with ID " +
					individualSegmentId;

			Assert.assertEquals(expectedTitle, notFoundException.getMessage());

			Response response = notFoundException.getResponse();

			Problem problem = (Problem)response.getEntity();

			Assert.assertEquals(Response.Status.NOT_FOUND, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	private void _mockFaroChannel(String channelId, int permissionType) {
		FaroChannel faroChannel = Mockito.mock(FaroChannel.class);

		Mockito.when(
			faroChannel.getGroupId()
		).thenReturn(
			_GROUP_ID_CHANNEL
		);

		Mockito.when(
			faroChannel.getPermissionType()
		).thenReturn(
			permissionType
		);

		Mockito.when(
			faroChannel.getWorkspaceGroupId()
		).thenReturn(
			_GROUP_ID_WORKSPACE
		);

		Mockito.when(
			_faroChannelLocalService.fetchFaroChannel(
				channelId, _GROUP_ID_WORKSPACE)
		).thenReturn(
			faroChannel
		);
	}

	private void _mockFaroUser(String roleName, long userId) {
		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getRoleId()
		).thenReturn(
			userId
		);

		Mockito.when(
			faroUser.getStatus()
		).thenReturn(
			FaroUserConstants.STATUS_APPROVED
		);

		Mockito.when(
			_faroUserLocalService.fetchFaroUser(_GROUP_ID_WORKSPACE, userId)
		).thenReturn(
			faroUser
		);

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			roleName
		);

		Mockito.when(
			_roleLocalService.fetchRole(userId)
		).thenReturn(
			role
		);
	}

	private void _mockIndividualSegment(
		String channelId, String individualSegmentId) {

		IndividualSegment individualSegment = new IndividualSegment();

		individualSegment.setChannelId(channelId);
		individualSegment.setId(individualSegmentId);

		Mockito.when(
			_contactsEngineClient.getIndividualSegment(
				_faroProject, individualSegmentId, false)
		).thenReturn(
			individualSegment
		);
	}

	private void _setPermissionChecker(long userId) {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		boolean omniadmin = false;

		if (userId == _USER_ID_OMNIADMIN) {
			omniadmin = true;
		}

		Mockito.when(
			permissionChecker.isOmniadmin()
		).thenReturn(
			omniadmin
		);

		Mockito.when(
			permissionChecker.getUserId()
		).thenReturn(
			userId
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	private static final String _CHANNEL_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _CHANNEL_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID_CHANNEL = RandomTestUtil.randomLong();

	private static final long _GROUP_ID_WORKSPACE = RandomTestUtil.randomLong();

	private static final String _INDIVIDUAL_SEGMENT_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_BAD_REQUEST =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_NO_CHANNEL =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_NOT_FOUND =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_SERVER_ERROR =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_UNKNOWN_CHANNEL =
		RandomTestUtil.randomString();

	private static final long _USER_ID_ADMINISTRATOR =
		RandomTestUtil.randomLong();

	private static final long _USER_ID_MEMBER = RandomTestUtil.randomLong();

	private static final long _USER_ID_OMNIADMIN = RandomTestUtil.randomLong();

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroChannelLocalService _faroChannelLocalService =
		Mockito.mock(FaroChannelLocalService.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroUserLocalService _faroUserLocalService = Mockito.mock(
		FaroUserLocalService.class);
	private final IndividualSegmentHelper _individualSegmentHelper =
		new IndividualSegmentHelper();
	private PermissionChecker _permissionChecker;
	private final RoleLocalService _roleLocalService = Mockito.mock(
		RoleLocalService.class);
	private final UserGroupRoleLocalService _userGroupRoleLocalService =
		Mockito.mock(UserGroupRoleLocalService.class);

}