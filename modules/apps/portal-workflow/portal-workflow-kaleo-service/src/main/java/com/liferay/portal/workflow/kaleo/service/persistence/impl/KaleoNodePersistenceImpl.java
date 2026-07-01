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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchNodeException;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNodeImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoNodeModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodeUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

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
 * The persistence implementation for the kaleo node service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoNodePersistence.class)
public class KaleoNodePersistenceImpl
	extends BasePersistenceImpl<KaleoNode, NoSuchNodeException>
	implements KaleoNodePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoNodeUtil</code> to access the kaleo node persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoNodeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoNode, NoSuchNodeException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo nodes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNodeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo nodes
	 * @param end the upper bound of the range of kaleo nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo nodes
	 */
	@Override
	public List<KaleoNode> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoNode> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo node in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node
	 * @throws NoSuchNodeException if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode findByCompanyId_First(
			long companyId, OrderByComparator<KaleoNode> orderByComparator)
		throws NoSuchNodeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo node in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node, or <code>null</code> if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoNode> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo nodes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo nodes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo nodes
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoNode, NoSuchNodeException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo nodes where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNodeModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo nodes
	 * @param end the upper bound of the range of kaleo nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo nodes
	 */
	@Override
	public List<KaleoNode> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoNode> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo node in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node
	 * @throws NoSuchNodeException if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoNode> orderByComparator)
		throws NoSuchNodeException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo node in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node, or <code>null</code> if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoNode> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo nodes where kaleoDefinitionVersionId = &#63; from the database.
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
	 * Returns the number of kaleo nodes where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo nodes
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoNode, NoSuchNodeException>
		_collectionPersistenceFinderByC_KDVI;

	/**
	 * Returns an ordered range of all the kaleo nodes where companyId = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoNodeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo nodes
	 * @param end the upper bound of the range of kaleo nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo nodes
	 */
	@Override
	public List<KaleoNode> findByC_KDVI(
		long companyId, long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoNode> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_KDVI.find(
			finderCache, new Object[] {companyId, kaleoDefinitionVersionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo node in the ordered set where companyId = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node
	 * @throws NoSuchNodeException if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode findByC_KDVI_First(
			long companyId, long kaleoDefinitionVersionId,
			OrderByComparator<KaleoNode> orderByComparator)
		throws NoSuchNodeException {

		return _collectionPersistenceFinderByC_KDVI.findFirst(
			finderCache, new Object[] {companyId, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo node in the ordered set where companyId = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo node, or <code>null</code> if a matching kaleo node could not be found
	 */
	@Override
	public KaleoNode fetchByC_KDVI_First(
		long companyId, long kaleoDefinitionVersionId,
		OrderByComparator<KaleoNode> orderByComparator) {

		return _collectionPersistenceFinderByC_KDVI.fetchFirst(
			finderCache, new Object[] {companyId, kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo nodes where companyId = &#63; and kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByC_KDVI(long companyId, long kaleoDefinitionVersionId) {
		_collectionPersistenceFinderByC_KDVI.remove(
			finderCache, new Object[] {companyId, kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo nodes where companyId = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo nodes
	 */
	@Override
	public int countByC_KDVI(long companyId, long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByC_KDVI.count(
			finderCache, new Object[] {companyId, kaleoDefinitionVersionId});
	}

	public KaleoNodePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");
		dbColumnNames.put("initial", "initial_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoNode.class);

		setModelImplClass(KaleoNodeImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoNodeTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo node with the primary key. Does not add the kaleo node to the database.
	 *
	 * @param kaleoNodeId the primary key for the new kaleo node
	 * @return the new kaleo node
	 */
	@Override
	public KaleoNode create(long kaleoNodeId) {
		KaleoNode kaleoNode = new KaleoNodeImpl();

		kaleoNode.setNew(true);
		kaleoNode.setPrimaryKey(kaleoNodeId);

		kaleoNode.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoNode;
	}

	/**
	 * Removes the kaleo node with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoNodeId the primary key of the kaleo node
	 * @return the kaleo node that was removed
	 * @throws NoSuchNodeException if a kaleo node with the primary key could not be found
	 */
	@Override
	public KaleoNode remove(long kaleoNodeId) throws NoSuchNodeException {
		return remove((Serializable)kaleoNodeId);
	}

	@Override
	protected KaleoNode removeImpl(KaleoNode kaleoNode) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoNode)) {
				kaleoNode = (KaleoNode)session.get(
					KaleoNodeImpl.class, kaleoNode.getPrimaryKeyObj());
			}

			if ((kaleoNode != null) &&
				ctPersistenceHelper.isRemove(kaleoNode)) {

				session.delete(kaleoNode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoNode != null) {
			clearCache(kaleoNode);
		}

		return kaleoNode;
	}

	@Override
	public KaleoNode updateImpl(KaleoNode kaleoNode) {
		boolean isNew = kaleoNode.isNew();

		if (!(kaleoNode instanceof KaleoNodeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoNode.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kaleoNode);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoNode proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoNode implementation " +
					kaleoNode.getClass());
		}

		KaleoNodeModelImpl kaleoNodeModelImpl = (KaleoNodeModelImpl)kaleoNode;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoNode.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoNode.setCreateDate(date);
			}
			else {
				kaleoNode.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoNodeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoNode.setModifiedDate(date);
			}
			else {
				kaleoNode.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoNode)) {
				if (!isNew) {
					session.evict(
						KaleoNodeImpl.class, kaleoNode.getPrimaryKeyObj());
				}

				session.save(kaleoNode);
			}
			else {
				kaleoNode = (KaleoNode)session.merge(kaleoNode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoNode, false);

		if (isNew) {
			kaleoNode.setNew(false);
		}

		kaleoNode.resetOriginalValues();

		return kaleoNode;
	}

	/**
	 * Returns the kaleo node with the primary key or throws a <code>NoSuchNodeException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the primary key of the kaleo node
	 * @return the kaleo node
	 * @throws NoSuchNodeException if a kaleo node with the primary key could not be found
	 */
	@Override
	public KaleoNode findByPrimaryKey(long kaleoNodeId)
		throws NoSuchNodeException {

		return findByPrimaryKey((Serializable)kaleoNodeId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo node with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoNodeId the primary key of the kaleo node
	 * @return the kaleo node, or <code>null</code> if a kaleo node with the primary key could not be found
	 */
	@Override
	public KaleoNode fetchByPrimaryKey(long kaleoNodeId) {
		return fetchByPrimaryKey((Serializable)kaleoNodeId);
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
		return "kaleoNodeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEONODE;
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
		return KaleoNodeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoNode";
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
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("label");
		ctMergeColumnNames.add("metadata");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("initial_");
		ctMergeColumnNames.add("terminal");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kaleoNodeId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo node persistence.
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
				_SQL_SELECT_KALEONODE_WHERE, _SQL_COUNT_KALEONODE_WHERE,
				KaleoNodeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNode.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoNode::getCompanyId));

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
				_SQL_SELECT_KALEONODE_WHERE, _SQL_COUNT_KALEONODE_WHERE,
				KaleoNodeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNode.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNode::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByC_KDVI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_KDVI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "kaleoDefinitionVersionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_KDVI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "kaleoDefinitionVersionId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_KDVI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "kaleoDefinitionVersionId"},
					false),
				_SQL_SELECT_KALEONODE_WHERE, _SQL_COUNT_KALEONODE_WHERE,
				KaleoNodeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoNode.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoNode::getCompanyId),
				new FinderColumn<>(
					"kaleoNode.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoNode::getKaleoDefinitionVersionId));

		KaleoNodeUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoNodeUtil.setPersistence(null);

		entityCache.removeCache(KaleoNodeImpl.class.getName());
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
		KaleoNodeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEONODE =
		"SELECT kaleoNode FROM KaleoNode kaleoNode";

	private static final String _SQL_SELECT_KALEONODE_WHERE =
		"SELECT kaleoNode FROM KaleoNode kaleoNode WHERE ";

	private static final String _SQL_COUNT_KALEONODE_WHERE =
		"SELECT COUNT(kaleoNode) FROM KaleoNode kaleoNode WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoNode exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type", "initial"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:343444223