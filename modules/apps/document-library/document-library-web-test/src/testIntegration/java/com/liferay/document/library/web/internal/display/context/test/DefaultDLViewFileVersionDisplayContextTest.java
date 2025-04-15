/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownGroupItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
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
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
@Sync
public class DefaultDLViewFileVersionDisplayContextTest {

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
	public void testCreateViewUsageDropdownItemWithoutUsage() throws Exception {
		DropdownItem dropdownItem = _getViewUsageDropdownItem();

		Assert.assertEquals("list-ul", dropdownItem.get("icon"));
		Assert.assertEquals("View Usages", dropdownItem.get("label"));
		Assert.assertTrue((Boolean)dropdownItem.get("disabled"));
	}

	@Test
	public void testCreateViewUsageDropdownItemWithUsage() throws Exception {
		_addLayoutClassedModelUsage(_fileEntry);

		DropdownItem dropdownItem = _getViewUsageDropdownItem();

		Assert.assertEquals("list-ul", dropdownItem.get("icon"));
		Assert.assertEquals("View Usages", dropdownItem.get("label"));
		Assert.assertFalse((Boolean)dropdownItem.get("disabled"));
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

	private List<DropdownItem> _getDropdownGroupItems(
		List<DropdownItem> dropdownItems, int groupIndex) {

		DropdownGroupItem dropdownGroupItem =
			(DropdownGroupItem)dropdownItems.get(groupIndex);

		return (List<DropdownItem>)dropdownGroupItem.get("items");
	}

	private List<DropdownItem> _getDropdownItems() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		String portletName = RandomTestUtil.randomString();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest() {

				@Override
				public String getPortletName() {
					return portletName;
				}

			};

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				portletName, StringPool.DASH, WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		DLViewFileVersionDisplayContext dLViewFileVersionDisplayContext =
			_dLDisplayContextProvider.getDLViewFileVersionDisplayContext(
				mockHttpServletRequest, new MockHttpServletResponse(),
				_fileEntry.getFileVersion());

		return dLViewFileVersionDisplayContext.getActionDropdownItems();
	}

	private DropdownItem _getViewUsageDropdownItem() throws Exception {
		List<DropdownItem> dropdownGroupItems = _getDropdownGroupItems(
			_getDropdownItems(), 1);

		return dropdownGroupItems.get(3);
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.display.context.DLDisplayContextProviderImpl",
		type = Inject.NoType.class
	)
	private DLDisplayContextProvider _dLDisplayContextProvider;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ItemSelector _itemSelector;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private ServiceContext _serviceContext;

}