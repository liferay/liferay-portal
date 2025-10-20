/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class CreatingThreadsForDBAccessCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_NEW, TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.LITERAL_NEW) {
			_checkDBAccessByNewClassInstantiation(detailAST);
		}
		else {
			_checkDBAccessByMethodCall(detailAST);
		}
	}

	private void _checkDBAccess(DetailAST detailAST) {
		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		DetailAST firstChildDetailAST = elistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.LAMBDA)) {

			return;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			firstChildDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			List<String> names = getNames(dotDetailAST, false);

			if (names.size() != 2) {
				continue;
			}

			String methodCallClassName = names.get(0);

			if (!methodCallClassName.equals("CompanyThreadLocal") &&
				!methodCallClassName.endsWith("LocalService") &&
				!methodCallClassName.endsWith("LocalServiceUtil")) {

				continue;
			}

			log(detailAST, _MSG_USE_COMPANY_INHERITABLE_THREAD_LOCAL_CALLABLE);
		}
	}

	private void _checkDBAccessByMethodCall(DetailAST detailAST) {
		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST == null) {
			return;
		}

		List<String> names = getNames(dotDetailAST, false);

		if (names.size() != 2) {
			return;
		}

		String methodCallClassName = names.get(0);
		String methodCallMethodName = names.get(1);

		if (methodCallClassName.equals("DependencyManagerSyncUtil") &&
			methodCallMethodName.equals("registerSyncCallable")) {

			_checkDBAccess(detailAST);

			return;
		}

		if (Character.isUpperCase(methodCallClassName.charAt(0)) ||
			!methodCallMethodName.equals("submit")) {

			return;
		}

		String variableTypeName = getVariableTypeName(
			detailAST, methodCallClassName, false);

		if ((variableTypeName == null) ||
			!variableTypeName.endsWith("ExecutorService")) {

			return;
		}

		_checkDBAccess(detailAST);
	}

	private void _checkDBAccessByNewClassInstantiation(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.IDENT)) {

			return;
		}

		String className = firstChildDetailAST.getText();

		if (!className.equals("FutureTask") && !className.equals("Thread")) {
			return;
		}

		_checkDBAccess(detailAST);
	}

	private static final String
		_MSG_USE_COMPANY_INHERITABLE_THREAD_LOCAL_CALLABLE =
			"company.inheritable.thread.local.callable.use";

}