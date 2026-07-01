/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchNodeSettingException;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSetting;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSettingTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNodeSettingImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNodeSettingModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodeSettingPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodeSettingUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
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
 * The persistence implementation for the kaleo node setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoNodeSettingPersistence.class)
public class KaleoNodeSettingPersistenceImpl
	extends BasePersistenceImpl<KaleoNodeSetting, NoSuchNodeSettingException>
	implements KaleoNodeSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoNodeSettingUtil</code> to access the kaleo node setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoNodeSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoNodeSetting, NoSuchNodeSettingException>
			_collectionPersistenceFinderByKaleoNodeId;

	/**
	 * Returns an ordered range of all the kaleo node settings where kaleoNodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNodeSettingModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param start the lower bound of the range of kaleo node settings
	 * @param end the upper bound of the range of kaleo node settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo node settings
	 */
	@Override
	public List<KaleoNodeSetting> findByKaleoNodeId(
		long kaleoNodeId, int start, int end,
		OrderByComparator<KaleoNodeSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoNodeId.find(
			finderCache, new Object[] {kaleoNodeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo node setting in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node setting
	 * @throws NoSuchNodeSettingException if a matching kaleo node setting could not be found
	 */
	@Override
	public KaleoNodeSetting findByKaleoNodeId_First(
			long kaleoNodeId,
			OrderByComparator<KaleoNodeSetting> orderByComparator)
		throws NoSuchNodeSettingException {

		return _collectionPersistenceFinderByKaleoNodeId.findFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo node setting in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node setting, or <code>null</code> if a matching kaleo node setting could not be found
	 */
	@Override
	public KaleoNodeSetting fetchByKaleoNodeId_First(
		long kaleoNodeId,
		OrderByComparator<KaleoNodeSetting> orderByComparator) {

		return _collectionPersistenceFinderByKaleoNodeId.fetchFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo node settings where kaleoNodeId = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 */
	@Override
	public void removeByKaleoNodeId(long kaleoNodeId) {
		_collectionPersistenceFinderByKaleoNodeId.remove(
			finderCache, new Object[] {kaleoNodeId});
	}

	/**
	 * Returns the number of kaleo node settings where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the number of matching kaleo node settings
	 */
	@Override
	public int countByKaleoNodeId(long kaleoNodeId) {
		return _collectionPersistenceFinderByKaleoNodeId.count(
			finderCache, new Object[] {kaleoNodeId});
	}

	private UniquePersistenceFinder
		<KaleoNodeSetting, NoSuchNodeSettingException>
			_uniquePersistenceFinderByKNI_N;

	/**
	 * Returns the kaleo node setting where kaleoNodeId = &#63; and name = &#63; or throws a <code>NoSuchNodeSettingException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the matching kaleo node setting
	 * @throws NoSuchNodeSettingException if a matching kaleo node setting could not be found
	 */
	@Override
	public KaleoNodeSetting findByKNI_N(long kaleoNodeId, String name)
		throws NoSuchNodeSettingException {

		return _uniquePersistenceFinderByKNI_N.find(
			finderCache, new Object[] {kaleoNodeId, name});
	}

	/**
	 * Returns the kaleo node setting where kaleoNodeId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo node setting, or <code>null</code> if a matching kaleo node setting could not be found
	 */
	@Override
	public KaleoNodeSetting fetchByKNI_N(
		long kaleoNodeId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByKNI_N.fetch(
			finderCache, new Object[] {kaleoNodeId, name}, useFinderCache);
	}

	/**
	 * Removes the kaleo node setting where kaleoNodeId = &#63; and name = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the kaleo node setting that was removed
	 */
	@Override
	public KaleoNodeSetting removeByKNI_N(long kaleoNodeId, String name)
		throws NoSuchNodeSettingException {

		KaleoNodeSetting kaleoNodeSetting = findByKNI_N(kaleoNodeId, name);

		return remove(kaleoNodeSetting);
	}

	/**
	 * Returns the number of kaleo node settings where kaleoNodeId = &#63; and name = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param name the name
	 * @return the number of matching kaleo node settings
	 */
	@Override
	public int countByKNI_N(long kaleoNodeId, String name) {
		return _uniquePersistenceFinderByKNI_N.count(
			finderCache, new Object[] {kaleoNodeId, name});
	}

	public KaleoNodeSettingPersistenceImpl() {
		setModelClass(KaleoNodeSetting.class);

		setModelImplClass(KaleoNodeSettingImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoNodeSettingTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo node setting with the primary key. Does not add the kaleo node setting to the database.
	 *
	 * @param kaleoNodeSettingId the primary key for the new kaleo node setting
	 * @return the new kaleo node setting
	 */
	@Override
	public KaleoNodeSetting create(long kaleoNodeSettingId) {
		KaleoNodeSetting kaleoNodeSetting = new KaleoNodeSettingImpl();

		kaleoNodeSetting.setNew(true);
		kaleoNodeSetting.setPrimaryKey(kaleoNodeSettingId);

		kaleoNodeSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoNodeSetting;
	}

	/**
	 * Removes the kaleo node setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoNodeSettingId the primary key of the kaleo node setting
	 * @return the kaleo node setting that was removed
	 * @throws NoSuchNodeSettingException if a kaleo node setting with the primary key could not be found
	 */
	@Override
	public KaleoNodeSetting remove(long kaleoNodeSettingId)
		throws NoSuchNodeSettingException {

		return remove((Serializable)kaleoNodeSettingId);
	}

	@Override
	protected KaleoNodeSetting removeImpl(KaleoNodeSetting kaleoNodeSetting) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoNodeSetting)) {
				kaleoNodeSetting = (KaleoNodeSetting)session.get(
					KaleoNodeSettingImpl.class,
					kaleoNodeSetting.getPrimaryKeyObj());
			}

			if ((kaleoNodeSetting != null) &&
				ctPersistenceHelper.isRemove(kaleoNodeSetting)) {

				session.delete(kaleoNodeSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoNodeSetting != null) {
			clearCache(kaleoNodeSetting);
		}

		return kaleoNodeSetting;
	}

	@Override
	public KaleoNodeSetting updateImpl(KaleoNodeSetting kaleoNodeSetting) {
		boolean isNew = kaleoNodeSetting.isNew();

		if (!(kaleoNodeSetting instanceof KaleoNodeSettingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoNodeSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoNodeSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoNodeSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoNodeSetting implementation " +
					kaleoNodeSetting.getClass());
		}

		KaleoNodeSettingModelImpl kaleoNodeSettingModelImpl =
			(KaleoNodeSettingModelImpl)kaleoNodeSetting;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoNodeSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoNodeSetting.setCreateDate(date);
			}
			else {
				kaleoNodeSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoNodeSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoNodeSetting.setModifiedDate(date);
			}
			else {
				kaleoNodeSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoNodeSetting)) {
				if (!isNew) {
					session.evict(
						KaleoNodeSettingImpl.class,
						kaleoNodeSetting.getPrimaryKeyObj());
				}

				session.save(kaleoNodeSetting);
			}
			else {
				kaleoNodeSetting = (KaleoNodeSetting)session.merge(
					kaleoNodeSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoNodeSetting, false);

		if (isNew) {
			kaleoNodeSetting.setNew(false);
		}

		kaleoNodeSetting.resetOriginalValues();

		return kaleoNodeSetting;
	}

	/**
	 * Returns the kaleo node setting with the primary key or throws a <code>NoSuchNodeSettingException</code> if it could not be found.
	 *
	 * @param kaleoNodeSettingId the primary key of the kaleo node setting
	 * @return the kaleo node setting
	 * @throws NoSuchNodeSettingException if a kaleo node setting with the primary key could not be found
	 */
	@Override
	public KaleoNodeSetting findByPrimaryKey(long kaleoNodeSettingId)
		throws NoSuchNodeSettingException {

		return findByPrimaryKey((Serializable)kaleoNodeSettingId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo node setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoNodeSettingId the primary key of the kaleo node setting
	 * @return the kaleo node setting, or <code>null</code> if a kaleo node setting with the primary key could not be found
	 */
	@Override
	public KaleoNodeSetting fetchByPrimaryKey(long kaleoNodeSettingId) {
		return fetchByPrimaryKey((Serializable)kaleoNodeSettingId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoNodeSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEONODESETTING;
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
		return KaleoNodeSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoNodeSetting";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoNodeSettingId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"kaleoNodeId", "name"});
	}

	/**
	 * Initializes the kaleo node setting persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByKaleoNodeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKaleoNodeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, false),
				_SQL_SELECT_KALEONODESETTING_WHERE,
				_SQL_COUNT_KALEONODESETTING_WHERE,
				KaleoNodeSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"kaleoNodeSetting.", "kaleoNodeId", FinderColumn.Type.LONG,
					"=", true, true, KaleoNodeSetting::getKaleoNodeId));

		_uniquePersistenceFinderByKNI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKNI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"kaleoNodeId", "name"}, 0, 2, false,
				KaleoNodeSetting::getKaleoNodeId,
				convertNullFunction(KaleoNodeSetting::getName)),
			_SQL_SELECT_KALEONODESETTING_WHERE, "",
			new FinderColumn<>(
				"kaleoNodeSetting.", "kaleoNodeId", FinderColumn.Type.LONG, "=",
				true, true, KaleoNodeSetting::getKaleoNodeId),
			new FinderColumn<>(
				"kaleoNodeSetting.", "name", FinderColumn.Type.STRING, "=",
				true, true, KaleoNodeSetting::getName));

		KaleoNodeSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoNodeSettingUtil.setPersistence(null);

		entityCache.removeCache(KaleoNodeSettingImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _ENTITY_ALIAS_PREFIX =
		KaleoNodeSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEONODESETTING =
		"SELECT kaleoNodeSetting FROM KaleoNodeSetting kaleoNodeSetting";

	private static final String _SQL_SELECT_KALEONODESETTING_WHERE =
		"SELECT kaleoNodeSetting FROM KaleoNodeSetting kaleoNodeSetting WHERE ";

	private static final String _SQL_COUNT_KALEONODESETTING_WHERE =
		"SELECT COUNT(kaleoNodeSetting) FROM KaleoNodeSetting kaleoNodeSetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoNodeSetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoNodeSettingPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:635138858