/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging;

import com.liferay.exportimport.internal.staging.permission.StagingPermissionImpl;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutStagingImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		Mockito.when(
			_liveGroup.hasLocalOrRemoteStagingGroup()
		).thenReturn(
			true
		);

		Mockito.when(
			_liveGroup.isStagedPortlet(Mockito.anyString())
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> {
				String portletId = invocationOnMock.getArgument(
					0, String.class);

				return Objects.equals(_STAGED_PORTLET_ID, portletId);
			}
		);
	}

	@Test
	public void testHasPermission() {
		StagingPermissionImpl stagingPermissionImpl =
			new StagingPermissionImpl();

		for (String actionId : _ACTION_IDS) {
			Assert.assertNull(
				_hasPermission(
					actionId, _group, RandomTestUtil.randomString(),
					stagingPermissionImpl));
			Assert.assertNull(
				_hasPermission(
					actionId, _group, _STAGED_PORTLET_ID,
					stagingPermissionImpl));
			Assert.assertNull(
				_hasPermission(
					actionId, _liveGroup, RandomTestUtil.randomString(),
					stagingPermissionImpl));
			Assert.assertNull(
				_hasPermission(
					actionId, _liveGroup, _STAGED_PORTLET_ID,
					stagingPermissionImpl));
		}

		Assert.assertFalse(
			_hasPermission(
				RandomTestUtil.randomString(), _liveGroup, _STAGED_PORTLET_ID,
				stagingPermissionImpl));
		Assert.assertNull(
			_hasPermission(
				RandomTestUtil.randomString(), _group,
				RandomTestUtil.randomString(), stagingPermissionImpl));
		Assert.assertNull(
			_hasPermission(
				RandomTestUtil.randomString(), _group, _STAGED_PORTLET_ID,
				stagingPermissionImpl));
		Assert.assertNull(
			_hasPermission(
				RandomTestUtil.randomString(), _liveGroup,
				RandomTestUtil.randomString(), stagingPermissionImpl));
	}

	private Boolean _hasPermission(
		String actionId, Group group, String portletId,
		StagingPermissionImpl stagingPermissionImpl) {

		return stagingPermissionImpl.hasPermission(
			null, group, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong(), portletId, actionId);
	}

	private static final String[] _ACTION_IDS = {
		ActionKeys.ACCESS, ActionKeys.ACCESS_IN_CONTROL_PANEL,
		ActionKeys.ADD_DISCUSSION, ActionKeys.ADD_TO_PAGE,
		ActionKeys.ASSIGN_MEMBERS, ActionKeys.CONFIGURATION,
		ActionKeys.CUSTOMIZE, ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
		ActionKeys.DOWNLOAD, ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW
	};

	private static final String _STAGED_PORTLET_ID =
		RandomTestUtil.randomString();

	private static final Group _group = Mockito.mock(Group.class);
	private static final Group _liveGroup = Mockito.mock(Group.class);

}