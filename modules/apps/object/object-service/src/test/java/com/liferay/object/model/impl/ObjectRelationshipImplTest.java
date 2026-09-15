/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Nathaly Gomes
 */
public class ObjectRelationshipImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());

		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	@Test
	public void testGetDefaultLanguageId() {
		ObjectRelationship objectRelationship = new ObjectRelationshipImpl();

		objectRelationship.setDescriptionMap(
			Collections.singletonMap(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()),
			LocaleUtil.SPAIN);
		objectRelationship.setLabelMap(
			Collections.singletonMap(
				LocaleUtil.GERMANY, RandomTestUtil.randomString()),
			LocaleUtil.GERMANY);

		Assert.assertEquals("de_DE", objectRelationship.getDefaultLanguageId());
	}

}