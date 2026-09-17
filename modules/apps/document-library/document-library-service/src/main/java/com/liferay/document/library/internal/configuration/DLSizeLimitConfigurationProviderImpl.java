/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration;

import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.internal.configuration.helper.DLSizeLimitConfigurationHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLSizeLimitConfigurationProvider.class)
public class DLSizeLimitConfigurationProviderImpl
	implements DLSizeLimitConfigurationProvider {

	@Override
	public long getCompanyFileMaxSize(long companyId) {
		return _dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(companyId);
	}

	@Override
	public long getCompanyMaxSizeToCopy(long companyId) {
		return _dlSizeLimitConfigurationHelper.getCompanyMaxSizeToCopy(
			companyId);
	}

	@Override
	public long getCompanyMaxSizeToDownload(long companyId) {
		return _dlSizeLimitConfigurationHelper.getCompanyMaxSizeToDownload(
			companyId);
	}

	@Override
	public Map<String, Long> getCompanyMimeTypeSizeLimit(long companyId) {
		return _dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
			companyId);
	}

	@Override
	public long getGroupFileMaxSize(long groupId) {
		return _dlSizeLimitConfigurationHelper.getGroupFileMaxSize(
			_getCompanyId(groupId), groupId);
	}

	@Override
	public long getGroupMaxSizeToCopy(long groupId) {
		return _dlSizeLimitConfigurationHelper.getGroupMaxSizeToCopy(
			_getCompanyId(groupId), groupId);
	}

	@Override
	public long getGroupMaxSizeToDownload(long groupId) {
		return _dlSizeLimitConfigurationHelper.getGroupMaxSizeToDownload(
			_getCompanyId(groupId), groupId);
	}

	@Override
	public Map<String, Long> getGroupMimeTypeSizeLimit(long groupId) {
		return _dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
			_getCompanyId(groupId), groupId);
	}

	@Override
	public long getSystemFileMaxSize() {
		return _dlSizeLimitConfigurationHelper.getSystemFileMaxSize();
	}

	@Override
	public long getSystemMaxSizeToCopy() {
		return _dlSizeLimitConfigurationHelper.getSystemMaxSizeToCopy();
	}

	@Override
	public long getSystemMaxSizeToDownload() {
		return _dlSizeLimitConfigurationHelper.getSystemMaxSizeToDownload();
	}

	@Override
	public Map<String, Long> getSystemMimeTypeSizeLimit() {
		return _dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit();
	}

	@Override
	public void updateCompanySizeLimit(
			long companyId, long fileMaxSize, long maxSizeToCopy,
			Map<String, Long> mimeTypeSizeLimit)
		throws Exception {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		_updateMimeTypeSizeLimitProperty(properties, mimeTypeSizeLimit);

		properties.put("fileMaxSize", fileMaxSize);
		properties.put("maxSizeToCopy", maxSizeToCopy);

		_configurationProvider.saveCompanyConfiguration(
			DLSizeLimitConfiguration.class, companyId, properties);
	}

	@Override
	public void updateGroupSizeLimit(
			long groupId, long fileMaxSize, long maxSizeToCopy,
			Map<String, Long> mimeTypeSizeLimit)
		throws Exception {

		Group group = _groupLocalService.fetchGroup(groupId);

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		_updateMimeTypeSizeLimitProperty(properties, mimeTypeSizeLimit);

		properties.put("fileMaxSize", fileMaxSize);
		properties.put("maxSizeToCopy", maxSizeToCopy);

		_configurationProvider.saveGroupConfiguration(
			DLSizeLimitConfiguration.class, group.getCompanyId(), groupId,
			properties);
	}

	@Override
	public void updateSystemSizeLimit(
			long fileMaxSize, long maxSizeToCopy,
			Map<String, Long> mimeTypeSizeLimit)
		throws Exception {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		_updateMimeTypeSizeLimitProperty(properties, mimeTypeSizeLimit);

		properties.put("fileMaxSize", fileMaxSize);
		properties.put("maxSizeToCopy", maxSizeToCopy);

		_configurationProvider.saveSystemConfiguration(
			DLSizeLimitConfiguration.class, properties);
	}

	private long _getCompanyId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group != null) {
			return group.getCompanyId();
		}

		return CompanyThreadLocal.getCompanyId();
	}

	private void _updateMimeTypeSizeLimitProperty(
		Dictionary<String, Object> properties,
		Map<String, Long> mimeTypeSizeLimit) {

		if (mimeTypeSizeLimit.isEmpty()) {
			properties.put("mimeTypeSizeLimit", new String[0]);
		}
		else {
			String[] mimeTypeSizeLimitArray =
				new String[mimeTypeSizeLimit.size()];

			int i = 0;

			for (Map.Entry<String, Long> entry : mimeTypeSizeLimit.entrySet()) {
				mimeTypeSizeLimitArray[i] =
					entry.getKey() + StringPool.COLON + entry.getValue();

				i++;
			}

			properties.put("mimeTypeSizeLimit", mimeTypeSizeLimitArray);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLSizeLimitConfigurationHelper _dlSizeLimitConfigurationHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}