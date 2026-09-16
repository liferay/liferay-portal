/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class AssetListTypeSettingsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTypeSettings() {
		String classNameIds =
			RandomTestUtil.randomLong() + StringPool.COMMA +
				RandomTestUtil.randomLong();

		long defaultGroupId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();

		UnicodeProperties unicodeProperties = _getTypeSettingsUnicodeProperties(
			defaultGroupId,
			HashMapBuilder.put(
				"classNameIds", classNameIds
			).put(
				"emailFromAddress", RandomTestUtil.randomString()
			).put(
				"scopeIds", "Group_default,Group_" + groupId + ",Layout_1"
			).build());

		Assert.assertEquals(
			"true", unicodeProperties.getProperty("anyAssetType"));
		Assert.assertEquals(
			classNameIds, unicodeProperties.getProperty("classNameIds"));
		Assert.assertNull(unicodeProperties.getProperty("emailFromAddress"));
		Assert.assertEquals(
			defaultGroupId + StringPool.COMMA + groupId,
			unicodeProperties.getProperty("groupIds"));
		Assert.assertNull(unicodeProperties.getProperty("scopeIds"));
	}

	@Test
	public void testGetTypeSettingsKeepsAnyAssetType() {
		String anyAssetType = String.valueOf(RandomTestUtil.randomLong());

		UnicodeProperties unicodeProperties = _getTypeSettingsUnicodeProperties(
			RandomTestUtil.randomLong(),
			Collections.singletonMap("anyAssetType", anyAssetType));

		Assert.assertEquals(
			anyAssetType, unicodeProperties.getProperty("anyAssetType"));
	}

	private UnicodeProperties _getTypeSettingsUnicodeProperties(
		long defaultGroupId, Map<String, String> preferencesMap) {

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		Mockito.when(
			portletPreferences.getNames()
		).thenReturn(
			Collections.enumeration(preferencesMap.keySet())
		);

		Mockito.when(
			portletPreferences.getValues(Mockito.anyString(), Mockito.any())
		).thenAnswer(
			invocation -> new String[] {
				preferencesMap.get(invocation.getArgument(0))
			}
		);

		return UnicodePropertiesBuilder.load(
			AssetListTypeSettingsUtil.getTypeSettings(
				defaultGroupId, portletPreferences)
		).build();
	}

}