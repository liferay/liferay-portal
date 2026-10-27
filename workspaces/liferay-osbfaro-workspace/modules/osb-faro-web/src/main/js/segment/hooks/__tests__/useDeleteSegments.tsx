import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Provider} from 'react-redux';
import {useDeleteSegments} from '../useDeleteSegments';
import {waitFor} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	individualSegment: {
		delete: jest.fn(() => Promise.resolve({})),
	},
}));

const DefaultComponent: React.FC<{
	ids: string[];
	name?: string;
	onSuccess?: () => void;
}> = ({ids, name, onSuccess}) => {
	const {deleteSegments, deleteSegmentsModal} = useDeleteSegments('23');

	return (
		<>
			<button
				onClick={() => deleteSegments({ids, name, onSuccess})}
				type="button"
			>
				{'trigger'}
			</button>

			{deleteSegmentsModal}
		</>
	);
};

const renderComponent = (
	props: React.ComponentProps<typeof DefaultComponent>
) =>
	render(
		<Provider store={mockStore()}>
			<DefaultComponent {...props} />
		</Provider>
	);

describe('useDeleteSegments', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('does not render the modal until a delete is requested', () => {
		renderComponent({ids: ['1']});

		expect(screen.queryByRole('dialog')).toBeNull();
	});

	it('asks to delete a single segment by name', async () => {
		renderComponent({ids: ['1'], name: 'Segment A'});

		fireEvent.click(screen.getByText('trigger'));

		expect(
			await screen.findByText('Deleting Segment A?')
		).toBeInTheDocument();
	});

	it('asks to delete several segments', async () => {
		renderComponent({ids: ['1', '2']});

		fireEvent.click(screen.getByText('trigger'));

		expect(await screen.findByText('Delete Segments?')).toBeInTheDocument();
	});

	it('does not delete anything until the user confirms', async () => {
		renderComponent({ids: ['1']});

		fireEvent.click(screen.getByText('trigger'));

		await screen.findByRole('dialog');

		expect(API.individualSegment.delete).not.toHaveBeenCalled();
	});

	it('deletes the segments and runs onSuccess after confirming', async () => {
		const onSuccess = jest.fn();

		renderComponent({ids: ['1', '2'], onSuccess});

		fireEvent.click(screen.getByText('trigger'));

		fireEvent.click(
			await screen.findByText('Delete', {selector: 'button'})
		);

		await waitFor(() => expect(onSuccess).toHaveBeenCalledTimes(1));

		expect(API.individualSegment.delete).toHaveBeenCalledWith({
			groupId: '23',
			ids: ['1', '2'],
		});
	});

	it('does not run onSuccess when the request fails', async () => {
		(API.individualSegment.delete as jest.Mock).mockImplementationOnce(() =>
			Promise.reject(new Error('error'))
		);

		const onSuccess = jest.fn();

		renderComponent({ids: ['1'], onSuccess});

		fireEvent.click(screen.getByText('trigger'));

		fireEvent.click(
			await screen.findByText('Delete', {selector: 'button'})
		);

		await waitFor(() =>
			expect(API.individualSegment.delete).toHaveBeenCalledTimes(1)
		);

		expect(onSuccess).not.toHaveBeenCalled();
	});

	it('closes without deleting when cancel is clicked', async () => {
		renderComponent({ids: ['1']});

		fireEvent.click(screen.getByText('trigger'));

		fireEvent.click(await screen.findByText('Cancel'));

		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());

		expect(API.individualSegment.delete).not.toHaveBeenCalled();
	});
});
