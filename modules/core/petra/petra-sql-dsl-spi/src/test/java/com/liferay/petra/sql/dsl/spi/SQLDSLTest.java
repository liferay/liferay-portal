/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Alias;
import com.liferay.petra.sql.dsl.expression.ColumnAlias;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.expression.ScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.expression.step.WhenThenStep;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.HavingStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.sql.dsl.query.sort.OrderByInfo;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.sql.dsl.spi.expression.AggregateExpression;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunctionType;
import com.liferay.petra.sql.dsl.spi.expression.DefaultAlias;
import com.liferay.petra.sql.dsl.spi.expression.DefaultColumnAlias;
import com.liferay.petra.sql.dsl.spi.expression.DefaultPredicate;
import com.liferay.petra.sql.dsl.spi.expression.DefaultScalarDSLQueryAlias;
import com.liferay.petra.sql.dsl.spi.expression.NullExpression;
import com.liferay.petra.sql.dsl.spi.expression.Operand;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.expression.ScalarList;
import com.liferay.petra.sql.dsl.spi.expression.TableStar;
import com.liferay.petra.sql.dsl.spi.expression.step.CaseWhenThen;
import com.liferay.petra.sql.dsl.spi.expression.step.ElseEnd;
import com.liferay.petra.sql.dsl.spi.expression.step.WhenThen;
import com.liferay.petra.sql.dsl.spi.query.From;
import com.liferay.petra.sql.dsl.spi.query.GroupBy;
import com.liferay.petra.sql.dsl.spi.query.Having;
import com.liferay.petra.sql.dsl.spi.query.Join;
import com.liferay.petra.sql.dsl.spi.query.JoinType;
import com.liferay.petra.sql.dsl.spi.query.Limit;
import com.liferay.petra.sql.dsl.spi.query.OrderBy;
import com.liferay.petra.sql.dsl.spi.query.QueryExpression;
import com.liferay.petra.sql.dsl.spi.query.QueryTable;
import com.liferay.petra.sql.dsl.spi.query.Select;
import com.liferay.petra.sql.dsl.spi.query.SetOperation;
import com.liferay.petra.sql.dsl.spi.query.SetOperationType;
import com.liferay.petra.sql.dsl.spi.query.Where;
import com.liferay.petra.sql.dsl.spi.query.sort.DefaultOrderByExpression;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.Clob;
import java.sql.Types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class SQLDSLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.clear();

					assertClasses.add(AggregateExpression.class);
					assertClasses.add(BaseASTNode.class);
					assertClasses.add(BaseTable.class);
					assertClasses.add(CaseWhenThen.class);
					assertClasses.add(DefaultAlias.class);
					assertClasses.add(DefaultASTNodeListener.class);
					assertClasses.add(DefaultColumn.class);
					assertClasses.add(DefaultColumnAlias.class);
					assertClasses.add(DefaultOrderByExpression.class);
					assertClasses.add(DefaultPredicate.class);

					Collections.addAll(
						assertClasses,
						DefaultPredicate.class.getDeclaredClasses());

					assertClasses.add(DefaultScalarDSLQueryAlias.class);
					assertClasses.add(DSLFunction.class);
					assertClasses.add(DSLFunctionType.class);
					assertClasses.add(DSLFunctionFactoryUtil.class);
					assertClasses.add(DSLQueryFactoryUtil.class);
					assertClasses.add(ElseEnd.class);
					assertClasses.add(From.class);
					assertClasses.add(GroupBy.class);
					assertClasses.add(Having.class);
					assertClasses.add(Join.class);
					assertClasses.add(JoinType.class);
					assertClasses.add(Limit.class);
					assertClasses.add(NullExpression.class);
					assertClasses.add(Operand.class);
					assertClasses.add(OrderBy.class);
					assertClasses.add(Predicate.class);
					assertClasses.add(QueryExpression.class);
					assertClasses.add(QueryTable.class);
					assertClasses.add(Scalar.class);
					assertClasses.add(ScalarList.class);
					assertClasses.add(Select.class);
					assertClasses.add(SetOperation.class);
					assertClasses.add(SetOperationType.class);
					assertClasses.add(TableStar.class);
					assertClasses.add(WhenThen.class);
					assertClasses.add(Where.class);
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@Test
	public void testAggregateExpression() {
		Expression<Long> countExpression = DSLFunctionFactoryUtil.countDistinct(
			ReferenceExampleTable.INSTANCE.nameColumn);

		AggregateExpression<Long> countAggregateExpression =
			(AggregateExpression<Long>)countExpression;

		Assert.assertTrue(
			countAggregateExpression.toString(),
			countAggregateExpression.isDistinct());
		Assert.assertSame(
			ReferenceExampleTable.INSTANCE.nameColumn,
			countAggregateExpression.getExpression());
		Assert.assertEquals("count", countAggregateExpression.getName());
	}

	@Test
	public void testAlias() {
		String name = "alias";

		Alias<String> alias = ReferenceExampleTable.INSTANCE.nameColumn.as(
			name);

		Assert.assertSame(name, alias.getName());
	}

	@Test
	public void testASTNodeListenerOrder() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			MainExampleTable.INSTANCE
		);

		StringBundler sb = new StringBundler();

		dslQuery.toSQL(
			sb::append,
			astNode -> {
				if (astNode instanceof Select) {
					Assert.assertEquals("", sb.toString());
				}
				else if (astNode instanceof From) {
					Assert.assertEquals("select * ", sb.toString());
				}
			});

		Assert.assertEquals("select * from MainExample", sb.toString());
	}

	@Test
	public void testBaseASTNode() {
		FromStep fromStep = DSLQueryFactoryUtil.select();

		From from = new From(fromStep, MainExampleTable.INSTANCE);

		Assert.assertSame(fromStep, from.getChild());

		OrderBy orderBy1 = new OrderBy(
			from,
			new OrderByExpression[] {
				MainExampleTable.INSTANCE.mainExampleIdColumn.ascending()
			});

		Assert.assertSame(from, orderBy1.getChild());

		Assert.assertEquals(
			"select * from MainExample order by MainExample.mainExampleId asc",
			orderBy1.toString());

		JoinStep joinStep = from.innerJoinON(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn.eq(
				MainExampleTable.INSTANCE.mainExampleIdColumn));

		OrderBy orderBy2 = orderBy1.withNewChild(joinStep);

		Assert.assertNotSame(orderBy1, orderBy2);

		Assert.assertEquals(
			"select * from MainExample inner join ReferenceExample on " +
				"ReferenceExample.mainExampleId = MainExample.mainExampleId " +
					"order by MainExample.mainExampleId asc",
			orderBy2.toString());

		CloneNotSupportedException cloneNotSupportedException =
			new CloneNotSupportedException();

		try {
			BaseASTNode baseASTNode = new BaseASTNode() {

				@Override
				protected Object clone() throws CloneNotSupportedException {
					throw cloneNotSupportedException;
				}

				@Override
				protected void doToSQL(
					Consumer<String> consumer,
					ASTNodeListener astNodeListener) {
				}

			};

			baseASTNode.withNewChild(null);

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertSame(
				cloneNotSupportedException, runtimeException.getCause());
		}
	}

	@Test
	public void testCaseSelect() {
		Alias<String> numberAlias = DSLFunctionFactoryUtil.caseWhenThen(
			MainExampleTable.INSTANCE.mainExampleIdColumn.eq(1L),
			new Scalar<>("one")
		).whenThen(
			MainExampleTable.INSTANCE.mainExampleIdColumn.eq(2L), "two"
		).whenThen(
			MainExampleTable.INSTANCE.mainExampleIdColumn.eq(3L), "three"
		).elseEnd(
			"unknown"
		).as(
			"number"
		);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			numberAlias
		).from(
			MainExampleTable.INSTANCE
		).where(
			numberAlias.neq("unknown")
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select case when MainExample.mainExampleId = ? then ? when ",
				"MainExample.mainExampleId = ? then ? when MainExample.",
				"mainExampleId = ? then ? else ? end number from MainExample ",
				"where number != ?"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(
				1L, "one", 2L, "two", 3L, "three", "unknown", "unknown"),
			defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testCaseWhenThen() {
		Predicate predicate = MainExampleTable.INSTANCE.mainExampleIdColumn.eq(
			1L);

		Scalar<String> scalar = new Scalar<>("one");

		CaseWhenThen<String> caseWhenThen = new CaseWhenThen<>(
			predicate, scalar);

		Assert.assertSame(predicate, caseWhenThen.getPredicate());
		Assert.assertSame(scalar, caseWhenThen.getThenExpression());
	}

	@Test
	public void testColumn() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"alias");

		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE.mainExampleIdColumn);
		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			aliasMainExampleTable.mainExampleIdColumn);
		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn.hashCode(),
			aliasMainExampleTable.mainExampleIdColumn.hashCode());
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE.nameColumn);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE);

		Assert.assertSame(
			aliasMainExampleTable,
			aliasMainExampleTable.mainExampleIdColumn.getTable());

		Assert.assertFalse(
			MainExampleTable.INSTANCE.mainExampleIdColumn.isNullAllowed());
		Assert.assertFalse(
			aliasMainExampleTable.mainExampleIdColumn.isNullAllowed());
		Assert.assertTrue(MainExampleTable.INSTANCE.nameColumn.isNullAllowed());
		Assert.assertTrue(aliasMainExampleTable.nameColumn.isNullAllowed());

		Assert.assertTrue(
			MainExampleTable.INSTANCE.mainExampleIdColumn.isPrimaryKey());
		Assert.assertTrue(
			aliasMainExampleTable.mainExampleIdColumn.isPrimaryKey());
		Assert.assertFalse(MainExampleTable.INSTANCE.nameColumn.isPrimaryKey());
		Assert.assertFalse(aliasMainExampleTable.nameColumn.isPrimaryKey());
	}

	@Test
	public void testConstructors() {
		new DSLFunctionFactoryUtil();
		new DSLQueryFactoryUtil();
	}

	@Test
	public void testDerivedTable() {
		ReferenceExampleTable aliasReferenceExampleTable =
			ReferenceExampleTable.INSTANCE.as("referenceExample");

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			DSLQueryFactoryUtil.select(
				ReferenceExampleTable.INSTANCE.mainExampleIdColumn,
				ReferenceExampleTable.INSTANCE.nameColumn
			).from(
				ReferenceExampleTable.INSTANCE
			).groupBy(
				ReferenceExampleTable.INSTANCE.mainExampleIdColumn,
				ReferenceExampleTable.INSTANCE.nameColumn
			).having(
				DSLFunctionFactoryUtil.count(
					ReferenceExampleTable.INSTANCE.nameColumn
				).gt(
					3L
				)
			).as(
				aliasReferenceExampleTable.getName()
			),
			aliasReferenceExampleTable.mainExampleIdColumn.eq(
				MainExampleTable.INSTANCE.mainExampleIdColumn)
		).orderBy(
			ReferenceExampleTable.INSTANCE.nameColumn.ascending()
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample left join (select ",
				"ReferenceExample.mainExampleId, ReferenceExample.name from ",
				"ReferenceExample group by ReferenceExample.mainExampleId, ",
				"ReferenceExample.name having count(ReferenceExample.name) > ",
				"?) referenceExample on referenceExample.mainExampleId = ",
				"MainExample.mainExampleId order by ReferenceExample.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertArrayEquals(
			new String[] {"MainExample", "ReferenceExample"},
			defaultASTNodeListener.getTableNames());
	}

	@Test
	public void testElseEnd() {
		WhenThenStep<String> whenThenStep = DSLFunctionFactoryUtil.caseWhenThen(
			MainExampleTable.INSTANCE.mainExampleIdColumn.eq(
				ReferenceExampleTable.INSTANCE.mainExampleIdColumn),
			"equals");

		String scalarValue = "not equals";

		ElseEnd<String> elseEnd = new ElseEnd<>(
			whenThenStep, new Scalar<>(scalarValue));

		Scalar<String> scalar = (Scalar<String>)elseEnd.getElseExpression();

		Assert.assertSame(scalarValue, scalar.getValue());
	}

	@Test
	public void testFrom() {
		From from = new From(
			DSLQueryFactoryUtil.count(), MainExampleTable.INSTANCE);

		Assert.assertSame(MainExampleTable.INSTANCE, from.getTable());
	}

	@Test
	public void testFunction() {
		Expression<?>[] expressions = new Expression<?>[] {
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE.flagColumn
		};

		DSLFunction<Long> dslFunction = new DSLFunction<>(
			DSLFunctionType.BITWISE_AND, expressions);

		Assert.assertSame(
			DSLFunctionType.BITWISE_AND, dslFunction.getDslFunctionType());

		Assert.assertSame(expressions, dslFunction.getExpressions());

		try {
			new DSLFunction<>(DSLFunctionType.BITWISE_AND);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Expressions is empty", exception.getMessage());
		}
	}

	@Test
	public void testFunctions() {
		Assert.assertEquals(
			"MainExample.mainExampleId + ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.add(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"MainExample.mainExampleId + ?",
			String.valueOf(
				DSLFunctionFactoryUtil.add(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"avg(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.avg(
					MainExampleTable.INSTANCE.mainExampleIdColumn)));
		Assert.assertEquals(
			"BITAND(MainExample.mainExampleId, " +
				"ReferenceExample.referenceExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.bitAnd(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"BITAND(MainExample.mainExampleId, ?)",
			String.valueOf(
				DSLFunctionFactoryUtil.bitAnd(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"CAST_CLOB_TEXT(MainExample.description)",
			String.valueOf(
				DSLFunctionFactoryUtil.castClobText(
					MainExampleTable.INSTANCE.descriptionColumn)));
		Assert.assertEquals(
			"CAST_LONG(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.castLong(
					MainExampleTable.INSTANCE.nameColumn)));
		Assert.assertEquals(
			"CAST_TEXT(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.castText(
					MainExampleTable.INSTANCE.mainExampleIdColumn)));
		Assert.assertEquals(
			"CONCAT(MainExample.name, ?, ReferenceExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.concat(
					MainExampleTable.INSTANCE.nameColumn,
					new Scalar<>("__delimiter__"),
					ReferenceExampleTable.INSTANCE.nameColumn)));
		Assert.assertEquals(
			"count(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.count(
					MainExampleTable.INSTANCE.nameColumn)));
		Assert.assertEquals(
			"LOWER(MainExample.name)",
			String.valueOf(
				DSLFunctionFactoryUtil.lower(
					MainExampleTable.INSTANCE.nameColumn)));
		Assert.assertEquals(
			"MainExample.mainExampleId / ?",
			String.valueOf(
				DSLFunctionFactoryUtil.divide(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId / ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.divide(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"CAST_DECIMAL(MainExample.mainExampleId) / ?",
			String.valueOf(
				DSLFunctionFactoryUtil.floatDivide(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"CAST_DECIMAL(MainExample.mainExampleId) / ReferenceExample." +
				"referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.floatDivide(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"max(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.max(
					MainExampleTable.INSTANCE.mainExampleIdColumn)));
		Assert.assertEquals(
			"min(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.min(
					MainExampleTable.INSTANCE.mainExampleIdColumn)));
		Assert.assertEquals(
			"MainExample.mainExampleId * ?",
			String.valueOf(
				DSLFunctionFactoryUtil.multiply(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId * ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.multiply(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"MainExample.mainExampleId - ?",
			String.valueOf(
				DSLFunctionFactoryUtil.subtract(
					MainExampleTable.INSTANCE.mainExampleIdColumn, 2L)));
		Assert.assertEquals(
			"MainExample.mainExampleId - ReferenceExample.referenceExampleId",
			String.valueOf(
				DSLFunctionFactoryUtil.subtract(
					MainExampleTable.INSTANCE.mainExampleIdColumn,
					ReferenceExampleTable.INSTANCE.referenceExampleIdColumn)));
		Assert.assertEquals(
			"sum(MainExample.mainExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.sum(
					MainExampleTable.INSTANCE.mainExampleIdColumn)));
		Assert.assertEquals(
			"(MainExample.mainExampleId + ReferenceExample.referenceExampleId)",
			String.valueOf(
				DSLFunctionFactoryUtil.withParentheses(
					DSLFunctionFactoryUtil.add(
						MainExampleTable.INSTANCE.mainExampleIdColumn,
						ReferenceExampleTable.INSTANCE.
							referenceExampleIdColumn))));
	}

	@Test
	public void testGroupBy() {
		From from = new From(
			DSLQueryFactoryUtil.select(
				MainExampleTable.INSTANCE.mainExampleIdColumn,
				MainExampleTable.INSTANCE.nameColumn),
			MainExampleTable.INSTANCE);

		GroupBy groupBy = new GroupBy(
			from, MainExampleTable.INSTANCE.nameColumn);

		Expression<?>[] expressions = groupBy.getExpressions();

		Assert.assertEquals(
			Arrays.toString(expressions), 1, expressions.length);

		Assert.assertSame(MainExampleTable.INSTANCE.nameColumn, expressions[0]);

		DSLQuery dslQuery = groupBy.limit(0, 20);

		Assert.assertEquals(
			"select MainExample.mainExampleId, MainExample.name from " +
				"MainExample group by MainExample.name ",
			dslQuery.toString());

		try {
			from.groupBy();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Expressions is empty", exception.getMessage());
		}
	}

	@Test
	public void testHaving() {
		HavingStep havingStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleIdColumn
		).from(
			MainExampleTable.INSTANCE
		).groupBy(
			MainExampleTable.INSTANCE.nameColumn
		);

		OrderByStep orderByStep = havingStep.having(null);

		Assert.assertEquals(
			"select MainExample.mainExampleId from MainExample group by " +
				"MainExample.name",
			orderByStep.toString());

		Predicate predicate = DSLFunctionFactoryUtil.count(
			MainExampleTable.INSTANCE.nameColumn
		).gt(
			10L
		);

		Having having = new Having(havingStep, predicate);

		Assert.assertEquals(
			"select MainExample.mainExampleId from MainExample group by " +
				"MainExample.name having count(MainExample.name) > ?",
			having.toString());

		Assert.assertSame(predicate, having.getPredicate());
	}

	@Test
	public void testJoin() {
		Assert.assertEquals("inner", JoinType.INNER.toString());
		Assert.assertEquals("left", JoinType.LEFT.toString());

		Predicate onPredicate =
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn.eq(
				MainExampleTable.INSTANCE.mainExampleIdColumn);

		Join join = new Join(
			DSLQueryFactoryUtil.countDistinct(
				MainExampleTable.INSTANCE.nameColumn
			).from(
				MainExampleTable.INSTANCE
			),
			JoinType.INNER, ReferenceExampleTable.INSTANCE, onPredicate);

		Assert.assertSame(ReferenceExampleTable.INSTANCE, join.getTable());
		Assert.assertSame(JoinType.INNER, join.getJoinType());
		Assert.assertSame(onPredicate, join.getOnPredicate());
	}

	@Test
	public void testJoinCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.countDistinct(
			MainExampleTable.INSTANCE.nameColumn
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			MainExampleTable.INSTANCE, null
		).innerJoinON(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn.eq(
				MainExampleTable.INSTANCE.mainExampleIdColumn)
		).innerJoinON(
			ReferenceExampleTable.INSTANCE, null
		).where(
			MainExampleTable.INSTANCE.nameColumn.neq("")
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select count(distinct MainExample.name) COUNT_VALUE from ",
				"MainExample inner join ReferenceExample on ",
				"ReferenceExample.mainExampleId = MainExample.mainExampleId ",
				"where MainExample.name != ?"),
			dslQuery.toString());
	}

	@Test
	public void testLargePredicate() {
		int count = 10000;

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			MainExampleTable.INSTANCE
		).where(
			() -> {
				Predicate predicate =
					MainExampleTable.INSTANCE.mainExampleIdColumn.eq(0L);

				for (long i = 1; i < count; i++) {
					predicate = predicate.or(
						MainExampleTable.INSTANCE.mainExampleIdColumn.eq(i));
				}

				return predicate;
			}
		);

		StringBundler sb = new StringBundler((2 * count) + 1);

		sb.append("select * from MainExample where ");

		for (int i = 0; i < count; i++) {
			sb.append("MainExample.mainExampleId = ?");
			sb.append(" or ");
		}

		sb.setIndex(sb.index() - 1);

		Assert.assertEquals(sb.toString(), dslQuery.toString());
	}

	@Test
	public void testLeftJoin() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleIdColumn
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			ReferenceExampleTable.INSTANCE,
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn.eq(
				MainExampleTable.INSTANCE.mainExampleIdColumn)
		).where(
			ReferenceExampleTable.INSTANCE.mainExampleIdColumn.isNull()
		).orderBy(
			MainExampleTable.INSTANCE.flagColumn.descending(),
			MainExampleTable.INSTANCE.nameColumn.ascending()
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select MainExample.mainExampleId from MainExample left join ",
				"ReferenceExample on ReferenceExample.mainExampleId = ",
				"MainExample.mainExampleId where ",
				"ReferenceExample.mainExampleId is NULL order by ",
				"MainExample.flag desc, MainExample.name asc"),
			dslQuery.toString());
	}

	@Test
	public void testOperands() {
		Assert.assertEquals("and", Operand.AND.toString());

		Assert.assertEquals("=", Operand.EQUAL.toString());

		Assert.assertEquals(">", Operand.GREATER_THAN.toString());

		Assert.assertEquals(">=", Operand.GREATER_THAN_OR_EQUAL.toString());

		Assert.assertEquals("in", Operand.IN.toString());

		Assert.assertEquals("is", Operand.IS.toString());

		Assert.assertEquals("is not", Operand.IS_NOT.toString());

		Assert.assertEquals("<", Operand.LESS_THAN.toString());

		Assert.assertEquals("<=", Operand.LESS_THAN_OR_EQUAL.toString());

		Assert.assertEquals("like", Operand.LIKE.toString());

		Assert.assertEquals("!=", Operand.NOT_EQUAL.toString());

		Assert.assertEquals("not in", Operand.NOT_IN.toString());

		Assert.assertEquals("not like", Operand.NOT_LIKE.toString());

		Assert.assertEquals("or", Operand.OR.toString());
	}

	@Test
	public void testOrderBy() {
		JoinStep joinStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.nameColumn
		).from(
			MainExampleTable.INSTANCE
		);

		Assert.assertEquals(
			"select MainExample.name from MainExample", joinStep.toString());

		LimitStep limitStep = joinStep.orderBy();

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy((OrderByExpression[])null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy(orderByStep -> null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", limitStep.toString());

		limitStep = joinStep.orderBy(
			orderByStep -> orderByStep.orderBy(
				MainExampleTable.INSTANCE.nameColumn.ascending()));

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.name asc",
			limitStep.toString());

		try {
			new OrderBy(joinStep, new OrderByExpression[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals(
				"Order by expressions is empty", exception.getMessage());
		}

		OrderByExpression orderByExpression =
			MainExampleTable.INSTANCE.nameColumn.ascending();

		OrderByExpression[] orderByExpressions = {orderByExpression};

		OrderBy orderBy = new OrderBy(joinStep, orderByExpressions);

		Assert.assertSame(orderByExpressions, orderBy.getOrderByExpressions());

		Assert.assertEquals(
			MainExampleTable.INSTANCE.nameColumn,
			orderByExpression.getExpression());
		Assert.assertTrue(
			orderByExpression.toString(), orderByExpression.isAscending());

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.name asc",
			orderBy.toString());
	}

	@Test
	public void testOrderByOrderByInfo() {
		OrderByStep orderByStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.nameColumn
		).from(
			MainExampleTable.INSTANCE
		);

		DSLQuery dslQuery = orderByStep.orderBy(
			MainExampleTable.INSTANCE, null);

		Assert.assertEquals(
			"select MainExample.name from MainExample", dslQuery.toString());

		OrderByInfo orderByInfo = new TestOrderByInfo(
			MainExampleTable.INSTANCE.flagColumn.getName());

		dslQuery = orderByStep.orderBy(MainExampleTable.INSTANCE, orderByInfo);

		Assert.assertEquals(
			"select MainExample.name from MainExample order by " +
				"MainExample.flag asc",
			dslQuery.toString());

		dslQuery = orderByStep.orderBy(
			ReferenceExampleTable.INSTANCE, orderByInfo);

		Assert.assertEquals(
			"select MainExample.name from MainExample", dslQuery.toString());
	}

	@Test
	public void testOrderByOrderByInfoWithAlias() {
		ColumnAlias<MainExampleTable, String> columnAlias =
			MainExampleTable.INSTANCE.nameColumn.as("columnAlias");

		OrderByStep orderByStep = DSLQueryFactoryUtil.select(
			columnAlias
		).from(
			columnAlias.getTable()
		);

		DSLQuery dslQuery = orderByStep.orderBy(columnAlias.getTable(), null);

		Assert.assertEquals(
			"select MainExample.name columnAlias from MainExample",
			dslQuery.toString());

		OrderByInfo orderByInfo = new TestOrderByInfo(
			columnAlias.getName(),
			ReferenceExampleTable.INSTANCE.referenceExampleIdColumn.getName());

		dslQuery = orderByStep.orderBy(columnAlias.getTable(), orderByInfo);

		Assert.assertEquals(
			"select MainExample.name columnAlias from MainExample order by " +
				"columnAlias asc",
			dslQuery.toString());
	}

	@Test
	public void testPredicate() {
		Predicate predicate = MainExampleTable.INSTANCE.nameColumn.eq("test");

		Assert.assertNull(Predicate.and(null, null));
		Assert.assertSame(predicate, Predicate.and(predicate, null));
		Assert.assertSame(predicate, Predicate.and(null, predicate));
		Assert.assertEquals(
			String.valueOf(predicate.and(predicate)),
			String.valueOf(Predicate.and(predicate, predicate)));

		Assert.assertNull(Predicate.or(null, null));
		Assert.assertSame(predicate, Predicate.or(predicate, null));
		Assert.assertSame(predicate, Predicate.or(null, predicate));
		Assert.assertEquals(
			String.valueOf(predicate.or(predicate)),
			String.valueOf(Predicate.or(predicate, predicate)));

		Assert.assertNull(Predicate.withParentheses(null));
		Assert.assertEquals(
			String.valueOf(predicate.withParentheses()),
			String.valueOf(Predicate.withParentheses(predicate)));
	}

	@Test
	public void testPredicateNot() {
		Predicate leftPredicate =
			MainExampleTable.INSTANCE.mainExampleIdColumn.gte(1L);
		Predicate rightPredicate =
			MainExampleTable.INSTANCE.mainExampleIdColumn.lte(3L);

		DefaultPredicate defaultPredicate = new DefaultPredicate(
			leftPredicate, Operand.OR, rightPredicate);

		Assert.assertEquals(
			"MainExample.mainExampleId >= ?", String.valueOf(leftPredicate));

		Assert.assertEquals(
			"not (MainExample.mainExampleId >= ?)",
			String.valueOf(leftPredicate.not()));

		Assert.assertEquals(
			"(MainExample.mainExampleId <= ?)",
			String.valueOf(rightPredicate.withParentheses()));

		Assert.assertEquals(
			"not (MainExample.mainExampleId <= ?)",
			String.valueOf(rightPredicate.not()));

		Assert.assertSame(leftPredicate, defaultPredicate.getLeftExpression());
		Assert.assertSame(Operand.OR, defaultPredicate.getOperand());
		Assert.assertSame(
			rightPredicate, defaultPredicate.getRightExpression());
		Assert.assertFalse(defaultPredicate.isNot());
		Assert.assertFalse(defaultPredicate.isWrapParentheses());

		Assert.assertEquals(
			"MainExample.mainExampleId >= ? or MainExample.mainExampleId <= ?",
			String.valueOf(defaultPredicate));
		Assert.assertEquals(
			"not (MainExample.mainExampleId >= ? or " +
				"MainExample.mainExampleId <= ?)",
			String.valueOf(defaultPredicate.not()));

		Predicate notPredicate = defaultPredicate.not();

		Assert.assertEquals(
			String.valueOf(defaultPredicate.not()),
			String.valueOf(notPredicate.not()));
	}

	@Test
	public void testPredicateParentheses() {
		Predicate leftPredicate =
			MainExampleTable.INSTANCE.mainExampleIdColumn.gte(1L);

		Predicate rightPredicate = MainExampleTable.INSTANCE.nameColumn.eq(
			"test"
		).or(
			MainExampleTable.INSTANCE.nameColumn.eq((String)null)
		).withParentheses();

		DefaultPredicate defaultPredicate = new DefaultPredicate(
			leftPredicate, Operand.AND, rightPredicate);

		Assert.assertSame(leftPredicate, defaultPredicate.getLeftExpression());
		Assert.assertSame(Operand.AND, defaultPredicate.getOperand());
		Assert.assertSame(
			rightPredicate, defaultPredicate.getRightExpression());

		Assert.assertFalse(defaultPredicate.isWrapParentheses());

		Assert.assertSame(rightPredicate, rightPredicate.withParentheses());

		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		).where(
			defaultPredicate
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select count(*) COUNT_VALUE from MainExample where ",
				"MainExample.mainExampleId >= ? and (MainExample.name = ? or ",
				"MainExample.name = ?)"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(1L, "test", null),
			defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testQueryTable() {
		FromStep fromStep1 = DSLQueryFactoryUtil.select(new Scalar<>(1));

		Table<?> table1 = fromStep1.as("alias");

		Assert.assertEquals("(select ?) alias", table1.toString());

		Assert.assertEquals(System.identityHashCode(table1), table1.hashCode());

		QueryTable queryTable = (QueryTable)table1;

		Assert.assertSame(fromStep1, queryTable.getDslQuery());

		FromStep fromStep2 = DSLQueryFactoryUtil.select(new Scalar<>(2));

		Table<?> table2 = fromStep2.as("alias");

		Assert.assertEquals(table1, table1);

		Assert.assertNotEquals(table1, table2);

		queryTable = (QueryTable)table2;

		Assert.assertSame(fromStep2, queryTable.getDslQuery());

		Table<?> table3 = fromStep2.as(
			"alias",
			Arrays.asList(
				MainExampleTable.INSTANCE.nameColumn,
				MainExampleTable.INSTANCE.descriptionColumn));

		Collection<Column<?, ?>> columns =
			(Collection<Column<?, ?>>)table3.getColumns();

		Assert.assertEquals(columns.toString(), 2, columns.size());

		Iterator<Column<?, ?>> iterator = columns.iterator();

		Column<?, ?> column = iterator.next();

		Assert.assertSame(table3, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.nameColumn.getName(), column.getName());

		column = iterator.next();

		Assert.assertSame(table3, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.descriptionColumn.getName(),
			column.getName());

		Table<?> table4 = fromStep2.as("alias", MainExampleTable.INSTANCE);

		columns = (Collection<Column<?, ?>>)table4.getColumns();

		Assert.assertEquals(columns.toString(), 4, columns.size());

		iterator = columns.iterator();

		column = iterator.next();

		Assert.assertSame(table4, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.descriptionColumn.getName(),
			column.getName());

		column = iterator.next();

		Assert.assertSame(table4, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.flagColumn.getName(), column.getName());

		column = iterator.next();

		Assert.assertSame(table4, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.mainExampleIdColumn.getName(),
			column.getName());

		column = iterator.next();

		Assert.assertSame(table4, column.getTable());
		Assert.assertEquals(
			MainExampleTable.INSTANCE.nameColumn.getName(), column.getName());
	}

	@Test
	public void testScalarDSLQueryAlias() {
		ScalarDSLQueryAlias scalarSubDSLQuery =
			(ScalarDSLQueryAlias)DSLQueryFactoryUtil.scalarSubDSLQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.count(
						MainExampleTable.INSTANCE.mainExampleIdColumn)
				).from(
					MainExampleTable.INSTANCE
				),
				Long.class, "mainExampleField", Types.BIGINT);

		Assert.assertEquals(
			"select count(MainExample.mainExampleId) from MainExample",
			String.valueOf(scalarSubDSLQuery.getDSLQuery()));
		Assert.assertEquals(Long.class, scalarSubDSLQuery.getJavaType());
		Assert.assertEquals("mainExampleField", scalarSubDSLQuery.getName());
		Assert.assertEquals(Types.BIGINT, scalarSubDSLQuery.getSQLType());
		Assert.assertEquals(
			"(select count(MainExample.mainExampleId) from MainExample" +
				") as mainExampleField",
			scalarSubDSLQuery.toString());
	}

	@Test
	public void testScalarList() {
		Long[] longs = {0L, 1L, 2L};

		ScalarList<Long> scalarList = new ScalarList<>(longs);

		Assert.assertSame(longs, scalarList.getValues());

		try {
			new ScalarList<>(new String[0]);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				IllegalArgumentException.class, exception.getClass());

			Assert.assertEquals("Values is empty", exception.getMessage());
		}
	}

	@Test
	public void testSelect() {
		List<Expression<?>> expressions = Arrays.asList(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE.flagColumn);

		Select select = new Select(true, expressions);

		Assert.assertTrue(select.isDistinct());
		Assert.assertSame(expressions, select.getExpressions());
	}

	@Test
	public void testSelect1() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(new Scalar<>(1));

		Assert.assertEquals("select ?", dslQuery.toString());
	}

	@Test
	public void testSelectDistinctWhereInWithAlias() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"mainTable");

		JoinStep joinStep = DSLQueryFactoryUtil.selectDistinct(
			aliasMainExampleTable.nameColumn
		).from(
			aliasMainExampleTable
		);

		DSLQuery dslQuery = joinStep.where(
			aliasMainExampleTable.flagColumn.in(new Integer[] {1, 2}));

		Assert.assertEquals(
			"select distinct mainTable.name from MainExample mainTable where " +
				"mainTable.flag in (?, ?)",
			dslQuery.toString());

		dslQuery = joinStep.where(
			aliasMainExampleTable.mainExampleIdColumn.in(
				new Long[] {1L, 2L, null})
		).orderBy(
			aliasMainExampleTable.nameColumn.ascending()
		);

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct mainTable.name from MainExample mainTable ",
				"where mainTable.mainExampleId in (?, ?, ?) order by ",
				"mainTable.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(1L, 2L, null),
			defaultASTNodeListener.getScalarValues());

		String[] strings = {"1", "2", "3"};

		dslQuery = joinStep.where(
			DSLFunctionFactoryUtil.castText(
				aliasMainExampleTable.mainExampleIdColumn
			).in(
				strings
			)
		).orderBy(
			aliasMainExampleTable.nameColumn.ascending()
		);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct mainTable.name from MainExample mainTable ",
				"where CAST_TEXT(mainTable.mainExampleId) in (?, ?, ?) order ",
				"by mainTable.name asc"),
			dslQuery.toSQL(defaultASTNodeListener));

		Assert.assertEquals(
			Arrays.asList(strings), defaultASTNodeListener.getScalarValues());
	}

	@Test
	public void testSelectTable() {
		FromStep fromStep = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE);

		Assert.assertEquals("select MainExample.*", fromStep.toString());

		fromStep = DSLQueryFactoryUtil.selectDistinct(
			MainExampleTable.INSTANCE);

		Assert.assertEquals(
			"select distinct MainExample.*", fromStep.toString());
	}

	@Test
	public void testSelfJoin() {
		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"tempMainExample");

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.mainExampleIdColumn,
			MainExampleTable.INSTANCE.nameColumn
		).from(
			MainExampleTable.INSTANCE
		).leftJoinOn(
			aliasMainExampleTable,
			MainExampleTable.INSTANCE.mainExampleIdColumn.lt(
				aliasMainExampleTable.mainExampleIdColumn)
		).where(
			aliasMainExampleTable.mainExampleIdColumn.isNull()
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select MainExample.mainExampleId, MainExample.name from ",
				"MainExample left join MainExample tempMainExample on ",
				"MainExample.mainExampleId < tempMainExample.mainExampleId ",
				"where tempMainExample.mainExampleId is NULL"),
			dslQuery.toString());
	}

	@Test
	public void testSimpleCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		);

		Assert.assertEquals(
			"select count(*) COUNT_VALUE from MainExample",
			dslQuery.toString());
	}

	@Test
	public void testSimpleSelect() {
		FromStep fromStep = DSLQueryFactoryUtil.select();

		Assert.assertEquals("select *", fromStep.toString());

		JoinStep joinStep = fromStep.from(MainExampleTable.INSTANCE);

		Assert.assertEquals("select * from MainExample", joinStep.toString());

		GroupByStep groupByStep = joinStep.where(() -> null);

		Assert.assertEquals(
			"select * from MainExample", groupByStep.toString());

		Predicate predicate = MainExampleTable.INSTANCE.nameColumn.eq("test");

		Assert.assertEquals("MainExample.name = ?", predicate.toString());

		predicate = predicate.and(
			MainExampleTable.INSTANCE.mainExampleIdColumn.gt(0L));

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		predicate = predicate.and((Expression<Boolean>)null);

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		predicate = predicate.or((Expression<Boolean>)null);

		Assert.assertEquals(
			"MainExample.name = ? and MainExample.mainExampleId > ?",
			predicate.toString());

		Where where = new Where(joinStep, predicate);

		Assert.assertSame(predicate, where.getPredicate());

		Assert.assertEquals(
			"select * from MainExample where MainExample.name = ? and " +
				"MainExample.mainExampleId > ?",
			where.toString());

		OrderByExpression orderByExpression =
			MainExampleTable.INSTANCE.mainExampleIdColumn.descending();

		Assert.assertEquals(
			"MainExample.mainExampleId desc", orderByExpression.toString());

		OrderBy orderBy = new OrderBy(
			where, new OrderByExpression[] {orderByExpression});

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc"),
			orderBy.toSQL(defaultASTNodeListener));

		Assert.assertArrayEquals(
			new String[] {MainExampleTable.INSTANCE.getTableName()},
			defaultASTNodeListener.getTableNames());

		Assert.assertEquals(
			Arrays.asList("test", 0L),
			defaultASTNodeListener.getScalarValues());

		Assert.assertEquals(-1, defaultASTNodeListener.getStart());
		Assert.assertEquals(-1, defaultASTNodeListener.getEnd());

		Limit limit = new Limit(orderBy, -1, -1);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc "),
			limit.toSQL(defaultASTNodeListener));

		Assert.assertEquals(-1, defaultASTNodeListener.getStart());
		Assert.assertEquals(-1, defaultASTNodeListener.getEnd());

		limit = new Limit(orderBy, 10, 30);

		defaultASTNodeListener = new DefaultASTNodeListener();

		Assert.assertEquals(
			StringBundler.concat(
				"select * from MainExample where MainExample.name = ? and ",
				"MainExample.mainExampleId > ? order by ",
				"MainExample.mainExampleId desc "),
			limit.toSQL(defaultASTNodeListener));

		Assert.assertEquals(10, defaultASTNodeListener.getStart());
		Assert.assertEquals(30, defaultASTNodeListener.getEnd());
	}

	@Test
	public void testSubqueryCount() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.count(
		).from(
			MainExampleTable.INSTANCE
		).where(
			MainExampleTable.INSTANCE.mainExampleIdColumn.in(
				DSLQueryFactoryUtil.select(
					ReferenceExampleTable.INSTANCE.mainExampleIdColumn
				).from(
					ReferenceExampleTable.INSTANCE
				).where(
					() -> ReferenceExampleTable.INSTANCE.nameColumn.eq("test")
				))
		);

		Assert.assertEquals(
			StringBundler.concat(
				"select count(*) COUNT_VALUE from MainExample where ",
				"MainExample.mainExampleId in (select ",
				"ReferenceExample.mainExampleId from ReferenceExample where ",
				"ReferenceExample.name = ?)"),
			dslQuery.toString());
	}

	@Test
	public void testTable() {
		Assert.assertEquals(
			"MainExample", MainExampleTable.INSTANCE.toString());

		MainExampleTable aliasMainExampleTable = MainExampleTable.INSTANCE.as(
			"alias");

		Assert.assertNotSame(MainExampleTable.INSTANCE, aliasMainExampleTable);

		Assert.assertEquals(
			MainExampleTable.INSTANCE, MainExampleTable.INSTANCE);
		Assert.assertEquals(MainExampleTable.INSTANCE, aliasMainExampleTable);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE, MainExampleTable.INSTANCE.nameColumn);
		Assert.assertNotEquals(
			MainExampleTable.INSTANCE, ReferenceExampleTable.INSTANCE);

		Assert.assertEquals(
			MainExampleTable.INSTANCE.hashCode(),
			aliasMainExampleTable.hashCode());

		Assert.assertSame(
			MainExampleTable.INSTANCE.nameColumn,
			MainExampleTable.INSTANCE.getColumn(
				MainExampleTable.INSTANCE.nameColumn.getName(),
				MainExampleTable.INSTANCE.nameColumn.getJavaType()));
		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(
				MainExampleTable.INSTANCE.nameColumn.getName(), Long.class));

		ColumnAlias<MainExampleTable, String> nameColumnAlias =
			aliasMainExampleTable.nameColumn.as("nameColumnAlias");

		Assert.assertEquals(
			"MainExample alias", aliasMainExampleTable.toString());

		Assert.assertNotSame(
			MainExampleTable.INSTANCE.nameColumn,
			nameColumnAlias.getExpression());

		aliasMainExampleTable = nameColumnAlias.getTable();

		Assert.assertEquals("alias", aliasMainExampleTable.getName());

		Column<MainExampleTable, String> column =
			(Column<MainExampleTable, String>)nameColumnAlias.getExpression();

		Assert.assertSame(column.getTable(), nameColumnAlias.getTable());

		Assert.assertEquals(
			MainExampleTable.INSTANCE.nameColumn,
			aliasMainExampleTable.getColumn(
				nameColumnAlias.getName(), column.getJavaType()));

		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(nameColumnAlias.getName()));
		Assert.assertNull(
			MainExampleTable.INSTANCE.getColumn(
				nameColumnAlias.getName(), column.getJavaType()));

		Collection<Column<MainExampleTable, ?>> columns =
			MainExampleTable.INSTANCE.getColumns();

		Assert.assertEquals(columns.toString(), 4, columns.size());

		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.mainExampleIdColumn));
		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.nameColumn));
		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.descriptionColumn));
		Assert.assertTrue(
			columns.contains(MainExampleTable.INSTANCE.flagColumn));

		try {
			columns.remove(MainExampleTable.INSTANCE.mainExampleIdColumn);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(
				UnsupportedOperationException.class, exception.getClass());
		}
	}

	@Test
	public void testTableStar() {
		TableStar tableStar = new TableStar(MainExampleTable.INSTANCE);

		Assert.assertSame(MainExampleTable.INSTANCE, tableStar.getTable());

		Assert.assertEquals("MainExample.*", tableStar.toString());

		tableStar = new TableStar(MainExampleTable.INSTANCE.as("alias"));

		Assert.assertEquals("alias.*", tableStar.toString());
	}

	@Test
	public void testUnionSelect() {
		Assert.assertEquals("union", SetOperationType.UNION.toString());

		Assert.assertEquals("union all", SetOperationType.UNION_ALL.toString());

		DSLQuery dslQuery1 = DSLQueryFactoryUtil.select(
			MainExampleTable.INSTANCE.nameColumn.as("name")
		).from(
			MainExampleTable.INSTANCE
		);

		DSLQuery dslQuery2 = DSLQueryFactoryUtil.select(
			ReferenceExampleTable.INSTANCE.nameColumn.as("name")
		).from(
			ReferenceExampleTable.INSTANCE
		);

		SetOperation setOperation = (SetOperation)dslQuery1.union(dslQuery2);

		Assert.assertSame(dslQuery1, setOperation.getLeftDSLQuery());

		Assert.assertSame(
			SetOperationType.UNION, setOperation.getSetOperationType());

		Assert.assertSame(dslQuery2, setOperation.getRightDSLQuery());

		Assert.assertEquals(
			"select MainExample.name name from MainExample union select " +
				"ReferenceExample.name name from ReferenceExample",
			setOperation.toString());

		setOperation = (SetOperation)dslQuery1.unionAll(
			DSLQueryFactoryUtil.select(
				ReferenceExampleTable.INSTANCE.nameColumn.as("name")
			).from(
				ReferenceExampleTable.INSTANCE
			));

		Assert.assertSame(
			SetOperationType.UNION_ALL, setOperation.getSetOperationType());

		String sql =
			"select MainExample.name name from MainExample union all select " +
				"ReferenceExample.name name from ReferenceExample";

		Assert.assertEquals(sql, setOperation.toString());

		setOperation = (SetOperation)setOperation.union(setOperation);

		Assert.assertEquals(
			StringBundler.concat(sql, " union ", sql), setOperation.toString());
	}

	@Test
	public void testWhenThen() {
		Predicate predicate = MainExampleTable.INSTANCE.mainExampleIdColumn.eq(
			2L);

		Scalar<String> scalar = new Scalar<>("two");

		WhenThen<String> whenThen = new WhenThen<>(
			DSLFunctionFactoryUtil.caseWhenThen(
				MainExampleTable.INSTANCE.mainExampleIdColumn.eq(1L), "one"),
			predicate, scalar);

		Assert.assertSame(predicate, whenThen.getPredicate());

		Assert.assertSame(scalar, whenThen.getThenExpression());
	}

	private static class MainExampleTable extends BaseTable<MainExampleTable> {

		public static final MainExampleTable INSTANCE = new MainExampleTable();

		public final Column<MainExampleTable, Clob> descriptionColumn =
			createColumn(
				"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
		public final Column<MainExampleTable, Integer> flagColumn =
			createColumn(
				"flag", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
		public final Column<MainExampleTable, Long> mainExampleIdColumn =
			createColumn(
				"mainExampleId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
		public final Column<MainExampleTable, String> nameColumn = createColumn(
			"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		private MainExampleTable() {
			super("MainExample", MainExampleTable::new);
		}

	}

	private static class ReferenceExampleTable
		extends BaseTable<ReferenceExampleTable> {

		public static final ReferenceExampleTable INSTANCE =
			new ReferenceExampleTable();

		public final Column<ReferenceExampleTable, Long> mainExampleIdColumn =
			createColumn(
				"mainExampleId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
		public final Column<ReferenceExampleTable, String> nameColumn =
			createColumn(
				"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
		public final Column<ReferenceExampleTable, Long>
			referenceExampleIdColumn = createColumn(
				"referenceExampleId", Long.class, Types.BIGINT,
				Column.FLAG_DEFAULT);

		private ReferenceExampleTable() {
			super("ReferenceExample", ReferenceExampleTable::new);
		}

	}

	private static class TestOrderByInfo implements OrderByInfo {

		@Override
		public String[] getOrderByFields() {
			return _orderByFields;
		}

		@Override
		public boolean isAscending(String field) {
			return true;
		}

		private TestOrderByInfo(String... orderByFields) {
			_orderByFields = orderByFields;
		}

		private final String[] _orderByFields;

	}

}