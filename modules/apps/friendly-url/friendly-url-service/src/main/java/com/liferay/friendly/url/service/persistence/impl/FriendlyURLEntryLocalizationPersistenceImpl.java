/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence.impl;

import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationTable;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationUtil;
import com.liferay.friendly.url.service.persistence.impl.constants.FURLPersistenceConstants;
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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
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
 * The persistence implementation for the friendly url entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FriendlyURLEntryLocalizationPersistence.class)
public class FriendlyURLEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl<FriendlyURLEntryLocalization>
	implements FriendlyURLEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FriendlyURLEntryLocalizationUtil</code> to access the friendly url entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FriendlyURLEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByFriendlyURLEntryId;
	private FinderPath _finderPathWithoutPaginationFindByFriendlyURLEntryId;
	private FinderPath _finderPathCountByFriendlyURLEntryId;

	/**
	 * Returns all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId) {

		return findByFriendlyURLEntryId(
			friendlyURLEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end) {

		return findByFriendlyURLEntryId(friendlyURLEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByFriendlyURLEntryId;
					finderArgs = new Object[] {friendlyURLEntryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByFriendlyURLEntryId;
				finderArgs = new Object[] {
					friendlyURLEntryId, start, end, orderByComparator
				};
			}

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (FriendlyURLEntryLocalization
							friendlyURLEntryLocalization : list) {

						if (friendlyURLEntryId !=
								friendlyURLEntryLocalization.
									getFriendlyURLEntryId()) {

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

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(
					_FINDER_COLUMN_FRIENDLYURLENTRYID_FRIENDLYURLENTRYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(friendlyURLEntryId);

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_First(
			long friendlyURLEntryId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByFriendlyURLEntryId_First(
				friendlyURLEntryId, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("friendlyURLEntryId=");
		sb.append(friendlyURLEntryId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_First(
		long friendlyURLEntryId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		List<FriendlyURLEntryLocalization> list = findByFriendlyURLEntryId(
			friendlyURLEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_Last(
			long friendlyURLEntryId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByFriendlyURLEntryId_Last(
				friendlyURLEntryId, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("friendlyURLEntryId=");
		sb.append(friendlyURLEntryId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_Last(
		long friendlyURLEntryId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		int count = countByFriendlyURLEntryId(friendlyURLEntryId);

		if (count == 0) {
			return null;
		}

		List<FriendlyURLEntryLocalization> list = findByFriendlyURLEntryId(
			friendlyURLEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the friendly url entry localizations before and after the current friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the current friendly url entry localization
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization[] findByFriendlyURLEntryId_PrevAndNext(
			long friendlyURLEntryLocalizationId, long friendlyURLEntryId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByPrimaryKey(friendlyURLEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization[] array =
				new FriendlyURLEntryLocalizationImpl[3];

			array[0] = getByFriendlyURLEntryId_PrevAndNext(
				session, friendlyURLEntryLocalization, friendlyURLEntryId,
				orderByComparator, true);

			array[1] = friendlyURLEntryLocalization;

			array[2] = getByFriendlyURLEntryId_PrevAndNext(
				session, friendlyURLEntryLocalization, friendlyURLEntryId,
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

	protected FriendlyURLEntryLocalization getByFriendlyURLEntryId_PrevAndNext(
		Session session,
		FriendlyURLEntryLocalization friendlyURLEntryLocalization,
		long friendlyURLEntryId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_FRIENDLYURLENTRYID_FRIENDLYURLENTRYID_2);

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
			sb.append(FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(friendlyURLEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						friendlyURLEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FriendlyURLEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the friendly url entry localizations where friendlyURLEntryId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 */
	@Override
	public void removeByFriendlyURLEntryId(long friendlyURLEntryId) {
		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findByFriendlyURLEntryId(
					friendlyURLEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByFriendlyURLEntryId(long friendlyURLEntryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			FinderPath finderPath = _finderPathCountByFriendlyURLEntryId;

			Object[] finderArgs = new Object[] {friendlyURLEntryId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(
					_FINDER_COLUMN_FRIENDLYURLENTRYID_FRIENDLYURLENTRYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(friendlyURLEntryId);

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
		_FINDER_COLUMN_FRIENDLYURLENTRYID_FRIENDLYURLENTRYID_2 =
			"friendlyURLEntryLocalization.friendlyURLEntryId = ?";

	private FinderPath _finderPathFetchByFriendlyURLEntryId_LanguageId;

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByFriendlyURLEntryId_LanguageId(
				friendlyURLEntryId, languageId);

		if (friendlyURLEntryLocalization == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("friendlyURLEntryId=");
			sb.append(friendlyURLEntryId);

			sb.append(", languageId=");
			sb.append(languageId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFriendlyURLEntryLocalizationException(
				sb.toString());
		}

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId) {

		return fetchByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId, true);
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {friendlyURLEntryId, languageId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByFriendlyURLEntryId_LanguageId, finderArgs,
					this);
			}

			if (result instanceof FriendlyURLEntryLocalization) {
				FriendlyURLEntryLocalization friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)result;

				if ((friendlyURLEntryId !=
						friendlyURLEntryLocalization.getFriendlyURLEntryId()) ||
					!Objects.equals(
						languageId,
						friendlyURLEntryLocalization.getLanguageId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(
					_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_FRIENDLYURLENTRYID_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(
						_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_LANGUAGEID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(friendlyURLEntryId);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					List<FriendlyURLEntryLocalization> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByFriendlyURLEntryId_LanguageId,
								finderArgs, list);
						}
					}
					else {
						FriendlyURLEntryLocalization
							friendlyURLEntryLocalization = list.get(0);

						result = friendlyURLEntryLocalization;

						cacheResult(friendlyURLEntryLocalization);
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
				return (FriendlyURLEntryLocalization)result;
			}
		}
	}

	/**
	 * Removes the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the friendly url entry localization that was removed
	 */
	@Override
	public FriendlyURLEntryLocalization removeByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByFriendlyURLEntryId_LanguageId(friendlyURLEntryId, languageId);

		return remove(friendlyURLEntryLocalization);
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63; and languageId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByFriendlyURLEntryId_LanguageId(
				friendlyURLEntryId, languageId);

		if (friendlyURLEntryLocalization == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_FRIENDLYURLENTRYID_2 =
			"friendlyURLEntryLocalization.friendlyURLEntryId = ? AND ";

	private static final String
		_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_LANGUAGEID_2 =
			"friendlyURLEntryLocalization.languageId = ?";

	private static final String
		_FINDER_COLUMN_FRIENDLYURLENTRYID_LANGUAGEID_LANGUAGEID_3 =
			"(friendlyURLEntryLocalization.languageId IS NULL OR friendlyURLEntryLocalization.languageId = '')";

	private FinderPath _finderPathWithPaginationFindByG_C_U;
	private FinderPath _finderPathWithoutPaginationFindByG_C_U;
	private FinderPath _finderPathCountByG_C_U;

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle) {

		return findByG_C_U(
			groupId, classNameId, urlTitle, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end) {

		return findByG_C_U(groupId, classNameId, urlTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_U(
			groupId, classNameId, urlTitle, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_U;
					finderArgs = new Object[] {groupId, classNameId, urlTitle};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_U;
				finderArgs = new Object[] {
					groupId, classNameId, urlTitle, start, end,
					orderByComparator
				};
			}

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (FriendlyURLEntryLocalization
							friendlyURLEntryLocalization : list) {

						if ((groupId !=
								friendlyURLEntryLocalization.getGroupId()) ||
							(classNameId !=
								friendlyURLEntryLocalization.
									getClassNameId()) ||
							!urlTitle.equals(
								friendlyURLEntryLocalization.getUrlTitle())) {

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

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_U_CLASSNAMEID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_U_First(
			long groupId, long classNameId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_U_First(
				groupId, classNameId, urlTitle, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_U_First(
		long groupId, long classNameId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		List<FriendlyURLEntryLocalization> list = findByG_C_U(
			groupId, classNameId, urlTitle, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_U_Last(
			long groupId, long classNameId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_U_Last(
				groupId, classNameId, urlTitle, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_U_Last(
		long groupId, long classNameId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		int count = countByG_C_U(groupId, classNameId, urlTitle);

		if (count == 0) {
			return null;
		}

		List<FriendlyURLEntryLocalization> list = findByG_C_U(
			groupId, classNameId, urlTitle, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the friendly url entry localizations before and after the current friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the current friendly url entry localization
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization[] findByG_C_U_PrevAndNext(
			long friendlyURLEntryLocalizationId, long groupId, long classNameId,
			String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		urlTitle = Objects.toString(urlTitle, "");

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByPrimaryKey(friendlyURLEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization[] array =
				new FriendlyURLEntryLocalizationImpl[3];

			array[0] = getByG_C_U_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				urlTitle, orderByComparator, true);

			array[1] = friendlyURLEntryLocalization;

			array[2] = getByG_C_U_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				urlTitle, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FriendlyURLEntryLocalization getByG_C_U_PrevAndNext(
		Session session,
		FriendlyURLEntryLocalization friendlyURLEntryLocalization, long groupId,
		long classNameId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_G_C_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_U_CLASSNAMEID_2);

		boolean bindUrlTitle = false;

		if (urlTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_3);
		}
		else {
			bindUrlTitle = true;

			sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_2);
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
			sb.append(FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (bindUrlTitle) {
			queryPos.add(urlTitle);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						friendlyURLEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FriendlyURLEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 */
	@Override
	public void removeByG_C_U(long groupId, long classNameId, String urlTitle) {
		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findByG_C_U(
					groupId, classNameId, urlTitle, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByG_C_U(long groupId, long classNameId, String urlTitle) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = _finderPathCountByG_C_U;

			Object[] finderArgs = new Object[] {groupId, classNameId, urlTitle};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_U_CLASSNAMEID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_C_U_URLTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
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

	private static final String _FINDER_COLUMN_G_C_U_GROUPID_2 =
		"friendlyURLEntryLocalization.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_U_CLASSNAMEID_2 =
		"friendlyURLEntryLocalization.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_U_URLTITLE_2 =
		"friendlyURLEntryLocalization.urlTitle = ?";

	private static final String _FINDER_COLUMN_G_C_U_URLTITLE_3 =
		"(friendlyURLEntryLocalization.urlTitle IS NULL OR friendlyURLEntryLocalization.urlTitle = '')";

	private FinderPath _finderPathWithPaginationFindByC_C_U_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C_U_C;
	private FinderPath _finderPathCountByC_C_U_C;

	/**
	 * Returns all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @return the matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C_U_C;
					finderArgs = new Object[] {
						companyId, classNameId, urlTitle, ctCollectionId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C_U_C;
				finderArgs = new Object[] {
					companyId, classNameId, urlTitle, ctCollectionId, start,
					end, orderByComparator
				};
			}

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (FriendlyURLEntryLocalization
							friendlyURLEntryLocalization : list) {

						if ((companyId !=
								friendlyURLEntryLocalization.getCompanyId()) ||
							(classNameId !=
								friendlyURLEntryLocalization.
									getClassNameId()) ||
							!urlTitle.equals(
								friendlyURLEntryLocalization.getUrlTitle()) ||
							(ctCollectionId !=
								friendlyURLEntryLocalization.
									getCtCollectionId())) {

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

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_C_C_U_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_U_C_CLASSNAMEID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_2);
				}

				sb.append(_FINDER_COLUMN_C_C_U_C_CTCOLLECTIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					queryPos.add(ctCollectionId);

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Returns the first friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByC_C_U_C_First(
			long companyId, long classNameId, String urlTitle,
			long ctCollectionId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByC_C_U_C_First(
				companyId, classNameId, urlTitle, ctCollectionId,
				orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByC_C_U_C_First(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		List<FriendlyURLEntryLocalization> list = findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByC_C_U_C_Last(
			long companyId, long classNameId, String urlTitle,
			long ctCollectionId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByC_C_U_C_Last(
				companyId, classNameId, urlTitle, ctCollectionId,
				orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByC_C_U_C_Last(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		int count = countByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId);

		if (count == 0) {
			return null;
		}

		List<FriendlyURLEntryLocalization> list = findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the friendly url entry localizations before and after the current friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the current friendly url entry localization
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization[] findByC_C_U_C_PrevAndNext(
			long friendlyURLEntryLocalizationId, long companyId,
			long classNameId, String urlTitle, long ctCollectionId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		urlTitle = Objects.toString(urlTitle, "");

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByPrimaryKey(friendlyURLEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization[] array =
				new FriendlyURLEntryLocalizationImpl[3];

			array[0] = getByC_C_U_C_PrevAndNext(
				session, friendlyURLEntryLocalization, companyId, classNameId,
				urlTitle, ctCollectionId, orderByComparator, true);

			array[1] = friendlyURLEntryLocalization;

			array[2] = getByC_C_U_C_PrevAndNext(
				session, friendlyURLEntryLocalization, companyId, classNameId,
				urlTitle, ctCollectionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FriendlyURLEntryLocalization getByC_C_U_C_PrevAndNext(
		Session session,
		FriendlyURLEntryLocalization friendlyURLEntryLocalization,
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_C_C_U_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_U_C_CLASSNAMEID_2);

		boolean bindUrlTitle = false;

		if (urlTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_3);
		}
		else {
			bindUrlTitle = true;

			sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_2);
		}

		sb.append(_FINDER_COLUMN_C_C_U_C_CTCOLLECTIONID_2);

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
			sb.append(FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (bindUrlTitle) {
			queryPos.add(urlTitle);
		}

		queryPos.add(ctCollectionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						friendlyURLEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FriendlyURLEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 */
	@Override
	public void removeByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findByC_C_U_C(
					companyId, classNameId, urlTitle, ctCollectionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = _finderPathCountByC_C_U_C;

			Object[] finderArgs = new Object[] {
				companyId, classNameId, urlTitle, ctCollectionId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_C_C_U_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_U_C_CLASSNAMEID_2);

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_C_C_U_C_URLTITLE_2);
				}

				sb.append(_FINDER_COLUMN_C_C_U_C_CTCOLLECTIONID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					queryPos.add(ctCollectionId);

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

	private static final String _FINDER_COLUMN_C_C_U_C_COMPANYID_2 =
		"friendlyURLEntryLocalization.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_U_C_CLASSNAMEID_2 =
		"friendlyURLEntryLocalization.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_U_C_URLTITLE_2 =
		"friendlyURLEntryLocalization.urlTitle = ? AND ";

	private static final String _FINDER_COLUMN_C_C_U_C_URLTITLE_3 =
		"(friendlyURLEntryLocalization.urlTitle IS NULL OR friendlyURLEntryLocalization.urlTitle = '') AND ";

	private static final String _FINDER_COLUMN_C_C_U_C_CTCOLLECTIONID_2 =
		"friendlyURLEntryLocalization.ctCollectionId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C_L;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C_L;
	private FinderPath _finderPathCountByG_C_C_L;

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @return the matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C_L;
					finderArgs = new Object[] {
						groupId, classNameId, classPK, languageId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C_L;
				finderArgs = new Object[] {
					groupId, classNameId, classPK, languageId, start, end,
					orderByComparator
				};
			}

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (FriendlyURLEntryLocalization
							friendlyURLEntryLocalization : list) {

						if ((groupId !=
								friendlyURLEntryLocalization.getGroupId()) ||
							(classNameId !=
								friendlyURLEntryLocalization.
									getClassNameId()) ||
							(classPK !=
								friendlyURLEntryLocalization.getClassPK()) ||
							!languageId.equals(
								friendlyURLEntryLocalization.getLanguageId())) {

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

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_L_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_L_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_L_CLASSPK_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_C_L_First(
			long groupId, long classNameId, long classPK, String languageId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_C_L_First(
				groupId, classNameId, classPK, languageId, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", languageId=");
		sb.append(languageId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_C_L_First(
		long groupId, long classNameId, long classPK, String languageId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		List<FriendlyURLEntryLocalization> list = findByG_C_C_L(
			groupId, classNameId, classPK, languageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_C_L_Last(
			long groupId, long classNameId, long classPK, String languageId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_C_L_Last(
				groupId, classNameId, classPK, languageId, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", languageId=");
		sb.append(languageId);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_C_L_Last(
		long groupId, long classNameId, long classPK, String languageId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		int count = countByG_C_C_L(groupId, classNameId, classPK, languageId);

		if (count == 0) {
			return null;
		}

		List<FriendlyURLEntryLocalization> list = findByG_C_C_L(
			groupId, classNameId, classPK, languageId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the friendly url entry localizations before and after the current friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the current friendly url entry localization
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization[] findByG_C_C_L_PrevAndNext(
			long friendlyURLEntryLocalizationId, long groupId, long classNameId,
			long classPK, String languageId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		languageId = Objects.toString(languageId, "");

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByPrimaryKey(friendlyURLEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization[] array =
				new FriendlyURLEntryLocalizationImpl[3];

			array[0] = getByG_C_C_L_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				classPK, languageId, orderByComparator, true);

			array[1] = friendlyURLEntryLocalization;

			array[2] = getByG_C_C_L_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				classPK, languageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FriendlyURLEntryLocalization getByG_C_C_L_PrevAndNext(
		Session session,
		FriendlyURLEntryLocalization friendlyURLEntryLocalization, long groupId,
		long classNameId, long classPK, String languageId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_L_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_L_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_L_CLASSPK_2);

		boolean bindLanguageId = false;

		if (languageId.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_3);
		}
		else {
			bindLanguageId = true;

			sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_2);
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
			sb.append(FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (bindLanguageId) {
			queryPos.add(languageId);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						friendlyURLEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FriendlyURLEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 */
	@Override
	public void removeByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findByG_C_C_L(
					groupId, classNameId, classPK, languageId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");

			FinderPath finderPath = _finderPathCountByG_C_C_L;

			Object[] finderArgs = new Object[] {
				groupId, classNameId, classPK, languageId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_L_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_L_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_L_CLASSPK_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(_FINDER_COLUMN_G_C_C_L_LANGUAGEID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindLanguageId) {
						queryPos.add(languageId);
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

	private static final String _FINDER_COLUMN_G_C_C_L_GROUPID_2 =
		"friendlyURLEntryLocalization.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_L_CLASSNAMEID_2 =
		"friendlyURLEntryLocalization.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_L_CLASSPK_2 =
		"friendlyURLEntryLocalization.classPK = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_L_LANGUAGEID_2 =
		"friendlyURLEntryLocalization.languageId = ?";

	private static final String _FINDER_COLUMN_G_C_C_L_LANGUAGEID_3 =
		"(friendlyURLEntryLocalization.languageId IS NULL OR friendlyURLEntryLocalization.languageId = '')";

	private FinderPath _finderPathFetchByG_C_L_U;

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and languageId = &#63; and urlTitle = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_L_U(
			long groupId, long classNameId, String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_L_U(groupId, classNameId, languageId, urlTitle);

		if (friendlyURLEntryLocalization == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", languageId=");
			sb.append(languageId);

			sb.append(", urlTitle=");
			sb.append(urlTitle);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFriendlyURLEntryLocalizationException(
				sb.toString());
		}

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and languageId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_L_U(
		long groupId, long classNameId, String languageId, String urlTitle) {

		return fetchByG_C_L_U(groupId, classNameId, languageId, urlTitle, true);
	}

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and languageId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_L_U(
		long groupId, long classNameId, String languageId, String urlTitle,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");
			urlTitle = Objects.toString(urlTitle, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, classNameId, languageId, urlTitle
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_C_L_U, finderArgs, this);
			}

			if (result instanceof FriendlyURLEntryLocalization) {
				FriendlyURLEntryLocalization friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)result;

				if ((groupId != friendlyURLEntryLocalization.getGroupId()) ||
					(classNameId !=
						friendlyURLEntryLocalization.getClassNameId()) ||
					!Objects.equals(
						languageId,
						friendlyURLEntryLocalization.getLanguageId()) ||
					!Objects.equals(
						urlTitle, friendlyURLEntryLocalization.getUrlTitle())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_L_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_L_U_CLASSNAMEID_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_L_U_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(_FINDER_COLUMN_G_C_L_U_LANGUAGEID_2);
				}

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_L_U_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_C_L_U_URLTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					List<FriendlyURLEntryLocalization> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_C_L_U, finderArgs, list);
						}
					}
					else {
						FriendlyURLEntryLocalization
							friendlyURLEntryLocalization = list.get(0);

						result = friendlyURLEntryLocalization;

						cacheResult(friendlyURLEntryLocalization);
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
				return (FriendlyURLEntryLocalization)result;
			}
		}
	}

	/**
	 * Removes the friendly url entry localization where groupId = &#63; and classNameId = &#63; and languageId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the friendly url entry localization that was removed
	 */
	@Override
	public FriendlyURLEntryLocalization removeByG_C_L_U(
			long groupId, long classNameId, String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByG_C_L_U(groupId, classNameId, languageId, urlTitle);

		return remove(friendlyURLEntryLocalization);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByG_C_L_U(
		long groupId, long classNameId, String languageId, String urlTitle) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_L_U(groupId, classNameId, languageId, urlTitle);

		if (friendlyURLEntryLocalization == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_L_U_GROUPID_2 =
		"friendlyURLEntryLocalization.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_L_U_CLASSNAMEID_2 =
		"friendlyURLEntryLocalization.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_L_U_LANGUAGEID_2 =
		"friendlyURLEntryLocalization.languageId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_L_U_LANGUAGEID_3 =
		"(friendlyURLEntryLocalization.languageId IS NULL OR friendlyURLEntryLocalization.languageId = '') AND ";

	private static final String _FINDER_COLUMN_G_C_L_U_URLTITLE_2 =
		"friendlyURLEntryLocalization.urlTitle = ?";

	private static final String _FINDER_COLUMN_G_C_L_U_URLTITLE_3 =
		"(friendlyURLEntryLocalization.urlTitle IS NULL OR friendlyURLEntryLocalization.urlTitle = '')";

	private FinderPath _finderPathWithPaginationFindByG_C_NotL_U;
	private FinderPath _finderPathWithPaginationCountByG_C_NotL_U;

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle) {

		return findByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle,
		int start, int end) {

		return findByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle, start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");
			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_C_NotL_U;
			finderArgs = new Object[] {
				groupId, classNameId, languageId, urlTitle, start, end,
				orderByComparator
			};

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (FriendlyURLEntryLocalization
							friendlyURLEntryLocalization : list) {

						if ((groupId !=
								friendlyURLEntryLocalization.getGroupId()) ||
							(classNameId !=
								friendlyURLEntryLocalization.
									getClassNameId()) ||
							languageId.equals(
								friendlyURLEntryLocalization.getLanguageId()) ||
							!urlTitle.equals(
								friendlyURLEntryLocalization.getUrlTitle())) {

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

				sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_NOTL_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_NOTL_U_CLASSNAMEID_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_2);
				}

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
					}

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_NotL_U_First(
			long groupId, long classNameId, String languageId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_NotL_U_First(
				groupId, classNameId, languageId, urlTitle, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", languageId!=");
		sb.append(languageId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_NotL_U_First(
		long groupId, long classNameId, String languageId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		List<FriendlyURLEntryLocalization> list = findByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByG_C_NotL_U_Last(
			long groupId, long classNameId, String languageId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByG_C_NotL_U_Last(
				groupId, classNameId, languageId, urlTitle, orderByComparator);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", languageId!=");
		sb.append(languageId);

		sb.append(", urlTitle=");
		sb.append(urlTitle);

		sb.append("}");

		throw new NoSuchFriendlyURLEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_NotL_U_Last(
		long groupId, long classNameId, String languageId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		int count = countByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle);

		if (count == 0) {
			return null;
		}

		List<FriendlyURLEntryLocalization> list = findByG_C_NotL_U(
			groupId, classNameId, languageId, urlTitle, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the friendly url entry localizations before and after the current friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the current friendly url entry localization
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization[] findByG_C_NotL_U_PrevAndNext(
			long friendlyURLEntryLocalizationId, long groupId, long classNameId,
			String languageId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		languageId = Objects.toString(languageId, "");
		urlTitle = Objects.toString(urlTitle, "");

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByPrimaryKey(friendlyURLEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization[] array =
				new FriendlyURLEntryLocalizationImpl[3];

			array[0] = getByG_C_NotL_U_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				languageId, urlTitle, orderByComparator, true);

			array[1] = friendlyURLEntryLocalization;

			array[2] = getByG_C_NotL_U_PrevAndNext(
				session, friendlyURLEntryLocalization, groupId, classNameId,
				languageId, urlTitle, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected FriendlyURLEntryLocalization getByG_C_NotL_U_PrevAndNext(
		Session session,
		FriendlyURLEntryLocalization friendlyURLEntryLocalization, long groupId,
		long classNameId, String languageId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_G_C_NOTL_U_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_NOTL_U_CLASSNAMEID_2);

		boolean bindLanguageId = false;

		if (languageId.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_3);
		}
		else {
			bindLanguageId = true;

			sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_2);
		}

		boolean bindUrlTitle = false;

		if (urlTitle.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_3);
		}
		else {
			bindUrlTitle = true;

			sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_2);
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
			sb.append(FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (bindLanguageId) {
			queryPos.add(languageId);
		}

		if (bindUrlTitle) {
			queryPos.add(urlTitle);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						friendlyURLEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<FriendlyURLEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 */
	@Override
	public void removeByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle) {

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findByG_C_NotL_U(
					groupId, classNameId, languageId, urlTitle,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByG_C_NotL_U(
		long groupId, long classNameId, String languageId, String urlTitle) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			languageId = Objects.toString(languageId, "");
			urlTitle = Objects.toString(urlTitle, "");

			FinderPath finderPath = _finderPathWithPaginationCountByG_C_NotL_U;

			Object[] finderArgs = new Object[] {
				groupId, classNameId, languageId, urlTitle
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_G_C_NOTL_U_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_NOTL_U_CLASSNAMEID_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(_FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_2);
				}

				boolean bindUrlTitle = false;

				if (urlTitle.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_3);
				}
				else {
					bindUrlTitle = true;

					sb.append(_FINDER_COLUMN_G_C_NOTL_U_URLTITLE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					if (bindUrlTitle) {
						queryPos.add(urlTitle);
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

	private static final String _FINDER_COLUMN_G_C_NOTL_U_GROUPID_2 =
		"friendlyURLEntryLocalization.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_NOTL_U_CLASSNAMEID_2 =
		"friendlyURLEntryLocalization.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_2 =
		"friendlyURLEntryLocalization.languageId != ? AND ";

	private static final String _FINDER_COLUMN_G_C_NOTL_U_LANGUAGEID_3 =
		"(friendlyURLEntryLocalization.languageId IS NULL OR friendlyURLEntryLocalization.languageId != '') AND ";

	private static final String _FINDER_COLUMN_G_C_NOTL_U_URLTITLE_2 =
		"friendlyURLEntryLocalization.urlTitle = ?";

	private static final String _FINDER_COLUMN_G_C_NOTL_U_URLTITLE_3 =
		"(friendlyURLEntryLocalization.urlTitle IS NULL OR friendlyURLEntryLocalization.urlTitle = '')";

	public FriendlyURLEntryLocalizationPersistenceImpl() {
		setModelClass(FriendlyURLEntryLocalization.class);

		setModelImplClass(FriendlyURLEntryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(FriendlyURLEntryLocalizationTable.INSTANCE);
	}

	/**
	 * Caches the friendly url entry localization in the entity cache if it is enabled.
	 *
	 * @param friendlyURLEntryLocalization the friendly url entry localization
	 */
	@Override
	public void cacheResult(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					friendlyURLEntryLocalization.getCtCollectionId())) {

			entityCache.putResult(
				FriendlyURLEntryLocalizationImpl.class,
				friendlyURLEntryLocalization.getPrimaryKey(),
				friendlyURLEntryLocalization);

			finderCache.putResult(
				_finderPathFetchByFriendlyURLEntryId_LanguageId,
				new Object[] {
					friendlyURLEntryLocalization.getFriendlyURLEntryId(),
					friendlyURLEntryLocalization.getLanguageId()
				},
				friendlyURLEntryLocalization);

			finderCache.putResult(
				_finderPathFetchByG_C_L_U,
				new Object[] {
					friendlyURLEntryLocalization.getGroupId(),
					friendlyURLEntryLocalization.getClassNameId(),
					friendlyURLEntryLocalization.getLanguageId(),
					friendlyURLEntryLocalization.getUrlTitle()
				},
				friendlyURLEntryLocalization);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the friendly url entry localizations in the entity cache if it is enabled.
	 *
	 * @param friendlyURLEntryLocalizations the friendly url entry localizations
	 */
	@Override
	public void cacheResult(
		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (friendlyURLEntryLocalizations.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizations) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						friendlyURLEntryLocalization.getCtCollectionId())) {

				if (entityCache.getResult(
						FriendlyURLEntryLocalizationImpl.class,
						friendlyURLEntryLocalization.getPrimaryKey()) == null) {

					cacheResult(friendlyURLEntryLocalization);
				}
			}
		}
	}

	/**
	 * Clears the cache for all friendly url entry localizations.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(FriendlyURLEntryLocalizationImpl.class);

		finderCache.clearCache(FriendlyURLEntryLocalizationImpl.class);
	}

	/**
	 * Clears the cache for the friendly url entry localization.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		entityCache.removeResult(
			FriendlyURLEntryLocalizationImpl.class,
			friendlyURLEntryLocalization);
	}

	@Override
	public void clearCache(
		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations) {

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizations) {

			entityCache.removeResult(
				FriendlyURLEntryLocalizationImpl.class,
				friendlyURLEntryLocalization);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FriendlyURLEntryLocalizationImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				FriendlyURLEntryLocalizationImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		FriendlyURLEntryLocalizationModelImpl
			friendlyURLEntryLocalizationModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					friendlyURLEntryLocalizationModelImpl.
						getCtCollectionId())) {

			Object[] args = new Object[] {
				friendlyURLEntryLocalizationModelImpl.getFriendlyURLEntryId(),
				friendlyURLEntryLocalizationModelImpl.getLanguageId()
			};

			finderCache.putResult(
				_finderPathFetchByFriendlyURLEntryId_LanguageId, args,
				friendlyURLEntryLocalizationModelImpl);

			args = new Object[] {
				friendlyURLEntryLocalizationModelImpl.getGroupId(),
				friendlyURLEntryLocalizationModelImpl.getClassNameId(),
				friendlyURLEntryLocalizationModelImpl.getLanguageId(),
				friendlyURLEntryLocalizationModelImpl.getUrlTitle()
			};

			finderCache.putResult(
				_finderPathFetchByG_C_L_U, args,
				friendlyURLEntryLocalizationModelImpl);
		}
	}

	/**
	 * Creates a new friendly url entry localization with the primary key. Does not add the friendly url entry localization to the database.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key for the new friendly url entry localization
	 * @return the new friendly url entry localization
	 */
	@Override
	public FriendlyURLEntryLocalization create(
		long friendlyURLEntryLocalizationId) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			new FriendlyURLEntryLocalizationImpl();

		friendlyURLEntryLocalization.setNew(true);
		friendlyURLEntryLocalization.setPrimaryKey(
			friendlyURLEntryLocalizationId);

		friendlyURLEntryLocalization.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return friendlyURLEntryLocalization;
	}

	/**
	 * Removes the friendly url entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization that was removed
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization remove(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return remove((Serializable)friendlyURLEntryLocalizationId);
	}

	/**
	 * Removes the friendly url entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the friendly url entry localization
	 * @return the friendly url entry localization that was removed
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization remove(Serializable primaryKey)
		throws NoSuchFriendlyURLEntryLocalizationException {

		Session session = null;

		try {
			session = openSession();

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				(FriendlyURLEntryLocalization)session.get(
					FriendlyURLEntryLocalizationImpl.class, primaryKey);

			if (friendlyURLEntryLocalization == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFriendlyURLEntryLocalizationException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(friendlyURLEntryLocalization);
		}
		catch (NoSuchFriendlyURLEntryLocalizationException
					noSuchEntityException) {

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
	protected FriendlyURLEntryLocalization removeImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(friendlyURLEntryLocalization)) {
				friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)session.get(
						FriendlyURLEntryLocalizationImpl.class,
						friendlyURLEntryLocalization.getPrimaryKeyObj());
			}

			if ((friendlyURLEntryLocalization != null) &&
				ctPersistenceHelper.isRemove(friendlyURLEntryLocalization)) {

				session.delete(friendlyURLEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (friendlyURLEntryLocalization != null) {
			clearCache(friendlyURLEntryLocalization);
		}

		return friendlyURLEntryLocalization;
	}

	@Override
	public FriendlyURLEntryLocalization updateImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		boolean isNew = friendlyURLEntryLocalization.isNew();

		if (!(friendlyURLEntryLocalization instanceof
				FriendlyURLEntryLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					friendlyURLEntryLocalization.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					friendlyURLEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in friendlyURLEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FriendlyURLEntryLocalization implementation " +
					friendlyURLEntryLocalization.getClass());
		}

		FriendlyURLEntryLocalizationModelImpl
			friendlyURLEntryLocalizationModelImpl =
				(FriendlyURLEntryLocalizationModelImpl)
					friendlyURLEntryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(friendlyURLEntryLocalization)) {
				if (!isNew) {
					session.evict(
						FriendlyURLEntryLocalizationImpl.class,
						friendlyURLEntryLocalization.getPrimaryKeyObj());
				}

				session.save(friendlyURLEntryLocalization);
			}
			else {
				friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)session.merge(
						friendlyURLEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			FriendlyURLEntryLocalizationImpl.class,
			friendlyURLEntryLocalizationModelImpl, false, true);

		cacheUniqueFindersCache(friendlyURLEntryLocalizationModelImpl);

		if (isNew) {
			friendlyURLEntryLocalization.setNew(false);
		}

		friendlyURLEntryLocalization.resetOriginalValues();

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the friendly url entry localization
	 * @return the friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByPrimaryKey(
			Serializable primaryKey)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			fetchByPrimaryKey(primaryKey);

		if (friendlyURLEntryLocalization == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFriendlyURLEntryLocalizationException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization with the primary key or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByPrimaryKey(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return findByPrimaryKey((Serializable)friendlyURLEntryLocalizationId);
	}

	/**
	 * Returns the friendly url entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the friendly url entry localization
	 * @return the friendly url entry localization, or <code>null</code> if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByPrimaryKey(
		Serializable primaryKey) {

		if (ctPersistenceHelper.isProductionMode(
				FriendlyURLEntryLocalization.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			(FriendlyURLEntryLocalization)entityCache.getResult(
				FriendlyURLEntryLocalizationImpl.class, primaryKey);

		if (friendlyURLEntryLocalization != null) {
			return friendlyURLEntryLocalization;
		}

		Session session = null;

		try {
			session = openSession();

			friendlyURLEntryLocalization =
				(FriendlyURLEntryLocalization)session.get(
					FriendlyURLEntryLocalizationImpl.class, primaryKey);

			if (friendlyURLEntryLocalization != null) {
				cacheResult(friendlyURLEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization, or <code>null</code> if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByPrimaryKey(
		long friendlyURLEntryLocalizationId) {

		return fetchByPrimaryKey((Serializable)friendlyURLEntryLocalizationId);
	}

	@Override
	public Map<Serializable, FriendlyURLEntryLocalization> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				FriendlyURLEntryLocalization.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, FriendlyURLEntryLocalization> map =
			new HashMap<Serializable, FriendlyURLEntryLocalization>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				fetchByPrimaryKey(primaryKey);

			if (friendlyURLEntryLocalization != null) {
				map.put(primaryKey, friendlyURLEntryLocalization);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						FriendlyURLEntryLocalization.class, primaryKey)) {

				FriendlyURLEntryLocalization friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)entityCache.getResult(
						FriendlyURLEntryLocalizationImpl.class, primaryKey);

				if (friendlyURLEntryLocalization == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, friendlyURLEntryLocalization);
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

			for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
					(List<FriendlyURLEntryLocalization>)query.list()) {

				map.put(
					friendlyURLEntryLocalization.getPrimaryKeyObj(),
					friendlyURLEntryLocalization);

				cacheResult(friendlyURLEntryLocalization);
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
	 * Returns all the friendly url entry localizations.
	 *
	 * @return the friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findAll(
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findAll(
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

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

			List<FriendlyURLEntryLocalization> list = null;

			if (useFinderCache) {
				list =
					(List<FriendlyURLEntryLocalization>)finderCache.getResult(
						finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION;

					sql = sql.concat(
						FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<FriendlyURLEntryLocalization>)QueryUtil.list(
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
	 * Removes all the friendly url entry localizations from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				findAll()) {

			remove(friendlyURLEntryLocalization);
		}
	}

	/**
	 * Returns the number of friendly url entry localizations.
	 *
	 * @return the number of friendly url entry localizations
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					FriendlyURLEntryLocalization.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION);

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "friendlyURLEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION;
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
		return FriendlyURLEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FriendlyURLEntryLocalization";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("friendlyURLEntryId");
		ctMergeColumnNames.add("languageId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("urlTitle");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("friendlyURLEntryLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"friendlyURLEntryId", "languageId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "languageId", "urlTitle"});
	}

	/**
	 * Initializes the friendly url entry localization persistence.
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

		_finderPathWithPaginationFindByFriendlyURLEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFriendlyURLEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"friendlyURLEntryId"}, true);

		_finderPathWithoutPaginationFindByFriendlyURLEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByFriendlyURLEntryId", new String[] {Long.class.getName()},
			new String[] {"friendlyURLEntryId"}, true);

		_finderPathCountByFriendlyURLEntryId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByFriendlyURLEntryId", new String[] {Long.class.getName()},
			new String[] {"friendlyURLEntryId"}, false);

		_finderPathFetchByFriendlyURLEntryId_LanguageId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByFriendlyURLEntryId_LanguageId",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"friendlyURLEntryId", "languageId"}, true);

		_finderPathWithPaginationFindByG_C_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "urlTitle"}, true);

		_finderPathWithoutPaginationFindByG_C_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "classNameId", "urlTitle"}, true);

		_finderPathCountByG_C_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "classNameId", "urlTitle"}, false);

		_finderPathWithPaginationFindByC_C_U_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_U_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "urlTitle", "ctCollectionId"
			},
			true);

		_finderPathWithoutPaginationFindByC_C_U_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_U_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "urlTitle", "ctCollectionId"
			},
			true);

		_finderPathCountByC_C_U_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_U_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "urlTitle", "ctCollectionId"
			},
			false);

		_finderPathWithPaginationFindByG_C_C_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_L",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "languageId"},
			true);

		_finderPathWithoutPaginationFindByG_C_C_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_L",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "languageId"},
			true);

		_finderPathCountByG_C_C_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_L",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "classPK", "languageId"},
			false);

		_finderPathFetchByG_C_L_U = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_L_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "languageId", "urlTitle"},
			true);

		_finderPathWithPaginationFindByG_C_NotL_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_NotL_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId", "languageId", "urlTitle"},
			true);

		_finderPathWithPaginationCountByG_C_NotL_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_NotL_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"groupId", "classNameId", "languageId", "urlTitle"},
			false);

		FriendlyURLEntryLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FriendlyURLEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(
			FriendlyURLEntryLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION =
		"SELECT friendlyURLEntryLocalization FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization";

	private static final String _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE =
		"SELECT friendlyURLEntryLocalization FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization WHERE ";

	private static final String _SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION =
		"SELECT COUNT(friendlyURLEntryLocalization) FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization";

	private static final String _SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(friendlyURLEntryLocalization) FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"friendlyURLEntryLocalization.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No FriendlyURLEntryLocalization exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FriendlyURLEntryLocalization exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLEntryLocalizationPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}