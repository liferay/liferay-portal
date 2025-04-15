/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.product.content.contributor;

import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.PURCHASABLE,
	service = CPContentContributor.class
)
public class PurchasabilityCPContentContributor
	implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.PURCHASABLE;
	}

	@Override
	public JSONObject getValue(
		CPInstance cpInstance, HttpServletRequest httpServletRequest) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (cpInstance == null) {
			return jsonObject;
		}

		jsonObject.put(
			CPContentContributorConstants.PURCHASABLE,
			cpInstance.isPurchasable());

		return jsonObject;
	}

	@Reference
	private JSONFactory _jsonFactory;

}