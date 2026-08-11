/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import mockSub from '../../../../../../frontend-js/frontend-js-web/src/main/resources/META-INF/resources/liferay/util/sub';
import DeleteStructureModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/DeleteStructureModalContent';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual<typeof import('frontend-js-web')>('frontend-js-web'),
	sub: mockSub,
}));

const STRUCTURE_NAME = 'Marketing & Sales/EMEA';

const DEFAULT_PROPS = {
	closeModal: jest.fn(),
	name: STRUCTURE_NAME,
	onDelete: jest.fn(),
	usesCount: 2,
};

describe('DeleteStructureModalContent', () => {
	const languageGet = Liferay.Language.get;

	beforeEach(() => {
		Liferay.Language.get = jest.fn((key: string) =>
			key === 'delete-x' ? 'Delete {0}' : key
		);
	});

	afterEach(() => {
		Liferay.Language.get = languageGet;
	});

	it('renders a structure name containing HTML characters as typed', () => {
		render(<DeleteStructureModalContent {...DEFAULT_PROPS} />);

		expect(
			screen.getByText(`Delete ${STRUCTURE_NAME}`)
		).toBeInTheDocument();
	});
});
