/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.model.listener;

import com.liferay.analytics.batch.exportimport.model.listener.BaseAnalyticsDXPEntityModelListener;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = ModelListener.class)
public class ExpandoColumnModelListener
	extends BaseAnalyticsDXPEntityModelListener<ExpandoColumn> {

	@Override
	public void onAfterRemove(ExpandoColumn expandoColumn) {
		if (!FeatureFlagManagerUtil.isEnabled(
				expandoColumn.getCompanyId(), "LRAC-10757")) {

			try {
				AnalyticsConfiguration analyticsConfiguration =
					_analyticsSettingsManager.getAnalyticsConfiguration(
						expandoColumn.getCompanyId());

				String[] syncedUserFieldNames =
					analyticsConfiguration.syncedUserFieldNames();

				if (syncedUserFieldNames.length == 0) {
					return;
				}

				_analyticsSettingsManager.updateCompanyConfiguration(
					expandoColumn.getCompanyId(),
					HashMapBuilder.<String, Object>put(
						"previousSyncedUserFieldNames", syncedUserFieldNames
					).<String, Serializable>put(
						"syncedUserFieldNames",
						ArrayUtil.filter(
							syncedUserFieldNames,
							syncedUserFieldName -> !Objects.equals(
								syncedUserFieldName, expandoColumn.getName()))
					).build());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
		else {
			super.onAfterRemove(expandoColumn);
		}
	}

	@Override
	protected boolean isTracked(ExpandoColumn expandoColumn) {
		if (_isCustomField(
				Organization.class.getName(), expandoColumn.getTableId())) {

			return true;
		}

		if (_isCustomField(User.class.getName(), expandoColumn.getTableId())) {
			AnalyticsConfiguration analyticsConfiguration =
				analyticsConfigurationRegistry.getAnalyticsConfiguration(
					expandoColumn.getCompanyId());

			if (ArrayUtil.isEmpty(
					analyticsConfiguration.syncedUserFieldNames())) {

				return false;
			}

			for (String syncedUserFieldName :
					analyticsConfiguration.syncedUserFieldNames()) {

				if (Objects.equals(
						expandoColumn.getName(), syncedUserFieldName)) {

					return true;
				}
			}

			return false;
		}

		return false;
	}

	private boolean _isCustomField(String className, long tableId) {
		long classNameId = _classNameLocalService.getClassNameId(className);

		try {
			ExpandoTable expandoTable = _expandoTableLocalService.getTable(
				tableId);

			if (Objects.equals(
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					expandoTable.getName()) &&
				(expandoTable.getClassNameId() == classNameId)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get expando table " + tableId, exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoColumnModelListener.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

}