/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.exception.NoSuchRelationException;
import com.liferay.social.kernel.model.SocialRelation;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the social relation service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialRelationUtil
 * @generated
 */
@ProviderType
public interface SocialRelationPersistence
	extends BasePersistence<SocialRelation>, CTPersistence<SocialRelation> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SocialRelationUtil} to access the social relation persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of social relations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching social relations
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching social relations
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByUserId1(
		long userId1, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByUserId1_First(
			long userId1,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByUserId1_First(
		long userId1,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where userId1 = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 */
	public void removeByUserId1(long userId1);

	/**
	 * Returns the number of social relations where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @return the number of matching social relations
	 */
	public int countByUserId1(long userId1);

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByUserId2(
		long userId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByUserId2_First(
			long userId2,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByUserId2_First(
		long userId2,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where userId2 = &#63; from the database.
	 *
	 * @param userId2 the user id2
	 */
	public void removeByUserId2(long userId2);

	/**
	 * Returns the number of social relations where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @return the number of matching social relations
	 */
	public int countByUserId2(long userId2);

	/**
	 * Returns an ordered range of all the social relations where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByType_First(
			int type,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByType_First(
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public void removeByType(int type);

	/**
	 * Returns the number of social relations where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching social relations
	 */
	public int countByType(int type);

	/**
	 * Returns an ordered range of all the social relations where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByC_T(
		long companyId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByC_T_First(
			long companyId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByC_T_First(
		long companyId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	public void removeByC_T(long companyId, int type);

	/**
	 * Returns the number of social relations where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching social relations
	 */
	public int countByC_T(long companyId, int type);

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByU1_U2(
		long userId1, long userId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByU1_U2_First(
			long userId1, long userId2,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByU1_U2_First(
		long userId1, long userId2,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where userId1 = &#63; and userId2 = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 */
	public void removeByU1_U2(long userId1, long userId2);

	/**
	 * Returns the number of social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @return the number of matching social relations
	 */
	public int countByU1_U2(long userId1, long userId2);

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByU1_T(
		long userId1, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByU1_T_First(
			long userId1, int type,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByU1_T_First(
		long userId1, int type,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where userId1 = &#63; and type = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 */
	public void removeByU1_T(long userId1, int type);

	/**
	 * Returns the number of social relations where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @return the number of matching social relations
	 */
	public int countByU1_T(long userId1, int type);

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social relations
	 */
	public java.util.List<SocialRelation> findByU2_T(
		long userId2, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByU2_T_First(
			long userId2, int type,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
				orderByComparator)
		throws NoSuchRelationException;

	/**
	 * Returns the first social relation in the ordered set where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByU2_T_First(
		long userId2, int type,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator);

	/**
	 * Removes all the social relations where userId2 = &#63; and type = &#63; from the database.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 */
	public void removeByU2_T(long userId2, int type);

	/**
	 * Returns the number of social relations where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the number of matching social relations
	 */
	public int countByU2_T(long userId2, int type);

	/**
	 * Returns the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; or throws a <code>NoSuchRelationException</code> if it could not be found.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the matching social relation
	 * @throws NoSuchRelationException if a matching social relation could not be found
	 */
	public SocialRelation findByU1_U2_T(long userId1, long userId2, int type)
		throws NoSuchRelationException;

	/**
	 * Returns the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public SocialRelation fetchByU1_U2_T(
		long userId1, long userId2, int type, boolean useFinderCache);

	/**
	 * Removes the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; from the database.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the social relation that was removed
	 */
	public SocialRelation removeByU1_U2_T(long userId1, long userId2, int type)
		throws NoSuchRelationException;

	/**
	 * Returns the number of social relations where userId1 = &#63; and userId2 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the number of matching social relations
	 */
	public int countByU1_U2_T(long userId1, long userId2, int type);

	/**
	 * Creates a new social relation with the primary key. Does not add the social relation to the database.
	 *
	 * @param relationId the primary key for the new social relation
	 * @return the new social relation
	 */
	public SocialRelation create(long relationId);

	/**
	 * Removes the social relation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation that was removed
	 * @throws NoSuchRelationException if a social relation with the primary key could not be found
	 */
	public SocialRelation remove(long relationId)
		throws NoSuchRelationException;

	public SocialRelation updateImpl(SocialRelation socialRelation);

	/**
	 * Returns the social relation with the primary key or throws a <code>NoSuchRelationException</code> if it could not be found.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation
	 * @throws NoSuchRelationException if a social relation with the primary key could not be found
	 */
	public SocialRelation findByPrimaryKey(long relationId)
		throws NoSuchRelationException;

	/**
	 * Returns the social relation with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param relationId the primary key of the social relation
	 * @return the social relation, or <code>null</code> if a social relation with the primary key could not be found
	 */
	public SocialRelation fetchByPrimaryKey(long relationId);

	/**
	 * Returns the social relation where userId1 = &#63; and userId2 = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the matching social relation, or <code>null</code> if a matching social relation could not be found
	 */
	public default SocialRelation fetchByU1_U2_T(
		long userId1, long userId2, int type) {

		return fetchByU1_U2_T(userId1, userId2, type, true);
	}

	/**
	 * Returns all the social relations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where userId1 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId1(long userId1) {
		return findByUserId1(
			userId1, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where userId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId1(
		long userId1, int start, int end) {

		return findByUserId1(userId1, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId1(
		long userId1, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByUserId1(userId1, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where userId2 = &#63;.
	 *
	 * @param userId2 the user id2
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId2(long userId2) {
		return findByUserId2(
			userId2, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId2(
		long userId2, int start, int end) {

		return findByUserId2(userId2, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByUserId2(
		long userId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByUserId2(userId2, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByType(int type) {
		return findByType(
			type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByType(
		int type, int start, int end) {

		return findByType(type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByType(type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByC_T(
		long companyId, int type) {

		return findByC_T(
			companyId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByC_T(
		long companyId, int type, int start, int end) {

		return findByC_T(companyId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByC_T(
		long companyId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByC_T(companyId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_U2(
		long userId1, long userId2) {

		return findByU1_U2(
			userId1, userId2,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_U2(
		long userId1, long userId2, int start, int end) {

		return findByU1_U2(userId1, userId2, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and userId2 = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param userId2 the user id2
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_U2(
		long userId1, long userId2, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByU1_U2(
			userId1, userId2, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where userId1 = &#63; and type = &#63;.
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_T(
		long userId1, int type) {

		return findByU1_T(
			userId1, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where userId1 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_T(
		long userId1, int type, int start, int end) {

		return findByU1_T(userId1, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where userId1 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId1 the user id1
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU1_T(
		long userId1, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByU1_T(userId1, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social relations where userId2 = &#63; and type = &#63;.
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @return the matching social relations
	 */
	public default java.util.List<SocialRelation> findByU2_T(
		long userId2, int type) {

		return findByU2_T(
			userId2, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social relations where userId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @return the range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU2_T(
		long userId2, int type, int start, int end) {

		return findByU2_T(userId2, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social relations where userId2 = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRelationModelImpl</code>.
	 * </p>
	 *
	 * @param userId2 the user id2
	 * @param type the type
	 * @param start the lower bound of the range of social relations
	 * @param end the upper bound of the range of social relations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social relations
	 */
	public default java.util.List<SocialRelation> findByU2_T(
		long userId2, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRelation>
			orderByComparator) {

		return findByU2_T(userId2, type, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1749024675