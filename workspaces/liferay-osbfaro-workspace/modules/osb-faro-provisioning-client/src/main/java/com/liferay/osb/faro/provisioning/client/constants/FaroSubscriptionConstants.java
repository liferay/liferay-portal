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
				ProductConstants.BASIC_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.BASIC_PRODUCT_ENTRY_NAME, 1000,
					300000)
			).put(
				ProductConstants.BUSINESS_CONTACTS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.BUSINESS_PRODUCT_ENTRY_NAME,
					ProductConstants.BUSINESS_CONTACTS_PRODUCT_ENTRY_NAME, 5000,
					0)
			).put(
				ProductConstants.BUSINESS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.BUSINESS_PRODUCT_ENTRY_NAME, 10000,
					5000000)
			).put(
				ProductConstants.BUSINESS_TRACKED_PAGES_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.BUSINESS_PRODUCT_ENTRY_NAME,
					ProductConstants.BUSINESS_TRACKED_PAGES_PRODUCT_ENTRY_NAME,
					0, 5000000)
			).put(
				ProductConstants.DATA_PLATFORM_ENTERPRISE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						DATA_PLATFORM_ENTERPRISE_PRODUCT_ENTRY_NAME,
					100000, 60000000)
			).put(
				ProductConstants.DATA_PLATFORM_PRIVATE_BETA_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					20, 300, null, 5, -1, -1, 3, 1000,
					ProductConstants.
						DATA_PLATFORM_PRIVATE_BETA_PRODUCT_ENTRY_NAME,
					300000, 3)
			).put(
				ProductConstants.DATA_PLATFORM_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					-1, 100000, null, 50, 2000000, 3, -1, -1,
					ProductConstants.DATA_PLATFORM_PRODUCT_ENTRY_NAME, -1, 20)
			).put(
				ProductConstants.DIGITAL_SALES_ROOM_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.DIGITAL_SALES_ROOM_PRODUCT_ENTRY_NAME, -1,
					-1)
			).put(
				ProductConstants.ENTERPRISE_CONTACTS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.ENTERPRISE_PRODUCT_ENTRY_NAME,
					ProductConstants.ENTERPRISE_CONTACTS_PRODUCT_ENTRY_NAME,
					5000, 0)
			).put(
				ProductConstants.ENTERPRISE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.ENTERPRISE_PRODUCT_ENTRY_NAME,
					100000, 60000000)
			).put(
				ProductConstants.ENTERPRISE_TRACKED_PAGES_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.ENTERPRISE_PRODUCT_ENTRY_NAME,
					ProductConstants.
						ENTERPRISE_TRACKED_PAGES_PRODUCT_ENTRY_NAME,
					0, 5000000)
			).put(
				ProductConstants.LXC_BUSINESS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_BUSINESS_PRODUCT_ENTRY_NAME,
					10000, 5000000)
			).put(
				ProductConstants.LXC_CSP_1K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_1K_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_1K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					1000, 0)
			).put(
				ProductConstants.LXC_CSP_1K_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_1K_USERS_PRODUCT_ENTRY_NAME,
					1000, -1)
			).put(
				ProductConstants.LXC_CSP_5K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_5K_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_5K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					5000, 0)
			).put(
				ProductConstants.LXC_CSP_5K_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_5K_USERS_PRODUCT_ENTRY_NAME,
					5000, -1)
			).put(
				ProductConstants.
					LXC_CSP_10K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_10K_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_10K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					10000, 0)
			).put(
				ProductConstants.LXC_CSP_10K_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_10K_USERS_PRODUCT_ENTRY_NAME,
					10000, -1)
			).put(
				ProductConstants.
					LXC_CSP_20K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_20K_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_20K_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					20000, 0)
			).put(
				ProductConstants.LXC_CSP_20K_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_20K_USERS_PRODUCT_ENTRY_NAME,
					20000, -1)
			).put(
				ProductConstants.
					LXC_CSP_100_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_100_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_100_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					100, 0)
			).put(
				ProductConstants.LXC_CSP_100_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_100_USERS_PRODUCT_ENTRY_NAME,
					100, -1)
			).put(
				ProductConstants.
					LXC_CSP_500_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					ProductConstants.LXC_CSP_500_USERS_PRODUCT_ENTRY_NAME,
					ProductConstants.
						LXC_CSP_500_USERS_EXTRA_USER_PRODUCT_ENTRY_NAME,
					500, 0)
			).put(
				ProductConstants.LXC_CSP_500_USERS_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_500_USERS_PRODUCT_ENTRY_NAME,
					500, -1)
			).put(
				ProductConstants.LXC_CSP_CUSTOM_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_CSP_CUSTOM_PRODUCT_ENTRY_NAME,
					-1, -1)
			).put(
				ProductConstants.LXC_ENTERPRISE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_ENTERPRISE_PRODUCT_ENTRY_NAME,
					100000, 60000000)
			).put(
				ProductConstants.LXC_PRO_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null, ProductConstants.LXC_PRO_PRODUCT_ENTRY_NAME, 1000,
					300000)
			).put(
				ProductConstants.
					LXC_SUBSCRIPTION_ENGAGE_SITE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						LXC_SUBSCRIPTION_ENGAGE_SITE_PRODUCT_ENTRY_NAME,
					-1, -1)
			).put(
				ProductConstants.
					LXC_SUBSCRIPTION_SUPPORT_SITE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						LXC_SUBSCRIPTION_SUPPORT_SITE_PRODUCT_ENTRY_NAME,
					-1, -1)
			).put(
				ProductConstants.
					LXC_SUBSCRIPTION_TRANSACT_SITE_PRODUCT_ENTRY_NAME,
				new FaroSubscriptionPlan(
					null,
					ProductConstants.
						LXC_SUBSCRIPTION_TRANSACT_SITE_PRODUCT_ENTRY_NAME,
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