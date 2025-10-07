/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.change.tracking.registry.CTModelRegistration;
import com.liferay.portal.change.tracking.registry.CTModelRegistry;
import com.liferay.portal.change.tracking.sql.CTSQLTransformer;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class CTSQLTransformerPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		CTModelRegistry.registerCTModel(
			new CTModelRegistration(
				MainTable.class, "MainTable", "mainTableId"));
		CTModelRegistry.registerCTModel(
			new CTModelRegistration(
				ReferenceTable.class, "ReferenceTable", "referenceTableId"));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CTModelRegistry.unregisterCTModel("MainTable");
		CTModelRegistry.unregisterCTModel("ReferenceTable");
	}

	@Test
	public void testJoinCount() throws Exception {
		_assertTransform("join_count_in.sql", 0, 20);
		_assertTransform("join_count_in.sql", 1, 20);
	}

	@Test
	public void testJoinSelect() throws Exception {
		_assertTransform("join_select_in.sql", 0, 20);
		_assertTransform("join_select_in.sql", 1, 20);
	}

	@Test
	public void testLeftJoin() throws Exception {
		_assertTransform("left_join_in.sql", 0, 20);
		_assertTransform("left_join_in.sql", 1, 20);
	}

	@Test
	public void testSelfJoin() throws Exception {
		_assertTransform("self_join_in.sql", 0, 20);
		_assertTransform("self_join_in.sql", 1, 20);
	}

	@Test
	public void testSimpleCount() throws Exception {
		_assertTransform("simple_count_in.sql", 0, 20);
		_assertTransform("simple_count_in.sql", 1, 20);
	}

	@Test
	public void testSimpleSelect() throws Exception {
		_assertTransform("simple_select_in.sql", 0, 20);
		_assertTransform("simple_select_in.sql", 1, 20);
	}

	@Test
	public void testSubqueryCount() throws Exception {
		_assertTransform("subquery_count_in.sql", 0, 20);
		_assertTransform("subquery_count_in.sql", 1, 20);
	}

	@Test
	public void testSubquerySelect() throws Exception {
		_assertTransform("subquery_select_in.sql", 0, 20);
		_assertTransform("subquery_select_in.sql", 1, 20);
	}

	@Test
	public void testUnionCount() throws Exception {
		_assertTransform("union_select_count_in.sql", 0, 20);
		_assertTransform("union_select_count_in.sql", 1, 20);
	}

	@Test
	public void testUpdateAndDelete() throws Exception {
		_assertTransform("delete_in.sql", 0, 20);
		_assertTransform("delete_in.sql", 1, 20);
		_assertTransform("update_in.sql", 0, 20);
		_assertTransform("update_in.sql", 1, 20);
	}

	private void _assertTransform(
			String inputSQLFileName, long ctCollectionId, int maxTime)
		throws Exception {

		String inputSQL = StreamUtil.toString(
			CTSQLTransformerPerformanceTest.class.getResourceAsStream(
				"dependencies/" + inputSQLFileName));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.change.tracking.internal." +
					"CTSQLTransformerImpl",
				LoggerTestUtil.WARN);
			PerformanceTimer performanceTimer = new PerformanceTimer(maxTime);
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctSQLTransformer.transform(inputSQL);
		}
	}

	@Inject
	private CTSQLTransformer _ctSQLTransformer;

	private static class MainTable {
	}

	private static class ReferenceTable {
	}

}