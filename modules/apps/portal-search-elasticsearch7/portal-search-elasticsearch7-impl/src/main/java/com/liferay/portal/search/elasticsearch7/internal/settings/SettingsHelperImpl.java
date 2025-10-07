/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.settings;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.SettingsHelper;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;

/**
 * @author André de Oliveira
 */
public class SettingsHelperImpl implements SettingsHelper {

	public SettingsHelperImpl(Settings.Builder builder) {
		_builder = builder;
	}

	public Settings build() {
		return _builder.build();
	}

	@Override
	public String get(String key) {
		return _builder.get(key);
	}

	public Settings.Builder getBuilder() {
		return _builder;
	}

	@Override
	public void loadFromSource(String source) {
		if (StringUtils.isBlank(source)) {
			return;
		}

		source = source.trim();

		if (source.charAt(0) == CharPool.OPEN_CURLY_BRACE) {
			_builder.loadFromSource(source, XContentType.JSON);
		}
		else {
			_builder.loadFromSource(source, XContentType.YAML);
		}
	}

	public void put(String key, boolean value) {
		_builder.put(key, value);
	}

	public void put(String key, List<String> values) {
		_builder.putList(key, values);
	}

	@Override
	public void put(String key, String value) {
		if (!StringUtils.isBlank(value)) {
			_builder.put(key, value);
		}
	}

	private final Settings.Builder _builder;

}