import GroupOperator from '../GroupOperator';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {Conjunctions} from '../../utils/constants';
import {selectPickerOption} from 'test/helpers';

const renderOperator = ({conjunctionName = Conjunctions.And, onChange} = {}) =>
	render(
		<GroupOperator
			conjunctionName={conjunctionName}
			id='group-operator'
			onChange={onChange ?? jest.fn()}
		/>
	);

describe('GroupOperator', () => {
	afterEach(cleanup);

	it('should render the AND conjunction as "All" followed by the sentence', () => {
		const {container} = renderOperator();

		expect(
			screen.getByRole('combobox', {name: /^match logic$/i})
		).toHaveTextContent(/^all/i);
		expect(container).toHaveTextContent(
			/of these criteria must be met\./i
		);
	});

	it('should render the OR conjunction as "Any"', () => {
		renderOperator({conjunctionName: Conjunctions.Or});

		expect(
			screen.getByRole('combobox', {name: /^match logic$/i})
		).toHaveTextContent(/^any/i);
	});

	it.each`
		from                | option      | expected
		${Conjunctions.And} | ${/^any$/i} | ${Conjunctions.Or}
		${Conjunctions.Or}  | ${/^all$/i} | ${Conjunctions.And}
	`(
		'should call onChange with $expected when $option is chosen',
		({expected, from, option}) => {
			const onChange = jest.fn();

			renderOperator({conjunctionName: from, onChange});

			selectPickerOption(/^match logic$/i, option);

			expect(onChange).toHaveBeenCalledWith(expected);
		}
	);
});
