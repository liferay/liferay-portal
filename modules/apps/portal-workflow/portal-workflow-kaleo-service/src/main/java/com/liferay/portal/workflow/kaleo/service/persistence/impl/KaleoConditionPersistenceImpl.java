/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.workflow.kaleo.exception.NoSuchConditionException;
import com.liferay.portal.workflow.kaleo.model.KaleoCondition;
import com.liferay.portal.workflow.kaleo.model.KaleoConditionTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoConditionImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoConditionModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoConditionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoConditionUtil;
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
 * The persistence implementation for the kaleo condition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoConditionPersistence.class)
public class KaleoConditionPersistenceImpl
	extends BasePersistenceImpl<KaleoCondition, NoSuchConditionException>
	implements KaleoConditionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoConditionUtil</code> to access the kaleo condition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoConditionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoCondition, NoSuchConditionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo conditions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoConditionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo conditions
	 * @param end the upper bound of the range of kaleo conditions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo conditions
	 */
	@Override
	public List<KaleoCondition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoCondition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo condition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo condition
	 * @throws NoSuchConditionException if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition findByCompanyId_First(
			long companyId, OrderByComparator<KaleoCondition> orderByComparator)
		throws NoSuchConditionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo condition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo condition, or <code>null</code> if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoCondition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo conditions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo conditions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo conditions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoCondition, NoSuchConditionException>
			_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo conditions where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoConditionModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo conditions
	 * @param end the upper bound of the range of kaleo conditions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo conditions
	 */
	@Override
	public List<KaleoCondition> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoCondition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo condition in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo condition
	 * @throws NoSuchConditionException if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoCondition> orderByComparator)
		throws NoSuchConditionException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo condition in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo condition, or <code>null</code> if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoCondition> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo conditions where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKaleoDefinitionVersionId.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo conditions where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo conditions
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private UniquePersistenceFinder<KaleoCondition, NoSuchConditionException>
		_uniquePersistenceFinderByKaleoNodeId;

	/**
	 * Returns the kaleo condition where kaleoNodeId = &#63; or throws a <code>NoSuchConditionException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the matching kaleo condition
	 * @throws NoSuchConditionException if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition findByKaleoNodeId(long kaleoNodeId)
		throws NoSuchConditionException {

		return _uniquePersistenceFinderByKaleoNodeId.find(
			finderCache, new Object[] {kaleoNodeId});
	}

	/**
	 * Returns the kaleo condition where kaleoNodeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo condition, or <code>null</code> if a matching kaleo condition could not be found
	 */
	@Override
	public KaleoCondition fetchByKaleoNodeId(
		long kaleoNodeId, boolean useFinderCache) {

		return _uniquePersistenceFinderByKaleoNodeId.fetch(
			finderCache, new Object[] {kaleoNodeId}, useFinderCache);
	}

	/**
	 * Removes the kaleo condition where kaleoNodeId = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the kaleo condition that was removed
	 */
	@Override
	public KaleoCondition removeByKaleoNodeId(long kaleoNodeId)
		throws NoSuchConditionException {

		KaleoCondition kaleoCondition = findByKaleoNodeId(kaleoNodeId);

		return remove(kaleoCondition);
	}

	/**
	 * Returns the number of kaleo conditions where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the number of matching kaleo conditions
	 */
	@Override
	public int countByKaleoNodeId(long kaleoNodeId) {
		return _uniquePersistenceFinderByKaleoNodeId.count(
			finderCache, new Object[] {kaleoNodeId});
	}

	public KaleoConditionPersistenceImpl() {
		setModelClass(KaleoCondition.class);

		setModelImplClass(KaleoConditionImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoConditionTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo condition with the primary key. Does not add the kaleo condition to the database.
	 *
	 * @param kaleoConditionId the primary key for the new kaleo condition
	 * @return the new kaleo condition
	 */
	@Override
	public KaleoCondition create(long kaleoConditionId) {
		KaleoCondition kaleoCondition = new KaleoConditionImpl();

		kaleoCondition.setNew(true);
		kaleoCondition.setPrimaryKey(kaleoConditionId);

		kaleoCondition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoCondition;
	}

	/**
	 * Removes the kaleo condition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoConditionId the primary key of the kaleo condition
	 * @return the kaleo condition that was removed
	 * @throws NoSuchConditionException if a kaleo condition with the primary key could not be found
	 */
	@Override
	public KaleoCondition remove(long kaleoConditionId)
		throws NoSuchConditionException {

		return remove((Serializable)kaleoConditionId);
	}

	@Override
	protected KaleoCondition removeImpl(KaleoCondition kaleoCondition) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoCondition)) {
				kaleoCondition = (KaleoCondition)session.get(
					KaleoConditionImpl.class,
					kaleoCondition.getPrimaryKeyObj());
			}

			if ((kaleoCondition != null) &&
				ctPersistenceHelper.isRemove(kaleoCondition)) {

				session.delete(kaleoCondition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoCondition != null) {
			clearCache(kaleoCondition);
		}

		return kaleoCondition;
	}

	@Override
	public KaleoCondition updateImpl(KaleoCondition kaleoCondition) {
		boolean isNew = kaleoCondition.isNew();

		if (!(kaleoCondition instanceof KaleoConditionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoCondition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoCondition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoCondition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoCondition implementation " +
					kaleoCondition.getClass());
		}

		KaleoConditionModelImpl kaleoConditionModelImpl =
			(KaleoConditionModelImpl)kaleoCondition;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoCondition.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoCondition.setCreateDate(date);
			}
			else {
				kaleoCondition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoConditionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoCondition.setModifiedDate(date);
			}
			else {
				kaleoCondition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoCondition)) {
				if (!isNew) {
					session.evict(
						KaleoConditionImpl.class,
						kaleoCondition.getPrimaryKeyObj());
				}

				session.save(kaleoCondition);
			}
			else {
				kaleoCondition = (KaleoCondition)session.merge(kaleoCondition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoCondition, false);

		if (isNew) {
			kaleoCondition.setNew(false);
		}

		kaleoCondition.resetOriginalValues();

		return kaleoCondition;
	}

	/**
	 * Returns the kaleo condition with the primary key or throws a <code>NoSuchConditionException</code> if it could not be found.
	 *
	 * @param kaleoConditionId the primary key of the kaleo condition
	 * @return the kaleo condition
	 * @throws NoSuchConditionException if a kaleo condition with the primary key could not be found
	 */
	@Override
	public KaleoCondition findByPrimaryKey(long kaleoConditionId)
		throws NoSuchConditionException {

		return findByPrimaryKey((Serializable)kaleoConditionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo condition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoConditionId the primary key of the kaleo condition
	 * @return the kaleo condition, or <code>null</code> if a kaleo condition with the primary key could not be found
	 */
	@Override
	public KaleoCondition fetchByPrimaryKey(long kaleoConditionId) {
		return fetchByPrimaryKey((Serializable)kaleoConditionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoConditionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOCONDITION;
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
		return KaleoConditionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoCondition";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("script");
		ctMergeColumnNames.add("scriptLanguage");
		ctMergeColumnNames.add("scriptRequiredContexts");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoConditionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo condition persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_KALEOCONDITION_WHERE,
				_SQL_COUNT_KALEOCONDITION_WHERE,
				KaleoConditionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoCondition.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoCondition::getCompanyId));

		_collectionPersistenceFinderByKaleoDefinitionVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, false),
				_SQL_SELECT_KALEOCONDITION_WHERE,
				_SQL_COUNT_KALEOCONDITION_WHERE,
				KaleoConditionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoCondition.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoCondition::getKaleoDefinitionVersionId));

		_uniquePersistenceFinderByKaleoNodeId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKaleoNodeId",
				new String[] {Long.class.getName()},
				new String[] {"kaleoNodeId"}, 0, 0, false,
				KaleoCondition::getKaleoNodeId),
			_SQL_SELECT_KALEOCONDITION_WHERE, "",
			new FinderColumn<>(
				"kaleoCondition.", "kaleoNodeId", FinderColumn.Type.LONG, "=",
				true, true, KaleoCondition::getKaleoNodeId));

		KaleoConditionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoConditionUtil.setPersistence(null);

		entityCache.removeCache(KaleoConditionImpl.class.getName());
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
		KaleoConditionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOCONDITION =
		"SELECT kaleoCondition FROM KaleoCondition kaleoCondition";

	private static final String _SQL_SELECT_KALEOCONDITION_WHERE =
		"SELECT kaleoCondition FROM KaleoCondition kaleoCondition WHERE ";

	private static final String _SQL_COUNT_KALEOCONDITION_WHERE =
		"SELECT COUNT(kaleoCondition) FROM KaleoCondition kaleoCondition WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoCondition exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoConditionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-594868692