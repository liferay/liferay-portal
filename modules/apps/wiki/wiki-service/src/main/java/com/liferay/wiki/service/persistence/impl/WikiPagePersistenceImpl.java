/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageTable;
import com.liferay.wiki.model.impl.WikiPageImpl;
import com.liferay.wiki.model.impl.WikiPageModelImpl;
import com.liferay.wiki.service.persistence.WikiPagePersistence;
import com.liferay.wiki.service.persistence.WikiPageUtil;
import com.liferay.wiki.service.persistence.impl.constants.WikiPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the wiki page service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = WikiPagePersistence.class)
public class WikiPagePersistenceImpl
	extends BasePersistenceImpl<WikiPage> implements WikiPagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WikiPageUtil</code> to access the wiki page persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WikiPageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByResourcePrimKey;
	private FinderPath _finderPathWithoutPaginationFindByResourcePrimKey;
	private FinderPath _finderPathCountByResourcePrimKey;

	/**
	 * Returns all the wiki pages where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByResourcePrimKey(long resourcePrimKey) {
		return findByResourcePrimKey(
			resourcePrimKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByResourcePrimKey(
		long resourcePrimKey, int start, int end) {

		return findByResourcePrimKey(resourcePrimKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByResourcePrimKey;
					finderArgs = new Object[] {resourcePrimKey};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByResourcePrimKey;
				finderArgs = new Object[] {
					resourcePrimKey, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (resourcePrimKey != wikiPage.getResourcePrimKey()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_RESOURCEPRIMKEY_RESOURCEPRIMKEY_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByResourcePrimKey_First(
			long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByResourcePrimKey_First(
		long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByResourcePrimKey(
			resourcePrimKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByResourcePrimKey_Last(
			long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByResourcePrimKey_Last(
			resourcePrimKey, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByResourcePrimKey_Last(
		long resourcePrimKey, OrderByComparator<WikiPage> orderByComparator) {

		int count = countByResourcePrimKey(resourcePrimKey);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByResourcePrimKey(
			resourcePrimKey, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByResourcePrimKey_PrevAndNext(
			long pageId, long resourcePrimKey,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByResourcePrimKey_PrevAndNext(
				session, wikiPage, resourcePrimKey, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByResourcePrimKey_PrevAndNext(
				session, wikiPage, resourcePrimKey, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByResourcePrimKey_PrevAndNext(
		Session session, WikiPage wikiPage, long resourcePrimKey,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_RESOURCEPRIMKEY_RESOURCEPRIMKEY_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(resourcePrimKey);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	@Override
	public void removeByResourcePrimKey(long resourcePrimKey) {
		for (WikiPage wikiPage :
				findByResourcePrimKey(
					resourcePrimKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByResourcePrimKey(long resourcePrimKey) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByResourcePrimKey;

			Object[] finderArgs = new Object[] {resourcePrimKey};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_RESOURCEPRIMKEY_RESOURCEPRIMKEY_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String
		_FINDER_COLUMN_RESOURCEPRIMKEY_RESOURCEPRIMKEY_2 =
			"wikiPage.resourcePrimKey = ?";

	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the wiki pages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid;
					finderArgs = new Object[] {uuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid;
				finderArgs = new Object[] {uuid, start, end, orderByComparator};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (!uuid.equals(wikiPage.getUuid())) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_First(
			String uuid, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByUuid_First(uuid, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_First(
		String uuid, OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_Last(
			String uuid, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByUuid_Last(uuid, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_Last(
		String uuid, OrderByComparator<WikiPage> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where uuid = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByUuid_PrevAndNext(
			long pageId, String uuid,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		uuid = Objects.toString(uuid, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, wikiPage, uuid, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByUuid_PrevAndNext(
				session, wikiPage, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByUuid_PrevAndNext(
		Session session, WikiPage wikiPage, String uuid,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (WikiPage wikiPage :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"wikiPage.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(wikiPage.uuid IS NULL OR wikiPage.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the wiki page where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUUID_G(String uuid, long groupId)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByUUID_G(uuid, groupId);

		if (wikiPage == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageException(sb.toString());
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the wiki page where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof WikiPage) {
				WikiPage wikiPage = (WikiPage)result;

				if (!Objects.equals(uuid, wikiPage.getUuid()) ||
					(groupId != wikiPage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(groupId);

					List<WikiPage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						WikiPage wikiPage = list.get(0);

						result = wikiPage;

						cacheResult(wikiPage);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (WikiPage)result;
			}
		}
	}

	/**
	 * Removes the wiki page where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageException {

		WikiPage wikiPage = findByUUID_G(uuid, groupId);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		WikiPage wikiPage = fetchByUUID_G(uuid, groupId);

		if (wikiPage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"wikiPage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(wikiPage.uuid IS NULL OR wikiPage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"wikiPage.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid_C;
					finderArgs = new Object[] {uuid, companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid_C;
				finderArgs = new Object[] {
					uuid, companyId, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (!uuid.equals(wikiPage.getUuid()) ||
							(companyId != wikiPage.getCompanyId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByUuid_C_PrevAndNext(
			long pageId, String uuid, long companyId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		uuid = Objects.toString(uuid, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, wikiPage, uuid, companyId, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByUuid_C_PrevAndNext(
				session, wikiPage, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByUuid_C_PrevAndNext(
		Session session, WikiPage wikiPage, String uuid, long companyId,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (WikiPage wikiPage :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"wikiPage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(wikiPage.uuid IS NULL OR wikiPage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"wikiPage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the wiki pages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCompanyId;
					finderArgs = new Object[] {companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCompanyId;
				finderArgs = new Object[] {
					companyId, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (companyId != wikiPage.getCompanyId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByCompanyId_First(
			long companyId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByCompanyId_First(
		long companyId, OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByCompanyId_Last(
			long companyId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByCompanyId_Last(companyId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByCompanyId_Last(
		long companyId, OrderByComparator<WikiPage> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where companyId = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByCompanyId_PrevAndNext(
			long pageId, long companyId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, wikiPage, companyId, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByCompanyId_PrevAndNext(
				session, wikiPage, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByCompanyId_PrevAndNext(
		Session session, WikiPage wikiPage, long companyId,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (WikiPage wikiPage :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"wikiPage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByNodeId;
	private FinderPath _finderPathWithoutPaginationFindByNodeId;
	private FinderPath _finderPathCountByNodeId;

	/**
	 * Returns all the wiki pages where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByNodeId(long nodeId) {
		return findByNodeId(nodeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByNodeId(long nodeId, int start, int end) {
		return findByNodeId(nodeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByNodeId(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByNodeId(nodeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByNodeId(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByNodeId;
					finderArgs = new Object[] {nodeId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByNodeId;
				finderArgs = new Object[] {
					nodeId, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (nodeId != wikiPage.getNodeId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_NODEID_NODEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByNodeId_First(
			long nodeId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByNodeId_First(nodeId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByNodeId_First(
		long nodeId, OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByNodeId(nodeId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByNodeId_Last(
			long nodeId, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByNodeId_Last(nodeId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByNodeId_Last(
		long nodeId, OrderByComparator<WikiPage> orderByComparator) {

		int count = countByNodeId(nodeId);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByNodeId(
			nodeId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByNodeId_PrevAndNext(
			long pageId, long nodeId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByNodeId_PrevAndNext(
				session, wikiPage, nodeId, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByNodeId_PrevAndNext(
				session, wikiPage, nodeId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByNodeId_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_NODEID_NODEID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 */
	@Override
	public void removeByNodeId(long nodeId) {
		for (WikiPage wikiPage :
				findByNodeId(
					nodeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63;.
	 *
	 * @param nodeId the node ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByNodeId(long nodeId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByNodeId;

			Object[] finderArgs = new Object[] {nodeId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_NODEID_NODEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_NODEID_NODEID_2 =
		"wikiPage.nodeId = ?";

	private FinderPath _finderPathWithPaginationFindByFormat;
	private FinderPath _finderPathWithoutPaginationFindByFormat;
	private FinderPath _finderPathCountByFormat;

	/**
	 * Returns all the wiki pages where format = &#63;.
	 *
	 * @param format the format
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByFormat(String format) {
		return findByFormat(format, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where format = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param format the format
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByFormat(String format, int start, int end) {
		return findByFormat(format, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where format = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param format the format
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByFormat(
		String format, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByFormat(format, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where format = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param format the format
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByFormat(
		String format, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			format = Objects.toString(format, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByFormat;
					finderArgs = new Object[] {format};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByFormat;
				finderArgs = new Object[] {
					format, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if (!format.equals(wikiPage.getFormat())) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				boolean bindFormat = false;

				if (format.isEmpty()) {
					sb.append(_FINDER_COLUMN_FORMAT_FORMAT_3);
				}
				else {
					bindFormat = true;

					sb.append(_FINDER_COLUMN_FORMAT_FORMAT_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindFormat) {
						queryPos.add(format);
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByFormat_First(
			String format, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByFormat_First(format, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("format=");
		sb.append(format);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByFormat_First(
		String format, OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByFormat(format, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByFormat_Last(
			String format, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByFormat_Last(format, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("format=");
		sb.append(format);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where format = &#63;.
	 *
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByFormat_Last(
		String format, OrderByComparator<WikiPage> orderByComparator) {

		int count = countByFormat(format);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByFormat(
			format, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where format = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param format the format
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByFormat_PrevAndNext(
			long pageId, String format,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		format = Objects.toString(format, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByFormat_PrevAndNext(
				session, wikiPage, format, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByFormat_PrevAndNext(
				session, wikiPage, format, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByFormat_PrevAndNext(
		Session session, WikiPage wikiPage, String format,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		boolean bindFormat = false;

		if (format.isEmpty()) {
			sb.append(_FINDER_COLUMN_FORMAT_FORMAT_3);
		}
		else {
			bindFormat = true;

			sb.append(_FINDER_COLUMN_FORMAT_FORMAT_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindFormat) {
			queryPos.add(format);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where format = &#63; from the database.
	 *
	 * @param format the format
	 */
	@Override
	public void removeByFormat(String format) {
		for (WikiPage wikiPage :
				findByFormat(
					format, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where format = &#63;.
	 *
	 * @param format the format
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByFormat(String format) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			format = Objects.toString(format, "");

			FinderPath finderPath = _finderPathCountByFormat;

			Object[] finderArgs = new Object[] {format};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				boolean bindFormat = false;

				if (format.isEmpty()) {
					sb.append(_FINDER_COLUMN_FORMAT_FORMAT_3);
				}
				else {
					bindFormat = true;

					sb.append(_FINDER_COLUMN_FORMAT_FORMAT_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindFormat) {
						queryPos.add(format);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_FORMAT_FORMAT_2 =
		"wikiPage.format = ?";

	private static final String _FINDER_COLUMN_FORMAT_FORMAT_3 =
		"(wikiPage.format IS NULL OR wikiPage.format = '')";

	private FinderPath _finderPathWithPaginationFindByR_N;
	private FinderPath _finderPathWithoutPaginationFindByR_N;
	private FinderPath _finderPathCountByR_N;

	/**
	 * Returns all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N(long resourcePrimKey, long nodeId) {
		return findByR_N(
			resourcePrimKey, nodeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N(
		long resourcePrimKey, long nodeId, int start, int end) {

		return findByR_N(resourcePrimKey, nodeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N(
		long resourcePrimKey, long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByR_N(
			resourcePrimKey, nodeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N(
		long resourcePrimKey, long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_N;
					finderArgs = new Object[] {resourcePrimKey, nodeId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_N;
				finderArgs = new Object[] {
					resourcePrimKey, nodeId, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((resourcePrimKey !=
								wikiPage.getResourcePrimKey()) ||
							(nodeId != wikiPage.getNodeId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_NODEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_First(
			long resourcePrimKey, long nodeId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_First(
			resourcePrimKey, nodeId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_First(
		long resourcePrimKey, long nodeId,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByR_N(
			resourcePrimKey, nodeId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_Last(
			long resourcePrimKey, long nodeId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_Last(
			resourcePrimKey, nodeId, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_Last(
		long resourcePrimKey, long nodeId,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByR_N(resourcePrimKey, nodeId);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByR_N(
			resourcePrimKey, nodeId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByR_N_PrevAndNext(
			long pageId, long resourcePrimKey, long nodeId,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByR_N_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByR_N_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByR_N_PrevAndNext(
		Session session, WikiPage wikiPage, long resourcePrimKey, long nodeId,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_R_N_RESOURCEPRIMKEY_2);

		sb.append(_FINDER_COLUMN_R_N_NODEID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(resourcePrimKey);

		queryPos.add(nodeId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 */
	@Override
	public void removeByR_N(long resourcePrimKey, long nodeId) {
		for (WikiPage wikiPage :
				findByR_N(
					resourcePrimKey, nodeId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N(long resourcePrimKey, long nodeId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByR_N;

			Object[] finderArgs = new Object[] {resourcePrimKey, nodeId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_NODEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_R_N_RESOURCEPRIMKEY_2 =
		"wikiPage.resourcePrimKey = ? AND ";

	private static final String _FINDER_COLUMN_R_N_NODEID_2 =
		"wikiPage.nodeId = ?";

	private FinderPath _finderPathWithPaginationFindByR_S;
	private FinderPath _finderPathWithoutPaginationFindByR_S;
	private FinderPath _finderPathCountByR_S;

	/**
	 * Returns all the wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_S(long resourcePrimKey, int status) {
		return findByR_S(
			resourcePrimKey, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_S(
		long resourcePrimKey, int status, int start, int end) {

		return findByR_S(resourcePrimKey, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByR_S(
			resourcePrimKey, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_S;
					finderArgs = new Object[] {resourcePrimKey, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_S;
				finderArgs = new Object[] {
					resourcePrimKey, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((resourcePrimKey !=
								wikiPage.getResourcePrimKey()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_S_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_S_First(
			long resourcePrimKey, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_S_First(
			resourcePrimKey, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_S_First(
		long resourcePrimKey, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByR_S(
			resourcePrimKey, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_S_Last(
			long resourcePrimKey, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_S_Last(
			resourcePrimKey, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_S_Last(
		long resourcePrimKey, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByR_S(resourcePrimKey, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByR_S(
			resourcePrimKey, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByR_S_PrevAndNext(
			long pageId, long resourcePrimKey, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByR_S_PrevAndNext(
				session, wikiPage, resourcePrimKey, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByR_S_PrevAndNext(
				session, wikiPage, resourcePrimKey, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByR_S_PrevAndNext(
		Session session, WikiPage wikiPage, long resourcePrimKey, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_R_S_RESOURCEPRIMKEY_2);

		sb.append(_FINDER_COLUMN_R_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(resourcePrimKey);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long resourcePrimKey, int status) {
		for (WikiPage wikiPage :
				findByR_S(
					resourcePrimKey, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_S(long resourcePrimKey, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByR_S;

			Object[] finderArgs = new Object[] {resourcePrimKey, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_S_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_R_S_RESOURCEPRIMKEY_2 =
		"wikiPage.resourcePrimKey = ? AND ";

	private static final String _FINDER_COLUMN_R_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_ERC;
	private FinderPath _finderPathWithoutPaginationFindByG_ERC;
	private FinderPath _finderPathCountByG_ERC;

	/**
	 * Returns all the wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_ERC(
		long groupId, String externalReferenceCode) {

		return findByG_ERC(
			groupId, externalReferenceCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return findByG_ERC(groupId, externalReferenceCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_ERC;
					finderArgs = new Object[] {groupId, externalReferenceCode};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_ERC;
				finderArgs = new Object[] {
					groupId, externalReferenceCode, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							!externalReferenceCode.equals(
								wikiPage.getExternalReferenceCode())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_ERC(
			groupId, externalReferenceCode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_ERC_Last(
			long groupId, String externalReferenceCode,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_ERC_Last(
			groupId, externalReferenceCode, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_Last(
		long groupId, String externalReferenceCode,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_ERC(groupId, externalReferenceCode);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_ERC(
			groupId, externalReferenceCode, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_ERC_PrevAndNext(
			long pageId, long groupId, String externalReferenceCode,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_ERC_PrevAndNext(
				session, wikiPage, groupId, externalReferenceCode,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByG_ERC_PrevAndNext(
				session, wikiPage, groupId, externalReferenceCode,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_ERC_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId,
		String externalReferenceCode,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

		boolean bindExternalReferenceCode = false;

		if (externalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
		}
		else {
			bindExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindExternalReferenceCode) {
			queryPos.add(externalReferenceCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_ERC(
		long groupId, String externalReferenceCode) {

		return filterFindByG_ERC(
			groupId, externalReferenceCode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return filterFindByG_ERC(
			groupId, externalReferenceCode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_ERC(
				groupId, externalReferenceCode, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_ERC(
					groupId, externalReferenceCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

		boolean bindExternalReferenceCode = false;

		if (externalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
		}
		else {
			bindExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindExternalReferenceCode) {
				queryPos.add(externalReferenceCode);
			}

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_ERC_PrevAndNext(
			long pageId, long groupId, String externalReferenceCode,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_ERC_PrevAndNext(
				pageId, groupId, externalReferenceCode, orderByComparator);
		}

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_ERC_PrevAndNext(
				session, wikiPage, groupId, externalReferenceCode,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = filterGetByG_ERC_PrevAndNext(
				session, wikiPage, groupId, externalReferenceCode,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_ERC_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId,
		String externalReferenceCode,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

		boolean bindExternalReferenceCode = false;

		if (externalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
		}
		else {
			bindExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindExternalReferenceCode) {
			queryPos.add(externalReferenceCode);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 */
	@Override
	public void removeByG_ERC(long groupId, String externalReferenceCode) {
		for (WikiPage wikiPage :
				findByG_ERC(
					groupId, externalReferenceCode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_ERC(long groupId, String externalReferenceCode) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			FinderPath finderPath = _finderPathCountByG_ERC;

			Object[] finderArgs = new Object[] {groupId, externalReferenceCode};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_ERC(long groupId, String externalReferenceCode) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_ERC(groupId, externalReferenceCode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_ERC(
				groupId, externalReferenceCode);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_ERC_GROUPID_2);

		boolean bindExternalReferenceCode = false;

		if (externalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3);
		}
		else {
			bindExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindExternalReferenceCode) {
				queryPos.add(externalReferenceCode);
			}

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_ERC_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_2 =
		"wikiPage.externalReferenceCode = ?";

	private static final String _FINDER_COLUMN_G_ERC_EXTERNALREFERENCECODE_3 =
		"(wikiPage.externalReferenceCode IS NULL OR wikiPage.externalReferenceCode = '')";

	private FinderPath _finderPathWithPaginationFindByN_T;
	private FinderPath _finderPathWithoutPaginationFindByN_T;
	private FinderPath _finderPathCountByN_T;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T(long nodeId, String title) {
		return findByN_T(
			nodeId, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T(
		long nodeId, String title, int start, int end) {

		return findByN_T(nodeId, title, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_T(nodeId, title, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_T;
					finderArgs = new Object[] {nodeId, title};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_T;
				finderArgs = new Object[] {
					nodeId, title, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							!title.equals(wikiPage.getTitle())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_TITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_First(
			long nodeId, String title,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_First(nodeId, title, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_First(
		long nodeId, String title,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_T(nodeId, title, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_Last(
			long nodeId, String title,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_Last(nodeId, title, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_Last(
		long nodeId, String title,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_T(nodeId, title);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_T(
			nodeId, title, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and title = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_T_PrevAndNext(
			long pageId, long nodeId, String title,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		title = Objects.toString(title, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_T_PrevAndNext(
				session, wikiPage, nodeId, title, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_T_PrevAndNext(
				session, wikiPage, nodeId, title, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_T_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, String title,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_T_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_T_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_N_T_TITLE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (bindTitle) {
			queryPos.add(StringUtil.toLowerCase(title));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 */
	@Override
	public void removeByN_T(long nodeId, String title) {
		for (WikiPage wikiPage :
				findByN_T(
					nodeId, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T(long nodeId, String title) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = _finderPathCountByN_T;

			Object[] finderArgs = new Object[] {nodeId, title};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_TITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_T_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_T_TITLE_2 =
		"lower(wikiPage.title) = ?";

	private static final String _FINDER_COLUMN_N_T_TITLE_3 =
		"(wikiPage.title IS NULL OR wikiPage.title = '')";

	private FinderPath _finderPathWithPaginationFindByN_H;
	private FinderPath _finderPathWithoutPaginationFindByN_H;
	private FinderPath _finderPathCountByN_H;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H(long nodeId, boolean head) {
		return findByN_H(
			nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H(
		long nodeId, boolean head, int start, int end) {

		return findByN_H(nodeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H(nodeId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H;
					finderArgs = new Object[] {nodeId, head};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H;
				finderArgs = new Object[] {
					nodeId, head, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_HEAD_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_First(
			long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_First(nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_First(
		long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H(nodeId, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_Last(
			long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_Last(nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_Last(
		long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H(nodeId, head);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H(
			nodeId, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_PrevAndNext(
			long pageId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_PrevAndNext(
				session, wikiPage, nodeId, head, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_PrevAndNext(
				session, wikiPage, nodeId, head, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByN_H(long nodeId, boolean head) {
		for (WikiPage wikiPage :
				findByN_H(
					nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H(long nodeId, boolean head) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByN_H;

			Object[] finderArgs = new Object[] {nodeId, head};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_HEAD_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_HEAD_2 = "wikiPage.head = ?";

	private FinderPath _finderPathWithPaginationFindByN_P;
	private FinderPath _finderPathWithoutPaginationFindByN_P;
	private FinderPath _finderPathCountByN_P;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_P(long nodeId, String parentTitle) {
		return findByN_P(
			nodeId, parentTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_P(
		long nodeId, String parentTitle, int start, int end) {

		return findByN_P(nodeId, parentTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_P(
		long nodeId, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_P(
			nodeId, parentTitle, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_P(
		long nodeId, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_P;
					finderArgs = new Object[] {nodeId, parentTitle};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_P;
				finderArgs = new Object[] {
					nodeId, parentTitle, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							!parentTitle.equals(wikiPage.getParentTitle())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_P_NODEID_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_P_First(
			long nodeId, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_P_First(
			nodeId, parentTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_P_First(
		long nodeId, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_P(
			nodeId, parentTitle, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_P_Last(
			long nodeId, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_P_Last(
			nodeId, parentTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_P_Last(
		long nodeId, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_P(nodeId, parentTitle);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_P(
			nodeId, parentTitle, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_P_PrevAndNext(
			long pageId, long nodeId, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_P_PrevAndNext(
				session, wikiPage, nodeId, parentTitle, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_P_PrevAndNext(
				session, wikiPage, nodeId, parentTitle, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_P_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_P_NODEID_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and parentTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 */
	@Override
	public void removeByN_P(long nodeId, String parentTitle) {
		for (WikiPage wikiPage :
				findByN_P(
					nodeId, parentTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param parentTitle the parent title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_P(long nodeId, String parentTitle) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = _finderPathCountByN_P;

			Object[] finderArgs = new Object[] {nodeId, parentTitle};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_P_NODEID_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_P_PARENTTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_P_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_P_PARENTTITLE_2 =
		"lower(wikiPage.parentTitle) = ?";

	private static final String _FINDER_COLUMN_N_P_PARENTTITLE_3 =
		"(wikiPage.parentTitle IS NULL OR wikiPage.parentTitle = '')";

	private FinderPath _finderPathWithPaginationFindByN_R;
	private FinderPath _finderPathWithoutPaginationFindByN_R;
	private FinderPath _finderPathCountByN_R;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_R(long nodeId, String redirectTitle) {
		return findByN_R(
			nodeId, redirectTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_R(
		long nodeId, String redirectTitle, int start, int end) {

		return findByN_R(nodeId, redirectTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_R(
		long nodeId, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_R(
			nodeId, redirectTitle, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_R(
		long nodeId, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_R;
					finderArgs = new Object[] {nodeId, redirectTitle};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_R;
				finderArgs = new Object[] {
					nodeId, redirectTitle, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							!redirectTitle.equals(
								wikiPage.getRedirectTitle())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_R_NODEID_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_R_First(
			long nodeId, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_R_First(
			nodeId, redirectTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_R_First(
		long nodeId, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_R(
			nodeId, redirectTitle, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_R_Last(
			long nodeId, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_R_Last(
			nodeId, redirectTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_R_Last(
		long nodeId, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_R(nodeId, redirectTitle);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_R(
			nodeId, redirectTitle, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_R_PrevAndNext(
			long pageId, long nodeId, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		redirectTitle = Objects.toString(redirectTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_R_PrevAndNext(
				session, wikiPage, nodeId, redirectTitle, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_R_PrevAndNext(
				session, wikiPage, nodeId, redirectTitle, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_R_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_R_NODEID_2);

		boolean bindRedirectTitle = false;

		if (redirectTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_3);
		}
		else {
			bindRedirectTitle = true;

			sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (bindRedirectTitle) {
			queryPos.add(StringUtil.toLowerCase(redirectTitle));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and redirectTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 */
	@Override
	public void removeByN_R(long nodeId, String redirectTitle) {
		for (WikiPage wikiPage :
				findByN_R(
					nodeId, redirectTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param redirectTitle the redirect title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_R(long nodeId, String redirectTitle) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = _finderPathCountByN_R;

			Object[] finderArgs = new Object[] {nodeId, redirectTitle};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_R_NODEID_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_R_REDIRECTTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_R_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_R_REDIRECTTITLE_2 =
		"lower(wikiPage.redirectTitle) = ?";

	private static final String _FINDER_COLUMN_N_R_REDIRECTTITLE_3 =
		"(wikiPage.redirectTitle IS NULL OR wikiPage.redirectTitle = '')";

	private FinderPath _finderPathWithPaginationFindByN_S;
	private FinderPath _finderPathWithoutPaginationFindByN_S;
	private FinderPath _finderPathCountByN_S;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_S(long nodeId, int status) {
		return findByN_S(
			nodeId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_S(
		long nodeId, int status, int start, int end) {

		return findByN_S(nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_S(
		long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_S(nodeId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_S(
		long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_S;
					finderArgs = new Object[] {nodeId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_S;
				finderArgs = new Object[] {
					nodeId, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_S_First(
			long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_S_First(nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_S_First(
		long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_S(
			nodeId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_S_Last(
			long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_S_Last(nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_S_Last(
		long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_S(nodeId, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_S(
			nodeId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_S_PrevAndNext(
			long pageId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_S_PrevAndNext(
				session, wikiPage, nodeId, status, orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_S_PrevAndNext(
				session, wikiPage, nodeId, status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_S_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_N_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByN_S(long nodeId, int status) {
		for (WikiPage wikiPage :
				findByN_S(
					nodeId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_S(long nodeId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByN_S;

			Object[] finderArgs = new Object[] {nodeId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathFetchByR_N_V;

	/**
	 * Returns the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_V(
			long resourcePrimKey, long nodeId, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_V(resourcePrimKey, nodeId, version);

		if (wikiPage == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("resourcePrimKey=");
			sb.append(resourcePrimKey);

			sb.append(", nodeId=");
			sb.append(nodeId);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageException(sb.toString());
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_V(
		long resourcePrimKey, long nodeId, double version) {

		return fetchByR_N_V(resourcePrimKey, nodeId, version, true);
	}

	/**
	 * Returns the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_V(
		long resourcePrimKey, long nodeId, double version,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {resourcePrimKey, nodeId, version};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByR_N_V, finderArgs, this);
			}

			if (result instanceof WikiPage) {
				WikiPage wikiPage = (WikiPage)result;

				if ((resourcePrimKey != wikiPage.getResourcePrimKey()) ||
					(nodeId != wikiPage.getNodeId()) ||
					(version != wikiPage.getVersion())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_V_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_V_NODEID_2);

				sb.append(_FINDER_COLUMN_R_N_V_VERSION_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					queryPos.add(version);

					List<WikiPage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByR_N_V, finderArgs, list);
						}
					}
					else {
						WikiPage wikiPage = list.get(0);

						result = wikiPage;

						cacheResult(wikiPage);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (WikiPage)result;
			}
		}
	}

	/**
	 * Removes the wiki page where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByR_N_V(
			long resourcePrimKey, long nodeId, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByR_N_V(resourcePrimKey, nodeId, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_V(long resourcePrimKey, long nodeId, double version) {
		WikiPage wikiPage = fetchByR_N_V(resourcePrimKey, nodeId, version);

		if (wikiPage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_R_N_V_RESOURCEPRIMKEY_2 =
		"wikiPage.resourcePrimKey = ? AND ";

	private static final String _FINDER_COLUMN_R_N_V_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_R_N_V_VERSION_2 =
		"wikiPage.version = ?";

	private FinderPath _finderPathWithPaginationFindByR_N_H;
	private FinderPath _finderPathWithoutPaginationFindByR_N_H;
	private FinderPath _finderPathCountByR_N_H;

	/**
	 * Returns all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_H(
		long resourcePrimKey, long nodeId, boolean head) {

		return findByR_N_H(
			resourcePrimKey, nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_H(
		long resourcePrimKey, long nodeId, boolean head, int start, int end) {

		return findByR_N_H(resourcePrimKey, nodeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_H(
		long resourcePrimKey, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByR_N_H(
			resourcePrimKey, nodeId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_H(
		long resourcePrimKey, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_N_H;
					finderArgs = new Object[] {resourcePrimKey, nodeId, head};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_N_H;
				finderArgs = new Object[] {
					resourcePrimKey, nodeId, head, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((resourcePrimKey !=
								wikiPage.getResourcePrimKey()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_H_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_R_N_H_HEAD_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					queryPos.add(head);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_H_First(
			long resourcePrimKey, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_H_First(
			resourcePrimKey, nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_H_First(
		long resourcePrimKey, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByR_N_H(
			resourcePrimKey, nodeId, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_H_Last(
			long resourcePrimKey, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_H_Last(
			resourcePrimKey, nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_H_Last(
		long resourcePrimKey, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByR_N_H(resourcePrimKey, nodeId, head);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByR_N_H(
			resourcePrimKey, nodeId, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByR_N_H_PrevAndNext(
			long pageId, long resourcePrimKey, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByR_N_H_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, head,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByR_N_H_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, head,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByR_N_H_PrevAndNext(
		Session session, WikiPage wikiPage, long resourcePrimKey, long nodeId,
		boolean head, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_R_N_H_RESOURCEPRIMKEY_2);

		sb.append(_FINDER_COLUMN_R_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_R_N_H_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(resourcePrimKey);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByR_N_H(long resourcePrimKey, long nodeId, boolean head) {
		for (WikiPage wikiPage :
				findByR_N_H(
					resourcePrimKey, nodeId, head, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_H(long resourcePrimKey, long nodeId, boolean head) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByR_N_H;

			Object[] finderArgs = new Object[] {resourcePrimKey, nodeId, head};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_H_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_R_N_H_HEAD_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					queryPos.add(head);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_R_N_H_RESOURCEPRIMKEY_2 =
		"wikiPage.resourcePrimKey = ? AND ";

	private static final String _FINDER_COLUMN_R_N_H_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_R_N_H_HEAD_2 =
		"wikiPage.head = ?";

	private FinderPath _finderPathWithPaginationFindByR_N_S;
	private FinderPath _finderPathWithoutPaginationFindByR_N_S;
	private FinderPath _finderPathCountByR_N_S;

	/**
	 * Returns all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_S(
		long resourcePrimKey, long nodeId, int status) {

		return findByR_N_S(
			resourcePrimKey, nodeId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_S(
		long resourcePrimKey, long nodeId, int status, int start, int end) {

		return findByR_N_S(resourcePrimKey, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_S(
		long resourcePrimKey, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByR_N_S(
			resourcePrimKey, nodeId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByR_N_S(
		long resourcePrimKey, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByR_N_S;
					finderArgs = new Object[] {resourcePrimKey, nodeId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByR_N_S;
				finderArgs = new Object[] {
					resourcePrimKey, nodeId, status, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((resourcePrimKey !=
								wikiPage.getResourcePrimKey()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_S_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_R_N_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_S_First(
			long resourcePrimKey, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_S_First(
			resourcePrimKey, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_S_First(
		long resourcePrimKey, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByR_N_S(
			resourcePrimKey, nodeId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByR_N_S_Last(
			long resourcePrimKey, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByR_N_S_Last(
			resourcePrimKey, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("resourcePrimKey=");
		sb.append(resourcePrimKey);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByR_N_S_Last(
		long resourcePrimKey, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByR_N_S(resourcePrimKey, nodeId, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByR_N_S(
			resourcePrimKey, nodeId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByR_N_S_PrevAndNext(
			long pageId, long resourcePrimKey, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByR_N_S_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByR_N_S_PrevAndNext(
				session, wikiPage, resourcePrimKey, nodeId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByR_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long resourcePrimKey, long nodeId,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_R_N_S_RESOURCEPRIMKEY_2);

		sb.append(_FINDER_COLUMN_R_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_R_N_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(resourcePrimKey);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByR_N_S(long resourcePrimKey, long nodeId, int status) {
		for (WikiPage wikiPage :
				findByR_N_S(
					resourcePrimKey, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where resourcePrimKey = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByR_N_S(long resourcePrimKey, long nodeId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByR_N_S;

			Object[] finderArgs = new Object[] {
				resourcePrimKey, nodeId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_R_N_S_RESOURCEPRIMKEY_2);

				sb.append(_FINDER_COLUMN_R_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_R_N_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(resourcePrimKey);

					queryPos.add(nodeId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_R_N_S_RESOURCEPRIMKEY_2 =
		"wikiPage.resourcePrimKey = ? AND ";

	private static final String _FINDER_COLUMN_R_N_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_R_N_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathFetchByG_ERC_V;

	/**
	 * Returns the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_ERC_V(
			groupId, externalReferenceCode, version);

		if (wikiPage == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageException(sb.toString());
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		return fetchByG_ERC_V(groupId, externalReferenceCode, version, true);
	}

	/**
	 * Returns the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, externalReferenceCode, version
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_ERC_V, finderArgs, this);
			}

			if (result instanceof WikiPage) {
				WikiPage wikiPage = (WikiPage)result;

				if ((groupId != wikiPage.getGroupId()) ||
					!Objects.equals(
						externalReferenceCode,
						wikiPage.getExternalReferenceCode()) ||
					(version != wikiPage.getVersion())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_ERC_V_GROUPID_2);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_ERC_V_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_G_ERC_V_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_G_ERC_V_VERSION_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(version);

					List<WikiPage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_ERC_V, finderArgs, list);
						}
					}
					else {
						WikiPage wikiPage = list.get(0);

						result = wikiPage;

						cacheResult(wikiPage);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (WikiPage)result;
			}
		}
	}

	/**
	 * Removes the wiki page where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByG_ERC_V(
			groupId, externalReferenceCode, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		WikiPage wikiPage = fetchByG_ERC_V(
			groupId, externalReferenceCode, version);

		if (wikiPage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_ERC_V_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_ERC_V_EXTERNALREFERENCECODE_2 =
		"wikiPage.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_G_ERC_V_EXTERNALREFERENCECODE_3 =
		"(wikiPage.externalReferenceCode IS NULL OR wikiPage.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_G_ERC_V_VERSION_2 =
		"wikiPage.version = ?";

	private FinderPath _finderPathWithPaginationFindByG_N_H;
	private FinderPath _finderPathWithoutPaginationFindByG_N_H;
	private FinderPath _finderPathCountByG_N_H;

	/**
	 * Returns all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H(long groupId, long nodeId, boolean head) {
		return findByG_N_H(
			groupId, nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end) {

		return findByG_N_H(groupId, nodeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByG_N_H(
			groupId, nodeId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_H;
					finderArgs = new Object[] {groupId, nodeId, head};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_H;
				finderArgs = new Object[] {
					groupId, nodeId, head, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_First(
			long groupId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_First(
			groupId, nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_First(
		long groupId, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_N_H(
			groupId, nodeId, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_Last(
			long groupId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_Last(
			groupId, nodeId, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_Last(
		long groupId, long nodeId, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_N_H(groupId, nodeId, head);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_N_H(
			groupId, nodeId, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_N_H_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_N_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByG_N_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_N_H_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H(
		long groupId, long nodeId, boolean head) {

		return filterFindByG_N_H(
			groupId, nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end) {

		return filterFindByG_N_H(groupId, nodeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H(
		long groupId, long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H(
				groupId, nodeId, head, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_H(
					groupId, nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_N_H_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H_PrevAndNext(
				pageId, groupId, nodeId, head, orderByComparator);
		}

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_N_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = filterGetByG_N_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_N_H_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 */
	@Override
	public void removeByG_N_H(long groupId, long nodeId, boolean head) {
		for (WikiPage wikiPage :
				findByG_N_H(
					groupId, nodeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H(long groupId, long nodeId, boolean head) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByG_N_H;

			Object[] finderArgs = new Object[] {groupId, nodeId, head};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H(long groupId, long nodeId, boolean head) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_H(groupId, nodeId, head);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_N_H(groupId, nodeId, head);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_HEAD_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_N_H_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_HEAD_2 =
		"wikiPage.head = ?";

	private FinderPath _finderPathWithPaginationFindByG_N_S;
	private FinderPath _finderPathWithoutPaginationFindByG_N_S;
	private FinderPath _finderPathCountByG_N_S;

	/**
	 * Returns all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_S(long groupId, long nodeId, int status) {
		return findByG_N_S(
			groupId, nodeId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_S(
		long groupId, long nodeId, int status, int start, int end) {

		return findByG_N_S(groupId, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_S(
		long groupId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByG_N_S(
			groupId, nodeId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_S(
		long groupId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_S;
					finderArgs = new Object[] {groupId, nodeId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_S;
				finderArgs = new Object[] {
					groupId, nodeId, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_S_First(
			long groupId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_S_First(
			groupId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_S_First(
		long groupId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_N_S(
			groupId, nodeId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_S_Last(
			long groupId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_S_Last(
			groupId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_S_Last(
		long groupId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_N_S(groupId, nodeId, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_N_S(
			groupId, nodeId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_N_S_PrevAndNext(
			long pageId, long groupId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_N_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByG_N_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_S(
		long groupId, long nodeId, int status) {

		return filterFindByG_N_S(
			groupId, nodeId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_S(
		long groupId, long nodeId, int status, int start, int end) {

		return filterFindByG_N_S(groupId, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_S(
		long groupId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_S(
				groupId, nodeId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_S(
					groupId, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(status);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_N_S_PrevAndNext(
			long pageId, long groupId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_S_PrevAndNext(
				pageId, groupId, nodeId, status, orderByComparator);
		}

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_N_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = filterGetByG_N_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByG_N_S(long groupId, long nodeId, int status) {
		for (WikiPage wikiPage :
				findByG_N_S(
					groupId, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_S(long groupId, long nodeId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByG_N_S;

			Object[] finderArgs = new Object[] {groupId, nodeId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_S(long groupId, long nodeId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_S(groupId, nodeId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_N_S(groupId, nodeId, status);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_N_S_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByU_N_S;
	private FinderPath _finderPathWithoutPaginationFindByU_N_S;
	private FinderPath _finderPathCountByU_N_S;

	/**
	 * Returns all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByU_N_S(long userId, long nodeId, int status) {
		return findByU_N_S(
			userId, nodeId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByU_N_S(
		long userId, long nodeId, int status, int start, int end) {

		return findByU_N_S(userId, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByU_N_S(
		long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByU_N_S(
			userId, nodeId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByU_N_S(
		long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByU_N_S;
					finderArgs = new Object[] {userId, nodeId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByU_N_S;
				finderArgs = new Object[] {
					userId, nodeId, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((userId != wikiPage.getUserId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_N_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_U_N_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(nodeId);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByU_N_S_First(
			long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByU_N_S_First(
			userId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByU_N_S_First(
		long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByU_N_S(
			userId, nodeId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByU_N_S_Last(
			long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByU_N_S_Last(
			userId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByU_N_S_Last(
		long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByU_N_S(userId, nodeId, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByU_N_S(
			userId, nodeId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByU_N_S_PrevAndNext(
			long pageId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByU_N_S_PrevAndNext(
				session, wikiPage, userId, nodeId, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByU_N_S_PrevAndNext(
				session, wikiPage, userId, nodeId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByU_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long userId, long nodeId,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_U_N_S_USERID_2);

		sb.append(_FINDER_COLUMN_U_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_U_N_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where userId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByU_N_S(long userId, long nodeId, int status) {
		for (WikiPage wikiPage :
				findByU_N_S(
					userId, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByU_N_S(long userId, long nodeId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByU_N_S;

			Object[] finderArgs = new Object[] {userId, nodeId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_U_N_S_USERID_2);

				sb.append(_FINDER_COLUMN_U_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_U_N_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(nodeId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_U_N_S_USERID_2 =
		"wikiPage.userId = ? AND ";

	private static final String _FINDER_COLUMN_U_N_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_U_N_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathFetchByN_T_V;

	/**
	 * Returns the wiki page where nodeId = &#63; and title = &#63; and version = &#63; or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_V(long nodeId, String title, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_V(nodeId, title, version);

		if (wikiPage == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("nodeId=");
			sb.append(nodeId);

			sb.append(", title=");
			sb.append(title);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPageException(sb.toString());
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page where nodeId = &#63; and title = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_V(long nodeId, String title, double version) {
		return fetchByN_T_V(nodeId, title, version, true);
	}

	/**
	 * Returns the wiki page where nodeId = &#63; and title = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_V(
		long nodeId, String title, double version, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {nodeId, title, version};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByN_T_V, finderArgs, this);
			}

			if (result instanceof WikiPage) {
				WikiPage wikiPage = (WikiPage)result;

				if ((nodeId != wikiPage.getNodeId()) ||
					!Objects.equals(title, wikiPage.getTitle()) ||
					(version != wikiPage.getVersion())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_V_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_V_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_V_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_T_V_VERSION_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(version);

					List<WikiPage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByN_T_V, finderArgs, list);
						}
					}
					else {
						WikiPage wikiPage = list.get(0);

						result = wikiPage;

						cacheResult(wikiPage);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (WikiPage)result;
			}
		}
	}

	/**
	 * Removes the wiki page where nodeId = &#63; and title = &#63; and version = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the wiki page that was removed
	 */
	@Override
	public WikiPage removeByN_T_V(long nodeId, String title, double version)
		throws NoSuchPageException {

		WikiPage wikiPage = findByN_T_V(nodeId, title, version);

		return remove(wikiPage);
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and version = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param version the version
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_V(long nodeId, String title, double version) {
		WikiPage wikiPage = fetchByN_T_V(nodeId, title, version);

		if (wikiPage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_N_T_V_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_T_V_TITLE_2 =
		"lower(wikiPage.title) = ? AND ";

	private static final String _FINDER_COLUMN_N_T_V_TITLE_3 =
		"(wikiPage.title IS NULL OR wikiPage.title = '') AND ";

	private static final String _FINDER_COLUMN_N_T_V_VERSION_2 =
		"wikiPage.version = ?";

	private FinderPath _finderPathWithPaginationFindByN_T_H;
	private FinderPath _finderPathWithoutPaginationFindByN_T_H;
	private FinderPath _finderPathCountByN_T_H;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_H(long nodeId, String title, boolean head) {
		return findByN_T_H(
			nodeId, title, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_H(
		long nodeId, String title, boolean head, int start, int end) {

		return findByN_T_H(nodeId, title, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_H(
		long nodeId, String title, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_T_H(
			nodeId, title, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_H(
		long nodeId, String title, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_T_H;
					finderArgs = new Object[] {nodeId, title, head};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_T_H;
				finderArgs = new Object[] {
					nodeId, title, head, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							!title.equals(wikiPage.getTitle()) ||
							(head != wikiPage.isHead())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_H_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_H_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_H_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_T_H_HEAD_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(head);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_H_First(
			long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_H_First(
			nodeId, title, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_H_First(
		long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_T_H(
			nodeId, title, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_H_Last(
			long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_H_Last(
			nodeId, title, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_H_Last(
		long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_T_H(nodeId, title, head);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_T_H(
			nodeId, title, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_T_H_PrevAndNext(
			long pageId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		title = Objects.toString(title, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_T_H_PrevAndNext(
				session, wikiPage, nodeId, title, head, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_T_H_PrevAndNext(
				session, wikiPage, nodeId, title, head, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_T_H_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, String title,
		boolean head, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_T_H_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_T_H_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_N_T_H_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_T_H_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (bindTitle) {
			queryPos.add(StringUtil.toLowerCase(title));
		}

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; and head = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 */
	@Override
	public void removeByN_T_H(long nodeId, String title, boolean head) {
		for (WikiPage wikiPage :
				findByN_T_H(
					nodeId, title, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_H(long nodeId, String title, boolean head) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = _finderPathCountByN_T_H;

			Object[] finderArgs = new Object[] {nodeId, title, head};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_H_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_H_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_H_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_T_H_HEAD_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(head);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_T_H_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_T_H_TITLE_2 =
		"lower(wikiPage.title) = ? AND ";

	private static final String _FINDER_COLUMN_N_T_H_TITLE_3 =
		"(wikiPage.title IS NULL OR wikiPage.title = '') AND ";

	private static final String _FINDER_COLUMN_N_T_H_HEAD_2 =
		"wikiPage.head = ?";

	private FinderPath _finderPathWithPaginationFindByN_T_S;
	private FinderPath _finderPathWithoutPaginationFindByN_T_S;
	private FinderPath _finderPathCountByN_T_S;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_S(long nodeId, String title, int status) {
		return findByN_T_S(
			nodeId, title, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_S(
		long nodeId, String title, int status, int start, int end) {

		return findByN_T_S(nodeId, title, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_S(
		long nodeId, String title, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_T_S(
			nodeId, title, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_T_S(
		long nodeId, String title, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_T_S;
					finderArgs = new Object[] {nodeId, title, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_T_S;
				finderArgs = new Object[] {
					nodeId, title, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							!title.equals(wikiPage.getTitle()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_S_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_S_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_S_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_T_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_S_First(
			long nodeId, String title, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_S_First(
			nodeId, title, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_S_First(
		long nodeId, String title, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_T_S(
			nodeId, title, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_T_S_Last(
			long nodeId, String title, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_T_S_Last(
			nodeId, title, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_T_S_Last(
		long nodeId, String title, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_T_S(nodeId, title, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_T_S(
			nodeId, title, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_T_S_PrevAndNext(
			long pageId, long nodeId, String title, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		title = Objects.toString(title, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_T_S_PrevAndNext(
				session, wikiPage, nodeId, title, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_T_S_PrevAndNext(
				session, wikiPage, nodeId, title, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_T_S_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, String title,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_T_S_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_T_S_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_N_T_S_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_T_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		if (bindTitle) {
			queryPos.add(StringUtil.toLowerCase(title));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and title = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 */
	@Override
	public void removeByN_T_S(long nodeId, String title, int status) {
		for (WikiPage wikiPage :
				findByN_T_S(
					nodeId, title, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and title = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param title the title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_T_S(long nodeId, String title, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = _finderPathCountByN_T_S;

			Object[] finderArgs = new Object[] {nodeId, title, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_T_S_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_T_S_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_N_T_S_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_T_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_T_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_T_S_TITLE_2 =
		"lower(wikiPage.title) = ? AND ";

	private static final String _FINDER_COLUMN_N_T_S_TITLE_3 =
		"(wikiPage.title IS NULL OR wikiPage.title = '') AND ";

	private static final String _FINDER_COLUMN_N_T_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByN_H_P;
	private FinderPath _finderPathWithoutPaginationFindByN_H_P;
	private FinderPath _finderPathCountByN_H_P;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P(
		long nodeId, boolean head, String parentTitle) {

		return findByN_H_P(
			nodeId, head, parentTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P(
		long nodeId, boolean head, String parentTitle, int start, int end) {

		return findByN_H_P(nodeId, head, parentTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P(
		long nodeId, boolean head, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_P(
			nodeId, head, parentTitle, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P(
		long nodeId, boolean head, String parentTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H_P;
					finderArgs = new Object[] {nodeId, head, parentTitle};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H_P;
				finderArgs = new Object[] {
					nodeId, head, parentTitle, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!parentTitle.equals(wikiPage.getParentTitle())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_First(
			long nodeId, boolean head, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_First(
			nodeId, head, parentTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_First(
		long nodeId, boolean head, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_P(
			nodeId, head, parentTitle, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_Last(
			long nodeId, boolean head, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_Last(
			nodeId, head, parentTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_Last(
		long nodeId, boolean head, String parentTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_P(nodeId, head, parentTitle);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_P(
			nodeId, head, parentTitle, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_P_PrevAndNext(
			long pageId, long nodeId, boolean head, String parentTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_P_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_H_P_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_P_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String parentTitle, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_P_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_P_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 */
	@Override
	public void removeByN_H_P(long nodeId, boolean head, String parentTitle) {
		for (WikiPage wikiPage :
				findByN_H_P(
					nodeId, head, parentTitle, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P(long nodeId, boolean head, String parentTitle) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = _finderPathCountByN_H_P;

			Object[] finderArgs = new Object[] {nodeId, head, parentTitle};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_PARENTTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_P_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_PARENTTITLE_2 =
		"lower(wikiPage.parentTitle) = ?";

	private static final String _FINDER_COLUMN_N_H_P_PARENTTITLE_3 =
		"(wikiPage.parentTitle IS NULL OR wikiPage.parentTitle = '')";

	private FinderPath _finderPathWithPaginationFindByN_H_R;
	private FinderPath _finderPathWithoutPaginationFindByN_H_R;
	private FinderPath _finderPathCountByN_H_R;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R(
		long nodeId, boolean head, String redirectTitle) {

		return findByN_H_R(
			nodeId, head, redirectTitle, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R(
		long nodeId, boolean head, String redirectTitle, int start, int end) {

		return findByN_H_R(nodeId, head, redirectTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R(
		long nodeId, boolean head, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_R(
			nodeId, head, redirectTitle, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R(
		long nodeId, boolean head, String redirectTitle, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H_R;
					finderArgs = new Object[] {nodeId, head, redirectTitle};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H_R;
				finderArgs = new Object[] {
					nodeId, head, redirectTitle, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!redirectTitle.equals(
								wikiPage.getRedirectTitle())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_First(
			long nodeId, boolean head, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_First(
			nodeId, head, redirectTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_First(
		long nodeId, boolean head, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_R(
			nodeId, head, redirectTitle, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_Last(
			long nodeId, boolean head, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_Last(
			nodeId, head, redirectTitle, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_Last(
		long nodeId, boolean head, String redirectTitle,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_R(nodeId, head, redirectTitle);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_R(
			nodeId, head, redirectTitle, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_R_PrevAndNext(
			long pageId, long nodeId, boolean head, String redirectTitle,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		redirectTitle = Objects.toString(redirectTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_R_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_R_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_R_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String redirectTitle, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_R_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_R_HEAD_2);

		boolean bindRedirectTitle = false;

		if (redirectTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_3);
		}
		else {
			bindRedirectTitle = true;

			sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindRedirectTitle) {
			queryPos.add(StringUtil.toLowerCase(redirectTitle));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 */
	@Override
	public void removeByN_H_R(long nodeId, boolean head, String redirectTitle) {
		for (WikiPage wikiPage :
				findByN_H_R(
					nodeId, head, redirectTitle, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R(long nodeId, boolean head, String redirectTitle) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = _finderPathCountByN_H_R;

			Object[] finderArgs = new Object[] {nodeId, head, redirectTitle};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_REDIRECTTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_R_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_REDIRECTTITLE_2 =
		"lower(wikiPage.redirectTitle) = ?";

	private static final String _FINDER_COLUMN_N_H_R_REDIRECTTITLE_3 =
		"(wikiPage.redirectTitle IS NULL OR wikiPage.redirectTitle = '')";

	private FinderPath _finderPathWithPaginationFindByN_H_S;
	private FinderPath _finderPathWithoutPaginationFindByN_H_S;
	private FinderPath _finderPathCountByN_H_S;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_S(long nodeId, boolean head, int status) {
		return findByN_H_S(
			nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_S(
		long nodeId, boolean head, int status, int start, int end) {

		return findByN_H_S(nodeId, head, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_S(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_S(
			nodeId, head, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_S(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H_S;
					finderArgs = new Object[] {nodeId, head, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H_S;
				finderArgs = new Object[] {
					nodeId, head, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_S_HEAD_2);

				sb.append(_FINDER_COLUMN_N_H_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_S_First(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_S_First(
			nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_S_First(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_S(
			nodeId, head, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_S_Last(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_S_Last(
			nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_S_Last(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_S(nodeId, head, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_S(
			nodeId, head, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_S_PrevAndNext(
			long pageId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_S_PrevAndNext(
				session, wikiPage, nodeId, head, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_H_S_PrevAndNext(
				session, wikiPage, nodeId, head, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_S_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_S_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_S_HEAD_2);

		sb.append(_FINDER_COLUMN_N_H_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByN_H_S(long nodeId, boolean head, int status) {
		for (WikiPage wikiPage :
				findByN_H_S(
					nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_S(long nodeId, boolean head, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByN_H_S;

			Object[] finderArgs = new Object[] {nodeId, head, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_S_HEAD_2);

				sb.append(_FINDER_COLUMN_N_H_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_S_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByN_H_NotS;
	private FinderPath _finderPathWithPaginationCountByN_H_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status) {

		return findByN_H_NotS(
			nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end) {

		return findByN_H_NotS(nodeId, head, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_NotS(
			nodeId, head, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_NotS(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByN_H_NotS;
			finderArgs = new Object[] {
				nodeId, head, status, start, end, orderByComparator
			};

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							(status == wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_NOTS_HEAD_2);

				sb.append(_FINDER_COLUMN_N_H_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_NotS_First(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_NotS_First(
			nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_NotS_First(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_NotS(
			nodeId, head, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_NotS_Last(
			long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_NotS_Last(
			nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_NotS_Last(
		long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_NotS(nodeId, head, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_NotS(
			nodeId, head, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_NotS_PrevAndNext(
			long pageId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, status, orderByComparator,
				true);

			array[1] = wikiPage;

			array[2] = getByN_H_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_NotS_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_NOTS_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_NOTS_HEAD_2);

		sb.append(_FINDER_COLUMN_N_H_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByN_H_NotS(long nodeId, boolean head, int status) {
		for (WikiPage wikiPage :
				findByN_H_NotS(
					nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_NotS(long nodeId, boolean head, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByN_H_NotS;

			Object[] finderArgs = new Object[] {nodeId, head, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_NOTS_HEAD_2);

				sb.append(_FINDER_COLUMN_N_H_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_NOTS_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_NOTS_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_NOTS_STATUS_2 =
		"wikiPage.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_U_N_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_N_S;
	private FinderPath _finderPathCountByG_U_N_S;

	/**
	 * Returns all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		return findByG_U_N_S(
			groupId, userId, nodeId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start,
		int end) {

		return findByG_U_N_S(groupId, userId, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByG_U_N_S(
			groupId, userId, nodeId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U_N_S;
					finderArgs = new Object[] {groupId, userId, nodeId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U_N_S;
				finderArgs = new Object[] {
					groupId, userId, nodeId, status, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(userId != wikiPage.getUserId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(nodeId);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_U_N_S_First(
			long groupId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_U_N_S_First(
			groupId, userId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_U_N_S_First(
		long groupId, long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_U_N_S(
			groupId, userId, nodeId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_U_N_S_Last(
			long groupId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_U_N_S_Last(
			groupId, userId, nodeId, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_U_N_S_Last(
		long groupId, long userId, long nodeId, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_U_N_S(groupId, userId, nodeId, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_U_N_S(
			groupId, userId, nodeId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_U_N_S_PrevAndNext(
			long pageId, long groupId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_U_N_S_PrevAndNext(
				session, wikiPage, groupId, userId, nodeId, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByG_U_N_S_PrevAndNext(
				session, wikiPage, groupId, userId, nodeId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_U_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long userId,
		long nodeId, int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		return filterFindByG_U_N_S(
			groupId, userId, nodeId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start,
		int end) {

		return filterFindByG_U_N_S(
			groupId, userId, nodeId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_U_N_S(
		long groupId, long userId, long nodeId, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_N_S(
				groupId, userId, nodeId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_U_N_S(
					groupId, userId, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(nodeId);

			queryPos.add(status);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_U_N_S_PrevAndNext(
			long pageId, long groupId, long userId, long nodeId, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_N_S_PrevAndNext(
				pageId, groupId, userId, nodeId, status, orderByComparator);
		}

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_U_N_S_PrevAndNext(
				session, wikiPage, groupId, userId, nodeId, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = filterGetByG_U_N_S_PrevAndNext(
				session, wikiPage, groupId, userId, nodeId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_U_N_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long userId,
		long nodeId, int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(nodeId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		for (WikiPage wikiPage :
				findByG_U_N_S(
					groupId, userId, nodeId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByG_U_N_S;

			Object[] finderArgs = new Object[] {
				groupId, userId, nodeId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(nodeId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and userId = &#63; and nodeId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param nodeId the node ID
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_N_S(
		long groupId, long userId, long nodeId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_N_S(groupId, userId, nodeId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_U_N_S(
				groupId, userId, nodeId, status);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_N_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_U_N_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(nodeId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_U_N_S_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_N_S_USERID_2 =
		"wikiPage.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_N_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_N_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_N_T_H;
	private FinderPath _finderPathWithoutPaginationFindByG_N_T_H;
	private FinderPath _finderPathCountByG_N_T_H;

	/**
	 * Returns all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		return findByG_N_T_H(
			groupId, nodeId, title, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end) {

		return findByG_N_T_H(groupId, nodeId, title, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByG_N_T_H(
			groupId, nodeId, title, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_T_H;
					finderArgs = new Object[] {groupId, nodeId, title, head};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_T_H;
				finderArgs = new Object[] {
					groupId, nodeId, title, head, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							!title.equals(wikiPage.getTitle()) ||
							(head != wikiPage.isHead())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(head);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_T_H_First(
			long groupId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_T_H_First(
			groupId, nodeId, title, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_T_H_First(
		long groupId, long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_N_T_H(
			groupId, nodeId, title, head, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_T_H_Last(
			long groupId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_T_H_Last(
			groupId, nodeId, title, head, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", title=");
		sb.append(title);

		sb.append(", head=");
		sb.append(head);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_T_H_Last(
		long groupId, long nodeId, String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_N_T_H(groupId, nodeId, title, head);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_N_T_H(
			groupId, nodeId, title, head, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_N_T_H_PrevAndNext(
			long pageId, long groupId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		title = Objects.toString(title, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_N_T_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, title, head,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByG_N_T_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, title, head,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_N_T_H_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		if (bindTitle) {
			queryPos.add(StringUtil.toLowerCase(title));
		}

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		return filterFindByG_N_T_H(
			groupId, nodeId, title, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end) {

		return filterFindByG_N_T_H(
			groupId, nodeId, title, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_T_H(
		long groupId, long nodeId, String title, boolean head, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_T_H(
				groupId, nodeId, title, head, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_T_H(
					groupId, nodeId, title, head, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		title = Objects.toString(title, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			if (bindTitle) {
				queryPos.add(StringUtil.toLowerCase(title));
			}

			queryPos.add(head);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_N_T_H_PrevAndNext(
			long pageId, long groupId, long nodeId, String title, boolean head,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_T_H_PrevAndNext(
				pageId, groupId, nodeId, title, head, orderByComparator);
		}

		title = Objects.toString(title, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_N_T_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, title, head,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = filterGetByG_N_T_H_PrevAndNext(
				session, wikiPage, groupId, nodeId, title, head,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_N_T_H_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		String title, boolean head,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		if (bindTitle) {
			queryPos.add(StringUtil.toLowerCase(title));
		}

		queryPos.add(head);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 */
	@Override
	public void removeByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		for (WikiPage wikiPage :
				findByG_N_T_H(
					groupId, nodeId, title, head, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			title = Objects.toString(title, "");

			FinderPath finderPath = _finderPathCountByG_N_T_H;

			Object[] finderArgs = new Object[] {groupId, nodeId, title, head};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

				boolean bindTitle = false;

				if (title.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
				}
				else {
					bindTitle = true;

					sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
				}

				sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					if (bindTitle) {
						queryPos.add(StringUtil.toLowerCase(title));
					}

					queryPos.add(head);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and title = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param title the title
	 * @param head the head
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_T_H(
		long groupId, long nodeId, String title, boolean head) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_T_H(groupId, nodeId, title, head);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_N_T_H(
				groupId, nodeId, title, head);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		title = Objects.toString(title, "");

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_T_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_T_H_NODEID_2);

		boolean bindTitle = false;

		if (title.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_3);
		}
		else {
			bindTitle = true;

			sb.append(_FINDER_COLUMN_G_N_T_H_TITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_T_H_HEAD_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			if (bindTitle) {
				queryPos.add(StringUtil.toLowerCase(title));
			}

			queryPos.add(head);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_N_T_H_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_H_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_H_TITLE_2 =
		"lower(wikiPage.title) = ? AND ";

	private static final String _FINDER_COLUMN_G_N_T_H_TITLE_3 =
		"(wikiPage.title IS NULL OR wikiPage.title = '') AND ";

	private static final String _FINDER_COLUMN_G_N_T_H_HEAD_2 =
		"wikiPage.head = ?";

	private FinderPath _finderPathWithPaginationFindByG_N_H_S;
	private FinderPath _finderPathWithoutPaginationFindByG_N_H_S;
	private FinderPath _finderPathCountByG_N_H_S;

	/**
	 * Returns all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		return findByG_N_H_S(
			groupId, nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start,
		int end) {

		return findByG_N_H_S(groupId, nodeId, head, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return findByG_N_H_S(
			groupId, nodeId, head, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_H_S;
					finderArgs = new Object[] {groupId, nodeId, head, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_H_S;
				finderArgs = new Object[] {
					groupId, nodeId, head, status, start, end, orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_S_First(
			long groupId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_S_First(
			groupId, nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_S_First(
		long groupId, long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_N_H_S(
			groupId, nodeId, head, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_S_Last(
			long groupId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_S_Last(
			groupId, nodeId, head, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_S_Last(
		long groupId, long nodeId, boolean head, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_N_H_S(groupId, nodeId, head, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_N_H_S(
			groupId, nodeId, head, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_N_H_S_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_N_H_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByG_N_H_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_N_H_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		return filterFindByG_N_H_S(
			groupId, nodeId, head, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start,
		int end) {

		return filterFindByG_N_H_S(
			groupId, nodeId, head, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H_S(
				groupId, nodeId, head, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_H_S(
					groupId, nodeId, head, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			queryPos.add(status);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_N_H_S_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H_S_PrevAndNext(
				pageId, groupId, nodeId, head, status, orderByComparator);
		}

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_N_H_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = filterGetByG_N_H_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_N_H_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, int status, OrderByComparator<WikiPage> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		for (WikiPage wikiPage :
				findByG_N_H_S(
					groupId, nodeId, head, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = _finderPathCountByG_N_H_S;

			Object[] finderArgs = new Object[] {groupId, nodeId, head, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

				sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H_S(
		long groupId, long nodeId, boolean head, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_H_S(groupId, nodeId, head, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_N_H_S(
				groupId, nodeId, head, status);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_HEAD_2);

		sb.append(_FINDER_COLUMN_G_N_H_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_N_H_S_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_S_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByN_H_P_S;
	private FinderPath _finderPathWithoutPaginationFindByN_H_P_S;
	private FinderPath _finderPathCountByN_H_P_S;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status) {

		return findByN_H_P_S(
			nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end) {

		return findByN_H_P_S(
			nodeId, head, parentTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_P_S(
			nodeId, head, parentTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H_P_S;
					finderArgs = new Object[] {
						nodeId, head, parentTitle, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H_P_S;
				finderArgs = new Object[] {
					nodeId, head, parentTitle, status, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!parentTitle.equals(wikiPage.getParentTitle()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_S_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_S_First(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_S_First(
			nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_S_First(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_P_S(
			nodeId, head, parentTitle, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_S_Last(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_S_Last(
			nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_S_Last(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_P_S(nodeId, head, parentTitle, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_P_S(
			nodeId, head, parentTitle, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_P_S_PrevAndNext(
			long pageId, long nodeId, boolean head, String parentTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_P_S_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_P_S_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_P_S_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_P_S_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_P_S_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_H_P_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status) {

		for (WikiPage wikiPage :
				findByN_H_P_S(
					nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P_S(
		long nodeId, boolean head, String parentTitle, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = _finderPathCountByN_H_P_S;

			Object[] finderArgs = new Object[] {
				nodeId, head, parentTitle, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_S_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_S_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_P_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_S_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_S_PARENTTITLE_2 =
		"lower(wikiPage.parentTitle) = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_S_PARENTTITLE_3 =
		"(wikiPage.parentTitle IS NULL OR wikiPage.parentTitle = '') AND ";

	private static final String _FINDER_COLUMN_N_H_P_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByN_H_P_NotS;
	private FinderPath _finderPathWithPaginationCountByN_H_P_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_P_NotS(
			nodeId, head, parentTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByN_H_P_NotS;
			finderArgs = new Object[] {
				nodeId, head, parentTitle, status, start, end, orderByComparator
			};

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!parentTitle.equals(wikiPage.getParentTitle()) ||
							(status == wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_NotS_First(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_NotS_First(
			nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_NotS_First(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_P_NotS(
			nodeId, head, parentTitle, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_P_NotS_Last(
			long nodeId, boolean head, String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_P_NotS_Last(
			nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_P_NotS_Last(
		long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_P_NotS(nodeId, head, parentTitle, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_P_NotS(
			nodeId, head, parentTitle, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_P_NotS_PrevAndNext(
			long pageId, long nodeId, boolean head, String parentTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_P_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_P_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, parentTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_P_NotS_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_P_NOTS_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_P_NOTS_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_H_P_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		for (WikiPage wikiPage :
				findByN_H_P_NotS(
					nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and parentTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_P_NotS(
		long nodeId, boolean head, String parentTitle, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = _finderPathWithPaginationCountByN_H_P_NotS;

			Object[] finderArgs = new Object[] {
				nodeId, head, parentTitle, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_P_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_P_NOTS_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_NOTS_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_2 =
		"lower(wikiPage.parentTitle) = ? AND ";

	private static final String _FINDER_COLUMN_N_H_P_NOTS_PARENTTITLE_3 =
		"(wikiPage.parentTitle IS NULL OR wikiPage.parentTitle = '') AND ";

	private static final String _FINDER_COLUMN_N_H_P_NOTS_STATUS_2 =
		"wikiPage.status != ?";

	private FinderPath _finderPathWithPaginationFindByN_H_R_S;
	private FinderPath _finderPathWithoutPaginationFindByN_H_R_S;
	private FinderPath _finderPathCountByN_H_R_S;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status) {

		return findByN_H_R_S(
			nodeId, head, redirectTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end) {

		return findByN_H_R_S(
			nodeId, head, redirectTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_R_S(
			nodeId, head, redirectTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByN_H_R_S;
					finderArgs = new Object[] {
						nodeId, head, redirectTitle, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByN_H_R_S;
				finderArgs = new Object[] {
					nodeId, head, redirectTitle, status, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!redirectTitle.equals(
								wikiPage.getRedirectTitle()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_S_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_R_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_S_First(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_S_First(
			nodeId, head, redirectTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_S_First(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_R_S(
			nodeId, head, redirectTitle, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_S_Last(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_S_Last(
			nodeId, head, redirectTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_S_Last(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_R_S(nodeId, head, redirectTitle, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_R_S(
			nodeId, head, redirectTitle, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_R_S_PrevAndNext(
			long pageId, long nodeId, boolean head, String redirectTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		redirectTitle = Objects.toString(redirectTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_R_S_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_R_S_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_R_S_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_R_S_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_R_S_HEAD_2);

		boolean bindRedirectTitle = false;

		if (redirectTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_3);
		}
		else {
			bindRedirectTitle = true;

			sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_H_R_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindRedirectTitle) {
			queryPos.add(StringUtil.toLowerCase(redirectTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status) {

		for (WikiPage wikiPage :
				findByN_H_R_S(
					nodeId, head, redirectTitle, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status = &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R_S(
		long nodeId, boolean head, String redirectTitle, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = _finderPathCountByN_H_R_S;

			Object[] finderArgs = new Object[] {
				nodeId, head, redirectTitle, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_S_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_S_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_R_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_R_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_S_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_2 =
		"lower(wikiPage.redirectTitle) = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_S_REDIRECTTITLE_3 =
		"(wikiPage.redirectTitle IS NULL OR wikiPage.redirectTitle = '') AND ";

	private static final String _FINDER_COLUMN_N_H_R_S_STATUS_2 =
		"wikiPage.status = ?";

	private FinderPath _finderPathWithPaginationFindByN_H_R_NotS;
	private FinderPath _finderPathWithPaginationCountByN_H_R_NotS;

	/**
	 * Returns all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByN_H_R_NotS;
			finderArgs = new Object[] {
				nodeId, head, redirectTitle, status, start, end,
				orderByComparator
			};

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!redirectTitle.equals(
								wikiPage.getRedirectTitle()) ||
							(status == wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_NotS_First(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_NotS_First(
			nodeId, head, redirectTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_NotS_First(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByN_H_R_NotS_Last(
			long nodeId, boolean head, String redirectTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByN_H_R_NotS_Last(
			nodeId, head, redirectTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", redirectTitle=");
		sb.append(redirectTitle);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByN_H_R_NotS_Last(
		long nodeId, boolean head, String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByN_H_R_NotS(nodeId, head, redirectTitle, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByN_H_R_NotS(
			nodeId, head, redirectTitle, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByN_H_R_NotS_PrevAndNext(
			long pageId, long nodeId, boolean head, String redirectTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		redirectTitle = Objects.toString(redirectTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByN_H_R_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByN_H_R_NotS_PrevAndNext(
				session, wikiPage, nodeId, head, redirectTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByN_H_R_NotS_PrevAndNext(
		Session session, WikiPage wikiPage, long nodeId, boolean head,
		String redirectTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_N_H_R_NOTS_NODEID_2);

		sb.append(_FINDER_COLUMN_N_H_R_NOTS_HEAD_2);

		boolean bindRedirectTitle = false;

		if (redirectTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_3);
		}
		else {
			bindRedirectTitle = true;

			sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_N_H_R_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindRedirectTitle) {
			queryPos.add(StringUtil.toLowerCase(redirectTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 */
	@Override
	public void removeByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		for (WikiPage wikiPage :
				findByN_H_R_NotS(
					nodeId, head, redirectTitle, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where nodeId = &#63; and head = &#63; and redirectTitle = &#63; and status &ne; &#63;.
	 *
	 * @param nodeId the node ID
	 * @param head the head
	 * @param redirectTitle the redirect title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByN_H_R_NotS(
		long nodeId, boolean head, String redirectTitle, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			redirectTitle = Objects.toString(redirectTitle, "");

			FinderPath finderPath = _finderPathWithPaginationCountByN_H_R_NotS;

			Object[] finderArgs = new Object[] {
				nodeId, head, redirectTitle, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_NODEID_2);

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_HEAD_2);

				boolean bindRedirectTitle = false;

				if (redirectTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_3);
				}
				else {
					bindRedirectTitle = true;

					sb.append(_FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_N_H_R_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindRedirectTitle) {
						queryPos.add(StringUtil.toLowerCase(redirectTitle));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_N_H_R_NOTS_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_NOTS_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_2 =
		"lower(wikiPage.redirectTitle) = ? AND ";

	private static final String _FINDER_COLUMN_N_H_R_NOTS_REDIRECTTITLE_3 =
		"(wikiPage.redirectTitle IS NULL OR wikiPage.redirectTitle = '') AND ";

	private static final String _FINDER_COLUMN_N_H_R_NOTS_STATUS_2 =
		"wikiPage.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_N_H_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_N_H_P_S;
	private FinderPath _finderPathCountByG_N_H_P_S;

	/**
	 * Returns all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		return findByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end) {

		return findByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end, OrderByComparator<WikiPage> orderByComparator) {

		return findByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki pages
	 */
	@Override
	public List<WikiPage> findByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_N_H_P_S;
					finderArgs = new Object[] {
						groupId, nodeId, head, parentTitle, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_N_H_P_S;
				finderArgs = new Object[] {
					groupId, nodeId, head, parentTitle, status, start, end,
					orderByComparator
				};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WikiPage wikiPage : list) {
						if ((groupId != wikiPage.getGroupId()) ||
							(nodeId != wikiPage.getNodeId()) ||
							(head != wikiPage.isHead()) ||
							!parentTitle.equals(wikiPage.getParentTitle()) ||
							(status != wikiPage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						7 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(7);
				}

				sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_P_S_First(
			long groupId, long nodeId, boolean head, String parentTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_P_S_First(
			groupId, nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the first wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_P_S_First(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		List<WikiPage> list = findByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page
	 * @throws NoSuchPageException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage findByG_N_H_P_S_Last(
			long groupId, long nodeId, boolean head, String parentTitle,
			int status, OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByG_N_H_P_S_Last(
			groupId, nodeId, head, parentTitle, status, orderByComparator);

		if (wikiPage != null) {
			return wikiPage;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", nodeId=");
		sb.append(nodeId);

		sb.append(", head=");
		sb.append(head);

		sb.append(", parentTitle=");
		sb.append(parentTitle);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPageException(sb.toString());
	}

	/**
	 * Returns the last wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchByG_N_H_P_S_Last(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator) {

		int count = countByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status);

		if (count == 0) {
			return null;
		}

		List<WikiPage> list = findByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] findByG_N_H_P_S_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head,
			String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = getByG_N_H_P_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, parentTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = getByG_N_H_P_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, parentTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage getByG_N_H_P_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(WikiPageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		return filterFindByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end) {

		return filterFindByG_N_H_P_S(
			groupId, nodeId, head, parentTitle, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages that the user has permissions to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki pages that the user has permission to view
	 */
	@Override
	public List<WikiPage> filterFindByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle, int status,
		int start, int end, OrderByComparator<WikiPage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H_P_S(
				groupId, nodeId, head, parentTitle, status, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_N_H_P_S(
					groupId, nodeId, head, parentTitle, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		parentTitle = Objects.toString(parentTitle, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			if (bindParentTitle) {
				queryPos.add(StringUtil.toLowerCase(parentTitle));
			}

			queryPos.add(status);

			return (List<WikiPage>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the wiki pages before and after the current wiki page in the ordered set of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param pageId the primary key of the current wiki page
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage[] filterFindByG_N_H_P_S_PrevAndNext(
			long pageId, long groupId, long nodeId, boolean head,
			String parentTitle, int status,
			OrderByComparator<WikiPage> orderByComparator)
		throws NoSuchPageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_N_H_P_S_PrevAndNext(
				pageId, groupId, nodeId, head, parentTitle, status,
				orderByComparator);
		}

		parentTitle = Objects.toString(parentTitle, "");

		WikiPage wikiPage = findByPrimaryKey(pageId);

		Session session = null;

		try {
			session = openSession();

			WikiPage[] array = new WikiPageImpl[3];

			array[0] = filterGetByG_N_H_P_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, parentTitle, status,
				orderByComparator, true);

			array[1] = wikiPage;

			array[2] = filterGetByG_N_H_P_S_PrevAndNext(
				session, wikiPage, groupId, nodeId, head, parentTitle, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WikiPage filterGetByG_N_H_P_S_PrevAndNext(
		Session session, WikiPage wikiPage, long groupId, long nodeId,
		boolean head, String parentTitle, int status,
		OrderByComparator<WikiPage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				9 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(8);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(WikiPageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, WikiPageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, WikiPageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(nodeId);

		queryPos.add(head);

		if (bindParentTitle) {
			queryPos.add(StringUtil.toLowerCase(parentTitle));
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(wikiPage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WikiPage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 */
	@Override
	public void removeByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		for (WikiPage wikiPage :
				findByG_N_H_P_S(
					groupId, nodeId, head, parentTitle, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages
	 */
	@Override
	public int countByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			parentTitle = Objects.toString(parentTitle, "");

			FinderPath finderPath = _finderPathCountByG_N_H_P_S;

			Object[] finderArgs = new Object[] {
				groupId, nodeId, head, parentTitle, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_WIKIPAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

				sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

				boolean bindParentTitle = false;

				if (parentTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
				}
				else {
					bindParentTitle = true;

					sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
				}

				sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(nodeId);

					queryPos.add(head);

					if (bindParentTitle) {
						queryPos.add(StringUtil.toLowerCase(parentTitle));
					}

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of wiki pages that the user has permission to view where groupId = &#63; and nodeId = &#63; and head = &#63; and parentTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param nodeId the node ID
	 * @param head the head
	 * @param parentTitle the parent title
	 * @param status the status
	 * @return the number of matching wiki pages that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_H_P_S(
		long groupId, long nodeId, boolean head, String parentTitle,
		int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_N_H_P_S(groupId, nodeId, head, parentTitle, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<WikiPage> wikiPages = findByG_N_H_P_S(
				groupId, nodeId, head, parentTitle, status);

			wikiPages = InlineSQLHelperUtil.filter(wikiPages, groupId);

			return wikiPages.size();
		}

		parentTitle = Objects.toString(parentTitle, "");

		StringBundler sb = new StringBundler(6);

		sb.append(_FILTER_SQL_COUNT_WIKIPAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_NODEID_2);

		sb.append(_FINDER_COLUMN_G_N_H_P_S_HEAD_2);

		boolean bindParentTitle = false;

		if (parentTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3);
		}
		else {
			bindParentTitle = true;

			sb.append(_FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2);
		}

		sb.append(_FINDER_COLUMN_G_N_H_P_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), WikiPage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(nodeId);

			queryPos.add(head);

			if (bindParentTitle) {
				queryPos.add(StringUtil.toLowerCase(parentTitle));
			}

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_N_H_P_S_GROUPID_2 =
		"wikiPage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_P_S_NODEID_2 =
		"wikiPage.nodeId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_P_S_HEAD_2 =
		"wikiPage.head = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_2 =
		"lower(wikiPage.parentTitle) = ? AND ";

	private static final String _FINDER_COLUMN_G_N_H_P_S_PARENTTITLE_3 =
		"(wikiPage.parentTitle IS NULL OR wikiPage.parentTitle = '') AND ";

	private static final String _FINDER_COLUMN_G_N_H_P_S_STATUS_2 =
		"wikiPage.status = ?";

	public WikiPagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WikiPage.class);

		setModelImplClass(WikiPageImpl.class);
		setModelPKClass(long.class);

		setTable(WikiPageTable.INSTANCE);
	}

	/**
	 * Caches the wiki page in the entity cache if it is enabled.
	 *
	 * @param wikiPage the wiki page
	 */
	@Override
	public void cacheResult(WikiPage wikiPage) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					wikiPage.getCtCollectionId())) {

			entityCache.putResult(
				WikiPageImpl.class, wikiPage.getPrimaryKey(), wikiPage);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {wikiPage.getUuid(), wikiPage.getGroupId()},
				wikiPage);

			finderCache.putResult(
				_finderPathFetchByR_N_V,
				new Object[] {
					wikiPage.getResourcePrimKey(), wikiPage.getNodeId(),
					wikiPage.getVersion()
				},
				wikiPage);

			finderCache.putResult(
				_finderPathFetchByG_ERC_V,
				new Object[] {
					wikiPage.getGroupId(), wikiPage.getExternalReferenceCode(),
					wikiPage.getVersion()
				},
				wikiPage);

			finderCache.putResult(
				_finderPathFetchByN_T_V,
				new Object[] {
					wikiPage.getNodeId(), wikiPage.getTitle(),
					wikiPage.getVersion()
				},
				wikiPage);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the wiki pages in the entity cache if it is enabled.
	 *
	 * @param wikiPages the wiki pages
	 */
	@Override
	public void cacheResult(List<WikiPage> wikiPages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (wikiPages.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (WikiPage wikiPage : wikiPages) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						wikiPage.getCtCollectionId())) {

				if (entityCache.getResult(
						WikiPageImpl.class, wikiPage.getPrimaryKey()) == null) {

					cacheResult(wikiPage);
				}
			}
		}
	}

	/**
	 * Clears the cache for all wiki pages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(WikiPageImpl.class);

		finderCache.clearCache(WikiPageImpl.class);
	}

	/**
	 * Clears the cache for the wiki page.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(WikiPage wikiPage) {
		entityCache.removeResult(WikiPageImpl.class, wikiPage);
	}

	@Override
	public void clearCache(List<WikiPage> wikiPages) {
		for (WikiPage wikiPage : wikiPages) {
			entityCache.removeResult(WikiPageImpl.class, wikiPage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(WikiPageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(WikiPageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		WikiPageModelImpl wikiPageModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					wikiPageModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				wikiPageModelImpl.getUuid(), wikiPageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, wikiPageModelImpl);

			args = new Object[] {
				wikiPageModelImpl.getResourcePrimKey(),
				wikiPageModelImpl.getNodeId(), wikiPageModelImpl.getVersion()
			};

			finderCache.putResult(
				_finderPathFetchByR_N_V, args, wikiPageModelImpl);

			args = new Object[] {
				wikiPageModelImpl.getGroupId(),
				wikiPageModelImpl.getExternalReferenceCode(),
				wikiPageModelImpl.getVersion()
			};

			finderCache.putResult(
				_finderPathFetchByG_ERC_V, args, wikiPageModelImpl);

			args = new Object[] {
				wikiPageModelImpl.getNodeId(), wikiPageModelImpl.getTitle(),
				wikiPageModelImpl.getVersion()
			};

			finderCache.putResult(
				_finderPathFetchByN_T_V, args, wikiPageModelImpl);
		}
	}

	/**
	 * Creates a new wiki page with the primary key. Does not add the wiki page to the database.
	 *
	 * @param pageId the primary key for the new wiki page
	 * @return the new wiki page
	 */
	@Override
	public WikiPage create(long pageId) {
		WikiPage wikiPage = new WikiPageImpl();

		wikiPage.setNew(true);
		wikiPage.setPrimaryKey(pageId);

		String uuid = PortalUUIDUtil.generate();

		wikiPage.setUuid(uuid);

		wikiPage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return wikiPage;
	}

	/**
	 * Removes the wiki page with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page that was removed
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage remove(long pageId) throws NoSuchPageException {
		return remove((Serializable)pageId);
	}

	/**
	 * Removes the wiki page with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the wiki page
	 * @return the wiki page that was removed
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage remove(Serializable primaryKey) throws NoSuchPageException {
		Session session = null;

		try {
			session = openSession();

			WikiPage wikiPage = (WikiPage)session.get(
				WikiPageImpl.class, primaryKey);

			if (wikiPage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(wikiPage);
		}
		catch (NoSuchPageException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected WikiPage removeImpl(WikiPage wikiPage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(wikiPage)) {
				wikiPage = (WikiPage)session.get(
					WikiPageImpl.class, wikiPage.getPrimaryKeyObj());
			}

			if ((wikiPage != null) && ctPersistenceHelper.isRemove(wikiPage)) {
				session.delete(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (wikiPage != null) {
			clearCache(wikiPage);
		}

		return wikiPage;
	}

	@Override
	public WikiPage updateImpl(WikiPage wikiPage) {
		boolean isNew = wikiPage.isNew();

		if (!(wikiPage instanceof WikiPageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(wikiPage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(wikiPage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in wikiPage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WikiPage implementation " +
					wikiPage.getClass());
		}

		WikiPageModelImpl wikiPageModelImpl = (WikiPageModelImpl)wikiPage;

		if (Validator.isNull(wikiPage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			wikiPage.setUuid(uuid);
		}

		if (Validator.isNull(wikiPage.getExternalReferenceCode())) {
			wikiPage.setExternalReferenceCode(wikiPage.getUuid());
		}
		else {
			if (!Objects.equals(
					wikiPageModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					wikiPage.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = wikiPage.getCompanyId();

					long groupId = wikiPage.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = wikiPage.getPrimaryKey();
					}

					try {
						wikiPage.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								WikiPage.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								wikiPage.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (wikiPage.getCreateDate() == null)) {
			if (serviceContext == null) {
				wikiPage.setCreateDate(date);
			}
			else {
				wikiPage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!wikiPageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				wikiPage.setModifiedDate(date);
			}
			else {
				wikiPage.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = wikiPage.getCompanyId();

			long groupId = wikiPage.getGroupId();

			long pageId = 0;

			if (!isNew) {
				pageId = wikiPage.getPrimaryKey();
			}

			try {
				wikiPage.setTitle(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, WikiPage.class.getName(),
						pageId, ContentTypes.TEXT_PLAIN, Sanitizer.MODE_ALL,
						wikiPage.getTitle(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(wikiPage)) {
				if (!isNew) {
					session.evict(
						WikiPageImpl.class, wikiPage.getPrimaryKeyObj());
				}

				session.save(wikiPage);
			}
			else {
				wikiPage = (WikiPage)session.merge(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			WikiPageImpl.class, wikiPageModelImpl, false, true);

		cacheUniqueFindersCache(wikiPageModelImpl);

		if (isNew) {
			wikiPage.setNew(false);
		}

		wikiPage.resetOriginalValues();

		return wikiPage;
	}

	/**
	 * Returns the wiki page with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the wiki page
	 * @return the wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPageException {

		WikiPage wikiPage = fetchByPrimaryKey(primaryKey);

		if (wikiPage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page with the primary key or throws a <code>NoSuchPageException</code> if it could not be found.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page
	 * @throws NoSuchPageException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage findByPrimaryKey(long pageId) throws NoSuchPageException {
		return findByPrimaryKey((Serializable)pageId);
	}

	/**
	 * Returns the wiki page with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the wiki page
	 * @return the wiki page, or <code>null</code> if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(WikiPage.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		WikiPage wikiPage = (WikiPage)entityCache.getResult(
			WikiPageImpl.class, primaryKey);

		if (wikiPage != null) {
			return wikiPage;
		}

		Session session = null;

		try {
			session = openSession();

			wikiPage = (WikiPage)session.get(WikiPageImpl.class, primaryKey);

			if (wikiPage != null) {
				cacheResult(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return wikiPage;
	}

	/**
	 * Returns the wiki page with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page, or <code>null</code> if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage fetchByPrimaryKey(long pageId) {
		return fetchByPrimaryKey((Serializable)pageId);
	}

	@Override
	public Map<Serializable, WikiPage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(WikiPage.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, WikiPage> map = new HashMap<Serializable, WikiPage>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			WikiPage wikiPage = fetchByPrimaryKey(primaryKey);

			if (wikiPage != null) {
				map.put(primaryKey, wikiPage);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						WikiPage.class, primaryKey)) {

				WikiPage wikiPage = (WikiPage)entityCache.getResult(
					WikiPageImpl.class, primaryKey);

				if (wikiPage == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, wikiPage);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (WikiPage wikiPage : (List<WikiPage>)query.list()) {
				map.put(wikiPage.getPrimaryKeyObj(), wikiPage);

				cacheResult(wikiPage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the wiki pages.
	 *
	 * @return the wiki pages
	 */
	@Override
	public List<WikiPage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the wiki pages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of wiki pages
	 */
	@Override
	public List<WikiPage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the wiki pages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of wiki pages
	 */
	@Override
	public List<WikiPage> findAll(
		int start, int end, OrderByComparator<WikiPage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the wiki pages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of wiki pages
	 */
	@Override
	public List<WikiPage> findAll(
		int start, int end, OrderByComparator<WikiPage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindAll;
					finderArgs = FINDER_ARGS_EMPTY;
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindAll;
				finderArgs = new Object[] {start, end, orderByComparator};
			}

			List<WikiPage> list = null;

			if (useFinderCache) {
				list = (List<WikiPage>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_WIKIPAGE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_WIKIPAGE;

					sql = sql.concat(WikiPageModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<WikiPage>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Removes all the wiki pages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (WikiPage wikiPage : findAll()) {
			remove(wikiPage);
		}
	}

	/**
	 * Returns the number of wiki pages.
	 *
	 * @return the number of wiki pages
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					WikiPage.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_WIKIPAGE);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "pageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WIKIPAGE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return WikiPageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WikiPage";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctMergeColumnNames.add("resourcePrimKey");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("externalReferenceCode");
		ctMergeColumnNames.add("nodeId");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("minorEdit");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("summary");
		ctMergeColumnNames.add("format");
		ctMergeColumnNames.add("head");
		ctMergeColumnNames.add("parentTitle");
		ctMergeColumnNames.add("redirectTitle");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("pageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"resourcePrimKey", "nodeId", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "externalReferenceCode", "version"});

		_uniqueIndexColumnNames.add(
			new String[] {"nodeId", "title", "version"});
	}

	/**
	 * Initializes the wiki page persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByResourcePrimKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByResourcePrimKey",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"resourcePrimKey"}, true);

		_finderPathWithoutPaginationFindByResourcePrimKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByResourcePrimKey",
			new String[] {Long.class.getName()},
			new String[] {"resourcePrimKey"}, true);

		_finderPathCountByResourcePrimKey = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByResourcePrimKey",
			new String[] {Long.class.getName()},
			new String[] {"resourcePrimKey"}, false);

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathWithPaginationFindByNodeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByNodeId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId"}, true);

		_finderPathWithoutPaginationFindByNodeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByNodeId",
			new String[] {Long.class.getName()}, new String[] {"nodeId"}, true);

		_finderPathCountByNodeId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByNodeId",
			new String[] {Long.class.getName()}, new String[] {"nodeId"},
			false);

		_finderPathWithPaginationFindByFormat = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFormat",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"format"}, true);

		_finderPathWithoutPaginationFindByFormat = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByFormat",
			new String[] {String.class.getName()}, new String[] {"format"},
			true);

		_finderPathCountByFormat = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByFormat",
			new String[] {String.class.getName()}, new String[] {"format"},
			false);

		_finderPathWithPaginationFindByR_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId"}, true);

		_finderPathWithoutPaginationFindByR_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"resourcePrimKey", "nodeId"}, true);

		_finderPathCountByR_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"resourcePrimKey", "nodeId"}, false);

		_finderPathWithPaginationFindByR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"resourcePrimKey", "status"}, true);

		_finderPathWithoutPaginationFindByR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"resourcePrimKey", "status"}, true);

		_finderPathCountByR_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"resourcePrimKey", "status"}, false);

		_finderPathWithPaginationFindByG_ERC = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ERC",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "externalReferenceCode"}, true);

		_finderPathWithoutPaginationFindByG_ERC = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "externalReferenceCode"}, true);

		_finderPathCountByG_ERC = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ERC",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "externalReferenceCode"}, false);

		_finderPathWithPaginationFindByN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "title"}, true);

		_finderPathWithoutPaginationFindByN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "title"}, true);

		_finderPathCountByN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "title"}, false);

		_finderPathWithPaginationFindByN_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head"}, true);

		_finderPathWithoutPaginationFindByN_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"nodeId", "head"}, true);

		_finderPathCountByN_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"nodeId", "head"}, false);

		_finderPathWithPaginationFindByN_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "parentTitle"}, true);

		_finderPathWithoutPaginationFindByN_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_P",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "parentTitle"}, true);

		_finderPathCountByN_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_P",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "parentTitle"}, false);

		_finderPathWithPaginationFindByN_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "redirectTitle"}, true);

		_finderPathWithoutPaginationFindByN_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "redirectTitle"}, true);

		_finderPathCountByN_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"nodeId", "redirectTitle"}, false);

		_finderPathWithPaginationFindByN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "status"}, true);

		_finderPathWithoutPaginationFindByN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"nodeId", "status"}, true);

		_finderPathCountByN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"nodeId", "status"}, false);

		_finderPathFetchByR_N_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByR_N_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Double.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "version"}, true);

		_finderPathWithPaginationFindByR_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "head"}, true);

		_finderPathWithoutPaginationFindByR_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "head"}, true);

		_finderPathCountByR_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "head"}, false);

		_finderPathWithPaginationFindByR_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "status"}, true);

		_finderPathWithoutPaginationFindByR_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "status"}, true);

		_finderPathCountByR_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"resourcePrimKey", "nodeId", "status"}, false);

		_finderPathFetchByG_ERC_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_ERC_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Double.class.getName()
			},
			new String[] {"groupId", "externalReferenceCode", "version"}, true);

		_finderPathWithPaginationFindByG_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "nodeId", "head"}, true);

		_finderPathWithoutPaginationFindByG_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "nodeId", "head"}, true);

		_finderPathCountByG_N_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "nodeId", "head"}, false);

		_finderPathWithPaginationFindByG_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "nodeId", "status"}, true);

		_finderPathWithoutPaginationFindByG_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "status"}, true);

		_finderPathCountByG_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "status"}, false);

		_finderPathWithPaginationFindByU_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId", "nodeId", "status"}, true);

		_finderPathWithoutPaginationFindByU_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "nodeId", "status"}, true);

		_finderPathCountByU_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"userId", "nodeId", "status"}, false);

		_finderPathFetchByN_T_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByN_T_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Double.class.getName()
			},
			new String[] {"nodeId", "title", "version"}, true);

		_finderPathWithPaginationFindByN_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T_H",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "title", "head"}, true);

		_finderPathWithoutPaginationFindByN_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T_H",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"nodeId", "title", "head"}, true);

		_finderPathCountByN_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T_H",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"nodeId", "title", "head"}, false);

		_finderPathWithPaginationFindByN_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_T_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "title", "status"}, true);

		_finderPathWithoutPaginationFindByN_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_T_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"nodeId", "title", "status"}, true);

		_finderPathCountByN_T_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_T_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"nodeId", "title", "status"}, false);

		_finderPathWithPaginationFindByN_H_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle"}, true);

		_finderPathWithoutPaginationFindByN_H_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle"}, true);

		_finderPathCountByN_H_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle"}, false);

		_finderPathWithPaginationFindByN_H_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle"}, true);

		_finderPathWithoutPaginationFindByN_H_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_R",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle"}, true);

		_finderPathCountByN_H_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_R",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle"}, false);

		_finderPathWithPaginationFindByN_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "status"}, true);

		_finderPathWithoutPaginationFindByN_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"nodeId", "head", "status"}, true);

		_finderPathCountByN_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"nodeId", "head", "status"}, false);

		_finderPathWithPaginationFindByN_H_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "status"}, true);

		_finderPathWithPaginationCountByN_H_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"nodeId", "head", "status"}, false);

		_finderPathWithPaginationFindByG_U_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "nodeId", "status"}, true);

		_finderPathWithoutPaginationFindByG_U_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "userId", "nodeId", "status"}, true);

		_finderPathCountByG_U_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_N_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "userId", "nodeId", "status"}, false);

		_finderPathWithPaginationFindByG_N_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "nodeId", "title", "head"}, true);

		_finderPathWithoutPaginationFindByG_N_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "nodeId", "title", "head"}, true);

		_finderPathCountByG_N_T_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_T_H",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "nodeId", "title", "head"}, false);

		_finderPathWithPaginationFindByG_N_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "status"}, true);

		_finderPathWithoutPaginationFindByG_N_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "status"}, true);

		_finderPathCountByG_N_H_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_H_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "status"}, false);

		_finderPathWithPaginationFindByN_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle", "status"}, true);

		_finderPathWithoutPaginationFindByN_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle", "status"}, true);

		_finderPathCountByN_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle", "status"}, false);

		_finderPathWithPaginationFindByN_H_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_P_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle", "status"}, true);

		_finderPathWithPaginationCountByN_H_P_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_P_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "parentTitle", "status"}, false);

		_finderPathWithPaginationFindByN_H_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle", "status"}, true);

		_finderPathWithoutPaginationFindByN_H_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByN_H_R_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle", "status"}, true);

		_finderPathCountByN_H_R_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByN_H_R_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle", "status"}, false);

		_finderPathWithPaginationFindByN_H_R_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByN_H_R_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle", "status"}, true);

		_finderPathWithPaginationCountByN_H_R_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByN_H_R_NotS",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {"nodeId", "head", "redirectTitle", "status"}, false);

		_finderPathWithPaginationFindByG_N_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_H_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "parentTitle", "status"},
			true);

		_finderPathWithoutPaginationFindByG_N_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_H_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "parentTitle", "status"},
			true);

		_finderPathCountByG_N_H_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_H_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "nodeId", "head", "parentTitle", "status"},
			false);

		WikiPageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		WikiPageUtil.setPersistence(null);

		entityCache.removeCache(WikiPageImpl.class.getName());
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = WikiPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_WIKIPAGE =
		"SELECT wikiPage FROM WikiPage wikiPage";

	private static final String _SQL_SELECT_WIKIPAGE_WHERE =
		"SELECT wikiPage FROM WikiPage wikiPage WHERE ";

	private static final String _SQL_COUNT_WIKIPAGE =
		"SELECT COUNT(wikiPage) FROM WikiPage wikiPage";

	private static final String _SQL_COUNT_WIKIPAGE_WHERE =
		"SELECT COUNT(wikiPage) FROM WikiPage wikiPage WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"wikiPage.resourcePrimKey";

	private static final String _FILTER_SQL_SELECT_WIKIPAGE_WHERE =
		"SELECT DISTINCT {wikiPage.*} FROM WikiPage wikiPage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {WikiPage.*} FROM (SELECT DISTINCT wikiPage.pageId FROM WikiPage wikiPage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_WIKIPAGE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN WikiPage ON TEMP_TABLE.pageId = WikiPage.pageId";

	private static final String _FILTER_SQL_COUNT_WIKIPAGE_WHERE =
		"SELECT COUNT(DISTINCT wikiPage.pageId) AS COUNT_VALUE FROM WikiPage wikiPage WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "wikiPage";

	private static final String _FILTER_ENTITY_TABLE = "WikiPage";

	private static final String _ORDER_BY_ENTITY_ALIAS = "wikiPage.";

	private static final String _ORDER_BY_ENTITY_TABLE = "WikiPage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No WikiPage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WikiPage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}