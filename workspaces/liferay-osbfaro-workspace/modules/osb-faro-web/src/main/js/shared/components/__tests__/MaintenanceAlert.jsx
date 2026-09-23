import * as data from 'test/data';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MaintenanceAlert, mapState} from '../MaintenanceAlert';
import {mockStoreData} from 'test/mock-store';
import {Project, RemoteData} from 'shared/util/records';
import {ProjectStates} from 'shared/util/constants';

jest.unmock('react-dom');

const store = mockStoreData.setIn(
	['projects', '23'],
	new RemoteData({
		data: data.getImmutableMock(Project, data.mockProject, '23', {
			state: ProjectStates.Scheduled,
			stateStartDate: data.getTimestamp()
		})
	})
);

const mockProject = data.getImmutableMock(Project, data.mockProject, '23', {
	state: ProjectStates.Scheduled,
	stateStartDate: data.getTimestamp()
});

describe('MaintenanceAlert', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, queryByText} = render(
			<MaintenanceAlert project={new Project()} />
		);

		// No alert shown when project state is not Scheduled
		expect(
			container.querySelector('.maintenance-alert-root')
		).toBeInTheDocument();
		expect(queryByText('Scheduled Maintenance')).toBeNull();
	});

	it('should render w/ maintenance alert', () => {
		const {getByText} = render(<MaintenanceAlert project={mockProject} />);

		// Alert should be shown when project state is Scheduled
		expect(getByText(/Scheduled Maintenance/)).toBeInTheDocument();
	});

	it('should render the scheduled date and time of the maintenance', () => {
		const project = data.getImmutableMock(Project, data.mockProject, '23', {
			state: ProjectStates.Scheduled,
			stateStartDate: Date.parse('2026-06-10T14:30:00Z')
		});

		const {getByText} = render(<MaintenanceAlert project={project} />);

		expect(
			getByText(
				'A system-wide maintenance has been scheduled to take place on Jun 10, 2026 at 2:30 PM.'
			)
		).toBeInTheDocument();
	});
});

describe('mapState', () => {
	it('should map store state to props', () => {
		const router = {groupId: '23'};

		const result = mapState(store, router);

		expect(result).toHaveProperty('project');
		expect(result).toHaveProperty('groupId', '23');
		expect(result).toHaveProperty('alertDismissed');
	});
});
