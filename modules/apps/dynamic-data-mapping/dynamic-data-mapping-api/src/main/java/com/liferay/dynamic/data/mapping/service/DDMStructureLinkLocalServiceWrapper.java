/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMStructureLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLinkLocalService
 * @generated
 */
public class DDMStructureLinkLocalServiceWrapper
	implements DDMStructureLinkLocalService,
			   ServiceWrapper<DDMStructureLinkLocalService> {

	public DDMStructureLinkLocalServiceWrapper() {
		this(null);
	}

	public DDMStructureLinkLocalServiceWrapper(
		DDMStructureLinkLocalService ddmStructureLinkLocalService) {

		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
	}

	/**
	 * Adds the ddm structure link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was added
	 */
	@Override
	public DDMStructureLink addDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return _ddmStructureLinkLocalService.addDDMStructureLink(
			ddmStructureLink);
	}

	@Override
	public DDMStructureLink addStructureLink(
		long classNameId, long classPK, long structureId) {

		return _ddmStructureLinkLocalService.addStructureLink(
			classNameId, classPK, structureId);
	}

	/**
	 * Creates a new ddm structure link with the primary key. Does not add the ddm structure link to the database.
	 *
	 * @param structureLinkId the primary key for the new ddm structure link
	 * @return the new ddm structure link
	 */
	@Override
	public DDMStructureLink createDDMStructureLink(long structureLinkId) {
		return _ddmStructureLinkLocalService.createDDMStructureLink(
			structureLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddm structure link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was removed
	 */
	@Override
	public DDMStructureLink deleteDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return _ddmStructureLinkLocalService.deleteDDMStructureLink(
			ddmStructureLink);
	}

	/**
	 * Deletes the ddm structure link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link that was removed
	 * @throws PortalException if a ddm structure link with the primary key could not be found
	 */
	@Override
	public DDMStructureLink deleteDDMStructureLink(long structureLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.deleteDDMStructureLink(
			structureLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteStructureLink(DDMStructureLink structureLink) {
		_ddmStructureLinkLocalService.deleteStructureLink(structureLink);
	}

	@Override
	public void deleteStructureLink(long structureLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLinkLocalService.deleteStructureLink(structureLinkId);
	}

	@Override
	public void deleteStructureLink(
			long classNameId, long classPK, long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmStructureLinkLocalService.deleteStructureLink(
			classNameId, classPK, structureId);
	}

	@Override
	public void deleteStructureLinks(long classNameId, long classPK) {
		_ddmStructureLinkLocalService.deleteStructureLinks(
			classNameId, classPK);
	}

	@Override
	public void deleteStructureStructureLinks(long structureId) {
		_ddmStructureLinkLocalService.deleteStructureStructureLinks(
			structureId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmStructureLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmStructureLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmStructureLinkLocalService.dynamicQuery();
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

		return _ddmStructureLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
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

		return _ddmStructureLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
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

		return _ddmStructureLinkLocalService.dynamicQuery(
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

		return _ddmStructureLinkLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmStructureLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMStructureLink fetchDDMStructureLink(long structureLinkId) {
		return _ddmStructureLinkLocalService.fetchDDMStructureLink(
			structureLinkId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmStructureLinkLocalService.getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public java.util.List<DDMStructureLink> getClassNameStructureLinks(
		long classNameId) {

		return _ddmStructureLinkLocalService.getClassNameStructureLinks(
			classNameId);
	}

	/**
	 * Returns the ddm structure link with the primary key.
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link
	 * @throws PortalException if a ddm structure link with the primary key could not be found
	 */
	@Override
	public DDMStructureLink getDDMStructureLink(long structureLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getDDMStructureLink(
			structureLinkId);
	}

	/**
	 * Returns a range of all the ddm structure links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure links
	 * @param end the upper bound of the range of ddm structure links (not inclusive)
	 * @return the range of ddm structure links
	 */
	@Override
	public java.util.List<DDMStructureLink> getDDMStructureLinks(
		int start, int end) {

		return _ddmStructureLinkLocalService.getDDMStructureLinks(start, end);
	}

	/**
	 * Returns the number of ddm structure links.
	 *
	 * @return the number of ddm structure links
	 */
	@Override
	public int getDDMStructureLinksCount() {
		return _ddmStructureLinkLocalService.getDDMStructureLinksCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmStructureLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmStructureLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public DDMStructureLink getStructureLink(long structureLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getStructureLink(structureLinkId);
	}

	@Override
	public java.util.List<DDMStructureLink> getStructureLinks(
		long structureId) {

		return _ddmStructureLinkLocalService.getStructureLinks(structureId);
	}

	@Override
	public java.util.List<DDMStructureLink> getStructureLinks(
		long structureId, int start, int end) {

		return _ddmStructureLinkLocalService.getStructureLinks(
			structureId, start, end);
	}

	@Override
	public java.util.List<DDMStructureLink> getStructureLinks(
		long classNameId, long classPK) {

		return _ddmStructureLinkLocalService.getStructureLinks(
			classNameId, classPK);
	}

	@Override
	public java.util.List<DDMStructureLink> getStructureLinks(
		long classNameId, long classPK, int start, int end) {

		return _ddmStructureLinkLocalService.getStructureLinks(
			classNameId, classPK, start, end);
	}

	@Override
	public int getStructureLinksCount(long classNameId, long classPK) {
		return _ddmStructureLinkLocalService.getStructureLinksCount(
			classNameId, classPK);
	}

	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getStructureLinkStructures(long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getStructureLinkStructures(
			classNameId, classPK);
	}

	@Override
	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getStructureLinkStructures(
				long classNameId, long classPK, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getStructureLinkStructures(
			classNameId, classPK, start, end);
	}

	@Override
	public DDMStructureLink getUniqueStructureLink(
			long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.getUniqueStructureLink(
			classNameId, classPK);
	}

	/**
	 * Updates the ddm structure link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was updated
	 */
	@Override
	public DDMStructureLink updateDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return _ddmStructureLinkLocalService.updateDDMStructureLink(
			ddmStructureLink);
	}

	@Override
	public DDMStructureLink updateStructureLink(
			long structureLinkId, long classNameId, long classPK,
			long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureLinkLocalService.updateStructureLink(
			structureLinkId, classNameId, classPK, structureId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmStructureLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMStructureLink> getCTPersistence() {
		return _ddmStructureLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMStructureLink> getModelClass() {
		return _ddmStructureLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStructureLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmStructureLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMStructureLinkLocalService getWrappedService() {
		return _ddmStructureLinkLocalService;
	}

	@Override
	public void setWrappedService(
		DDMStructureLinkLocalService ddmStructureLinkLocalService) {

		_ddmStructureLinkLocalService = ddmStructureLinkLocalService;
	}

	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1279273604