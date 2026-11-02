/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceVirtualOrderItemFileEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceVirtualOrderItemFileEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceVirtualOrderItemFileEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.type.virtual.order.service.impl.CommerceVirtualOrderItemFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce virtual order item file entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceVirtualOrderItemFileEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce virtual order item file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceVirtualOrderItemFileEntry
		addCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry);

	public CommerceVirtualOrderItemFileEntry
			addCommerceVirtualOrderItemFileEntry(
				long userId, long groupId, long commerceOrderItemId,
				long fileEntryId, String url, int usages, String version)
		throws PortalException;

	/**
	 * Creates a new commerce virtual order item file entry with the primary key. Does not add the commerce virtual order item file entry to the database.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key for the new commerce virtual order item file entry
	 * @return the new commerce virtual order item file entry
	 */
	@Transactional(enabled = false)
	public CommerceVirtualOrderItemFileEntry
		createCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemFileEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce virtual order item file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceVirtualOrderItemFileEntry
		deleteCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry);

	/**
	 * Deletes the commerce virtual order item file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceVirtualOrderItemFileEntry
			deleteCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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
	public CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemFileEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemId, long fileEntryId);

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
			String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the commerce virtual order item file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @return the range of commerce virtual order item file entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(long commerceVirtualOrderItemId);

	/**
	 * Returns all the commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @return the matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator);

	/**
	 * Returns the number of commerce virtual order item file entries.
	 *
	 * @return the number of commerce virtual order item file entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceVirtualOrderItemFileEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceVirtualOrderItemFileEntriesCount(
		long commerceVirtualOrderItemId);

	/**
	 * Returns the commerce virtual order item file entry with the primary key.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
		throws PortalException;

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry
	 * @throws PortalException if a matching commerce virtual order item file entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

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

	public CommerceVirtualOrderItemFileEntry incrementUsages(
			long commerceVirtualOrderItemFileEntryId)
		throws PortalException;

	/**
	 * Updates the commerce virtual order item file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceVirtualOrderItemFileEntry
		updateCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry);

	public CommerceVirtualOrderItemFileEntry
			updateCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId, long fileEntryId,
				String url, int usages, String version)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1535223376