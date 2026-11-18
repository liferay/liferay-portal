import ConnectorOverview from '../ConnectorOverview';
import mockStore from 'test/mock-store';
import React from 'react';
import {ConnectorConfig, Entity} from '../types';
import {DataSource} from 'shared/util/records';
import {DataSourceStates, DataSourceStatuses} from 'shared/util/constants';
import {fetch} from 'shared/api/data-source';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {fromJS} from 'immutable';
import {generateConnectorToken, updateConnector} from 'shared/api/connector';
import {Provider} from 'react-redux';
import {revoke} from 'shared/api/api-tokens';

jest.unmock('react-dom');

const useParamsMock = jest.fn();
const useCurrentUserMock = jest.fn();
const useDisconnectDataSourceMock = jest.fn();
const useRequestMock = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => useParamsMock(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => useCurrentUserMock(),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: (args: any) => useRequestMock(args),
}));

jest.mock('settings/components/data-source/utils', () => ({
	useDisconnectDataSource: (args: any) => useDisconnectDataSourceMock(args),
}));

jest.mock('shared/api/connector', () => ({
	generateConnectorToken: jest.fn(() =>
		Promise.resolve({token: 'fake-token'})
	),
	updateConnector: jest.fn(() => Promise.resolve({})),
}));

jest.mock('shared/api/data-source', () => ({
	fetch: jest.fn(() => Promise.resolve({})),
}));

jest.mock('shared/api/api-tokens', () => ({
	revoke: jest.fn(() => Promise.resolve({})),
}));

jest.mock('settings/components/base-page/BasePage', () => ({
	__esModule: true,
	default: ({children}: any) => <div>{children}</div>,
}));

jest.mock('settings/components/AssignedPropertiesTable', () => ({
	AssignedPropertiesTable: () => <div data-testid="assigned-properties" />,
}));

jest.mock('shared/components/ErrorDisplay', () => ({
	__esModule: true,
	default: () => <div data-testid="error-display" />,
}));

jest.mock('shared/components/Loading', () => ({
	__esModule: true,
	default: () => <div data-testid="loading" />,
}));

jest.mock('settings/components/data-source/DataSourceEditableTitle', () => ({
	DataSourceEditableTitle: ({
		description,
		displayType,
		label,
		onUpdateName,
	}: any) => (
		<div data-displaytype={displayType} data-testid="data-source-title">
			<span data-testid="data-source-title-label">{label}</span>

			{description && (
				<span data-testid="data-source-title-description">
					{description}
				</span>
			)}

			<button
				data-testid="trigger-update-name"
				onClick={() => onUpdateName('Renamed Data Source')}
				type="button"
			/>
		</div>
	),
}));

jest.mock('settings/components/CopyInputValue', () => ({
	CopyInputValue: ({title, value}: any) => (
		<div data-testid="copy-input">
			<span data-testid="copy-input-title">{title}</span>
			<span data-testid="copy-input-value">{value}</span>
		</div>
	),
}));

const buildConfig = (
	overrides: Partial<ConnectorConfig> = {}
): ConnectorConfig => ({
	columns: [],
	displayName: 'Acme',
	endpointPath: '/api/acme',
	entities: [],
	languages: {
		connectDescription: 'connectDescription',
		connectTitle: 'connectTitle',
		endpointHelper: 'endpointHelper',
		endpointLabel: 'endpointLabel',
		tokenLabel: 'tokenLabel',
	},
	singleton: false,
	slug: 'acme',
	type: 'ACME',
	...overrides,
});

const buildDataSource = (
	status: string,
	{
		credentials,
		providerData = null,
		state,
	}: {
		credentials?: {[key: string]: any};
		providerData?: any;
		state?: string;
	} = {}
) =>
	new DataSource({
		credentials: credentials ? fromJS(credentials) : undefined,
		id: 'ds-1',
		name: 'My Data Source',
		provider: providerData ? fromJS(providerData) : undefined,
		state,
		status,
	});

