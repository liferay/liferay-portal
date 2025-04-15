/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.portlet.action;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.product.navigation.applications.menu.web.internal.constants.ProductNavigationApplicationsMenuPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.InputStream;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationApplicationsMenuPortletKeys.PRODUCT_NAVIGATION_APPLICATIONS_MENU,
		"mvc.command.name=/applications_menu/liferay_logo"
	},
	service = MVCResourceCommand.class
)
public class ApplicationsMenuLiferayLogoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String applicationsMenuDefaultLiferayLogo =
			_getApplicationsMenuDefaultLiferayLogo();
		ClassLoader classLoader = ImageToolUtil.class.getClassLoader();
		InputStream inputStream = null;

		try {
			int index = applicationsMenuDefaultLiferayLogo.indexOf(
				CharPool.SEMICOLON);

			if (index == -1) {
				inputStream = classLoader.getResourceAsStream(
					applicationsMenuDefaultLiferayLogo);
			}
			else {
				String bundleIdString =
					applicationsMenuDefaultLiferayLogo.substring(0, index);

				int bundleId = GetterUtil.getInteger(bundleIdString, -1);

				String name = applicationsMenuDefaultLiferayLogo.substring(
					index + 1);

				if (bundleId < 0) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Fallback to portal class loader because of " +
								"invalid bundle ID " + bundleIdString);
					}

					inputStream = classLoader.getResourceAsStream(name);
				}
				else {
					Bundle bundle = _bundleContext.getBundle(bundleId);

					if (bundle != null) {
						URL url = bundle.getResource(name);

						inputStream = url.openStream();
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to configure the default Liferay logo: " +
						exception.getMessage());

				return;
			}
		}

		if (inputStream == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Default Liferay logo is not available");
			}

			return;
		}

		resourceResponse.setContentType(
			MimeTypesUtil.getExtensionContentType(
				applicationsMenuDefaultLiferayLogo));

		PortletResponseUtil.write(resourceResponse, inputStream);
	}

	private String _getApplicationsMenuDefaultLiferayLogo() {
		return GetterUtil.getString(
			PropsUtil.get(PropsKeys.APPLICATIONS_MENU_DEFAULT_LIFERAY_LOGO),
			"com/liferay/portal/dependencies/liferay_logo.png");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ApplicationsMenuLiferayLogoMVCResourceCommand.class);

	private BundleContext _bundleContext;

}