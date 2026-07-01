/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceReportException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReportTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceReportImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceReportModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceReportPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceReportUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddm form instance report service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFormInstanceReportPersistence.class)
public class DDMFormInstanceReportPersistenceImpl
	extends BasePersistenceImpl
		<DDMFormInstanceReport, NoSuchFormInstanceReportException>
	implements DDMFormInstanceReportPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFormInstanceReportUtil</code> to access the ddm form instance report persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFormInstanceReportImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<DDMFormInstanceReport, NoSuchFormInstanceReportException>
			_uniquePersistenceFinderByFormInstanceId;

	/**
	 * Returns the ddm form instance report where formInstanceId = &#63; or throws a <code>NoSuchFormInstanceReportException</code> if it could not be found.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance report
	 * @throws NoSuchFormInstanceReportException if a matching ddm form instance report could not be found
	 */
	@Override
	public DDMFormInstanceReport findByFormInstanceId(long formInstanceId)
		throws NoSuchFormInstanceReportException {

		return _uniquePersistenceFinderByFormInstanceId.find(
			finderCache, new Object[] {formInstanceId});
	}

	/**
	 * Returns the ddm form instance report where formInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param formInstanceId the form instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance report, or <code>null</code> if a matching ddm form instance report could not be found
	 */
	@Override
	public DDMFormInstanceReport fetchByFormInstanceId(
		long formInstanceId, boolean useFinderCache) {

		return _uniquePersistenceFinderByFormInstanceId.fetch(
			finderCache, new Object[] {formInstanceId}, useFinderCache);
	}

	/**
	 * Removes the ddm form instance report where formInstanceId = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the ddm form instance report that was removed
	 */
	@Override
	public DDMFormInstanceReport removeByFormInstanceId(long formInstanceId)
		throws NoSuchFormInstanceReportException {

		DDMFormInstanceReport ddmFormInstanceReport = findByFormInstanceId(
			formInstanceId);

		return remove(ddmFormInstanceReport);
	}

	/**
	 * Returns the number of ddm form instance reports where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance reports
	 */
	@Override
	public int countByFormInstanceId(long formInstanceId) {
		return _uniquePersistenceFinderByFormInstanceId.count(
			finderCache, new Object[] {formInstanceId});
	}

	public DDMFormInstanceReportPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMFormInstanceReport.class);

		setModelImplClass(DDMFormInstanceReportImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFormInstanceReportTable.INSTANCE);
	}

	/**
	 * Creates a new ddm form instance report with the primary key. Does not add the ddm form instance report to the database.
	 *
	 * @param formInstanceReportId the primary key for the new ddm form instance report
	 * @return the new ddm form instance report
	 */
	@Override
	public DDMFormInstanceReport create(long formInstanceReportId) {
		DDMFormInstanceReport ddmFormInstanceReport =
			new DDMFormInstanceReportImpl();

		ddmFormInstanceReport.setNew(true);
		ddmFormInstanceReport.setPrimaryKey(formInstanceReportId);

		ddmFormInstanceReport.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmFormInstanceReport;
	}

	/**
	 * Removes the ddm form instance report with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceReportId the primary key of the ddm form instance report
	 * @return the ddm form instance report that was removed
	 * @throws NoSuchFormInstanceReportException if a ddm form instance report with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceReport remove(long formInstanceReportId)
		throws NoSuchFormInstanceReportException {

		return remove((Serializable)formInstanceReportId);
	}

	@Override
	protected DDMFormInstanceReport removeImpl(
		DDMFormInstanceReport ddmFormInstanceReport) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmFormInstanceReport)) {
				ddmFormInstanceReport = (DDMFormInstanceReport)session.get(
					DDMFormInstanceReportImpl.class,
					ddmFormInstanceReport.getPrimaryKeyObj());
			}

			if ((ddmFormInstanceReport != null) &&
				ctPersistenceHelper.isRemove(ddmFormInstanceReport)) {

				session.delete(ddmFormInstanceReport);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmFormInstanceReport != null) {
			clearCache(ddmFormInstanceReport);
		}

		return ddmFormInstanceReport;
	}

	@Override
	public DDMFormInstanceReport updateImpl(
		DDMFormInstanceReport ddmFormInstanceReport) {

		boolean isNew = ddmFormInstanceReport.isNew();

		if (!(ddmFormInstanceReport instanceof
				DDMFormInstanceReportModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmFormInstanceReport.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmFormInstanceReport);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmFormInstanceReport proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMFormInstanceReport implementation " +
					ddmFormInstanceReport.getClass());
		}

		DDMFormInstanceReportModelImpl ddmFormInstanceReportModelImpl =
			(DDMFormInstanceReportModelImpl)ddmFormInstanceReport;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmFormInstanceReport.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmFormInstanceReport.setCreateDate(date);
			}
			else {
				ddmFormInstanceReport.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ddmFormInstanceReportModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmFormInstanceReport.setModifiedDate(date);
			}
			else {
				ddmFormInstanceReport.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmFormInstanceReport)) {
				if (!isNew) {
					session.evict(
						DDMFormInstanceReportImpl.class,
						ddmFormInstanceReport.getPrimaryKeyObj());
				}

				session.save(ddmFormInstanceReport);
			}
			else {
				ddmFormInstanceReport = (DDMFormInstanceReport)session.merge(
					ddmFormInstanceReport);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmFormInstanceReport, false);

		if (isNew) {
			ddmFormInstanceReport.setNew(false);
		}

		ddmFormInstanceReport.resetOriginalValues();

		return ddmFormInstanceReport;
	}

	/**
	 * Returns the ddm form instance report with the primary key or throws a <code>NoSuchFormInstanceReportException</code> if it could not be found.
	 *
	 * @param formInstanceReportId the primary key of the ddm form instance report
	 * @return the ddm form instance report
	 * @throws NoSuchFormInstanceReportException if a ddm form instance report with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceReport findByPrimaryKey(long formInstanceReportId)
		throws NoSuchFormInstanceReportException {

		return findByPrimaryKey((Serializable)formInstanceReportId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm form instance report with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceReportId the primary key of the ddm form instance report
	 * @return the ddm form instance report, or <code>null</code> if a ddm form instance report with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceReport fetchByPrimaryKey(long formInstanceReportId) {
		return fetchByPrimaryKey((Serializable)formInstanceReportId);
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
		return "formInstanceReportId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFORMINSTANCEREPORT;
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
		return DDMFormInstanceReportModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMFormInstanceReport";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("formInstanceId");
		ctMergeColumnNames.add("data_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("formInstanceReportId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the ddm form instance report persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByFormInstanceId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByFormInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"formInstanceId"}, 0, 0, false,
					DDMFormInstanceReport::getFormInstanceId),
				_SQL_SELECT_DDMFORMINSTANCEREPORT_WHERE, "",
				new FinderColumn<>(
					"ddmFormInstanceReport.", "formInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMFormInstanceReport::getFormInstanceId));

		DDMFormInstanceReportUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFormInstanceReportUtil.setPersistence(null);

		entityCache.removeCache(DDMFormInstanceReportImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_DDMFORMINSTANCEREPORT =
		"SELECT ddmFormInstanceReport FROM DDMFormInstanceReport ddmFormInstanceReport";

	private static final String _SQL_SELECT_DDMFORMINSTANCEREPORT_WHERE =
		"SELECT ddmFormInstanceReport FROM DDMFormInstanceReport ddmFormInstanceReport WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMFormInstanceReport exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceReportPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"data"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:354323527