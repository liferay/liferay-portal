/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMStorageLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStorageLinkLocalService
 * @generated
 */
public class DDMStorageLinkLocalServiceWrapper
	implements DDMStorageLinkLocalService,
			   ServiceWrapper<DDMStorageLinkLocalService> {

	public DDMStorageLinkLocalServiceWrapper() {
		this(null);
	}

	public DDMStorageLinkLocalServiceWrapper(
		DDMStorageLinkLocalService ddmStorageLinkLocalService) {

		_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
	}

	/**
	 * Adds the ddm storage link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStorageLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStorageLink the ddm storage link
	 * @return the ddm storage link that was added
	 */
	@Override
	public DDMStorageLink addDDMStorageLink(DDMStorageLink ddmStorageLink) {
		return _ddmStorageLinkLocalService.addDDMStorageLink(ddmStorageLink);
	}

	@Override
	public DDMStorageLink addStorageLink(
			long classNameId, long classPK, long structureVersionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.addStorageLink(
			classNameId, classPK, structureVersionId, serviceContext);
	}

	/**
	 * Creates a new ddm storage link with the primary key. Does not add the ddm storage link to the database.
	 *
	 * @param storageLinkId the primary key for the new ddm storage link
	 * @return the new ddm storage link
	 */
	@Override
	public DDMStorageLink createDDMStorageLink(long storageLinkId) {
		return _ddmStorageLinkLocalService.createDDMStorageLink(storageLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteClassStorageLink(long classPK) {
		_ddmStorageLinkLocalService.deleteClassStorageLink(classPK);
	}

	/**
	 * Deletes the ddm storage link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStorageLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStorageLink the ddm storage link
	 * @return the ddm storage link that was removed
	 */
	@Override
	public DDMStorageLink deleteDDMStorageLink(DDMStorageLink ddmStorageLink) {
		return _ddmStorageLinkLocalService.deleteDDMStorageLink(ddmStorageLink);
	}

	/**
	 * Deletes the ddm storage link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStorageLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param storageLinkId the primary key of the ddm storage link
	 * @return the ddm storage link that was removed
	 * @throws PortalException if a ddm storage link with the primary key could not be found
	 */
	@Override
	public DDMStorageLink deleteDDMStorageLink(long storageLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.deleteDDMStorageLink(storageLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteStorageLink(DDMStorageLink storageLink) {
		_ddmStorageLinkLocalService.deleteStorageLink(storageLink);
	}

	@Override
	public void deleteStorageLink(long storageLinkId) {
		_ddmStorageLinkLocalService.deleteStorageLink(storageLinkId);
	}

	@Override
	public void deleteStructureStorageLinks(long structureId) {
		_ddmStorageLinkLocalService.deleteStructureStorageLinks(structureId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmStorageLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmStorageLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmStorageLinkLocalService.dynamicQuery();
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

		return _ddmStorageLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl</code>.
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

		return _ddmStorageLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl</code>.
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

		return _ddmStorageLinkLocalService.dynamicQuery(
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

		return _ddmStorageLinkLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmStorageLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMStorageLink fetchDDMStorageLink(long storageLinkId) {
		return _ddmStorageLinkLocalService.fetchDDMStorageLink(storageLinkId);
	}

	/**
	 * Returns the ddm storage link with the matching UUID and company.
	 *
	 * @param uuid the ddm storage link's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ddm storage link, or <code>null</code> if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink fetchDDMStorageLinkByUuidAndCompanyId(
		String uuid, long companyId) {

		return _ddmStorageLinkLocalService.
			fetchDDMStorageLinkByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmStorageLinkLocalService.getActionableDynamicQuery();
	}

	@Override
	public DDMStorageLink getClassStorageLink(long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.getClassStorageLink(classPK);
	}

	/**
	 * Returns the ddm storage link with the primary key.
	 *
	 * @param storageLinkId the primary key of the ddm storage link
	 * @return the ddm storage link
	 * @throws PortalException if a ddm storage link with the primary key could not be found
	 */
	@Override
	public DDMStorageLink getDDMStorageLink(long storageLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.getDDMStorageLink(storageLinkId);
	}

	/**
	 * Returns the ddm storage link with the matching UUID and company.
	 *
	 * @param uuid the ddm storage link's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ddm storage link
	 * @throws PortalException if a matching ddm storage link could not be found
	 */
	@Override
	public DDMStorageLink getDDMStorageLinkByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.getDDMStorageLinkByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the ddm storage links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStorageLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm storage links
	 * @param end the upper bound of the range of ddm storage links (not inclusive)
	 * @return the range of ddm storage links
	 */
	@Override
	public java.util.List<DDMStorageLink> getDDMStorageLinks(
		int start, int end) {

		return _ddmStorageLinkLocalService.getDDMStorageLinks(start, end);
	}

	/**
	 * Returns the number of ddm storage links.
	 *
	 * @return the number of ddm storage links
	 */
	@Override
	public int getDDMStorageLinksCount() {
		return _ddmStorageLinkLocalService.getDDMStorageLinksCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmStorageLinkLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmStorageLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStorageLinkLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public int getStructureStorageLinksCount(long structureId) {
		return _ddmStorageLinkLocalService.getStructureStorageLinksCount(
			structureId);
	}

	/**
	 * Updates the ddm storage link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStorageLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStorageLink the ddm storage link
	 * @return the ddm storage link that was updated
	 */
	@Override
	public DDMStorageLink updateDDMStorageLink(DDMStorageLink ddmStorageLink) {
		return _ddmStorageLinkLocalService.updateDDMStorageLink(ddmStorageLink);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmStorageLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMStorageLink> getCTPersistence() {
		return _ddmStorageLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMStorageLink> getModelClass() {
		return _ddmStorageLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStorageLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmStorageLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMStorageLinkLocalService getWrappedService() {
		return _ddmStorageLinkLocalService;
	}

	@Override
	public void setWrappedService(
		DDMStorageLinkLocalService ddmStorageLinkLocalService) {

		_ddmStorageLinkLocalService = ddmStorageLinkLocalService;
	}

	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1667023335