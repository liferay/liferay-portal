/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CategorizeEventPayload} from '../Categorization/events';

export interface Message {
	agentDefinitionExternalReferenceCodes?: string[];
	categorization?: CategorizeEventPayload;
	error?: boolean;
	images?: string[];
	sender: string;
	text: string;
}

export interface ChatMessageSentData {
	agentDefinitionExternalReferenceCodes?: string[];
	data?: string;
	mimeType?: string;
	type?: string;
}
