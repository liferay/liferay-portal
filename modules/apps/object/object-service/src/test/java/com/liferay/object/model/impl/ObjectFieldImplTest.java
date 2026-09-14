/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringPool;
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
 * @author Carolina Barbosa
 */
public class ObjectFieldImplTest {

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
	public void testGetAttachmentDownloadActionKey() {
		_testGetAttachmentDownloadActionKey("formatId", "DOWNLOAD_FORMAT_ID");
		_testGetAttachmentDownloadActionKey(
			"friendlyURLMapper", "DOWNLOAD_FRIENDLY_URL_MAPPER");
	}

	@Test
	public void testGetDefaultLanguageId() {
		ObjectField objectField = new ObjectFieldImpl();

		objectField.setDescriptionMap(
			Collections.singletonMap(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()),
			LocaleUtil.SPAIN);
		objectField.setLabelMap(
			Collections.singletonMap(
				LocaleUtil.GERMANY, RandomTestUtil.randomString()),
			LocaleUtil.GERMANY);

		Assert.assertEquals("de_DE", objectField.getDefaultLanguageId());
	}

	@Test
	public void testGetReadOnly() {
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_CONDITIONAL,
			ObjectFieldConstants.READ_ONLY_CONDITIONAL);
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_FALSE,
			ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(
			StringPool.BLANK, ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(null, ObjectFieldConstants.READ_ONLY_FALSE);
		_testGetReadOnly(
			ObjectFieldConstants.READ_ONLY_TRUE,
			ObjectFieldConstants.READ_ONLY_TRUE);
	}

	private void _testGetAttachmentDownloadActionKey(
		String objectFieldName, String expectedActionKey) {

		ObjectField objectField = new ObjectFieldImpl();

		objectField.setName(objectFieldName);

		Assert.assertEquals(
			expectedActionKey, objectField.getAttachmentDownloadActionKey());
	}

	private void _testGetReadOnly(
		String actualReadOnly, String expectedReadOnly) {

		ObjectField objectField = new ObjectFieldImpl();

		objectField.setReadOnly(actualReadOnly);

		Assert.assertEquals(expectedReadOnly, objectField.getReadOnly());
	}

}