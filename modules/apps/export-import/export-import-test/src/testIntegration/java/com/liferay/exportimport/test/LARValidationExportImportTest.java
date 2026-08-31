/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.LayoutImportException;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León
 */
@RunWith(Arquillian.class)
public class LARValidationExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@Test
	public void testValidateImportLayoutsFileWithWrongBuildNumber()
		throws Exception {

		exportLayouts(
			new long[] {layout.getLayoutId()}, getExportParameterMap());

		_replaceHeaderAttribute("build-number", "6210");

		try {
			_validateImportLayoutsFile();

			Assert.fail();
		}
		catch (LayoutImportException layoutImportException) {
			Assert.assertEquals(
				LayoutImportException.TYPE_WRONG_BUILD_NUMBER,
				layoutImportException.getType());
		}
	}

	@Test
	public void testValidateImportLayoutsFileWithWrongSchemaVersion()
		throws Exception {

		exportLayouts(
			new long[] {layout.getLayoutId()}, getExportParameterMap());

		_replaceHeaderAttribute("schema-version", "1.0.0");

		try {
			_validateImportLayoutsFile();

			Assert.fail();
		}
		catch (LayoutImportException layoutImportException) {
			Assert.assertEquals(
				LayoutImportException.TYPE_WRONG_LAR_SCHEMA_VERSION,
				layoutImportException.getType());
		}
	}

	@Test
	public void testValidateImportPortletInfoWithWrongBuildNumber()
		throws Exception {

		_exportPortletInfo();

		_replaceHeaderAttribute("build-number", "6210");

		try {
			_validateImportPortletInfo();

			Assert.fail();
		}
		catch (LayoutImportException layoutImportException) {
			Assert.assertEquals(
				LayoutImportException.TYPE_WRONG_BUILD_NUMBER,
				layoutImportException.getType());
		}
	}

	@Test
	public void testValidateImportPortletInfoWithWrongSchemaVersion()
		throws Exception {

		_exportPortletInfo();

		_replaceHeaderAttribute("schema-version", "1.0.0");

		try {
			_validateImportPortletInfo();

			Assert.fail();
		}
		catch (LayoutImportException layoutImportException) {
			Assert.assertEquals(
				LayoutImportException.TYPE_WRONG_LAR_SCHEMA_VERSION,
				layoutImportException.getType());
		}
	}

	private void _exportPortletInfo() throws Exception {
		larFile = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							TestPropsValues.getUser(), layout.getPlid(),
							group.getGroupId(),
							BookmarksPortletKeys.BOOKMARKS_ADMIN,
							getExportParameterMap(), StringPool.BLANK)));
	}

	private void _replaceHeaderAttribute(String name, String value)
		throws Exception {

		File file = FileUtil.createTempFile("lar");

		FileUtil.copyFile(larFile, file);

		Document document = null;

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			document = SAXReaderUtil.read(
				zipReader.getEntryAsInputStream("manifest.xml"));
		}

		Element rootElement = document.getRootElement();

		Element headerElement = rootElement.element("header");

		headerElement.addAttribute(name, value);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter(file);

		zipWriter.addEntry("manifest.xml", document.formattedString());

		FileUtil.delete(larFile);

		larFile = file;
	}

	private void _validateImportLayoutsFile() throws Exception {
		User user = TestPropsValues.getUser();

		ExportImportLocalServiceUtil.validateImportLayoutsFile(
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					user.getUserId(), importedGroup.getGroupId(),
					StringPool.BLANK, StringPool.BLANK,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							user, importedGroup.getGroupId(), false, null,
							getImportParameterMap()),
					WorkflowConstants.STATUS_DRAFT, new ServiceContext()),
			larFile);
	}

	private void _validateImportPortletInfo() throws Exception {
		Layout importedGroupLayout = LayoutTestUtil.addTypePortletLayout(
			importedGroup);

		ExportImportLocalServiceUtil.validateImportPortletInfo(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							TestPropsValues.getUser(),
							importedGroupLayout.getPlid(),
							importedGroup.getGroupId(),
							BookmarksPortletKeys.BOOKMARKS_ADMIN,
							getImportParameterMap())),
			larFile);
	}

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}