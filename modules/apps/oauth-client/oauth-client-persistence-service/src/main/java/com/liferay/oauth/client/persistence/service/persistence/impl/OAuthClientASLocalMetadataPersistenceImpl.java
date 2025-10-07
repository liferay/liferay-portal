/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadataTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataUtil;
import com.liferay.oauth.client.persistence.service.persistence.impl.constants.OAuthClientPersistenceConstants;
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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

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
 * The persistence implementation for the o auth client as local metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuthClientASLocalMetadataPersistence.class)
public class OAuthClientASLocalMetadataPersistenceImpl
	extends BasePersistenceImpl<OAuthClientASLocalMetadata>
	implements OAuthClientASLocalMetadataPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuthClientASLocalMetadataUtil</code> to access the o auth client as local metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuthClientASLocalMetadataImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

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

		List<OAuthClientASLocalMetadata> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientASLocalMetadata>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
						list) {

					if (companyId !=
							oAuthClientASLocalMetadata.getCompanyId()) {

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

			sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<OAuthClientASLocalMetadata>)QueryUtil.list(
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
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (oAuthClientASLocalMetadata != null) {
			return oAuthClientASLocalMetadata;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOAuthClientASLocalMetadataException(sb.toString());
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		List<OAuthClientASLocalMetadata> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByCompanyId_Last(
			long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByCompanyId_Last(companyId, orderByComparator);

		if (oAuthClientASLocalMetadata != null) {
			return oAuthClientASLocalMetadata;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchOAuthClientASLocalMetadataException(sb.toString());
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<OAuthClientASLocalMetadata> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata[] findByCompanyId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			findByPrimaryKey(oAuthClientASLocalMetadataId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientASLocalMetadata[] array =
				new OAuthClientASLocalMetadataImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, oAuthClientASLocalMetadata, companyId,
				orderByComparator, true);

			array[1] = oAuthClientASLocalMetadata;

			array[2] = getByCompanyId_PrevAndNext(
				session, oAuthClientASLocalMetadata, companyId,
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

	protected OAuthClientASLocalMetadata getByCompanyId_PrevAndNext(
		Session session, OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
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

		sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);

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
			sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientASLocalMetadata)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientASLocalMetadata> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId) {

		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientASLocalMetadataModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, OAuthClientASLocalMetadataImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, OAuthClientASLocalMetadataImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<OAuthClientASLocalMetadata>)QueryUtil.list(
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
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata[] filterFindByCompanyId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				oAuthClientASLocalMetadataId, companyId, orderByComparator);
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			findByPrimaryKey(oAuthClientASLocalMetadataId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientASLocalMetadata[] array =
				new OAuthClientASLocalMetadataImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, oAuthClientASLocalMetadata, companyId,
				orderByComparator, true);

			array[1] = oAuthClientASLocalMetadata;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, oAuthClientASLocalMetadata, companyId,
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

	protected OAuthClientASLocalMetadata filterGetByCompanyId_PrevAndNext(
		Session session, OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientASLocalMetadataModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, OAuthClientASLocalMetadataImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, OAuthClientASLocalMetadataImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientASLocalMetadata)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientASLocalMetadata> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the o auth client as local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(oAuthClientASLocalMetadata);
		}
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE);

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

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas =
				findByCompanyId(companyId);

			oAuthClientASLocalMetadatas = InlineSQLHelperUtil.filter(
				oAuthClientASLocalMetadatas);

			return oAuthClientASLocalMetadatas.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"oAuthClientASLocalMetadata.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByUserId;
				finderArgs = new Object[] {userId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByUserId;
			finderArgs = new Object[] {userId, start, end, orderByComparator};
		}

		List<OAuthClientASLocalMetadata> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientASLocalMetadata>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
						list) {

					if (userId != oAuthClientASLocalMetadata.getUserId()) {
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

			sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

				list = (List<OAuthClientASLocalMetadata>)QueryUtil.list(
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
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByUserId_First(
			long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByUserId_First(userId, orderByComparator);

		if (oAuthClientASLocalMetadata != null) {
			return oAuthClientASLocalMetadata;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOAuthClientASLocalMetadataException(sb.toString());
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByUserId_First(
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		List<OAuthClientASLocalMetadata> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByUserId_Last(
			long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByUserId_Last(userId, orderByComparator);

		if (oAuthClientASLocalMetadata != null) {
			return oAuthClientASLocalMetadata;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchOAuthClientASLocalMetadataException(sb.toString());
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByUserId_Last(
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<OAuthClientASLocalMetadata> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata[] findByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			findByPrimaryKey(oAuthClientASLocalMetadataId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientASLocalMetadata[] array =
				new OAuthClientASLocalMetadataImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, oAuthClientASLocalMetadata, userId, orderByComparator,
				true);

			array[1] = oAuthClientASLocalMetadata;

			array[2] = getByUserId_PrevAndNext(
				session, oAuthClientASLocalMetadata, userId, orderByComparator,
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

	protected OAuthClientASLocalMetadata getByUserId_PrevAndNext(
		Session session, OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
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

		sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

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
			sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientASLocalMetadata)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientASLocalMetadata> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUserId(long userId) {
		return filterFindByUserId(
			userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end) {

		return filterFindByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUserId(userId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientASLocalMetadataModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, OAuthClientASLocalMetadataImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, OAuthClientASLocalMetadataImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);

			return (List<OAuthClientASLocalMetadata>)QueryUtil.list(
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
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata[] filterFindByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUserId_PrevAndNext(
				oAuthClientASLocalMetadataId, userId, orderByComparator);
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			findByPrimaryKey(oAuthClientASLocalMetadataId);

		Session session = null;

		try {
			session = openSession();

			OAuthClientASLocalMetadata[] array =
				new OAuthClientASLocalMetadataImpl[3];

			array[0] = filterGetByUserId_PrevAndNext(
				session, oAuthClientASLocalMetadata, userId, orderByComparator,
				true);

			array[1] = oAuthClientASLocalMetadata;

			array[2] = filterGetByUserId_PrevAndNext(
				session, oAuthClientASLocalMetadata, userId, orderByComparator,
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

	protected OAuthClientASLocalMetadata filterGetByUserId_PrevAndNext(
		Session session, OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					OAuthClientASLocalMetadataModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(OAuthClientASLocalMetadataModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, OAuthClientASLocalMetadataImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, OAuthClientASLocalMetadataImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						oAuthClientASLocalMetadata)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<OAuthClientASLocalMetadata> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the o auth client as local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(oAuthClientASLocalMetadata);
		}
	}

	/**
	 * Returns the number of o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE);

			sb.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(userId);

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
	 * Returns the number of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByUserId(long userId) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUserId(userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas =
				findByUserId(userId);

			oAuthClientASLocalMetadatas = InlineSQLHelperUtil.filter(
				oAuthClientASLocalMetadatas);

			return oAuthClientASLocalMetadatas.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), OAuthClientASLocalMetadata.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userId);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"oAuthClientASLocalMetadata.userId = ?";

	private FinderPath _finderPathFetchByLocalWellKnownURI;

	/**
	 * Returns the o auth client as local metadata where localWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByLocalWellKnownURI(
			String localWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByLocalWellKnownURI(localWellKnownURI);

		if (oAuthClientASLocalMetadata == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("localWellKnownURI=");
			sb.append(localWellKnownURI);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchOAuthClientASLocalMetadataException(sb.toString());
		}

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURI = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURI(
		String localWellKnownURI) {

		return fetchByLocalWellKnownURI(localWellKnownURI, true);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localWellKnownURI the local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURI(
		String localWellKnownURI, boolean useFinderCache) {

		localWellKnownURI = Objects.toString(localWellKnownURI, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {localWellKnownURI};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByLocalWellKnownURI, finderArgs, this);
		}

		if (result instanceof OAuthClientASLocalMetadata) {
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				(OAuthClientASLocalMetadata)result;

			if (!Objects.equals(
					localWellKnownURI,
					oAuthClientASLocalMetadata.getLocalWellKnownURI())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE);

			boolean bindLocalWellKnownURI = false;

			if (localWellKnownURI.isEmpty()) {
				sb.append(_FINDER_COLUMN_LOCALWELLKNOWNURI_LOCALWELLKNOWNURI_3);
			}
			else {
				bindLocalWellKnownURI = true;

				sb.append(_FINDER_COLUMN_LOCALWELLKNOWNURI_LOCALWELLKNOWNURI_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindLocalWellKnownURI) {
					queryPos.add(localWellKnownURI);
				}

				List<OAuthClientASLocalMetadata> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByLocalWellKnownURI, finderArgs,
							list);
					}
				}
				else {
					OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
						list.get(0);

					result = oAuthClientASLocalMetadata;

					cacheResult(oAuthClientASLocalMetadata);
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
			return (OAuthClientASLocalMetadata)result;
		}
	}

	/**
	 * Removes the o auth client as local metadata where localWellKnownURI = &#63; from the database.
	 *
	 * @param localWellKnownURI the local well known uri
	 * @return the o auth client as local metadata that was removed
	 */
	@Override
	public OAuthClientASLocalMetadata removeByLocalWellKnownURI(
			String localWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			findByLocalWellKnownURI(localWellKnownURI);

		return remove(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the number of o auth client as local metadatas where localWellKnownURI = &#63;.
	 *
	 * @param localWellKnownURI the local well known uri
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByLocalWellKnownURI(String localWellKnownURI) {
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByLocalWellKnownURI(localWellKnownURI);

		if (oAuthClientASLocalMetadata == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_LOCALWELLKNOWNURI_LOCALWELLKNOWNURI_2 =
			"oAuthClientASLocalMetadata.localWellKnownURI = ?";

	private static final String
		_FINDER_COLUMN_LOCALWELLKNOWNURI_LOCALWELLKNOWNURI_3 =
			"(oAuthClientASLocalMetadata.localWellKnownURI IS NULL OR oAuthClientASLocalMetadata.localWellKnownURI = '')";

	public OAuthClientASLocalMetadataPersistenceImpl() {
		setModelClass(OAuthClientASLocalMetadata.class);

		setModelImplClass(OAuthClientASLocalMetadataImpl.class);
		setModelPKClass(long.class);

		setTable(OAuthClientASLocalMetadataTable.INSTANCE);
	}

	/**
	 * Caches the o auth client as local metadata in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 */
	@Override
	public void cacheResult(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		entityCache.putResult(
			OAuthClientASLocalMetadataImpl.class,
			oAuthClientASLocalMetadata.getPrimaryKey(),
			oAuthClientASLocalMetadata);

		finderCache.putResult(
			_finderPathFetchByLocalWellKnownURI,
			new Object[] {oAuthClientASLocalMetadata.getLocalWellKnownURI()},
			oAuthClientASLocalMetadata);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the o auth client as local metadatas in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadatas the o auth client as local metadatas
	 */
	@Override
	public void cacheResult(
		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (oAuthClientASLocalMetadatas.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				oAuthClientASLocalMetadatas) {

			if (entityCache.getResult(
					OAuthClientASLocalMetadataImpl.class,
					oAuthClientASLocalMetadata.getPrimaryKey()) == null) {

				cacheResult(oAuthClientASLocalMetadata);
			}
		}
	}

	/**
	 * Clears the cache for all o auth client as local metadatas.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(OAuthClientASLocalMetadataImpl.class);

		finderCache.clearCache(OAuthClientASLocalMetadataImpl.class);
	}

	/**
	 * Clears the cache for the o auth client as local metadata.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		entityCache.removeResult(
			OAuthClientASLocalMetadataImpl.class, oAuthClientASLocalMetadata);
	}

	@Override
	public void clearCache(
		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas) {

		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				oAuthClientASLocalMetadatas) {

			entityCache.removeResult(
				OAuthClientASLocalMetadataImpl.class,
				oAuthClientASLocalMetadata);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(OAuthClientASLocalMetadataImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				OAuthClientASLocalMetadataImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		OAuthClientASLocalMetadataModelImpl
			oAuthClientASLocalMetadataModelImpl) {

		Object[] args = new Object[] {
			oAuthClientASLocalMetadataModelImpl.getLocalWellKnownURI()
		};

		finderCache.putResult(
			_finderPathFetchByLocalWellKnownURI, args,
			oAuthClientASLocalMetadataModelImpl);
	}

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	@Override
	public OAuthClientASLocalMetadata create(
		long oAuthClientASLocalMetadataId) {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			new OAuthClientASLocalMetadataImpl();

		oAuthClientASLocalMetadata.setNew(true);
		oAuthClientASLocalMetadata.setPrimaryKey(oAuthClientASLocalMetadataId);

		oAuthClientASLocalMetadata.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Removes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata remove(long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException {

		return remove((Serializable)oAuthClientASLocalMetadataId);
	}

	/**
	 * Removes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata remove(Serializable primaryKey)
		throws NoSuchOAuthClientASLocalMetadataException {

		Session session = null;

		try {
			session = openSession();

			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				(OAuthClientASLocalMetadata)session.get(
					OAuthClientASLocalMetadataImpl.class, primaryKey);

			if (oAuthClientASLocalMetadata == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchOAuthClientASLocalMetadataException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(oAuthClientASLocalMetadata);
		}
		catch (NoSuchOAuthClientASLocalMetadataException
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
	protected OAuthClientASLocalMetadata removeImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuthClientASLocalMetadata)) {
				oAuthClientASLocalMetadata =
					(OAuthClientASLocalMetadata)session.get(
						OAuthClientASLocalMetadataImpl.class,
						oAuthClientASLocalMetadata.getPrimaryKeyObj());
			}

			if (oAuthClientASLocalMetadata != null) {
				session.delete(oAuthClientASLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuthClientASLocalMetadata != null) {
			clearCache(oAuthClientASLocalMetadata);
		}

		return oAuthClientASLocalMetadata;
	}

	@Override
	public OAuthClientASLocalMetadata updateImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		boolean isNew = oAuthClientASLocalMetadata.isNew();

		if (!(oAuthClientASLocalMetadata instanceof
				OAuthClientASLocalMetadataModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuthClientASLocalMetadata.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuthClientASLocalMetadata);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuthClientASLocalMetadata proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuthClientASLocalMetadata implementation " +
					oAuthClientASLocalMetadata.getClass());
		}

		OAuthClientASLocalMetadataModelImpl
			oAuthClientASLocalMetadataModelImpl =
				(OAuthClientASLocalMetadataModelImpl)oAuthClientASLocalMetadata;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (oAuthClientASLocalMetadata.getCreateDate() == null)) {
			if (serviceContext == null) {
				oAuthClientASLocalMetadata.setCreateDate(date);
			}
			else {
				oAuthClientASLocalMetadata.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!oAuthClientASLocalMetadataModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				oAuthClientASLocalMetadata.setModifiedDate(date);
			}
			else {
				oAuthClientASLocalMetadata.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuthClientASLocalMetadata);
			}
			else {
				oAuthClientASLocalMetadata =
					(OAuthClientASLocalMetadata)session.merge(
						oAuthClientASLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			OAuthClientASLocalMetadataImpl.class,
			oAuthClientASLocalMetadataModelImpl, false, true);

		cacheUniqueFindersCache(oAuthClientASLocalMetadataModelImpl);

		if (isNew) {
			oAuthClientASLocalMetadata.setNew(false);
		}

		oAuthClientASLocalMetadata.resetOriginalValues();

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByPrimaryKey(Serializable primaryKey)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			fetchByPrimaryKey(primaryKey);

		if (oAuthClientASLocalMetadata == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchOAuthClientASLocalMetadataException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByPrimaryKey(
			long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException {

		return findByPrimaryKey((Serializable)oAuthClientASLocalMetadataId);
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata, or <code>null</code> if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByPrimaryKey(
		long oAuthClientASLocalMetadataId) {

		return fetchByPrimaryKey((Serializable)oAuthClientASLocalMetadataId);
	}

	/**
	 * Returns all the o auth client as local metadatas.
	 *
	 * @return the o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
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

		List<OAuthClientASLocalMetadata> list = null;

		if (useFinderCache) {
			list = (List<OAuthClientASLocalMetadata>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA;

				sql = sql.concat(
					OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<OAuthClientASLocalMetadata>)QueryUtil.list(
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
	 * Removes all the o auth client as local metadatas from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				findAll()) {

			remove(oAuthClientASLocalMetadata);
		}
	}

	/**
	 * Returns the number of o auth client as local metadatas.
	 *
	 * @return the number of o auth client as local metadatas
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
					_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA);

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
		return "oAuthClientASLocalMetadataId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuthClientASLocalMetadataModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth client as local metadata persistence.
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

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_finderPathFetchByLocalWellKnownURI = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByLocalWellKnownURI",
			new String[] {String.class.getName()},
			new String[] {"localWellKnownURI"}, true);

		OAuthClientASLocalMetadataUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuthClientASLocalMetadataUtil.setPersistence(null);

		entityCache.removeCache(OAuthClientASLocalMetadataImpl.class.getName());
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA =
		"SELECT oAuthClientASLocalMetadata FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata";

	private static final String _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE =
		"SELECT oAuthClientASLocalMetadata FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String _SQL_COUNT_OAUTHCLIENTASLOCALMETADATA =
		"SELECT COUNT(oAuthClientASLocalMetadata) FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata";

	private static final String _SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE =
		"SELECT COUNT(oAuthClientASLocalMetadata) FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"oAuthClientASLocalMetadata.oAuthClientASLocalMetadataId";

	private static final String
		_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE =
			"SELECT DISTINCT {oAuthClientASLocalMetadata.*} FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {OAuthClientASLocalMetadata.*} FROM (SELECT DISTINCT oAuthClientASLocalMetadata.oAuthClientASLocalMetadataId FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String
		_FILTER_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN OAuthClientASLocalMetadata ON TEMP_TABLE.oAuthClientASLocalMetadataId = OAuthClientASLocalMetadata.oAuthClientASLocalMetadataId";

	private static final String
		_FILTER_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE =
			"SELECT COUNT(DISTINCT oAuthClientASLocalMetadata.oAuthClientASLocalMetadataId) AS COUNT_VALUE FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"oAuthClientASLocalMetadata";

	private static final String _FILTER_ENTITY_TABLE =
		"OAuthClientASLocalMetadata";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"oAuthClientASLocalMetadata.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"OAuthClientASLocalMetadata.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No OAuthClientASLocalMetadata exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OAuthClientASLocalMetadata exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientASLocalMetadataPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}