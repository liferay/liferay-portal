/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.configuration.persistence.listener;

import com.liferay.multi.factor.authentication.timebased.otp.web.internal.configuration.MFATimeBasedOTPConfiguration;
import com.liferay.multi.factor.authentication.timebased.otp.web.internal.util.MFATimeBasedOTPUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.timebased.otp.web.internal.configuration.MFATimeBasedOTPConfiguration",
	service = ConfigurationModelListener.class
)
public class MFATimeBasedOTPConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		MFATimeBasedOTPConfiguration mfaTimeBasedOTPConfiguration =
			ConfigurableUtil.createConfigurable(
				MFATimeBasedOTPConfiguration.class, properties);

		if (mfaTimeBasedOTPConfiguration.algorithmKeySize() >=
				MFATimeBasedOTPUtil.
					MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN) {

			return;
		}

		throw new ConfigurationModelListenerException(
			ResourceBundleUtil.getString(
				ResourceBundleUtil.getBundle(
					LocaleThreadLocal.getThemeDisplayLocale(), getClass()),
				"the-algorithm-key-size-must-be-at-least-x-bytes-in-fips-mode",
				MFATimeBasedOTPUtil.
					MFA_TIMEBASED_OTP_FIPS_ALGORITHM_KEY_SIZE_MIN),
			MFATimeBasedOTPConfiguration.class, getClass(), properties);
	}

}