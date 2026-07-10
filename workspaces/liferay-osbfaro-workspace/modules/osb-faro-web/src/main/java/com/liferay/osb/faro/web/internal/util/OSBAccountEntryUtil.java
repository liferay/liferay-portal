/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class OSBAccountEntryUtil {

	public static OSBAccountEntry build(FaroProject faroProject)
		throws Exception {

		JSONObject subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
			faroProject.getSubscription());

		List<OSBOfferingEntry> offeringEntries = new ArrayList<>();

		int status = ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE;

		if (!subscriptionJSONObject.getBoolean("active")) {
			status = 0;
		}

		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			_getProductEntryId(subscriptionJSONObject.getString("name")));
		osbOfferingEntry.setQuantity(1);
		osbOfferingEntry.setStatus(status);

		long startDate = subscriptionJSONObject.getLong("startDate");

		if (startDate > 0) {
			osbOfferingEntry.setStartDate(new Date(startDate));
		}

		long endDate = subscriptionJSONObject.getLong("endDate");

		if (endDate > 0) {
			osbOfferingEntry.setSupportEndDate(new Date(endDate));
		}

		offeringEntries.add(osbOfferingEntry);

		JSONArray addOnsJSONArray = subscriptionJSONObject.getJSONArray(
			"addOns");

		if (addOnsJSONArray != null) {
			for (int i = 0; i < addOnsJSONArray.length(); i++) {
				OSBOfferingEntry curOSBOfferingEntry = new OSBOfferingEntry();

				JSONObject addOnJSONObject = addOnsJSONArray.getJSONObject(i);

				curOSBOfferingEntry.setProductEntryId(
					ProductConstants.getProductEntryId(
						addOnJSONObject.getString("name")));
				curOSBOfferingEntry.setQuantity(
					addOnJSONObject.getInt("quantity"));

				curOSBOfferingEntry.setStatus(status);

				offeringEntries.add(curOSBOfferingEntry);
			}
		}

		return OSBAccountEntryBuilder.setCorpProjectUuid(
			faroProject.getCorpProjectUuid()
		).setName(
			faroProject.getCorpProjectName()
		).setOfferingEntries(
			offeringEntries
		).build();
	}

	private static String _getProductEntryId(String name) {
		String productEntryId = ProductConstants.getProductEntryId(name);

		if (productEntryId == null) {
			return ProductConstants.BASIC_PRODUCT_ENTRY_ID;
		}

		return productEntryId;
	}

}