/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AssetVocabularyService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyService
 * @generated
 */
public class AssetVocabularyServiceWrapper
	implements AssetVocabularyService, ServiceWrapper<AssetVocabularyService> {

	public AssetVocabularyServiceWrapper() {
		this(null);
	}

	public AssetVocabularyServiceWrapper(
		AssetVocabularyService assetVocabularyService) {

		_assetVocabularyService = assetVocabularyService;
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.addVocabulary(
			groupId, title, titleMap, descriptionMap, settings, visibilityType,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.addVocabulary(
			groupId, title, titleMap, descriptionMap, settings, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String title,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.addVocabulary(
			groupId, title, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long groupId, String name, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.addVocabulary(
			groupId, name, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long groupId, String name,
			String title, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.addVocabulary(
			externalReferenceCode, groupId, name, title, titleMap,
			descriptionMap, settings, visibilityType, serviceContext);
	}

	@Override
	public java.util.List<AssetVocabulary> deleteVocabularies(
			long[] vocabularyIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.deleteVocabularies(
			vocabularyIds, serviceContext);
	}

	@Override
	public void deleteVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyService.deleteVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabulary deleteVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.deleteVocabularyByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public AssetVocabulary fetchVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.fetchVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabulary fetchVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.fetchVocabularyByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.
			getAssetVocabularyByExternalReferenceCode(
				groupId, externalReferenceCode);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds) {

		return _assetVocabularyService.getGroupsVocabularies(groupIds);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className) {

		return _assetVocabularyService.getGroupsVocabularies(
			groupIds, className);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		return _assetVocabularyService.getGroupsVocabularies(
			groupIds, className, classTypePK);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getGroupVocabularies(groupId);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getGroupVocabularies(
			groupId, createDefaultVocabulary);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetVocabulary>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getGroupVocabularies(
			groupId, createDefaultVocabulary, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return _assetVocabularyService.getGroupVocabularies(
			groupId, visibilityType);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetVocabulary>
			orderByComparator) {

		return _assetVocabularyService.getGroupVocabularies(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetVocabulary>
			orderByComparator) {

		return _assetVocabularyService.getGroupVocabularies(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds) {

		return _assetVocabularyService.getGroupVocabularies(groupIds);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return _assetVocabularyService.getGroupVocabularies(
			groupIds, visibilityTypes);
	}

	@Override
	public int getGroupVocabulariesCount(long groupId) {
		return _assetVocabularyService.getGroupVocabulariesCount(groupId);
	}

	@Override
	public int getGroupVocabulariesCount(long groupId, String name) {
		return _assetVocabularyService.getGroupVocabulariesCount(groupId, name);
	}

	@Override
	public int getGroupVocabulariesCount(long[] groupIds) {
		return _assetVocabularyService.getGroupVocabulariesCount(groupIds);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetVocabularyDisplay
			getGroupVocabulariesDisplay(
				long groupId, String name, int start, int end,
				boolean addDefaultVocabulary,
				com.liferay.portal.kernel.util.OrderByComparator
					<AssetVocabulary> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getGroupVocabulariesDisplay(
			groupId, name, start, end, addDefaultVocabulary, orderByComparator);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetVocabularyDisplay
			getGroupVocabulariesDisplay(
				long groupId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<AssetVocabulary> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getGroupVocabulariesDisplay(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getOrAddEmptyVocabulary(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetVocabularyService.getOSGiServiceIdentifier();
	}

	@Override
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.getVocabulary(vocabularyId);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetVocabularyDisplay
			searchVocabulariesDisplay(
				long groupId, String title, boolean addDefaultVocabulary,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.searchVocabulariesDisplay(
			groupId, title, addDefaultVocabulary, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetVocabularyDisplay
			searchVocabulariesDisplay(
				long groupId, String title, boolean addDefaultVocabulary,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.searchVocabulariesDisplay(
			groupId, title, addDefaultVocabulary, start, end, sort);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings, visibilityType);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyService.updateVocabulary(
			vocabularyId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public AssetVocabularyService getWrappedService() {
		return _assetVocabularyService;
	}

	@Override
	public void setWrappedService(
		AssetVocabularyService assetVocabularyService) {

		_assetVocabularyService = assetVocabularyService;
	}

	private AssetVocabularyService _assetVocabularyService;

}