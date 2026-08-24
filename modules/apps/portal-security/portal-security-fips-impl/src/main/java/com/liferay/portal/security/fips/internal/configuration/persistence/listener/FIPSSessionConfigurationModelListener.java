/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration;
import com.liferay.portal.security.fips.constants.FIPSConstants;
import com.liferay.portal.security.fips.util.FIPSUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Manuele Castro
 */
@Component(
	property = {
		"model.class.name=com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration",
		"model.class.name=com.liferay.portal.security.fips.configuration.FIPSSessionConfiguration.scoped"
	},
	service = ConfigurationModelListener.class
)
public class FIPSSessionConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		_checkCryptoOfficerRole(properties);
		_validate(
			"fips-session-idle-timeout",
			FIPSUtil.toMinutes(
				GetterUtil.getInteger(properties.get("idleTimeout")),
				GetterUtil.getString(properties.get("idleTimeoutTimeUnit"))),
			FIPSConstants.SESSION_IDLE_TIMEOUT_MAX_MINUTES, properties);
		_validate(
			"fips-session-maximum-age",
			FIPSUtil.toMinutes(
				GetterUtil.getInteger(properties.get("maximumAge")),
				GetterUtil.getString(properties.get("maximumAgeTimeUnit"))),
			FIPSConstants.SESSION_MAXIMUM_AGE_MAX_MINUTES, properties);
	}

	private void _checkCryptoOfficerRole(Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		User user = _userLocalService.fetchUser(
			PrincipalThreadLocal.getUserId());

		if ((user != null) && FIPSUtil.hasCryptoOfficerRole(user)) {
			return;
		}

		throw new ConfigurationModelListenerException(
			StringBundler.concat(
				"Only a user with the role \"", RoleConstants.CRYPTO_OFFICER,
				"\" is able to update the FIPS session configuration"),
			FIPSSessionConfiguration.class, getClass(), properties);
	}

	private void _validate(
			String name, long minutes, int maximumMinutes,
			Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if ((minutes > 0) && (minutes <= maximumMinutes)) {
			return;
		}

		throw new ConfigurationModelListenerException(
			StringBundler.concat(
				name, " is expected to be between 1 and ", maximumMinutes,
				" minutes but was ", minutes),
			FIPSSessionConfiguration.class, getClass(), properties);
	}

	@Reference
	private UserLocalService _userLocalService;

}