/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Combines several disposer functions into a single one. Calling the returned
 * function runs every disposer, so a plugin can register its listeners as a
 * list and tear them all down at once.
 */
function composeDisposers(disposers: (() => void)[]) {
	return () => disposers.forEach((dispose) => dispose());
}

export {composeDisposers};
export default composeDisposers;
