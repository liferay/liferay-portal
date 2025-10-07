/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.InetAddress;

import java.util.Date;

/**
 * @author Matthew Kong
 */
public class FaroProjectImpl extends FaroProjectBaseImpl {

	@Override
	public Date getLastAnniversaryDate() throws Exception {
		if (_subscriptionJSONObject == null) {
			_subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
				getSubscription());
		}

		return new Date(_subscriptionJSONObject.getLong("lastAnniversaryDate"));
	}

	@Override
	public String getProjectId() {
		return StringUtil.removeSubstring(getWeDeployKey(), ".lfr.cloud");
	}

	@Override
	public Date getStartDate() throws Exception {
		if (_subscriptionJSONObject == null) {
			_subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
				getSubscription());
		}

		return new Date(
			_subscriptionJSONObject.getLong("startDate", getCreateTime()));
	}

	@Override
	public String getSubscriptionName() throws Exception {
		if (_subscriptionJSONObject == null) {
			_subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
				getSubscription());
		}

		return _subscriptionJSONObject.getString("name");
	}

	@Override
	public boolean isAllowedIPAddress(String ipAddress) {
		try {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
				getIpAddresses());

			if (jsonArray.length() == 0) {
				return true;
			}

			for (Object object : jsonArray) {
				if (_matches((String)object, ipAddress)) {
					return true;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean isTrial() {
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				getSubscription());

			return StringUtil.equals(
				jsonObject.getString("name"), "Liferay Analytics Cloud Basic");
		}
		catch (Exception exception) {
			_log.error(exception);

			return true;
		}
	}

	private boolean _matches(String ipAddress1, String ipAddress2)
		throws Exception {

		if (ipAddress1.indexOf(CharPool.SLASH) == -1) {
			return ipAddress1.equals(ipAddress2);
		}

		String[] parts = StringUtil.split(ipAddress1, CharPool.SLASH);

		ipAddress1 = parts[0];

		int netmask = GetterUtil.getInteger(parts[1]);

		InetAddress inetAddress1 = InetAddress.getByName(ipAddress1);
		InetAddress inetAddress2 = InetAddress.getByName(ipAddress2);

		byte[] addressBytes1 = inetAddress1.getAddress();
		byte[] addressBytes2 = inetAddress2.getAddress();

		int netmaskBytes = netmask / 8;

		for (int i = 0; i < netmaskBytes; i++) {
			if (addressBytes1[i] != addressBytes2[i]) {
				return false;
			}
		}

		byte finalByte = (byte)(0xFF00 >> (netmask & 0x07));

		if ((finalByte == 0) ||
			((addressBytes1[netmaskBytes] & finalByte) ==
				(addressBytes2[netmaskBytes] & finalByte))) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectImpl.class);

	private JSONObject _subscriptionJSONObject;

}