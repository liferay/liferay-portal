/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
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
import com.liferay.portal.kernel.service.version.VersionService;
import com.liferay.portal.kernel.service.version.VersionServiceListener;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersion;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ERCVersionedEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntryLocalServiceUtil
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry",
		"version.model.class.name=com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersion"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ERCVersionedEntryLocalService
	extends BaseLocalService, PersistedModelLocalService,
			VersionService<ERCVersionedEntry, ERCVersionedEntryVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.ERCVersionedEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the erc versioned entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ERCVersionedEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the erc versioned entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ERCVersionedEntry addERCVersionedEntry(
		ERCVersionedEntry ercVersionedEntry);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ERCVersionedEntry checkout(
			ERCVersionedEntry publishedERCVersionedEntry, int version)
		throws PortalException;

	/**
	 * Creates a new erc versioned entry. Does not add the erc versioned entry to the database.
	 *
	 * @return the new erc versioned entry
	 */
	@Override
	@Transactional(enabled = false)
	public ERCVersionedEntry create();

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ERCVersionedEntry delete(
			ERCVersionedEntry publishedERCVersionedEntry)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ERCVersionedEntry deleteDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException;

	/**
	 * Deletes the erc versioned entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public ERCVersionedEntry deleteERCVersionedEntry(
		ERCVersionedEntry ercVersionedEntry);

	/**
	 * Deletes the erc versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry that was removed
	 * @throws PortalException if a erc versioned entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public ERCVersionedEntry deleteERCVersionedEntry(long ercVersionedEntryId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Override
	public ERCVersionedEntryVersion deleteVersion(
			ERCVersionedEntryVersion ercVersionedEntryVersion)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
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

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchDraft(ERCVersionedEntry ercVersionedEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchDraft(long primaryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchERCVersionedEntry(long ercVersionedEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchERCVersionedEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchERCVersionedEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntryVersion fetchLatestVersion(
		ERCVersionedEntry ercVersionedEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchPublished(
		ERCVersionedEntry ercVersionedEntry);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry fetchPublished(long primaryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry getDraft(ERCVersionedEntry ercVersionedEntry)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry getDraft(long primaryKey) throws PortalException;

	/**
	 * Returns a range of all the erc versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @return the range of erc versioned entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ERCVersionedEntry> getERCVersionedEntries(int start, int end);

	/**
	 * Returns the number of erc versioned entries.
	 *
	 * @return the number of erc versioned entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getERCVersionedEntriesCount();

	/**
	 * Returns the erc versioned entry with the primary key.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws PortalException if a erc versioned entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry getERCVersionedEntry(long ercVersionedEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry getERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntry getERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
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

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ERCVersionedEntryVersion getVersion(
			ERCVersionedEntry ercVersionedEntry, int version)
		throws PortalException;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ERCVersionedEntryVersion> getVersions(
		ERCVersionedEntry ercVersionedEntry);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ERCVersionedEntry publishDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException;

	@Override
	public void registerListener(
		VersionServiceListener<ERCVersionedEntry, ERCVersionedEntryVersion>
			versionServiceListener);

	@Override
	public void unregisterListener(
		VersionServiceListener<ERCVersionedEntry, ERCVersionedEntryVersion>
			versionServiceListener);

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ERCVersionedEntry updateDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException;

	/**
	 * Updates the erc versioned entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftERCVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ERCVersionedEntry updateERCVersionedEntry(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException;

}