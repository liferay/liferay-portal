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
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
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
	extends BasePersistenceImpl
		<ExportImportReportEntry, NoSuchExportImportReportEntryException>
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

	private CollectionPersistenceFinder
		<ExportImportReportEntry, NoSuchExportImportReportEntryException>
			_collectionPersistenceFinderByC_E;

	/**
	 * Returns an ordered range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
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

		return _collectionPersistenceFinderByC_E.find(
			finderCache, new Object[] {companyId, exportImportConfigurationId},
			start, end, orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_E.findFirst(
			finderCache, new Object[] {companyId, exportImportConfigurationId},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_E.fetchFirst(
			finderCache, new Object[] {companyId, exportImportConfigurationId},
			orderByComparator);
	}

	/**
	 * Removes all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 */
	@Override
	public void removeByC_E(long companyId, long exportImportConfigurationId) {
		_collectionPersistenceFinderByC_E.remove(
			finderCache, new Object[] {companyId, exportImportConfigurationId});
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
		return _collectionPersistenceFinderByC_E.count(
			finderCache, new Object[] {companyId, exportImportConfigurationId});
	}

	private UniquePersistenceFinder
		<ExportImportReportEntry, NoSuchExportImportReportEntryException>
			_uniquePersistenceFinderByG_C_C_C_E_T;

	/**
	 * Returns the export import report entry where groupId = &#63; and companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and exportImportConfigurationId = &#63; and type = &#63; or throws a <code>NoSuchExportImportReportEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param type the type
	 * @return the matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry findByG_C_C_C_E_T(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId, long exportImportConfigurationId, int type)
		throws NoSuchExportImportReportEntryException {

		return _uniquePersistenceFinderByG_C_C_C_E_T.find(
			finderCache,
			new Object[] {
				groupId, companyId, classExternalReferenceCode, classNameId,
				exportImportConfigurationId, type
			});
	}

	/**
	 * Returns the export import report entry where groupId = &#63; and companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and exportImportConfigurationId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	@Override
	public ExportImportReportEntry fetchByG_C_C_C_E_T(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long exportImportConfigurationId, int type,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_C_C_E_T.fetch(
			finderCache,
			new Object[] {
				groupId, companyId, classExternalReferenceCode, classNameId,
				exportImportConfigurationId, type
			},
			useFinderCache);
	}

	/**
	 * Removes the export import report entry where groupId = &#63; and companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and exportImportConfigurationId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param type the type
	 * @return the export import report entry that was removed
	 */
	@Override
	public ExportImportReportEntry removeByG_C_C_C_E_T(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId, long exportImportConfigurationId, int type)
		throws NoSuchExportImportReportEntryException {

		ExportImportReportEntry exportImportReportEntry = findByG_C_C_C_E_T(
			groupId, companyId, classExternalReferenceCode, classNameId,
			exportImportConfigurationId, type);

		return remove(exportImportReportEntry);
	}

	/**
	 * Returns the number of export import report entries where groupId = &#63; and companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and exportImportConfigurationId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param type the type
	 * @return the number of matching export import report entries
	 */
	@Override
	public int countByG_C_C_C_E_T(
		long groupId, long companyId, String classExternalReferenceCode,
		long classNameId, long exportImportConfigurationId, int type) {

		return _uniquePersistenceFinderByG_C_C_C_E_T.count(
			finderCache,
			new Object[] {
				groupId, companyId, classExternalReferenceCode, classNameId,
				exportImportConfigurationId, type
			});
	}

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

		cacheUniqueFindersResult(exportImportReportEntry, false);

		if (isNew) {
			exportImportReportEntry.setNew(false);
		}

		exportImportReportEntry.resetOriginalValues();

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
		_collectionPersistenceFinderByC_E = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "exportImportConfigurationId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "exportImportConfigurationId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "exportImportConfigurationId"},
				false),
			_SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE,
			_SQL_COUNT_EXPORTIMPORTREPORTENTRY_WHERE,
			ExportImportReportEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"exportImportReportEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportReportEntry::getCompanyId),
			new FinderColumn<>(
				"exportImportReportEntry.", "exportImportConfigurationId",
				FinderColumn.Type.LONG, "=", true, true,
				ExportImportReportEntry::getExportImportConfigurationId));

		_uniquePersistenceFinderByG_C_C_C_E_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C_C_E_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName()
				},
				new String[] {
					"groupId", "companyId", "classExternalReferenceCode",
					"classNameId", "exportImportConfigurationId", "type_"
				},
				0, 4, false, ExportImportReportEntry::getGroupId,
				ExportImportReportEntry::getCompanyId,
				convertNullFunction(
					ExportImportReportEntry::getClassExternalReferenceCode),
				ExportImportReportEntry::getClassNameId,
				ExportImportReportEntry::getExportImportConfigurationId,
				ExportImportReportEntry::getType),
			_SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE, "",
			new FinderColumn<>(
				"exportImportReportEntry.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportReportEntry::getGroupId),
			new FinderColumn<>(
				"exportImportReportEntry.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportReportEntry::getCompanyId),
			new FinderColumn<>(
				"exportImportReportEntry.", "classExternalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ExportImportReportEntry::getClassExternalReferenceCode),
			new FinderColumn<>(
				"exportImportReportEntry.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				ExportImportReportEntry::getClassNameId),
			new FinderColumn<>(
				"exportImportReportEntry.", "exportImportConfigurationId",
				FinderColumn.Type.LONG, "=", true, true,
				ExportImportReportEntry::getExportImportConfigurationId),
			new FinderColumn<>(
				"exportImportReportEntry.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				ExportImportReportEntry::getType));

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

	private static final String _ENTITY_ALIAS_PREFIX =
		ExportImportReportEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPORTIMPORTREPORTENTRY =
		"SELECT exportImportReportEntry FROM ExportImportReportEntry exportImportReportEntry";

	private static final String _SQL_SELECT_EXPORTIMPORTREPORTENTRY_WHERE =
		"SELECT exportImportReportEntry FROM ExportImportReportEntry exportImportReportEntry WHERE ";

	private static final String _SQL_COUNT_EXPORTIMPORTREPORTENTRY_WHERE =
		"SELECT COUNT(exportImportReportEntry) FROM ExportImportReportEntry exportImportReportEntry WHERE ";

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
// LIFERAY-SERVICE-BUILDER-HASH:-2130745214