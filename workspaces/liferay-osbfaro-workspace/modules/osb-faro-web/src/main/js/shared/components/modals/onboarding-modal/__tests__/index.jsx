import mockStore from 'test/mock-store';
import OnboardingModal from '../index';
import React from 'react';
import {cleanup} from '@testing-library/react';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

describe('OnboardingModal', () => {
	afterEach(cleanup);

	it('renders', async () => {
		await renderModal(observer => (
			<Provider store={mockStore()}>
				<OnboardingModal observer={observer} onClose={noop} />
			</Provider>
		));

		expect(document.body).toMatchSnapshot();
	});
});
