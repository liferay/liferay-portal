import ConfirmationModal from '../ConfirmationModal';
import {useModal} from '@clayui/modal';
import React from 'react';
import {cleanup, fireEvent, render, screen, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

const DefaultComponent = ({onClose = jest.fn(), ...props}) => {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			<button onClick={() => onOpenChange(true)} type="button">
				{'trigger'}
			</button>

			{open && (
				<ConfirmationModal
					message="message"
					observer={observer}
					onClose={() => {
						onClose();

						onOpenChange(false);
					}}
					onSubmit={jest.fn()}
					{...props}
				/>
			)}
		</>
	);
};

const renderOpen = async (props) => {
	const result = render(<DefaultComponent {...props} />);

	fireEvent.click(screen.getByText('trigger'));

	await waitFor(() =>
		expect(document.querySelector('.modal-body')).not.toBeNull()
	);

	return result;
};

describe('ConfirmationModal', () => {
	afterEach(cleanup);

	it('should render the title and the message', async () => {
		await renderOpen({message: 'This is a message.', title: 'A title'});

		expect(screen.getByText('A title')).toBeInTheDocument();
		expect(screen.getByText('This is a message.')).toBeInTheDocument();
	});

	it('should render the default title and buttons', async () => {
		await renderOpen();

		expect(screen.getByText('Confirm')).toBeInTheDocument();
		expect(screen.getByText('Cancel')).toBeInTheDocument();
		expect(screen.getByText('Continue')).toBeInTheDocument();
	});

	it('should render the icon and the class of its type', async () => {
		await renderOpen({type: 'warning'});

		expect(document.querySelector('.modal-warning')).toBeTruthy();
		expect(
			document.querySelector('.lexicon-icon-warning-full')
		).toBeTruthy();
	});

	it('should render without an icon when it has no type', async () => {
		await renderOpen();

		expect(document.querySelector('.modal-title-indicator')).toBeNull();
	});

	it('should render a warning submit button', async () => {
		await renderOpen({
			submitButtonDisplay: 'warning',
			submitMessage: 'delete',
		});

		expect(
			screen.getByText('delete').closest('.btn-warning')
		).toBeTruthy();
	});

	it('should render custom button texts', async () => {
		await renderOpen({cancelMessage: 'leave', submitMessage: 'stay'});

		expect(screen.getByText('leave')).toBeInTheDocument();
		expect(screen.getByText('stay')).toBeInTheDocument();
	});

	it('should close when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderOpen({onClose});

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('should submit and close', async () => {
		const onClose = jest.fn();
		const onSubmit = jest.fn();

		await renderOpen({onClose, onSubmit});

		fireEvent.click(screen.getByText('Continue'));

		expect(onSubmit).toHaveBeenCalledTimes(1);
		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('should submit without closing', async () => {
		const onClose = jest.fn();
		const onSubmit = jest.fn();

		await renderOpen({closeAfterSubmit: false, onClose, onSubmit});

		fireEvent.click(screen.getByText('Continue'));

		expect(onSubmit).toHaveBeenCalledTimes(1);
		expect(onClose).not.toHaveBeenCalled();
	});

	it('should close after a resolved promise', async () => {
		const onClose = jest.fn();

		await renderOpen({onClose, onSubmit: () => Promise.resolve()});

		fireEvent.click(screen.getByText('Continue'));

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));
	});

	it('should not close after a rejected promise', async () => {
		const onClose = jest.fn();
		const onSubmit = jest.fn(() => Promise.reject());

		await renderOpen({onClose, onSubmit});

		fireEvent.click(screen.getByText('Continue'));

		await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));

		expect(onClose).not.toHaveBeenCalled();
	});
});
