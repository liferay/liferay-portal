/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joshua Cords
 */
public class ElasticsearchConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_elasticsearchConfigurationModelListener =
			new ElasticsearchConfigurationModelListener();
	}

	@Test
	public void testOnBeforeSaveTrackTotalHitsLimitLessThanIndexMaxResultWindow() {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("indexMaxResultWindow", 10000);
		properties.put("trackTotalHits", true);
		properties.put("trackTotalHitsLimit", 9000);

		try {
			_elasticsearchConfigurationModelListener.onBeforeSave(
				"pid", properties);

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			String configurationModelListenerExceptionMessage =
				configurationModelListenerException.getMessage();

			Assert.assertTrue(
				configurationModelListenerExceptionMessage.contains(
					"track-total-hits-limit-is-less-than-the-max-result-" +
						"window"));
		}
	}

	private ElasticsearchConfigurationModelListener
		_elasticsearchConfigurationModelListener;

}