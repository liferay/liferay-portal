/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch, navigate} from 'frontend-js-web';
import React from 'react';

import AudienceBuilder from '../../src/main/resources/META-INF/resources/js/AudienceBuilder';
import {AudiencesCriteriaType} from '../../src/main/resources/META-INF/resources/js/types';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(),
	navigate: jest.fn(),
}));

const AUDIENCES_CRITERIA_TYPES: AudiencesCriteriaType[] = [
	{
		audiencesCriterias: [
			{
				icon: 'user',
				inputType: 'text',
				key: 'age',
				label: 'Age',
				options: [],
				type: 'number',
			},
			{
				icon: 'user',
				inputType: 'text',
				key: 'city',
				label: 'City',
				options: [],
				type: 'string',
			},
		],
		key: 'user',
		label: 'User',
	},
];

const getSerializedRuleAttributes = (container: HTMLElement) => {
	const input =
		container.querySelector<HTMLInputElement>('input[name="json"]');

	const serializedCriteria = JSON.parse(input!.value);

	return serializedCriteria.rules.map(
		(rule: {attribute: string}) => rule.attribute
	);
};

const getAttributeElement = (label: string) => {
	const attributeElement = screen
		.getByText(label)
		.closest('[role="menuitem"]') as HTMLElement;

	return attributeElement;
};

