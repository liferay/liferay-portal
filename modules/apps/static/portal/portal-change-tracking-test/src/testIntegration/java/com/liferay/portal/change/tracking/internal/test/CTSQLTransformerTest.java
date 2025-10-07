/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.change.tracking.registry.CTModelRegistration;
import com.liferay.portal.change.tracking.registry.CTModelRegistry;
import com.liferay.portal.change.tracking.sql.CTSQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class CTSQLTransformerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_db = DBManagerUtil.getDB();

		CTModelRegistry.registerCTModel(
			new CTModelRegistration(
				MainTable.class, "MainTable", "mainTableId"));

		_createCTEntries(1, MainTable.class, 6L, null, null);
		_createCTEntries(2, MainTable.class, null, 1L, null);
		_createCTEntries(3, MainTable.class, null, 1L, null);
		_createCTEntries(4, MainTable.class, null, null, 4L);
		_createCTEntries(5, MainTable.class, 7L, null, 4L);
		_createCTEntries(6, MainTable.class, null, null, null);

		_db.runSQL(
			StringBundler.concat(
				"create table MainTable (mainTableId LONG not null, ",
				"ctCollectionId LONG not null, companyId LONG, groupId LONG, ",
				"name VARCHAR(20), primary key (mainTableId, ",
				"ctCollectionId));"));

		_db.runSQL("insert into MainTable values (1, 0, 2, 3, 'mt1 v1')");
		_db.runSQL("insert into MainTable values (2, 0, 2, 3, 'mt2 v1')");
		_db.runSQL("insert into MainTable values (3, 0, 2, 3, 'mt3 v1')");
		_db.runSQL("insert into MainTable values (4, 0, 2, 3, 'mt4 v1')");
		_db.runSQL("insert into MainTable values (5, 0, 2, 4, 'mt5 v1')");

		_db.runSQL(
			"insert into MainTable values (6, " + _getCTCollectionId(1) +
				" , 2, 3, 'mt6 add')");

		_db.runSQL(
			"insert into MainTable values (1, " + _getCTCollectionId(2) +
				" , 2, 3, 'mt1 modify')");

		_db.runSQL(
			"insert into MainTable values (1, " + _getCTCollectionId(3) +
				" , 2, 4, 'mt1 moved')");

		_db.runSQL(
			"insert into MainTable values (7, " + _getCTCollectionId(5) +
				" , 2, 3, 'mt7 add')");

		CTModelRegistry.registerCTModel(
			new CTModelRegistration(
				ReferenceTable.class, "ReferenceTable", "referenceTableId"));

		_createCTEntries(1, ReferenceTable.class, 6L, null, null);
		_createCTEntries(2, ReferenceTable.class, null, 1L, null);
		_createCTEntries(3, ReferenceTable.class, null, 1L, null);
		_createCTEntries(4, ReferenceTable.class, null, null, 5L);
		_createCTEntries(5, ReferenceTable.class, null, 1L, 4L);
		_createCTEntries(6, ReferenceTable.class, null, null, null);

		_db.runSQL(
			StringBundler.concat(
				"create table ReferenceTable (referenceTableId LONG not null, ",
				"ctCollectionId LONG not null, mainTableId LONG, name ",
				"VARCHAR(20), primary key (referenceTableId, ",
				"ctCollectionId))"));

		_db.runSQL("insert into ReferenceTable values (1, 0, 1, 'rt1 v1')");
		_db.runSQL("insert into ReferenceTable values (2, 0, 1, 'rt2 v1')");
		_db.runSQL("insert into ReferenceTable values (3, 0, 2, 'rt3 v1')");
		_db.runSQL("insert into ReferenceTable values (4, 0, 2, 'rt4 v1')");
		_db.runSQL("insert into ReferenceTable values (5, 0, 2, 'rt5 v1')");

		_db.runSQL(
			"insert into ReferenceTable values (6, " + _getCTCollectionId(1) +
				" , 1, 'rt6 add')");

		_db.runSQL(
			"insert into ReferenceTable values (1, " + _getCTCollectionId(2) +
				" , 1, 'rt1 modify')");

		_db.runSQL(
			"insert into ReferenceTable values (1, " + _getCTCollectionId(3) +
				" , 2, 'rt1 moved')");

		_db.runSQL(
			"insert into ReferenceTable values (1, " + _getCTCollectionId(5) +
				" , 2, 'rt1 modify2')");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		CTPreferences ctPreferences =
			_ctPreferencesLocalService.getCTPreferences(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		_ctPreferencesLocalService.deleteCTPreferences(ctPreferences);

		_db.runSQL("drop table MainTable");

		CTModelRegistry.unregisterCTModel("MainTable");

		_db.runSQL("drop table ReferenceTable");

		CTModelRegistry.unregisterCTModel("ReferenceTable");

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.service.impl." +
					"CTCollectionLocalServiceImpl",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.internal.search." +
					"CTSearchEventListener",
				LoggerTestUtil.WARN)) {

			for (CTCollection ctCollection : _ctCollections) {
				_ctCollectionLocalService.deleteCTCollection(ctCollection);
			}
		}
	}

	@Test
	public void testJoinCount() throws Exception {
		_assertQuery(
			"join_count_in.sql", "join_count_out.sql", 0,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", _getCTCollectionId(6),
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testJoinCountAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt6 add"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testJoinCountModify() throws Exception {
		long ctCollectionId = _getCTCollectionId(2);

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 modify"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testJoinCountMoved() throws Exception {
		long ctCollectionId = _getCTCollectionId(3);

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 moved"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testJoinCountRemove() throws Exception {
		long ctCollectionId = _getCTCollectionId(4);

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"join_count_in.sql", "join_count_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt5 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));
	}

	@Test
	public void testJoinSelect() throws Exception {
		_assertQuery(
			"join_select_in.sql", "join_select_out.sql", 0,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql",
			_getCTCollectionId(6), ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});
	}

	@Test
	public void testJoinSelectAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt6 add"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});
	}

	@Test
	public void testJoinSelectModify() throws Exception {
		long ctCollectionId = _getCTCollectionId(2);

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"));

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 modify"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 modify", rs.getString("name"));
			});
	}

	@Test
	public void testJoinSelectMoved() throws Exception {
		long ctCollectionId = _getCTCollectionId(3);

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"));

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 moved"),
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			});
	}

	@Test
	public void testJoinSelectRemove() throws Exception {
		long ctCollectionId = _getCTCollectionId(4);

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"join_select_in.sql", "join_select_out_ct.sql", ctCollectionId,
			ps -> ps.setString(1, "rt5 v1"));
	}

	@Test
	public void testLeftJoin() throws Exception {
		_assertQuery(
			"left_join_in.sql", "left_join_out.sql", 0,
			ps -> {
			},
			rs -> Assert.assertEquals(3, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(4, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(5, rs.getLong("mainTableId")));
	}

	@Test
	public void testLeftJoinAdd() throws Exception {
		_assertQuery(
			"left_join_in.sql", "left_join_out_ct.sql", _getCTCollectionId(1),
			ps -> {
			},
			rs -> Assert.assertEquals(3, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(4, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(5, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(6, rs.getLong("mainTableId")));
	}

	@Test
	public void testLeftJoinModify() throws Exception {
		_assertQuery(
			"left_join_in.sql", "left_join_out_ct.sql", _getCTCollectionId(2),
			ps -> {
			},
			rs -> Assert.assertEquals(3, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(4, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(5, rs.getLong("mainTableId")));
	}

	@Test
	public void testLeftJoinRemove() throws Exception {
		_assertQuery(
			"left_join_in.sql", "left_join_out_ct.sql", _getCTCollectionId(4),
			ps -> {
			},
			rs -> Assert.assertEquals(3, rs.getLong("mainTableId")),
			rs -> Assert.assertEquals(5, rs.getLong("mainTableId")));
	}

	@Test
	public void testSelfJoin() throws Exception {
		_assertQuery(
			"self_join_in.sql", "self_join_out.sql", 0,
			ps -> {
			},
			rs -> {
				Assert.assertEquals(5, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
			});
	}

	@Test
	public void testSelfJoinAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);

		_assertQuery(
			"self_join_in.sql", "self_join_out_ct.sql", ctCollectionId,
			ps -> {
			},
			rs -> {
				Assert.assertEquals(6, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
			});
	}

	@Test
	public void testSelfJoinModify() throws Exception {
		_assertQuery(
			"self_join_in.sql", "self_join_out_ct.sql", _getCTCollectionId(2),
			ps -> {
			},
			rs -> {
				Assert.assertEquals(5, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
			});
	}

	@Test
	public void testSelfJoinRemove() throws Exception {
		_assertQuery(
			"self_join_in.sql", "self_join_out_ct.sql", _getCTCollectionId(4),
			ps -> {
			},
			rs -> {
				Assert.assertEquals(5, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
			});
	}

	@Test
	public void testSimpleCount() throws Exception {
		_assertQuery(
			"simple_count_in.sql", "simple_count_out.sql", 0,
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)));

		_assertQuery(
			"simple_count_in.sql", "simple_count_out_ct.sql",
			_getCTCollectionId(6),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testSimpleCountAdd() throws Exception {
		_assertQuery(
			"simple_count_in.sql", "simple_count_out_ct.sql",
			_getCTCollectionId(1),
			ps -> {
			},
			rs -> Assert.assertEquals(6, rs.getLong(1)));
	}

	@Test
	public void testSimpleCountModify() throws Exception {
		_assertQuery(
			"simple_count_in.sql", "simple_count_out_ct.sql",
			_getCTCollectionId(2),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testSimpleCountMoved() throws Exception {
		_assertQuery(
			"simple_count_in.sql", "simple_count_out_ct.sql",
			_getCTCollectionId(3),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testSimpleCountRemove() throws Exception {
		_assertQuery(
			"simple_count_in.sql", "simple_count_out_ct.sql",
			_getCTCollectionId(4),
			ps -> {
			},
			rs -> Assert.assertEquals(4, rs.getLong(1)));
	}

	@Test
	public void testSimpleSelect() throws Exception {
		long groupId = 3;

		_assertQuery(
			"simple_select_in.sql", "simple_select_out.sql", 0,
			ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(3, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt3 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(4, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt4 v1", rs.getString("name"));
			});

		_assertQuery(
			"simple_select_in.sql", "simple_select_out_ct.sql",
			_getCTCollectionId(6), ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(3, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt3 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(4, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt4 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSimpleSelectAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);
		long groupId = 3;

		_assertQuery(
			"simple_select_in.sql", "simple_select_out_ct.sql", ctCollectionId,
			ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(3, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt3 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(4, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt4 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(6, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt6 add", rs.getString("name"));
			});
	}

	@Test
	public void testSimpleSelectModify() throws Exception {
		long ctCollectionId = _getCTCollectionId(2);
		long groupId = 3;

		_assertQuery(
			"simple_select_in.sql", "simple_select_out_ct.sql", ctCollectionId,
			ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 modify", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(3, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt3 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(4, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt4 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSimpleSelectMoved() throws Exception {
		long ctCollectionId = _getCTCollectionId(3);
		long groupId = 4;

		_assertQuery(
			"simple_select_in.sql", "simple_select_out_ct.sql", ctCollectionId,
			ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 moved", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(5, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt5 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSimpleSelectRemove() throws Exception {
		long groupId = 3;

		_assertQuery(
			"simple_select_in.sql", "simple_select_out_ct.sql",
			_getCTCollectionId(4), ps -> ps.setLong(1, groupId),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			},
			rs -> {
				Assert.assertEquals(3, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(groupId, rs.getLong("groupId"));
				Assert.assertEquals("mt3 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSubqueryCount() throws Exception {
		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out.sql", 0,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			_getCTCollectionId(6), ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testSubqueryCountAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt6 add"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testSubqueryCountModify() throws Exception {
		long ctCollectionId = _getCTCollectionId(2);

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 modify"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testSubqueryCountMoved() throws Exception {
		long ctCollectionId = _getCTCollectionId(3);

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 moved"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));
	}

	@Test
	public void testSubqueryCountRemove() throws Exception {
		long ctCollectionId = _getCTCollectionId(4);

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> Assert.assertEquals(1, rs.getLong(1)));

		_assertQuery(
			"subquery_count_in.sql", "subquery_count_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt5 v1"),
			rs -> Assert.assertEquals(0, rs.getLong(1)));
	}

	@Test
	public void testSubquerySelect() throws Exception {
		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out.sql", 0,
			ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			_getCTCollectionId(6), ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSubquerySelectAdd() throws Exception {
		long ctCollectionId = _getCTCollectionId(1);

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt6 add"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSubquerySelectModify() throws Exception {
		long ctCollectionId = _getCTCollectionId(2);

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"));

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 modify"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(
					ctCollectionId, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 modify", rs.getString("name"));
			});
	}

	@Test
	public void testSubquerySelectMoved() throws Exception {
		long ctCollectionId = _getCTCollectionId(3);

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"));

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 moved"),
			rs -> {
				Assert.assertEquals(2, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt2 v1", rs.getString("name"));
			});
	}

	@Test
	public void testSubquerySelectRemove() throws Exception {
		long ctCollectionId = _getCTCollectionId(4);

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt1 v1"),
			rs -> {
				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals(0, rs.getLong("ctCollectionId"));
				Assert.assertEquals(3, rs.getLong("groupId"));
				Assert.assertEquals("mt1 v1", rs.getString("name"));
			});

		_assertQuery(
			"subquery_select_in.sql", "subquery_select_out_ct.sql",
			ctCollectionId, ps -> ps.setString(1, "rt5 v1"));
	}

	@Test
	public void testUnionCount() throws Exception {
		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out.sql", 0,
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)),
			rs -> Assert.assertEquals(5, rs.getLong(1)));

		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out_ct.sql",
			_getCTCollectionId(6),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)),
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testUnionCountAdd() throws Exception {
		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out_ct.sql",
			_getCTCollectionId(1),
			ps -> {
			},
			rs -> Assert.assertEquals(6, rs.getLong(1)),
			rs -> Assert.assertEquals(6, rs.getLong(1)));
	}

	@Test
	public void testUnionCountModify() throws Exception {
		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out_ct.sql",
			_getCTCollectionId(2),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)),
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testUnionCountMoved() throws Exception {
		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out_ct.sql",
			_getCTCollectionId(3),
			ps -> {
			},
			rs -> Assert.assertEquals(5, rs.getLong(1)),
			rs -> Assert.assertEquals(5, rs.getLong(1)));
	}

	@Test
	public void testUnionCountRemove() throws Exception {
		_assertQuery(
			"union_select_count_in.sql", "union_select_count_out_ct.sql",
			_getCTCollectionId(4),
			ps -> {
			},
			rs -> Assert.assertEquals(4, rs.getLong(1)),
			rs -> Assert.assertEquals(4, rs.getLong(1)));
	}

	@Test
	public void testUpdateAndDelete() throws Exception {
		long ctCollectionId7 = _createCTEntries(
			7, MainTable.class, null, null, null);

		long ctCollectionId8 = _createCTEntries(
			8, MainTable.class, null, null, null);

		_db.runSQL(
			"insert into MainTable values (1, " + ctCollectionId7 +
				" , 2, 3, 'temp')");

		_assertQuery(
			"select * from MainTable where mainTableId = 1 and " +
				"ctCollectionId = " + ctCollectionId7,
			rs -> {
				Assert.assertTrue(rs.next());

				Assert.assertEquals("temp", rs.getString("name"));

				Assert.assertFalse(rs.next());
			});

		_assertUpdate(
			"update_in.sql", "update_out.sql", ctCollectionId7,
			ps -> {
				ps.setLong(1, ctCollectionId8);
				ps.setLong(2, 1);
			});

		_assertQuery(
			"select * from MainTable where mainTableId = 1 and " +
				"ctCollectionId = " + ctCollectionId7,
			rs -> Assert.assertFalse(rs.next()));

		_assertQuery(
			"select * from MainTable where ctCollectionId = " + ctCollectionId8,
			rs -> {
				Assert.assertTrue(rs.next());

				Assert.assertEquals(1, rs.getLong("mainTableId"));
				Assert.assertEquals("temp", rs.getString("name"));

				Assert.assertFalse(rs.next());
			});

		_assertUpdate(
			"delete_in.sql", "delete_out.sql", ctCollectionId8,
			ps -> ps.setLong(1, 1));

		_assertQuery(
			"select * from MainTable where mainTableId = 1 and " +
				"ctCollectionId = " + ctCollectionId8,
			rs -> Assert.assertFalse(rs.next()));
	}

	private static long _createCTEntries(
			int ctCollectionIndex, Class<?> modelClass, Long addedPK,
			Long modifiedPK, Long removedPK)
		throws Exception {

		CTCollection ctCollection = null;

		if (ctCollectionIndex <= _ctCollections.size()) {
			ctCollection = _ctCollections.get(ctCollectionIndex - 1);
		}

		if (ctCollection == null) {
			ctCollection = _ctCollectionLocalService.addCTCollection(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 0,
				CTSQLTransformerTest.class.getName(), null);

			_ctCollections.add(ctCollection);
		}

		if (addedPK != null) {
			_ctEntryLocalService.addCTEntry(
				null, ctCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(modelClass),
				_getCTModelProxy(addedPK), TestPropsValues.getUserId(),
				CTConstants.CT_CHANGE_TYPE_ADDITION);
		}

		if (modifiedPK != null) {
			_ctEntryLocalService.addCTEntry(
				null, ctCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(modelClass),
				_getCTModelProxy(modifiedPK), TestPropsValues.getUserId(),
				CTConstants.CT_CHANGE_TYPE_MODIFICATION);
		}

		if (removedPK != null) {
			_ctEntryLocalService.addCTEntry(
				null, ctCollection.getCtCollectionId(),
				_classNameLocalService.getClassNameId(modelClass),
				_getCTModelProxy(removedPK), TestPropsValues.getUserId(),
				CTConstants.CT_CHANGE_TYPE_DELETION);
		}

		return ctCollection.getCtCollectionId();
	}

	private static long _getCTCollectionId(int ctCollectionIndex) {
		if (ctCollectionIndex == 0) {
			return 0;
		}

		CTCollection ctCollection = _ctCollections.get(ctCollectionIndex - 1);

		return ctCollection.getCtCollectionId();
	}

	private static CTModel<?> _getCTModelProxy(long primaryKey) {
		return (CTModel<?>)ProxyUtil.newProxyInstance(
			CTSQLTransformer.class.getClassLoader(),
			new Class<?>[] {CTModel.class},
			(proxy, method, args) -> {
				String methodName = method.getName();

				if (methodName.equals("getMvccVersion")) {
					return 0L;
				}

				if (methodName.equals("getPrimaryKey")) {
					return primaryKey;
				}

				throw new UnsupportedOperationException(method.toString());
			});
	}

	@SafeVarargs
	private final void _assertQuery(
			String inputSQLFileName, String expectedOutputSQLFileName,
			long ctCollectionId,
			UnsafeConsumer<PreparedStatement, Exception>
				preparedStatementUnsafeConsumer,
			UnsafeConsumer<ResultSet, Exception>... resultSetUnsafeConsumers)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.getCTPreferences(companyId, userId);

		ctPreferences.setCtCollectionId(ctCollectionId);

		_ctPreferencesLocalService.updateCTPreferences(ctPreferences);

		long originalUserId = PrincipalThreadLocal.getUserId();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					companyId, ctCollectionId);
			Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_getSQL(
					inputSQLFileName, expectedOutputSQLFileName,
					ctCollectionId))) {

			PrincipalThreadLocal.setName(userId);

			preparedStatementUnsafeConsumer.accept(preparedStatement);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				for (UnsafeConsumer<ResultSet, Exception> unsafeConsumer :
						resultSetUnsafeConsumers) {

					Assert.assertTrue(resultSet.next());

					unsafeConsumer.accept(resultSet);
				}

				Assert.assertFalse(resultSet.next());
			}
		}
		finally {
			PrincipalThreadLocal.setName(originalUserId);
		}
	}

	private void _assertQuery(
			String sql, UnsafeConsumer<ResultSet, Exception> unsafeConsumer)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			unsafeConsumer.accept(resultSet);
		}
	}

	private void _assertUpdate(
			String inputSQLFileName, String expectedOutputSQLFileName,
			long ctCollectionId,
			UnsafeConsumer<PreparedStatement, Exception>
				preparedStatementUnsafeConsumer)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		long userId = TestPropsValues.getUserId();

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.getCTPreferences(companyId, userId);

		ctPreferences.setCtCollectionId(ctCollectionId);

		_ctPreferencesLocalService.updateCTPreferences(ctPreferences);

		long originalUserId = PrincipalThreadLocal.getUserId();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					companyId, ctCollectionId);
			Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_getSQL(
					inputSQLFileName, expectedOutputSQLFileName,
					ctCollectionId))) {

			PrincipalThreadLocal.setName(userId);

			preparedStatementUnsafeConsumer.accept(preparedStatement);

			Assert.assertEquals(1, preparedStatement.executeUpdate());
		}
		finally {
			PrincipalThreadLocal.setName(originalUserId);
		}
	}

	private String _getSQL(
			String inputSQLFileName, String expectedOutputSQLFileName,
			long ctCollectionId)
		throws Exception {

		String inputSQL = StreamUtil.toString(
			CTSQLTransformerTest.class.getResourceAsStream(
				"dependencies/" + inputSQLFileName));

		String expectedOutputSQL = _normalizeSQL(
			StreamUtil.toString(
				CTSQLTransformerTest.class.getResourceAsStream(
					"dependencies/" + expectedOutputSQLFileName)));

		expectedOutputSQL = StringUtil.replace(
			expectedOutputSQL, "[$", "$]",
			HashMapBuilder.put(
				"CT_COLLECTION_ID", String.valueOf(ctCollectionId)
			).put(
				"MAIN_TABLE_CLASS_NAME_ID",
				String.valueOf(
					_classNameLocalService.getClassNameId(MainTable.class))
			).put(
				"REFERENCE_TABLE_CLASS_NAME_ID",
				String.valueOf(
					_classNameLocalService.getClassNameId(ReferenceTable.class))
			).build());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.change.tracking.internal." +
					"CTSQLTransformerImpl",
				LoggerTestUtil.WARN)) {

			String newSQL = _ctSQLTransformer.transform(inputSQL);

			Assert.assertEquals(expectedOutputSQL, newSQL);

			return newSQL;
		}
	}

	private String _normalizeSQL(String sql) {
		return StringUtil.replace(
			sql.trim(), new String[] {"\n", "    ", "   ", "  ", "( ", " )"},
			new String[] {" ", " ", " ", " ", "(", ")"});
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	private static final List<CTCollection> _ctCollections = new ArrayList<>();

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@Inject
	private static CTPreferencesLocalService _ctPreferencesLocalService;

	@Inject
	private static CTSQLTransformer _ctSQLTransformer;

	private static DB _db;

	private static class MainTable {
	}

	private static class ReferenceTable {
	}

}