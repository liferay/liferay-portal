/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureUtil;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@FeatureFlag("LPD-34651")
@RunWith(Arquillian.class)
public class DDMStructureServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.SUPPORTS,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_classNameId = PortalUtil.getClassNameId(DDL_RECORD_SET_CLASS_NAME);
		_group = GroupTestUtil.addGroup();
		_siteAdminUser = UserTestUtil.addGroupAdminUser(group);

		setUpPermissionThreadLocal();
		setUpPrincipalThreadLocal();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testFetchStructureByExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		DDMStructure structure = _addStructure(externalReferenceCode);

		Assert.assertNotNull(
			_ddmStructureService.fetchStructureByExternalReferenceCode(
				externalReferenceCode, structure.getGroupId(),
				structure.getClassNameId()));

		_ddmStructureService.deleteStructure(structure.getStructureId());
	}

	@Test
	public void testGetStructureByExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchStructureException.class,
			StringBundler.concat(
				"No DDMStructure exists with the key {externalReferenceCode=",
				externalReferenceCode, ", groupId=", group.getGroupId(),
				", classNameId=", _classNameId, "}"),
			() -> _ddmStructureService.getStructureByExternalReferenceCode(
				externalReferenceCode, group.getGroupId(), _classNameId));

		DDMStructure structure = _addStructure(externalReferenceCode);

		Assert.assertEquals(
			structure,
			_ddmStructureService.getStructureByExternalReferenceCode(
				externalReferenceCode, structure.getGroupId(),
				structure.getClassNameId()));

		_ddmStructureService.deleteStructure(structure.getStructureId());
	}

	@Test
	public void testGetStructures() throws Exception {
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));

		List<DDMStructure> ddmStructures = _ddmStructureService.getStructures(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), _group.getGroupId()}, _classNameId,
			WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());
	}

	@Test
	public void testGetStructuresCountWithoutUserPermission() throws Exception {
		addStructure(group, _classNameId, StringUtil.randomString());
		addStructure(group, _classNameId, StringUtil.randomString());
		addStructure(group, _classNameId, StringUtil.randomString());

		Assert.assertEquals(
			3,
			_ddmStructureService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId));

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Assert.assertEquals(
				0,
				_ddmStructureService.getStructuresCount(
					group.getCompanyId(), new long[] {group.getGroupId()},
					_classNameId));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetStructuresCountWithoutUserPermissionAndWithKeywords()
		throws Exception {

		addStructure(group, _classNameId, "Basic Structure");
		addStructure(group, _classNameId, "Blank Structure");
		addStructure(group, _classNameId, "Sample Structure");

		Assert.assertEquals(
			3,
			_ddmStructureService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Structure", WorkflowConstants.STATUS_ANY));

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Assert.assertEquals(
				0,
				_ddmStructureService.getStructuresCount(
					group.getCompanyId(), new long[] {group.getGroupId()},
					_classNameId, "Structure", WorkflowConstants.STATUS_ANY));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(user);
		}
	}

	@Test
	@Transactional
	public void testGetStructuresWithSiteAdminPermission() throws Throwable {
		DDMStructure structure1 = addStructure(
			_classNameId, StringUtil.randomString());
		DDMStructure structure2 = addStructure(
			_classNameId, StringUtil.randomString());

		_ddmStructures.add(structure1);
		_ddmStructures.add(structure2);

		long[] groupIds = {group.getGroupId(), _group.getGroupId()};

		User siteAdminUser = UserTestUtil.addGroupAdminUser(group);

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(siteAdminUser));

			List<DDMStructure> ddmStructures =
				DDMStructureUtil.filterFindByGroupId(groupIds);

			Assert.assertEquals(
				ddmStructures.toString(), 2, ddmStructures.size());
			Assert.assertEquals(structure1, ddmStructures.get(0));
			Assert.assertEquals(structure2, ddmStructures.get(1));
		}
		finally {
			_userLocalService.deleteUser(siteAdminUser);
		}

		siteAdminUser = UserTestUtil.addGroupAdminUser(_group);

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(siteAdminUser));

			List<DDMStructure> ddmStructures =
				DDMStructureUtil.filterFindByGroupId(groupIds);

			Assert.assertEquals(
				ddmStructures.toString(), 0, ddmStructures.size());
		}
		finally {
			_userLocalService.deleteUser(siteAdminUser);
		}
	}

	@Test
	public void testGetStructuresWithoutUserPermission() throws Exception {
		addStructure(group, _classNameId, StringUtil.randomString());
		addStructure(group, _classNameId, StringUtil.randomString());
		addStructure(group, _classNameId, StringUtil.randomString());

		List<DDMStructure> ddmStructures = _ddmStructureService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			ddmStructures = _ddmStructureService.getStructures(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				ddmStructures.toString(), 0, ddmStructures.size());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetStructuresWithoutUserPermissionAndWithKeywords()
		throws Exception {

		addStructure(group, _classNameId, "Basic Structure");
		addStructure(group, _classNameId, "Blank Structure");
		addStructure(group, _classNameId, "Sample Structure");

		List<DDMStructure> ddmStructures = _ddmStructureService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			"Structure", WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			ddmStructures = _ddmStructureService.getStructures(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Structure", WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				ddmStructures.toString(), 0, ddmStructures.size());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testSearch() throws Exception {
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), _group.getGroupId()}, _classNameId,
			StringPool.BLANK, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());
	}

	@Test
	public void testSearchByNameAndDescription() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		List<DDMStructure> expectedDDMStructures = new ArrayList<>(3);

		expectedDDMStructures.add(
			addStructure(_classNameId, name, description));
		expectedDDMStructures.add(
			addStructure(_classNameId, name, StringUtil.randomString()));
		expectedDDMStructures.add(
			addStructure(_classNameId, StringUtil.randomString(), description));

		_ddmStructures.addAll(expectedDDMStructures);

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), _group.getGroupId()}, _classNameId,
			name, description, StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT, WorkflowConstants.STATUS_ANY,
			true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());

		for (DDMStructure ddmStructure : expectedDDMStructures) {
			Assert.assertTrue(ddmStructures.contains(ddmStructure));
		}
	}

	@Test
	public void testSearchByType() throws Exception {
		_ddmStructures.add(
			addStructure(
				0, _classNameId, null, StringUtil.randomString(),
				StringPool.BLANK, read("test-structure.xsd"),
				StorageType.DEFAULT.getValue(),
				DDMStructureConstants.TYPE_DEFAULT,
				WorkflowConstants.STATUS_APPROVED));

		_ddmStructures.add(
			addStructure(
				0, _classNameId, null, StringUtil.randomString(),
				StringPool.BLANK, read("test-structure.xsd"),
				StorageType.DEFAULT.getValue(),
				DDMStructureConstants.TYPE_FRAGMENT,
				WorkflowConstants.STATUS_APPROVED));

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());

		ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());
	}

	@Test
	public void testSearchCount() throws Exception {
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));

		int count = _ddmStructureService.searchCount(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), _group.getGroupId()}, _classNameId,
			StringPool.BLANK, WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testSearchCountByNameAndDescription() throws Exception {
		String name = StringUtil.randomString();
		String description = StringUtil.randomString();

		_ddmStructures.add(addStructure(_classNameId, name, description));

		_ddmStructures.add(
			addStructure(_classNameId, name, StringUtil.randomString()));
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString(), description));

		int count = _ddmStructureService.searchCount(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), _group.getGroupId()}, _classNameId,
			name, description, StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT, WorkflowConstants.STATUS_ANY,
			true);

		Assert.assertEquals(3, count);
	}

	@Test
	public void testSearchCountByType() throws Exception {
		int initialCount = _ddmStructureService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_ANY, true);

		_ddmStructures.add(
			addStructure(
				0, _classNameId, null, StringUtil.randomString(),
				StringPool.BLANK, read("test-structure.xsd"),
				StorageType.DEFAULT.getValue(),
				DDMStructureConstants.TYPE_FRAGMENT,
				WorkflowConstants.STATUS_APPROVED));

		int count = _ddmStructureService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_ANY, true);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchWithSiteAdminPermission() throws Exception {
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));

		DDMStructure structure = addStructure(
			_classNameId, StringUtil.randomString());

		_ddmStructures.add(structure);

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(),
			new long[] {group.getGroupId(), group.getGroupId()}, _classNameId,
			StringPool.BLANK, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 2, ddmStructures.size());
	}

	@Test
	public void testSearchWithSiteMemberPermission() throws Exception {
		_ddmStructures.add(
			addStructure(_classNameId, StringUtil.randomString()));

		DDMStructure structure = addStructure(
			_classNameId, StringUtil.randomString());

		_ddmStructures.add(structure);

		long[] groupIds = {group.getGroupId(), group.getGroupId()};

		User siteMemberUser = UserTestUtil.addGroupUser(
			group, RoleConstants.SITE_MEMBER);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(siteMemberUser));

		List<DDMStructure> ddmStructures = _ddmStructureService.search(
			TestPropsValues.getCompanyId(), groupIds, _classNameId,
			StringPool.BLANK, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 0, ddmStructures.size());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void setUpPermissionThreadLocal() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_siteAdminUser));
	}

	protected void setUpPrincipalThreadLocal() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(_siteAdminUser.getUserId());
	}

	private DDMStructure _addStructure(String externalReferenceCode)
		throws Exception {

		DDMStructure structure = addStructure(
			group, _classNameId, RandomTestUtil.randomString());

		structure.setExternalReferenceCode(externalReferenceCode);

		return _ddmStructureLocalService.updateStructure(
			externalReferenceCode, structure.getUserId(),
			structure.getStructureId(), structure.getGroupId(),
			structure.getParentStructureId(), structure.getClassNameId(),
			structure.getStructureKey(), structure.getNameMap(),
			structure.getDescriptionMap(), structure.getDefinition(),
			ServiceContextTestUtil.getServiceContext(structure.getGroupId()));
	}

	private long _classNameId;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMStructureService _ddmStructureService;

	@DeleteAfterTestRun
	private final List<DDMStructure> _ddmStructures = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@DeleteAfterTestRun
	private User _siteAdminUser;

	@Inject
	private UserLocalService _userLocalService;

}