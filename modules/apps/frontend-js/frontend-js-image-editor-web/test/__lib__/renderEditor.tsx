/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {RenderResult, render} from '@testing-library/react';
import React from 'react';

import {AnnouncerProvider} from '../../src/main/resources/META-INF/resources/js/chrome/Announcer';
import {EditorInstanceProvider} from '../../src/main/resources/META-INF/resources/js/chrome/instance';

export function renderEditor(ui: React.ReactElement): RenderResult {
	return render(
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<AnnouncerProvider>
				<EditorInstanceProvider value="aie-">
					{ui}
				</EditorInstanceProvider>
			</AnnouncerProvider>
		</ClayIconSpriteContext.Provider>
	);
}
