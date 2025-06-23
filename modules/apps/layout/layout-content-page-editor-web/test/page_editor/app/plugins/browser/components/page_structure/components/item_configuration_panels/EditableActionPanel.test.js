/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {State} from '@liferay/frontend-js-state-web';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableTypes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {pageContentsAtom} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import EditableActionPanel from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/EditableActionPanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableLanguages: {
				en_US: {
					default: false,
					displayName: 'English (United States)',
					languageIcon: 'en-us',
					languageId: 'en_US',
					w3cLanguageId: 'en-US',
				},
			},
			selectedMappingTypes: {
				subtype: {
					id: 'subtype',
				},
				type: {
					id: 'type',
				},
			},
		},
	})
);

const MAPPED_ACTION = {
	classNameId: 'classNameId',
	classPK: 'classPK',
};

function getStateWithConfig(config = {}) {
	return {
		fragmentEntryLinks: {
			0: {
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
						'editable-id-0': {
							config,
						},
					},
				},
			},
		},
		languageId: 'en_US',
		layoutData: {items: {'fragment-id': {config: {}}}},
		mappingFields: [],
		segmentsExperienceId: 0,
	};
}

function renderActionPanel(
	{state = getStateWithConfig(), type = EDITABLE_TYPES.text} = {},
	dispatch = () => {}
) {
	return render(
		<StoreAPIContextProvider dispatch={dispatch} getState={() => state}>
			<EditableActionPanel
				item={{
					editableId: 'editable-id-0',
					fragmentEntryLinkId: '0',
					itemId: '',
					parentId: 'fragment-id',
					type,
				}}
			/>
		</StoreAPIContextProvider>,
		{
			baseElement: document.body,
		}
	);
}

describe('EditableActionPanel', () => {
	beforeAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'saved',
		});
	});

	it('does not render interaction selector when no action is selected', () => {
		renderActionPanel();

		expect(
			screen.queryByText('success-interaction')
		).not.toBeInTheDocument();

		expect(screen.queryByText('error-interaction')).not.toBeInTheDocument();
	});

	it('does not render interaction selector after unmapping', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {},
			}),
		});

		expect(
			screen.queryByText('success-interaction')
		).not.toBeInTheDocument();

		expect(screen.queryByText('error-interaction')).not.toBeInTheDocument();
	});

	it('renders interaction and reload selectors when an action is selected', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {...MAPPED_ACTION, fieldId: 'actionFieldId'},
			}),
		});

		expect(screen.getByText('success-interaction')).toBeInTheDocument();
		expect(
			screen.getByText('reload-page-after-success')
		).toBeInTheDocument();

		expect(screen.getByText('error-interaction')).toBeInTheDocument();
		expect(screen.getByText('reload-page-after-error')).toBeInTheDocument();
	});

	it('renders interaction and reload selectors when an action is mapped to structure', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {mappedField: 'mappedField'},
			}),
		});

		expect(screen.getByText('success-interaction')).toBeInTheDocument();
		expect(
			screen.getByText('reload-page-after-success')
		).toBeInTheDocument();

		expect(screen.getByText('error-interaction')).toBeInTheDocument();
		expect(screen.getByText('reload-page-after-error')).toBeInTheDocument();
	});

	it('renders interaction and reload selectors when an action is mapped inside a collection', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {collectionFieldId: 'collectionFieldId'},
			}),
		});

		expect(screen.getByText('success-interaction')).toBeInTheDocument();
		expect(
			screen.getByText('reload-page-after-success')
		).toBeInTheDocument();

		expect(screen.getByText('error-interaction')).toBeInTheDocument();
		expect(screen.getByText('reload-page-after-error')).toBeInTheDocument();
	});

	it('renders text and preview selectors when selecting notification', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {...MAPPED_ACTION, fieldId: 'actionFieldId'},
				onSuccess: {interaction: 'notification'},
			}),
		});

		expect(screen.getByText('success-text')).toBeInTheDocument();
		expect(
			screen.getByLabelText('preview-success-notification')
		).toBeInTheDocument();
	});

	it('renders layout selector and does not allow to reload when selecting Go to page', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {...MAPPED_ACTION, fieldId: 'actionFieldId'},
				onSuccess: {interaction: 'page'},
			}),
		});

		expect(screen.getByText('success-page')).toBeInTheDocument();
		expect(
			screen.queryByText('reload-page-after-success')
		).not.toBeInTheDocument();
	});

	it('renders url input and does not allow to reload when selecting External URL', () => {
		renderActionPanel({
			state: getStateWithConfig({
				mappedAction: {...MAPPED_ACTION, fieldId: 'actionFieldId'},
				onSuccess: {interaction: 'url'},
			}),
		});

		expect(screen.getByText('success-external-url')).toBeInTheDocument();
		expect(
			screen.queryByText('reload-page-after-success')
		).not.toBeInTheDocument();
	});

	describe('EditableActionPanel with LPS-195263', () => {
		it('renders display page and does not allow to reload when selecting External URL', () => {
			renderActionPanel({
				state: getStateWithConfig({
					mappedAction: {...MAPPED_ACTION, fieldId: 'actionFieldId'},
					onSuccess: {interaction: 'displayPage'},
				}),
			});

			expect(screen.getByText('display-page')).toBeInTheDocument();
			expect(
				screen.queryByText('reload-page-after-success')
			).not.toBeInTheDocument();
		});
	});
});
