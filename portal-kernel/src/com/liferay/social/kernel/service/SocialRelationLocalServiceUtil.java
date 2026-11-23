/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialRelation;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SocialRelation. This utility wraps
 * <code>com.liferay.portlet.social.service.impl.SocialRelationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRelationLocalService
 * @generated
 */
public class SocialRelationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialRelationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a social relation between the two users to the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the type of the relation
	 * @return the social relation
	 */
	public static SocialRelation addRelation(
			long userId1, long userId2, int type)
		throws PortalException {

		return getService().addRelation(userId1, userId2, type);
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
	public static SocialRelation addSocialRelation(
		SocialRelation socialRelation) {

		return getService().addSocialRelation(socialRelation);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new social relation with the primary key. Does not add the social relation to the database.
	 *
	 * @param relationId the primary key for the new social relation
	 * @return the new social relation
	 */
	public static SocialRelation createSocialRelation(long relationId) {
		return getService().createSocialRelation(relationId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relationId the primary key of the relation
	 */
	public static void deleteRelation(long relationId) throws PortalException {
		getService().deleteRelation(relationId);
	}

	/**
	 * Removes the matching relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the relation's type
	 */
	public static void deleteRelation(long userId1, long userId2, int type)
		throws PortalException {

		getService().deleteRelation(userId1, userId2, type);
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relation the relation to be removed
	 */
	public static void deleteRelation(SocialRelation relation)
		throws PortalException {

		getService().deleteRelation(relation);
	}

	/**
	 * Removes all relations involving the user from the database.
	 *
	 * @param userId the primary key of the user
	 */
	public static void deleteRelations(long userId) {
		getService().deleteRelations(userId);
	}

	/**
	 * Removes all relations between User1 and User2.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 */
	public static void deleteRelations(long userId1, long userId2)
		throws PortalException {

		getService().deleteRelations(userId1, userId2);
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
	public static SocialRelation deleteSocialRelation(long relationId)
		throws PortalException {

		return getService().deleteSocialRelation(relationId);
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
	public static SocialRelation deleteSocialRelation(
		SocialRelation socialRelation) {

		return getService().deleteSocialRelation(socialRelation);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SocialRelation fetchSocialRelation(long relationId) {
		return getService().fetchSocialRelation(relationId);
	}

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public static SocialRelation fetchSocialRelationByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchSocialRelationByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	public static List<SocialRelation> getInverseRelations(
		long userId, int type, int start, int end) {

		return getService().getInverseRelations(userId, type, start, end);
	}

	/**
	 * Returns the number of inverse relations of the given type for which the
	 * user is User2 of the relation.
	 *
	 * @param userId the primary key of the user
	 * @param type the relation's type
	 * @return the number of matching relations
	 */
	public static int getInverseRelationsCount(long userId, int type) {
		return getService().getInverseRelationsCount(userId, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the social relation with the primary key.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation
	 * @throws PortalException if a social relation with the primary key could not be found
	 */
	public static SocialRelation getSocialRelation(long relationId)
		throws PortalException {

		return getService().getSocialRelation(relationId);
	}

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation
	 * @throws PortalException if a matching social relation could not be found
	 */
	public static SocialRelation getSocialRelationByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getSocialRelationByUuidAndCompanyId(
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
	public static List<SocialRelation> getSocialRelations(int start, int end) {
		return getService().getSocialRelations(start, end);
	}

	/**
	 * Returns the number of social relations.
	 *
	 * @return the number of social relations
	 */
	public static int getSocialRelationsCount() {
		return getService().getSocialRelationsCount();
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
	public static boolean hasRelation(long userId1, long userId2, int type) {
		return getService().hasRelation(userId1, userId2, type);
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
	public static boolean isRelatable(long userId1, long userId2, int type) {
		return getService().isRelatable(userId1, userId2, type);
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
	public static SocialRelation updateSocialRelation(
		SocialRelation socialRelation) {

		return getService().updateSocialRelation(socialRelation);
	}

	public static SocialRelationLocalService getService() {
		return _service;
	}

	public static void setService(SocialRelationLocalService service) {
		_service = service;
	}

	private static volatile SocialRelationLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1544819988