/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import PageContentSelectors from '../../../src/main/resources/META-INF/resources/js/components/PageContentSelectors';

const mockProps = {
	deactivateSimulationURL: 'http://test.com',
	namespace: 'test_namespace',
	portletNamespace: 'portlet_test_namespace',
	segmentationEnabled: true,
	segmentsCompanyConfigurationURL: 'http://test.com',
	segmentsEntries: [
		{id: 0, name: 'Anyone'},
		{id: 50959, name: 'Liferayers'},
	],
	segmentsExperiences: [
		{
			segmentsEntryName: 'Liferayers',
			segmentsExperienceActive: true,
			segmentsExperienceId: '50963',
			segmentsExperienceName: 'Experience 1',
			statusLabel: 'Active',
		},
		{
			segmentsEntryName: 'Anyone',
			segmentsExperienceActive: true,
			segmentsExperienceId: '43499',
			segmentsExperienceName: 'Default',
			statusLabel: 'Active',
		},
	],
	simulateSegmentsEntriesURL: 'http://test.com',
};

jest.mock('frontend-js-web', () => ({
	debounce: jest.fn(),
	sub: jest.fn(),
}));

describe('PageContentSelectors', () => {
	beforeEach(() => {
		global.Liferay.on = jest.fn().mockReturnValue({
			detach: jest.fn(),
		});
	});

	afterEach(() => {
		jest.restoreAllMocks();
		jest.clearAllMocks();
	});

	it('renders a selector to choose between “Experiences” or “Segments”', () => {
		jest.useFakeTimers();

		render(<PageContentSelectors {...mockProps} />);

		const previewBySelector = screen.getByRole('combobox', {
			name: /preview-by/i,
		});

		expect(screen.getByText('preview-by')).toBeInTheDocument();
		expect(previewBySelector).toBeInTheDocument();
		expect(previewBySelector).toHaveTextContent('segments');

		userEvent.click(previewBySelector);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.getElementById('segments')).toBeInTheDocument();
		expect(document.getElementById('experiences')).toBeInTheDocument();
	});

	it('If no segments available renders empty segments message', () => {
		render(<PageContentSelectors {...mockProps} segmentsEntries={[]} />);

		expect(
			screen.getByText('no-segments-have-been-added-yet')
		).toBeInTheDocument();
	});

	it('If no experiences available renders empty experiences message', async () => {
		jest.useFakeTimers();

		render(
			<PageContentSelectors {...mockProps} segmentsExperiences={[]} />
		);

		const previewBySelector = screen.getByRole('combobox', {
			name: /preview-by/i,
		});

		userEvent.click(previewBySelector);

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.click(document.getElementById('experiences'));

		expect(
			screen.getByText('no-experiences-have-been-added-yet')
		).toBeInTheDocument();
	});
});
