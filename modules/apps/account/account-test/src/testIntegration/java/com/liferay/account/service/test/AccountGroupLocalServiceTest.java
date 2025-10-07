/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.exception.AccountGroupNameException;
import com.liferay.account.exception.DefaultAccountGroupException;
import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.service.test.util.AccountGroupTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Lee
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountGroupLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAccountGroupName() throws Exception {
		try {
			_accountGroupLocalService.addAccountGroup(
				StringPool.BLANK, TestPropsValues.getUserId(), null, "",
				new ServiceContext());

			Assert.fail();
		}
		catch (AccountGroupNameException accountGroupNameException) {
			String message = accountGroupNameException.getMessage();

			Assert.assertTrue(message.contains("Name is null"));
		}

		AccountGroup accountGroup = _addAccountGroup();

		try {
			_accountGroupLocalService.updateAccountGroup(
				StringPool.BLANK, accountGroup.getUserId(), null, "",
				new ServiceContext());

			Assert.fail();
		}
		catch (AccountGroupNameException accountGroupNameException) {
			String message = accountGroupNameException.getMessage();

			Assert.assertTrue(message.contains("Name is null"));
		}
	}

	@Test
	public void testAddAccountGroup() throws Exception {
		AccountGroup accountGroup = _addAccountGroup();

		Assert.assertNotNull(
			_accountGroupLocalService.fetchAccountGroup(
				accountGroup.getAccountGroupId()));
	}

	@Test
	public void testDeleteAccountGroup() throws Exception {
		_testDeleteAccountGroup(
			_addAccountGroup(),
			accountGroup -> _accountGroupLocalService.deleteAccountGroup(
				accountGroup));
		_testDeleteAccountGroup(
			_addAccountGroup(),
			accountGroup -> _accountGroupLocalService.deleteAccountGroup(
				accountGroup.getAccountGroupId()));
	}

	@Test
	public void testDeleteAccountGroupWithAccountGroupRel() throws Exception {
		AccountGroup accountGroup = _addAccountGroup();

		AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withAccountGroups(accountGroup));

		Assert.assertEquals(
			1,
			_accountGroupRelLocalService.
				getAccountGroupRelsCountByAccountGroupId(
					accountGroup.getAccountGroupId()));

		_accountGroupLocalService.deleteAccountGroup(accountGroup);

		Assert.assertEquals(
			0,
			_accountGroupRelLocalService.
				getAccountGroupRelsCountByAccountGroupId(
					accountGroup.getAccountGroupId()));
	}

	@Test
	public void testDeleteDefaultAccountGroup() throws Exception {
		try {
			_accountGroupLocalService.deleteAccountGroup(
				_accountGroupLocalService.getDefaultAccountGroup(
					TestPropsValues.getCompanyId()));
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					DefaultAccountGroupException.
						MustNotDeleteDefaultAccountGroup);
		}
	}

	@Test
	public void testGetOrAddEmptyAccountGroup() throws Exception {

		// Lazy referencing disabled

		try {
			_accountGroupLocalService.getOrAddEmptyAccountGroup(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString());
		}
		catch (NoSuchGroupException noSuchGroupException) {
			Assert.assertNotNull(noSuchGroupException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AccountGroup accountGroup =
				_accountGroupLocalService.getOrAddEmptyAccountGroup(
					RandomTestUtil.randomString(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					RandomTestUtil.randomString());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, accountGroup.getStatus());
		}
	}

	@Test
	public void testHasDefaultAccountGroupWhenCompanyIsCreated()
		throws Exception {

		Assert.assertTrue(
			_accountGroupLocalService.hasDefaultAccountGroup(
				TestPropsValues.getCompanyId()));

		AccountGroup defaultAccountGroup =
			_accountGroupLocalService.getDefaultAccountGroup(
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), defaultAccountGroup.getCompanyId());
	}

	@Test
	public void testSearchAccountGroups() throws Exception {
		_addAccountGroup();
		_addAccountGroup();

		List<AccountGroup> expectedAccountGroups =
			_accountGroupLocalService.getAccountGroups(
				TestPropsValues.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		expectedAccountGroups = ListUtil.filter(
			expectedAccountGroups,
			accountGroup -> !accountGroup.isDefaultAccountGroup());

		BaseModelSearchResult<AccountGroup> baseModelSearchResult =
			_accountGroupLocalService.searchAccountGroups(
				TestPropsValues.getCompanyId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			expectedAccountGroups.size(), baseModelSearchResult.getLength());
		Assert.assertTrue(
			expectedAccountGroups.containsAll(
				baseModelSearchResult.getBaseModels()));
	}

	@Test
	public void testSearchAccountGroupsWithKeywords() throws Exception {
		String keywords = RandomTestUtil.randomString();

		List<AccountGroup> expectedAccountGroups = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			expectedAccountGroups.add(
				_addAccountGroup(RandomTestUtil.randomString(), keywords + i));
		}

		BaseModelSearchResult<AccountGroup> baseModelSearchResult =
			_accountGroupLocalService.searchAccountGroups(
				TestPropsValues.getCompanyId(), keywords, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create(
					"AccountGroup", "name", true));

		Assert.assertEquals(
			expectedAccountGroups.size(), baseModelSearchResult.getLength());
		Assert.assertEquals(
			expectedAccountGroups, baseModelSearchResult.getBaseModels());
	}

	@Test
	public void testSearchAccountGroupsWithPagination() throws Exception {
		String keywords = RandomTestUtil.randomString();

		List<AccountGroup> expectedAccountGroups = Arrays.asList(
			_addAccountGroup(keywords, RandomTestUtil.randomString()),
			_addAccountGroup(keywords, RandomTestUtil.randomString()),
			_addAccountGroup(keywords, RandomTestUtil.randomString()),
			_addAccountGroup(keywords, RandomTestUtil.randomString()),
			_addAccountGroup(keywords, RandomTestUtil.randomString()));

		Comparator<AccountGroup> comparator =
			(accountGroup1, accountGroup2) -> {
				String name1 = accountGroup1.getName();
				String name2 = accountGroup2.getName();

				return name1.compareToIgnoreCase(name2);
			};

		_testSearchAccountGroupsWithPagination(
			comparator, expectedAccountGroups, keywords, false);
		_testSearchAccountGroupsWithPagination(
			comparator, expectedAccountGroups, keywords, true);
	}

	@Test
	public void testUpdateAccountGroupWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AccountGroup accountGroup =
				_accountGroupLocalService.getOrAddEmptyAccountGroup(
					RandomTestUtil.randomString(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					RandomTestUtil.randomString());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, accountGroup.getStatus());

			accountGroup = _accountGroupLocalService.updateAccountGroup(
				accountGroup.getExternalReferenceCode(),
				accountGroup.getAccountGroupId(), accountGroup.getDescription(),
				accountGroup.getName(), null);

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, accountGroup.getStatus());
		}
	}

	@Test
	public void testUpdateDefaultAccountGroup() throws Exception {
		try {
			AccountGroup accountGroup =
				_accountGroupLocalService.getDefaultAccountGroup(
					TestPropsValues.getCompanyId());

			_accountGroupLocalService.updateAccountGroup(
				StringPool.BLANK, accountGroup.getAccountGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new ServiceContext());
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					DefaultAccountGroupException.
						MustNotUpdateDefaultAccountGroup);
		}
	}

	private AccountGroup _addAccountGroup() throws Exception {
		return AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private AccountGroup _addAccountGroup(String description, String name)
		throws Exception {

		return AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, description, name);
	}

	private void _testDeleteAccountGroup(
			AccountGroup accountGroup,
			UnsafeConsumer<AccountGroup, Exception> unsafeConsumer)
		throws Exception {

		unsafeConsumer.accept(accountGroup);

		Assert.assertNull(
			_accountGroupLocalService.fetchAccountGroup(
				accountGroup.getAccountGroupId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(accountGroup.getModelClassName()),
				accountGroup.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			accountGroup.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	private void _testSearchAccountGroupsWithPagination(
			Comparator<AccountGroup> comparator,
			List<AccountGroup> expectedAccountGroups, String keywords,
			boolean reversed)
		throws Exception {

		int delta = 3;
		int start = 1;

		BaseModelSearchResult<AccountGroup> baseModelSearchResult =
			_accountGroupLocalService.searchAccountGroups(
				TestPropsValues.getCompanyId(), keywords, start, start + delta,
				OrderByComparatorFactoryUtil.create(
					"AccountGroup", "name", !reversed));

		Assert.assertEquals(
			expectedAccountGroups.size(), baseModelSearchResult.getLength());

		List<AccountGroup> actualAccountGroups =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			actualAccountGroups.toString(), delta, actualAccountGroups.size());

		if (reversed) {
			expectedAccountGroups.sort(comparator.reversed());
		}
		else {
			expectedAccountGroups.sort(comparator);
		}

		Assert.assertEquals(
			ListUtil.subList(expectedAccountGroups, start, start + delta),
			actualAccountGroups);
	}

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}