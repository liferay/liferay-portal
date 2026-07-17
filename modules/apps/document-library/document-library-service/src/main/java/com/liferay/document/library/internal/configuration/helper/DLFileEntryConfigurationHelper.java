/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.helper;

import com.liferay.document.library.configuration.DLFileEntryConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Marco Galluzzi
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLFileEntryConfiguration",
	service = DLFileEntryConfigurationHelper.class
)
public class DLFileEntryConfigurationHelper {

	public int getCompanyMaxNumberOfPages(long companyId) {
		DLFileEntryConfiguration dlFileEntryConfiguration =
			_getCompanyDLFileEntryConfiguration(companyId);

		return dlFileEntryConfiguration.maxNumberOfPages();
	}

	public long getCompanyPreviewableProcessorMaxSize(long companyId) {
		DLFileEntryConfiguration dlFileEntryConfiguration =
			_getCompanyDLFileEntryConfiguration(companyId);

		return dlFileEntryConfiguration.previewableProcessorMaxSize();
	}

	public Set<Long> getGroupIds() {
		Set<Long> groupIds = new HashSet<>();

		for (String groupKey : _groupKeys.values()) {
			groupIds.add(GetterUtil.getLong(groupKey.split("--")[1]));
		}

		return groupIds;
	}

	public int getGroupMaxNumberOfPages(long companyId, long groupId) {
		DLFileEntryConfiguration dlFileEntryConfiguration =
			_getGroupDLFileEntryConfiguration(companyId, groupId);

		return dlFileEntryConfiguration.maxNumberOfPages();
	}

	public long getGroupPreviewableProcessorMaxSize(
		long companyId, long groupId) {

		DLFileEntryConfiguration dlFileEntryConfiguration =
			_getGroupDLFileEntryConfiguration(companyId, groupId);

		return dlFileEntryConfiguration.previewableProcessorMaxSize();
	}

	public int getSystemMaxNumberOfPages() {
		return _systemDLFileEntryConfiguration.maxNumberOfPages();
	}

	public long getSystemPreviewableProcessorMaxSize() {
		return _systemDLFileEntryConfiguration.previewableProcessorMaxSize();
	}

	public void unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_companyConfigurationBeans.remove(companyId);

			_groupConfigurationBeans.clear();
			_groupKeys.clear();

			return;
		}

		String groupKey = _groupKeys.remove(pid);

		if (groupKey != null) {
			_groupConfigurationBeans.remove(groupKey);
		}
	}

	public void updateCompanyConfiguration(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		_companyConfigurationBeans.put(
			companyId,
			ConfigurableUtil.createConfigurable(
				DLFileEntryConfiguration.class, dictionary));
		_companyIds.put(pid, companyId);
	}

	public void updateGroupConfiguration(
		long companyId, long groupId, String pid,
		Dictionary<String, ?> dictionary) {

		String groupKey = _getGroupKey(companyId, groupId);

		_groupConfigurationBeans.put(
			groupKey,
			ConfigurableUtil.createConfigurable(
				DLFileEntryConfiguration.class, dictionary));
		_groupKeys.put(pid, groupKey);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_systemDLFileEntryConfiguration = ConfigurableUtil.createConfigurable(
			DLFileEntryConfiguration.class, properties);
	}

	private DLFileEntryConfiguration _getCompanyDLFileEntryConfiguration(
		long companyId) {

		return _getDLFileEntryConfiguration(
			companyId, _companyConfigurationBeans,
			() -> _systemDLFileEntryConfiguration);
	}

	private <T> DLFileEntryConfiguration _getDLFileEntryConfiguration(
		T key, Map<T, DLFileEntryConfiguration> configurationBeans,
		Supplier<DLFileEntryConfiguration> supplier) {

		DLFileEntryConfiguration dlFileEntryConfiguration =
			configurationBeans.get(key);

		if (dlFileEntryConfiguration != null) {
			return dlFileEntryConfiguration;
		}

		return supplier.get();
	}

	private DLFileEntryConfiguration _getGroupDLFileEntryConfiguration(
		long companyId, long groupId) {

		return _getDLFileEntryConfiguration(
			_getGroupKey(companyId, groupId), _groupConfigurationBeans,
			() -> _getCompanyDLFileEntryConfiguration(companyId));
	}

	private String _getGroupKey(long companyId, long groupId) {
		return companyId + "--" + groupId;
	}

	private final Map<Long, DLFileEntryConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<String, DLFileEntryConfiguration>
		_groupConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, String> _groupKeys = new ConcurrentHashMap<>();
	private volatile DLFileEntryConfiguration _systemDLFileEntryConfiguration;

}