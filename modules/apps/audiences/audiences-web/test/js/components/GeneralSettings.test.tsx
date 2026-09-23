/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useRef, useState} from 'react';

import GeneralSettings from '../../../src/main/resources/META-INF/resources/js/components/GeneralSettings';
import {Site} from '../../../src/main/resources/META-INF/resources/js/types';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(),
}));

const SCOPE_SITES: Site[] = [
	{
		descriptiveName: 'Liferay DXP',
		externalReferenceCode: 'SITE-1',
		id: 1,
		logo: '/logo-1',
	},
	{
		descriptiveName: 'Liferay Design',
		externalReferenceCode: 'SITE-2',
		id: 2,
		logo: '/logo-2',
	},
];

function GeneralSettingsWrapper({errorMessage}: {errorMessage?: string}) {
	const [expanded, setExpanded] = useState(false);
	const [externalReferenceCode, setExternalReferenceCode] = useState('');

	const externalReferenceCodeInputRef = useRef<HTMLInputElement>(null);

	return (
		<GeneralSettings
			companyGroupERC=""
			expanded={expanded}
			externalReferenceCode={externalReferenceCode}
			externalReferenceCodeInputRef={externalReferenceCodeInputRef}
			namespace="_test_"
			onExpandedChange={setExpanded}
			onExternalReferenceCodeChange={setExternalReferenceCode}
			onScopeChange={() => {}}
			saveErrors={{externalReferenceCode: errorMessage}}
			scope="all"
		/>
	);
}

describe('GeneralSettings', () => {
	it('starts collapsed, expands on interaction, and edits the external reference code', async () => {
		render(<GeneralSettingsWrapper />);

		const toggle = screen.getByRole('button', {name: 'general-settings'});

		expect(toggle.getAttribute('aria-expanded')).toBe('false');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('true');

		const input = screen.getByRole('textbox', {
			name: 'erc',
		});

		expect(input.getAttribute('id')).toBe(
			'_test_externalReferenceCodeInput'
		);
		expect((input as HTMLInputElement).value).toBe('');

		await userEvent.type(input, 'ABC-123');

		expect((input as HTMLInputElement).value).toBe('ABC-123');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('false');

		await userEvent.click(toggle);

		expect(toggle.getAttribute('aria-expanded')).toBe('true');
		expect(
			(
				screen.getByRole('textbox', {
					name: 'erc',
				}) as HTMLInputElement
			).value
		).toBe('ABC-123');
	});

	it('announces the error message and marks the input invalid', () => {
		render(
			<GeneralSettings
				companyGroupERC=""
				expanded
				externalReferenceCode="ERC-123"
				externalReferenceCodeInputRef={React.createRef()}
				namespace="_test_"
				onExpandedChange={() => {}}
				onExternalReferenceCodeChange={() => {}}
				onScopeChange={() => {}}
				saveErrors={{externalReferenceCode: 'error-message'}}
				scope="all"
			/>
		);

		const alert = screen.getByRole('alert');

		expect(alert).toHaveTextContent('error-message');

		const input = screen.getByRole('textbox', {name: 'erc'});

		expect(input).toHaveAttribute('aria-invalid', 'true');
		expect(input).toHaveAttribute(
			'aria-describedby',
			'_test_externalReferenceCodeError'
		);

		expect(alert).toContainElement(
			document.getElementById('_test_externalReferenceCodeError')
		);
	});

	describe('scope', () => {
		const renderGeneralSettings = ({
			groupERCsError,
			scope,
		}: {
			groupERCsError?: string;
			scope: 'all' | Site[];
		}) => {
			const onScopeChange = jest.fn();

			render(
				<GeneralSettings
					companyGroupERC="GLOBAL"
					expanded
					externalReferenceCode="ERC-123"
					externalReferenceCodeInputRef={React.createRef()}
					namespace="_test_"
					onExpandedChange={() => {}}
					onExternalReferenceCodeChange={() => {}}
					onScopeChange={onScopeChange}
					saveErrors={{groupERCs: groupERCsError}}
					scope={scope}
				/>
			);

			return onScopeChange;
		};

		it('shows the audience available for all sites and hides the scope sites', () => {
			renderGeneralSettings({scope: 'all'});

			expect(
				screen.getByRole('checkbox', {
					name: 'make-this-audience-available-for-all-sites',
				})
			).toBeChecked();

			expect(screen.queryByText('scope-sites')).toBeNull();
		});

		it('lists the scope sites and removes one', async () => {
			const onScopeChange = renderGeneralSettings({scope: SCOPE_SITES});

			expect(
				screen.getByRole('checkbox', {
					name: 'make-this-audience-available-for-all-sites',
				})
			).not.toBeChecked();

			const scopeSitesList = screen.getByRole('list', {
				name: 'scope-sites',
			});

			expect(
				within(scopeSitesList).getAllByRole('listitem')
			).toHaveLength(2);

			await userEvent.click(
				within(scopeSitesList).getAllByRole('button', {
					name: 'remove-x',
				})[0]
			);

			expect(onScopeChange).toHaveBeenCalledWith([SCOPE_SITES[1]]);
		});

		it('makes the audience available for all sites', async () => {
			const onScopeChange = renderGeneralSettings({scope: SCOPE_SITES});

			await userEvent.click(
				screen.getByRole('checkbox', {
					name: 'make-this-audience-available-for-all-sites',
				})
			);

			expect(onScopeChange).toHaveBeenCalledWith('all');
		});

		it('shows the scope error under the sites', () => {
			renderGeneralSettings({groupERCsError: 'error-message', scope: []});

			expect(screen.getByRole('alert')).toHaveTextContent(
				'error-message'
			);
		});
	});
});
