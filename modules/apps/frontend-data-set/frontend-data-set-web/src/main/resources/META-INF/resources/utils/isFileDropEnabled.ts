/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFileDropSettings} from '../index';

const isFileDropEnabled = (fileDropSettings: IFileDropSettings): boolean => {
	return fileDropSettings && fileDropSettings.enabled;
};

export default isFileDropEnabled;
