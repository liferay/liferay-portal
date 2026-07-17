/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.closure;

import com.liferay.change.tracking.closure.CTClosure;
import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.reference.TableJoinHolder;
import com.liferay.change.tracking.internal.reference.TableReferenceDefinitionManager;
import com.liferay.change.tracking.internal.reference.TableReferenceInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 * @author Preston Crary
 */
@Component(service = CTClosureFactory.class)
public class CTClosureFactoryImpl implements CTClosureFactory {

	@Override
	public void clearCache(long ctCollectionId) {
		_ctClosuresPortalCache.remove(ctCollectionId);
	}

	@Override
	public CTClosure create(long ctCollectionId) {
		return create(ctCollectionId, 0);
	}

	@Override
	public CTClosure create(long ctCollectionId, long classNameId) {
		Map<Long, CTClosure> ctClosures = _ctClosuresPortalCache.get(
			ctCollectionId);

		if (ctClosures == null) {
			ctClosures = new ConcurrentHashMap<>();

			_ctClosuresPortalCache.put(ctCollectionId, ctClosures);
		}

		CTClosure ctClosure = ctClosures.get(classNameId);

		if (ctClosure != null) {
			return ctClosure;
		}

		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos;

		if (classNameId > 0) {
			combinedTableReferenceInfos =
				_tableReferenceDefinitionManager.getCombinedTableReferenceInfos(
					classNameId);
		}
		else {
			combinedTableReferenceInfos =
				_tableReferenceDefinitionManager.
					getCombinedTableReferenceInfos();
		}

		ctClosure = new CTClosureImpl(
			ctCollectionId,
			_buildClosureMap(
				ctCollectionId, classNameId, combinedTableReferenceInfos));

		ctClosures.put(classNameId, ctClosure);

		return ctClosure;
	}

	@Activate
	protected void activate() {
		_ctClosuresPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, _CT_CLOSURES_PORTAL_CACHE_NAME);

		DB db = DBManagerUtil.getDB();

		DBType dbType = db.getDBType();

		_oracle = dbType == DBType.ORACLE;

