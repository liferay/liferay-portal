/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchPeerBindingException;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.model.SamlPeerBindingTable;
import com.liferay.saml.persistence.model.impl.SamlPeerBindingImpl;
import com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingPersistence;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
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
 * The persistence implementation for the saml peer binding service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlPeerBindingPersistence.class)
public class SamlPeerBindingPersistenceImpl
	extends BasePersistenceImpl<SamlPeerBinding>
	implements SamlPeerBindingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlPeerBindingUtil</code> to access the saml peer binding persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlPeerBindingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_D_SNIV;
	private FinderPath _finderPathWithoutPaginationFindByC_D_SNIV;
	private FinderPath _finderPathCountByC_D_SNIV;

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		return findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end) {

		return findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end, OrderByComparator<SamlPeerBinding> orderByComparator) {

		return findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end, OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		samlNameIdValue = Objects.toString(samlNameIdValue, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_D_SNIV;
				finderArgs = new Object[] {companyId, deleted, samlNameIdValue};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_D_SNIV;
			finderArgs = new Object[] {
				companyId, deleted, samlNameIdValue, start, end,
				orderByComparator
			};
		}

		List<SamlPeerBinding> list = null;

		if (useFinderCache) {
			list = (List<SamlPeerBinding>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (SamlPeerBinding samlPeerBinding : list) {
					if ((companyId != samlPeerBinding.getCompanyId()) ||
						(deleted != samlPeerBinding.isDeleted()) ||
						!samlNameIdValue.equals(
							samlPeerBinding.getSamlNameIdValue())) {

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

			sb.append(_SQL_SELECT_SAMLPEERBINDING_WHERE);

			sb.append(_FINDER_COLUMN_C_D_SNIV_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_D_SNIV_DELETED_2);

			boolean bindSamlNameIdValue = false;

			if (samlNameIdValue.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_3);
			}
			else {
				bindSamlNameIdValue = true;

				sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(SamlPeerBindingModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(deleted);

				if (bindSamlNameIdValue) {
					queryPos.add(samlNameIdValue);
				}

				list = (List<SamlPeerBinding>)QueryUtil.list(
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
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_D_SNIV_First(
			long companyId, boolean deleted, String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		SamlPeerBinding samlPeerBinding = fetchByC_D_SNIV_First(
			companyId, deleted, samlNameIdValue, orderByComparator);

		if (samlPeerBinding != null) {
			return samlPeerBinding;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", deleted=");
		sb.append(deleted);

		sb.append(", samlNameIdValue=");
		sb.append(samlNameIdValue);

		sb.append("}");

		throw new NoSuchPeerBindingException(sb.toString());
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_D_SNIV_First(
		long companyId, boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		List<SamlPeerBinding> list = findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_D_SNIV_Last(
			long companyId, boolean deleted, String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		SamlPeerBinding samlPeerBinding = fetchByC_D_SNIV_Last(
			companyId, deleted, samlNameIdValue, orderByComparator);

		if (samlPeerBinding != null) {
			return samlPeerBinding;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", deleted=");
		sb.append(deleted);

		sb.append(", samlNameIdValue=");
		sb.append(samlNameIdValue);

		sb.append("}");

		throw new NoSuchPeerBindingException(sb.toString());
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_D_SNIV_Last(
		long companyId, boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		int count = countByC_D_SNIV(companyId, deleted, samlNameIdValue);

		if (count == 0) {
			return null;
		}

		List<SamlPeerBinding> list = findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saml peer bindings before and after the current saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param samlPeerBindingId the primary key of the current saml peer binding
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding[] findByC_D_SNIV_PrevAndNext(
			long samlPeerBindingId, long companyId, boolean deleted,
			String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		samlNameIdValue = Objects.toString(samlNameIdValue, "");

		SamlPeerBinding samlPeerBinding = findByPrimaryKey(samlPeerBindingId);

		Session session = null;

		try {
			session = openSession();

			SamlPeerBinding[] array = new SamlPeerBindingImpl[3];

			array[0] = getByC_D_SNIV_PrevAndNext(
				session, samlPeerBinding, companyId, deleted, samlNameIdValue,
				orderByComparator, true);

			array[1] = samlPeerBinding;

			array[2] = getByC_D_SNIV_PrevAndNext(
				session, samlPeerBinding, companyId, deleted, samlNameIdValue,
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

	protected SamlPeerBinding getByC_D_SNIV_PrevAndNext(
		Session session, SamlPeerBinding samlPeerBinding, long companyId,
		boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator,
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

		sb.append(_SQL_SELECT_SAMLPEERBINDING_WHERE);

		sb.append(_FINDER_COLUMN_C_D_SNIV_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_D_SNIV_DELETED_2);

		boolean bindSamlNameIdValue = false;

		if (samlNameIdValue.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_3);
		}
		else {
			bindSamlNameIdValue = true;

			sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_2);
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
			sb.append(SamlPeerBindingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(deleted);

		if (bindSamlNameIdValue) {
			queryPos.add(samlNameIdValue);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						samlPeerBinding)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SamlPeerBinding> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 */
	@Override
	public void removeByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		for (SamlPeerBinding samlPeerBinding :
				findByC_D_SNIV(
					companyId, deleted, samlNameIdValue, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(samlPeerBinding);
		}
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the number of matching saml peer bindings
	 */
	@Override
	public int countByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		samlNameIdValue = Objects.toString(samlNameIdValue, "");

		FinderPath finderPath = _finderPathCountByC_D_SNIV;

		Object[] finderArgs = new Object[] {
			companyId, deleted, samlNameIdValue
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_SAMLPEERBINDING_WHERE);

			sb.append(_FINDER_COLUMN_C_D_SNIV_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_D_SNIV_DELETED_2);

			boolean bindSamlNameIdValue = false;

			if (samlNameIdValue.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_3);
			}
			else {
				bindSamlNameIdValue = true;

				sb.append(_FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(deleted);

				if (bindSamlNameIdValue) {
					queryPos.add(samlNameIdValue);
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

	private static final String _FINDER_COLUMN_C_D_SNIV_COMPANYID_2 =
		"samlPeerBinding.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_D_SNIV_DELETED_2 =
		"samlPeerBinding.deleted = ? AND ";

	private static final String _FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_2 =
		"samlPeerBinding.samlNameIdValue = ?";

	private static final String _FINDER_COLUMN_C_D_SNIV_SAMLNAMEIDVALUE_3 =
		"(samlPeerBinding.samlNameIdValue IS NULL OR samlPeerBinding.samlNameIdValue = '')";

	private FinderPath _finderPathWithPaginationFindByC_U_SPEI_D;
	private FinderPath _finderPathWithoutPaginationFindByC_U_SPEI_D;
	private FinderPath _finderPathCountByC_U_SPEI_D;

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		return findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end) {

		return findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		samlPeerEntityId = Objects.toString(samlPeerEntityId, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_U_SPEI_D;
				finderArgs = new Object[] {
					companyId, userId, samlPeerEntityId, deleted
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_U_SPEI_D;
			finderArgs = new Object[] {
				companyId, userId, samlPeerEntityId, deleted, start, end,
				orderByComparator
			};
		}

		List<SamlPeerBinding> list = null;

		if (useFinderCache) {
			list = (List<SamlPeerBinding>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (SamlPeerBinding samlPeerBinding : list) {
					if ((companyId != samlPeerBinding.getCompanyId()) ||
						(userId != samlPeerBinding.getUserId()) ||
						!samlPeerEntityId.equals(
							samlPeerBinding.getSamlPeerEntityId()) ||
						(deleted != samlPeerBinding.isDeleted())) {

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

			sb.append(_SQL_SELECT_SAMLPEERBINDING_WHERE);

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_USERID_2);

			boolean bindSamlPeerEntityId = false;

			if (samlPeerEntityId.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_3);
			}
			else {
				bindSamlPeerEntityId = true;

				sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_2);
			}

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_DELETED_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(SamlPeerBindingModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				if (bindSamlPeerEntityId) {
					queryPos.add(samlPeerEntityId);
				}

				queryPos.add(deleted);

				list = (List<SamlPeerBinding>)QueryUtil.list(
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
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_U_SPEI_D_First(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		SamlPeerBinding samlPeerBinding = fetchByC_U_SPEI_D_First(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);

		if (samlPeerBinding != null) {
			return samlPeerBinding;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", samlPeerEntityId=");
		sb.append(samlPeerEntityId);

		sb.append(", deleted=");
		sb.append(deleted);

		sb.append("}");

		throw new NoSuchPeerBindingException(sb.toString());
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_U_SPEI_D_First(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		List<SamlPeerBinding> list = findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_U_SPEI_D_Last(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		SamlPeerBinding samlPeerBinding = fetchByC_U_SPEI_D_Last(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);

		if (samlPeerBinding != null) {
			return samlPeerBinding;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", samlPeerEntityId=");
		sb.append(samlPeerEntityId);

		sb.append(", deleted=");
		sb.append(deleted);

		sb.append("}");

		throw new NoSuchPeerBindingException(sb.toString());
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_U_SPEI_D_Last(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		int count = countByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted);

		if (count == 0) {
			return null;
		}

		List<SamlPeerBinding> list = findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the saml peer bindings before and after the current saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param samlPeerBindingId the primary key of the current saml peer binding
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding[] findByC_U_SPEI_D_PrevAndNext(
			long samlPeerBindingId, long companyId, long userId,
			String samlPeerEntityId, boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		samlPeerEntityId = Objects.toString(samlPeerEntityId, "");

		SamlPeerBinding samlPeerBinding = findByPrimaryKey(samlPeerBindingId);

		Session session = null;

		try {
			session = openSession();

			SamlPeerBinding[] array = new SamlPeerBindingImpl[3];

			array[0] = getByC_U_SPEI_D_PrevAndNext(
				session, samlPeerBinding, companyId, userId, samlPeerEntityId,
				deleted, orderByComparator, true);

			array[1] = samlPeerBinding;

			array[2] = getByC_U_SPEI_D_PrevAndNext(
				session, samlPeerBinding, companyId, userId, samlPeerEntityId,
				deleted, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected SamlPeerBinding getByC_U_SPEI_D_PrevAndNext(
		Session session, SamlPeerBinding samlPeerBinding, long companyId,
		long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator,
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

		sb.append(_SQL_SELECT_SAMLPEERBINDING_WHERE);

		sb.append(_FINDER_COLUMN_C_U_SPEI_D_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_SPEI_D_USERID_2);

		boolean bindSamlPeerEntityId = false;

		if (samlPeerEntityId.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_3);
		}
		else {
			bindSamlPeerEntityId = true;

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_2);
		}

		sb.append(_FINDER_COLUMN_C_U_SPEI_D_DELETED_2);

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
			sb.append(SamlPeerBindingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(userId);

		if (bindSamlPeerEntityId) {
			queryPos.add(samlPeerEntityId);
		}

		queryPos.add(deleted);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						samlPeerBinding)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<SamlPeerBinding> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 */
	@Override
	public void removeByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		for (SamlPeerBinding samlPeerBinding :
				findByC_U_SPEI_D(
					companyId, userId, samlPeerEntityId, deleted,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(samlPeerBinding);
		}
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the number of matching saml peer bindings
	 */
	@Override
	public int countByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		samlPeerEntityId = Objects.toString(samlPeerEntityId, "");

		FinderPath finderPath = _finderPathCountByC_U_SPEI_D;

		Object[] finderArgs = new Object[] {
			companyId, userId, samlPeerEntityId, deleted
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_COUNT_SAMLPEERBINDING_WHERE);

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_USERID_2);

			boolean bindSamlPeerEntityId = false;

			if (samlPeerEntityId.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_3);
			}
			else {
				bindSamlPeerEntityId = true;

				sb.append(_FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_2);
			}

			sb.append(_FINDER_COLUMN_C_U_SPEI_D_DELETED_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				if (bindSamlPeerEntityId) {
					queryPos.add(samlPeerEntityId);
				}

				queryPos.add(deleted);

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

	private static final String _FINDER_COLUMN_C_U_SPEI_D_COMPANYID_2 =
		"samlPeerBinding.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_SPEI_D_USERID_2 =
		"samlPeerBinding.userId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_2 =
		"samlPeerBinding.samlPeerEntityId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_SPEI_D_SAMLPEERENTITYID_3 =
		"(samlPeerBinding.samlPeerEntityId IS NULL OR samlPeerBinding.samlPeerEntityId = '') AND ";

	private static final String _FINDER_COLUMN_C_U_SPEI_D_DELETED_2 =
		"samlPeerBinding.deleted = ?";

	public SamlPeerBindingPersistenceImpl() {
		setModelClass(SamlPeerBinding.class);

		setModelImplClass(SamlPeerBindingImpl.class);
		setModelPKClass(long.class);

		setTable(SamlPeerBindingTable.INSTANCE);
	}

	/**
	 * Caches the saml peer binding in the entity cache if it is enabled.
	 *
	 * @param samlPeerBinding the saml peer binding
	 */
	@Override
	public void cacheResult(SamlPeerBinding samlPeerBinding) {
		entityCache.putResult(
			SamlPeerBindingImpl.class, samlPeerBinding.getPrimaryKey(),
			samlPeerBinding);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the saml peer bindings in the entity cache if it is enabled.
	 *
	 * @param samlPeerBindings the saml peer bindings
	 */
	@Override
	public void cacheResult(List<SamlPeerBinding> samlPeerBindings) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (samlPeerBindings.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (SamlPeerBinding samlPeerBinding : samlPeerBindings) {
			if (entityCache.getResult(
					SamlPeerBindingImpl.class,
					samlPeerBinding.getPrimaryKey()) == null) {

				cacheResult(samlPeerBinding);
			}
		}
	}

	/**
	 * Clears the cache for all saml peer bindings.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(SamlPeerBindingImpl.class);

		finderCache.clearCache(SamlPeerBindingImpl.class);
	}

	/**
	 * Clears the cache for the saml peer binding.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(SamlPeerBinding samlPeerBinding) {
		entityCache.removeResult(SamlPeerBindingImpl.class, samlPeerBinding);
	}

	@Override
	public void clearCache(List<SamlPeerBinding> samlPeerBindings) {
		for (SamlPeerBinding samlPeerBinding : samlPeerBindings) {
			entityCache.removeResult(
				SamlPeerBindingImpl.class, samlPeerBinding);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(SamlPeerBindingImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(SamlPeerBindingImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	@Override
	public SamlPeerBinding create(long samlPeerBindingId) {
		SamlPeerBinding samlPeerBinding = new SamlPeerBindingImpl();

		samlPeerBinding.setNew(true);
		samlPeerBinding.setPrimaryKey(samlPeerBindingId);

		samlPeerBinding.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlPeerBinding;
	}

	/**
	 * Removes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding remove(long samlPeerBindingId)
		throws NoSuchPeerBindingException {

		return remove((Serializable)samlPeerBindingId);
	}

	/**
	 * Removes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding remove(Serializable primaryKey)
		throws NoSuchPeerBindingException {

		Session session = null;

		try {
			session = openSession();

			SamlPeerBinding samlPeerBinding = (SamlPeerBinding)session.get(
				SamlPeerBindingImpl.class, primaryKey);

			if (samlPeerBinding == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPeerBindingException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(samlPeerBinding);
		}
		catch (NoSuchPeerBindingException noSuchEntityException) {
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
	protected SamlPeerBinding removeImpl(SamlPeerBinding samlPeerBinding) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlPeerBinding)) {
				samlPeerBinding = (SamlPeerBinding)session.get(
					SamlPeerBindingImpl.class,
					samlPeerBinding.getPrimaryKeyObj());
			}

			if (samlPeerBinding != null) {
				session.delete(samlPeerBinding);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlPeerBinding != null) {
			clearCache(samlPeerBinding);
		}

		return samlPeerBinding;
	}

	@Override
	public SamlPeerBinding updateImpl(SamlPeerBinding samlPeerBinding) {
		boolean isNew = samlPeerBinding.isNew();

		if (!(samlPeerBinding instanceof SamlPeerBindingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlPeerBinding.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlPeerBinding);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlPeerBinding proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlPeerBinding implementation " +
					samlPeerBinding.getClass());
		}

		SamlPeerBindingModelImpl samlPeerBindingModelImpl =
			(SamlPeerBindingModelImpl)samlPeerBinding;

		if (isNew && (samlPeerBinding.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				samlPeerBinding.setCreateDate(date);
			}
			else {
				samlPeerBinding.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlPeerBinding);
			}
			else {
				samlPeerBinding = (SamlPeerBinding)session.merge(
					samlPeerBinding);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			SamlPeerBindingImpl.class, samlPeerBindingModelImpl, false, true);

		if (isNew) {
			samlPeerBinding.setNew(false);
		}

		samlPeerBinding.resetOriginalValues();

		return samlPeerBinding;
	}

	/**
	 * Returns the saml peer binding with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPeerBindingException {

		SamlPeerBinding samlPeerBinding = fetchByPrimaryKey(primaryKey);

		if (samlPeerBinding == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPeerBindingException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return samlPeerBinding;
	}

	/**
	 * Returns the saml peer binding with the primary key or throws a <code>NoSuchPeerBindingException</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding findByPrimaryKey(long samlPeerBindingId)
		throws NoSuchPeerBindingException {

		return findByPrimaryKey((Serializable)samlPeerBindingId);
	}

	/**
	 * Returns the saml peer binding with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding, or <code>null</code> if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding fetchByPrimaryKey(long samlPeerBindingId) {
		return fetchByPrimaryKey((Serializable)samlPeerBindingId);
	}

	/**
	 * Returns all the saml peer bindings.
	 *
	 * @return the saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findAll(
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findAll(
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator,
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

		List<SamlPeerBinding> list = null;

		if (useFinderCache) {
			list = (List<SamlPeerBinding>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_SAMLPEERBINDING);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_SAMLPEERBINDING;

				sql = sql.concat(SamlPeerBindingModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<SamlPeerBinding>)QueryUtil.list(
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
	 * Removes all the saml peer bindings from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (SamlPeerBinding samlPeerBinding : findAll()) {
			remove(samlPeerBinding);
		}
	}

	/**
	 * Returns the number of saml peer bindings.
	 *
	 * @return the number of saml peer bindings
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_SAMLPEERBINDING);

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
		return "samlPeerBindingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLPEERBINDING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlPeerBindingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml peer binding persistence.
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

		_finderPathWithPaginationFindByC_D_SNIV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_D_SNIV",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "deleted", "samlNameIdValue"}, true);

		_finderPathWithoutPaginationFindByC_D_SNIV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_D_SNIV",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "deleted", "samlNameIdValue"}, true);

		_finderPathCountByC_D_SNIV = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_D_SNIV",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "deleted", "samlNameIdValue"}, false);

		_finderPathWithPaginationFindByC_U_SPEI_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_SPEI_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId", "samlPeerEntityId", "deleted"},
			true);

		_finderPathWithoutPaginationFindByC_U_SPEI_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_SPEI_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"companyId", "userId", "samlPeerEntityId", "deleted"},
			true);

		_finderPathCountByC_U_SPEI_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U_SPEI_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"companyId", "userId", "samlPeerEntityId", "deleted"},
			false);

		SamlPeerBindingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlPeerBindingUtil.setPersistence(null);

		entityCache.removeCache(SamlPeerBindingImpl.class.getName());
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_SAMLPEERBINDING =
		"SELECT samlPeerBinding FROM SamlPeerBinding samlPeerBinding";

	private static final String _SQL_SELECT_SAMLPEERBINDING_WHERE =
		"SELECT samlPeerBinding FROM SamlPeerBinding samlPeerBinding WHERE ";

	private static final String _SQL_COUNT_SAMLPEERBINDING =
		"SELECT COUNT(samlPeerBinding) FROM SamlPeerBinding samlPeerBinding";

	private static final String _SQL_COUNT_SAMLPEERBINDING_WHERE =
		"SELECT COUNT(samlPeerBinding) FROM SamlPeerBinding samlPeerBinding WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "samlPeerBinding.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No SamlPeerBinding exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlPeerBinding exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlPeerBindingPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}