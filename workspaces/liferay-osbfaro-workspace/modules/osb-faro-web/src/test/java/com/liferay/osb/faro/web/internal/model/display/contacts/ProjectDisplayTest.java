/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class ProjectDisplayTest {

	@Test
	public void testGetURL() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/my-workspace"
		);

		Assert.assertEquals(
			"/web/my-workspace", _getURL(RandomTestUtil.randomLong(), group));
	}

	@Test
	public void testGetURLWithoutGroup() {
		Assert.assertNull(_getURL(RandomTestUtil.randomLong(), null));
	}

	private String _getURL(long groupId, Group group) {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			faroProject.getTimeZoneId()
		).thenReturn(
			"UTC"
		);

		try (MockedStatic<FaroUserLocalServiceUtil>
				faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
					FaroUserLocalServiceUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class)) {

			groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(groupId)
			).thenReturn(
				group
			);

			portalUtilMockedStatic.when(
				PortalUtil::getPathFriendlyURLPublic
			).thenReturn(
				"/web"
			);

			return ReflectionTestUtil.getFieldValue(
				new ProjectDisplay(faroProject), "_url");
		}
	}

}