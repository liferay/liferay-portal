/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.dashboard.web.test.util.ContentDashboardTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.text.Format;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Yurena Cabrera
 */
@RunWith(Arquillian.class)
public class GetContentDashboardItemsXlsMVCResourceCommandTest {

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
	public void testServeResource() throws Exception {
		_testServeResourceWithFileEntry();
		_testServeResourceWithFullLastPage();
		_testServeResourceWithJournalArticle();
	}

	private void _assertWorkbook(
		List<String> expectedWorkbookValues, Workbook actualWorkbook) {

		Assert.assertEquals(1, actualWorkbook.getNumberOfSheets());

		Sheet actualWorkbookSheet = actualWorkbook.getSheetAt(0);

		Assert.assertEquals(1, actualWorkbookSheet.getLastRowNum());
		_assertWorkbookRow(
			ListUtil.fromArray(
				"ID", "Title", "Author", "Type", "Subtype",
				"Site or Asset Library", "Status", "Categories", "Tags",
				"Modified Date", "Review Date", "Description", "Extension",
				"File Name", "Size", "Display Date", "Creation Date",
				"Languages Translated Into"),
			actualWorkbookSheet.getRow(0));
		_assertWorkbookRow(
			expectedWorkbookValues, actualWorkbookSheet.getRow(1));
	}

	private void _assertWorkbookRow(
		List<String> expectedRowValues, Row workbookRow) {

		for (short i = 0; i < workbookRow.getLastCellNum(); i++) {
			String actualWorkbookCellValue = StringPool.BLANK;
			Cell actualWorkbookCell = workbookRow.getCell(i);

			if (actualWorkbookCell != null) {
				actualWorkbookCellValue =
					actualWorkbookCell.getStringCellValue();
			}

			Assert.assertEquals(
				expectedRowValues.get(i), actualWorkbookCellValue);
		}
	}

	private ByteArrayOutputStream _serveResource(long groupId)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = ContentDashboardTestUtil.getThemeDisplay(
			_group);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter("groupId", String.valueOf(groupId));
		mockHttpServletRequest.setParameter("scopeId", String.valueOf(groupId));

		serviceContext.setRequest(mockHttpServletRequest);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest(mockHttpServletRequest);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayResourceRequest.setParameter(
			"groupId", String.valueOf(groupId));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.apache.poi.POIDocument", LoggerTestUtil.WARN)) {

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, mockLiferayResourceResponse);
		}

		return (ByteArrayOutputStream)
			mockLiferayResourceResponse.getPortletOutputStream();
	}

	private void _testServeResourceWithFileEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Date createDate = new Date();

		serviceContext.setCreateDate(createDate);

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"fileName.pdf", "application/pdf", new byte[0], createDate, null,
			createDate, serviceContext);

		List<String> expectedWorkbookValues = ListUtil.fromArray(
			String.valueOf(fileEntry.getFileEntryId()), "fileName.pdf",
			"Test Test", "Document", "Basic Document (Vectorial)",
			_group.getName(serviceContext.getLocale()), "Approved", "", "",
			_toString(fileEntry.getModifiedDate()), _toString(createDate), "",
			"pdf", "fileName.pdf", "0 B", "", _toString(createDate), "");

		ByteArrayOutputStream byteArrayOutputStream = _serveResource(
			_group.getGroupId());

		_assertWorkbook(
			expectedWorkbookValues,
			new HSSFWorkbook(
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));

		DLAppLocalServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());
	}

	private void _testServeResourceWithFullLastPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.search.configuration." +
						"DefaultSearchResultPermissionFilterConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"searchQueryResultWindowLimit", 1
					).build())) {

			ByteArrayOutputStream byteArrayOutputStream = _serveResource(
				_group.getGroupId());

			Workbook workbook = new HSSFWorkbook(
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

			Sheet sheet = workbook.getSheetAt(0);

			Assert.assertEquals(1, sheet.getLastRowNum());
		}

		JournalArticleLocalServiceUtil.deleteArticle(journalArticle);
	}

	private void _testServeResourceWithJournalArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Locale locale = LocaleUtil.getDefault();

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		List<String> expectedWorkbookValues = ListUtil.fromArray(
			String.valueOf(journalArticle.getResourcePrimKey()),
			journalArticle.getTitle(locale), "Test Test", "Web Content Article",
			ddmStructure.getName(locale), _group.getName(locale), "Approved",
			"", "", _toString(journalArticle.getModifiedDate()),
			StringPool.DASH, journalArticle.getDescription(locale), "", "", "",
			_toString(journalArticle.getDisplayDate()),
			_toString(journalArticle.getCreateDate()),
			StringUtil.merge(
				journalArticle.getAvailableLanguageIds(), StringPool.COMMA));

		ByteArrayOutputStream byteArrayOutputStream = _serveResource(
			_group.getGroupId());

		_assertWorkbook(
			expectedWorkbookValues,
			new HSSFWorkbook(
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
	}

	private String _toString(Date date) throws Exception {
		ThemeDisplay themeDisplay = ContentDashboardTestUtil.getThemeDisplay(
			_group);

		Format format = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), themeDisplay.getTimeZone());

		return format.format(date);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/content_dashboard/get_content_dashboard_items_xls"
	)
	private MVCResourceCommand _mvcResourceCommand;

}