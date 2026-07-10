/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.installer;

import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.unit.BatchEngineUnit;
import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(service = FileInstaller.class)
public class BatchEngineFileInstaller implements FileInstaller {

	@Override
	public boolean canTransformURL(File file) {
		String fileName = file.getName();

		if (!StringUtil.endsWith(fileName, ".zip") ||
			_isClientExtension(file)) {

			return false;
		}

		try (ZipFile zipFile = new ZipFile(file)) {
			for (BatchEngineUnit batchEngineUnit :
					_getBatchEngineUnitsCollection(zipFile)) {

				if (!batchEngineUnit.isValid()) {
					continue;
				}

				BatchEngineUnitConfiguration batchEngineUnitConfiguration =
					_getBatchEngineUnitConfiguration(batchEngineUnit);

				if ((batchEngineUnitConfiguration != null) &&
					(batchEngineUnitConfiguration.getCompanyId() > 0) &&
					(batchEngineUnitConfiguration.getUserId() > 0) &&
					Validator.isNotNull(
						batchEngineUnitConfiguration.getClassName()) &&
					Validator.isNotNull(
						batchEngineUnitConfiguration.getVersion())) {

					return true;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(new AutoDeployException(exception));
			}
		}

		return false;
	}

	public boolean isBatchEngineTechnical(String zipEntryName) {
		return zipEntryName.endsWith(
			BatchEngineTaskContentType.JSONT.getFileExtension());
	}

	@Override
	public URL transformURL(File file) throws AutoDeployException {
		try (ZipFile zipFile = new ZipFile(file)) {
			_deploy(zipFile);
		}
		catch (Exception exception) {
			throw new AutoDeployException(exception);
		}

		return null;
	}

	@Override
	public void uninstall(File file) {
	}

	private void _deploy(ZipFile zipFile) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Deploying batch engine file " + zipFile.getName());
		}

		try {
			_batchEngineUnitProcessor.processBatchEngineUnits(
				_getBatchEngineUnitsCollection(zipFile));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to process batch engine units of file " +
					zipFile.getName(),
				exception);
		}
	}

	private BatchEngineUnitConfiguration _getBatchEngineUnitConfiguration(
			BatchEngineUnit batchEngineUnit)
		throws IOException {

		BatchEngineUnitConfiguration batchEngineUnitConfiguration =
			batchEngineUnit.getBatchEngineUnitConfiguration();

		if (batchEngineUnitConfiguration.getCompanyId() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Using default company ID for this batch process");
			}

			try {
				Company company = _companyLocalService.getCompanyByWebId(
					PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));

				batchEngineUnitConfiguration.setCompanyId(
					company.getCompanyId());
			}
			catch (PortalException portalException) {
				_log.error("Unable to get default company ID", portalException);
			}
		}

		if (batchEngineUnitConfiguration.getUserId() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Using default user ID for this batch process");
			}

			try {
				List<User> users = _userLocalService.getUsersByRoleName(
					batchEngineUnitConfiguration.getCompanyId(),
					RoleConstants.ADMINISTRATOR, 0, 1);

				User user = users.get(0);

				batchEngineUnitConfiguration.setUserId(user.getUserId());
			}
			catch (PortalException portalException) {
				_log.error("Unable to get default user ID", portalException);
			}
		}

		return batchEngineUnitConfiguration;
	}

	private Collection<BatchEngineUnit> _getBatchEngineUnitsCollection(
		ZipFile zipFile) {

		Map<String, ZipEntry> batchEngineZipEntries = new HashMap<>();
		Map<String, BatchEngineUnit> batchEngineUnits = new TreeMap<>(
			new NaturalOrderStringComparator());
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}

			String key = _getBatchEngineZipEntryKey(zipEntry);

			ZipEntry complementZipEntry = batchEngineZipEntries.get(key);

			if (complementZipEntry == null) {
				batchEngineZipEntries.put(key, zipEntry);

				batchEngineUnits.put(
					key, new AdvancedZipBatchEngineUnitImpl(zipFile, zipEntry));

				continue;
			}

			batchEngineUnits.put(
				key,
				new ClassicZipBatchEngineUnitImpl(
					zipFile, zipEntry, complementZipEntry));

			batchEngineZipEntries.remove(key);
		}

		return batchEngineUnits.values();
	}

	private String _getBatchEngineZipEntryKey(ZipEntry zipEntry) {
		String zipEntryName = zipEntry.getName();

		if (isBatchEngineTechnical(zipEntryName)) {
			return zipEntryName;
		}

		if (!zipEntryName.contains(StringPool.SLASH)) {
			return StringPool.BLANK;
		}

		return zipEntryName.substring(
			0, zipEntryName.lastIndexOf(StringPool.SLASH));
	}

	private boolean _isClientExtension(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (Objects.equals(
						name, "WEB-INF/liferay-plugin-package.properties") ||
					(name.endsWith(".client-extension-config.json") &&
					 (name.indexOf('/') == -1))) {

					return true;
				}
			}

			return false;
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to check if " + file + " is a client extension",
				ioException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineFileInstaller.class);

	@Reference
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private UserLocalService _userLocalService;

}