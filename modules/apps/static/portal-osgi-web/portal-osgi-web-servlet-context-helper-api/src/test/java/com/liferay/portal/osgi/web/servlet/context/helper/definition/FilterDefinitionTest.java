/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.definition;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 */
public class FilterDefinitionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddMultipleURLPatterns() {
		String urlPattern = "/o/module";

		List<String> urlPatterns = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			urlPatterns.add(urlPattern + "/" + i);
		}

		_filterDefinition.setURLPatterns(urlPatterns);

		urlPatterns = _filterDefinition.getURLPatterns();

		Assert.assertEquals(urlPatterns.toString(), 10, urlPatterns.size());

		for (int i = 0; i < 10; i++) {
			Assert.assertEquals(urlPattern + "/" + i, urlPatterns.get(i));
		}
	}

	@Test
	public void testAddSingleURLPattern() {
		String urlPattern = "/o/module";

		_filterDefinition.addURLPattern(urlPattern);

		List<String> urlPatterns = _filterDefinition.getURLPatterns();

		Assert.assertEquals(urlPatterns.toString(), 1, urlPatterns.size());
		Assert.assertEquals(urlPattern, urlPatterns.get(0));
	}

	@Test
	public void testSetFilter() {
		Filter filter = ProxyFactory.newDummyInstance(Filter.class);

		_filterDefinition.setFilter(filter);

		Assert.assertSame(filter, _filterDefinition.getFilter());
	}

	@Test
	public void testSetFilterName() {
		String filterName = "Module Filter";

		_filterDefinition.setName(filterName);

		Assert.assertEquals(filterName, _filterDefinition.getName());
	}

	@Test
	public void testSetMultipleInitParameters() {
		Map<String, String> initParameters = new HashMap<>();

		for (int i = 0; i < 10; i++) {
			initParameters.put("parameter-" + i, String.valueOf(i));
		}

		_filterDefinition.setInitParameters(initParameters);

		initParameters = _filterDefinition.getInitParameters();

		Assert.assertEquals(
			initParameters.toString(), 10, initParameters.size());

		for (int i = 0; i < 10; i++) {
			String expectedValue = String.valueOf(i);
			String value = initParameters.get("parameter-" + i);

			Assert.assertEquals(expectedValue, value);
		}
	}

	@Test
	public void testSetSingleInitParameter() {
		String key = "parameter";
		String value = "1";

		_filterDefinition.setInitParameter(key, value);

		Map<String, String> initParameters =
			_filterDefinition.getInitParameters();

		Assert.assertEquals(
			initParameters.toString(), 1, initParameters.size());
		Assert.assertEquals(value, initParameters.get(key));
	}

	private final FilterDefinition _filterDefinition = new FilterDefinition();

}