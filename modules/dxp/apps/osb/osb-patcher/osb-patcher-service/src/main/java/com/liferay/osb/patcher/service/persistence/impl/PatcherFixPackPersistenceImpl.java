/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixPackTable;
import com.liferay.osb.patcher.model.impl.PatcherFixPackImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
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
 * The persistence implementation for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPackPersistence.class)
public class PatcherFixPackPersistenceImpl
	extends BasePersistenceImpl<PatcherFixPack>
	implements PatcherFixPackPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixPackUtil</code> to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixPackImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByPatcherBuildId;

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPatcherBuildId(patcherBuildId);

		if (patcherFixPack == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("patcherBuildId=");
			sb.append(patcherBuildId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherFixPackException(sb.toString());
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherBuildId(long patcherBuildId) {
		return fetchByPatcherBuildId(patcherBuildId, true);
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherBuildId(
		long patcherBuildId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {patcherBuildId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByPatcherBuildId, finderArgs, this);
		}

		if (result instanceof PatcherFixPack) {
			PatcherFixPack patcherFixPack = (PatcherFixPack)result;

			if (patcherBuildId != patcherFixPack.getPatcherBuildId()) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PATCHERBUILDID_PATCHERBUILDID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherBuildId);

				List<PatcherFixPack> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByPatcherBuildId, finderArgs, list);
					}
				}
				else {
					PatcherFixPack patcherFixPack = list.get(0);

					result = patcherFixPack;

					cacheResult(patcherFixPack);
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
			return (PatcherFixPack)result;
		}
	}

	/**
	 * Removes the patcher fix pack where patcherBuildId = &#63; from the database.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPatcherBuildId(long patcherBuildId)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPatcherBuildId(patcherBuildId);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherBuildId = &#63;.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPatcherBuildId(long patcherBuildId) {
		PatcherFixPack patcherFixPack = fetchByPatcherBuildId(patcherBuildId);

		if (patcherFixPack == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_PATCHERBUILDID_PATCHERBUILDID_2 =
		"patcherFixPack.patcherBuildId = ?";

	private FinderPath _finderPathWithPaginationFindByPatcherFixComponentId;
	private FinderPath _finderPathWithoutPaginationFindByPatcherFixComponentId;
	private FinderPath _finderPathCountByPatcherFixComponentId;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId) {

		return findByPatcherFixComponentId(
			patcherFixComponentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end) {

		return findByPatcherFixComponentId(
			patcherFixComponentId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPatcherFixComponentId(
			patcherFixComponentId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByPatcherFixComponentId;
				finderArgs = new Object[] {patcherFixComponentId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPatcherFixComponentId;
			finderArgs = new Object[] {
				patcherFixComponentId, start, end, orderByComparator
			};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if (patcherFixComponentId !=
							patcherFixPack.getPatcherFixComponentId()) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPatcherFixComponentId_First(
			long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPatcherFixComponentId_First(
			patcherFixComponentId, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherFixComponentId_First(
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPatcherFixComponentId(
			patcherFixComponentId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPatcherFixComponentId_Last(
			long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPatcherFixComponentId_Last(
			patcherFixComponentId, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPatcherFixComponentId_Last(
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPatcherFixComponentId(patcherFixComponentId);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPatcherFixComponentId(
			patcherFixComponentId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPatcherFixComponentId_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPatcherFixComponentId_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPatcherFixComponentId_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
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

	protected PatcherFixPack getByPatcherFixComponentId_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixComponentId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId) {

		return filterFindByPatcherFixComponentId(
			patcherFixComponentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end) {

		return filterFindByPatcherFixComponentId(
			patcherFixComponentId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherFixComponentId(
				patcherFixComponentId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPatcherFixComponentId(
					patcherFixComponentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPatcherFixComponentId_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPatcherFixComponentId_PrevAndNext(
				patcherFixPackId, patcherFixComponentId, orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPatcherFixComponentId_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPatcherFixComponentId_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
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

	protected PatcherFixPack filterGetByPatcherFixComponentId_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixComponentId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 */
	@Override
	public void removeByPatcherFixComponentId(long patcherFixComponentId) {
		for (PatcherFixPack patcherFixPack :
				findByPatcherFixComponentId(
					patcherFixComponentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPatcherFixComponentId(long patcherFixComponentId) {
		FinderPath finderPath = _finderPathCountByPatcherFixComponentId;

		Object[] finderArgs = new Object[] {patcherFixComponentId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPatcherFixComponentId(long patcherFixComponentId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPatcherFixComponentId(patcherFixComponentId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPatcherFixComponentId(
				patcherFixComponentId);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

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

	private static final String
		_FINDER_COLUMN_PATCHERFIXCOMPONENTID_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ?";

	private FinderPath _finderPathWithPaginationFindByVersion;
	private FinderPath _finderPathWithoutPaginationFindByVersion;
	private FinderPath _finderPathCountByVersion;

	/**
	 * Returns all the patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByVersion(int version) {
		return findByVersion(
			version, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByVersion(int version, int start, int end) {
		return findByVersion(version, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByVersion(version, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByVersion;
				finderArgs = new Object[] {version};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByVersion;
			finderArgs = new Object[] {version, start, end, orderByComparator};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if (version != patcherFixPack.getVersion()) {
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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(version);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByVersion_First(
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByVersion_First(
			version, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByVersion_First(
		int version, OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByVersion(
			version, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByVersion_Last(
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByVersion_Last(
			version, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByVersion_Last(
		int version, OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByVersion(version);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByVersion(
			version, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByVersion_PrevAndNext(
				session, patcherFixPack, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByVersion_PrevAndNext(
				session, patcherFixPack, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack getByVersion_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByVersion(int version) {
		return filterFindByVersion(
			version, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end) {

		return filterFindByVersion(version, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByVersion(version, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByVersion(
					version, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(version);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByVersion_PrevAndNext(
				patcherFixPackId, version, orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByVersion_PrevAndNext(
				session, patcherFixPack, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByVersion_PrevAndNext(
				session, patcherFixPack, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack filterGetByVersion_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where version = &#63; from the database.
	 *
	 * @param version the version
	 */
	@Override
	public void removeByVersion(int version) {
		for (PatcherFixPack patcherFixPack :
				findByVersion(
					version, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByVersion(int version) {
		FinderPath finderPath = _finderPathCountByVersion;

		Object[] finderArgs = new Object[] {version};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(version);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByVersion(int version) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByVersion(version);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByVersion(version);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_VERSION_VERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(version);

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

	private static final String _FINDER_COLUMN_VERSION_VERSION_2 =
		"patcherFixPack.version = ?";

	private FinderPath _finderPathWithPaginationFindByPFCI_PPVI;
	private FinderPath _finderPathWithoutPaginationFindByPFCI_PPVI;
	private FinderPath _finderPathCountByPFCI_PPVI;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end) {

		return findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByPFCI_PPVI;
				finderArgs = new Object[] {
					patcherFixComponentId, patcherProjectVersionId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPFCI_PPVI;
			finderArgs = new Object[] {
				patcherFixComponentId, patcherProjectVersionId, start, end,
				orderByComparator
			};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if ((patcherFixComponentId !=
							patcherFixPack.getPatcherFixComponentId()) ||
						(patcherProjectVersionId !=
							patcherFixPack.getPatcherProjectVersionId())) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_First(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_First(
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_Last(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_Last(
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPFCI_PPVI_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPFCI_PPVI_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack getByPFCI_PPVI_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return filterFindByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end) {

		return filterFindByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI(
				patcherFixComponentId, patcherProjectVersionId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPFCI_PPVI(
					patcherFixComponentId, patcherProjectVersionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI_PrevAndNext(
				patcherFixPackId, patcherFixComponentId,
				patcherProjectVersionId, orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPFCI_PPVI_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPFCI_PPVI_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack filterGetByPFCI_PPVI_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	@Override
	public void removeByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		for (PatcherFixPack patcherFixPack :
				findByPFCI_PPVI(
					patcherFixComponentId, patcherProjectVersionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		FinderPath finderPath = _finderPathCountByPFCI_PPVI;

		Object[] finderArgs = new Object[] {
			patcherFixComponentId, patcherProjectVersionId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPFCI_PPVI(
				patcherFixComponentId, patcherProjectVersionId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPFCI_PPVI(
				patcherFixComponentId, patcherProjectVersionId);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

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

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ?";

	private FinderPath _finderPathWithPaginationFindByPFCI_V;
	private FinderPath _finderPathWithoutPaginationFindByPFCI_V;
	private FinderPath _finderPathCountByPFCI_V;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version) {

		return findByPFCI_V(
			patcherFixComponentId, version, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end) {

		return findByPFCI_V(patcherFixComponentId, version, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_V(
			patcherFixComponentId, version, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByPFCI_V;
				finderArgs = new Object[] {patcherFixComponentId, version};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPFCI_V;
			finderArgs = new Object[] {
				patcherFixComponentId, version, start, end, orderByComparator
			};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if ((patcherFixComponentId !=
							patcherFixPack.getPatcherFixComponentId()) ||
						(version != patcherFixPack.getVersion())) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(version);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_V_First(
			long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_V_First(
			patcherFixComponentId, version, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_V_First(
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPFCI_V(
			patcherFixComponentId, version, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_V_Last(
			long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_V_Last(
			patcherFixComponentId, version, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", version=");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_V_Last(
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPFCI_V(patcherFixComponentId, version);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPFCI_V(
			patcherFixComponentId, version, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPFCI_V_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId, version,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPFCI_V_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId, version,
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

	protected PatcherFixPack getByPFCI_V_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixComponentId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version) {

		return filterFindByPFCI_V(
			patcherFixComponentId, version, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end) {

		return filterFindByPFCI_V(
			patcherFixComponentId, version, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_V(
				patcherFixComponentId, version, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPFCI_V(
					patcherFixComponentId, version, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(version);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_V_PrevAndNext(
				patcherFixPackId, patcherFixComponentId, version,
				orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPFCI_V_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId, version,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPFCI_V_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId, version,
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

	protected PatcherFixPack filterGetByPFCI_V_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixComponentId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_V(long patcherFixComponentId, int version) {
		for (PatcherFixPack patcherFixPack :
				findByPFCI_V(
					patcherFixComponentId, version, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_V(long patcherFixComponentId, int version) {
		FinderPath finderPath = _finderPathCountByPFCI_V;

		Object[] finderArgs = new Object[] {patcherFixComponentId, version};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(version);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_V(long patcherFixComponentId, int version) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPFCI_V(patcherFixComponentId, version);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPFCI_V(
				patcherFixComponentId, version);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_V_VERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(version);

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

	private static final String _FINDER_COLUMN_PFCI_V_PATCHERFIXCOMPONENTID_2 =
		"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_V_VERSION_2 =
		"patcherFixPack.version = ?";

	private FinderPath _finderPathFetchByPFCI_N;

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_N(
			patcherProjectVersionId, name);

		if (patcherFixPack == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("patcherProjectVersionId=");
			sb.append(patcherProjectVersionId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherFixPackException(sb.toString());
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name) {

		return fetchByPFCI_N(patcherProjectVersionId, name, true);
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {patcherProjectVersionId, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByPFCI_N, finderArgs, this);
		}

		if (result instanceof PatcherFixPack) {
			PatcherFixPack patcherFixPack = (PatcherFixPack)result;

			if ((patcherProjectVersionId !=
					patcherFixPack.getPatcherProjectVersionId()) ||
				!Objects.equals(name, patcherFixPack.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_N_PATCHERPROJECTVERSIONID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_PFCI_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_PFCI_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				if (bindName) {
					queryPos.add(name);
				}

				List<PatcherFixPack> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByPFCI_N, finderArgs, list);
					}
				}
				else {
					PatcherFixPack patcherFixPack = list.get(0);

					result = patcherFixPack;

					cacheResult(patcherFixPack);
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
			return (PatcherFixPack)result;
		}
	}

	/**
	 * Removes the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPFCI_N(
			long patcherProjectVersionId, String name)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPFCI_N(
			patcherProjectVersionId, name);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_N(long patcherProjectVersionId, String name) {
		PatcherFixPack patcherFixPack = fetchByPFCI_N(
			patcherProjectVersionId, name);

		if (patcherFixPack == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_PFCI_N_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_N_NAME_2 =
		"patcherFixPack.name = ?";

	private static final String _FINDER_COLUMN_PFCI_N_NAME_3 =
		"(patcherFixPack.name IS NULL OR patcherFixPack.name = '')";

	private FinderPath _finderPathWithPaginationFindByPFCI_S;
	private FinderPath _finderPathWithoutPaginationFindByPFCI_S;
	private FinderPath _finderPathCountByPFCI_S;

	/**
	 * Returns all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status) {

		return findByPFCI_S(
			patcherProjectVersionId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end) {

		return findByPFCI_S(patcherProjectVersionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_S(
			patcherProjectVersionId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByPFCI_S;
				finderArgs = new Object[] {patcherProjectVersionId, status};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByPFCI_S;
			finderArgs = new Object[] {
				patcherProjectVersionId, status, start, end, orderByComparator
			};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if ((patcherProjectVersionId !=
							patcherFixPack.getPatcherProjectVersionId()) ||
						(status != patcherFixPack.getStatus())) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(status);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_S_First(
			long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_S_First(
			patcherProjectVersionId, status, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_S_First(
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPFCI_S(
			patcherProjectVersionId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_S_Last(
			long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_S_Last(
			patcherProjectVersionId, status, orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_S_Last(
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPFCI_S(patcherProjectVersionId, status);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPFCI_S(
			patcherProjectVersionId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPFCI_S_PrevAndNext(
				session, patcherFixPack, patcherProjectVersionId, status,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPFCI_S_PrevAndNext(
				session, patcherFixPack, patcherProjectVersionId, status,
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

	protected PatcherFixPack getByPFCI_S_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status) {

		return filterFindByPFCI_S(
			patcherProjectVersionId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end) {

		return filterFindByPFCI_S(
			patcherProjectVersionId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_S(
				patcherProjectVersionId, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPFCI_S(
					patcherProjectVersionId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(status);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_S_PrevAndNext(
				patcherFixPackId, patcherProjectVersionId, status,
				orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPFCI_S_PrevAndNext(
				session, patcherFixPack, patcherProjectVersionId, status,
				orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPFCI_S_PrevAndNext(
				session, patcherFixPack, patcherProjectVersionId, status,
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

	protected PatcherFixPack filterGetByPFCI_S_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 */
	@Override
	public void removeByPFCI_S(long patcherProjectVersionId, int status) {
		for (PatcherFixPack patcherFixPack :
				findByPFCI_S(
					patcherProjectVersionId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_S(long patcherProjectVersionId, int status) {
		FinderPath finderPath = _finderPathCountByPFCI_S;

		Object[] finderArgs = new Object[] {patcherProjectVersionId, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProjectVersionId);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_S(long patcherProjectVersionId, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPFCI_S(patcherProjectVersionId, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPFCI_S(
				patcherProjectVersionId, status);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherProjectVersionId);

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

	private static final String
		_FINDER_COLUMN_PFCI_S_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_S_STATUS_2 =
		"patcherFixPack.status = ?";

	private FinderPath _finderPathWithPaginationFindByPFCI_PPVI_GtV;
	private FinderPath _finderPathWithPaginationCountByPFCI_PPVI_GtV;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByPFCI_PPVI_GtV;
		finderArgs = new Object[] {
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator
		};

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if ((patcherFixComponentId !=
							patcherFixPack.getPatcherFixComponentId()) ||
						(patcherProjectVersionId !=
							patcherFixPack.getPatcherProjectVersionId()) ||
						(version >= patcherFixPack.getVersion())) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(version);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_GtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_GtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", version>");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_GtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_GtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_GtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", version>");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_GtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPFCI_PPVI_GtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPFCI_PPVI_GtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack getByPFCI_PPVI_GtV_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI_GtV(
				patcherFixComponentId, patcherProjectVersionId, version, start,
				end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPFCI_PPVI_GtV(
					patcherFixComponentId, patcherProjectVersionId, version,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(version);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI_GtV_PrevAndNext(
				patcherFixPackId, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPFCI_PPVI_GtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPFCI_PPVI_GtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack filterGetByPFCI_PPVI_GtV_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		for (PatcherFixPack patcherFixPack :
				findByPFCI_PPVI_GtV(
					patcherFixComponentId, patcherProjectVersionId, version,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		FinderPath finderPath = _finderPathWithPaginationCountByPFCI_PPVI_GtV;

		Object[] finderArgs = new Object[] {
			patcherFixComponentId, patcherProjectVersionId, version
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(version);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPFCI_PPVI_GtV(
				patcherFixComponentId, patcherProjectVersionId, version);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPFCI_PPVI_GtV(
				patcherFixComponentId, patcherProjectVersionId, version);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(version);

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

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_GTV_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_GTV_VERSION_2 =
		"patcherFixPack.version > ?";

	private FinderPath _finderPathWithPaginationFindByPFCI_PPVI_LtV;
	private FinderPath _finderPathWithPaginationCountByPFCI_PPVI_LtV;

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByPFCI_PPVI_LtV;
		finderArgs = new Object[] {
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator
		};

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PatcherFixPack patcherFixPack : list) {
					if ((patcherFixComponentId !=
							patcherFixPack.getPatcherFixComponentId()) ||
						(patcherProjectVersionId !=
							patcherFixPack.getPatcherProjectVersionId()) ||
						(version <= patcherFixPack.getVersion())) {

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

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(version);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_LtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_LtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", version<");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_LtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		List<PatcherFixPack> list = findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_LtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_LtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);

		if (patcherFixPack != null) {
			return patcherFixPack;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("patcherFixComponentId=");
		sb.append(patcherFixComponentId);

		sb.append(", patcherProjectVersionId=");
		sb.append(patcherProjectVersionId);

		sb.append(", version<");
		sb.append(version);

		sb.append("}");

		throw new NoSuchPatcherFixPackException(sb.toString());
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_LtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		int count = countByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);

		if (count == 0) {
			return null;
		}

		List<PatcherFixPack> list = findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] findByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = getByPFCI_PPVI_LtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = getByPFCI_PPVI_LtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack getByPFCI_PPVI_LtV_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

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
			sb.append(PatcherFixPackModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI_LtV(
				patcherFixComponentId, patcherProjectVersionId, version, start,
				end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByPFCI_PPVI_LtV(
					patcherFixComponentId, patcherProjectVersionId, version,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(version);

			return (List<PatcherFixPack>)QueryUtil.list(
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
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack[] filterFindByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws NoSuchPatcherFixPackException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByPFCI_PPVI_LtV_PrevAndNext(
				patcherFixPackId, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator);
		}

		PatcherFixPack patcherFixPack = findByPrimaryKey(patcherFixPackId);

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack[] array = new PatcherFixPackImpl[3];

			array[0] = filterGetByPFCI_PPVI_LtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, true);

			array[1] = patcherFixPack;

			array[2] = filterGetByPFCI_PPVI_LtV_PrevAndNext(
				session, patcherFixPack, patcherFixComponentId,
				patcherProjectVersionId, version, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PatcherFixPack filterGetByPFCI_PPVI_LtV_PrevAndNext(
		Session session, PatcherFixPack patcherFixPack,
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PatcherFixPackModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PatcherFixPackImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PatcherFixPackImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(patcherFixComponentId);

		queryPos.add(patcherProjectVersionId);

		queryPos.add(version);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						patcherFixPack)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PatcherFixPack> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	@Override
	public void removeByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		for (PatcherFixPack patcherFixPack :
				findByPFCI_PPVI_LtV(
					patcherFixComponentId, patcherProjectVersionId, version,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		FinderPath finderPath = _finderPathWithPaginationCountByPFCI_PPVI_LtV;

		Object[] finderArgs = new Object[] {
			patcherFixComponentId, patcherProjectVersionId, version
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				queryPos.add(version);

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

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	@Override
	public int filterCountByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByPFCI_PPVI_LtV(
				patcherFixComponentId, patcherProjectVersionId, version);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<PatcherFixPack> patcherFixPacks = findByPFCI_PPVI_LtV(
				patcherFixComponentId, patcherProjectVersionId, version);

			patcherFixPacks = InlineSQLHelperUtil.filter(patcherFixPacks);

			return patcherFixPacks.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2);

		sb.append(_FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PatcherFixPack.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(patcherFixComponentId);

			queryPos.add(patcherProjectVersionId);

			queryPos.add(version);

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

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_LTV_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_LTV_VERSION_2 =
		"patcherFixPack.version < ?";

	private FinderPath _finderPathFetchByPFCI_PPVI_N_V;

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		if (patcherFixPack == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("patcherFixComponentId=");
			sb.append(patcherFixComponentId);

			sb.append(", patcherProjectVersionId=");
			sb.append(patcherProjectVersionId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherFixPackException(sb.toString());
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		return fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version,
			true);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByPFCI_PPVI_N_V, finderArgs, this);
		}

		if (result instanceof PatcherFixPack) {
			PatcherFixPack patcherFixPack = (PatcherFixPack)result;

			if ((patcherFixComponentId !=
					patcherFixPack.getPatcherFixComponentId()) ||
				(patcherProjectVersionId !=
					patcherFixPack.getPatcherProjectVersionId()) ||
				!Objects.equals(name, patcherFixPack.getName()) ||
				(version != patcherFixPack.getVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2);
			}

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(version);

				List<PatcherFixPack> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByPFCI_PPVI_N_V, finderArgs, list);
					}
				}
				else {
					PatcherFixPack patcherFixPack = list.get(0);

					result = patcherFixPack;

					cacheResult(patcherFixPack);
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
			return (PatcherFixPack)result;
		}
	}

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		if (patcherFixPack == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2 =
		"patcherFixPack.name = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3 =
		"(patcherFixPack.name IS NULL OR patcherFixPack.name = '') AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2 =
		"patcherFixPack.version = ?";

	public PatcherFixPackPersistenceImpl() {
		setModelClass(PatcherFixPack.class);

		setModelImplClass(PatcherFixPackImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixPackTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix pack in the entity cache if it is enabled.
	 *
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void cacheResult(PatcherFixPack patcherFixPack) {
		entityCache.putResult(
			PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey(),
			patcherFixPack);

		finderCache.putResult(
			_finderPathFetchByPatcherBuildId,
			new Object[] {patcherFixPack.getPatcherBuildId()}, patcherFixPack);

		finderCache.putResult(
			_finderPathFetchByPFCI_N,
			new Object[] {
				patcherFixPack.getPatcherProjectVersionId(),
				patcherFixPack.getName()
			},
			patcherFixPack);

		finderCache.putResult(
			_finderPathFetchByPFCI_PPVI_N_V,
			new Object[] {
				patcherFixPack.getPatcherFixComponentId(),
				patcherFixPack.getPatcherProjectVersionId(),
				patcherFixPack.getName(), patcherFixPack.getVersion()
			},
			patcherFixPack);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fix packs in the entity cache if it is enabled.
	 *
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void cacheResult(List<PatcherFixPack> patcherFixPacks) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixPacks.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			if (entityCache.getResult(
					PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey()) ==
						null) {

				cacheResult(patcherFixPack);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix packs.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixPackImpl.class);

		finderCache.clearCache(PatcherFixPackImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix pack.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixPack patcherFixPack) {
		entityCache.removeResult(PatcherFixPackImpl.class, patcherFixPack);
	}

	@Override
	public void clearCache(List<PatcherFixPack> patcherFixPacks) {
		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			entityCache.removeResult(PatcherFixPackImpl.class, patcherFixPack);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixPackImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixPackImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherFixPackModelImpl patcherFixPackModelImpl) {

		Object[] args = new Object[] {
			patcherFixPackModelImpl.getPatcherBuildId()
		};

		finderCache.putResult(
			_finderPathFetchByPatcherBuildId, args, patcherFixPackModelImpl);

		args = new Object[] {
			patcherFixPackModelImpl.getPatcherProjectVersionId(),
			patcherFixPackModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByPFCI_N, args, patcherFixPackModelImpl);

		args = new Object[] {
			patcherFixPackModelImpl.getPatcherFixComponentId(),
			patcherFixPackModelImpl.getPatcherProjectVersionId(),
			patcherFixPackModelImpl.getName(),
			patcherFixPackModelImpl.getVersion()
		};

		finderCache.putResult(
			_finderPathFetchByPFCI_PPVI_N_V, args, patcherFixPackModelImpl);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Override
	public PatcherFixPack create(long patcherFixPackId) {
		PatcherFixPack patcherFixPack = new PatcherFixPackImpl();

		patcherFixPack.setNew(true);
		patcherFixPack.setPrimaryKey(patcherFixPackId);

		patcherFixPack.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixPack;
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack remove(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return remove((Serializable)patcherFixPackId);
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack remove(Serializable primaryKey)
		throws NoSuchPatcherFixPackException {

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack patcherFixPack = (PatcherFixPack)session.get(
				PatcherFixPackImpl.class, primaryKey);

			if (patcherFixPack == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixPackException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFixPack);
		}
		catch (NoSuchPatcherFixPackException noSuchEntityException) {
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
	protected PatcherFixPack removeImpl(PatcherFixPack patcherFixPack) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFixPack.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixPack)) {
				patcherFixPack = (PatcherFixPack)session.get(
					PatcherFixPackImpl.class,
					patcherFixPack.getPrimaryKeyObj());
			}

			if (patcherFixPack != null) {
				session.delete(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixPack != null) {
			clearCache(patcherFixPack);
		}

		return patcherFixPack;
	}

	@Override
	public PatcherFixPack updateImpl(PatcherFixPack patcherFixPack) {
		boolean isNew = patcherFixPack.isNew();

		if (!(patcherFixPack instanceof PatcherFixPackModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixPack.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixPack);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixPack proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixPack implementation " +
					patcherFixPack.getClass());
		}

		PatcherFixPackModelImpl patcherFixPackModelImpl =
			(PatcherFixPackModelImpl)patcherFixPack;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFixPack.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFixPack.setCreateDate(date);
			}
			else {
				patcherFixPack.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixPackModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFixPack.setModifiedDate(date);
			}
			else {
				patcherFixPack.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixPack);
			}
			else {
				patcherFixPack = (PatcherFixPack)session.merge(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixPackImpl.class, patcherFixPackModelImpl, false, true);

		cacheUniqueFindersCache(patcherFixPackModelImpl);

		if (isNew) {
			patcherFixPack.setNew(false);
		}

		patcherFixPack.resetOriginalValues();

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(primaryKey);

		if (patcherFixPack == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixPackException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return findByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack fetchByPrimaryKey(long patcherFixPackId) {
		return fetchByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns all the patcher fix packs.
	 *
	 * @return the patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
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

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIXPACK);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXPACK;

				sql = sql.concat(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFixPack>)QueryUtil.list(
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
	 * Removes all the patcher fix packs from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFixPack patcherFixPack : findAll()) {
			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIXPACK);

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

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(long pk) {
		return getPatcherFixPatcherFixPacks(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end) {

		return getPatcherFixPatcherFixPacks(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return patcherFixPackToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherFixPackToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher fix pack has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixes(long pk) {
		if (getPatcherFixesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		long[] addedKeys =
			patcherFixPackToPatcherFixTableMapper.addTableMappings(
				companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		removePatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher fix pack
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		patcherFixPackToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes to be associated with the patcher fix pack
	 */
	@Override
	public void setPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		try {
			long[] patcherFixPKs = new long[patcherFixes.size()];

			for (int i = 0; i < patcherFixes.size(); i++) {
				PatcherFix patcherFix = patcherFixes.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixes(pk, patcherFixPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixPackId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXPACK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixPackModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix pack persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherFixPackToPatcherFixTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixPackId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixPackId",
				"patcherFixId", this, PatcherFix.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByPatcherBuildId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPatcherBuildId",
			new String[] {Long.class.getName()},
			new String[] {"patcherBuildId"}, true);

		_finderPathWithPaginationFindByPatcherFixComponentId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByPatcherFixComponentId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"patcherFixComponentId"}, true);

		_finderPathWithoutPaginationFindByPatcherFixComponentId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByPatcherFixComponentId",
				new String[] {Long.class.getName()},
				new String[] {"patcherFixComponentId"}, true);

		_finderPathCountByPatcherFixComponentId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPatcherFixComponentId", new String[] {Long.class.getName()},
			new String[] {"patcherFixComponentId"}, false);

		_finderPathWithPaginationFindByVersion = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByVersion",
			new String[] {
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"version"}, true);

		_finderPathWithoutPaginationFindByVersion = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByVersion",
			new String[] {Integer.class.getName()}, new String[] {"version"},
			true);

		_finderPathCountByVersion = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByVersion",
			new String[] {Integer.class.getName()}, new String[] {"version"},
			false);

		_finderPathWithPaginationFindByPFCI_PPVI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_PPVI",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherFixComponentId", "patcherProjectVersionId"},
			true);

		_finderPathWithoutPaginationFindByPFCI_PPVI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPFCI_PPVI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"patcherFixComponentId", "patcherProjectVersionId"},
			true);

		_finderPathCountByPFCI_PPVI = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_PPVI",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"patcherFixComponentId", "patcherProjectVersionId"},
			false);

		_finderPathWithPaginationFindByPFCI_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_V",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherFixComponentId", "version"}, true);

		_finderPathWithoutPaginationFindByPFCI_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPFCI_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"patcherFixComponentId", "version"}, true);

		_finderPathCountByPFCI_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_V",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"patcherFixComponentId", "version"}, false);

		_finderPathFetchByPFCI_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPFCI_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"patcherProjectVersionId", "name"}, true);

		_finderPathWithPaginationFindByPFCI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"patcherProjectVersionId", "status"}, true);

		_finderPathWithoutPaginationFindByPFCI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPFCI_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"patcherProjectVersionId", "status"}, true);

		_finderPathCountByPFCI_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"patcherProjectVersionId", "status"}, false);

		_finderPathWithPaginationFindByPFCI_PPVI_GtV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_PPVI_GtV",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "version"
			},
			true);

		_finderPathWithPaginationCountByPFCI_PPVI_GtV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByPFCI_PPVI_GtV",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "version"
			},
			false);

		_finderPathWithPaginationFindByPFCI_PPVI_LtV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPFCI_PPVI_LtV",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "version"
			},
			true);

		_finderPathWithPaginationCountByPFCI_PPVI_LtV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByPFCI_PPVI_LtV",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "version"
			},
			false);

		_finderPathFetchByPFCI_PPVI_N_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPFCI_PPVI_N_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "name",
				"version"
			},
			true);

		PatcherFixPackUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixPackUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixPackImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixPackId");
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

	protected TableMapper<PatcherFixPack, PatcherFix>
		patcherFixPackToPatcherFixTableMapper;

	private static final String _SQL_SELECT_PATCHERFIXPACK =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack";

	private static final String _SQL_SELECT_PATCHERFIXPACK_WHERE =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _SQL_COUNT_PATCHERFIXPACK =
		"SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack";

	private static final String _SQL_COUNT_PATCHERFIXPACK_WHERE =
		"SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"patcherFixPack.patcherFixPackId";

	private static final String _FILTER_SQL_SELECT_PATCHERFIXPACK_WHERE =
		"SELECT DISTINCT {patcherFixPack.*} FROM OSBPatcher_PatcherFixPack patcherFixPack WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OSBPatcher_PatcherFixPack.*} FROM (SELECT DISTINCT patcherFixPack.patcherFixPackId FROM OSBPatcher_PatcherFixPack patcherFixPack WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PATCHERFIXPACK_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OSBPatcher_PatcherFixPack ON TEMP_TABLE.patcherFixPackId = OSBPatcher_PatcherFixPack.patcherFixPackId";

	private static final String _FILTER_SQL_COUNT_PATCHERFIXPACK_WHERE =
		"SELECT COUNT(DISTINCT patcherFixPack.patcherFixPackId) AS COUNT_VALUE FROM OSBPatcher_PatcherFixPack patcherFixPack WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "patcherFixPack";

	private static final String _FILTER_ENTITY_TABLE =
		"OSBPatcher_PatcherFixPack";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixPack.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OSBPatcher_PatcherFixPack.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFixPack exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFixPack exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPackPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}