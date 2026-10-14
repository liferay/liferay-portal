/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service;

import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
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
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetAutoTaggerEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetAutoTaggerEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetAutoTaggerEntryLocalService
	extends BaseLocalService, CTService<AssetAutoTaggerEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.asset.auto.tagger.service.impl.AssetAutoTaggerEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset auto tagger entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetAutoTaggerEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset auto tagger entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry);

	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
		AssetEntry assetEntry, AssetTag assetTag);

	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
			AssetEntry assetEntry, String assetTagName)
		throws PortalException;

	/**
	 * Creates a new asset auto tagger entry with the primary key. Does not add the asset auto tagger entry to the database.
	 *
	 * @param assetAutoTaggerEntryId the primary key for the new asset auto tagger entry
	 * @return the new asset auto tagger entry
	 */
	@Transactional(enabled = false)
	public AssetAutoTaggerEntry createAssetAutoTaggerEntry(
		long assetAutoTaggerEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset auto tagger entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetAutoTaggerEntry deleteAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry);

	/**
	 * Deletes the asset auto tagger entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry that was removed
	 * @throws PortalException if a asset auto tagger entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetAutoTaggerEntry deleteAssetAutoTaggerEntry(
			long assetAutoTaggerEntryId)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
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
	public AssetAutoTaggerEntry fetchAssetAutoTaggerEntry(
		long assetAutoTaggerEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetAutoTaggerEntry fetchAssetAutoTaggerEntry(
		long assetEntryId, long assetTagId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetAutoTaggerEntry> getAssetAutoTaggerEntries(
		AssetEntry assetEntry);

	/**
	 * Returns a range of all the asset auto tagger entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset auto tagger entries
	 * @param end the upper bound of the range of asset auto tagger entries (not inclusive)
	 * @return the range of asset auto tagger entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetAutoTaggerEntry> getAssetAutoTaggerEntries(
		int start, int end);

	/**
	 * Returns the number of asset auto tagger entries.
	 *
	 * @return the number of asset auto tagger entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetAutoTaggerEntriesCount();

	/**
	 * Returns the asset auto tagger entry with the primary key.
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry
	 * @throws PortalException if a asset auto tagger entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetAutoTaggerEntry getAssetAutoTaggerEntry(
			long assetAutoTaggerEntryId)
		throws PortalException;

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

	/**
	 * Updates the asset auto tagger entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetAutoTaggerEntry updateAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetAutoTaggerEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetAutoTaggerEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetAutoTaggerEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1141338155