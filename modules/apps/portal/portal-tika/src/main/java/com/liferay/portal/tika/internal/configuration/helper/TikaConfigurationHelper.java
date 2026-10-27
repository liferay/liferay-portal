/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.internal.configuration.helper;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.tika.internal.configuration.TikaConfiguration;

import java.io.InputStream;

import java.net.URL;

import java.util.Map;

import org.apache.tika.config.TikaConfig;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.portal.tika.internal.configuration.TikaConfiguration",
	service = TikaConfigurationHelper.class
)
public class TikaConfigurationHelper {

	public TikaConfig getTikaConfig() {
		return _tikaConfigDCLSingleton.getSingleton(this::_createTikaConfig);
	}

	public boolean useForkProcess(String mimeType) {
		if (_tikaConfiguration.textExtractionForkProcessEnabled() &&
			ArrayUtil.contains(
				_tikaConfiguration.textExtractionForkProcessMimeTypes(),
				mimeType)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Fork process is enabled for " + mimeType);
			}

			return true;
		}

		return false;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		TikaConfiguration tikaConfiguration =
			ConfigurableUtil.createConfigurable(
				TikaConfiguration.class, properties);

		_tikaConfigURL = _getTikaConfigURL(tikaConfiguration.tikaConfigXml());
		_tikaConfiguration = tikaConfiguration;

		_tikaConfigDCLSingleton.destroy(null);
	}

	private TikaConfig _createTikaConfig() {
		try (InputStream inputStream = _tikaConfigURL.openStream()) {
			return new TikaConfig(inputStream);
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to create Tika configuration", exception);
		}
	}

	private URL _getTikaConfigURL(String tikaConfigXml) {
		Class<?> clazz = TikaConfigurationHelper.class;

		URL url = clazz.getResource(tikaConfigXml);

		if (url != null) {
			return url;
		}

		ClassLoader classLoader = clazz.getClassLoader();

		url = classLoader.getResource(tikaConfigXml);

		if (url != null) {
			return url;
		}

		classLoader = PortalClassLoaderUtil.getClassLoader();

		url = classLoader.getResource(tikaConfigXml);

		if (url != null) {
			return url;
		}

		throw new IllegalArgumentException(
			"Unable to read Tika configuration " + tikaConfigXml);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TikaConfigurationHelper.class);

	private final DCLSingleton<TikaConfig> _tikaConfigDCLSingleton =
		new DCLSingleton<>();
	private volatile URL _tikaConfigURL;
	private volatile TikaConfiguration _tikaConfiguration;

}