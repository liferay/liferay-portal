/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.launch.LaunchThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.preview.PreviewableResolverUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = {
		"after-filter=Launch Preview Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Launch Edit Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class LaunchEditFilter extends BaseLaunchFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if ((LaunchThreadLocal.getMode() != null) ||
			(PreviewableResolverUtil.getPreviewId() != null)) {

			processFilter(
				LaunchEditFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		long companyId = CompanyThreadLocal.getCompanyId();
		long launchSetId = _getLaunchSetId(httpServletRequest);

		if ((launchSetId <= 0) ||
			!_isEditable(companyId, httpServletRequest, launchSetId)) {

			processFilter(
				LaunchEditFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			getPreviewableMap(companyId, launchSetId);

		try (SafeCloseable safeCloseable =
				LaunchThreadLocal.setLaunchSetIdWithSafeCloseable(
					launchSetId, LaunchThreadLocal.Mode.EDIT)) {

			processFilter(
				filterChain, httpServletRequest, httpServletResponse,
				previewableMap);
		}
	}

	private long _getLaunchSetId(HttpServletRequest httpServletRequest) {
		long launchSetId = ParamUtil.getLong(
			httpServletRequest, "editLaunchSetId");

		if (launchSetId > 0) {
			return launchSetId;
		}

		launchSetId = ParamUtil.getLong(
			httpServletRequest,
			StringBundler.concat(
				StringPool.UNDERLINE,
				ParamUtil.getString(httpServletRequest, "p_p_id"),
				"_editLaunchSetId"));

		if (launchSetId > 0) {
			return launchSetId;
		}

		return GetterUtil.getLong(
			httpServletRequest.getHeader("X-Liferay-Launch-Set-Id"));
	}

	private boolean _isEditable(
			long companyId, HttpServletRequest httpServletRequest,
			long launchSetId)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			return false;
		}

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchSet");
		ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
			launchSetId);

		if ((objectDefinition == null) || (objectEntry == null) ||
			(objectDefinition.getObjectDefinitionId() !=
				objectEntry.getObjectDefinitionId()) ||
			objectEntry.isApproved()) {

			return false;
		}

		return _objectEntryService.hasModelResourcePermission(
			user, launchSetId, ActionKeys.VIEW);
	}

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}