import React from 'react';
import {AssignedPropertiesTable} from '../AssignedPropertiesTable';
import {DataSourceStatuses, DataSourceTypes} from 'shared/util/constants';
import {fireEvent, render, screen} from '@testing-library/react';
import {fromJS} from 'immutable';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

let mockSelectChannelsModalProps: any;

jest.mock('shared/components/modals/SelectChannelsModal', () => ({
	__esModule: true,
	default: (props: any) => {
		mockSelectChannelsModalProps = props;

		return <div data-testid="select-channels-modal" />;
	},
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
	handleUpdateDataSource: jest.fn(),
	updateDataSourceFn: jest.fn(),
};

const openSelectChannelsModal = (providerType: DataSourceTypes) => {
	render(
		<AssignedPropertiesTable
			{...defaultProps}
			dataSource={mockDataSource(providerType)}
		/>
	);

	expect(screen.queryByTestId('select-channels-modal')).toBeNull();

	fireEvent.click(screen.getByText(Liferay.Language.get('select-property')));

	expect(screen.getByTestId('select-channels-modal')).toBeInTheDocument();

	return mockSelectChannelsModalProps;
};

describe('AssignedPropertiesTable', () => {
	it('should preselect the properties syncing a site for a Demandbase data source', () => {
		const {autoSelectFilter} = openSelectChannelsModal(
			DataSourceTypes.Demandbase
		);

		expect(autoSelectFilter({groupsCount: 3})).toBe(true);
		expect(autoSelectFilter({groupsCount: 0})).toBe(false);
	});

	it('should not preselect any property for a non Demandbase data source', () => {
		const modalProps = openSelectChannelsModal(DataSourceTypes.Liferay);

		expect(modalProps.autoSelectFilter).toBeUndefined();
	});

	it('should open the modal for the group of the page', () => {
		const modalProps = openSelectChannelsModal(DataSourceTypes.Liferay);

		expect(modalProps.groupId).toBe('23');
	});
});
