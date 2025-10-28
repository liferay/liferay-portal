/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SegmentEdit from '../../../../src/main/resources/META-INF/resources/js/components/segment_edit/SegmentEdit';

const PROPERTY_GROUPS_BASIC = [
	{
		entityName: 'First Test Values Group',
		name: 'First Test Values Group',
		properties: [
			{
				label: 'Value',
				name: 'value',
				options: [],
				selectEntity: null,
				type: 'string',
			},
		],
		propertyKey: 'first-test-values-group',
	},
];

const DEFAULT_REDIRECT = '/test-url';

const CONTRIBUTORS = [
	{
		conjunctionId: 'and',
		conjunctionInputId: 'conjunction-input-1',
		initialQuery: {
			conjunctionName: 'and',
			groupId: 'group_01',
			items: [
				{
					operatorName: 'eq',
					propertyName: 'value',
					value: 'value',
				},
			],
		},
		inputId: 'input-id-for-backend-form',
		propertyKey: 'first-test-values-group',
	},
];

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	navigate: jest.fn(),
}));

function _renderSegmentEditComponent({
	source = undefined,
	redirect = DEFAULT_REDIRECT,
	hasUpdatePermission = undefined,
	contributors = undefined,
	showInEditMode = undefined,
	isSegmentationEnabled = true,
} = {}) {
	return render(
		<SegmentEdit
			availableLocales={{
				en_US: '',
			}}
			contributors={contributors}
			defaultLanguageId="en_US"
			hasUpdatePermission={hasUpdatePermission}
			initialSegmentName={{
				en_US: 'Segment title',
			}}
			isSegmentationEnabled={isSegmentationEnabled}
			locale="en_US"
			redirect={redirect}
			showInEditMode={showInEditMode}
			source={source}
		/>
	);
}

describe('SegmentEdit', () => {
	beforeAll(() => {
		window.Liferay = {
			...Liferay,
			CustomDialogs: {},
			FeatureFlags: {},
		};
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders', () => {
		const {asFragment} = _renderSegmentEditComponent();

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders with edit buttons if the user has update permissions', () => {
		const hasUpdatePermission = true;

		const {asFragment} = _renderSegmentEditComponent({
			hasUpdatePermission,
		});

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders without edit buttons if the user does not have update permissions', () => {
		const hasUpdatePermission = false;

		const {asFragment} = _renderSegmentEditComponent({
			hasUpdatePermission,
		});

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders with given values', () => {
		const hasUpdatePermission = true;

		const {asFragment, getByTestId} = render(
			<SegmentEdit
				availableLocales={{
					en_US: '',
				}}
				contributors={CONTRIBUTORS}
				defaultLanguageId="en_US"
				hasUpdatePermission={hasUpdatePermission}
				initialSegmentName={{
					en_US: 'Segment title',
				}}
				locale="en_US"
				propertyGroups={PROPERTY_GROUPS_BASIC}
				redirect="/test-url"
				showInEditMode={true}
			/>
		);

		expect(getByTestId(CONTRIBUTORS[0].inputId).value).toBe(
			"(value eq 'value')"
		);

		expect(asFragment()).toMatchSnapshot();
	});

	it('redirects when cancelling without any edition', () => {
		const mockConfirm = jest.fn();

		window.confirm = mockConfirm;

		const hasUpdatePermission = true;

		const {getByText} = _renderSegmentEditComponent({
			contributors: CONTRIBUTORS,
			hasUpdatePermission,
			propertyGroups: PROPERTY_GROUPS_BASIC,
		});

		const cancelButton = getByText('cancel');

		expect(cancelButton).not.toBe(null);

		fireEvent.click(cancelButton);

		expect(navigate).toHaveBeenCalledTimes(1);
		expect(navigate).toHaveBeenCalledWith(DEFAULT_REDIRECT);
		expect(mockConfirm).toHaveBeenCalledTimes(0);
	});

	it('redirects when cancelling after title edition', (done) => {
		const mockConfirm = jest.fn();

		window.confirm = mockConfirm;

		const hasUpdatePermission = true;

		const {getByPlaceholderText, getByText} = _renderSegmentEditComponent({
			contributors: CONTRIBUTORS,
			hasUpdatePermission,
			propertyGroups: PROPERTY_GROUPS_BASIC,
		});

		const localizedInput = getByPlaceholderText('untitled-segment');
		const cancelButton = getByText('cancel');

		fireEvent.change(localizedInput, {target: {value: 'A'}});

		waitFor(() => expect(localizedInput.value).toBe('A')).then(() => {
			expect(cancelButton).not.toBe(null);

			fireEvent.click(cancelButton);

			expect(navigate).toHaveBeenCalledTimes(0);
			expect(mockConfirm).toHaveBeenCalledTimes(1);
			expect(mockConfirm).toHaveBeenCalledWith(
				'criteria-cancel-confirmation-message'
			);
			done();
		});
	});

	it('renders a dissmisible alert if Segmentation service is disabled', () => {
		const isSegmentationEnabled = false;

		const {container, getByText} = _renderSegmentEditComponent({
			isSegmentationEnabled,
		});

		expect(getByText('segmentation-is-disabled')).toBeInTheDocument();

		expect(
			container.getElementsByClassName(
				'segment-edit-page-root--with-warning'
			).length
		).toBe(1);

		expect(
			container.querySelectorAll(
				'.alert-dismissible .lexicon-icon.lexicon-icon-times'
			).length
		).toBe(1);
	});

	it('renders a dismissible alert which is effectively dismissible', async () => {
		const isSegmentationEnabled = false;

		const {container, getByLabelText, getByText, queryByText} =
			_renderSegmentEditComponent({
				isSegmentationEnabled,
			});

		expect(getByText('segmentation-is-disabled')).toBeInTheDocument();

		const dismissButton = getByLabelText('Close', {selector: 'button'});

		fireEvent.click(dismissButton);

		expect(queryByText('segmentation-is-disabled')).not.toBeInTheDocument();

		expect(
			container.querySelectorAll(
				'.alert-dismissible .lexicon-icon.lexicon-icon-times'
			).length
		).toBe(0);

		expect(
			container.getElementsByClassName(
				'segment-edit-page-root--with-warning'
			).length
		).toBe(0);
	});
});
