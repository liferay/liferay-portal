/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.punchout.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.punchout.constants.PunchOutConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.punchout.dto.v1_0.Cart;
import com.liferay.headless.commerce.punchout.dto.v1_0.Group;
import com.liferay.headless.commerce.punchout.dto.v1_0.PunchOutSession;
import com.liferay.headless.commerce.punchout.dto.v1_0.User;
import com.liferay.headless.commerce.punchout.resource.v1_0.PunchOutSessionResource;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.BadRequestException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class PunchOutSessionResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser(_group.getGroupId());

		_accountEntry = CommerceAccountTestUtil.addBusinessAccountEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new GroupServiceSettingsLocator(
				_commerceChannel.getGroupId(), PunchOutConstants.SERVICE_NAME));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue("enabled", Boolean.TRUE.toString());
		modifiableSettings.setValue("punchOutStartURL", _PUNCH_OUT_START_URL);

		modifiableSettings.store();

		Role role = _roleLocalService.fetchRole(
			_group.getCompanyId(),
			PunchOutConstants.ROLE_NAME_ACCOUNT_PUNCH_OUT);

		if (role == null) {
			_role = RoleTestUtil.addRole(
				PunchOutConstants.ROLE_NAME_ACCOUNT_PUNCH_OUT,
				RoleConstants.TYPE_ACCOUNT);
		}
	}

	@Test
	public void testPostPunchOutSessionRequest() throws Exception {
		PunchOutSessionResource.Builder builder =
			_punchOutSessionResourceFactory.create();

		PunchOutSessionResource punchOutSessionResource = builder.user(
			_user
		).build();

		Assert.assertThrows(
			BadRequestException.class,
			() -> punchOutSessionResource.postPunchOutSessionRequest(
				_getPunchOutSession(
					"http://127.0.0.1/" + RandomTestUtil.randomString())));

		PunchOutSession punchOutSession =
			punchOutSessionResource.postPunchOutSessionRequest(
				_getPunchOutSession(
					"https://8.8.8.8/" + RandomTestUtil.randomString()));

		Assert.assertTrue(
			StringUtil.startsWith(
				punchOutSession.getPunchOutStartURL(), _PUNCH_OUT_START_URL));
	}

	private PunchOutSession _getPunchOutSession(String punchOutReturnURL) {
		PunchOutSession punchOutSession = new PunchOutSession();

		punchOutSession.setBuyerAccountReferenceCode(
			_accountEntry::getExternalReferenceCode);
		punchOutSession.setBuyerGroup(
			() -> {
				Group group = new Group();

				group.setName(_group::getGroupKey);

				return group;
			});
		punchOutSession.setBuyerUser(
			() -> {
				User user = new User();

				user.setEmail(_user::getEmailAddress);
				user.setFirstName(_user::getFirstName);
				user.setLastName(_user::getLastName);

				return user;
			});
		punchOutSession.setCart(
			() -> {
				Cart cart = new Cart();

				cart.setCurrencyCode(_commerceCurrency::getCode);

				return cart;
			});
		punchOutSession.setPunchOutReturnURL(() -> punchOutReturnURL);
		punchOutSession.setPunchOutSessionType(RandomTestUtil::randomString);

		return punchOutSession;
	}

	private static final String _PUNCH_OUT_START_URL =
		"https://example.com/start";

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private com.liferay.portal.kernel.model.Group _group;

	@Inject
	private PunchOutSessionResource.Factory _punchOutSessionResourceFactory;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private com.liferay.portal.kernel.model.User _user;

}