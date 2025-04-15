/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.ServletContext;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class TextExtractorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = _textExtractor.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Class<?> tesseractOCRParserClass = classLoader.loadClass(
			"org.apache.tika.parser.ocr.TesseractOCRParser");

		Map<String, Boolean> tesseractPresent =
			ReflectionTestUtil.getAndSetFieldValue(
				tesseractOCRParserClass, "TESSERACT_PRESENT",
				new HashMap<String, Boolean>() {

					@Override
					public boolean containsKey(Object key) {
						return true;
					}

					@Override
					public Boolean get(Object key) {
						return Boolean.FALSE;
					}

				});

		_resetTikaConfigCloseable = () -> ReflectionTestUtil.setFieldValue(
			tesseractOCRParserClass, "TESSERACT_PRESENT", tesseractPresent);
	}

	@AfterClass
	public static void tearDownClass() throws IOException {
		_resetTikaConfigCloseable.close();
	}

	@Test
	public void testCustomTikaConfigXml() throws Exception {
		InputStream tikaCustomXmlInputStream = _getInputStream(
			_TIKA_CUSTOM_XML);

		File portalClassesTikaXml = new File(
			_getPortalClassesPath(), _TIKA_CUSTOM_XML);

		StreamUtil.transfer(
			tikaCustomXmlInputStream,
			new FileOutputStream(portalClassesTikaXml));

		try {
			_withTikaConfiguration(
				true,
				new String[] {
					ContentTypes.TEXT_HTML, ContentTypes.APPLICATION_PDF
				},
				_TIKA_CUSTOM_XML,
				() -> {
					String text = extractText("test.html");

					Assert.assertEquals("Extract test.", text);

					text = extractText("test.odt");

					Assert.assertEquals("", text);

					text = extractText("test-2010.pdf");

					Assert.assertEquals("", text);

					text = extractText("test.txt");

					Assert.assertEquals("Extract test.", text);
				});
		}
		finally {
			portalClassesTikaXml.delete();
		}
	}

	@Test
	public void testDoc() {
		String text = extractText("test.doc");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testDocx() {
		String text = extractText("test-2007.docx");

		Assert.assertTrue(text, text.contains("Extract test."));

		text = extractText("test-2010.docx");

		Assert.assertTrue(text, text.contains("Extract test."));
	}

	@Test
	public void testEmpty() {
		Assert.assertEquals(
			StringPool.BLANK,
			_textExtractor.extractText(
				new UnsyncByteArrayInputStream(new byte[0]), -1));
	}

	@Test
	public void testForkProcessEnabled() throws Exception {
		_withTikaConfiguration(
			true, new String[] {ContentTypes.APPLICATION_PDF}, null,
			() -> {
				String text = extractText("test-2010.pdf");

				Assert.assertEquals("Extract test.", text);

				text = extractText("test.pdf");

				Assert.assertEquals("Extract test.", text);
			});
	}

	@Test
	public void testHtml() {
		String text = extractText("test.html");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testJpg() {
		String text = extractText("test.jpg");

		Assert.assertEquals("", text);
	}

	@Test
	public void testOdt() {
		String text = extractText("test.odt");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testPdf() {
		String text = extractText("test-2010.pdf");

		Assert.assertEquals("Extract test.", text);

		text = extractText("test.pdf");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testPpt() {
		String text = extractText("test.ppt");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testPptx() {
		String text = extractText("test-2010.pptx");

		Assert.assertTrue(text, text.contains("Extract test."));
	}

	@Test
	public void testRtf() {
		String text = extractText("test.rtf");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testTxt() {
		String text = extractText("test.txt");

		Assert.assertEquals("Extract test.", text);
	}

	@Test
	public void testTxtEncodedWithShift_JIS() throws IOException {
		String expectedText = new String(
			StreamUtil.toByteArray(
				_getInputStream("test-encoding-Shift_JIS.txt")),
			Charset.forName("Shift_JIS"));

		Assert.assertEquals(
			expectedText.trim(), extractText("test-encoding-Shift_JIS.txt"));
	}

	@Test
	public void testWrongTikaConfigXml() {
		Object tikaConfigurationHelper = ReflectionTestUtil.getFieldValue(
			_textExtractor, "_tikaConfigurationHelper");

		Object originalConfiguration = ReflectionTestUtil.getFieldValue(
			tikaConfigurationHelper, "_tikaConfiguration");

		try {
			ReflectionTestUtil.invoke(
				tikaConfigurationHelper, "activate", new Class<?>[] {Map.class},
				HashMapBuilder.<String, Object>put(
					"tikaConfigXml", "wrong/tika.xml"
				).build());
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());
			Assert.assertEquals(
				"Unable to read tika configuration wrong/tika.xml",
				exception.getMessage());
			Assert.assertSame(
				originalConfiguration,
				ReflectionTestUtil.getFieldValue(
					tikaConfigurationHelper, "_tikaConfiguration"));
		}
	}

	@Test
	public void testXls() {
		String text = extractText("test.xls");

		Assert.assertEquals("Sheet1\n\tExtract test.", text);
	}

	@Test
	public void testXlsx() {
		String text = extractText("test-2010.xlsx");

		Assert.assertTrue(text, text.contains("Extract test."));
	}

	@Test
	public void testXml() {
		String text = extractText("test.xml");

		Assert.assertEquals("<test>Extract test.</test>", text);
	}

	@Test
	public void testZipWithPNGzTXt() {
		Assert.assertEquals(
			StringPool.BLANK, extractText("test-LPS-112649.zip"));
	}

	protected String extractText(String fileName) {
		String text = _textExtractor.extractText(_getInputStream(fileName), -1);

		return text.trim();
	}

	private InputStream _getInputStream(String fileName) {
		Class<?> clazz = getClass();

		return clazz.getResourceAsStream("dependencies/" + fileName);
	}

	private File _getPortalClassesPath() {
		ServletContext servletContext = ServletContextPool.get(
			ServletContextClassLoaderPool.getServletContextName(
				PortalClassLoaderUtil.getClassLoader()));

		return new File(
			servletContext.getRealPath(StringPool.SLASH), "WEB-INF/classes");
	}

	private void _withTikaConfiguration(
			boolean textExtractionForkProcessEnabled,
			String[] textExtractionForkProcessMimeTypes, String tikaConfigXml,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		HashMapDictionaryBuilder.HashMapDictionaryWrapper<String, Object>
			hashMapDictionaryWrapper =
				new HashMapDictionaryBuilder.HashMapDictionaryWrapper<>();

		if (textExtractionForkProcessEnabled) {
			hashMapDictionaryWrapper.put(
				"textExtractionForkProcessEnabled",
				textExtractionForkProcessEnabled);
			hashMapDictionaryWrapper.put(
				"textExtractionForkProcessMimeTypes",
				textExtractionForkProcessMimeTypes);
		}

		if (tikaConfigXml != null) {
			hashMapDictionaryWrapper.put("tikaConfigXml", tikaConfigXml);
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_TIKA_CONFIGURATION, hashMapDictionaryWrapper.build())) {

			unsafeRunnable.run();
		}
	}

	private static final String _TIKA_CONFIGURATION =
		"com.liferay.portal.tika.internal.configuration.TikaConfiguration";

	private static final String _TIKA_CUSTOM_XML = "tika-custom.xml";

	private static Closeable _resetTikaConfigCloseable;

	@Inject
	private static TextExtractor _textExtractor;

}