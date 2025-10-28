/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import React from 'react';

import '@testing-library/jest-dom';

import {saveVariationsListPriorityService} from '../../../src/main/resources/META-INF/resources/js/actions/index';
import VariationsNav from '../../../src/main/resources/META-INF/resources/js/components/VariationsNav/index';
import {
	emptyStateNoSegments,
	emptyStateOneAvailableSegments,
	emptyStateOneAvailableSegmentsWithEntryValid,
	listWithFourVariationsAndNoMoreSegmentsEntries,
	listWithTwoVariations,
} from '../mocks/variationsNavProps';

const _getComponent = (props) => {
	return <VariationsNav {...props} />;
};

jest.mock('frontend-js-components-web', () => ({
	openConfirmModal: jest.fn(({onConfirm}) => onConfirm(true)),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	sub: jest.fn((str) => str),
}));

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/actions/index.js',
	() => {
		const moduleMock = jest.fn();
		moduleMock.mockReturnValue({
			ok: true,
			status: 200,
		});

		return {
			saveVariationsListPriorityService: moduleMock,
		};
	}
);

describe('VariationsNav Initial State', () => {
	beforeEach(() => {
		window['openSelectSegmentsEntryDialogMethod'] = jest.fn();
	});

	afterEach(() => {
		jest.restoreAllMocks();
		jest.clearAllMocks();
		cleanup();
	});

	it('shows an initial state with the proper texts', () => {
		const {getByText} = render(_getComponent(emptyStateNoSegments));

		expect(getByText('personalized-variations')).toBeInTheDocument();
		expect(getByText('no-personalized-variations-yet')).toBeInTheDocument();
		expect(
			getByText('no-personalized-variations-were-found')
		).toBeInTheDocument();
	});

	it('shows an initial state without the default variation', () => {
		const {queryByText} = render(_getComponent(emptyStateNoSegments));

		expect(queryByText('Anyone')).not.toBeInTheDocument();
	});

	it('shows the proper texts when a segment is available and no variations are created', () => {
		const {getByText} = render(
			_getComponent(emptyStateOneAvailableSegments)
		);

		expect(getByText('personalized-variations')).toBeInTheDocument();
		expect(getByText('no-personalized-variations-yet')).toBeInTheDocument();
		expect(
			getByText('no-personalized-variations-were-found')
		).toBeInTheDocument();
	});

	it('shows an initial state without the add variation button if entry is not valid', () => {
		const {queryByText} = render(
			_getComponent(emptyStateOneAvailableSegments)
		);

		expect(
			queryByText('add-personalized-variation')
		).not.toBeInTheDocument();
	});

	it('shows an add-personalized-variation button, when a segment is available, the entry is valid and no variations are created', () => {
		const {getByText} = render(
			_getComponent(emptyStateOneAvailableSegmentsWithEntryValid)
		);

		const addPersonalizedVariationButton = getByText(
			'add-personalized-variation'
		);

		expect(addPersonalizedVariationButton).toBeInTheDocument();
		expect(addPersonalizedVariationButton).not.toBeDisabled();

		fireEvent.click(addPersonalizedVariationButton);
		expect(
			window['openSelectSegmentsEntryDialogMethod']
		).toHaveBeenCalled();
	});
});

