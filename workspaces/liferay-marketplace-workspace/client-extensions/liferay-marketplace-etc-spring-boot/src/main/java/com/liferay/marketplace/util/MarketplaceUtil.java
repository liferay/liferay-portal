/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.util;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Keven Leone
 */
public class MarketplaceUtil {

	public static JSONArray createCloudProvisioningJSONArray(
		Page<OrderItem> orderItemPage) {

		JSONArray jsonArray = new JSONArray();

		for (OrderItem orderItem : orderItemPage.getItems()) {
			jsonArray.put(
				new JSONObject(
				).put(
					"deployments", new JSONArray()
				).put(
					"orderItemId", orderItem.getId()
				).put(
					"sku", orderItem.getSku()
				).put(
					"shippedQuantity", 0
				).put(
					"quantity",
					orderItem.getQuantity(
					).intValue()
				));
		}

		return jsonArray;
	}

	public static String createTemporaryDeployment(
			Map<String, String> customFields, JSONArray jsonArray,
			JSONObject jsonObject, String projectId)
		throws Exception {

		UUID uuid = UUID.randomUUID();

		jsonObject.put(
			"deployments",
			jsonObject.getJSONArray(
				"deployments"
			).put(
				new JSONObject(
				).put(
					"id", uuid.toString()
				).put(
					"loading", true
				).put(
					"projectId", projectId
				)
			));

		customFields.put("cloud-provisioning", jsonArray.toString());

		return uuid.toString();
	}

	public static void deleteDeployment(
		String deploymentId, JSONObject jsonObject) {

		JSONArray deploymentsJSONArray = jsonObject.getJSONArray("deployments");

		for (int i = 0; i < deploymentsJSONArray.length(); i++) {
			JSONObject deploymentJSONObject =
				deploymentsJSONArray.getJSONObject(i);

			if (Objects.equals(
					deploymentJSONObject.getString("id"), deploymentId)) {

				deploymentsJSONArray.remove(i);
			}
		}
	}

	public static JSONObject getCloudProvisioningJSONObject(
		JSONArray jsonArray, long orderItemId) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (Objects.equals(
					jsonObject.getLong("orderItemId"), orderItemId)) {

				return jsonObject;
			}
		}

		return new JSONObject();
	}

}