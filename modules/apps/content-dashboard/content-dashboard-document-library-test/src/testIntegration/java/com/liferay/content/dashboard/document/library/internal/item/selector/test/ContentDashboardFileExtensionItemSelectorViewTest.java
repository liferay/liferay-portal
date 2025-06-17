/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.selector.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.file.criterion.FileExtensionItemSelectorCriterion;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
@Sync
public class ContentDashboardFileExtensionItemSelectorViewTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetData() throws Exception {
		_addFileEntry(_group, "java");
		_addFileEntry(_group, "liferay");
		_addFileEntry(_group, "mp3");
		_addFileEntry(_group, "mp4");
		_addFileEntry(_group, "pdf");
		_addFileEntry(_group, "png");
		_addFileEntry(_group, "ppt");
		_addFileEntry(_group, "txt");
		_addFileEntry(_group, "xls");
		_addFileEntry(_group, "zip");

		Map<String, Object> data = _getData(null);

		JSONArray fileExtensionGroupsJSONArray = (JSONArray)data.get(
			"fileExtensionGroups");

		Assert.assertEquals(10, fileExtensionGroupsJSONArray.length());

		_assertExtensionGroupJSONObject(
			"mp3", "document-multimedia", "Audio",
			fileExtensionGroupsJSONArray.getJSONObject(0));
		_assertExtensionGroupJSONObject(
			"java", "document-code", "Code",
			fileExtensionGroupsJSONArray.getJSONObject(1));
		_assertExtensionGroupJSONObject(
			"zip", "document-compressed", "Compressed",
			fileExtensionGroupsJSONArray.getJSONObject(2));
		_assertExtensionGroupJSONObject(
			"png", "document-image", "Image",
			fileExtensionGroupsJSONArray.getJSONObject(3));
		_assertExtensionGroupJSONObject(
			"ppt", "document-presentation", "Presentation",
			fileExtensionGroupsJSONArray.getJSONObject(4));
		_assertExtensionGroupJSONObject(
			"xls", "document-table", "Spreadsheet",
			fileExtensionGroupsJSONArray.getJSONObject(5));
		_assertExtensionGroupJSONObject(
			"txt", "document-text", "Text",
			fileExtensionGroupsJSONArray.getJSONObject(6));
		_assertExtensionGroupJSONObject(
			"pdf", "document-vector", "Vectorial",
			fileExtensionGroupsJSONArray.getJSONObject(7));
		_assertExtensionGroupJSONObject(
			"mp4", "document-multimedia", "Video",
			fileExtensionGroupsJSONArray.getJSONObject(8));
		_assertExtensionGroupJSONObject(
			"liferay", "document-default", "Other",
			fileExtensionGroupsJSONArray.getJSONObject(9));

		Assert.assertNotNull(data.get("itemSelectorSaveEvent"));
	}

	@Test
	public void testGetDataSelectedGroup() throws Exception {
		_addFileEntry(_group, "java");
		_addFileEntry(_group, "liferay");

		Group group2 = GroupTestUtil.addGroup();

		try {
			Map<String, Object> data = _getData(group2);

			JSONArray fileExtensionGroupsJSONArray = (JSONArray)data.get(
				"fileExtensionGroups");

			Assert.assertEquals(0, fileExtensionGroupsJSONArray.length());

			_addFileEntry(group2, "xls");
			_addFileEntry(group2, "zip");

			data = _getData(group2);

			fileExtensionGroupsJSONArray = (JSONArray)data.get(
				"fileExtensionGroups");

			Assert.assertEquals(2, fileExtensionGroupsJSONArray.length());
		}
		finally {
			_groupLocalService.deleteGroup(group2);
		}
	}

	private FileEntry _addFileEntry(Group group, String fileExtension)
		throws Exception {

		return DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + "." + fileExtension,
			MimeTypesUtil.getExtensionContentType(fileExtension), new byte[0],
			null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertExtensionGroupJSONObject(
		String expectedFileExtension, String expectedIcon, String expectedLabel,
		JSONObject fileExtensionGroupJSONObject) {

		Assert.assertEquals(
			expectedIcon, fileExtensionGroupJSONObject.getString("icon"));
		Assert.assertEquals(
			expectedLabel, fileExtensionGroupJSONObject.getString("label"));

		JSONArray fileExtensionsJSONArray =
			fileExtensionGroupJSONObject.getJSONArray("fileExtensions");

		Assert.assertEquals(1, fileExtensionsJSONArray.length());

		JSONObject fileExtensionJSONObject =
			fileExtensionsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			expectedFileExtension,
			fileExtensionJSONObject.getString("fileExtension"));
	}

	private Map<String, Object> _getData(Group group) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			"null-" + WebKeys.CURRENT_PORTLET_URL, new MockLiferayPortletURL());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = _getThemeDisplay(_group);

		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		FileExtensionItemSelectorCriterion fileExtensionItemSelectorCriterion =
			new FileExtensionItemSelectorCriterion();

		if (group != null) {
			fileExtensionItemSelectorCriterion.setSelectedGroupIds(
				new long[] {group.getGroupId()});
		}

		_contentDashboardFileExtensionItemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			fileExtensionItemSelectorCriterion, new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), true);

		Object contentDashboardFileExtensionItemSelectorViewDisplayContext =
			mockHttpServletRequest.getAttribute(
				"com.liferay.content.dashboard.document.library.internal." +
					"item.display.context.ContentDashboardFileExtensionItem" +
						"SelectorViewDisplayContext");

		return ReflectionTestUtil.invoke(
			contentDashboardFileExtensionItemSelectorViewDisplayContext,
			"getData", new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay(Group group) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.document.library.internal.item.selector.ContentDashboardFileExtensionItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<ItemSelectorCriterion>
		_contentDashboardFileExtensionItemSelectorView;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}