/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import MemberRenderer from '../../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/MemberRenderer';

const _renderMember = (props: {
	itemData: any;
	ownerId?: string;
	value: string;
}) => render(<MemberRenderer {...(props as any)} />);

describe('MemberRenderer', () => {
	it('marks a user as the owner when the id matches the ownerId', () => {
		_renderMember({
			itemData: {id: 123},
			ownerId: '123',
			value: 'Jane Owner',
		});

		expect(screen.getByText('(owner)')).toBeInTheDocument();
	});

	it('renders a non-owner user with a portrait and no owner badge', () => {
		_renderMember({
			itemData: {id: 1},
			ownerId: '999',
			value: 'John Member',
		});

		expect(screen.queryByText('(owner)')).not.toBeInTheDocument();
		expect(screen.getByAltText('John Member')).toBeInTheDocument();
	});

	it('renders a user group row without an owner badge or portrait', () => {
		_renderMember({
			itemData: {id: 5, numberOfUserAccounts: 3},
			value: 'Marketing',
		});

		expect(screen.getByText('Marketing')).toBeInTheDocument();
		expect(screen.queryByText('(owner)')).not.toBeInTheDocument();
		expect(screen.queryByAltText('Marketing')).not.toBeInTheDocument();
	});
});
