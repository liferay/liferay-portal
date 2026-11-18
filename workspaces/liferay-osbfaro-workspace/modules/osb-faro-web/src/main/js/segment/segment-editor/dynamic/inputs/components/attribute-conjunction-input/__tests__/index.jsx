import AttributeConjunctionInput from '../index';
import React from 'react';
import {encodeAttributeId} from '../utils';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {mockListGeometry, scrollListToBottom} from 'test/infinite-scroll';
import {mockEventAttributeDefinition} from 'test/data';
import {range} from 'lodash';
import {ReferencedObjectsProvider} from '../../../../context/referencedObjects';
import {RelationalOperators} from '../../../../utils/constants';

jest.unmock('react-dom');

const renderWithProvider = props =>
	render(
		<ReferencedObjectsProvider>
			<AttributeConjunctionInput
				attributesDataSourceFn={() =>
					Promise.resolve({items: [], total: 0})
				}
				{...props}
			/>
		</ReferencedObjectsProvider>
	);

describe('AttributeConjunctionInput', () => {
	it('loads attributes as the list is scrolled and selects one from a later page', async () => {
		const attributes = range(30).map(index =>
			mockEventAttributeDefinition(index)
		);

		const attributesDataSourceFn = jest.fn(({page, pageSize}) =>
			Promise.resolve({
				items: attributes.slice((page - 1) * pageSize, page * pageSize),
				total: attributes.length
			})
		);

		const onChange = jest.fn();

		const restoreListGeometry = mockListGeometry();

		renderWithProvider({
			attributes: attributes.slice(0, 25),
			attributesDataSourceFn,
			conjunctionCriterion: {
				operatorName: RelationalOperators.EQ,
				propertyName: `attribute/${encodeAttributeId('name-0')}`,
				value: 'test value'
			},
			onChange,
			touched: {attribute: true, attributeValue: true},
			valid: {attribute: true, attributeValue: true}
		});

		fireEvent.click(
			screen.getByRole('combobox', {name: /^event attributes$/i})
		);

		await screen.findByRole('option', {name: 'displayName-24'});

		expect(screen.queryByText('All Event Attributes')).toBeNull();

		scrollListToBottom();

		fireEvent.click(
			await screen.findByRole('option', {name: 'displayName-27'})
		);

		expect(attributesDataSourceFn).toHaveBeenLastCalledWith({
			page: 2,
			pageSize: 25,
			query: ''
		});

		await waitFor(() =>
			expect(onChange).toHaveBeenCalledWith(
				expect.objectContaining({
					attribute: attributes[27],
					criterion: expect.objectContaining({
						propertyName: `attribute/${encodeAttributeId(
							'name-27'
						)}`
					})
				})
			)
		);

		restoreListGeometry();
	});

	it('should build the criterion propertyName from the hex-encoded attribute name, not its id', () => {
		const onChange = jest.fn();
		const attribute = mockEventAttributeDefinition(2);

		renderWithProvider({
			attributes: [attribute],
			conjunctionCriterion: {
				operatorName: RelationalOperators.EQ,
				propertyName: 'attribute/',
				value: ''
			},
			onChange,
			touched: {attribute: false, attributeValue: false},
			valid: {attribute: false, attributeValue: false}
		});

		expect(onChange).toHaveBeenCalledWith(
			expect.objectContaining({
				criterion: expect.objectContaining({
					propertyName: `attribute/${encodeAttributeId(
						attribute.name
					)}`
				})
			})
		);
	});

	describe('small', () => {
		it('should apply the form-control-sm class to the attribute picker when small is true', () => {
			const {container} = renderWithProvider({
				attributes: range(4).map(index =>
					mockEventAttributeDefinition(index)
				),
				conjunctionCriterion: {
					operatorName: RelationalOperators.EQ,
					propertyName: `attribute/${encodeAttributeId('name-1')}`,
					value: 'test value'
				},
				onChange: jest.fn(),
				small: true,
				touched: {attribute: true, attributeValue: true},
				valid: {attribute: true, attributeValue: true}
			});

			expect(
				container.querySelector('.form-control-sm')
			).toBeTruthy();
		});
	});

	describe('onClear', () => {
		const attribute = mockEventAttributeDefinition(1);
		const conjunctionCriterion = {
			operatorName: RelationalOperators.EQ,
			propertyName: `attribute/${encodeAttributeId(attribute.name)}`,
			value: 'test value'
		};

		it('should call onClear when the clear button is clicked', () => {
			const onClear = jest.fn();

			const {getByLabelText} = renderWithProvider({
				attributes: [attribute],
				conjunctionCriterion,
				onChange: jest.fn(),
				onClear,
				touched: {attribute: true, attributeValue: false},
				valid: {attribute: true, attributeValue: false}
			});

			fireEvent.click(getByLabelText(/^clear$/i));

			expect(onClear).toHaveBeenCalledTimes(1);
		});

		it('should not render the clear button when onClear is not provided', () => {
			const {queryByLabelText} = renderWithProvider({
				attributes: [attribute],
				conjunctionCriterion,
				onChange: jest.fn(),
				touched: {attribute: true, attributeValue: false},
				valid: {attribute: true, attributeValue: false}
			});

			expect(queryByLabelText(/^clear$/i)).toBeNull();
		});
	});
});
