/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.upgrade.v1_0_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.calendar.test.util.CalendarUpgradeTestUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.model.impl.ResourcePermissionImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UpgradeClassNamesTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"create table ResourceBlock (mvccVersion LONG default 0 not ",
				"null, resourceBlockId LONG not null primary key, companyId ",
				"LONG, groupId LONG, name VARCHAR(75) null, permissionsHash ",
				"VARCHAR(75) null, referenceCount LONG)"));

		setUpUpgradeCalendarResource();
	}

	@After
	public void tearDown() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("drop table ResourceBlock");
	}

	@Test
	public void testDeletesDuplicatedOldResourcePermissions() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String primKey = RandomTestUtil.randomString();
		long roleId = RandomTestUtil.randomLong();
		int scope = RandomTestUtil.nextInt();

		_newResourcePermission = addResourcePermission(
			companyId, "com.liferay.calendar", primKey, roleId, scope);
		_oldResourcePermission = addResourcePermission(
			companyId, "com.liferay.portlet.calendar", primKey, roleId, scope);

		_upgradeProcess.upgrade();

		assertNewResourcePermissionExists();

		assertOldResourcePermissionDoesNotExist();
	}

	@Test
	public void testDoesNotDeleteOldResourcePermissionsWithDifferentCompanyId()
		throws Exception {

		String primKey = RandomTestUtil.randomString();
		long roleId = RandomTestUtil.randomLong();
		int scope = RandomTestUtil.nextInt();

		_newResourcePermission = addResourcePermission(
			RandomTestUtil.randomLong(), "com.liferay.calendar", primKey,
			roleId, scope);
		_oldResourcePermission = addResourcePermission(
			RandomTestUtil.randomLong(), "com.liferay.portlet.calendar",
			primKey, roleId, scope);

		_upgradeProcess.upgrade();

		assertNewResourcePermissionExists();

		assertOldResourcePermissionDoesNotExist();
	}

	@Test
	public void testDoesNotDeleteOldResourcePermissionsWithDifferentPrimKey()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		long roleId = RandomTestUtil.randomLong();
		int scope = RandomTestUtil.nextInt();

		_newResourcePermission = addResourcePermission(
			companyId, "com.liferay.calendar", RandomTestUtil.randomString(),
			roleId, scope);
		_oldResourcePermission = addResourcePermission(
			companyId, "com.liferay.portlet.calendar",
			RandomTestUtil.randomString(), roleId, scope);

		_upgradeProcess.upgrade();

		assertNewResourcePermissionExists();

		assertOldPermissionExists();
	}

	@Test
	public void testDoesNotDeleteOldResourcePermissionsWithDifferentRoleId()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		String primKey = RandomTestUtil.randomString();
		int scope = RandomTestUtil.nextInt();

		_newResourcePermission = addResourcePermission(
			companyId, "com.liferay.calendar", primKey,
			RandomTestUtil.randomLong(), scope);
		_oldResourcePermission = addResourcePermission(
			companyId, "com.liferay.portlet.calendar", primKey,
			RandomTestUtil.randomLong(), scope);

		_upgradeProcess.upgrade();

		assertNewResourcePermissionExists();

		assertOldPermissionExists();
	}

	@Test
	public void testDoesNotDeleteOldResourcePermissionsWithDifferentScope()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		String primKey = RandomTestUtil.randomString();
		long roleId = RandomTestUtil.randomLong();

		_newResourcePermission = addResourcePermission(
			companyId, "com.liferay.calendar", primKey, roleId,
			RandomTestUtil.nextInt());
		_oldResourcePermission = addResourcePermission(
			companyId, "com.liferay.portlet.calendar", primKey, roleId,
			RandomTestUtil.nextInt());

		_upgradeProcess.upgrade();

		assertNewResourcePermissionExists();

		assertOldPermissionExists();
	}

	@Test
	public void testUpdateCalEventClassNameIdInVocabularies() throws Exception {
		_calEventClassName = addClassName(
			"com.liferay.portlet.calendar.model.CalEvent");

		addAssetVocabulary(_calEventClassName.getClassNameId());

		_upgradeProcess.upgrade();

		assertNewClassNameIdExists();
	}

	protected void addAssetVocabulary(long classNameId) throws Exception {
		Group group = GroupTestUtil.addGroup();

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			StringBundler.concat(
				"multiValued=true\nselectedClassNameIds=", classNameId, ":-1"),
			ServiceContextTestUtil.getServiceContext());
	}

	protected ClassName addClassName(String value) {
		ClassName className = _classNameLocalService.createClassName(
			_counterLocalService.increment());

		className.setValue(value);

		return _classNameLocalService.addClassName(className);
	}

	protected ResourcePermission addResourcePermission(
			long companyId, String name, String primKey, long roleId, int scope)
		throws Exception {

		return ResourcePermissionTestUtil.addResourcePermission(
			companyId, name, primKey, roleId, scope);
	}

	protected void assertNewClassNameIdExists() {
		long calBookingClassNameId = _classNameLocalService.getClassNameId(
			"com.liferay.calendar.model.CalendarBooking");

		EntityCacheUtil.clearCache();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				_assetVocabulary.getVocabularyId());

		String settings = assetVocabulary.getSettings();

		boolean containsNewClassNameId = settings.contains(
			String.valueOf(calBookingClassNameId));

		Assert.assertTrue(containsNewClassNameId);
	}

	protected void assertNewResourcePermissionExists() {
		EntityCacheUtil.clearCache(ResourcePermissionImpl.class);

		_newResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_newResourcePermission.getResourcePermissionId());

		Assert.assertNotNull(_newResourcePermission);
	}

	protected void assertOldPermissionExists() {
		EntityCacheUtil.clearCache(ResourcePermissionImpl.class);

		_oldResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_oldResourcePermission.getResourcePermissionId());

		Assert.assertNotNull(_oldResourcePermission);
	}

	protected void assertOldResourcePermissionDoesNotExist() {
		EntityCacheUtil.clearCache(ResourcePermissionImpl.class);

		_oldResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				_oldResourcePermission.getResourcePermissionId());

		Assert.assertNull(_oldResourcePermission);
	}

	protected void setUpUpgradeCalendarResource() {
		_upgradeProcess = CalendarUpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, "v1_0_4.UpgradeClassNames");
	}

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private ClassName _calEventClassName;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	private ResourcePermission _newResourcePermission;
	private ResourcePermission _oldResourcePermission;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.calendar.internal.upgrade.registry.CalendarServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}