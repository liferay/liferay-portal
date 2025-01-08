/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';

export default async function getLayoutTemplateByName(
	layoutSetPrototypes: LayoutSetPrototype[],
	targetName: string
): Promise<LayoutSetPrototype> {
	const targetLayout = layoutSetPrototypes.find(
		(layoutSetPrototype) =>
			layoutSetPrototype.nameCurrentValue === targetName
	);

	if (targetLayout) {
		return {
			layoutSetPrototypeId: targetLayout.layoutSetPrototypeId,
			nameCurrentValue: targetLayout.nameCurrentValue,
			uuid: targetLayout.uuid,
		};
	}
	else {
		return {
			layoutSetPrototypeId: undefined,
			nameCurrentValue: undefined,
			uuid: undefined,
		};
	}
}
