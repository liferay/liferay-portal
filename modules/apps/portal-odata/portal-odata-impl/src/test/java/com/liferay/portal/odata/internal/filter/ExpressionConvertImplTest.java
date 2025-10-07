/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.internal.filter;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.NavigationPropertyExpression;
import com.liferay.portal.odata.filter.expression.factory.ExpressionFactory;
import com.liferay.portal.odata.internal.filter.expression.factory.ExpressionFactoryImpl;
import com.liferay.portal.search.internal.query.NestedFieldQueryHelperImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.Instant;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * The type Expression convert impl test.
 *
 * @author Cristina González
 */
public class ExpressionConvertImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		FastDateFormatFactory fastDateFormatFactory = Mockito.mock(
			FastDateFormatFactory.class);

		Mockito.when(
			fastDateFormatFactory.getSimpleDateFormat(
				PropsUtil.get(PropsKeys.INDEX_DATE_FORMAT_PATTERN))
		).thenReturn(
			_simpleDateFormat
		);

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			fastDateFormatFactory);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testConvertBinaryExpressionWithCount()
		throws ExpressionVisitException {

		BinaryExpression binaryExpression =
			_expressionFactory.createBinaryExpression(
				_expressionFactory.createMemberExpression(
					_expressionFactory.createNavigationPropertyExpression(
						"EntityModelName",
						NavigationPropertyExpression.Type.COUNT)),
				BinaryExpression.Operation.GE,
				_expressionFactory.createLiteralExpression(
					"2", LiteralExpression.Type.INTEGER));

		_expressionConvertImpl.convert(
			binaryExpression, LocaleUtil.getDefault(), _entityModel);
	}

	@Test
	public void testConvertBinaryExpressionWithEqOnPrimitiveField()
		throws ExpressionVisitException {

		BinaryExpression binaryExpression =
			_expressionFactory.createBinaryExpression(
				_expressionFactory.createMemberExpression(
					_expressionFactory.createPrimitivePropertyExpression(
						"title")),
				BinaryExpression.Operation.EQ,
				_expressionFactory.createLiteralExpression(
					"test", LiteralExpression.Type.STRING));

		QueryFilter queryFilter = (QueryFilter)_expressionConvertImpl.convert(
			binaryExpression, LocaleUtil.getDefault(), _entityModel);

		TermQuery termQuery = (TermQuery)queryFilter.getQuery();

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertEquals("title", queryTerm.getField());
		Assert.assertEquals("test", queryTerm.getValue());
	}

	@Test
	public void testConvertBinaryExpressionWithGtOnPrimitiveFieldAndLiteralField()
		throws ExpressionVisitException {

		BinaryExpression binaryExpression =
			_expressionFactory.createBinaryExpression(
				_expressionFactory.createMemberExpression(
					_expressionFactory.createPrimitivePropertyExpression(
						"dateTime")),
				BinaryExpression.Operation.GT,
				_expressionFactory.createLiteralExpression(
					"2012-05-29T09:13:28Z", LiteralExpression.Type.DATE_TIME));

		QueryFilter queryFilter = (QueryFilter)_expressionConvertImpl.convert(
			binaryExpression, LocaleUtil.getDefault(), _entityModel);

		TermRangeQuery termRangeQuery = (TermRangeQuery)queryFilter.getQuery();

		Assert.assertEquals("dateTime", termRangeQuery.getField());
		Assert.assertEquals("20120529091328", termRangeQuery.getLowerTerm());
		Assert.assertFalse(termRangeQuery.includesLower());
		Assert.assertNull(termRangeQuery.getUpperTerm());
		Assert.assertTrue(termRangeQuery.includesUpper());
	}

	@Test
	public void testConvertBinaryExpressionWithGtOnPrimitiveFieldAndNowMethod()
		throws ExpressionVisitException, ParseException {

		Date initialDate = new Date();

		Instant initialInstant = initialDate.toInstant();

		BinaryExpression binaryExpression =
			_expressionFactory.createBinaryExpression(
				_expressionFactory.createMemberExpression(
					_expressionFactory.createPrimitivePropertyExpression(
						"dateTime")),
				BinaryExpression.Operation.GT,
				_expressionFactory.createMethodExpression(
					Collections.emptyList(), MethodExpression.Type.NOW));

		ExpressionConvertImpl expressionConvertImpl =
			new ExpressionConvertImpl() {
				{
					nestedFieldQueryHelper = new NestedFieldQueryHelperImpl();
				}
			};

		QueryFilter queryFilter = (QueryFilter)expressionConvertImpl.convert(
			binaryExpression, LocaleUtil.getDefault(), _entityModel);

		TermRangeQuery termRangeQuery = (TermRangeQuery)queryFilter.getQuery();

		Assert.assertEquals("dateTime", termRangeQuery.getField());
		Assert.assertFalse(termRangeQuery.includesLower());
		Assert.assertNull(termRangeQuery.getUpperTerm());
		Assert.assertTrue(termRangeQuery.includesUpper());

		Date lowerTermDate = _simpleDateFormat.parse(
			termRangeQuery.getLowerTerm());

		Instant lowerTermInstant = lowerTermDate.toInstant();

		Date finalDate = new Date();

		Instant finalInstant = finalDate.toInstant();

		Assert.assertTrue(
			lowerTermInstant.getEpochSecond() >=
				initialInstant.getEpochSecond());
		Assert.assertTrue(
			lowerTermInstant.getEpochSecond() <= finalInstant.getEpochSecond());
	}

	@Test
	public void testConvertListExpressionWithInOnPrimitiveField()
		throws ExpressionVisitException {

		ListExpression listExpression = _expressionFactory.createListExpression(
			_expressionFactory.createMemberExpression(
				_expressionFactory.createPrimitivePropertyExpression("title")),
			ListExpression.Operation.IN,
			Collections.singletonList(
				_expressionFactory.createLiteralExpression(
					"test", LiteralExpression.Type.STRING)));

		QueryFilter queryFilter = (QueryFilter)_expressionConvertImpl.convert(
			listExpression, LocaleUtil.getDefault(), _entityModel);

		BooleanQuery booleanQuery = (BooleanQuery)queryFilter.getQuery();

		List<BooleanClause<Query>> booleanClauses = booleanQuery.clauses();

		Assert.assertEquals(
			booleanClauses.toString(), 1, booleanClauses.size());

		BooleanClause<Query> booleanClause = booleanClauses.get(0);

		TermQuery termQuery = (TermQuery)booleanClause.getClause();

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertEquals("title", queryTerm.getField());
		Assert.assertEquals("test", queryTerm.getValue());
	}

	@Test
	public void testConvertMemberExpressionWithLambdaAnyEqOnCollectionField()
		throws ExpressionVisitException {

		MemberExpression memberExpression =
			_expressionFactory.createMemberExpression(
				_expressionFactory.createCollectionPropertyExpression(
					_expressionFactory.createLambdaFunctionExpression(
						LambdaFunctionExpression.Type.ANY, "k",
						_expressionFactory.createBinaryExpression(
							_expressionFactory.createMemberExpression(
								_expressionFactory.
									createLambdaVariableExpression("k")),
							BinaryExpression.Operation.EQ,
							_expressionFactory.createLiteralExpression(
								"'keyword1'", LiteralExpression.Type.STRING))),
					_expressionFactory.createPrimitivePropertyExpression(
						"keywords")));

		QueryFilter queryFilter = (QueryFilter)_expressionConvertImpl.convert(
			memberExpression, LocaleUtil.getDefault(), _entityModel);

		TermQuery termQuery = (TermQuery)queryFilter.getQuery();

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertNotNull(queryTerm);
		Assert.assertEquals("keywords.raw", queryTerm.getField());
		Assert.assertEquals("keyword1", queryTerm.getValue());
	}

	private static final EntityModel _entityModel = new EntityModel() {

		@Override
		public Map<String, EntityField> getEntityFieldsMap() {
			return HashMapBuilder.put(
				"dateTime",
				(EntityField)new DateTimeEntityField(
					"dateTime", locale -> "dateTime", locale -> "dateTime")
			).put(
				"keywords",
				new CollectionEntityField(
					new StringEntityField("keywords", locale -> "keywords.raw"))
			).put(
				"title", new StringEntityField("title", locale -> "title")
			).build();
		}

		@Override
		public String getName() {
			return "SomeEntityName";
		}

	};

	private static final ExpressionFactory _expressionFactory =
		new ExpressionFactoryImpl();

	private final ExpressionConvertImpl _expressionConvertImpl =
		new ExpressionConvertImpl() {
			{
				nestedFieldQueryHelper = new NestedFieldQueryHelperImpl();
			}
		};
	private final SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(
		"yyyyMMddHHmmss");

}