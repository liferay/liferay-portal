/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.odata.filter.expression;

import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Pedro Leite
 */
public class SOSQLExpressionVisitorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testVisitLiteralExpression() throws Exception {
		_testVisitLiteralExpression("'test'", "'test'");
		_testVisitLiteralExpression("'test\\'s'", "'test''s'");
		_testVisitLiteralExpression("'test\\\\'", "'test\\'");
	}

	private void _testVisitLiteralExpression(String expectedText, String text)
		throws Exception {

		Assert.assertEquals(
			expectedText,
			_sosqlExpressionVisitorImpl.visitLiteralExpression(
				new LiteralExpression() {

					@Override
					public <T> T accept(ExpressionVisitor<T> expressionVisitor)
						throws ExpressionVisitException {

						return expressionVisitor.visitLiteralExpression(this);
					}

					@Override
					public String getText() {
						return text;
					}

					@Override
					public Type getType() {
						return Type.STRING;
					}

				}));
	}

	private final SOSQLExpressionVisitorImpl _sosqlExpressionVisitorImpl =
		new SOSQLExpressionVisitorImpl(
			RandomTestUtil.randomLong(),
			Mockito.mock(ObjectFieldLocalService.class));

}