const renderOverview = (
	props: Partial<{
		config: ConnectorConfig;
		dataSource: DataSource;
	}> = {}
) =>
	render(
		<Provider store={mockStore()}>
			<ConnectorOverview
				config={props.config ?? buildConfig()}
				dataSource={
					props.dataSource ??
					buildDataSource(DataSourceStatuses.Active)
				}
			/>
		</Provider>
	);

const mockEntityCounts = (counts: {[entity: string]: number | undefined}) =>
	useRequestMock.mockReturnValue({
		data: counts,
		error: false,
		loading: false,
	});

const mockEntityCount = (count: number | undefined) =>
	mockEntityCounts(count === undefined ? {} : {[Entity.Accounts]: count});

describe('ConnectorOverview', () => {
	beforeEach(() => {
		window.localStorage.clear();

		(generateConnectorToken as jest.Mock).mockClear();
		(updateConnector as jest.Mock).mockClear();
		(fetch as jest.Mock).mockClear();
		(fetch as jest.Mock).mockResolvedValue({});
		(revoke as jest.Mock).mockClear();
		useDisconnectDataSourceMock.mockClear();

		useParamsMock.mockReturnValue({groupId: '23', id: 'ds-1'});
		useCurrentUserMock.mockReturnValue({isAdmin: () => true});
		useDisconnectDataSourceMock.mockReturnValue({
			handleDisconnect: jest.fn(),
		});
		useRequestMock.mockReturnValue({
			data: undefined,
			error: false,
			loading: false,
		});
	});

	describe('Page header status', () => {
		it('renders the Active badge with success display when status is ACTIVE', () => {
			const {getByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(getByTestId('data-source-title-label').textContent).toBe(
				'Active'
			);
			expect(
				getByTestId('data-source-title').getAttribute(
					'data-displaytype'
				)
			).toBe('success');
		});

		it('renders the Inactive badge with warning display when status is INACTIVE', () => {
			const {getByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(getByTestId('data-source-title-label').textContent).toBe(
				'Inactive'
			);
			expect(
				getByTestId('data-source-title').getAttribute(
					'data-displaytype'
				)
			).toBe('warning');
		});

		it('renders the Disconnected badge with secondary display when state is DISCONNECTED', () => {
			const {getByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(getByTestId('data-source-title-label').textContent).toBe(
				'Disconnected'
			);
			expect(
				getByTestId('data-source-title').getAttribute(
					'data-displaytype'
				)
			).toBe('secondary');
		});

		it('renders the data source ID below the title', () => {
			const {getByTestId} = renderOverview();

			expect(
				getByTestId('data-source-title-description').textContent
			).toBe('ID: ds-1');
		});
	});

	describe('Authentication card — default (non-disconnected)', () => {
		it.each([
			['Active', DataSourceStatuses.Active],
			['Inactive', DataSourceStatuses.Inactive],
		])(
			'renders the configure-data-source description text for status %s',
			(_, status) => {
				const {getByText} = renderOverview({
					dataSource: buildDataSource(status),
				});

				expect(
					getByText(
						/^to configure your data source, utilize the token and endpoint url provided by liferay data platform\.$/i
					)
				).toBeTruthy();
			}
		);

		it('renders the Learn more about data sources link', () => {
			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(
				getByText(/^learn more about data sources\.$/i)
			).toBeTruthy();
		});

		it('does not render the Data Source Type or Data Source ID inputs', () => {
			const {container} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(container.querySelector('#dataSourceType')).toBeNull();
			expect(container.querySelector('#dataSourceId')).toBeNull();
		});

		it('renders the Endpoint URL and Token copy inputs', () => {
			const {getAllByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			const titles = getAllByTestId('copy-input-title').map(
				(node) => node.textContent
			);

			expect(titles).toContain('Endpoint URL');
			expect(titles).toContain('Token');
		});

		it('does not render any connection-status alert in this card', () => {
			const {queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(queryByText('SUCCESS_ALERT')).toBeNull();
			expect(queryByText('DISCONNECTED_ALERT')).toBeNull();
		});
	});

	describe('Authentication card — DISCONNECTED (manual disconnection)', () => {
		const renderDisconnected = () =>
			renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

		it('renders the generate-a-new-token description text', () => {
			const {getByText} = renderDisconnected();

			expect(
				getByText(
					/^generate a new token to continue configuring this data source\.$/i
				)
			).toBeTruthy();
		});

		it('renders the Generate Token button', () => {
			const {getByLabelText} = renderDisconnected();

			expect(getByLabelText(/^generate token$/i)).toBeTruthy();
		});

		it('does not render the Endpoint URL or Token copy inputs', () => {
			const {queryAllByTestId} = renderDisconnected();

			expect(queryAllByTestId('copy-input')).toHaveLength(0);
		});

		it('does not auto-fetch a token on mount', async () => {
			renderDisconnected();

			await Promise.resolve();

			expect(generateConnectorToken).not.toHaveBeenCalled();
		});

		it('on Generate Token click: generates a token, updates status to ACTIVE, then refetches the data source', async () => {
			const {getByLabelText} = renderDisconnected();

			fireEvent.click(getByLabelText(/^generate token$/i));

			await waitFor(() =>
				expect(generateConnectorToken).toHaveBeenCalledWith({
					groupId: '23',
					type: 'acme',
				})
			);

			await waitFor(() =>
				expect(updateConnector).toHaveBeenCalledWith('acme', {
					groupId: '23',
					id: 'ds-1',
					status: DataSourceStatuses.Active,
				})
			);

			await waitFor(() => expect(fetch).toHaveBeenCalled());
		});
	});

	describe('Token state and auto-fetch', () => {
		it.each([
			['Active', DataSourceStatuses.Active],
			['Inactive', DataSourceStatuses.Inactive],
		])(
			'fetches the OAuth2 token for status %s by calling generateConnectorToken with the connector slug',
			async (_, status) => {
				renderOverview({
					dataSource: buildDataSource(status),
				});

				await waitFor(() =>
					expect(generateConnectorToken).toHaveBeenCalledWith({
						groupId: '23',
						type: 'acme',
					})
				);
			}
		);

		it('renders the fetched token in the Token copy input', async () => {
			(generateConnectorToken as jest.Mock).mockResolvedValueOnce({
				token: 'fetched-token',
			});

			const {getAllByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			await waitFor(() => {
				const values = getAllByTestId('copy-input-value').map(
					(node) => node.textContent
				);

				expect(values).toContain('fetched-token');
			});
		});

		it('does not auto-fetch a token when the data source is disconnected (user mints a new one via Generate Token)', async () => {
			renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			await Promise.resolve();

			expect(generateConnectorToken).not.toHaveBeenCalled();
		});

		it('on Generate Token click: stores the freshly minted token in state', async () => {
			(generateConnectorToken as jest.Mock).mockResolvedValueOnce({
				token: 'freshly-minted-token',
			});

			const {getByLabelText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			fireEvent.click(getByLabelText(/^generate token$/i));

			await waitFor(() =>
				expect(generateConnectorToken).toHaveBeenCalledWith({
					groupId: '23',
					type: 'acme',
				})
			);
		});
	});

	describe('Disconnect button', () => {
		it('shows the disconnect button when the user is admin and the data source is active', () => {
			const {getByLabelText} = renderOverview();

			expect(getByLabelText(/^disconnect data source$/i)).toBeTruthy();
		});

		it('shows the disconnect button when the data source is inactive but still has a token', () => {
			const {getByLabelText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(getByLabelText(/^disconnect data source$/i)).toBeTruthy();
		});

		it('hides the disconnect button when the data source is already disconnected', () => {
			const {queryByLabelText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(queryByLabelText(/^disconnect data source$/i)).toBeNull();
		});

		it('hides the disconnect button when the user is not an admin', () => {
			useCurrentUserMock.mockReturnValue({isAdmin: () => false});

			const {queryByLabelText} = renderOverview();

			expect(queryByLabelText(/^disconnect data source$/i)).toBeNull();
		});

		it('on click: invokes handleDisconnect from useDisconnectDataSource', () => {
			const handleDisconnect = jest.fn();

			useDisconnectDataSourceMock.mockReturnValue({handleDisconnect});

			const {getByLabelText} = renderOverview();

			fireEvent.click(getByLabelText(/^disconnect data source$/i));

			expect(handleDisconnect).toHaveBeenCalled();
		});

		it('disconnect: passes a beforeSubmit that revokes the fetched OAuth2 token (so revoke runs before the disconnect endpoint)', async () => {
			(generateConnectorToken as jest.Mock).mockResolvedValueOnce({
				token: 'fetched-token',
			});

			const {getAllByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			await waitFor(() => {
				const values = getAllByTestId('copy-input-value').map(
					(node) => node.textContent
				);

				expect(values).toContain('fetched-token');
			});

			const {beforeSubmit} =
				useDisconnectDataSourceMock.mock.calls[
					useDisconnectDataSourceMock.mock.calls.length - 1
				][0];

			await beforeSubmit();

			expect(revoke).toHaveBeenCalledWith({
				groupId: '23',
				token: 'fetched-token',
			});
		});

		it('disconnect: beforeSubmit is a no-op when the auto-fetch did not produce a token', async () => {
			(generateConnectorToken as jest.Mock).mockResolvedValueOnce({});

			renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			await waitFor(() =>
				expect(generateConnectorToken).toHaveBeenCalled()
			);

			const {beforeSubmit} =
				useDisconnectDataSourceMock.mock.calls[
					useDisconnectDataSourceMock.mock.calls.length - 1
				][0];

			await beforeSubmit();

			expect(revoke).not.toHaveBeenCalled();
		});

		it('disconnect: beforeSubmit propagates revoke failures so the shared hook can show its error alert', async () => {
			(generateConnectorToken as jest.Mock).mockResolvedValueOnce({
				token: 'fetched-token',
			});
			(revoke as jest.Mock).mockRejectedValueOnce(
				new Error('Unable to revoke OAuth2 authorization')
			);

			const {getAllByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			await waitFor(() => {
				const values = getAllByTestId('copy-input-value').map(
					(node) => node.textContent
				);

				expect(values).toContain('fetched-token');
			});

			const {beforeSubmit} =
				useDisconnectDataSourceMock.mock.calls[
					useDisconnectDataSourceMock.mock.calls.length - 1
				][0];

			await expect(beforeSubmit()).rejects.toThrow(
				'Unable to revoke OAuth2 authorization'
			);

			expect(revoke).toHaveBeenCalledWith({
				groupId: '23',
				token: 'fetched-token',
			});
		});

		it('disconnect: onSubmit refetches the data source', async () => {
			renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			const {onSubmit} =
				useDisconnectDataSourceMock.mock.calls[
					useDisconnectDataSourceMock.mock.calls.length - 1
				][0];

			await onSubmit();

			expect(fetch).toHaveBeenCalled();
		});
	});

	describe('Edit data source name', () => {
		it('on submit: calls updateConnector with the new name and refetches the data source', async () => {
			const {getByTestId} = renderOverview();

			fireEvent.click(getByTestId('trigger-update-name'));

			await waitFor(() =>
				expect(updateConnector).toHaveBeenCalledWith('acme', {
					groupId: '23',
					id: 'ds-1',
					name: 'Renamed Data Source',
				})
			);

			await waitFor(() => expect(fetch).toHaveBeenCalled());
		});
	});

	describe('Endpoint URL', () => {
		it('builds the endpoint URL using the window origin and config.endpointPath', () => {
			const config = buildConfig({endpointPath: '/api/custom'});

			const {getAllByTestId} = renderOverview({config});

			const values = getAllByTestId('copy-input-value').map(
				(node) => node.textContent
			);

			expect(values).toContain(`${window.location.origin}/api/custom`);
		});
	});

	describe('Synced Data card — count fetch states', () => {
		it('on count error: renders the Synced Data card normally without showing the generic error display', () => {
			useRequestMock.mockReturnValue({
				data: undefined,
				error: new Error('boom'),
				loading: false,
			});

			const {getByText, queryByTestId} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(queryByTestId('error-display')).toBeNull();

			expect(
				getByText(
					/^you have successfully connected to your data source\. complete your data source configuration to start syncing data\.$/i
				)
			).toBeTruthy();
		});

		it('on count error: renders the failed primary-entity row as Unconfigured with a 0-count, instead of an error indicator', () => {
			useRequestMock.mockReturnValue({
				data: undefined,
				error: new Error('boom'),
				loading: false,
			});

			const config = buildConfig({
				entities: [{entity: Entity.Events}],
			});

			const {container, getByText, queryByText} = renderOverview({
				config,
			});

			expect(container.querySelectorAll('.list-group-item')).toHaveLength(
				2
			);
			expect(getByText(/^events$/i)).toBeTruthy();
			expect(getByText(/^unconfigured$/i)).toBeTruthy();
			expect(getByText(/0 items synced/i)).toBeTruthy();
			expect(queryByText(/^error$/i)).toBeNull();
			expect(queryByText(/^sorry, an error occurred\.$/i)).toBeNull();
		});

		it('renders a loading state while the count is being fetched', () => {
			useRequestMock.mockReturnValue({
				data: undefined,
				error: false,
				loading: true,
			});

			const {getByTestId} = renderOverview();

			expect(getByTestId('loading')).toBeTruthy();
		});
	});

	describe('Synced Data card — Connection Status alert', () => {
		it('ACTIVE with zero accounts: warning / "successfully connected" message', () => {
			mockEntityCount(0);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^you have successfully connected to your data source\. complete your data source configuration to start syncing data\.$/i
				)
			).toBeTruthy();
		});

		it('ACTIVE with accounts: success / "all data is up to date" message', () => {
			mockEntityCount(7);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^all data coming from this data source is up to date\. there are no errors to report\.$/i
				)
			).toBeTruthy();
		});

		it('INACTIVE without data: warning / "your token was generated" message', () => {
			mockEntityCount(0);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(
				getByText(
					/^your token was generated successfully\. complete your data source configuration to start syncing data\.$/i
				)
			).toBeTruthy();
		});

		it('INACTIVE with data (90d): warning / "data is no longer being received — review" message', () => {
			mockEntityCount(12);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(
				getByText(
					/^data is no longer being received\. review your data source configuration to confirm it is still active\.$/i
				)
			).toBeTruthy();
		});

		it('DISCONNECTED: warning / "data is no longer being received — reconnect" message', () => {
			mockEntityCount(7);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(
				getByText(
					/^data is no longer being received\. reconnect to resume syncing\.$/i
				)
			).toBeTruthy();
		});
	});

	describe('Synced Data card — Available Data alert', () => {
		it('ACTIVE with zero accounts: no alert', () => {
			mockEntityCount(0);

			const {queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				queryByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeNull();
		});

		it('ACTIVE with accounts: info / "Your data may take some time to appear" alert', () => {
			mockEntityCount(7);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeTruthy();
		});

		it('ACTIVE with accounts: syncing alert can be dismissed and the dismissal is persisted in localStorage', () => {
			mockEntityCount(7);

			const {container, queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			const closeButton = container.querySelector(
				'.alert .close'
			) as HTMLButtonElement;

			expect(closeButton).toBeTruthy();

			fireEvent.click(closeButton);

			expect(
				queryByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeNull();

			expect(
				window.localStorage.getItem(
					'connector-overview:syncing-alert-dismissed:ds-1'
				)
			).toBe('true');
		});

		it('ACTIVE with accounts: syncing alert is hidden on re-render when localStorage flag is set', () => {
			window.localStorage.setItem(
				'connector-overview:syncing-alert-dismissed:ds-1',
				'true'
			);

			mockEntityCount(7);

			const {queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				queryByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeNull();
		});

		it('ACTIVE with accounts: dismissal of one data source does not hide the alert for another data source', () => {
			window.localStorage.setItem(
				'connector-overview:syncing-alert-dismissed:other-ds',
				'true'
			);

			mockEntityCount(7);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeTruthy();
		});

		it('DISCONNECTED with data: previously-synced alert can be dismissed and the dismissal is persisted in localStorage', () => {
			mockEntityCount(7);

			const {container, queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			const closeButton = container.querySelector(
				'.alert .close'
			) as HTMLButtonElement;

			expect(closeButton).toBeTruthy();

			fireEvent.click(closeButton);

			expect(
				queryByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeNull();

			expect(
				window.localStorage.getItem(
					'connector-overview:previously-synced-alert-dismissed:ds-1'
				)
			).toBe('true');
		});

		it('DISCONNECTED with data: previously-synced alert is hidden on re-render when localStorage flag is set', () => {
			window.localStorage.setItem(
				'connector-overview:previously-synced-alert-dismissed:ds-1',
				'true'
			);

			mockEntityCount(7);

			const {queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(
				queryByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeNull();
		});

		it('dismissing the syncing alert does not hide the previously-synced alert, and vice versa', () => {
			window.localStorage.setItem(
				'connector-overview:syncing-alert-dismissed:ds-1',
				'true'
			);

			mockEntityCount(7);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(
				getByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeTruthy();
		});

		it('INACTIVE without data: no alert', () => {
			mockEntityCount(0);

			const {queryByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(
				queryByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeNull();
		});

		it('INACTIVE with data (90d): info / "Previously synced data remains available" alert', () => {
			mockEntityCount(12);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive),
			});

			expect(
				getByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeTruthy();
		});

		it('DISCONNECTED: info / "Previously synced data remains available" alert', () => {
			mockEntityCount(0);

			const {getByText} = renderOverview({
				dataSource: buildDataSource(DataSourceStatuses.Inactive, {
					state: DataSourceStates.Disconnected,
				}),
			});

			expect(
				getByText(
					/^previously synced data remains available\. reconnect or check your data source connection to resume data syncing\.$/i
				)
			).toBeTruthy();
		});
	});

	describe('Synced Data card — entities table', () => {
		it('renders one row per entity declared in config.entities with the entity-derived label', () => {
			mockEntityCount(0);

			const config = buildConfig({
				entities: [{entity: Entity.Accounts}, {entity: Entity.Events}],
			});

			const {getByText} = renderOverview({config});

			expect(getByText(/^accounts$/i)).toBeTruthy();
			expect(getByText(/^events$/i)).toBeTruthy();
		});

		it('marks each entity row Configured/Unconfigured based on its own count, independent of the others', () => {
			mockEntityCounts({
				[Entity.Accounts]: 0,
				[Entity.Events]: 5,
			});

			const config = buildConfig({
				entities: [{entity: Entity.Accounts}, {entity: Entity.Events}],
			});

			const {getAllByText} = renderOverview({config});

			expect(getAllByText(/^configured$/i)).toHaveLength(1);
			expect(getAllByText(/^unconfigured$/i)).toHaveLength(1);
		});
	});

	describe('Synced Data card — multi-entity hasData aggregation', () => {
		it('treats hasData as true when at least one entity has count > 0 (Accounts: 0, Events: 1) — Available Data alert appears for ACTIVE', () => {
			mockEntityCounts({
				[Entity.Accounts]: 0,
				[Entity.Events]: 1,
			});

			const config = buildConfig({
				entities: [{entity: Entity.Accounts}, {entity: Entity.Events}],
			});

			const {getByText} = renderOverview({
				config,
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeTruthy();
		});

		it('uses the "all data is up to date" Connection Status message when at least one entity has data', () => {
			mockEntityCounts({
				[Entity.Accounts]: 0,
				[Entity.Events]: 3,
			});

			const config = buildConfig({
				entities: [{entity: Entity.Accounts}, {entity: Entity.Events}],
			});

			const {getByText} = renderOverview({
				config,
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				getByText(
					/^all data coming from this data source is up to date\. there are no errors to report\.$/i
				)
			).toBeTruthy();
		});

		it('treats hasData as false when every entity has count 0', () => {
			mockEntityCounts({
				[Entity.Accounts]: 0,
				[Entity.Events]: 0,
			});

			const config = buildConfig({
				entities: [{entity: Entity.Accounts}, {entity: Entity.Events}],
			});

			const {getByText, queryByText} = renderOverview({
				config,
				dataSource: buildDataSource(DataSourceStatuses.Active),
			});

			expect(
				queryByText(
					/^your data may take some time to appear as syncing completes\.$/i
				)
			).toBeNull();

			expect(
				getByText(
					/^you have successfully connected to your data source\. complete your data source configuration to start syncing data\.$/i
				)
			).toBeTruthy();
		});
	});
});
