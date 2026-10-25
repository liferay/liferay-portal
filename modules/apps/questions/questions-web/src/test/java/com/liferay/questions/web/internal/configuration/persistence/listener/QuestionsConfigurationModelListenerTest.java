/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.configuration.persistence.listener;

import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * @author Shuyang Zhou
 */
public class QuestionsConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() {
		List<Dictionary<String, Object>> dictionaries = new ArrayList<>();

		BundleContext bundleContext = (BundleContext)ProxyUtil.newProxyInstance(
			BundleContext.class.getClassLoader(),
			new Class<?>[] {BundleContext.class},
			(proxy, method, arguments) -> {
				dictionaries.add((Dictionary<String, Object>)arguments[2]);

				return null;
			});

		QuestionsConfigurationModelListener
			questionsConfigurationModelListener =
				new QuestionsConfigurationModelListener();

		questionsConfigurationModelListener.activate(
			bundleContext,
			Collections.<String, Object>singletonMap(
				"enableCustomAssetRenderer", true));

		Assert.assertEquals(dictionaries.toString(), 2, dictionaries.size());

		for (Dictionary<String, Object> dictionary : dictionaries) {
			Assert.assertEquals(
				dictionary.toString(), 100,
				dictionary.get(Constants.SERVICE_RANKING));
		}
	}

}