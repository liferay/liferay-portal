/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryLinkTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class PublishFragmentEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null);
	}

	@Test
	@TestInfo("LPS-97184")
	public void testCannotPublishFragmentWithInvalidConfiguration()
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry(
			_readFileToString(), RandomTestUtil.randomString());

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			_getActionRequest(fragmentEntry), mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			"Please provide a valid configuration for the fragment.",
			_language.get(
				LocaleUtil.getSiteDefault(), jsonObject.getString("error")));
	}

	@Test
	@TestInfo({"LPD-79507", "LPD-95510"})
	public void testPublishFragmentWithPropagateChangesEnabled()
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry(
			null,
			"<div><lfr-drop-zone data-lfr-drop-zone-id=\"1\">" +
				"</lfr-drop-zone></div>");

		_testPublishFragmentWithPropagateChangesEnabled(
			fragmentEntry,
			StringBundler.concat(
				fragmentEntry.getHtml(), "<!--", RandomTestUtil.randomString(),
				"-->"));

		_testPublishFragmentWithPropagateChangesEnabled(
			_getFragmentEntry(
				null,
				"<div><lfr-drop-zone></lfr-drop-zone>" +
					"<lfr-drop-zone></lfr-drop-zone></div>"),
			"<div><lfr-drop-zone></lfr-drop-zone>" +
				"<lfr-drop-zone></lfr-drop-zone>" +
					"<lfr-drop-zone></lfr-drop-zone></div>");
	}

	private void _assertFragmentEntryLinkHTML(
		String expectedHTML, FragmentEntryLink... fragmentEntryLinks) {

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			Assert.assertEquals(expectedHTML, fragmentEntryLink.getHtml());
		}
	}

	private void _assertLayoutStatusApproved(Layout... layouts) {
		for (Layout layout : layouts) {
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, layout.getStatus());
		}
	}

	private ActionRequest _getActionRequest(FragmentEntry fragmentEntry)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"fragmentEntryId",
			String.valueOf(fragmentEntry.getFragmentEntryId()));

		return mockLiferayPortletActionRequest;
	}

	private FragmentEntry _getFragmentEntry(String configuration, String html)
		throws Exception {

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), html, RandomTestUtil.randomString(),
			false, configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_DRAFT,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _readFileToString() throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/fragment/internal/portlet/action/test/dependencies" +
				"/fragment_configuration_invalid.json");
	}

	private void _testPublishFragmentWithPropagateChangesEnabled(
			FragmentEntry fragmentEntry, String updatedHTML)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Layout draftLayout = layout.fetchDraftLayout();

			FragmentEntryLink fragmentEntryLink =
				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					StringPool.BLANK, fragmentEntry.getCss(),
					fragmentEntry.getConfiguration(),
					fragmentEntry.getExternalReferenceCode(), null,
					fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
					fragmentEntry.getFragmentEntryKey(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(draftLayout.getPlid()),
					fragmentEntry.getType());

			ContentLayoutTestUtil.publishLayout(draftLayout, layout);

			_assertFragmentEntryLinkHTML(
				fragmentEntry.getHtml(),
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId()),
				FragmentEntryLinkTestUtil.getFragmentEntryLink(
					_group.getGroupId(),
					fragmentEntryLink.getExternalReferenceCode(),
					layout.getPlid()));

			_assertLayoutStatusApproved(
				_layoutLocalService.getLayout(draftLayout.getPlid()),
				_layoutLocalService.getLayout(layout.getPlid()));

			fragmentEntry.setHtml(updatedHTML);

			fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
				fragmentEntry);

			try (AutoCloseable autoCloseable =
					_layoutServiceContextHelper.getServiceContextAutoCloseable(
						layout, TestPropsValues.getUser())) {

				_mvcActionCommand.processAction(
					_getActionRequest(fragmentEntry),
					new MockLiferayPortletActionResponse());
			}

			_assertLayoutStatusApproved(
				_layoutLocalService.getLayout(draftLayout.getPlid()),
				_layoutLocalService.getLayout(layout.getPlid()));

			_assertFragmentEntryLinkHTML(
				fragmentEntry.getHtml(),
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId()),
				FragmentEntryLinkTestUtil.getFragmentEntryLink(
					_group.getGroupId(),
					fragmentEntryLink.getExternalReferenceCode(),
					layout.getPlid()));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject(filter = "mvc.command.name=/fragment/publish_fragment_entry")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}