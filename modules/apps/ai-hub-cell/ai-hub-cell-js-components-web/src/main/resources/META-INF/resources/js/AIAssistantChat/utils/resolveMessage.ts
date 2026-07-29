/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CategorizeEventPayload} from '../../Categorization/events';
import {Result} from '../../TranslateContent/types';
import {ContentType} from '../components/ContentTypeSelectorMessageBalloon';
import {GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE} from '../events';
import {AgentComponent, Message} from '../types';
import getGeneratedFieldValues from './getGeneratedFieldValues';
import parseContentDraftsMessage from './parseContentDraftsMessage';

export interface TranslateMessage {
	agentInstanceId: number;
	availableLanguageIds?: string[];
	results?: Result[];
	targetLanguageIds?: string[];
}

export type ResolvedMessage =
	| {categorization: CategorizeEventPayload; type: 'categorization'}
	| {component: AgentComponent; type: 'select-component'}
	| {contentTypes: ContentType[]; type: 'content-types'}
	| {fieldValues: Record<string, string>; type: 'field-values'}
	| {images: string[]; type: 'images'}
	| {translate: TranslateMessage; type: 'translate'}
	| {type: 'assistant'}
	| {type: 'content-drafts'}
	| {type: 'user'};

function getFieldValues(item: Message): Record<string, string> {
	if (
		item.error ||
		!item.agentDefinitionExternalReferenceCodes?.includes(
			GENERATE_FIELD_VALUE_AGENT_EXTERNAL_REFERENCE_CODE
		)
	) {
		return {};
	}

	return getGeneratedFieldValues(item.text);
}

function parseTranslateMessage(text: string): TranslateMessage | null {
	try {
		const json = JSON.parse(
			text
				.trim()
				.replace(/^```(?:json)?/i, '')
				.replace(/```$/, '')
				.trim()
		);

		if (json?.action === 'translate') {
			return json;
		}
	}
	catch {}

	return null;
}

export default function resolveMessage(item: Message): ResolvedMessage {
	if (item.component?.type === 'select') {
		return {component: item.component, type: 'select-component'};
	}

	if (item.sender === 'user') {
		return {type: 'user'};
	}

	if (item.categorization) {
		return {categorization: item.categorization, type: 'categorization'};
	}

	if (item.images?.length) {
		return {images: item.images, type: 'images'};
	}

	if (item.contentTypes) {
		return {contentTypes: item.contentTypes, type: 'content-types'};
	}

	if (parseContentDraftsMessage(item.text).drafts.length) {
		return {type: 'content-drafts'};
	}

	const translate = parseTranslateMessage(item.text);

	if (translate) {
		return {translate, type: 'translate'};
	}

	const fieldValues = getFieldValues(item);

	if (Object.keys(fieldValues).length) {
		return {fieldValues, type: 'field-values'};
	}

	return {type: 'assistant'};
}
