/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal;

import com.liferay.portal.kernel.monitoring.DataSample;
import com.liferay.portal.kernel.monitoring.DataSampleFactory;
import com.liferay.portal.kernel.monitoring.MethodSignature;
import com.liferay.portal.kernel.monitoring.PortletRequestType;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.monitoring.internal.statistics.portal.PortalRequestDataSample;
import com.liferay.portal.monitoring.internal.statistics.portlet.PortletRequestDataSample;
import com.liferay.portal.monitoring.internal.statistics.service.ServiceRequestDataSample;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = DataSampleFactory.class)
public class DataSampleFactoryImpl implements DataSampleFactory {

	@Override
	public DataSample createPortalRequestDataSample(
		long companyId, long groupId, String referer, String remoteAddr,
		String remoteUser, String requestURI, String requestURL,
		String userAgent) {

		return new PortalRequestDataSample(
			companyId, groupId, referer, remoteAddr, remoteUser, requestURI,
			requestURL, userAgent);
	}

	@Override
	public DataSample createPortletRequestDataSample(
		PortletRequestType requestType, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		return new PortletRequestDataSample(
			requestType, portletRequest, portletResponse, _portal);
	}

	@Override
	public DataSample createServiceRequestDataSample(
		MethodSignature methodSignature) {

		return new ServiceRequestDataSample(methodSignature);
	}

	@Reference
	private Portal _portal;

}