import React from 'react';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {DataSourceStatuses, DataSourceTypes} from 'shared/util/constants';
import {fireEvent, render, screen} from '@testing-library/react';
import {fromJS} from 'immutable';
import {modalTypes} from 'shared/actions/modals';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('shared/api/data-source', () => ({
	fetchChannelDatasources: jest.fn(),
}));

jest.mock('shared/hoc/CrossPageSelect', () => ({
	__esModule: true,
	default: ({renderNav}: {renderNav?: () => React.ReactNode}) => (
		<div>{renderNav?.()}</div>
	),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => true}),
}));

jest.mock('shared/hooks/useQueryPagination', () => ({
	useQueryPagination: () => ({
		delta: 10,
		orderIOMap: {},
		page: 1,
		query: '',
	}),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: () => ({
		data: {items: [], total: 0},
		error: false,
		refetch: jest.fn(),
	}),
}));

const mockDataSource = (providerType: DataSourceTypes) => ({
	id: '42',
	provider: fromJS({}),
	providerType,
	status: DataSourceStatuses.Active,
});

const defaultProps = {
	addAlert: jest.fn(),
	close: jest.fn(),
	handleUpdateDataSource: jest.fn(),
	updateDataSourceFn: jest.fn(),
};

const openSelectChannelsModal = (
	open: jest.Mock,
	providerType: DataSourceTypes
) => {
	render(
		<AssignedPropertiesTable
			{...defaultProps}
			dataSource={mockDataSource(providerType)}
			open={open}
		/>
	);

	fireEvent.click(screen.getByText(Liferay.Language.get('select-property')));

	return open.mock.calls[0][1];
};

describe('AssignedPropertiesTable', () => {
	it('should preselect the properties syncing a site for a Demandbase data source', () => {
		const {autoSelectFilter} = openSelectChannelsModal(
			jest.fn(),
			DataSourceTypes.Demandbase
		);

		expect(autoSelectFilter({groupsCount: 3})).toBe(true);
		expect(autoSelectFilter({groupsCount: 0})).toBe(false);
	});

	it('should not preselect any property for a non Demandbase data source', () => {
		const open = jest.fn();

		const modalProps = openSelectChannelsModal(
			open,
			DataSourceTypes.Liferay
		);

		expect(open).toHaveBeenCalledWith(
			modalTypes.SELECT_CHANNELS_MODAL,
			expect.anything()
		);

		expect(modalProps.autoSelectFilter).toBeUndefined();
	});
});
