/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.instrumentation;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.internal.graphql.exception.IntrospectionFieldLimitExceededException;

import graphql.GraphQLContext;

import graphql.execution.CoercedVariables;
import graphql.execution.ExecutionContextBuilder;
import graphql.execution.ExecutionId;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;

import graphql.introspection.IntrospectionQuery;

import graphql.language.NodeUtil;

import graphql.parser.Parser;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Magdalena Jedraszak
 */
public class IntrospectionFieldLimitInstrumentationTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		SchemaParser schemaParser = new SchemaParser();

		_graphQLSchema = schemaGenerator.makeExecutableSchema(
			schemaParser.parse("type Query { hello: String }"),
			RuntimeWiring.newRuntimeWiring(
			).build());
	}

	@Test
	public void testBeginExecuteOperation() {
		_beginExecuteOperation(
			"{ __schema { directives { args { name } } types { fields { args " +
				"{ name } } } } }");
		_beginExecuteOperation(
			"{ __schema { types { enumValues { name } fields { type { " +
				"enumValues { name } } } } } }");
		_beginExecuteOperation(
			"{ __schema { types { ofType { ofType { ofType { name } } } " +
				"possibleTypes { ofType { ofType { name } } } } } }");
		_beginExecuteOperation(
			"{ a: __type(name: \"Query\") { enumValues { name } } b: " +
				"__type(name: \"Query\") { enumValues { name } } }");
		_beginExecuteOperation(
			"{ a: __typename b: __typename __schema { queryType { name } } }");
		_beginExecuteOperation(IntrospectionQuery.INTROSPECTION_QUERY);
	}

	@Test
	public void testBeginExecuteOperationWithMaxDepthExceeded() {
		_assertIntrospectionFieldLimitExceeded(
			"Maximum query depth exceeded. 21 > 20",
			StringBundler.concat(
				"{ __schema { types { ", "ofType { ".repeat(20), "name",
				" }".repeat(20), " } } }"));
	}

	@Test
	public void testBeginExecuteOperationWithMaxFieldsCountExceeded() {
		_assertIntrospectionFieldLimitExceeded(
			"Field count is greater than the introspection field limit of 500",
			"{ __schema { types { " + _getAliasedFields("name", 501) +
				" } } }");
		_assertIntrospectionFieldLimitExceeded(
			"Field count is greater than the introspection field limit of 500",
			_getNestedFragments(40, "__schema { types { name } }"));
	}

	@Test
	public void testBeginExecuteOperationWithMaxListOfListsFieldsCountExceeded() {
		_assertIntrospectionFieldLimitExceeded(
			"List of lists field count is greater than the introspection " +
				"list of lists field limit of 2",
			"{ __schema { types { interfaces { possibleTypes { name } } } } }");
		_assertIntrospectionFieldLimitExceeded(
			"List of lists field count is greater than the introspection " +
				"list of lists field limit of 2",
			"{ __type(name: \"Query\") { interfaces { possibleTypes { fields " +
				"{ name } } } } }");
	}

	@Test
	public void testBeginExecuteOperationWithNonintrospectionQuery() {
		_beginExecuteOperation("{ " + _getAliasedFields("hello", 501) + " }");
		_beginExecuteOperation(_getNestedFragments(40, "hello"));
	}

	@Test
	public void testBeginExecuteOperationWithRepeatedField() {
		_assertIntrospectionFieldLimitExceeded(
			"Query.__schema is present more than once",
			"{ a: __schema { queryType { name } } b: __schema { queryType { " +
				"name } } }");
		_assertIntrospectionFieldLimitExceeded(
			"__Field.args is present more than once",
			"{ __schema { types { fields { a: args { name } b: args { name } " +
				"} } } }");
		_assertIntrospectionFieldLimitExceeded(
			"__Field.name is present more than once",
			"{ __schema { types { fields { a: name b: name } } } }");
		_assertIntrospectionFieldLimitExceeded(
			"__Schema.directives is present more than once",
			StringBundler.concat(
				"{ ...Root } fragment Root on Query { __schema { ",
				"...Directives } } fragment Directives on __Schema { a: ",
				"directives { name } b: directives { name } }"));
		_assertIntrospectionFieldLimitExceeded(
			"__Schema.types is present more than once",
			"{ __schema { a: types { name } b: types { name } } }");
		_assertIntrospectionFieldLimitExceeded(
			"__Type.__typename is present more than once",
			"{ __schema { types { a: __typename b: __typename } } }");
		_assertIntrospectionFieldLimitExceeded(
			"__Type.fields is present more than once",
			"{ __schema { types { fields { type { fields { name } } } } } }");
	}

	private void _assertIntrospectionFieldLimitExceeded(
		String message, String query) {

		try {
			_beginExecuteOperation(query);

			Assert.fail();
		}
		catch (IntrospectionFieldLimitExceededException
					introspectionFieldLimitExceededException) {

			Assert.assertEquals(
				message, introspectionFieldLimitExceededException.getMessage());
		}
	}

	private void _beginExecuteOperation(String query) {
		NodeUtil.GetOperationResult getOperationResult = NodeUtil.getOperation(
			Parser.parse(query), null);

		_introspectionFieldLimitInstrumentation.beginExecuteOperation(
			new InstrumentationExecuteOperationParameters(
				ExecutionContextBuilder.newExecutionContextBuilder(
				).coercedVariables(
					CoercedVariables.emptyVariables()
				).executionId(
					ExecutionId.generate()
				).fragmentsByName(
					getOperationResult.fragmentsByName
				).graphQLContext(
					GraphQLContext.newContext(
					).build()
				).graphQLSchema(
					_graphQLSchema
				).locale(
					LocaleUtil.US
				).operationDefinition(
					getOperationResult.operationDefinition
				).build()),
			null);
	}

	private String _getAliasedFields(String fieldName, int count) {
		StringBundler sb = new StringBundler(count * 4);

		for (int i = 0; i < count; i++) {
			sb.append(" a");
			sb.append(i);
			sb.append(": ");
			sb.append(fieldName);
		}

		return sb.toString();
	}

	private String _getNestedFragments(int depth, String selection) {
		StringBundler sb = new StringBundler((depth * 7) + 6);

		sb.append("{ ...F0 }");

		for (int i = 0; i < depth; i++) {
			sb.append(" fragment F");
			sb.append(i);
			sb.append(" on Query { ...F");
			sb.append(i + 1);
			sb.append(" ...F");
			sb.append(i + 1);
			sb.append(" }");
		}

		sb.append(" fragment F");
		sb.append(depth);
		sb.append(" on Query { ");
		sb.append(selection);
		sb.append(" }");

		return sb.toString();
	}

	private GraphQLSchema _graphQLSchema;
	private final IntrospectionFieldLimitInstrumentation
		_introspectionFieldLimitInstrumentation =
			new IntrospectionFieldLimitInstrumentation();

}