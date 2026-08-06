/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LayoutContentVersionLocalService}.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionLocalService
 * @generated
 */
public class LayoutContentVersionLocalServiceWrapper
	implements LayoutContentVersionLocalService,
			   ServiceWrapper<LayoutContentVersionLocalService> {

	public LayoutContentVersionLocalServiceWrapper() {
		this(null);
	}

	public LayoutContentVersionLocalServiceWrapper(
		LayoutContentVersionLocalService layoutContentVersionLocalService) {

		_layoutContentVersionLocalService = layoutContentVersionLocalService;
	}

	/**
	 * Adds the layout content version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was added
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		addLayoutContentVersion(
			com.liferay.layout.content.model.LayoutContentVersion
				layoutContentVersion) {

		return _layoutContentVersionLocalService.addLayoutContentVersion(
			layoutContentVersion);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			addLayoutContentVersion(
				String externalReferenceCode, long userId, String data,
				java.util.Map<java.util.Locale, String> nameMap, long plid,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.addLayoutContentVersion(
			externalReferenceCode, userId, data, nameMap, plid, status);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			addOrUpdateLayoutContentVersion(
				String externalReferenceCode, long userId, String data,
				java.util.Map<java.util.Locale, String> nameMap, long plid,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.
			addOrUpdateLayoutContentVersion(
				externalReferenceCode, userId, data, nameMap, plid, status);
	}

	/**
	 * Creates a new layout content version with the primary key. Does not add the layout content version to the database.
	 *
	 * @param layoutContentVersionId the primary key for the new layout content version
	 * @return the new layout content version
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		createLayoutContentVersion(long layoutContentVersionId) {

		return _layoutContentVersionLocalService.createLayoutContentVersion(
			layoutContentVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout content version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was removed
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		deleteLayoutContentVersion(
			com.liferay.layout.content.model.LayoutContentVersion
				layoutContentVersion) {

		return _layoutContentVersionLocalService.deleteLayoutContentVersion(
			layoutContentVersion);
	}

	/**
	 * Deletes the layout content version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version that was removed
	 * @throws PortalException if a layout content version with the primary key could not be found
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			deleteLayoutContentVersion(long layoutContentVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.deleteLayoutContentVersion(
			layoutContentVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutContentVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutContentVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutContentVersionLocalService.dynamicQuery();
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

		return _layoutContentVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
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

		return _layoutContentVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
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

		return _layoutContentVersionLocalService.dynamicQuery(
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

		return _layoutContentVersionLocalService.dynamicQueryCount(
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

		return _layoutContentVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		fetchLayoutContentVersion(long layoutContentVersionId) {

		return _layoutContentVersionLocalService.fetchLayoutContentVersion(
			layoutContentVersionId);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		fetchLayoutContentVersionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _layoutContentVersionLocalService.
			fetchLayoutContentVersionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutContentVersionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutContentVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public long getLatestApprovedLayoutContentVersionId(long plid) {
		return _layoutContentVersionLocalService.
			getLatestApprovedLayoutContentVersionId(plid);
	}

	/**
	 * Returns the layout content version with the primary key.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version
	 * @throws PortalException if a layout content version with the primary key could not be found
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			getLayoutContentVersion(long layoutContentVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.getLayoutContentVersion(
			layoutContentVersionId);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			getLayoutContentVersionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.
			getLayoutContentVersionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns a range of all the layout content versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of layout content versions
	 */
	@Override
	public java.util.List<com.liferay.layout.content.model.LayoutContentVersion>
		getLayoutContentVersions(int start, int end) {

		return _layoutContentVersionLocalService.getLayoutContentVersions(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.layout.content.model.LayoutContentVersion>
			getLayoutContentVersions(long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.getLayoutContentVersions(plid);
	}

	/**
	 * Returns the number of layout content versions.
	 *
	 * @return the number of layout content versions
	 */
	@Override
	public int getLayoutContentVersionsCount() {
		return _layoutContentVersionLocalService.
			getLayoutContentVersionsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutContentVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the layout content version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was updated
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
		updateLayoutContentVersion(
			com.liferay.layout.content.model.LayoutContentVersion
				layoutContentVersion) {

		return _layoutContentVersionLocalService.updateLayoutContentVersion(
			layoutContentVersion);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersion
			updateLayoutContentVersion(
				long layoutContentVersionId,
				java.util.Map<java.util.Locale, String> nameMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionLocalService.updateLayoutContentVersion(
			layoutContentVersionId, nameMap);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutContentVersionLocalService.getBasePersistence();
	}

	@Override
	public LayoutContentVersionLocalService getWrappedService() {
		return _layoutContentVersionLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutContentVersionLocalService layoutContentVersionLocalService) {

		_layoutContentVersionLocalService = layoutContentVersionLocalService;
	}

	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-643647227