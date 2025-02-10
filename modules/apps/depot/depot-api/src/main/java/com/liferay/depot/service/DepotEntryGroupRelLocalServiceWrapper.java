/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DepotEntryGroupRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelLocalService
 * @generated
 */
public class DepotEntryGroupRelLocalServiceWrapper
	implements DepotEntryGroupRelLocalService,
			   ServiceWrapper<DepotEntryGroupRelLocalService> {

	public DepotEntryGroupRelLocalServiceWrapper() {
		this(null);
	}

	public DepotEntryGroupRelLocalServiceWrapper(
		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService) {

		_depotEntryGroupRelLocalService = depotEntryGroupRelLocalService;
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		boolean ddmStructuresAvailable, long depotEntryId, long toGroupId,
		boolean searchable) {

		return _depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			ddmStructuresAvailable, depotEntryId, toGroupId, searchable);
	}

	/**
	 * Adds the depot entry group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was added
	 */
	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return _depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId) {

		return _depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntryId, toGroupId);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId, boolean searchable) {

		return _depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntryId, toGroupId, searchable);
	}

	/**
	 * Creates a new depot entry group rel with the primary key. Does not add the depot entry group rel to the database.
	 *
	 * @param depotEntryGroupRelId the primary key for the new depot entry group rel
	 * @return the new depot entry group rel
	 */
	@Override
	public DepotEntryGroupRel createDepotEntryGroupRel(
		long depotEntryGroupRelId) {

		return _depotEntryGroupRelLocalService.createDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the depot entry group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was removed
	 */
	@Override
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return _depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	/**
	 * Deletes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws PortalException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteToGroupDepotEntryGroupRels(long toGroupId) {
		_depotEntryGroupRelLocalService.deleteToGroupDepotEntryGroupRels(
			toGroupId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _depotEntryGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _depotEntryGroupRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _depotEntryGroupRelLocalService.dynamicQuery();
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

		return _depotEntryGroupRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
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

		return _depotEntryGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
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

		return _depotEntryGroupRelLocalService.dynamicQuery(
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

		return _depotEntryGroupRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _depotEntryGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DepotEntryGroupRel fetchDepotEntryGroupRel(
		long depotEntryGroupRelId) {

		return _depotEntryGroupRelLocalService.fetchDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	@Override
	public DepotEntryGroupRel fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
		long depotEntryId, long toGroupId) {

		return _depotEntryGroupRelLocalService.
			fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
				depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel matching the UUID and group.
	 *
	 * @param uuid the depot entry group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel fetchDepotEntryGroupRelByUuidAndGroupId(
		String uuid, long groupId) {

		return _depotEntryGroupRelLocalService.
			fetchDepotEntryGroupRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _depotEntryGroupRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the depot entry group rel with the primary key.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws PortalException if a depot entry group rel with the primary key could not be found
	 */
	@Override
	public DepotEntryGroupRel getDepotEntryGroupRel(long depotEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel matching the UUID and group.
	 *
	 * @param uuid the depot entry group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry group rel
	 * @throws PortalException if a matching depot entry group rel could not be found
	 */
	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.
			getDepotEntryGroupRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.List<DepotEntryGroupRel> getDepotEntryGroupRels(
		com.liferay.depot.model.DepotEntry depotEntry) {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRels(
			depotEntry);
	}

	/**
	 * Returns a range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of depot entry group rels
	 */
	@Override
	public java.util.List<DepotEntryGroupRel> getDepotEntryGroupRels(
		int start, int end) {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRels(
			start, end);
	}

	@Override
	public java.util.List<DepotEntryGroupRel> getDepotEntryGroupRels(
		long groupId, int start, int end) {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRels(
			groupId, start, end);
	}

	/**
	 * Returns all the depot entry group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry group rels
	 * @param companyId the primary key of the company
	 * @return the matching depot entry group rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DepotEntryGroupRel>
		getDepotEntryGroupRelsByUuidAndCompanyId(String uuid, long companyId) {

		return _depotEntryGroupRelLocalService.
			getDepotEntryGroupRelsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of depot entry group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching depot entry group rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DepotEntryGroupRel>
		getDepotEntryGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator) {

		return _depotEntryGroupRelLocalService.
			getDepotEntryGroupRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of depot entry group rels.
	 *
	 * @return the number of depot entry group rels
	 */
	@Override
	public int getDepotEntryGroupRelsCount() {
		return _depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount();
	}

	@Override
	public int getDepotEntryGroupRelsCount(
		com.liferay.depot.model.DepotEntry depotEntry) {

		return _depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
			depotEntry);
	}

	@Override
	public int getDepotEntryGroupRelsCount(long groupId) {
		return _depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
			groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _depotEntryGroupRelLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _depotEntryGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _depotEntryGroupRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<DepotEntryGroupRel> getSearchableDepotEntryGroupRels(
		long groupId, int start, int end) {

		return _depotEntryGroupRelLocalService.getSearchableDepotEntryGroupRels(
			groupId, start, end);
	}

	@Override
	public int getSearchableDepotEntryGroupRelsCount(long groupId) {
		return _depotEntryGroupRelLocalService.
			getSearchableDepotEntryGroupRelsCount(groupId);
	}

	@Override
	public DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.updateDDMStructuresAvailable(
			depotEntryGroupRelId, ddmStructuresAvailable);
	}

	/**
	 * Updates the depot entry group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was updated
	 */
	@Override
	public DepotEntryGroupRel updateDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return _depotEntryGroupRelLocalService.updateDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelLocalService.updateSearchable(
			depotEntryGroupRelId, searchable);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _depotEntryGroupRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DepotEntryGroupRel> getCTPersistence() {
		return _depotEntryGroupRelLocalService.getCTPersistence();
	}

	@Override
	public Class<DepotEntryGroupRel> getModelClass() {
		return _depotEntryGroupRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DepotEntryGroupRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _depotEntryGroupRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DepotEntryGroupRelLocalService getWrappedService() {
		return _depotEntryGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService) {

		_depotEntryGroupRelLocalService = depotEntryGroupRelLocalService;
	}

	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

}