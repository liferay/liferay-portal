/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.petra.sql.dsl.query.DSLQuery;
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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ObjectRelationship. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see ObjectRelationshipLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectRelationshipLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectRelationshipLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object relationship local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectRelationshipLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the object relationship to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectRelationshipLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectRelationship the object relationship
	 * @return the object relationship that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship addObjectRelationship(
		ObjectRelationship objectRelationship);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long userId, long objectDefinitionId1,
			long objectDefinitionId2, long parameterObjectFieldId,
			String deletionType, boolean edge, Map<Locale, String> labelMap,
			String name, boolean system, String type, ObjectField objectField)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long userId, long objectDefinitionId1,
			long objectDefinitionId2, ObjectField objectField)
		throws PortalException;

	public void addObjectRelationshipMappingTableValues(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship createManyToManyObjectRelationshipTable(
			long userId, ObjectRelationship objectRelationship)
		throws PortalException;

	/**
	 * Creates a new object relationship with the primary key. Does not add the object relationship to the database.
	 *
	 * @param objectRelationshipId the primary key for the new object relationship
	 * @return the new object relationship
	 */
	@Transactional(enabled = false)
	public ObjectRelationship createObjectRelationship(
		long objectRelationshipId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the object relationship with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectRelationshipLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship that was removed
	 * @throws PortalException if a object relationship with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectRelationship deleteObjectRelationship(
			long objectRelationshipId)
		throws PortalException;

	/**
	 * Deletes the object relationship from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectRelationshipLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectRelationship the object relationship
	 * @return the object relationship that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectRelationship deleteObjectRelationship(
			ObjectRelationship objectRelationship)
		throws PortalException;

	public void deleteObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1)
		throws PortalException;

	public void deleteObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1, long primaryKey2)
		throws PortalException;

	public void deleteObjectRelationships(long objectDefinitionId1)
		throws PortalException;

	public void deleteObjectRelationships(
			long objectDefinitionId1, boolean reverse)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectRelationshipModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectRelationshipModelImpl</code>.
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
	public ObjectRelationship fetchObjectRelationship(
		long objectRelationshipId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationship(
			long objectDefinitionId1, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByExternalReferenceCode(
		String externalReferenceCode, long objectDefinitionId1);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByExternalReferenceCode(
		String externalReferenceCode, long companyId, long objectDefinitionId1);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByObjectDefinitionId(
		long objectDefinitionId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByObjectDefinitionId1(
		long objectDefinitionId1, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByObjectFieldId2(
		long objectFieldId2);

	/**
	 * Returns the object relationship with the matching UUID and company.
	 *
	 * @param uuid the object relationship's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object relationship, or <code>null</code> if a matching object relationship could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchObjectRelationshipByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship fetchReverseObjectRelationship(
		ObjectRelationship objectRelationship, boolean reverse);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getAllObjectRelationships(
		long objectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the object relationship with the primary key.
	 *
	 * @param objectRelationshipId the primary key of the object relationship
	 * @return the object relationship
	 * @throws PortalException if a object relationship with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship getObjectRelationship(long objectRelationshipId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship getObjectRelationship(
			long objectDefinitionId1, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship getObjectRelationshipByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship getObjectRelationshipByObjectDefinitionId(
			long objectDefinitionId, String name)
		throws PortalException;

	/**
	 * Returns the object relationship with the matching UUID and company.
	 *
	 * @param uuid the object relationship's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object relationship
	 * @throws PortalException if a matching object relationship could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectRelationship getObjectRelationshipByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the object relationships.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectRelationshipModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object relationships
	 * @param end the upper bound of the range of object relationships (not inclusive)
	 * @return the range of object relationships
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, boolean edge);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, long objectDefinition2, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, String deletionType, boolean reverse);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationshipsByObjectDefinitionId2(
		long objectDefinitionId2);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationshipsByObjectDefinitionId2(
		long objectDefinitionId2, boolean edge);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectRelationship> getObjectRelationshipsByObjectDefinitionId2(
		long objectDefinitionId2, String type);

	/**
	 * Returns the number of object relationships.
	 *
	 * @return the number of object relationships
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectRelationshipsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Long, List<ObjectRelationship>> getObjectRelationshipsMap(
		long companyId);

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

	public void registerObjectRelationshipsRelatedInfoCollectionProviders(
		ObjectDefinition objectDefinition1,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		List<ObjectRelationship> objectRelationships);

	/**
	 * Updates the object relationship in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectRelationshipLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectRelationship the object relationship
	 * @return the object relationship that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship updateObjectRelationship(
		ObjectRelationship objectRelationship);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectRelationship updateObjectRelationship(
			String externalReferenceCode, long objectRelationshipId,
			long parameterObjectFieldId, String deletionType, boolean edge,
			Map<Locale, String> labelMap, ObjectField objectField)
		throws PortalException;

	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws PortalException;

}