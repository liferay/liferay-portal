/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ClickGoalPicker from '../../../src/main/resources/META-INF/resources/js/components/ClickGoalPicker/ClickGoalPicker.es';
import {segmentsExperiment} from '../fixtures.es';
import renderApp from '../renderApp.es';

describe('ClickGoalPicker', () => {
	afterEach(cleanup);

	beforeAll(() => {
		window.Liferay = {
			...Liferay,
			FeatureFlags: {
				'LRAC-15017': true,
			},
		};
	});

	it('Experiment renders when goal value is click', () => {
		const experiment = {
			...segmentsExperiment,
			goal: {
				label: 'Click',
				value: 'click',
			},
		};

		const {getByText} = renderApp({
			initialSegmentsExperiment: experiment,
		});

		expect(getByText(experiment.name)).toBeInTheDocument();
		expect(getByText('review-and-run-test')).toBeInTheDocument();
		expect(getByText('click-goal-description')).toBeInTheDocument();
		expect(getByText('element-id')).toBeInTheDocument();
	});

	it('User clicks in "Select Clickable Element" button, the button turns from default to active mode', () => {
		const experiment = {
			...segmentsExperiment,
			goal: {
				label: 'Click',
				value: 'click',
			},
		};

		const {getByText} = render(
			<ClickGoalPicker segmentsExperiment={experiment} />
		);

		const setElementButton = getByText('select-clickable-element');
		userEvent.click(setElementButton);

		const clickGoalRoot = document.body.querySelector(
			'div.lfr-segments-experiment-click-goal-root'
		);
		expect(clickGoalRoot).not.toBe(null);
	});

	it('The user can select a clickable element in a draft experiment', () => {
		const experiment = {
			...segmentsExperiment,
			goal: {
				label: 'Click',
				value: 'click',
			},
		};

		const {getByText} = render(
			<ClickGoalPicker segmentsExperiment={experiment} />
		);

		expect(experiment.status.label).toBe('Draft');

		const selectClickableElementButton = getByText(
			'select-clickable-element'
		);
		expect(selectClickableElementButton.attributes['disabled']).toBe(
			undefined
		);
	});

	it('The user can change a clickable element in a draft experiment', () => {
		const experiment = {
			...segmentsExperiment,
			goal: {
				label: 'Click',
				target: 'myButtonId',
				value: 'click',
			},
		};

		const {getByText} = render(
			<ClickGoalPicker target={experiment.goal.target} />
		);

		expect(experiment.status.label).toBe('Draft');
		const editElement = getByText('change-clickable-element');
		expect(editElement.attributes['disabled']).toBe(undefined);
	});

	it('The user selects a clickable element target and the HTML id of the element displays inside the input', () => {
		const experiment = {
			...segmentsExperiment,
			goal: {
				label: 'Click',
				target: 'myButtonId',
				value: 'click',
			},
		};

		const {getByLabelText} = render(
			<ClickGoalPicker target={experiment.goal.target} />
		);

		const input = getByLabelText('element-id');
		expect(input).toBeInTheDocument();
		expect(input.value).toBe(experiment.goal.target);
	});

	test.todo(
		'All clickable elements highlighted when the user clicks on set element button'
	);

	test.todo(
		'A tooltip click element to set as click target for your goal appears when the user clicks on set element button'
	);

	test.todo('Selectable as target elements show tooltips on hover');

	test.todo(
		'Cancel selection click target element proccess when clicking out of selection zone'
	);

	test.todo(
		'When hovering over to invalid click target elements, the mouse is displayed in not-allowed mode'
	);

	test.todo('The user can remove a selected click target in a draft element');

	test.todo(
		'The user clicks in the UI reference to the selected click target element, the page scrolls to make it visible'
	);
});
