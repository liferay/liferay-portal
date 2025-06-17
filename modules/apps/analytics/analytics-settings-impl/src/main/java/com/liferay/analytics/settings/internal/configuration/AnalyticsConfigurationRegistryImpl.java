/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.internal.configuration;

import com.liferay.analytics.batch.exportimport.AnalyticsDXPEntityBatchExporter;
import com.liferay.analytics.batch.exportimport.constants.AnalyticsDXPEntityBatchExporterConstants;
import com.liferay.analytics.machine.learning.constants.AnalyticsMachineLearningConstants;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(
	configurationPid = "com.liferay.analytics.settings.configuration.AnalyticsConfiguration",
	service = AnalyticsConfigurationRegistry.class
)
public class AnalyticsConfigurationRegistryImpl
	implements AnalyticsConfigurationRegistry {

	@Override
	public AnalyticsConfiguration getAnalyticsConfiguration(long companyId) {
		return _analyticsConfigurations.getOrDefault(
			companyId, _systemAnalyticsConfiguration);
	}

	@Override
	public AnalyticsConfiguration getAnalyticsConfiguration(String pid) {
		Long companyId = _companyIds.get(pid);

		if (companyId == null) {
			return _systemAnalyticsConfiguration;
		}

		return getAnalyticsConfiguration(companyId);
	}

	@Override
	public Dictionary<String, Object> getAnalyticsConfigurationProperties(
		long companyId) {

		if (!isActive()) {
			return null;
		}

		for (Map.Entry<String, Long> entry : _companyIds.entrySet()) {
			if (Objects.equals(entry.getValue(), companyId)) {
				try {
					Configuration configuration =
						_configurationAdmin.getConfiguration(
							entry.getKey(), StringPool.QUESTION);

					return configuration.getProperties();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get configuration for company " +
								companyId,
							exception);
					}

					break;
				}
			}
		}

		return null;
	}

	@Override
	public Map<Long, AnalyticsConfiguration> getAnalyticsConfigurations() {
		return _analyticsConfigurations;
	}

	@Override
	public long getCompanyId(String pid) {
		return _companyIds.getOrDefault(pid, CompanyConstants.SYSTEM);
	}

	@Override
	public boolean isActive() {
		if (!_active && _hasConfiguration()) {
			_active = true;
		}
		else if (_active && !_hasConfiguration()) {
			_active = false;
		}

		return _active;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_companyLocalService.forEachCompany(
			company -> {
				if (GetterUtil.getBoolean(
						PropsUtil.get(
							PropsKeys.
								ANALYTICS_CLOUD_CONFIGURATION_DELETE_ON_STARTUP)) ||
					_isEnabled(company)) {

					_activatedCompanyIds.put(company.getCompanyId(), true);
				}
			});

		_executorService = _portalExecutorManager.getPortalExecutor(
			AnalyticsConfigurationRegistryImpl.class.getName());
		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new AnalyticsConfigurationManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.analytics.settings.configuration." +
					"AnalyticsConfiguration.scoped"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_systemAnalyticsConfiguration = ConfigurableUtil.createConfigurable(
			AnalyticsConfiguration.class, properties);
	}

	private void _addAnalyticsAdmin(long companyId) throws Exception {
		User user = _userLocalService.fetchUserByScreenName(
			companyId, AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		if (user != null) {
			return;
		}

		Company company = _companyLocalService.getCompany(companyId);

		Role role = _roleLocalService.getRole(
			companyId, "Analytics Administrator");

		user = _userLocalService.addUser(
			0, companyId, true, null, null, false,
			AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN,
			"analytics.administrator@" + company.getMx(),
			LocaleUtil.getDefault(), "Analytics", "", "Administrator", 0, 0,
			true, 0, 1, 1970, "", UserConstants.TYPE_REGULAR, null, null,
			new long[] {role.getRoleId()}, null, false, new ServiceContext());

		_userLocalService.updateUser(user);
	}

	private void _addSAPEntry(long companyId) throws Exception {
		String sapEntryName = _SAP_ENTRY_OBJECT[0];

		SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
			companyId, sapEntryName);

		if (sapEntry != null) {
			return;
		}

		_sapEntryLocalService.addSAPEntry(
			_userLocalService.getGuestUserId(companyId), _SAP_ENTRY_OBJECT[1],
			false, true, sapEntryName,
			Collections.singletonMap(LocaleUtil.getDefault(), sapEntryName),
			new ServiceContext());
	}

	private void _clearConfiguration(long companyId) {
		_companyLocalService.removePreferences(
			companyId,
			new String[] {
				"liferayAnalyticsConnectionType",
				"liferayAnalyticsDataSourceId", "liferayAnalyticsEndpointURL",
				"liferayAnalyticsFaroBackendSecuritySignature",
				"liferayAnalyticsFaroBackendURL", "liferayAnalyticsGroupIds",
				"liferayAnalyticsProjectId", "liferayAnalyticsURL"
			});

		for (String groupId :
				PrefsPropsUtil.getStringArray(
					companyId, "liferayAnalyticsGroupIds", StringPool.COMMA)) {

			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(groupId));

			if (group == null) {
				continue;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.remove("analyticsChannelId");

			group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

			_groupLocalService.updateGroup(group);
		}

		try {
			_configurationProvider.deleteCompanyConfiguration(
				AnalyticsConfiguration.class, companyId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _deleteAnalyticsAdmin(long companyId) throws Exception {
		User user = _userLocalService.fetchUserByScreenName(
			companyId, AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		if (user != null) {
			_userLocalService.deleteUser(user);
		}
	}

	private void _deleteSAPEntry(long companyId) throws Exception {
		SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
			companyId, AnalyticsSecurityConstants.SERVICE_ACCESS_POLICY_NAME);

		if (sapEntry != null) {
			_sapEntryLocalService.deleteSAPEntry(sapEntry);
		}
	}

	private void _disable(long companyId) {
		try {
			if (companyId != CompanyConstants.SYSTEM) {
				_analyticsDXPEntityBatchExporter.unscheduleExportTriggers(
					companyId,
					new String[] {
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_DXP_ENTITIES,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_ORDER,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_PRODUCT,
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_ASSET_ENTITIES,
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_MOST_VIEWED_RECOMMENDER,
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_USER_PERSONALIZATION_RECOMMENDER
					});

				_executorService.execute(
					() -> {
						try {
							_deleteAnalyticsAdmin(companyId);
							_deleteSAPEntry(companyId);
						}
						catch (Exception exception) {
							_log.error(exception);
						}
					});
			}

			if (_active && !_hasConfiguration()) {
				_active = false;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _enable(long companyId) {
		try {
			_active = true;

			_addAnalyticsAdmin(companyId);
			_addSAPEntry(companyId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _firstSync(long companyId, Dictionary<String, ?> dictionary) {
		try {
			Set<String> dispatchTriggerNames = new HashSet<>();

			if (_isSyncedAccountSettingsEnabled(dictionary) ||
				_isSyncedContactSettingsEnabled(dictionary)) {

				dispatchTriggerNames.add(
					AnalyticsDXPEntityBatchExporterConstants.
						DISPATCH_TRIGGER_NAME_DXP_ENTITIES);
			}

			if (_isSyncedCommerceSettingsEnabled(dictionary)) {
				Collections.addAll(
					dispatchTriggerNames,
					AnalyticsDXPEntityBatchExporterConstants.
						DISPATCH_TRIGGER_NAME_ORDER,
					AnalyticsDXPEntityBatchExporterConstants.
						DISPATCH_TRIGGER_NAME_PRODUCT);
			}

			if (_isContentRecommenderMostPopularItemsEnabled(dictionary)) {
				Collections.addAll(
					dispatchTriggerNames,
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_ASSET_ENTITIES,
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_MOST_VIEWED_RECOMMENDER);
			}

			if (_isContentRecommenderUserPersonalizationEnabled(dictionary)) {
				Collections.addAll(
					dispatchTriggerNames,
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_ASSET_ENTITIES,
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_USER_PERSONALIZATION_RECOMMENDER);
			}

			if (!dispatchTriggerNames.isEmpty()) {
				_analyticsDXPEntityBatchExporter.scheduleExportTriggers(
					companyId, dispatchTriggerNames.toArray(new String[0]));

				_analyticsDXPEntityBatchExporter.export(
					companyId, dispatchTriggerNames.toArray(new String[0]));
			}

			_analyticsSettingsManager.updateCompanyConfiguration(
				companyId, Collections.singletonMap("firstSync", false));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private boolean _hasConfiguration() {
		Configuration[] configurations = null;

		try {
			configurations = _configurationAdmin.listConfigurations(
				"(service.pid=" + AnalyticsConfiguration.class.getName() +
					"*)");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to list analytics configurations", exception);
			}
		}

		if (configurations == null) {
			return false;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if (Validator.isNotNull(properties.get("token"))) {
				return true;
			}
		}

		return false;
	}

	private boolean _isContentRecommenderMostPopularItemsChanged(
		Dictionary<String, ?> dictionary) {

		boolean contentRecommenderMostPopularItemsEnabled =
			GetterUtil.getBoolean(
				dictionary.get("contentRecommenderMostPopularItemsEnabled"));
		boolean previousContentRecommenderMostPopularItemsEnabled =
			GetterUtil.getBoolean(
				dictionary.get(
					"previousContentRecommenderMostPopularItemsEnabled"));

		if (contentRecommenderMostPopularItemsEnabled !=
				previousContentRecommenderMostPopularItemsEnabled) {

			return true;
		}

		return false;
	}

	private boolean _isContentRecommenderMostPopularItemsEnabled(
		Dictionary<String, ?> dictionary) {

		return GetterUtil.getBoolean(
			dictionary.get("contentRecommenderMostPopularItemsEnabled"));
	}

	private boolean _isContentRecommenderUserPersonalizationChanged(
		Dictionary<String, ?> dictionary) {

		boolean contentRecommenderUserPersonalizationEnabled =
			GetterUtil.getBoolean(
				dictionary.get("contentRecommenderUserPersonalizationEnabled"));
		boolean previousContentRecommenderUserPersonalizationEnabled =
			GetterUtil.getBoolean(
				dictionary.get(
					"previousContentRecommenderUserPersonalizationEnabled"));

		if (contentRecommenderUserPersonalizationEnabled !=
				previousContentRecommenderUserPersonalizationEnabled) {

			return true;
		}

		return false;
	}

	private boolean _isContentRecommenderUserPersonalizationEnabled(
		Dictionary<String, ?> dictionary) {

		return GetterUtil.getBoolean(
			dictionary.get("contentRecommenderUserPersonalizationEnabled"));
	}

	private boolean _isEnabled(Company company) {
		boolean hasEnabled = false;

		Properties properties = PropsUtil.getProperties(
			PropsKeys.ANALYTICS_CLOUD_CONFIGURATION_DELETE_ON_STARTUP, false);

		properties.remove(
			PropsKeys.ANALYTICS_CLOUD_CONFIGURATION_DELETE_ON_STARTUP);

		for (Object deleteOnStartup : properties.values()) {
			if (GetterUtil.getBoolean(deleteOnStartup)) {
				hasEnabled = true;

				break;
			}
		}

		if (!hasEnabled) {
			return false;
		}

		return GetterUtil.getBoolean(
			PropsUtil.get(
				PropsKeys.ANALYTICS_CLOUD_CONFIGURATION_DELETE_ON_STARTUP,
				new Filter(company.getVirtualHostname())));
	}

	private boolean _isSyncedAccountFieldsChanged(
		Dictionary<String, ?> dictionary) {

		String[] previousSyncedAccountFieldNames = GetterUtil.getStringValues(
			dictionary.get("previousSyncedAccountFieldNames"));

		Arrays.sort(previousSyncedAccountFieldNames);

		String[] syncedAccountFieldNames = GetterUtil.getStringValues(
			dictionary.get("syncedAccountFieldNames"));

		Arrays.sort(syncedAccountFieldNames);

		if ((previousSyncedAccountFieldNames.length != 0) &&
			!Arrays.equals(
				previousSyncedAccountFieldNames, syncedAccountFieldNames)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedAccountSettingsChanged(
		Dictionary<String, ?> dictionary) {

		if (GetterUtil.getBoolean(dictionary.get("previousSyncAllAccounts")) !=
				GetterUtil.getBoolean(dictionary.get("syncAllAccounts"))) {

			return true;
		}

		String[] previousSyncedAccountGroupIds = GetterUtil.getStringValues(
			dictionary.get("previousSyncedAccountGroupIds"));

		Arrays.sort(previousSyncedAccountGroupIds);

		String[] syncedAccountGroupIds = GetterUtil.getStringValues(
			dictionary.get("syncedAccountGroupIds"));

		Arrays.sort(syncedAccountGroupIds);

		if (!GetterUtil.getBoolean(dictionary.get("syncAllAccounts")) &&
			!Arrays.equals(
				previousSyncedAccountGroupIds, syncedAccountGroupIds)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedAccountSettingsEnabled(
		Dictionary<String, ?> dictionary) {

		String[] previousSyncedAccountGroupIds = GetterUtil.getStringValues(
			dictionary.get("previousSyncedAccountGroupIds"));
		String[] syncedAccountGroupIds = GetterUtil.getStringValues(
			dictionary.get("syncedAccountGroupIds"));

		if (GetterUtil.getBoolean(dictionary.get("syncAllAccounts")) ||
			(previousSyncedAccountGroupIds.length != 0) ||
			(syncedAccountGroupIds.length != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedCommerceSettingsChanged(
		Dictionary<String, ?> dictionary) {

		String[] commerceSyncEnabledAnalyticsChannelIds =
			GetterUtil.getStringValues(
				dictionary.get("commerceSyncEnabledAnalyticsChannelIds"));

		Arrays.sort(commerceSyncEnabledAnalyticsChannelIds);

		String[] previousCommerceSyncEnabledAnalyticsChannelIds =
			GetterUtil.getStringValues(
				dictionary.get(
					"previousCommerceSyncEnabledAnalyticsChannelIds"));

		Arrays.sort(previousCommerceSyncEnabledAnalyticsChannelIds);

		String[] previousSyncedCommerceChannelIds = GetterUtil.getStringValues(
			dictionary.get("previousSyncedCommerceChannelIds"));

		Arrays.sort(previousSyncedCommerceChannelIds);

		String[] syncedCommerceChannelIds = GetterUtil.getStringValues(
			dictionary.get("syncedCommerceChannelIds"));

		Arrays.sort(syncedCommerceChannelIds);

		if (!Arrays.equals(
				commerceSyncEnabledAnalyticsChannelIds,
				previousCommerceSyncEnabledAnalyticsChannelIds) ||
			!Arrays.equals(
				previousSyncedCommerceChannelIds, syncedCommerceChannelIds)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedCommerceSettingsEnabled(
		Dictionary<String, ?> dictionary) {

		String[] commerceSyncEnabledAnalyticsChannelIds =
			GetterUtil.getStringValues(
				dictionary.get("commerceSyncEnabledAnalyticsChannelIds"));
		String[] syncedCommerceChannelIds = GetterUtil.getStringValues(
			dictionary.get("syncedCommerceChannelIds"));

		if ((commerceSyncEnabledAnalyticsChannelIds.length != 0) &&
			(syncedCommerceChannelIds.length != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedContactSettingsChanged(
		Dictionary<String, ?> dictionary) {

		if (GetterUtil.getBoolean(dictionary.get("previousSyncAllContacts")) !=
				GetterUtil.getBoolean(dictionary.get("syncAllContacts"))) {

			return true;
		}

		String[] previousSyncedOrganizationIds = GetterUtil.getStringValues(
			dictionary.get("previousSyncedOrganizationIds"));

		Arrays.sort(previousSyncedOrganizationIds);

		String[] syncedOrganizationIds = GetterUtil.getStringValues(
			dictionary.get("syncedOrganizationIds"));

		Arrays.sort(syncedOrganizationIds);

		String[] previousSyncedUserGroupIds = GetterUtil.getStringValues(
			dictionary.get("previousSyncedUserGroupIds"));

		Arrays.sort(previousSyncedUserGroupIds);

		String[] syncedUserGroupIds = GetterUtil.getStringValues(
			dictionary.get("syncedUserGroupIds"));

		Arrays.sort(syncedUserGroupIds);

		if (!GetterUtil.getBoolean(dictionary.get("syncAllContacts")) &&
			(!Arrays.equals(
				previousSyncedOrganizationIds, syncedOrganizationIds) ||
			 !Arrays.equals(previousSyncedUserGroupIds, syncedUserGroupIds))) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedContactSettingsEnabled(
		Dictionary<String, ?> dictionary) {

		String[] syncedOrganizationIds = GetterUtil.getStringValues(
			dictionary.get("syncedOrganizationIds"));
		String[] syncedUserGroupIds = GetterUtil.getStringValues(
			dictionary.get("syncedUserGroupIds"));

		if (GetterUtil.getBoolean(dictionary.get("syncAllContacts")) ||
			(syncedOrganizationIds.length != 0) ||
			(syncedUserGroupIds.length != 0)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedOrderFieldsChanged(
		Dictionary<String, ?> dictionary) {

		String[] previousSyncedOrderFieldNames = GetterUtil.getStringValues(
			dictionary.get("previousSyncedOrderFieldNames"));

		Arrays.sort(previousSyncedOrderFieldNames);

		String[] syncedOrderFieldNames = GetterUtil.getStringValues(
			dictionary.get("syncedOrderFieldNames"));

		Arrays.sort(syncedOrderFieldNames);

		if ((previousSyncedOrderFieldNames.length != 0) &&
			!Arrays.equals(
				previousSyncedOrderFieldNames, syncedOrderFieldNames)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedProductFieldsChanged(
		Dictionary<String, ?> dictionary) {

		String[] previousSyncedProductFieldNames = GetterUtil.getStringValues(
			dictionary.get("previousSyncedProductFieldNames"));

		Arrays.sort(previousSyncedProductFieldNames);

		String[] syncedProductFieldNames = GetterUtil.getStringValues(
			dictionary.get("syncedProductFieldNames"));

		Arrays.sort(syncedProductFieldNames);

		if ((previousSyncedProductFieldNames.length != 0) &&
			!Arrays.equals(
				previousSyncedProductFieldNames, syncedProductFieldNames)) {

			return true;
		}

		return false;
	}

	private boolean _isSyncedUserFieldsChanged(
		Dictionary<String, ?> dictionary) {

		String[] previousSyncedContactFieldNames = GetterUtil.getStringValues(
			dictionary.get("previousSyncedContactFieldNames"));

		Arrays.sort(previousSyncedContactFieldNames);

		String[] previousSyncedUserFieldNames = GetterUtil.getStringValues(
			dictionary.get("previousSyncedUserFieldNames"));

		Arrays.sort(previousSyncedUserFieldNames);

		String[] syncedContactFieldNames = GetterUtil.getStringValues(
			dictionary.get("syncedContactFieldNames"));

		Arrays.sort(syncedContactFieldNames);

		String[] syncedUserFieldNames = GetterUtil.getStringValues(
			dictionary.get("syncedUserFieldNames"));

		Arrays.sort(syncedUserFieldNames);

		if ((previousSyncedContactFieldNames.length != 0) &&
			(previousSyncedUserFieldNames.length != 0) &&
			(!Arrays.equals(
				previousSyncedContactFieldNames, syncedContactFieldNames) ||
			 !Arrays.equals(
				 previousSyncedUserFieldNames, syncedUserFieldNames))) {

			return true;
		}

		return false;
	}

	private void _sync(long companyId, Dictionary<String, ?> dictionary) {
		try {
			Set<String> refreshDispatchTriggerNames = new HashSet<>();
			Set<String> unscheduleDispatchTriggerNames = new HashSet<>();

			if ((_isContentRecommenderMostPopularItemsChanged(dictionary) &&
				 _isContentRecommenderMostPopularItemsEnabled(dictionary)) ||
				(_isContentRecommenderUserPersonalizationChanged(dictionary) &&
				 _isContentRecommenderUserPersonalizationEnabled(dictionary))) {

				refreshDispatchTriggerNames.add(
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_ASSET_ENTITIES);

				if (_isContentRecommenderMostPopularItemsChanged(dictionary) &&
					_isContentRecommenderMostPopularItemsEnabled(dictionary)) {

					refreshDispatchTriggerNames.add(
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_MOST_VIEWED_RECOMMENDER);
				}

				if (_isContentRecommenderUserPersonalizationChanged(
						dictionary) &&
					_isContentRecommenderUserPersonalizationEnabled(
						dictionary)) {

					refreshDispatchTriggerNames.add(
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_USER_PERSONALIZATION_RECOMMENDER);
				}
			}

			if ((_isSyncedAccountSettingsChanged(dictionary) &&
				 _isSyncedAccountSettingsEnabled(dictionary)) ||
				(_isSyncedAccountSettingsEnabled(dictionary) &&
				 _isSyncedAccountFieldsChanged(dictionary)) ||
				(_isSyncedContactSettingsChanged(dictionary) &&
				 _isSyncedContactSettingsEnabled(dictionary)) ||
				(_isSyncedContactSettingsEnabled(dictionary) &&
				 _isSyncedUserFieldsChanged(dictionary))) {

				refreshDispatchTriggerNames.add(
					AnalyticsDXPEntityBatchExporterConstants.
						DISPATCH_TRIGGER_NAME_DXP_ENTITIES);
			}

			if (_isSyncedCommerceSettingsChanged(dictionary)) {
				if (_isSyncedCommerceSettingsEnabled(dictionary)) {
					Collections.addAll(
						refreshDispatchTriggerNames,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_ORDER,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_PRODUCT);
				}
				else {
					Collections.addAll(
						unscheduleDispatchTriggerNames,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_ORDER,
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_PRODUCT);
				}
			}

			if (_isSyncedCommerceSettingsEnabled(dictionary)) {
				if (_isSyncedOrderFieldsChanged(dictionary)) {
					refreshDispatchTriggerNames.add(
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_ORDER);
				}

				if (_isSyncedProductFieldsChanged(dictionary)) {
					refreshDispatchTriggerNames.add(
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_PRODUCT);
				}
			}

			if (!refreshDispatchTriggerNames.isEmpty()) {
				_analyticsDXPEntityBatchExporter.refreshExportTriggers(
					companyId,
					refreshDispatchTriggerNames.toArray(new String[0]));

				if (refreshDispatchTriggerNames.contains(
						AnalyticsDXPEntityBatchExporterConstants.
							DISPATCH_TRIGGER_NAME_DXP_ENTITIES)) {

					_analyticsDXPEntityBatchExporter.export(
						companyId,
						new String[] {
							AnalyticsDXPEntityBatchExporterConstants.
								DISPATCH_TRIGGER_NAME_DXP_ENTITIES
						});
				}

				if (refreshDispatchTriggerNames.contains(
						AnalyticsMachineLearningConstants.
							DISPATCH_TRIGGER_NAME_ASSET_ENTITIES)) {

					_analyticsDXPEntityBatchExporter.export(
						companyId,
						new String[] {
							AnalyticsMachineLearningConstants.
								DISPATCH_TRIGGER_NAME_ASSET_ENTITIES
						});
				}
			}

			if (!_isContentRecommenderMostPopularItemsEnabled(dictionary) &&
				!_isContentRecommenderUserPersonalizationEnabled(dictionary)) {

				unscheduleDispatchTriggerNames.add(
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_ASSET_ENTITIES);
			}

			if (_isContentRecommenderMostPopularItemsChanged(dictionary) &&
				!_isContentRecommenderMostPopularItemsEnabled(dictionary)) {

				unscheduleDispatchTriggerNames.add(
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_MOST_VIEWED_RECOMMENDER);
			}

			if (_isContentRecommenderUserPersonalizationChanged(dictionary) &&
				!_isContentRecommenderUserPersonalizationEnabled(dictionary)) {

				unscheduleDispatchTriggerNames.add(
					AnalyticsMachineLearningConstants.
						DISPATCH_TRIGGER_NAME_USER_PERSONALIZATION_RECOMMENDER);
			}

			if (!_isSyncedAccountSettingsEnabled(dictionary) &&
				!_isSyncedContactSettingsEnabled(dictionary)) {

				unscheduleDispatchTriggerNames.add(
					AnalyticsDXPEntityBatchExporterConstants.
						DISPATCH_TRIGGER_NAME_DXP_ENTITIES);
			}

			if (!unscheduleDispatchTriggerNames.isEmpty()) {
				_analyticsDXPEntityBatchExporter.unscheduleExportTriggers(
					companyId,
					unscheduleDispatchTriggerNames.toArray(new String[0]));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_analyticsConfigurations.remove(companyId);
		}
	}

	private void _updated(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		if (companyId != CompanyConstants.SYSTEM) {
			_analyticsConfigurations.put(
				companyId,
				ConfigurableUtil.createConfigurable(
					AnalyticsConfiguration.class, dictionary));
			_companyIds.put(pid, companyId);
		}

		if (!_initializedCompanyIds.contains(companyId)) {
			_initializedCompanyIds.add(companyId);

			if (Validator.isNull(dictionary.get("previousToken"))) {
				_activatedCompanyIds.remove(companyId);
			}
			else if (_activatedCompanyIds.getOrDefault(companyId, false)) {
				_activatedCompanyIds.remove(companyId);

				_clearConfiguration(companyId);

				return;
			}
		}

		if (Validator.isNull(dictionary.get("token"))) {
			if (Validator.isNotNull(dictionary.get("previousToken"))) {
				_disable((Long)dictionary.get("companyId"));
			}
		}
		else {
			if (Validator.isNull(dictionary.get("previousToken"))) {
				_enable((Long)dictionary.get("companyId"));
			}

			AnalyticsConfiguration analyticsConfiguration =
				getAnalyticsConfiguration(companyId);

			if (!FeatureFlagManagerUtil.isEnabled("LRAC-10757") &&
				analyticsConfiguration.wizardMode()) {

				return;
			}

			if (!FeatureFlagManagerUtil.isEnabled("LRAC-10757") &&
				analyticsConfiguration.firstSync()) {

				_firstSync(companyId, dictionary);
			}
			else {
				_sync((Long)dictionary.get("companyId"), dictionary);
			}
		}
	}

	private static final String[] _SAP_ENTRY_OBJECT = {
		AnalyticsSecurityConstants.SERVICE_ACCESS_POLICY_NAME,
		StringBundler.concat(
			"com.liferay.segments.asah.rest.internal.resource.v1_0.",
			"ExperimentResourceImpl#deleteExperiment\n",
			"com.liferay.segments.asah.rest.internal.resource.v1_0.",
			"ExperimentRunResourceImpl#postExperimentRun\n",
			"com.liferay.segments.asah.rest.internal.resource.v1_0.",
			"StatusResourceImpl#postExperimentStatus")
	};

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsConfigurationRegistryImpl.class);

	private final Map<Long, Boolean> _activatedCompanyIds =
		new ConcurrentHashMap<>();
	private boolean _active;
	private final Map<Long, AnalyticsConfiguration> _analyticsConfigurations =
		new ConcurrentHashMap<>();

	@Reference
	private AnalyticsDXPEntityBatchExporter _analyticsDXPEntityBatchExporter;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private ExecutorService _executorService;

	@Reference
	private GroupLocalService _groupLocalService;

	private final Set<Long> _initializedCompanyIds = new HashSet<>();

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;
	private volatile AnalyticsConfiguration _systemAnalyticsConfiguration;

	@Reference
	private UserLocalService _userLocalService;

	private class AnalyticsConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			long companyId = getCompanyId(pid);

			_unmapPid(pid);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				_disable(companyId);
			}
		}

		@Override
		public String getName() {
			return "com.liferay.analytics.settings.configuration." +
				"AnalyticsConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary) {
			_unmapPid(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						companyId)) {

				_updated(companyId, pid, dictionary);
			}
		}

	}

}