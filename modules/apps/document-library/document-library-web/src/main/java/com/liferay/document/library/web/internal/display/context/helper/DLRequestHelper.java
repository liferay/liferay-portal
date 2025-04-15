/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.DLGroupServiceSettings;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera
 */
public class DLRequestHelper extends BaseRequestHelper {

	public DLRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public DLGroupServiceSettings getDLGroupServiceSettings() {
		if (_dlGroupServiceSettings != null) {
			return _dlGroupServiceSettings;
		}

		HttpServletRequest httpServletRequest = getRequest();

		_dlGroupServiceSettings =
			(DLGroupServiceSettings)httpServletRequest.getAttribute(
				DLWebKeys.DOCUMENT_LIBRARY_GROUP_SERVICE_SETTINGS);

		if (_dlGroupServiceSettings != null) {
			return _dlGroupServiceSettings;
		}

		String portletResource = getPortletResource();

		try {
			if (Validator.isNotNull(portletResource)) {
				_dlGroupServiceSettings = DLGroupServiceSettings.getInstance(
					getScopeGroupId(), httpServletRequest.getParameterMap());
			}
			else {
				_dlGroupServiceSettings = DLGroupServiceSettings.getInstance(
					getScopeGroupId());
			}
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		httpServletRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_GROUP_SERVICE_SETTINGS,
			_dlGroupServiceSettings);

		return _dlGroupServiceSettings;
	}

	public DLPortletInstanceSettings getDLPortletInstanceSettings() {
		if (_dlPortletInstanceSettings != null) {
			return _dlPortletInstanceSettings;
		}

		HttpServletRequest httpServletRequest = getRequest();

		_dlPortletInstanceSettings =
			(DLPortletInstanceSettings)httpServletRequest.getAttribute(
				DLWebKeys.DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS);

		if (_dlPortletInstanceSettings != null) {
			return _dlPortletInstanceSettings;
		}

		String portletResource = getPortletResource();

		try {
			if (Validator.isNotNull(portletResource)) {
				_dlPortletInstanceSettings =
					DLPortletInstanceSettings.getInstance(
						getLayout(), getResourcePortletId(),
						httpServletRequest.getParameterMap());
			}
			else {
				_dlPortletInstanceSettings =
					DLPortletInstanceSettings.getInstance(
						getLayout(), getPortletId());
			}
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}

		httpServletRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_PORTLET_INSTANCE_SETTINGS,
			_dlPortletInstanceSettings);

		return _dlPortletInstanceSettings;
	}

	private DLGroupServiceSettings _dlGroupServiceSettings;
	private DLPortletInstanceSettings _dlPortletInstanceSettings;

}