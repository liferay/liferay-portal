/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalArticleResource;
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
 * Provides the local service interface for JournalArticleResource. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticleResourceLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface JournalArticleResourceLocalService
	extends BaseLocalService, CTService<JournalArticleResource>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalArticleResourceLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the journal article resource local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link JournalArticleResourceLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the journal article resource to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public JournalArticleResource addJournalArticleResource(
		JournalArticleResource journalArticleResource);

	/**
	 * Creates a new journal article resource with the primary key. Does not add the journal article resource to the database.
	 *
	 * @param resourcePrimKey the primary key for the new journal article resource
	 * @return the new journal article resource
	 */
	@Transactional(enabled = false)
	public JournalArticleResource createJournalArticleResource(
		long resourcePrimKey);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the journal article resource from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public JournalArticleResource deleteJournalArticleResource(
		JournalArticleResource journalArticleResource);

	/**
	 * Deletes the journal article resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource that was removed
	 * @throws PortalException if a journal article resource with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public JournalArticleResource deleteJournalArticleResource(
			long resourcePrimKey)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
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
	public JournalArticleResource fetchArticleResource(
		long groupId, String articleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource fetchArticleResource(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource fetchJournalArticleResource(
		long resourcePrimKey);

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource, or <code>null</code> if a matching journal article resource could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource fetchJournalArticleResourceByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource getArticleResource(
			long articleResourcePrimKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getArticleResourcePrimKey(long groupId, String articleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getArticleResourcePrimKey(
		String uuid, long groupId, String articleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalArticleResource> getArticleResources(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the journal article resource with the primary key.
	 *
	 * @param resourcePrimKey the primary key of the journal article resource
	 * @return the journal article resource
	 * @throws PortalException if a journal article resource with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource getJournalArticleResource(
			long resourcePrimKey)
		throws PortalException;

	/**
	 * Returns the journal article resource matching the UUID and group.
	 *
	 * @param uuid the journal article resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article resource
	 * @throws PortalException if a matching journal article resource could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JournalArticleResource getJournalArticleResourceByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the journal article resources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleResourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @return the range of journal article resources
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalArticleResource> getJournalArticleResources(
		int start, int end);

	/**
	 * Returns all the journal article resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal article resources
	 * @param companyId the primary key of the company
	 * @return the matching journal article resources, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of journal article resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal article resources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal article resources
	 * @param end the upper bound of the range of journal article resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal article resources, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<JournalArticleResource>
		getJournalArticleResourcesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<JournalArticleResource> orderByComparator);

	/**
	 * Returns the number of journal article resources.
	 *
	 * @return the number of journal article resources
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getJournalArticleResourcesCount();

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
	 * Updates the journal article resource in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticleResource the journal article resource
	 * @return the journal article resource that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public JournalArticleResource updateJournalArticleResource(
		JournalArticleResource journalArticleResource);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<JournalArticleResource> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<JournalArticleResource> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalArticleResource>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1250804130