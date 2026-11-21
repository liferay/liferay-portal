/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.settings;

import com.liferay.portal.search.test.util.settings.BaseSettingsHelperTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;

/**
 * @author Bryan Engler
 */
public class SettingsHelperTest extends BaseSettingsHelperTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	protected String get(String key) {
		return _settingsHelper.get(key);
	}

	@Override
	protected void loadFromSource(String source) {
		_settingsHelper.loadFromSource(source);
	}

	@Override
	protected void put(String key, String value) {
		_settingsHelper.put(key, value);
	}

	private final SettingsHelper _settingsHelper = new SettingsHelper();

}