/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.db.DBManagerImpl;
import com.liferay.portal.dao.db.MySQLDB;
import com.liferay.portal.dao.orm.hibernate.SQLQueryImpl;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.Dialect;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionWrapper;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceWrapper;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.xml.SAXReaderImpl;
import com.liferay.util.dao.orm.CustomSQL;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class GroupFinderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		DBManagerImpl dbManagerImpl = new DBManagerImpl();

		dbManagerImpl.setDB(new MySQLDB(0, 0));

		DBManagerUtil.setDBManager(dbManagerImpl);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public long getClassNameId(Class<?> clazz) {
					return _classNameIds.computeIfAbsent(
						clazz, key -> _counter.incrementAndGet());
				}

				private final Map<Class<?>, Long> _classNameIds =
					new HashMap<>();
				private final AtomicLong _counter = new AtomicLong();

			});

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CustomSQL.class.getName(), LoggerTestUtil.OFF)) {

			Class.forName(
				CustomSQLUtil.class.getName(), true,
				CustomSQLUtil.class.getClassLoader());
		}

		SQLQuery sqlQuery = new SQLQueryImpl(null, true) {

			@Override
			public SQLQuery addScalar(String columnAlias, Type type) {
				return this;
			}

			@Override
			public List<?> list(boolean unmodifiable) {
				return Collections.emptyList();
			}

			@Override
			public Query setLong(int pos, long value) {
				return this;
			}

			@Override
			public Query setString(int pos, String value) {
				return this;
			}

		};

		Session session = new SessionWrapper(null) {

			@Override
			public SQLQuery createSynchronizedSQLQuery(DSLQuery dslQuery) {
				_capturedSQL = _normalizeSQL(dslQuery.toString());

				return sqlQuery;
			}

			@Override
			public SQLQuery createSynchronizedSQLQuery(String queryString) {
				_capturedSQL = _normalizeSQL(queryString);

				return sqlQuery;
			}

		};

		_groupFinderImpl = new GroupFinderImpl() {

			@Override
			public void closeSession(Session session) {
			}

			@Override
			public Dialect getDialect() {
				return null;
			}

			@Override
			public Session openSession() throws ORMException {
				return session;
			}

		};
	}

	@Test
	public void testCountByC_C_PG_N_DWithBasicQuery() throws Exception {
		_groupFinderImpl.countByC_C_PG_N_D(
			_COMPANY_ID, null, 0L, new String[] {"%test%"},
			new String[] {"%desc%"}, new LinkedHashMap<>(), true);

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct group_.groupid from group_ where group_.",
				"companyid = ? and group_.parentgroupid = ? and group_.",
				"livegroupid = ? and group_.groupkey != ? and (lower(",
				"group_.name) like ?) and (lower(group_.description) like ?)"),
			_capturedSQL);
	}

	@Test
	public void testCountByC_C_PG_N_DWithClassNameIds() throws Exception {
		_groupFinderImpl.countByC_C_PG_N_D(
			_COMPANY_ID, new long[] {10L, 20L}, 0L, new String[] {"%test%"},
			new String[] {"%desc%"}, new LinkedHashMap<>(), true);

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct group_.groupid from group_ where group_.",
				"companyid = ? and group_.parentgroupid = ? and group_.",
				"livegroupid = ? and group_.groupkey != ? and (lower(",
				"group_.name) like ?) and (lower(group_.description) like ?) ",
				"and (group_.classnameid = ? or group_.classnameid = ?)"),
			_capturedSQL);
	}

	@Test
	public void testCountByC_C_PG_N_DWithOrOperator() throws Exception {
		_groupFinderImpl.countByC_C_PG_N_D(
			_COMPANY_ID, null, 0L, new String[] {"%a%", "%b%"},
			new String[] {"%c%", "%d%"}, new LinkedHashMap<>(), false);

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct group_.groupid from group_ where group_.",
				"companyid = ? and group_.parentgroupid = ? and group_.",
				"livegroupid = ? and group_.groupkey != ? and ((lower(",
				"group_.name) like ? or lower(group_.name) like ?) or ",
				"(lower(group_.description) like ? or lower(group_.",
				"description) like ?))"),
			_capturedSQL);
	}

	@Test
	public void testCountByC_C_PG_N_DWithParentGroupIdAny() throws Exception {
		_groupFinderImpl.countByC_C_PG_N_D(
			_COMPANY_ID, null, GroupConstants.ANY_PARENT_GROUP_ID,
			new String[] {"%test%"}, new String[] {"%desc%"},
			new LinkedHashMap<>(), true);

		Assert.assertEquals(
			StringBundler.concat(
				"select distinct group_.groupid from group_ where group_.",
				"companyid = ? and group_.parentgroupid != ? and group_.",
				"livegroupid = ? and group_.groupkey != ? and (lower(",
				"group_.name) like ?) and (lower(group_.description) like ?)"),
			_capturedSQL);
	}

	@Test
	public void testFindByC_C_PG_N_DWithActionId() throws Exception {
		RoleLocalServiceUtil.setService(
			new RoleLocalServiceWrapper(null) {

				@Override
				public Role fetchRole(long companyId, String name) {
					return new RoleImpl();
				}

				@Override
				public boolean hasUserRole(long userId, long roleId) {
					return false;
				}

			});

		ResourceActionLocalServiceUtil.setService(
			new ResourceActionLocalServiceWrapper(null) {

				@Override
				public ResourceAction fetchResourceAction(
					String name, String actionId) {

					return new ResourceActionImpl();
				}

			});

		_groupFinderImpl.findByC_C_PG_N_D(
			_COMPANY_ID, null, GroupConstants.ANY_PARENT_GROUP_ID,
			new String[] {null}, new String[] {null},
			LinkedHashMapBuilder.<String, Object>put(
				"actionId", "VIEW"
			).put(
				"userId", 42L
			).build(),
			true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct group_.groupid",
				", group_.name, group_.type_, group_.friendlyurl from group_ ",
				"where group_.companyid = ? and group_.parentgroupid != ? and ",
				"group_.livegroupid = ? and group_.groupkey != ? and (group_.",
				"groupid in (select usergrouprole.groupid from usergrouprole ",
				"inner join users_groups on users_groups.groupid = ",
				"usergrouprole.groupid and users_groups.userid = ? where ",
				"usergrouprole.userid = ? and (usergrouprole.roleid = ? or ",
				"usergrouprole.roleid = ?)) or group_.groupid in (select ",
				"users_groups.groupid from users_groups inner join ",
				"usergrouprole on usergrouprole.userid = users_groups.userid ",
				"and usergrouprole.groupid = users_groups.groupid inner join ",
				"resourcepermission on resourcepermission.roleid = ",
				"usergrouprole.roleid where users_groups.userid = ? and ",
				"bitand(resourcepermission.actionids, ?) != ?) or group_.",
				"groupid in (select users_groups.groupid from users_groups ",
				"inner join users_roles on users_roles.userid = users_groups.",
				"userid inner join resourcepermission on resourcepermission.",
				"roleid = users_roles.roleid where users_groups.userid = ? ",
				"and bitand(resourcepermission.actionids, ?) != ?))) ",
				"tempgroup order by replace(tempgroup.name, ?, ?) asc"),
			_capturedSQL);
	}

	@Test
	public void testFindByC_C_PG_N_DWithBasicQuery() throws Exception {
		_groupFinderImpl.findByC_C_PG_N_D(
			_COMPANY_ID, null, 0L, new String[] {"%test%"},
			new String[] {"%desc%"}, new LinkedHashMap<>(), true,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct group_.groupid",
				", group_.name, group_.type_, group_.friendlyurl from group_ ",
				"where group_.companyid = ? and group_.parentgroupid = ? and ",
				"group_.livegroupid = ? and group_.groupkey != ? and (lower(",
				"group_.name) like ?) and (lower(group_.description) like ?)) ",
				"tempgroup order by replace(tempgroup.name, ?, ?) asc"),
			_capturedSQL);
	}

	@Test
	public void testFindByC_C_PG_N_DWithClassNameIds() throws Exception {
		_groupFinderImpl.findByC_C_PG_N_D(
			_COMPANY_ID, new long[] {10L}, 0L, new String[] {"%test%"},
			new String[] {"%desc%"}, new LinkedHashMap<>(), true,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct group_.groupid",
				", group_.name, group_.type_, group_.friendlyurl from group_ ",
				"where group_.companyid = ? and group_.parentgroupid = ? and ",
				"group_.livegroupid = ? and group_.groupkey != ? and (lower(",
				"group_.name) like ?) and (lower(group_.description) like ?) ",
				"and (group_.classnameid = ?)) tempgroup order by replace(",
				"tempgroup.name, ?, ?) asc"),
			_capturedSQL);
	}

	@Test
	public void testFindByC_C_PG_N_DWithOrOperator() throws Exception {
		_groupFinderImpl.findByC_C_PG_N_D(
			_COMPANY_ID, null, 0L, new String[] {"%a%", "%b%"},
			new String[] {"%c%", "%d%"}, new LinkedHashMap<>(), false,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct group_.groupid",
				", group_.name, group_.type_, group_.friendlyurl from group_ ",
				"where group_.companyid = ? and group_.parentgroupid = ? and ",
				"group_.livegroupid = ? and group_.groupkey != ? and ((lower(",
				"group_.name) like ? or lower(group_.name) like ?) or (lower(",
				"group_.description) like ? or lower(group_.description) like ",
				"?))) tempgroup order by replace(tempgroup.name, ?, ?) asc"),
			_capturedSQL);
	}

	@Test
	public void testGetConditionWithNull() {
		Assert.assertNull(_getCondition(null));
	}

	@Test
	public void testGetConditionWithoutWhere() {
		Assert.assertEquals(
			"", _getCondition("INNER JOIN Foo ON Foo.barId = Bar.barId"));
	}

	@Test
	public void testGetConditionWithWhere() {
		Assert.assertEquals(
			"( Foo.status = ?) AND ",
			_getCondition(
				"INNER JOIN Foo ON Foo.barId = Bar.barId WHERE Foo.status = " +
					"?"));
	}

	@Test
	public void testGetWhereWithClassNameIds() {
		Assert.assertEquals(
			"((Group_.classNameId = ?) OR (Group_.classNameId = ?) OR " +
				"(Group_.classNameId = ?)) AND ",
			_groupFinderImpl.getWhere(
				Collections.singletonMap(
					"classNameIds", new long[] {10L, 20L, 30L})));
	}

	@Test
	public void testGetWhereWithEmptyParams() {
		Assert.assertEquals(
			"", _groupFinderImpl.getWhere(Collections.emptyMap()));
	}

	@Test
	public void testGetWhereWithExcludedGroupIds() {
		Assert.assertEquals(
			"((Group_.groupId != ?) AND (Group_.groupId != ?)) AND ",
			_groupFinderImpl.getWhere(
				Collections.singletonMap(
					"excludedGroupIds", Arrays.asList(1L, 2L))));
	}

	@Test
	public void testGetWhereWithNullParams() {
		Assert.assertEquals("", _groupFinderImpl.getWhere(null));
	}

	@Test
	public void testGetWhereWithSingleClassNameId() {
		Assert.assertEquals(
			"(Group_.classNameId = ?) AND ",
			_groupFinderImpl.getWhere(
				Collections.singletonMap("classNameIds", 10L)));
	}

	@Test
	public void testGetWhereWithTypes() {
		Assert.assertEquals(
			"((Group_.type_ = ?) OR (Group_.type_ = ?) OR (" +
				"Group_.type_ = ?)) AND ",
			_groupFinderImpl.getWhere(
				Collections.singletonMap("types", Arrays.asList(1, 2, 3))));
	}

	@Test
	public void testRemoveWhereWithNull() {
		Assert.assertNull(_removeWhere(null));
	}

	@Test
	public void testRemoveWhereWithoutWhere() {
		Assert.assertEquals(
			"INNER JOIN Foo ON Foo.barId = Bar.barId",
			_removeWhere("INNER JOIN Foo ON Foo.barId = Bar.barId"));
	}

	@Test
	public void testRemoveWhereWithWhere() {
		Assert.assertEquals(
			"INNER JOIN Foo ON Foo.barId = Bar.barId ",
			_removeWhere(
				"INNER JOIN Foo ON Foo.barId = Bar.barId WHERE Foo.status = " +
					"?"));
	}

	private static String _normalizeSQL(String sql) {
		return sql.replaceAll(
			"\\s+", " "
		).trim(
		).toLowerCase();
	}

	private String _getCondition(String join) {
		return ReflectionTestUtil.invoke(
			_groupFinderImpl, "_getCondition", new Class<?>[] {String.class},
			join);
	}

	private String _removeWhere(String join) {
		return ReflectionTestUtil.invoke(
			_groupFinderImpl, "_removeWhere", new Class<?>[] {String.class},
			join);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static String _capturedSQL;
	private static GroupFinderImpl _groupFinderImpl;

}