/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.constants.CommerceObjectActionExecutorConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.health.status.CommerceHealthStatusRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Crescenzo Rega
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CommerceHealthStatusTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_objectAction = _addObjectAction(
			"orderStatus = 10", RandomTestUtil.randomString());
	}

	@Test
	public void testFixIssue1() throws Exception {
		CommerceHealthStatus commerceHealthStatus =
			_commerceHealthStatusRegistry.getCommerceHealthStatus(
				CommerceHealthStatusConstants.
					SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_KEY);

		_objectActionLocalService.deleteObjectAction(_objectAction);

		Assert.assertFalse(
			commerceHealthStatus.isFixed(
				_user.getCompanyId(), _commerceChannel.getCommerceChannelId()));

		commerceHealthStatus.fixIssue(_getMockHttpServletRequest());

		Assert.assertTrue(
			commerceHealthStatus.isFixed(
				_user.getCompanyId(), _commerceChannel.getCommerceChannelId()));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _user.getCompanyId());

		List<ObjectAction> objectActions = ListUtil.filter(
			_objectActionLocalService.getObjectActions(
				objectDefinition.getObjectDefinitionId()),
			objectAction -> StringUtil.equals(
				objectAction.getObjectActionExecutorKey(),
				CommerceObjectActionExecutorConstants.
					KEY_SPLIT_COMMERCE_ORDER_BY_CATALOG));

		ObjectAction objectAction = objectActions.get(0);

		Assert.assertEquals(
			"orderStatus = 10", objectAction.getConditionExpression());
		Assert.assertEquals(
			"liferay/commerce_order_status",
			objectAction.getObjectActionTriggerKey());
		Assert.assertFalse(objectAction.isActive());
	}

	@Test
	public void testFixIssue2() throws Exception {
		CommerceHealthStatus commerceHealthStatus =
			_commerceHealthStatusRegistry.getCommerceHealthStatus(
				CommerceHealthStatusConstants.
					SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_KEY);

		Assert.assertTrue(
			commerceHealthStatus.isFixed(
				_user.getCompanyId(), _commerceChannel.getCommerceChannelId()));

		_objectAction.setObjectActionExecutorKey(
			ObjectActionExecutorConstants.KEY_NOTIFICATION);

		_objectAction = _objectActionLocalService.updateObjectAction(
			_objectAction);

		Assert.assertFalse(
			commerceHealthStatus.isFixed(
				_user.getCompanyId(), _commerceChannel.getCommerceChannelId()));

		commerceHealthStatus.fixIssue(_getMockHttpServletRequest());

		Assert.assertTrue(
			commerceHealthStatus.isFixed(
				_user.getCompanyId(), _commerceChannel.getCommerceChannelId()));
	}

	private ObjectAction _addObjectAction(
			String conditionExpression, String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_ORDER", _user.getCompanyId());

		return _objectActionLocalService.addObjectAction(
			null, _serviceContext.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, conditionExpression,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			externalReferenceCode,
			CommerceObjectActionExecutorConstants.
				KEY_SPLIT_COMMERCE_ORDER_BY_CATALOG,
			"liferay/commerce_order_status",
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", objectDefinition.getObjectDefinitionId()
			).build(),
			false);
	}

	private HttpServletRequest _getMockHttpServletRequest() throws Exception {
		HttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_user.getCompanyId()));

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, StringPool.SLASH);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockHttpServletRequest.setAttribute(WebKeys.USER_ID, _user.getUserId());

		return mockHttpServletRequest;
	}

	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceHealthStatusRegistry _commerceHealthStatusRegistry;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ObjectAction _objectAction;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}