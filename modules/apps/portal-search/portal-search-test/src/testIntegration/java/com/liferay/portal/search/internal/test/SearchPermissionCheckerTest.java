/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchPermissionChecker;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.internal.test.util.BaseTestFilterVisitor;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryRoleLocalServiceUtil;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class SearchPermissionCheckerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();

		_depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		_group = GroupTestUtil.addGroup();
		_organization = OrganizationTestUtil.addOrganization();

		_serviceReference = _bundleContext.getServiceReference(
			SearchPermissionChecker.class);

		_searchPermissionChecker = _bundleContext.getService(_serviceReference);
	}

	@After
	public void tearDown() throws Exception {
		_bundleContext.ungetService(_serviceReference);
	}

	@Test
	public void testAdministratorRolePermissionFilter() throws Exception {
		_user = UserTestUtil.addOmniadminUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		BooleanFilter booleanFilter = _getBooleanFilter(null);

		Assert.assertFalse(booleanFilter.hasClauses());
	}

	@Test
	public void testCompanyPermissionFilter() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		UserLocalServiceUtil.addRoleUser(_role.getRoleId(), _user.getUserId());

		_addViewPermission(
			ResourceConstants.SCOPE_COMPANY, TestPropsValues.getCompanyId(),
			_role.getRoleId());

		BooleanFilter booleanFilter = _getBooleanFilter(null);

		Assert.assertFalse(booleanFilter.hasClauses());
	}

	@Ignore
	@Test
	public void testContributedRolesPermissionFilter() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_CLASS_NAME_SEGMENTS_CONFIGURATION,
					HashMapDictionaryBuilder.<String, Object>put(
						"roleSegmentationEnabled", true
					).build())) {

			_user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			Criteria criteria = new Criteria();

			_segmentsCriteriaContributor.contribute(
				criteria,
				String.format("(firstName eq '%s')", _user.getFirstName()),
				Criteria.Conjunction.AND);

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId(), CriteriaSerializer.serialize(criteria));

			_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			SegmentsEntryRoleLocalServiceUtil.addSegmentsEntryRole(
				segmentsEntry.getSegmentsEntryId(), _role.getRoleId(),
				ServiceContextTestUtil.getServiceContext());

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(WebKeys.USER, _user);

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_assertFieldValue(
				new long[] {_group.getGroupId()}, Field.ROLE_ID,
				String.valueOf(_role.getRoleId()));
		}
	}

	@Test
	public void testDepotRolePermissionFilter() throws Exception {
		_user = UserTestUtil.addGroupUser(
			_depotEntry.getGroup(), DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		_addViewPermission(
			ResourceConstants.SCOPE_GROUP, _depotEntry.getGroupId(),
			role.getRoleId());

		_assertFieldValue(
			null, Field.GROUP_ID, String.valueOf(_depotEntry.getGroupId()));
		_assertFieldValue(
			null, Field.GROUP_ROLE_ID,
			_depotEntry.getGroupId() + StringPool.DASH + role.getRoleId());
	}

	@Test
	public void testGroupIdsPermissionFilter() throws Exception {
		_user = UserTestUtil.addOrganizationAdminUser(_organization);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(),
			RoleConstants.ORGANIZATION_ADMINISTRATOR);

		_assertFieldValue(
			new long[] {_group.getGroupId()}, Field.GROUP_ROLE_ID,
			_group.getGroupId() + StringPool.DASH + role.getRoleId(), false);
		_assertFieldValue(
			null, Field.GROUP_ROLE_ID,
			_group.getGroupId() + StringPool.DASH + role.getRoleId(), false);
	}

	@Test
	public void testGroupPermissionFilter() throws Exception {
		_user = UserTestUtil.addGroupAdminUser(_group);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		_addViewPermission(
			ResourceConstants.SCOPE_GROUP, _group.getGroupId(),
			role.getRoleId());

		_assertFieldValue(
			null, Field.GROUP_ID, String.valueOf(_group.getGroupId()));
		_assertFieldValue(
			null, Field.ROLE_ID, String.valueOf(role.getRoleId()));
	}

	@Test
	public void testGroupTemplatePermissionFilter() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		UserLocalServiceUtil.addRoleUser(_role.getRoleId(), _user.getUserId());

		_addViewPermission(
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			GroupConstants.DEFAULT_PARENT_GROUP_ID, _role.getRoleId());

		BooleanFilter booleanFilter = _getBooleanFilter(null);

		Assert.assertFalse(booleanFilter.hasClauses());
	}

	@Test
	public void testGuestPermissionFilter() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_addViewPermission(
			ResourceConstants.SCOPE_GROUP, _group.getGroupId(),
			role.getRoleId());

		_assertFieldValue(
			new long[] {_group.getGroupId()}, Field.GROUP_ID,
			String.valueOf(_group.getGroupId()));
		_assertFieldValue(
			null, Field.GROUP_ID, String.valueOf(_group.getGroupId()));
		_assertFieldValue(
			new long[] {_group.getGroupId()}, Field.ROLE_ID,
			String.valueOf(role.getRoleId()));
	}

	@Test
	public void testOrganizationRolePermissionFilter() throws Exception {
		_user = UserTestUtil.addOrganizationAdminUser(_organization);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(),
			RoleConstants.ORGANIZATION_ADMINISTRATOR);

		_addViewPermission(
			ResourceConstants.SCOPE_GROUP, _organization.getGroupId(),
			role.getRoleId());

		_assertFieldValue(
			null, Field.GROUP_ID, String.valueOf(_organization.getGroupId()));
		_assertFieldValue(
			null, Field.ROLE_ID, String.valueOf(role.getRoleId()));
	}

	@Test
	public void testUserGroupRolePermissionFilter() throws Exception {
		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleLocalServiceUtil.addGroupRole(
			_group.getGroupId(), _role.getRoleId());

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			_user.getUserId(), _group.getGroupId(),
			new long[] {_role.getRoleId()});

		_assertFieldValue(
			null, Field.GROUP_ROLE_ID,
			_group.getGroupId() + StringPool.DASH + _role.getRoleId());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected String getClassName() {
		return DLFileEntry.class.getName();
	}

	private void _addViewPermission(int scope, long primKey, long roleId)
		throws Exception {

		ResourcePermissionLocalServiceUtil.addResourcePermission(
			TestPropsValues.getCompanyId(), getClassName(), scope,
			String.valueOf(primKey), roleId, ActionKeys.VIEW);

		_resourcePermission =
			ResourcePermissionLocalServiceUtil.getResourcePermission(
				TestPropsValues.getCompanyId(), getClassName(), scope,
				String.valueOf(primKey), roleId);
	}

	private void _assertFieldValue(long[] groupIds, String field, String value)
		throws Exception {

		_assertFieldValue(groupIds, field, value, true);
	}

	private void _assertFieldValue(
			long[] groupIds, String field, String value, boolean expected)
		throws Exception {

		BooleanFilter booleanFilter = _getBooleanFilter(groupIds);

		TestFilterVisitor testFilterVisitor = new TestFilterVisitor(
			expected, field, value);

		booleanFilter.accept(testFilterVisitor);

		testFilterVisitor.assertField();
	}

	private BooleanFilter _getBooleanFilter(long[] groupIds) throws Exception {
		return _searchPermissionChecker.getPermissionBooleanFilter(
			TestPropsValues.getCompanyId(), groupIds, _user.getUserId(),
			getClassName(), new BooleanFilter(), new SearchContext());
	}

	private static final String _CLASS_NAME_SEGMENTS_CONFIGURATION =
		"com.liferay.segments.configuration.SegmentsConfiguration";

	private BundleContext _bundleContext;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private ResourcePermission _resourcePermission;

	@DeleteAfterTestRun
	private Role _role;

	private SearchPermissionChecker _searchPermissionChecker;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	private ServiceReference<SearchPermissionChecker> _serviceReference;

	@DeleteAfterTestRun
	private User _user;

	private static class TestFilterVisitor extends BaseTestFilterVisitor<Void> {

		public TestFilterVisitor(boolean expected, String field, String value) {
			_expected = expected;
			_field = field;
			_value = value;
		}

		public void assertField() {
			Assert.assertEquals(_expected, _found);
		}

		@Override
		public Void visit(BooleanFilter booleanFilter) {
			for (BooleanClause<Filter> booleanClause :
					booleanFilter.getMustBooleanClauses()) {

				Filter filter = booleanClause.getClause();

				filter.accept(this);
			}

			for (BooleanClause<Filter> booleanClause :
					booleanFilter.getShouldBooleanClauses()) {

				Filter filter = booleanClause.getClause();

				filter.accept(this);
			}

			return null;
		}

		@Override
		public Void visit(TermsFilter termsFilter) {
			if (_field.equals(termsFilter.getField()) &&
				ArrayUtil.contains(termsFilter.getValues(), _value)) {

				_found = true;
			}

			return null;
		}

		private final boolean _expected;
		private final String _field;
		private boolean _found;
		private final String _value;

	}

}