/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.common.spi.service.context;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nilton Vieira
 */
public class ServiceContextBuilder {

	public static ServiceContextBuilder create(
		long groupId, HttpServletRequest httpServletRequest,
		String viewableBy) {

		return new ServiceContextBuilder(
			groupId, httpServletRequest, viewableBy);
	}

	public ServiceContextBuilder assetCategoryIds(Long[] assetCategoryIds) {
		if (assetCategoryIds != null) {
			_serviceContext.setAssetCategoryIds(
				ArrayUtil.toArray(assetCategoryIds));
		}

		return this;
	}

	public ServiceContextBuilder assetTagNames(String[] assetTagNames) {
		_serviceContext.setAssetTagNames(assetTagNames);

		return this;
	}

	public ServiceContext build() {
		return _serviceContext;
	}

	public ServiceContextBuilder expandoBridgeAttributes(
		Map<String, Serializable> expandoBridgeAttributes) {

		if (expandoBridgeAttributes != null) {
			_serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);
		}

		return this;
	}

	public ServiceContextBuilder permissions(
		ModelPermissions modelPermissions) {

		if (!_serviceContext.isAddGroupPermissions() &&
			!_serviceContext.isAddGuestPermissions()) {

			_serviceContext.setModelPermissions(modelPermissions);
		}

		return this;
	}

	private ServiceContextBuilder(
		long groupId, HttpServletRequest httpServletRequest,
		String viewableBy) {

		if (httpServletRequest != null) {
			Map<String, String> headers = new HashMap<>();

			Enumeration<String> enumeration =
				httpServletRequest.getHeaderNames();

			while (enumeration.hasMoreElements()) {
				String header = enumeration.nextElement();

				String value = httpServletRequest.getHeader(header);

				headers.put(header, value);
			}

			_serviceContext.setHeaders(headers);
		}

		if (StringUtil.equalsIgnoreCase(viewableBy, "anyone")) {
			_serviceContext.setAddGroupPermissions(true);
			_serviceContext.setAddGuestPermissions(true);
		}
		else if (StringUtil.equalsIgnoreCase(viewableBy, "members")) {
			_serviceContext.setAddGroupPermissions(true);
			_serviceContext.setAddGuestPermissions(false);
		}
		else {
			_serviceContext.setAddGroupPermissions(false);
			_serviceContext.setAddGuestPermissions(false);
		}

		_serviceContext.setScopeGroupId(groupId);
	}

	private final ServiceContext _serviceContext = new ServiceContext();

}