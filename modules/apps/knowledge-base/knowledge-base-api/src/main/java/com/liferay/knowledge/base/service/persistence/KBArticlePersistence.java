/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence;

import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the kb article service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KBArticleUtil
 * @generated
 */
@ProviderType
public interface KBArticlePersistence
	extends BasePersistence<KBArticle>, CTPersistence<KBArticle> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KBArticleUtil} to access the kb article persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByResourcePrimKey_First(
			long resourcePrimKey,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByResourcePrimKey_First(
		long resourcePrimKey,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByResourcePrimKey_Last(
			long resourcePrimKey,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByResourcePrimKey_Last(
		long resourcePrimKey,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByResourcePrimKey_PrevAndNext(
			long kbArticleId, long resourcePrimKey,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	public void removeByResourcePrimKey(long resourcePrimKey);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching kb articles
	 */
	public int countByResourcePrimKey(long resourcePrimKey);

	/**
	 * Returns all the kb articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid(String uuid);

	/**
	 * Returns a range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where uuid = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByUuid_PrevAndNext(
			long kbArticleId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of kb articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb articles
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException;

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the kb article where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb article that was removed
	 */
	public KBArticle removeByUUID_G(String uuid, long groupId)
		throws NoSuchArticleException;

	/**
	 * Returns the number of kb articles where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid_C(String uuid, long companyId);

	/**
	 * Returns a range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByUuid_C_PrevAndNext(
			long kbArticleId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb articles
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_First(
			long resourcePrimKey, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_First(
		long resourcePrimKey, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_Last(
			long resourcePrimKey, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_Last(
		long resourcePrimKey, long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 */
	public void removeByR_G(long resourcePrimKey, long groupId);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	public int countByR_G(long resourcePrimKey, long groupId);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G(long resourcePrimKey, long groupId);

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_V(long resourcePrimKey, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_V(long resourcePrimKey, int version);

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_V(
		long resourcePrimKey, int version, boolean useFinderCache);

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public KBArticle removeByR_V(long resourcePrimKey, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public int countByR_V(long resourcePrimKey, int version);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_L_First(
			long resourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_L_First(
		long resourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_L_Last(
			long resourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_L_Last(
		long resourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 */
	public void removeByR_L(long resourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByR_L(long resourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByR_L(long[] resourcePrimKeys, boolean latest);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_M_First(
			long resourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_M_First(
		long resourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_M_Last(
			long resourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_M_Last(
		long resourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 */
	public void removeByR_M(long resourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByR_M(long resourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByR_M(long[] resourcePrimKeys, boolean main);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long resourcePrimKey, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_S_First(
			long resourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_S_First(
		long resourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_S_Last(
			long resourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_S_Last(
		long resourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	public void removeByR_S(long resourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_S(long resourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	public int countByR_S(long[] resourcePrimKeys, int[] statuses);

	/**
	 * Returns all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_ERC_First(
			long groupId, String externalReferenceCode,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_ERC_Last(
			long groupId, String externalReferenceCode,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_Last(
		long groupId, String externalReferenceCode,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_ERC_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_ERC_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 */
	public void removeByG_ERC(long groupId, String externalReferenceCode);

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles
	 */
	public int countByG_ERC(long groupId, String externalReferenceCode);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_ERC(long groupId, String externalReferenceCode);

	/**
	 * Returns all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L(long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_L_First(
			long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_L_First(
		long groupId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_L_Last(
			long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_L_Last(
		long groupId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_L_PrevAndNext(
			long kbArticleId, long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L(
		long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L(
		long groupId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L(
		long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_L_PrevAndNext(
			long kbArticleId, long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	public void removeByG_L(long groupId, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_L(long groupId, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_L(long groupId, boolean latest);

	/**
	 * Returns all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M(long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_M_First(
			long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_M_First(
		long groupId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_M_Last(
			long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_M_Last(
		long groupId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_M_PrevAndNext(
			long kbArticleId, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M(
		long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M(
		long groupId, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M(
		long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_M_PrevAndNext(
			long kbArticleId, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 */
	public void removeByG_M(long groupId, boolean main);

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByG_M(long groupId, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_M(long groupId, boolean main);

	/**
	 * Returns all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_S(long groupId, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_S(
		long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_S_First(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_S_First(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_S_Last(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_S_Last(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_S_PrevAndNext(
			long kbArticleId, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_S(long groupId, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_S(
		long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_S_PrevAndNext(
			long kbArticleId, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByG_S(long groupId, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_S(long groupId, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_S(long groupId, int status);

	/**
	 * Returns all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L(long companyId, boolean latest);

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_L_First(
			long companyId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_L_First(
		long companyId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_L_Last(
			long companyId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_L_Last(
		long companyId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByC_L_PrevAndNext(
			long kbArticleId, long companyId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 */
	public void removeByC_L(long companyId, boolean latest);

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByC_L(long companyId, boolean latest);

	/**
	 * Returns all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M(long companyId, boolean main);

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_M_First(
			long companyId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_M_First(
		long companyId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_M_Last(
			long companyId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_M_Last(
		long companyId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByC_M_PrevAndNext(
			long kbArticleId, long companyId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 */
	public void removeByC_M(long companyId, boolean main);

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByC_M(long companyId, boolean main);

	/**
	 * Returns all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByC_S(long companyId, int status);

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_S(
		long companyId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_S(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_S_First(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_S_First(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_S_Last(
			long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_S_Last(
		long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByC_S_PrevAndNext(
			long kbArticleId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public void removeByC_S(long companyId, int status);

	/**
	 * Returns the number of kb articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByC_S(long companyId, int status);

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_L_First(
			long parentResourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_L_First(
		long parentResourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_L_Last(
			long parentResourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_L_Last(
		long parentResourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByP_L_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	public void removeByP_L(long parentResourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByP_L(long parentResourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByP_L(long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_M_First(
			long parentResourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_M_First(
		long parentResourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_M_Last(
			long parentResourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_M_Last(
		long parentResourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByP_M_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	public void removeByP_M(long parentResourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByP_M(long parentResourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByP_M(long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_S_First(
			long parentResourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_S_First(
		long parentResourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_S_Last(
			long parentResourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_S_Last(
		long parentResourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByP_S_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	public void removeByP_S(long parentResourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_S(long parentResourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_S(long[] parentResourcePrimKeys, int status);

	/**
	 * Returns all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByLtD_S(Date displayDate, int status);

	/**
	 * Returns a range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByLtD_S_First(
			Date displayDate, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByLtD_S_First(
		Date displayDate, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByLtD_S_Last(
			Date displayDate, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByLtD_S_Last(
		Date displayDate, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByLtD_S_PrevAndNext(
			long kbArticleId, Date displayDate, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public void removeByLtD_S(Date displayDate, int status);

	/**
	 * Returns the number of kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByLtD_S(Date displayDate, int status);

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_V(
		long resourcePrimKey, long groupId, int version);

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_V(
		long resourcePrimKey, long groupId, int version,
		boolean useFinderCache);

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public KBArticle removeByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public int countByR_G_V(long resourcePrimKey, long groupId, int version);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_L_First(
			long resourcePrimKey, long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_L_First(
		long resourcePrimKey, long groupId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_L_Last(
			long resourcePrimKey, long groupId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_L_Last(
		long resourcePrimKey, long groupId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	public void removeByR_G_L(
		long resourcePrimKey, long groupId, boolean latest);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByR_G_L(long resourcePrimKey, long groupId, boolean latest);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_L(
		long resourcePrimKey, long groupId, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_M_First(
			long resourcePrimKey, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_M_First(
		long resourcePrimKey, long groupId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_M_Last(
			long resourcePrimKey, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_M_Last(
		long resourcePrimKey, long groupId, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 */
	public void removeByR_G_M(long resourcePrimKey, long groupId, boolean main);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByR_G_M(long resourcePrimKey, long groupId, boolean main);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_M(
		long resourcePrimKey, long groupId, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_S_First(
			long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_S_First(
		long resourcePrimKey, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_S_Last(
			long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_S_Last(
		long resourcePrimKey, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByR_G_S(long resourcePrimKey, long groupId, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_S(long resourcePrimKey, long groupId, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_S(long[] resourcePrimKeys, long groupId, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_S(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_S(
		long[] resourcePrimKeys, long groupId, int status);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_NotS_First(
			long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_NotS_First(
		long resourcePrimKey, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_NotS_Last(
			long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_NotS_Last(
		long resourcePrimKey, long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByR_G_NotS(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_NotS(long resourcePrimKey, long groupId, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_NotS(
		long resourcePrimKey, long groupId, int status);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_L_NotS_First(
			long resourcePrimKey, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_L_NotS_First(
		long resourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_L_NotS_Last(
			long resourcePrimKey, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_L_NotS_Last(
		long resourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByR_L_NotS(
		long resourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_L_NotS(
		long resourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_M_NotS_First(
			long resourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_M_NotS_First(
		long resourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_M_NotS_Last(
			long resourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_M_NotS_Last(
		long resourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public void removeByR_M_NotS(
		long resourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_M_NotS(long resourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status);

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, int version);

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, int version,
		boolean useFinderCache);

	/**
	 * Removes the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public KBArticle removeByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws NoSuchArticleException;

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public int countByG_ERC_V(
		long groupId, String externalReferenceCode, int version);

	/**
	 * Returns all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_ERC_S_First(
			long groupId, String externalReferenceCode, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_S_First(
		long groupId, String externalReferenceCode, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_ERC_S_Last(
			long groupId, String externalReferenceCode, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_ERC_S_Last(
		long groupId, String externalReferenceCode, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_ERC_S_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_ERC_S_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 */
	public void removeByG_ERC_S(
		long groupId, String externalReferenceCode, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_ERC_S(
		long groupId, String externalReferenceCode, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_ERC_S(
		long groupId, String externalReferenceCode, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_First(
		long groupId, long parentResourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_Last(
		long groupId, long parentResourcePrimKey, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_L_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_L_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	public void removeByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_First(
			long groupId, long parentResourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_First(
		long groupId, long parentResourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_Last(
			long groupId, long parentResourcePrimKey, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_Last(
		long groupId, long parentResourcePrimKey, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_M_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_M_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	public void removeByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_S_First(
			long groupId, long parentResourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_S_First(
		long groupId, long parentResourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_S_Last(
			long groupId, long parentResourcePrimKey, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_S_Last(
		long groupId, long parentResourcePrimKey, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	public void removeByG_P_S(
		long groupId, long parentResourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_S(
		long groupId, long parentResourcePrimKey, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_S(
		long groupId, long parentResourcePrimKey, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_First(
			long groupId, long kbFolderId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_First(
		long groupId, long kbFolderId, String urlTitle,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_Last(
			long groupId, long kbFolderId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_Last(
		long groupId, long kbFolderId, String urlTitle,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_UT_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_UT_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 */
	public void removeByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_UT(long groupId, long kbFolderId, String urlTitle);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_L_First(
			long groupId, long kbFolderId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_L_First(
		long groupId, long kbFolderId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_L_Last(
			long groupId, long kbFolderId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_L_Last(
		long groupId, long kbFolderId, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_L_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_L_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 */
	public void removeByG_KBFI_L(long groupId, long kbFolderId, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_L(long groupId, long kbFolderId, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_S_First(
			long groupId, long kbFolderId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_S_First(
		long groupId, long kbFolderId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_S_Last(
			long groupId, long kbFolderId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_S_Last(
		long groupId, long kbFolderId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 */
	public void removeByG_KBFI_S(long groupId, long kbFolderId, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_S(long groupId, long kbFolderId, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_S(long groupId, long kbFolderId, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_L_First(
			long groupId, String sections, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_L_First(
		long groupId, String sections, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_L_Last(
			long groupId, String sections, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_L_Last(
		long groupId, String sections, boolean latest,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_LikeS_L_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_LikeS_L_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 */
	public void removeByG_LikeS_L(
		long groupId, String sections, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_L(long groupId, String sections, boolean latest);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_L(
		long groupId, String sections, boolean latest);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_M_First(
			long groupId, String sections, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_M_First(
		long groupId, String sections, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_M_Last(
			long groupId, String sections, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_M_Last(
		long groupId, String sections, boolean main,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_LikeS_M_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_LikeS_M_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 */
	public void removeByG_LikeS_M(long groupId, String sections, boolean main);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_M(long groupId, String sections, boolean main);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_M(
		long groupId, String[] sectionses, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_M(
		long groupId, String sections, boolean main);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_M(
		long groupId, String[] sectionses, boolean main);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_S_First(
			long groupId, String sections, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_S_First(
		long groupId, String sections, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_S_Last(
			long groupId, String sections, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_S_Last(
		long groupId, String sections, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_LikeS_S_PrevAndNext(
			long kbArticleId, long groupId, String sections, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_LikeS_S_PrevAndNext(
			long kbArticleId, long groupId, String sections, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 */
	public void removeByG_LikeS_S(long groupId, String sections, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_S(long groupId, String sections, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_S(long groupId, String[] sectionses, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_S(
		long groupId, String sections, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_S(
		long groupId, String[] sectionses, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_L_NotS_First(
			long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_L_NotS_First(
		long groupId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_L_NotS_Last(
			long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_L_NotS_Last(
		long groupId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByG_L_NotS(long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_L_NotS(long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_L_NotS(long groupId, boolean latest, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_M_NotS_First(
			long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_M_NotS_First(
		long groupId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_M_NotS_Last(
			long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_M_NotS_Last(
		long groupId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	public void removeByG_M_NotS(long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_M_NotS(long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_M_NotS(long groupId, boolean main, int status);

	/**
	 * Returns all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_L_NotS_First(
			long companyId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_L_NotS_First(
		long companyId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_L_NotS_Last(
			long companyId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_L_NotS_Last(
		long companyId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByC_L_NotS_PrevAndNext(
			long kbArticleId, long companyId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByC_L_NotS(long companyId, boolean latest, int status);

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByC_L_NotS(long companyId, boolean latest, int status);

	/**
	 * Returns all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_M_NotS_First(
			long companyId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_M_NotS_First(
		long companyId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByC_M_NotS_Last(
			long companyId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByC_M_NotS_Last(
		long companyId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByC_M_NotS_PrevAndNext(
			long kbArticleId, long companyId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 */
	public void removeByC_M_NotS(long companyId, boolean main, int status);

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByC_M_NotS(long companyId, boolean main, int status);

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_L_NotS_First(
			long parentResourcePrimKey, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_L_NotS_First(
		long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_L_NotS_Last(
			long parentResourcePrimKey, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_L_NotS_Last(
		long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByP_L_NotS_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status);

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_M_NotS_First(
			long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_M_NotS_First(
		long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByP_M_NotS_Last(
			long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByP_M_NotS_Last(
		long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByP_M_NotS_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean main,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public void removeByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_L_NotS_First(
			long resourcePrimKey, long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_L_NotS_First(
		long resourcePrimKey, long groupId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_L_NotS_Last(
			long resourcePrimKey, long groupId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_L_NotS_Last(
		long resourcePrimKey, long groupId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status);

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_M_NotS_First(
			long resourcePrimKey, long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_M_NotS_First(
		long resourcePrimKey, long groupId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByR_G_M_NotS_Last(
			long resourcePrimKey, long groupId, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByR_G_M_NotS_Last(
		long resourcePrimKey, long groupId, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByR_G_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByR_G_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	public void removeByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_S_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_S_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_S_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_S_Last(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_L_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_L_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_NotS_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_NotS_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_L_NotS_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_L_NotS_Last(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_S_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_S_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_S_Last(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_S_Last(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_M_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_M_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public void removeByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_NotS_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_NotS_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_P_M_NotS_Last(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_P_M_NotS_Last(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_P_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_P_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public void removeByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_S_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_S_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_S_Last(
			long groupId, long kbFolderId, String urlTitle, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_S_Last(
		long groupId, long kbFolderId, String urlTitle, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_UT_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_UT_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	public void removeByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_NotS_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_NotS_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_UT_NotS_Last(
			long groupId, long kbFolderId, String urlTitle, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_UT_NotS_Last(
		long groupId, long kbFolderId, String urlTitle, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_UT_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_UT_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	public void removeByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_L_NotS_First(
			long groupId, long kbFolderId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_L_NotS_First(
		long groupId, long kbFolderId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_KBFI_L_NotS_Last(
			long groupId, long kbFolderId, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_KBFI_L_NotS_Last(
		long groupId, long kbFolderId, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_KBFI_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_KBFI_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_L_NotS_First(
			long groupId, String sections, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_L_NotS_First(
		long groupId, String sections, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_L_NotS_Last(
			long groupId, String sections, boolean latest, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_L_NotS_Last(
		long groupId, String sections, boolean latest, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_LikeS_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_LikeS_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 */
	public void removeByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_M_NotS_First(
			long groupId, String sections, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_M_NotS_First(
		long groupId, String sections, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public KBArticle findByG_LikeS_M_NotS_Last(
			long groupId, String sections, boolean main, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public KBArticle fetchByG_LikeS_M_NotS_Last(
		long groupId, String sections, boolean main, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] findByG_LikeS_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle[] filterFindByG_LikeS_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
				orderByComparator)
		throws NoSuchArticleException;

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status);

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public java.util.List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status);

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public java.util.List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 */
	public void removeByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status);

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public int countByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status);

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public int filterCountByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status);

	/**
	 * Caches the kb article in the entity cache if it is enabled.
	 *
	 * @param kbArticle the kb article
	 */
	public void cacheResult(KBArticle kbArticle);

	/**
	 * Caches the kb articles in the entity cache if it is enabled.
	 *
	 * @param kbArticles the kb articles
	 */
	public void cacheResult(java.util.List<KBArticle> kbArticles);

	/**
	 * Creates a new kb article with the primary key. Does not add the kb article to the database.
	 *
	 * @param kbArticleId the primary key for the new kb article
	 * @return the new kb article
	 */
	public KBArticle create(long kbArticleId);

	/**
	 * Removes the kb article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article that was removed
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle remove(long kbArticleId) throws NoSuchArticleException;

	public KBArticle updateImpl(KBArticle kbArticle);

	/**
	 * Returns the kb article with the primary key or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public KBArticle findByPrimaryKey(long kbArticleId)
		throws NoSuchArticleException;

	/**
	 * Returns the kb article with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article, or <code>null</code> if a kb article with the primary key could not be found
	 */
	public KBArticle fetchByPrimaryKey(long kbArticleId);

	/**
	 * Returns all the kb articles.
	 *
	 * @return the kb articles
	 */
	public java.util.List<KBArticle> findAll();

	/**
	 * Returns a range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of kb articles
	 */
	public java.util.List<KBArticle> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of kb articles
	 */
	public java.util.List<KBArticle> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of kb articles
	 */
	public java.util.List<KBArticle> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBArticle>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb articles from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of kb articles.
	 *
	 * @return the number of kb articles
	 */
	public int countAll();

}