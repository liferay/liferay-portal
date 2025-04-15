/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutClassedModelUsageActionMenuContributor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntryLayoutClassedModelUsageActionMenuContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_fileEntry = _addDLFileEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testGetLayoutClassedModelUsageActionDropdownItems()
		throws Exception {

		LayoutClassedModelUsage layoutClassedModelUsage =
			_addLayoutClassedModelUsage(_fileEntry);

		HttpServletRequest mockHttpServletRequest = _getHttpServletRequest();

		List<DropdownItem> dropdownItems =
			_layoutClassedModelUsageActionMenuContributor.
				getLayoutClassedModelUsageActionDropdownItems(
					mockHttpServletRequest, layoutClassedModelUsage);

		DropdownItem dropdownItem = dropdownItems.get(0);

		String href = GetterUtil.getString(dropdownItem.get("href"));

		Assert.assertNotNull(href);

		Layout layout = _layoutLocalService.getLayout(
			layoutClassedModelUsage.getPlid());

		Assert.assertTrue(
			StringUtil.contains(
				href, layout.getFriendlyURL(LocaleUtil.getDefault()),
				StringPool.BLANK));

		Assert.assertEquals("View in Page", dropdownItem.get("label"));
	}

	private FileEntry _addDLFileEntry(String fileName, String content)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			content.getBytes(), null, null, null, _serviceContext);
	}

	private LayoutClassedModelUsage _addLayoutClassedModelUsage(
			FileEntry fileEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), PortalUtil.getClassNameId(FileEntry.class),
			fileEntry.getFileEntryId(), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			layout.getPlid(), _serviceContext);
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "model.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
		type = LayoutClassedModelUsageActionMenuContributor.class
	)
	private LayoutClassedModelUsageActionMenuContributor
		_layoutClassedModelUsageActionMenuContributor;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ServiceContext _serviceContext;

}