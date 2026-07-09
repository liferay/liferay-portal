import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {SyncMarketoCampaignDataStep} from '../SyncMarketoCampaignDataStep';
import {updateMarketoCampaign} from 'shared/api/data-source';
import React from 'react';

jest.unmock('react-dom');

const useWizardPageMock = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('settings/components/base-page/WizardPageContext', () => ({
	useWizardPage: () => useWizardPageMock(),
}));

jest.mock('shared/actions/alerts', () => ({
	addAlert: jest.fn(),
}));

jest.mock('shared/api/data-source', () => ({
	updateMarketoCampaign: jest.fn(),
}));

const immutableMap = (values: {[key: string]: any}) => ({
	get: (key: string) => values[key],
});

const mockDataSource = (enableAllLeads: boolean) => ({
	id: '42',
	provider: immutableMap({
		contactsConfiguration: immutableMap({enableAllLeads}),
	}),
});

const getCheckbox = (container: HTMLElement) =>
	container.querySelector('input[type="checkbox"]') as HTMLInputElement;

describe('SyncMarketoCampaignDataStep', () => {
	beforeEach(() => {
		(addAlert as jest.Mock).mockClear();
		(updateMarketoCampaign as jest.Mock).mockReset();
		(updateMarketoCampaign as jest.Mock).mockResolvedValue({});
		useWizardPageMock.mockReturnValue({dataSource: mockDataSource(false)});
	});

	it('initializes the individuals checkbox from the data source configuration', async () => {
		useWizardPageMock.mockReturnValue({dataSource: mockDataSource(true)});

		const {container} = render(
			<SyncMarketoCampaignDataStep
				onNext={jest.fn()}
				onPrev={jest.fn()}
			/>
		);

		await waitFor(() => expect(getCheckbox(container).checked).toBe(true));
	});

	it('submits the enabled leads configuration and advances to the next step', async () => {
		const onNext = jest.fn();

		const {container} = render(
			<SyncMarketoCampaignDataStep onNext={onNext} onPrev={jest.fn()} />
		);

		fireEvent.click(getCheckbox(container));

		fireEvent.submit(container.querySelector('form')!);

		await waitFor(() =>
			expect(updateMarketoCampaign).toHaveBeenCalledWith({
				contactsConfiguration: {enableAllLeads: true},
				groupId: '23',
				id: '42',
			})
		);

		await waitFor(() => expect(onNext).toHaveBeenCalled());
	});

	it('shows an error alert when the update request fails', async () => {
		const onNext = jest.fn();

		(updateMarketoCampaign as jest.Mock).mockRejectedValue(
			new Error('Request error')
		);

		useWizardPageMock.mockReturnValue({dataSource: mockDataSource(true)});

		const {container} = render(
			<SyncMarketoCampaignDataStep onNext={onNext} onPrev={jest.fn()} />
		);

		fireEvent.submit(container.querySelector('form')!);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
				),
			})
		);

		await waitFor(() => expect(onNext).toHaveBeenCalled());
	});

	it('does not send the update request when there is no data source', async () => {
		const onNext = jest.fn();

		useWizardPageMock.mockReturnValue({dataSource: null});

		const {container} = render(
			<SyncMarketoCampaignDataStep onNext={onNext} onPrev={jest.fn()} />
		);

		fireEvent.submit(container.querySelector('form')!);

		await waitFor(() =>
			expect(updateMarketoCampaign).not.toHaveBeenCalled()
		);

		expect(onNext).not.toHaveBeenCalled();
	});
});
