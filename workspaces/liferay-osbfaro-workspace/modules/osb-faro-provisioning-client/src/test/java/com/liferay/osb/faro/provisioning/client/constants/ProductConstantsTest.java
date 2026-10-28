/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.constants;

import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rachael Koestartyo
 */
public class ProductConstantsTest {

	@After
	public void tearDown() {
		_setSubscriptionPushEnabled(false);
	}

	@Test
	public void testGetBaseProductEntryIds() {
		List<String> baseProductEntryIds =
			ProductConstants.getBaseProductEntryIds();

		Assert.assertTrue(
			baseProductEntryIds.contains(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertFalse(
			baseProductEntryIds.contains(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS));
		Assert.assertFalse(
			baseProductEntryIds.contains(
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));
	}

	@Test
	public void testGetBaseProductEntryIdsWithSubscriptionPushEnabled() {
		_setSubscriptionPushEnabled(true);

		List<String> baseProductEntryIds =
			ProductConstants.getBaseProductEntryIds();

		Assert.assertTrue(
			baseProductEntryIds.contains(
				ProductConstants.PRODUCT_ENTRY_ID_DATA_PLATFORM_ENTERPRISE));
		Assert.assertFalse(
			baseProductEntryIds.contains(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertTrue(
			baseProductEntryIds.contains(
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));

		List<String> koroneikiBaseProductEntryIds =
			ProductConstants.getKoroneikiBaseProductEntryIds();

		Assert.assertEquals(
			baseProductEntryIds.toString(), koroneikiBaseProductEntryIds.size(),
			baseProductEntryIds.size());
	}

	@Test
	public void testGetProductEntryId() {
		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			ProductConstants.getProductEntryId(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE));
	}

	@Test
	public void testGetProductEntryIdWithSubscriptionPushEnabled() {
		_setSubscriptionPushEnabled(true);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_DATA_PLATFORM_ENTERPRISE,
			ProductConstants.getProductEntryId(
				ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_ENTERPRISE));
		Assert.assertEquals(
			ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE,
			ProductConstants.getProductEntryId(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE));
		Assert.assertNull(
			ProductConstants.getProductEntryId(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE_CONTACTS));
	}

	@Test
	public void testGetProductEntryIds() {
		String[] productEntryIds = ProductConstants.getProductEntryIds();

		Assert.assertTrue(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.PRODUCT_ENTRY_ID_DATA_PLATFORM_ENTERPRISE));
		Assert.assertTrue(
			ArrayUtil.contains(
				productEntryIds, ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertTrue(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS));
		Assert.assertFalse(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));
	}

	@Test
	public void testGetProductEntryIdsWithSubscriptionPushEnabled() {
		_setSubscriptionPushEnabled(true);

		String[] productEntryIds = ProductConstants.getProductEntryIds();

		Assert.assertTrue(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.PRODUCT_ENTRY_ID_DATA_PLATFORM_ENTERPRISE));
		Assert.assertFalse(
			ArrayUtil.contains(
				productEntryIds, ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertFalse(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE_CONTACTS));
		Assert.assertTrue(
			ArrayUtil.contains(
				productEntryIds,
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));
	}

	@Test
	public void testGetProductName() {
		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
			ProductConstants.getProductName(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertNull(
			ProductConstants.getProductName(
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));
	}

	@Test
	public void testGetProductNameWithSubscriptionPushEnabled() {
		_setSubscriptionPushEnabled(true);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
			ProductConstants.getKoroneikiProductName(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertNull(
			ProductConstants.getProductName(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE));
		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
			ProductConstants.getProductName(
				ProductConstants.SALESFORCE_PRODUCT_ID_ENTERPRISE));
	}

	private void _setSubscriptionPushEnabled(boolean enabled) {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "OSB_FARO_SUBSCRIPTION_PUSH_ENABLED",
			enabled);
	}

}