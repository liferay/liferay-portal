/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration.helper;

import com.liferay.document.library.internal.configuration.DLSizeLimitConfiguration;
import com.liferay.document.library.internal.util.MimeTypeSizeLimitUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

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

		return map.getOrDefault(_getType(mimeType) + "/*", 0L);
	}

	public long getGroupFileMaxSize(long companyId, long groupId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getGroupDLSizeLimitConfiguration(companyId, groupId);

		return dlSizeLimitConfiguration.fileMaxSize();
	}

	public long getGroupMaxSizeToCopy(long companyId, long groupId) {
		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			_getGroupDLSizeLimitConfiguration(companyId, groupId);

		return dlSizeLimitConfiguration.maxSizeToCopy();
	}

	public Map<String, Long> getGroupMimeTypeSizeLimit(
		long companyId, long groupId) {

		return Collections.unmodifiableMap(
			_groupMimeTypeSizeLimitsMap.computeIfAbsent(
				_getGroupKey(companyId, groupId),
				groupKey -> _computeGroupMimeTypeSizeLimit(
					companyId, groupId)));
	}

	public long getGroupMimeTypeSizeLimit(
		long companyId, long groupId, String mimeType) {

		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = _groupMimeTypeSizeLimitsMap.computeIfAbsent(
			_getGroupKey(companyId, groupId),
			groupKey -> _computeGroupMimeTypeSizeLimit(companyId, groupId));

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		return map.getOrDefault(_getType(mimeType) + "/*", 0L);
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

	public long getSystemMimeTypeSizeLimit(String mimeType) {
		if (Validator.isNull(mimeType)) {
			return 0;
		}

		Map<String, Long> map = getSystemMimeTypeSizeLimit();

		long sizeLimit = map.getOrDefault(mimeType, 0L);

		if (sizeLimit != 0) {
			return sizeLimit;
		}

		return map.getOrDefault(_getType(mimeType) + "/*", 0L);
	}

	public void unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_companyConfigurationBeans.remove(companyId);
			_companyMimeTypeSizeLimitsMap.remove(companyId);

			_groupMimeTypeSizeLimitsMap.clear();

			return;
		}

		String groupKey = _groupKeys.remove(pid);

		if (groupKey != null) {
			_groupConfigurationBeans.remove(groupKey);
			_groupMimeTypeSizeLimitsMap.remove(groupKey);
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
		long companyId, long groupId, String pid,
		Dictionary<String, ?> dictionary) {

		String groupKey = _getGroupKey(companyId, groupId);

		_groupConfigurationBeans.put(
			groupKey,
			ConfigurableUtil.createConfigurable(
				DLSizeLimitConfiguration.class, dictionary));
		_groupKeys.put(pid, groupKey);
		_groupMimeTypeSizeLimitsMap.remove(groupKey);
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

	private Map<String, Long> _computeGroupMimeTypeSizeLimit(
		long companyId, long groupId) {

		return _computeMimeTypeSizeLimit(
			_getGroupDLSizeLimitConfiguration(companyId, groupId));
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

	private <T> DLSizeLimitConfiguration _getDLSizeLimitConfiguration(
		T key, Map<T, DLSizeLimitConfiguration> configurationBeans,
		Supplier<DLSizeLimitConfiguration> supplier) {

		DLSizeLimitConfiguration dlSizeLimitConfiguration =
			configurationBeans.get(key);

		if (dlSizeLimitConfiguration != null) {
			return dlSizeLimitConfiguration;
		}

		return supplier.get();
	}

	private DLSizeLimitConfiguration _getGroupDLSizeLimitConfiguration(
		long companyId, long groupId) {

		return _getDLSizeLimitConfiguration(
			_getGroupKey(companyId, groupId), _groupConfigurationBeans,
			() -> _getCompanyDLSizeLimitConfiguration(companyId));
	}

	private String _getGroupKey(long companyId, long groupId) {
		return companyId + "--" + groupId;
	}

	private String _getType(String mimeType) {
		int index = mimeType.indexOf(CharPool.SLASH);

		if (index < 0) {
			return mimeType;
		}

		return mimeType.substring(0, index);
	}

	private final Map<Long, DLSizeLimitConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private volatile Map<Long, Map<String, Long>> _companyMimeTypeSizeLimitsMap;
	private final Map<String, DLSizeLimitConfiguration>
		_groupConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, String> _groupKeys = new ConcurrentHashMap<>();
	private volatile Map<String, Map<String, Long>> _groupMimeTypeSizeLimitsMap;
	private volatile DLSizeLimitConfiguration _systemDLSizeLimitConfiguration;

}