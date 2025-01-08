/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../../../helpers/ApiHelpers';

export default async function deleteLayoutSetPrototype(
	apiHelpers: ApiHelpers,
	layoutSetPrototypeId: string
) {
	await apiHelpers.jsonWebServicesLayoutSetPrototype.deleteLayoutSetPrototypes(
		layoutSetPrototypeId
	);
}
