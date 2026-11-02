/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service;

import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
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
 * Provides the local service interface for DLOpenerFileEntryReference. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLOpenerFileEntryReferenceLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLOpenerFileEntryReferenceLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.document.library.opener.service.impl.DLOpenerFileEntryReferenceLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the dl opener file entry reference local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLOpenerFileEntryReferenceLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the dl opener file entry reference to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLOpenerFileEntryReference addDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference);

	public DLOpenerFileEntryReference addPlaceholderDLOpenerFileEntryReference(
			long userId, String referenceType, FileEntry fileEntry, int type)
		throws PortalException;

	/**
	 * Creates a new dl opener file entry reference with the primary key. Does not add the dl opener file entry reference to the database.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key for the new dl opener file entry reference
	 * @return the new dl opener file entry reference
	 */
	@Transactional(enabled = false)
	public DLOpenerFileEntryReference createDLOpenerFileEntryReference(
		long dlOpenerFileEntryReferenceId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the dl opener file entry reference from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLOpenerFileEntryReference deleteDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference);

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #deleteDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	public void deleteDLOpenerFileEntryReference(FileEntry fileEntry)
		throws PortalException;

	/**
	 * Deletes the dl opener file entry reference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 * @throws PortalException if a dl opener file entry reference with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLOpenerFileEntryReference deleteDLOpenerFileEntryReference(
			long dlOpenerFileEntryReferenceId)
		throws PortalException;

	public void deleteDLOpenerFileEntryReference(
			String referenceType, FileEntry fileEntry)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #fetchDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		FileEntry fileEntry);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		long dlOpenerFileEntryReferenceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		String referenceKey, FileEntry fileEntry);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #getDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			FileEntry fileEntry)
		throws PortalException;

	/**
	 * Returns the dl opener file entry reference with the primary key.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference
	 * @throws PortalException if a dl opener file entry reference with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			long dlOpenerFileEntryReferenceId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			String referenceType, FileEntry fileEntry)
		throws PortalException;

	/**
	 * Returns a range of all the dl opener file entry references.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dl opener file entry references
	 * @param end the upper bound of the range of dl opener file entry references (not inclusive)
	 * @return the range of dl opener file entry references
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLOpenerFileEntryReference> getDLOpenerFileEntryReferences(
		int start, int end);

	/**
	 * Returns the number of dl opener file entry references.
	 *
	 * @return the number of dl opener file entry references
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLOpenerFileEntryReferencesCount();

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
	 * Updates the dl opener file entry reference in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLOpenerFileEntryReference updateDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference);

	public DLOpenerFileEntryReference updateDLOpenerFileEntryReference(
		String referenceKey, String referenceType, FileEntry fileEntry);

}
// LIFERAY-SERVICE-BUILDER-HASH:1400744894