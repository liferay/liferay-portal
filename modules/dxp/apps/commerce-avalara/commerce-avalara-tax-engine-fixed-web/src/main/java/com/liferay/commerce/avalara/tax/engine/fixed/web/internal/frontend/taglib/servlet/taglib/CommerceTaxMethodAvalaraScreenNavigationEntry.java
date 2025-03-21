/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.tax.engine.fixed.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.avalara.connector.CommerceAvalaraConnector;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorChannelConfiguration;
import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.constants.CommerceAvalaraWebKeys;
import com.liferay.commerce.avalara.connector.dispatch.CommerceAvalaraDispatchTrigger;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceTaxMethodAvalaraScreenNavigationEntry
	extends CommerceTaxMethodAvalaraScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceTaxMethod> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceTaxMethod commerceTaxMethod) {
		if (commerceTaxMethod == null) {
			return false;
		}

		String engineKey = commerceTaxMethod.getEngineKey();

		if (engineKey.equals("avalara")) {
			return true;
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long commerceTaxMethodId = ParamUtil.getLong(
				httpServletRequest, "commerceTaxMethodId");

			CommerceTaxMethod commerceTaxMethod =
				_commerceTaxMethodService.getCommerceTaxMethod(
					commerceTaxMethodId);

			CommerceAvalaraConnectorChannelConfiguration
				commerceAvalaraConnectorChannelConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorChannelConfiguration.class,
						new ParameterMapSettingsLocator(
							httpServletRequest.getParameterMap(),
							new GroupServiceSettingsLocator(
								commerceTaxMethod.getGroupId(),
								CommerceAvalaraConnectorChannelConfiguration.
									class.getName())));

			httpServletRequest.setAttribute(
				CommerceAvalaraConnectorChannelConfiguration.class.getName(),
				commerceAvalaraConnectorChannelConfiguration);

			if (_verifyConnection(
					httpServletRequest, commerceTaxMethod.getCompanyId())) {

				httpServletRequest.setAttribute(
					CommerceAvalaraWebKeys.CONNECTION_ESTABLISHED,
					Boolean.TRUE);

				_setCompanies(httpServletRequest);
				_setJobPreviouslyRun(httpServletRequest, commerceTaxMethod);
				_setLatestJob(httpServletRequest, commerceTaxMethod);
			}
			else {
				httpServletRequest.setAttribute(
					CommerceAvalaraWebKeys.CONNECTION_ESTABLISHED,
					Boolean.FALSE);
			}
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/avalara_settings.jsp");
	}

	private void _setCompanies(HttpServletRequest httpServletRequest) {
		Map<String, String> companies = new HashMap<>();

		try {
			companies = _commerceAvalaraConnector.getCompanyCodes();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		httpServletRequest.setAttribute(
			CommerceAvalaraWebKeys.AVALARA_COMPANIES, companies);
	}

	private void _setJobPreviouslyRun(
		HttpServletRequest httpServletRequest,
		CommerceTaxMethod commerceTaxMethod) {

		boolean jobPreviouslyRun =
			_commerceAvalaraDispatchTriggerHelper.isJobPreviouslyRun(
				commerceTaxMethod);

		httpServletRequest.setAttribute(
			CommerceAvalaraWebKeys.JOB_PREVIOUSLY_RUN, jobPreviouslyRun);
	}

	private void _setLatestJob(
		HttpServletRequest httpServletRequest,
		CommerceTaxMethod commerceTaxMethod) {

		httpServletRequest.setAttribute(
			CommerceAvalaraWebKeys.LATEST_DISPATCH_LOG,
			_commerceAvalaraDispatchTriggerHelper.getLatestDispatchLog(
				commerceTaxMethod));
	}

	private boolean _verifyConnection(
		HttpServletRequest httpServletRequest, long companyId) {

		try {
			CommerceAvalaraConnectorConfiguration
				commerceAvalaraConnectorConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorConfiguration.class,
						new ParameterMapSettingsLocator(
							httpServletRequest.getParameterMap(),
							new CompanyServiceSettingsLocator(
								companyId,
								CommerceAvalaraConnectorConfiguration.class.
									getName())));

			_commerceAvalaraConnector.verifyConnection(
				commerceAvalaraConnectorConfiguration.accountNumber(),
				commerceAvalaraConnectorConfiguration.licenseKey(),
				commerceAvalaraConnectorConfiguration.serviceURL());

			return true;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTaxMethodAvalaraScreenNavigationEntry.class);

	@Reference
	private CommerceAvalaraConnector _commerceAvalaraConnector;

	@Reference
	private CommerceAvalaraDispatchTrigger
		_commerceAvalaraDispatchTriggerHelper;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.avalara.tax.engine.fixed.web)"
	)
	private ServletContext _servletContext;

}