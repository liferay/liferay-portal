/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import AIAssistantChat from './AIAssistantChat/AIAssistantChat';

import type {ComponentProps} from 'react';

export default function renderAIAssistantChat(
	container: Element,
	props: ComponentProps<typeof AIAssistantChat>
): void {

	// @ts-ignore

	render(AIAssistantChat, props, container);
}
