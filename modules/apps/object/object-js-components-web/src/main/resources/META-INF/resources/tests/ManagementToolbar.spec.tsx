/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ManagementToolbar} from '../components/ManagementToolbar/index';

const baseProps = {
	backURL: '',
	entityId: 1,
	hasPublishPermission: false,
	hasUpdatePermission: false,
	helpMessage: '',
	label: 'Object Label',
	objectDefinitionExternalReferenceCode: 'ERC',
	objectDefinitionExternalReferenceCodeSaveURL: '',
	onGetEntity: () => Promise.resolve({} as any),
	onSubmit: () => {},
	portletNamespace: '_test_',
};

describe('ManagementToolbar', () => {
	it('renders inheritance label with title attribute when inheritanceTitle is set', () => {
		render(
			<ManagementToolbar
				{...baseProps}
				inheritanceClassName="label-inverse-info"
				inheritanceLabel="inherited"
				inheritanceTitle="strict-inheritance-tooltip"
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(inheritanceLabel).toHaveAttribute(
			'title',
			'strict-inheritance-tooltip'
		);
	});

	it('renders lock icon inside the inheritance label when inheritanceIconSymbol is lock', () => {
		render(
			<ManagementToolbar
				{...baseProps}
				inheritanceClassName="label-inverse-info"
				inheritanceIconSymbol="lock"
				inheritanceLabel="inherited"
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeTruthy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeFalsy();
	});

	it('renders unlock icon inside the inheritance label when inheritanceIconSymbol is unlock', () => {
		render(
			<ManagementToolbar
				{...baseProps}
				inheritanceClassName="label-inverse-info"
				inheritanceIconSymbol="unlock"
				inheritanceLabel="inherited"
			/>
		);

		const [inheritanceLabel] =
			document.getElementsByClassName('label-inverse-info');

		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeTruthy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeFalsy();
	});

	it('does not render an icon or title when inheritanceIconSymbol and inheritanceTitle are unset', () => {
		render(
			<ManagementToolbar
				{...baseProps}
				inheritanceClassName="label-inverse-secondary"
				inheritanceLabel="standard"
			/>
		);

		const [inheritanceLabel] = document.getElementsByClassName(
			'label-inverse-secondary'
		);

		expect(inheritanceLabel).not.toHaveAttribute('title');
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-lock')
		).toBeFalsy();
		expect(
			inheritanceLabel.querySelector('.lexicon-icon-unlock')
		).toBeFalsy();
	});
});
