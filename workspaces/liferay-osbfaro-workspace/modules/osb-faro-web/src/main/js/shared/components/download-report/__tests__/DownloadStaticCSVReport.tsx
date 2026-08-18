import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {CSVType} from '../utils';
import {DownloadStaticCSVReport} from '../DownloadStaticCSVReport';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('shared/actions/alerts', () => ({
	actionTypes: {},
	addAlert: jest.fn(() => ({
		meta: {},
		payload: {},
		type: 'addAlert',
	})),
}));

jest.mock('shared/api', () => ({
	csv: {
		fetchCount: jest.fn(),
		fetchCSV: jest.fn(),
	},
}));

const mockedGenerateURL = jest.fn(() => 'http://test-url.com');

jest.mock('../utils', () => {
	const original = jest.requireActual('../utils');
	return {
		...original,
		useDownloadCSV: jest.fn(() => mockedGenerateURL),
	};
});

jest.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});

const {useDownloadCSV: mockedUseDownloadCSV} = jest.requireMock('../utils');

const DefaultComponent = ({
	getFDSQuery,
	objectType,
	rangeSelectors,
	type = CSVType.Individual,
}: {
	getFDSQuery?: () => {filter: string; query: string};
	objectType?: string;
	rangeSelectors?: any;
	type?: CSVType;
}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/123/456/individuals']}>
			<Route path="/workspace/:groupId/:channelId/individuals">
				<MockedProvider
					cache={
						new InMemoryCache({
							addTypename: false,
						})
					}
				>
					<DownloadStaticCSVReport
						disabled={false}
						getFDSQuery={getFDSQuery}
						objectType={objectType}
						rangeSelectors={rangeSelectors}
						type={type}
						typeLang="Individuals"
					/>
				</MockedProvider>
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('DownloadStaticCSVReport', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders component', async () => {
		render(<DefaultComponent />);

		const downloadBtn = screen.getByRole('button', {
			name: /download report/i,
		});

		fireEvent.click(downloadBtn);

		await waitFor(() => {
			expect(screen.getByTestId('submit')).toBeInTheDocument();
		});

		expect(document.body).toMatchSnapshot();
	});

	it('displays modal content', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(9000)
		);

		render(<DefaultComponent />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() => {
			expect(
				screen.getByText(/The generated CSV file supports up to/)
			).toBeInTheDocument();
		});

		expect(screen.getByTestId('cancel')).toBeInTheDocument();
		expect(screen.getByTestId('submit')).toBeInTheDocument();

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(screen.queryByTestId('submit')).not.toBeInTheDocument();
		});
	});

	it('displays info alert about download CSV report', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(9000)
		);

		render(<DefaultComponent />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(addAlert).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Default,
				})
			);
		});
	});

	it('displays warning alert when csv reached 10,000 entries', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(11000)
		);

		render(<DefaultComponent />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(addAlert).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Warning,
				})
			);
		});
	});

	it('displays error alert when csv returns any type of errors', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: false})
		);

		render(<DefaultComponent />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(addAlert).toHaveBeenCalledWith({
				alertType: Alert.Types.Error,
				message: Liferay.Language.get(
					'it-was-not-possible-to-generate-a-csv-file-at-this-moment.-please-try-again-later'
				),
			});
		});
	});

	it('passes objectType and type to the useDownloadCSV hook', () => {
		render(
			<DefaultComponent objectType="ObjectType1" type={CSVType.Asset} />
		);

		expect(mockedUseDownloadCSV).toHaveBeenCalledWith(
			expect.objectContaining({
				objectType: 'ObjectType1',
				type: CSVType.Asset,
			})
		);
	});

	it('reads the filter and query from getFDSQuery and forwards them to the export URL and count request', async () => {
		const getFDSQuery = jest.fn(() => ({
			filter: "(assetType eq 'blog')",
			query: 'liferay',
		}));
		const rangeSelectors = {
			rangeEnd: '',
			rangeKey: '30',
			rangeStart: '',
		};

		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(9000)
		);

		render(
			<DefaultComponent
				getFDSQuery={getFDSQuery}
				rangeSelectors={rangeSelectors}
			/>
		);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(mockedGenerateURL).toHaveBeenCalledWith(rangeSelectors, {
				filter: "(assetType eq 'blog')",
				query: 'liferay',
			});
		});

		expect(API.csv.fetchCount).toHaveBeenCalledWith(
			expect.objectContaining({
				filter: "(assetType eq 'blog')",
				query: 'liferay',
			})
		);
		expect(getFDSQuery).toHaveBeenCalledTimes(1);
	});

	it('forwards an undefined filter and query when getFDSQuery is not provided', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(9000)
		);

		render(<DefaultComponent />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(mockedGenerateURL).toHaveBeenCalledWith(undefined, {
				filter: undefined,
				query: undefined,
			});
		});
	});

	it('requests the count for the type being exported instead of a hardcoded type', async () => {
		(API.csv.fetchCSV as jest.Mock).mockReturnValue(
			Promise.resolve({ok: true})
		);
		(API.csv.fetchCount as jest.Mock).mockReturnValue(
			Promise.resolve(9000)
		);

		render(<DefaultComponent type={CSVType.Asset} />);

		fireEvent.click(
			screen.getByRole('button', {
				name: /download report/i,
			})
		);

		await waitFor(() =>
			expect(screen.getByTestId('submit')).toBeInTheDocument()
		);

		fireEvent.click(screen.getByTestId('submit'));

		await waitFor(() => {
			expect(API.csv.fetchCount).toHaveBeenCalledWith(
				expect.objectContaining({
					type: CSVType.Asset,
				})
			);
		});
	});
});