describe('VariationsNav With segments', () => {
	beforeEach(() => {
		window['openSelectSegmentsEntryDialogMethod'] = jest.fn();
		window.confirm = jest.fn(() => true);
		window.submitForm = jest.fn();
	});

	afterEach(() => {
		jest.restoreAllMocks();
		jest.clearAllMocks();
		cleanup();
	});

	it('shows a variations nav list with the proper title and description', () => {
		const {getByText} = render(_getComponent(listWithTwoVariations));
		expect(getByText('personalized-variations')).toBeInTheDocument();
		expect(
			getByText(
				'create-personalized-variations-of-the-collections-for-different-segments'
			)
		).toBeInTheDocument();
	});

	it('shows a variations nav list with a create-variation button, if segments available', () => {
		const {queryByTitle} = render(
			_getComponent(listWithFourVariationsAndNoMoreSegmentsEntries)
		);

		const addNewVariationButton = queryByTitle('create-variation');
		expect(addNewVariationButton).not.toBeInTheDocument();
	});

	it('shows a variations nav list with a create-variation button hidden, if no more segments are available', () => {
		const {getByTitle} = render(_getComponent(listWithTwoVariations));

		const addNewVariationButton = getByTitle('create-variation');
		expect(addNewVariationButton).toBeInTheDocument();
		expect(addNewVariationButton).not.toBeDisabled();
	});

	it('shows a variations nav list with the proper items list UI, normal and hover state', () => {
		const {container, getByText} = render(
			_getComponent(listWithTwoVariations)
		);

		const itemOne = getByText('Anyone');
		const itemTwo = getByText('Liferayers');

		expect(itemOne).toBeInTheDocument();
		expect(itemTwo).toBeInTheDocument();

		expect(container.getElementsByClassName('active').length).toBe(1);

		expect(
			container.getElementsByClassName('lexicon-icon-ellipsis-v').length
		).toBe(2);

		fireEvent.mouseOver(itemOne);
		expect(
			container.getElementsByClassName('lexicon-icon-drag').length
		).toBe(1);
		fireEvent.mouseOut(itemOne);
		expect(
			container.getElementsByClassName('lexicon-icon-drag').length
		).toBe(0);
	});

	it('shows a variations nav list with an action menu for each item', () => {
		const {getAllByText} = render(_getComponent(listWithTwoVariations));

		const prioritizeButtons = getAllByText('prioritize');
		const deprioritizeButtons = getAllByText('deprioritize');

		expect(prioritizeButtons.length).toBe(2);
		expect(deprioritizeButtons.length).toBe(2);

		expect(prioritizeButtons[0]).toBeDisabled();
		expect(prioritizeButtons[1]).not.toBeDisabled();
		expect(deprioritizeButtons[1]).toBeDisabled();
		expect(deprioritizeButtons[0]).not.toBeDisabled();

		const deleteButtons = getAllByText('delete');
		expect(deleteButtons.length).toBe(2);
		expect(deleteButtons[0]).toBeDisabled();
		expect(deleteButtons[1]).not.toBeDisabled();

		fireEvent.click(deleteButtons[1]);

		expect(openConfirmModal).toHaveBeenCalledWith({
			message: 'are-you-sure-you-want-to-delete-this',
			onConfirm: expect.any(Function),
		});

		expect(window.submitForm).toHaveBeenCalledWith(
			undefined,
			'delete-asset-list-entry-url-1'
		);
	});

	it('shows a variations nav list with a non deletable default item', () => {
		const {getAllByText} = render(_getComponent(listWithTwoVariations));

		const deleteButtons = getAllByText('delete');
		expect(deleteButtons.length).toBe(2);
		expect(deleteButtons[0]).toBeDisabled();

		fireEvent.click(deleteButtons[0]);
		expect(openConfirmModal).not.toHaveBeenCalled();
		expect(window.submitForm).not.toHaveBeenCalledWith();
	});
});

describe('VariationsNav', () => {
	afterEach(() => {
		jest.restoreAllMocks();
		cleanup();
	});

	it('responds to dnd events', async () => {
		const {getAllByRole} = render(_getComponent(listWithTwoVariations));
		let nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Anyone');
		expect(nodes[1].innerHTML).toContain('Liferayers');

		fireEvent.dragStart(nodes[0]);
		fireEvent.dragEnter(nodes[1]);
		fireEvent.dragOver(nodes[1]);

		fireEvent.drop(nodes[1]);

		nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Liferayers');
		expect(nodes[1].innerHTML).toContain('Anyone');

		expect(await saveVariationsListPriorityService).toHaveBeenCalled();
	});

	it('responds to reorder on click event', async () => {
		const {getAllByRole, getAllByText} = render(
			_getComponent(listWithTwoVariations)
		);
		let nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Anyone');
		expect(nodes[1].innerHTML).toContain('Liferayers');

		const prioritizeVariationButtons = getAllByText('prioritize');
		fireEvent.click(prioritizeVariationButtons[1]);

		nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Liferayers');
		expect(nodes[1].innerHTML).toContain('Anyone');

		expect(await saveVariationsListPriorityService).toHaveBeenCalled();
	});

	it('throws an error if the API call fails', async () => {
		saveVariationsListPriorityService.mockReturnValue({
			ok: false,
			status: 500,
		});

		const {getAllByRole, getAllByText} = render(
			_getComponent(listWithTwoVariations)
		);
		let nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Anyone');
		expect(nodes[1].innerHTML).toContain('Liferayers');

		const prioritizeVariationButtons = getAllByText('prioritize');
		fireEvent.click(prioritizeVariationButtons[1]);

		nodes = getAllByRole('listitem');

		expect(nodes[0].innerHTML).toContain('Liferayers');
		expect(nodes[1].innerHTML).toContain('Anyone');

		expect(await saveVariationsListPriorityService).toHaveBeenCalled();

		expect(openToast).toHaveBeenCalledWith({
			message: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});
	});
});