describe('AudienceBuilder', () => {
	beforeAll(() => {
		(Liferay.Language as {direction: Record<string, string>}).direction = {
			en_US: 'ltr',
		};
	});

	it('renders editor, updates name, back and cancel link to backURL', async () => {
		const {getByLabelText, getByText, queryByText} = render(
			<AudienceBuilder backURL="/back" namespace="_test_" />
		);

		expect(getByText('new-audience')).toBeTruthy();
		expect(getByText('cancel')).toBeTruthy();
		expect(getByText('save')).toBeTruthy();
		expect(getByText('attributes-types')).toBeTruthy();

		expect(getByLabelText('back').getAttribute('href')).toBe('/back');
		expect(getByText('cancel').getAttribute('href')).toBe('/back');

		const input = getByLabelText('name');

		expect(input.getAttribute('name')).toBe('_test_name');
		expect(input.getAttribute('maxLength')).toBe('75');
		expect(input.hasAttribute('required')).toBe(true);

		expect((input as HTMLInputElement).value).toBe('');
		expect(input.getAttribute('placeholder')).toBe('new-audience');

		await userEvent.type(input, 'My Audience');

		expect(getByText('My Audience')).toBeTruthy();
		expect(queryByText('new-audience')).toBeNull();
	});

	describe('save', () => {
		const renderAudienceBuilder = () =>
			render(
				<AudienceBuilder
					audiencesEntryId={42}
					externalReferenceCode="ERC-123"
					name="My Audience"
					namespace="_test_"
					redirect="/audiences"
					updateAudiencesEntryActionURL="/update"
				/>
			);

		beforeEach(() => {
			jest.clearAllMocks();
		});

		it('posts the values and navigates to the redirect on success', async () => {
			(fetch as jest.Mock).mockResolvedValue({ok: true});

			renderAudienceBuilder();

			const saveButton = screen.getByRole('button', {name: 'save'});

			await userEvent.click(saveButton);

			const [url, {body}] = (fetch as jest.Mock).mock.calls[0];

			expect(url).toBe('/update');

			expect(body.get('_test_audiencesEntryId')).toBe('42');
			expect(body.get('_test_externalReferenceCode')).toBe('ERC-123');
			expect(body.get('_test_json')).not.toBeNull();
			expect(body.get('_test_name')).toBe('My Audience');

			await waitFor(() =>
				expect(navigate).toHaveBeenCalledWith('/audiences')
			);

			expect(saveButton).toBeDisabled();
			expect(Liferay.Util.openToast).not.toHaveBeenCalled();
		});

		it('shows the error on the external reference code and keeps the values when the save fails', async () => {
			(fetch as jest.Mock).mockResolvedValue({
				json: () =>
					Promise.resolve({
						errorField: 'externalReferenceCode',
						errorMessage: 'error-message',
					}),
				ok: false,
			});

			renderAudienceBuilder();

			const saveButton = screen.getByRole('button', {name: 'save'});

			await userEvent.click(saveButton);

			expect(await screen.findByText('error-message')).toBeVisible();

			expect(screen.getByRole('textbox', {name: 'erc'})).toBeVisible();

			expect(navigate).not.toHaveBeenCalled();
			expect(Liferay.Util.openToast).not.toHaveBeenCalled();

			expect(saveButton).toBeEnabled();
			expect(screen.getByLabelText('name')).toHaveValue('My Audience');
		});

		it('clears the error on the external reference code when it changes', async () => {
			(fetch as jest.Mock).mockResolvedValue({
				json: () =>
					Promise.resolve({
						errorField: 'externalReferenceCode',
						errorMessage: 'error-message',
					}),
				ok: false,
			});

			renderAudienceBuilder();

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(await screen.findByText('error-message')).toBeVisible();

			await userEvent.type(
				screen.getByRole('textbox', {name: 'erc'}),
				'4'
			);

			expect(screen.queryByText('error-message')).toBeNull();
		});

		it('shows the error on the name when the save fails', async () => {
			(fetch as jest.Mock).mockResolvedValue({
				json: () =>
					Promise.resolve({
						errorField: 'name',
						errorMessage: 'error-message',
					}),
				ok: false,
			});

			renderAudienceBuilder();

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(await screen.findByText('error-message')).toBeVisible();

			expect(Liferay.Util.openToast).not.toHaveBeenCalled();
		});

		it('shows the error in a toast when the save fails without a field', async () => {
			(fetch as jest.Mock).mockResolvedValue({
				json: () => Promise.resolve({errorMessage: 'error-message'}),
				ok: false,
			});

			renderAudienceBuilder();

			const saveButton = screen.getByRole('button', {name: 'save'});

			await userEvent.click(saveButton);

			await waitFor(() =>
				expect(Liferay.Util.openToast).toHaveBeenCalledWith({
					autoClose: false,
					message: 'error-message',
					toastProps: {className: 'audience-builder-save-error'},
					type: 'danger',
				})
			);

			expect(navigate).not.toHaveBeenCalled();
			expect(saveButton).toBeEnabled();
		});

		it('does not save when pressing enter on an input', async () => {
			renderAudienceBuilder();

			await userEvent.type(screen.getByLabelText('name'), '{Enter}');

			expect(fetch).not.toHaveBeenCalled();
		});
	});

	describe('keyboard movement', () => {
		it('adds a condition from the sidebar at the chosen position', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
						],
					}}
				/>
			);

			await userEvent.click(getAttributeElement('City'));
			await userEvent.keyboard('{Enter}');

			expect(
				screen.getByText(
					'use-arrows-to-move-it-and-press-enter-to-select-the-new-position-press-esc-to-cancel'
				)
			).toBeInTheDocument();

			await userEvent.keyboard('{ArrowUp}{ArrowUp}');

			expect(
				container.querySelector('.audience-builder-rule')
			).toHaveClass('audience-builder-rule--drop-top');

			await userEvent.keyboard('{Enter}');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'city',
				'age',
			]);
		});

		it('adds a condition at the end when confirming the initial target', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
						],
					}}
				/>
			);

			await userEvent.click(getAttributeElement('City'));
			await userEvent.keyboard('{Enter}{Enter}');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'age',
				'city',
			]);
		});

		it('adds the condition immediately when there are no conditions', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
				/>
			);

			await userEvent.click(getAttributeElement('City'));
			await userEvent.keyboard('{Enter}');

			expect(
				screen.getByText('a-condition-was-added')
			).toBeInTheDocument();

			expect(getSerializedRuleAttributes(container)).toEqual(['city']);
		});

		it('reorders a condition', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								attribute: 'city',
								operator: 'eq',
								value: 'Madrid',
							},
						],
					}}
				/>
			);

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Enter}');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'city',
				'age',
			]);
		});

		it('commits the movement with space', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								attribute: 'city',
								operator: 'eq',
								value: 'Madrid',
							},
						],
					}}
				/>
			);

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}[Space]');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'city',
				'age',
			]);
		});

		it('reorders a condition above a removed-criteria error row', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'removed', operator: 'eq', value: ''},
							{attribute: 'age', operator: 'gt', value: '18'},
						],
					}}
				/>
			);

			screen.getByTitle('move-x').focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowUp}{ArrowUp}{ArrowUp}{Enter}');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'age',
				'removed',
			]);
		});

		it('moves a nested condition out of its group', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								conjunction: 'OR',
								rules: [
									{
										attribute: 'city',
										operator: 'eq',
										value: 'Madrid',
									},
									{
										attribute: 'country',
										operator: 'eq',
										value: 'ES',
									},
								],
							},
						],
					}}
				/>
			);

			screen.getAllByTitle('move-x')[1].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard(
				'{ArrowUp}{ArrowUp}{ArrowUp}{ArrowUp}{Enter}'
			);

			expect(getSerializedRuleAttributes(container)).toEqual([
				'city',
				'age',
				'country',
			]);
		});

		it('groups two conditions with the keyboard', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								attribute: 'city',
								operator: 'eq',
								value: 'Madrid',
							},
							{attribute: 'country', operator: 'eq', value: 'ES'},
						],
					}}
				/>
			);

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Enter}');

			const input =
				container.querySelector<HTMLInputElement>('input[name="json"]');

			const serializedCriteria = JSON.parse(input!.value);

			expect(
				serializedCriteria.rules[0].rules.map(
					(rule: {attribute: string}) => rule.attribute
				)
			).toEqual(['city', 'age']);
			expect(serializedCriteria.rules[1].attribute).toBe('country');
		});

		it('cancels the movement with escape', async () => {
			const {container} = render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								attribute: 'city',
								operator: 'eq',
								value: 'Madrid',
							},
						],
					}}
				/>
			);

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Escape}');

			expect(getSerializedRuleAttributes(container)).toEqual([
				'age',
				'city',
			]);
		});
	});

	describe('keyboard navigation', () => {
		it('moves the focus through the conditions with the arrow keys', async () => {
			render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
							{
								attribute: 'city',
								operator: 'eq',
								value: 'Madrid',
							},
						],
					}}
				/>
			);

			const conditionsList = screen.getByRole('list', {
				name: 'conditions',
			});

			const rows = within(conditionsList).getAllByRole('listitem');

			expect(rows[0]).toHaveAttribute('tabindex', '0');
			expect(rows[1]).toHaveAttribute('tabindex', '-1');

			const moveButtons = within(conditionsList).getAllByTitle('move-x');

			expect(moveButtons[0]).toHaveAttribute('tabindex', '0');
			expect(moveButtons[1]).toHaveAttribute('tabindex', '-1');

			await userEvent.click(rows[0]);
			await userEvent.keyboard('{ArrowDown}');

			expect(rows[1]).toHaveFocus();
			expect(moveButtons[1]).toHaveAttribute('tabindex', '0');

			await userEvent.keyboard('{ArrowUp}');

			expect(rows[0]).toHaveFocus();
		});

		it('moves the focus through the row controls with the horizontal arrow keys', async () => {
			render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					rulesGroup={{
						conjunction: 'AND',
						rules: [
							{attribute: 'age', operator: 'gt', value: '18'},
						],
					}}
				/>
			);

			const conditionsList = screen.getByRole('list', {
				name: 'conditions',
			});

			const [row] = within(conditionsList).getAllByRole('listitem');

			const moveButton = within(conditionsList).getByTitle('move-x');

			await userEvent.click(row);
			await userEvent.keyboard('{ArrowRight}');

			expect(moveButton).toHaveFocus();

			await userEvent.keyboard('{ArrowRight}');

			expect(
				within(conditionsList).getByLabelText('operator')
			).toHaveFocus();

			await userEvent.keyboard('{ArrowLeft}');

			expect(moveButton).toHaveFocus();

			await userEvent.keyboard('{ArrowLeft}');

			expect(row).toHaveFocus();
		});
	});
});
