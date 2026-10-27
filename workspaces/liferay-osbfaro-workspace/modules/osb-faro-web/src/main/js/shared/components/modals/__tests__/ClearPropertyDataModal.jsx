import ClearPropertyDataModal from '../ClearPropertyDataModal';
import React from 'react';
import {Alert} from 'shared/types';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {sub} from 'shared/util/lang';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';

jest.unmock('react-dom');

jest.mock('shared/components/DeleteConfirmationModal', () => ({
	children,
	deleteButtonLabel,
	deleteConfirmationText,
	onClose,
	onSubmit,
	title,
}) => (
	<div>
		<h1>{title}</h1>

		<div data-testid="confirmation-text">{deleteConfirmationText}</div>

		{children}

		<button onClick={onClose} type="button">
			{'close'}
		</button>

		<button onClick={onSubmit} type="button">
			{deleteButtonLabel}
		</button>
	</div>
));

const renderClearPropertyDataModal = (props) =>
	render(
		<ClearPropertyDataModal
			addAlert={jest.fn()}
			name="Acme"
			observer={{}}
			onClose={jest.fn()}
			onSubmit={jest.fn(() => Promise.resolve())}
			{...props}
		/>
	);

describe('ClearPropertyDataModal', () => {
	afterEach(cleanup);

	it('asks to copy the sentence of the property', async () => {
		await renderClearPropertyDataModal();

		expect(screen.getByTestId('confirmation-text')).toHaveTextContent(
			sub(Liferay.Language.get('clear-x'), ['Acme'])
		);
	});

	it('uses the label of the action on the button', async () => {
		await renderClearPropertyDataModal();

		expect(
			screen.getByRole('button', {
				name: Liferay.Language.get('clear-data'),
			})
		).toBeInTheDocument();
	});

	it('submits the request of the caller', async () => {
		const onSubmit = jest.fn(() => Promise.resolve());

		await renderClearPropertyDataModal({onSubmit});

		fireEvent.click(
			screen.getByRole('button', {name: Liferay.Language.get('clear-data')})
		);

		expect(onSubmit).toHaveBeenCalledTimes(1);
	});

	it('shows the unauthorized alert when the request is not allowed', async () => {
		const addAlert = jest.fn();

		await renderClearPropertyDataModal({
			addAlert,
			onSubmit: () => Promise.reject(new Error(UNAUTHORIZED_ACCESS)),
		});

		fireEvent.click(
			screen.getByRole('button', {name: Liferay.Language.get('clear-data')})
		);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get('unauthorized-access'),
				timeout: false,
			})
		);
	});

	it('shows the generic error alert when the request fails', async () => {
		const addAlert = jest.fn();

		await renderClearPropertyDataModal({
			addAlert,
			onSubmit: () => Promise.reject(new Error('boom')),
		});

		fireEvent.click(
			screen.getByRole('button', {name: Liferay.Language.get('clear-data')})
		);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get('error'),
				timeout: false,
			})
		);
	});
});
