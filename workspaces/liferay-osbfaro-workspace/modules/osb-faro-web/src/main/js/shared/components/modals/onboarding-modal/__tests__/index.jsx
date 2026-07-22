import mockStore from 'test/mock-store';
import OnboardingModal from '../index';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {noop} from 'lodash';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

describe('OnboardingModal', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<OnboardingModal onClose={noop} />
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
