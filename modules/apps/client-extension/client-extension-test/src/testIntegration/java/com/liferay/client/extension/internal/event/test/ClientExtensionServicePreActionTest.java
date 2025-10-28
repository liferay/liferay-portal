/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.event.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import junit.framework.Assert;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class ClientExtensionServicePreActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_layout = LayoutTestUtil.addTypeContentLayout(
			TestPropsValues.getUserId(), _group);
	}

	@Test
	public void testProcessServicePreActionControlPanelLayoutThemeCSS()
		throws Exception {

		_addThemeCSSClientExtensionEntry();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(Layout.class), _layout.getPlid(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Group controlPanelGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);

		Layout controlPanelLayout = _layoutLocalService.fetchDefaultLayout(
			controlPanelGroup.getGroupId(), true);

		_assertThemeCSSURLs(controlPanelLayout, Collections.emptyMap(), false);

		_assertThemeCSSURLs(
			controlPanelLayout,
			HashMapBuilder.put(
				"p_l_mode", Constants.PREVIEW
			).build(),
			false);

		_assertThemeCSSURLs(
			controlPanelLayout,
			HashMapBuilder.put(
				"p_l_mode", Constants.PREVIEW
			).put(
				"p_p_id",
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET
			).build(),
			false);

		_assertThemeCSSURLs(
			controlPanelLayout,
			HashMapBuilder.put(
				"p_l_mode", Constants.PREVIEW
			).put(
				"p_p_id",
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET
			).put(
				StringBundler.concat(
					StringPool.UNDERLINE,
					ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
					"_selPlid"),
				String.valueOf(_layout.getPlid())
			).build(),
			true);
	}

	@Test
	public void testProcessServicePreActionLayoutFavicon() throws Exception {
		_addFaviconClientExtensionEntry();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(Layout.class), _layout.getPlid(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertFaviconURL();
	}

	@Test
	public void testProcessServicePreActionLayoutSetFavicon() throws Exception {
		_addFaviconClientExtensionEntry();

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(LayoutSet.class), layoutSet.getLayoutSetId(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertFaviconURL();
	}

	@Test
	public void testProcessServicePreActionLayoutSetThemeCSS()
		throws Exception {

		_addThemeCSSClientExtensionEntry();

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(LayoutSet.class), layoutSet.getLayoutSetId(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertThemeCSSURLs(_layout, Collections.emptyMap(), true);
	}

	@Test
	public void testProcessServicePreActionLayoutThemeCSS() throws Exception {
		_addThemeCSSClientExtensionEntry();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(Layout.class), _layout.getPlid(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertThemeCSSURLs(_layout, Collections.emptyMap(), true);
		_assertThemeCSSURLs(
			_layout,
			HashMapBuilder.put(
				"languageId", "ar"
			).build(),
			true);
	}

	@Test
	public void testProcessServicePreActionMasterLayoutFavicon()
		throws Exception {

		_addFaviconClientExtensionEntry();

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(Layout.class),
			masterLayoutPageTemplateEntry.getPlid(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout.setMasterLayoutPlid(masterLayoutPageTemplateEntry.getPlid());

		_layout = _layoutLocalService.updateLayout(_layout);

		_assertFaviconURL();
	}

	@Test
	public void testProcessServicePreActionMasterLayoutThemeCSS()
		throws Exception {

		_addThemeCSSClientExtensionEntry();

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(Layout.class),
			masterLayoutPageTemplateEntry.getPlid(),
			_clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout.setMasterLayoutPlid(masterLayoutPageTemplateEntry.getPlid());

		_layout = _layoutLocalService.updateLayout(_layout);

		_assertThemeCSSURLs(_layout, Collections.emptyMap(), true);
	}

	private void _addFaviconClientExtensionEntry() throws Exception {
		_clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_THEME_FAVICON,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"url", _URL_FAVICON
				).buildString());
	}

	private void _addThemeCSSClientExtensionEntry() throws Exception {
		_clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_THEME_CSS,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"clayRTLURL", _URL_CLAY_RTL_CSS
				).put(
					"clayURL", _URL_CLAY_CSS
				).put(
					"mainRTLURL", _URL_MAIN_RTL_CSS
				).put(
					"mainURL", _URL_MAIN_CSS
				).buildString());
	}

	private void _assertFaviconURL() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_layout, Collections.emptyMap());

		_processServicePreAction(mockHttpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Assert.assertEquals(_URL_FAVICON, themeDisplay.getFaviconURL());
	}

	private void _assertThemeCSSURLs(
			Layout layout, Map<String, String> params,
			boolean clientExtensionApplied)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(layout, params);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_processServicePreAction(mockHttpServletRequest);

		if (clientExtensionApplied) {
			if (_portal.isRightToLeft(mockHttpServletRequest)) {
				Assert.assertEquals(
					_URL_CLAY_RTL_CSS, themeDisplay.getClayCSSURL());
				Assert.assertEquals(
					_URL_MAIN_RTL_CSS, themeDisplay.getMainCSSURL());
			}
			else {
				Assert.assertEquals(
					_URL_CLAY_CSS, themeDisplay.getClayCSSURL());
				Assert.assertEquals(
					_URL_MAIN_CSS, themeDisplay.getMainCSSURL());
			}
		}
		else {
			MatcherAssert.assertThat(
				themeDisplay.getClayCSSURL(),
				CoreMatchers.startsWith("/o/classic-theme/css/clay."));
			MatcherAssert.assertThat(
				themeDisplay.getMainCSSURL(),
				CoreMatchers.startsWith("/o/classic-theme/css/main."));
		}
	}

	private LifecycleAction _getLifecycleAction() {
		Bundle bundle = FrameworkUtil.getBundle(
			ClientExtensionServicePreActionTest.class);

		ServiceTrackerList<LifecycleAction> lifecycleActions =
			ServiceTrackerListFactory.open(
				bundle.getBundleContext(), LifecycleAction.class,
				"(key=servlet.service.events.pre)");

		for (LifecycleAction lifecycleAction : lifecycleActions) {
			Class<?> clazz = lifecycleAction.getClass();

			if (Objects.equals(
					clazz.getName(),
					"com.liferay.client.extension.internal.events." +
						"ClientExtensionsServicePreAction")) {

				return lifecycleAction;
			}
		}

		throw new AssertionError(
			"ClientExtensionsServicePreAction is not registered");
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Layout layout, Map<String, String> params)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameters(params);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLifecycleRender(true);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processServicePreAction(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		LifecycleAction lifecycleAction = _getLifecycleAction();

		lifecycleAction.processLifecycleEvent(
			new LifecycleEvent(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	private static final String _URL_CLAY_CSS =
		"http://" + RandomTestUtil.randomString() + ".com/styles.css";

	private static final String _URL_CLAY_RTL_CSS =
		"http://" + RandomTestUtil.randomString() + ".com/styles_rtl.css";

	private static final String _URL_FAVICON =
		"http://" + RandomTestUtil.randomString() + ".com";

	private static final String _URL_MAIN_CSS =
		"http://" + RandomTestUtil.randomString() + ".com/main.css";

	private static final String _URL_MAIN_RTL_CSS =
		"http://" + RandomTestUtil.randomString() + ".com/main_rtl.css";

	private ClientExtensionEntry _clientExtensionEntry;

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

}