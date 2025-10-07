/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyDisplay;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AssetVocabulary. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetVocabularyService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset vocabulary remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetVocabularyServiceUtil} if injection and service tracking are not available.
	 */
	public AssetVocabulary addVocabulary(
			long groupId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long groupId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long groupId, String title, ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long groupId, String name, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long groupId, String name,
			String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException;

	public List<AssetVocabulary> deleteVocabularies(
			long[] vocabularyIds, ServiceContext serviceContext)
		throws PortalException;

	public void deleteVocabulary(long vocabularyId) throws PortalException;

	public AssetVocabulary deleteVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchVocabulary(long vocabularyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupsVocabularies(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean createDefaultVocabulary, int start, int end,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupVocabulariesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupVocabulariesCount(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupVocabulariesCount(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyDisplay getGroupVocabulariesDisplay(
			long groupId, String name, int start, int end,
			boolean addDefaultVocabulary,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyDisplay getGroupVocabulariesDisplay(
			long groupId, String name, int start, int end,
			OrderByComparator<AssetVocabulary> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyDisplay searchVocabulariesDisplay(
			long groupId, String title, boolean addDefaultVocabulary, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyDisplay searchVocabulariesDisplay(
			long groupId, String title, boolean addDefaultVocabulary, int start,
			int end, Sort sort)
		throws PortalException;

	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings)
		throws PortalException;

	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException;

	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException;

}