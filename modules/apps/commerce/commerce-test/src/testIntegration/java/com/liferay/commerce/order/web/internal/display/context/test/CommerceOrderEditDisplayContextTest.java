/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.vies.configuration.VIESAccountEntryValidatorConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.test.util.AccountEntryValidatorResultTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.validator.TestAccountEntryValidator;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
@Sync
public class CommerceOrderEditDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				_group.getCompanyId(),
				VIESAccountEntryValidatorConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"enabled", "false"
				).build());

		_user = UserTestUtil.addUser();

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
			AccountEntryValidatorResultTestUtil.getOrAddObjectDefinition(
				CommerceOrderEditDisplayContextTest.class);

		Bundle bundle = FrameworkUtil.getBundle(
			CommerceOrderEditDisplayContextTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AccountEntryValidator.class, _testAccountEntryValidator,
			HashMapDictionaryBuilder.<String, Object>put(
				"account.entry.validator.key", RandomTestUtil.randomString()
			).build());
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		if (_companyConfigurationTemporarySwapper != null) {
			_companyConfigurationTemporarySwapper.close();
		}
	}

	@FeatureFlag("LPD-89850")
	@Test
	@TestInfo("LPD-99093")
	public void testGetValidationButtonCssClass() throws Exception {
		Assert.assertEquals("text-secondary", _getValidationButtonCssClass());

		_testAccountEntryValidator.setSkipped(true);

		Assert.assertEquals("text-info", _getValidationButtonCssClass());

		_testAccountEntryValidator.setSkipped(false);

		ObjectEntry objectEntry = _addObjectEntry(
			AccountEntryValidatorConstants.RESULT_SUCCESS);

		Assert.assertEquals("text-success", _getValidationButtonCssClass());

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		objectEntry = _addObjectEntry(
			AccountEntryValidatorConstants.RESULT_FAILURE);

		Assert.assertEquals("text-danger", _getValidationButtonCssClass());

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@FeatureFlag("LPD-89850")
	@Test
	@TestInfo("LPD-99364")
	public void testIsValidationButtonVisible() throws Exception {
		Role role = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_REGULAR, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		User user = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user.getUserId(), role.getRoleId());

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS});

		Assert.assertFalse(_isValidationButtonVisible(user));

		_resourcePermissionLocalService.setResourcePermissions(
			_commerceOrder.getCompanyId(), CommerceOrderConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry.getAccountEntryGroupId()),
			role.getRoleId(),
			new String[] {
				CommerceOrderActionKeys.MANAGE_ACCOUNTS_SCOPED_COMMERCE_ORDERS,
				CommerceOrderActionKeys.MANAGE_COMMERCE_ORDERS
			});

		Assert.assertTrue(_isValidationButtonVisible(user));
	}

	private ObjectEntry _addObjectEntry(String resultStatus) throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			0, _user.getUserId(), _objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"className", TestAccountEntryValidator.class.getName()
			).put(
				"classPK",
				_testAccountEntryValidator.getClassPK(_accountEntry, null)
			).put(
				"r_accountToAccountValidatorResults_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"resultStatus", resultStatus
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));
	}

	private Object _getCommerceOrderEditDisplayContext(
			PermissionChecker permissionChecker, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"commerceOrderId",
			String.valueOf(_commerceOrder.getCommerceOrderId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT);
	}

	private String _getValidationButtonCssClass() throws Exception {
		return ReflectionTestUtil.invoke(
			_getCommerceOrderEditDisplayContext(
				PermissionThreadLocal.getPermissionChecker(), _user),
			"getValidationButtonCssClass", new Class<?>[0]);
	}

	private boolean _isValidationButtonVisible(User user) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			return ReflectionTestUtil.invoke(
				_getCommerceOrderEditDisplayContext(permissionChecker, user),
				"isValidationButtonVisible", new Class<?>[0]);
		}
	}

	private AccountEntry _accountEntry;
	private CommerceOrder _commerceOrder;
	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject(
		filter = "mvc.command.name=/commerce_order/edit_commerce_order",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceRegistration<AccountEntryValidator> _serviceRegistration;
	private final TestAccountEntryValidator _testAccountEntryValidator =
		new TestAccountEntryValidator();
	private User _user;

}