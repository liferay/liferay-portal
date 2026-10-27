import React from 'react';
import {AssignIndividualsDataToPropertiesStep} from '../AssignIndividualsDataToChannelsStep';
import {DataSourceTypes} from 'shared/util/constants';
import {fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const useWizardPageMock = jest.fn();

let mockSelectChannelsModalProps: any;

jest.mock('shared/components/modals/SelectChannelsModal', () => ({
	__esModule: true,
	default: (props: any) => {
		mockSelectChannelsModalProps = props;

		return <div data-testid="select-channels-modal" />;
	},
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: jest.fn()}),
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
	onPrev: jest.fn(),
	onSubmit: jest.fn(),
	updateDataSourceFn: jest.fn(),
};

const openSelectChannelsModal = () => {
	render(
		<AssignIndividualsDataToPropertiesStep {...(defaultProps as any)} />
	);

	expect(screen.queryByTestId('select-channels-modal')).toBeNull();

	fireEvent.click(screen.getByText(Liferay.Language.get('select')));

	expect(screen.getByTestId('select-channels-modal')).toBeInTheDocument();

	return mockSelectChannelsModalProps;
};

describe('AssignIndividualsDataToPropertiesStep', () => {
	it('should preselect the properties syncing a site for a Demandbase data source', () => {
		useWizardPageMock.mockReturnValue({
			dataSource: mockDataSource(DataSourceTypes.Demandbase),
		});

		const {autoSelectFilter} = openSelectChannelsModal();

		expect(autoSelectFilter({groupsCount: 1})).toBe(true);
		expect(autoSelectFilter({groupsCount: 0})).toBe(false);
	});

	it('should not preselect any property for a non Demandbase data source', () => {
		useWizardPageMock.mockReturnValue({
			dataSource: mockDataSource(DataSourceTypes.Salesforce),
		});

		const modalProps = openSelectChannelsModal();

		expect(modalProps.autoSelectFilter).toBeUndefined();
	});
});
