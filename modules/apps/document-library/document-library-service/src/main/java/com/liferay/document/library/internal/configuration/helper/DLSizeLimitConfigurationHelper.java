/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.helper;

import com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration;
import com.liferay.document.library.internal.util.MimeTypeSizeLimitUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(
	configurationPid = "com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration",
	service = DLSizeLimitConfigurationHelper.class
)
public class DLSizeLimitConfigurationHelper {

	public long getCompanyFileMaxSize(long companyId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getCompanyDLSizeLimitConfiguration(companyId);

		return dlSizeLimitConfiguration.fileMaxSize();
	}

	public long getCompanyMaxSizeToCopy(long companyId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getCompanyDLSizeLimitConfiguration(companyId);

		return dlSizeLimitConfiguration.maxSizeToCopy();
	}

	public Map<String, Long> getCompanyMimeTypeSizeLimit(long companyId) {
		return Collections.unmodifiableMap(
			_companyMimeTypeSizeLimitsMap.computeIfAbsent(
				companyId, this::_computeCompanyMimeTypeSizeLimit));
	}

	public long getCompanyMimeTypeSizeLimit(long companyId, String mimeType) {
		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = _companyMimeTypeSizeLimitsMap.computeIfAbsent(
			companyId, this::_computeCompanyMimeTypeSizeLimit);

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		List<String> parts = StringUtil.split(mimeType, CharPool.SLASH);

		return map.getOrDefault(String.format("%s/*", parts.get(0)), 0L);
	}

	public long getGroupFileMaxSize(long groupId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getGroupDLSizeLimitConfiguration(groupId);

		return dlSizeLimitConfiguration.fileMaxSize();
	}

	public long getGroupMaxSizeToCopy(long groupId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getGroupDLSizeLimitConfiguration(groupId);

		return dlSizeLimitConfiguration.maxSizeToCopy();
	}

	public Map<String, Long> getGroupMimeTypeSizeLimit(long groupId) {
		return Collections.unmodifiableMap(
			_groupMimeTypeSizeLimitsMap.computeIfAbsent(
				groupId, this::_computeGroupMimeTypeSizeLimit));
	}

	public long getGroupMimeTypeSizeLimit(long groupId, String mimeType) {
		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = _groupMimeTypeSizeLimitsMap.computeIfAbsent(
			groupId, this::_computeGroupMimeTypeSizeLimit);

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		List<String> parts = StringUtil.split(mimeType, CharPool.SLASH);

		return map.getOrDefault(String.format("%s/*", parts.get(0)), 0L);
	}

	public long getSystemFileMaxSize() {
		return _systemDLSizeLimitConfiguration.fileMaxSize();
	}

	public long getSystemMaxSizeToCopy() {
		return _systemDLSizeLimitConfiguration.maxSizeToCopy();
	}

	public Map<String, Long> getSystemMimeTypeSizeLimit() {
		return Collections.unmodifiableMap(
			_computeMimeTypeSizeLimit(_systemDLSizeLimitConfiguration));
	}

	public void unmapPid(String pid) {
		if (_companyIds.containsKey(pid)) {
			long companyId = _companyIds.remove(pid);

			_companyConfigurationBeans.remove(companyId);
			_companyMimeTypeSizeLimitsMap.remove(companyId);

			_groupMimeTypeSizeLimitsMap.clear();
		}
		else if (_groupIds.containsKey(pid)) {
			long groupId = _groupIds.remove(pid);

			_groupConfigurationBeans.remove(groupId);
			_groupMimeTypeSizeLimitsMap.remove(groupId);
		}
	}

	public void updateCompanyConfiguration(
		long companyId, String pid, Dictionary<String, ?> dictionary) {

		_companyConfigurationBeans.put(
			companyId,
			ConfigurableUtil.createConfigurable(
				DLSizeLimitConfiguration.class, dictionary));
		_companyIds.put(pid, companyId);
		_companyMimeTypeSizeLimitsMap.remove(companyId);
	}

	public void updateGroupConfiguration(
		long groupId, String pid, Dictionary<String, ?> dictionary) {

		_groupConfigurationBeans.put(
			groupId,
			ConfigurableUtil.createConfigurable(
				DLSizeLimitConfiguration.class, dictionary));
		_groupIds.put(pid, groupId);
		_groupMimeTypeSizeLimitsMap.remove(groupId);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_companyMimeTypeSizeLimitsMap = new ConcurrentHashMap<>();
		_groupMimeTypeSizeLimitsMap = new ConcurrentHashMap<>();
		_systemDLSizeLimitConfiguration = ConfigurableUtil.createConfigurable(
			DLSizeLimitConfiguration.class, properties);
	}

	private Map<String, Long> _computeCompanyMimeTypeSizeLimit(long companyId) {
		return _computeMimeTypeSizeLimit(
			_getCompanyDLSizeLimitConfiguration(companyId));
	}

	private Map<String, Long> _computeGroupMimeTypeSizeLimit(long groupId) {
		return _computeMimeTypeSizeLimit(
			_getGroupDLSizeLimitConfiguration(groupId));
	}

	private Map<String, Long> _computeMimeTypeSizeLimit(
		DLSizeLimitConfiguration dlSizeLimitConfiguration) {

		Map<String, Long> mimeTypeSizeLimits = new LinkedHashMap<>();

		for (String mimeTypeSizeLimit :
				dlSizeLimitConfiguration.mimeTypeSizeLimit()) {

			MimeTypeSizeLimitUtil.parseMimeTypeSizeLimit(
				mimeTypeSizeLimit, mimeTypeSizeLimits::put);
		}

		return mimeTypeSizeLimits;
	}

	private DLSizeLimitConfiguration _getCompanyDLSizeLimitConfiguration(
		long companyId) {

		return _getDLSizeLimitConfiguration(
			companyId, _companyConfigurationBeans,
			() -> _systemDLSizeLimitConfiguration);
	}

	private DLSizeLimitConfiguration _getDLSizeLimitConfiguration(
		long key, Map<Long, DLSizeLimitConfiguration> configurationBeans,
		Supplier<DLSizeLimitConfiguration> supplier) {

		if (configurationBeans.containsKey(key)) {
			return configurationBeans.get(key);
		}

		return supplier.get();
	}

	private DLSizeLimitConfiguration _getGroupDLSizeLimitConfiguration(
		long groupId) {

		return _getDLSizeLimitConfiguration(
			groupId, _groupConfigurationBeans,
			() -> {
				Group group = _groupLocalService.fetchGroup(groupId);

				long companyId = CompanyThreadLocal.getCompanyId();

				if (group != null) {
					companyId = group.getCompanyId();
				}

				return _getCompanyDLSizeLimitConfiguration(companyId);
			});
	}

	private final Map<Long, DLSizeLimitConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private volatile Map<Long, Map<String, Long>> _companyMimeTypeSizeLimitsMap;
	private final Map<Long, DLSizeLimitConfiguration> _groupConfigurationBeans =
		new ConcurrentHashMap<>();
	private final Map<String, Long> _groupIds = new ConcurrentHashMap<>();

	@Reference
	private GroupLocalService _groupLocalService;

	private volatile Map<Long, Map<String, Long>> _groupMimeTypeSizeLimitsMap;
	private volatile DLSizeLimitConfiguration _systemDLSizeLimitConfiguration;

}