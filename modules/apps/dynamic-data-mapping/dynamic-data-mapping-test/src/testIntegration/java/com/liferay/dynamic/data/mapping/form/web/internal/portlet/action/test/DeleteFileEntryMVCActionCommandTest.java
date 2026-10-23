/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.util.DDMFormUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Franca
 */
@RunWith(Arquillian.class)
public class DeleteFileEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ddmFormDefaultUser = DDMFormUtil.getDDMFormDefaultUser(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessAction() throws Exception {
		Assert.assertNotNull(_processAction(_addFileEntry()));
		Assert.assertNull(
			_processAction(
				_addTempFileEntry(DDMFormConstants.TEMP_FOLDER_NAME)));
		Assert.assertNotNull(
			_processAction(_addTempFileEntry(RandomTestUtil.randomString())));
	}

	private FileEntry _addFileEntry() throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, _ddmFormDefaultUser.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "file.txt",
			ContentTypes.TEXT_PLAIN, "file.txt", StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY), 0,
			null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private FileEntry _addTempFileEntry(String folderName) throws Exception {
		return TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _ddmFormDefaultUser.getUserId(), folderName,
			TempFileEntryUtil.getTempFileName("file.txt"),
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			ContentTypes.TEXT_PLAIN);
	}

	private DLFileEntry _processAction(FileEntry fileEntry) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"oldFileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_mvcActionCommand.processAction(
			new MockLiferayPortletActionRequest(mockHttpServletRequest),
			new MockLiferayPortletActionResponse());

		return _dlFileEntryLocalService.fetchDLFileEntry(
			fileEntry.getFileEntryId());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private User _ddmFormDefaultUser;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/dynamic_data_mapping_form/delete_file_entry"
	)
	private MVCActionCommand _mvcActionCommand;

}