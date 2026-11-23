/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {RemoteInstanceFormValues} from '../types/remoteInstance';

export type RemoteInstanceErrors = Partial<
	Record<keyof RemoteInstanceFormValues, string>
>;

const MAX_PORT = 65535;

export default function validateRemoteInstance(
	values: RemoteInstanceFormValues,
	clientSecretStored: boolean
): RemoteInstanceErrors {
	const errors: RemoteInstanceErrors = {};

	const requiredMessage = Liferay.Language.get('this-field-is-required');

	if (!values.name.trim()) {
		errors.name = requiredMessage;
	}

	if (!values.hostname.trim()) {
		errors.hostname = requiredMessage;
	}

	if (!values.clientId.trim()) {
		errors.clientId = requiredMessage;
	}

	if (!clientSecretStored && !values.clientSecret) {
		errors.clientSecret = requiredMessage;
	}

	const port = values.port.trim();

	if (port) {
		const portNumber = Number(port);

		if (!/^\d+$/.test(port) || portNumber < 1 || portNumber > MAX_PORT) {
			errors.port = sub(
				Liferay.Language.get('the-remote-port-x-is-not-valid'),
				port
			);
		}
	}

	return errors;
}
