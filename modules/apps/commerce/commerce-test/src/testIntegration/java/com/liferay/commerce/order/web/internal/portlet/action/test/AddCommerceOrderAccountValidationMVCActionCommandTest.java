/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action.test;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
@Sync
public class AddCommerceOrderAccountValidationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com", serviceContext);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannelLocalServiceUtil.addCommerceChannel(
			null, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(), "Test Channel",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrency.getCode(), serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			_group.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			commerceCurrency.getCommerceCurrencyId());

		_objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_ACCOUNT_VALIDATOR_RESULT", _accountEntry.getCompanyId());
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@FeatureFlag("LPD-89850")
	@Test
	@TestInfo("LPD-89854")
	public void testProcessAction() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				"accountValidationsAlreadySucceeded"));

		TestAccountEntryValidator testAccountEntryValidator =
			new TestAccountEntryValidator();

		BundleContext bundleContext = FrameworkUtil.getBundle(
			AddCommerceOrderAccountValidationMVCActionCommandTest.class
		).getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AccountEntryValidator.class, testAccountEntryValidator,
			HashMapDictionaryBuilder.<String, Object>put(
				"account.entry.validator.key", RandomTestUtil.randomString()
			).build());

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertFalse(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				"accountValidationsAlreadySucceeded"));

		Assert.assertEquals(1, _getEntriesPKWithManualResult().size());

		JSONObject jsonObject = testAccountEntryValidator.getJSONObject();

		Assert.assertEquals(
			_commerceOrder.getBillingAddressId(),
			jsonObject.getLong("billingAddressId"));
		Assert.assertEquals(
			_commerceOrder.getCommerceOrderId(),
			jsonObject.getLong("commerceOrderId"));
		Assert.assertEquals(
			_commerceOrder.getShippingAddressId(),
			jsonObject.getLong("shippingAddressId"));

		for (long objectEntryId : _getEntriesPKWithManualResult()) {
			_objectEntryLocalService.deleteObjectEntry(objectEntryId);
		}

		_objectEntryLocalService.addObjectEntry(
			0, _user.getUserId(), _objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"className",
				testAccountEntryValidator.getClass(
				).getName()
			).put(
				"classPK",
				testAccountEntryValidator.getClassPK(_accountEntry, null)
			).put(
				"r_accountToAccountValidatorResults_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"resultStatus", AccountEntryValidatorConstants.RESULT_SUCCESS
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				"accountValidationsAlreadySucceeded"));

		Assert.assertEquals(0, _getEntriesPKWithManualResult().size());
	}

	private List<Long> _getEntriesPKWithManualResult() throws Exception {
		String filterString = StringBundler.concat(
			"(resultStatus eq '", AccountEntryValidatorConstants.RESULT_MANUAL,
			"') and (r_accountToAccountValidatorResults_accountEntryId eq '",
			_accountEntry.getAccountEntryId(), "')");

		return _objectEntryLocalService.getPrimaryKeys(
			new Long[] {0L}, _accountEntry.getCompanyId(),
			_accountEntry.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			_filterFactory.create(filterString, _objectDefinition), false, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"commerceOrderId",
			String.valueOf(_commerceOrder.getCommerceOrderId()));
		mockLiferayPortletActionRequest.setParameter(
			"validationMessage", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private AccountEntry _accountEntry;
	private CommerceOrder _commerceOrder;
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "filter.factory.key=default", type = FilterFactory.class)
	private FilterFactory<Predicate> _filterFactory;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/commerce_order/add_commerce_order_account_validation",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ServiceRegistration<AccountEntryValidator> _serviceRegistration;
	private User _user;

	private static class TestAccountEntryValidator
		implements AccountEntryValidator {

		@Override
		public AccountEntryValidatorConfiguration
				getAccountEntryValidatorConfiguration(long companyId)
			throws ConfigurationException {

			return new AccountEntryValidatorConfiguration() {

				@Override
				public int checkInterval() {
					return 0;
				}

				@Override
				public boolean enabled() {
					return true;
				}

			};
		}

		public JSONObject getJSONObject() {
			return _jsonObject;
		}

		@Override
		public String getClassPK(
			AccountEntry accountEntry, JSONObject jsonObject) {

			_jsonObject = jsonObject;

			return _key;
		}

		@Override
		public AccountEntryValidatorResult validate(
			AccountEntry accountEntry, JSONObject jsonObject) {

			_jsonObject = jsonObject;

			AccountEntryValidatorResult.Builder
				accountEntryValidatorResultBuilder =
					AccountEntryValidatorResult.builder(_key);

			return accountEntryValidatorResultBuilder.build();
		}

		private JSONObject _jsonObject;
		private final String _key = RandomTestUtil.randomString();

	}

}