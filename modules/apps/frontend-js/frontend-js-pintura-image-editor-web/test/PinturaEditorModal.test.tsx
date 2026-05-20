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
const mockProcessImage = jest.fn();

jest.mock('@clayui/modal', () => {

	// eslint-disable-next-line @typescript-eslint/no-require-imports

	const ReactModule = require('react');

	const Modal = ({children}: {children: React.ReactNode}) => (
		<div role="dialog">{children}</div>
	);

	Modal.Header = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	Modal.Body = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	Modal.Footer = ({last}: {last: React.ReactNode}) => <div>{last}</div>;
	Modal.Item = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	Modal.ItemGroup = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);
	Modal.Title = ({children}: {children: React.ReactNode}) => (
		<div>{children}</div>
	);

	return {
		__esModule: true,
		default: Modal,
		useModal: ({defaultOpen}: {defaultOpen?: boolean} = {}) => {
			const [open, setOpen] = ReactModule.useState(!!defaultOpen);

			return {observer: {}, onOpenChange: setOpen, open};
		},
	};
});

jest.mock('@pqina/pintura', () => ({
	getEditorDefaults: jest.fn(() => ({})),
}));

jest.mock('@pqina/react-pintura', () => {

	// eslint-disable-next-line @typescript-eslint/no-require-imports

	const ReactModule = require('react');

	return {
		PinturaEditor: ReactModule.forwardRef(
			(
				props: {onProcess: (result: {dest: Blob}) => void},
				ref: React.ForwardedRef<unknown>
			) => {
				ReactModule.useImperativeHandle(ref, () => ({
					editor: {
						processImage: () => {
							mockProcessImage();

							return Promise.resolve().then(() =>
								props.onProcess({dest: mockBlob})
							);
						},
					},
				}));

				return <div data-testid="pintura-editor" />;
			}
		),
	};
});

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
		render(<PinturaEditorModalWrapper defaultOpen onSave={jest.fn()} />);

		expect(screen.getByText('Edit photo.jpg')).toBeInTheDocument();
	});

	it('renders the Pintura editor', () => {
		render(<PinturaEditorModalWrapper defaultOpen onSave={jest.fn()} />);

		expect(screen.getByTestId('pintura-editor')).toBeInTheDocument();
	});

	it('closes the modal when Cancel is clicked', async () => {
		render(<PinturaEditorModalWrapper defaultOpen onSave={jest.fn()} />);

		await userEvent.click(screen.getByText('cancel'));

		await waitFor(() => {
			expect(
				screen.queryByTestId('pintura-editor')
			).not.toBeInTheDocument();
		});
	});

	it('calls onSave with the processed blob when Done is clicked', async () => {
		const onSave = jest.fn();

		render(<PinturaEditorModalWrapper defaultOpen onSave={onSave} />);

		await userEvent.click(screen.getByText('done'));

		await waitFor(() => {
			expect(mockProcessImage).toHaveBeenCalledTimes(1);
			expect(onSave).toHaveBeenCalledWith(mockBlob);
		});
	});

	it('shows a loading state on Done while saving', async () => {
		let resolveOnSave: () => void;

		const onSave = jest.fn(
			() =>
				new Promise<void>((resolve) => {
					resolveOnSave = resolve;
				})
		);

		render(<PinturaEditorModalWrapper defaultOpen onSave={onSave} />);

		await userEvent.click(screen.getByText('done'));

		await waitFor(() => {
			expect(screen.getByText('saving')).toBeInTheDocument();
		});

		resolveOnSave!();
	});

	it('closes the modal after Done is clicked', async () => {
		render(<PinturaEditorModalWrapper defaultOpen onSave={jest.fn()} />);

		await userEvent.click(screen.getByText('done'));

		await waitFor(() => {
			expect(
				screen.queryByTestId('pintura-editor')
			).not.toBeInTheDocument();
		});
	});

	it('does not render when open is false', () => {
		render(
			<PinturaEditorModalWrapper defaultOpen={false} onSave={jest.fn()} />
		);

		expect(screen.queryByTestId('pintura-editor')).not.toBeInTheDocument();
	});
});
