/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;

import java.util.Dictionary;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joshua Cords
 */
@Component(
	property = "model.class.name=com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	service = ConfigurationModelListener.class
)
public class ElasticsearchConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try {
			if (!GetterUtil.getBoolean(properties.get("trackTotalHits"))) {
				_log.error(
					"The track total hits configuration is deprecated, use " +
						"the track total hits limit instead");
			}

			_validateTrackTotalHitsLimit(properties);
		}
		catch (Exception exception) {
			throw new ConfigurationModelListenerException(
				exception.getMessage(), ElasticsearchConfiguration.class,
				getClass(), properties);
		}
	}

	private String _getMessage(String key) {
		try {
			return ResourceBundleUtil.getString(_getResourceBundle(), key);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return key;
		}
	}

	private ResourceBundle _getResourceBundle() {
		return ResourceBundleUtil.getBundle(
			"content.Language", LocaleThreadLocal.getThemeDisplayLocale(),
			getClass());
	}

	private void _validateTrackTotalHitsLimit(
			Dictionary<String, Object> properties)
		throws Exception {

		int indexMaxResultWindow = GetterUtil.getInteger(
			properties.get("indexMaxResultWindow"));
		int trackTotalHitsLimit = GetterUtil.getInteger(
			properties.get("trackTotalHitsLimit"));

		if (trackTotalHitsLimit >= indexMaxResultWindow) {
			return;
		}

		_log.error("Track total hits limit is less than the max result window");

		throw new Exception(
			_getMessage(
				"track-total-hits-limit-is-less-than-the-max-result-window"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchConfigurationModelListener.class);

}