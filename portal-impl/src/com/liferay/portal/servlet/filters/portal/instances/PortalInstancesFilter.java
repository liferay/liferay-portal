/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.portal.instances;

import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * This filter is used to set the company ID in the request so that subsequent
 * calls in the thread have the company ID properly set. This filter should
 * also always be the first filter in the list of filters.
 * </p>
 *
 * @author Jorge Díaz
 */
public class PortalInstancesFilter extends BaseFilter implements TryFilter {

	@Override
	public Object doFilterTry(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		// Company ID needs to always be called here so that it is properly set
		// in subsequent calls

		try {
			PortalInstances.getCompanyId(
				httpServletRequest,
				_isVirtualHostsStrictAccess(httpServletRequest));
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			_log.error(noSuchVirtualHostException);

			httpServletRequest.setAttribute(
				WebKeys.UNKNOWN_VIRTUAL_HOST, Boolean.TRUE);

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return null;
		}

		return null;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private boolean _isVirtualHostsStrictAccess(
		HttpServletRequest httpServletRequest) {

		if (!PropsValues.VIRTUAL_HOSTS_STRICT_ACCESS ||
			((httpServletRequest.getDispatcherType() == DispatcherType.ERROR) &&
			 GetterUtil.getBoolean(
				 httpServletRequest.getAttribute(
					 WebKeys.UNKNOWN_VIRTUAL_HOST)))) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstancesFilter.class);

}