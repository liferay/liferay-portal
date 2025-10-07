/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Neil Griffin
 */
public class HeaderResponseImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());
	}

	@Test
	public void testAddDependencyScriptDataTemplate() {
		ReflectionTestUtil.invoke(
			new HeaderResponseImpl(), "_validateParsedElements",
			new Class<?>[] {String.class},
			"<script id=\"dt\" type=\"data/template\">+<p>foo</p><p>bar</p>" +
				"</script>");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				HeaderResponseImpl.class.getName(), LoggerTestUtil.ERROR)) {

			try {
				ReflectionTestUtil.invoke(
					new HeaderResponseImpl(), "_validateParsedElements",
					new Class<?>[] {String.class},
					"<script id=\"dt\" type=\"text/javascript\"><p>foo</p><p>" +
						"bar</p></script>");

				Assert.fail(
					"Was able to add a <script type=\"text-javascript\"> " +
						"with multiple child elements");
			}
			catch (IllegalArgumentException illegalArgumentException) {
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(XMLStreamException.class, throwable.getClass());
		}
	}

	@Test
	public void testWellFormedXML() {
		_testTagName("link");
		_testTagName("LINK");
		_testTagName("meta");
		_testTagName("META");
	}

	private void _testTagName(String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		String openTagCloseTag = openTag + closeTag;

		String actual = ReflectionTestUtil.invoke(
			HeaderResponseImpl.class, "_addClosingTags",
			new Class<?>[] {String.class}, openTag);

		Assert.assertEquals(openTagCloseTag, actual);

		actual = ReflectionTestUtil.invoke(
			HeaderResponseImpl.class, "_addClosingTags",
			new Class<?>[] {String.class}, openTagCloseTag);

		Assert.assertEquals(openTagCloseTag, actual);

		actual = ReflectionTestUtil.invoke(
			HeaderResponseImpl.class, "_addClosingTags",
			new Class<?>[] {String.class}, "<head>" + openTag + "</head>");

		String openCloseTagInsideHead = "<head>" + openTagCloseTag + "</head>";

		Assert.assertEquals(openCloseTagInsideHead, actual);

		actual = ReflectionTestUtil.invoke(
			HeaderResponseImpl.class, "_addClosingTags",
			new Class<?>[] {String.class}, openCloseTagInsideHead);

		Assert.assertEquals(openCloseTagInsideHead, actual);
	}

}