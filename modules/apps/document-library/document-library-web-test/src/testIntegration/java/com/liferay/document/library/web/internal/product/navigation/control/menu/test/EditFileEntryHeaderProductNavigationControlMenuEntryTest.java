/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.product.navigation.control.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.portlet.Portlet;

import jodd.net.MimeTypes;

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
public class EditFileEntryHeaderProductNavigationControlMenuEntryTest {

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
	public void testIsShowFileEntryHeaderInAnotherView() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(_addFileEntry());

		mockHttpServletRequest.setParameter("mvcRenderCommandName", "/");

		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(mockHttpServletRequest));
	}

	@Test
	public void testIsShowFileEntryHeaderInViewerView() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(fileEntry);

		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/document_library/view_file_entry");

		_deleteResourcePermissions(fileEntry);

		Assert.assertTrue(
			_productNavigationControlMenuEntry.isShow(mockHttpServletRequest));
	}

	@Test
	public void testIsShowFileEntryHeaderWithGuestPermissions()
		throws Exception {

		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(_addFileEntry())));
	}

	@Test
	public void testIsShowFileEntryHeaderWithoutFileEntry() throws Exception {
		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(null)));
	}

	@Test
	public void testIsShowFileEntryHeaderWithoutGuestPermissions()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_deleteResourcePermissions(fileEntry);

		Assert.assertTrue(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(fileEntry)));
	}

	private FileEntry _addFileEntry() throws Exception {
		return _dlAppService.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			MimeTypes.MIME_APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			StringPool.BLANK, (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _deleteResourcePermissions(FileEntry fileEntry)
		throws Exception {

		_resourcePermissionLocalService.deleteResourcePermissions(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, fileEntry.getFileEntryId());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			FileEntry fileEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"mvcRenderCommandName", "/document_library/edit_file_entry");

		if (fileEntry != null) {
			mockHttpServletRequest.setAttribute(
				WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY, fileEntry);
		}

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayResourceResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private Object _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTROL_PANEL);

		themeDisplay.setLayout(layout);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.DLAdminPortlet"
	)
	private Portlet _portlet;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.product.navigation.control.menu.EditFileEntryHeaderProductNavigationControlMenuEntry"
	)
	private ProductNavigationControlMenuEntry
		_productNavigationControlMenuEntry;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}