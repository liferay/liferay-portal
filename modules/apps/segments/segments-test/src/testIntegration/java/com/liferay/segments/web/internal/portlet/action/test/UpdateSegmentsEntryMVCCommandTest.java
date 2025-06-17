/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class UpdateSegmentsEntryMVCCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddSegmentsEntry() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequests();

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		User user = TestPropsValues.getUser();

		mockLiferayPortletActionRequest.setAttribute(WebKeys.USER, user);

		mockLiferayPortletActionRequest.setParameter(
			"criterionFilteruser",
			String.format("(lastName eq '%s')", user.getLastName()));

		mockLiferayPortletActionRequest.setParameter(
			"name_" + LocaleUtil.getDefault(), "New Segments Entry");
		mockLiferayPortletActionRequest.setParameter(
			"description_" + LocaleUtil.getDefault(), "This is a description.");
		mockLiferayPortletActionRequest.setParameter("active", StringPool.TRUE);
		mockLiferayPortletActionRequest.setParameter(
			"type", User.class.getName());
		mockLiferayPortletActionRequest.setParameter(
			"saveAndContinue", StringPool.TRUE);
		mockLiferayPortletActionRequest.setParameter(
			"segmentsEntryKey", "key12345");

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				_group.getGroupId(), "key12345");

		Assert.assertEquals(
			"New Segments Entry",
			segmentsEntry.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			"This is a description.",
			segmentsEntry.getDescription(LocaleUtil.getDefault()));

		String criteria = segmentsEntry.getCriteria();

		Assert.assertTrue(
			criteria.contains(
				String.format("(lastName eq '%s')", user.getLastName())));

		Assert.assertEquals(_group.getGroupId(), segmentsEntry.getGroupId());
	}

	@Test
	public void testUpdateSegmentsEntry() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequests();

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		User user = TestPropsValues.getUser();

		mockLiferayPortletActionRequest.setAttribute(WebKeys.USER, user);

		SegmentsEntry initialSegmentsEntry = _addSegmentEntry(
			String.format("(firstName eq '%s')", user.getFirstName()));

		mockLiferayPortletActionRequest.setParameter(
			"segmentsEntryId",
			String.valueOf(initialSegmentsEntry.getSegmentsEntryId()));

		mockLiferayPortletActionRequest.setParameter(
			"criterionFilteruser",
			String.format("(lastName eq '%s')", user.getLastName()));
		mockLiferayPortletActionRequest.setParameter(
			"name_" + LocaleUtil.getDefault(), "New Segments Entry");
		mockLiferayPortletActionRequest.setParameter(
			"segmentsEntryKey", initialSegmentsEntry.getSegmentsEntryKey());
		mockLiferayPortletActionRequest.setParameter(
			"description_" + LocaleUtil.getDefault(), "This is a description.");
		mockLiferayPortletActionRequest.setParameter(
			"saveAndContinue", StringPool.TRUE);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		SegmentsEntry finalSegmentsEntry =
			_segmentsEntryLocalService.getSegmentsEntry(
				initialSegmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			initialSegmentsEntry.getSegmentsEntryId(),
			finalSegmentsEntry.getSegmentsEntryId());
		Assert.assertEquals(
			"New Segments Entry",
			finalSegmentsEntry.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			"This is a description.",
			finalSegmentsEntry.getDescription(LocaleUtil.getDefault()));

		String criteria = finalSegmentsEntry.getCriteria();

		Assert.assertTrue(
			criteria.contains(
				String.format("(lastName eq '%s')", user.getLastName())));

		Assert.assertEquals(
			initialSegmentsEntry.getGroupId(), finalSegmentsEntry.getGroupId());
	}

	private SegmentsEntry _addSegmentEntry(String filterString)
		throws Exception {

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria, filterString, Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			TestPropsValues.getGroupId(),
			CriteriaSerializer.serialize(criteria));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequests()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		Portlet portlet = _portletLocalService.getPortletById(
			SegmentsPortletKeys.SEGMENTS);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)PortletConfigFactoryUtil.create(
				portlet, null);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, liferayPortletConfig);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, SegmentsPortletKeys.SEGMENTS);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		return themeDisplay;
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/segments/update_segments_entry",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}