/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelService;

import java.util.Collections;
import java.util.List;

import org.junit.After;
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
public class SaveVariantSegmentsExperienceMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessAction() throws Exception {
		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				defaultSegmentsExperienceId, _layout.getPlid(), "AB test",
				"A/B test description",
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK, _serviceContext);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, _group.getGroupId(), 0, _layout.getPlid(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), "Variant 1"),
				false, new UnicodeProperties(true), _serviceContext);

		_segmentsExperimentRelService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(), _serviceContext);

		_addFragmentEntryLink(_draftLayout, defaultSegmentsExperienceId);

		FragmentEntryLink draftLayoutFragmentEntryLink = _addFragmentEntryLink(
			_draftLayout, segmentsExperience.getSegmentsExperienceId());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(segmentsExperience.getSegmentsExperienceId()));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		List<FragmentEntryLink>
			defaultSegmentsExperienceLayoutFragmentEntryLinks =
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						_group.getGroupId(), defaultSegmentsExperienceId,
						_layout.getPlid());

		Assert.assertEquals(
			defaultSegmentsExperienceLayoutFragmentEntryLinks.toString(), 0,
			defaultSegmentsExperienceLayoutFragmentEntryLinks.size());

		List<FragmentEntryLink>
			variantSegmentsExperienceDraftLayoutFragmentEntryLinks =
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						_group.getGroupId(),
						segmentsExperience.getSegmentsExperienceId(),
						_draftLayout.getPlid());
		List<FragmentEntryLink>
			variantSegmentsExperienceLayoutFragmentEntryLinks =
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						_group.getGroupId(),
						segmentsExperience.getSegmentsExperienceId(),
						_layout.getPlid());

		Assert.assertEquals(
			variantSegmentsExperienceLayoutFragmentEntryLinks.toString(),
			variantSegmentsExperienceDraftLayoutFragmentEntryLinks.size(),
			variantSegmentsExperienceLayoutFragmentEntryLinks.size());

		FragmentEntryLink publishLayoutFragmentEntryLink =
			variantSegmentsExperienceLayoutFragmentEntryLinks.get(0);

		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getConfiguration(),
			publishLayoutFragmentEntryLink.getConfiguration());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getCss(),
			publishLayoutFragmentEntryLink.getCss());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getEditableValues(),
			publishLayoutFragmentEntryLink.getEditableValues());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getHtml(),
			publishLayoutFragmentEntryLink.getHtml());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getJs(),
			publishLayoutFragmentEntryLink.getJs());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getRendererKey(),
			publishLayoutFragmentEntryLink.getRendererKey());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getEditableValues(),
			publishLayoutFragmentEntryLink.getEditableValues());
		Assert.assertEquals(
			draftLayoutFragmentEntryLink.getPosition(),
			publishLayoutFragmentEntryLink.getPosition());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			Layout layout, long segmentsExperienceId)
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry();

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			segmentsExperienceId, fragmentEntry.getType());
	}

	private FragmentEntry _getFragmentEntry() throws Exception {
		if (_fragmentEntry != null) {
			return _fragmentEntry;
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);
		String html = "<div data-lfr-styles><span>Test</span>Fragment</div>";

		_fragmentEntry = _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry-key",
			RandomTestUtil.randomString(), StringPool.BLANK, html,
			StringPool.BLANK, false, StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);

		return _fragmentEntry;
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET),
				null));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID,
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLanguageId(LanguageUtil.getLanguageId(LocaleUtil.US));
		themeDisplay.setLayout(_draftLayout);
		themeDisplay.setLayoutSet(_draftLayout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_draftLayout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_draftLayout.getPlid());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/save_variant_segments_experience",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperienceService _segmentsExperienceService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Inject
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	private ServiceContext _serviceContext;

}