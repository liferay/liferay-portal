/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.smoke.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.initializer.extender.SiteInitializerUtil;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class SiteInitializerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testToMap() {
		String enUSValue = RandomTestUtil.randomString();
		String randomLanguageId =
			StringUtil.toLowerCase(StringUtil.randomString(2)) +
				StringPool.UNDERLINE +
					StringUtil.toUpperCase(StringUtil.randomString(2));
		String randomValue = RandomTestUtil.randomString();

		Map<Locale, String> map = SiteInitializerUtil.toMap(
			JSONUtil.put(
				randomLanguageId, randomValue
			).put(
				"en_US", enUSValue
			).toString());

		Assert.assertEquals(
			enUSValue, map.get(LocaleUtil.fromLanguageId("en_US", false)));
		Assert.assertEquals(
			randomValue,
			map.get(LocaleUtil.fromLanguageId(randomLanguageId, false)));
		Assert.assertEquals(map.toString(), 2, map.size());
	}

}