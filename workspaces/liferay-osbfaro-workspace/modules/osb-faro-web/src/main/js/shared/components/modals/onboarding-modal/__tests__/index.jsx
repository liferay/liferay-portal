import mockStore from 'test/mock-store';
import OnboardingModal, {getOnboardingState} from '../index';
import React from 'react';
import {cleanup, screen} from '@testing-library/react';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

const renderOnboardingModal = (props = {}) =>
	renderModal(observer => (
		<Provider store={mockStore()}>
			<OnboardingModal
				groupId="23"
				observer={observer}
				onClose={noop}
				{...props}
			/>
		</Provider>
	));

describe('OnboardingModal', () => {
	afterEach(() => {
		cleanup();

		window.sessionStorage.clear();
	});

	it('renders', async () => {
		await renderModal(observer => (
			<Provider store={mockStore()}>
				<OnboardingModal observer={observer} onClose={noop} />
			</Provider>
		));

		expect(document.body).toMatchSnapshot();
	});

	it('renders the step of its initial state', async () => {
		await renderOnboardingModal({
			initialState: {dxpConnected: false, groupId: '23', step: 3},
		});

		expect(screen.getByText('Add Other Members')).toBeInTheDocument();
	});

	it('stores its state for the next page of the workspace', async () => {
		await renderOnboardingModal({
			initialState: {dxpConnected: true, groupId: '23', step: 3},
		});

		expect(getOnboardingState('23')).toEqual({
			dxpConnected: true,
			groupId: '23',
			step: 3,
		});
	});

	it('clears its state when it closes', async () => {
		const {unmount} = await renderOnboardingModal();

		expect(getOnboardingState('23')).not.toBeNull();

		unmount();

		expect(getOnboardingState('23')).toBeNull();
	});
});

describe('getOnboardingState', () => {
	afterEach(() => {
		window.sessionStorage.clear();
	});

	it.each([
		[
			'the state of the workspace',
			'{"dxpConnected":true,"groupId":"23","step":3}',
			{dxpConnected: true, groupId: '23', step: 3},
		],
		[
			'null for the state of another workspace',
			'{"dxpConnected":true,"groupId":"24","step":3}',
			null,
		],
		[
			'null for a state without a step',
			'{"dxpConnected":true,"groupId":"23"}',
			null,
		],
		['null for a value that is not JSON', 'onboarding', null],
	])('returns %s', (name, storedValue, expected) => {
		window.sessionStorage.setItem('onboardingState', storedValue);

		expect(getOnboardingState('23')).toEqual(expected);
	});

	it('returns null when nothing is stored', () => {
		expect(getOnboardingState('23')).toBeNull();
	});
});
