/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.text.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.wiki.model.WikiPage;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class TextEngineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConvert() {
		TextEngine textEngine = new TextEngine();

		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.when(
			wikiPage.getContent()
		).thenReturn(
			"a <b>bold</b> & 1 < 2"
		);

		Assert.assertEquals(
			"<pre>a &lt;b&gt;bold&lt;/b&gt; &amp; 1 &lt; 2</pre>",
			textEngine.convert(wikiPage, null, null, null));
	}

}