/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.WishListResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class WishListResourceTest extends BaseWishListResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_accountEntry = AccountEntryLocalServiceUtil.addAccountEntry(
			StringPool.BLANK, _user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, 1,
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId()));

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());
	}

	@Override
	@Test
	public void testPatchWishList() throws Exception {
		WishList postWishList = testPatchWishList_addWishList();

		WishList randomPatchWishList = randomPatchWishList();

		wishListResource.patchWishList(
			postWishList.getId(), null, randomPatchWishList);

		WishList expectedPatchWishList = postWishList.clone();

		BeanTestUtil.copyProperties(randomPatchWishList, expectedPatchWishList);

		WishList getWishList = wishListResource.getWishList(
			postWishList.getId());

		assertEquals(expectedPatchWishList, getWishList);
		assertValid(getWishList);

		_testPatchWishListWithWishListItems(postWishList);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"defaultWishList", "name"};
	}

	@Override
	protected WishList randomWishList() throws Exception {
		return new WishList() {
			{
				defaultWishList = false;
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected WishList testDeleteWishList_addWishList() throws Exception {
		return _postChannelWishList(randomWishList());
	}

	@Override
	protected WishList
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				String externalReferenceCode, WishList wishList)
		throws Exception {

		return _postChannelWishList(wishList);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeWishListsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected WishList testGetChannelWishListsPage_addWishList(
			Long channelId, WishList wishList)
		throws Exception {

		return wishListResource.postChannelWishList(
			channelId, _accountEntry.getAccountEntryId(), wishList);
	}

	@Override
	protected Long testGetChannelWishListsPage_getChannelId() throws Exception {
		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected WishList testGetWishList_addWishList() throws Exception {
		return _postChannelWishList(randomWishList());
	}

	@Override
	protected WishList testGraphQLWishList_addWishList() throws Exception {
		return _postChannelWishList(randomWishList());
	}

	@Override
	protected WishList testPatchWishList_addWishList() throws Exception {
		return _postChannelWishList(randomWishList());
	}

	@Override
	protected WishList
			testPostChannelByExternalReferenceCodeWishList_addWishList(
				WishList wishList)
		throws Exception {

		return _postChannelWishList(wishList);
	}

	@Override
	protected WishList testPostChannelWishList_addWishList(WishList wishList)
		throws Exception {

		return _postChannelWishList(wishList);
	}

	private WishList _postChannelWishList(WishList wishList) throws Exception {
		return wishListResource.postChannelWishList(
			_commerceChannel.getCommerceChannelId(),
			_accountEntry.getAccountEntryId(), wishList);
	}

	private WishListItem _randomWishListItem() throws Exception {
		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.ONE);

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CProduct commerceProduct = cpDefinition.getCProduct();

		return new WishListItem() {
			{
				productId = commerceProduct.getCProductId();
				skuId = cpInstance.getCPInstanceId();
			}
		};
	}

	private void _testPatchWishListWithWishListItems(WishList wishList)
		throws Exception {

		User omniadminUser = UserTestUtil.addOmniadminUser();

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		WishListResource wishListResource = WishListResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "wishListItems"
		).build();

		wishList.setWishListItems(new WishListItem[] {_randomWishListItem()});

		WishList patchWishList = wishListResource.patchWishList(
			wishList.getId(), _accountEntry.getAccountEntryId(), wishList);

		WishListItem[] wishListItems = patchWishList.getWishListItems();

		Assert.assertTrue(wishListItems.length == 1);
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}