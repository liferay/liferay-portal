/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.translator.internal.configuration.persistence.listener;

import com.liferay.ai.hub.cell.translator.internal.configuration.AIHubCellTranslatorConfiguration;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "model.class.name=com.liferay.ai.hub.cell.translator.internal.configuration.AIHubCellTranslatorConfiguration",
	service = ConfigurationModelListener.class
)
public class AIHubCellTranslatorConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!GetterUtil.getBoolean(properties.get("enabled"))) {
			return;
		}

		long companyId = GetterUtil.getLong(
			properties.get("companyId"), CompanyConstants.SYSTEM);

		if ((companyId != CompanyConstants.SYSTEM) &&
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-62272")) {

			throw new ConfigurationModelListenerException(
				_language.get(
					LocaleThreadLocal.getThemeDisplayLocale(),
					"ai-hub-must-be-enabled-to-use-the-ai-hub-translator"),
				AIHubCellTranslatorConfiguration.class, getClass(), properties);
		}
	}

	@Reference
	private Language _language;

}