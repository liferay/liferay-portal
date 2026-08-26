/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const formatVariablesForTextarea = (raw) => {
	if (raw === undefined || raw === null) {
		return '';
	}

	if (typeof raw === 'object') {
		try {
			return JSON.stringify(raw, null, 2);
		}
		catch (error) {
			return String(raw);
		}
	}

	return String(raw);
};

const parseVariablesInput = (text) => {
	if (!text.trim()) {
		return undefined;
	}

	try {
		return JSON.parse(text);
	}
	catch (error) {
		return text;
	}
};

export {formatVariablesForTextarea, parseVariablesInput};
