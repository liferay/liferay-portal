/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shakir Shamim
 */
@RunWith(Arquillian.class)
public class DLItemSelectorViewDisplayContextTest {

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
	public void testGetRepositoryEntries() throws Exception {
		_testGetRepositoryEntriesWhenCurIsInRange();
		_testGetRepositoryEntriesWhenCurIsOutOfRange();
	}

	@Test
	@TestInfo("LPD-116823")
	public void testGetUploadURL() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		Assert.assertNotNull(_getUploadURL());

		AssetTestUtil.addVocabulary(
			_depotEntry.getGroupId(),
			PortalUtil.getClassNameId(DLFileEntry.class),
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true, false);

		Assert.assertNull(_getUploadURL());
	}

	private List<Object> _getRepositoryEntries(
			long folderId, int cur, int delta)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockHttpServletRequest.setParameter(
			SearchContainer.DEFAULT_CUR_PARAM, String.valueOf(cur));
		mockHttpServletRequest.setParameter(
			SearchContainer.DEFAULT_DELTA_PARAM, String.valueOf(delta));
		mockHttpServletRequest.setParameter(
			"folderId", String.valueOf(folderId));

		_itemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			new FileItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), false);

		return ReflectionTestUtil.invoke(
			mockHttpServletRequest.getAttribute(
				"DL_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT"),
			"getRepositoryEntries", new Class<?>[0]);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Object _getUploadURL() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_itemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			new FileItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), false);

		Object dlItemSelectorViewDisplayContext =
			mockHttpServletRequest.getAttribute(
				"DL_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT");

		ReflectionTestUtil.invoke(
			dlItemSelectorViewDisplayContext, "getRepositoryEntries",
			new Class<?>[0]);

		return ReflectionTestUtil.invoke(
			dlItemSelectorViewDisplayContext, "getUploadURL",
			new Class<?>[] {LiferayPortletResponse.class},
			new MockLiferayPortletRenderResponse());
	}

	private void _testGetRepositoryEntriesWhenCurIsInRange() throws Exception {
		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		DLAppTestUtil.addFileEntry(folder);
		DLAppTestUtil.addFileEntry(folder);

		List<Object> repositoryEntries = _getRepositoryEntries(
			folder.getFolderId(), 2, 1);

		Assert.assertEquals(
			repositoryEntries.toString(), 1, repositoryEntries.size());
	}

	private void _testGetRepositoryEntriesWhenCurIsOutOfRange()
		throws Exception {

		Folder folder = DLAppTestUtil.addFolder(_group.getGroupId());

		DLAppTestUtil.addFileEntry(folder);
		DLAppTestUtil.addFileEntry(folder);

		List<Object> repositoryEntries = _getRepositoryEntries(
			folder.getFolderId(), 2, 5);

		Assert.assertEquals(
			repositoryEntries.toString(), 2, repositoryEntries.size());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.item.selector.web.internal.file.DLFileItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<FileItemSelectorCriterion> _itemSelectorView;

}