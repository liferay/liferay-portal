/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.system.SystemObjectDefinitionManager;
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
 * Provides the local service interface for ObjectDefinition. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see ObjectDefinitionLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectDefinitionLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object definition local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectDefinitionLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition addCustomObjectDefinition(
			long userId, long objectFolderId, String className,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, Map<Locale, String> labelMap,
			String name, String panelAppOrder, String panelCategoryKey,
			Map<Locale, String> pluralLabelMap, boolean portlet, String scope,
			String storageType,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields)
		throws PortalException;

	/**
	 * Adds the object definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition addObjectDefinition(
		ObjectDefinition objectDefinition);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition addOrUpdateSystemObjectDefinition(
			long companyId, long objectFolderId,
			SystemObjectDefinitionManager systemObjectDefinitionManager)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			String className, String dbTableName, boolean enableComments,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, Map<Locale, String> labelMap,
			boolean modifiable, String name, String panelAppOrder,
			String panelCategoryKey, String pkObjectFieldDBColumnName,
			String pkObjectFieldName, Map<Locale, String> pluralLabelMap,
			boolean portlet, String scope, String titleObjectFieldName,
			int version, int status,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields)
		throws PortalException;

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	@Transactional(enabled = false)
	public ObjectDefinition createObjectDefinition(long objectDefinitionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCompanyObjectDefinitions(long companyId)
		throws PortalException;

	/**
	 * Deletes the object definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition that was removed
	 * @throws PortalException if a object definition with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectDefinition deleteObjectDefinition(long objectDefinitionId)
		throws PortalException;

	/**
	 * Deletes the object definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectDefinition deleteObjectDefinition(
			ObjectDefinition objectDefinition)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deployInactiveObjectDefinition(
		ObjectDefinition objectDefinition);

	public void deployObjectDefinition(ObjectDefinition objectDefinition);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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

	public ObjectDefinition enableAccountEntryRestricted(
			ObjectRelationship objectRelationship)
		throws PortalException;

	public ObjectDefinition
			enableAccountEntryRestrictedForNondefaultStorageType(
				ObjectField objectField)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinition(long objectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinition(long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinitionByClassName(
		long companyId, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinitionByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the object definition with the matching UUID and company.
	 *
	 * @param uuid the object definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinitionByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchSystemObjectDefinition(
		long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getBoundObjectDefinitions(
		long companyId, long rootObjectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getCustomObjectDefinitions(int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the object definition with the primary key.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws PortalException if a object definition with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinition(long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinition(long companyId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the object definition with the matching UUID and company.
	 *
	 * @param uuid the object definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition
	 * @throws PortalException if a matching object definition could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinitionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(
		boolean accountEntryRestricted);

	/**
	 * Returns a range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of object definitions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(
		long companyId, boolean active, boolean system, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(
		long companyId, boolean active, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(
		long companyId, int status);

	/**
	 * Returns the number of object definitions.
	 *
	 * @return the number of object definitions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectDefinitionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectDefinitionsCount(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectFolderObjectDefinitions(
		long objectFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectFolderObjectDefinitionsCount(long objectFolderId)
		throws PortalException;

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
	public List<ObjectDefinition> getSystemObjectDefinitions();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getUnmodifiableSystemObjectDefinitions(
		long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasObjectRelationship(long objectDefinitionId);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition publishCustomObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition publishSystemObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException;

	public void undeployObjectDefinition(ObjectDefinition objectDefinition);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long accountEntryRestrictedObjectFieldId,
			long descriptionObjectFieldId, long objectFolderId,
			long titleObjectFieldId, boolean accountEntryRestricted,
			boolean active, String className, boolean enableCategorization,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntryHistory,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, boolean portlet,
			Map<Locale, String> pluralLabelMap, String scope, int status,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateExternalReferenceCode(
			long objectDefinitionId, String externalReferenceCode)
		throws PortalException;

	/**
	 * Updates the object definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateObjectDefinition(
		ObjectDefinition objectDefinition);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateObjectFolderId(
			long objectDefinitionId, long objectFolderId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updatePortlet(long objectDefinitionId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateRootDescendantNodeObjectDefinition(
		ObjectDefinition objectDefinition, long rootObjectDefinitionId);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateRootObjectDefinitionId(
			long objectDefinitionId, long rootObjectDefinitionId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateSystemObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long objectFolderId, long titleObjectFieldId,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws PortalException;

	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws PortalException;

}