/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class LiferayCommercePIMConnectorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetKey() {
		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertEquals(
			"liferay-commerce", liferayCommercePIMConnector.getKey());
	}

	@Test
	public void testGetName() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, "liferay-commerce")
		).thenReturn(
			"Liferay Commerce"
		);

		languageUtil.setLanguage(language);

		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertEquals(
			"Liferay Commerce",
			liferayCommercePIMConnector.getName(LocaleUtil.US));
	}

	@Test
	public void testGetPIMConnectorChannelFields() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, "catalog-id")
		).thenReturn(
			"Catalog ID"
		);

		Mockito.when(
			language.get(LocaleUtil.US, "description")
		).thenReturn(
			"Description"
		);

		Mockito.when(
			language.get(LocaleUtil.US, "name")
		).thenReturn(
			"Name"
		);

		Mockito.when(
			language.get(LocaleUtil.US, "sku")
		).thenReturn(
			"SKU"
		);

		Mockito.when(
			language.get(LocaleUtil.US, "tags")
		).thenReturn(
			"Tags"
		);

		languageUtil.setLanguage(language);

		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		List<PIMConnectorChannelField> pimConnectorChannelFields =
			liferayCommercePIMConnector.getPIMConnectorChannelFields(
				LocaleUtil.US);

		Assert.assertEquals(
			pimConnectorChannelFields.toString(), 5,
			pimConnectorChannelFields.size());

		Assert.assertEquals(
			Arrays.asList(
				"catalogId", "description", "name", "skus[].sku", "tags"),
			TransformUtil.transform(
				pimConnectorChannelFields, PIMConnectorChannelField::getName));

		PIMConnectorChannelField pimConnectorChannelField =
			pimConnectorChannelFields.get(0);

		Assert.assertEquals("Catalog ID", pimConnectorChannelField.getLabel());
		Assert.assertFalse(pimConnectorChannelField.isMultiple());
		Assert.assertTrue(pimConnectorChannelField.isRequired());

		pimConnectorChannelField = pimConnectorChannelFields.get(1);

		Assert.assertEquals("Description", pimConnectorChannelField.getLabel());
		Assert.assertFalse(pimConnectorChannelField.isMultiple());
		Assert.assertFalse(pimConnectorChannelField.isRequired());

		pimConnectorChannelField = pimConnectorChannelFields.get(2);

		Assert.assertEquals("Name", pimConnectorChannelField.getLabel());
		Assert.assertFalse(pimConnectorChannelField.isMultiple());
		Assert.assertTrue(pimConnectorChannelField.isRequired());

		pimConnectorChannelField = pimConnectorChannelFields.get(3);

		Assert.assertEquals("SKU", pimConnectorChannelField.getLabel());
		Assert.assertFalse(pimConnectorChannelField.isMultiple());
		Assert.assertTrue(pimConnectorChannelField.isRequired());

		pimConnectorChannelField = pimConnectorChannelFields.get(4);

		Assert.assertEquals("Tags", pimConnectorChannelField.getLabel());
		Assert.assertTrue(pimConnectorChannelField.isMultiple());
		Assert.assertFalse(pimConnectorChannelField.isRequired());
	}

	@Test
	public void testIsActive() {
		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertTrue(
			liferayCommercePIMConnector.isActive(RandomTestUtil.randomLong()));
	}

}