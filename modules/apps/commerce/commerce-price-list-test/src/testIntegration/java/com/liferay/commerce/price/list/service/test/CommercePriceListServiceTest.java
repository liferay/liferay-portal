/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class CommercePriceListServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_commerceCatalog = _addCommerceCatalog();

		_commercePriceList = CommercePriceListTestUtil.addCommercePriceList(
			_commerceCatalog.getGroupId(), false,
			CommercePriceListConstants.TYPE_PRICE_LIST,
			RandomTestUtil.randomDouble());

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommercePriceList.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commercePriceList.getCommercePriceListId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testSetCatalogBasePriceList() throws Exception {
		CommerceCatalog commerceCatalog = _addCommerceCatalog();

		CommercePriceList commercePriceList1 =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PRICE_LIST);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commercePriceListService.setCatalogBasePriceList(
				commerceCatalog.getGroupId(),
				_commercePriceList.getCommercePriceListId(),
				CommercePriceListConstants.TYPE_PRICE_LIST);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					StringBundler.concat(
						"User ", _user.getUserId(), " must have ",
						ActionKeys.UPDATE, " permission for")));
		}

		_assertCatalogBaseCommercePriceList(
			commercePriceList1, commerceCatalog.getGroupId(),
			CommercePriceListConstants.TYPE_PRICE_LIST);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchPriceListException.class,
				() -> _commercePriceListService.setCatalogBasePriceList(
					commerceCatalog.getGroupId(),
					_commercePriceList.getCommercePriceListId(),
					CommercePriceListConstants.TYPE_PRICE_LIST));
		}

		_assertCatalogBaseCommercePriceList(
			commercePriceList1, commerceCatalog.getGroupId(),
			CommercePriceListConstants.TYPE_PRICE_LIST);

		CommercePriceList commercePriceList2 =
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					_commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PROMOTION);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchPriceListException.class,
				() -> _commercePriceListService.setCatalogBasePriceList(
					_commerceCatalog.getGroupId(),
					_commercePriceList.getCommercePriceListId(),
					CommercePriceListConstants.TYPE_PROMOTION));
		}

		_assertCatalogBaseCommercePriceList(
			commercePriceList2, _commerceCatalog.getGroupId(),
			CommercePriceListConstants.TYPE_PROMOTION);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commercePriceListService.setCatalogBasePriceList(
				_commerceCatalog.getGroupId(),
				_commercePriceList.getCommercePriceListId(),
				CommercePriceListConstants.TYPE_PRICE_LIST);
		}

		Assert.assertEquals(
			_commercePriceList,
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(
					_commerceCatalog.getGroupId(),
					CommercePriceListConstants.TYPE_PRICE_LIST));
	}

	private CommerceCatalog _addCommerceCatalog() throws Exception {
		return _commerceCatalogLocalService.addCommerceCatalog(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), "USD", "en_US", false,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertCatalogBaseCommercePriceList(
			CommercePriceList expectedCommercePriceList, long groupId,
			String type)
		throws Exception {

		Assert.assertEquals(
			expectedCommercePriceList,
			_commercePriceListLocalService.
				fetchCatalogBaseCommercePriceListByType(groupId, type));

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				_commercePriceList.getCommercePriceListId());

		Assert.assertFalse(commercePriceList.isCatalogBasePriceList());
	}

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommercePriceListService _commercePriceListService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}