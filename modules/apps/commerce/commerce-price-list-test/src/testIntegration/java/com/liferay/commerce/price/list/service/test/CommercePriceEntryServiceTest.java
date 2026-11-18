/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.test.util.price.list.CommercePriceEntryTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
public class CommercePriceEntryServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_commercePriceEntry = CommercePriceEntryTestUtil.addCommercePriceEntry(
			group.getGroupId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommercePriceList.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commercePriceEntry.getCommercePriceListId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCommercePriceEntry() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			BigDecimal price = BigDecimal.valueOf(RandomTestUtil.randomInt());

			CommercePriceEntry commercePriceEntry =
				_commercePriceEntryService.addOrUpdateCommercePriceEntry(
					null, _commercePriceEntry.getCommercePriceEntryId(), 0,
					null, _commercePriceEntry.getCommercePriceListId(), price,
					null, null, null,
					ServiceContextTestUtil.getServiceContext());

			Assert.assertTrue(
				BigDecimalUtil.eq(price, commercePriceEntry.getPrice()));
		}

		Group group = GroupTestUtil.addGroup();

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				group.getGroupId());

		_testAddOrUpdateCommercePriceEntry(
			commercePriceEntry, 0,
			commercePriceEntry.getExternalReferenceCode());
		_testAddOrUpdateCommercePriceEntry(
			commercePriceEntry, commercePriceEntry.getCommercePriceEntryId(),
			null);
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private void _testAddOrUpdateCommercePriceEntry(
			CommercePriceEntry commercePriceEntry, long commercePriceEntryId,
			String externalReferenceCode)
		throws Exception {

		BigDecimal price = BigDecimal.valueOf(RandomTestUtil.randomInt());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commercePriceEntryService.addOrUpdateCommercePriceEntry(
				externalReferenceCode, commercePriceEntryId, 0, null,
				_commercePriceEntry.getCommercePriceListId(), price, null, null,
				null, ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		Calendar calendar = new GregorianCalendar();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commercePriceEntryService.addOrUpdateCommercePriceEntry(
				externalReferenceCode, commercePriceEntryId, 0, null,
				_commercePriceEntry.getCommercePriceListId(), true, null, null,
				null, null, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, price,
				false, null, null, ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		CommercePriceEntry actualCommercePriceEntry =
			_commercePriceEntryLocalService.getCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				commercePriceEntry.getPrice(),
				actualCommercePriceEntry.getPrice()));
	}

	private CommercePriceEntry _commercePriceEntry;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceEntryService _commercePriceEntryService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}