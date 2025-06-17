/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.expression.NullExpression;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryEntryTable;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntry;
import com.liferay.portal.tools.service.builder.test.model.DSLQueryStatusEntryTable;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DSLQueryStatusEntryPersistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DSLQueryEntryPersistenceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_testDSLQueryEntry1 = _addDSLQueryEntry(1L, _TEST_NAME_1);
		_testDSLQueryEntry2 = _addDSLQueryEntry(2L, _TEST_NAME_2);
		_testDSLQueryEntry3 = _addDSLQueryEntry(3L, _TEST_NAME_3);

		_currentTime = System.currentTimeMillis();

		_addDSLQueryStatusEntry(
			_testDSLQueryEntry1, 1L, _TEST_STATUS_1, _currentTime - 1);
		_addDSLQueryStatusEntry(
			_testDSLQueryEntry1, 2L, _TEST_STATUS_2, _currentTime);
		_addDSLQueryStatusEntry(
			_testDSLQueryEntry2, 3L, _TEST_STATUS_1, _currentTime - 1);
	}

	@Test
	public void testDSLQueryCount() {
		Assert.assertEquals(
			3,
			_dslQueryStatusEntryPersistence.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				)));

		Assert.assertEquals(
			2,
			_dslQueryStatusEntryPersistence.dslQueryCount(
				DSLQueryFactoryUtil.countDistinct(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				)));

		Assert.assertEquals(
			3,
			_dslQueryStatusEntryPersistence.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).where(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId.in(
						DSLQueryFactoryUtil.select(
							DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId
						).from(
							DSLQueryStatusEntryTable.INSTANCE
						).where(
							DSLQueryStatusEntryTable.INSTANCE.statusDate.lt(
								new Date(_currentTime))
						))
				)));

		Assert.assertEquals(
			2,
			_dslQueryStatusEntryPersistence.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).where(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId.in(
						DSLQueryFactoryUtil.select(
							DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId
						).from(
							DSLQueryStatusEntryTable.INSTANCE
						).where(
							DSLQueryStatusEntryTable.INSTANCE.statusDate.gte(
								new Date(_currentTime))
						))
				)));
	}

	@Test
	public void testDSLQueryGroupBy() {
		Assert.assertEquals(
			Arrays.asList(_TEST_STATUS_1),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryStatusEntryTable.INSTANCE.status
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).groupBy(
					DSLQueryStatusEntryTable.INSTANCE.status
				).having(
					DSLFunctionFactoryUtil.countDistinct(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
					).gte(
						2L
					)
				)));

		Assert.assertEquals(
			Arrays.asList(_TEST_STATUS_2),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryStatusEntryTable.INSTANCE.status
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).groupBy(
					DSLQueryStatusEntryTable.INSTANCE.status
				).having(
					DSLFunctionFactoryUtil.countDistinct(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
					).lt(
						2L
					)
				)));
	}

	@Test
	public void testDSLQueryJoin() {

		// Test 1, left join

		Assert.assertEquals(
			Arrays.asList(1L, 1L, 2L, 3L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId
				).from(
					DSLQueryEntryTable.INSTANCE
				).leftJoinOn(
					DSLQueryStatusEntryTable.INSTANCE,
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId)
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				)));

		// Test 2, inner join on dslQueryEntryId

		Assert.assertEquals(
			Arrays.asList(1L, 1L, 2L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId
				).from(
					DSLQueryEntryTable.INSTANCE
				).innerJoinON(
					DSLQueryStatusEntryTable.INSTANCE,
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId)
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				)));

		// Test 3, inner join on dslQueryStatusEntryId

		Assert.assertEquals(
			Arrays.asList(1L, 2L, 3L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId
				).from(
					DSLQueryEntryTable.INSTANCE
				).innerJoinON(
					DSLQueryStatusEntryTable.INSTANCE,
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId)
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				)));

		// Test 4, self join

		DSLQueryStatusEntryTable aliasDSLQueryStatusEntryTable =
			DSLQueryStatusEntryTable.INSTANCE.as(
				"tempDSLQueryStatusEntryTable");

		Assert.assertEquals(
			Arrays.asList(2L, 3L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).leftJoinOn(
					aliasDSLQueryStatusEntryTable,
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.gt(
						aliasDSLQueryStatusEntryTable.dslQueryEntryId)
				).where(
					aliasDSLQueryStatusEntryTable.dslQueryEntryId.isNotNull()
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
	}

	@Test
	public void testDSLQueryLimit() {
		Assert.assertEquals(
			Arrays.asList(
				_testDSLQueryEntry1, _testDSLQueryEntry2, _testDSLQueryEntry3),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE
				).from(
					DSLQueryEntryTable.INSTANCE
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				)));

		Assert.assertEquals(
			Arrays.asList(_testDSLQueryEntry1),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE
				).from(
					DSLQueryEntryTable.INSTANCE
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				).limit(
					0, 1
				)));

		Assert.assertEquals(
			Arrays.asList(_testDSLQueryEntry1, _testDSLQueryEntry2),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryEntryTable.INSTANCE
				).from(
					DSLQueryEntryTable.INSTANCE
				).orderBy(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
				).limit(
					0, 2
				)));
	}

	@Test
	public void testDSLQueryOrderBy() {
		Assert.assertEquals(
			Arrays.asList(1L, 3L, 2L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.status.ascending(),
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));

		Assert.assertEquals(
			Arrays.asList(3L, 1L, 2L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.status.ascending(),
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						descending()
				)));

		Assert.assertEquals(
			Arrays.asList(2L, 3L, 1L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.status.descending(),
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						descending()
				)));
	}

	@Test
	public void testDSLQueryWithCaseWhenThen() {
		Assert.assertEquals(
			Arrays.asList(0.5F, 1.0F, 1.5F),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.caseWhenThen(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
							eq(new Scalar<>(0L)),
						(Expression<Float>)
							(Expression<?>)NullExpression.INSTANCE
					).elseEnd(
						DSLFunctionFactoryUtil.floatDivide(
							DSLQueryStatusEntryTable.INSTANCE.
								dslQueryStatusEntryId,
							new Scalar<>(2L))
					).as(
						"alias"
					)
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
	}

	@Test
	public void testDSLQueryWithDivide() {
		Assert.assertEquals(
			Arrays.asList(0L, 1L, 1L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.divide(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId,
						new Scalar<>(2L)
					).as(
						"alias"
					)
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
	}

	@Test
	public void testDSLQueryWithDSLFunction() {
		Assert.assertEquals(
			Arrays.asList(0L, 1L, 2L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.subtract(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId,
						new Scalar<>(1L)
					).as(
						"alias"
					)
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
		Assert.assertEquals(
			Arrays.asList(2L, 1L, 0L),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.subtract(
						new Scalar<>(3L),
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId
					).as(
						"alias"
					)
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
	}

	@Test
	public void testDSLQueryWithFloatDivide() {
		Assert.assertEquals(
			Arrays.asList(0.5F, 1.0F, 1.5F),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.floatDivide(
						DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId,
						new Scalar<>(2L)
					).as(
						"alias"
					)
				).from(
					DSLQueryStatusEntryTable.INSTANCE
				).orderBy(
					DSLQueryStatusEntryTable.INSTANCE.dslQueryStatusEntryId.
						ascending()
				)));
	}

	@Test
	public void testDSLQueryWithFloatDivideOnColumn() {
		DSLQueryEntry testDSLQueryEntry = _addDSLQueryEntry(
			Long.MAX_VALUE, RandomTestUtil.randomString());

		_dslQueryEntries.add(testDSLQueryEntry);

		Assert.assertEquals(
			Arrays.asList(1.0F),
			_dslQueryEntryPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					DSLFunctionFactoryUtil.floatDivide(
						(Expression<Number>)
							(Column<?, ?>)
								DSLQueryEntryTable.INSTANCE.dslQueryEntryId,
						(Expression<Number>)
							(Column<?, ?>)
								DSLQueryEntryTable.INSTANCE.dslQueryEntryId
					).as(
						"alias"
					)
				).from(
					DSLQueryEntryTable.INSTANCE
				).where(
					DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
						Long.MAX_VALUE)
				)));
	}

	@Test
	public void testDSLQueryWithUpdate() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLQueryEntryTable.INSTANCE.name
		).from(
			DSLQueryEntryTable.INSTANCE
		).where(
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.neq(
				_testDSLQueryEntry1.getDslQueryEntryId())
		).orderBy(
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.ascending()
		);

		Assert.assertEquals(
			Arrays.asList(_TEST_NAME_2, _TEST_NAME_3),
			_dslQueryEntryPersistence.dslQuery(dslQuery));

		_dslQueryEntryPersistence.remove(_testDSLQueryEntry2);

		Assert.assertEquals(
			Arrays.asList(_TEST_NAME_3),
			_dslQueryEntryPersistence.dslQuery(dslQuery));

		_testDSLQueryEntry3.setName("updated.name");

		_testDSLQueryEntry3 = _dslQueryEntryPersistence.updateImpl(
			_testDSLQueryEntry3);

		Assert.assertEquals(
			Arrays.asList("updated.name"),
			_dslQueryEntryPersistence.dslQuery(dslQuery));

		_addDSLQueryEntry(4L, "test.name.4");

		Assert.assertEquals(
			Arrays.asList("updated.name", "test.name.4"),
			_dslQueryEntryPersistence.dslQuery(dslQuery));
	}

	@Test
	public void testDSLQueryWithUpdateMultipleTables() {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLQueryEntryTable.INSTANCE
		).from(
			DSLQueryEntryTable.INSTANCE
		).leftJoinOn(
			DSLQueryStatusEntryTable.INSTANCE,
			DSLQueryEntryTable.INSTANCE.dslQueryEntryId.eq(
				DSLQueryStatusEntryTable.INSTANCE.dslQueryEntryId)
		).where(
			DSLQueryStatusEntryTable.INSTANCE.statusDate.gte(
				new Date(_currentTime))
		);

		Assert.assertEquals(
			Arrays.asList(_testDSLQueryEntry1),
			_dslQueryEntryPersistence.dslQuery(dslQuery));

		_dslQueryEntryPersistence.remove(_testDSLQueryEntry1);

		Assert.assertEquals(
			Collections.emptyList(),
			_dslQueryEntryPersistence.dslQuery(dslQuery));

		_addDSLQueryStatusEntry(
			_testDSLQueryEntry2, 4L, _TEST_STATUS_2, _currentTime);

		Assert.assertEquals(
			Arrays.asList(_testDSLQueryEntry2),
			_dslQueryEntryPersistence.dslQuery(dslQuery));
	}

	private DSLQueryEntry _addDSLQueryEntry(long id, String name) {
		DSLQueryEntry dslQueryEntry = _dslQueryEntryPersistence.create(id);

		dslQueryEntry.setName(name);

		dslQueryEntry = _dslQueryEntryPersistence.updateImpl(dslQueryEntry);

		_dslQueryEntries.add(dslQueryEntry);

		return dslQueryEntry;
	}

	private DSLQueryStatusEntry _addDSLQueryStatusEntry(
		DSLQueryEntry dslQueryEntry, long id, String status, long time) {

		DSLQueryStatusEntry dslQueryStatusEntry =
			_dslQueryStatusEntryPersistence.create(id);

		dslQueryStatusEntry.setDslQueryEntryId(
			dslQueryEntry.getDslQueryEntryId());
		dslQueryStatusEntry.setStatus(status);
		dslQueryStatusEntry.setStatusDate(new Date(time));

		dslQueryStatusEntry = _dslQueryStatusEntryPersistence.updateImpl(
			dslQueryStatusEntry);

		_dslQueryStatusEntries.add(dslQueryStatusEntry);

		return dslQueryStatusEntry;
	}

	private static final String _TEST_NAME_1 = "test.name.1";

	private static final String _TEST_NAME_2 = "test.name.2";

	private static final String _TEST_NAME_3 = "test.name.3";

	private static final String _TEST_STATUS_1 = "test.status.1";

	private static final String _TEST_STATUS_2 = "test.status.2";

	private long _currentTime;

	@DeleteAfterTestRun
	private final List<DSLQueryEntry> _dslQueryEntries = new ArrayList<>();

	@Inject
	private DSLQueryEntryPersistence _dslQueryEntryPersistence;

	@DeleteAfterTestRun
	private final List<DSLQueryStatusEntry> _dslQueryStatusEntries =
		new ArrayList<>();

	@Inject
	private DSLQueryStatusEntryPersistence _dslQueryStatusEntryPersistence;

	private DSLQueryEntry _testDSLQueryEntry1;
	private DSLQueryEntry _testDSLQueryEntry2;
	private DSLQueryEntry _testDSLQueryEntry3;

}