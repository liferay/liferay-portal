/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetVocabularyGroupRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyGroupRelLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.asset.kernel.model.AssetVocabularyGroupRel"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetVocabularyGroupRelLocalService
	extends BaseLocalService, CTService<AssetVocabularyGroupRel>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset vocabulary group rel local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetVocabularyGroupRelLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset vocabulary group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabularyGroupRel addAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel);

	public AssetVocabularyGroupRel addAssetVocabularyGroupRel(
			long groupId, long vocabularyId)
		throws PortalException;

	/**
	 * Creates a new asset vocabulary group rel with the primary key. Does not add the asset vocabulary group rel to the database.
	 *
	 * @param assetVocabularyGroupRelId the primary key for the new asset vocabulary group rel
	 * @return the new asset vocabulary group rel
	 */
	@Transactional(enabled = false)
	public AssetVocabularyGroupRel createAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset vocabulary group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel);

	/**
	 * Deletes the asset vocabulary group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws PortalException;

	public void deleteAssetVocabularyGroupRelsByGroupId(long groupId);

	public void deleteAssetVocabularyGroupRelsByVocabularyId(long vocabularyId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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
	public AssetVocabularyGroupRel fetchAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId);

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyGroupRel fetchAssetVocabularyGroupRelByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the asset vocabulary group rel with the primary key.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyGroupRel getAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws PortalException;

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel
	 * @throws PortalException if a matching asset vocabulary group rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetVocabularyGroupRel getAssetVocabularyGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the asset vocabulary group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @return the range of asset vocabulary group rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabularyGroupRel> getAssetVocabularyGroupRels(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabularyGroupRel> getAssetVocabularyGroupRelsByGroupId(
		long groupId);

	/**
	 * Returns all the asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByVocabularyId(long vocabularyId);

	/**
	 * Returns the number of asset vocabulary group rels.
	 *
	 * @return the number of asset vocabulary group rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetVocabularyGroupRelsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetVocabularyGroupRelsCount(long vocabularyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	public void setAssetVocabularyGroupRels(long vocabularyId, long[] groupIds)
		throws PortalException;

	/**
	 * Updates the asset vocabulary group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetVocabularyGroupRel updateAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetVocabularyGroupRel> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetVocabularyGroupRel> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetVocabularyGroupRel>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2142249179