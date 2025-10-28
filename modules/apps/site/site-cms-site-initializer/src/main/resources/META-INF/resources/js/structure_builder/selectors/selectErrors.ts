/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../contexts/StateContext';
import {Uuid} from '../types/Uuid';
import findChild from '../utils/findChild';
import {ValidationProperty, getErrorMessage} from '../utils/validation';

export default function selectErrors(uuid: Uuid) {
	return (state: State) => {
		const errors = state.invalids.get(uuid);

		const messages = new Map<ValidationProperty, string>();

		if (!errors) {
			return messages;
		}

		let item;

		if (uuid === state.structure.uuid) {
			item = state.structure;
		}
		else {
			item = findChild({root: state.structure, uuid});
		}

		if (!item) {
			return messages;
		}

		const {erc, name} = item;

		for (const [property, error] of errors.entries()) {
			const message = getErrorMessage(property, error, {erc, name});

			if (message) {
				messages.set(property, message);
			}
		}

		return messages;
	};
}
