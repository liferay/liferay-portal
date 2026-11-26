/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.site.initializer.SiteInitializer;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class SiteInitializerUtilTest {

	@Test
	public void testIsInitialized() {
		String key = RandomTestUtil.randomString();

		Mockito.when(
			_siteInitializer.getKey()
		).thenReturn(
			key
		);

		Assert.assertFalse(
			SiteInitializerUtil.isInitialized(
				_mockGroup(key, "0.1"), _siteInitializer, "0.1"));
		Assert.assertTrue(
			SiteInitializerUtil.isInitialized(
				_mockGroup(key, "1.0"), _siteInitializer, "1.0"));

		Assert.assertFalse(
			SiteInitializerUtil.isInitialized(
				_mockGroup(RandomTestUtil.randomString(), "1.0"),
				_siteInitializer, "1.0"));
		Assert.assertFalse(
			SiteInitializerUtil.isInitialized(
				_mockGroup(key, "1.0"), _siteInitializer, "1.1"));
	}

	private Group _mockGroup(
		String siteInitializerKey, String siteInitializerVersion) {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getTypeSettingsProperties()
		).thenReturn(
			UnicodePropertiesBuilder.create(
				true
			).put(
				"siteInitializerKey", siteInitializerKey
			).put(
				"siteInitializerVersion", siteInitializerVersion
			).build()
		);

		return group;
	}

	private final SiteInitializer _siteInitializer = Mockito.mock(
		SiteInitializer.class);

}