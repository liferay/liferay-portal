/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.test.util;

import com.liferay.commerce.wish.list.service.persistence.CommerceWishListItemUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

/**
 * @author Shuyang Zhou
 */
public class CommerceWishListItemTestUtil {

	public static int getCommerceWishListItemsCount(long commerceWishListId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceWishListItemUtil.getPersistence(),
			commerceWishListItemPersistence ->
				commerceWishListItemPersistence.countByCommerceWishListId(
					commerceWishListId));
	}

}