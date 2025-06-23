/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.model.PatcherFixRelTable;
import com.liferay.osb.patcher.model.impl.PatcherFixRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixRelPersistence.class)
public class PatcherFixRelPersistenceImpl
	extends BasePersistenceImpl<PatcherFixRel>
	implements PatcherFixRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixRelUtil</code> to access the patcher fix rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByChildPatcherFixId;
	private FinderPath _finderPathWithoutPaginationFindByChildPatcherFixId;
	private FinderPath _finderPathCountByChildPatcherFixId;

	/**
	 * Returns all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByChildPatcherFixId(long childPatcherFixId) {
		return findByChildPatcherFixId(
			childPatcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end) {

		return findByChildPatcherFixId(childPatcherFixId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return findByChildPatcherFixId(
			childPatcherFixId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByChildPatcherFixId;
				finderArgs = new Object[] {childPatcherFixId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByChildPatcherFixId;
			finderArgs = new Object[] {
				childPatcherFixId, start, end, orderByComparator
			};
		}

		List<PatcherFixRel> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixRel>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixRel patcherFixRel : list) {
					if (childPatcherFixId !=
							patcherFixRel.getChildPatcherFixId()) {

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

			sb.append(_SQL_SELECT_PATCHERFIXREL_WHERE);

			sb.append(_FINDER_COLUMN_CHILDPATCHERFIXID_CHILDPATCHERFIXID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixRelModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(childPatcherFixId);

				list = (List<PatcherFixRel>)QueryUtil.list(
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
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByChildPatcherFixId_First(
			long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByChildPatcherFixId_First(
			childPatcherFixId, orderByComparator);

		if (patcherFixRel != null) {
			return patcherFixRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("childPatcherFixId=");
		sb.append(childPatcherFixId);

		sb.append("}");

		throw new NoSuchPatcherFixRelException(sb.toString());
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByChildPatcherFixId_First(
		long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		List<PatcherFixRel> list = findByChildPatcherFixId(
			childPatcherFixId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByChildPatcherFixId_Last(
			long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByChildPatcherFixId_Last(
			childPatcherFixId, orderByComparator);

		if (patcherFixRel != null) {
			return patcherFixRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("childPatcherFixId=");
		sb.append(childPatcherFixId);

		sb.append("}");

		throw new NoSuchPatcherFixRelException(sb.toString());
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByChildPatcherFixId_Last(
		long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		int count = countByChildPatcherFixId(childPatcherFixId);

		if (count == 0) {
			return null;
		}

		List<PatcherFixRel> list = findByChildPatcherFixId(
			childPatcherFixId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel[] findByChildPatcherFixId_PrevAndNext(
			long patcherFixRelId, long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = findByPrimaryKey(patcherFixRelId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixRel[] array = new PatcherFixRelImpl[3];

			array[0] = getByChildPatcherFixId_PrevAndNext(
				session, patcherFixRel, childPatcherFixId, orderByComparator,
				true);

			array[1] = patcherFixRel;

			array[2] = getByChildPatcherFixId_PrevAndNext(
				session, patcherFixRel, childPatcherFixId, orderByComparator,
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

	protected PatcherFixRel getByChildPatcherFixId_PrevAndNext(
		Session session, PatcherFixRel patcherFixRel, long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERFIXREL_WHERE);

		sb.append(_FINDER_COLUMN_CHILDPATCHERFIXID_CHILDPATCHERFIXID_2);

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
			sb.append(PatcherFixRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(childPatcherFixId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix rels where childPatcherFixId = &#63; from the database.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 */
	@Override
	public void removeByChildPatcherFixId(long childPatcherFixId) {
		for (PatcherFixRel patcherFixRel :
				findByChildPatcherFixId(
					childPatcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFixRel);
		}
	}

	/**
	 * Returns the number of patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	@Override
	public int countByChildPatcherFixId(long childPatcherFixId) {
		FinderPath finderPath = _finderPathCountByChildPatcherFixId;

		Object[] finderArgs = new Object[] {childPatcherFixId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERFIXREL_WHERE);

			sb.append(_FINDER_COLUMN_CHILDPATCHERFIXID_CHILDPATCHERFIXID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(childPatcherFixId);

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
		_FINDER_COLUMN_CHILDPATCHERFIXID_CHILDPATCHERFIXID_2 =
			"patcherFixRel.childPatcherFixId = ?";

	private FinderPath _finderPathWithPaginationFindByParentPatcherFixId;
	private FinderPath _finderPathWithoutPaginationFindByParentPatcherFixId;
	private FinderPath _finderPathCountByParentPatcherFixId;

	/**
	 * Returns all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId) {

		return findByParentPatcherFixId(
			parentPatcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end) {

		return findByParentPatcherFixId(parentPatcherFixId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return findByParentPatcherFixId(
			parentPatcherFixId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByParentPatcherFixId;
				finderArgs = new Object[] {parentPatcherFixId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByParentPatcherFixId;
			finderArgs = new Object[] {
				parentPatcherFixId, start, end, orderByComparator
			};
		}

		List<PatcherFixRel> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixRel>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixRel patcherFixRel : list) {
					if (parentPatcherFixId !=
							patcherFixRel.getParentPatcherFixId()) {

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

			sb.append(_SQL_SELECT_PATCHERFIXREL_WHERE);

			sb.append(_FINDER_COLUMN_PARENTPATCHERFIXID_PARENTPATCHERFIXID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixRelModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(parentPatcherFixId);

				list = (List<PatcherFixRel>)QueryUtil.list(
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
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByParentPatcherFixId_First(
			long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByParentPatcherFixId_First(
			parentPatcherFixId, orderByComparator);

		if (patcherFixRel != null) {
			return patcherFixRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentPatcherFixId=");
		sb.append(parentPatcherFixId);

		sb.append("}");

		throw new NoSuchPatcherFixRelException(sb.toString());
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByParentPatcherFixId_First(
		long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		List<PatcherFixRel> list = findByParentPatcherFixId(
			parentPatcherFixId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel findByParentPatcherFixId_Last(
			long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByParentPatcherFixId_Last(
			parentPatcherFixId, orderByComparator);

		if (patcherFixRel != null) {
			return patcherFixRel;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentPatcherFixId=");
		sb.append(parentPatcherFixId);

		sb.append("}");

		throw new NoSuchPatcherFixRelException(sb.toString());
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	@Override
	public PatcherFixRel fetchByParentPatcherFixId_Last(
		long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		int count = countByParentPatcherFixId(parentPatcherFixId);

		if (count == 0) {
			return null;
		}

		List<PatcherFixRel> list = findByParentPatcherFixId(
			parentPatcherFixId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel[] findByParentPatcherFixId_PrevAndNext(
			long patcherFixRelId, long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = findByPrimaryKey(patcherFixRelId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixRel[] array = new PatcherFixRelImpl[3];

			array[0] = getByParentPatcherFixId_PrevAndNext(
				session, patcherFixRel, parentPatcherFixId, orderByComparator,
				true);

			array[1] = patcherFixRel;

			array[2] = getByParentPatcherFixId_PrevAndNext(
				session, patcherFixRel, parentPatcherFixId, orderByComparator,
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

	protected PatcherFixRel getByParentPatcherFixId_PrevAndNext(
		Session session, PatcherFixRel patcherFixRel, long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERFIXREL_WHERE);

		sb.append(_FINDER_COLUMN_PARENTPATCHERFIXID_PARENTPATCHERFIXID_2);

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
			sb.append(PatcherFixRelModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentPatcherFixId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixRel)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixRel> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix rels where parentPatcherFixId = &#63; from the database.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 */
	@Override
	public void removeByParentPatcherFixId(long parentPatcherFixId) {
		for (PatcherFixRel patcherFixRel :
				findByParentPatcherFixId(
					parentPatcherFixId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFixRel);
		}
	}

	/**
	 * Returns the number of patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	@Override
	public int countByParentPatcherFixId(long parentPatcherFixId) {
		FinderPath finderPath = _finderPathCountByParentPatcherFixId;

		Object[] finderArgs = new Object[] {parentPatcherFixId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERFIXREL_WHERE);

			sb.append(_FINDER_COLUMN_PARENTPATCHERFIXID_PARENTPATCHERFIXID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(parentPatcherFixId);

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
		_FINDER_COLUMN_PARENTPATCHERFIXID_PARENTPATCHERFIXID_2 =
			"patcherFixRel.parentPatcherFixId = ?";

	public PatcherFixRelPersistenceImpl() {
		setModelClass(PatcherFixRel.class);

		setModelImplClass(PatcherFixRelImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixRelTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix rel in the entity cache if it is enabled.
	 *
	 * @param patcherFixRel the patcher fix rel
	 */
	@Override
	public void cacheResult(PatcherFixRel patcherFixRel) {
		entityCache.putResult(
			PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey(),
			patcherFixRel);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fix rels in the entity cache if it is enabled.
	 *
	 * @param patcherFixRels the patcher fix rels
	 */
	@Override
	public void cacheResult(List<PatcherFixRel> patcherFixRels) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixRels.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			if (entityCache.getResult(
					PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey()) ==
						null) {

				cacheResult(patcherFixRel);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixRelImpl.class);

		finderCache.clearCache(PatcherFixRelImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixRel patcherFixRel) {
		entityCache.removeResult(PatcherFixRelImpl.class, patcherFixRel);
	}

	@Override
	public void clearCache(List<PatcherFixRel> patcherFixRels) {
		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			entityCache.removeResult(PatcherFixRelImpl.class, patcherFixRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixRelImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	 *
	 * @param patcherFixRelId the primary key for the new patcher fix rel
	 * @return the new patcher fix rel
	 */
	@Override
	public PatcherFixRel create(long patcherFixRelId) {
		PatcherFixRel patcherFixRel = new PatcherFixRelImpl();

		patcherFixRel.setNew(true);
		patcherFixRel.setPrimaryKey(patcherFixRelId);

		patcherFixRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixRel;
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel remove(long patcherFixRelId)
		throws NoSuchPatcherFixRelException {

		return remove((Serializable)patcherFixRelId);
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel remove(Serializable primaryKey)
		throws NoSuchPatcherFixRelException {

		Session session = null;

		try {
			session = openSession();

			PatcherFixRel patcherFixRel = (PatcherFixRel)session.get(
				PatcherFixRelImpl.class, primaryKey);

			if (patcherFixRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFixRel);
		}
		catch (NoSuchPatcherFixRelException noSuchEntityException) {
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
	protected PatcherFixRel removeImpl(PatcherFixRel patcherFixRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixRel)) {
				patcherFixRel = (PatcherFixRel)session.get(
					PatcherFixRelImpl.class, patcherFixRel.getPrimaryKeyObj());
			}

			if (patcherFixRel != null) {
				session.delete(patcherFixRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixRel != null) {
			clearCache(patcherFixRel);
		}

		return patcherFixRel;
	}

	@Override
	public PatcherFixRel updateImpl(PatcherFixRel patcherFixRel) {
		boolean isNew = patcherFixRel.isNew();

		if (!(patcherFixRel instanceof PatcherFixRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixRel implementation " +
					patcherFixRel.getClass());
		}

		PatcherFixRelModelImpl patcherFixRelModelImpl =
			(PatcherFixRelModelImpl)patcherFixRel;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixRel);
			}
			else {
				patcherFixRel = (PatcherFixRel)session.merge(patcherFixRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixRelImpl.class, patcherFixRelModelImpl, false, true);

		if (isNew) {
			patcherFixRel.setNew(false);
		}

		patcherFixRel.resetOriginalValues();

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixRelException {

		PatcherFixRel patcherFixRel = fetchByPrimaryKey(primaryKey);

		if (patcherFixRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>NoSuchPatcherFixRelException</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(long patcherFixRelId)
		throws NoSuchPatcherFixRelException {

		return findByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 */
	@Override
	public PatcherFixRel fetchByPrimaryKey(long patcherFixRelId) {
		return fetchByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns all the patcher fix rels.
	 *
	 * @return the patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(
		int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix rels
	 */
	@Override
	public List<PatcherFixRel> findAll(
		int start, int end, OrderByComparator<PatcherFixRel> orderByComparator,
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

		List<PatcherFixRel> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixRel>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIXREL);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXREL;

				sql = sql.concat(PatcherFixRelModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFixRel>)QueryUtil.list(
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
	 * Removes all the patcher fix rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFixRel patcherFixRel : findAll()) {
			remove(patcherFixRel);
		}
	}

	/**
	 * Returns the number of patcher fix rels.
	 *
	 * @return the number of patcher fix rels
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIXREL);

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
		return "patcherFixRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix rel persistence.
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

		_finderPathWithPaginationFindByChildPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByChildPatcherFixId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"childPatcherFixId"}, true);

		_finderPathWithoutPaginationFindByChildPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByChildPatcherFixId", new String[] {Long.class.getName()},
			new String[] {"childPatcherFixId"}, true);

		_finderPathCountByChildPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByChildPatcherFixId", new String[] {Long.class.getName()},
			new String[] {"childPatcherFixId"}, false);

		_finderPathWithPaginationFindByParentPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentPatcherFixId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentPatcherFixId"}, true);

		_finderPathWithoutPaginationFindByParentPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByParentPatcherFixId", new String[] {Long.class.getName()},
			new String[] {"parentPatcherFixId"}, true);

		_finderPathCountByParentPatcherFixId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByParentPatcherFixId", new String[] {Long.class.getName()},
			new String[] {"parentPatcherFixId"}, false);

		PatcherFixRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixRelUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PATCHERFIXREL =
		"SELECT patcherFixRel FROM PatcherFixRel patcherFixRel";

	private static final String _SQL_SELECT_PATCHERFIXREL_WHERE =
		"SELECT patcherFixRel FROM PatcherFixRel patcherFixRel WHERE ";

	private static final String _SQL_COUNT_PATCHERFIXREL =
		"SELECT COUNT(patcherFixRel) FROM PatcherFixRel patcherFixRel";

	private static final String _SQL_COUNT_PATCHERFIXREL_WHERE =
		"SELECT COUNT(patcherFixRel) FROM PatcherFixRel patcherFixRel WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFixRel exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFixRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}