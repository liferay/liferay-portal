/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.ast.ASTNode;
import com.liferay.petra.sql.dsl.ast.ASTNodeListener;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.query.WhereStep;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.DefaultPredicate;
import com.liferay.petra.sql.dsl.spi.expression.Operand;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.sql.dsl.spi.query.From;
import com.liferay.petra.sql.dsl.spi.query.Join;
import com.liferay.petra.sql.dsl.spi.query.JoinType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Preston Crary
 */
public class TableJoinHolderFactory {

	public static <T extends Table<T>> TableJoinHolder create(
		Function<FromStep, JoinStep> joinFunction, boolean parent,
		Column<T, Long> primaryKeyColumn,
		TableReferenceDefinition<T> tableReferenceDefinition) {

		JoinStep joinStep = joinFunction.apply(_validationFromStep);

		if (!(joinStep instanceof Join)) {
			throw new IllegalArgumentException(
				StringBundler.concat("Missing join in \"", joinStep, "\""));
		}

		JoinStepASTNodeListener<T> joinStepASTNodeListener =
			new JoinStepASTNodeListener<>(tableReferenceDefinition.getTable());

		joinStep.toSQL(_emptyStringConsumer, joinStepASTNodeListener);

		if (joinStepASTNodeListener._fromTable == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Join function must use provided from step for join step ",
					"\"", joinStep, "\""));
		}

		if (!joinStepASTNodeListener._hasRequiredTable) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Required table \"", tableReferenceDefinition.getTable(),
					"\" is unused in join step \"", joinStep, "\""));
		}

		if (joinStepASTNodeListener._invalidJoin != null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Invalid join for join step \"", joinStep,
					"\", ensure table alias is used for self joins"));
		}

		if (joinStepASTNodeListener._invalidJoinOrder) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"First join must be on table \"",
					tableReferenceDefinition.getTable(), "\" for join step \"",
					joinStep, "\""));
		}

		if (joinStepASTNodeListener._invalidJoinType != null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Invalid join type \"",
					joinStepASTNodeListener._invalidJoinType,
					"\" for join step \"", joinStep, "\""));
		}

		if (joinStepASTNodeListener._invalidOperand != null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Invalid predicate operand \"",
					joinStepASTNodeListener._invalidOperand,
					"\" for join step \"", joinStep, "\""));
		}

		if (!joinStepASTNodeListener._tables.containsAll(
				joinStepASTNodeListener._columnTables)) {

			List<Table<?>> columnTables = new ArrayList<>(
				joinStepASTNodeListener._columnTables);

			Comparator<Table<?>> comparator = Comparator.comparing(
				Table::getName);

			columnTables.sort(comparator);

			List<Table<?>> joinTables = new ArrayList<>(
				joinStepASTNodeListener._tables);

			joinTables.sort(comparator);

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Predicate column tables ", columnTables,
					" do not match join tables ", joinTables,
					" for join step \"", joinStep, "\""));
		}

		Column<?, Long> fromPKColumn = TableUtil.getPrimaryKeyColumn(
			joinStepASTNodeListener._fromTable);

		if (fromPKColumn == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"No long type primary key column found for table \"",
					joinStepASTNodeListener._fromTable, "\" for join step \"",
					joinStep, "\""));
		}

		WhereStep missingRequirementWhereStep = null;
		Predicate missingRequirementWherePredicate = null;

		if (parent) {
			missingRequirementWherePredicate = fromPKColumn.isNull();

			List<BridgePredicate> bridgePredicates = _getBridgePredicates(
				joinStep);
			Set<Column<?, ?>> childColumns = _getChildColumns(
				primaryKeyColumn.getTable(), joinStep);

			Iterator<BridgePredicate> iterator = bridgePredicates.iterator();

			while (iterator.hasNext()) {
				BridgePredicate bridgePredicate = iterator.next();

				if (bridgePredicate.hasOnlyTable(primaryKeyColumn.getTable())) {
					missingRequirementWherePredicate =
						missingRequirementWherePredicate.and(
							bridgePredicate._predicate);

					iterator.remove();
				}
			}

			missingRequirementWhereStep = _getMissingRequirementWhereStep(
				primaryKeyColumn, fromPKColumn, bridgePredicates);

			for (Column<?, ?> column : childColumns) {
				Class<?> clazz = column.getJavaType();

				missingRequirementWherePredicate =
					missingRequirementWherePredicate.and(
						column.isNotNull()
					).and(
						() -> {
							if (clazz == String.class) {
								Column<?, String> stringColumn =
									(Column<?, String>)column;

								return stringColumn.neq(StringPool.BLANK);
							}

							if (clazz != Long.class) {
								return null;
							}

							Column<?, Long> longColumn =
								(Column<?, Long>)column;

							return longColumn.neq(0L);
						}
					);
			}
		}

		return new TableJoinHolder(
			primaryKeyColumn, joinFunction, missingRequirementWherePredicate,
			missingRequirementWhereStep, fromPKColumn);
	}

	private static List<BridgePredicate> _getBridgePredicates(
		JoinStep joinStep) {

		List<BridgePredicate> bridgePredicates = new LinkedList<>();

		Queue<DefaultPredicate> defaultPredicateQueue = new LinkedList<>();
		Queue<Expression<?>> expressionQueue = new LinkedList<>();

		ASTNode astNode = joinStep;

		while (astNode instanceof Join) {
			Join join = (Join)astNode;

			defaultPredicateQueue.add((DefaultPredicate)join.getOnPredicate());

			DefaultPredicate defaultPredicate = null;

			while ((defaultPredicate = defaultPredicateQueue.poll()) != null) {
				Expression<?> leftExpression =
					defaultPredicate.getLeftExpression();
				Expression<?> rightExpression =
					defaultPredicate.getRightExpression();

				if (defaultPredicate.getOperand() == Operand.AND) {
					defaultPredicateQueue.add((DefaultPredicate)leftExpression);
					defaultPredicateQueue.add(
						(DefaultPredicate)rightExpression);
				}
				else {
					Set<Table<?>> tables = Collections.newSetFromMap(
						new IdentityHashMap<>());

					expressionQueue.add(leftExpression);
					expressionQueue.add(rightExpression);

					Expression<?> expression = null;

					while ((expression = expressionQueue.poll()) != null) {
						if (expression instanceof Column) {
							Column<?, ?> column = (Column<?, ?>)expression;

							tables.add(column.getTable());
						}
						else if (expression instanceof DSLFunction) {
							DSLFunction<?> dslFunction =
								(DSLFunction<?>)expression;

							Collections.addAll(
								expressionQueue, dslFunction.getExpressions());
						}
					}

					bridgePredicates.add(
						new BridgePredicate(tables, defaultPredicate));
				}
			}

			astNode = join.getChild();
		}

		return bridgePredicates;
	}

	private static Set<Column<?, ?>> _getChildColumns(
		Table<?> table, JoinStep joinStep) {

		Set<Column<?, ?>> childColumns = new HashSet<>();

		joinStep.toSQL(
			_emptyStringConsumer,
			astNode -> {
				if (astNode instanceof DefaultPredicate) {
					DefaultPredicate defaultPredicate =
						(DefaultPredicate)astNode;

					if (defaultPredicate.getOperand() == Operand.EQUAL) {
						Expression<?> leftExpression =
							defaultPredicate.getLeftExpression();
						Expression<?> rightExpression =
							defaultPredicate.getRightExpression();

						if ((leftExpression instanceof Column) &&
							!(rightExpression instanceof Scalar)) {

							Column<?, ?> column = (Column<?, ?>)leftExpression;

							if (column.getTable() == table) {
								childColumns.add(column);
							}
						}

						if (!(leftExpression instanceof Scalar) &&
							(rightExpression instanceof Column)) {

							Column<?, ?> column = (Column<?, ?>)rightExpression;

							if (column.getTable() == table) {
								childColumns.add(column);
							}
						}
					}
				}
			});

		return childColumns;
	}

	private static WhereStep _getMissingRequirementWhereStep(
		Column<?, Long> primaryKeyColumn, Column<?, Long> fromPKColumn,
		List<BridgePredicate> bridgePredicates) {

		Table<?> parentTable = fromPKColumn.getTable();

		JoinStep joinStep = DSLQueryFactoryUtil.select(
			primaryKeyColumn, new Scalar<>(parentTable.getTableName())
		).from(
			primaryKeyColumn.getTable()
		);

		Set<Table<?>> tables = Collections.newSetFromMap(
			new IdentityHashMap<>());

		tables.add(primaryKeyColumn.getTable());

		Set<BridgePredicate> resolvedBridgePredicates = new HashSet<>();

		int previousSize = -1;

		while (resolvedBridgePredicates.size() != previousSize) {
			previousSize = resolvedBridgePredicates.size();

			for (BridgePredicate bridgePredicate : bridgePredicates) {
				if (resolvedBridgePredicates.contains(bridgePredicate)) {
					continue;
				}

				Table<?> table = null;

				for (Table<?> predicateTable : bridgePredicate._tables) {
					if (!tables.contains(predicateTable)) {
						if (table != null) {
							table = null;

							break;
						}

						table = predicateTable;
					}
				}

				if (table == null) {
					continue;
				}

				tables.add(table);

				Predicate predicate = null;

				for (BridgePredicate currentBridgePredicate :
						bridgePredicates) {

					if (resolvedBridgePredicates.contains(
							currentBridgePredicate)) {

						continue;
					}

					if (tables.containsAll(currentBridgePredicate._tables)) {
						resolvedBridgePredicates.add(currentBridgePredicate);

						if (predicate == null) {
							predicate = currentBridgePredicate._predicate;
						}
						else {
							predicate = predicate.and(
								currentBridgePredicate._predicate);
						}
					}
				}

				joinStep = joinStep.leftJoinOn(table, predicate);
			}
		}

		if (!resolvedBridgePredicates.containsAll(bridgePredicates)) {
			StringBundler sb = new StringBundler();

			sb.append("Unable to apply predicates [");

			for (BridgePredicate bridgePredicate : bridgePredicates) {
				if (!resolvedBridgePredicates.contains(bridgePredicate)) {
					sb.append(bridgePredicate._predicate);
					sb.append(", ");
				}
			}

			sb.setStringAt("] to ", sb.index() - 1);

			sb.append(joinStep);

			throw new IllegalArgumentException(sb.toString());
		}

		return joinStep;
	}

	private static final Consumer<String> _emptyStringConsumer = string -> {
	};
	private static final Set<Operand> _validOperands = new HashSet<>(
		Arrays.asList(Operand.AND, Operand.EQUAL, Operand.LIKE));
	private static final FromStep _validationFromStep =
		new ValidationFromStep();

	private static class BridgePredicate {

		public boolean hasOnlyTable(Table<?> childTable) {
			for (Table<?> table : _tables) {
				if (table != childTable) {
					return false;
				}
			}

			return true;
		}

		private BridgePredicate(Set<Table<?>> tables, Predicate predicate) {
			_tables = tables;
			_predicate = predicate;
		}

		private final Predicate _predicate;
		private final Set<Table<?>> _tables;

	}

	private static class JoinStepASTNodeListener<T extends Table<T>>
		implements ASTNodeListener {

		@Override
		public void process(ASTNode astNode) {
			if (astNode instanceof Column) {
				Column<?, ?> column = (Column<?, ?>)astNode;

				_columnTables.add(column.getTable());

				if (!_hasRequiredTable &&
					Objects.equals(_table, column.getTable())) {

					_hasRequiredTable = true;
				}
			}
			else if (astNode instanceof DefaultPredicate) {
				DefaultPredicate defaultPredicate = (DefaultPredicate)astNode;

				Operand operand = defaultPredicate.getOperand();

				if (!_validOperands.contains(operand) &&
					(_invalidOperand == null)) {

					_invalidOperand = operand;
				}
			}
			else if (astNode instanceof From) {
				From from = (From)astNode;

				if (from.getChild() == _validationFromStep) {
					_fromTable = from.getTable();

					_tables.add(from.getTable());
				}
			}
			else if (astNode instanceof Join) {
				Join join = (Join)astNode;

				JoinType joinType = join.getJoinType();

				if (joinType != JoinType.INNER) {
					_invalidJoinType = joinType;
				}

				Table<?> table = join.getTable();

				if ((_tables.size() == 1) && (table != _table)) {
					_invalidJoinOrder = true;
				}

				if (table.equals(_fromTable) &&
					Objects.equals(_fromTable.getName(), table.getName())) {

					_invalidJoin = join;
				}

				_tables.add(table);
			}
		}

		private JoinStepASTNodeListener(T table) {
			_table = table;
		}

		private final Set<Table<?>> _columnTables = Collections.newSetFromMap(
			new IdentityHashMap<>());
		private Table<?> _fromTable;
		private boolean _hasRequiredTable;
		private Join _invalidJoin;
		private boolean _invalidJoinOrder;
		private JoinType _invalidJoinType;
		private Operand _invalidOperand;
		private final T _table;
		private final Set<Table<?>> _tables = Collections.newSetFromMap(
			new IdentityHashMap<>());

	}

	private static class ValidationFromStep implements FromStep {

		@Override
		public Table<?> as(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Table<?> as(
			String name, Collection<Column<?, ?>> templateColumns) {

			throw new UnsupportedOperationException();
		}

		@Override
		public <T extends Table<T>> T as(String name, T templateTable) {
			throw new UnsupportedOperationException();
		}

		@Override
		public JoinStep from(Table<?> table) {
			return new From(this, table);
		}

		@Override
		public void toSQL(
			Consumer<String> consumer, ASTNodeListener astNodeListener) {

			consumer.accept(StringPool.TRIPLE_PERIOD);
		}

		@Override
		public DSLQuery union(DSLQuery dslQuery) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DSLQuery unionAll(DSLQuery dslQuery) {
			throw new UnsupportedOperationException();
		}

	}

}