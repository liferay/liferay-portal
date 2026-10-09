/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.configuration;

import com.liferay.document.library.configuration.DLFileEntryConfiguration;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.constants.DLFileEntryConfigurationConstants;
import com.liferay.document.library.exception.DLFileEntryConfigurationException;
import com.liferay.document.library.internal.configuration.helper.DLFileEntryConfigurationHelper;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(service = DLFileEntryConfigurationProvider.class)
public class DLFileEntryConfigurationProviderImpl
	implements DLFileEntryConfigurationProvider {

	@Override
	public int getCompanyMaxNumberOfPages(long companyId) {
		return _getMinimumMaxNumberOfPages(
			_dlFileEntryConfigurationHelper.getCompanyMaxNumberOfPages(
				companyId),
			_dlFileEntryConfigurationHelper.getSystemMaxNumberOfPages());
	}

	@Override
	public long getCompanyPreviewableProcessorMaxSize(long companyId) {
		return _getMinimumPreviewableProcessorMaxSize(
			_dlFileEntryConfigurationHelper.
				getCompanyPreviewableProcessorMaxSize(companyId),
			_dlFileEntryConfigurationHelper.
				getSystemPreviewableProcessorMaxSize());
	}

	@Override
	public int getGroupMaxNumberOfPages(long groupId) {
		long companyId = _getCompanyId(groupId);

		return _getMinimumMaxNumberOfPages(
			_dlFileEntryConfigurationHelper.getGroupMaxNumberOfPages(
				companyId, groupId),
			_dlFileEntryConfigurationHelper.getCompanyMaxNumberOfPages(
				companyId),
			_dlFileEntryConfigurationHelper.getSystemMaxNumberOfPages());
	}

	@Override
	public long getGroupPreviewableProcessorMaxSize(long groupId) {
		long companyId = _getCompanyId(groupId);

		return _getMinimumPreviewableProcessorMaxSize(
			_dlFileEntryConfigurationHelper.getGroupPreviewableProcessorMaxSize(
				companyId, groupId),
			_dlFileEntryConfigurationHelper.
				getCompanyPreviewableProcessorMaxSize(companyId),
			_dlFileEntryConfigurationHelper.
				getSystemPreviewableProcessorMaxSize());
	}

	@Override
	public Map<Long, Long> getGroupPreviewableProcessorMaxSizeMap() {
		Map<Long, Long> map = new HashMap<>();

		for (long groupId : _dlFileEntryConfigurationHelper.getGroupIds()) {
			long previewableProcessorMaxSize =
				getGroupPreviewableProcessorMaxSize(groupId);

			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group == null) ||
				(previewableProcessorMaxSize ==
					getCompanyPreviewableProcessorMaxSize(
						group.getCompanyId()))) {

				continue;
			}

			map.put(groupId, previewableProcessorMaxSize);
		}

		return map;
	}

	@Override
	public int getMaxNumberOfPages(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::getCompanyMaxNumberOfPages,
			this::getGroupMaxNumberOfPages, this::getSystemMaxNumberOfPages);
	}

	@Override
	public int getMaxNumberOfPagesLimit(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, companyId -> getSystemMaxNumberOfPages(),
			this::_getGroupMaxNumberOfPagesLimit,
			() ->
				DLFileEntryConfigurationConstants.
					MAX_NUMBER_OF_PAGES_UNLIMITED);
	}

	@Override
	public long getPreviewableProcessorMaxSize(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::getCompanyPreviewableProcessorMaxSize,
			this::getGroupPreviewableProcessorMaxSize,
			this::getSystemPreviewableProcessorMaxSize);
	}

	@Override
	public long getPreviewableProcessorMaxSizeLimit(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, companyId -> getSystemPreviewableProcessorMaxSize(),
			this::_getGroupPreviewableProcessorMaxSizeLimit,
			() ->
				DLFileEntryConfigurationConstants.
					PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED);
	}

	@Override
	public int getSystemMaxNumberOfPages() {
		return _dlFileEntryConfigurationHelper.getSystemMaxNumberOfPages();
	}

	@Override
	public long getSystemPreviewableProcessorMaxSize() {
		return _dlFileEntryConfigurationHelper.
			getSystemPreviewableProcessorMaxSize();
	}

	@Override
	public void update(
			long previewableProcessorMaxSize, int maxNumberOfPages,
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			_updateCompanyValues(
				scopePK, maxNumberOfPages, previewableProcessorMaxSize);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			_updateGroupValues(
				scopePK, maxNumberOfPages, previewableProcessorMaxSize);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_configurationProvider.saveSystemConfiguration(
				DLFileEntryConfiguration.class,
				_createDictionary(
					maxNumberOfPages, previewableProcessorMaxSize));
		}
		else {
			throw new IllegalArgumentException("Unsupported scope: " + scope);
		}
	}

	private HashMapDictionary<String, Object> _createDictionary(
		int maxNumberOfPages, long previewableProcessorMaxSize) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"maxNumberOfPages", maxNumberOfPages
		).put(
			"previewableProcessorMaxSize", previewableProcessorMaxSize
		).build();
	}

	private long _getCompanyId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		long companyId = CompanyThreadLocal.getCompanyId();

		if (group != null) {
			companyId = group.getCompanyId();
		}

		return companyId;
	}

	private int _getGroupMaxNumberOfPagesLimit(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return getSystemMaxNumberOfPages();
		}

		return getCompanyMaxNumberOfPages(group.getCompanyId());
	}

	private long _getGroupPreviewableProcessorMaxSizeLimit(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return getSystemPreviewableProcessorMaxSize();
		}

		return getCompanyPreviewableProcessorMaxSize(group.getCompanyId());
	}

	private long _getMinimum(long unlimitedValue, long... values) {
		long minimum = Long.MAX_VALUE;

		for (long value : values) {
			if ((value == unlimitedValue) || (value >= minimum)) {
				continue;
			}

			minimum = value;
		}

		if (minimum == Long.MAX_VALUE) {
			return unlimitedValue;
		}

		return minimum;
	}

	private int _getMinimumMaxNumberOfPages(long... values) {
		return (int)_getMinimum(
			DLFileEntryConfigurationConstants.MAX_NUMBER_OF_PAGES_UNLIMITED,
			values);
	}

	private long _getMinimumPreviewableProcessorMaxSize(long... values) {
		return _getMinimum(
			DLFileEntryConfigurationConstants.
				PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED,
			values);
	}

	private <T> T _getScopeConfigurationAttribute(
		ExtendedObjectClassDefinition.Scope scope, long scopePK,
		Function<Long, T> companyFunction, Function<Long, T> groupFunction,
		Supplier<T> systemFunction) {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return companyFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return groupFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return systemFunction.get();
		}

		throw new IllegalArgumentException("Unsupported scope: " + scope);
	}

	private boolean _isLimitExceeded(
		long limit, long value, long unlimitedValue) {

		if ((limit != unlimitedValue) &&
			((value == unlimitedValue) || (limit < value))) {

			return true;
		}

		return false;
	}

	private void _updateCompanyValues(
			long companyId, int maxNumberOfPages,
			long previewableProcessorMaxSize)
		throws Exception {

		_validateMaxNumberOfPages(
			maxNumberOfPages, getSystemMaxNumberOfPages());
		_validatePreviewableProcessorMaxSize(
			previewableProcessorMaxSize,
			getSystemPreviewableProcessorMaxSize());

		_configurationProvider.saveCompanyConfiguration(
			DLFileEntryConfiguration.class, companyId,
			_createDictionary(maxNumberOfPages, previewableProcessorMaxSize));
	}

	private void _updateGroupValues(
			long groupId, int maxNumberOfPages,
			long previewableProcessorMaxSize)
		throws Exception {

		_validateMaxNumberOfPages(
			maxNumberOfPages, getSystemMaxNumberOfPages());
		_validatePreviewableProcessorMaxSize(
			previewableProcessorMaxSize,
			getSystemPreviewableProcessorMaxSize());

		Group group = _groupLocalService.getGroup(groupId);

		_validateMaxNumberOfPages(
			maxNumberOfPages, getCompanyMaxNumberOfPages(group.getCompanyId()));
		_validatePreviewableProcessorMaxSize(
			previewableProcessorMaxSize,
			getCompanyPreviewableProcessorMaxSize(group.getCompanyId()));

		_configurationProvider.saveGroupConfiguration(
			DLFileEntryConfiguration.class, group.getCompanyId(), groupId,
			_createDictionary(maxNumberOfPages, previewableProcessorMaxSize));
	}

	private void _validateMaxNumberOfPages(
			int maxNumberOfPages, int maxNumberOfPagesLimit)
		throws Exception {

		if (_isLimitExceeded(
				maxNumberOfPagesLimit, maxNumberOfPages,
				DLFileEntryConfigurationConstants.
					MAX_NUMBER_OF_PAGES_UNLIMITED)) {

			throw new DLFileEntryConfigurationException.
				InvalidMaxNumberOfPagesException();
		}
	}

	private void _validatePreviewableProcessorMaxSize(
			long previewableProcessorMaxSize,
			long previewableProcessorMaxSizeLimit)
		throws Exception {

		if (_isLimitExceeded(
				previewableProcessorMaxSizeLimit, previewableProcessorMaxSize,
				DLFileEntryConfigurationConstants.
					PREVIEWABLE_PROCESSOR_MAX_SIZE_UNLIMITED)) {

			throw new DLFileEntryConfigurationException.
				InvalidPreviewableProcessorMaxSizeException();
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLFileEntryConfigurationHelper _dlFileEntryConfigurationHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}