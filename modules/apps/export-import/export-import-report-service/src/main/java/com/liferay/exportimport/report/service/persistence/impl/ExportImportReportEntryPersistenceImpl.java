/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.persistence.impl;

import com.liferay.exportimport.report.exception.NoSuchExportImportReportEntryException;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.model.ExportImportReportEntryTable;
import com.liferay.exportimport.report.model.impl.ExportImportReportEntryImpl;
import com.liferay.exportimport.report.model.impl.ExportImportReportEntryModelImpl;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryPersistence;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryUtil;
import com.liferay.exportimport.report.service.persistence.impl.constants.ExportImportReportPersistenceConstants;
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
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the export import report entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Carlos Correa
 * @generated
 */
@Component(service = ExportImportReportEntryPersistence.class)
public class ExportImportReportEntryPersistenceImpl
	extends BasePersistenceImpl<ExportImportReportEntry>
	implements ExportImportReportEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExportImportReportEntryUtil</code> to access the export import report entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExportImportReportEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByC_E;
	private FinderPath _finderPathWithoutPaginationFindByC_E;
	private FinderPath _finderPathCountByC_E;

	/**
	 * Returns all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the matching export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId) {

		return findByC_E(
			companyId, exportImportConfigurationId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @return the range of matching export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end) {

		return findByC_E(
			companyId, exportImportConfigurationId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return findByC_E(
			companyId, exportImportConfigurationId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_E;
				finderArgs = new Object[] {
					companyId, exportImportConfigurationId
				};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_E;
			finderArgs = new Object[] {
				companyId, exportImportConfigurationId, start, end,
				orderByComparator
			};
		}

		List<ExportImportReportEntry> list = null;

		if (useFinderCache) {
			list = (List<ExportImportReportEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ExportImportReportEntry exportImportReportEntry : list) {
					if ((companyId != exportImportReportEntry.getCompanyId()) ||
						(exportImportConfigurationId !=
							exportImportReportEntry.
								getExportImportConfigurationId())) {

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

			sb.append(_SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_EXPORTIMPORTCONFIGURATIONID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(ExportImportReportEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(exportImportConfigurationId);

				list = (List<ExportImportReportEntry>)QueryUtil.list(
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
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry findByC_E_First(
			long companyId, long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException {

		ExportImportReportEntry exportImportReportEntry = fetchByC_E_First(
			companyId, exportImportConfigurationId, orderByComparator);

		if (exportImportReportEntry != null) {
			return exportImportReportEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", exportImportConfigurationId=");
		sb.append(exportImportConfigurationId);

		sb.append("}");

		throw new NoSuchExportImportReportEntryException(sb.toString());
	}

	/**
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry fetchByC_E_First(
		long companyId, long exportImportConfigurationId,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		List<ExportImportReportEntry> list = findByC_E(
			companyId, exportImportConfigurationId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry findByC_E_Last(
			long companyId, long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException {

		ExportImportReportEntry exportImportReportEntry = fetchByC_E_Last(
			companyId, exportImportConfigurationId, orderByComparator);

		if (exportImportReportEntry != null) {
			return exportImportReportEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", exportImportConfigurationId=");
		sb.append(exportImportConfigurationId);

		sb.append("}");

		throw new NoSuchExportImportReportEntryException(sb.toString());
	}

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry fetchByC_E_Last(
		long companyId, long exportImportConfigurationId,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		int count = countByC_E(companyId, exportImportConfigurationId);

		if (count == 0) {
			return null;
		}

		List<ExportImportReportEntry> list = findByC_E(
			companyId, exportImportConfigurationId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the export import report entries before and after the current export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param exportImportReportEntryId the primary key of the current export import report entry
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry[] findByC_E_PrevAndNext(
			long exportImportReportEntryId, long companyId,
			long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException {

		ExportImportReportEntry exportImportReportEntry = findByPrimaryKey(
			exportImportReportEntryId);

		Session session = null;

		try {
			session = openSession();

			ExportImportReportEntry[] array =
				new ExportImportReportEntryImpl[3];

			array[0] = getByC_E_PrevAndNext(
				session, exportImportReportEntry, companyId,
				exportImportConfigurationId, orderByComparator, true);

			array[1] = exportImportReportEntry;

			array[2] = getByC_E_PrevAndNext(
				session, exportImportReportEntry, companyId,
				exportImportConfigurationId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ExportImportReportEntry getByC_E_PrevAndNext(
		Session session, ExportImportReportEntry exportImportReportEntry,
		long companyId, long exportImportConfigurationId,
		OrderByComparator<ExportImportReportEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_EXPORTIMPORTCONFIGURATIONID_2);

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
			sb.append(ExportImportReportEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(exportImportConfigurationId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						exportImportReportEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ExportImportReportEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 */
	@Override
	public void removeByC_E(long companyId, long exportImportConfigurationId) {
		for (ExportImportReportEntry exportImportReportEntry :
				findByC_E(
					companyId, exportImportConfigurationId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(exportImportReportEntry);
		}
	}

	/**
	 * Returns the number of export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the number of matching export import report entries
	 */
	@Override
	public int countByC_E(long companyId, long exportImportConfigurationId) {
		FinderPath finderPath = _finderPathCountByC_E;

		Object[] finderArgs = new Object[] {
			companyId, exportImportConfigurationId
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_EXPORTIMPORTREPORTENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_EXPORTIMPORTCONFIGURATIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(exportImportConfigurationId);

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

	private static final String _FINDER_COLUMN_C_E_COMPANYID_2 =
		"exportImportReportEntry.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_C_E_EXPORTIMPORTCONFIGURATIONID_2 =
			"exportImportReportEntry.exportImportConfigurationId = ?";

	public ExportImportReportEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExportImportReportEntry.class);

		setModelImplClass(ExportImportReportEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ExportImportReportEntryTable.INSTANCE);
	}

	/**
	 * Caches the export import report entry in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntry the export import report entry
	 */
	@Override
	public void cacheResult(ExportImportReportEntry exportImportReportEntry) {
		entityCache.putResult(
			ExportImportReportEntryImpl.class,
			exportImportReportEntry.getPrimaryKey(), exportImportReportEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the export import report entries in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntries the export import report entries
	 */
	@Override
	public void cacheResult(
		List<ExportImportReportEntry> exportImportReportEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (exportImportReportEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ExportImportReportEntry exportImportReportEntry :
				exportImportReportEntries) {

			if (entityCache.getResult(
					ExportImportReportEntryImpl.class,
					exportImportReportEntry.getPrimaryKey()) == null) {

				cacheResult(exportImportReportEntry);
			}
		}
	}

	/**
	 * Clears the cache for all export import report entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ExportImportReportEntryImpl.class);

		finderCache.clearCache(ExportImportReportEntryImpl.class);
	}

	/**
	 * Clears the cache for the export import report entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ExportImportReportEntry exportImportReportEntry) {
		entityCache.removeResult(
			ExportImportReportEntryImpl.class, exportImportReportEntry);
	}

	@Override
	public void clearCache(
		List<ExportImportReportEntry> exportImportReportEntries) {

		for (ExportImportReportEntry exportImportReportEntry :
				exportImportReportEntries) {

			entityCache.removeResult(
				ExportImportReportEntryImpl.class, exportImportReportEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(ExportImportReportEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				ExportImportReportEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new export import report entry with the primary key. Does not add the export import report entry to the database.
	 *
	 * @param exportImportReportEntryId the primary key for the new export import report entry
	 * @return the new export import report entry
	 */
	@Override
	public ExportImportReportEntry create(long exportImportReportEntryId) {
		ExportImportReportEntry exportImportReportEntry =
			new ExportImportReportEntryImpl();

		exportImportReportEntry.setNew(true);
		exportImportReportEntry.setPrimaryKey(exportImportReportEntryId);

		exportImportReportEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return exportImportReportEntry;
	}

	/**
	 * Removes the export import report entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry that was removed
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry remove(long exportImportReportEntryId)
		throws NoSuchExportImportReportEntryException {

		return remove((Serializable)exportImportReportEntryId);
	}

	/**
	 * Removes the export import report entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the export import report entry
	 * @return the export import report entry that was removed
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry remove(Serializable primaryKey)
		throws NoSuchExportImportReportEntryException {

		Session session = null;

		try {
			session = openSession();

			ExportImportReportEntry exportImportReportEntry =
				(ExportImportReportEntry)session.get(
					ExportImportReportEntryImpl.class, primaryKey);

			if (exportImportReportEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchExportImportReportEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(exportImportReportEntry);
		}
		catch (NoSuchExportImportReportEntryException noSuchEntityException) {
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
	protected ExportImportReportEntry removeImpl(
		ExportImportReportEntry exportImportReportEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(exportImportReportEntry)) {
				exportImportReportEntry = (ExportImportReportEntry)session.get(
					ExportImportReportEntryImpl.class,
					exportImportReportEntry.getPrimaryKeyObj());
			}

			if (exportImportReportEntry != null) {
				session.delete(exportImportReportEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (exportImportReportEntry != null) {
			clearCache(exportImportReportEntry);
		}

		return exportImportReportEntry;
	}

	@Override
	public ExportImportReportEntry updateImpl(
		ExportImportReportEntry exportImportReportEntry) {

		boolean isNew = exportImportReportEntry.isNew();

		if (!(exportImportReportEntry instanceof
				ExportImportReportEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(exportImportReportEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					exportImportReportEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in exportImportReportEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExportImportReportEntry implementation " +
					exportImportReportEntry.getClass());
		}

		ExportImportReportEntryModelImpl exportImportReportEntryModelImpl =
			(ExportImportReportEntryModelImpl)exportImportReportEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (exportImportReportEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				exportImportReportEntry.setCreateDate(date);
			}
			else {
				exportImportReportEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!exportImportReportEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				exportImportReportEntry.setModifiedDate(date);
			}
			else {
				exportImportReportEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(exportImportReportEntry);
			}
			else {
				exportImportReportEntry =
					(ExportImportReportEntry)session.merge(
						exportImportReportEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			ExportImportReportEntryImpl.class, exportImportReportEntryModelImpl,
			false, true);

		if (isNew) {
			exportImportReportEntry.setNew(false);
		}

		exportImportReportEntry.resetOriginalValues();

		return exportImportReportEntry;
	}

	/**
	 * Returns the export import report entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the export import report entry
	 * @return the export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchExportImportReportEntryException {

		ExportImportReportEntry exportImportReportEntry = fetchByPrimaryKey(
			primaryKey);

		if (exportImportReportEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchExportImportReportEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return exportImportReportEntry;
	}

	/**
	 * Returns the export import report entry with the primary key or throws a <code>NoSuchExportImportReportEntryException</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry findByPrimaryKey(
			long exportImportReportEntryId)
		throws NoSuchExportImportReportEntryException {

		return findByPrimaryKey((Serializable)exportImportReportEntryId);
	}

	/**
	 * Returns the export import report entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry, or <code>null</code> if a export import report entry with the primary key could not be found
	 */
	@Override
	public ExportImportReportEntry fetchByPrimaryKey(
		long exportImportReportEntryId) {

		return fetchByPrimaryKey((Serializable)exportImportReportEntryId);
	}

	/**
	 * Returns all the export import report entries.
	 *
	 * @return the export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @return the range of export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findAll(
		int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of export import report entries
	 */
	@Override
	public List<ExportImportReportEntry> findAll(
		int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator,
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

		List<ExportImportReportEntry> list = null;

		if (useFinderCache) {
			list = (List<ExportImportReportEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_EXPORTIMPORTREPORTENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_EXPORTIMPORTREPORTENTRY;

				sql = sql.concat(
					ExportImportReportEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ExportImportReportEntry>)QueryUtil.list(
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
	 * Removes all the export import report entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ExportImportReportEntry exportImportReportEntry : findAll()) {
			remove(exportImportReportEntry);
		}
	}

	/**
	 * Returns the number of export import report entries.
	 *
	 * @return the number of export import report entries
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
					_SQL_COUNT_EXPORTIMPORTREPORTENTRY);

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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "exportImportReportEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPORTIMPORTREPORTENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ExportImportReportEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the export import report entry persistence.
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

		_finderPathWithPaginationFindByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "exportImportConfigurationId"}, true);

		_finderPathWithoutPaginationFindByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "exportImportConfigurationId"}, true);

		_finderPathCountByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "exportImportConfigurationId"}, false);

		ExportImportReportEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ExportImportReportEntryUtil.setPersistence(null);

		entityCache.removeCache(ExportImportReportEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ExportImportReportPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ExportImportReportPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ExportImportReportPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_EXPORTIMPORTREPORTENTRY =
		"SELECT exportImportReportEntry FROM ExportImportReportEntry exportImportReportEntry";

	private static final String _SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE =
		"SELECT exportImportReportEntry FROM ExportImportReportEntry exportImportReportEntry WHERE ";

	private static final String _SQL_COUNT_EXPORTIMPORTREPORTENTRY =
		"SELECT COUNT(exportImportReportEntry) FROM ExportImportReportEntry exportImportReportEntry";

	private static final String _SQL_COUNT_EXPORTIMPORTREPORTENTRY_WHERE =
		"SELECT COUNT(exportImportReportEntry) FROM ExportImportReportEntry exportImportReportEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"exportImportReportEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ExportImportReportEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExportImportReportEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportReportEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}