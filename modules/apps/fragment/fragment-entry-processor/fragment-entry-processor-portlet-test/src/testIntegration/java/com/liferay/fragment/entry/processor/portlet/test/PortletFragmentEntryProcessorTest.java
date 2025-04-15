/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.portlet.constants.FragmentEntryLinkPortletKeys;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.Portlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class PortletFragmentEntryProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = new MockServiceContext(_layout, _getThemeDisplay());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCanAddMoreThanOneInstanceableWidget() throws Exception {
		_addFragmentEntry(
			_getHTML(
				FragmentEntryLinkPortletKeys.
					FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET_ALIAS,
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	@Test
	public void testCanAddOneNoninstanceableWidget() throws Exception {
		_addFragmentEntry(
			_getHTML(
				FragmentEntryLinkPortletKeys.
					FRAGMENT_ENTRY_LINK_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
				StringPool.BLANK));
	}

	@Test(expected = FragmentEntryContentException.class)
	public void testCannotAddMoreThanOneNoninstanceableWidget()
		throws Exception {

		_addFragmentEntry(
			_getHTML(
				FragmentEntryLinkPortletKeys.
					FRAGMENT_ENTRY_LINK_NONINSTANCEABLE_TEST_PORTLET_ALIAS,
				StringPool.BLANK, StringPool.BLANK));
	}

	@Test
	public void testFragmentEntryLinkPortletPreferences() throws Exception {
		String instanceId = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_getHTML(
				FragmentEntryLinkPortletKeys.
					FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET_ALIAS,
				instanceId));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(), _serviceContext);

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid());

		List<PortletPreferences> filteredPortletPreferencesList =
			ListUtil.filter(
				portletPreferencesList,
				portletPreferences -> {
					String portletId = portletPreferences.getPortletId();

					return portletId.startsWith(
						FragmentEntryLinkPortletKeys.
							FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET);
				});

		Assert.assertEquals(
			filteredPortletPreferencesList.toString(), 1,
			filteredPortletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		String curInstanceId = PortletIdCodec.decodeInstanceId(
			portletPreferences.getPortletId());

		Assert.assertEquals(
			fragmentEntryLink.getNamespace() + instanceId, curInstanceId);
	}

	private FragmentEntry _addFragmentEntry(String html) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.addFragmentCollection(
				null, _group.getGroupId(), "Fragment Collection",
				StringPool.BLANK, serviceContext);

		return _fragmentEntryService.addFragmentEntry(
			null, _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), "fragment-entry",
			"Fragment Entry", null, html, null, false, null, null, 0, false,
			false, FragmentConstants.TYPE_SECTION, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private String _getHTML(String portletAlias, String... instanceIds) {
		StringBundler sb = new StringBundler();

		sb.append("<div>");

		for (String instanceId : instanceIds) {
			sb.append("<lfr-widget-");
			sb.append(portletAlias);
			sb.append(" id=\"");
			sb.append(instanceId);
			sb.append("\"></lfr-widget-");
			sb.append(portletAlias);
			sb.append(">");
		}

		sb.append("</div>");

		return sb.toString();
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLookAndFeel(
			_themeLocalService.getTheme(
				_company.getCompanyId(), layoutSet.getThemeId()),
			null);
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(_getHttpServletRequest());
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionService _fragmentCollectionService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryService _fragmentEntryService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "jakarta.portlet.name=" + FragmentEntryLinkPortletKeys.FRAGMENT_ENTRY_LINK_INSTANCEABLE_TEST_PORTLET
	)
	private final Portlet _instanceablePortlet = null;

	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject(
		filter = "jakarta.portlet.name=" + FragmentEntryLinkPortletKeys.FRAGMENT_ENTRY_LINK_NONINSTANCEABLE_TEST_PORTLET
	)
	private final Portlet _noninstanceablePortlet = null;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private ThemeLocalService _themeLocalService;

	private static class MockServiceContext extends ServiceContext {

		public MockServiceContext(Layout layout, ThemeDisplay themeDisplay) {
			_layout = layout;
			_themeDisplay = themeDisplay;
		}

		@Override
		public HttpServletRequest getRequest() {
			HttpServletRequest httpServletRequest =
				new MockHttpServletRequest();

			httpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, _themeDisplay);

			return httpServletRequest;
		}

		@Override
		public HttpServletResponse getResponse() {
			return new MockHttpServletResponse();
		}

		@Override
		public ThemeDisplay getThemeDisplay() {
			return _themeDisplay;
		}

		private final Layout _layout;
		private final ThemeDisplay _themeDisplay;

	}

}