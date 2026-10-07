/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {FDSTableCellHTMLElementBuilder} from '@liferay/js-api/data-set';

const cellRenderer: FDSTableCellHTMLElementBuilder = ({value}) => {
	const element = document.createElement('span');

	element.textContent = `Rendered ${String(value)}`;

	return element;
};

export default cellRenderer;
