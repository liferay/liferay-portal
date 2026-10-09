/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.model.listener;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.model.CTSchemaVersionTable;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterRemove(Company company) {
		try {
			_configurationProvider.deleteCompanyConfiguration(
				CTSettingsConfiguration.class, company.getCompanyId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	@Override
	public void onBeforeRemove(Company company) {
		try {
			_ctCollectionLocalService.deleteCompanyCTCollections(
				company.getCompanyId());

			for (CTCollectionTemplate ctCollectionTemplate :
					_ctCollectionTemplateLocalService.getCTCollectionTemplates(
						company.getCompanyId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

				_ctCollectionTemplateLocalService.deleteCTCollectionTemplate(
					ctCollectionTemplate);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.fetchCTPreferences(
				company.getCompanyId(), 0);

		if (ctPreferences != null) {
			_ctPreferencesLocalService.deleteCTPreferences(ctPreferences);
		}

		for (CTRemote ctRemote :
				_ctRemoteLocalService.getCTRemotes(company.getCompanyId())) {

			_ctRemoteLocalService.deleteCTRemote(ctRemote);
		}

		for (CTSchemaVersion ctSchemaVersion :
				_ctSchemaVersionLocalService.<List<CTSchemaVersion>>dslQuery(
					DSLQueryFactoryUtil.select(
						CTSchemaVersionTable.INSTANCE
					).from(
						CTSchemaVersionTable.INSTANCE
					).where(
						CTSchemaVersionTable.INSTANCE.companyId.eq(
							company.getCompanyId())
					))) {

			_ctSchemaVersionLocalService.deleteCTSchemaVersion(ctSchemaVersion);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyModelListener.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTCollectionTemplateLocalService _ctCollectionTemplateLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

}