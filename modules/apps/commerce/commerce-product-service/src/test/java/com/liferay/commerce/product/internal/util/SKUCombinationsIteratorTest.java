/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.util;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.impl.CPDefinitionImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionRelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelImpl;
import com.liferay.portal.kernel.configuration.ConfigurationFactory;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Luca Pellizzon
 */
public class SKUCombinationsIteratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ConfigurationFactoryUtil.setConfigurationFactory(
			Mockito.mock(ConfigurationFactory.class, Mockito.RETURNS_MOCKS));
	}

	@Test
	public void testSKUCombinationsIterator() throws Exception {
		int optionsAndValuesCount = 2;

		CPDefinition cpDefinition = _createCPDefinition();

		Map<CPDefinitionOptionRel, CPDefinitionOptionValueRel[]>
			combinationGeneratorMap = _createCombinationGeneratorMap(
				optionsAndValuesCount, cpDefinition);

		Assert.assertEquals(
			combinationGeneratorMap.toString(), optionsAndValuesCount,
			combinationGeneratorMap.size());

		Iterator<CPDefinitionOptionValueRel[]> iterator =
			new SKUCombinationsIterator(combinationGeneratorMap);

		int count = 0;

		while (iterator.hasNext()) {
			CPDefinitionOptionValueRel[] cpDefinitionOptionValueRels =
				iterator.next();

			count += cpDefinitionOptionValueRels.length;
		}

		Assert.assertEquals(
			(int)Math.pow(optionsAndValuesCount, optionsAndValuesCount + 1),
			count);
	}

	private CPDefinition _createCPDefinition() {
		CPDefinition cpDefinition = new CPDefinitionImpl();

		Map<Locale, String> map = Collections.singletonMap(
			LocaleUtil.US, "Title-Description");

		cpDefinition.setCPDefinitionId(RandomTestUtil.randomLong());
		cpDefinition.setGroupId(RandomTestUtil.randomLong());
		cpDefinition.setCompanyId(RandomTestUtil.randomLong());
		cpDefinition.setUserId(RandomTestUtil.randomLong());
		cpDefinition.setUserName(RandomTestUtil.randomString());
		cpDefinition.setNameMap(map);
		cpDefinition.setDescriptionMap(map);

		return cpDefinition;
	}

	private CPDefinitionOptionRel _createCPDefinitionOptionRel(
		CPDefinition cpDefinition) {

		CPDefinitionOptionRel cpDefinitionOptionRel =
			new CPDefinitionOptionRelImpl();

		Date date = new Date();

		cpDefinitionOptionRel.setCPDefinitionOptionRelId(
			RandomTestUtil.randomLong());
		cpDefinitionOptionRel.setGroupId(cpDefinition.getGroupId());
		cpDefinitionOptionRel.setCompanyId(cpDefinition.getCompanyId());
		cpDefinitionOptionRel.setUserId(cpDefinition.getUserId());
		cpDefinitionOptionRel.setUserName(cpDefinition.getUserName());
		cpDefinitionOptionRel.setCreateDate(date);
		cpDefinitionOptionRel.setModifiedDate(date);
		cpDefinitionOptionRel.setCPDefinitionId(
			cpDefinition.getCPDefinitionId());
		cpDefinitionOptionRel.setName(RandomTestUtil.randomString());
		cpDefinitionOptionRel.setDescription(RandomTestUtil.randomString());
		cpDefinitionOptionRel.setSkuContributor(true);

		return cpDefinitionOptionRel;
	}

	private CPDefinitionOptionValueRel _createCPDefinitionOptionValueRel(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			new CPDefinitionOptionValueRelImpl();

		Date date = new Date();

		cpDefinitionOptionValueRel.setCPDefinitionOptionValueRelId(
			RandomTestUtil.randomLong());
		cpDefinitionOptionValueRel.setGroupId(
			cpDefinitionOptionRel.getGroupId());
		cpDefinitionOptionValueRel.setCompanyId(
			cpDefinitionOptionRel.getCompanyId());
		cpDefinitionOptionValueRel.setUserId(cpDefinitionOptionRel.getUserId());
		cpDefinitionOptionValueRel.setUserName(
			cpDefinitionOptionRel.getUserName());
		cpDefinitionOptionValueRel.setCreateDate(date);
		cpDefinitionOptionValueRel.setModifiedDate(date);
		cpDefinitionOptionValueRel.setCPDefinitionOptionRelId(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId());
		cpDefinitionOptionValueRel.setKey(RandomTestUtil.randomString());
		cpDefinitionOptionValueRel.setName(RandomTestUtil.randomString());

		return cpDefinitionOptionValueRel;
	}

	private Map<CPDefinitionOptionRel, CPDefinitionOptionValueRel[]>
		_createCombinationGeneratorMap(
			int optionsAndValuesCount, CPDefinition cpDefinition) {

		Map<CPDefinitionOptionRel, CPDefinitionOptionValueRel[]>
			combinationGeneratorMap = new HashMap<>();

		for (int i = 0; i < optionsAndValuesCount; i++) {
			CPDefinitionOptionValueRel[] cpDefinitionOptionValueRelArray =
				new CPDefinitionOptionValueRel[optionsAndValuesCount];

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_createCPDefinitionOptionRel(cpDefinition);

			for (int j = 0; j < optionsAndValuesCount; j++) {
				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					_createCPDefinitionOptionValueRel(cpDefinitionOptionRel);

				cpDefinitionOptionValueRelArray[j] = cpDefinitionOptionValueRel;
			}

			combinationGeneratorMap.put(
				cpDefinitionOptionRel, cpDefinitionOptionValueRelArray);
		}

		return combinationGeneratorMap;
	}

}