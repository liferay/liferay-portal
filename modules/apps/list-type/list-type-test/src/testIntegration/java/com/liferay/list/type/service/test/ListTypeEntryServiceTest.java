/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());
		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false, Collections.emptyList());
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = TestPropsValues.getUser();

		_setUser(_user);
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddListTypeEntry() throws Exception {
		try {
			_testAddListTypeEntry(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testAddListTypeEntry(_user);
	}

	@Test
	public void testDeleteListTypeEntry() throws Exception {
		try {
			_testDeleteListTypeEntry(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testDeleteListTypeEntry(_user);
	}

	@Test
	public void testGetListTypeEntry() throws Exception {
		try {
			_testGetListTypeEntry(_guestUser);
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetListTypeEntry(_user);
	}

	@Test
	public void testGetListTypeEntryByExternalReferenceCode() throws Exception {
		try {
			_testGetListTypeEntryByExternalReferenceCode(_guestUser);
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetListTypeEntryByExternalReferenceCode(_user);
	}

	@Test
	public void testGetOrAddEmptyListTypeEntry() throws Exception {

		// Lazy referencing disabled

		String key = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchListTypeEntryException.class,
			StringBundler.concat(
				"No ListTypeEntry exists with the key {listTypeDefinitionId=",
				_listTypeDefinition.getListTypeDefinitionId(), ", key=", key,
				"}"),
			() -> _listTypeEntryService.getOrAddEmptyListTypeEntry(
				_user.getUserId(),
				_listTypeDefinition.getListTypeDefinitionId(), key));

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			ListTypeEntry listTypeEntry =
				_listTypeEntryService.getOrAddEmptyListTypeEntry(
					_user.getUserId(),
					_listTypeDefinition.getListTypeDefinitionId(),
					RandomTestUtil.randomString());

			// Without permissions

			User user = UserTestUtil.addUser();

			_setUser(user);

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have UPDATE permission for ",
					_listTypeDefinition.getModelClassName(), " ",
					_listTypeDefinition.getListTypeDefinitionId()),
				() -> _listTypeEntryService.getOrAddEmptyListTypeEntry(
					user.getUserId(),
					_listTypeDefinition.getListTypeDefinitionId(),
					RandomTestUtil.randomString()));

			// Without permissions, existing list type entry

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have VIEW permission for ",
					_listTypeDefinition.getModelClassName(), " ",
					_listTypeDefinition.getListTypeDefinitionId()),
				() -> _listTypeEntryService.getOrAddEmptyListTypeEntry(
					user.getUserId(), listTypeEntry.getListTypeDefinitionId(),
					listTypeEntry.getKey()));
		}
	}

	@Test
	public void testUpdateListTypeEntry() throws Exception {
		try {
			_testUpdateListTypeEntry(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testUpdateListTypeEntry(_user);
	}

	private ListTypeEntry _addListTypeEntry(User user) throws Exception {
		return _listTypeEntryLocalService.addListTypeEntry(
			null, user.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(),
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_listTypeDefinition.isSystem());
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testAddListTypeEntry(User user) throws Exception {
		ListTypeEntry listTypeEntry = null;

		try {
			_setUser(user);

			listTypeEntry = _listTypeEntryService.addListTypeEntry(
				null, _listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				_listTypeDefinition.isSystem());
		}
		finally {
			if (listTypeEntry != null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private void _testDeleteListTypeEntry(User user) throws Exception {
		ListTypeEntry deleteListTypeEntry = null;
		ListTypeEntry listTypeEntry = null;

		try {
			_setUser(user);

			listTypeEntry = _addListTypeEntry(user);

			deleteListTypeEntry = _listTypeEntryService.deleteListTypeEntry(
				listTypeEntry.getListTypeEntryId());
		}
		finally {
			if (deleteListTypeEntry == null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private void _testGetListTypeEntry(User user) throws Exception {
		ListTypeEntry listTypeEntry = null;

		try {
			_setUser(user);

			listTypeEntry = _addListTypeEntry(user);

			_listTypeEntryService.getListTypeEntry(
				listTypeEntry.getListTypeEntryId());
		}
		finally {
			if (listTypeEntry != null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private void _testGetListTypeEntryByExternalReferenceCode(User user)
		throws Exception {

		ListTypeEntry listTypeEntry = null;

		try {
			_setUser(user);

			listTypeEntry = _addListTypeEntry(user);

			_listTypeEntryService.getListTypeEntryByExternalReferenceCode(
				listTypeEntry.getExternalReferenceCode(),
				listTypeEntry.getCompanyId(),
				_listTypeDefinition.getListTypeDefinitionId());
		}
		finally {
			if (listTypeEntry != null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private void _testUpdateListTypeEntry(User user) throws Exception {
		ListTypeEntry listTypeEntry = null;

		try {
			_setUser(user);

			listTypeEntry = _addListTypeEntry(user);

			String externalReferenceCode = RandomTestUtil.randomString();

			listTypeEntry = _listTypeEntryService.updateListTypeEntry(
				externalReferenceCode, listTypeEntry.getListTypeEntryId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()));

			Assert.assertEquals(
				externalReferenceCode,
				listTypeEntry.getExternalReferenceCode());
		}
		finally {
			if (listTypeEntry != null) {
				_listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
			}
		}
	}

	private User _guestUser;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ListTypeEntryService _listTypeEntryService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private User _user;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}