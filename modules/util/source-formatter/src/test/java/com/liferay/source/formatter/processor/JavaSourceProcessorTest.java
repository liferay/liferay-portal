/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringBundler;

import org.junit.Test;

/**
 * @author Hugo Huijser
 */
public class JavaSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testAnnotationParameterImports() throws Exception {
		test("AnnotationParameterImports.testjava");
	}

	@Test
	public void testAnonymousInnerClass() throws Exception {
		test("AnonymousInnerClass.testjava");
	}

	@Test
	public void testAssertUsage() throws Exception {
		test(
			"AssertUsage.testjava",
			"Use org.junit.Assert instead of org.testng.Assert, see LPS-55690");
	}

	@Test
	public void testAssignmentsAndSetCallsOrder() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"AssignmentsAndSetCallsOrder.testjava"
			).addExpectedMessage(
				"The variable assignment for \"appDeployments\" should come " +
					"before the variable assignment for \"dataDefinitionId\"",
				20
			).addExpectedMessage(
				"The variable assignment for \"settings\" should come before " +
					"the variable assignment for \"type\"",
				24
			).addExpectedMessage(
				"The variable assignment for \"type\" should come before the " +
					"method calling \"setName\"",
				33
			).addExpectedMessage(
				"The variable assignment for \"settings\" should come before " +
					"the variable assignment for \"type\"",
				39
			).addExpectedMessage(
				"The method calling \"setCompany\" should come before the " +
					"method calling \"setName\"",
				45
			));
	}

	@Test
	public void testBaseReferenceVariableWithoutComponent() throws Exception {
		test(
			"BaseReferenceVariableWithoutComponent.testjava",
			"@Reference variable \"_testField\" should be protected instead " +
				"of private in a class without @Component",
			19);
	}

	@Test
	public void testBuilder() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"Builder.testjava"
			).addExpectedMessage(
				"Include method call \"hashMap.put\" (23) in " +
					"\"HashMapBuilder\" (19)",
				19
			).addExpectedMessage(
				"Inline variable definition \"company\" (29) inside " +
					"\"HashMapBuilder\" (31), possibly by using a lambda " +
						"function",
				29
			).addExpectedMessage(
				"Null values are not allowed in \"HashMapBuilder\"", 38
			).addExpectedMessage(
				"Use \"HashMapBuilder\" (43, 45)", 43
			).addExpectedMessage(
				"Use \"HashMapBuilder\" instead of new instance of \"HashMap\"",
				49
			));
	}

	@Test
	public void testChainPutForOrgJSONObject() throws Exception {
		test(
			"ChainPutForOrgJSONObject.testjava",
			"Chaining on \"jsonObject.put\" is preferred", 18);
	}

	@Test
	public void testCollapseImports() throws Exception {
		test("CollapseImports.testjava");
	}

	@Test
	public void testCombineIfStatementAndReturn() throws Exception {
		test("CombineIfStatementAndReturn.testjava");
	}

	@Test
	public void testCombineLines() throws Exception {
		test("CombineLines.testjava");
	}

	@Test
	public void testCommentStyling() throws Exception {
		test("CommentStyling.testjava");
	}

	@Test
	public void testCompanyThreadLocalUsage() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"CompanyThreadLocalUsage.testjava"
			).addExpectedMessage(
				StringBundler.concat(
					"Do not use \"CompanyThreadLocal.setCompanyId\", use ",
					"\"CompanyThreadLocal.setCompanyIdWithSafeCloseable\" ",
					"instead"),
				16
			).addExpectedMessage(
				StringBundler.concat(
					"Missing calling \"close\" to variable ",
					"\"_safeCloseable1\", use \"_safeCloseable1.close\" or ",
					"try-with-resources statement instead"),
				28
			).addExpectedMessage(
				StringBundler.concat(
					"Missing calling \"close\" to variable ",
					"\"_safeCloseable2\", use \"_safeCloseable2.close\" or ",
					"try-with-resources statement instead"),
				29
			));
	}

	@Test
	public void testConstructorParameterOrder() throws Exception {
		test("ConstructorParameterOrder.testjava");
	}

	@Test
	public void testDeserializationSecurity() throws Exception {
		test(
			"DeserializationSecurity.testjava",
			"Use ProtectedObjectInputStream instead of new ObjectInputStream");
	}

	@Test
	public void testDiamondOperator() throws Exception {
		test("DiamondOperator.testjava");
	}

	@Test
	public void testDuplicateConstructors() throws Exception {
		test(
			"DuplicateConstructors.testjava",
			"Duplicate DuplicateConstructors");
	}

	@Test
	public void testDuplicateMethods() throws Exception {
		test("DuplicateMethods.testjava", "Duplicate method");
	}

	@Test
	public void testDuplicateVariables() throws Exception {
		test("DuplicateVariables.testjava", "Duplicate _STRING_2");
	}

	@Test
	public void testElseStatement() throws Exception {
		test("ElseStatement1.testjava");
		test(
			"ElseStatement2.testjava",
			"Else statement is not needed because of the \"return\" " +
				"statement on line 17",
			19);
	}

	@Test
	public void testExceedMaxLineLength() throws Exception {
		test("ExceedMaxLineLength.testjava", "> 80", 27);
	}

	@Test
	public void testExceptionMapper() throws Exception {
		test(
			"ExceptionMapperService.testjava",
			"The value of \"osgi.jaxrs.name\" should end with " +
				"\"ExceptionMapper\"",
			21);
	}

	@Test
	public void testExceptionPrintStackTrace() throws Exception {
		test(
			"ExceptionPrintStackTrace.testjava",
			"Avoid using method \"printStackTrace\"", 22);
	}

	@Test
	public void testExceptionVariableName() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"ExceptionVariableName.testjava"
			).addExpectedMessage(
				"Rename exception variable \"e\" to \"configurationException\"",
				28
			).addExpectedMessage(
				"Rename exception variable \"e\" to \"configurationException\"",
				41
			).addExpectedMessage(
				"Rename exception variable \"re\" to \"exception\"", 52
			).addExpectedMessage(
				"Rename exception variable \"ioe\" to \"ioException1\"", 57
			).addExpectedMessage(
				"Rename exception variable \"oie\" to \"ioException2\"", 61
			).addExpectedMessage(
				"Rename exception variable \"ioe1\" to \"ioException1\"", 72
			).addExpectedMessage(
				"Rename exception variable \"ioe2\" to \"ioException2\"", 76
			).addExpectedMessage(
				"Rename exception variable \"ioe1\" to \"ioException\"", 87
			).addExpectedMessage(
				"Rename exception variable \"ioe2\" to \"ioException\"", 93
			));
	}

	@Test
	public void testFeatureFlagsAnnotationTest() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"FeatureFlagsAnnotationTest.testjava"
			).addExpectedMessage(
				"Use annotation \"@FeatureFlags\" instead of \"PropsUtil." +
					"addProperties\" for feature flag",
				22
			).addExpectedMessage(
				"Use annotation \"@FeatureFlags\" instead of \"PropsUtil." +
					"addProperties\" for feature flag",
				32
			).addExpectedMessage(
				"Use annotation \"@FeatureFlags\" instead of \"PropsUtil." +
					"addProperties\" for feature flag",
				42
			));
	}

	@Test
	public void testFormatAnnotations() throws Exception {
		test("FormatAnnotations1.testjava");
		test("FormatAnnotations2.testjava");
	}

	@Test
	public void testFormatBooleanStatements() throws Exception {
		test("FormatBooleanStatements.testjava");
	}

	@Test
	public void testFormatImports() throws Exception {
		test("FormatImports.testjava");
	}

	@Test
	public void testFormatJSONObject() throws Exception {
		test("FormatJSONObject.testjava");
	}

	@Test
	public void testFormatReturnStatements() throws Exception {
		test("FormatReturnStatements.testjava");
	}

	@Test
	public void testGetFeatureFlag() throws Exception {
		test(
			"GetFeatureFlag.testjava",
			"Use \"FeatureFlagManagerUtil.isEnabled\" instead of " +
				"\"PropsUtil.get\" for feature flag",
			17);
	}

	@Test
	public void testIfClauseIncorrectLineBreaks() throws Exception {
		test("IfClauseIncorrectLineBreaks.testjava");
	}

	@Test
	public void testIfClauseWhitespace() throws Exception {
		test("IfClauseWhitespace.testjava");
	}

	@Test
	public void testImmediateAttribute() throws Exception {
		test(
			"ImmediateAttribute.testjava",
			"Do not use \"immediate = true\" in @Component");
	}

	@Test
	public void testIncorrectClose() throws Exception {
		test("IncorrectClose.testjava");
	}

	@Test
	public void testIncorrectCopyright() throws Exception {
		test("IncorrectCopyright.testjava", "File must start with copyright");
	}

	@Test
	public void testIncorrectEmptyLinesInUpgradeProcess() throws Exception {
		test("IncorrectEmptyLinesInUpgradeProcess.testjava");
	}

	@Test
	public void testIncorrectExecuteUpdateCall() throws Exception {
		test(
			"IncorrectExecuteUpdateCall.testjava",
			"Use \"addBatch()\" and \"executeBatch()\" instead of \"" +
				"executeUpdate()\" inside loops",
			28);
	}

	@Test
	public void testIncorrectImports() throws Exception {
		test("IncorrectImports1.testjava");
		test(
			SourceProcessorTestParameters.create(
				"IncorrectImports2.testjava"
			).addExpectedMessage(
				"Illegal import: edu.emory.mathcs.backport.java"
			).addExpectedMessage(
				"Illegal import: jodd.util.StringPool"
			).addExpectedMessage(
				"Use ProxyUtil instead of java.lang.reflect.Proxy"
			));
	}

	@Test
	public void testIncorrectInitialRequestPortalInstanceLifecycleListener()
		throws Exception {

		test(
			"IncorrectInitialRequestPortalInstanceLifecycleListener1.testjava",
			StringBundler.concat(
				"Missing \"activate(BundleContext bundleContext)\" method ",
				"with \"@Activate\" annotation that calls ",
				"\"super.activate(bundleContext)\""));

		test(
			SourceProcessorTestParameters.create(
				"IncorrectInitialRequestPortalInstanceLifecycleListener2." +
					"testjava"
			).addExpectedMessage(
				"The \"activate\" method is missing the \"@Override\" " +
					"annotation",
				22
			).addExpectedMessage(
				"The \"activate\" method must call \"super.activate(" +
					"bundleContext)\"",
				22
			));
	}

	@Test
	public void testIncorrectMethodCallsInUpgradeSteps() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectMethodCallsInUpgradeSteps.testjava"
			).addExpectedMessage(
				"Only \"Table.create*\" and \"UpgradeProcessFactory.*\" " +
					"calls are allowed in \"getPostUpgradeSteps\" and \"" +
						"getPreUpgradeSteps\", see LPD-44331",
				35
			).addExpectedMessage(
				"Only \"Table.create*\" and \"UpgradeProcessFactory.*\" " +
					"calls are allowed in \"getPostUpgradeSteps\" and \"" +
						"getPreUpgradeSteps\", see LPD-44331",
				38
			));
	}

	@Test
	public void testIncorrectOperatorOrder() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectOperatorOrder.testjava"
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 44
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 48
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 52
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 88
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 92
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 96
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 132
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 136
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 140
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 176
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 180
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 184
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 220
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 224
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 228
			).addExpectedMessage(
				"\"3\" should be on the right hand side of the operator", 264
			).addExpectedMessage(
				"\"+3\" should be on the right hand side of the operator", 268
			).addExpectedMessage(
				"\"-3\" should be on the right hand side of the operator", 272
			));
	}

	@Test
	public void testIncorrectParameterNames() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectParameterNames.testjava"
			).addExpectedMessage(
				"Parameter \"StringMap\" must match pattern " +
					"\"^[a-z][_a-zA-Z0-9]*$\"",
				15
			).addExpectedMessage(
				"Parameter \"TestString\" must match pattern " +
					"\"^[a-z][_a-zA-Z0-9]*$\"",
				19
			));
	}

	@Test
	public void testIncorrectReferenceCardinality() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectReferenceCardinality.testjava"
			).addExpectedMessage(
				"Use Snapshot instead of \"cardinality = " +
					"ReferenceCardinality.OPTIONAL\", see LPS-184625",
				20
			).addExpectedMessage(
				StringBundler.concat(
					"When using \"cardinality = ReferenceCardinality.",
					"OPTIONAL\" and \"policyOption = ReferencePolicyOption.",
					"GREEDY\", always use \"policy = ReferencePolicy.",
					"DYNAMIC\" as well"),
				20
			));
	}

	@Test
	public void testIncorrectVariableNames() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"IncorrectVariableNames1.testjava"
			).addExpectedMessage(
				"public constant \"_TEST_1\" of type \"int\" must match " +
					"pattern \"^[A-Z0-9][_A-Z0-9]*$\"",
				13
			).addExpectedMessage(
				"Protected or public non-static field \"_test2\" must match " +
					"pattern \"^[a-z0-9][_a-zA-Z0-9]*$\"",
				19
			));
		test(
			"IncorrectVariableNames2.testjava",
			"private constant \"STRING_1\" of type \"String\" must match " +
				"pattern \"^_[A-Z0-9][_A-Z0-9]*$\"",
			17);
		test(
			SourceProcessorTestParameters.create(
				"IncorrectVariableNames3.testjava"
			).addExpectedMessage(
				"Local non-final variable \"TestMapWithARatherLongName\" " +
					"must match pattern \"^[a-z0-9][_a-zA-Z0-9]*$\"",
				17
			).addExpectedMessage(
				"Local non-final variable \"TestString\" must match pattern " +
					"\"^[a-z0-9][_a-zA-Z0-9]*$\"",
				20
			));
	}

	@Test
	public void testIncorrectWhitespace() throws Exception {
		test("IncorrectWhitespace.testjava");
	}

	@Test
	public void testInefficientStringMethods() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"InefficientStringMethods.testjava"
			).addExpectedMessage(
				"Use StringUtil.equalsIgnoreCase", 17
			).addExpectedMessage(
				"Use StringUtil.toLowerCase", 21
			).addExpectedMessage(
				"Use StringUtil.toUpperCase", 22
			));
	}

	@Test
	public void testJavaNewProblemInstantiationParameters() throws Exception {
		test("JavaNewProblemInstantiationParameters.testjava");
	}

	@Test
	public void testJavaParameterAnnotations() throws Exception {
		test("JavaParameterAnnotations.testjava");
	}

	@Test
	public void testJavaTermDividers() throws Exception {
		test("JavaTermDividers.testjava");
	}

	@Test
	public void testJavaVariableFinalableFields1() throws Exception {
		test("JavaVariableFinalableFields1.testjava");
	}

	@Test
	public void testJavaVariableFinalableFields2() throws Exception {
		test("JavaVariableFinalableFields2.testjava");
	}

	@Test
	public void testListUtilUsages() throws Exception {
		test(
			"ListUtilUsages.testjava",
			"Use \"ListUtil.isEmpty(list)\" to simplify code", 16);
	}

	@Test
	public void testLocalVariableTypeInferences() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"LocalVariableTypeInferences.testjava"
			).addExpectedMessage(
				"Avoid using \"var\" to declare variable", 17
			).addExpectedMessage(
				"Avoid using \"var\" to declare variable", 25
			));
	}

	@Test
	public void testLogLevels() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"Levels.testjava"
			).addExpectedMessage(
				"Do not use _log.isErrorEnabled()", 18
			).addExpectedMessage(
				"Use _log.isDebugEnabled()", 27
			).addExpectedMessage(
				"Use _log.isDebugEnabled()", 32
			).addExpectedMessage(
				"Use _log.isInfoEnabled()", 44
			).addExpectedMessage(
				"Use _log.isTraceEnabled()", 49
			).addExpectedMessage(
				"Use _log.isWarnEnabled()", 59
			));
	}

	@Test
	public void testLogParameters() throws Exception {
		test("LogParameters.testjava");
	}

	@Test
	public void testMapBuilderGenerics() throws Exception {
		test("MapBuilderGenerics.testjava");
	}

	@Test
	public void testMethodEquals() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MethodEquals.testjava"
			).addExpectedMessage(
				"Use \"Objects.equals\" instead of calling \"equals\" on " +
					"method",
				15
			).addExpectedMessage(
				"Use \"Objects.equals\" instead of calling \"equals\" on " +
					"method",
				21
			));
	}

	@Test
	public void testMissingAuthor() throws Exception {
		test("MissingAuthor.testjava", "Missing author", 11);
	}

	@Test
	public void testMissingDiamondOperator() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MissingDiamondOperator.testjava"
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"ArrayList\"", 36
			).addExpectedMessage(
				"Missing generic types \"<String, String>\" for type " +
					"\"ArrayList\"",
				38
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type " +
					"\"ConcurrentHashMap\"",
				44
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type " +
					"\"ConcurrentSkipListMap\"",
				46
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type " +
					"\"ConcurrentSkipListSet\"",
				48
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type " +
					"\"CopyOnWriteArraySet\"",
				50
			).addExpectedMessage(
				"Missing generic types \"<Position, String>\" for type " +
					"\"EnumMap\"",
				52
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"HashMap\"", 59
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"HashSet\"", 61
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"Hashtable\"", 63
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"IdentityHashMap\"",
				65
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"LinkedHashMap\"", 68
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"LinkedHashSet\"", 70
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"LinkedList\"", 72
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"Stack\"", 74
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"TreeMap\"", 76
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"TreeSet\"", 78
			).addExpectedMessage(
				"Missing diamond operator \"<>\" for type \"Vector\"", 80
			));
	}

	@Test
	public void testMissingEmptyLines() throws Exception {
		test("MissingEmptyLines.testjava");
	}

	@Test
	public void testMissingEmptyLinesAfterMethodCalls() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MissingEmptyLinesAfterMethodCalls.testjava"
			).addExpectedMessage(
				"There should be an empty line after \"registry.register\"", 14
			).addExpectedMessage(
				"There should be an empty line after \"registry.register\"", 15
			).addExpectedMessage(
				"There should be an empty line after \"registry.register\"", 25
			));
	}

	@Test
	public void testMissingEmptyLinesAfterReferencingVariable()
		throws Exception {

		test(
			"MissingEmptyLinesAfterReferencingVariable.testjava",
			"There should be an empty line before line \"47\", as we " +
				"finished referencing variable \"group\"",
			47);
	}

	@Test
	public void testMissingEmptyLinesBeforeMethodCalls() throws Exception {
		test(
			"MissingEmptyLinesBeforeMethodCalls.testjava",
			"There should be an empty line before \"portletPreferences.store\"",
			17);
	}

	@Test
	public void testMissingEmptyLinesBetweenAssigningAndUsingVariable()
		throws Exception {

		test(
			"MissingEmptyLinesBetweenAssigningAndUsingVariable.testjava",
			"There should be an empty line between assigning and using " +
				"variable \"jsonBeginPos\"",
			16);
	}

	@Test
	public void testMissingEmptyLinesInInstanceInit() throws Exception {
		test(
			"MissingEmptyLinesInInstanceInit.testjava",
			"There should be an empty line after line \"18\"", 18);
	}

	@Test
	public void testMissingParameterizedSQLStatement() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MissingParameterizedSQLStatement.testjava"
			).addExpectedMessage(
				"Use \"PreparedStatement.set*\" to parameterize \"ownerType\"",
				23
			).addExpectedMessage(
				"Use \"PreparedStatement.set*\" to parameterize \"portletId\"",
				23
			));
	}

	@Test
	public void testMissingSerialVersionUID() throws Exception {
		test(
			"MissingSerialVersionUID.testjava",
			"Assign ProcessCallable implementation a serialVersionUID");
	}

	@Test
	public void testMoveUpgradeSteps() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"MoveUpgradeSteps.testjava"
			).addExpectedMessage(
				"Move \"alterTableAddColumn\" call inside " +
					"\"getPreUpgradeSteps\" method",
				17
			).addExpectedMessage(
				"Move \"alterTableAddColumn\" call inside " +
					"\"getPostUpgradeSteps\" method",
				21
			));
	}

	@Test
	public void testNullAssertionInIfStatement() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"NullAssertionInIfStatement.testjava"
			).addExpectedMessage(
				"Null check for variable \"list\" should always be first in " +
					"if-statement",
				16
			).addExpectedMessage(
				"Null check for variable \"list\" should always be first in " +
					"if-statement",
				24
			).addExpectedMessage(
				"Null check for variable \"nameList1\" should always be " +
					"first in if-statement",
				37
			));
	}

	@Test
	public void testNullVariable() throws Exception {
		test("NullVariable.testjava");
	}

	@Test
	public void testPackageName() throws Exception {
		test(
			"PackageName.testjava",
			"The declared package \"com.liferay.source.formatter.hello." +
				"world\" does not match the expected package");
	}

	@Test
	public void testProxyUsage() throws Exception {
		test(
			"ProxyUsage.testjava",
			"Use ProxyUtil instead of java.lang.reflect.Proxy");
	}

	@Test
	public void testReadabilityImprovement() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"ReadabilityImprovement.testjava"
			).addExpectedMessage(
				"Create a new variable for the left hand side operand of the " +
					"\"+\" operator for better readability",
				14
			).addExpectedMessage(
				"Create a new variable for the left hand side operand of the " +
					"\"+\" operator for better readability",
				22
			));
	}

	@Test
	public void testRecordClass() throws Exception {
		test("RecordClass.testjava", "Do not declare record class", 11);
	}

	@Test
	public void testRedundantCommas() throws Exception {
		test("RedundantCommas.testjava");
	}

	@Test
	public void testRedundantLog() throws Exception {
		test(
			"RedundantLog.testjava",
			"Redundant log between line \"17\" and line \"22\".", 17);
	}

	@Test
	public void testReferenceMethods() throws Exception {
		test(
			"ReferenceMethods.testjava",
			"Do not use @Reference on method testMethod, use @Reference on " +
				"field or ServiceTracker/ServiceTrackerList" +
					"/ServiceTrackerMap instead");
	}

	@Test
	public void testResultCountSet() throws Exception {
		test(
			"ResultSetCount.testjava", "Use resultSet.getInt(1) for count", 26);
	}

	@Test
	public void testResultSetGetCall() throws Exception {
		test(
			"ResultSetGetCall.testjava",
			"Do not use \"TableName.ColumnName\" as the parameter when " +
				"calling method \"resultSet.get*\", use column index or " +
					"column name instead",
			43);
	}

	@Test
	public void testRunSQLStyling() throws Exception {
		test("RunSQLStyling.testjava");
	}

	@Test
	public void testSealedModifierUsage() throws Exception {
		test(
			"SealedModifierUsage.testjava",
			"Do not use modifier \"sealed\" and \"non-sealed\"", 11);
	}

	@Test
	public void testSecureRandomNumberGeneration() throws Exception {
		test(
			"SecureRandomNumberGeneration.testjava",
			"Use SecureRandomUtil or com.liferay.portal.kernel.security." +
				"SecureRandom instead of java.security.SecureRandom, see " +
					"LPS-39508");
	}

	@Test
	public void testServiceProxyFactoryNewServiceTrackedInstance()
		throws Exception {

		test(
			"ServiceProxyFactoryNewServiceTrackedInstance.testjava",
			"Pass \"ServiceProxyFactoryNewServiceTrackedInstance.class\" as " +
				"the second parameter when calling method " +
					"\"ServiceProxyFactory.newServiceTrackedInstance\"",
			21);
	}

	@Test
	public void testSimplifyListUtilCalls() throws Exception {
		test("SimplifyListUtilCalls.testjava");
	}

	@Test
	public void testSizeIsZeroCheck() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"SizeIsZero.testjava"
			).addExpectedMessage(
				"Use method \"_testList.isEmpty()\" instead", 19
			).addExpectedMessage(
				"Use method \"myList.isEmpty()\" instead", 24
			));
	}

	@Test
	public void testSortAnnotationParameters() throws Exception {
		test("SortAnnotationParameters.testjava");
	}

	@Test
	public void testSortChainedMethodCalls() throws Exception {
		test("SortChainedMethodCalls.testjava");
	}

	@Test
	public void testSortExceptions() throws Exception {
		test("SortExceptions.testjava");
	}

	@Test
	public void testSortJavaTerms() throws Exception {
		test("SortJavaTerms1.testjava");
		test("SortJavaTerms2.testjava");
		test("SortJavaTerms3.testjava");
		test("SortJavaTerms4.testjava");
		test("SortJavaTerms5.testjava");
	}

	@Test
	public void testSortMethodCalls() throws Exception {
		test("SortMethodCalls.testjava");
	}

	@Test
	public void testSortMethodsWithAnnotatedParameters() throws Exception {
		test("SortMethodsWithAnnotatedParameters.testjava");
	}

	@Test
	public void testSQLBooleanValues() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"SQLBooleanValues.testjava"
			).addExpectedMessage(
				"Use \"[$TRUE$]\" instead of \"true\" in SQL statements", 21
			).addExpectedMessage(
				"Use \"[$TRUE$]\" instead of \"true\" in SQL statements", 28
			).addExpectedMessage(
				"Use \"[$TRUE$]\" instead of \"true\" in SQL statements", 42
			).addExpectedMessage(
				"Use \"[$FALSE$]\" instead of \"false\" in SQL statements", 53
			).addExpectedMessage(
				"Use \"SQLTransformer.transform\" to wrap SQL statement if " +
					"it contains \"[$FALSE$]\" or \"[$TRUE$]\"",
				63
			).addExpectedMessage(
				"Use \"[$FALSE$]\" instead of \"false\" in SQL statements", 72
			).addExpectedMessage(
				"Use \"SQLTransformer.transform\" to wrap SQL statement if " +
					"it contains \"[$FALSE$]\" or \"[$TRUE$]\"",
				83
			));
	}

	@Test
	public void testStaticFinalLog() throws Exception {
		test("StaticFinalLog.testjava");
	}

	@Test
	public void testStringConcatenation() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"StringConcatenation.testjava"
			).addExpectedMessage(
				"When concatenating multiple literal strings, only the first " +
					"literal string can start with \" \"",
				19
			).addExpectedMessage(
				"Use method \"StringBundler.concat\" when concatenating more " +
					"than 3 strings",
				33
			).addExpectedMessage(
				"Do not use \"StringBundler.concat\" when concatenating less " +
					"than 3 elements",
				41
			));
	}

	@Test
	public void testSwitchExpression() throws Exception {
		test(
			"SwitchExpression.testjava",
			"Use \"if/else\" statement instead of \"switch\"", 14);
	}

	@Test
	public void testTextBlock() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"TextBlock.testjava"
			).addExpectedMessage(
				"Do not use text block", 14
			).addExpectedMessage(
				"Do not use text block", 23
			).addExpectedMessage(
				"Do not use text block", 29
			));
	}

	@Test
	public void testThrowsSystemException() throws Exception {
		test("ThrowsSystemException.testjava");
	}

	@Test
	public void testToJSONStringMethodCalls() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"ToJSONStringMethodCalls.testjava"
			).addExpectedMessage(
				"Use \"toString\" instead of \"toJSONString\"", 21
			).addExpectedMessage(
				"Use \"toString\" instead of \"toJSONString\"", 30
			).addExpectedMessage(
				"Use \"toString\" instead of \"toJSONString\"", 34
			).addExpectedMessage(
				"Use \"toString\" instead of \"toJSONString\"", 58
			));
	}

	@Test
	public void testTruncateLongLines() throws Exception {
		test("TruncateLongLines.testjava");
	}

	@Test
	public void testUnnecessaryConfigurationPolicy() throws Exception {
		test(
			"UnnecessaryConfigurationPolicy.testjava",
			"Remove \"configurationPolicy = ConfigurationPolicy.OPTIONAL\" " +
				"as it is unnecessary",
			14);
	}

	@Test
	public void testUnnecessaryMethodCalls() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"UnnecessaryMethodCalls.testjava"
			).addExpectedMessage(
				"Use \"webCachePool\" instead of calling method " +
					"\"_getWebCachePool\"",
				26
			).addExpectedMessage(
				"Use \"webCachePool\" instead of calling method " +
					"\"_getWebCachePool\"",
				34
			).addExpectedMessage(
				"Use \"this.name\" instead of calling method \"_getName\"", 38
			).addExpectedMessage(
				"Use \"webCachePool\" instead of calling method " +
					"\"_getWebCachePool\"",
				44
			).addExpectedMessage(
				"Use \"webCachePool_1\" instead of calling method " +
					"\"getWebCachePool\"",
				70
			));
	}

	@Test
	public void testUnnecessaryTypeCastInStringBundlerConcat()
		throws Exception {

		test("UnnecessaryTypeCastInStringBundlerConcat.testjava");
	}

	@Test
	public void testUnnecessaryUpgradeProcessClass() throws Exception {
		test(
			"UnnecessaryUpgradeProcessClass.testjava",
			"No need to create \"UnnecessaryUpgradeProcessClass\" class. " +
				"Replace it by inline calls to the \"UpgradeProcessFactory\" " +
					"class in the registrator class",
			13);
	}

	@Test
	public void testUnusedImport() throws Exception {
		test("UnusedImport.testjava");
	}

	@Test
	public void testUnusedMethods() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"UnusedMethods.testjava"
			).addExpectedMessage(
				"Method \"_getInteger\" is unused", 24
			).addExpectedMessage(
				"Method \"_getString\" is unused", 32
			));
	}

	@Test
	public void testUnusedParameter() throws Exception {
		test("UnusedParameter.testjava", "Parameter \"color\" is unused", 17);
	}

	@Test
	public void testUnusedVariable() throws Exception {
		test(
			SourceProcessorTestParameters.create(
				"UnusedVariable.testjava"
			).addExpectedMessage(
				"Variable \"matcher\" is unused", 17
			).addExpectedMessage(
				"Variable \"hello\" is unused", 20
			).addExpectedMessage(
				"Variable \"_s\" is unused", 32
			));
	}

	@Test
	public void testUpgradeDropTable() throws Exception {
		test("UpgradeDropTable.testjava");
	}

	@Test
	public void testUpgradeProcessUnnecessaryIfStatement() throws Exception {
		test(
			"UpgradeProcessUnnecessaryIfStatement1.testjava",
			"No need to use if-statement to wrap \"alterColumn*\" and " +
				"\"alterTable*\" calls",
			17);
		test(
			"UpgradeProcessUnnecessaryIfStatement2.testjava",
			"No need to use if-statement to wrap \"alterColumn*\" and " +
				"\"alterTable*\" calls",
			17);
	}

	@Test
	public void testUsePassedInVariable() throws Exception {
		test("UsePassedInVariable.testjava");
	}

}