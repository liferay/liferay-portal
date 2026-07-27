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
import {
	AudiencesCriteriaRulesGroup,
	AudiencesCriteriaType,
} from '../../src/main/resources/META-INF/resources/js/types';

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

const getSerializedCriteria = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'save'}));

	const [, {body}] = (fetch as jest.Mock).mock.calls.at(-1);

	return JSON.parse(body.get('json'));
};

const getSerializedRuleAttributes = async () => {
	const serializedCriteria = await getSerializedCriteria();

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

	beforeEach(() => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({}),
		});
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
		expect(input.getAttribute('aria-required')).toBe('true');

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
						error: {externalReferenceCode: 'error-message'},
					}),
			});

			renderAudienceBuilder();

			const saveButton = screen.getByRole('button', {name: 'save'});

			await userEvent.click(saveButton);

			expect(await screen.findByText('error-message')).toBeVisible();

			const externalReferenceCodeInput = screen.getByRole('textbox', {
				name: 'erc',
			});

			expect(externalReferenceCodeInput).toBeVisible();
			expect(externalReferenceCodeInput).toHaveFocus();

			expect(navigate).not.toHaveBeenCalled();
			expect(Liferay.Util.openToast).not.toHaveBeenCalled();

			expect(saveButton).toBeEnabled();
			expect(screen.getByLabelText('name')).toHaveValue('My Audience');
		});

		it('shows the error in a toast when the save fails without a field', async () => {
			(fetch as jest.Mock).mockResolvedValue({
				json: () => Promise.resolve({error: {other: 'error-message'}}),
			});

			renderAudienceBuilder();

			const saveButton = screen.getByRole('button', {name: 'save'});

			await userEvent.click(saveButton);

			await waitFor(() =>
				expect(Liferay.Util.openToast).toHaveBeenCalledWith({
					message: 'error-message',
					type: 'danger',
				})
			);

			expect(navigate).not.toHaveBeenCalled();
			expect(saveButton).toBeEnabled();
		});

		it('reports every empty field and clears each error when it changes', async () => {
			render(
				<AudienceBuilder
					externalReferenceCode=" "
					name=" "
					namespace="_test_"
					updateAudiencesEntryActionURL="/update"
				/>
			);

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(screen.getByText('please-enter-a-valid-name')).toBeVisible();
			expect(screen.getByText('this-field-is-required')).toBeVisible();

			expect(screen.getByRole('textbox', {name: 'erc'})).toBeVisible();

			expect(screen.getByLabelText('name')).toHaveFocus();

			expect(fetch).not.toHaveBeenCalled();
			expect(Liferay.Util.openToast).not.toHaveBeenCalled();

			await userEvent.type(screen.getByLabelText('name'), 'A');

			expect(screen.queryByText('please-enter-a-valid-name')).toBeNull();

			await userEvent.type(
				screen.getByRole('textbox', {name: 'erc'}),
				'E'
			);

			expect(screen.queryByText('this-field-is-required')).toBeNull();
		});
	});

	describe('keyboard movement', () => {
		const renderAudienceBuilder = (
			rulesGroup?: AudiencesCriteriaRulesGroup
		) =>
			render(
				<AudienceBuilder
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					externalReferenceCode="ERC-123"
					name="My Audience"
					rulesGroup={rulesGroup}
					updateAudiencesEntryActionURL="/update"
				/>
			);

		it('adds a condition from the sidebar at the chosen position', async () => {
			const {container} = renderAudienceBuilder({
				conjunction: 'AND',
				rules: [{attribute: 'age', operator: 'gt', value: '18'}],
			});

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

			expect(await getSerializedRuleAttributes()).toEqual([
				'city',
				'age',
			]);
		});

		it('adds a condition at the end when confirming the initial target', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [{attribute: 'age', operator: 'gt', value: '18'}],
			});

			await userEvent.click(getAttributeElement('City'));
			await userEvent.keyboard('{Enter}{Enter}');

			expect(await getSerializedRuleAttributes()).toEqual([
				'age',
				'city',
			]);
		});

		it('adds the condition immediately when there are no conditions', async () => {
			renderAudienceBuilder();

			await userEvent.click(getAttributeElement('City'));
			await userEvent.keyboard('{Enter}');

			expect(
				screen.getByText('a-condition-was-added')
			).toBeInTheDocument();

			expect(await getSerializedRuleAttributes()).toEqual(['city']);
		});

		it('reorders a condition', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [
					{attribute: 'age', operator: 'gt', value: '18'},
					{attribute: 'city', operator: 'eq', value: 'Madrid'},
				],
			});

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Enter}');

			expect(await getSerializedRuleAttributes()).toEqual([
				'city',
				'age',
			]);
		});

		it('commits the movement with space', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [
					{attribute: 'age', operator: 'gt', value: '18'},
					{attribute: 'city', operator: 'eq', value: 'Madrid'},
				],
			});

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}[Space]');

			expect(await getSerializedRuleAttributes()).toEqual([
				'city',
				'age',
			]);
		});

		it('reorders a condition above a removed-criteria error row', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [
					{attribute: 'removed', operator: 'eq', value: ''},
					{attribute: 'age', operator: 'gt', value: '18'},
				],
			});

			screen.getByTitle('move-x').focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowUp}{ArrowUp}{ArrowUp}{Enter}');

			expect(await getSerializedRuleAttributes()).toEqual([
				'age',
				'removed',
			]);
		});

		it('moves a nested condition out of its group', async () => {
			renderAudienceBuilder({
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
							{attribute: 'country', operator: 'eq', value: 'ES'},
						],
					},
				],
			});

			screen.getAllByTitle('move-x')[1].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard(
				'{ArrowUp}{ArrowUp}{ArrowUp}{ArrowUp}{Enter}'
			);

			expect(await getSerializedRuleAttributes()).toEqual([
				'city',
				'age',
				'country',
			]);
		});

		it('groups two conditions with the keyboard', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [
					{attribute: 'age', operator: 'gt', value: '18'},
					{attribute: 'city', operator: 'eq', value: 'Madrid'},
					{attribute: 'country', operator: 'eq', value: 'ES'},
				],
			});

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Enter}');

			const serializedCriteria = await getSerializedCriteria();

			expect(
				serializedCriteria.rules[0].rules.map(
					(rule: {attribute: string}) => rule.attribute
				)
			).toEqual(['city', 'age']);
			expect(serializedCriteria.rules[1].attribute).toBe('country');
		});

		it('cancels the movement with escape', async () => {
			renderAudienceBuilder({
				conjunction: 'AND',
				rules: [
					{attribute: 'age', operator: 'gt', value: '18'},
					{attribute: 'city', operator: 'eq', value: 'Madrid'},
				],
			});

			screen.getAllByTitle('move-x')[0].focus();

			await userEvent.keyboard('{Enter}');

			await userEvent.keyboard('{ArrowDown}{Escape}');

			expect(await getSerializedRuleAttributes()).toEqual([
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
