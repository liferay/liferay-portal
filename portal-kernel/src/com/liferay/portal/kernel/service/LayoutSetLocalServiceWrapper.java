/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link LayoutSetLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetLocalService
 * @generated
 */
public class LayoutSetLocalServiceWrapper
	implements LayoutSetLocalService, ServiceWrapper<LayoutSetLocalService> {

	public LayoutSetLocalServiceWrapper() {
		this(null);
	}

	public LayoutSetLocalServiceWrapper(
		LayoutSetLocalService layoutSetLocalService) {

		_layoutSetLocalService = layoutSetLocalService;
	}

	/**
	 * Adds the layout set to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was added
	 */
	@Override
	public LayoutSet addLayoutSet(LayoutSet layoutSet) {
		return _layoutSetLocalService.addLayoutSet(layoutSet);
	}

	@Override
	public LayoutSet addLayoutSet(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.addLayoutSet(groupId, privateLayout);
	}

	/**
	 * Creates a new layout set with the primary key. Does not add the layout set to the database.
	 *
	 * @param layoutSetId the primary key for the new layout set
	 * @return the new layout set
	 */
	@Override
	public LayoutSet createLayoutSet(long layoutSetId) {
		return _layoutSetLocalService.createLayoutSet(layoutSetId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the layout set from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was removed
	 */
	@Override
	public LayoutSet deleteLayoutSet(LayoutSet layoutSet) {
		return _layoutSetLocalService.deleteLayoutSet(layoutSet);
	}

	/**
	 * Deletes the layout set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set that was removed
	 * @throws PortalException if a layout set with the primary key could not be found
	 */
	@Override
	public LayoutSet deleteLayoutSet(long layoutSetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.deleteLayoutSet(layoutSetId);
	}

	@Override
	public void deleteLayoutSet(
			long groupId, boolean privateLayout, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetLocalService.deleteLayoutSet(
			groupId, privateLayout, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutSetLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutSetLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutSetLocalService.dynamicQuery();
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

		return _layoutSetLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
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

		return _layoutSetLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
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

		return _layoutSetLocalService.dynamicQuery(
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

		return _layoutSetLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutSetLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public LayoutSet fetchLayoutSet(long layoutSetId) {
		return _layoutSetLocalService.fetchLayoutSet(layoutSetId);
	}

	@Override
	public LayoutSet fetchLayoutSet(long groupId, boolean privateLayout) {
		return _layoutSetLocalService.fetchLayoutSet(groupId, privateLayout);
	}

	@Override
	public LayoutSet fetchLayoutSet(String virtualHostname) {
		return _layoutSetLocalService.fetchLayoutSet(virtualHostname);
	}

	@Override
	public LayoutSet fetchLayoutSetByLogoId(
		boolean privateLayout, long logoId) {

		return _layoutSetLocalService.fetchLayoutSetByLogoId(
			privateLayout, logoId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutSetLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutSetLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout set with the primary key.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set
	 * @throws PortalException if a layout set with the primary key could not be found
	 */
	@Override
	public LayoutSet getLayoutSet(long layoutSetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.getLayoutSet(layoutSetId);
	}

	@Override
	public LayoutSet getLayoutSet(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.getLayoutSet(groupId, privateLayout);
	}

	@Override
	public LayoutSet getLayoutSet(String virtualHostname)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.getLayoutSet(virtualHostname);
	}

	/**
	 * Returns a range of all the layout sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @return the range of layout sets
	 */
	@Override
	public java.util.List<LayoutSet> getLayoutSets(int start, int end) {
		return _layoutSetLocalService.getLayoutSets(start, end);
	}

	@Override
	public java.util.List<LayoutSet> getLayoutSetsByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		return _layoutSetLocalService.getLayoutSetsByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid);
	}

	/**
	 * Returns the number of layout sets.
	 *
	 * @return the number of layout sets
	 */
	@Override
	public int getLayoutSetsCount() {
		return _layoutSetLocalService.getLayoutSetsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutSetLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public int getPageCount(long groupId, boolean privateLayout) {
		return _layoutSetLocalService.getPageCount(groupId, privateLayout);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public LayoutSet updateFaviconFileEntryId(
			long groupId, boolean privateLayout, long faviconFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateFaviconFileEntryId(
			groupId, privateLayout, faviconFileEntryId);
	}

	/**
	 * Updates the layout set in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was updated
	 */
	@Override
	public LayoutSet updateLayoutSet(LayoutSet layoutSet) {
		return _layoutSetLocalService.updateLayoutSet(layoutSet);
	}

	@Override
	public void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean mergeLayoutSetPrototype,
			boolean privateLayout, boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetLocalService.updateLayoutSetPrototypeLinkEnabled(
			groupId, mergeLayoutSetPrototype, privateLayout,
			layoutSetPrototypeLinkEnabled, layoutSetPrototypeUuid);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, byte[] bytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, bytes);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			java.io.File file)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, file);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, inputStream);
	}

	@Override
	public LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			java.io.InputStream inputStream, boolean cleanUpStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateLogo(
			groupId, privateLayout, hasLogo, inputStream, cleanUpStream);
	}

	@Override
	public LayoutSet updateLookAndFeel(
			long groupId, boolean privateLayout, String themeId,
			String colorSchemeId, String css)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateLookAndFeel(
			groupId, privateLayout, themeId, colorSchemeId, css);
	}

	@Override
	public void updateLookAndFeel(
			long groupId, String themeId, String colorSchemeId, String css)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetLocalService.updateLookAndFeel(
			groupId, themeId, colorSchemeId, css);
	}

	@Override
	public LayoutSet updateSettings(
			long groupId, boolean privateLayout, String settings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateSettings(
			groupId, privateLayout, settings);
	}

	@Override
	public LayoutSet updateVirtualHosts(
			long groupId, boolean privateLayout,
			java.util.TreeMap<String, String> virtualHostnames)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetLocalService.updateVirtualHosts(
			groupId, privateLayout, virtualHostnames);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutSetLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<LayoutSet> getCTPersistence() {
		return _layoutSetLocalService.getCTPersistence();
	}

	@Override
	public Class<LayoutSet> getModelClass() {
		return _layoutSetLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutSet>, R, E> updateUnsafeFunction)
		throws E {

		return _layoutSetLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public LayoutSetLocalService getWrappedService() {
		return _layoutSetLocalService;
	}

	@Override
	public void setWrappedService(LayoutSetLocalService layoutSetLocalService) {
		_layoutSetLocalService = layoutSetLocalService;
	}

	private LayoutSetLocalService _layoutSetLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1596769649