/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.type.GlobalCSSCET;
import com.liferay.client.extension.type.factory.CETImplFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class CETFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreateGlobalCSSCETFromClientExtensionEntry()
		throws Exception {

		CETFactoryImpl cetFactoryImpl = new CETFactoryImpl();

		cetFactoryImpl.activate();

		ClientExtensionEntry clientExtensionEntry = Mockito.mock(
			ClientExtensionEntry.class);

		Mockito.when(
			clientExtensionEntry.getProperties()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			clientExtensionEntry.getType()
		).thenReturn(
			ClientExtensionEntryConstants.TYPE_GLOBAL_CSS
		);

		Mockito.when(
			clientExtensionEntry.getTypeSettings()
		).thenReturn(
			"scope=company\nurl=http://example.com/a.css"
		);

		GlobalCSSCET globalCSSCET = (GlobalCSSCET)cetFactoryImpl.create(
			clientExtensionEntry, false);

		Assert.assertEquals("http://example.com/a.css", globalCSSCET.getURL());
		Assert.assertEquals("company", globalCSSCET.getScope());
	}

	@Test
	public void testTransformURLsWithAmpersands() {
		CETFactoryImpl cetFactoryImpl = new CETFactoryImpl();

		CETImplFactory<?> cetImplFactory = Mockito.mock(CETImplFactory.class);

		Mockito.when(
			cetImplFactory.isURLCETPropertyName(Mockito.anyString())
		).thenReturn(
			true
		);

		UnicodeProperties unicodeProperties = cetFactoryImpl.transformURLs(
			StringPool.BLANK, cetImplFactory,
			UnicodePropertiesBuilder.put(
				"url", "https://example.com/css2?family=Arial&family=Times"
			).build());

		Assert.assertEquals(
			"https://example.com/css2?family=Arial&family=Times",
			unicodeProperties.get("url"));
	}

	@Test
	public void testTransformURLsWithQuotes() {
		CETFactoryImpl cetFactoryImpl = new CETFactoryImpl();

		CETImplFactory<?> cetImplFactory = Mockito.mock(CETImplFactory.class);

		Mockito.when(
			cetImplFactory.isURLCETPropertyName(Mockito.anyString())
		).thenReturn(
			true
		);

		UnicodeProperties unicodeProperties = cetFactoryImpl.transformURLs(
			StringPool.BLANK, cetImplFactory,
			UnicodePropertiesBuilder.put(
				"url", "http://example.com' onmouseover='alert(1)"
			).build());

		Assert.assertEquals(
			"http://example.com&#39; onmouseover=&#39;alert(1)",
			unicodeProperties.get("url"));

		unicodeProperties = cetFactoryImpl.transformURLs(
			StringPool.BLANK, cetImplFactory,
			UnicodePropertiesBuilder.put(
				"url", "http://example.com\" onmouseover=\"alert(1)"
			).build());

		Assert.assertEquals(
			"http://example.com&#34; onmouseover=&#34;alert(1)",
			unicodeProperties.get("url"));
	}

}