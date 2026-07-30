/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.setting.util;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Cheryl Tang
 */
public class ObjectDefinitionSettingUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsSitemapable() {
		String name = ObjectDefinitionSettingConstants.NAME_SITEMAPABLE;

		long objectDefinitionId = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = _mockObjectDefinition(
			objectDefinitionId, false, false);

		Assert.assertTrue(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition, Collections.emptyMap()));

		objectDefinition = _mockObjectDefinition(
			objectDefinitionId, true, true);

		Assert.assertFalse(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition, Collections.emptyMap()));
		Assert.assertFalse(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition,
				_getObjectDefinitionSettingsMap(
					name, objectDefinitionId, StringPool.TRUE)));

		objectDefinition = _mockObjectDefinition(
			objectDefinitionId, true, false);

		Assert.assertFalse(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition, Collections.emptyMap()));
		Assert.assertFalse(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition,
				_getObjectDefinitionSettingsMap(
					name, objectDefinitionId, StringPool.FALSE)));
		Assert.assertFalse(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition,
				_getObjectDefinitionSettingsMap(
					RandomTestUtil.randomString(), objectDefinitionId,
					StringPool.TRUE)));
		Assert.assertTrue(
			ObjectDefinitionSettingUtil.isSitemapable(
				objectDefinition,
				_getObjectDefinitionSettingsMap(
					name, objectDefinitionId, StringPool.TRUE)));
	}

	private Map<Long, ObjectDefinitionSetting> _getObjectDefinitionSettingsMap(
		String name, long objectDefinitionId, String value) {

		ObjectDefinitionSetting objectDefinitionSetting = Mockito.mock(
			ObjectDefinitionSetting.class);

		Mockito.when(
			objectDefinitionSetting.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			objectDefinitionSetting.getValue()
		).thenReturn(
			value
		);

		return Collections.singletonMap(
			objectDefinitionId, objectDefinitionSetting);
	}

	private ObjectDefinition _mockObjectDefinition(
		long objectDefinitionId, boolean system,
		boolean unmodifiableSystemObject) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		Mockito.when(
			objectDefinition.isSystem()
		).thenReturn(
			system
		);

		Mockito.when(
			objectDefinition.isUnmodifiableSystemObject()
		).thenReturn(
			unmodifiableSystemObject
		);

		return objectDefinition;
	}

}