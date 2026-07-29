/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CategorizeEventPayload} from '../Categorization/events';
import {ContentType} from './components/ContentTypeSelectorMessageBalloon';

export interface HttpRequestAction {
	body?: Record<string, unknown>;
	href: string;
	method: string;
}

export interface AgentComponentOption {
	action: {'http-request': HttpRequestAction};
	label: string;
}

export interface AgentComponent {
	options: AgentComponentOption[];
	title: string;
	type: 'select';
}

export interface Message {
	agentDefinitionExternalReferenceCodes?: string[];
	categorization?: CategorizeEventPayload;
	component?: AgentComponent;
	contentTypes?: ContentType[];
	error?: boolean;
	images?: string[];
	sender: string;
	text: string;
}

export interface ChatMessageSentData {
	agentDefinitionExternalReferenceCodes?: string[];
	component?: AgentComponent;
	data?: string;
	mimeType?: string;
	type?: string;
}
