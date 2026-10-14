/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DEDataDefinitionFieldLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DEDataDefinitionFieldLinkLocalService
 * @generated
 */
public class DEDataDefinitionFieldLinkLocalServiceWrapper
	implements DEDataDefinitionFieldLinkLocalService,
			   ServiceWrapper<DEDataDefinitionFieldLinkLocalService> {

	public DEDataDefinitionFieldLinkLocalServiceWrapper() {
		this(null);
	}

	public DEDataDefinitionFieldLinkLocalServiceWrapper(
		DEDataDefinitionFieldLinkLocalService
			deDataDefinitionFieldLinkLocalService) {

		_deDataDefinitionFieldLinkLocalService =
			deDataDefinitionFieldLinkLocalService;
	}

	/**
	 * Adds the de data definition field link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was added
	 */
	@Override
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return _deDataDefinitionFieldLinkLocalService.
			addDEDataDefinitionFieldLink(deDataDefinitionFieldLink);
	}

	@Override
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.
			addDEDataDefinitionFieldLink(
				groupId, classNameId, classPK, ddmStructureId, fieldName);
	}

	@Override
	public DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.
			addDEDataDefinitionFieldLink(
				groupId, classNameId, classPK, ddmStructureId, fieldName,
				serviceContext);
	}

	/**
	 * Creates a new de data definition field link with the primary key. Does not add the de data definition field link to the database.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key for the new de data definition field link
	 * @return the new de data definition field link
	 */
	@Override
	public DEDataDefinitionFieldLink createDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId) {

		return _deDataDefinitionFieldLinkLocalService.
			createDEDataDefinitionFieldLink(deDataDefinitionFieldLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the de data definition field link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was removed
	 */
	@Override
	public DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return _deDataDefinitionFieldLinkLocalService.
			deleteDEDataDefinitionFieldLink(deDataDefinitionFieldLink);
	}

	/**
	 * Deletes the de data definition field link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link that was removed
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.
			deleteDEDataDefinitionFieldLink(deDataDefinitionFieldLinkId);
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(long ddmStructureId) {
		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			ddmStructureId);
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long classPK) {

		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			classNameId, classPK);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#deleteDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName) {

		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldName);
	}

	@Override
	public void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		_deDataDefinitionFieldLinkLocalService.deleteDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldNames);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _deDataDefinitionFieldLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _deDataDefinitionFieldLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _deDataDefinitionFieldLinkLocalService.dynamicQuery();
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

		return _deDataDefinitionFieldLinkLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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

		return _deDataDefinitionFieldLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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

		return _deDataDefinitionFieldLinkLocalService.dynamicQuery(
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

		return _deDataDefinitionFieldLinkLocalService.dynamicQueryCount(
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

		return _deDataDefinitionFieldLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId) {

		return _deDataDefinitionFieldLinkLocalService.
			fetchDEDataDefinitionFieldLink(deDataDefinitionFieldLinkId);
	}

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink
		fetchDEDataDefinitionFieldLinkByUuidAndGroupId(
			String uuid, long groupId) {

		return _deDataDefinitionFieldLinkLocalService.
			fetchDEDataDefinitionFieldLinkByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLinks(
		long classNameId, long classPK, long ddmStructureId, String fieldName) {

		return _deDataDefinitionFieldLinkLocalService.
			fetchDEDataDefinitionFieldLinks(
				classNameId, classPK, ddmStructureId, fieldName);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _deDataDefinitionFieldLinkLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<Long> getClassPKS(
		long classNameId, long ddmStructureId) {

		return _deDataDefinitionFieldLinkLocalService.getClassPKS(
			classNameId, ddmStructureId);
	}

	/**
	 * Returns the de data definition field link with the primary key.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink getDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLink(deDataDefinitionFieldLinkId);
	}

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link
	 * @throws PortalException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink
			getDEDataDefinitionFieldLinkByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinkByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the de data definition field links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @return the range of de data definition field links
	 */
	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinks(int start, int end) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinks(start, end);
	}

	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinks(long ddmStructureId) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinks(ddmStructureId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#getDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinks(
			long classNameId, long ddmStructureId, String fieldName) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinks(
				classNameId, ddmStructureId, fieldName);
	}

	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinks(
			long classNameId, long ddmStructureId, String[] fieldNames) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinks(
				classNameId, ddmStructureId, fieldNames);
	}

	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinks(
			long ddmStructureId, String[] fieldNames) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinks(ddmStructureId, fieldNames);
	}

	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
			long classNameId, long classPK) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
				classNameId, classPK);
	}

	/**
	 * Returns all the de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @return the matching de data definition field links, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinksByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching de data definition field links, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<DEDataDefinitionFieldLink> orderByComparator) {

		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinksByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of de data definition field links.
	 *
	 * @return the number of de data definition field links
	 */
	@Override
	public int getDEDataDefinitionFieldLinksCount() {
		return _deDataDefinitionFieldLinkLocalService.
			getDEDataDefinitionFieldLinksCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _deDataDefinitionFieldLinkLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _deDataDefinitionFieldLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _deDataDefinitionFieldLinkLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _deDataDefinitionFieldLinkLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the de data definition field link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was updated
	 */
	@Override
	public DEDataDefinitionFieldLink updateDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return _deDataDefinitionFieldLinkLocalService.
			updateDEDataDefinitionFieldLink(deDataDefinitionFieldLink);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _deDataDefinitionFieldLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DEDataDefinitionFieldLink> getCTPersistence() {
		return _deDataDefinitionFieldLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<DEDataDefinitionFieldLink> getModelClass() {
		return _deDataDefinitionFieldLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DEDataDefinitionFieldLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _deDataDefinitionFieldLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DEDataDefinitionFieldLinkLocalService getWrappedService() {
		return _deDataDefinitionFieldLinkLocalService;
	}

	@Override
	public void setWrappedService(
		DEDataDefinitionFieldLinkLocalService
			deDataDefinitionFieldLinkLocalService) {

		_deDataDefinitionFieldLinkLocalService =
			deDataDefinitionFieldLinkLocalService;
	}

	private DEDataDefinitionFieldLinkLocalService
		_deDataDefinitionFieldLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2070426916