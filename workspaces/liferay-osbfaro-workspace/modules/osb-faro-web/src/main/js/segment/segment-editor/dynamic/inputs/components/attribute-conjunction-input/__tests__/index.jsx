import AttributeConjunctionInput from '../index';
import React from 'react';
import {encodeAttributeId} from '../utils';
import {fireEvent, render} from '@testing-library/react';
import {mockEventAttributeDefinition} from 'test/data';
import {range} from 'lodash';
import {ReferencedObjectsProvider} from '../../../../context/referencedObjects';
import {RelationalOperators} from '../../../../utils/constants';

jest.unmock('react-dom');

const renderWithProvider = props =>
	render(
		<ReferencedObjectsProvider>
			<AttributeConjunctionInput {...props} />
		</ReferencedObjectsProvider>
	);

describe('AttributeConjunctionInput', () => {
	it('should render', () => {
		const {container, getAllByText, getByText} = renderWithProvider({
			attributes: range(4).map(index =>
				mockEventAttributeDefinition(index)
			),
			conjunctionCriterion: {
				operatorName: RelationalOperators.EQ,
				propertyName: `attribute/${encodeAttributeId('name-1')}`,
				value: 'test value'
			},
			onChange: jest.fn(),
			touched: {attribute: true, attributeValue: true},
			valid: {attribute: true, attributeValue: true}
		});
		fireEvent.click(getAllByText('displayName-1')[0]);

		expect(getByText('displayName-0')).toBeTruthy();
		expect(getAllByText('displayName-1')[1]).toBeTruthy();
		expect(getByText('displayName-2')).toBeTruthy();
		expect(getByText('displayName-3')).toBeTruthy();

		expect(container).toMatchSnapshot();
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

			fireEvent.click(getByLabelText('Clear'));

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

			expect(queryByLabelText('Clear')).toBeNull();
		});
	});
});
