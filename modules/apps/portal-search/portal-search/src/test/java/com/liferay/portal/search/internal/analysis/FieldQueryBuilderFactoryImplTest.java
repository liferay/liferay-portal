/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.analysis;

import com.liferay.portal.kernel.search.query.QueryPreProcessConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Rodrigo Guedes de Souza
 */
public class FieldQueryBuilderFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_fieldQueryBuilderFactoryImpl.descriptionFieldQueryBuilder =
			_descriptionFieldQueryBuilder;
		_fieldQueryBuilderFactoryImpl.queryPreProcessConfiguration =
			_queryPreProcessConfiguration;
		_fieldQueryBuilderFactoryImpl.substringFieldQueryBuilder =
			_substringFieldQueryBuilder;
		_fieldQueryBuilderFactoryImpl.titleFieldQueryBuilder =
			_titleFieldQueryBuilder;

		Mockito.when(
			_queryPreProcessConfiguration.isSubstringSearchAlways(
				Mockito.anyString())
		).thenReturn(
			false
		);
	}

	@Test
	public void testGetQueryBuilder() {

		// Description field

		Assert.assertSame(
			_descriptionFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder("description"));

		// Object entry title fields

		Assert.assertSame(
			_titleFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder("objectEntryTitle"));
		Assert.assertSame(
			_titleFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder(
				"objectEntryTitle_en_US"));

		// Substring field

		Mockito.when(
			_queryPreProcessConfiguration.isSubstringSearchAlways("extension")
		).thenReturn(
			true
		);

		Assert.assertSame(
			_substringFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder("extension"));

		// Title fields

		Assert.assertSame(
			_titleFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder("name"));
		Assert.assertSame(
			_titleFieldQueryBuilder,
			_fieldQueryBuilderFactoryImpl.getQueryBuilder("title"));

		// Unmapped field

		Assert.assertNull(
			_fieldQueryBuilderFactoryImpl.getQueryBuilder(
				"objectEntryContent"));
	}

	private final DescriptionFieldQueryBuilder _descriptionFieldQueryBuilder =
		Mockito.mock(DescriptionFieldQueryBuilder.class);
	private final FieldQueryBuilderFactoryImpl _fieldQueryBuilderFactoryImpl =
		new FieldQueryBuilderFactoryImpl();
	private final QueryPreProcessConfiguration _queryPreProcessConfiguration =
		Mockito.mock(QueryPreProcessConfiguration.class);
	private final SubstringFieldQueryBuilder _substringFieldQueryBuilder =
		Mockito.mock(SubstringFieldQueryBuilder.class);
	private final TitleFieldQueryBuilder _titleFieldQueryBuilder = Mockito.mock(
		TitleFieldQueryBuilder.class);

}