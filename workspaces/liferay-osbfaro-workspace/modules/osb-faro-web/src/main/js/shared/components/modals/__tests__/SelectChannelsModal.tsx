import React from 'react';
import SelectChannelsModal, {ISelectableChannel} from '../SelectChannelsModal';
import {fireEvent, render} from '@testing-library/react';
import {useModal} from '@clayui/modal';
import {useRequest} from 'shared/hooks/useRequest';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/hoc/CrossPageSelect', () => ({
	__esModule: true,
	default: () => <div />,
}));

const mockChannels = [
	{groupsCount: 2, id: 'channel-1', name: 'Channel With Sites'},
	{groupsCount: 0, id: 'channel-2', name: 'Channel Without Sites'},
	{groupsCount: 1, id: 'channel-3', name: 'Another Channel With Sites'},
];

const hasSyncedSites = (channel: ISelectableChannel) => channel.groupsCount > 0;

const TestModal = (
	props: Omit<React.ComponentProps<typeof SelectChannelsModal>, 'observer'>
) => {
	const {observer} = useModal({defaultOpen: true});

	return <SelectChannelsModal {...props} observer={observer} />;
};

const defaultProps = {
	groupId: '23',
	initialItems: [],
	onClose: jest.fn(),
	onSelect: jest.fn(),
};

const submitButton = () =>
	document.querySelector('.modal-footer button[type="submit"]') as Element;

describe('SelectChannelsModal', () => {
	beforeEach(() => {
		(useRequest as jest.Mock).mockReturnValue({
			data: {items: mockChannels, total: 3},
			error: false,
			loading: false,
		});
	});

	it('should not preselect any channel when no filter is given', async () => {
		const onSelect = jest.fn();

		render(<TestModal {...defaultProps} onSelect={onSelect} />);

		await waitForModal();

		fireEvent.click(submitButton());

		expect(onSelect).not.toHaveBeenCalled();
	});

	it('should auto-select the channels matching the given filter on first data load', async () => {
		const onSelect = jest.fn();

		render(
			<TestModal
				{...defaultProps}
				autoSelectFilter={hasSyncedSites}
				onSelect={onSelect}
			/>
		);

		await waitForModal();

		fireEvent.click(submitButton());

		expect(onSelect).toHaveBeenCalledWith(
			expect.arrayContaining(['channel-1', 'channel-3'])
		);
	});

	it('should not auto-select the channels rejected by the given filter', async () => {
		const onSelect = jest.fn();

		render(
			<TestModal
				{...defaultProps}
				autoSelectFilter={hasSyncedSites}
				onSelect={onSelect}
			/>
		);

		await waitForModal();

		fireEvent.click(submitButton());

		expect(onSelect.mock.calls[0][0]).not.toContain('channel-2');
	});

	it('should not duplicate channels already present in initialItems', async () => {
		const onSelect = jest.fn();

		render(
			<TestModal
				{...defaultProps}
				autoSelectFilter={hasSyncedSites}
				initialItems={['channel-1']}
				onSelect={onSelect}
			/>
		);

		await waitForModal();

		fireEvent.click(submitButton());

		const selectedIds: string[] = onSelect.mock.calls[0][0];

		expect(selectedIds.filter((id) => id === 'channel-1')).toHaveLength(1);
	});
});
