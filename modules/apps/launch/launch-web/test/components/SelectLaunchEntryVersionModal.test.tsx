/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import React from 'react';

import {LaunchEntryVersion} from '../../src/main/resources/META-INF/resources/js/api/launches';
import SelectLaunchEntryVersionModal from '../../src/main/resources/META-INF/resources/js/components/SelectLaunchEntryVersionModal';

const LAUNCH_ENTRY_VERSIONS: LaunchEntryVersion[] = [
	{
		label: 'Version 1.0',
		modified: '2026-01-01T00:00:00Z',
		status: 0,
		userName: 'Test User',
		versionKey: 'v1',
	},
	{
		label: 'Version 2.0',
		modified: '2026-02-01T00:00:00Z',
		status: 0,
		userName: 'Test User',
		versionKey: 'v2',
	},
	{
		label: 'Version 3.0',
		modified: '2026-03-01T00:00:00Z',
		status: 0,
		userName: 'Test User',
		versionKey: 'v3',
	},
];

const DEFAULT_PROPS = {
	launchEntryVersions: LAUNCH_ENTRY_VERSIONS,
	onClose: () => {},
	onSelect: () => {},
};

function renderOpenModal(
	props: Partial<typeof DEFAULT_PROPS> & {baseVersionKey?: string} = {}
) {
	render(<SelectLaunchEntryVersionModal {...DEFAULT_PROPS} {...props} />);

	act(() => {
		jest.runAllTimers();
	});
}

describe('SelectLaunchEntryVersionModal', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('falls back to the first version when the list is empty', () => {
		renderOpenModal({launchEntryVersions: []});

		expect(screen.queryAllByRole('radio')).toHaveLength(0);
	});

	it('falls back to the first version when the preselected base version key does not match any version', () => {
		renderOpenModal({baseVersionKey: 'unknown'});

		const radios = screen.getAllByRole('radio');

		expect(radios[0]).toBeChecked();
		expect(radios[1]).not.toBeChecked();
		expect(radios[2]).not.toBeChecked();
	});

	it('falls back to the first version when the preselected base version key is not provided', () => {
		renderOpenModal();

		const radios = screen.getAllByRole('radio');

		expect(radios[0]).toBeChecked();
		expect(radios[1]).not.toBeChecked();
		expect(radios[2]).not.toBeChecked();
	});

	it('preselects the version that matches the preselected base version key', () => {
		renderOpenModal({baseVersionKey: 'v2'});

		const radios = screen.getAllByRole('radio');

		expect(radios[0]).not.toBeChecked();
		expect(radios[1]).toBeChecked();
		expect(radios[2]).not.toBeChecked();
	});
});
