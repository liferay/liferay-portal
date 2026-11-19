/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.url.provider;

import com.liferay.launch.url.provider.LaunchEditURLProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = LaunchEditURLProvider.class)
public class LaunchEditURLProviderImpl implements LaunchEditURLProvider {

	@Override
	public String getEditURL(long launchSetId, String url) {
		if ((launchSetId <= 0) || Validator.isNull(url)) {
			return url;
		}

		return HttpComponentsUtil.setParameter(
			url, "editLaunchSetId", launchSetId);
	}

	@Override
	public String getEditURL(String className, long draftId, String url) {
		long companyId = CompanyThreadLocal.getCompanyId();

		if ((draftId <= 0) ||
			!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-72278")) {

			return url;
		}

		try {
			return getEditURL(
				_getLaunchSetId(className, companyId, draftId), url);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return url;
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

	private long _getLaunchSetId(String className, long companyId, long draftId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "LaunchEntry");

		if (objectDefinition == null) {
			return 0;
		}

		Group companyGroup = _groupLocalService.getCompanyGroup(companyId);
		Predicate predicate = _getEqualsPredicate(
			"className", objectDefinition.getObjectDefinitionId(), className);

		List<Map<String, Serializable>> launchEntryValuesList =
			_objectEntryLocalService.getValuesList(
				companyGroup.getGroupId(), companyId,
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				predicate.and(
					_getEqualsPredicate(
						"draftId", objectDefinition.getObjectDefinitionId(),
						draftId)),
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (launchEntryValuesList.isEmpty()) {
			return 0;
		}

		return MapUtil.getLong(
			launchEntryValuesList.get(0),
			"r_launchSetToLaunchEntries_c_launchSetId");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchEditURLProviderImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}