/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.fragment.model.FragmentCollection;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for FragmentCollection. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface FragmentCollectionLocalService
	extends BaseLocalService, CTService<FragmentCollection>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCollectionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment collection local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentCollectionLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the fragment collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentCollection addFragmentCollection(
		FragmentCollection fragmentCollection);

	@Indexable(type = IndexableType.REINDEX)
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new fragment collection with the primary key. Does not add the fragment collection to the database.
	 *
	 * @param fragmentCollectionId the primary key for the new fragment collection
	 * @return the new fragment collection
	 */
	@Transactional(enabled = false)
	public FragmentCollection createFragmentCollection(
		long fragmentCollectionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the fragment collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentCollection deleteFragmentCollection(
			FragmentCollection fragmentCollection)
		throws PortalException;

	/**
	 * Deletes the fragment collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection that was removed
	 * @throws PortalException if a fragment collection with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException;

	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
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
	public FragmentCollection fetchFragmentCollection(
		long fragmentCollectionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection fetchFragmentCollection(
		long groupId, String fragmentCollectionKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection fetchFragmentCollectionByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the fragment collection matching the UUID and group.
	 *
	 * @param uuid the fragment collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection fetchFragmentCollectionByUuidAndGroupId(
		String uuid, long groupId);

	public String generateFragmentCollectionKey(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the fragment collection with the primary key.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection
	 * @throws PortalException if a fragment collection with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection getFragmentCollection(long fragmentCollectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection getFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the fragment collection matching the UUID and group.
	 *
	 * @param uuid the fragment collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment collection
	 * @throws PortalException if a matching fragment collection could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentCollection getFragmentCollectionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the fragment collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of fragment collections
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	/**
	 * Returns all the fragment collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment collections
	 * @param companyId the primary key of the company
	 * @return the matching fragment collections, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollectionsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of fragment collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment collections
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching fragment collections, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentCollection> getFragmentCollectionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator);

	/**
	 * Returns the number of fragment collections.
	 *
	 * @return the number of fragment collections
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long groupId, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(long[] groupIds, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace);

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueFragmentCollectionName(long groupId, String name);

	/**
	 * Updates the fragment collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentCollection updateFragmentCollection(
		FragmentCollection fragmentCollection);

	@Indexable(type = IndexableType.REINDEX)
	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<FragmentCollection> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<FragmentCollection> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentCollection>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:2135139472