import React from 'react';
import Welcome from '../Welcome';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {noop} from 'lodash';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn(),
}));

describe('Welcome', () => {
	afterEach(cleanup);

	beforeEach(() => {
		useLDPEnabled.mockReturnValue(false);
	});

	it('renders', () => {
		const {container} = render(<Welcome onClose={noop} onNext={noop} />);

		expect(container).toMatchSnapshot();
	});

	it('calls onNext when "Next" is clicked', () => {
		const spy = jest.fn();

		const {queryByText} = render(<Welcome onClose={noop} onNext={spy} />);

		expect(spy).not.toBeCalled();

		fireEvent.click(queryByText('Next'));

		expect(spy).toBeCalled();
	});

	it('shows the Analytics Cloud welcome message for a non-LDP workspace', () => {
		const {queryByText} = render(<Welcome onClose={noop} onNext={noop} />);

		expect(queryByText('Welcome to Analytics Cloud')).toBeTruthy();
	});

	it('shows the Liferay Data Platform welcome message for an LDP workspace', () => {
		useLDPEnabled.mockReturnValue(true);

		const {queryByText} = render(<Welcome onClose={noop} onNext={noop} />);

		expect(queryByText('Welcome to Liferay Data Platform')).toBeTruthy();
	});
});
