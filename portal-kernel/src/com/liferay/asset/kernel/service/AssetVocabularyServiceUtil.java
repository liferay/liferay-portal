/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for AssetVocabulary. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetVocabularyServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyService
 * @generated
 */
public class AssetVocabularyServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AssetVocabulary addVocabulary(
			long groupId, String title, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			groupId, title, titleMap, descriptionMap, settings, visibilityType,
			serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long groupId, String title, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			groupId, title, titleMap, descriptionMap, settings, serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long groupId, String title,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(groupId, title, serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long groupId, String name, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			groupId, name, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			String externalReferenceCode, long groupId, String name,
			String title, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			externalReferenceCode, groupId, name, title, titleMap,
			descriptionMap, settings, visibilityType, serviceContext);
	}

	public static List<AssetVocabulary> deleteVocabularies(
			long[] vocabularyIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().deleteVocabularies(vocabularyIds, serviceContext);
	}

	public static void deleteVocabulary(long vocabularyId)
		throws PortalException {

		getService().deleteVocabulary(vocabularyId);
	}

	public static AssetVocabulary deleteVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteVocabularyByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static AssetVocabulary fetchVocabulary(long vocabularyId)
		throws PortalException {

		return getService().fetchVocabulary(vocabularyId);
	}

	public static AssetVocabulary fetchVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchVocabularyByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getAssetVocabularyByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(long[] groupIds) {
		return getService().getGroupsVocabularies(groupIds);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className) {

		return getService().getGroupsVocabularies(groupIds, className);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		return getService().getGroupsVocabularies(
			groupIds, className, classTypePK);
	}

	public static List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws PortalException {

		return getService().getGroupVocabularies(groupId);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary)
		throws PortalException {

		return getService().getGroupVocabularies(
			groupId, createDefaultVocabulary);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary, int start, int end,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		return getService().getGroupVocabularies(
			groupId, createDefaultVocabulary, start, end, orderByComparator);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return getService().getGroupVocabularies(groupId, visibilityType);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return getService().getGroupVocabularies(
			groupId, start, end, orderByComparator);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return getService().getGroupVocabularies(
			groupId, name, start, end, orderByComparator);
	}

	public static List<AssetVocabulary> getGroupVocabularies(long[] groupIds) {
		return getService().getGroupVocabularies(groupIds);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return getService().getGroupVocabularies(groupIds, visibilityTypes);
	}

	public static int getGroupVocabulariesCount(long groupId) {
		return getService().getGroupVocabulariesCount(groupId);
	}

	public static int getGroupVocabulariesCount(long groupId, String name) {
		return getService().getGroupVocabulariesCount(groupId, name);
	}

	public static int getGroupVocabulariesCount(long[] groupIds) {
		return getService().getGroupVocabulariesCount(groupIds);
	}

	public static com.liferay.asset.kernel.model.AssetVocabularyDisplay
			getGroupVocabulariesDisplay(
				long groupId, String name, int start, int end,
				boolean addDefaultVocabulary,
				OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		return getService().getGroupVocabulariesDisplay(
			groupId, name, start, end, addDefaultVocabulary, orderByComparator);
	}

	public static com.liferay.asset.kernel.model.AssetVocabularyDisplay
			getGroupVocabulariesDisplay(
				long groupId, String name, int start, int end,
				OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException {

		return getService().getGroupVocabulariesDisplay(
			groupId, name, start, end, orderByComparator);
	}

	public static AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getOrAddEmptyVocabulary(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException {

		return getService().getVocabulary(vocabularyId);
	}

	public static com.liferay.asset.kernel.model.AssetVocabularyDisplay
			searchVocabulariesDisplay(
				long groupId, String title, boolean addDefaultVocabulary,
				int start, int end)
		throws PortalException {

		return getService().searchVocabulariesDisplay(
			groupId, title, addDefaultVocabulary, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetVocabularyDisplay
			searchVocabulariesDisplay(
				long groupId, String title, boolean addDefaultVocabulary,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().searchVocabulariesDisplay(
			groupId, title, addDefaultVocabulary, start, end, sort);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings, visibilityType);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	public static AssetVocabularyService getService() {
		return _service;
	}

	public static void setService(AssetVocabularyService service) {
		_service = service;
	}

	private static volatile AssetVocabularyService _service;

}