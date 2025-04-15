/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

/**
 * @author Eduardo García
 */
public abstract class BaseRSSMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		if (isRSSFeedsEnabled(resourceRequest)) {
			try {
				PortletResponseUtil.sendFile(
					resourceRequest, resourceResponse, null,
					getRSS(resourceRequest, resourceResponse),
					ContentTypes.TEXT_XML_UTF8);
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}
		else {
			try {
				PortalUtil.sendRSSFeedsDisabledError(
					resourceRequest, resourceResponse);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return false;
	}

	protected abstract byte[] getRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception;

	protected boolean isRSSFeedsEnabled(ResourceRequest resourceRequest) {
		return PortalUtil.isRSSFeedsEnabled();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseRSSMVCResourceCommand.class);

}