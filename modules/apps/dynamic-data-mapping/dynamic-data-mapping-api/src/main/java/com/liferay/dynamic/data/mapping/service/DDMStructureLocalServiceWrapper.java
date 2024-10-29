/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMStructureLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLocalService
 * @generated
 */
public class DDMStructureLocalServiceWrapper
	implements DDMStructureLocalService,
			   ServiceWrapper<DDMStructureLocalService> {

	public DDMStructureLocalServiceWrapper() {
		this(null);
	}

	public DDMStructureLocalServiceWrapper(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	/**
	 * Adds the ddm structure to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructure the ddm structure
	 * @return the ddm structure that was added
	 */
	@Override
	public DDMStructure addDDMStructure(DDMStructure ddmStructure) {
		return _ddmStructureLocalService.addDDMStructure(ddmStructure);
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, long parentStructureId, long classNameId,
			String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			String storageType, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.addStructure(
			userId, groupId, parentStructureId, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType, type,
			serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, long parentStructureId, long classNameId,
			String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String definition, String storageType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.addStructure(
			userId, groupId, parentStructureId, classNameId, structureKey,
			nameMap, descriptionMap, definition, storageType, serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, long classNameId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			String storageType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.addStructure(
			userId, groupId, classNameId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, storageType, serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			long userId, long groupId, String parentStructureKey,
			long classNameId, String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			String storageType, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.addStructure(
			userId, groupId, parentStructureKey, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, storageType, type,
			serviceContext);
	}

	@Override
	public DDMStructure addStructure(
			String externalReferenceCode, long userId, long groupId,
			long parentStructureId, long classNameId, String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			String storageType, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.addStructure(
			externalReferenceCode, userId, groupId, parentStructureId,
			classNameId, structureKey, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, storageType, type, serviceContext);
	}

	/**
	 * Adds the resources to the structure.
	 *
	 * @param structure the structure to add resources to
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 */
	@Override
	public void addStructureResources(
			DDMStructure structure, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.addStructureResources(
			structure, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the model resources with the permissions to the structure.
	 *
	 * @param structure the structure to add resources to
	 * @param modelPermissions the model permissions to be added
	 */
	@Override
	public void addStructureResources(
			DDMStructure structure,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.addStructureResources(
			structure, modelPermissions);
	}

	/**
	 * Copies a structure, creating a new structure with all the values
	 * extracted from the original one. The new structure supports a new name
	 * and description.
	 *
	 * @param userId the primary key of the structure's creator/owner
	 * @param sourceStructureId the primary key of the structure to be copied
	 * @param nameMap the new structure's locales and localized names
	 * @param descriptionMap the new structure's locales and localized
	 descriptions
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, guest permissions, and
	 group permissions for the structure.
	 * @return the new structure
	 */
	@Override
	public DDMStructure copyStructure(
			long userId, long sourceStructureId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.copyStructure(
			userId, sourceStructureId, nameMap, descriptionMap, serviceContext);
	}

	@Override
	public DDMStructure copyStructure(
			long userId, long sourceStructureId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.copyStructure(
			userId, sourceStructureId, serviceContext);
	}

	/**
	 * Creates a new ddm structure with the primary key. Does not add the ddm structure to the database.
	 *
	 * @param structureId the primary key for the new ddm structure
	 * @return the new ddm structure
	 */
	@Override
	public DDMStructure createDDMStructure(long structureId) {
		return _ddmStructureLocalService.createDDMStructure(structureId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ddm structure from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructure the ddm structure
	 * @return the ddm structure that was removed
	 */
	@Override
	public DDMStructure deleteDDMStructure(DDMStructure ddmStructure) {
		return _ddmStructureLocalService.deleteDDMStructure(ddmStructure);
	}

	/**
	 * Deletes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws PortalException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure deleteDDMStructure(long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.deleteDDMStructure(structureId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, this method verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param structure the structure to be deleted
	 */
	@Override
	public DDMStructure deleteStructure(DDMStructure structure)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.deleteStructure(structure);
	}

	/**
	 * Deletes the structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, the system verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param structureId the primary key of the structure to be deleted
	 */
	@Override
	public void deleteStructure(long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.deleteStructure(structureId);
	}

	/**
	 * Deletes the matching structure and its resources.
	 *
	 * <p>
	 * Before deleting the structure, the system verifies whether the structure
	 * is required by another entity. If it is needed, an exception is thrown.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param structureKey the unique string identifying the structure
	 */
	@Override
	public void deleteStructure(
			long groupId, long classNameId, String structureKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.deleteStructure(
			groupId, classNameId, structureKey);
	}

	@Override
	public void deleteStructure(
			String externalReferenceCode, long groupId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.deleteStructure(
			externalReferenceCode, groupId, classNameId);
	}

	/**
	 * Deletes all the structures of the group.
	 *
	 * <p>
	 * Before deleting the structures, the system verifies whether each
	 * structure is required by another entity. If any of the structures are
	 * needed, an exception is thrown.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 */
	@Override
	public void deleteStructures(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.deleteStructures(groupId);
	}

	@Override
	public void deleteStructures(long groupId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.deleteStructures(groupId, classNameId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmStructureLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmStructureLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmStructureLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _ddmStructureLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _ddmStructureLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _ddmStructureLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _ddmStructureLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _ddmStructureLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMStructure fetchDDMStructure(long structureId) {
		return _ddmStructureLocalService.fetchDDMStructure(structureId);
	}

	/**
	 * Returns the ddm structure matching the UUID and group.
	 *
	 * @param uuid the ddm structure's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure fetchDDMStructureByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the structure with the ID.
	 *
	 * @param structureId the primary key of the structure
	 * @return the structure with the structure ID, or <code>null</code> if a
	 matching structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(long structureId) {
		return _ddmStructureLocalService.fetchStructure(structureId);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param structureKey the unique string identifying the structure
	 * @return the matching structure, or <code>null</code> if a matching
	 structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(
		long groupId, long classNameId, String structureKey) {

		return _ddmStructureLocalService.fetchStructure(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group, optionally searching ancestor sites (that have sharing enabled)
	 * and global scoped sites.
	 *
	 * <p>
	 * This method first searches in the group. If the structure is still not
	 * found and <code>includeAncestorStructures</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param structureKey the unique string identifying the structure
	 * @param includeAncestorStructures whether to include ancestor sites (that
	 have sharing enabled) and include global scoped sites in the
	 search
	 * @return the matching structure, or <code>null</code> if a matching
	 structure could not be found
	 */
	@Override
	public DDMStructure fetchStructure(
		long groupId, long classNameId, String structureKey,
		boolean includeAncestorStructures) {

		return _ddmStructureLocalService.fetchStructure(
			groupId, classNameId, structureKey, includeAncestorStructures);
	}

	@Override
	public DDMStructure fetchStructure(
		String externalReferenceCode, long groupId, long classNameId) {

		return _ddmStructureLocalService.fetchStructure(
			externalReferenceCode, groupId, classNameId);
	}

	@Override
	public DDMStructure fetchStructureByUuidAndGroupId(
		String uuid, long groupId, boolean includeAncestorStructures) {

		return _ddmStructureLocalService.fetchStructureByUuidAndGroupId(
			uuid, groupId, includeAncestorStructures);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmStructureLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<DDMStructure> getChildrenStructures(
		long parentStructureId) {

		return _ddmStructureLocalService.getChildrenStructures(
			parentStructureId);
	}

	/**
	 * Returns all the structures matching the class name ID.
	 *
	 * @param companyId the primary key of the structure's company
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @return the structures matching the class name ID
	 */
	@Override
	public java.util.List<DDMStructure> getClassStructures(
		long companyId, long classNameId) {

		return _ddmStructureLocalService.getClassStructures(
			companyId, classNameId);
	}

	/**
	 * Returns a range of all the structures matching the class name ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the structure's company
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getClassStructures(
		long companyId, long classNameId, int start, int end) {

		return _ddmStructureLocalService.getClassStructures(
			companyId, classNameId, start, end);
	}

	/**
	 * Returns all the structures matching the class name ID ordered by the
	 * comparator.
	 *
	 * @param companyId the primary key of the structure's company
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param orderByComparator the comparator to order the structures
	 (optionally <code>null</code>)
	 * @return the matching structures ordered by the comparator
	 */
	@Override
	public java.util.List<DDMStructure> getClassStructures(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getClassStructures(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the ddm structure with the primary key.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws PortalException if a ddm structure with the primary key could not be found
	 */
	@Override
	public DDMStructure getDDMStructure(long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getDDMStructure(structureId);
	}

	/**
	 * Returns the ddm structure matching the UUID and group.
	 *
	 * @param uuid the ddm structure's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm structure
	 * @throws PortalException if a matching ddm structure could not be found
	 */
	@Override
	public DDMStructure getDDMStructureByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getDDMStructureByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of ddm structures
	 */
	@Override
	public java.util.List<DDMStructure> getDDMStructures(int start, int end) {
		return _ddmStructureLocalService.getDDMStructures(start, end);
	}

	/**
	 * Returns all the ddm structures matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structures
	 * @param companyId the primary key of the company
	 * @return the matching ddm structures, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DDMStructure> getDDMStructuresByUuidAndCompanyId(
		String uuid, long companyId) {

		return _ddmStructureLocalService.getDDMStructuresByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of ddm structures matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm structures
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm structures, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DDMStructure> getDDMStructuresByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getDDMStructuresByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm structures.
	 *
	 * @return the number of ddm structures
	 */
	@Override
	public int getDDMStructuresCount() {
		return _ddmStructureLocalService.getDDMStructuresCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _ddmStructureLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmStructureLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmStructureLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the structure with the ID.
	 *
	 * @param structureId the primary key of the structure
	 * @return the structure with the ID
	 */
	@Override
	public DDMStructure getStructure(long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getStructure(structureId);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group.
	 *
	 * @param groupId the primary key of the structure's group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param structureKey the unique string identifying the structure
	 * @return the matching structure
	 */
	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getStructure(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the structure matching the class name ID, structure key, and
	 * group, optionally searching ancestor sites (that have sharing enabled)
	 * and global scoped sites.
	 *
	 * <p>
	 * This method first searches in the group. If the structure is still not
	 * found and <code>includeAncestorStructures</code> is set to
	 * <code>true</code>, this method searches the group's ancestor sites (that
	 * have sharing enabled) and lastly searches global scoped sites.
	 * </p>
	 *
	 * @param groupId the primary key of the structure's group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param structureKey the unique string identifying the structure
	 * @param includeAncestorStructures whether to include ancestor sites (that
	 have sharing enabled) and include global scoped sites in the
	 search in the search
	 * @return the matching structure
	 */
	@Override
	public DDMStructure getStructure(
			long groupId, long classNameId, String structureKey,
			boolean includeAncestorStructures)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getStructure(
			groupId, classNameId, structureKey, includeAncestorStructures);
	}

	/**
	 * Returns all the structures matching the group, name, and description.
	 *
	 * @param groupId the primary key of the structure's group
	 * @param name the structure's name
	 * @param description the structure's description
	 * @return the matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructure(
		long groupId, String name, String description) {

		return _ddmStructureLocalService.getStructure(
			groupId, name, description);
	}

	@Override
	public DDMStructure getStructure(
			String externalReferenceCode, long groupId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getStructure(
			externalReferenceCode, groupId, classNameId);
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMForm getStructureDDMForm(
			DDMStructure structure)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.getStructureDDMForm(structure);
	}

	/**
	 * Returns all the structures present in the system.
	 *
	 * @return the structures present in the system
	 */
	@Override
	public java.util.List<DDMStructure> getStructures() {
		return _ddmStructureLocalService.getStructures();
	}

	/**
	 * Returns all the structures present in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the structures present in the group
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(long groupId) {
		return _ddmStructureLocalService.getStructures(groupId);
	}

	/**
	 * Returns a range of all the structures belonging to the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long groupId, int start, int end) {

		return _ddmStructureLocalService.getStructures(groupId, start, end);
	}

	/**
	 * Returns all the structures matching class name ID and group.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @return the matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long groupId, long classNameId) {

		return _ddmStructureLocalService.getStructures(groupId, classNameId);
	}

	/**
	 * Returns a range of all the structures that match the class name ID and
	 * group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long groupId, long classNameId, int start, int end) {

		return _ddmStructureLocalService.getStructures(
			groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the structures matching the class name ID
	 * and group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the structures
	 (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getStructures(
			groupId, classNameId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getStructures(
			companyId, groupIds, classNameId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<DDMStructure> getStructures(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getStructures(
			companyId, groupIds, classNameId, keywords, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<DDMStructure> getStructures(
		long groupId, String name, String description) {

		return _ddmStructureLocalService.getStructures(
			groupId, name, description);
	}

	/**
	 * Returns all the structures belonging to the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the structures belonging to the groups
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(long[] groupIds) {
		return _ddmStructureLocalService.getStructures(groupIds);
	}

	/**
	 * Returns all the structures matching the class name ID and belonging to
	 * the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @return the matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long[] groupIds, long classNameId) {

		return _ddmStructureLocalService.getStructures(groupIds, classNameId);
	}

	/**
	 * Returns a range of all the structures matching the class name ID and
	 * belonging to the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @return the range of matching structures
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long[] groupIds, long classNameId, int start, int end) {

		return _ddmStructureLocalService.getStructures(
			groupIds, classNameId, start, end);
	}

	@Override
	public java.util.List<DDMStructure> getStructures(
		long[] groupIds, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getStructures(
			groupIds, classNameId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the structures matching the group, class
	 * name ID, name, and description.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @param name the name keywords
	 * @param description the description keywords
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the structures
	 (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public java.util.List<DDMStructure> getStructures(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.getStructures(
			groupIds, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of structures belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of structures belonging to the group
	 */
	@Override
	public int getStructuresCount(long groupId) {
		return _ddmStructureLocalService.getStructuresCount(groupId);
	}

	/**
	 * Returns the number of structures matching the class name ID and group.
	 *
	 * @param groupId the primary key of the group
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @return the number of matching structures
	 */
	@Override
	public int getStructuresCount(long groupId, long classNameId) {
		return _ddmStructureLocalService.getStructuresCount(
			groupId, classNameId);
	}

	@Override
	public int getStructuresCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status) {

		return _ddmStructureLocalService.getStructuresCount(
			companyId, groupIds, classNameId, keywords, status);
	}

	/**
	 * Returns the number of structures matching the class name ID and belonging
	 * to the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name for the structure's
	 related model
	 * @return the number of matching structures
	 */
	@Override
	public int getStructuresCount(long[] groupIds, long classNameId) {
		return _ddmStructureLocalService.getStructuresCount(
			groupIds, classNameId);
	}

	@Override
	public boolean hasStructure(
		long groupId, long classNameId, String structureKey) {

		return _ddmStructureLocalService.hasStructure(
			groupId, classNameId, structureKey);
	}

	@Override
	public String prepareLocalizedDefinitionForImport(
		DDMStructure structure, java.util.Locale defaultImportLocale) {

		return _ddmStructureLocalService.prepareLocalizedDefinitionForImport(
			structure, defaultImportLocale);
	}

	@Override
	public void revertStructure(
			long userId, long structureId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLocalService.revertStructure(
			userId, structureId, version, serviceContext);
	}

	@Override
	public java.util.List<DDMStructure> search(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.search(
			companyId, groupIds, classNameId, classPK, keywords, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the structures matching the groups and
	 * class name IDs, and matching the keywords in the structure names and
	 * descriptions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the structure's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name of the model the
	 structure is related to
	 * @param keywords the keywords (space separated), which may occur in the
	 structure's name or description (optionally <code>null</code>)
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the structures
	 (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public java.util.List<DDMStructure> search(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.search(
			companyId, groupIds, classNameId, keywords, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the structures matching the groups, class
	 * name IDs, name keyword, description keyword, storage type, and type.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the structure's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name of the model the
	 structure is related to
	 * @param name the name keywords
	 * @param description the description keywords
	 * @param storageType the structure's storage type. It can be "xml" or
	 "expando". For more information, see {@link StorageType}.
	 * @param type the structure's type. For more information, see {@link
	 DDMStructureConstants}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field
	 * @param start the lower bound of the range of structures to return
	 * @param end the upper bound of the range of structures to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the structures
	 (optionally <code>null</code>)
	 * @return the range of matching structures ordered by the comparator
	 */
	@Override
	public java.util.List<DDMStructure> search(
		long companyId, long[] groupIds, long classNameId, String name,
		String description, String storageType, int type, int status,
		boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator) {

		return _ddmStructureLocalService.search(
			companyId, groupIds, classNameId, name, description, storageType,
			type, status, andOperator, start, end, orderByComparator);
	}

	@Override
	public int searchCount(
			long companyId, long[] groupIds, long classNameId, long classPK,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.searchCount(
			companyId, groupIds, classNameId, classPK, keywords);
	}

	/**
	 * Returns the number of structures matching the groups and class name IDs,
	 * and matching the keywords in the structure names and descriptions.
	 *
	 * @param companyId the primary key of the structure's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameId the primary key of the class name of the model the
	 structure is related to
	 * @param keywords the keywords (space separated), which may occur in the
	 structure's name or description (optionally <code>null</code>)
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String keywords,
		int status) {

		return _ddmStructureLocalService.searchCount(
			companyId, groupIds, classNameId, keywords, status);
	}

	/**
	 * Returns the number of structures matching the groups, class name IDs,
	 * name keyword, description keyword, storage type, and type
	 *
	 * @param companyId the primary key of the structure's company
	 * @param groupIds the primary keys of the groups
	 * @param classNameId
	 * @param name the name keywords
	 * @param description the description keywords
	 * @param storageType the structure's storage type. It can be "xml" or
	 "expando". For more information, see {@link StorageType}.
	 * @param type the structure's type. For more information, see {@link
	 DDMStructureConstants}.
	 * @param andOperator whether every field must match its keywords, or just
	 one field
	 * @return the number of matching structures
	 */
	@Override
	public int searchCount(
		long companyId, long[] groupIds, long classNameId, String name,
		String description, String storageType, int type, int status,
		boolean andOperator) {

		return _ddmStructureLocalService.searchCount(
			companyId, groupIds, classNameId, name, description, storageType,
			type, status, andOperator);
	}

	/**
	 * Updates the ddm structure in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructure the ddm structure
	 * @return the ddm structure that was updated
	 */
	@Override
	public DDMStructure updateDDMStructure(DDMStructure ddmStructure) {
		return _ddmStructureLocalService.updateDDMStructure(ddmStructure);
	}

	@Override
	public DDMStructure updateStructure(
			long userId, long structureId,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			userId, structureId, ddmForm, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			long userId, long groupId, long parentStructureId, long classNameId,
			String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			userId, groupId, parentStructureId, classNameId, structureKey,
			nameMap, descriptionMap, ddmForm, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			long userId, long structureId, long parentStructureId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			userId, structureId, parentStructureId, nameMap, descriptionMap,
			ddmForm, ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			long userId, long structureId, long parentStructureId,
			String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			userId, structureId, parentStructureId, structureKey, nameMap,
			descriptionMap, definition, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			String externalReferenceCode, long userId, long groupId,
			long structureId, long parentStructureId, long classNameId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.model.DDMForm ddmForm,
			com.liferay.dynamic.data.mapping.model.DDMFormLayout ddmFormLayout,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			externalReferenceCode, userId, groupId, structureId,
			parentStructureId, classNameId, nameMap, descriptionMap, ddmForm,
			ddmFormLayout, serviceContext);
	}

	@Override
	public DDMStructure updateStructure(
			String externalReferenceCode, long userId, long groupId,
			long structureId, long parentStructureId, long classNameId,
			String structureKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String definition,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLocalService.updateStructure(
			externalReferenceCode, userId, groupId, structureId,
			parentStructureId, classNameId, structureKey, nameMap,
			descriptionMap, definition, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmStructureLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMStructure> getCTPersistence() {
		return _ddmStructureLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMStructure> getModelClass() {
		return _ddmStructureLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStructure>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmStructureLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMStructureLocalService getWrappedService() {
		return _ddmStructureLocalService;
	}

	@Override
	public void setWrappedService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	private DDMStructureLocalService _ddmStructureLocalService;

}