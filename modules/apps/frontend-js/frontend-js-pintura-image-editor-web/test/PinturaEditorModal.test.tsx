/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useModal} from '@clayui/modal';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PinturaEditorModal from '../src/main/resources/META-INF/resources/js/PinturaEditorModal';

const mockBlob = new Blob(['image-data'], {type: 'image/jpeg'});
const mockProcessImage = jest.fn(() =>
	Promise.resolve({dest: mockBlob, imageState: {}})
);

jest.mock('@pqina/pintura', () => ({
	getEditorDefaults: jest.fn(() => ({})),
}));

jest.mock('@pqina/react-pintura', () => ({
	PinturaEditor: React.forwardRef(
		(_props: unknown, ref: React.ForwardedRef<unknown>) => {
			React.useImperativeHandle(ref, () => ({
				processImage: mockProcessImage,
			}));

			return <div data-testid="pintura-editor" />;
		}
	),
}));

const PinturaEditorModalWrapper = ({
	defaultOpen,
	onSave,
}: {
	defaultOpen: boolean;
	onSave: (blob: Blob) => void;
}) => {
	const {observer, onOpenChange, open} = useModal({defaultOpen});

	return (
		<>
			<button onClick={() => onOpenChange(true)}>open modal</button>

			<PinturaEditorModal
				imageName="photo.jpg"
				imageUrl="https://example.com/photo.jpg"
				observer={observer}
				onOpenChange={onOpenChange}
				onSave={onSave}
				open={open}
			/>
		</>
	);
};

describe('PinturaEditorModal', () => {
	beforeAll(() => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation((key) => {
			if (key === 'edit-x') {
				return 'Edit {0}';
			}

			return key;
		});
	});

	afterAll(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		mockProcessImage.mockClear();
	});

	it('renders with the correct title', () => {
		render(
			<PinturaEditorModalWrapper
				defaultOpen
				onSave={jest.fn()}
			/>
		);

		expect(
			screen.getByText('Edit photo.jpg')
		).toBeInTheDocument();
	});

	it('renders the Pintura editor', () => {
		render(
			<PinturaEditorModalWrapper
				defaultOpen
				onSave={jest.fn()}
			/>
		);

		expect(screen.getByTestId('pintura-editor')).toBeInTheDocument();
	});

	it('closes the modal when Cancel is clicked', async () => {
		render(
			<PinturaEditorModalWrapper
				defaultOpen
				onSave={jest.fn()}
			/>
		);

		await userEvent.click(screen.getByText('cancel'));

		await waitFor(() => {
			expect(
				screen.queryByTestId('pintura-editor')
			).not.toBeInTheDocument();
		});
	});

	it('calls onSave with the processed blob when Done is clicked', async () => {
		const onSave = jest.fn();

		render(
			<PinturaEditorModalWrapper
				defaultOpen
				onSave={onSave}
			/>
		);

		await userEvent.click(screen.getByText('done'));

		await waitFor(() => {
			expect(mockProcessImage).toHaveBeenCalledTimes(1);
			expect(onSave).toHaveBeenCalledWith(mockBlob);
		});
	});

	it('closes the modal after Done is clicked', async () => {
		render(
			<PinturaEditorModalWrapper
				defaultOpen
				onSave={jest.fn()}
			/>
		);

		await userEvent.click(screen.getByText('done'));

		await waitFor(() => {
			expect(
				screen.queryByTestId('pintura-editor')
			).not.toBeInTheDocument();
		});
	});

	it('does not render when open is false', () => {
		render(
			<PinturaEditorModalWrapper
				defaultOpen={false}
				onSave={jest.fn()}
			/>
		);

		expect(
			screen.queryByTestId('pintura-editor')
		).not.toBeInTheDocument();
	});
});
