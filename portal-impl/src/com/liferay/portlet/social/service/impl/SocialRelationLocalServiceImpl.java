/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portlet.social.service.base.SocialRelationLocalServiceBaseImpl;
import com.liferay.social.kernel.exception.RelationUserIdException;
import com.liferay.social.kernel.model.SocialRelation;
import com.liferay.social.kernel.model.SocialRelationConstants;

import java.util.List;

/**
 * The social relation local service. This service provides methods to handle
 * unidirectional or bidirectional relations between users.
 *
 * <p>
 * Relations between users can be unidirectional or bidirectional. The type of
 * relation is determined by an integer constant. Some example relations are
 * <i>co-worker, friend, romantic partner, sibling, spouse, child, enemy,
 * follower, parent, subordinate, supervisor</i>.
 * </p>
 *
 * <p>
 * The two users participating in the relation are designated as User1 and
 * User2. In case of unidirectional relations User1 should always be the subject
 * of the relation. You can use the following English sentence to find out which
 * user to use as User1 and which to use as User2:
 * </p>
 *
 * <p>
 * User1 is <i>&lt;relation&gt;</i> of User2 (e.g. User1 is parent of User2;
 * User1 is supervisor of User2)
 * </p>
 *
 * <p>
 * For bidirectional relations, the service automatically generates the inverse
 * relation.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class SocialRelationLocalServiceImpl
	extends SocialRelationLocalServiceBaseImpl {

	/**
	 * Adds a social relation between the two users to the database.
	 *
	 * @param  userId1 the user that is the subject of the relation
	 * @param  userId2 the user at the other end of the relation
	 * @param  type the type of the relation
	 * @return the social relation
	 */
	@Override
	public SocialRelation addRelation(long userId1, long userId2, int type)
		throws PortalException {

		if (userId1 == userId2) {
			throw new RelationUserIdException();
		}

		User user1 = _userPersistence.findByPrimaryKey(userId1);
		User user2 = _userPersistence.findByPrimaryKey(userId2);

		if (user1.getCompanyId() != user2.getCompanyId()) {
			throw new RelationUserIdException();
		}

		SocialRelation relation = socialRelationPersistence.fetchByU1_U2_T(
			userId1, userId2, type);

		if (relation == null) {
			long relationId = counterLocalService.increment();

			relation = socialRelationPersistence.create(relationId);

			relation.setCompanyId(user1.getCompanyId());
			relation.setCreateDate(System.currentTimeMillis());
			relation.setUserId1(userId1);
			relation.setUserId2(userId2);
			relation.setType(type);

			relation = socialRelationPersistence.update(relation);
		}

		if (_isTypeBi(type)) {
			SocialRelation biRelation =
				socialRelationPersistence.fetchByU1_U2_T(
					userId2, userId1, type);

			if (biRelation == null) {
				long biRelationId = counterLocalService.increment();

				biRelation = socialRelationPersistence.create(biRelationId);

				biRelation.setCompanyId(user1.getCompanyId());
				biRelation.setCreateDate(System.currentTimeMillis());
				biRelation.setUserId1(userId2);
				biRelation.setUserId2(userId1);
				biRelation.setType(type);

				socialRelationPersistence.update(biRelation);
			}
		}

		return relation;
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relationId the primary key of the relation
	 */
	@Override
	public void deleteRelation(long relationId) throws PortalException {
		SocialRelation relation = socialRelationPersistence.findByPrimaryKey(
			relationId);

		deleteRelation(relation);
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
		throws PortalException {

		SocialRelation relation = socialRelationPersistence.findByU1_U2_T(
			userId1, userId2, type);

		deleteRelation(relation);
	}

	/**
	 * Removes the relation (and its inverse in case of a bidirectional
	 * relation) from the database.
	 *
	 * @param relation the relation to be removed
	 */
	@Override
	public void deleteRelation(SocialRelation relation) throws PortalException {
		socialRelationPersistence.remove(relation);

		if (_isTypeBi(relation.getType())) {
			SocialRelation biRelation = socialRelationPersistence.findByU1_U2_T(
				relation.getUserId2(), relation.getUserId1(),
				relation.getType());

			socialRelationPersistence.remove(biRelation);
		}
	}

	/**
	 * Removes all relations involving the user from the database.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteRelations(long userId) {
		socialRelationPersistence.removeByUserId1(userId);
		socialRelationPersistence.removeByUserId2(userId);
	}

	/**
	 * Removes all relations between User1 and User2.
	 *
	 * @param userId1 the user that is the subject of the relation
	 * @param userId2 the user at the other end of the relation
	 */
	@Override
	public void deleteRelations(long userId1, long userId2)
		throws PortalException {

		List<SocialRelation> relations = socialRelationPersistence.findByU1_U2(
			userId1, userId2);

		for (SocialRelation relation : relations) {
			deleteRelation(relation);
		}
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
	 * @param  userId the primary key of the user
	 * @param  type the relation's type
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching relations
	 */
	@Override
	public List<SocialRelation> getInverseRelations(
		long userId, int type, int start, int end) {

		return socialRelationPersistence.findByU2_T(userId, type, start, end);
	}

	/**
	 * Returns the number of inverse relations of the given type for which the
	 * user is User2 of the relation.
	 *
	 * @param  userId the primary key of the user
	 * @param  type the relation's type
	 * @return the number of matching relations
	 */
	@Override
	public int getInverseRelationsCount(long userId, int type) {
		return socialRelationPersistence.countByU2_T(userId, type);
	}

	/**
	 * Returns <code>true</code> if a relation of the given type exists where
	 * the user with primary key <code>userId1</code> is User1 of the relation
	 * and the user with the primary key <code>userId2</code> is User2 of the
	 * relation.
	 *
	 * @param  userId1 the user that is the subject of the relation
	 * @param  userId2 the user at the other end of the relation
	 * @param  type the relation's type
	 * @return <code>true</code> if the relation exists; <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean hasRelation(long userId1, long userId2, int type) {
		SocialRelation relation = socialRelationPersistence.fetchByU1_U2_T(
			userId1, userId2, type);

		if (relation == null) {
			return false;
		}

		return true;
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
	 * @param  userId1 the user that is the subject of the relation
	 * @param  userId2 the user at the other end of the relation
	 * @param  type the relation's type
	 * @return <code>true</code> if the two users can be in a new relation of
	 *         the given type; <code>false</code> otherwise
	 */
	@Override
	public boolean isRelatable(long userId1, long userId2, int type) {
		if (userId1 == userId2) {
			return false;
		}

		User user1 = _userPersistence.fetchByPrimaryKey(userId1);

		if ((user1 == null) || user1.isGuestUser()) {
			return false;
		}

		User user2 = _userPersistence.fetchByPrimaryKey(userId2);

		if ((user2 == null) || user2.isGuestUser()) {
			return false;
		}

		return !hasRelation(userId1, userId2, type);
	}

	private boolean _isTypeBi(int type) {
		if ((type == SocialRelationConstants.TYPE_UNI_CHILD) ||
			(type == SocialRelationConstants.TYPE_UNI_ENEMY) ||
			(type == SocialRelationConstants.TYPE_UNI_FOLLOWER) ||
			(type == SocialRelationConstants.TYPE_UNI_PARENT) ||
			(type == SocialRelationConstants.TYPE_UNI_SUBORDINATE) ||
			(type == SocialRelationConstants.TYPE_UNI_SUPERVISOR)) {

			return false;
		}

		return true;
	}

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}