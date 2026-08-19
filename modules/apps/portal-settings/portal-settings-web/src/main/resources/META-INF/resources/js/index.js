/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CountryRegionDynamicSelect} from '@liferay/address-web';
import {autoFields} from 'frontend-js-web/auto_fields';

function main({namespace}) {
	autoFields({
		contentBox: `#${namespace}addresses`,
		fieldIndexes: `${namespace}addressesIndexes`,
		namespace,
		on: {
			clone(event) {

				// The row is produced with cloneNode, which does not carry
				// listeners over, so the change listener the original select
				// had is already gone.

				CountryRegionDynamicSelect.default({
					countrySelect: `${namespace}addressCountryId${event.guid}`,
					regionSelect: `${namespace}addressRegionId${event.guid}`,
				});
			},
		},
	});
}

export {CountryRegionDynamicSelect, main};
