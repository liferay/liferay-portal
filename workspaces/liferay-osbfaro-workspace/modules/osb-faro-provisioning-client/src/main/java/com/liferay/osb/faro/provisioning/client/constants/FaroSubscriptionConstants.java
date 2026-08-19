/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.constants;

import com.liferay.osb.faro.provisioning.client.subscription.FaroSubscriptionPlan;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Matthew Kong
 */
public class FaroSubscriptionConstants {

	public static final double LIMIT_APPROACHING_THRESHOLD = .8;

	public static final int STATUS_LIMIT_APPROACHING = 1;

	public static final int STATUS_LIMIT_OVER = 2;

	public static final int STATUS_OK = 0;

	public static FaroSubscriptionPlan getFaroSubscriptionPlan(String name) {
		return _faroSubscriptionPlans.get(name);
	}

	public static FaroSubscriptionPlan getFaroSubscriptionPlanByProductEntryId(
		String productEntryId) {

		return getFaroSubscriptionPlan(
			ProductConstants.getProductName(productEntryId));
	}

	public static Map<String, FaroSubscriptionPlan> getFaroSubscriptionPlans() {
		return _faroSubscriptionPlans;
	}

	public static Map<String, Integer> getStatuses() {
		return _statuses;
	}

	private static final Map<String, FaroSubscriptionPlan>
		_faroSubscriptionPlans =
			HashMapBuilder.<String, FaroSubscriptionPlan>put(
				ProductConstants.PRODUCT_ENTRY_NAME_BASIC,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_BASIC, 1000,
					300000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS, 10000,
					5000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS_CONTACTS,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS,
					ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS_CONTACTS, 5000,
					0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS_TRACKED_PAGES,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS,
					ProductConstants.PRODUCT_ENTRY_NAME_BUSINESS_TRACKED_PAGES,
					0, 5000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM,
				new FaroSubscriptionPlan(
					-1, 100000, null, 50, 2000000, 3, -1, -1,
					ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM, -1, 20)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_ENTERPRISE,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						PRODUCT_ENTRY_NAME_DATA_PLATFORM_ENTERPRISE,
					100000, 60000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA,
				new FaroSubscriptionPlan(
					20, 300, null, 5, -1, -1, 3, 1000,
					ProductConstants.
						PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA,
					300000, 3)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_DIGITAL_SALES_ROOM,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.PRODUCT_ENTRY_NAME_DIGITAL_SALES_ROOM, -1,
					-1)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
					100000, 60000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE_CONTACTS,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
					ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE_CONTACTS,
					5000, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE_TRACKED_PAGES,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_ENTERPRISE,
					ProductConstants.
						PRODUCT_ENTRY_NAME_ENTERPRISE_TRACKED_PAGES,
					0, 5000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_BUSINESS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_BUSINESS,
					10000, 5000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_1K_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_1K_USERS,
					1000, -1)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_1K_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_1K_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_1K_USERS_EXTRA_USER,
					1000, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_5K_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_5K_USERS,
					5000, -1)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_5K_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_5K_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_5K_USERS_EXTRA_USER,
					5000, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_10K_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_10K_USERS,
					10000, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_CSP_10K_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_10K_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_10K_USERS_EXTRA_USER,
					10000, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_20K_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_20K_USERS,
					20000, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_CSP_20K_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_20K_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_20K_USERS_EXTRA_USER,
					20000, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_100_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_100_USERS,
					100, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_CSP_100_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_100_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_100_USERS_EXTRA_USER,
					100, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_500_USERS,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_500_USERS,
					500, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_CSP_500_USERS_EXTRA_USER,
				new FaroSubscriptionPlan(
					ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_500_USERS,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_CSP_500_USERS_EXTRA_USER,
					500, 0)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_CUSTOM,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_CSP_CUSTOM,
					-1, -1)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_ENTERPRISE,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_ENTERPRISE,
					100000, 60000000)
			).put(
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_PRO,
				new FaroSubscriptionPlan(
					null, ProductConstants.PRODUCT_ENTRY_NAME_LXC_PRO, 1000,
					300000)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_ENGAGE_SITE,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_ENGAGE_SITE,
					-1, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_SUPPORT_SITE,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_SUPPORT_SITE,
					-1, -1)
			).put(
				ProductConstants.
					PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_TRANSACT_SITE,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						PRODUCT_ENTRY_NAME_LXC_SUBSCRIPTION_TRANSACT_SITE,
					-1, -1)
			).build();
	private static final Map<String, Integer> _statuses = HashMapBuilder.put(
		"approaching", STATUS_LIMIT_APPROACHING
	).put(
		"ok", STATUS_OK
	).put(
		"over", STATUS_LIMIT_OVER
	).build();

}