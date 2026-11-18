import ErrorDisplay from '../ErrorDisplay';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {noop} from 'lodash';

jest.unmock('react-dom');

describe('ErrorDisplay', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<ErrorDisplay />);
		expect(container).toMatchSnapshot();
	});

	it('should render with button', () => {
		const {queryByText} = render(<ErrorDisplay onReload={noop} />);
		expect(queryByText(/^reload$/i)).toBeTruthy();
	});
});
