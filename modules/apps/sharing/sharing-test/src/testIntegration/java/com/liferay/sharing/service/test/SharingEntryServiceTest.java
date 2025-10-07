/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.sharing.exception.DuplicateSharingEntryException;
import com.liferay.sharing.exception.InvalidSharingEntryActionException;
import com.liferay.sharing.exception.InvalidSharingEntryExpirationDateException;
import com.liferay.sharing.exception.NoSuchEntryException;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermissionChecker;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class SharingEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _classNameLocalService.getClassNameId(
			Group.class.getName());
		_group = GroupTestUtil.addGroup();
		_fromUser = UserTestUtil.addOmniadminUser();
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
		_toUser = UserTestUtil.addUser();
		_user = UserTestUtil.addUser();

		UserTestUtil.setUser(_fromUser);

		Bundle bundle = FrameworkUtil.getBundle(SharingEntryServiceTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testAddOrUpdateSharingEntryAddsNewSharingEntry()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		Instant instant = Instant.now();

		Date expirationDate = Date.from(instant.plus(2, ChronoUnit.DAYS));

		SharingEntry sharingEntry =
			_sharingEntryService.addOrUpdateSharingEntry(
				null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
				_group.getGroupId(), true,
				Collections.singletonList(SharingEntryAction.VIEW),
				expirationDate, _serviceContext);

		Assert.assertEquals(_group.getCompanyId(), sharingEntry.getCompanyId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getGroupId());
		Assert.assertEquals(_fromUser.getUserId(), sharingEntry.getUserId());
		Assert.assertEquals(_toUser.getUserId(), sharingEntry.getToUserId());
		Assert.assertEquals(_classNameId, sharingEntry.getClassNameId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getClassPK());
		Assert.assertTrue(sharingEntry.isShareable());
		Assert.assertEquals(expirationDate, sharingEntry.getExpirationDate());
	}

	@Test
	public void testAddOrUpdateSharingEntryUpdatesSharingEntry()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		Instant instant = Instant.now();

		Date expirationDate = Date.from(instant.plus(2, ChronoUnit.DAYS));

		SharingEntry addSharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), expirationDate,
			_serviceContext);

		Assert.assertTrue(addSharingEntry.isShareable());
		Assert.assertEquals(1, addSharingEntry.getActionIds());
		Assert.assertEquals(
			expirationDate, addSharingEntry.getExpirationDate());

		expirationDate = Date.from(instant.plus(3, ChronoUnit.DAYS));

		SharingEntry updateSharingEntry =
			_sharingEntryService.addOrUpdateSharingEntry(
				null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
				_group.getGroupId(), false,
				Arrays.asList(
					SharingEntryAction.VIEW, SharingEntryAction.UPDATE),
				expirationDate, _serviceContext);

		Assert.assertFalse(updateSharingEntry.isShareable());
		Assert.assertEquals(3, updateSharingEntry.getActionIds());
		Assert.assertEquals(
			expirationDate, updateSharingEntry.getExpirationDate());

		Assert.assertEquals(
			addSharingEntry.getSharingEntryId(),
			updateSharingEntry.getSharingEntryId());
	}

	@Test(expected = InvalidSharingEntryActionException.class)
	public void testAddOrUpdateSharingEntryWithEmptySharingEntryActions()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		_sharingEntryService.addOrUpdateSharingEntry(
			null, _toUser.getUserId(), 0, _classNameId, _group.getGroupId(),
			_group.getGroupId(), true, Collections.emptyList(), null,
			_serviceContext);
	}

	@Test(expected = InvalidSharingEntryExpirationDateException.class)
	public void testAddOrUpdateSharingEntryWithExpirationDateInThePast()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		Instant instant = Instant.now();

		Date expirationDate = Date.from(instant.minus(2, ChronoUnit.DAYS));

		_sharingEntryService.addOrUpdateSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), expirationDate,
			_serviceContext);
	}

	@Test
	public void testAddOrUpdateSharingEntryWithExternalReferenceCode()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		String externalReferenceCode = RandomTestUtil.randomString();

		_sharingEntryService.addOrUpdateSharingEntry(
			externalReferenceCode, 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, _serviceContext);

		SharingEntry sharingEntry =
			_sharingEntryLocalService.fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId());

		Assert.assertNotNull(sharingEntry);
		Assert.assertEquals(
			externalReferenceCode, sharingEntry.getExternalReferenceCode());
	}

	@Test(expected = DuplicateSharingEntryException.class)
	public void testAddSharingEntryWithExistingExternalReferenceCode()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		String externalReferenceCode = RandomTestUtil.randomString();

		_sharingEntryService.addSharingEntry(
			externalReferenceCode, 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, _serviceContext);

		_sharingEntryService.addSharingEntry(
			externalReferenceCode, 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, _serviceContext);
	}

	@Test
	public void testAddSharingEntryWithExternalReferenceCode()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		String externalReferenceCode = RandomTestUtil.randomString();

		_sharingEntryService.addSharingEntry(
			externalReferenceCode, 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null, _serviceContext);

		SharingEntry sharingEntry =
			_sharingEntryLocalService.fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId());

		Assert.assertNotNull(sharingEntry);
		Assert.assertEquals(
			externalReferenceCode, sharingEntry.getExternalReferenceCode());
	}

	@Test(expected = PrincipalException.class)
	public void testAddSharingEntryWithInvalidClassNameIdThrowsException()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		ClassName invalidClassName = _classNameLocalService.addClassName(
			"InvalidClassName");

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), invalidClassName.getClassNameId(),
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test
	public void testAddSharingEntryWithUpdateAndViewPermission()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);

		Assert.assertEquals(_group.getCompanyId(), sharingEntry.getCompanyId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getGroupId());
		Assert.assertEquals(_fromUser.getUserId(), sharingEntry.getUserId());
		Assert.assertEquals(_toUser.getUserId(), sharingEntry.getToUserId());
		Assert.assertEquals(_classNameId, sharingEntry.getClassNameId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getClassPK());
		Assert.assertTrue(sharingEntry.isShareable());
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSharingEntryWithUpdateAndViewPermissionWhenUserHasShareableAddDiscussionAndViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(
				SharingEntryAction.ADD_DISCUSSION, SharingEntryAction.VIEW),
			null, _serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test
	public void testAddSharingEntryWithUpdateAndViewPermissionWhenUserHasShareableUpdateAndViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSharingEntryWithUpdateAndViewPermissionWhenUserHasUnshareableUpdateAndViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSharingEntryWithUpdateAndViewPermissionWhenUserHasUpdatePermissionAndShareableViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.UPDATE);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSharingEntryWithUpdateAndViewPermissionWhenUserHasViewPermission()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAddSharingEntryWithUpdatePermissionWhenUserHasViewPermissionThrowsException()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.UPDATE), null,
			_serviceContext);
	}

	@Test
	public void testAddSharingEntryWithViewPermission() throws Exception {
		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		Assert.assertEquals(_group.getCompanyId(), sharingEntry.getCompanyId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getGroupId());
		Assert.assertEquals(_fromUser.getUserId(), sharingEntry.getUserId());
		Assert.assertEquals(_toUser.getUserId(), sharingEntry.getToUserId());
		Assert.assertEquals(_classNameId, sharingEntry.getClassNameId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getClassPK());
		Assert.assertTrue(sharingEntry.isShareable());
	}

	@Test
	public void testAddSharingEntryWithViewPermissionWhenUserHasShareableUpdateAndViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			null, _serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);
	}

	@Test
	public void testAddSharingEntryWithViewPermissionWhenUserHasViewPermissionAndShareableViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);
	}

	@Test
	public void testAddSharingEntryWithViewPermissionWhenUserHasViewPermissionAndUnshareableViewSharingEntryAction()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		_sharingEntryLocalService.addSharingEntry(
			null, _user.getUserId(), 0, _fromUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		_sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testDeleteNonexistingSharingEntry() throws Exception {
		_sharingEntryService.updateSharingEntry(
			RandomTestUtil.randomLong(),
			Arrays.asList(
				SharingEntryAction.ADD_DISCUSSION, SharingEntryAction.UPDATE,
				SharingEntryAction.VIEW),
			true, null, _serviceContext);
	}

	@Test
	public void testDeleteSharingEntry() throws Exception {
		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, _fromUser.getUserId(), 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		_sharingEntryService.deleteSharingEntry(
			sharingEntry.getSharingEntryId(), _serviceContext);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				sharingEntry.getSharingEntryId()));

		sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, _fromUser.getUserId(), 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		_sharingEntryService.deleteSharingEntry(sharingEntry);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				sharingEntry.getSharingEntryId()));
	}

	@Test
	public void testDeleteSharingEntryByExternalReferenceCode()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		String externalReferenceCode = RandomTestUtil.randomString();

		_sharingEntryLocalService.addSharingEntry(
			externalReferenceCode, _fromUser.getUserId(), 0,
			_toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true, Arrays.asList(SharingEntryAction.VIEW),
			null, _serviceContext);

		Assert.assertNotNull(
			_sharingEntryLocalService.fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId()));

		_sharingEntryService.deleteSharingEntryByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, _group.getGroupId()));
	}

	@Test(expected = NoSuchEntryException.class)
	public void testDeleteSharingEntryByExternalReferenceCodeWithNonexistingExternalReferenceCode()
		throws Exception {

		_sharingEntryService.deleteSharingEntryByExternalReferenceCode(
			RandomTestUtil.randomString(), _group.getGroupId());
	}

	@Test
	public void testGetSharingEntries() throws Exception {
		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		User user1 = UserTestUtil.addUser();

		SharingEntry sharingEntry1 = _sharingEntryLocalService.addSharingEntry(
			null, _fromUser.getUserId(), 0, user1.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		User user2 = UserTestUtil.addUser();

		SharingEntry sharingEntry2 = _sharingEntryLocalService.addSharingEntry(
			null, _fromUser.getUserId(), 0, user2.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		List<SharingEntry> sharingEntries =
			_sharingEntryService.getSharingEntries(
				_classNameId, _group.getGroupId(), _group.getGroupId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			Arrays.asList(sharingEntry1, sharingEntry2), sharingEntries);

		sharingEntries = _sharingEntryService.getSharingEntries(
			_classNameId, _group.getGroupId(), _group.getGroupId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			OrderByComparatorFactoryUtil.create(
				"SharingEntry", "createDate", false));

		Assert.assertEquals(
			Arrays.asList(sharingEntry2, sharingEntry1), sharingEntries);
	}

	@Test(expected = PrincipalException.class)
	public void testSharingEntryCannotBeDeletedByAnyUserOtherThanTheSharer()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryLocalService.addSharingEntry(
			null, _fromUser.getUserId(), 0, _toUser.getUserId(), _classNameId,
			_group.getGroupId(), _group.getGroupId(), false,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		UserTestUtil.setUser(_toUser);

		_sharingEntryService.deleteSharingEntry(
			sharingEntry.getSharingEntryId(), _serviceContext);

		Assert.assertNull(
			_sharingEntryLocalService.fetchSharingEntry(
				sharingEntry.getSharingEntryId()));
	}

	@Test
	public void testUpdateSharingEntryShareable() throws Exception {
		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		Assert.assertTrue(sharingEntry.isShareable());

		sharingEntry = _sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Collections.singletonList(SharingEntryAction.VIEW), false, null,
			_serviceContext);

		Assert.assertFalse(sharingEntry.isShareable());
	}

	@Test
	public void testUpdateSharingEntryWithExpirationDateInTheFuture()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		Assert.assertNull(sharingEntry.getExpirationDate());

		Instant instant = Instant.now();

		Date expirationDate = Date.from(instant.plus(2, ChronoUnit.DAYS));

		sharingEntry = _sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Collections.singletonList(SharingEntryAction.VIEW), true,
			expirationDate, _serviceContext);

		Assert.assertEquals(expirationDate, sharingEntry.getExpirationDate());
	}

	@Test(expected = InvalidSharingEntryExpirationDateException.class)
	public void testUpdateSharingEntryWithExpirationDateInThePast()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		Instant instant = Instant.now();

		Date expirationDate = Date.from(instant.minus(2, ChronoUnit.DAYS));

		_sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Collections.singletonList(SharingEntryAction.VIEW), true,
			expirationDate, _serviceContext);
	}

	@Test
	public void testUpdateSharingEntryWithUpdateAndViewPermission()
		throws Exception {

		_registerSharingPermissionChecker(
			SharingEntryAction.UPDATE, SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		sharingEntry = _sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			true, null, _serviceContext);

		Assert.assertEquals(3, sharingEntry.getActionIds());
		Assert.assertEquals(_group.getCompanyId(), sharingEntry.getCompanyId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getGroupId());
		Assert.assertEquals(_fromUser.getUserId(), sharingEntry.getUserId());
		Assert.assertEquals(_toUser.getUserId(), sharingEntry.getToUserId());
		Assert.assertEquals(_classNameId, sharingEntry.getClassNameId());
		Assert.assertEquals(_group.getGroupId(), sharingEntry.getClassPK());
		Assert.assertTrue(sharingEntry.isShareable());
	}

	@Test(expected = PrincipalException.class)
	public void testUpdateSharingEntryWithUpdateAndViewPermissionWhenUserHasViewPermission()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		UserTestUtil.setUser(_toUser);

		_sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			true, null, _serviceContext);
	}

	@Test(expected = PrincipalException.class)
	public void testUpdateSharingEntryWithUpdatePermissionWhenUserHasViewPermission()
		throws Exception {

		_registerSharingPermissionChecker(SharingEntryAction.VIEW);

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, _toUser.getUserId(), _classNameId, _group.getGroupId(),
			_group.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		UserTestUtil.setUser(_toUser);

		_sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(),
			Arrays.asList(SharingEntryAction.UPDATE, SharingEntryAction.VIEW),
			true, null, _serviceContext);
	}

	private void _registerSharingPermissionChecker(
		SharingEntryAction... sharingEntryActions) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("model.class.name", Group.class.getName());

		_serviceRegistration = _bundleContext.registerService(
			SharingPermissionChecker.class,
			new TestSharingPermissionChecker(sharingEntryActions), properties);
	}

	private BundleContext _bundleContext;
	private long _classNameId;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private User _fromUser;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private ServiceRegistration<SharingPermissionChecker> _serviceRegistration;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private SharingEntryService _sharingEntryService;

	@DeleteAfterTestRun
	private User _toUser;

	@DeleteAfterTestRun
	private User _user;

	private class TestSharingPermissionChecker
		implements SharingPermissionChecker {

		public TestSharingPermissionChecker(
			SharingEntryAction[] sharingEntryActions) {

			_sharingEntryActions = sharingEntryActions;
		}

		@Override
		public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, long groupId,
			Collection<SharingEntryAction> sharingEntryActions) {

			for (SharingEntryAction sharingEntryAction : sharingEntryActions) {
				if (!ArrayUtil.contains(
						_sharingEntryActions, sharingEntryAction)) {

					return false;
				}
			}

			return true;
		}

		private final SharingEntryAction[] _sharingEntryActions;

	}

}