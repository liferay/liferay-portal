/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class GetLayoutActionsMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	@TestInfo({"LPD-110311", "LPS-132422"})
	public void testGetActionDropdownItems() throws Exception {
		_assertActionDropdownItems(
			_layout.getPlid(), "Edit", "Translate", "View", "Preview Draft",
			"Convert to Page Template", "Make a Copy", "Export for Translation",
			"Import Translation", "Configure", "Permissions", "Delete");

		Layout layout = LayoutTestUtil.addTypeEmptyLayout(_group);

		_assertActionDropdownItems(layout.getPlid(), "Edit", "Delete");

		_testGetActionDropdownItemsWithLayoutPortlet();
		_testGetActionDropdownItemsWithMasterLayoutPortlet();
	}

	private void _assertActionDropdownItems(long plid, String... actions)
		throws Exception {

		Assert.assertEquals(
			Arrays.asList(actions), _getActionDropdownItemLabels(plid));
	}

	private void _assertNoOrphanWidgetsActionDropdownItem(long plid)
		throws Exception {

		List<String> actionDropdownItemLabels = _getActionDropdownItemLabels(
			plid);

		Assert.assertFalse(actionDropdownItemLabels.contains("Orphan Widgets"));
	}

	private List<String> _getActionDropdownItemLabels(long plid)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(plid);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			byteArrayOutputStream.toString());

		JSONArray actionDropdownItemsJSONArray =
			_getActionDropdownItemsJSONArray(
				jsonObject.getJSONArray("actions"));

		List<String> actionDropdownItemLabels = new ArrayList<>();

		for (int i = 0; i < actionDropdownItemsJSONArray.length(); i++) {
			JSONObject dropdownItemJSONObject =
				actionDropdownItemsJSONArray.getJSONObject(i);

			actionDropdownItemLabels.add(
				dropdownItemJSONObject.getString("label"));
		}

		return actionDropdownItemLabels;
	}

	private JSONArray _getActionDropdownItemsJSONArray(JSONArray jsonArray) {
		JSONArray allDropdownItemsJSONArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

			for (int j = 0; j < itemsJSONArray.length(); j++) {
				allDropdownItemsJSONArray.put(itemsJSONArray.getJSONObject(j));
			}
		}

		return allDropdownItemsJSONArray;
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(long plid)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter("plid", String.valueOf(plid));

		return mockLiferayResourceRequest;
	}

	private String _getPortletId(Layout layout) {
		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		List<String> portletIds =
			_portletRegistry.getFragmentEntryLinkPortletIds(
				fragmentEntryLinks.get(0));

		Assert.assertEquals(portletIds.toString(), 1, portletIds.size());

		return portletIds.get(0);
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testGetActionDropdownItemsWithLayoutPortlet()
		throws Exception {

		Layout draftLayout = _layout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			draftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		_layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				_getPortletId(_layout)));

		_assertNoOrphanWidgetsActionDropdownItem(_layout.getPlid());
	}

	private void _testGetActionDropdownItemsWithMasterLayoutPortlet()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		Layout masterDraftLayout = masterLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addPortletToLayout(
			masterDraftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(masterDraftLayout, masterLayout);

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_group, false, false,
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		_portletPreferencesLocalService.addPortletPreferences(
			layout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			_getPortletId(masterLayout), null, null);

		_assertNoOrphanWidgetsActionDropdownItem(layout.getPlid());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/get_layout_actions")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletRegistry _portletRegistry;

}