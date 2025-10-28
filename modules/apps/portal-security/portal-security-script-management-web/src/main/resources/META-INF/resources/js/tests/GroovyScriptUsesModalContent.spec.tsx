/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {GroovyScriptUsesModalContent} from '../components/GroovyScriptUsesModalContent';

const groovyScriptUsesMock = [
	{
		companyWebId: 'company1.com',
		sourceName: 'company1ActiveGroovyObjectAction',
		sourceURL: 'http://exampleMock.com',
	},
];

describe('GroovyScriptUsesModalContent component', () => {
	it('check if modal title renders correctly', () => {
		const {getByText} = render(
			<GroovyScriptUsesModalContent
				groovyScriptUses={[]}
				handleOnClose={() => {}}
			/>
		);

		const modalTitle = getByText('setting-cannot-be-deactivated');

		expect(modalTitle).toBeInTheDocument();
	});

	it('check if modal description renders correctly', () => {
		const {getByText} = render(
			<GroovyScriptUsesModalContent
				groovyScriptUses={[]}
				handleOnClose={() => {}}
			/>
		);

		const modalFirstDescription = getByText(
			'resolve-all-active-scripting-uses-before-proceeding-you-can-deactivate-the-source-entity-or-remove-the-script'
		);

		expect(modalFirstDescription).toBeInTheDocument();
	});

	it('check if table headers renders correctly', () => {
		const {getByText} = render(
			<GroovyScriptUsesModalContent
				groovyScriptUses={groovyScriptUsesMock}
				handleOnClose={() => {}}
			/>
		);

		const scriptSourceTableHeader = getByText('script-source');
		const instanceWebIdTableHeader = getByText('instance-web-id');

		expect(scriptSourceTableHeader).toBeInTheDocument();
		expect(instanceWebIdTableHeader).toBeInTheDocument();
	});

	it('check if source name renders correctly', () => {
		const {getByText} = render(
			<GroovyScriptUsesModalContent
				groovyScriptUses={groovyScriptUsesMock}
				handleOnClose={() => {}}
			/>
		);

		const sourceName = getByText('company1ActiveGroovyObjectAction');

		expect(sourceName).toBeInTheDocument();
		expect(sourceName).toHaveAttribute(
			'href',
			groovyScriptUsesMock[0].sourceURL
		);
		expect(sourceName.tagName).toBe('A');
	});

	it('check if company web id renders correctly', () => {
		const {getByText} = render(
			<GroovyScriptUsesModalContent
				groovyScriptUses={groovyScriptUsesMock}
				handleOnClose={() => {}}
			/>
		);

		const companyWebId = getByText('company1.com');

		expect(companyWebId).toBeInTheDocument();
	});
});
