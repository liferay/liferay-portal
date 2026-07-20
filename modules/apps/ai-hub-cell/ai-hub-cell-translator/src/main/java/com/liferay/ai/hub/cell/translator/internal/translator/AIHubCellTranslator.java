/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.translator.internal.translator;

import com.liferay.ai.hub.cell.translator.internal.configuration.AIHubCellTranslatorConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	configurationPid = "com.liferay.ai.hub.cell.translator.internal.configuration.AIHubCellTranslatorConfiguration",
	service = Translator.class
)
public class AIHubCellTranslator implements Translator {

	@Override
	public boolean isAIAssisted() {
		return true;
	}

	@Override
	public boolean isEnabled(long companyId) throws ConfigurationException {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-62272")) {
			return false;
		}

		AIHubCellTranslatorConfiguration aiHubCellTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AIHubCellTranslatorConfiguration.class, companyId);

		return aiHubCellTranslatorConfiguration.enabled();
	}

	@Override
	public TranslatorPacket translate(TranslatorPacket translatorPacket)
		throws PortalException {

		throw new UnsupportedOperationException(
			"AI Hub translation is available only through the content editor " +
				"chat");
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}