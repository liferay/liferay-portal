import * as data from 'test/data';
import React from 'react';
import WorkspaceList from '..';
import {cleanup, render} from '@testing-library/react';
import {DataSourceStates} from 'shared/util/constants';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {range, uniqueId} from 'lodash';
import {MemoryRouter} from 'react-router';

const FRIENDLY_URL_BASE = '/faro-liferay-';

jest.unmock('react-dom');

const mockAccountList = range(3).map(
	i =>
		new Project(
			fromJS(
				data.mockProject(i, {
					groupId: Number(uniqueId()),
					name: `mockProject_AccountA ${i}`
				})
			)
		)
);

const mockAccountWithFriendlyURLList = range(2).map(
	i =>
		new Project(
			fromJS(
				data.mockProject(i, {
					corpProjectUuid: null,
					friendlyURL: `${FRIENDLY_URL_BASE}${i}`,
					groupId: 123,
					name: `mockProject_AccountA ${i}`
				})
			)
		)
);

const mockAccountWithNullStateList = range(2).map(
	i =>
		new Project(
			fromJS(
				data.mockProject(i, {
					corpProjectUuid: null,
					friendlyURL: `${FRIENDLY_URL_BASE}${i}`,
					groupId: null,
					name: `mockProject_AccountA ${i}`,
					state: null
				})
			)
		)
);

const mockAccountWithUnconfiguredList = range(2).map(
	i =>
		new Project(
			fromJS(
				data.mockProject(i, {
					corpProjectUuid: '12345',
					groupId: 123,
					name: `mockProject_AccountA ${i}`,
					state: DataSourceStates.Unconfigured
				})
			)
		)
);

const DefaultComponent = props => (
	<MemoryRouter>
		<WorkspaceList {...props} />
	</MemoryRouter>
);

describe('WorkspaceList', () => {
	afterEach(cleanup);
	it('should render', () => {
		const {container} = render(
			<DefaultComponent accounts={mockAccountList} />
		);
		expect(container).toMatchSnapshot();
	});

	it('should render WorkspaceList with friendlyURL', () => {
		const {container} = render(
			<DefaultComponent accounts={mockAccountWithFriendlyURLList} />
		);
		expect(container).toMatchSnapshot();
	});

	it('should render an unconfigured WorkspaceList', () => {
		const {queryAllByText} = render(
			<DefaultComponent accounts={mockAccountWithUnconfiguredList} />
		);

		expect(queryAllByText('Configuration Required')).toBeTruthy();
	});

	it('should render an unconfigured WorkspaceList when the state and groupId is null', () => {
		const {queryAllByText} = render(
			<DefaultComponent accounts={mockAccountWithNullStateList} />
		);

		expect(queryAllByText('Configuration Required')).toBeTruthy();
	});
});
