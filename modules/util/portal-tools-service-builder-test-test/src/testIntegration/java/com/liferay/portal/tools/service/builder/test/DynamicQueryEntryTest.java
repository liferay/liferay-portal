/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DBManager;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.DynamicQueryEntry;
import com.liferay.portal.tools.service.builder.test.service.DynamicQueryEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DynamicQueryEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		long time = System.currentTimeMillis();

		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				100, new Date(time - 4000), "first", new Date(time - 4000),
				"alpha", 1));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				200, new Date(time - 3000), null, new Date(time - 1500), "beta",
				2));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				300, new Date(time - 2000), "third", new Date(time - 2000),
				"gamma", 1));
		_dynamicQueryEntries.add(
			_addDynamicQueryEntry(
				400, new Date(time - 1000), "fourth", new Date(time - 500),
				"delta", 3));
	}

	@AfterClass
	public static void tearDownClass() {
		for (DynamicQueryEntry dynamicQueryEntry : _dynamicQueryEntries) {
			_dynamicQueryEntryLocalService.deleteDynamicQueryEntry(
				dynamicQueryEntry);
		}
	}

	@Test
	public void testAddOrder() {
		_testAddOrder(
			OrderFactoryUtil.asc("amount"), "alpha", "beta", "gamma", "delta");
		_testAddOrder(
			OrderFactoryUtil.desc("amount"), "delta", "gamma", "beta", "alpha");
	}

	@Test
	public void testCriterionWithCompareProperty() {
		_testCriterion(
			RestrictionsFactoryUtil.eqProperty("createDate", "modifiedDate"),
			"alpha", "gamma");
		_testCriterion(
			RestrictionsFactoryUtil.geProperty("modifiedDate", "createDate"),
			"alpha", "beta", "gamma", "delta");
		_testCriterion(
			RestrictionsFactoryUtil.gtProperty("modifiedDate", "createDate"),
			"beta", "delta");
		_testCriterion(
			RestrictionsFactoryUtil.leProperty("createDate", "modifiedDate"),
			"alpha", "beta", "gamma", "delta");
		_testCriterion(
			RestrictionsFactoryUtil.ltProperty("createDate", "modifiedDate"),
			"beta", "delta");
		_testCriterion(
			RestrictionsFactoryUtil.neProperty("createDate", "modifiedDate"),
			"beta", "delta");
	}

	@Test
	public void testCriterionWithConjunction() {
		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(RestrictionsFactoryUtil.eq("status", 1));
		conjunction.add(RestrictionsFactoryUtil.ge("amount", 100L));
		conjunction.add(RestrictionsFactoryUtil.le("amount", 300L));

		_testCriterion(conjunction, "alpha", "gamma");
	}

	@Test
	public void testCriterionWithDisjunction() {
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		disjunction.add(RestrictionsFactoryUtil.isNull("description"));
		disjunction.add(RestrictionsFactoryUtil.like("name", "g%"));

		_testCriterion(disjunction, "alpha", "beta", "gamma");
	}

	@Test
	public void testCriterionWithEq() {
		_testCriterion(RestrictionsFactoryUtil.eq("name", "alpha"), "alpha");

		Property property = PropertyFactoryUtil.forName("name");

		_testCriterion(property.eq("alpha"), "alpha");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("name", "alpha"));

		dynamicQuery.setProjection(property);

		_testCriterion(property.eq(dynamicQuery), "alpha");

		_testCriterion(property.eqAll(dynamicQuery), "alpha");

		_testCriterion(
			RestrictionsFactoryUtil.sqlRestriction(
				"name = ?", "alpha", Type.STRING),
			"alpha");
	}

	@Test
	public void testCriterionWithGe() {
		_testCriterion(
			RestrictionsFactoryUtil.ge("amount", 200L), "beta", "gamma",
			"delta");

		Property property = PropertyFactoryUtil.forName("amount");

		_testCriterion(property.ge(200L), "beta", "gamma", "delta");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		dynamicQuery.setProjection(property);

		_testCriterion(property.ge(dynamicQuery), "beta", "gamma", "delta");

		dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		dynamicQuery.setProjection(property);

		_testCriterion(property.geAll(dynamicQuery), "gamma", "delta");
		_testCriterion(
			property.geSome(dynamicQuery), "alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testCriterionWithGt() {
		_testCriterion(
			RestrictionsFactoryUtil.gt("amount", 200L), "gamma", "delta");

		Property property = PropertyFactoryUtil.forName("amount");

		_testCriterion(property.gt(200L), "gamma", "delta");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		dynamicQuery.setProjection(property);

		_testCriterion(property.gt(dynamicQuery), "gamma", "delta");

		dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		dynamicQuery.setProjection(property);

		_testCriterion(property.gtAll(dynamicQuery), "delta");
		_testCriterion(property.gtSome(dynamicQuery), "beta", "gamma", "delta");
	}

	@Test
	public void testCriterionWithIn() {
		_testCriterion(
			RestrictionsFactoryUtil.in("status", new Integer[] {1, 3}), "alpha",
			"gamma", "delta");

		Property property = PropertyFactoryUtil.forName("status");

		_testCriterion(
			property.in(Arrays.asList(1, 3)), "alpha", "gamma", "delta");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ne("status", 2));
		dynamicQuery.setProjection(property);

		_testCriterion(property.in(dynamicQuery), "alpha", "gamma", "delta");
		_testCriterion(property.notIn(dynamicQuery), "beta");

		dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.distinct(
				ProjectionFactoryUtil.property("status")));

		_testCriterion(property.in(dynamicQuery), "alpha", "gamma");
	}

	@Test
	public void testCriterionWithLe() {
		_testCriterion(
			RestrictionsFactoryUtil.le("amount", 200L), "alpha", "beta");

		Property property = PropertyFactoryUtil.forName("amount");

		_testCriterion(property.le(200L), "alpha", "beta");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		dynamicQuery.setProjection(property);

		_testCriterion(property.le(dynamicQuery), "alpha", "beta");

		dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 1));
		dynamicQuery.setProjection(property);

		_testCriterion(property.leAll(dynamicQuery), "alpha");
		_testCriterion(property.leSome(dynamicQuery), "alpha", "beta", "gamma");
	}

	@Test
	public void testCriterionWithLike() throws Exception {
		_testCriterion(RestrictionsFactoryUtil.ilike("name", "A%"), "alpha");

		_testCriterion(
			RestrictionsFactoryUtil.like("name", "%a"), "alpha", "beta",
			"gamma", "delta");

		Property property = PropertyFactoryUtil.forName("name");

		_testCriterion(property.like("%a"), "alpha", "beta", "gamma", "delta");

		_testCriterion(
			RestrictionsFactoryUtil.sqlRestriction("name like 'alph%'"),
			"alpha");

		_testCriterion(
			RestrictionsFactoryUtil.sqlRestriction("name not like 'alph%'"),
			"beta", "gamma", "delta");

		DynamicQueryEntry dynamicQueryEntry = _addDynamicQueryEntry(
			500, new Date(), null, new Date(), "a%b", 0);

		try {
			_testCriterion(
				RestrictionsFactoryUtil.sqlRestriction(
					"name like 'a=%%' ESCAPE '='"),
				"a%b");
		}
		finally {
			_dynamicQueryEntryLocalService.deleteDynamicQueryEntry(
				dynamicQueryEntry);
		}
	}

	@Test
	public void testCriterionWithLt() {
		_testCriterion(RestrictionsFactoryUtil.lt("amount", 200L), "alpha");

		Property property = PropertyFactoryUtil.forName("amount");

		_testCriterion(property.lt(200L), "alpha");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("status", 2));
		dynamicQuery.setProjection(property);

		_testCriterion(property.lt(dynamicQuery), "alpha");

		dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.ge("amount", 300L));
		dynamicQuery.setProjection(property);

		_testCriterion(property.ltAll(dynamicQuery), "alpha", "beta");
		_testCriterion(property.ltSome(dynamicQuery), "alpha", "beta", "gamma");
	}

	@Test
	public void testCriterionWithNe() {
		_testCriterion(
			RestrictionsFactoryUtil.ne("name", "alpha"), "beta", "gamma",
			"delta");

		Property property = PropertyFactoryUtil.forName("name");

		_testCriterion(property.ne("alpha"), "beta", "gamma", "delta");

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		dynamicQuery.setProjection(property);

		_testCriterion(property.ne(dynamicQuery), "beta", "gamma", "delta");
	}

	@Test
	public void testCriterionWithNestedConjunctionInsideDisjunction() {
		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(RestrictionsFactoryUtil.eq("status", 1));
		conjunction.add(RestrictionsFactoryUtil.ge("amount", 200L));

		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(conjunction);
		disjunction.add(RestrictionsFactoryUtil.eq("name", "delta"));

		_testCriterion(disjunction, "gamma", "delta");
	}

	@Test
	public void testCriterionWithNestedDisjunctionInsideConjunction() {
		Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

		disjunction.add(RestrictionsFactoryUtil.eq("name", "alpha"));
		disjunction.add(RestrictionsFactoryUtil.eq("name", "gamma"));

		Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

		conjunction.add(disjunction);
		conjunction.add(RestrictionsFactoryUtil.eq("status", 1));

		_testCriterion(conjunction, "alpha", "gamma");
	}

	@Test
	public void testCriterionWithNull() {
		_testCriterion(
			RestrictionsFactoryUtil.isNotNull("description"), "alpha", "gamma",
			"delta");
		_testCriterion(RestrictionsFactoryUtil.isNull("description"), "beta");

		Property property = PropertyFactoryUtil.forName("description");

		_testCriterion(property.isNotNull(), "alpha", "gamma", "delta");
		_testCriterion(property.isNull(), "beta");
	}

	@Test
	public void testCriterionWithOperators() {
		_testCriterion(
			RestrictionsFactoryUtil.and(
				RestrictionsFactoryUtil.eq("status", 1),
				RestrictionsFactoryUtil.ge("amount", 200L)),
			"gamma");

		_testCriterion(
			RestrictionsFactoryUtil.between("amount", 150L, 350L), "beta",
			"gamma");

		_testCriterion(
			RestrictionsFactoryUtil.not(
				RestrictionsFactoryUtil.eq("name", "alpha")),
			"beta", "gamma", "delta");

		_testCriterion(
			RestrictionsFactoryUtil.or(
				RestrictionsFactoryUtil.eq("name", "alpha"),
				RestrictionsFactoryUtil.eq("name", "delta")),
			"alpha", "delta");
	}

	@Test
	public void testDynamicQueryWithAddOrderByComparator() {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		OrderFactoryUtil.addOrderByComparator(
			dynamicQuery,
			new OrderByComparator<DynamicQueryEntry>() {

				@Override
				public int compare(
					DynamicQueryEntry dynamicQueryEntry1,
					DynamicQueryEntry dynamicQueryEntry2) {

					return Long.compare(
						dynamicQueryEntry2.getAmount(),
						dynamicQueryEntry1.getAmount());
				}

				@Override
				public String getOrderBy() {
					return "amount DESC";
				}

			});

		_testDynamicQuery(dynamicQuery, "delta", "gamma", "beta", "alpha");
	}

	@Test
	public void testDynamicQueryWithAlias() {
		_testDynamicQueryWithAlias(null);
		_testDynamicQueryWithAlias(RandomTestUtil.randomString());
	}

	@Test
	public void testDynamicQueryWithInLargeCollectionTriggersDisjunction()
		throws Exception {

		DBManager dbManager = ReflectionTestUtil.getFieldValue(
			DBManagerUtil.class, "_dbManager");

		int dbInMaxParameters = RandomTestUtil.randomInt(10, 100);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					dbManager, "_databaseInMaxParameters", dbInMaxParameters)) {

			Assert.assertEquals(
				dbInMaxParameters, DBManagerUtil.getDBInMaxParameters());

			List<Long> ids = new ArrayList<>(dbInMaxParameters + 1);

			for (int i = 0; i < dbInMaxParameters; i++) {
				ids.add(i * -1L);
			}

			DynamicQueryEntry dynamicQueryEntry = _dynamicQueryEntries.get(0);

			ids.add(dynamicQueryEntry.getDynamicQueryEntryId());

			DynamicQuery dynamicQuery =
				_dynamicQueryEntryLocalService.dynamicQuery();

			dynamicQuery.add(
				RestrictionsFactoryUtil.in("dynamicQueryEntryId", ids));

			_testDynamicQuery(dynamicQuery, "alpha");
		}
	}

	@Test
	public void testDynamicQueryWithProjection() {
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.avg("amount"), 250.0);
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.count("dynamicQueryEntryId"),
			Long.valueOf(_dynamicQueryEntries.size()));
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.countDistinct("status"), 3L);
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.max("amount"), 400L);
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.min("amount"), 100L);
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.rowCount(),
			Long.valueOf(_dynamicQueryEntries.size()));
		_testDynamicQueryWithProjection(
			null, ProjectionFactoryUtil.sum("amount"), 1000L);
	}

	@Test
	public void testDynamicQueryWithProjectionGroupProperty() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(ProjectionFactoryUtil.count("dynamicQueryEntryId"));
		projectionList.add(ProjectionFactoryUtil.groupProperty("status"));

		_testDynamicQueryWithProjection(
			OrderFactoryUtil.asc("status"), projectionList,
			new Object[] {2L, 1}, new Object[] {1L, 2}, new Object[] {1L, 3});
	}

	@Test
	public void testDynamicQueryWithProjectionReusedProjectionInstance() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		Projection projection = ProjectionFactoryUtil.property("name");

		projectionList.add(projection, "a");
		projectionList.add(projection, "b");

		_testDynamicQueryWithProjection(
			OrderFactoryUtil.asc("dynamicQueryEntryId"), projectionList,
			new Object[] {"alpha", "alpha"}, new Object[] {"beta", "beta"},
			new Object[] {"gamma", "gamma"}, new Object[] {"delta", "delta"});
	}

	@Test
	public void testDynamicQueryWithProjectionSqlGroupProjection() {
		_testDynamicQueryWithProjection(
			OrderFactoryUtil.asc("status"),
			ProjectionFactoryUtil.sqlGroupProjection(
				"count(*) AS c, status AS s", "status", new String[] {"c", "s"},
				new Type[] {Type.LONG, Type.INTEGER}),
			new Object[] {2L, 1}, new Object[] {1L, 2}, new Object[] {1L, 3});
	}

	@Test
	public void testDynamicQueryWithProjectionSqlProjection() {
		_testDynamicQueryWithProjection(
			OrderFactoryUtil.asc("amount"),
			ProjectionFactoryUtil.sqlProjection(
				"name AS sqlName", new String[] {"sqlName"},
				new Type[] {Type.STRING}),
			"alpha", "beta", "gamma", "delta");
	}

	@Test
	public void testSetLimit() {
		_testSetLimit(-5, -3);
		_testSetLimit(2, 2);
		_testSetLimit(5, 2);
		_testSetLimit(0, 2, "alpha", "beta");
		_testSetLimit(1, 2, "beta");
		_testSetLimit(-5, 3, "alpha", "beta", "gamma");
		_testSetLimit(2, QueryUtil.ALL_POS, "gamma", "delta");
		_testSetLimit(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, "alpha", "beta", "gamma",
			"delta");
		_testSetLimit(QueryUtil.ALL_POS, -50);
	}

	private static DynamicQueryEntry _addDynamicQueryEntry(
			long amount, Date createDate, String description, Date modifiedDate,
			String name, int status)
		throws Exception {

		DynamicQueryEntry dynamicQueryEntry =
			_dynamicQueryEntryLocalService.createDynamicQueryEntry(
				RandomTestUtil.nextLong());

		dynamicQueryEntry.setCreateDate(createDate);
		dynamicQueryEntry.setModifiedDate(modifiedDate);
		dynamicQueryEntry.setAmount(amount);
		dynamicQueryEntry.setDescription(description);
		dynamicQueryEntry.setName(name);
		dynamicQueryEntry.setStatus(status);

		return _dynamicQueryEntryLocalService.addDynamicQueryEntry(
			dynamicQueryEntry);
	}

	private void _testAddOrder(Order order, String... expectedResults) {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(order);

		_testDynamicQuery(dynamicQuery, expectedResults);
	}

	private void _testCriterion(
		Criterion criterion, String... expectedResults) {

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.add(criterion);
		dynamicQuery.addOrder(OrderFactoryUtil.asc("dynamicQueryEntryId"));

		_testDynamicQuery(dynamicQuery, expectedResults);
	}

	private void _testDynamicQuery(
		DynamicQuery dynamicQuery, Object... expectedResults) {

		List<?> results = _dynamicQueryEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(
			results.toString(), expectedResults.length, results.size());

		for (int i = 0; i < expectedResults.length; i++) {
			Object result = results.get(i);

			if (result instanceof DynamicQueryEntry) {
				DynamicQueryEntry dynamicQueryEntry = (DynamicQueryEntry)result;

				result = dynamicQueryEntry.getName();
			}

			if (expectedResults[i] instanceof Object[]) {
				Assert.assertArrayEquals(
					results.toString(), (Object[])expectedResults[i],
					(Object[])result);
			}
			else {
				Assert.assertEquals(
					results.toString(), expectedResults[i], result);
			}
		}
	}

	private void _testDynamicQueryWithAlias(String alias) {
		Class<?> clazz = _dynamicQueryEntryLocalService.getClass();

		// Test 1

		DynamicQuery dynamicQuery = null;

		if (alias == null) {
			dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();

			dynamicQuery.add(RestrictionsFactoryUtil.eq("this.status", 1));
		}
		else {
			dynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());

			dynamicQuery.add(RestrictionsFactoryUtil.eq(alias + ".status", 1));
		}

		dynamicQuery.addOrder(OrderFactoryUtil.asc("amount"));

		_testDynamicQuery(dynamicQuery, "alpha", "gamma");

		// Test 2

		dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DynamicQueryEntry.class, "child", clazz.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("child.name", "alpha"));
		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("child.name"));

		DynamicQuery parentDynamicQuery = null;
		Property property = null;

		if (alias == null) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					"child.status", "this.status"));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					"this.status", "child.status"));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					"child.amount", "this.amount"));

			parentDynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();
			property = PropertyFactoryUtil.forName("name");
		}
		else {
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					"child.status", alias + ".status"));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					alias + ".status", "child.status"));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eqProperty(
					"child.amount", alias + ".amount"));

			parentDynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());
			property = PropertyFactoryUtil.forName(alias + ".name");
		}

		parentDynamicQuery.add(property.eq(dynamicQuery));

		_testDynamicQuery(parentDynamicQuery, "alpha");

		// Test 3

		if (alias == null) {
			dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();
		}
		else {
			dynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());
		}

		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				"exists (select 1 from DynamicQueryEntry where status = " +
					"this_.status and amount > this_.amount)"));

		_testDynamicQuery(dynamicQuery, "alpha");

		// Test 4

		if (alias == null) {
			dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();
		}
		else {
			dynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());
		}

		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				"exists (select 1 from DynamicQueryEntry where status = " +
					"this_.status and amount > ?)",
				250L, Type.LONG));
		dynamicQuery.addOrder(OrderFactoryUtil.asc("amount"));

		_testDynamicQuery(dynamicQuery, "alpha", "gamma", "delta");

		// Test 5

		if (alias == null) {
			dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();
		}
		else {
			dynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());
		}

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.sqlProjection(
				"(select count(*) from DynamicQueryEntry where status = " +
					"this_.status) AS statusCount",
				new String[] {"statusCount"}, new Type[] {Type.LONG}));
		dynamicQuery.addOrder(OrderFactoryUtil.asc("amount"));

		_testDynamicQuery(dynamicQuery, 2L, 1L, 2L, 1L);

		// Test 6

		if (alias == null) {
			dynamicQuery = _dynamicQueryEntryLocalService.dynamicQuery();
		}
		else {
			dynamicQuery = DynamicQueryFactoryUtil.forClass(
				DynamicQueryEntry.class, alias, clazz.getClassLoader());
		}

		dynamicQuery.add(
			RestrictionsFactoryUtil.sqlRestriction(
				"exists (select 1 from DynamicQueryEntry where status = " +
					"this_.status and amount > this_.amount)"));

		Assert.assertEquals(
			1L, _dynamicQueryEntryLocalService.dynamicQueryCount(dynamicQuery));
	}

	private void _testDynamicQueryWithProjection(
		Order order, Projection projection, Object... expectedResults) {

		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.setProjection(projection);

		if (order != null) {
			dynamicQuery.addOrder(order);
		}

		_testDynamicQuery(dynamicQuery, expectedResults);
	}

	private void _testSetLimit(int start, int end, String... expectedResults) {
		DynamicQuery dynamicQuery =
			_dynamicQueryEntryLocalService.dynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("dynamicQueryEntryId"));
		dynamicQuery.setLimit(start, end);

		_testDynamicQuery(dynamicQuery, expectedResults);
	}

	private static final List<DynamicQueryEntry> _dynamicQueryEntries =
		new ArrayList<>();

	@Inject
	private static DynamicQueryEntryLocalService _dynamicQueryEntryLocalService;

}