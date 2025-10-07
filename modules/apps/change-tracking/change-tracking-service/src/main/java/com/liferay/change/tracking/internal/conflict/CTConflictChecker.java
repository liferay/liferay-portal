/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.conflict.CTEntryConflictHelper;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.CTRowUtil;
import com.liferay.change.tracking.internal.reference.TableJoinHolder;
import com.liferay.change.tracking.internal.reference.TableReferenceDefinitionManager;
import com.liferay.change.tracking.internal.reference.TableReferenceInfo;
import com.liferay.change.tracking.internal.resolver.ConstraintResolverContextImpl;
import com.liferay.change.tracking.internal.resolver.ConstraintResolverKey;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.resolver.ConstraintResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNode;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.WhereStep;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.sql.dsl.spi.query.Join;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class CTConflictChecker<T extends CTModel<T>> {

	public CTConflictChecker(
		ClassNameLocalService classNameLocalService,
		ServiceTrackerMap<ConstraintResolverKey, ConstraintResolver<?>>
			constraintResolverServiceTrackerMap,
		ServiceTrackerMap<String, CTDisplayRenderer<?>>
			ctDisplayRendererServiceTrackerMap,
		ServiceTrackerMap<String, CTEntryConflictHelper>
			ctEntryConflictHelperServiceTrackerMap,
		CTEntryLocalService ctEntryLocalService, CTService<T> ctService,
		CTSettingsConfiguration ctSettingsConfiguration, long modelClassNameId,
		long sourceCTCollectionId,
		TableReferenceDefinitionManager tableReferenceDefinitionManager,
		long targetCTCollectionId) {

		_classNameLocalService = classNameLocalService;
		_constraintResolverServiceTrackerMap =
			constraintResolverServiceTrackerMap;
		_ctDisplayRendererServiceTrackerMap =
			ctDisplayRendererServiceTrackerMap;
		_ctEntryConflictHelperServiceTrackerMap =
			ctEntryConflictHelperServiceTrackerMap;
		_ctEntryLocalService = ctEntryLocalService;
		_ctService = ctService;
		_ctSettingsConfiguration = ctSettingsConfiguration;
		_modelClassNameId = modelClassNameId;
		_sourceCTCollectionId = sourceCTCollectionId;
		_tableReferenceDefinitionManager = tableReferenceDefinitionManager;
		_targetCTCollectionId = targetCTCollectionId;
	}

	public void addCTEntry(CTEntry ctEntry) {
		if (ctEntry.getChangeType() ==
				CTConstants.CT_CHANGE_TYPE_MODIFICATION) {

			if (_modificationCTEntries == null) {
				_modificationCTEntries = new HashMap<>();
			}

			_modificationCTEntries.put(ctEntry.getModelClassPK(), ctEntry);
		}

		_ctEntries.add(ctEntry);
	}

	public List<ConflictInfo> check() throws PortalException {
		return _ctService.updateWithUnsafeFunction(this::_check);
	}

	private List<ConflictInfo> _check(CTPersistence<T> ctPersistence)
		throws PortalException {

		Connection connection = CurrentConnectionUtil.getConnection(
			ctPersistence.getDataSource());

		Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.PK);

		if (primaryKeyNames.size() != 1) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"{ctPersistence=", ctPersistence, ", primaryKeyNames=",
					primaryKeyNames, "}"));
		}

		Iterator<String> iterator = primaryKeyNames.iterator();

		String primaryKeyName = iterator.next();

		List<ConflictInfo> conflictInfos = new ArrayList<>();

		_checkAdditions(
			connection, ctPersistence, conflictInfos, primaryKeyName);
		_checkDeletions(
			connection, ctPersistence, conflictInfos, primaryKeyName);

		if (_modificationCTEntries != null) {
			_checkModifications(
				connection, ctPersistence, conflictInfos, primaryKeyName);
		}

		List<String[]> uniqueIndexColumnNames =
			ctPersistence.getUniqueIndexColumnNames();

		if (!uniqueIndexColumnNames.isEmpty()) {
			for (String[] columnNames : uniqueIndexColumnNames) {
				_checkConstraint(
					connection, ctPersistence, conflictInfos, primaryKeyName,
					columnNames);
			}
		}

		_checkMissingRequirements(connection, ctPersistence, conflictInfos);

		_checkCTEntries(ctPersistence, conflictInfos);

		return conflictInfos;
	}

	private void _checkAdditions(
		Connection connection, CTPersistence<T> ctPersistence,
		List<ConflictInfo> conflictInfos, String primaryKeyName) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select publication.", primaryKeyName, " from ",
					ctPersistence.getTableName(),
					" publication inner join CTEntry on CTEntry.modelClassPK ",
					"= publication.", primaryKeyName,
					" where CTEntry.ctCollectionId = ", _sourceCTCollectionId,
					" and CTEntry.modelClassNameId = ", _modelClassNameId,
					" and CTEntry.changeType = ",
					CTConstants.CT_CHANGE_TYPE_ADDITION,
					" and publication.ctCollectionId = ",
					_targetCTCollectionId));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				conflictInfos.add(
					new AdditionConflictInfo(resultSet.getLong(1)));
			}
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private void _checkConstraint(
			Connection connection, CTPersistence<T> ctPersistence,
			List<ConflictInfo> conflictInfos, String primaryKeyName,
			String[] columnNames)
		throws PortalException {

		String constraintConflictsSQL = CTRowUtil.getConstraintConflictsSQL(
			ctPersistence.getTableName(), primaryKeyName, columnNames,
			_targetCTCollectionId);

		List<Map.Entry<Long, Long>> nextPrimaryKeys =
			_getConflictingPrimaryKeys(
				connection, ctPersistence.getTableName(), primaryKeyName,
				columnNames, constraintConflictsSQL);

		if (nextPrimaryKeys.isEmpty()) {
			return;
		}

		ConstraintResolver<T> constraintResolver =
			(ConstraintResolver<T>)
				_constraintResolverServiceTrackerMap.getService(
					new ConstraintResolverKey(
						ctPersistence.getModelClass(), columnNames));

		if (constraintResolver == null) {
			StringBundler sb = new StringBundler(2 * columnNames.length);

			for (String columnName : columnNames) {
				sb.append(columnName);
				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);

			String columnNamesString = sb.toString();

			for (Map.Entry<Long, Long> currentPrimaryKeys : nextPrimaryKeys) {
				conflictInfos.add(
					new DefaultConstraintConflictInfo(
						currentPrimaryKeys.getKey(),
						currentPrimaryKeys.getValue(), columnNamesString));
			}

			return;
		}

		ConstraintResolverContextImpl<T> constraintResolverContextImpl =
			new ConstraintResolverContextImpl<>(
				_ctService, _sourceCTCollectionId, _targetCTCollectionId);

		Set<Map.Entry<Long, Long>> attemptedPrimaryKeys = new HashSet<>();
		Set<Map.Entry<Long, Long>> resolvedPrimaryKeys = new HashSet<>(
			nextPrimaryKeys);

		while (!nextPrimaryKeys.isEmpty()) {
			Map.Entry<Long, Long> currentPrimaryKeys = nextPrimaryKeys.get(0);

			constraintResolverContextImpl.setPrimaryKeys(
				currentPrimaryKeys.getKey(), currentPrimaryKeys.getValue());

			constraintResolver.resolveConflict(constraintResolverContextImpl);

			Session session = ctPersistence.getCurrentSession();

			session.flush();

			session.clear();

			attemptedPrimaryKeys.add(currentPrimaryKeys);

			nextPrimaryKeys = _getConflictingPrimaryKeys(
				connection, ctPersistence.getTableName(), primaryKeyName,
				columnNames, constraintConflictsSQL);

			resolvedPrimaryKeys.addAll(nextPrimaryKeys);

			nextPrimaryKeys.removeAll(attemptedPrimaryKeys);
		}

		List<Map.Entry<Long, Long>> unresolvedPrimaryKeys =
			_getConflictingPrimaryKeys(
				connection, ctPersistence.getTableName(), primaryKeyName,
				columnNames, constraintConflictsSQL);

		resolvedPrimaryKeys.removeAll(unresolvedPrimaryKeys);

		for (Map.Entry<Long, Long> currentPrimaryKeys : resolvedPrimaryKeys) {
			conflictInfos.add(
				new ConstraintResolverConflictInfo(
					constraintResolver, true, currentPrimaryKeys.getKey(),
					currentPrimaryKeys.getValue()));
		}

		if (unresolvedPrimaryKeys.isEmpty()) {
			return;
		}

		for (Map.Entry<Long, Long> currentPrimaryKeys : unresolvedPrimaryKeys) {
			conflictInfos.add(
				new ConstraintResolverConflictInfo(
					constraintResolver, false, currentPrimaryKeys.getKey(),
					currentPrimaryKeys.getValue()));
		}
	}

	private void _checkCTEntries(
		CTPersistence<T> ctPersistence, List<ConflictInfo> conflictInfos) {

		Class<?> clazz = ctPersistence.getModelClass();

		CTEntryConflictHelper ctEntryConflictHelper =
			_ctEntryConflictHelperServiceTrackerMap.getService(clazz.getName());

		if (ctEntryConflictHelper == null) {
			return;
		}

		for (CTEntry ctEntry : _ctEntries) {
			String missingRequirementTypeName =
				ctEntryConflictHelper.getMissingRequirementTypeName(
					ctEntry, _targetCTCollectionId);

			if (Validator.isNotNull(missingRequirementTypeName)) {
				conflictInfos.add(
					new MissingRequirementConflictInfo(
						ctEntry.getModelClassPK(), missingRequirementTypeName));
			}

			if (ctEntryConflictHelper.hasModificationConflict(
					ctEntry, _targetCTCollectionId)) {

				conflictInfos.add(
					new ModificationConflictInfo(
						ctEntry.getModelClassPK(), false));
			}

			if (ctEntryConflictHelper.hasDeletionModificationConflict(
					ctEntry, _targetCTCollectionId)) {

				conflictInfos.add(
					new DeletionModificationConflictInfo(
						ctEntry.getModelClassPK()));
			}
		}
	}

	private void _checkDeletions(
		Connection connection, CTPersistence<T> ctPersistence,
		List<ConflictInfo> conflictInfos, String primaryKeyName) {

		if (_ctSettingsConfiguration.
				modificationDeletionConflictCheckEnabled()) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select publication.", primaryKeyName, " from ",
							ctPersistence.getTableName(),
							" publication inner join CTEntry on ",
							"CTEntry.modelClassPK = publication.",
							primaryKeyName, " where CTEntry.ctCollectionId = ",
							_sourceCTCollectionId,
							" and CTEntry.modelClassNameId = ",
							_modelClassNameId, " and CTEntry.changeType = ",
							CTConstants.CT_CHANGE_TYPE_DELETION,
							" and (publication.ctCollectionId = ",
							_targetCTCollectionId,
							" or publication.ctCollectionId = ",
							CTConstants.CT_COLLECTION_ID_PRODUCTION,
							") and CTEntry.modelMvccVersion != ",
							"publication.mvccVersion"));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					conflictInfos.add(
						new ModificationDeletionConflictInfo(
							resultSet.getLong(1), false));
				}
			}
			catch (SQLException sqlException) {
				throw new ORMException(sqlException);
			}
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct ctEntry1.modelClassPK from CTEntry ",
					"ctEntry1 inner join CTCollection on ",
					"ctEntry1.ctCollectionId = CTCollection.ctCollectionId ",
					"and CTCollection.status = ",
					WorkflowConstants.STATUS_DRAFT, " inner join (select ",
					"CTCollection.ctCollectionId, modelClassNameId, ",
					"modelClassPK, changeType from CTEntry inner join ",
					"CTCollection on CTEntry.ctCollectionId = ",
					"CTCollection.ctCollectionId and CTCollection.status = ",
					WorkflowConstants.STATUS_DRAFT, ") ctEntry2 on ",
					"ctEntry1.modelClassNameId = ctEntry2.modelClassNameId ",
					"and ctEntry1.modelClassPK = ctEntry2.modelClassPK where ",
					"ctEntry1.modelClassNameId = ", _modelClassNameId, " and ",
					"ctEntry1.changeType = ",
					CTConstants.CT_CHANGE_TYPE_DELETION,
					" and ctEntry1.ctCollectionId = ", _sourceCTCollectionId,
					" and ctEntry2.changeType = ",
					CTConstants.CT_CHANGE_TYPE_MODIFICATION,
					" and ctEntry2.ctCollectionId != ", _sourceCTCollectionId));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				conflictInfos.add(
					new ModificationDeletionConflictInfo(
						resultSet.getLong(1), true));
			}
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private void _checkMissingRequirements(
			Connection connection, CTPersistence<T> ctPersistence,
			List<ConflictInfo> conflictInfos)
		throws PortalException {

		if (_ctEntries.isEmpty()) {
			return;
		}

		DSLQuery ctEntryDSLQuery = DSLQueryFactoryUtil.select(
			CTEntryTable.INSTANCE.modelClassPK
		).from(
			CTEntryTable.INSTANCE
		).where(
			CTEntryTable.INSTANCE.ctCollectionId.eq(
				_sourceCTCollectionId
			).and(
				CTEntryTable.INSTANCE.modelClassNameId.eq(_modelClassNameId)
			).and(
				CTEntryTable.INSTANCE.changeType.eq(
					CTConstants.CT_CHANGE_TYPE_ADDITION)
			)
		);

		Map<Long, TableReferenceInfo<?>> combinedTableReferenceInfos =
			_tableReferenceDefinitionManager.getCombinedTableReferenceInfos();

		TableReferenceInfo<?> tableReferenceInfo =
			combinedTableReferenceInfos.get(_modelClassNameId);

		if (tableReferenceInfo == null) {
			throw new IllegalArgumentException(
				"No table reference definition for " +
					ctPersistence.getModelClass());
		}

		DSLQuery dslQuery = null;

		Map<Table<?>, List<TableJoinHolder>> parentTableJoinHoldersMap =
			tableReferenceInfo.getParentTableJoinHoldersMap();

		for (List<TableJoinHolder> tableJoinHolders :
				parentTableJoinHoldersMap.values()) {

			for (TableJoinHolder tableJoinHolder : tableJoinHolders) {
				if (tableJoinHolder.isReversed()) {
					continue;
				}

				DSLQuery nextDSLQuery = _getMissingRequirementsDSLQuery(
					ctEntryDSLQuery, tableJoinHolder);

				if (dslQuery == null) {
					dslQuery = nextDSLQuery;
				}
				else {
					dslQuery = dslQuery.union(nextDSLQuery);
				}
			}
		}

		if (dslQuery != null) {
			try (PreparedStatement preparedStatement = _getPreparedStatement(
					connection, dslQuery);
				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					long modelClassPK = resultSet.getLong(1);

					String tableName = resultSet.getString(2);

					ClassName className = _classNameLocalService.getClassName(
						_tableReferenceDefinitionManager.getClassNameId(
							tableName));

					String classNameValue = className.getValue();

					conflictInfos.add(
						new MissingRequirementConflictInfo(
							classNameValue, modelClassPK,
							_ctDisplayRendererServiceTrackerMap.getService(
								classNameValue)));
				}
			}
			catch (SQLException sqlException) {
				throw new ORMException(
					"Unable to execute query: " + dslQuery, sqlException);
			}
		}
	}

	private void _checkModifications(
		Connection connection, CTPersistence<T> ctPersistence,
		List<ConflictInfo> conflictInfos, String primaryKeyName) {

		List<Long> resolvedPrimaryKeys = _getModifiedPrimaryKeys(
			connection, ctPersistence, primaryKeyName, true);

		for (Long resolvedPrimaryKey : resolvedPrimaryKeys) {
			conflictInfos.add(
				new ModificationConflictInfo(resolvedPrimaryKey, true));
		}

		_resolveModificationConflicts(
			connection, ctPersistence, primaryKeyName, resolvedPrimaryKeys);

		List<Long> unresolvedPrimaryKeys = _getModifiedPrimaryKeys(
			connection, ctPersistence, primaryKeyName, false);

		for (Long unresolvedPrimaryKey : unresolvedPrimaryKeys) {
			conflictInfos.add(
				new ModificationConflictInfo(unresolvedPrimaryKey, false));
		}

		_updateModelMvccVersion(
			connection, primaryKeyName, ctPersistence.getTableName(),
			unresolvedPrimaryKeys);

		List<Long> deletionModificationPKs = _getDeletionModificationPKs(
			connection, ctPersistence, primaryKeyName);

		for (long deletionModificationPK : deletionModificationPKs) {
			conflictInfos.add(
				new DeletionModificationConflictInfo(deletionModificationPK));
		}
	}

	private void _copyModificationConflictCTRow(
		Connection connection, CTPersistence<?> ctPersistence,
		String primaryKeyName, long primaryKey, long tempCTCollectionId) {

		StringBundler sb = new StringBundler("select ");

		Set<String> ignoredColumnNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.IGNORE);
		Set<String> maxColumnNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.MAX);
		Set<String> minColumnNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.MIN);

		Map<String, Integer> tableColumnsMap =
			ctPersistence.getTableColumnsMap();

		for (String name : tableColumnsMap.keySet()) {
			if (name.equals("ctCollectionId")) {
				sb.append(_sourceCTCollectionId);
				sb.append(" as ");
			}
			else if (name.equals("mvccVersion")) {
				sb.append("(publication.mvccVersion + 1) ");
			}
			else if (ignoredColumnNames.contains(name)) {
				sb.append("production.");
			}
			else if (maxColumnNames.contains(name) ||
					 minColumnNames.contains(name)) {

				sb.append("composite.");
			}
			else {
				sb.append("publication.");
			}

			sb.append(name);
			sb.append(", ");
		}

		sb.setStringAt(" from ", sb.index() - 1);

		sb.append(ctPersistence.getTableName());
		sb.append(" production inner join ");
		sb.append(ctPersistence.getTableName());
		sb.append(" publication on production.");
		sb.append(primaryKeyName);
		sb.append(" = publication.");
		sb.append(primaryKeyName);

		if (!maxColumnNames.isEmpty() || !minColumnNames.isEmpty()) {
			sb.append(" inner join (select ");
			sb.append(primaryKeyName);

			for (String maxColumnName : maxColumnNames) {
				sb.append(", max(");
				sb.append(maxColumnName);
				sb.append(") ");
				sb.append(maxColumnName);
			}

			for (String minColumnName : minColumnNames) {
				sb.append(", min(");
				sb.append(minColumnName);
				sb.append(") ");
				sb.append(minColumnName);
			}

			sb.append(" from ");
			sb.append(ctPersistence.getTableName());
			sb.append(" where ctCollectionId in (");
			sb.append(_targetCTCollectionId);
			sb.append(", ");
			sb.append(tempCTCollectionId);
			sb.append(") group by ");
			sb.append(primaryKeyName);
			sb.append(") composite on composite.");
			sb.append(primaryKeyName);
			sb.append(" = production.");
			sb.append(primaryKeyName);
		}

		sb.append(" where publication.ctCollectionId = ");
		sb.append(tempCTCollectionId);
		sb.append(" and production.ctCollectionId = ");
		sb.append(_targetCTCollectionId);
		sb.append(" and publication.");
		sb.append(primaryKeyName);
		sb.append(" = ");
		sb.append(primaryKey);
		sb.append(" and production.");
		sb.append(primaryKeyName);
		sb.append(" = ");
		sb.append(primaryKey);

		try {
			CTRowUtil.copyCTRows(ctPersistence, connection, sb.toString());
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private List<Map.Entry<Long, Long>> _getConflictingPrimaryKeys(
		Connection connection, String tableName, String primaryKeyName,
		String[] columnNames, String constraintConflictsSQL) {

		Set<Long> verifyPrimaryKeys = new HashSet<>();
		Set<Long> ignorablePrimaryKeys = new HashSet<>();

		for (CTEntry ctEntry : _ctEntries) {
			if (ctEntry.getChangeType() !=
					CTConstants.CT_CHANGE_TYPE_ADDITION) {

				ignorablePrimaryKeys.add(ctEntry.getModelClassPK());
			}
			else {
				verifyPrimaryKeys.add(ctEntry.getModelClassPK());
			}
		}

		if (verifyPrimaryKeys.isEmpty()) {
			return Collections.emptyList();
		}

		String constraintEntriesSQL = CTRowUtil.getConstraintEntitiesSQL(
			tableName, primaryKeyName, columnNames, _sourceCTCollectionId,
			verifyPrimaryKeys);

		List<Map.Entry<Long, Long>> primaryKeys = new ArrayList<>();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				constraintEntriesSQL);
			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				long sourcePK = resultSet1.getLong(1);

				try (PreparedStatement preparedStatement2 =
						connection.prepareStatement(constraintConflictsSQL)) {

					preparedStatement2.setObject(1, sourcePK);

					for (int i = 2; i < (columnNames.length + 2); i++) {
						preparedStatement2.setObject(
							i, resultSet1.getObject(i));
					}

					try (ResultSet resultSet2 =
							preparedStatement2.executeQuery()) {

						while (resultSet2.next()) {
							long targetPK = resultSet2.getLong(1);

							if (ignorablePrimaryKeys.contains(targetPK)) {
								continue;
							}

							primaryKeys.add(
								new AbstractMap.SimpleImmutableEntry<>(
									sourcePK, targetPK));
						}
					}
				}
				catch (SQLException sqlException) {
					throw new ORMException(sqlException);
				}
			}
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}

		return primaryKeys;
	}

	private List<Long> _getDeletionModificationPKs(
		Connection connection, CTPersistence<T> ctPersistence,
		String primaryKeyName) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CTEntry.modelClassPK from CTEntry left join ",
					ctPersistence.getTableName(), " publication on ",
					"publication.", primaryKeyName, " = CTEntry.modelClassPK ",
					"and (publication.ctCollectionId = ", _targetCTCollectionId,
					" or publication.ctCollectionId = ",
					CTConstants.CT_COLLECTION_ID_PRODUCTION,
					") where CTEntry.ctCollectionId = ", _sourceCTCollectionId,
					" and CTEntry.modelClassNameId = ", _modelClassNameId,
					" and CTEntry.changeType = ",
					CTConstants.CT_CHANGE_TYPE_MODIFICATION, " and ",
					"publication.", primaryKeyName, " is null"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<Long> primaryKeys = new ArrayList<>();

			while (resultSet.next()) {
				primaryKeys.add(resultSet.getLong(1));
			}

			return primaryKeys;
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private DSLQuery _getMissingRequirementsDSLQuery(
		DSLQuery ctEntryDSLQuery, TableJoinHolder tableJoinHolder) {

		WhereStep whereStep = tableJoinHolder.getMissingRequirementWhereStep();

		Deque<Join> joins = new LinkedList<>();

		ASTNode astNode = whereStep;

		while (astNode instanceof Join) {
			Join join = (Join)astNode;

			joins.push(join);

			astNode = join.getChild();
		}

		Join join = null;

		JoinStep joinStep = (JoinStep)astNode;

		while ((join = joins.poll()) != null) {
			Predicate predicate = join.getOnPredicate();

			Table<?> table = join.getTable();

			predicate = predicate.and(
				() -> {
					Column<?, Long> ctCollectionIdColumn = table.getColumn(
						"ctCollectionId", Long.class);

					if (ctCollectionIdColumn == null) {
						return null;
					}

					if (_targetCTCollectionId ==
							CTConstants.CT_COLLECTION_ID_PRODUCTION) {

						return ctCollectionIdColumn.in(
							new Long[] {
								_sourceCTCollectionId, _targetCTCollectionId
							});
					}

					return ctCollectionIdColumn.in(
						new Long[] {
							_sourceCTCollectionId, _targetCTCollectionId,
							CTConstants.CT_COLLECTION_ID_PRODUCTION
						});
				});

			joinStep = joinStep.leftJoinOn(table, predicate);
		}

		Column<?, Long> childPKColumn = tableJoinHolder.getChildPKColumn();

		Table<?> childTable = childPKColumn.getTable();

		Column<?, Long> ctCollectionIdColumn = childTable.getColumn(
			"ctCollectionId", Long.class);

		Predicate missingRequirementWherePredicate =
			tableJoinHolder.getMissingRequirementWherePredicate();

		return joinStep.where(
			missingRequirementWherePredicate.and(
				childPKColumn.in(
					ctEntryDSLQuery
				).and(
					ctCollectionIdColumn.eq(_sourceCTCollectionId)
				)));
	}

	private List<Long> _getModifiedPrimaryKeys(
		Connection connection, CTPersistence<T> ctPersistence,
		String primaryKeyName, boolean resolved) {

		Set<String> strictColumnNames = ctPersistence.getCTColumnNames(
			CTColumnResolutionType.STRICT);

		StringBundler sb = new StringBundler();

		sb.append("select publication.");
		sb.append(primaryKeyName);
		sb.append(" from ");
		sb.append(ctPersistence.getTableName());
		sb.append(" publication inner join ");
		sb.append(ctPersistence.getTableName());
		sb.append(" production on publication.");
		sb.append(primaryKeyName);
		sb.append(" = production.");
		sb.append(primaryKeyName);
		sb.append(" and publication.ctCollectionId = ");
		sb.append(_sourceCTCollectionId);
		sb.append(" and production.ctCollectionId = ");
		sb.append(_targetCTCollectionId);
		sb.append(" inner join CTEntry ctEntry on ctEntry.ctCollectionId = ");
		sb.append(_sourceCTCollectionId);
		sb.append(" and ctEntry.modelClassNameId = ");
		sb.append(_modelClassNameId);
		sb.append(" and ctEntry.modelClassPK = production.");
		sb.append(primaryKeyName);
		sb.append(" and ctEntry.changeType = ");
		sb.append(CTConstants.CT_CHANGE_TYPE_MODIFICATION);
		sb.append(" and ctEntry.modelMvccVersion != production.mvccVersion");

		Map<String, Integer> columnsMap = new HashMap<>(
			ctPersistence.getTableColumnsMap());

		Set<String> columnNames = columnsMap.keySet();

		columnNames.retainAll(strictColumnNames);

		Collection<Integer> columnTypes = columnsMap.values();

		String andOr = " or ";
		String comparison = " != ";

		if (resolved) {
			andOr = " and ";
			comparison = " = ";
		}

		if (!columnTypes.contains(Types.BLOB)) {
			sb.append(" where ");

			for (Map.Entry<String, Integer> entry : columnsMap.entrySet()) {
				String conflictColumnName = entry.getKey();

				sb.append("((");

				if (entry.getValue() == Types.CLOB) {
					sb.append("CAST_CLOB_TEXT(publication.");
					sb.append(conflictColumnName);
					sb.append(")");
					sb.append(comparison);
					sb.append("CAST_CLOB_TEXT(production.");
					sb.append(conflictColumnName);
					sb.append(")");
				}
				else {
					sb.append("publication.");
					sb.append(conflictColumnName);
					sb.append(comparison);
					sb.append("production.");
					sb.append(conflictColumnName);
				}

				sb.append(") or (publication.");
				sb.append(conflictColumnName);
				sb.append(" is null and production.");
				sb.append(conflictColumnName);

				if (!resolved) {
					sb.append(" is not null) or (publication.");
					sb.append(conflictColumnName);
					sb.append(" is not null and production.");
					sb.append(conflictColumnName);
				}

				sb.append(" is null))");

				sb.append(andOr);
			}

			sb.setIndex(sb.index() - 1);
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(sb.toString()));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<Long> primaryKeys = new ArrayList<>();

			while (resultSet.next()) {
				long primaryKey = resultSet.getLong(1);

				primaryKeys.add(primaryKey);
			}

			return primaryKeys;
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
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

	private void _resolveModificationConflicts(
		Connection connection, CTPersistence<T> ctPersistence,
		String primaryKeyName, List<Long> resolvedPrimaryKeys) {

		if (resolvedPrimaryKeys.isEmpty()) {
			return;
		}

		long tempCTCollectionId = -_sourceCTCollectionId;

		StringBundler sb = new StringBundler(
			(2 * resolvedPrimaryKeys.size()) + 9);

		sb.append("update ");
		sb.append(ctPersistence.getTableName());
		sb.append(" set ctCollectionId = ");
		sb.append(tempCTCollectionId);
		sb.append(" where ctCollectionId = ");
		sb.append(_sourceCTCollectionId);
		sb.append(" and ");
		sb.append(primaryKeyName);
		sb.append(" = ?");

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, sb.toString())) {

			for (Long primaryKey : resolvedPrimaryKeys) {
				preparedStatement.setLong(1, primaryKey);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}

		for (long primaryKey : resolvedPrimaryKeys) {
			_copyModificationConflictCTRow(
				connection, ctPersistence, primaryKeyName, primaryKey,
				tempCTCollectionId);
		}

		ctPersistence.clearCache(new HashSet<>(resolvedPrimaryKeys));

		sb = new StringBundler(7);

		sb.append("delete from ");
		sb.append(ctPersistence.getTableName());
		sb.append(" where ctCollectionId = ");
		sb.append(tempCTCollectionId);
		sb.append(" and ");
		sb.append(primaryKeyName);
		sb.append(" = ?");

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, sb.toString())) {

			for (Long primaryKey : resolvedPrimaryKeys) {
				preparedStatement.setLong(1, primaryKey);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private void _updateModelMvccVersion(
		Connection connection, String primaryKeyName, String tableName,
		List<Long> unresolvedPrimaryKeys) {

		StringBundler sb = new StringBundler(
			(2 * unresolvedPrimaryKeys.size()) + 18);

		sb.append("select publication.");
		sb.append(primaryKeyName);
		sb.append(", publication.mvccVersion from ");
		sb.append(tableName);
		sb.append(" publication inner join CTEntry on publication.");
		sb.append(primaryKeyName);
		sb.append(" = CTEntry.modelClassPK and CTEntry.changeType = ");
		sb.append(CTConstants.CT_CHANGE_TYPE_MODIFICATION);
		sb.append(" and CTEntry.ctCollectionId = ");
		sb.append(_sourceCTCollectionId);
		sb.append(" and CTEntry.modelClassNameId = ");
		sb.append(_modelClassNameId);
		sb.append(" and publication.mvccVersion != CTEntry.modelMvccVersion");
		sb.append(" where publication.ctCollectionId = ");
		sb.append(_targetCTCollectionId);

		if (!unresolvedPrimaryKeys.isEmpty()) {
			sb.append(" and publication.");
			sb.append(primaryKeyName);
			sb.append(" not in (");

			for (Long unresolvedPrimaryKey : unresolvedPrimaryKeys) {
				sb.append(unresolvedPrimaryKey);
				sb.append(", ");
			}

			sb.setStringAt(")", sb.index() - 1);
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString());
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long pk = resultSet.getLong(1);

				CTEntry ctEntry = _modificationCTEntries.get(pk);

				if (ctEntry != null) {
					_ctEntryLocalService.updateModelMvccVersion(
						ctEntry.getCtEntryId(), resultSet.getLong(2));
				}
			}
		}
		catch (SQLException sqlException) {
			throw new ORMException(sqlException);
		}
	}

	private final ClassNameLocalService _classNameLocalService;
	private final ServiceTrackerMap
		<ConstraintResolverKey, ConstraintResolver<?>>
			_constraintResolverServiceTrackerMap;
	private final ServiceTrackerMap<String, CTDisplayRenderer<?>>
		_ctDisplayRendererServiceTrackerMap;
	private final Set<CTEntry> _ctEntries = new HashSet<>();
	private final ServiceTrackerMap<String, CTEntryConflictHelper>
		_ctEntryConflictHelperServiceTrackerMap;
	private final CTEntryLocalService _ctEntryLocalService;
	private final CTService<T> _ctService;
	private final CTSettingsConfiguration _ctSettingsConfiguration;
	private final long _modelClassNameId;
	private Map<Serializable, CTEntry> _modificationCTEntries;
	private final long _sourceCTCollectionId;
	private final TableReferenceDefinitionManager
		_tableReferenceDefinitionManager;
	private final long _targetCTCollectionId;

}