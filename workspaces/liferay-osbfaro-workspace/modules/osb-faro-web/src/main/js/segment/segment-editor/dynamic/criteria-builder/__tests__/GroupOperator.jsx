import GroupOperator from '../GroupOperator';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Conjunctions} from '../../utils/constants';

jest.unmock('react-dom');

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

	it('should offer the All and Any options', () => {
		renderOperator();

		fireEvent.click(screen.getByRole('combobox', {name: /^match logic$/i}));

		expect(screen.getByRole('option', {name: /^all$/i})).toBeInTheDocument();
		expect(screen.getByRole('option', {name: /^any$/i})).toBeInTheDocument();
	});

	it('should call onChange with the OR conjunction when Any is chosen', () => {
		const onChange = jest.fn();

		renderOperator({onChange});

		fireEvent.click(screen.getByRole('combobox', {name: /^match logic$/i}));
		fireEvent.click(screen.getByRole('option', {name: /^any$/i}));

		expect(onChange).toHaveBeenCalledWith(Conjunctions.Or);
	});

	it('should call onChange with the AND conjunction when All is chosen', () => {
		const onChange = jest.fn();

		renderOperator({conjunctionName: Conjunctions.Or, onChange});

		fireEvent.click(screen.getByRole('combobox', {name: /^match logic$/i}));
		fireEvent.click(screen.getByRole('option', {name: /^all$/i}));

		expect(onChange).toHaveBeenCalledWith(Conjunctions.And);
	});
});
