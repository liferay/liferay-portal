/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.internal.util.LaunchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.preview.PreviewableResolverUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Carlos Correa
 */
public abstract class BaseLaunchFilter extends BasePortalFilter {

	protected Map<Class<?>, Map<Serializable, Object>> getPreviewableMap(
			long companyId, long launchSetId)
		throws Exception {

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			new HashMap<>();

		for (Map<String, Serializable> launchEntryValues :
				_getLaunchEntryValuesList(companyId, launchSetId)) {

			String className = MapUtil.getString(
				launchEntryValues, "className");

			LaunchEntryManager launchEntryManager =
				launchEntryManagerRegistry.getLaunchEntryManager(className);

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

	protected void processFilter(
			FilterChain filterChain, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Map<Class<?>, Map<Serializable, Object>> previewableMap)
		throws Exception {

		Class<?> clazz = getClass();

		if (previewableMap.isEmpty()) {
			processFilter(
				clazz.getName(), httpServletRequest, httpServletResponse,
				filterChain);

			return;
		}

		Long previewId = PreviewableResolverUtil.addPreviewableMap(
			previewableMap);

		try (SafeCloseable safeCloseable =
				PreviewableResolverUtil.setPreviewIdWithSafeCloseable(
					previewId)) {

			processFilter(
				clazz.getName(), httpServletRequest, httpServletResponse,
				filterChain);
		}
		finally {
			PreviewableResolverUtil.removePreviewableMap(previewId);
		}
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected LaunchEntryManagerRegistry launchEntryManagerRegistry;

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	private List<Map<String, Serializable>> _getLaunchEntryValuesList(
			long companyId, long launchSetId)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchEntry");

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		Group companyGroup = groupLocalService.getCompanyGroup(companyId);

		return objectEntryLocalService.getValuesList(
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

	private static final Log _log = LogFactoryUtil.getLog(
		BaseLaunchFilter.class);

}