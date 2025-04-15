/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockBodyContent;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Kyle Stiemann
 */
public class HtmlTopTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDataSennaTrackAttribute() throws Exception {
		_testDataSennaTrackAttributeAdded(
			"<script type=\"text/javascript\" " +
				"src=\"http://liferay.com/javascript-file.js\"></script>",
			"permanent");
		_testDataSennaTrackAttributeAdded(
			"<link rel=\"stylesheet\" type=\"text/css\" " +
				"src=\"http://liferay.com/css-file.css\">",
			"temporary");
		_testDataSennaTrackAttributeAdded(
			"<style type=\"text/css\">.example{background-color:red;}</style>",
			"temporary");
		_testDataSennaTrackAttributeAdded(
			"<script type=\"text/javascript\" " +
				"src=\"http://liferay.com/javascript-file.js\" " +
					"data-senna-track=\"temporary\"></script>",
			"temporary");
		_testDataSennaTrackAttributeAdded(
			"<link rel=\"stylesheet\" type=\"text/css\" " +
				"src=\"http://liferay.com/css-file.css\" " +
					"data-senna-track=\"permanent\">",
			"permanent");
		_testDataSennaTrackAttributeAdded(
			"<style type=\"text/css\" data-senna-track=\"permanent\"" +
				">.example{background-color:red;}</style>",
			"permanent");
		_testDataSennaTrackAttributeAdded(
			"<meta content=\"initial-scale=1.0, width=device-width\" " +
				"name=\"viewport\" />",
			null);
	}

	private void _assertContainsRegex(
		String string, String containedRegex, String message) {

		Pattern pattern = Pattern.compile(containedRegex);

		Matcher matcher = pattern.matcher(string);

		Assert.assertTrue(message, matcher.find());
	}

	private String _getElementAttributes(String element) {
		Matcher matcher = _getElementNameAndAttributesPattern.matcher(element);

		Assert.assertTrue(matcher.find());

		return matcher.group(_ELEMENT_ATTRIBUTES_GROUP_INDEX);
	}

	private String _getElementName(String element) {
		Matcher matcher = _getElementNameAndAttributesPattern.matcher(element);

		Assert.assertTrue(matcher.find());

		return matcher.group(_ELEMENT_NAME_GROUP_INDEX);
	}

	private void _testDataSennaTrackAttributeAdded(
			String element, String expectedDataSennaTrackValue)
		throws Exception {

		HtmlTopTag htmlTopTag = new HtmlTopTag();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		final JspWriter jspWriter = new MockJspWriter(unsyncStringWriter);

		PageContext pageContext = new MockPageContext() {

			@Override
			public JspWriter getOut() {
				return jspWriter;
			}

			@Override
			public ServletRequest getRequest() {
				return new MockHttpServletRequest() {

					@Override
					public Object getAttribute(String name) {
						if (!WebKeys.OUTPUT_DATA.equals(name)) {
							return null;
						}

						return new OutputData() {

							@Override
							public void addDataSB(
								String outputKey, String webKey,
								StringBundler sb) {

								try {
									jspWriter.write(sb.toString());
								}
								catch (IOException ioException) {
									ReflectionUtil.throwException(ioException);
								}
							}

						};
					}

				};
			}

			@Override
			public BodyContent pushBody() {
				final UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				return new MockBodyContent(
					StringPool.BLANK, unsyncStringWriter) {

					@Override
					public String getString() {
						return unsyncStringWriter.toString();
					}

				};
			}

		};

		htmlTopTag.setPageContext(pageContext);

		htmlTopTag.setPosition("auto");

		htmlTopTag.doStartTag();

		BodyContent bodyContent = pageContext.pushBody();

		bodyContent.print(element);

		htmlTopTag.setBodyContent(bodyContent);

		htmlTopTag.doEndTag();

		String htmlTopTagOutputString = unsyncStringWriter.toString();

		String elementName = _getElementName(element);

		String elementBeginRegex = "<" + elementName + "[^>]+";

		String dataSennaTrackAttributeName = "data-senna-track";

		if (expectedDataSennaTrackValue != null) {
			String dataSennaTrackAttribute = StringBundler.concat(
				dataSennaTrackAttributeName, "=\"", expectedDataSennaTrackValue,
				"\"");

			String dataSennaTrackAttributeRegex =
				elementBeginRegex + dataSennaTrackAttribute + "[\\s>]";

			_assertContainsRegex(
				htmlTopTagOutputString, dataSennaTrackAttributeRegex,
				dataSennaTrackAttribute + " is not contained in " +
					htmlTopTagOutputString);

			int countOfDataSennaTrackAttributeNames = StringUtil.count(
				htmlTopTagOutputString, dataSennaTrackAttributeName);

			Assert.assertEquals(1, countOfDataSennaTrackAttributeNames);
		}
		else {
			Assert.assertFalse(
				htmlTopTagOutputString.contains(dataSennaTrackAttributeName));
		}

		String elementAttributes = _getElementAttributes(element);

		String originalElementAttributesRegex =
			elementBeginRegex + elementAttributes;

		_assertContainsRegex(
			htmlTopTagOutputString, originalElementAttributesRegex, null);
	}

	private static final int _ELEMENT_ATTRIBUTES_GROUP_INDEX = 2;

	private static final int _ELEMENT_NAME_GROUP_INDEX = 1;

	private static final Pattern _getElementNameAndAttributesPattern =
		Pattern.compile("<(\\S+)\\s+([^>]+)");

}