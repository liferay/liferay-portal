import BooleanInput from '../BooleanInput';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {Property} from 'shared/util/records';

jest.unmock('react-dom');

describe('BooleanInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getByText} = render(
			<BooleanInput
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
			/>
		);
		fireEvent.click(getByText('Select an option'));

		expect(getByText(/^true$/i)).toBeTruthy();
		expect(getByText(/^false$/i)).toBeTruthy();
		expect(container).toMatchSnapshot();
	});

	it('should render with data', () => {
		const {container, getAllByText, getByText} = render(
			<BooleanInput
				displayValue='Do Not Call'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				value='true'
			/>
		);
		fireEvent.click(getByText(/^true$/i));

		expect(getAllByText(/^true$/i)[1]).toBeTruthy();
		expect(getByText(/^false$/i)).toBeTruthy();
		expect(container).toMatchSnapshot();
	});
});
