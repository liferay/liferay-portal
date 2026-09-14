/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AudiencesEntryGroupRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRelLocalService
 * @generated
 */
public class AudiencesEntryGroupRelLocalServiceWrapper
	implements AudiencesEntryGroupRelLocalService,
			   ServiceWrapper<AudiencesEntryGroupRelLocalService> {

	public AudiencesEntryGroupRelLocalServiceWrapper() {
		this(null);
	}

	public AudiencesEntryGroupRelLocalServiceWrapper(
		AudiencesEntryGroupRelLocalService audiencesEntryGroupRelLocalService) {

		_audiencesEntryGroupRelLocalService =
			audiencesEntryGroupRelLocalService;
	}

	/**
	 * Adds the audiences entry group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was added
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
		addAudiencesEntryGroupRel(
			com.liferay.audiences.model.AudiencesEntryGroupRel
				audiencesEntryGroupRel) {

		return _audiencesEntryGroupRelLocalService.addAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	/**
	 * Creates a new audiences entry group rel with the primary key. Does not add the audiences entry group rel to the database.
	 *
	 * @param audiencesEntryGroupRelId the primary key for the new audiences entry group rel
	 * @return the new audiences entry group rel
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
		createAudiencesEntryGroupRel(long audiencesEntryGroupRelId) {

		return _audiencesEntryGroupRelLocalService.createAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryGroupRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the audiences entry group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
		deleteAudiencesEntryGroupRel(
			com.liferay.audiences.model.AudiencesEntryGroupRel
				audiencesEntryGroupRel) {

		return _audiencesEntryGroupRelLocalService.deleteAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	/**
	 * Deletes the audiences entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 * @throws PortalException if a audiences entry group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
			deleteAudiencesEntryGroupRel(long audiencesEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryGroupRelLocalService.deleteAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _audiencesEntryGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _audiencesEntryGroupRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _audiencesEntryGroupRelLocalService.dynamicQuery();
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

		return _audiencesEntryGroupRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
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

		return _audiencesEntryGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
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

		return _audiencesEntryGroupRelLocalService.dynamicQuery(
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

		return _audiencesEntryGroupRelLocalService.dynamicQueryCount(
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

		return _audiencesEntryGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
		fetchAudiencesEntryGroupRel(long audiencesEntryGroupRelId) {

		return _audiencesEntryGroupRelLocalService.fetchAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _audiencesEntryGroupRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the audiences entry group rel with the primary key.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel
	 * @throws PortalException if a audiences entry group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
			getAudiencesEntryGroupRel(long audiencesEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryGroupRelLocalService.getAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	/**
	 * Returns a range of all the audiences entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of audiences entry group rels
	 */
	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntryGroupRel>
		getAudiencesEntryGroupRels(int start, int end) {

		return _audiencesEntryGroupRelLocalService.getAudiencesEntryGroupRels(
			start, end);
	}

	/**
	 * Returns the number of audiences entry group rels.
	 *
	 * @return the number of audiences entry group rels
	 */
	@Override
	public int getAudiencesEntryGroupRelsCount() {
		return _audiencesEntryGroupRelLocalService.
			getAudiencesEntryGroupRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _audiencesEntryGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _audiencesEntryGroupRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryGroupRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the audiences entry group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was updated
	 */
	@Override
	public com.liferay.audiences.model.AudiencesEntryGroupRel
		updateAudiencesEntryGroupRel(
			com.liferay.audiences.model.AudiencesEntryGroupRel
				audiencesEntryGroupRel) {

		return _audiencesEntryGroupRelLocalService.updateAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _audiencesEntryGroupRelLocalService.getBasePersistence();
	}

	@Override
	public AudiencesEntryGroupRelLocalService getWrappedService() {
		return _audiencesEntryGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		AudiencesEntryGroupRelLocalService audiencesEntryGroupRelLocalService) {

		_audiencesEntryGroupRelLocalService =
			audiencesEntryGroupRelLocalService;
	}

	private AudiencesEntryGroupRelLocalService
		_audiencesEntryGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1089861851