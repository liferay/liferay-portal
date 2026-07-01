/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getCategoricalColors} from '../../palette';
import {PieDatum} from '../types/PieDatum';

export function getPieSliceColors(data: PieDatum[]): string[] {
	return getCategoricalColors(data.length).map(
		(color, index) => data[index].color || color
	);
}
