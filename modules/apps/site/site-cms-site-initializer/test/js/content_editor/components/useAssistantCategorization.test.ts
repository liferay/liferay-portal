/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook, waitFor} from '@testing-library/react';

import useAssistantCategorization from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/useAssistantCategorization';
import {
	CATEGORIZE_EVENT,
	REQUEST_CATEGORIZE_EVENT,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/components/categorizationAgentEvents';
import ObjectEntryService from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/CategorizationSuggestionService'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/content_editor/utils/getEditedContent'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService'
);

const mockGetObjectEntry = ObjectEntryService.getObjectEntry as jest.Mock;

type TCategorizationFields = Parameters<
	typeof useAssistantCategorization
>[0]['categorizationFields'];

const DRAFT_FIELDS = {
	assetCategoryIds: {
		serverValue: '39001',
		value: [{taxonomyCategoryId: 39001}],
	},
	assetTagNames: {
		serverValue: 'draft-tag',
		value: ['draft-tag'],
	},
} as TCategorizationFields;

const PERSISTED_ENTRY = {
	data: {
		contentRawText: 'persisted content',
		keywords: ['persisted-tag'],
		scopeId: 555,
		systemProperties: {
			objectDefinitionBrief: {
				classNameId: 30982,
				externalReferenceCode: 'erc',
			},
		},
		taxonomyCategoryBriefs: [{taxonomyCategoryId: 88001}],
	},
};

function renderAssistantCategorization(
	categorizationFields: TCategorizationFields
) {
	const fire = jest.fn();
	const handlers: Record<string, (payload: unknown) => void> = {};

	(global as any).Liferay.detach = jest.fn();
	(global as any).Liferay.fire = fire;
	(global as any).Liferay.on = jest.fn(
		(event: string, handler: (payload: unknown) => void) => {
			handlers[event] = handler;
		}
	);

	renderHook(() =>
		useAssistantCategorization({
			assetLibraryId: '123',
			categorizationFields,
			cmsGroupId: '456',
			contentAPIURL: 'contentAPIURL',
			onUpdateCategorization: jest.fn(),
			panel: null,
		})
	);

	return {fire, handlers};
}

describe('useAssistantCategorization', () => {
	beforeEach(() => {
		mockGetObjectEntry.mockReset();
		mockGetObjectEntry.mockResolvedValue(PERSISTED_ENTRY);
	});

	it('sends the unsaved tags from the side panel as the current tags', async () => {
		const {fire, handlers} = renderAssistantCategorization(DRAFT_FIELDS);

		handlers[REQUEST_CATEGORIZE_EVENT]({
			actions: [{agent: 'tag', count: 3, targets: []}],
		});

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				CATEGORIZE_EVENT,
				expect.objectContaining({
					agent: 'L_GENERATE_TAGS',
					currentTagNames: ['draft-tag'],
				})
			)
		);
	});

	it('keeps the sent tags when the side panel edits them in place afterwards', async () => {
		const assetTagNames = {serverValue: 'draft-tag', value: ['draft-tag']};

		const {fire, handlers} = renderAssistantCategorization({
			...DRAFT_FIELDS,
			assetTagNames,
		} as TCategorizationFields);

		handlers[REQUEST_CATEGORIZE_EVENT]({
			actions: [{agent: 'tag', count: 3, targets: []}],
		});

		await waitFor(() => expect(fire).toHaveBeenCalled());

		assetTagNames.value.splice(0, 1);

		expect(fire).toHaveBeenCalledWith(
			CATEGORIZE_EVENT,
			expect.objectContaining({currentTagNames: ['draft-tag']})
		);
	});

	it('sends the unsaved categories from the side panel as the current categories', async () => {
		const {fire, handlers} = renderAssistantCategorization(DRAFT_FIELDS);

		handlers[REQUEST_CATEGORIZE_EVENT]({
			actions: [{agent: 'categorize', count: 3, targets: []}],
		});

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				CATEGORIZE_EVENT,
				expect.objectContaining({
					agent: 'L_AUTO_CATEGORIZE',
					currentCategoryIds: [39001],
				})
			)
		);
	});

	it('falls back to the persisted entry when the side panel fields are not loaded', async () => {
		const {fire, handlers} = renderAssistantCategorization(null);

		handlers[REQUEST_CATEGORIZE_EVENT]({
			actions: [{agent: 'tag', count: 3, targets: []}],
		});

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				CATEGORIZE_EVENT,
				expect.objectContaining({
					currentTagNames: ['persisted-tag'],
				})
			)
		);
	});
});
