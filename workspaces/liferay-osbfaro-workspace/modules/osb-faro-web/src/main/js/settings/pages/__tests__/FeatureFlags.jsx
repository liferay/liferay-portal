import FeatureFlags from 'settings/pages/FeatureFlags';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {
	FEATURE_FLAGS,
	FEATURE_FLAGS_STORAGE_KEY
} from 'shared/util/feature-flags';
import {reloadPage} from 'shared/util/router';

jest.unmock('react-dom');

jest.mock('shared/util/router', () => ({
	...jest.requireActual('shared/util/router'),
	reloadPage: jest.fn()
}));

// Isolate the panel from the settings chrome (sidebar, notifications, Apollo).
jest.mock(
	'settings/components/base-page/BasePage',
	() =>
		({children, pageTitle}) =>
			(
				<div>
					<h1>{pageTitle}</h1>

					{children}
				</div>
			)
);

describe('FeatureFlags', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(() => {
		cleanup();
		window.localStorage.clear();
	});

	it('renders a toggle for every registered feature flag', () => {
		const {container, getByText} = render(<FeatureFlags />);

		expect(getByText(/^feature flags$/i)).toBeTruthy();
		expect(
			container.querySelectorAll('[data-testid="toggle-switch-input"]')
		).toHaveLength(FEATURE_FLAGS.length);
	});

	it('persists a toggle change and reloads the page', () => {
		const {container} = render(<FeatureFlags />);

		fireEvent.click(
			container.querySelector('[data-testid="toggle-switch-input"]')
		);

		expect(
			JSON.parse(window.localStorage.getItem(FEATURE_FLAGS_STORAGE_KEY))
		).toEqual({[FEATURE_FLAGS[0].key]: true});

		expect(reloadPage).toHaveBeenCalledTimes(1);
	});
});
