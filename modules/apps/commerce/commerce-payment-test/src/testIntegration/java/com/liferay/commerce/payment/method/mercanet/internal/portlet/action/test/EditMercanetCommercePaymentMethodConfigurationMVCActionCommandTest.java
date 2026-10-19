/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class
	EditMercanetCommercePaymentMethodConfigurationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		String merchantId = RandomTestUtil.randomString();
		String secretKey = RandomTestUtil.randomString();
		User user = TestPropsValues.getUser();

		_processAction(commerceChannel, merchantId, secretKey, user);

		_assertSettings(commerceChannel, merchantId, secretKey);

		merchantId = RandomTestUtil.randomString();

		_processAction(
			commerceChannel, merchantId, Portal.TEMP_OBFUSCATION_VALUE, user);

		_assertSettings(commerceChannel, merchantId, secretKey);

		secretKey = RandomTestUtil.randomString();

		_processAction(commerceChannel, merchantId, secretKey, user);

		_assertSettings(commerceChannel, merchantId, secretKey);

		try {
			_processAction(
				commerceChannel, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), UserTestUtil.addUser());

			Assert.fail();
		}
		catch (PortletException portletException) {
			Assert.assertTrue(
				portletException.getCause() instanceof PrincipalException);
		}

		_assertSettings(commerceChannel, merchantId, secretKey);
	}

	private void _assertSettings(
			CommerceChannel commerceChannel, String merchantId,
			String secretKey)
		throws Exception {

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				commerceChannel.getGroupId(),
				"com.liferay.commerce.payment.method.mercanet"));

		Assert.assertEquals("test", settings.getValue("environment", null));
		Assert.assertEquals("1", settings.getValue("keyVersion", null));
		Assert.assertEquals(merchantId, settings.getValue("merchantId", null));
		Assert.assertEquals(secretKey, settings.getValue("secretKey", null));
	}

	private void _processAction(
			CommerceChannel commerceChannel, String merchantId,
			String secretKey, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			Constants.CMD, Constants.UPDATE);
		mockLiferayPortletActionRequest.setParameter(
			"commerceChannelId",
			String.valueOf(commerceChannel.getCommerceChannelId()));
		mockLiferayPortletActionRequest.setParameter(
			"settings--environment--", "test");
		mockLiferayPortletActionRequest.setParameter(
			"settings--keyVersion--", "1");
		mockLiferayPortletActionRequest.setParameter(
			"settings--merchantId--", merchantId);
		mockLiferayPortletActionRequest.setParameter(
			"settings--secretKey--", secretKey);
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
	}

	@Inject(
		filter = "mvc.command.name=/commerce_payment_methods/edit_mercanet_commerce_payment_method_configuration",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

}