/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.pql;

import java.util.Properties;
import java.util.Set;

/**
 * @author Michael Hashimoto
 */
public abstract class PQLEntity {

	public static String fixPQL(String pql) {
		while (true) {
			pql = pql.trim();

			if (!pql.startsWith("(") || !pql.endsWith(")")) {
				break;
			}

			String subpql = pql.substring(1, pql.length() - 1);

			int parenthesisCount = 0;

			for (int i = 0; i < subpql.length(); i++) {
				char c = subpql.charAt(i);

				if (c == '(') {
					parenthesisCount++;
				}

				if (c != ')') {
					continue;
				}

				if (parenthesisCount < 1) {
					return pql.trim();
				}

				parenthesisCount--;
			}

			if (parenthesisCount > 0) {
				return pql.trim();
			}

			pql = subpql;
		}

		return pql.trim();
	}

	public static String removeModifierFromPQL(String pql) {
		pql = fixPQL(pql);

		String modifier = _getModifierFromPQL(pql);

		if (modifier != null) {
			pql = pql.substring(modifier.length());
		}

		return pql.trim();
	}

	public PQLEntity(String pql) throws Exception {
		if (pql != null) {
			pql = fixPQL(pql);

			_setModifierFromPQL(pql);

			pql = removeModifierFromPQL(pql);
		}

		_pql = pql;
	}

	public PQLModifier getPQLModifier() {
		return _pqlModifier;
	}

	public abstract Object getPQLResult(Properties properties) throws Exception;

	protected String getPQL() {
		return _pql;
	}

	private static String _getModifierFromPQL(String pql) {
		pql = fixPQL(pql);

		Set<String> availableModifiers = PQLModifier.getAvailableModifiers();

		for (String modifier : availableModifiers) {
			if (pql.startsWith(modifier)) {
				return modifier;
			}
		}

		return null;
	}

	private void _setModifierFromPQL(String pql) throws Exception {
		pql = fixPQL(pql);

		String modifier = _getModifierFromPQL(pql);

		if (modifier != null) {
			_pqlModifier = PQLModifierFactory.newPQLModifier(modifier);
		}
		else {
			_pqlModifier = null;
		}
	}

	private final String _pql;
	private PQLModifier _pqlModifier;

}