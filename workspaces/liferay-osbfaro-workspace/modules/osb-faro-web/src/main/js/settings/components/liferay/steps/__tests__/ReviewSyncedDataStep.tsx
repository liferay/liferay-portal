import DataSourceQuery from 'shared/queries/DataSourceQuery';
import React from 'react';
import {DataSourceTypes, OrderByDirections} from 'shared/util/constants';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider, MockedResponse} from '@apollo/client/testing';
import {render} from '@testing-library/react';
import {ReviewSyncedDataStep} from '../ReviewSyncedDataStep';

const defaultMock = {
	request: {
		query: DataSourceQuery,
		variables: {
			size: 1,
			sort: {column: 'createDate', type: OrderByDirections.Descending},
			type: DataSourceTypes.Liferay,
		},
	},
	result: {
		data: {
			dataSources: [
				{
					contactsSyncDetails: {selected: true},
					id: '123',
					sitesSyncDetails: {selected: false},
				},
			],
		},
	},
};

const Wrapper = ({
	children,
	mocks = [defaultMock],
}: {
	children: React.ReactNode;
	mocks?: MockedResponse[];
}) => (
	<MemoryRouter>
		<MockedProvider addTypename={false} mocks={mocks}>
			{children}
		</MockedProvider>
	</MemoryRouter>
);

describe('ReviewSyncedDataStep', () => {
	it('should render', () => {
		const {container} = render(
			<Wrapper>
				<ReviewSyncedDataStep onNext={jest.fn()} onPrev={jest.fn()} />
			</Wrapper>
		);

		expect(container).toMatchSnapshot();
	});
});
