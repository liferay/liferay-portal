/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.xml;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class StripDoctypeXMLReaderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInternalBufferWithInputStream() throws Exception {
		String prologue = "<?xml version=\"1.0\"?>";

		String xml = prologue + "<root />";

		byte[] bytes = new byte[prologue.length() + 1];

		InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

		StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(
			inputStream);

		StringBundler sb = new StringBundler();
		int length;

		while ((length = stripDoctypeFilter.read(bytes, 0, bytes.length)) > 0) {
			sb.append(new String(bytes, 0, length));
		}

		String result = sb.toString();

		Assert.assertEquals(xml, result);
	}

	@Test
	public void testInternalBufferWithReader() throws Exception {
		String prologue = "<?xml version=\"1.0\"?>";

		String xml = prologue + "<root />";

		char[] chars = new char[prologue.length() + 1];

		Reader reader = new StringReader(xml);

		StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(reader);

		StringBundler sb = new StringBundler();
		int length;

		while ((length = stripDoctypeFilter.read(chars, 0, chars.length)) > 0) {
			sb.append(new String(chars, 0, length));
		}

		String result = sb.toString();

		Assert.assertEquals(xml, result);
	}

	@Test(timeout = 1000)
	@TestInfo("LPD-110183")
	public void testReadInputStream() throws Exception {
		byte[] buff = new byte[4096];

		for (int i = 0; i < _ORIGINAL_XML.length; i++) {
			String xml = _ORIGINAL_XML[i];

			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

			StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(
				inputStream);

			int length = stripDoctypeFilter.read(buff, 0, buff.length);

			String result = new String(buff, 0, length);

			Assert.assertEquals(_SANITIZED_XML[i], result);
		}

		for (String xml : _UNTERMINATED_DOCTYPE_XML) {
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

			StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(
				inputStream);

			Assert.assertEquals(
				-1, stripDoctypeFilter.read(buff, 0, buff.length));
		}
	}

	@Test(timeout = 1000)
	@TestInfo("LPD-110183")
	public void testReadReader() throws Exception {
		char[] chars = new char[4096];

		for (int i = 0; i < _ORIGINAL_XML.length; i++) {
			String xml = _ORIGINAL_XML[i];

			Reader reader = new StringReader(xml);

			StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(
				reader);

			int length = stripDoctypeFilter.read(chars, 0, chars.length);

			String result = new String(chars, 0, length);

			Assert.assertEquals(_SANITIZED_XML[i], result);
		}

		for (String xml : _UNTERMINATED_DOCTYPE_XML) {
			Reader reader = new StringReader(xml);

			StripDoctypeFilter stripDoctypeFilter = new StripDoctypeFilter(
				reader);

			Assert.assertEquals(
				-1, stripDoctypeFilter.read(chars, 0, chars.length));
		}
	}

	private static final String[] _ORIGINAL_XML = {
		"<?xml version=\"1.0\"?><!DOCTYPE root><root />",
		"<!DOCTYPE root [<!ELEMENT root ANY >]><root />",
		"<!-- comment --><!DOCTYPE root [<!ELEMENT root ANY >]><root />",
		"<?xml version=\"1.0\"?><!-- comment --><!DOCTYPE root [" +
			"<!ELEMENT root ANY >]><root />",
		"<?xml version=\"1.0\"?><root />",
		"<?xml version=\"1.0\"?><root attribute=\"<!DOCTYPE root>\"/>"
	};

	private static final String[] _SANITIZED_XML = {
		"<?xml version=\"1.0\"?><root />", "<root />",
		"<!-- comment --><root />",
		"<?xml version=\"1.0\"?><!-- comment --><root />",
		"<?xml version=\"1.0\"?><root />",
		"<?xml version=\"1.0\"?><root attribute=\"<!DOCTYPE root>\"/>"
	};

	private static final String[] _UNTERMINATED_DOCTYPE_XML = {
		"<!D", "<!DOCTYPE root", "<!DOCTYPE root [<!ELEMENT root ANY >"
	};

}