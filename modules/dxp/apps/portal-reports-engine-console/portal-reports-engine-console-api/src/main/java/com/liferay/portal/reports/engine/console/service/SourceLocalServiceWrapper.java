/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SourceLocalService
 * @generated
 */
public class SourceLocalServiceWrapper
	implements ServiceWrapper<SourceLocalService>, SourceLocalService {

	public SourceLocalServiceWrapper() {
		this(null);
	}

	public SourceLocalServiceWrapper(SourceLocalService sourceLocalService) {
		_sourceLocalService = sourceLocalService;
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Source addSource(
			long userId, long groupId,
			java.util.Map<java.util.Locale, String> nameMap,
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.addSource(
			userId, groupId, nameMap, driverClassName, driverUrl,
			driverUserName, driverPassword, serviceContext);
	}

	/**
	 * Adds the source to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was added
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source addSource(
		com.liferay.portal.reports.engine.console.model.Source source) {

		return _sourceLocalService.addSource(source);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new source with the primary key. Does not add the source to the database.
	 *
	 * @param sourceId the primary key for the new source
	 * @return the new source
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source createSource(
		long sourceId) {

		return _sourceLocalService.createSource(sourceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the source with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sourceId the primary key of the source
	 * @return the source that was removed
	 * @throws PortalException if a source with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source deleteSource(
			long sourceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.deleteSource(sourceId);
	}

	/**
	 * Deletes the source from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source deleteSource(
			com.liferay.portal.reports.engine.console.model.Source source)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.deleteSource(source);
	}

	@Override
	public void deleteSources(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_sourceLocalService.deleteSources(groupId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sourceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sourceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sourceLocalService.dynamicQuery();
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

		return _sourceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
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

		return _sourceLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
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

		return _sourceLocalService.dynamicQuery(
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

		return _sourceLocalService.dynamicQueryCount(dynamicQuery);
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

		return _sourceLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Source fetchSource(
		long sourceId) {

		return _sourceLocalService.fetchSource(sourceId);
	}

	/**
	 * Returns the source matching the UUID and group.
	 *
	 * @param uuid the source's UUID
	 * @param groupId the primary key of the group
	 * @return the matching source, or <code>null</code> if a matching source could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source
		fetchSourceByUuidAndGroupId(String uuid, long groupId) {

		return _sourceLocalService.fetchSourceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sourceLocalService.getActionableDynamicQuery();
	}

	@Override
	public String[] getAttachmentsFileNames(
		com.liferay.portal.reports.engine.console.model.Source source) {

		return _sourceLocalService.getAttachmentsFileNames(source);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _sourceLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sourceLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sourceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the source with the primary key.
	 *
	 * @param sourceId the primary key of the source
	 * @return the source
	 * @throws PortalException if a source with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source getSource(
			long sourceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.getSource(sourceId);
	}

	/**
	 * Returns the source matching the UUID and group.
	 *
	 * @param uuid the source's UUID
	 * @param groupId the primary key of the group
	 * @return the matching source
	 * @throws PortalException if a matching source could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source
			getSourceByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.getSourceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the sources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sources
	 * @param end the upper bound of the range of sources (not inclusive)
	 * @return the range of sources
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Source> getSources(
			int start, int end) {

		return _sourceLocalService.getSources(start, end);
	}

	/**
	 * Returns all the sources matching the UUID and company.
	 *
	 * @param uuid the UUID of the sources
	 * @param companyId the primary key of the company
	 * @return the matching sources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Source>
			getSourcesByUuidAndCompanyId(String uuid, long companyId) {

		return _sourceLocalService.getSourcesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of sources matching the UUID and company.
	 *
	 * @param uuid the UUID of the sources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of sources
	 * @param end the upper bound of the range of sources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching sources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Source>
			getSourcesByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.reports.engine.console.model.Source>
						orderByComparator) {

		return _sourceLocalService.getSourcesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sources.
	 *
	 * @return the number of sources
	 */
	@Override
	public int getSourcesCount() {
		return _sourceLocalService.getSourcesCount();
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Source updateSource(
			long sourceId, java.util.Map<java.util.Locale, String> nameMap,
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sourceLocalService.updateSource(
			sourceId, nameMap, driverClassName, driverUrl, driverUserName,
			driverPassword, serviceContext);
	}

	/**
	 * Updates the source in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was updated
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Source updateSource(
		com.liferay.portal.reports.engine.console.model.Source source) {

		return _sourceLocalService.updateSource(source);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sourceLocalService.getBasePersistence();
	}

	@Override
	public SourceLocalService getWrappedService() {
		return _sourceLocalService;
	}

	@Override
	public void setWrappedService(SourceLocalService sourceLocalService) {
		_sourceLocalService = sourceLocalService;
	}

	private SourceLocalService _sourceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-282234527