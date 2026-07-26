/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AutofixDefinition} from './AutofixDefinition';
import {TITLE_AUTOFIX_DEFINITION} from './TitleAutofixDefinition';

export const AUTOFIX_DEFINITIONS: Record<string, AutofixDefinition> = {
	missingOrEmptyTitleTag: TITLE_AUTOFIX_DEFINITION,
};
