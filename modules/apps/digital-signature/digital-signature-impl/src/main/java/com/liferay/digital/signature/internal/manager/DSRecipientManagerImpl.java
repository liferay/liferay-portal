/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.manager;

import com.liferay.digital.signature.internal.http.DSHttp;
import com.liferay.digital.signature.manager.DSRecipientManager;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = DSRecipientManager.class)
public class DSRecipientManagerImpl implements DSRecipientManager {

	@Override
	public DSRecipient getDSRecipient(
		long companyId, long groupId, String dsEnvelopeId,
		String emailAddress) {

		JSONObject jsonObject = _dsHttp.get(
			companyId, groupId,
			StringBundler.concat("envelopes/", dsEnvelopeId, "/recipients"));

		JSONArray jsonArray = jsonObject.getJSONArray("signers");

		if (jsonArray == null) {
			return null;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject signerJSONObject = jsonArray.getJSONObject(i);

			if (!StringUtil.equalsIgnoreCase(
					emailAddress, signerJSONObject.getString("email"))) {

				continue;
			}

			DSRecipient dsRecipient = new DSRecipient();

			dsRecipient.setDSRecipientId(
				signerJSONObject.getString("recipientId"));
			dsRecipient.setEmailAddress(signerJSONObject.getString("email"));
			dsRecipient.setName(signerJSONObject.getString("name"));
			dsRecipient.setStatus(signerJSONObject.getString("status"));
			dsRecipient.setTabsJSONObject(
				signerJSONObject.getJSONObject("tabs"));

			return dsRecipient;
		}

		return null;
	}

	@Reference
	private DSHttp _dsHttp;

}