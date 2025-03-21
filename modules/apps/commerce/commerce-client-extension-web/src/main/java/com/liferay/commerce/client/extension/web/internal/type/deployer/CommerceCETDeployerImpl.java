/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.client.extension.web.internal.type.deployer;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.CommerceCheckoutStepCET;
import com.liferay.client.extension.type.deployer.CommerceCETDeployer;
import com.liferay.commerce.client.extension.web.internal.util.ClientExtensionCommerceCheckoutStep;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.UserService;

import jakarta.servlet.ServletContext;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = CommerceCETDeployer.class)
public class CommerceCETDeployerImpl implements CommerceCETDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(CET cet) {
		return Arrays.asList(
			_register(
				CommerceCheckoutStep.class,
				new ClientExtensionCommerceCheckoutStep(
					(CommerceCheckoutStepCET)cet,
					_commercePaymentMethodGroupRelLocalService, _jsonFactory,
					_jspRenderer, _portalCatapult, _servletContext,
					_userService)));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private <T extends Registrable> ServiceRegistration<?> _register(
		Class<? super T> clazz, T registrable) {

		return _bundleContext.registerService(
			clazz, registrable, registrable.getDictionary());
	}

	private BundleContext _bundleContext;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private PortalCatapult _portalCatapult;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.client.extension.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserService _userService;

}