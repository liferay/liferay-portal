/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialRelation;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SocialRelation. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SocialRelationLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.social.kernel.model.SocialRelation"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SocialRelationLocalService
	extends BaseLocalService, CTService<SocialRelation>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.social.service.impl.SocialRelationLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the social relation local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SocialRelationLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a social relation between the two users to the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the type of the relation
	 * @return the social relation
	 */
	public SocialRelation addRelation(long userId1, long userId2, int type)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public SocialRelation addSocialRelation(SocialRelation socialRelation);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new social relation with the primary key. Does not add the social relation to the database.
	 *
	 * @param relationId the primary key for the new social relation
	 * @return the new social relation
	 */
	@Transactional(enabled = false)
	public SocialRelation createSocialRelation(long relationId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relationId the primary key of the relation
	 */
	public void deleteRelation(long relationId) throws PortalException;

	/**
	 * Removes the matching relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 * @param type the relation's type
	 */
	public void deleteRelation(long userId1, long userId2, int type)
		throws PortalException;

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relation the relation to be removed
	 */
	public void deleteRelation(SocialRelation relation) throws PortalException;

	/**
	 * Removes all relations involving the user from the database.
	 *
	 * @param userId the primary key of the user
	 */
	public void deleteRelations(long userId);

	/**
	 * Removes all relations between User1 and User2.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 */
	public void deleteRelations(long userId1, long userId2)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public SocialRelation deleteSocialRelation(long relationId)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public SocialRelation deleteSocialRelation(SocialRelation socialRelation);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRelation fetchSocialRelation(long relationId);

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRelation fetchSocialRelationByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRelation> getInverseRelations(
		long userId, int type, int start, int end);

	/**
	 * Returns the number of inverse relations of the given type for which the
	 * user is User2 of the relation.
	 *
	 * @param userId the primary key of the user
	 * @param type the relation's type
	 * @return the number of matching relations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getInverseRelationsCount(long userId, int type);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the social relation with the primary key.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation
	 * @throws PortalException if a social relation with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRelation getSocialRelation(long relationId)
		throws PortalException;

	/**
	 * Returns the social relation with the matching UUID and company.
	 *
	 * @param uuid the social relation's UUID
	 * @param companyId the primary key of the company
	 * @return the matching social relation
	 * @throws PortalException if a matching social relation could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SocialRelation getSocialRelationByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SocialRelation> getSocialRelations(int start, int end);

	/**
	 * Returns the number of social relations.
	 *
	 * @return the number of social relations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialRelationsCount();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRelation(long userId1, long userId2, int type);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isRelatable(long userId1, long userId2, int type);

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
	@Indexable(type = IndexableType.REINDEX)
	public SocialRelation updateSocialRelation(SocialRelation socialRelation);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SocialRelation> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SocialRelation> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SocialRelation>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2002708065