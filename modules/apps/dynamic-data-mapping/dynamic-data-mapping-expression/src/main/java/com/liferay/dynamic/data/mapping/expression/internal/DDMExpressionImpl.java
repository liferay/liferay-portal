/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.expression.internal;

import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionActionHandler;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionObserver;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionParameterAccessor;
import com.liferay.dynamic.data.mapping.expression.internal.parser.DDMExpressionDSLExpressionVisitor;
import com.liferay.dynamic.data.mapping.expression.internal.parser.DDMExpressionEvaluatorVisitor;
import com.liferay.dynamic.data.mapping.expression.internal.parser.DDMExpressionModelVisitor;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionLexer;
import com.liferay.dynamic.data.mapping.expression.internal.parser.generated.DDMExpressionParser;
import com.liferay.dynamic.data.mapping.expression.model.Expression;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author Miguel Angelo Caldas Gallindo
 * @author Marcellus Tavares
 */
public class DDMExpressionImpl<T> implements DDMExpression<T> {

	@Override
	public T evaluate() throws DDMExpressionException {
		try {
			DDMExpressionEvaluatorVisitor ddmExpressionEvaluatorVisitor =
				new DDMExpressionEvaluatorVisitor(
					_ddmExpressionActionHandler, _ddmExpressionFieldAccessor,
					_ddmExpressionFunctionFactories, _ddmExpressionObserver,
					_ddmExpressionParameterAccessor, _variables);

			return (T)_expressionContext.accept(ddmExpressionEvaluatorVisitor);
		}
		catch (Exception exception) {
			throw new DDMExpressionException(exception);
		}
	}

	@Override
	public com.liferay.petra.sql.dsl.expression.Expression<?> getDSLExpression()
		throws DDMExpressionException {

		try {
			DDMExpressionDSLExpressionVisitor
				ddmExpressionDSLExpressionVisitor =
					new DDMExpressionDSLExpressionVisitor(_variables);

			return (com.liferay.petra.sql.dsl.expression.Expression<?>)
				_expressionContext.accept(ddmExpressionDSLExpressionVisitor);
		}
		catch (Exception exception) {
			throw new DDMExpressionException(exception);
		}
	}

	@Override
	public Expression getModel() {
		return _expressionContext.accept(new DDMExpressionModelVisitor());
	}

	@Override
	public void setVariable(String name, Object value) {
		if (value instanceof Number) {
			value = new BigDecimal(value.toString());
		}

		_variables.put(name, value);
	}

	@Override
	public void setVariables(Map<String, Object> variables) {
		_variables.putAll(variables);
	}

	protected DDMExpressionImpl(
			DDMExpressionFunctionRegistry ddmExpressionFunctionRegistry,
			String expression)
		throws DDMExpressionException {

		if ((expression == null) || expression.isEmpty()) {
			throw new IllegalArgumentException();
		}

		DDMExpressionLexer ddmExpressionLexer = new DDMExpressionLexer(
			new ANTLRInputStream(expression));

		ddmExpressionLexer.removeErrorListener(ConsoleErrorListener.INSTANCE);

		DDMExpressionParser ddmExpressionParser = new DDMExpressionParser(
			new CommonTokenStream(ddmExpressionLexer));

		ddmExpressionParser.removeErrorListener(ConsoleErrorListener.INSTANCE);

		ddmExpressionParser.setErrorHandler(new BailErrorStrategy());

		try {
			_expressionContext = ddmExpressionParser.expression();
		}
		catch (Exception exception) {
			throw new DDMExpressionException.InvalidSyntax(exception);
		}

		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();

		DDMExpressionListener ddmExpressionListener =
			new DDMExpressionListener();

		parseTreeWalker.walk(ddmExpressionListener, _expressionContext);

		Set<String> ddmExpressionFunctionNames =
			ddmExpressionListener.getFunctionNames();

		_ddmExpressionFunctionFactories =
			ddmExpressionFunctionRegistry.getDDMExpressionFunctionFactories(
				ddmExpressionFunctionNames);

		Set<String> undefinedDDMExpressionFunctionNames = new HashSet<>(
			ddmExpressionFunctionNames);

		undefinedDDMExpressionFunctionNames.removeAll(
			_ddmExpressionFunctionFactories.keySet());

		if (!undefinedDDMExpressionFunctionNames.isEmpty()) {
			throw new DDMExpressionException.FunctionNotDefined(
				undefinedDDMExpressionFunctionNames);
		}

		for (String variableName : ddmExpressionListener.getVariableNames()) {
			_variables.put(variableName, null);
		}
	}

	protected Set<String> getExpressionVariableNames() {
		return _variables.keySet();
	}

	protected void setDDMExpressionActionHandler(
		DDMExpressionActionHandler ddmExpressionActionHandler) {

		_ddmExpressionActionHandler = ddmExpressionActionHandler;
	}

	protected void setDDMExpressionFieldAccessor(
		DDMExpressionFieldAccessor ddmExpressionFieldAccessor) {

		_ddmExpressionFieldAccessor = ddmExpressionFieldAccessor;
	}

	protected void setDDMExpressionObserver(
		DDMExpressionObserver ddmExpressionObserver) {

		_ddmExpressionObserver = ddmExpressionObserver;
	}

	protected void setDDMExpressionParameterAccessor(
		DDMExpressionParameterAccessor ddmExpressionParameterAccessor) {

		_ddmExpressionParameterAccessor = ddmExpressionParameterAccessor;
	}

	private DDMExpressionActionHandler _ddmExpressionActionHandler;
	private DDMExpressionFieldAccessor _ddmExpressionFieldAccessor;
	private final Map<String, DDMExpressionFunctionFactory>
		_ddmExpressionFunctionFactories;
	private DDMExpressionObserver _ddmExpressionObserver;
	private DDMExpressionParameterAccessor _ddmExpressionParameterAccessor;
	private final DDMExpressionParser.ExpressionContext _expressionContext;
	private final Map<String, Object> _variables = new HashMap<>();

}