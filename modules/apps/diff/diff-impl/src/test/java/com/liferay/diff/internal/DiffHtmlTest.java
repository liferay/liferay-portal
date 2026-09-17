/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.diff.internal;

import com.liferay.diff.DiffHtml;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProvider;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.StringReader;

import javax.xml.transform.TransformerFactory;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class DiffHtmlTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Mockito.when(
			_secureXMLFactoryProvider.newTransformerFactory()
		).thenReturn(
			_transformerFactory
		);

		ReflectionTestUtil.setFieldValue(
			SecureXMLFactoryProviderUtil.class, "_secureXMLFactoryProvider",
			_secureXMLFactoryProvider);
	}

	@Test
	public void testDiffMustNotHaveXMLDeclaration() throws Exception {
		String source = StringUtil.randomString();
		String target = StringUtil.randomString();

		String diff = _diffHtml.diff(
			new StringReader(source), new StringReader(target));

		Assert.assertFalse(diff.startsWith("<?xml"));
	}

	@Test
	public void testDiffWhereSourceAndTargetAreDifferent() throws Exception {
		String source = StringUtil.randomString();
		String target = StringUtil.randomString();

		String diff = _diffHtml.diff(
			new StringReader(source), new StringReader(target));

		Assert.assertNotEquals(source, diff);
		Assert.assertNotEquals(target, diff);
	}

	@Test
	public void testDiffWhereSourceAndTargetAreEmpty() throws Exception {
		Assert.assertEquals(
			StringPool.BLANK,
			_diffHtml.diff(
				new StringReader(StringPool.BLANK),
				new StringReader(StringPool.BLANK)));
	}

	@Test
	public void testDiffWhereSourceAndTargetAreIdentical() throws Exception {
		String content = StringUtil.randomString();

		Assert.assertEquals(
			content,
			_diffHtml.diff(
				new StringReader(content), new StringReader(content)));
	}

	@Test(expected = NullPointerException.class)
	public void testDiffWhereSourceAndTargetAreNull() throws Exception {
		_diffHtml.diff(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testDiffWhereSourceIsNull() throws Exception {
		_diffHtml.diff(null, new StringReader(StringUtil.randomString()));
	}

	@Test(expected = NullPointerException.class)
	public void testDiffWhereTargetIsNull() throws Exception {
		_diffHtml.diff(new StringReader(StringUtil.randomString()), null);
	}

	private static final SecureXMLFactoryProvider _secureXMLFactoryProvider =
		Mockito.mock(SecureXMLFactoryProvider.class);
	private static final TransformerFactory _transformerFactory =
		TransformerFactory.newInstance();

	private final DiffHtml _diffHtml = new DiffHtmlImpl();

}