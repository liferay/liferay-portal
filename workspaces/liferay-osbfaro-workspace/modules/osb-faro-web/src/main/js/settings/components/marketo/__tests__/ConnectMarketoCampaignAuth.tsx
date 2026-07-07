jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({groupId: '23'}),
}));

jest.mock('shared/api/data-source', () => ({
	createMarketoCampaign: jest.fn(),
	updateMarketoCampaign: jest.fn(),
}));

import {Alert} from 'shared/types';
import {ConnectMarketoCampaignAuth} from '../ConnectMarketoCampaignAuth';
import {createMarketoCampaign} from 'shared/api/data-source';
import {fireEvent, render, waitFor} from '@testing-library/react';

const fillAndSubmit = (container: HTMLElement) => {
	const setValue = (name: string, value: string) =>
		fireEvent.change(container.querySelector(`input[name="${name}"]`)!, {
			target: {value},
		});

	setValue('marketoDataSource', 'https://example.mktorest.com');
	setValue('clientId', 'client-id');
	setValue('clientSecret', 'client-secret');

	fireEvent.submit(container.querySelector('form')!);
};

const rejectWithStatus = (status?: number) => {
	const error: any = new Error('Request error');

	error.status = status;

	(createMarketoCampaign as jest.Mock).mockReturnValue(Promise.reject(error));
};

describe('ConnectMarketoCampaignAuth', () => {
	beforeEach(() => {
		(createMarketoCampaign as jest.Mock).mockClear();
	});

	it('shows the invalid-credentials message on a 401', async () => {
		const addAlert = jest.fn();

		rejectWithStatus(401);

		const {container} = render(
			<ConnectMarketoCampaignAuth
				addAlert={addAlert}
				onSubmit={jest.fn()}
			/>
		);

		fillAndSubmit(container);

		await waitFor(() => expect(createMarketoCampaign).toHaveBeenCalled());

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'the-credentials-are-invalid-or-have-expired.-verify-your-credentials-and-try-again'
				),
			})
		);
	});

	it('shows the not-eligible message on a 403', async () => {
		const addAlert = jest.fn();

		rejectWithStatus(403);

		const {container} = render(
			<ConnectMarketoCampaignAuth
				addAlert={addAlert}
				onSubmit={jest.fn()}
			/>
		);

		fillAndSubmit(container);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'your-account-or-organization-is-not-eligible.-verify-your-data-source-configuration-and-try-again'
				),
			})
		);
	});

	it('falls back to the generic message for an unmapped status', async () => {
		const addAlert = jest.fn();

		rejectWithStatus(500);

		const {container} = render(
			<ConnectMarketoCampaignAuth
				addAlert={addAlert}
				onSubmit={jest.fn()}
			/>
		);

		fillAndSubmit(container);

		await waitFor(() =>
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
				),
			})
		);
	});
});
