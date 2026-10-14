/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.service;

import com.liferay.asset.link.model.AssetLink;
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

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AssetLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetLinkLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetLinkLocalService
	extends BaseLocalService, CTService<AssetLink>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.asset.link.service.impl.AssetLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetLinkLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the asset link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetLink addAssetLink(AssetLink assetLink);

	/**
	 * Adds a new asset link.
	 *
	 * @param userId the primary key of the link's creator
	 * @param entryId1 the primary key of the first asset entry
	 * @param entryId2 the primary key of the second asset entry
	 * @param type the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @param weight the weight of the relationship, allowing precedence
	 ordering of links
	 * @return the asset link
	 */
	public AssetLink addLink(
			long userId, long entryId1, long entryId2, int type, int weight)
		throws PortalException;

	/**
	 * Creates a new asset link with the primary key. Does not add the asset link to the database.
	 *
	 * @param linkId the primary key for the new asset link
	 * @return the new asset link
	 */
	@Transactional(enabled = false)
	public AssetLink createAssetLink(long linkId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the asset link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetLink deleteAssetLink(AssetLink assetLink);

	/**
	 * Deletes the asset link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link that was removed
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AssetLink deleteAssetLink(long linkId) throws PortalException;

	public void deleteGroupLinks(long groupId);

	/**
	 * Deletes the asset link.
	 *
	 * @param link the asset link
	 */
	public void deleteLink(AssetLink link);

	/**
	 * Deletes the asset link.
	 *
	 * @param linkId the primary key of the asset link
	 */
	public void deleteLink(long linkId) throws PortalException;

	/**
	 * Deletes all links associated with the asset entry.
	 *
	 * @param entryId the primary key of the asset entry
	 */
	public void deleteLinks(long entryId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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
	public AssetLink fetchAssetLink(long linkId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the asset link with the primary key.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetLink getAssetLink(long linkId) throws PortalException;

	/**
	 * Returns a range of all the asset links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @return the range of asset links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getAssetLinks(int start, int end);

	/**
	 * Returns the number of asset links.
	 *
	 * @return the number of asset links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetLinksCount();

	/**
	 * Returns all the asset links whose first entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first entry ID is the given entry ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getDirectLinks(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getDirectLinks(
		long entryId, boolean excludeInvisibleLinks);

	/**
	 * Returns all the asset links of the given link type whose first entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first entry ID is
	 the given entry ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getDirectLinks(long entryId, int typeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getDirectLinks(
		long entryId, int typeId, boolean excludeInvisibleLinks);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns all the asset links whose first or second entry ID is the given
	 * entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first or second entry ID is the given entry
	 ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getLinks(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getLinks(
		long groupId, Date startDate, Date endDate, int start, int end);

	/**
	 * Returns all the asset links of the given link type whose first or second
	 * entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first or second
	 entry ID is the given entry ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getLinks(long entryId, int typeId);

	/**
	 * Returns all the asset links of an AssetEntry.
	 *
	 * @param classNameId AssetEntry's classNameId
	 * @param classPK AssetEntry's classPK
	 * @return the asset links of the given entry params
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getLinks(long classNameId, long classPK);

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
	 * Returns all the asset links of the given link type whose second entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose second entry ID is
	 the given entry ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetLink> getReverseLinks(long entryId, int typeId);

	/**
	 * Updates the asset link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AssetLink updateAssetLink(AssetLink assetLink);

	public AssetLink updateLink(
			long userId, long entryId1, long entryId2, int typeId, int weight)
		throws PortalException;

	/**
	 * Updates all links of the asset entry, replacing them with links
	 * associating the asset entry with the asset entries of the given link
	 * entry IDs.
	 *
	 * <p>
	 * If no link exists with a given link entry ID, a new link is created
	 * associating the current asset entry with the asset entry of that link
	 * entry ID. An existing link is deleted if either of its entry IDs is not
	 * contained in the given link entry IDs.
	 * </p>
	 *
	 * @param userId the primary key of the user updating the links
	 * @param entryId the primary key of the asset entry to be managed
	 * @param linkEntryIds the primary keys of the asset entries to be linked
	 with the asset entry to be managed
	 * @param typeId the type of the asset links to be created. Acceptable
	 values include {@link AssetLinkConstants#TYPE_RELATED} which is a
	 bidirectional relationship and {@link
	 AssetLinkConstants#TYPE_CHILD} which is a unidirectional
	 relationship. For more information see {@link AssetLinkConstants}
	 */
	public void updateLinks(
			long userId, long entryId, long[] linkEntryIds, int typeId)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AssetLink> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AssetLink> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetLink>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:2049600806