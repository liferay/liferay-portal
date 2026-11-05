/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMDataProviderInstanceLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderInstanceLinkLocalService
 * @generated
 */
public class DDMDataProviderInstanceLinkLocalServiceWrapper
	implements DDMDataProviderInstanceLinkLocalService,
			   ServiceWrapper<DDMDataProviderInstanceLinkLocalService> {

	public DDMDataProviderInstanceLinkLocalServiceWrapper() {
		this(null);
	}

	public DDMDataProviderInstanceLinkLocalServiceWrapper(
		DDMDataProviderInstanceLinkLocalService
			ddmDataProviderInstanceLinkLocalService) {

		_ddmDataProviderInstanceLinkLocalService =
			ddmDataProviderInstanceLinkLocalService;
	}

	@Override
	public DDMDataProviderInstanceLink addDataProviderInstanceLink(
		long dataProviderInstanceId, long structureId) {

		return _ddmDataProviderInstanceLinkLocalService.
			addDataProviderInstanceLink(dataProviderInstanceId, structureId);
	}

	/**
	 * Adds the ddm data provider instance link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was added
	 */
	@Override
	public DDMDataProviderInstanceLink addDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return _ddmDataProviderInstanceLinkLocalService.
			addDDMDataProviderInstanceLink(ddmDataProviderInstanceLink);
	}

	/**
	 * Creates a new ddm data provider instance link with the primary key. Does not add the ddm data provider instance link to the database.
	 *
	 * @param dataProviderInstanceLinkId the primary key for the new ddm data provider instance link
	 * @return the new ddm data provider instance link
	 */
	@Override
	public DDMDataProviderInstanceLink createDDMDataProviderInstanceLink(
		long dataProviderInstanceLinkId) {

		return _ddmDataProviderInstanceLinkLocalService.
			createDDMDataProviderInstanceLink(dataProviderInstanceLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteDataProviderInstanceLink(
		DDMDataProviderInstanceLink dataProviderInstanceLink) {

		_ddmDataProviderInstanceLinkLocalService.deleteDataProviderInstanceLink(
			dataProviderInstanceLink);
	}

	@Override
	public void deleteDataProviderInstanceLink(long dataProviderInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmDataProviderInstanceLinkLocalService.deleteDataProviderInstanceLink(
			dataProviderInstanceLinkId);
	}

	/**
	 * Deletes the ddm data provider instance link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was removed
	 */
	@Override
	public DDMDataProviderInstanceLink deleteDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return _ddmDataProviderInstanceLinkLocalService.
			deleteDDMDataProviderInstanceLink(ddmDataProviderInstanceLink);
	}

	/**
	 * Deletes the ddm data provider instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link that was removed
	 * @throws PortalException if a ddm data provider instance link with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink deleteDDMDataProviderInstanceLink(
			long dataProviderInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLinkLocalService.
			deleteDDMDataProviderInstanceLink(dataProviderInstanceLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmDataProviderInstanceLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmDataProviderInstanceLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmDataProviderInstanceLinkLocalService.dynamicQuery();
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

		return _ddmDataProviderInstanceLinkLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
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

		return _ddmDataProviderInstanceLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
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

		return _ddmDataProviderInstanceLinkLocalService.dynamicQuery(
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

		return _ddmDataProviderInstanceLinkLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _ddmDataProviderInstanceLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMDataProviderInstanceLink fetchDDMDataProviderInstanceLink(
		long dataProviderInstanceLinkId) {

		return _ddmDataProviderInstanceLinkLocalService.
			fetchDDMDataProviderInstanceLink(dataProviderInstanceLinkId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmDataProviderInstanceLinkLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<DDMDataProviderInstanceLink>
		getDataProviderInstanceLinks(long structureId) {

		return _ddmDataProviderInstanceLinkLocalService.
			getDataProviderInstanceLinks(structureId);
	}

	/**
	 * Returns the ddm data provider instance link with the primary key.
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link
	 * @throws PortalException if a ddm data provider instance link with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink getDDMDataProviderInstanceLink(
			long dataProviderInstanceLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLinkLocalService.
			getDDMDataProviderInstanceLink(dataProviderInstanceLinkId);
	}

	/**
	 * Returns a range of all the ddm data provider instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instance links
	 * @param end the upper bound of the range of ddm data provider instance links (not inclusive)
	 * @return the range of ddm data provider instance links
	 */
	@Override
	public java.util.List<DDMDataProviderInstanceLink>
		getDDMDataProviderInstanceLinks(int start, int end) {

		return _ddmDataProviderInstanceLinkLocalService.
			getDDMDataProviderInstanceLinks(start, end);
	}

	/**
	 * Returns the number of ddm data provider instance links.
	 *
	 * @return the number of ddm data provider instance links
	 */
	@Override
	public int getDDMDataProviderInstanceLinksCount() {
		return _ddmDataProviderInstanceLinkLocalService.
			getDDMDataProviderInstanceLinksCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmDataProviderInstanceLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmDataProviderInstanceLinkLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLinkLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the ddm data provider instance link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was updated
	 */
	@Override
	public DDMDataProviderInstanceLink updateDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return _ddmDataProviderInstanceLinkLocalService.
			updateDDMDataProviderInstanceLink(ddmDataProviderInstanceLink);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmDataProviderInstanceLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMDataProviderInstanceLink> getCTPersistence() {
		return _ddmDataProviderInstanceLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMDataProviderInstanceLink> getModelClass() {
		return _ddmDataProviderInstanceLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMDataProviderInstanceLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmDataProviderInstanceLinkLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public DDMDataProviderInstanceLinkLocalService getWrappedService() {
		return _ddmDataProviderInstanceLinkLocalService;
	}

	@Override
	public void setWrappedService(
		DDMDataProviderInstanceLinkLocalService
			ddmDataProviderInstanceLinkLocalService) {

		_ddmDataProviderInstanceLinkLocalService =
			ddmDataProviderInstanceLinkLocalService;
	}

	private DDMDataProviderInstanceLinkLocalService
		_ddmDataProviderInstanceLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1035262227