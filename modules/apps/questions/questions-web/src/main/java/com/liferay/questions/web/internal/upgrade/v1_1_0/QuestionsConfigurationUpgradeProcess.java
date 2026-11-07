/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.questions.web.internal.upgrade.v1_1_0;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.questions.web.internal.configuration.QuestionsConfiguration;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Marco Galluzzi
 */
public class QuestionsConfigurationUpgradeProcess extends UpgradeProcess {

	public QuestionsConfigurationUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider,
		MBCategoryLocalService mbCategoryLocalService) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
		_mbCategoryLocalService = mbCategoryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_updateCompanyConfigurations();
		_updateSystemConfiguration();
	}

	private String _getRootTopicExternalReferenceCode(
		Dictionary<String, Object> properties) {

		long rootTopicId = (long)properties.get("rootTopicId");

		if (rootTopicId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			return StringPool.BLANK;
		}

		MBCategory mbCategory = _mbCategoryLocalService.fetchMBCategory(
			rootTopicId);

		if (mbCategory == null) {
			return StringPool.BLANK;
		}

		return mbCategory.getExternalReferenceCode();
	}

	private Dictionary<String, Object> _getUpdatedProperties(
		Configuration configuration) {

		if (configuration == null) {
			return null;
		}

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			return null;
		}

		properties.put(
			"rootTopicExternalReferenceCode",
			_getRootTopicExternalReferenceCode(properties));

		return properties;
	}

	private void _updateCompanyConfigurations() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(%s=%s.scoped)", ConfigurationAdmin.SERVICE_FACTORYPID,
				_CLASS_NAME_QUESTIONS_CONFIGURATION));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties = _getUpdatedProperties(
				configuration);

			if (properties == null) {
				continue;
			}

			long companyId = (long)properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey());

			if (_companyLocalService.fetchCompany(companyId) == null) {
				continue;
			}

			_configurationProvider.saveCompanyConfiguration(
				QuestionsConfiguration.class, companyId, properties);
		}
	}

	private void _updateSystemConfiguration() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(%s=%s)", Constants.SERVICE_PID,
				_CLASS_NAME_QUESTIONS_CONFIGURATION));

		if (configurations == null) {
			return;
		}

		Dictionary<String, Object> properties = _getUpdatedProperties(
			configurations[0]);

		if (properties == null) {
			return;
		}

		_configurationProvider.saveSystemConfiguration(
			QuestionsConfiguration.class, properties);
	}

	private static final String _CLASS_NAME_QUESTIONS_CONFIGURATION =
		"com.liferay.questions.web.internal.configuration." +
			"QuestionsConfiguration";

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;
	private final MBCategoryLocalService _mbCategoryLocalService;

}