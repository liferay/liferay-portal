/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.scheduler.ScheduledPublishInfo;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class SchedulePublicationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
	}

	@After
	public void tearDown() throws Exception {
		_publishScheduler.unschedulePublish(_ctCollection.getCtCollectionId());

		_ctCollectionLocalService.deleteCTCollection(
			_ctCollectionLocalService.getCTCollection(
				_ctCollection.getCtCollectionId()));
	}

	@Test
	public void testProcessAction() throws Exception {
		long time = (System.currentTimeMillis() / Time.SECOND) * Time.SECOND;

		Date publishDate = new Date(time + Time.DAY);

		_processAction(publishDate, "/change_tracking/schedule_publication");

		Date reschedulePublishDate = new Date(time + (2 * Time.DAY));

		_processAction(
			reschedulePublishDate, "/change_tracking/reschedule_publication");
	}

	private void _processAction(Date publishDate, String actionName)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, actionName);
		mockLiferayPortletActionRequest.setParameter(
			"ctCollectionId",
			String.valueOf(_ctCollection.getCtCollectionId()));
		mockLiferayPortletActionRequest.setParameter(
			"publishTime", String.valueOf(publishDate.getTime()));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertTrue(jsonObject.toString(), jsonObject.has("redirect"));

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(publishDate, ctCollection.getScheduledDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, ctCollection.getStatus());

		ScheduledPublishInfo scheduledPublishInfo =
			_publishScheduler.getScheduledPublishInfo(ctCollection);

		Assert.assertEquals(publishDate, scheduledPublishInfo.getStartDate());
	}

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(filter = "mvc.command.name=/change_tracking/schedule_publication")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PublishScheduler _publishScheduler;

}