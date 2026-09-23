/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microsoft.translator.internal;

import com.liferay.microsoft.translator.internal.configuration.MicrosoftTranslatorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.microsofttranslator.MicrosoftTranslator;
import com.liferay.portal.kernel.microsofttranslator.MicrosoftTranslatorFactory;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hugo Huijser
 */
@Component(
	configurationPid = "com.liferay.microsoft.translator.internal.configuration.MicrosoftTranslatorConfiguration",
	service = MicrosoftTranslatorFactory.class
)
public class MicrosoftTranslatorFactoryImpl
	implements MicrosoftTranslatorFactory {

	@Override
	public MicrosoftTranslator getMicrosoftTranslator() {
		if (_microsoftTranslator == null) {
			_microsoftTranslator = new MicrosoftTranslatorImpl(
				_secretResolver.resolve(
					CompanyConstants.SYSTEM,
					_microsoftTranslatorConfiguration.subscriptionKey()));
		}

		return _microsoftTranslator;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_microsoftTranslatorConfiguration = ConfigurableUtil.createConfigurable(
			MicrosoftTranslatorConfiguration.class, properties);

		_microsoftTranslator = null;
	}

	@Deactivate
	protected void deactivate() {
		_microsoftTranslator = null;
	}

	private volatile MicrosoftTranslator _microsoftTranslator;
	private volatile MicrosoftTranslatorConfiguration
		_microsoftTranslatorConfiguration;

	@Reference
	private SecretResolver _secretResolver;

}