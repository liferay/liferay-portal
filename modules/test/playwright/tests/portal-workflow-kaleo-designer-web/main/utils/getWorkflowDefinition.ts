/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {readFileSync} from 'fs';
import * as path from 'path';

type fileNames =
	| 'basic'
	| 'bcc-reception-type'
	| 'same-source-transitions'
	| 'sample-client-extension';

function getXMLContentFromFile(fileName: string) {
	const absolutePath = path.resolve(__dirname, `../dependencies/${fileName}`);

	return readFileSync(absolutePath, 'utf-8');
}

export function getWorkflowDefinition(name: fileNames) {
	return {
		active: true,
		content: getXMLContentFromFile(`${name}-workflow-definition.xml`),
	};
}
