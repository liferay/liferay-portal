/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.instrumentation;

import com.liferay.portal.vulcan.internal.graphql.exception.IntrospectionFieldLimitExceededException;

import graphql.ExecutionResult;

import graphql.com.google.common.collect.ImmutableListMultimap;

import graphql.execution.AbortExecutionException;
import graphql.execution.ExecutionContext;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;

import graphql.introspection.GoodFaithIntrospection;
import graphql.introspection.Introspection;

import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import graphql.language.SelectionSet;

import graphql.normalized.ExecutableNormalizedField;
import graphql.normalized.ExecutableNormalizedOperation;
import graphql.normalized.ExecutableNormalizedOperationFactory;

import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeUtil;
import graphql.schema.GraphQLUnmodifiedType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Magdalena Jedraszak
 */
public class IntrospectionFieldLimitInstrumentation
	extends SimpleInstrumentation {

	@Override
	public InstrumentationContext<ExecutionResult> beginExecuteOperation(
		InstrumentationExecuteOperationParameters
			instrumentationExecuteOperationParameters,
		InstrumentationState instrumentationState) {

		ExecutionContext executionContext =
			instrumentationExecuteOperationParameters.getExecutionContext();

		OperationDefinition operationDefinition =
			executionContext.getOperationDefinition();

		if (!_isIntrospection(
				executionContext.getFragmentsByName(), new HashSet<>(),
				operationDefinition.getSelectionSet())) {

			return super.beginExecuteOperation(
				instrumentationExecuteOperationParameters,
				instrumentationState);
		}

		int fieldsCount = _getFieldsCount(
			executionContext.getFragmentsByName(), new HashMap<>(),
			operationDefinition.getSelectionSet());

		if (fieldsCount > GoodFaithIntrospection.GOOD_FAITH_MAX_FIELDS_COUNT) {
			throw new IntrospectionFieldLimitExceededException(
				"Field count is greater than the introspection field limit " +
					"of " + GoodFaithIntrospection.GOOD_FAITH_MAX_FIELDS_COUNT);
		}

		ExecutableNormalizedOperation executableNormalizedOperation =
			_createExecutableNormalizedOperation(executionContext);

		ImmutableListMultimap<FieldCoordinates, ExecutableNormalizedField>
			coordinatesToNormalizedFields =
				executableNormalizedOperation.
					getCoordinatesToNormalizedFields();

		GraphQLSchema graphQLSchema = executionContext.getGraphQLSchema();

		for (FieldCoordinates fieldCoordinates :
				coordinatesToNormalizedFields.keySet()) {

			if (_isRepeated(
					coordinatesToNormalizedFields.get(fieldCoordinates),
					fieldCoordinates, graphQLSchema)) {

				throw new IntrospectionFieldLimitExceededException(
					fieldCoordinates + " is present more than once");
			}
		}

		_validateListOfListsFieldsCount(
			executableNormalizedOperation.getTopLevelFields(), graphQLSchema,
			0);

		return super.beginExecuteOperation(
			instrumentationExecuteOperationParameters, instrumentationState);
	}

	private ExecutableNormalizedOperation _createExecutableNormalizedOperation(
		ExecutionContext executionContext) {

		try {
			return ExecutableNormalizedOperationFactory.
				createExecutableNormalizedOperation(
					executionContext.getGraphQLSchema(),
					executionContext.getOperationDefinition(),
					executionContext.getFragmentsByName(),
					executionContext.getCoercedVariables(),
					ExecutableNormalizedOperationFactory.Options.defaultOptions(
					).graphQLContext(
						executionContext.getGraphQLContext()
					).locale(
						executionContext.getLocale()
					).maxChildrenDepth(
						GoodFaithIntrospection.GOOD_FAITH_MAX_DEPTH_COUNT
					).maxFieldsCount(
						GoodFaithIntrospection.GOOD_FAITH_MAX_FIELDS_COUNT
					));
		}
		catch (AbortExecutionException abortExecutionException) {
			throw new IntrospectionFieldLimitExceededException(
				abortExecutionException.getMessage());
		}
	}

	private int _getFieldsCount(
		Map<String, FragmentDefinition> fragmentDefinitions,
		Map<String, Integer> fragmentFieldsCounts, SelectionSet selectionSet) {

		if (selectionSet == null) {
			return 0;
		}

		int fieldsCount = 0;

		for (Selection<?> selection : selectionSet.getSelections()) {
			if (selection instanceof Field) {
				Field field = (Field)selection;

				int childFieldsCount = _getFieldsCount(
					fragmentDefinitions, fragmentFieldsCounts,
					field.getSelectionSet());

				fieldsCount += 1 + childFieldsCount;
			}
			else if (selection instanceof FragmentSpread) {
				FragmentSpread fragmentSpread = (FragmentSpread)selection;

				Integer fragmentFieldsCount = fragmentFieldsCounts.get(
					fragmentSpread.getName());

				if (fragmentFieldsCount == null) {
					fragmentFieldsCount = 0;

					FragmentDefinition fragmentDefinition =
						fragmentDefinitions.get(fragmentSpread.getName());

					if (fragmentDefinition != null) {
						fragmentFieldsCount = _getFieldsCount(
							fragmentDefinitions, fragmentFieldsCounts,
							fragmentDefinition.getSelectionSet());
					}

					fragmentFieldsCounts.put(
						fragmentSpread.getName(), fragmentFieldsCount);
				}

				fieldsCount += fragmentFieldsCount;
			}
			else if (selection instanceof InlineFragment) {
				InlineFragment inlineFragment = (InlineFragment)selection;

				fieldsCount += _getFieldsCount(
					fragmentDefinitions, fragmentFieldsCounts,
					inlineFragment.getSelectionSet());
			}

			if (fieldsCount >
					GoodFaithIntrospection.GOOD_FAITH_MAX_FIELDS_COUNT) {

				return GoodFaithIntrospection.GOOD_FAITH_MAX_FIELDS_COUNT + 1;
			}
		}

		return fieldsCount;
	}

	private boolean _isIntrospection(
		Map<String, FragmentDefinition> fragmentDefinitions,
		Set<String> fragmentNames, SelectionSet selectionSet) {

		for (Selection<?> selection : selectionSet.getSelections()) {
			if (selection instanceof Field) {
				Field field = (Field)selection;

				String name = field.getName();

				if (name.equals(Introspection.SchemaMetaFieldDef.getName()) ||
					name.equals(Introspection.TypeMetaFieldDef.getName())) {

					return true;
				}
			}
			else if (selection instanceof FragmentSpread) {
				FragmentSpread fragmentSpread = (FragmentSpread)selection;

				if (!fragmentNames.add(fragmentSpread.getName())) {
					continue;
				}

				FragmentDefinition fragmentDefinition = fragmentDefinitions.get(
					fragmentSpread.getName());

				if ((fragmentDefinition != null) &&
					_isIntrospection(
						fragmentDefinitions, fragmentNames,
						fragmentDefinition.getSelectionSet())) {

					return true;
				}
			}
			else if (selection instanceof InlineFragment) {
				InlineFragment inlineFragment = (InlineFragment)selection;

				if (_isIntrospection(
						fragmentDefinitions, fragmentNames,
						inlineFragment.getSelectionSet())) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _isIntrospectionListOfListsField(
		ExecutableNormalizedField executableNormalizedField,
		GraphQLSchema graphQLSchema) {

		for (String objectTypeName :
				executableNormalizedField.getObjectTypeNames()) {

			GraphQLObjectType graphQLObjectType = graphQLSchema.getObjectType(
				objectTypeName);

			if ((graphQLObjectType != null) &&
				Introspection.isIntrospectionTypes(graphQLObjectType) &&
				_isListOfListsField(
					graphQLObjectType.getFieldDefinition(
						executableNormalizedField.getName()))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isListOfListsField(
		GraphQLFieldDefinition graphQLFieldDefinition) {

		if (graphQLFieldDefinition == null) {
			return false;
		}

		GraphQLType graphQLType = GraphQLTypeUtil.unwrapNonNull(
			graphQLFieldDefinition.getType());

		if (!GraphQLTypeUtil.isList(graphQLType)) {
			return false;
		}

		GraphQLUnmodifiedType graphQLUnmodifiedType = GraphQLTypeUtil.unwrapAll(
			graphQLType);

		if (!(graphQLUnmodifiedType instanceof GraphQLFieldsContainer)) {
			return false;
		}

		GraphQLFieldsContainer graphQLFieldsContainer =
			(GraphQLFieldsContainer)graphQLUnmodifiedType;

		for (GraphQLFieldDefinition childGraphQLFieldDefinition :
				graphQLFieldsContainer.getFieldDefinitions()) {

			if (GraphQLTypeUtil.isList(
					GraphQLTypeUtil.unwrapNonNull(
						childGraphQLFieldDefinition.getType()))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isRepeated(
		Collection<ExecutableNormalizedField> executableNormalizedFields,
		FieldCoordinates fieldCoordinates, GraphQLSchema graphQLSchema) {

		if (executableNormalizedFields.size() < 2) {
			return false;
		}

		GraphQLObjectType graphQLObjectType = graphQLSchema.getObjectType(
			fieldCoordinates.getTypeName());

		if (graphQLObjectType == null) {
			return false;
		}

		String fieldName = fieldCoordinates.getFieldName();

		if (!Introspection.isIntrospectionTypes(graphQLObjectType) &&
			!fieldName.equals(Introspection.SchemaMetaFieldDef.getName())) {

			return false;
		}

		if (_isListOfListsField(
				graphQLObjectType.getFieldDefinition(fieldName))) {

			return true;
		}

		Set<ExecutableNormalizedField> parentExecutableNormalizedFields =
			new HashSet<>();

		for (ExecutableNormalizedField executableNormalizedField :
				executableNormalizedFields) {

			if (!parentExecutableNormalizedFields.add(
					executableNormalizedField.getParent())) {

				return true;
			}
		}

		return false;
	}

	private void _validateListOfListsFieldsCount(
		List<ExecutableNormalizedField> executableNormalizedFields,
		GraphQLSchema graphQLSchema, int listOfListsFieldsCount) {

		for (ExecutableNormalizedField executableNormalizedField :
				executableNormalizedFields) {

			int childListOfListsFieldsCount = listOfListsFieldsCount;

			if (_isIntrospectionListOfListsField(
					executableNormalizedField, graphQLSchema)) {

				childListOfListsFieldsCount++;

				if (childListOfListsFieldsCount >
						_MAX_LIST_OF_LISTS_FIELDS_COUNT) {

					throw new IntrospectionFieldLimitExceededException(
						"List of lists field count is greater than the " +
							"introspection list of lists field limit of " +
								_MAX_LIST_OF_LISTS_FIELDS_COUNT);
				}
			}

			_validateListOfListsFieldsCount(
				executableNormalizedField.getChildren(), graphQLSchema,
				childListOfListsFieldsCount);
		}
	}

	private static final int _MAX_LIST_OF_LISTS_FIELDS_COUNT = 2;

}