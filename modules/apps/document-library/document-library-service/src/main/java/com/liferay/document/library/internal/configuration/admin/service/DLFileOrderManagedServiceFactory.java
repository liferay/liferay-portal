/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.admin.service;

import com.liferay.document.library.internal.configuration.DLFileOrderConfiguration;
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
 * @author Sam Ziemer
 */
@Component(
	configurationPid = "com.liferay.document.library.internal.configuration.DLFileOrderConfiguration",
	property = Constants.SERVICE_PID + "=com.liferay.document.library.internal.configuration.DLFileOrderConfiguration.scoped",
	service = ManagedServiceFactory.class
)
public class DLFileOrderManagedServiceFactory implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		_unmapPid(pid);
	}

	public String getCompanyOrderByColumn(long companyId) {
		DLFileOrderConfiguration dlFileOrderConfiguration =
			_getCompanyDLFileOrderConfiguration(companyId);

		return dlFileOrderConfiguration.orderByColumn();
	}

	public String getCompanySortBy(long companyId) {
		DLFileOrderConfiguration dlFileOrderConfiguration =
			_getCompanyDLFileOrderConfiguration(companyId);

		return dlFileOrderConfiguration.sortBy();
	}

	public String getGroupOrderByColumn(long companyId, long groupId) {
		DLFileOrderConfiguration dlFileOrderConfiguration =
			_getGroupDLFileOrderConfiguration(companyId, groupId);

		return dlFileOrderConfiguration.orderByColumn();
	}

	public String getGroupSortBy(long companyId, long groupId) {
		DLFileOrderConfiguration dlFileOrderConfiguration =
			_getGroupDLFileOrderConfiguration(companyId, groupId);

		return dlFileOrderConfiguration.sortBy();
	}

	@Override
	public String getName() {
		return "com.liferay.document.library.internal.configuration." +
			"DLFileOrderConfiguration.scoped";
	}

	public String getSystemOrderByColumn() {
		return _systemDLFileOrderConfiguration.orderByColumn();
	}

	public String getSystemSortBy() {
		return _systemDLFileOrderConfiguration.sortBy();
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
			_updateCompanyConfiguration(companyId, pid, dictionary);

			return;
		}

		_updateGroupConfiguration(companyId, groupId, pid, dictionary);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemDLFileOrderConfiguration = ConfigurableUtil.createConfigurable(
			DLFileOrderConfiguration.class, properties);
	}

	private DLFileOrderConfiguration _getCompanyDLFileOrderConfiguration(
		long companyId) {

		return _getDLFileOrderConfiguration(
			_companyDLFileOrderConfigurations, companyId,
			() -> _systemDLFileOrderConfiguration);
	}

	private <T> DLFileOrderConfiguration _getDLFileOrderConfiguration(
		Map<T, DLFileOrderConfiguration> dlFileOrderConfigurations, T key,
		Supplier<DLFileOrderConfiguration> supplier) {

		DLFileOrderConfiguration dlFileOrderConfiguration =
			dlFileOrderConfigurations.get(key);

		if (dlFileOrderConfiguration != null) {
			return dlFileOrderConfiguration;
		}

		return supplier.get();
	}

	private DLFileOrderConfiguration _getGroupDLFileOrderConfiguration(
		long companyId, long groupId) {

		return _getDLFileOrderConfiguration(
			_groupDLFileOrderConfigurations, _getGroupKey(companyId, groupId),
			() -> _getCompanyDLFileOrderConfiguration(companyId));
	}

	private String _getGroupKey(long companyId, long groupId) {
		return companyId + "--" + groupId;
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_companyDLFileOrderConfigurations.remove(companyId);

			_groupDLFileOrderConfigurations.clear();
			_groupKeys.clear();

			return;
		}

		String groupKey = _groupKeys.remove(pid);

		if (groupKey != null) {
			_groupDLFileOrderConfigurations.remove(groupKey);
		}
	}

	private void _updateCompanyConfiguration(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		_companyDLFileOrderConfigurations.put(
			companyId,
			ConfigurableUtil.createConfigurable(
				DLFileOrderConfiguration.class, dictionary));
		_companyIds.put(pid, companyId);
	}

	private void _updateGroupConfiguration(
		long companyId, long groupId, String pid,
		Dictionary<String, ?> dictionary) {

		String groupKey = _getGroupKey(companyId, groupId);

		_groupDLFileOrderConfigurations.put(
			groupKey,
			ConfigurableUtil.createConfigurable(
				DLFileOrderConfiguration.class, dictionary));
		_groupKeys.put(pid, groupKey);
	}

	private final Map<Long, DLFileOrderConfiguration>
		_companyDLFileOrderConfigurations = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<String, DLFileOrderConfiguration>
		_groupDLFileOrderConfigurations = new ConcurrentHashMap<>();
	private final Map<String, String> _groupKeys = new ConcurrentHashMap<>();
	private volatile DLFileOrderConfiguration _systemDLFileOrderConfiguration;

}