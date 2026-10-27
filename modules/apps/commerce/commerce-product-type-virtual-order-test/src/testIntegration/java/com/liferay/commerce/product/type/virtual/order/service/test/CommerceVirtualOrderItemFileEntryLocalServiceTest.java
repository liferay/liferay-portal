/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.order.exception.CommerceVirtualOrderItemUrlException;
import com.liferay.commerce.product.type.virtual.order.exception.NoSuchVirtualOrderItemFileEntryException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceVirtualOrderItemFileEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateCommerceVirtualOrderItemFileEntry() throws Exception {
		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			_commerceVirtualOrderItemFileEntryLocalService.
				addCommerceVirtualOrderItemFileEntry(
					TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
					RandomTestUtil.randomLong(), 0,
					"https://" + RandomTestUtil.randomString(), 0,
					RandomTestUtil.randomString());

		Assert.assertThrows(
			CommerceVirtualOrderItemUrlException.class,
			() ->
				_commerceVirtualOrderItemFileEntryLocalService.
					updateCommerceVirtualOrderItemFileEntry(
						commerceVirtualOrderItemFileEntry.
							getCommerceVirtualOrderItemFileEntryId(),
						0, "file:///" + RandomTestUtil.randomString(), 0,
						RandomTestUtil.randomString()));

		Assert.assertThrows(
			NoSuchVirtualOrderItemFileEntryException.class,
			() ->
				_commerceVirtualOrderItemFileEntryLocalService.
					updateCommerceVirtualOrderItemFileEntry(
						RandomTestUtil.randomLong(), 0,
						"https://" + RandomTestUtil.randomString(), 0,
						RandomTestUtil.randomString()));
	}

	@Inject
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

}