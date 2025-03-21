/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.servlet.taglib;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.monitoring.DataSample;
import com.liferay.portal.kernel.monitoring.DataSampleFactory;
import com.liferay.portal.kernel.monitoring.DataSampleThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;
import com.liferay.portal.monitoring.internal.constants.MonitoringWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	enabled = false, service = DynamicInclude.class
)
public class MonitoringTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String key) {

		if (!_monitoringConfiguration.monitorPortalRequest()) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		DataSample dataSample =
			_dataSampleFactory.createPortalRequestDataSample(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				httpServletRequest.getHeader(HttpHeaders.REFERER),
				httpServletRequest.getRemoteAddr(),
				httpServletRequest.getRemoteUser(),
				httpServletRequest.getRequestURI(),
				String.valueOf(httpServletRequest.getRequestURL()) +
					".jsp_display",
				httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

		dataSample.setDescription("Portal Request");

		dataSample.prepare();

		DataSampleThreadLocal.initialize();

		httpServletRequest.setAttribute(
			MonitoringWebKeys.PORTAL_REQUEST_DATA_SAMPLE, dataSample);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, properties);
	}

	@Reference
	private DataSampleFactory _dataSampleFactory;

	private volatile MonitoringConfiguration _monitoringConfiguration;

}