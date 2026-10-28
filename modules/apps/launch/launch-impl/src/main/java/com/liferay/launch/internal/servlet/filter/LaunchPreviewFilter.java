/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.launch.LaunchPreviewThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Launch Preview Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class LaunchPreviewFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long previewLaunchSetId = ParamUtil.getLong(
			httpServletRequest, "previewLaunchSetId", -1);

		if ((previewLaunchSetId == -1) ||
			(PreviewableResolverUtil.getPreviewId() != null)) {

			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-72278")) {
			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		String mode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!mode.equals(Constants.PREVIEW)) {
			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameter(
					_portal.getCurrentURL(httpServletRequest), "p_l_mode",
					Constants.PREVIEW));

			return;
		}

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isGuestUser()) {
			_portal.sendError(
				new PrincipalException.MustBeAuthenticated(
					(user == null) ? 0 : user.getUserId()),
				httpServletRequest, httpServletResponse);

			return;
		}

		try (SafeCloseable safeCloseable =
				LaunchPreviewThreadLocal.setLaunchSetIdWithSafeCloseable(
					previewLaunchSetId)) {

			_processPreviewFilter(
				companyId, filterChain, httpServletRequest, httpServletResponse,
				previewLaunchSetId);
		}
	}

	private Predicate _getEqualsPredicate(
			String fieldName, long objectDefinitionId, Object value)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, fieldName);

		Table<?> table = _objectFieldLocalService.getTable(
			objectDefinitionId, objectField.getName());

		Column<?, Object> column = (Column<?, Object>)table.getColumn(
			objectField.getDBColumnName());

		return column.eq(value);
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
			_getEqualsPredicate(
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

	private void _processPreviewFilter(
			long companyId, FilterChain filterChain,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long previewLaunchSetId)
		throws Exception {

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			_getPreviewableMap(companyId, previewLaunchSetId);

		if (previewableMap.isEmpty()) {
			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			previewableMap);

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			processFilter(
				LaunchPreviewFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchPreviewFilter.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

}