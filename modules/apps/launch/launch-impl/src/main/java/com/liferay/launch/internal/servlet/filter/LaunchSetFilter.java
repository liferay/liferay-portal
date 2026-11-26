/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.internal.util.LaunchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.launch.LaunchSetMode;
import com.liferay.portal.kernel.launch.LaunchSetThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.preview.PreviewableResolverUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Carlos Correa
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Launch Set Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class LaunchSetFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return FeatureFlagManagerUtil.isEnabled(
			CompanyThreadLocal.getCompanyId(), "LPD-72278");
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if ((LaunchSetThreadLocal.getLaunchSetMode() != null) ||
			(PreviewableResolverUtil.getPreviewId() != null)) {

			processFilter(
				LaunchSetFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		boolean fromHeader = false;
		long launchSetId = ParamUtil.getLong(
			httpServletRequest, "p_launch_set_id", -1);
		String launchSetModeString = ParamUtil.getString(
			httpServletRequest, "p_launch_set_mode");

		if ((launchSetId == -1) && Validator.isNull(launchSetModeString)) {
			fromHeader = true;
			launchSetId = GetterUtil.getLong(
				httpServletRequest.getHeader("X-Liferay-Launch-Set-Id"), -1);
			launchSetModeString = httpServletRequest.getHeader(
				"X-Liferay-Launch-Set-Mode");
		}

		LaunchSetMode launchSetMode = LaunchSetMode.parse(launchSetModeString);

		if (launchSetMode == LaunchSetMode.EDIT) {
			if ((launchSetId > 0) &&
				_isEditable(httpServletRequest, launchSetId)) {

				_processFilter(
					filterChain, httpServletRequest, httpServletResponse,
					launchSetId, LaunchSetMode.EDIT);

				return;
			}
		}
		else if ((launchSetMode == LaunchSetMode.PREVIEW) ||
				 ((launchSetId != -1) &&
				  Validator.isNull(launchSetModeString))) {

			if (!fromHeader &&
				!Objects.equals(
					ParamUtil.getString(httpServletRequest, "p_l_mode"),
					Constants.PREVIEW)) {

				httpServletResponse.sendRedirect(
					HttpComponentsUtil.addParameter(
						_portal.getCurrentURL(httpServletRequest), "p_l_mode",
						Constants.PREVIEW));

				return;
			}

			User user = _portal.getUser(httpServletRequest);

			if ((user != null) && !user.isGuestUser()) {
				_processFilter(
					filterChain, httpServletRequest, httpServletResponse,
					Math.max(launchSetId, 0), LaunchSetMode.PREVIEW);

				return;
			}

			if (!fromHeader) {
				httpServletResponse.sendRedirect(
					HttpComponentsUtil.addParameter(
						_portal.getPathMain() + "/portal/login", "redirect",
						_portal.getCurrentCompleteURL(httpServletRequest)));

				return;
			}
		}

		processFilter(
			LaunchSetFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	private List<Map<String, Serializable>> _getLaunchEntryValuesList(
			long companyId, long launchSetId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchEntry");

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);

		return _objectEntryLocalService.getValuesList(
			companyGroup.getGroupId(), companyId, objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			LaunchUtil.getEqualsPredicate(
				"r_launchSetToLaunchEntries_c_launchSetId",
				objectDefinition.getObjectDefinitionId(), launchSetId),
			null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private UnsafeSupplier<BaseModel<?>, PortalException>
			_getPreviewBaseModelUnsafeSupplier(
				long classPK, LaunchEntryManager launchEntryManager,
				Map<String, Serializable> launchEntryValues,
				LaunchEntryManager.Version publishedVersion)
		throws Exception {

		LaunchEntryManager.Draft draft = launchEntryManager.fetchDraft(
			classPK, MapUtil.getLong(launchEntryValues, "draftId"));

		if (draft != null) {
			return draft::getPreviewBaseModel;
		}

		String versionKey = MapUtil.getString(launchEntryValues, "versionKey");

		if (Objects.equals(publishedVersion.getVersionKey(), versionKey)) {
			return null;
		}

		LaunchEntryManager.Version version = launchEntryManager.fetchVersion(
			classPK, versionKey);

		if (version == null) {
			return null;
		}

		return version::getPreviewBaseModel;
	}

	private Map<Class<?>, Map<Serializable, Object>> _getPreviewableMap(
			long companyId, long launchSetId)
		throws Exception {

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			new HashMap<>();

		for (Map<String, Serializable> launchEntryValues :
				_getLaunchEntryValuesList(companyId, launchSetId)) {

			String className = MapUtil.getString(
				launchEntryValues, "className");

			LaunchEntryManager launchEntryManager =
				_launchEntryManagerRegistry.getLaunchEntryManager(className);

			if (launchEntryManager == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"No launch entry manager exists for " + className);
				}

				continue;
			}

			long classPK = MapUtil.getLong(launchEntryValues, "classPK");

			LaunchEntryManager.Item item = launchEntryManager.fetchItem(
				classPK);
			LaunchEntryManager.Version publishedVersion =
				launchEntryManager.fetchPublishedVersion(classPK);

			if ((item == null) || (publishedVersion == null)) {
				continue;
			}

			UnsafeSupplier<BaseModel<?>, PortalException> unsafeSupplier =
				_getPreviewBaseModelUnsafeSupplier(
					classPK, launchEntryManager, launchEntryValues,
					publishedVersion);

			if (unsafeSupplier == null) {
				continue;
			}

			Map<Serializable, Object> previewValues =
				previewableMap.computeIfAbsent(
					launchEntryManager.getModelClass(), key -> new HashMap<>());

			previewValues.put(
				item.getPrimaryKey(),
				new UnsafeSupplierValue<>(unsafeSupplier));
		}

		return previewableMap;
	}

	private boolean _isEditable(
			HttpServletRequest httpServletRequest, long launchSetId)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				CompanyThreadLocal.getCompanyId(), "LaunchSet");
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
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

	private void _processFilter(
			FilterChain filterChain, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long launchSetId,
			LaunchSetMode launchSetMode)
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				LaunchSetThreadLocal.setLaunchSetIdWithSafeCloseable(
					launchSetId, launchSetMode)) {

			Map<Class<?>, Map<Serializable, Object>> previewableMap =
				_getPreviewableMap(
					CompanyThreadLocal.getCompanyId(), launchSetId);

			if (previewableMap.isEmpty()) {
				processFilter(
					LaunchSetFilter.class.getName(), httpServletRequest,
					httpServletResponse, filterChain);

				return;
			}

			Long previewId = PreviewableResolverUtil.addPreviewableMap(
				previewableMap);

			try (SafeCloseable safeCloseable2 =
					PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
						previewId)) {

				processFilter(
					LaunchSetFilter.class.getName(), httpServletRequest,
					httpServletResponse, filterChain);
			}
			finally {
				PreviewableResolverUtil.removePreviewableMap(previewId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetFilter.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}