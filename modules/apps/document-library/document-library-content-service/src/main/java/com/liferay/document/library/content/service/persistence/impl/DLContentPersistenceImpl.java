/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service.persistence.impl;

import com.liferay.document.library.content.exception.NoSuchContentException;
import com.liferay.document.library.content.model.DLContent;
import com.liferay.document.library.content.model.DLContentTable;
import com.liferay.document.library.content.model.impl.DLContentImpl;
import com.liferay.document.library.content.model.impl.DLContentModelImpl;
import com.liferay.document.library.content.service.persistence.DLContentPersistence;
import com.liferay.document.library.content.service.persistence.DLContentUtil;
import com.liferay.document.library.content.service.persistence.impl.constants.DLPersistenceConstants;
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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

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
 * The persistence implementation for the document library content service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DLContentPersistence.class)
public class DLContentPersistenceImpl
	extends BasePersistenceImpl<DLContent> implements DLContentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLContentUtil</code> to access the document library content persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLContentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_R;
	private FinderPath _finderPathWithoutPaginationFindByC_R;
	private FinderPath _finderPathCountByC_R;

	/**
	 * Returns all the document library contents where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @return the matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R(long companyId, long repositoryId) {
		return findByC_R(
			companyId, repositoryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library contents where companyId = &#63; and repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @return the range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R(
		long companyId, long repositoryId, int start, int end) {

		return findByC_R(companyId, repositoryId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R(
		long companyId, long repositoryId, int start, int end,
		OrderByComparator<DLContent> orderByComparator) {

		return findByC_R(
			companyId, repositoryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R(
		long companyId, long repositoryId, int start, int end,
		OrderByComparator<DLContent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_R;
					finderArgs = new Object[] {companyId, repositoryId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_R;
				finderArgs = new Object[] {
					companyId, repositoryId, start, end, orderByComparator
				};
			}

			List<DLContent> list = null;

			if (useFinderCache) {
				list = (List<DLContent>)dummyFinderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLContent dlContent : list) {
						if ((companyId != dlContent.getCompanyId()) ||
							(repositoryId != dlContent.getRepositoryId())) {

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

				sb.append(_SQL_SELECT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_REPOSITORYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLContentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					list = (List<DLContent>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						dummyFinderCache.putResult(
							finderPath, finderArgs, list);
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
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_First(
			long companyId, long repositoryId,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_First(
			companyId, repositoryId, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_First(
		long companyId, long repositoryId,
		OrderByComparator<DLContent> orderByComparator) {

		List<DLContent> list = findByC_R(
			companyId, repositoryId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_Last(
			long companyId, long repositoryId,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_Last(
			companyId, repositoryId, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_Last(
		long companyId, long repositoryId,
		OrderByComparator<DLContent> orderByComparator) {

		int count = countByC_R(companyId, repositoryId);

		if (count == 0) {
			return null;
		}

		List<DLContent> list = findByC_R(
			companyId, repositoryId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library contents before and after the current document library content in the ordered set where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param contentId the primary key of the current document library content
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library content
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent[] findByC_R_PrevAndNext(
			long contentId, long companyId, long repositoryId,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = findByPrimaryKey(contentId);

		Session session = null;

		try {
			session = openSession();

			DLContent[] array = new DLContentImpl[3];

			array[0] = getByC_R_PrevAndNext(
				session, dlContent, companyId, repositoryId, orderByComparator,
				true);

			array[1] = dlContent;

			array[2] = getByC_R_PrevAndNext(
				session, dlContent, companyId, repositoryId, orderByComparator,
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

	protected DLContent getByC_R_PrevAndNext(
		Session session, DLContent dlContent, long companyId, long repositoryId,
		OrderByComparator<DLContent> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DLCONTENT_WHERE);

		sb.append(_FINDER_COLUMN_C_R_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_R_REPOSITORYID_2);

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
			sb.append(DLContentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(repositoryId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlContent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLContent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library contents where companyId = &#63; and repositoryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByC_R(long companyId, long repositoryId) {
		for (DLContent dlContent :
				findByC_R(
					companyId, repositoryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlContent);
		}
	}

	/**
	 * Returns the number of document library contents where companyId = &#63; and repositoryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @return the number of matching document library contents
	 */
	@Override
	public int countByC_R(long companyId, long repositoryId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			FinderPath finderPath = _finderPathCountByC_R;

			Object[] finderArgs = new Object[] {companyId, repositoryId};

			Long count = (Long)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_REPOSITORYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					count = (Long)query.uniqueResult();

					dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_R_COMPANYID_2 =
		"dlContent.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_REPOSITORYID_2 =
		"dlContent.repositoryId = ?";

	private FinderPath _finderPathWithPaginationFindByC_R_P;
	private FinderPath _finderPathWithoutPaginationFindByC_R_P;
	private FinderPath _finderPathCountByC_R_P;

	/**
	 * Returns all the document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @return the matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_P(
		long companyId, long repositoryId, String path) {

		return findByC_R_P(
			companyId, repositoryId, path, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @return the range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_P(
		long companyId, long repositoryId, String path, int start, int end) {

		return findByC_R_P(companyId, repositoryId, path, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_P(
		long companyId, long repositoryId, String path, int start, int end,
		OrderByComparator<DLContent> orderByComparator) {

		return findByC_R_P(
			companyId, repositoryId, path, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_P(
		long companyId, long repositoryId, String path, int start, int end,
		OrderByComparator<DLContent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			path = Objects.toString(path, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_R_P;
					finderArgs = new Object[] {companyId, repositoryId, path};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_R_P;
				finderArgs = new Object[] {
					companyId, repositoryId, path, start, end, orderByComparator
				};
			}

			List<DLContent> list = null;

			if (useFinderCache) {
				list = (List<DLContent>)dummyFinderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLContent dlContent : list) {
						if ((companyId != dlContent.getCompanyId()) ||
							(repositoryId != dlContent.getRepositoryId()) ||
							!path.equals(dlContent.getPath())) {

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

				sb.append(_SQL_SELECT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_P_REPOSITORYID_2);

				boolean bindPath = false;

				if (path.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_P_PATH_3);
				}
				else {
					bindPath = true;

					sb.append(_FINDER_COLUMN_C_R_P_PATH_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLContentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					if (bindPath) {
						queryPos.add(path);
					}

					list = (List<DLContent>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						dummyFinderCache.putResult(
							finderPath, finderArgs, list);
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
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_P_First(
			long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_P_First(
			companyId, repositoryId, path, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append(", path=");
		sb.append(path);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_P_First(
		long companyId, long repositoryId, String path,
		OrderByComparator<DLContent> orderByComparator) {

		List<DLContent> list = findByC_R_P(
			companyId, repositoryId, path, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_P_Last(
			long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_P_Last(
			companyId, repositoryId, path, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append(", path=");
		sb.append(path);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_P_Last(
		long companyId, long repositoryId, String path,
		OrderByComparator<DLContent> orderByComparator) {

		int count = countByC_R_P(companyId, repositoryId, path);

		if (count == 0) {
			return null;
		}

		List<DLContent> list = findByC_R_P(
			companyId, repositoryId, path, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library contents before and after the current document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param contentId the primary key of the current document library content
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library content
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent[] findByC_R_P_PrevAndNext(
			long contentId, long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		path = Objects.toString(path, "");

		DLContent dlContent = findByPrimaryKey(contentId);

		Session session = null;

		try {
			session = openSession();

			DLContent[] array = new DLContentImpl[3];

			array[0] = getByC_R_P_PrevAndNext(
				session, dlContent, companyId, repositoryId, path,
				orderByComparator, true);

			array[1] = dlContent;

			array[2] = getByC_R_P_PrevAndNext(
				session, dlContent, companyId, repositoryId, path,
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

	protected DLContent getByC_R_P_PrevAndNext(
		Session session, DLContent dlContent, long companyId, long repositoryId,
		String path, OrderByComparator<DLContent> orderByComparator,
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

		sb.append(_SQL_SELECT_DLCONTENT_WHERE);

		sb.append(_FINDER_COLUMN_C_R_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_R_P_REPOSITORYID_2);

		boolean bindPath = false;

		if (path.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_R_P_PATH_3);
		}
		else {
			bindPath = true;

			sb.append(_FINDER_COLUMN_C_R_P_PATH_2);
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
			sb.append(DLContentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(repositoryId);

		if (bindPath) {
			queryPos.add(path);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlContent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLContent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 */
	@Override
	public void removeByC_R_P(long companyId, long repositoryId, String path) {
		for (DLContent dlContent :
				findByC_R_P(
					companyId, repositoryId, path, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlContent);
		}
	}

	/**
	 * Returns the number of document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @return the number of matching document library contents
	 */
	@Override
	public int countByC_R_P(long companyId, long repositoryId, String path) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			path = Objects.toString(path, "");

			FinderPath finderPath = _finderPathCountByC_R_P;

			Object[] finderArgs = new Object[] {companyId, repositoryId, path};

			Long count = (Long)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_P_REPOSITORYID_2);

				boolean bindPath = false;

				if (path.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_P_PATH_3);
				}
				else {
					bindPath = true;

					sb.append(_FINDER_COLUMN_C_R_P_PATH_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					if (bindPath) {
						queryPos.add(path);
					}

					count = (Long)query.uniqueResult();

					dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_R_P_COMPANYID_2 =
		"dlContent.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_P_REPOSITORYID_2 =
		"dlContent.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_P_PATH_2 =
		"dlContent.path = ?";

	private static final String _FINDER_COLUMN_C_R_P_PATH_3 =
		"(dlContent.path IS NULL OR dlContent.path = '')";

	private FinderPath _finderPathWithPaginationFindByC_R_LikeP;
	private FinderPath _finderPathWithPaginationCountByC_R_LikeP;

	/**
	 * Returns all the document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @return the matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_LikeP(
		long companyId, long repositoryId, String path) {

		return findByC_R_LikeP(
			companyId, repositoryId, path, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @return the range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_LikeP(
		long companyId, long repositoryId, String path, int start, int end) {

		return findByC_R_LikeP(companyId, repositoryId, path, start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_LikeP(
		long companyId, long repositoryId, String path, int start, int end,
		OrderByComparator<DLContent> orderByComparator) {

		return findByC_R_LikeP(
			companyId, repositoryId, path, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library contents
	 */
	@Override
	public List<DLContent> findByC_R_LikeP(
		long companyId, long repositoryId, String path, int start, int end,
		OrderByComparator<DLContent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			path = Objects.toString(path, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_R_LikeP;
			finderArgs = new Object[] {
				companyId, repositoryId, path, start, end, orderByComparator
			};

			List<DLContent> list = null;

			if (useFinderCache) {
				list = (List<DLContent>)dummyFinderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DLContent dlContent : list) {
						if ((companyId != dlContent.getCompanyId()) ||
							(repositoryId != dlContent.getRepositoryId()) ||
							!StringUtil.wildcardMatches(
								dlContent.getPath(), path, '_', '%', '\\',
								true)) {

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

				sb.append(_SQL_SELECT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_LIKEP_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_LIKEP_REPOSITORYID_2);

				boolean bindPath = false;

				if (path.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_3);
				}
				else {
					bindPath = true;

					sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DLContentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					if (bindPath) {
						queryPos.add(path);
					}

					list = (List<DLContent>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						dummyFinderCache.putResult(
							finderPath, finderArgs, list);
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
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_LikeP_First(
			long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_LikeP_First(
			companyId, repositoryId, path, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append(", pathLIKE");
		sb.append(path);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the first document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_LikeP_First(
		long companyId, long repositoryId, String path,
		OrderByComparator<DLContent> orderByComparator) {

		List<DLContent> list = findByC_R_LikeP(
			companyId, repositoryId, path, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_LikeP_Last(
			long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_LikeP_Last(
			companyId, repositoryId, path, orderByComparator);

		if (dlContent != null) {
			return dlContent;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", repositoryId=");
		sb.append(repositoryId);

		sb.append(", pathLIKE");
		sb.append(path);

		sb.append("}");

		throw new NoSuchContentException(sb.toString());
	}

	/**
	 * Returns the last document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_LikeP_Last(
		long companyId, long repositoryId, String path,
		OrderByComparator<DLContent> orderByComparator) {

		int count = countByC_R_LikeP(companyId, repositoryId, path);

		if (count == 0) {
			return null;
		}

		List<DLContent> list = findByC_R_LikeP(
			companyId, repositoryId, path, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the document library contents before and after the current document library content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param contentId the primary key of the current document library content
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library content
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent[] findByC_R_LikeP_PrevAndNext(
			long contentId, long companyId, long repositoryId, String path,
			OrderByComparator<DLContent> orderByComparator)
		throws NoSuchContentException {

		path = Objects.toString(path, "");

		DLContent dlContent = findByPrimaryKey(contentId);

		Session session = null;

		try {
			session = openSession();

			DLContent[] array = new DLContentImpl[3];

			array[0] = getByC_R_LikeP_PrevAndNext(
				session, dlContent, companyId, repositoryId, path,
				orderByComparator, true);

			array[1] = dlContent;

			array[2] = getByC_R_LikeP_PrevAndNext(
				session, dlContent, companyId, repositoryId, path,
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

	protected DLContent getByC_R_LikeP_PrevAndNext(
		Session session, DLContent dlContent, long companyId, long repositoryId,
		String path, OrderByComparator<DLContent> orderByComparator,
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

		sb.append(_SQL_SELECT_DLCONTENT_WHERE);

		sb.append(_FINDER_COLUMN_C_R_LIKEP_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_R_LIKEP_REPOSITORYID_2);

		boolean bindPath = false;

		if (path.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_3);
		}
		else {
			bindPath = true;

			sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_2);
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
			sb.append(DLContentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(repositoryId);

		if (bindPath) {
			queryPos.add(path);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(dlContent)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DLContent> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 */
	@Override
	public void removeByC_R_LikeP(
		long companyId, long repositoryId, String path) {

		for (DLContent dlContent :
				findByC_R_LikeP(
					companyId, repositoryId, path, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dlContent);
		}
	}

	/**
	 * Returns the number of document library contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @return the number of matching document library contents
	 */
	@Override
	public int countByC_R_LikeP(
		long companyId, long repositoryId, String path) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			path = Objects.toString(path, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_R_LikeP;

			Object[] finderArgs = new Object[] {companyId, repositoryId, path};

			Long count = (Long)dummyFinderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_LIKEP_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_LIKEP_REPOSITORYID_2);

				boolean bindPath = false;

				if (path.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_3);
				}
				else {
					bindPath = true;

					sb.append(_FINDER_COLUMN_C_R_LIKEP_PATH_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					if (bindPath) {
						queryPos.add(path);
					}

					count = (Long)query.uniqueResult();

					dummyFinderCache.putResult(finderPath, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_R_LIKEP_COMPANYID_2 =
		"dlContent.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_LIKEP_REPOSITORYID_2 =
		"dlContent.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_LIKEP_PATH_2 =
		"dlContent.path LIKE ?";

	private static final String _FINDER_COLUMN_C_R_LIKEP_PATH_3 =
		"(dlContent.path IS NULL OR dlContent.path LIKE '')";

	private FinderPath _finderPathFetchByC_R_P_V;

	/**
	 * Returns the document library content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; or throws a <code>NoSuchContentException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @return the matching document library content
	 * @throws NoSuchContentException if a matching document library content could not be found
	 */
	@Override
	public DLContent findByC_R_P_V(
			long companyId, long repositoryId, String path, String version)
		throws NoSuchContentException {

		DLContent dlContent = fetchByC_R_P_V(
			companyId, repositoryId, path, version);

		if (dlContent == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", repositoryId=");
			sb.append(repositoryId);

			sb.append(", path=");
			sb.append(path);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchContentException(sb.toString());
		}

		return dlContent;
	}

	/**
	 * Returns the document library content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @return the matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_P_V(
		long companyId, long repositoryId, String path, String version) {

		return fetchByC_R_P_V(companyId, repositoryId, path, version, true);
	}

	/**
	 * Returns the document library content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library content, or <code>null</code> if a matching document library content could not be found
	 */
	@Override
	public DLContent fetchByC_R_P_V(
		long companyId, long repositoryId, String path, String version,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			path = Objects.toString(path, "");
			version = Objects.toString(version, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, repositoryId, path, version
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = dummyFinderCache.getResult(
					_finderPathFetchByC_R_P_V, finderArgs, this);
			}

			if (result instanceof DLContent) {
				DLContent dlContent = (DLContent)result;

				if ((companyId != dlContent.getCompanyId()) ||
					(repositoryId != dlContent.getRepositoryId()) ||
					!Objects.equals(path, dlContent.getPath()) ||
					!Objects.equals(version, dlContent.getVersion())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_DLCONTENT_WHERE);

				sb.append(_FINDER_COLUMN_C_R_P_V_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_R_P_V_REPOSITORYID_2);

				boolean bindPath = false;

				if (path.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_P_V_PATH_3);
				}
				else {
					bindPath = true;

					sb.append(_FINDER_COLUMN_C_R_P_V_PATH_2);
				}

				boolean bindVersion = false;

				if (version.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_R_P_V_VERSION_3);
				}
				else {
					bindVersion = true;

					sb.append(_FINDER_COLUMN_C_R_P_V_VERSION_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(repositoryId);

					if (bindPath) {
						queryPos.add(path);
					}

					if (bindVersion) {
						queryPos.add(version);
					}

					List<DLContent> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							dummyFinderCache.putResult(
								_finderPathFetchByC_R_P_V, finderArgs, list);
						}
					}
					else {
						DLContent dlContent = list.get(0);

						result = dlContent;

						cacheResult(dlContent);
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
				return (DLContent)result;
			}
		}
	}

	/**
	 * Removes the document library content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @return the document library content that was removed
	 */
	@Override
	public DLContent removeByC_R_P_V(
			long companyId, long repositoryId, String path, String version)
		throws NoSuchContentException {

		DLContent dlContent = findByC_R_P_V(
			companyId, repositoryId, path, version);

		return remove(dlContent);
	}

	/**
	 * Returns the number of document library contents where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @return the number of matching document library contents
	 */
	@Override
	public int countByC_R_P_V(
		long companyId, long repositoryId, String path, String version) {

		DLContent dlContent = fetchByC_R_P_V(
			companyId, repositoryId, path, version);

		if (dlContent == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_R_P_V_COMPANYID_2 =
		"dlContent.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_P_V_REPOSITORYID_2 =
		"dlContent.repositoryId = ? AND ";

	private static final String _FINDER_COLUMN_C_R_P_V_PATH_2 =
		"dlContent.path = ? AND ";

	private static final String _FINDER_COLUMN_C_R_P_V_PATH_3 =
		"(dlContent.path IS NULL OR dlContent.path = '') AND ";

	private static final String _FINDER_COLUMN_C_R_P_V_VERSION_2 =
		"dlContent.version = ?";

	private static final String _FINDER_COLUMN_C_R_P_V_VERSION_3 =
		"(dlContent.version IS NULL OR dlContent.version = '')";

	public DLContentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("path", "path_");
		dbColumnNames.put("data", "data_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLContent.class);

		setModelImplClass(DLContentImpl.class);
		setModelPKClass(long.class);

		setTable(DLContentTable.INSTANCE);
	}

	/**
	 * Caches the document library content in the entity cache if it is enabled.
	 *
	 * @param dlContent the document library content
	 */
	@Override
	public void cacheResult(DLContent dlContent) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlContent.getCtCollectionId())) {

			dummyEntityCache.putResult(
				DLContentImpl.class, dlContent.getPrimaryKey(), dlContent);

			dummyFinderCache.putResult(
				_finderPathFetchByC_R_P_V,
				new Object[] {
					dlContent.getCompanyId(), dlContent.getRepositoryId(),
					dlContent.getPath(), dlContent.getVersion()
				},
				dlContent);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the document library contents in the entity cache if it is enabled.
	 *
	 * @param dlContents the document library contents
	 */
	@Override
	public void cacheResult(List<DLContent> dlContents) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (dlContents.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DLContent dlContent : dlContents) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						dlContent.getCtCollectionId())) {

				if (dummyEntityCache.getResult(
						DLContentImpl.class, dlContent.getPrimaryKey()) ==
							null) {

					cacheResult(dlContent);
				}
			}
		}
	}

	/**
	 * Clears the cache for all document library contents.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		dummyEntityCache.clearCache(DLContentImpl.class);

		dummyFinderCache.clearCache(DLContentImpl.class);
	}

	/**
	 * Clears the cache for the document library content.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DLContent dlContent) {
		dummyEntityCache.removeResult(DLContentImpl.class, dlContent);
	}

	@Override
	public void clearCache(List<DLContent> dlContents) {
		for (DLContent dlContent : dlContents) {
			dummyEntityCache.removeResult(DLContentImpl.class, dlContent);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		dummyFinderCache.clearCache(DLContentImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			dummyEntityCache.removeResult(DLContentImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DLContentModelImpl dlContentModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					dlContentModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				dlContentModelImpl.getCompanyId(),
				dlContentModelImpl.getRepositoryId(),
				dlContentModelImpl.getPath(), dlContentModelImpl.getVersion()
			};

			dummyFinderCache.putResult(
				_finderPathFetchByC_R_P_V, args, dlContentModelImpl);
		}
	}

	/**
	 * Creates a new document library content with the primary key. Does not add the document library content to the database.
	 *
	 * @param contentId the primary key for the new document library content
	 * @return the new document library content
	 */
	@Override
	public DLContent create(long contentId) {
		DLContent dlContent = new DLContentImpl();

		dlContent.setNew(true);
		dlContent.setPrimaryKey(contentId);

		dlContent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlContent;
	}

	/**
	 * Removes the document library content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content that was removed
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent remove(long contentId) throws NoSuchContentException {
		return remove((Serializable)contentId);
	}

	/**
	 * Removes the document library content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the document library content
	 * @return the document library content that was removed
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent remove(Serializable primaryKey)
		throws NoSuchContentException {

		Session session = null;

		try {
			session = openSession();

			DLContent dlContent = (DLContent)session.get(
				DLContentImpl.class, primaryKey);

			if (dlContent == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchContentException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dlContent);
		}
		catch (NoSuchContentException noSuchEntityException) {
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
	protected DLContent removeImpl(DLContent dlContent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlContent)) {
				dlContent = (DLContent)session.get(
					DLContentImpl.class, dlContent.getPrimaryKeyObj());
			}

			if ((dlContent != null) &&
				ctPersistenceHelper.isRemove(dlContent)) {

				session.delete(dlContent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlContent != null) {
			clearCache(dlContent);
		}

		return dlContent;
	}

	@Override
	public DLContent updateImpl(DLContent dlContent) {
		boolean isNew = dlContent.isNew();

		if (!(dlContent instanceof DLContentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlContent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlContent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlContent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLContent implementation " +
					dlContent.getClass());
		}

		DLContentModelImpl dlContentModelImpl = (DLContentModelImpl)dlContent;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(dlContent)) {
				if (!isNew) {
					session.evict(
						DLContentImpl.class, dlContent.getPrimaryKeyObj());
				}

				session.save(dlContent);
			}
			else {
				dlContent = (DLContent)session.merge(dlContent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		dummyEntityCache.putResult(
			DLContentImpl.class, dlContentModelImpl, false, true);

		cacheUniqueFindersCache(dlContentModelImpl);

		if (isNew) {
			dlContent.setNew(false);
		}

		dlContent.resetOriginalValues();

		return dlContent;
	}

	/**
	 * Returns the document library content with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library content
	 * @return the document library content
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent findByPrimaryKey(Serializable primaryKey)
		throws NoSuchContentException {

		DLContent dlContent = fetchByPrimaryKey(primaryKey);

		if (dlContent == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchContentException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dlContent;
	}

	/**
	 * Returns the document library content with the primary key or throws a <code>NoSuchContentException</code> if it could not be found.
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content
	 * @throws NoSuchContentException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent findByPrimaryKey(long contentId)
		throws NoSuchContentException {

		return findByPrimaryKey((Serializable)contentId);
	}

	/**
	 * Returns the document library content with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the document library content
	 * @return the document library content, or <code>null</code> if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(DLContent.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DLContent dlContent = (DLContent)dummyEntityCache.getResult(
			DLContentImpl.class, primaryKey);

		if (dlContent != null) {
			return dlContent;
		}

		Session session = null;

		try {
			session = openSession();

			dlContent = (DLContent)session.get(DLContentImpl.class, primaryKey);

			if (dlContent != null) {
				cacheResult(dlContent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return dlContent;
	}

	/**
	 * Returns the document library content with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content, or <code>null</code> if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent fetchByPrimaryKey(long contentId) {
		return fetchByPrimaryKey((Serializable)contentId);
	}

	@Override
	public Map<Serializable, DLContent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DLContent.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DLContent> map =
			new HashMap<Serializable, DLContent>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DLContent dlContent = fetchByPrimaryKey(primaryKey);

			if (dlContent != null) {
				map.put(primaryKey, dlContent);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DLContent.class, primaryKey)) {

				DLContent dlContent = (DLContent)dummyEntityCache.getResult(
					DLContentImpl.class, primaryKey);

				if (dlContent == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, dlContent);
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

			for (DLContent dlContent : (List<DLContent>)query.list()) {
				map.put(dlContent.getPrimaryKeyObj(), dlContent);

				cacheResult(dlContent);
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
	 * Returns all the document library contents.
	 *
	 * @return the document library contents
	 */
	@Override
	public List<DLContent> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the document library contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @return the range of document library contents
	 */
	@Override
	public List<DLContent> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the document library contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library contents
	 */
	@Override
	public List<DLContent> findAll(
		int start, int end, OrderByComparator<DLContent> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the document library contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of document library contents
	 */
	@Override
	public List<DLContent> findAll(
		int start, int end, OrderByComparator<DLContent> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

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

			List<DLContent> list = null;

			if (useFinderCache) {
				list = (List<DLContent>)dummyFinderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DLCONTENT);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DLCONTENT;

					sql = sql.concat(DLContentModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DLContent>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						dummyFinderCache.putResult(
							finderPath, finderArgs, list);
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
	 * Removes all the document library contents from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DLContent dlContent : findAll()) {
			remove(dlContent);
		}
	}

	/**
	 * Returns the number of document library contents.
	 *
	 * @return the number of document library contents
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DLContent.class)) {

			Long count = (Long)dummyFinderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DLCONTENT);

					count = (Long)query.uniqueResult();

					dummyFinderCache.putResult(
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
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "contentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLCONTENT;
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
		return DLContentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLContent";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("path_");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("data_");
		ctMergeColumnNames.add("size_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("contentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "repositoryId", "path_", "version"});
	}

	/**
	 * Initializes the document library content persistence.
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

		_finderPathWithPaginationFindByC_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "repositoryId"}, true);

		_finderPathWithoutPaginationFindByC_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "repositoryId"}, true);

		_finderPathCountByC_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_R",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "repositoryId"}, false);

		_finderPathWithPaginationFindByC_R_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_"}, true);

		_finderPathWithoutPaginationFindByC_R_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_"}, true);

		_finderPathCountByC_R_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_R_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_"}, false);

		_finderPathWithPaginationFindByC_R_LikeP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R_LikeP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_"}, true);

		_finderPathWithPaginationCountByC_R_LikeP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_R_LikeP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_"}, false);

		_finderPathFetchByC_R_P_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_R_P_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), String.class.getName()
			},
			new String[] {"companyId", "repositoryId", "path_", "version"},
			true);

		DLContentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DLContentUtil.setPersistence(null);

		dummyEntityCache.removeCache(DLContentImpl.class.getName());
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	private static final String _SQL_SELECT_DLCONTENT =
		"SELECT dlContent FROM DLContent dlContent";

	private static final String _SQL_SELECT_DLCONTENT_WHERE =
		"SELECT dlContent FROM DLContent dlContent WHERE ";

	private static final String _SQL_COUNT_DLCONTENT =
		"SELECT COUNT(dlContent) FROM DLContent dlContent";

	private static final String _SQL_COUNT_DLCONTENT_WHERE =
		"SELECT COUNT(dlContent) FROM DLContent dlContent WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dlContent.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DLContent exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLContent exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLContentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"path", "data", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}