/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
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
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ObjectEntryVersion. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryVersionLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryVersionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry version local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryVersionLocalServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectEntryVersion addObjectEntryVersion(ObjectEntry objectEntry)
		throws PortalException;

	/**
	 * Adds the object entry version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryVersion addObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion);

	public void checkObjectEntryVersions(long companyId) throws PortalException;

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	@Transactional(enabled = false)
	public ObjectEntryVersion createObjectEntryVersion(
		long objectEntryVersionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryVersionId)
		throws PortalException;

	public ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException;

	/**
	 * Deletes the object entry version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectEntryVersion deleteObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion);

	public void deleteObjectEntryVersionByObjectDefinitionId(
		Long objectDefinitionId);

	public void deleteObjectEntryVersions(long objectEntryId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
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

	public ObjectEntryVersion expireObjectEntryVersion(
			long userId, ObjectEntry objectEntry, int version,
			ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntryVersion expireObjectEntryVersion(
			long userId, ObjectEntryVersion objectEntryVersion)
		throws PortalException;

	public void expireObjectEntryVersions(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion fetchLatestApprovedObjectEntryVersion(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryVersionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryId, int version);

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion fetchObjectEntryVersionByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the object entry version with the primary key.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion getObjectEntryVersion(long objectEntryVersionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException;

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version
	 * @throws PortalException if a matching object entry version could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryVersion getObjectEntryVersionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryVersion> getObjectEntryVersions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryVersion> getObjectEntryVersions(long objectEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryVersion> getObjectEntryVersions(
		long objectEntryId, int start, int end);

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryVersionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryVersionsCount(long objectEntryId);

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
	public boolean isLatestObjectEntryVersion(long objectEntryId, int version)
		throws PortalException;

	public ObjectEntryVersion updateLatestObjectEntryVersion(
			ObjectEntry objectEntry)
		throws PortalException;

	/**
	 * Updates the object entry version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryVersion updateObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion);

}