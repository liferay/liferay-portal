/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceAccountGroupTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.FDSDataProviderRegistry;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactory;
import com.liferay.frontend.data.set.provider.search.FDSKeywordsFactoryRegistry;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class CommerceProductAccountGroupFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			commerceCatalog.getGroupId());

		_accountGroup1 = _addAccountGroup(cpDefinition);
		_accountGroup2 = _addAccountGroup(cpDefinition);

		_mockHttpServletRequest.setParameter(
			"cpDefinitionId", String.valueOf(cpDefinition.getCPDefinitionId()));

		_fdsDataProvider = _fdsDataProviderRegistry.getFDSDataProvider(
			_FDS_NAME);

		FDSKeywordsFactory fdsKeywordsFactory =
			_fdsKeywordsFactoryRegistry.getFDSKeywordsFactory(_FDS_NAME);

		_fdsKeywords = fdsKeywordsFactory.create(_mockHttpServletRequest);
	}

	@Test
	public void testGetItems() throws Exception {
		try {
			_getAccountGroupNames(
				_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertPrincipalException(principalException);
		}

		Assert.assertEquals(
			Collections.singleton(_accountGroup1.getName()),
			_getAccountGroupNames(_addAccountEntryUser(_accountGroup1)));
		Assert.assertEquals(
			Collections.emptySet(), _getAccountGroupNames(_addUser()));
		Assert.assertEquals(
			SetUtil.fromArray(
				_accountGroup1.getName(), _accountGroup2.getName()),
			_getAccountGroupNames(TestPropsValues.getUser()));
	}

	@Test
	public void testGetItemsCount() throws Exception {
		try {
			_getItemsCount(
				_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertPrincipalException(principalException);
		}

		Assert.assertEquals(
			1, _getItemsCount(_addAccountEntryUser(_accountGroup1)));
		Assert.assertEquals(0, _getItemsCount(_addUser()));
		Assert.assertEquals(2, _getItemsCount(TestPropsValues.getUser()));
	}

	private User _addAccountEntryUser(AccountGroup accountGroup)
		throws Exception {

		User user = _addUser();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {user.getUserId()},
				null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId());

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap());

		RoleTestUtil.addResourcePermission(
			accountRole.getRole(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			AccountActionKeys.VIEW_ACCOUNT_GROUPS);

		_accountRoleLocalService.associateUser(
			accountEntry.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		return user;
	}

	private AccountGroup _addAccountGroup(CPDefinition cpDefinition)
		throws Exception {

		AccountGroup accountGroup =
			CommerceAccountGroupTestUtil.addAccountGroup(
				TestPropsValues.getGroupId());

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		return accountGroup;
	}

	private User _addUser() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		return user;
	}

	private void _assertPrincipalException(
			PrincipalException.MustHavePermission principalException)
		throws Exception {

		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW}, principalException.actionId);
		Assert.assertEquals(guestUser.getUserId(), principalException.userId);
	}

	private FDSPagination _createFDSPagination(
		int startPosition, int endPosition) {

		return new FDSPagination() {

			@Override
			public int getEndPosition() {
				return endPosition;
			}

			@Override
			public int getPage() {
				return 1;
			}

			@Override
			public int getPageSize() {
				return endPosition - startPosition;
			}

			@Override
			public int getStartPosition() {
				return startPosition;
			}

		};
	}

	private Set<String> _getAccountGroupNames(User user) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return SetUtil.fromList(
				TransformUtil.transform(
					_fdsDataProvider.getItems(
						_fdsKeywords, _createFDSPagination(0, 20),
						_mockHttpServletRequest, null),
					item -> ReflectionTestUtil.<String>invoke(
						item, "getName", new Class<?>[0])));
		}
	}

	private int _getItemsCount(User user) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			return _fdsDataProvider.getItemsCount(
				_fdsKeywords, _mockHttpServletRequest);
		}
	}

	private static final String _FDS_NAME =
		CPPortletKeys.CP_DEFINITIONS + "-productAccountGroups";

	private AccountGroup _accountGroup1;
	private AccountGroup _accountGroup2;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	private FDSDataProvider<?> _fdsDataProvider;

	@Inject
	private FDSDataProviderRegistry _fdsDataProviderRegistry;

	private FDSKeywords _fdsKeywords;

	@Inject
	private FDSKeywordsFactoryRegistry _fdsKeywordsFactoryRegistry;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

	@Inject
	private UserLocalService _userLocalService;

}