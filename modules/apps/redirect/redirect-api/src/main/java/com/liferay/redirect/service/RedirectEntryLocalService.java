/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service;

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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.redirect.model.RedirectEntry;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for RedirectEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see RedirectEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface RedirectEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.redirect.service.impl.RedirectEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the redirect entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link RedirectEntryLocalServiceUtil} if injection and service tracking are not available.
	 */
	public void addEntryResources(
			RedirectEntry entry, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addEntryResources(
			RedirectEntry entry, ModelPermissions modelPermissions)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the redirect entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry addRedirectEntry(RedirectEntry redirectEntry);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new redirect entry with the primary key. Does not add the redirect entry to the database.
	 *
	 * @param redirectEntryId the primary key for the new redirect entry
	 * @return the new redirect entry
	 */
	@Transactional(enabled = false)
	public RedirectEntry createRedirectEntry(long redirectEntryId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteRedirectEntries(long groupId) throws PortalException;

	/**
	 * Deletes the redirect entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry that was removed
	 * @throws PortalException if a redirect entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public RedirectEntry deleteRedirectEntry(long redirectEntryId)
		throws PortalException;

	/**
	 * Deletes the redirect entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public RedirectEntry deleteRedirectEntry(RedirectEntry redirectEntry)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
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
	public RedirectEntry fetchRedirectEntry(long redirectEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public RedirectEntry fetchRedirectEntry(long groupId, String sourceURL);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public RedirectEntry fetchRedirectEntry(
		long groupId, String sourceURL, boolean updateLastOccurrenceDate);

	/**
	 * Returns the redirect entry matching the UUID and group.
	 *
	 * @param uuid the redirect entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public RedirectEntry fetchRedirectEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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

	/**
	 * Returns a range of all the redirect entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of redirect entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<RedirectEntry> getRedirectEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<RedirectEntry> getRedirectEntries(
		long groupId, String destinationURL);

	/**
	 * Returns all the redirect entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the redirect entries
	 * @param companyId the primary key of the company
	 * @return the matching redirect entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<RedirectEntry> getRedirectEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of redirect entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the redirect entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching redirect entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<RedirectEntry> getRedirectEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RedirectEntry> orderByComparator);

	/**
	 * Returns the number of redirect entries.
	 *
	 * @return the number of redirect entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRedirectEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRedirectEntriesCount(long groupId);

	/**
	 * Returns the redirect entry with the primary key.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry
	 * @throws PortalException if a redirect entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public RedirectEntry getRedirectEntry(long redirectEntryId)
		throws PortalException;

	/**
	 * Returns the redirect entry matching the UUID and group.
	 *
	 * @param uuid the redirect entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching redirect entry
	 * @throws PortalException if a matching redirect entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public RedirectEntry getRedirectEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			boolean permanent, String sourceURL)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL, Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries)
		throws PortalException;

	/**
	 * Updates the redirect entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public RedirectEntry updateRedirectEntry(RedirectEntry redirectEntry);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1119207528