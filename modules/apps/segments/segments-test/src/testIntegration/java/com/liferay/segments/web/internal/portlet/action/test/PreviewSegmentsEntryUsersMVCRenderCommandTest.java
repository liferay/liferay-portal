/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.PortletSession;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class PreviewSegmentsEntryUsersMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());
	}

	@Test
	public void testGetSearchContainerTwoTimesWithSessionCriteria()
		throws Exception {

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		PortletSession portletSession =
			mockLiferayPortletRenderRequest.getPortletSession();

		portletSession.setAttribute(
			"PREVIEW_SEGMENTS_ENTRY_CRITERIA", criteria);

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);

		ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);

		SearchContainer<User> searchContainer = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<User> users = searchContainer.getResults();

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetSearchContainerWithSegmentsEntry() throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"segmentsEntryId",
			String.valueOf(segmentsEntry.getSegmentsEntryId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		SearchContainer<User> searchContainer = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<User> users = searchContainer.getResults();

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetSearchContainerWithSessionCriteria() throws Exception {
		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		PortletSession portletSession =
			mockLiferayPortletRenderRequest.getPortletSession();

		portletSession.setAttribute(
			"PREVIEW_SEGMENTS_ENTRY_CRITERIA", criteria);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		SearchContainer<User> searchContainer = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<User> users = searchContainer.getResults();

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetSearchContainerWithSessionCriteriaAndSegmentsEntry()
		throws Exception {

		Criteria criteria1 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		Criteria criteria2 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		PortletSession portletSession =
			mockLiferayPortletRenderRequest.getPortletSession();

		portletSession.setAttribute(
			"PREVIEW_SEGMENTS_ENTRY_CRITERIA", criteria1);

		mockLiferayPortletRenderRequest.setParameter(
			"segmentsEntryId",
			String.valueOf(segmentsEntry.getSegmentsEntryId()));

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		SearchContainer<User> searchContainer = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<User> users = searchContainer.getResults();

		Assert.assertEquals(_user1, users.get(0));
	}

	@Test
	public void testGetSearchContainerWithSessionCriteriaAndSegmentsEntryAndClearSessionCriteria()
		throws Exception {

		Criteria criteria1 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		Criteria criteria2 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		PortletSession portletSession =
			mockLiferayPortletRenderRequest.getPortletSession();

		portletSession.setAttribute(
			"PREVIEW_SEGMENTS_ENTRY_CRITERIA", criteria1);

		mockLiferayPortletRenderRequest.setParameter(
			"segmentsEntryId",
			String.valueOf(segmentsEntry.getSegmentsEntryId()));
		mockLiferayPortletRenderRequest.setParameter(
			"clearSessionCriteria", Boolean.TRUE.toString());

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		SearchContainer<User> searchContainer = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(_CLASS_NAME),
			"getSearchContainer", new Class<?>[0]);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<User> users = searchContainer.getResults();

		Assert.assertEquals(_user2, users.get(0));
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		Portlet portlet = _portletLocalService.getPortletById(
			SegmentsPortletKeys.SEGMENTS);

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(portlet, null));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);

		return themeDisplay;
	}

	private static final String _CLASS_NAME =
		"com.liferay.segments.web.internal.display.context." +
			"PreviewSegmentsEntryUsersDisplayContext";

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/segments/preview_segments_entry_users",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}