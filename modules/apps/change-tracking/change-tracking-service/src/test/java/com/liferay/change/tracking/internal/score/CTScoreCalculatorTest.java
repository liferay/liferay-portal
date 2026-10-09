/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.score;

import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author David Truong
 */
public class CTScoreCalculatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_ctScoreCalculator = Mockito.spy(new CTScoreCalculator());

		ReflectionTestUtil.setFieldValue(
			_ctScoreCalculator, "_ctEntryLocalService", _ctEntryLocalService);
	}

	@Test
	public void testCalculateCTCollectionScoreWithMultipleModelClassNameIds() {
		Mockito.doReturn(
			4
		).when(
			_ctScoreCalculator
		).calculate(
			_MODEL_CLASS_NAME_ID_1
		);

		Mockito.doReturn(
			24
		).when(
			_ctScoreCalculator
		).calculate(
			_MODEL_CLASS_NAME_ID_2
		);

		Assert.assertEquals(
			(4 * 3) + (24 * 2),
			_calculateCTCollectionScore(
				Arrays.asList(
					new Object[] {_MODEL_CLASS_NAME_ID_1, 3L},
					new Object[] {_MODEL_CLASS_NAME_ID_2, 2L})));
	}

	@Test
	public void testCalculateCTCollectionScoreWithNoCTEntries() {
		Assert.assertEquals(
			0, _calculateCTCollectionScore(Collections.emptyList()));
	}

	@Test
	public void testCalculateCTCollectionScoreWithSingleModelClassNameId() {
		Mockito.doReturn(
			5
		).when(
			_ctScoreCalculator
		).calculate(
			_MODEL_CLASS_NAME_ID_1
		);

		Assert.assertEquals(
			5 * 7,
			_calculateCTCollectionScore(
				Collections.singletonList(
					new Object[] {_MODEL_CLASS_NAME_ID_1, 7L})));
	}

	@Test
	public void testGetScoreSizeClassification() {
		Assert.assertEquals(
			"small", _ctScoreCalculator.getScoreSizeClassification(10000));
		Assert.assertEquals(
			"medium", _ctScoreCalculator.getScoreSizeClassification(10001));
		Assert.assertEquals(
			"medium", _ctScoreCalculator.getScoreSizeClassification(20000));
		Assert.assertEquals(
			"large", _ctScoreCalculator.getScoreSizeClassification(20001));
	}

	private int _calculateCTCollectionScore(List<Object[]> results) {
		Mockito.doReturn(
			results
		).when(
			_ctEntryLocalService
		).dslQuery(
			Mockito.any()
		);

		return _ctScoreCalculator.calculateCTCollectionScore(
			RandomTestUtil.randomLong());
	}

	private static final Long _MODEL_CLASS_NAME_ID_1 =
		RandomTestUtil.randomLong();

	private static final Long _MODEL_CLASS_NAME_ID_2 =
		RandomTestUtil.randomLong();

	private final CTEntryLocalService _ctEntryLocalService = Mockito.mock(
		CTEntryLocalService.class);
	private CTScoreCalculator _ctScoreCalculator;

}