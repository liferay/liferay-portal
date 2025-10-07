/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.stock.activity.CommerceLowStockActivity;
import com.liferay.commerce.stock.activity.CommerceLowStockActivityRegistry;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.LowStockAction;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class LowStockActionResourceTest
	extends BaseLowStockActionResourceTestCase {

	@Override
	@Test
	public void testGetLowStockActionsPage() throws Exception {
		Page<LowStockAction> page =
			lowStockActionResource.getLowStockActionsPage();

		List<CommerceLowStockActivity> commerceLowStockActivities =
			_commerceLowStockActivityRegistry.getCommerceLowStockActivities();

		Assert.assertEquals(
			commerceLowStockActivities.toString(), page.getTotalCount(),
			commerceLowStockActivities.size());

		assertValid(page);
	}

	@Inject
	private CommerceLowStockActivityRegistry _commerceLowStockActivityRegistry;

}