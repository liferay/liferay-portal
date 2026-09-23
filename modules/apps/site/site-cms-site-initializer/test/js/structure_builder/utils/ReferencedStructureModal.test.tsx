/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {ReferencedStructureModal} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/openReferencedStructureModal';
import {DEFAULT_STRUCTURE} from '../mocks/MockStateProvider';

const OBJECT_DEFINITIONS: Record<string, Partial<ObjectDefinition>> = {
	BASIC_DOCUMENT: {label: {en_US: 'Basic Document'}},
	BLOG: {label: {en_US: 'Blog'}},
};

const renderComponent = () => {
	return render(
		<ReferencedStructureModal
			closeModal={jest.fn()}
			objectDefinitions={OBJECT_DEFINITIONS as ObjectDefinitions}
			onAdd={jest.fn()}
			parentUuid={'parentUuid' as Uuid}
			status="saved"
			structure={DEFAULT_STRUCTURE}
			systemFieldNames={{}}
		/>
	);
};

describe('ReferencedStructureModal', () => {
	let originalResizeObserver: typeof ResizeObserver;

	beforeAll(() => {
		originalResizeObserver = global.ResizeObserver;

		(global as any).ResizeObserver = class {
			observe() {}
			unobserve() {}
			disconnect() {}
		};
	});

	afterAll(() => {
		global.ResizeObserver = originalResizeObserver;
	});

	describe('Add a structure by typing the name in the input and pressing enter', () => {
		it('adds a structure that already exists', async () => {
			renderComponent();

			const input = screen.getByLabelText('content-structures');

			expect(
				screen.queryByLabelText('Remove Basic Document')
			).not.toBeInTheDocument();

			await userEvent.type(input, 'Basic Document');
			await userEvent.type(input, '{Enter}');

			expect(
				screen.getByLabelText('Remove Basic Document')
			).toBeInTheDocument();
		});

		it('does not add a structure whose name does not match any existing structure', async () => {
			renderComponent();

			const input = screen.getByLabelText('content-structures');

			expect(
				screen.queryByLabelText('Remove My Structure')
			).not.toBeInTheDocument();

			await userEvent.type(input, 'My Structure');
			await userEvent.type(input, '{Enter}');

			expect(
				screen.queryByLabelText('Remove My Structure')
			).not.toBeInTheDocument();
		});

		it('adds a structure without regard to lowercase and uppercase letters', async () => {
			renderComponent();

			const input = screen.getByLabelText('content-structures');

			expect(
				screen.queryByLabelText('Remove Blog')
			).not.toBeInTheDocument();

			await userEvent.type(input, 'blog');
			await userEvent.type(input, '{Enter}');

			expect(screen.getByLabelText('Remove Blog')).toBeInTheDocument();
		});
	});
});
