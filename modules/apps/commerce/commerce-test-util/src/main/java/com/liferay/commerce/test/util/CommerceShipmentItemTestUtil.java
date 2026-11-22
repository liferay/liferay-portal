/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util;

import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.service.persistence.CommerceShipmentItemUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class CommerceShipmentItemTestUtil {

	public static List<CommerceShipmentItem>
			getCommerceShipmentItemsByCommerceOrderItemId(
				long commerceOrderItemId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceShipmentItemUtil.getPersistence(),
			commerceShipmentItemPersistence ->
				commerceShipmentItemPersistence.findByCommerceOrderItemId(
					commerceOrderItemId));
	}

}