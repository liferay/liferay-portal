/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.admin.service;

import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Rachael Koestartyo
 */
@Component(
	configurationPid = "com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration",
	property = Constants.SERVICE_PID + "=com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class CookiesPreferenceHandlingManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	public int getCompanyConsentRenewalPeriod(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.consentRenewalPeriod();
	}

	public long getCompanyCustomFloatingIconImageId(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.
			customFloatingIconImageId();
	}

	public int getCompanyDissentRenewalPeriod(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.dissentRenewalPeriod();
	}

	public boolean getCompanyEnabled(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.enabled();
	}

	public boolean getCompanyExplicitConsentMode(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.explicitConsentMode();
	}

	public String getCompanyFloatingIcon(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.floatingIcon();
	}

	public boolean getCompanyFloatingIconEnabled(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.floatingIconEnabled();
	}

	public boolean getCompanyStoreConsent(long companyId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getCompanyCookiesPreferenceHandlingConfiguration(companyId);

		return cookiesPreferenceHandlingConfiguration.storeConsent();
	}

	public int getGroupConsentRenewalPeriod(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.consentRenewalPeriod();
	}

	public long getGroupCustomFloatingIconImageId(
		long companyId, long groupId) {

		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.
			customFloatingIconImageId();
	}

	public int getGroupDissentRenewalPeriod(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.dissentRenewalPeriod();
	}

	public boolean getGroupEnabled(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.enabled();
	}

	public boolean getGroupExplicitConsentMode(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.explicitConsentMode();
	}

	public String getGroupFloatingIcon(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.floatingIcon();
	}

	public boolean getGroupFloatingIconEnabled(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.floatingIconEnabled();
	}

	public boolean getGroupStoreConsent(long companyId, long groupId) {
		CookiesPreferenceHandlingConfiguration
			cookiesPreferenceHandlingConfiguration =
				_getGroupCookiesPreferenceHandlingConfiguration(
					companyId, groupId);

		return cookiesPreferenceHandlingConfiguration.storeConsent();
	}

	@Override
	public String getName() {
		return "com.liferay.cookies.configuration." +
			"CookiesPreferenceHandlingConfiguration.scoped";
	}

	public int getSystemConsentRenewalPeriod() {
		return _systemCookiesPreferenceHandlingConfiguration.
			consentRenewalPeriod();
	}

	public long getSystemCustomFloatingIconImageId() {
		return _systemCookiesPreferenceHandlingConfiguration.
			customFloatingIconImageId();
	}

	public int getSystemDissentRenewalPeriod() {
		return _systemCookiesPreferenceHandlingConfiguration.
			dissentRenewalPeriod();
	}

	public boolean getSystemEnabled() {
		return _systemCookiesPreferenceHandlingConfiguration.enabled();
	}

	public boolean getSystemExplicitConsentMode() {
		return _systemCookiesPreferenceHandlingConfiguration.
			explicitConsentMode();
	}

	public String getSystemFloatingIcon() {
		return _systemCookiesPreferenceHandlingConfiguration.floatingIcon();
	}

	public boolean getSystemFloatingIconEnabled() {
		return _systemCookiesPreferenceHandlingConfiguration.
			floatingIconEnabled();
	}

	public boolean getSystemStoreConsent() {
		return _systemCookiesPreferenceHandlingConfiguration.storeConsent();
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		_unmapPid(pid);

		long companyId = GetterUtil.getLong(
			dictionary.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId == CompanyConstants.SYSTEM) {
			return;
		}

		long groupId = GetterUtil.getLong(
			dictionary.get("groupId"), GroupConstants.DEFAULT_PARENT_GROUP_ID);

		if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
			_companyConfigurationBeans.put(
				companyId,
				ConfigurableUtil.createConfigurable(
					CookiesPreferenceHandlingConfiguration.class, dictionary));
			_companyIds.put(pid, companyId);

			return;
		}

		String groupKey = _getGroupKey(companyId, groupId);

		_groupConfigurationBeans.put(
			groupKey,
			ConfigurableUtil.createConfigurable(
				CookiesPreferenceHandlingConfiguration.class, dictionary));
		_groupKeys.put(pid, groupKey);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemCookiesPreferenceHandlingConfiguration =
			ConfigurableUtil.createConfigurable(
				CookiesPreferenceHandlingConfiguration.class, properties);
	}

	private CookiesPreferenceHandlingConfiguration
		_getCompanyCookiesPreferenceHandlingConfiguration(long companyId) {

		return _getCookiesPreferenceHandlingConfiguration(
			companyId, _companyConfigurationBeans,
			() -> _systemCookiesPreferenceHandlingConfiguration);
	}

	private <T> CookiesPreferenceHandlingConfiguration
		_getCookiesPreferenceHandlingConfiguration(
			T key,
			Map<T, CookiesPreferenceHandlingConfiguration> configurationBeans,
			Supplier<CookiesPreferenceHandlingConfiguration> supplier) {

		if (configurationBeans.containsKey(key)) {
			return configurationBeans.get(key);
		}

		return supplier.get();
	}

	private CookiesPreferenceHandlingConfiguration
		_getGroupCookiesPreferenceHandlingConfiguration(
			long companyId, long groupId) {

		return _getCookiesPreferenceHandlingConfiguration(
			_getGroupKey(companyId, groupId), _groupConfigurationBeans,
			() -> _getCompanyCookiesPreferenceHandlingConfiguration(companyId));
	}

	private String _getGroupKey(long companyId, long groupId) {
		return companyId + "--" + groupId;
	}

	private void _unmapPid(String pid) {
		if (_companyIds.containsKey(pid)) {
			long companyId = _companyIds.remove(pid);

			_companyConfigurationBeans.remove(companyId);

			_groupConfigurationBeans.clear();
			_groupKeys.clear();
		}
		else if (_groupKeys.containsKey(pid)) {
			String groupKey = _groupKeys.remove(pid);

			_groupConfigurationBeans.remove(groupKey);
		}
	}

	private final Map<Long, CookiesPreferenceHandlingConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<String, CookiesPreferenceHandlingConfiguration>
		_groupConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, String> _groupKeys = new ConcurrentHashMap<>();
	private volatile CookiesPreferenceHandlingConfiguration
		_systemCookiesPreferenceHandlingConfiguration;

}