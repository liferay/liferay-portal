/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.upgrade.helper;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryConfiguration;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration;
import com.liferay.document.library.internal.constants.LegacyDLKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = DLConfigurationUpgradeHelper.class)
public class DLConfigurationUpgradeHelper {

	public static final String CLASS_NAME_DL_FILE_ENTRY_CONFIGURATION =
		"com.liferay.document.library.configuration.DLFileEntryConfiguration";

	public static final String CLASS_NAME_PDF_PREVIEW_CONFIGURATION =
		"com.liferay.document.library.preview.pdf.internal.configuration." +
			"PDFPreviewConfiguration";

	public void deleteConfigurations(String className) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format("(%s=%s*)", Constants.SERVICE_PID, className));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			configuration.delete();
		}
	}

	public long getDLFileEntryConfigurationPreviewableProcessorMaxSize()
		throws Exception {

		Configuration dlFileEntryConfiguration = getSystemConfiguration(
			CLASS_NAME_DL_FILE_ENTRY_CONFIGURATION);

		if (dlFileEntryConfiguration != null) {
			return _getAttributeValue(
				dlFileEntryConfiguration, "previewableProcessorMaxSize",
				DLFileEntryConfigurationConstants.
					PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT);
		}

		return DLFileEntryConfigurationConstants.
			PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT;
	}

	public Configuration getSystemConfiguration(String className)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format("(%s=%s)", Constants.SERVICE_PID, className));

		if (configurations == null) {
			return null;
		}

		return configurations[0];
	}

	public boolean hasDLFileEntryConfigurationChanges() throws Exception {
		Configuration dlFileEntryConfiguration = getSystemConfiguration(
			DLConfigurationUpgradeHelper.
				CLASS_NAME_DL_FILE_ENTRY_CONFIGURATION);

		if (dlFileEntryConfiguration != null) {
			Dictionary<String, Object> dictionary =
				dlFileEntryConfiguration.getProperties();

			if (dictionary != null) {
				Long previewableProcessorMaxSize = (Long)dictionary.get(
					"previewableProcessorMaxSize");

				if ((previewableProcessorMaxSize != null) &&
					(previewableProcessorMaxSize !=
						DLFileEntryConfigurationConstants.
							PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT)) {

					return false;
				}
			}
		}

		return true;
	}

	public boolean hasDLSizeLimitConfigurationChanges() throws Exception {
		Configuration dlSizeLimitConfiguration = getSystemConfiguration(
			DLSizeLimitConfiguration.class.getName());

		if (dlSizeLimitConfiguration != null) {
			Dictionary<String, Object> dictionary =
				dlSizeLimitConfiguration.getProperties();

			if (dictionary != null) {
				Long fileMaxSize = (Long)dictionary.get("fileMaxSize");

				if ((fileMaxSize != null) && (fileMaxSize != 0)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean hasLegacyProps() throws Exception {
		if (Validator.isNull(
				_prefsProps.getString(LegacyDLKeys.DL_FILE_EXTENSIONS, null)) &&
			Validator.isNull(
				_prefsProps.getString(LegacyDLKeys.DL_FILE_MAX_SIZE, null)) &&
			Validator.isNull(
				_prefsProps.getString(
					LegacyDLKeys.DL_FILE_ENTRY_PREVIEWABLE_PROCESSOR_MAX_SIZE,
					null))) {

			return false;
		}

		return true;
	}

	public void updateDLFileEntryConfigurationSystemConfiguration(
			long previewableProcessorMaxSize)
		throws Exception {

		Configuration dlFileEntryConfiguration = getSystemConfiguration(
			CLASS_NAME_DL_FILE_ENTRY_CONFIGURATION);
		Configuration pdfPreviewConfiguration = getSystemConfiguration(
			CLASS_NAME_PDF_PREVIEW_CONFIGURATION);

		if ((dlFileEntryConfiguration != null) ||
			(pdfPreviewConfiguration != null)) {

			_configurationProvider.saveSystemConfiguration(
				DLFileEntryConfiguration.class,
				_createDLFileEntryConfigurationDictionary(
					_getAttributeValue(
						pdfPreviewConfiguration, "maxNumberOfPages",
						DLFileEntryConfigurationConstants.
							MAX_NUMBER_OF_PAGES_DEFAULT),
					previewableProcessorMaxSize));
		}
	}

	public void updateDLSizeLimitConfiguration() throws Exception {
		long fileMaxSize = _updateDLConfigurationFileMaxSize();

		if (fileMaxSize > 0) {
			_updateDLSizeLimitConfigurationFileMaxSize(fileMaxSize);
		}
	}

	public void updateScopedConfigurations(
			long systemPreviewableProcessorMaxSize)
		throws Exception {

		Configuration[] configurations = _getScopedConfigurations(
			CLASS_NAME_PDF_PREVIEW_CONFIGURATION);

		for (Configuration configuration : configurations) {
			long companyId = _getAttributeValue(
				configuration,
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				0L);
			Dictionary<String, Object> dictionary =
				_createDLFileEntryConfigurationDictionary(
					_getAttributeValue(
						configuration, "maxNumberOfPages",
						DLFileEntryConfigurationConstants.
							MAX_NUMBER_OF_PAGES_DEFAULT),
					Math.max(
						DLFileEntryConfigurationConstants.
							PREVIEWABLE_PROCESSOR_MAX_SIZE_DEFAULT,
						systemPreviewableProcessorMaxSize));
			long groupId = _getAttributeValue(
				configuration,
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(), 0L);

			if ((companyId != 0) && (groupId == 0)) {
				_configurationProvider.saveCompanyConfiguration(
					DLFileEntryConfiguration.class, companyId, dictionary);

				continue;
			}

			if (groupId == 0) {
				continue;
			}

			_configurationProvider.saveGroupConfiguration(
				DLFileEntryConfiguration.class, companyId, groupId, dictionary);
		}
	}

	private HashMapDictionary<String, Object>
		_createDLFileEntryConfigurationDictionary(
			int maxNumberOfPages, long previewableProcessorMaxSize) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"maxNumberOfPages", maxNumberOfPages
		).put(
			"previewableProcessorMaxSize", previewableProcessorMaxSize
		).build();
	}

	private <T> T _getAttributeValue(
		Configuration configuration, String attributeName, T defaultValue) {

		if (configuration == null) {
			return defaultValue;
		}

		Dictionary<String, Object> dictionary = configuration.getProperties();

		if (dictionary == null) {
			return defaultValue;
		}

		T value = (T)dictionary.get(attributeName);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	private Configuration[] _getScopedConfigurations(String className)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(%s=%s.scoped)", ConfigurationAdmin.SERVICE_FACTORYPID,
				className));

		if (configurations == null) {
			return new Configuration[0];
		}

		return configurations;
	}

	private long _updateDLConfigurationFileMaxSize() throws Exception {
		Configuration configuration = getSystemConfiguration(
			DLConfiguration.class.getName());

		if (configuration == null) {
			return 0;
		}

		Dictionary<String, Object> dictionary = configuration.getProperties();

		if (dictionary == null) {
			return 0;
		}

		Long fileMaxSize = (Long)dictionary.get("fileMaxSize");

		if (fileMaxSize == null) {
			return 0;
		}

		dictionary.remove("fileMaxSize");

		configuration.update(dictionary);

		return fileMaxSize;
	}

	private void _updateDLSizeLimitConfigurationFileMaxSize(long fileMaxSize)
		throws Exception {

		Configuration configuration = _configurationAdmin.getConfiguration(
			DLSizeLimitConfiguration.class.getName(), StringPool.QUESTION);

		configuration.update(
			HashMapDictionaryBuilder.put(
				"fileMaxSize", fileMaxSize
			).build());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PrefsProps _prefsProps;

}