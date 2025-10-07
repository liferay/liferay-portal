/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.jasper.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.reports.engine.ByteArrayReportResultContainer;
import com.liferay.portal.reports.engine.MemoryReportDesignRetriever;
import com.liferay.portal.reports.engine.ReportDataSourceType;
import com.liferay.portal.reports.engine.ReportDesignRetriever;
import com.liferay.portal.reports.engine.ReportEngine;
import com.liferay.portal.reports.engine.ReportFormat;
import com.liferay.portal.reports.engine.ReportRequest;
import com.liferay.portal.reports.engine.ReportRequestContext;
import com.liferay.portal.reports.engine.ReportResultContainer;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael C. Han
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@RunWith(Arquillian.class)
@Sync
public class ReportEngineImplTest extends TestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testCompileCsv() throws Exception {
		_compile(
			ReportDataSourceType.CSV, "CsvDataSource.txt",
			"CsvDataSourceReport.jrxml", ReportFormat.CSV);
	}

	@Test
	public void testCompileXls() throws Exception {
		_compile(
			ReportDataSourceType.XLS, "XlsDataSource.data.xls",
			"XlsDataSourceReport.jrxml", ReportFormat.CSV);
	}

	@Test
	public void testCompileXml() throws Exception {
		_compile(
			ReportDataSourceType.XML, "northwind.xml", "OrdersReport.jrxml",
			ReportFormat.CSV);
	}

	@Test
	public void testExportCsv() throws Exception {
		_export(ReportFormat.CSV);
	}

	@Test
	public void testExportPdf() throws Exception {
		_export(ReportFormat.PDF);
	}

	@Test
	public void testExportPdfWithChineseCharacters() throws Exception {
		_testExportPdfWithFontExtension(
			"dependencies/reports_admin_template_chinese_characters.jrxml",
			"中国文字");
	}

	@Test
	public void testExportPdfWithJapaneseCharacters() throws Exception {
		_testExportPdfWithFontExtension(
			"dependencies/reports_admin_template_japanese_characters.jrxml",
			"本語の文字");
	}

	@Test
	public void testExportRtf() throws Exception {
		_export(ReportFormat.RTF);
	}

	@Test
	public void testExportTxt() throws Exception {
		_export(ReportFormat.TXT);
	}

	@Test
	public void testExportXls() throws Exception {
		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"org.apache.poi.POIDocument", LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"org.apache.poi.hpsf.Section", LoggerTestUtil.WARN)) {

			_export(ReportFormat.XLS);
		}
	}

	@Test
	public void testExportXml() throws Exception {
		_export(ReportFormat.XML);
	}

	private ReportRequest _compile(
			ReportDataSourceType reportDataSourceType,
			String dataSourceFileName, String dataSourceReportFileName,
			ReportFormat reportFormat)
		throws Exception {

		ReportRequest reportRequest = _getReportRequest(
			reportDataSourceType, dataSourceFileName, dataSourceReportFileName,
			reportFormat);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"net.sf.jasperreports.engine.xml.JRTextFieldFactory",
				LoggerTestUtil.WARN)) {

			_reportEngine.compile(reportRequest);
		}

		return reportRequest;
	}

	private void _export(ReportFormat reportFormat) throws Exception {
		ReportRequest reportRequest = _compile(
			ReportDataSourceType.CSV, "CsvDataSource.txt",
			"CsvDataSourceReport.jrxml", reportFormat);

		ReportResultContainer reportResultContainer =
			new ByteArrayReportResultContainer(null);

		_reportEngine.execute(reportRequest, reportResultContainer);

		Assert.assertFalse(reportResultContainer.hasError());
		Assert.assertNotNull(reportResultContainer.getResults());
	}

	private ReportRequest _getReportRequest(
			ReportDataSourceType reportDataSourceType,
			String dataSourceFileName, String dataSourceReportFileName,
			ReportFormat reportFormat)
		throws Exception {

		ReportRequestContext reportRequestContext = new ReportRequestContext(
			reportDataSourceType);

		Class<?> reportEngineImplTestClass = getClass();

		ClassLoader classLoader = reportEngineImplTestClass.getClassLoader();

		InputStream dataSourceInputStream = classLoader.getResourceAsStream(
			dataSourceFileName);

		reportRequestContext.setAttribute(
			ReportRequestContext.DATA_SOURCE_BYTE_ARRAY,
			IOUtils.toByteArray(dataSourceInputStream));

		reportRequestContext.setAttribute(
			ReportRequestContext.DATA_SOURCE_COLUMN_NAMES,
			"city,id,name,address,state");

		InputStream dataSourceReportInputStream =
			reportEngineImplTestClass.getResourceAsStream(
				dataSourceReportFileName);

		if (dataSourceReportInputStream == null) {
			dataSourceReportInputStream = classLoader.getResourceAsStream(
				dataSourceReportFileName);
		}

		byte[] reportByteArray = IOUtils.toByteArray(
			dataSourceReportInputStream);

		ReportDesignRetriever reportDesignRetriever =
			new MemoryReportDesignRetriever(
				"test", new Date(), reportByteArray);

		return new ReportRequest(
			reportRequestContext, reportDesignRetriever,
			new HashMap<String, String>(), reportFormat.getValue());
	}

	private void _testExportPdfWithFontExtension(
			String dataSourceReportFileName, String expectedCharacters)
		throws Exception {

		ReportRequest reportRequest = _compile(
			ReportDataSourceType.CSV, "CsvDataSource.txt",
			dataSourceReportFileName, ReportFormat.PDF);

		ReportResultContainer reportResultContainer =
			new ByteArrayReportResultContainer(null);

		_reportEngine.execute(reportRequest, reportResultContainer);

		Assert.assertFalse(reportResultContainer.hasError());
		Assert.assertNotNull(reportResultContainer.getResults());

		try (PDDocument pdDocument = PDDocument.load(
				reportResultContainer.getResults())) {

			PDFTextStripper pdfTextStripper = new PDFTextStripper();

			String text = pdfTextStripper.getText(pdDocument);

			Assert.assertTrue(text.contains(expectedCharacters));
		}
	}

	@Inject
	private ReportEngine _reportEngine;

}