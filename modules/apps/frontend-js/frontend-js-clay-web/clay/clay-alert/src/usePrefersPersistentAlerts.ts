/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useProvider} from '@clayui/provider';

import {useBodyHasClass} from './useBodyHasClass';

/**
 * Returns whether alerts must stay open instead of closing automatically. It
 * is `true` when the Provider `persistentAlerts` prop is set or the
 * `c-prefers-persistent-alerts` class is present on the body.
 */
export function usePrefersPersistentAlerts() {
	const {persistentAlerts} = useProvider();

	const bodyPrefersPersistentAlerts = useBodyHasClass(
		'c-prefers-persistent-alerts'
	);

	return persistentAlerts || bodyPrefersPersistentAlerts;
}
