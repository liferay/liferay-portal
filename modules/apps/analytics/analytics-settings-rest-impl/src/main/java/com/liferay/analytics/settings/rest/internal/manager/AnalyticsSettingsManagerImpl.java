/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.manager;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.constants.FieldAccountConstants;
import com.liferay.analytics.settings.rest.constants.FieldOrderConstants;
import com.liferay.analytics.settings.rest.constants.FieldPeopleConstants;
import com.liferay.analytics.settings.rest.constants.FieldProductConstants;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.settings.SettingsDescriptor;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(service = AnalyticsSettingsManager.class)
public class AnalyticsSettingsManagerImpl implements AnalyticsSettingsManager {

	@Override
	public void deleteCompanyConfiguration(long companyId)
		throws ConfigurationException {

		List<Group> groups = ListUtil.concat(
			_groupLocalService.getGroups(
				companyId, GroupConstants.ANY_PARENT_GROUP_ID, true),
			_groupLocalService.getGroups(
				companyId, _CLASS_NAME_COMMERCE_CHANNEL, 0));

		for (Group group : groups) {
			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			if (typeSettingsUnicodeProperties.remove("analyticsChannelId") !=
					null) {

				_groupLocalService.updateGroup(group);
			}
		}

		_configurationProvider.deleteCompanyConfiguration(
			AnalyticsConfiguration.class, companyId);
	}

