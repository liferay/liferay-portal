/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.configuration.StagingConfiguration;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = StagingURLHelper.class)
public class StagingURLHelperImpl implements StagingURLHelper {

	@Override
	public String buildRemoteURL(
		ExportImportConfiguration exportImportConfiguration) {

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		String remoteAddress = MapUtil.getString(settingsMap, "remoteAddress");
		int remotePort = MapUtil.getInteger(settingsMap, "remotePort");
		String remotePathContext = MapUtil.getString(
			settingsMap, "remotePathContext");
		boolean secureConnection = MapUtil.getBoolean(
			settingsMap, "secureConnection");

		return buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);
	}

	@Override
	public String buildRemoteURL(
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection) {

		if (!_isRemoteHostAllowed(remoteAddress)) {
			RemoteOptionsException remoteOptionsException =
				new RemoteOptionsException(
					RemoteOptionsException.REMOTE_ADDRESS);

			remoteOptionsException.setRemoteAddress(remoteAddress);

			throw new SystemException(
				"Remote host " + remoteAddress + " is not allowed",
				remoteOptionsException);
		}

		StringBundler sb = new StringBundler(5);

		if (secureConnection) {
			sb.append(Http.HTTPS_WITH_SLASH);
		}
		else {
			sb.append(Http.HTTP_WITH_SLASH);
		}

		sb.append(remoteAddress);

		if (remotePort > 0) {
			sb.append(StringPool.COLON);
			sb.append(remotePort);
		}

		if (Validator.isNotNull(remotePathContext)) {
			sb.append(remotePathContext);
		}

		return sb.toString();
	}

	@Override
	public String buildRemoteURL(
		UnicodeProperties typeSettingsUnicodeProperties) {

		String remoteAddress = typeSettingsUnicodeProperties.getProperty(
			"remoteAddress");
		int remotePort = GetterUtil.getInteger(
			typeSettingsUnicodeProperties.getProperty("remotePort"));
		String remotePathContext = typeSettingsUnicodeProperties.getProperty(
			"remotePathContext");
		boolean secureConnection = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("secureConnection"));

		return buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);
	}

	private boolean _isRemoteHostAllowed(String remoteAddress) {
		try {
			StagingConfiguration stagingConfiguration =
				_configurationProvider.getCompanyConfiguration(
					StagingConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			String[] stagingRemoteHostsAllowed =
				stagingConfiguration.stagingRemoteHostsAllowed();

			if (ArrayUtil.isEmpty(stagingRemoteHostsAllowed)) {
				return true;
			}

			return ArrayUtil.contains(
				stagingRemoteHostsAllowed, remoteAddress, true);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingURLHelperImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

}