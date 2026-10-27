import React from 'react';
import {disconnect} from 'shared/api/data-source';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {useDisconnectDataSource} from '../utils';

jest.unmock('react-dom');

jest.mock('shared/api/data-source', () => ({
	disconnect: jest.fn(() => Promise.resolve({})),
}));

interface RunFlowOptions {
	beforeSubmit?: () => Promise<any> | any;
	onSubmit?: () => Promise<any> | any;
}

const runDisconnectFlow = async ({
	beforeSubmit,
	onSubmit = () => Promise.resolve(),
}: RunFlowOptions = {}) => {
	const addAlert = jest.fn();

	const Capture: React.FC = () => {
		const {disconnectModal, handleDisconnect} = useDisconnectDataSource({
			addAlert,
			beforeSubmit,
			groupId: '23',
			id: 'ds-1',
			onSubmit,
		});

		return (
			<>
				<button onClick={handleDisconnect} type="button">
					{'trigger'}
				</button>

				{disconnectModal}
			</>
		);
	};

	render(<Capture />);

	fireEvent.click(screen.getByText('trigger'));

	fireEvent.click(await screen.findByText('Disconnect'));

	await waitFor(() => expect(addAlert).toHaveBeenCalled());

	return {addAlert};
};

describe('useDisconnectDataSource', () => {
	beforeEach(() => {
		(disconnect as jest.Mock).mockClear();
		(disconnect as jest.Mock).mockResolvedValue({});
	});

	it('runs beforeSubmit before the disconnect API call', async () => {
		const callOrder: string[] = [];

		const beforeSubmit = jest.fn(() => {
			callOrder.push('beforeSubmit');

			return Promise.resolve();
		});

		(disconnect as jest.Mock).mockImplementation(() => {
			callOrder.push('disconnect');

			return Promise.resolve({});
		});

		await runDisconnectFlow({beforeSubmit});

		expect(callOrder).toEqual(['beforeSubmit', 'disconnect']);
	});

	it('runs the consumer onSubmit (refetch) after the disconnect API call', async () => {
		const callOrder: string[] = [];

		(disconnect as jest.Mock).mockImplementation(() => {
			callOrder.push('disconnect');

			return Promise.resolve({});
		});

		const onSubmit = jest.fn(() => {
			callOrder.push('onSubmit');

			return Promise.resolve();
		});

		await runDisconnectFlow({onSubmit});

		expect(callOrder).toEqual(['disconnect', 'onSubmit']);
	});

	it('skips beforeSubmit gracefully when the consumer does not provide one', async () => {
		const onSubmit = jest.fn(() => Promise.resolve());

		await runDisconnectFlow({onSubmit});

		expect(disconnect).toHaveBeenCalledWith({groupId: '23', id: 'ds-1'});
		expect(onSubmit).toHaveBeenCalled();
	});

	it('does not call disconnect when beforeSubmit throws', async () => {
		const beforeSubmit = jest.fn(() =>
			Promise.reject(new Error('revoke failed'))
		);
		const onSubmit = jest.fn(() => Promise.resolve());

		await runDisconnectFlow({beforeSubmit, onSubmit});

		expect(disconnect).not.toHaveBeenCalled();
		expect(onSubmit).not.toHaveBeenCalled();
	});

	it('does not render the modal until the disconnect is requested', () => {
		const Capture: React.FC = () => {
			const {disconnectModal} = useDisconnectDataSource({
				addAlert: jest.fn(),
				groupId: '23',
				id: 'ds-1',
				onSubmit: jest.fn(),
			});

			return <>{disconnectModal}</>;
		};

		render(<Capture />);

		expect(screen.queryByRole('dialog')).toBeNull();
	});
});
