/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayResourceResponse;
import com.liferay.portlet.internal.ResourceRequestImpl;
import com.liferay.portlet.internal.ResourceResponseImpl;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.filter.ResourceRequestWrapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class ResourceResponseFactory {

	public static LiferayResourceResponse create(
		ResourceRequest resourceRequest,
		HttpServletResponse httpServletResponse) {

		while (resourceRequest instanceof ResourceRequestWrapper) {
			ResourceRequestWrapper resourceRequestWrapper =
				(ResourceRequestWrapper)resourceRequest;

			resourceRequest = resourceRequestWrapper.getRequest();
		}

		ResourceResponseImpl resourceResponseImpl = new ResourceResponseImpl();

		resourceResponseImpl.init(
			(ResourceRequestImpl)resourceRequest, httpServletResponse);

		return resourceResponseImpl;
	}

}