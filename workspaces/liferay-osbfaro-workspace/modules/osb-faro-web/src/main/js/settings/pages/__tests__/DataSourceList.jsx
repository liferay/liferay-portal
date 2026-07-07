import React from 'react';
import {
	AddDataSourceNav,
	DataSourceName,
	disableRow,
	StatusRenderer
} from '../DataSourceList';
import {DataSourceStates, DataSourceTypes} from 'shared/util/constants';
import {MemoryRouter} from 'react-router-dom';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('DataSourceList exports', () => {
	describe('disableRow', () => {
		it('returns true when the data source is being deleted', () => {
			expect(
				disableRow({state: DataSourceStates.InProgressDeleting})
			).toBe(true);
		});

		it('returns false for any other state', () => {
			expect(disableRow({state: DataSourceStates.Ready})).toBe(false);
			expect(disableRow({state: DataSourceStates.CredentialsValid})).toBe(
				false
			);
			expect(disableRow({state: DataSourceStates.Disconnected})).toBe(
				false
			);
		});
	});

	describe('AddDataSourceNav', () => {
		it('renders a button that navigates directly when only one data source option is available', () => {
			const onClick = jest.fn();

			const {container, getByText} = render(
				<AddDataSourceNav items={[{label: 'Liferay DXP', onClick}]} />
			);

			expect(getByText('Add Data Source')).toBeInTheDocument();

			fireEvent.click(container.querySelector('button'));

			expect(onClick).toHaveBeenCalledTimes(1);
		});

		it('renders a dropdown that does not navigate directly when more than one data source option is available', () => {
			const firstOnClick = jest.fn();
			const secondOnClick = jest.fn();

			const {container, getByText} = render(
				<AddDataSourceNav
					items={[
						{label: 'Liferay DXP', onClick: firstOnClick},
						{label: 'Salesforce', onClick: secondOnClick}
					]}
				/>
			);

			expect(getByText('Add Data Source')).toBeInTheDocument();

			fireEvent.click(container.querySelector('button'));

			expect(firstOnClick).not.toHaveBeenCalled();
			expect(secondOnClick).not.toHaveBeenCalled();
		});
	});

	describe('StatusRenderer', () => {
		it('renders Not Configured when the data source has no state', () => {
			const {getByText} = render(<StatusRenderer data={{state: null}} />);

			expect(getByText(/Not Configured/)).toBeInTheDocument();
		});

		describe('3rd-party connector framework', () => {
			it.each([
				['Demandbase', DataSourceTypes.Demandbase],
				['Hubspot', DataSourceTypes.Hubspot]
			])(
				'renders Active for a %s connector with status ACTIVE',
				(_, providerType) => {
					const {getByText} = render(
						<StatusRenderer
							data={{
								providerType,
								state: DataSourceStates.CredentialsValid,
								status: 'ACTIVE'
							}}
						/>
					);

					expect(getByText(/Active/)).toBeInTheDocument();
				}
			);

			it.each([
				['Demandbase', DataSourceTypes.Demandbase],
				['Hubspot', DataSourceTypes.Hubspot]
			])(
				'renders Inactive for a %s connector with status INACTIVE',
				(_, providerType) => {
					const {getByText} = render(
						<StatusRenderer
							data={{
								providerType,
								state: DataSourceStates.CredentialsValid,
								status: 'INACTIVE'
							}}
						/>
					);

					expect(getByText(/Inactive/)).toBeInTheDocument();
				}
			);

			it.each([
				['Demandbase', DataSourceTypes.Demandbase],
				['Hubspot', DataSourceTypes.Hubspot]
			])(
				'renders Disconnected for a manually disconnected %s connector (state=DISCONNECTED takes precedence over status=INACTIVE)',
				(_, providerType) => {
					const {getByText, queryByText} = render(
						<StatusRenderer
							data={{
								providerType,
								state: DataSourceStates.Disconnected,
								status: 'INACTIVE'
							}}
						/>
					);

					expect(getByText(/Disconnected/)).toBeInTheDocument();
					expect(queryByText(/^Inactive$/)).toBeNull();
				}
			);
		});

		describe('legacy data sources', () => {
			it('falls back to the legacy display object for Liferay DXP data sources', () => {
				const {getByText} = render(
					<StatusRenderer
						data={{
							providerType: DataSourceTypes.Liferay,
							state: DataSourceStates.Disconnected
						}}
					/>
				);

				expect(getByText(/Disconnected/)).toBeInTheDocument();
			});

			it('falls back to the legacy display object for Salesforce data sources', () => {
				const {getByText} = render(
					<StatusRenderer
						data={{
							providerType: DataSourceTypes.Salesforce,
							state: DataSourceStates.CredentialsInvalid
						}}
					/>
				);

				expect(getByText(/Invalid Credentials/)).toBeInTheDocument();
			});

			it('falls back to the legacy display object when the providerType is unknown', () => {
				const {getByText} = render(
					<StatusRenderer
						data={{
							providerType: 'NOT_A_REGISTERED_CONNECTOR',
							state: DataSourceStates.Ready
						}}
					/>
				);

				expect(getByText(/Active/)).toBeInTheDocument();
			});
		});
	});

	describe('DataSourceName', () => {
		const renderInRouter = ui => render(<MemoryRouter>{ui}</MemoryRouter>);

		it('renders a link to the data source when not being deleted', () => {
			const {container, getByText} = renderInRouter(
				<DataSourceName
					data={{
						id: 'ds-1',
						name: 'My Data Source',
						state: DataSourceStates.Ready
					}}
					hrefFormatter={({id}) => `/ds/${id}`}
				/>
			);

			const link = container.querySelector('a');

			expect(link).not.toBeNull();
			expect(link.getAttribute('href')).toBe('/ds/ds-1');
			expect(getByText('My Data Source')).toBeInTheDocument();
		});

		it('renders the name as plain text (no link) while the data source is being deleted', () => {
			const {container, getByText} = renderInRouter(
				<DataSourceName
					data={{
						id: 'ds-1',
						name: 'Deleting Data Source',
						state: DataSourceStates.InProgressDeleting
					}}
					hrefFormatter={() => '/should-not-be-used'}
				/>
			);

			expect(container.querySelector('a')).toBeNull();
			expect(getByText('Deleting Data Source')).toBeInTheDocument();
		});
	});
});
