/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ElementVariation} from './elementVariationsReducer';

export default function getAvailableAudiences(
	audiences: Array<{label: string; value: string}>,
	elementVariations: ElementVariation[],
	elementVariation: Pick<ElementVariation, 'key' | 'targetElement'>
): Array<{label: string; value: string}> {
	const unavailableAudienceEntryERCs = new Set(
		elementVariations
			.filter(
				(siblingElementVariation) =>
					siblingElementVariation.key !== elementVariation.key &&
					siblingElementVariation.targetElement ===
						elementVariation.targetElement
			)
			.flatMap(
				(siblingElementVariation) =>
					siblingElementVariation.audienceEntryERCs
			)
	);

	return audiences.filter(
		(audience) => !unavailableAudienceEntryERCs.has(audience.value)
	);
}
