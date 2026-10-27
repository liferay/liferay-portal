import DeleteConfirmationModal from '../DeleteConfirmationModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	act,
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {useModal} from '@clayui/modal';

jest.unmock('react-dom');

const DefaultComponent = ({children, onClose = jest.fn(), ...props}) => {
	const {observer, onOpenChange, open} = useModal();

	return (
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider addTypename={false}>
					<div>
						<button
							onClick={() => onOpenChange(true)}
							type="button"
						>
							{'trigger'}
						</button>

						{open && (
							<DeleteConfirmationModal
								deleteConfirmationText="delete me"
								disabled={false}
								observer={observer}
								onClose={() => {
									onClose();
	
									onOpenChange(false);
								}}
								onSubmit={jest.fn()}
								{...props}
							>
								{children}
							</DeleteConfirmationModal>
						)}
					</div>
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);
};

const renderOpen = async (props) => {
	render(<DefaultComponent {...props} />);

	fireEvent.click(screen.getByText('trigger'));

	await screen.findByTestId('delete-confirmation-input');
};

describe('DeleteConfirmationModal', () => {
	afterEach(cleanup);

	it('should render with a custom title and message', async () => {
		await renderOpen({children: 'Custom Delete Message', title: 'Custom'});

		expect(screen.getByText('Custom')).toBeInTheDocument();
		expect(screen.getByText('Custom Delete Message')).toBeInTheDocument();
	});

	it('should render as a warning modal', async () => {
		await renderOpen();

		expect(document.querySelector('.modal-warning')).toBeTruthy();
		expect(
			document.querySelector('.lexicon-icon-warning-full')
		).toBeTruthy();
	});

	it('should disable the delete button until the text matches', async () => {
		await renderOpen();

		const button = screen.getByText('Delete').closest('button');
		const input = screen.getByTestId('delete-confirmation-input');

		fireEvent.change(input, {target: {value: 'nope'}});

		await waitFor(() => expect(button).toBeDisabled());

		fireEvent.change(input, {target: {value: 'delete me'}});

		await waitFor(() => expect(button).not.toBeDisabled());
	});

	it('should focus the input without showing its error', async () => {
		await renderOpen();

		act(() => {
			jest.runOnlyPendingTimers();
		});

		expect(screen.getByTestId('delete-confirmation-input')).toHaveFocus();
		expect(document.querySelector('.modal .has-error')).toBeNull();
	});

	it('should close when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderOpen({onClose});

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
