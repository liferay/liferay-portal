/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchLocalizedEntryLocalizationException;
import com.liferay.portal.tools.service.builder.test.compat730.model.LocalizedEntryLocalization;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LocalizedEntryLocalizationImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.LocalizedEntryLocalizationModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LocalizedEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.LocalizedEntryLocalizationUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the localized entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LocalizedEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl<LocalizedEntryLocalization>
	implements LocalizedEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LocalizedEntryLocalizationUtil</code> to access the localized entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LocalizedEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByLocalizedEntryId;
	private FinderPath _finderPathWithoutPaginationFindByLocalizedEntryId;
	private FinderPath _finderPathCountByLocalizedEntryId;

	/**
	 * Returns all the localized entry localizations where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @return the matching localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findByLocalizedEntryId(
		long localizedEntryId) {

		return findByLocalizedEntryId(
			localizedEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the localized entry localizations where localizedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @return the range of matching localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findByLocalizedEntryId(
		long localizedEntryId, int start, int end) {

		return findByLocalizedEntryId(localizedEntryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the localized entry localizations where localizedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findByLocalizedEntryId(
		long localizedEntryId, int start, int end,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator) {

		return findByLocalizedEntryId(
			localizedEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the localized entry localizations where localizedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findByLocalizedEntryId(
		long localizedEntryId, int start, int end,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByLocalizedEntryId;
				finderArgs = new Object[] {localizedEntryId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByLocalizedEntryId;
			finderArgs = new Object[] {
				localizedEntryId, start, end, orderByComparator
			};
		}

		List<LocalizedEntryLocalization> list = null;

		if (useFinderCache) {
			list = (List<LocalizedEntryLocalization>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (LocalizedEntryLocalization localizedEntryLocalization :
						list) {

					if (localizedEntryId !=
							localizedEntryLocalization.getLocalizedEntryId()) {

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

			sb.append(_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE);

			sb.append(_FINDER_COLUMN_LOCALIZEDENTRYID_LOCALIZEDENTRYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(LocalizedEntryLocalizationModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(localizedEntryId);

				list = (List<LocalizedEntryLocalization>)QueryUtil.list(
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

	/**
	 * Returns the first localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByLocalizedEntryId_First(
			long localizedEntryId,
			OrderByComparator<LocalizedEntryLocalization> orderByComparator)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			fetchByLocalizedEntryId_First(localizedEntryId, orderByComparator);

		if (localizedEntryLocalization != null) {
			return localizedEntryLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("localizedEntryId=");
		sb.append(localizedEntryId);

		sb.append("}");

		throw new NoSuchLocalizedEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the first localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_First(
		long localizedEntryId,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator) {

		List<LocalizedEntryLocalization> list = findByLocalizedEntryId(
			localizedEntryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByLocalizedEntryId_Last(
			long localizedEntryId,
			OrderByComparator<LocalizedEntryLocalization> orderByComparator)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			fetchByLocalizedEntryId_Last(localizedEntryId, orderByComparator);

		if (localizedEntryLocalization != null) {
			return localizedEntryLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("localizedEntryId=");
		sb.append(localizedEntryId);

		sb.append("}");

		throw new NoSuchLocalizedEntryLocalizationException(sb.toString());
	}

	/**
	 * Returns the last localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_Last(
		long localizedEntryId,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator) {

		int count = countByLocalizedEntryId(localizedEntryId);

		if (count == 0) {
			return null;
		}

		List<LocalizedEntryLocalization> list = findByLocalizedEntryId(
			localizedEntryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the localized entry localizations before and after the current localized entry localization in the ordered set where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryLocalizationId the primary key of the current localized entry localization
	 * @param localizedEntryId the localized entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization[] findByLocalizedEntryId_PrevAndNext(
			long localizedEntryLocalizationId, long localizedEntryId,
			OrderByComparator<LocalizedEntryLocalization> orderByComparator)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			findByPrimaryKey(localizedEntryLocalizationId);

		Session session = null;

		try {
			session = openSession();

			LocalizedEntryLocalization[] array =
				new LocalizedEntryLocalizationImpl[3];

			array[0] = getByLocalizedEntryId_PrevAndNext(
				session, localizedEntryLocalization, localizedEntryId,
				orderByComparator, true);

			array[1] = localizedEntryLocalization;

			array[2] = getByLocalizedEntryId_PrevAndNext(
				session, localizedEntryLocalization, localizedEntryId,
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

	protected LocalizedEntryLocalization getByLocalizedEntryId_PrevAndNext(
		Session session, LocalizedEntryLocalization localizedEntryLocalization,
		long localizedEntryId,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_LOCALIZEDENTRYID_LOCALIZEDENTRYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(LocalizedEntryLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(localizedEntryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						localizedEntryLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LocalizedEntryLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the localized entry localizations where localizedEntryId = &#63; from the database.
	 *
	 * @param localizedEntryId the localized entry ID
	 */
	@Override
	public void removeByLocalizedEntryId(long localizedEntryId) {
		for (LocalizedEntryLocalization localizedEntryLocalization :
				findByLocalizedEntryId(
					localizedEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(localizedEntryLocalization);
		}
	}

	/**
	 * Returns the number of localized entry localizations where localizedEntryId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @return the number of matching localized entry localizations
	 */
	@Override
	public int countByLocalizedEntryId(long localizedEntryId) {
		FinderPath finderPath = _finderPathCountByLocalizedEntryId;

		Object[] finderArgs = new Object[] {localizedEntryId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_LOCALIZEDENTRYLOCALIZATION_WHERE);

			sb.append(_FINDER_COLUMN_LOCALIZEDENTRYID_LOCALIZEDENTRYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(localizedEntryId);

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

	private static final String
		_FINDER_COLUMN_LOCALIZEDENTRYID_LOCALIZEDENTRYID_2 =
			"localizedEntryLocalization.localizedEntryId = ?";

	private FinderPath _finderPathFetchByLocalizedEntryId_LanguageId;
	private FinderPath _finderPathCountByLocalizedEntryId_LanguageId;

	/**
	 * Returns the localized entry localization where localizedEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchLocalizedEntryLocalizationException</code> if it could not be found.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the matching localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByLocalizedEntryId_LanguageId(
			long localizedEntryId, String languageId)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			fetchByLocalizedEntryId_LanguageId(localizedEntryId, languageId);

		if (localizedEntryLocalization == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("localizedEntryId=");
			sb.append(localizedEntryId);

			sb.append(", languageId=");
			sb.append(languageId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLocalizedEntryLocalizationException(sb.toString());
		}

		return localizedEntryLocalization;
	}

	/**
	 * Returns the localized entry localization where localizedEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_LanguageId(
		long localizedEntryId, String languageId) {

		return fetchByLocalizedEntryId_LanguageId(
			localizedEntryId, languageId, true);
	}

	/**
	 * Returns the localized entry localization where localizedEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching localized entry localization, or <code>null</code> if a matching localized entry localization could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByLocalizedEntryId_LanguageId(
		long localizedEntryId, String languageId, boolean useFinderCache) {

		languageId = Objects.toString(languageId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {localizedEntryId, languageId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByLocalizedEntryId_LanguageId, finderArgs,
				this);
		}

		if (result instanceof LocalizedEntryLocalization) {
			LocalizedEntryLocalization localizedEntryLocalization =
				(LocalizedEntryLocalization)result;

			if ((localizedEntryId !=
					localizedEntryLocalization.getLocalizedEntryId()) ||
				!Objects.equals(
					languageId, localizedEntryLocalization.getLanguageId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE);

			sb.append(
				_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LOCALIZEDENTRYID_2);

			boolean bindLanguageId = false;

			if (languageId.isEmpty()) {
				sb.append(
					_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_3);
			}
			else {
				bindLanguageId = true;

				sb.append(
					_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(localizedEntryId);

				if (bindLanguageId) {
					queryPos.add(languageId);
				}

				List<LocalizedEntryLocalization> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByLocalizedEntryId_LanguageId,
							finderArgs, list);
					}
				}
				else {
					LocalizedEntryLocalization localizedEntryLocalization =
						list.get(0);

					result = localizedEntryLocalization;

					cacheResult(localizedEntryLocalization);
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
			return (LocalizedEntryLocalization)result;
		}
	}

	/**
	 * Removes the localized entry localization where localizedEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the localized entry localization that was removed
	 */
	@Override
	public LocalizedEntryLocalization removeByLocalizedEntryId_LanguageId(
			long localizedEntryId, String languageId)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			findByLocalizedEntryId_LanguageId(localizedEntryId, languageId);

		return remove(localizedEntryLocalization);
	}

	/**
	 * Returns the number of localized entry localizations where localizedEntryId = &#63; and languageId = &#63;.
	 *
	 * @param localizedEntryId the localized entry ID
	 * @param languageId the language ID
	 * @return the number of matching localized entry localizations
	 */
	@Override
	public int countByLocalizedEntryId_LanguageId(
		long localizedEntryId, String languageId) {

		languageId = Objects.toString(languageId, "");

		FinderPath finderPath = _finderPathCountByLocalizedEntryId_LanguageId;

		Object[] finderArgs = new Object[] {localizedEntryId, languageId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_LOCALIZEDENTRYLOCALIZATION_WHERE);

			sb.append(
				_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LOCALIZEDENTRYID_2);

			boolean bindLanguageId = false;

			if (languageId.isEmpty()) {
				sb.append(
					_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_3);
			}
			else {
				bindLanguageId = true;

				sb.append(
					_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(localizedEntryId);

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

	private static final String
		_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LOCALIZEDENTRYID_2 =
			"localizedEntryLocalization.localizedEntryId = ? AND ";

	private static final String
		_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_2 =
			"localizedEntryLocalization.languageId = ?";

	private static final String
		_FINDER_COLUMN_LOCALIZEDENTRYID_LANGUAGEID_LANGUAGEID_3 =
			"(localizedEntryLocalization.languageId IS NULL OR localizedEntryLocalization.languageId = '')";

	public LocalizedEntryLocalizationPersistenceImpl() {
		setModelClass(LocalizedEntryLocalization.class);

		setModelImplClass(LocalizedEntryLocalizationImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the localized entry localization in the entity cache if it is enabled.
	 *
	 * @param localizedEntryLocalization the localized entry localization
	 */
	@Override
	public void cacheResult(
		LocalizedEntryLocalization localizedEntryLocalization) {

		entityCache.putResult(
			LocalizedEntryLocalizationImpl.class,
			localizedEntryLocalization.getPrimaryKey(),
			localizedEntryLocalization);

		finderCache.putResult(
			_finderPathFetchByLocalizedEntryId_LanguageId,
			new Object[] {
				localizedEntryLocalization.getLocalizedEntryId(),
				localizedEntryLocalization.getLanguageId()
			},
			localizedEntryLocalization);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the localized entry localizations in the entity cache if it is enabled.
	 *
	 * @param localizedEntryLocalizations the localized entry localizations
	 */
	@Override
	public void cacheResult(
		List<LocalizedEntryLocalization> localizedEntryLocalizations) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (localizedEntryLocalizations.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LocalizedEntryLocalization localizedEntryLocalization :
				localizedEntryLocalizations) {

			if (entityCache.getResult(
					LocalizedEntryLocalizationImpl.class,
					localizedEntryLocalization.getPrimaryKey()) == null) {

				cacheResult(localizedEntryLocalization);
			}
		}
	}

	/**
	 * Clears the cache for all localized entry localizations.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LocalizedEntryLocalizationImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the localized entry localization.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		LocalizedEntryLocalization localizedEntryLocalization) {

		entityCache.removeResult(
			LocalizedEntryLocalizationImpl.class, localizedEntryLocalization);
	}

	@Override
	public void clearCache(
		List<LocalizedEntryLocalization> localizedEntryLocalizations) {

		for (LocalizedEntryLocalization localizedEntryLocalization :
				localizedEntryLocalizations) {

			entityCache.removeResult(
				LocalizedEntryLocalizationImpl.class,
				localizedEntryLocalization);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LocalizedEntryLocalizationImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LocalizedEntryLocalizationModelImpl
			localizedEntryLocalizationModelImpl) {

		Object[] args = new Object[] {
			localizedEntryLocalizationModelImpl.getLocalizedEntryId(),
			localizedEntryLocalizationModelImpl.getLanguageId()
		};

		finderCache.putResult(
			_finderPathCountByLocalizedEntryId_LanguageId, args,
			Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByLocalizedEntryId_LanguageId, args,
			localizedEntryLocalizationModelImpl, false);
	}

	/**
	 * Creates a new localized entry localization with the primary key. Does not add the localized entry localization to the database.
	 *
	 * @param localizedEntryLocalizationId the primary key for the new localized entry localization
	 * @return the new localized entry localization
	 */
	@Override
	public LocalizedEntryLocalization create(
		long localizedEntryLocalizationId) {

		LocalizedEntryLocalization localizedEntryLocalization =
			new LocalizedEntryLocalizationImpl();

		localizedEntryLocalization.setNew(true);
		localizedEntryLocalization.setPrimaryKey(localizedEntryLocalizationId);

		return localizedEntryLocalization;
	}

	/**
	 * Removes the localized entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization that was removed
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization remove(long localizedEntryLocalizationId)
		throws NoSuchLocalizedEntryLocalizationException {

		return remove((Serializable)localizedEntryLocalizationId);
	}

	/**
	 * Removes the localized entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the localized entry localization
	 * @return the localized entry localization that was removed
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization remove(Serializable primaryKey)
		throws NoSuchLocalizedEntryLocalizationException {

		Session session = null;

		try {
			session = openSession();

			LocalizedEntryLocalization localizedEntryLocalization =
				(LocalizedEntryLocalization)session.get(
					LocalizedEntryLocalizationImpl.class, primaryKey);

			if (localizedEntryLocalization == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLocalizedEntryLocalizationException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(localizedEntryLocalization);
		}
		catch (NoSuchLocalizedEntryLocalizationException
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
	protected LocalizedEntryLocalization removeImpl(
		LocalizedEntryLocalization localizedEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(localizedEntryLocalization)) {
				localizedEntryLocalization =
					(LocalizedEntryLocalization)session.get(
						LocalizedEntryLocalizationImpl.class,
						localizedEntryLocalization.getPrimaryKeyObj());
			}

			if (localizedEntryLocalization != null) {
				session.delete(localizedEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (localizedEntryLocalization != null) {
			clearCache(localizedEntryLocalization);
		}

		return localizedEntryLocalization;
	}

	@Override
	public LocalizedEntryLocalization updateImpl(
		LocalizedEntryLocalization localizedEntryLocalization) {

		boolean isNew = localizedEntryLocalization.isNew();

		if (!(localizedEntryLocalization instanceof
				LocalizedEntryLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(localizedEntryLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					localizedEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in localizedEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LocalizedEntryLocalization implementation " +
					localizedEntryLocalization.getClass());
		}

		LocalizedEntryLocalizationModelImpl
			localizedEntryLocalizationModelImpl =
				(LocalizedEntryLocalizationModelImpl)localizedEntryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(localizedEntryLocalization);
			}
			else {
				localizedEntryLocalization =
					(LocalizedEntryLocalization)session.merge(
						localizedEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LocalizedEntryLocalizationImpl.class,
			localizedEntryLocalizationModelImpl, false, true);

		cacheUniqueFindersCache(localizedEntryLocalizationModelImpl);

		if (isNew) {
			localizedEntryLocalization.setNew(false);
		}

		localizedEntryLocalization.resetOriginalValues();

		return localizedEntryLocalization;
	}

	/**
	 * Returns the localized entry localization with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the localized entry localization
	 * @return the localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLocalizedEntryLocalizationException {

		LocalizedEntryLocalization localizedEntryLocalization =
			fetchByPrimaryKey(primaryKey);

		if (localizedEntryLocalization == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLocalizedEntryLocalizationException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return localizedEntryLocalization;
	}

	/**
	 * Returns the localized entry localization with the primary key or throws a <code>NoSuchLocalizedEntryLocalizationException</code> if it could not be found.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization
	 * @throws NoSuchLocalizedEntryLocalizationException if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization findByPrimaryKey(
			long localizedEntryLocalizationId)
		throws NoSuchLocalizedEntryLocalizationException {

		return findByPrimaryKey((Serializable)localizedEntryLocalizationId);
	}

	/**
	 * Returns the localized entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param localizedEntryLocalizationId the primary key of the localized entry localization
	 * @return the localized entry localization, or <code>null</code> if a localized entry localization with the primary key could not be found
	 */
	@Override
	public LocalizedEntryLocalization fetchByPrimaryKey(
		long localizedEntryLocalizationId) {

		return fetchByPrimaryKey((Serializable)localizedEntryLocalizationId);
	}

	/**
	 * Returns all the localized entry localizations.
	 *
	 * @return the localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the localized entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @return the range of localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the localized entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findAll(
		int start, int end,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the localized entry localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entry localizations
	 * @param end the upper bound of the range of localized entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of localized entry localizations
	 */
	@Override
	public List<LocalizedEntryLocalization> findAll(
		int start, int end,
		OrderByComparator<LocalizedEntryLocalization> orderByComparator,
		boolean useFinderCache) {

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

		List<LocalizedEntryLocalization> list = null;

		if (useFinderCache) {
			list = (List<LocalizedEntryLocalization>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_LOCALIZEDENTRYLOCALIZATION);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION;

				sql = sql.concat(
					LocalizedEntryLocalizationModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<LocalizedEntryLocalization>)QueryUtil.list(
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

	/**
	 * Removes all the localized entry localizations from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LocalizedEntryLocalization localizedEntryLocalization :
				findAll()) {

			remove(localizedEntryLocalization);
		}
	}

	/**
	 * Returns the number of localized entry localizations.
	 *
	 * @return the number of localized entry localizations
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					"SELECT COUNT(localizedEntryLocalization) FROM LocalizedEntryLocalization localizedEntryLocalization");

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

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "localizedEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LocalizedEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the localized entry localization persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			LocalizedEntryLocalizationPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new LocalizedEntryLocalizationModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name",
				LocalizedEntryLocalization.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByLocalizedEntryId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLocalizedEntryId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"localizedEntryId"}, true);

		_finderPathWithoutPaginationFindByLocalizedEntryId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLocalizedEntryId",
			new String[] {Long.class.getName()},
			new String[] {"localizedEntryId"}, true);

		_finderPathCountByLocalizedEntryId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByLocalizedEntryId", new String[] {Long.class.getName()},
			new String[] {"localizedEntryId"}, false);

		_finderPathFetchByLocalizedEntryId_LanguageId = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByLocalizedEntryId_LanguageId",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"localizedEntryId", "languageId"}, true);

		_finderPathCountByLocalizedEntryId_LanguageId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByLocalizedEntryId_LanguageId",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"localizedEntryId", "languageId"}, false);

		LocalizedEntryLocalizationUtil.setPersistence(this);
	}

	public void destroy() {
		LocalizedEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(LocalizedEntryLocalizationImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LocalizedEntryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION =
		"SELECT localizedEntryLocalization FROM LocalizedEntryLocalization localizedEntryLocalization";

	private static final String _SQL_SELECT_LOCALIZEDENTRYLOCALIZATION_WHERE =
		"SELECT localizedEntryLocalization FROM LocalizedEntryLocalization localizedEntryLocalization WHERE ";

	private static final String _SQL_COUNT_LOCALIZEDENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(localizedEntryLocalization) FROM LocalizedEntryLocalization localizedEntryLocalization WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LocalizedEntryLocalization exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LocalizedEntryLocalization exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LocalizedEntryLocalizationPersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class LocalizedEntryLocalizationModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return new Object[0];
				}

				return null;
			}

			LocalizedEntryLocalizationModelImpl
				localizedEntryLocalizationModelImpl =
					(LocalizedEntryLocalizationModelImpl)baseModel;

			long columnBitmask =
				localizedEntryLocalizationModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					localizedEntryLocalizationModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						localizedEntryLocalizationModelImpl.getColumnBitmask(
							columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					localizedEntryLocalizationModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			LocalizedEntryLocalizationModelImpl
				localizedEntryLocalizationModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						localizedEntryLocalizationModelImpl.
							getColumnOriginalValue(columnName);
				}
				else {
					arguments[i] =
						localizedEntryLocalizationModelImpl.getColumnValue(
							columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1368555129