/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.internal.function.captcha;

import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	configurationPid = "com.liferay.captcha.internal.configuration.FunctionCaptchaImplConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class FunctionCaptchaImplFactory {

	@Activate
	protected void activate(
		ComponentContext componentContext, Map<String, Object> properties) {

		_componentInstance = _componentFactory.newInstance(
			HashMapDictionaryBuilder.<String, Object>put(
				"captcha.engine.impl",
				StringBundler.concat(
					FunctionCaptchaImpl.class.getName(), StringPool.POUND,
					ConfigurationFactoryUtil.getExternalReferenceCode(
						properties))
			).putAll(
				properties
			).remove(
				Constants.SERVICE_PID
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_componentInstance != null) {
			_componentInstance.dispose();
		}
	}

	@Reference(
		target = "(component.factory=com.liferay.captcha.internal.function.captcha.FunctionCaptchaImpl)"
	)
	private ComponentFactory<FunctionCaptchaImpl> _componentFactory;

	private ComponentInstance<FunctionCaptchaImpl> _componentInstance;

}