/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode} from 'react';

import {
	PicklistBuilderContext,
	State,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/PicklistBuilderContext';

export const DEFAULT_STATE: State = {
	defaultLanguageLabels: {labels: {}, locale: 'en_US'},
	deletedOptions: false,
	erc: 'picklistERC',
	id: 1,
	name: {en_US: 'Picklist Name'},
	options: new Map([
		['option1ERC', {key: 'option1', name: {en_US: 'Option 1'}}],
	]),
	setDeletedOptions: jest.fn(),
	setErc: jest.fn(),
	setId: jest.fn(),
	setName: jest.fn(),
	setOptions: jest.fn(),
};

export function MockStateProvider({
	children,
	state = DEFAULT_STATE,
}: {
	children: ReactNode;
	state?: Partial<State>;
}) {
	return (
		<PicklistBuilderContext.Provider value={{...DEFAULT_STATE, ...state}}>
			{children}
		</PicklistBuilderContext.Provider>
	);
}