	@Override
	public AnalyticsConfiguration getAnalyticsConfiguration(long companyId)
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			AnalyticsConfiguration.class, companyId);
	}

	@Override
	public long[] getCommerceChannelIds(long companyId, long[] groupIds) {
		if (groupIds.length == 0) {
			return new long[0];
		}

		return TransformUtil.transformToLongArray(
			_groupLocalService.getGroups(
				companyId, _CLASS_NAME_COMMERCE_CHANNEL, 0),
			group -> {
				UnicodeProperties typeSettingsUnicodeProperties =
					group.getTypeSettingsProperties();

				long groupId = GetterUtil.getLong(
					typeSettingsUnicodeProperties.getProperty("siteGroupId"));

				if (ArrayUtil.contains(groupIds, groupId)) {
					return group.getClassPK();
				}

				return null;
			});
	}

	@Override
	public Long[] getCommerceChannelIds(
			String analyticsChannelId, long companyId)
		throws Exception {

		return ArrayUtil.toArray(
			getCommerceChannelIds(
				companyId,
				ArrayUtil.toArray(getSiteIds(analyticsChannelId, companyId))));
	}

	@Override
	public Long[] getSiteIds(String analyticsChannelId, long companyId)
		throws Exception {

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		List<Long> groupIds = new ArrayList<>();

		for (String groupId : analyticsConfiguration.syncedGroupIds()) {
			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(groupId));

			if (group == null) {
				group = _groupLocalService.fetchGroup(
					companyId, PortalUtil.getClassNameId(Organization.class),
					GetterUtil.getLong(groupId));

				if (group == null) {
					continue;
				}
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			if (Objects.equals(
					analyticsChannelId,
					typeSettingsUnicodeProperties.getProperty(
						"analyticsChannelId"))) {

				groupIds.add(GetterUtil.getLong(groupId));
			}
		}

		return groupIds.toArray(new Long[0]);
	}

	@Override
	public boolean isAnalyticsEnabled(long companyId) throws Exception {
		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		if (Validator.isNull(
				analyticsConfiguration.liferayAnalyticsDataSourceId()) ||
			Validator.isNull(
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature()) ||
			Validator.isNull(
				analyticsConfiguration.liferayAnalyticsFaroBackendURL())) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isSiteIdSynced(long companyId, long groupId)
		throws Exception {

		if (!isAnalyticsEnabled(companyId)) {
			return false;
		}

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		if (analyticsConfiguration.liferayAnalyticsEnableAllGroupIds() ||
			ArrayUtil.contains(
				analyticsConfiguration.syncedGroupIds(),
				String.valueOf(groupId))) {

			return true;
		}

		return false;
	}

	@Override
	public boolean syncedAccountSettingsEnabled(long companyId)
		throws Exception {

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		String[] previousSyncedAccountGroupIds =
			analyticsConfiguration.previousSyncedAccountGroupIds();
		String[] syncedAccountGroupIds =
			analyticsConfiguration.syncedAccountGroupIds();

		if (analyticsConfiguration.syncAllAccounts() ||
			(previousSyncedAccountGroupIds.length != 0) ||
			(syncedAccountGroupIds.length != 0)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean syncedContactSettingsEnabled(long companyId)
		throws Exception {

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		String[] syncedOrganizationIds =
			analyticsConfiguration.syncedOrganizationIds();
		String[] syncedUserGroupIds =
			analyticsConfiguration.syncedUserGroupIds();

		if (analyticsConfiguration.syncAllContacts() ||
			(syncedOrganizationIds.length != 0) ||
			(syncedUserGroupIds.length != 0)) {

			return true;
		}

		return false;
	}

	@Override
	public void updateCompanyConfiguration(
			long companyId, Map<String, Object> properties)
		throws Exception {

		Map<String, Object> configurationProperties = new HashMap<>();

		Configuration configuration = _getFactoryConfiguration(
			_getConfigurationPid(), ExtendedObjectClassDefinition.Scope.COMPANY,
			companyId);

		if (configuration != null) {
			configurationProperties = _toMap(configuration.getProperties());
		}

		SettingsDescriptor settingsDescriptor =
			_settingsLocatorHelper.getSettingsDescriptor(
				_getConfigurationPid());

		Set<String> allKeys = settingsDescriptor.getAllKeys();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			if (allKeys.contains(entry.getKey())) {
				configurationProperties.put(entry.getKey(), entry.getValue());
			}
		}

		for (String multiValuedKey : settingsDescriptor.getMultiValuedKeys()) {
			String[] value = (String[])configurationProperties.get(
				multiValuedKey);

			if ((value != null) && (value.length == 0)) {
				configurationProperties.remove(multiValuedKey);
			}

			configurationProperties.computeIfAbsent(
				multiValuedKey,
				key -> _defaults.getOrDefault(key, new String[0]));
		}

		_configurationProvider.saveCompanyConfiguration(
			AnalyticsConfiguration.class, companyId,
			_toDictionary(configurationProperties));
	}

	@Override
	public String[] updateSiteIds(
			String analyticsChannelId, long companyId, Long[] dataSourceSiteIds)
		throws Exception {

		_updateTypeSetting(analyticsChannelId, dataSourceSiteIds, false);

		AnalyticsConfiguration analyticsConfiguration =
			getAnalyticsConfiguration(companyId);

		Set<String> siteIds = SetUtil.fromArray(
			analyticsConfiguration.syncedGroupIds());

		for (Long dataSourceSiteId : dataSourceSiteIds) {
			siteIds.add(String.valueOf(dataSourceSiteId));
		}

		Long[] removeSiteIds = ArrayUtil.filter(
			getSiteIds(analyticsChannelId, companyId),
			siteId -> !ArrayUtil.contains(dataSourceSiteIds, siteId));

		_updateTypeSetting(analyticsChannelId, removeSiteIds, true);

		return ArrayUtil.filter(
			siteIds.toArray(new String[0]),
			siteId -> !ArrayUtil.contains(
				removeSiteIds, GetterUtil.getLong(siteId)));
	}

	private String _getConfigurationPid() {
		Class<?> clazz = AnalyticsConfiguration.class;

		Meta.OCD ocd = clazz.getAnnotation(Meta.OCD.class);

		return ocd.id();
	}

	private Configuration _getFactoryConfiguration(
			String factoryPid, ExtendedObjectClassDefinition.Scope scope,
			Serializable scopePK)
		throws Exception {

		try {
			String filterString = StringBundler.concat(
				"(&(service.factoryPid=", factoryPid, ".scoped)(",
				scope.getPropertyKey(), "=", scopePK, "))");

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(filterString);

			if (configurations != null) {
				return configurations[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			_log.error(exception);

			throw new ConfigurationException(
				"Unable to retrieve factory configuration " + factoryPid,
				exception);
		}
	}

	private Dictionary<String, Object> _toDictionary(Map<String, Object> map) {
		return new HashMapDictionary<>(map);
	}

	private Map<String, Object> _toMap(Dictionary<String, Object> dictionary) {
		if (dictionary == null) {
			return Collections.emptyMap();
		}

		Map<String, Object> map = new HashMap<>();

		for (String key : Collections.list(dictionary.keys())) {
			map.put(key, dictionary.get(key));
		}

		return map;
	}

	private <T> void _updateTypeSetting(
		String analyticsChannelId, T[] groupIds, boolean remove) {

		for (T groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(groupId));

			if (group == null) {
				continue;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			if (remove) {
				if (!analyticsChannelId.equals(
						typeSettingsUnicodeProperties.get(
							"analyticsChannelId"))) {

					continue;
				}

				typeSettingsUnicodeProperties.remove("analyticsChannelId");
			}
			else {
				if (analyticsChannelId.equals(
						typeSettingsUnicodeProperties.get(
							"analyticsChannelId"))) {

					continue;
				}

				typeSettingsUnicodeProperties.setProperty(
					"analyticsChannelId", analyticsChannelId);
			}

			_groupLocalService.updateGroup(group);
		}
	}

	private static final String _CLASS_NAME_COMMERCE_CHANNEL =
		"com.liferay.commerce.product.model.CommerceChannel";

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsSettingsManagerImpl.class);

	private static final Map<String, String[]> _defaults = HashMapBuilder.put(
		"syncedAccountFieldNames", FieldAccountConstants.FIELD_ACCOUNT_DEFAULTS
	).put(
		"syncedCategoryFieldNames", FieldProductConstants.FIELD_CATEGORY_NAMES
	).put(
		"syncedContactFieldNames", FieldPeopleConstants.FIELD_CONTACT_DEFAULTS
	).put(
		"syncedOrderFieldNames", FieldOrderConstants.FIELD_ORDER_NAMES
	).put(
		"syncedOrderItemFieldNames", FieldOrderConstants.FIELD_ORDER_ITEM_NAMES
	).put(
		"syncedProductChannelFieldNames",
		FieldProductConstants.FIELD_PRODUCT_CHANNEL_NAMES
	).put(
		"syncedProductFieldNames", FieldProductConstants.FIELD_PRODUCT_NAMES
	).put(
		"syncedUserFieldNames", FieldPeopleConstants.FIELD_USER_DEFAULTS
	).build();

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}