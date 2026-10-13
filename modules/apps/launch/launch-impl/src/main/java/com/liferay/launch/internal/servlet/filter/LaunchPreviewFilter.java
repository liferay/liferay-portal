/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.launch.LaunchPreviewThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
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
			long objectDefinitionId, String objectFieldName, Object value)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinitionId, objectFieldName);

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
				objectDefinition.getObjectDefinitionId(),
				"r_launchSetToLaunchEntries_c_launchSetId", launchSetId),
			null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private Serializable _getPreviewPrimaryKey(
			long classPK, LaunchEntryType launchEntryType,
			Map<String, Serializable> launchEntryValues,
			LaunchEntryType.Version publishedVersion)
		throws Exception {

		LaunchEntryType.Draft draft = launchEntryType.fetchDraft(
			classPK, GetterUtil.getLong(launchEntryValues.get("draftId")));

		if (draft != null) {
			return draft.getPreviewPrimaryKey();
		}

		String versionKey = MapUtil.getString(launchEntryValues, "versionKey");

		if (Objects.equals(publishedVersion.getVersionKey(), versionKey)) {
			return null;
		}

		LaunchEntryType.Version version = launchEntryType.fetchVersion(
			classPK, versionKey);

		if (version == null) {
			return null;
		}

		return version.getPreviewPrimaryKey();
	}

	private Map<Class<?>, Map<Serializable, Serializable>> _getPreviewableMap(
			long companyId, long launchSetId)
		throws Exception {

		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
			new HashMap<>();

		List<Map<String, Serializable>> launchEntryValuesList =
			_getLaunchEntryValuesList(companyId, launchSetId);

		for (Map<String, Serializable> launchEntryValues :
				launchEntryValuesList) {

			String className = MapUtil.getString(
				launchEntryValues, "className");

			LaunchEntryType launchEntryType =
				_launchEntryTypeRegistry.getLaunchEntryType(className);

			if (launchEntryType == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("No launch entry type exists for " + className);
				}

				continue;
			}

			long classPK = GetterUtil.getLong(launchEntryValues.get("classPK"));

			LaunchEntryType.Item item = launchEntryType.fetchItem(classPK);
			LaunchEntryType.Version publishedVersion =
				launchEntryType.fetchPublishedVersion(classPK);

			if ((item == null) || (publishedVersion == null)) {
				continue;
			}

			Serializable previewPrimaryKey = _getPreviewPrimaryKey(
				classPK, launchEntryType, launchEntryValues, publishedVersion);

			if (previewPrimaryKey == null) {
				continue;
			}

			Map<Serializable, Serializable> primaryKeys =
				previewableMap.computeIfAbsent(
					launchEntryType.getModelClass(), key -> new HashMap<>());

			primaryKeys.put(item.getPrimaryKey(), previewPrimaryKey);
		}

		return previewableMap;
	}

	private void _processPreviewFilter(
			long companyId, FilterChain filterChain,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long previewLaunchSetId)
		throws Exception {

		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
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
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private Portal _portal;

}