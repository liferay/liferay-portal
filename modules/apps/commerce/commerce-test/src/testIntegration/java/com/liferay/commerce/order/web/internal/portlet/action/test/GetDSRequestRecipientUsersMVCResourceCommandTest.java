/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class GetDSRequestRecipientUsersMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl",
			GetDSRequestRecipientUsersMVCResourceCommandTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition"
			});

		_saveDigitalSignatureConfiguration(true);

		_group = GroupTestUtil.addGroup();

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addPersonAccountEntry(
				TestPropsValues.getUserId(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			accountEntry.getAccountEntryId(), commerceCurrency.getCode(), 0);
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testServeResource() throws Exception {
		User user = UserTestUtil.addUser();

		PortletException portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				_commerceOrder.getCommerceOrderId(), user.getScreenName(),
				user));

		Assert.assertTrue(
			portletException.getCause() instanceof
				PrincipalException.MustHavePermission);

		_saveDigitalSignatureConfiguration(false);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_serveResource(
				_commerceOrder.getCommerceOrderId(), user.getScreenName(),
				TestPropsValues.getUser()));

		Assert.assertEquals(
			"Digital signature is not enabled for this order.",
			jsonObject.getString("errorMessage"));

		_saveDigitalSignatureConfiguration(true);

		portletException = Assert.assertThrows(
			PortletException.class,
			() -> _serveResource(
				RandomTestUtil.randomLong(), user.getScreenName(),
				TestPropsValues.getUser()));

		Assert.assertTrue(
			portletException.getCause() instanceof PrincipalException);

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_serveResource(
				_commerceOrder.getCommerceOrderId(), user.getScreenName(),
				TestPropsValues.getUser()));

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			user.getEmailAddress(), jsonObject.getString("emailAddress"));
		Assert.assertEquals(user.getFullName(), jsonObject.getString("name"));
		Assert.assertEquals(user.getUserId(), jsonObject.getLong("userId"));

		_testServeResourceWithoutUserViewPermission();
	}

	private void _saveDigitalSignatureConfiguration(boolean enabled)
		throws Exception {

		if (_companyConfigurationTemporarySwapper != null) {
			_companyConfigurationTemporarySwapper.close();
		}

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				DigitalSignatureConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"accountBaseURI", "https://demo.docusign.net/restapi"
				).put(
					"apiAccountId", RandomTestUtil.randomString()
				).put(
					"apiUsername", RandomTestUtil.randomString()
				).put(
					"embeddedViewEnabled", true
				).put(
					"enabled", enabled
				).put(
					"environment", "sandbox"
				).put(
					"integrationKey", RandomTestUtil.randomString()
				).put(
					"rsaPrivateKey", RandomTestUtil.randomString()
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());
	}

	private String _serveResource(
			long commerceOrderId, String keywords, User user)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.addParameter(
			"commerceOrderId", String.valueOf(commerceOrderId));
		mockLiferayResourceRequest.addParameter("keywords", keywords);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return byteArrayOutputStream.toString();
	}

	private void _testServeResourceWithoutUserViewPermission()
		throws Exception {

		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		User user1 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user1.getUserId(), role);

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountEntryId(
				accountEntry.getAccountEntryId());

		User user2 = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user2.getUserId());

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			_serveResource(
				_commerceOrder.getCommerceOrderId(), user2.getScreenName(),
				user1));

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(user2.getUserId(), jsonObject.getLong("userId"));

		User user3 = UserTestUtil.addUser();

		jsonArray = _jsonFactory.createJSONArray(
			_serveResource(
				_commerceOrder.getCommerceOrderId(), user3.getScreenName(),
				user1));

		Assert.assertEquals(jsonArray.toString(), 0, jsonArray.length());
	}

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(
		filter = "mvc.command.name=/commerce_order/get_ds_request_recipient_users"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}