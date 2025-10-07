/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionLocalizationException;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.model.CPDefinitionLocalizationTable;
import com.liferay.commerce.product.model.impl.CPDefinitionLocalizationImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionLocalizationModelImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
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
 * The persistence implementation for the cp definition localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionLocalizationPersistence.class)
public class CPDefinitionLocalizationPersistenceImpl
	extends BasePersistenceImpl<CPDefinitionLocalization>
	implements CPDefinitionLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionLocalizationUtil</code> to access the cp definition localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCPDefinitionId;
	private FinderPath _finderPathWithoutPaginationFindByCPDefinitionId;
	private FinderPath _finderPathCountByCPDefinitionId;

	/**
	 * Returns all the cp definition localizations where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findByCPDefinitionId(
		long CPDefinitionId) {

		return findByCPDefinitionId(
			CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp definition localizations where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @return the range of matching cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return findByCPDefinitionId(CPDefinitionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp definition localizations where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionLocalization> orderByComparator) {

		return findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp definition localizations where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPDefinitionLocalization.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCPDefinitionId;
					finderArgs = new Object[] {CPDefinitionId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCPDefinitionId;
				finderArgs = new Object[] {
					CPDefinitionId, start, end, orderByComparator
				};
			}

			List<CPDefinitionLocalization> list = null;

			if (useFinderCache) {
				list = (List<CPDefinitionLocalization>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CPDefinitionLocalization cpDefinitionLocalization :
							list) {

						if (CPDefinitionId !=
								cpDefinitionLocalization.getCPDefinitionId()) {

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

				sb.append(_SQL_SELECT_CPDEFINITIONLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CPDefinitionLocalizationModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					list = (List<CPDefinitionLocalization>)QueryUtil.list(
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
	 * Returns the first cp definition localization in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionLocalization> orderByComparator)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization =
			fetchByCPDefinitionId_First(CPDefinitionId, orderByComparator);

		if (cpDefinitionLocalization != null) {
			return cpDefinitionLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCPDefinitionLocalizationException(sb.toString());
	}

	/**
	 * Returns the first cp definition localization in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition localization, or <code>null</code> if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionLocalization> orderByComparator) {

		List<CPDefinitionLocalization> list = findByCPDefinitionId(
			CPDefinitionId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last cp definition localization in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization findByCPDefinitionId_Last(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionLocalization> orderByComparator)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization =
			fetchByCPDefinitionId_Last(CPDefinitionId, orderByComparator);

		if (cpDefinitionLocalization != null) {
			return cpDefinitionLocalization;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("CPDefinitionId=");
		sb.append(CPDefinitionId);

		sb.append("}");

		throw new NoSuchCPDefinitionLocalizationException(sb.toString());
	}

	/**
	 * Returns the last cp definition localization in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp definition localization, or <code>null</code> if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByCPDefinitionId_Last(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionLocalization> orderByComparator) {

		int count = countByCPDefinitionId(CPDefinitionId);

		if (count == 0) {
			return null;
		}

		List<CPDefinitionLocalization> list = findByCPDefinitionId(
			CPDefinitionId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the cp definition localizations before and after the current cp definition localization in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param cpDefinitionLocalizationId the primary key of the current cp definition localization
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization[] findByCPDefinitionId_PrevAndNext(
			long cpDefinitionLocalizationId, long CPDefinitionId,
			OrderByComparator<CPDefinitionLocalization> orderByComparator)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization = findByPrimaryKey(
			cpDefinitionLocalizationId);

		Session session = null;

		try {
			session = openSession();

			CPDefinitionLocalization[] array =
				new CPDefinitionLocalizationImpl[3];

			array[0] = getByCPDefinitionId_PrevAndNext(
				session, cpDefinitionLocalization, CPDefinitionId,
				orderByComparator, true);

			array[1] = cpDefinitionLocalization;

			array[2] = getByCPDefinitionId_PrevAndNext(
				session, cpDefinitionLocalization, CPDefinitionId,
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

	protected CPDefinitionLocalization getByCPDefinitionId_PrevAndNext(
		Session session, CPDefinitionLocalization cpDefinitionLocalization,
		long CPDefinitionId,
		OrderByComparator<CPDefinitionLocalization> orderByComparator,
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

		sb.append(_SQL_SELECT_CPDEFINITIONLOCALIZATION_WHERE);

		sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

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
			sb.append(CPDefinitionLocalizationModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(CPDefinitionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						cpDefinitionLocalization)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CPDefinitionLocalization> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the cp definition localizations where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		for (CPDefinitionLocalization cpDefinitionLocalization :
				findByCPDefinitionId(
					CPDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(cpDefinitionLocalization);
		}
	}

	/**
	 * Returns the number of cp definition localizations where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition localizations
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPDefinitionLocalization.class)) {

			FinderPath finderPath = _finderPathCountByCPDefinitionId;

			Object[] finderArgs = new Object[] {CPDefinitionId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CPDEFINITIONLOCALIZATION_WHERE);

				sb.append(_FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

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

	private static final String _FINDER_COLUMN_CPDEFINITIONID_CPDEFINITIONID_2 =
		"cpDefinitionLocalization.CPDefinitionId = ?";

	private FinderPath _finderPathFetchByCPDefinitionId_LanguageId;

	/**
	 * Returns the cp definition localization where CPDefinitionId = &#63; and languageId = &#63; or throws a <code>NoSuchCPDefinitionLocalizationException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param languageId the language ID
	 * @return the matching cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization findByCPDefinitionId_LanguageId(
			long CPDefinitionId, String languageId)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization =
			fetchByCPDefinitionId_LanguageId(CPDefinitionId, languageId);

		if (cpDefinitionLocalization == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("CPDefinitionId=");
			sb.append(CPDefinitionId);

			sb.append(", languageId=");
			sb.append(languageId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCPDefinitionLocalizationException(sb.toString());
		}

		return cpDefinitionLocalization;
	}

	/**
	 * Returns the cp definition localization where CPDefinitionId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param languageId the language ID
	 * @return the matching cp definition localization, or <code>null</code> if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByCPDefinitionId_LanguageId(
		long CPDefinitionId, String languageId) {

		return fetchByCPDefinitionId_LanguageId(
			CPDefinitionId, languageId, true);
	}

	/**
	 * Returns the cp definition localization where CPDefinitionId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition localization, or <code>null</code> if a matching cp definition localization could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByCPDefinitionId_LanguageId(
		long CPDefinitionId, String languageId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPDefinitionLocalization.class)) {

			languageId = Objects.toString(languageId, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {CPDefinitionId, languageId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByCPDefinitionId_LanguageId, finderArgs,
					this);
			}

			if (result instanceof CPDefinitionLocalization) {
				CPDefinitionLocalization cpDefinitionLocalization =
					(CPDefinitionLocalization)result;

				if ((CPDefinitionId !=
						cpDefinitionLocalization.getCPDefinitionId()) ||
					!Objects.equals(
						languageId, cpDefinitionLocalization.getLanguageId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CPDEFINITIONLOCALIZATION_WHERE);

				sb.append(
					_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_CPDEFINITIONID_2);

				boolean bindLanguageId = false;

				if (languageId.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_LANGUAGEID_3);
				}
				else {
					bindLanguageId = true;

					sb.append(
						_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_LANGUAGEID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(CPDefinitionId);

					if (bindLanguageId) {
						queryPos.add(languageId);
					}

					List<CPDefinitionLocalization> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByCPDefinitionId_LanguageId,
								finderArgs, list);
						}
					}
					else {
						CPDefinitionLocalization cpDefinitionLocalization =
							list.get(0);

						result = cpDefinitionLocalization;

						cacheResult(cpDefinitionLocalization);
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
				return (CPDefinitionLocalization)result;
			}
		}
	}

	/**
	 * Removes the cp definition localization where CPDefinitionId = &#63; and languageId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param languageId the language ID
	 * @return the cp definition localization that was removed
	 */
	@Override
	public CPDefinitionLocalization removeByCPDefinitionId_LanguageId(
			long CPDefinitionId, String languageId)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization =
			findByCPDefinitionId_LanguageId(CPDefinitionId, languageId);

		return remove(cpDefinitionLocalization);
	}

	/**
	 * Returns the number of cp definition localizations where CPDefinitionId = &#63; and languageId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param languageId the language ID
	 * @return the number of matching cp definition localizations
	 */
	@Override
	public int countByCPDefinitionId_LanguageId(
		long CPDefinitionId, String languageId) {

		CPDefinitionLocalization cpDefinitionLocalization =
			fetchByCPDefinitionId_LanguageId(CPDefinitionId, languageId);

		if (cpDefinitionLocalization == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_CPDEFINITIONID_2 =
			"cpDefinitionLocalization.CPDefinitionId = ? AND ";

	private static final String
		_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_LANGUAGEID_2 =
			"cpDefinitionLocalization.languageId = ?";

	private static final String
		_FINDER_COLUMN_CPDEFINITIONID_LANGUAGEID_LANGUAGEID_3 =
			"(cpDefinitionLocalization.languageId IS NULL OR cpDefinitionLocalization.languageId = '')";

	public CPDefinitionLocalizationPersistenceImpl() {
		setModelClass(CPDefinitionLocalization.class);

		setModelImplClass(CPDefinitionLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionLocalizationTable.INSTANCE);
	}

	/**
	 * Caches the cp definition localization in the entity cache if it is enabled.
	 *
	 * @param cpDefinitionLocalization the cp definition localization
	 */
	@Override
	public void cacheResult(CPDefinitionLocalization cpDefinitionLocalization) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpDefinitionLocalization.getCtCollectionId())) {

			entityCache.putResult(
				CPDefinitionLocalizationImpl.class,
				cpDefinitionLocalization.getPrimaryKey(),
				cpDefinitionLocalization);

			finderCache.putResult(
				_finderPathFetchByCPDefinitionId_LanguageId,
				new Object[] {
					cpDefinitionLocalization.getCPDefinitionId(),
					cpDefinitionLocalization.getLanguageId()
				},
				cpDefinitionLocalization);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cp definition localizations in the entity cache if it is enabled.
	 *
	 * @param cpDefinitionLocalizations the cp definition localizations
	 */
	@Override
	public void cacheResult(
		List<CPDefinitionLocalization> cpDefinitionLocalizations) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cpDefinitionLocalizations.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizations) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						cpDefinitionLocalization.getCtCollectionId())) {

				if (entityCache.getResult(
						CPDefinitionLocalizationImpl.class,
						cpDefinitionLocalization.getPrimaryKey()) == null) {

					cacheResult(cpDefinitionLocalization);
				}
			}
		}
	}

	/**
	 * Clears the cache for all cp definition localizations.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CPDefinitionLocalizationImpl.class);

		finderCache.clearCache(CPDefinitionLocalizationImpl.class);
	}

	/**
	 * Clears the cache for the cp definition localization.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CPDefinitionLocalization cpDefinitionLocalization) {
		entityCache.removeResult(
			CPDefinitionLocalizationImpl.class, cpDefinitionLocalization);
	}

	@Override
	public void clearCache(
		List<CPDefinitionLocalization> cpDefinitionLocalizations) {

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizations) {

			entityCache.removeResult(
				CPDefinitionLocalizationImpl.class, cpDefinitionLocalization);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CPDefinitionLocalizationImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CPDefinitionLocalizationImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CPDefinitionLocalizationModelImpl cpDefinitionLocalizationModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					cpDefinitionLocalizationModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				cpDefinitionLocalizationModelImpl.getCPDefinitionId(),
				cpDefinitionLocalizationModelImpl.getLanguageId()
			};

			finderCache.putResult(
				_finderPathFetchByCPDefinitionId_LanguageId, args,
				cpDefinitionLocalizationModelImpl);
		}
	}

	/**
	 * Creates a new cp definition localization with the primary key. Does not add the cp definition localization to the database.
	 *
	 * @param cpDefinitionLocalizationId the primary key for the new cp definition localization
	 * @return the new cp definition localization
	 */
	@Override
	public CPDefinitionLocalization create(long cpDefinitionLocalizationId) {
		CPDefinitionLocalization cpDefinitionLocalization =
			new CPDefinitionLocalizationImpl();

		cpDefinitionLocalization.setNew(true);
		cpDefinitionLocalization.setPrimaryKey(cpDefinitionLocalizationId);

		cpDefinitionLocalization.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpDefinitionLocalization;
	}

	/**
	 * Removes the cp definition localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cpDefinitionLocalizationId the primary key of the cp definition localization
	 * @return the cp definition localization that was removed
	 * @throws NoSuchCPDefinitionLocalizationException if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization remove(long cpDefinitionLocalizationId)
		throws NoSuchCPDefinitionLocalizationException {

		return remove((Serializable)cpDefinitionLocalizationId);
	}

	/**
	 * Removes the cp definition localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cp definition localization
	 * @return the cp definition localization that was removed
	 * @throws NoSuchCPDefinitionLocalizationException if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization remove(Serializable primaryKey)
		throws NoSuchCPDefinitionLocalizationException {

		Session session = null;

		try {
			session = openSession();

			CPDefinitionLocalization cpDefinitionLocalization =
				(CPDefinitionLocalization)session.get(
					CPDefinitionLocalizationImpl.class, primaryKey);

			if (cpDefinitionLocalization == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCPDefinitionLocalizationException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cpDefinitionLocalization);
		}
		catch (NoSuchCPDefinitionLocalizationException noSuchEntityException) {
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
	protected CPDefinitionLocalization removeImpl(
		CPDefinitionLocalization cpDefinitionLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionLocalization)) {
				cpDefinitionLocalization =
					(CPDefinitionLocalization)session.get(
						CPDefinitionLocalizationImpl.class,
						cpDefinitionLocalization.getPrimaryKeyObj());
			}

			if ((cpDefinitionLocalization != null) &&
				ctPersistenceHelper.isRemove(cpDefinitionLocalization)) {

				session.delete(cpDefinitionLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionLocalization != null) {
			clearCache(cpDefinitionLocalization);
		}

		return cpDefinitionLocalization;
	}

	@Override
	public CPDefinitionLocalization updateImpl(
		CPDefinitionLocalization cpDefinitionLocalization) {

		boolean isNew = cpDefinitionLocalization.isNew();

		if (!(cpDefinitionLocalization instanceof
				CPDefinitionLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionLocalization implementation " +
					cpDefinitionLocalization.getClass());
		}

		CPDefinitionLocalizationModelImpl cpDefinitionLocalizationModelImpl =
			(CPDefinitionLocalizationModelImpl)cpDefinitionLocalization;

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = cpDefinitionLocalization.getCompanyId();

			long groupId = 0;

			long cpDefinitionLocalizationId = 0;

			if (!isNew) {
				cpDefinitionLocalizationId =
					cpDefinitionLocalization.getPrimaryKey();
			}

			try {
				cpDefinitionLocalization.setName(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_PLAIN,
						Sanitizer.MODE_ALL, cpDefinitionLocalization.getName(),
						null));

				cpDefinitionLocalization.setShortDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cpDefinitionLocalization.getShortDescription(), null));

				cpDefinitionLocalization.setDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cpDefinitionLocalization.getDescription(), null));

				cpDefinitionLocalization.setMetaTitle(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cpDefinitionLocalization.getMetaTitle(), null));

				cpDefinitionLocalization.setMetaDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cpDefinitionLocalization.getMetaDescription(), null));

				cpDefinitionLocalization.setMetaKeywords(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CPDefinitionLocalization.class.getName(),
						cpDefinitionLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cpDefinitionLocalization.getMetaKeywords(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDefinitionLocalization)) {
				if (!isNew) {
					session.evict(
						CPDefinitionLocalizationImpl.class,
						cpDefinitionLocalization.getPrimaryKeyObj());
				}

				session.save(cpDefinitionLocalization);
			}
			else {
				cpDefinitionLocalization =
					(CPDefinitionLocalization)session.merge(
						cpDefinitionLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			CPDefinitionLocalizationImpl.class,
			cpDefinitionLocalizationModelImpl, false, true);

		cacheUniqueFindersCache(cpDefinitionLocalizationModelImpl);

		if (isNew) {
			cpDefinitionLocalization.setNew(false);
		}

		cpDefinitionLocalization.resetOriginalValues();

		return cpDefinitionLocalization;
	}

	/**
	 * Returns the cp definition localization with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp definition localization
	 * @return the cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCPDefinitionLocalizationException {

		CPDefinitionLocalization cpDefinitionLocalization = fetchByPrimaryKey(
			primaryKey);

		if (cpDefinitionLocalization == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCPDefinitionLocalizationException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cpDefinitionLocalization;
	}

	/**
	 * Returns the cp definition localization with the primary key or throws a <code>NoSuchCPDefinitionLocalizationException</code> if it could not be found.
	 *
	 * @param cpDefinitionLocalizationId the primary key of the cp definition localization
	 * @return the cp definition localization
	 * @throws NoSuchCPDefinitionLocalizationException if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization findByPrimaryKey(
			long cpDefinitionLocalizationId)
		throws NoSuchCPDefinitionLocalizationException {

		return findByPrimaryKey((Serializable)cpDefinitionLocalizationId);
	}

	/**
	 * Returns the cp definition localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cp definition localization
	 * @return the cp definition localization, or <code>null</code> if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CPDefinitionLocalization.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CPDefinitionLocalization cpDefinitionLocalization =
			(CPDefinitionLocalization)entityCache.getResult(
				CPDefinitionLocalizationImpl.class, primaryKey);

		if (cpDefinitionLocalization != null) {
			return cpDefinitionLocalization;
		}

		Session session = null;

		try {
			session = openSession();

			cpDefinitionLocalization = (CPDefinitionLocalization)session.get(
				CPDefinitionLocalizationImpl.class, primaryKey);

			if (cpDefinitionLocalization != null) {
				cacheResult(cpDefinitionLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return cpDefinitionLocalization;
	}

	/**
	 * Returns the cp definition localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cpDefinitionLocalizationId the primary key of the cp definition localization
	 * @return the cp definition localization, or <code>null</code> if a cp definition localization with the primary key could not be found
	 */
	@Override
	public CPDefinitionLocalization fetchByPrimaryKey(
		long cpDefinitionLocalizationId) {

		return fetchByPrimaryKey((Serializable)cpDefinitionLocalizationId);
	}

	@Override
	public Map<Serializable, CPDefinitionLocalization> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				CPDefinitionLocalization.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CPDefinitionLocalization> map =
			new HashMap<Serializable, CPDefinitionLocalization>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CPDefinitionLocalization cpDefinitionLocalization =
				fetchByPrimaryKey(primaryKey);

			if (cpDefinitionLocalization != null) {
				map.put(primaryKey, cpDefinitionLocalization);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CPDefinitionLocalization.class, primaryKey)) {

				CPDefinitionLocalization cpDefinitionLocalization =
					(CPDefinitionLocalization)entityCache.getResult(
						CPDefinitionLocalizationImpl.class, primaryKey);

				if (cpDefinitionLocalization == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, cpDefinitionLocalization);
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

			for (CPDefinitionLocalization cpDefinitionLocalization :
					(List<CPDefinitionLocalization>)query.list()) {

				map.put(
					cpDefinitionLocalization.getPrimaryKeyObj(),
					cpDefinitionLocalization);

				cacheResult(cpDefinitionLocalization);
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
	 * Returns all the cp definition localizations.
	 *
	 * @return the cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp definition localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @return the range of cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp definition localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findAll(
		int start, int end,
		OrderByComparator<CPDefinitionLocalization> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp definition localizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition localizations
	 * @param end the upper bound of the range of cp definition localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp definition localizations
	 */
	@Override
	public List<CPDefinitionLocalization> findAll(
		int start, int end,
		OrderByComparator<CPDefinitionLocalization> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPDefinitionLocalization.class)) {

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

			List<CPDefinitionLocalization> list = null;

			if (useFinderCache) {
				list = (List<CPDefinitionLocalization>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CPDEFINITIONLOCALIZATION);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CPDEFINITIONLOCALIZATION;

					sql = sql.concat(
						CPDefinitionLocalizationModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CPDefinitionLocalization>)QueryUtil.list(
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
	 * Removes all the cp definition localizations from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CPDefinitionLocalization cpDefinitionLocalization : findAll()) {
			remove(cpDefinitionLocalization);
		}
	}

	/**
	 * Returns the number of cp definition localizations.
	 *
	 * @return the number of cp definition localizations
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CPDefinitionLocalization.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_CPDEFINITIONLOCALIZATION);

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
		return "cpDefinitionLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONLOCALIZATION;
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
		return CPDefinitionLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDefinitionLocalization";
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
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("shortDescription");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("metaTitle");
		ctMergeColumnNames.add("metaDescription");
		ctMergeColumnNames.add("metaKeywords");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("cpDefinitionLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "languageId"});
	}

	/**
	 * Initializes the cp definition localization persistence.
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

		_finderPathWithPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPDefinitionId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"CPDefinitionId"}, true);

		_finderPathWithoutPaginationFindByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, true);

		_finderPathCountByCPDefinitionId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPDefinitionId",
			new String[] {Long.class.getName()},
			new String[] {"CPDefinitionId"}, false);

		_finderPathFetchByCPDefinitionId_LanguageId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByCPDefinitionId_LanguageId",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"CPDefinitionId", "languageId"}, true);

		CPDefinitionLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionLocalizationUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_CPDEFINITIONLOCALIZATION =
		"SELECT cpDefinitionLocalization FROM CPDefinitionLocalization cpDefinitionLocalization";

	private static final String _SQL_SELECT_CPDEFINITIONLOCALIZATION_WHERE =
		"SELECT cpDefinitionLocalization FROM CPDefinitionLocalization cpDefinitionLocalization WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONLOCALIZATION =
		"SELECT COUNT(cpDefinitionLocalization) FROM CPDefinitionLocalization cpDefinitionLocalization";

	private static final String _SQL_COUNT_CPDEFINITIONLOCALIZATION_WHERE =
		"SELECT COUNT(cpDefinitionLocalization) FROM CPDefinitionLocalization cpDefinitionLocalization WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"cpDefinitionLocalization.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CPDefinitionLocalization exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDefinitionLocalization exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLocalizationPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}