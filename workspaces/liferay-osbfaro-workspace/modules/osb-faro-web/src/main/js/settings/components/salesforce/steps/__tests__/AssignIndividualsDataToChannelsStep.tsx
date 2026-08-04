import React from 'react';
import {AssignIndividualsDataToPropertiesStep} from '../AssignIndividualsDataToChannelsStep';
import {DataSourceTypes} from 'shared/util/constants';
import {fireEvent, render, screen} from '@testing-library/react';
import {modalTypes} from 'shared/actions/modals';

jest.unmock('react-dom');

const useWizardPageMock = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useHistory: () => ({push: jest.fn()}),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('settings/components/base-page/WizardPageContext', () => ({
	useWizardPage: () => useWizardPageMock(),
}));

const mockDataSource = (providerType: DataSourceTypes) => ({
	id: '42',
	provider: {get: () => undefined},
	providerType,
});

const defaultProps = {
	addAlert: jest.fn(),
	close: jest.fn(),
	onPrev: jest.fn(),
	onSubmit: jest.fn(),
	updateDataSourceFn: jest.fn(),
};

const openSelectChannelsModal = (open: jest.Mock) => {
	render(
		<AssignIndividualsDataToPropertiesStep
			{...(defaultProps as any)}
			open={open}
		/>
	);

	fireEvent.click(screen.getByText(Liferay.Language.get('select')));

	return open.mock.calls[0][1];
};

describe('AssignIndividualsDataToPropertiesStep', () => {
	it('should preselect the properties syncing a site for a Demandbase data source', () => {
		useWizardPageMock.mockReturnValue({
			dataSource: mockDataSource(DataSourceTypes.Demandbase),
		});

		const {autoSelectFilter} = openSelectChannelsModal(jest.fn());

		expect(autoSelectFilter({groupsCount: 1})).toBe(true);
		expect(autoSelectFilter({groupsCount: 0})).toBe(false);
	});

	it('should not preselect any property for a non Demandbase data source', () => {
		useWizardPageMock.mockReturnValue({
			dataSource: mockDataSource(DataSourceTypes.Salesforce),
		});

		const open = jest.fn();

		const modalProps = openSelectChannelsModal(open);

		expect(open).toHaveBeenCalledWith(
			modalTypes.SELECT_CHANNELS_MODAL,
			expect.anything()
		);

		expect(modalProps.autoSelectFilter).toBeUndefined();
	});
});
