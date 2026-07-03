/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ScopesDisplay from '../../../../src/main/resources/META-INF/resources/js/common/components/ScopesDisplay';
import {Space as Scope} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('{0}', arg),
}));

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === 'available-in-spaces-x') {
		return 'Available in spaces: {0}';
	}

	return key;
});

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
};

const scopes = [
	{
		name: 'First scope',
		settings: {
			logoColor: 'outline-1',
		},
	},
	{
		name: 'Second scope',
		settings: {
			logoColor: 'outline-1',
		},
	},
	{
		name: 'Third scope',
		settings: {
			logoColor: 'outline-1',
		},
	},
] as Scope[];

const allScopes = [
	{
		id: -1,
	},
] as Scope[];

const allScopesLabel = Liferay.Language.get('all-spaces');
const availableInScopeLabel = Liferay.Language.get('available-in-spaces-x');

describe('ScopesDisplay', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders all scopes when no scopes are provided', () => {
		render(
			<ScopesDisplay
				allScopesLabel={allScopesLabel}
				availableInScopeLabel={availableInScopeLabel}
				scopes={[]}
			/>
		);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	it('renders all scopes when a scope with id -1 is provided', () => {
		render(
			<ScopesDisplay
				allScopesLabel={allScopesLabel}
				availableInScopeLabel={availableInScopeLabel}
				scopes={allScopes}
			/>
		);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	describe('When one scope is provided', () => {
		it('renders the first letter and the name for the single scope', () => {
			render(
				<ScopesDisplay
					allScopesLabel={allScopesLabel}
					availableInScopeLabel={availableInScopeLabel}
					scopes={[scopes[0]]}
				/>
			);

			expect(screen.getByText(scopes[0].name)).toBeInTheDocument();

			expect(
				screen.getByText(scopes[0].name.charAt(0).toUpperCase())
			).toBeInTheDocument();

			expect(screen.queryByText('+')).not.toBeInTheDocument();
		});
	});

	describe('When multiple scopes are provided', () => {
		it('renders the first letter and the name for the first scope', () => {
			render(
				<ScopesDisplay
					allScopesLabel={allScopesLabel}
					availableInScopeLabel={availableInScopeLabel}
					scopes={scopes}
				/>
			);

			expect(screen.getByText(scopes[0].name)).toBeInTheDocument();
			expect(
				screen.getByText(scopes[0].name.charAt(0).toUpperCase())
			).toBeInTheDocument();
		});

		it('renders a badge with the count of additional scopes', () => {
			render(
				<ScopesDisplay
					allScopesLabel={allScopesLabel}
					availableInScopeLabel={availableInScopeLabel}
					scopes={scopes}
				/>
			);

			const additionalScopesCount = scopes.length - 1;
			expect(
				screen.getByText(`+${additionalScopesCount}`)
			).toBeInTheDocument();
		});

		it('shows a tooltip with the names of additional scopes on badge hover', () => {
			render(
				<ScopesDisplay
					allScopesLabel={allScopesLabel}
					availableInScopeLabel={availableInScopeLabel}
					scopes={scopes}
				/>
			);

			const additionalScopesCount = scopes.length - 1;
			const scopeNames = `${scopes[0].name}, ${scopes[1].name}, ${scopes[2].name}`;

			const badge = screen.getByText(`+${additionalScopesCount}`);
			expect(badge.parentElement).toHaveAttribute(
				'title',
				`Available in spaces: ${scopeNames}`
			);
		});
	});
});
