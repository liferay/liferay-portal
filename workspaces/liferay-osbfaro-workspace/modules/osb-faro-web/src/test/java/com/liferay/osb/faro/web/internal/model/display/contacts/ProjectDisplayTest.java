/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class ProjectDisplayTest {

	@Test
	public void testGetIncidentReportEmailAddresses() throws Exception {
		String emailAddress = RandomTestUtil.randomString();
		FaroProject faroProject = _mockFaroProject(RandomTestUtil.randomLong());

		Mockito.when(
			faroProject.getIncidentReportEmailAddresses()
		).thenReturn(
			JSONUtil.writeValueAsString(Collections.singletonList(emailAddress))
		);

		Assert.assertEquals(
			Collections.singletonList(emailAddress),
			ReflectionTestUtil.getFieldValue(
				_toProjectDisplay(faroProject, null),
				"_incidentReportEmailAddresses"));
	}

	@Test
	public void testGetIncidentReportEmailAddressesWithoutEmailAddresses() {
		FaroProject faroProject = _mockFaroProject(RandomTestUtil.randomLong());

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			"{}"
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ProjectDisplay.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertNull(
				ReflectionTestUtil.getFieldValue(
					_toProjectDisplay(faroProject, null),
					"_incidentReportEmailAddresses"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

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
		return ReflectionTestUtil.getFieldValue(
			_toProjectDisplay(_mockFaroProject(groupId), group), "_url");
	}

	private FaroProject _mockFaroProject(long groupId) {
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

		return faroProject;
	}

	private ProjectDisplay _toProjectDisplay(
		FaroProject faroProject, Group group) {

		try (MockedStatic<FaroUserLocalServiceUtil>
				faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
					FaroUserLocalServiceUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class)) {

			groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.fetchGroup(faroProject.getGroupId())
			).thenReturn(
				group
			);

			portalUtilMockedStatic.when(
				PortalUtil::getPathFriendlyURLPublic
			).thenReturn(
				"/web"
			);

			return new ProjectDisplay(faroProject);
		}
	}

}