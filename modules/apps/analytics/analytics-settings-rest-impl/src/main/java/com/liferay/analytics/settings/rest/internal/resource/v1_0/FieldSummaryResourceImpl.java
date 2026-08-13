/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.constants.FieldAccountConstants;
import com.liferay.analytics.settings.rest.constants.FieldPeopleConstants;
import com.liferay.analytics.settings.rest.dto.v1_0.FieldSummary;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.FieldSummaryResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Riccardo Ferrari
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/field-summary.properties",
	scope = ServiceScope.PROTOTYPE, service = FieldSummaryResource.class
)
public class FieldSummaryResourceImpl extends BaseFieldSummaryResourceImpl {

	@Override
	public FieldSummary getField() throws Exception {
		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId());

		return new FieldSummary() {
			{
				setAccount(
					() -> {
						String[] syncedAccountFieldNames = _getOrDefault(
							FieldAccountConstants.FIELD_ACCOUNT_DEFAULTS,
							analyticsConfiguration.syncedAccountFieldNames());

						return syncedAccountFieldNames.length;
					});
				setPeople(
					() -> {
						String[] syncedContactFieldNames = _getOrDefault(
							FieldPeopleConstants.FIELD_CONTACT_DEFAULTS,
							analyticsConfiguration.syncedContactFieldNames());
						String[] syncedUserFieldNames = _getOrDefault(
							FieldPeopleConstants.FIELD_USER_DEFAULTS,
							analyticsConfiguration.syncedUserFieldNames());

						return syncedContactFieldNames.length +
							syncedUserFieldNames.length;
					});
			}
		};
	}

	private String[] _getOrDefault(
		String[] defaultFieldNames, String[] fieldNames) {

		if ((fieldNames != null) && (fieldNames.length > 0)) {
			return fieldNames;
		}

		return defaultFieldNames;
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

}