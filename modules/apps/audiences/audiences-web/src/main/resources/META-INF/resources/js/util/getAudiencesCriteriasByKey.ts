/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria, AudiencesCriteriaType} from '../types';

export function getAudiencesCriteriasByKey(
	audiencesCriteriaTypes: AudiencesCriteriaType[]
): Record<string, AudiencesCriteria> {
	return Object.fromEntries(
		audiencesCriteriaTypes
			.flatMap(
				(audiencesCriteriaType) =>
					audiencesCriteriaType.audiencesCriterias
			)
			.map((audiencesCriteria) => [
				audiencesCriteria.key,
				audiencesCriteria,
			])
	);
}
