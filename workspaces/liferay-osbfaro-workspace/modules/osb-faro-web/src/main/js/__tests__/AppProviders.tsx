import AppProviders from '../AppProviders';
import React, {useContext} from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {OnboardingContext} from 'shared/context/onboarding';

jest.unmock('react-dom');

const OnboardingTrigger = () => {
	const {onboardingTriggered, setOnboardingTriggered} =
		useContext(OnboardingContext);

	return (
		<button onClick={setOnboardingTriggered} type="button">
			{onboardingTriggered ? 'triggered' : 'not-triggered'}
		</button>
	);
};

describe('AppProviders', () => {
	afterEach(() => {
		window.sessionStorage.clear();
	});

	it('keeps the onboarding triggered when the app mounts again', () => {
		const {unmount} = render(
			<AppProviders>
				<OnboardingTrigger />
			</AppProviders>
		);

		fireEvent.click(screen.getByRole('button', {name: 'not-triggered'}));

		unmount();

		render(
			<AppProviders>
				<OnboardingTrigger />
			</AppProviders>
		);

		expect(
			screen.getByRole('button', {name: 'triggered'})
		).toBeInTheDocument();
	});
});
