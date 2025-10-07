/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetVocabulary. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.asset.kernel.model.AssetVocabulary"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetVocabularyLocalService
	extends BaseLocalService, CTService<AssetVocabulary>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset vocabulary local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetVocabularyLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset vocabulary to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabulary addAssetVocabulary(AssetVocabulary assetVocabulary);

	public AssetVocabulary addDefaultVocabulary(long groupId)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, int visibilityType, ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetVocabulary addVocabulary(
			long userId, long groupId, String name, String title,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String settings, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long userId, long groupId,
			String name, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType, ServiceContext serviceContext)
		throws PortalException;

	public void addVocabularyResources(
			AssetVocabulary vocabulary, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addVocabularyResources(
			AssetVocabulary vocabulary, ModelPermissions modelPermissions)
		throws PortalException;

	/**
	 * Creates a new asset vocabulary with the primary key. Does not add the asset vocabulary to the database.
	 *
	 * @param vocabularyId the primary key for the new asset vocabulary
	 * @return the new asset vocabulary
	 */
	@Transactional(enabled = false)
	public AssetVocabulary createAssetVocabulary(long vocabularyId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset vocabulary from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetVocabulary deleteAssetVocabulary(
		AssetVocabulary assetVocabulary);

	/**
	 * Deletes the asset vocabulary with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary that was removed
	 * @throws PortalException if a asset vocabulary with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetVocabulary deleteAssetVocabulary(long vocabularyId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteVocabularies(long groupId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public AssetVocabulary deleteVocabulary(AssetVocabulary vocabulary)
		throws PortalException;

	public void deleteVocabulary(long vocabularyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchAssetVocabulary(long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchAssetVocabularyByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the asset vocabulary matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchAssetVocabularyByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary fetchGroupVocabulary(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the asset vocabularies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of asset vocabularies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getAssetVocabularies(int start, int end);

	/**
	 * Returns all the asset vocabularies matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabularies
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabularies, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getAssetVocabulariesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of asset vocabularies matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabularies
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset vocabularies, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getAssetVocabulariesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator);

	/**
	 * Returns the number of asset vocabularies.
	 *
	 * @return the number of asset vocabularies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetVocabulariesCount();

	/**
	 * Returns the asset vocabulary with the primary key.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws PortalException if a asset vocabulary with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getAssetVocabulary(long vocabularyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the asset vocabulary matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary
	 * @throws PortalException if a matching asset vocabulary could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getAssetVocabularyByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getCompanyVocabularies(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

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
			long groupId, boolean addDefaultVocabulary)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupVocabulariesCount(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getGroupVocabulary(long groupId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long userId, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getVocabularies(Hits hits)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabulary> getVocabularies(long[] vocabularyIds)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<AssetVocabulary> searchVocabularies(
			long companyId, long[] groupIds, String title,
			int[] visibilityTypes, int start, int end, Sort sort)
		throws PortalException;

	/**
	 * Updates the asset vocabulary in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabulary updateAssetVocabulary(
		AssetVocabulary assetVocabulary);

	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabulary updateVocabulary(
			long vocabularyId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String settings,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetVocabulary> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetVocabulary> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetVocabulary>, R, E>
				updateUnsafeFunction)
		throws E;

}