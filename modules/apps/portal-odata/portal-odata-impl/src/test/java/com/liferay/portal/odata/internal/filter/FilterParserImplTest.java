/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.internal.filter;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.CollectionPropertyExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.LambdaVariableExpression;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.NavigationPropertyExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;
import com.liferay.portal.odata.internal.filter.expression.factory.ExpressionFactoryImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author David Arques
 */
public class FilterParserImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParseNonexistingField() {
		String filterString = "(nonExistingField eq 'value')";

		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(filterString)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage(
			"A property used in the filter criteria is not supported: %s",
			filterString);
	}

	@Test
	public void testParseWithContainsMethod() throws ExpressionVisitException {
		Expression expression = _filterParserImpl.parse(
			"contains(fieldExternal, 'value')");

		Assert.assertNotNull(expression);

		MethodExpression methodExpression = (MethodExpression)expression;

		Assert.assertEquals(
			MethodExpression.Type.CONTAINS, methodExpression.getType());

		List<Expression> expressions = methodExpression.getExpressions();

		MemberExpression memberExpression = (MemberExpression)expressions.get(
			0);

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)expressions.get(1);

		Assert.assertEquals("'value'", literalExpression.getText());
	}

	@Test
	public void testParseWithContainsMethodAndBooleanType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("contains(booleanExternal, 7)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithContainsMethodAndDateType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"contains(dateExternal, 2012-05-29T09:13:28Z)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithContainsMethodAndDoubleType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("contains(doubleExternal, 7)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithEmptyFilter() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Filter is null");
	}

	@Test
	public void testParseWithEqBinaryExpressionOnCollectionField() {
		try {
			_filterParserImpl.parse("collectionFieldExternal eq 'value'");
			Assert.fail("Expected ExpressionVisitException was not thrown");
		}
		catch (ExpressionVisitException expressionVisitException) {
			Assert.assertEquals(
				"Collection not allowed.",
				expressionVisitException.getMessage());
		}
	}

	@Test
	public void testParseWithEqBinaryExpressionWithBooleanFalse()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"booleanExternal eq false");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"booleanExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("false", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.BOOLEAN, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithBooleanInvalid() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("booleanExternal eq 'invalid'")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithEqBinaryExpressionWithBooleanTrue()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"booleanExternal eq true");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"booleanExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("true", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.BOOLEAN, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDate()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"dateExternal ge 2012-05-29");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"dateExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("2012-05-29", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.DATE, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDateTimeOffset()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"dateTimeExternal ge 2012-05-29T09:13:28Z");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"dateTimeExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals(
			"2012-05-29T09:13:28Z", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.DATE_TIME, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDateTimeOffsetAndNowMethod()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"dateTimeExternal ge now()");

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"dateTimeExternal", primitivePropertyExpression.getName());

		MethodExpression methodExpression =
			(MethodExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals(
			MethodExpression.Type.NOW, methodExpression.getType());

		List<Expression> expressions = methodExpression.getExpressions();

		Assert.assertTrue(expressions.isEmpty());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDateTimeWithInvalidType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("dateTimeExternal ge 2012-05-29")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessageContaining("Incompatible types");
	}

	@Test
	public void testParseWithEqBinaryExpressionWithDateWithInvalidType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"dateExternal ge 2012-05-29T09:13:28Z")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessageContaining("Incompatible types");
	}

	@Test
	public void testParseWithEqBinaryExpressionWithMemberWithComplexProperty()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"complexField/primitiveField eq 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		ComplexPropertyExpression complexPropertyExpression =
			(ComplexPropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"complexField", complexPropertyExpression.getName());

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)
				complexPropertyExpression.getPropertyExpression();

		Assert.assertEquals(
			"primitiveField", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithNoSingleQuotes() {
		String filterString = "(fieldExternal eq value)";

		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(filterString)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage(
			"A property used in the filter criteria is not supported: %s",
			filterString);
	}

	@Test
	public void testParseWithEqBinaryExpressionWithSingleQuotes()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal eq 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithEqBinaryExpressionWithSingleQuotesAndParentheses()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"(fieldExternal eq 'value')");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.EQ, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithGeBinaryExpression()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal ge 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.GE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithGtBinaryExpressionOnCount()
		throws ExpressionVisitException {

		BinaryExpression binaryExpression =
			(BinaryExpression)_filterParserImpl.parse(
				"EntityModelName/$count gt 2");

		Assert.assertEquals(
			BinaryExpression.Operation.GT, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		NavigationPropertyExpression navigationPropertyExpression =
			(NavigationPropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			NavigationPropertyExpression.Type.COUNT,
			navigationPropertyExpression.getType());
		Assert.assertEquals(
			"EntityModelName", navigationPropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals(String.valueOf(2), literalExpression.getText());
	}

	@Test
	public void testParseWithINMethod() throws ExpressionVisitException {
		Expression expression = _filterParserImpl.parse(
			"fieldExternal in ('value1', 'value2', 'value3')");

		Assert.assertNotNull(expression);

		ListExpression listExpression = (ListExpression)expression;

		Assert.assertEquals(
			ListExpression.Operation.IN, listExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)listExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		List<Expression> rightOperationExpressions =
			listExpression.getRightOperationExpressions();

		LiteralExpression literalExpression1 =
			(LiteralExpression)rightOperationExpressions.get(0);

		Assert.assertEquals("'value1'", literalExpression1.getText());
	}

	@Test
	public void testParseWithLambdaAllOnCollectionField() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"collectionFieldExternal/all(f:contains(f,'alu'))")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage(
			"An expression cannot be obtained from URI resources " +
				"[collectionFieldExternal, all]");
	}

	@Test
	public void testParseWithLambdaAnyContainsOnCollectionField()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"collectionFieldExternal/any(f:contains(f,'alu'))");

		Assert.assertNotNull(expression);

		MemberExpression memberExpression = (MemberExpression)expression;

		CollectionPropertyExpression collectionPropertyExpression =
			(CollectionPropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"collectionFieldExternal", collectionPropertyExpression.getName());

		LambdaFunctionExpression lambdaFunctionExpression =
			collectionPropertyExpression.getLambdaFunctionExpression();

		Assert.assertEquals(
			LambdaFunctionExpression.Type.ANY,
			lambdaFunctionExpression.getType());
		Assert.assertEquals("f", lambdaFunctionExpression.getVariableName());

		MethodExpression methodExpression =
			(MethodExpression)lambdaFunctionExpression.getExpression();

		Assert.assertEquals(
			MethodExpression.Type.CONTAINS, methodExpression.getType());

		List<Expression> methodExpressionExpressions =
			methodExpression.getExpressions();

		Assert.assertNotNull(methodExpressionExpressions);
		Assert.assertEquals(
			methodExpressionExpressions.toString(), 2,
			methodExpressionExpressions.size());

		MemberExpression methodExpressionMemberExpression =
			(MemberExpression)methodExpressionExpressions.get(0);

		LambdaVariableExpression lambdaVariableExpression =
			(LambdaVariableExpression)
				methodExpressionMemberExpression.getExpression();

		Assert.assertNotNull(lambdaVariableExpression);
		Assert.assertEquals("f", lambdaVariableExpression.getVariableName());

		LiteralExpression literalExpression =
			(LiteralExpression)methodExpressionExpressions.get(1);

		Assert.assertNotNull(literalExpression);
		Assert.assertEquals("'alu'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithLambdaAnyContainsOnNoncollectionField() {
		try {
			_filterParserImpl.parse("fieldExternal/any(f:contains(f,'alu'))");
			Assert.fail("Expected ExpressionVisitException was not thrown");
		}
		catch (ExpressionVisitException expressionVisitException) {
			Assert.assertEquals(
				"Expected token 'QualifiedName' not found.",
				expressionVisitException.getMessage());
		}
	}

	@Test
	public void testParseWithLambdaAnyEqOnCollectionFieldInComplexField()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"complexField/collectionField/any(f:contains(f,'alu'))");

		Assert.assertNotNull(expression);
	}

	@Test
	public void testParseWithLambdaAnyWithNoArgumentOnCollectionField()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"collectionFieldExternal/any()");

		Assert.assertNotNull(expression);

		MemberExpression memberExpression = (MemberExpression)expression;

		CollectionPropertyExpression collectionPropertyExpression =
			(CollectionPropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"collectionFieldExternal", collectionPropertyExpression.getName());

		LambdaFunctionExpression lambdaFunctionExpression =
			collectionPropertyExpression.getLambdaFunctionExpression();

		Assert.assertNull(lambdaFunctionExpression.getExpression());
		Assert.assertEquals(
			LambdaFunctionExpression.Type.ANY,
			lambdaFunctionExpression.getType());
		Assert.assertNull(lambdaFunctionExpression.getVariableName());
	}

	@Test
	public void testParseWithLeBinaryExpression()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"fieldExternal le 'value'");

		Assert.assertNotNull(expression);

		BinaryExpression binaryExpression = (BinaryExpression)expression;

		Assert.assertEquals(
			BinaryExpression.Operation.LE, binaryExpression.getOperation());

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("'value'", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.STRING, literalExpression.getType());
	}

	@Test
	public void testParseWithNotUnaryExpressionWithEqBinaryExpression()
		throws ExpressionVisitException {

		Expression expression = _filterParserImpl.parse(
			"not (booleanExternal eq true)");

		Assert.assertNotNull(expression);

		UnaryExpression unaryExpression = (UnaryExpression)expression;

		Assert.assertEquals(
			UnaryExpression.Operation.NOT, unaryExpression.getOperation());

		BinaryExpression binaryExpression =
			(BinaryExpression)unaryExpression.getExpression();

		MemberExpression memberExpression =
			(MemberExpression)binaryExpression.getLeftOperationExpression();

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"booleanExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)binaryExpression.getRightOperationExpression();

		Assert.assertEquals("true", literalExpression.getText());
		Assert.assertEquals(
			LiteralExpression.Type.BOOLEAN, literalExpression.getType());
	}

	@Test
	public void testParseWithNullExpression() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(null)
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Filter is null");
	}

	@Test
	public void testParseWithStarsWithMethod() throws ExpressionVisitException {
		Expression expression = _filterParserImpl.parse(
			"startswith(fieldExternal, 'value')");

		Assert.assertNotNull(expression);

		MethodExpression methodExpression = (MethodExpression)expression;

		Assert.assertEquals(
			MethodExpression.Type.STARTS_WITH, methodExpression.getType());

		List<Expression> expressions = methodExpression.getExpressions();

		MemberExpression memberExpression = (MemberExpression)expressions.get(
			0);

		PrimitivePropertyExpression primitivePropertyExpression =
			(PrimitivePropertyExpression)memberExpression.getExpression();

		Assert.assertEquals(
			"fieldExternal", primitivePropertyExpression.getName());

		LiteralExpression literalExpression =
			(LiteralExpression)expressions.get(1);

		Assert.assertEquals("'value'", literalExpression.getText());
	}

	@Test
	public void testParseWithStarsWithMethodAnDoubleType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("contains(doubleExternal, 7)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithStarsWithMethodAndBooleanType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse("startswith(booleanExternal, 7)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithStarsWithMethodAndDateType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"contains(dateExternal, 2012-05-29T09:13:28Z)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	@Test
	public void testParseWithStartsWithMethodAndDateType() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _filterParserImpl.parse(
				"startswith(dateExternal, 2012-05-29T09:13:28Z)")
		).isInstanceOf(
			ExpressionVisitException.class
		);

		exception.hasMessage("Incompatible types.");
	}

	private static final FilterParserImpl _filterParserImpl =
		new FilterParserImpl(
			new EntityModel() {

				@Override
				public Map<String, EntityField> getEntityFieldsMap() {
					return HashMapBuilder.put(
						"booleanExternal",
						(EntityField)new BooleanEntityField(
							"booleanExternal", locale -> "booleanInternal")
					).put(
						"collectionFieldExternal",
						new CollectionEntityField(
							new StringEntityField(
								"collectionFieldExternal",
								locale -> "collectionFieldInternal"))
					).put(
						"complexField",
						new ComplexEntityField(
							"complexField",
							ListUtil.fromArray(
								new CollectionEntityField(
									new StringEntityField(
										"collectionField",
										locale -> "collectionFieldInternal")),
								new StringEntityField(
									"primitiveField",
									locale -> "primitiveFieldInternal")))
					).put(
						"dateExternal",
						new DateEntityField(
							"dateExternal", locale -> "dateInternal",
							locale -> "dateInternal")
					).put(
						"dateTimeExternal",
						new DateTimeEntityField(
							"dateTimeExternal", locale -> "dateTimeInternal",
							locale -> "dateTimeInternal")
					).put(
						"doubleExternal",
						new DoubleEntityField(
							"doubleExternal", locale -> "doubleInternal")
					).put(
						"fieldExternal",
						new StringEntityField(
							"fieldExternal", locale -> "fieldInternal")
					).build();
				}

				@Override
				public Map<String, EntityRelationship>
					getEntityRelationshipsMap() {

					return HashMapBuilder.put(
						"EntityModelName",
						new EntityRelationship(
							this, "EntityModelName",
							EntityRelationship.Type.COLLECTION)
					).build();
				}

				@Override
				public String getName() {
					return "SomeEntityName";
				}

			},
			new ExpressionFactoryImpl());

}