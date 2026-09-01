/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.configuration.manager;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.definition.setting.util.ObjectDefinitionSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.internal.configuration.SitemapCompanyConfiguration;
import com.liferay.site.internal.configuration.SitemapGroupConfiguration;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = SitemapConfigurationManager.class)
public class SitemapConfigurationManagerImpl
	implements SitemapConfigurationManager {

	@Override
	public Long[] getCompanySitemapGroupIds(long companyId) throws Exception {
		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return TransformUtil.transform(
			sitemapCompanyConfiguration.companySitemapGroupIds(),
			GetterUtil::getLong, Long.class);
	}

	@Override
	public Long[] getCompanySitemapObjectDefinitionIds(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return TransformUtil.transform(
			sitemapCompanyConfiguration.companySitemapObjectDefinitionIds(),
			GetterUtil::getLong, Long.class);
	}

	@Override
	public List<ObjectDefinition> getCompanySitemapObjectDefinitions(
			long companyId)
		throws ConfigurationException {

		Map<Long, ObjectDefinitionSetting> objectDefinitionSettingsMap =
			_objectDefinitionSettingLocalService.getObjectDefinitionSettingsMap(
				companyId, ObjectDefinitionSettingConstants.NAME_SITEMAPABLE);

		return TransformUtil.transformToList(
			getCompanySitemapObjectDefinitionIds(companyId),
			objectDefinitionId -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectDefinitionId);

				if ((objectDefinition == null) ||
					!objectDefinition.isActive() ||
					!ObjectDefinitionSettingUtil.isSitemapable(
						objectDefinition, objectDefinitionSettingsMap)) {

					return null;
				}

				return objectDefinition;
			});
	}

	@Override
	public String getXMLSitemapIndexMode(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.xmlSitemapIndexMode();
	}

	@Override
	public boolean includeCategoriesCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.includeCategories();
	}

	@Override
	public boolean includeCategoriesGroupEnabled(long companyId, long groupId)
		throws ConfigurationException {

		if (!includeCategoriesCompanyEnabled(companyId)) {
			return false;
		}

		SitemapGroupConfiguration sitemapGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				SitemapGroupConfiguration.class, companyId, groupId);

		return sitemapGroupConfiguration.includeCategories();
	}

	@Override
	public boolean includePagesCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.includePages();
	}

	@Override
	public boolean includePagesGroupEnabled(long companyId, long groupId)
		throws ConfigurationException {

		if (!includePagesCompanyEnabled(companyId)) {
			return false;
		}

		SitemapGroupConfiguration sitemapGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				SitemapGroupConfiguration.class, companyId, groupId);

		return sitemapGroupConfiguration.includePages();
	}

	@Override
	public boolean includeWebContentCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.includeWebContent();
	}

	@Override
	public boolean includeWebContentGroupEnabled(long companyId, long groupId)
		throws ConfigurationException {

		if (!includeWebContentCompanyEnabled(companyId)) {
			return false;
		}

		SitemapGroupConfiguration sitemapGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				SitemapGroupConfiguration.class, companyId, groupId);

		return sitemapGroupConfiguration.includeWebContent();
	}

	@Override
	public boolean isCachedGenerationCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.cachedGenerationEnabled();
	}

	@Override
	public boolean isIndexModeAssetTypeCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		if (sitemapCompanyConfiguration.xmlSitemapIndexEnabled() &&
			StringUtil.equals(
				sitemapCompanyConfiguration.xmlSitemapIndexMode(),
				SitemapConstants.INDEX_MODE_ASSET_TYPE)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isObjectDefinitionCompanyIncluded(
			long companyId, String objectDefinitionId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return ArrayUtil.contains(
			sitemapCompanyConfiguration.companySitemapObjectDefinitionIds(),
			objectDefinitionId);
	}

	@Override
	public boolean isXMLSitemapIndexCompanyEnabled(long companyId)
		throws ConfigurationException {

		SitemapCompanyConfiguration sitemapCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				SitemapCompanyConfiguration.class, companyId);

		return sitemapCompanyConfiguration.xmlSitemapIndexEnabled();
	}

	@Override
	public void saveSitemapCompanyConfiguration(
			boolean cachedGenerationEnabled, long companyId,
			long[] companySitemapGroupIds,
			long[] companySitemapObjectDefinitionIds, boolean includeCategories,
			boolean includePages, boolean includeWebContent,
			boolean xmlSitemapIndexEnabled, String xmlSitemapIndexMode)
		throws ConfigurationException {

		_configurationProvider.saveCompanyConfiguration(
			SitemapCompanyConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"cachedGenerationEnabled", cachedGenerationEnabled
			).put(
				"companySitemapGroupIds", companySitemapGroupIds
			).put(
				"companySitemapObjectDefinitionIds",
				companySitemapObjectDefinitionIds
			).put(
				"includeCategories", includeCategories
			).put(
				"includePages", includePages
			).put(
				"includeWebContent", includeWebContent
			).put(
				"xmlSitemapIndexEnabled", xmlSitemapIndexEnabled
			).put(
				"xmlSitemapIndexMode", xmlSitemapIndexMode
			).build());
	}

	@Override
	public void saveSitemapGroupConfiguration(
			long groupId, boolean includeCategories, boolean includePages,
			boolean includeWebContent)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		_configurationProvider.saveGroupConfiguration(
			SitemapGroupConfiguration.class, group.getCompanyId(), groupId,
			HashMapDictionaryBuilder.<String, Object>put(
				"includeCategories", includeCategories
			).put(
				"includePages", includePages
			).put(
				"includeWebContent", includeWebContent
			).build());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

}