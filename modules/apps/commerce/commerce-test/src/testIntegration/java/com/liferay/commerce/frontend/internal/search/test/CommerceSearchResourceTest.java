/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.search.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.role.AccountRolePermissionThreadLocal;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
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
public class CommerceSearchResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		for (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper :
					_groupConfigurationTemporarySwappers) {

			groupConfigurationTemporarySwapper.close();
		}
	}

	@Test
	public void testGetWithAccountGroupFilterEnabled() throws Exception {
		User user1 = UserTestUtil.addUser();

		AccountEntry accountEntry1 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {user1.getUserId()},
				null, _serviceContext);

		AccountGroup accountGroup =
			CommerceAccountTestUtil.addAccountGroupAndAccountRel(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
				accountEntry1.getAccountEntryId(), _serviceContext);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableAccountGroupFilter(accountGroup, cpDefinition);

		CommerceChannel commerceChannel = _addCommerceChannel();

		Assert.assertEquals(
			Collections.singletonList(HtmlUtil.escape(cpDefinition.getName())),
			_getTitles(cpDefinition, commerceChannel.getSiteGroupId(), user1));

		User user2 = UserTestUtil.addUser();

		AccountEntry accountEntry2 =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {user2.getUserId()},
				null, _serviceContext);

		CommerceAccountTestUtil.addAccountGroupAndAccountRel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
			accountEntry2.getAccountEntryId(), _serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition, commerceChannel.getSiteGroupId(), user2));

		User user3 = UserTestUtil.addUser();

		CommerceAccountTestUtil.addBusinessAccountEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {user3.getUserId()}, null,
			_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition, commerceChannel.getSiteGroupId(), user3));

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition, commerceChannel.getSiteGroupId(), null));
	}

	@Test
	public void testGetWithChannelFilterEnabled() throws Exception {
		CommerceChannel commerceChannel = _addCommerceChannel();

		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(commerceChannel, cpDefinition1);

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition2, cpDefinition2.getDefaultLanguageId(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, cpDefinition1.getName(), StringPool.BLANK);

		_enableChannelFilter(_addCommerceChannel(), cpDefinition2);

		Assert.assertEquals(
			Collections.singletonList(HtmlUtil.escape(cpDefinition1.getName())),
			_getTitles(cpDefinition1, commerceChannel.getSiteGroupId(), null));

		User user1 = UserTestUtil.addUser();

		_addEligibleAccountEntry(
			CommerceAccountTestUtil.addBusinessAccountEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), new long[] {user1.getUserId()},
				null, _serviceContext),
			commerceChannel);

		Assert.assertEquals(
			Collections.singletonList(HtmlUtil.escape(cpDefinition1.getName())),
			_getTitles(cpDefinition1, commerceChannel.getSiteGroupId(), user1));

		User user2 = UserTestUtil.addUser();

		CommerceAccountTestUtil.addBusinessAccountEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), new long[] {user2.getUserId()}, null,
			_serviceContext);

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition1, commerceChannel.getSiteGroupId(), user2));

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition1, commerceChannel.getSiteGroupId(), null));
	}

	@Test
	public void testGetWithoutCommerceChannel() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_enableChannelFilter(_addCommerceChannel(), cpDefinition);

		Group group = GroupTestUtil.addGroup();

		Assert.assertEquals(
			Collections.emptyList(),
			_getTitles(cpDefinition, group.getGroupId(), null));
	}

	private CommerceChannel _addCommerceChannel() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), null);

		_groupConfigurationTemporarySwappers.add(
			new GroupConfigurationTemporarySwapper(
				commerceChannel.getGroupId(),
				CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT,
				HashMapDictionaryBuilder.<String, Object>put(
					"commerceSiteType", CommerceChannelConstants.SITE_TYPE_B2B
				).build()));

		return commerceChannel;
	}

	private void _addEligibleAccountEntry(
			AccountEntry accountEntry, CommerceChannel commerceChannel)
		throws Exception {

		_commerceChannelAccountEntryRelLocalService.
			addCommerceChannelAccountEntryRel(
				TestPropsValues.getUserId(), accountEntry.getAccountEntryId(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				commerceChannel.getCommerceChannelId(), true, 0,
				CommerceChannelAccountEntryRelConstants.TYPE_ELIGIBILITY);
	}

	private void _enableAccountGroupFilter(
			AccountGroup accountGroup, CPDefinition cpDefinition)
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), CPDefinition.class.getName(),
			cpDefinition.getCPDefinitionId());

		cpDefinition.setAccountGroupFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private void _enableChannelFilter(
			CommerceChannel commerceChannel, CPDefinition cpDefinition)
		throws Exception {

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
			commerceChannel.getCommerceChannelId(), _serviceContext);

		cpDefinition.setChannelFilterEnabled(true);

		_cpDefinitionLocalService.updateCPDefinition(cpDefinition);
	}

	private List<String> _getTitles(
			CPDefinition cpDefinition, long groupId, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPortalURL(_company.getPortalURL(groupId));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		themeDisplay.setSiteGroupId(groupId);

		if (user != null) {
			mockHttpServletRequest.setAttribute(
				WebKeys.USER_ID, user.getUserId());

			themeDisplay.setUser(user);
		}

		try (SafeCloseable safeCloseable =
				AccountRolePermissionThreadLocal.
					setAccountEntryIdWithSafeCloseable(0)) {

			Layout layout = LayoutTestUtil.addTypePortletLayout(groupId);

			Response response = ReflectionTestUtil.invoke(
				_commerceSearchResource, "get",
				new Class<?>[] {
					long.class, String.class, ThemeDisplay.class,
					HttpServletRequest.class
				},
				layout.getPlid(), cpDefinition.getName(), themeDisplay,
				mockHttpServletRequest);

			Assert.assertEquals(
				Response.Status.OK.getStatusCode(), response.getStatus());

			JSONArray jsonArray = _jsonFactory.createJSONArray(
				(String)response.getEntity());

			return JSONUtil.toList(
				jsonArray,
				jsonObject -> {
					if (!Objects.equals(jsonObject.getString("type"), "item")) {
						return null;
					}

					return jsonObject.getString("title");
				});
		}
	}

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.frontend.internal.search.CommerceSearchResource",
		type = Inject.NoType.class
	)
	private Object _commerceSearchResource;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private final List<GroupConfigurationTemporarySwapper>
		_groupConfigurationTemporarySwappers = new ArrayList<>();

	@Inject
	private JSONFactory _jsonFactory;

	private ServiceContext _serviceContext;

}