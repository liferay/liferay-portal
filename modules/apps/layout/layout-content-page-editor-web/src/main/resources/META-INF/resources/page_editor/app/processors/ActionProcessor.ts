/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getAlloyEditorProcessor} from '../js-index';
import getCKEditorProcessor from './getCKEditorProcessor';

const processor = Liferay.FeatureFlags['LPD-11235']
	? getCKEditorProcessor
	: getAlloyEditorProcessor;

export default processor('text');
