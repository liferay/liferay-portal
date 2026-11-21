/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.settings;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public abstract class BaseSettingsHelperTestCase {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testGetReturnsNull() {
		Assert.assertNull(get("test"));
	}

	@Test
	public void testLoadFromSourceJSON() throws Exception {
		loadFromSource(_getSource("settings.json"));

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			get("analysis.analyzer.1_10_ngram.filter"));

		Assert.assertEquals(
			"[\"lowercase\",\"1_10_ngram\"]", jsonArray.toString());

		Assert.assertEquals("0-all", get("index.auto_expand_replicas"));
		Assert.assertEquals("timestamp", get("index.default_pipeline"));
		Assert.assertEquals("7500", get("index.mapping.total_fields.limit"));
		Assert.assertEquals("9", get("index.max_ngram_diff"));

		loadFromSource("{\"index\":{\"max_ngram_diff\":8}}");

		Assert.assertEquals("8", get("index.max_ngram_diff"));
	}

	@Test
	public void testLoadFromSourceYAML() {
		loadFromSource(_getSource("settings.yml"));

		Assert.assertEquals("false", get("monitor.jvm.gc.enabled"));
		Assert.assertEquals("1", get("thread_pool.warmer.core"));
		Assert.assertEquals("2m", get("thread_pool.warmer.keep_alive"));
		Assert.assertEquals("8", get("thread_pool.warmer.max"));
		Assert.assertEquals("100", get("thread_pool.write.queue_size"));

		loadFromSource("monitor.jvm.gc.enabled: true");

		Assert.assertEquals("true", get("monitor.jvm.gc.enabled"));
	}

	@Test
	public void testPutKeyValue() {
		put("one", "two");
		put("three.four.five", "six");

		Assert.assertEquals("two", get("one"));
		Assert.assertEquals("six", get("three.four.five"));

		put("one", "two_overidden");

		Assert.assertEquals("two_overidden", get("one"));
	}

	protected abstract String get(String key);

	protected abstract void loadFromSource(String source);

	protected abstract void put(String key, String value);

	private String _getSource(String resourceName) {
		return StringUtil.read(getClass(), resourceName);
	}

}