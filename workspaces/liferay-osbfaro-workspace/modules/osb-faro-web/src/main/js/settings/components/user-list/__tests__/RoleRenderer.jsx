import React from 'react';
import RoleRenderer from '../RoleRenderer';
import {render} from '@testing-library/react';

const userRoleOptions = [
	{label: 'Administrator', value: 'Site Administrator'},
	{label: 'Member', value: 'Site Member'},
	{label: 'Owner', value: 'Site Owner'}
];

describe('RoleRenderer', () => {
	it('should render', () => {
		const {container} = render(
			<RoleRenderer data={{roleName: 'Site Owner'}} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render as being edited', () => {
		const {container} = render(
			<RoleRenderer
				data={{roleName: 'Site Member'}}
				editing
				options={userRoleOptions}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render and call the onUpdateEdits prop callback with the initial roleName', () => {
		const onUpdateEditsSpy = jest.fn();

		render(
			<RoleRenderer
				data={{roleName: 'Site Member'}}
				editing
				onUpdateEdits={onUpdateEditsSpy}
				options={userRoleOptions}
			/>
		);

		expect(onUpdateEditsSpy).toHaveBeenCalledWith(
			'roleName',
			'Site Member'
		);
	});
});
