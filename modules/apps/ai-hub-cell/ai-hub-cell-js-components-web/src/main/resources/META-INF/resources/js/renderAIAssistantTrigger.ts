/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import AIAssistantTriggerButton from './AIAssistantChat/AIAssistantTriggerButton';

import type {ComponentProps} from 'react';

export default function renderAIAssistantTrigger(
	container: Element,
	props: ComponentProps<typeof AIAssistantTriggerButton>
): void {
	render(AIAssistantTriggerButton, props, container);
}
