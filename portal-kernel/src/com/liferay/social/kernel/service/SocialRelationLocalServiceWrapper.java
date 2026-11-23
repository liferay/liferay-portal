/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.model.SocialRelation;

/**
 * Provides a wrapper for {@link SocialRelationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRelationLocalService
 * @generated
 */
public class SocialRelationLocalServiceWrapper
	implements ServiceWrapper<SocialRelationLocalService>,
			   SocialRelationLocalService {

	public SocialRelationLocalServiceWrapper() {
		this(null);
	}

	public SocialRelationLocalServiceWrapper(
		SocialRelationLocalService socialRelationLocalService) {

		_socialRelationLocalService = socialRelationLocalService;
	}

	/**
	 * Adds a social relation between the two users to the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the type of the relation
	 * @return the social relation
	 */
	@Override
	public SocialRelation addRelation(long userId1, long userId2, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.addRelation(userId1, userId2, type);
	}

	/**
	 * Adds the social relation to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRelationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRelation the social relation
	 * @return the social relation that was added
	 */
	@Override
	public SocialRelation addSocialRelation(SocialRelation socialRelation) {
		return _socialRelationLocalService.addSocialRelation(socialRelation);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new social relation with the primary key. Does not add the social relation to the database.
	 *
	 * @param relationId the primary key for the new social relation
	 * @return the new social relation
	 */
	@Override
	public SocialRelation createSocialRelation(long relationId) {
		return _socialRelationLocalService.createSocialRelation(relationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relationId the primary key of the relation
	 */
	@Override
	public void deleteRelation(long relationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialRelationLocalService.deleteRelation(relationId);
	}

	/**
	 * Removes the matching relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the relation's type
	 */
	@Override
	public void deleteRelation(long userId1, long userId2, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialRelationLocalService.deleteRelation(userId1, userId2, type);
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relation the relation to be removed
	 */
	@Override
	public void deleteRelation(SocialRelation relation)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialRelationLocalService.deleteRelation(relation);
	}

	/**
	 * Removes all relations involving the user from the database.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteRelations(long userId) {
		_socialRelationLocalService.deleteRelations(userId);
	}

	/**
	 * Removes all relations between User1 and User2.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 */
	@Override
	public void deleteRelations(long userId1, long userId2)
		throws com.liferay.portal.kernel.exception.PortalException {

		_socialRelationLocalService.deleteRelations(userId1, userId2);
	}

	/**
	 * Deletes the social relation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRelationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation that was removed
	 * @throws PortalException if a social relation with the primary key could not be found
	 */
	@Override
	public SocialRelation deleteSocialRelation(long relationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.deleteSocialRelation(relationId);
	}

	/**
	 * Deletes the social relation from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRelationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRelation the social relation
	 * @return the social relation that was removed
	 */
	@Override
	public SocialRelation deleteSocialRelation(SocialRelation socialRelation) {
		return _socialRelationLocalService.deleteSocialRelation(socialRelation);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _socialRelationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _socialRelationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _socialRelationLocalService.dynamicQuery();
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

		return _socialRelationLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
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

		return _socialRelationLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
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

		return _socialRelationLocalService.dynamicQuery(
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

		return _socialRelationLocalService.dynamicQueryCount(dynamicQuery);
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

		return _socialRelationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SocialRelation fetchSocialRelation(long relationId) {
		return _socialRelationLocalService.fetchSocialRelation(relationId);
	}

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Override
	public SocialRelation fetchSocialRelationByUuidAndCompanyId(
		String uuid, long companyId) {

		return _socialRelationLocalService.
			fetchSocialRelationByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _socialRelationLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _socialRelationLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the inverse relations of the given type for which
	 * the user is User2 of the relation.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param type the relation's type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching relations
	 */
	@Override
	public java.util.List<SocialRelation> getInverseRelations(
		long userId, int type, int start, int end) {

		return _socialRelationLocalService.getInverseRelations(
			userId, type, start, end);
	}

	/**
	 * Returns the number of inverse relations of the given type for which the
	 * user is User2 of the relation.
	 *
	 * @param userId the primary key of the user
	 * @param type the relation's type
	 * @return the number of matching relations
	 */
	@Override
	public int getInverseRelationsCount(long userId, int type) {
		return _socialRelationLocalService.getInverseRelationsCount(
			userId, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _socialRelationLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the social relation with the primary key.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation
	 * @throws PortalException if a social relation with the primary key could not be found
	 */
	@Override
	public SocialRelation getSocialRelation(long relationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.getSocialRelation(relationId);
	}

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation
	 * @throws PortalException if a matching social relation could not be found
	 */
	@Override
	public SocialRelation getSocialRelationByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _socialRelationLocalService.getSocialRelationByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the social relations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of social relations
	 */
	@Override
	public java.util.List<SocialRelation> getSocialRelations(
		int start, int end) {

		return _socialRelationLocalService.getSocialRelations(start, end);
	}

	/**
	 * Returns the number of social relations.
	 *
	 * @return the number of social relations
	 */
	@Override
	public int getSocialRelationsCount() {
		return _socialRelationLocalService.getSocialRelationsCount();
	}

	/**
	 * Returns <code>true</code> if a relation of the given type exists where
	 * the user with primary key <code>userId1</code> is User1 of the relation
	 * and the user with the primary key <code>userId2</code> is User2 of the
	 * relation.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the relation's type
	 * @return <code>true</code> if the relation exists; <code>false</code>
	 otherwise
	 */
	@Override
	public boolean hasRelation(long userId1, long userId2, int type) {
		return _socialRelationLocalService.hasRelation(userId1, userId2, type);
	}

	/**
	 * Returns <code>true</code> if the users can be in a relation of the given
	 * type where the user with primary key <code>userId1</code> is User1 of the
	 * relation and the user with the primary key <code>userId2</code> is User2
	 * of the relation.
	 *
	 * <p>
	 * This method returns <code>false</code> if User1 and User2 are the same,
	 * if either user is the default user, or if a matching relation already
	 * exists.
	 * </p>
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the relation's type
	 * @return <code>true</code> if the two users can be in a new relation of
	 the given type; <code>false</code> otherwise
	 */
	@Override
	public boolean isRelatable(long userId1, long userId2, int type) {
		return _socialRelationLocalService.isRelatable(userId1, userId2, type);
	}

	/**
	 * Updates the social relation in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SocialRelationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param socialRelation the social relation
	 * @return the social relation that was updated
	 */
	@Override
	public SocialRelation updateSocialRelation(SocialRelation socialRelation) {
		return _socialRelationLocalService.updateSocialRelation(socialRelation);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _socialRelationLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SocialRelation> getCTPersistence() {
		return _socialRelationLocalService.getCTPersistence();
	}

	@Override
	public Class<SocialRelation> getModelClass() {
		return _socialRelationLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialRelation>, R, E>
				updateUnsafeFunction)
		throws E {

		return _socialRelationLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SocialRelationLocalService getWrappedService() {
		return _socialRelationLocalService;
	}

	@Override
	public void setWrappedService(
		SocialRelationLocalService socialRelationLocalService) {

		_socialRelationLocalService = socialRelationLocalService;
	}

	private SocialRelationLocalService _socialRelationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-867796123