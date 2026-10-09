/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.expression.internal.parser;

import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionBaseVisitor;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.AdditionExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.BooleanParenthesisContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.DivisionExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.EqualsExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.FloatingPointLiteralContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.FunctionCallExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.GreaterThanExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.GreaterThanOrEqualsExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.LessThanExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.LessThanOrEqualsExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.LogicalConstantContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.LogicalVariableContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.MinusExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.MultiplicationExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.NotEqualsExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.NumericParenthesisContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.NumericVariableContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.SubtractionExpressionContext;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser.ToFloatingPointArrayContext;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.NullExpression;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * @author Guilherme Camacho
 */
public class DDMExpressionDSLExpressionVisitor
	extends DDMExpressionBaseVisitor<Object> {

	public DDMExpressionDSLExpressionVisitor(Map<String, Object> variables) {
		_variables = variables;
	}

	@Override
	public Object visitAdditionExpression(
		@NotNull AdditionExpressionContext additionExpressionContext) {

		Expression<Number> expression1 = (Expression<Number>)_getExpression(
			visitChild(additionExpressionContext, 0));
		Expression<Number> expression2 = (Expression<Number>)_getExpression(
			visitChild(additionExpressionContext, 2));

		return DSLFunctionFactoryUtil.add(expression1, expression2);
	}

	@Override
	public Object visitAndExpression(
		@NotNull DDMExpressionParser.AndExpressionContext
			andExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitAndExpression with and expression " +
				andExpressionContext.getText());
	}

	@Override
	public Object visitBooleanParenthesis(
		@NotNull BooleanParenthesisContext booleanParenthesisContext) {

		return visitChild(booleanParenthesisContext, 1);
	}

	@Override
	public Object visitDivisionExpression(
		@NotNull DivisionExpressionContext divisionExpressionContext) {

		Expression<Number> expression1 = (Expression<Number>)_getExpression(
			visitChild(divisionExpressionContext, 0));
		Expression<Number> expression2 = (Expression<Number>)_getExpression(
			visitChild(divisionExpressionContext, 2));

		return DSLFunctionFactoryUtil.caseWhenThen(
			expression2.eq(new Scalar<>(0)),
			(Expression<Float>)(Expression<?>)NullExpression.INSTANCE
		).whenThen(
			expression2.isNull(),
			(Expression<Float>)(Expression<?>)NullExpression.INSTANCE
		).elseEnd(
			DSLFunctionFactoryUtil.floatDivide(expression1, expression2)
		);
	}

	@Override
	public Object visitEqualsExpression(
		@NotNull EqualsExpressionContext equalsExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitEqualsExpression with equals expression " +
				equalsExpressionContext.getText());
	}

	@Override
	public Object visitExpression(
		@NotNull DDMExpressionParser.ExpressionContext expressionContext) {

		DDMExpressionParser.LogicalOrExpressionContext
			logicalOrExpressionContext =
				expressionContext.logicalOrExpression();

		return logicalOrExpressionContext.accept(this);
	}

	@Override
	public Object visitFloatingPointLiteral(
		@NotNull FloatingPointLiteralContext floatingPointLiteralContext) {

		return new BigDecimal(floatingPointLiteralContext.getText());
	}

	@Override
	public Object visitFunctionCallExpression(
		@NotNull FunctionCallExpressionContext functionCallExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitFunctionCallExpression with function " +
				"call expression " + functionCallExpressionContext.getText());
	}

	@Override
	public Object visitGreaterThanExpression(
		@NotNull GreaterThanExpressionContext greaterThanExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitGreaterThanExpression with greater than " +
				"expression " + greaterThanExpressionContext.getText());
	}

	@Override
	public Object visitGreaterThanOrEqualsExpression(
		@NotNull GreaterThanOrEqualsExpressionContext
			greaterThanOrEqualsExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitGreaterThanOrEqualsExpression with " +
				"greater than or equals expression " +
					greaterThanOrEqualsExpressionContext.getText());
	}

	@Override
	public Object visitIntegerLiteral(
		@NotNull DDMExpressionParser.IntegerLiteralContext
			integerLiteralContext) {

		return new BigDecimal(integerLiteralContext.getText());
	}

	@Override
	public Object visitLessThanExpression(
		@NotNull LessThanExpressionContext lessThanExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitLessThanExpression with less than " +
				"expression " + lessThanExpressionContext.getText());
	}

	@Override
	public Object visitLessThanOrEqualsExpression(
		@NotNull LessThanOrEqualsExpressionContext
			lessThanOrEqualsExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitLessThanOrEqualsExpression with less " +
				"than or equals expression " +
					lessThanOrEqualsExpressionContext.getText());
	}

	@Override
	public Object visitLogicalConstant(
		@NotNull LogicalConstantContext logicalConstantContext) {

		return Boolean.parseBoolean(logicalConstantContext.getText());
	}

	@Override
	public Object visitLogicalVariable(
		@NotNull LogicalVariableContext logicalVariableContext) {

		String variable = logicalVariableContext.getText();

		Object variableValue = _variables.get(variable);

		if ((variableValue == null) && (_variables.size() > 1)) {
			for (Map.Entry<String, Object> entry : _variables.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (!key.startsWith(variable) || (value == null)) {
					continue;
				}

				variableValue = value;
			}
		}

		if (variableValue == null) {
			throw new IllegalStateException(
				String.format("Variable \"%s\" not defined", variable));
		}

		return variableValue;
	}

	@Override
	public Object visitMinusExpression(
		@NotNull MinusExpressionContext minusExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitMinusExpression with minus expression " +
				minusExpressionContext.getText());
	}

	@Override
	public Object visitMultiplicationExpression(
		@NotNull MultiplicationExpressionContext
			multiplicationExpressionContext) {

		Expression<Number> expression1 = (Expression<Number>)_getExpression(
			visitChild(multiplicationExpressionContext, 0));
		Expression<Number> expression2 = (Expression<Number>)_getExpression(
			visitChild(multiplicationExpressionContext, 2));

		return DSLFunctionFactoryUtil.multiply(expression1, expression2);
	}

	@Override
	public Object visitNotEqualsExpression(
		@NotNull NotEqualsExpressionContext notEqualsExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitNotEqualsExpression with not equals " +
				"expression " + notEqualsExpressionContext.getText());
	}

	@Override
	public Object visitNotExpression(
		@NotNull DDMExpressionParser.NotExpressionContext
			notExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitNotExpression with not expression " +
				notExpressionContext.getText());
	}

	@Override
	public Object visitNumericParenthesis(
		@NotNull NumericParenthesisContext numericParenthesisContext) {

		Expression<Number> expression = (Expression<Number>)_getExpression(
			visitChild(numericParenthesisContext, 1));

		if (expression instanceof DSLFunction) {
			DSLFunction<Number> dslFunction = (DSLFunction<Number>)expression;

			return DSLFunctionFactoryUtil.withParentheses(dslFunction);
		}

		return expression;
	}

	@Override
	public Object visitNumericVariable(
		@NotNull NumericVariableContext numericVariableContext) {

		String variable = numericVariableContext.getText();

		Object variableValue = _variables.get(variable);

		if (variableValue == null) {
			throw new IllegalStateException(
				String.format("Variable \"%s\" not defined", variable));
		}

		return variableValue;
	}

	@Override
	public Object visitOrExpression(
		@NotNull DDMExpressionParser.OrExpressionContext orExpressionContext) {

		throw new UnsupportedOperationException(
			"Unsupported method visitOrExpression with or expression " +
				orExpressionContext.getText());
	}

	@Override
	public Object visitStringLiteral(
		@NotNull DDMExpressionParser.StringLiteralContext
			stringLiteralContext) {

		return StringUtil.unquote(stringLiteralContext.getText());
	}

	@Override
	public Object visitSubtractionExpression(
		@NotNull SubtractionExpressionContext subtractionExpressionContext) {

		Expression<Number> expression1 = (Expression<Number>)_getExpression(
			visitChild(subtractionExpressionContext, 0));
		Expression<Number> expression2 = (Expression<Number>)_getExpression(
			visitChild(subtractionExpressionContext, 2));

		return DSLFunctionFactoryUtil.subtract(expression1, expression2);
	}

	@Override
	public Object visitToFloatingPointArray(
		ToFloatingPointArrayContext toFloatingPointArrayContext) {

		return _getBigDecimalArray(
			toFloatingPointArrayContext.FloatingPointLiteral());
	}

	@Override
	public Object visitToIntegerArray(
		DDMExpressionParser.ToIntegerArrayContext toIntegerArrayContext) {

		return _getBigDecimalArray(toIntegerArrayContext.IntegerLiteral());
	}

	@Override
	public Object visitToStringArray(
		DDMExpressionParser.ToStringArrayContext toStringArrayContext) {

		List<TerminalNode> stringTerminalNodes = toStringArrayContext.STRING();

		String[] values = new String[stringTerminalNodes.size()];

		for (int i = 0; i < stringTerminalNodes.size(); i++) {
			TerminalNode terminalNode = stringTerminalNodes.get(i);

			values[i] = StringUtil.unquote(terminalNode.getText());
		}

		return values;
	}

	protected <T> T visitChild(
		ParserRuleContext parserRuleContext, int childIndex) {

		ParseTree parseTree = parserRuleContext.getChild(childIndex);

		return (T)parseTree.accept(this);
	}

	private BigDecimal _getBigDecimal(Comparable<?> comparable) {
		if (comparable == null) {
			return BigDecimal.ZERO;
		}

		if (comparable instanceof BigDecimal) {
			return (BigDecimal)comparable;
		}

		String value = comparable.toString();

		if (Validator.isNull(value)) {
			return BigDecimal.ZERO;
		}

		return new BigDecimal(value);
	}

	private BigDecimal[] _getBigDecimalArray(List<TerminalNode> terminalNodes) {
		BigDecimal[] values = new BigDecimal[terminalNodes.size()];

		for (int i = 0; i < terminalNodes.size(); i++) {
			TerminalNode terminalNode = terminalNodes.get(i);

			values[i] = new BigDecimal(terminalNode.getText());
		}

		return values;
	}

	private Expression<?> _getExpression(Object object) {
		if (object instanceof BigDecimal) {
			object = _getBigDecimal((Comparable<?>)object);
		}
		else if (object instanceof Expression) {
			return (Expression<?>)object;
		}

		return new Scalar<>(object);
	}

	private final Map<String, Object> _variables;

}