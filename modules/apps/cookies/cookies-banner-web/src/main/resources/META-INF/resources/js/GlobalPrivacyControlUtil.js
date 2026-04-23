/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

let _serverSignalActive = null;

export function initGlobalPrivacyControl(serverSignalActive) {
	_serverSignalActive = Boolean(serverSignalActive);
}

export function isGlobalPrivacyControlSignalActive() {
	if (_serverSignalActive === null) {
		return false;
	}

	return _serverSignalActive || navigator.globalPrivacyControl === true;
}
