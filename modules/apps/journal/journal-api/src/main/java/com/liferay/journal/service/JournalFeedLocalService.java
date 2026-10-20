/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.journal.model.JournalFeed;
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
import com.liferay.portal.kernel.service.permission.ModelPermissions;
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
 * Provides the local service interface for JournalFeed. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalFeedLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface JournalFeedLocalService
	extends BaseLocalService, CTService<JournalFeed>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalFeedLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the journal feed local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link JournalFeedLocalServiceUtil} if injection and service tracking are not available.
	 */
	public JournalFeed addFeed(
			long userId, long groupId, String feedId, boolean autoFeedId,
			String name, String description, long ddmStructureId,
			String ddmTemplateKey, String ddmRendererTemplateKey, int delta,
			String orderByCol, String orderByType,
			String targetLayoutFriendlyUrl, String targetPortletId,
			String contentField, String feedFormat, double feedVersion,
			ServiceContext serviceContext)
		throws PortalException;

	public void addFeedResources(
			JournalFeed feed, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addFeedResources(
			JournalFeed feed, ModelPermissions modelPermissions)
		throws PortalException;

	public void addFeedResources(
			long feedId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	/**
	 * Adds the journal feed to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public JournalFeed addJournalFeed(JournalFeed journalFeed);

	/**
	 * Creates a new journal feed with the primary key. Does not add the journal feed to the database.
	 *
	 * @param id the primary key for the new journal feed
	 * @return the new journal feed
	 */
	@Transactional(enabled = false)
	public JournalFeed createJournalFeed(long id);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteFeed(JournalFeed feed) throws PortalException;

	public void deleteFeed(long feedId) throws PortalException;

	public void deleteFeed(long groupId, String feedId) throws PortalException;

	/**
	 * Deletes the journal feed from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public JournalFeed deleteJournalFeed(JournalFeed journalFeed);

	/**
	 * Deletes the journal feed with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed that was removed
	 * @throws PortalException if a journal feed with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public JournalFeed deleteJournalFeed(long id) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
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
	public JournalFeed fetchFeed(long groupId, String feedId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed fetchJournalFeed(long id);

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed, or <code>null</code> if a matching journal feed could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed fetchJournalFeedByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed getFeed(long feedId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed getFeed(long groupId, String feedId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFeed> getFeeds();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the journal feed with the primary key.
	 *
	 * @param id the primary key of the journal feed
	 * @return the journal feed
	 * @throws PortalException if a journal feed with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed getJournalFeed(long id) throws PortalException;

	/**
	 * Returns the journal feed matching the UUID and group.
	 *
	 * @param uuid the journal feed's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal feed
	 * @throws PortalException if a matching journal feed could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalFeed getJournalFeedByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the journal feeds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalFeedModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @return the range of journal feeds
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFeed> getJournalFeeds(int start, int end);

	/**
	 * Returns all the journal feeds matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal feeds
	 * @param companyId the primary key of the company
	 * @return the matching journal feeds, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of journal feeds matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal feeds
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal feeds
	 * @param end the upper bound of the range of journal feeds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal feeds, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalFeed> getJournalFeedsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator);

	/**
	 * Returns the number of journal feeds.
	 *
	 * @return the number of journal feeds
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getJournalFeedsCount();

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
	public List<JournalFeed> search(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(long companyId, long groupId, String keywords);

	public JournalFeed updateFeed(
			long groupId, String feedId, String name, String description,
			long ddmStructureId, String ddmTemplateKey,
			String ddmRendererTemplateKey, int delta, String orderByCol,
			String orderByType, String targetLayoutFriendlyUrl,
			String targetPortletId, String contentField, String feedFormat,
			double feedVersion, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the journal feed in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalFeedLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalFeed the journal feed
	 * @return the journal feed that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public JournalFeed updateJournalFeed(JournalFeed journalFeed);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<JournalFeed> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<JournalFeed> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalFeed>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1551547002