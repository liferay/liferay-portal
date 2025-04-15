/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.monitoring;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

/**
 * @author Michael C. Han
 */
public interface DataSampleFactory {

	public DataSample createPortalRequestDataSample(
		long companyId, long groupId, String referer, String remoteAddr,
		String remoteUser, String requestURI, String requestURL,
		String userAgent);

	public DataSample createPortletRequestDataSample(
		PortletRequestType requestType, PortletRequest portletRequest,
		PortletResponse portletResponse);

	public DataSample createServiceRequestDataSample(
		MethodSignature methodSignature);

}