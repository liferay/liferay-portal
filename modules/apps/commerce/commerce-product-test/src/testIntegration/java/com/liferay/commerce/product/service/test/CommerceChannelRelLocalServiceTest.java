/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CommerceChannelRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		PrincipalThreadLocal.setName(_user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_group = GroupTestUtil.addGroup(
			_user.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_user.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _group.getGroupId(), _user.getUserId());

		_commerceChannel1 = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_commerceChannel2 = _commerceChannelLocalService.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Channel Test",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
	}

	@After
	public void tearDown() throws Exception {
		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			_commerceChannel1.getCommerceChannelId());
		_commerceChannelRelLocalService.deleteCommerceChannelRels(
			_commerceChannel2.getCommerceChannelId());
	}

	@Test
	public void testAddProductVisibility() throws Exception {
		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), _cpDefinition.getCPDefinitionId(),
			_commerceChannel1.getCommerceChannelId(), _serviceContext);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CPDefinition.class.getName(), _cpDefinition.getCPDefinitionId(),
			_commerceChannel2.getCommerceChannelId(), _serviceContext);

		List<CommerceChannelRel> commerceChannelRels =
			_commerceChannelRelLocalService.getCommerceChannelRels(
				CPDefinition.class.getName(), _cpDefinition.getCPDefinitionId(),
				"Channel Test", QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceChannelRels.toString(), 2, commerceChannelRels.size());
		Assert.assertEquals(
			commerceChannelRels.toString(), 2,
			_commerceChannelRelLocalService.getCommerceChannelRelsCount(
				CPDefinition.class.getName(), _cpDefinition.getCPDefinitionId(),
				"Channel Test"));
	}

	@Test
	public void testCommerceChannelCommerceCurrencyVisibility()
		throws Exception {

		List<CommerceCurrency> commerceCurrencies =
			_commerceCurrencyLocalService.getCommerceCurrencies(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		int count =
			_commerceChannelRelLocalService.
				getCommerceCurrencyCommerceChannelRelsCount(
					_commerceChannel1.getCommerceChannelId(), StringPool.BLANK);

		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelLocalService.addCommerceChannelRel(
				CommerceCurrency.class.getName(),
				commerceCurrencies.get(
					1
				).getCommerceCurrencyId(),
				_commerceChannel1.getCommerceChannelId(), _serviceContext);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			CommerceCurrency.class.getName(),
			commerceCurrencies.get(
				2
			).getCommerceCurrencyId(),
			_commerceChannel1.getCommerceChannelId(), _serviceContext);

		Assert.assertEquals(
			_commerceChannelRelLocalService.
				getCommerceCurrencyCommerceChannelRelsCount(
					_commerceChannel1.getCommerceChannelId(), StringPool.BLANK),
			count + 2);

		_commerceChannelRelLocalService.deleteCommerceChannelRel(
			commerceChannelRel.getCommerceChannelRelId());

		Assert.assertEquals(
			_commerceChannelRelLocalService.
				getCommerceCurrencyCommerceChannelRelsCount(
					_commerceChannel1.getCommerceChannelId(), StringPool.BLANK),
			count + 1);
	}

	@Test
	public void testCommerceChannelCountryVisibility() throws Exception {
		Country country1 = _countryLocalService.getCountryByA2(
			_user.getCompanyId(), "GB");
		Country country2 = _countryLocalService.getCountryByA2(
			_user.getCompanyId(), "US");

		int count =
			_commerceChannelRelLocalService.getCountryCommerceChannelRelsCount(
				_commerceChannel1.getCommerceChannelId(), StringPool.BLANK);

		CommerceChannelRel commerceChannelRel =
			_commerceChannelRelLocalService.addCommerceChannelRel(
				Country.class.getName(), country1.getCountryId(),
				_commerceChannel1.getCommerceChannelId(), _serviceContext);

		_commerceChannelRelLocalService.addCommerceChannelRel(
			Country.class.getName(), country2.getCountryId(),
			_commerceChannel1.getCommerceChannelId(), _serviceContext);

		Assert.assertEquals(
			_commerceChannelRelLocalService.getCountryCommerceChannelRelsCount(
				_commerceChannel1.getCommerceChannelId(), StringPool.BLANK),
			count + 2);

		_commerceChannelRelLocalService.deleteCommerceChannelRel(
			commerceChannelRel.getCommerceChannelRelId());

		Assert.assertEquals(
			_commerceChannelRelLocalService.getCountryCommerceChannelRelsCount(
				_commerceChannel1.getCommerceChannelId(), StringPool.BLANK),
			count + 1);
	}

	private static User _user;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommerceChannel _commerceChannel1;
	private CommerceChannel _commerceChannel2;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CountryLocalService _countryLocalService;

	private CPDefinition _cpDefinition;
	private Group _group;
	private ServiceContext _serviceContext;

}