/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useResource} from '@clayui/data-provider';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import SelectProjectModalContent from '../../js/components/modal/SelectProjectModalContent';
import {mockCloseModal} from '../../tests/js/__mocks__/frontend-js-components-web';
import {mockNavigate} from '../../tests/js/__mocks__/frontend-js-web';

jest.mock('@clayui/data-provider', () => {
	return {
		__esModule: true,
		...((jest.requireActual('@clayui/data-provider') ?? {}) as any),
		useResource: jest.fn(),
	};
});

const mockUseResource = useResource as jest.Mock;

const defaultProps = {
	addProjectURL: 'http://localhost/add-project',
	addTaskURL: 'http://localhost/add-task',
	closeModal: mockCloseModal,
	projectObjectDefinitionId: 123,
};

describe('SelectProjectModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockUseResource.mockReturnValue({
			loadMore: jest.fn(),
			resource: {
				items: [
					{
						embedded: {
							id: 123,
							scopeId: 123,
							title: 'project-1',
						},
					},
					{
						embedded: {
							id: 234,
							scopeId: 234,
							title: 'project-2',
						},
					},
				],
			},
		});
	});

	afterAll(() => {
		jest.restoreAllMocks();
	});

	describe('modal controls', () => {
		it('calls closeModal when cancel button is clicked', () => {
			render(<SelectProjectModalContent {...defaultProps} />);

			fireEvent.click(screen.getByText('cancel'));

			expect(mockCloseModal).toHaveBeenCalledTimes(1);
		});

		it('does not navigate when save is clicked without project selection', () => {
			render(<SelectProjectModalContent {...defaultProps} />);

			fireEvent.click(screen.getByText('save'));

			expect(mockNavigate).not.toHaveBeenCalled();
			expect(
				screen.getByText('this-field-is-required')
			).toBeInTheDocument();
		});
	});

	describe('rendering', () => {
		it('fetches projects with correct API parameters', () => {
			render(<SelectProjectModalContent {...defaultProps} />);

			expect(screen.getByText('cancel')).toBeInTheDocument();
			expect(screen.getByText('new-task')).toBeInTheDocument();
			expect(screen.getByText('project')).toBeInTheDocument();
			expect(screen.getByText('save')).toBeInTheDocument();
			expect(screen.getByText('select-a-project')).toBeInTheDocument();

			expect(mockUseResource).toHaveBeenCalledWith(
				expect.objectContaining({
					link: expect.stringContaining(
						'/o/search/v1.0/search?emptySearch=true&filter=objectDefinitionId eq 123&nestedFields=embedded'
					),
				})
			);
		});
	});

	describe('when no projects exist', () => {
		beforeEach(() => {
			mockUseResource.mockReturnValue({
				loadMore: jest.fn(),
				resource: {
					items: [],
				},
			});
		});

		it('navigates to add project URL when new-project button is clicked', () => {
			render(<SelectProjectModalContent {...defaultProps} />);

			fireEvent.click(screen.getByLabelText('project'));

			expect(screen.getByText('new-project')).toBeInTheDocument();
			expect(screen.getByText('no-projects-created')).toBeInTheDocument();

			fireEvent.click(screen.getByText('new-project'));

			expect(mockNavigate).toHaveBeenCalledTimes(1);

			const navigateArgs = mockNavigate.mock.calls[0][0];

			expect(navigateArgs).toBeInstanceOf(URL);
			expect(navigateArgs.pathname).toBe('/add-project');
		});
	});

	describe('when projects exist', () => {
		it('navigates to add task URL with correct parameters when save is clicked', () => {
			render(<SelectProjectModalContent {...defaultProps} />);

			fireEvent.click(screen.getByLabelText('project'));

			expect(screen.getByText('project-1')).toBeInTheDocument();
			expect(screen.getByText('project-2')).toBeInTheDocument();

			fireEvent.click(screen.getByText('project-1'));

			expect(screen.getByLabelText('project')).toHaveTextContent(
				'project-1'
			);

			fireEvent.click(screen.getByText('save'));

			expect(mockNavigate).toHaveBeenCalledTimes(1);

			const navigateArgs = mockNavigate.mock.calls[0][0];

			expect(navigateArgs).toBeInstanceOf(URL);
			expect(navigateArgs.pathname).toBe('/add-task');
			expect(navigateArgs.searchParams.get('projectGroupId')).toBe('123');
			expect(navigateArgs.searchParams.get('projectId')).toBe('123');
			expect(navigateArgs.searchParams.get('redirect')).toBe(
				window.location.href
			);
		});
	});
});
