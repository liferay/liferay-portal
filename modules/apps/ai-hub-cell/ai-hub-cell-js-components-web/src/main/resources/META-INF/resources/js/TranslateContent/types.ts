/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MutableRefObject} from 'react';

export interface Result {
	fields?: Record<string, string>;
	targetLanguageId: string;
}

export interface TranslateContentMessageBalloonProps {
	agentInstanceId: number;
	availableLanguageIds?: string[];
	requestedLanguageIds?: string[];
	results?: Result[];
	setIsGenerating: (isGenerating: boolean) => void;
	sourceLanguageIdRef: MutableRefObject<string>;
}
