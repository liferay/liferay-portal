/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getProfileURL from './getProfileURL';
import {openSuccessToast} from './toasts';

export default async function copyProfileURL(
	profilePath: string
): Promise<void> {
	await navigator.clipboard.writeText(getProfileURL(profilePath));

	openSuccessToast(Liferay.Language.get('copied-to-clipboard'));
}
