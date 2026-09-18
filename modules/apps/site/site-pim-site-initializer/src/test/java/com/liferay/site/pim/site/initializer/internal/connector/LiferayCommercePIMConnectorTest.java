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
import com.liferay.site.pim.site.initializer.connector.PIMConnectorField;
import com.liferay.site.pim.site.initializer.constants.PIMConnectorFieldConstants;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
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
	public void testGetPIMConnectorFields() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);

		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		List<PIMConnectorField> pimConnectorFields =
			liferayCommercePIMConnector.getPIMConnectorFields(LocaleUtil.US);

		Assert.assertEquals(
			pimConnectorFields.toString(), 5, pimConnectorFields.size());

		Assert.assertEquals(
			Arrays.asList(
				"catalogId", "description", "name", "skus[].sku", "tags"),
			TransformUtil.transform(
				pimConnectorFields, PIMConnectorField::getName));

		PIMConnectorField pimConnectorField = pimConnectorFields.get(0);

		Assert.assertEquals("catalog-id", pimConnectorField.getLabel());
		Assert.assertEquals(
			PIMConnectorFieldConstants.TYPE_LONG, pimConnectorField.getType());
		Assert.assertFalse(pimConnectorField.isMultiple());
		Assert.assertTrue(pimConnectorField.isRequired());

		pimConnectorField = pimConnectorFields.get(1);

		Assert.assertEquals("description", pimConnectorField.getLabel());
		Assert.assertEquals(
			PIMConnectorFieldConstants.TYPE_LOCALIZED_TEXT,
			pimConnectorField.getType());
		Assert.assertFalse(pimConnectorField.isMultiple());
		Assert.assertFalse(pimConnectorField.isRequired());

		pimConnectorField = pimConnectorFields.get(2);

		Assert.assertEquals("name", pimConnectorField.getLabel());
		Assert.assertEquals(
			PIMConnectorFieldConstants.TYPE_LOCALIZED_TEXT,
			pimConnectorField.getType());
		Assert.assertFalse(pimConnectorField.isMultiple());
		Assert.assertTrue(pimConnectorField.isRequired());

		pimConnectorField = pimConnectorFields.get(3);

		Assert.assertEquals("sku", pimConnectorField.getLabel());
		Assert.assertEquals(
			PIMConnectorFieldConstants.TYPE_TEXT, pimConnectorField.getType());
		Assert.assertFalse(pimConnectorField.isMultiple());
		Assert.assertTrue(pimConnectorField.isRequired());

		pimConnectorField = pimConnectorFields.get(4);

		Assert.assertEquals("tags", pimConnectorField.getLabel());
		Assert.assertEquals(
			PIMConnectorFieldConstants.TYPE_TEXT, pimConnectorField.getType());
		Assert.assertTrue(pimConnectorField.isMultiple());
		Assert.assertFalse(pimConnectorField.isRequired());
	}

	@Test
	public void testIsActive() {
		LiferayCommercePIMConnector liferayCommercePIMConnector =
			new LiferayCommercePIMConnector();

		Assert.assertTrue(
			liferayCommercePIMConnector.isActive(RandomTestUtil.randomLong()));
	}

}