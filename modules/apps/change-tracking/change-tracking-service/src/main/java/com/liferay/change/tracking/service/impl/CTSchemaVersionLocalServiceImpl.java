/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.service.base.CTSchemaVersionLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.version.Version;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.model.CTSchemaVersion",
	service = AopService.class
)
public class CTSchemaVersionLocalServiceImpl
	extends CTSchemaVersionLocalServiceBaseImpl {

	@Override
	public CTSchemaVersion addLatestCTSchemaVersion(long companyId) {
		CTSchemaVersion ctSchemaVersion = ctSchemaVersionPersistence.create(
			counterLocalService.increment(CTSchemaVersion.class.getName()));

		ctSchemaVersion.setCompanyId(companyId);

		Map<String, Serializable> schemaContext = new HashMap<>();

		for (Release release :
				_releaseLocalService.getReleases(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			schemaContext.put(
				release.getServletContextName(), release.getSchemaVersion());
		}

		ctSchemaVersion.setSchemaContext(schemaContext);

		return ctSchemaVersionPersistence.update(ctSchemaVersion);
	}

	@Override
	public CTSchemaVersion getLatestCTSchemaVersion(long companyId) {
		CTSchemaVersion ctSchemaVersion =
			ctSchemaVersionPersistence.fetchByCompanyId_First(companyId, null);

		if ((ctSchemaVersion == null) ||
			!isLatestCTSchemaVersion(ctSchemaVersion, true)) {

			ctSchemaVersion =
				ctSchemaVersionLocalService.addLatestCTSchemaVersion(companyId);
		}

		return ctSchemaVersion;
	}

	@Override
	public boolean isLatestCTSchemaVersion(
		CTSchemaVersion ctSchemaVersion, boolean strict) {

		try {
			CTSettingsConfiguration ctSettingsConfiguration =
				_configurationProvider.getCompanyConfiguration(
					CTSettingsConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			if (!ctSettingsConfiguration.schemaVersionCheckEnabled()) {
				return true;
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		if (ctSchemaVersion == null) {
			return false;
		}

		Map<String, Serializable> schemaContext =
			ctSchemaVersion.getSchemaContext();

		List<Release> releases = _releaseLocalService.getReleases(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		if (releases.size() != schemaContext.size()) {
			ctSchemaVersionLocalService.addLatestCTSchemaVersion(
				ctSchemaVersion.getCompanyId());

			return false;
		}

		for (Release release : releases) {
			String ctReleaseSchemaVersion = (String)schemaContext.get(
				release.getServletContextName());

			if (Objects.equals(
					ctReleaseSchemaVersion, release.getSchemaVersion())) {

				continue;
			}

			if (strict) {
				return false;
			}

			Version version1 = Version.parseVersion(ctReleaseSchemaVersion);
			Version version2 = Version.parseVersion(release.getSchemaVersion());

			if ((version1.getMajor() != version2.getMajor()) ||
				(version1.getMinor() != version2.getMinor())) {

				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isLatestCTSchemaVersion(long ctSchemaVersionId) {
		CTSchemaVersion ctSchemaVersion =
			ctSchemaVersionPersistence.fetchByPrimaryKey(ctSchemaVersionId);

		return isLatestCTSchemaVersion(ctSchemaVersion, false);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTSchemaVersionLocalServiceImpl.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ReleaseLocalService _releaseLocalService;

}