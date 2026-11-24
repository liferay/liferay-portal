import React from 'react';
import ToggleSwitch from '../ToggleSwitch';
import {cleanup, render} from '@testing-library/react';

describe('ToggleSwitch', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<ToggleSwitch label='foo' name='foo' />);
		expect(container).toMatchSnapshot();
	});

	it('should render with an initial value', () => {
		const {getByTestId} = render(
			<ToggleSwitch checked label='foo' name='foo' />
		);
		expect(getByTestId('toggle-switch-input').checked).toBeTrue();
	});
});