		if (dbType == DBType.SQLSERVER) {
			_sqlParameterLimit = _SQL_SERVER_PARAMETER_LIMIT;
		}
		else {
			_sqlParameterLimit = _SQL_PLACEHOLDER_LIMIT;
		}
	}

	private Map<Node, Collection<Node>> _buildClosureMap(
		long ctCollectionId, long classNameId,
		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos) {

		CTCollection ctCollection = _ctCollectionPersistence.fetchByPrimaryKey(
			ctCollectionId);
		Map<Long, List<Long>> map = new LinkedHashMap<>();

		List<CTEntry> ctEntries = new ArrayList<>(
			_ctEntryLocalService.getCTCollectionCTEntries(ctCollectionId));

		ctEntries.sort(
			(ctEntry1, ctEntry2) ->
				(int)(ctEntry1.getCtEntryId() - ctEntry2.getCtEntryId()));

		Collection<Node> nodes = new LinkedHashSet<>();

		for (CTEntry ctEntry : ctEntries) {
			if ((classNameId > 0) &&
				!combinedTableReferenceInfos.containsKey(
					ctEntry.getModelClassNameId())) {

				continue;
			}

			List<Long> primaryKeys = map.computeIfAbsent(
				ctEntry.getModelClassNameId(), key -> new ArrayList<>());

			primaryKeys.add(ctEntry.getModelClassPK());

			nodes.add(
				new Node(
					ctEntry.getModelClassNameId(), ctEntry.getModelClassPK()));
		}

		Map<Node, Collection<Edge>> edgeMap = new LinkedHashMap<>();

		ArrayDeque<Map.Entry<Long, List<Long>>> queue = new ArrayDeque<>(
			map.entrySet());

		while (!queue.isEmpty()) {
			Map.Entry<Long, List<Long>> queueEntry = queue.poll();

			long childClassNameId = queueEntry.getKey();

			TableReferenceInfo<?> childTableReferenceInfo =
				combinedTableReferenceInfos.get(childClassNameId);

			if (childTableReferenceInfo == null) {
				if ((ctCollection != null) && !ctCollection.isInProgress() &&
					(ctCollection.getStatus() !=
						WorkflowConstants.STATUS_PENDING)) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"No table reference definition for " +
								childClassNameId);
					}

					continue;
				}

				throw new IllegalArgumentException(
					"No table reference definition for " + childClassNameId);
			}

			List<Long> childPrimaryKeys = queueEntry.getValue();

			Long[] childPrimaryKeysArray = childPrimaryKeys.toArray(
				new Long[0]);

			Map<Table<?>, List<TableJoinHolder>> parentTableJoinHoldersMap =
				childTableReferenceInfo.getParentTableJoinHoldersMap();

			for (Map.Entry<Table<?>, List<TableJoinHolder>> entry :
					parentTableJoinHoldersMap.entrySet()) {

				long parentClassNameId =
					_tableReferenceDefinitionManager.getClassNameId(
						entry.getKey());

				if ((classNameId > 0) && !map.containsKey(parentClassNameId)) {
					continue;
				}

				TableReferenceInfo<?> parentTableReferenceInfo =
					combinedTableReferenceInfos.get(parentClassNameId);

				int i = 0;

				while (i < childPrimaryKeysArray.length) {
					int batchSize = _sqlParameterLimit;

					if ((i + batchSize) > childPrimaryKeysArray.length) {
						batchSize = childPrimaryKeysArray.length - i;
					}

					Long[] batchChildPrimaryKeys = new Long[batchSize];

					System.arraycopy(
						childPrimaryKeysArray, i, batchChildPrimaryKeys, 0,
						batchSize);

					List<Long> newParentPrimaryKeys = _collectParentPrimaryKeys(
						childClassNameId, batchChildPrimaryKeys, ctCollectionId,
						entry, edgeMap, nodes, parentClassNameId, classNameId,
						parentTableReferenceInfo);

					if (newParentPrimaryKeys != null) {
						queue.add(
							new AbstractMap.SimpleImmutableEntry<>(
								parentClassNameId, newParentPrimaryKeys));
					}

					i += batchSize;
				}
			}
		}

		return _getNodeMap(nodes, edgeMap);
	}

	private List<Long> _collectParentPrimaryKeys(
		long childClassNameId, Long[] childPrimaryKeys, long ctCollectionId,
		Map.Entry<Table<?>, List<TableJoinHolder>> entry,
		Map<Node, Collection<Edge>> edgeMap, Collection<Node> nodes,
		long parentClassNameId, long classNameId,
		TableReferenceInfo<?> parentTableReferenceInfo) {

		List<Long> newParentPrimaryKeys = null;

		int i = 0;

		while (i < childPrimaryKeys.length) {
			int batchSize = _sqlParameterLimit;

			if ((i + batchSize) > childPrimaryKeys.length) {
				batchSize = childPrimaryKeys.length - i;
			}

			Long[] batchChildPrimaryKeys = new Long[batchSize];

			System.arraycopy(
				childPrimaryKeys, i, batchChildPrimaryKeys, 0, batchSize);

			DSLQuery dslQuery = _getDSLQuery(
				ctCollectionId, batchChildPrimaryKeys, entry.getValue());

			try (Connection connection = _getConnection(
					parentTableReferenceInfo);

				PreparedStatement preparedStatement = _getPreparedStatement(
					connection, dslQuery);

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					Node parentNode = new Node(
						parentClassNameId, resultSet.getLong(1));

					if ((classNameId > 0) && !nodes.contains(parentNode)) {
						continue;
					}

					Node childNode = new Node(
						childClassNameId, resultSet.getLong(2));

					if (nodes.add(parentNode)) {
						if (newParentPrimaryKeys == null) {
							newParentPrimaryKeys = new ArrayList<>();
						}

						newParentPrimaryKeys.add(parentNode.getPrimaryKey());
					}

					Collection<Edge> edges = edgeMap.computeIfAbsent(
						parentNode, key -> new ArrayList<>());

					edges.add(new Edge(parentNode, childNode));
				}
			}
			catch (SQLException sqlException) {
				throw new ORMException(
					"Unable to execute query: " + dslQuery, sqlException);
			}

			i += batchSize;
		}

		return newParentPrimaryKeys;
	}

	private void _filterCyclingEdges(
		Edge edge, Map<Node, Collection<Edge>> edgeMap,
		Deque<Edge> backtraceEdges, Set<Edge> cyclingEdges,
		Set<Edge> resolvedEdges) {

		if (backtraceEdges.contains(edge)) {
			cyclingEdges.add(edge);

			return;
		}

		if (resolvedEdges.contains(edge) || cyclingEdges.contains(edge)) {
			return;
		}

		Collection<Edge> nextEdges = edgeMap.get(edge.getToNode());

		if (nextEdges == null) {
			resolvedEdges.add(edge);

			return;
		}

		backtraceEdges.push(edge);

		for (Edge nextEdge : nextEdges) {
			_filterCyclingEdges(
				nextEdge, edgeMap, backtraceEdges, cyclingEdges, resolvedEdges);
		}

		backtraceEdges.pop();

		if (!cyclingEdges.contains(edge)) {
			resolvedEdges.add(edge);
		}
	}

	private Predicate _getChildPKColumnPredicate(
		Column<?, Long> childPKColumn, Long[] childPrimaryKeysArray) {

		if (!_oracle) {
			return childPKColumn.in(childPrimaryKeysArray);
		}

		Predicate predicate = null;

		int i = 0;

		while (i < childPrimaryKeysArray.length) {
			int batchSize = _ORACLE_IN_CLAUSE_LIMIT;

			if ((i + batchSize) > childPrimaryKeysArray.length) {
				batchSize = childPrimaryKeysArray.length - i;
			}

			Long[] batchChildPrimaryKeys = new Long[batchSize];

			System.arraycopy(
				childPrimaryKeysArray, i, batchChildPrimaryKeys, 0, batchSize);

			if (predicate == null) {
				predicate = childPKColumn.in(batchChildPrimaryKeys);
			}
			else {
				predicate = predicate.or(
					childPKColumn.in(batchChildPrimaryKeys));
			}

			i += batchSize;
		}

		return predicate.withParentheses();
	}

	private Connection _getConnection(TableReferenceInfo<?> tableReferenceInfo)
		throws SQLException {

		TableReferenceDefinition<?> tableReferenceDefinition =
			tableReferenceInfo.getTableReferenceDefinition();

		BasePersistence<?> basePersistence =
			tableReferenceDefinition.getBasePersistence();

		DataSource dataSource = basePersistence.getDataSource();

		return dataSource.getConnection();
	}

	private DSLQuery _getDSLQuery(
		long ctCollectionId, Long[] childPrimaryKeysArray,
		List<TableJoinHolder> tableJoinHolders) {

		DSLQuery dslQuery = null;

		for (TableJoinHolder parentJoinHolder : tableJoinHolders) {
			Column<?, Long> parentPKColumn =
				parentJoinHolder.getParentPKColumn();
			Column<?, Long> childPKColumn = parentJoinHolder.getChildPKColumn();

			FromStep fromStep = DSLQueryFactoryUtil.selectDistinct(
				parentPKColumn, childPKColumn);

			Function<FromStep, JoinStep> joinFunction =
				parentJoinHolder.getJoinFunction();

			JoinStep joinStep = joinFunction.apply(fromStep);

			GroupByStep groupByStep = joinStep.where(
				() -> _getChildPKColumnPredicate(
					childPKColumn, childPrimaryKeysArray
				).and(
					() -> {
						Table<?> parentTable = parentPKColumn.getTable();

						Column<?, Long> ctCollectionIdColumn =
							parentTable.getColumn("ctCollectionId", Long.class);

						if ((ctCollectionIdColumn == null) ||
							!ctCollectionIdColumn.isPrimaryKey()) {

							return null;
						}

						return ctCollectionIdColumn.eq(
							CTConstants.CT_COLLECTION_ID_PRODUCTION
						).or(
							ctCollectionIdColumn.eq(ctCollectionId)
						).withParentheses();
					}
				));

			if (dslQuery == null) {
				dslQuery = groupByStep;
			}
			else {
				dslQuery = dslQuery.union(groupByStep);
			}
		}

		return dslQuery;
	}

	private Map<Node, Collection<Node>> _getNodeMap(
		Collection<Node> nodes, Map<Node, Collection<Edge>> edgeMap) {

		Map<Node, Collection<Node>> nodeMap = new HashMap<>();

		Deque<Edge> backtraceEdges = new LinkedList<>();
		Set<Edge> cyclingEdges = new HashSet<>();
		Set<Edge> resolvedEdges = new HashSet<>();

		for (Collection<Edge> edges : edgeMap.values()) {
			for (Edge edge : edges) {
				_filterCyclingEdges(
					edge, edgeMap, backtraceEdges, cyclingEdges, resolvedEdges);
			}
		}

		for (Edge edge : resolvedEdges) {
			Collection<Node> children = nodeMap.computeIfAbsent(
				edge.getFromNode(), node -> new LinkedHashSet<>());

			Node toNode = edge.getToNode();

			children.add(toNode);

			nodes.remove(toNode);
		}

		nodeMap.put(Node.ROOT_NODE, nodes);

		return nodeMap;
	}

	private PreparedStatement _getPreparedStatement(
			Connection connection, DSLQuery dslQuery)
		throws SQLException {

		DefaultASTNodeListener defaultASTNodeListener =
			new DefaultASTNodeListener();

		PreparedStatement preparedStatement = connection.prepareStatement(
			SQLTransformer.transform(dslQuery.toSQL(defaultASTNodeListener)));

		List<Object> scalarValues = defaultASTNodeListener.getScalarValues();

		for (int i = 0; i < scalarValues.size(); i++) {
			preparedStatement.setObject(i + 1, scalarValues.get(i));
		}

		return preparedStatement;
	}

	private static final String _CT_CLOSURES_PORTAL_CACHE_NAME =
		CTClosureFactoryImpl.class.getName() + "._ctClosuresPortalCache";

	private static final int _ORACLE_IN_CLAUSE_LIMIT = 1000;

	private static final int _SQL_PLACEHOLDER_LIMIT = 65533;

	private static final int _SQL_SERVER_PARAMETER_LIMIT = 2000;

	private static final Log _log = LogFactoryUtil.getLog(
		CTClosureFactoryImpl.class);

	private PortalCache<Long, Map<Long, CTClosure>> _ctClosuresPortalCache;

	@Reference
	private CTCollectionPersistence _ctCollectionPersistence;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	private boolean _oracle;
	private int _sqlParameterLimit;

	@Reference
	private TableReferenceDefinitionManager _tableReferenceDefinitionManager;

}