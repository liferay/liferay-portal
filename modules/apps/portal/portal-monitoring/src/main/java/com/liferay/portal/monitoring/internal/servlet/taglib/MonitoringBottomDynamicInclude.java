/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.monitoring.DataSample;
import com.liferay.portal.kernel.monitoring.DataSampleThreadLocal;
import com.liferay.portal.kernel.monitoring.RequestStatus;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;
import com.liferay.portal.monitoring.internal.constants.MonitoringWebKeys;
import com.liferay.portal.monitoring.internal.statistics.portal.PortalRequestDataSample;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	enabled = false, service = DynamicInclude.class
)
public class MonitoringBottomDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!_monitoringConfiguration.monitorPortalRequest()) {
			return;
		}

		PortalRequestDataSample portalRequestDataSample =
			(PortalRequestDataSample)httpServletRequest.getAttribute(
				MonitoringWebKeys.PORTAL_REQUEST_DATA_SAMPLE);

		if (portalRequestDataSample != null) {
			portalRequestDataSample.capture(RequestStatus.SUCCESS);

			portalRequestDataSample.setStatusCode(
				httpServletResponse.getStatus());

			DataSampleThreadLocal.addDataSample(portalRequestDataSample);
		}

		List<DataSample> dataSamples = DataSampleThreadLocal.getDataSamples();

		if (!_monitoringConfiguration.showPerRequestDataSample() ||
			ListUtil.isEmpty(dataSamples)) {

			return;
		}

		StringBundler sb = new StringBundler((dataSamples.size() * 2) + 2);

		sb.append("<!--\n");

		for (DataSample curDataSample : dataSamples) {
			sb.append(HtmlUtil.escape(curDataSample.toString()));
			sb.append("\n");
		}

		sb.append("-->");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.println(sb);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, properties);
	}

	private volatile MonitoringConfiguration _monitoringConfiguration;

}