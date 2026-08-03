/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.provider.PLOOriginalTranslationProvider;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class PLOOriginalTranslationProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGet() throws Exception {
		String key = "available-languages";
		Locale locale = LocaleUtil.getDefault();

		String originalValue = LanguageUtil.get(locale, key);

		Assert.assertNotNull(originalValue);

		PLOEntry ploEntry = _ploEntryLocalService.addOrUpdatePLOEntry(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			key, LanguageUtil.getLanguageId(locale),
			RandomTestUtil.randomString());

		Assert.assertEquals(ploEntry.getValue(), LanguageUtil.get(locale, key));

		Assert.assertEquals(
			originalValue, _ploOriginalTranslationProvider.get(locale, key));

		// Nonexistent key

		Assert.assertNull(
			_ploOriginalTranslationProvider.get(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()));
	}

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	@Inject
	private PLOOriginalTranslationProvider _ploOriginalTranslationProvider;

}