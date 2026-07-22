import ClayForm from '@clayui/form';
import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {ApolloProvider} from '@apollo/client';
import {
	Checkbox,
	formattedContainers,
	ReportContainer,
} from '../DownloadPDFReport';
import {CSVType, useDownloadCSV} from '../utils';
import {DownloadReportButton} from '../DownloadReportButton';
import {DownloadReportModal, ReportType} from '../DownloadReportModal';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {MemoryRouter} from 'react-router-dom';
import {sub} from 'shared/util/lang';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '2000',
		query: {
			rangeKey: RangeKeyTimeRanges.Last30Days,
		},
	}),
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

interface IWrapperCSVComponentProps extends React.HTMLAttributes<HTMLElement> {
	type: CSVType;
}

let CSV_URL = '';

const WrapperCSVComponent: React.FC<IWrapperCSVComponentProps> = ({
	type,
	...props
}) => {
	const generateURL = useDownloadCSV({
		assetId: '123',
		assetType: 'myAssetType',
		type,
	});

	return (
		<WrapperComponent
			{...props}
			alertMessage={
				sub(
					Liferay.Language.get(
						'the-x-file-is-being-generated-and-your-download-will-start-soon'
					),
					['CSV']
				) as string
			}
			infoMessage={
				sub(
					Liferay.Language.get(
						'the-x-list-will-be-downloaded-respecting-the-current-ordering,-filter,-and-search-results.-please-verify-if-the-desired-changes-are-applied'
					),
					[Liferay.Language.get('individuals')]
				) as string
			}
			onSubmit={(rangeSelectors) => {
				const url = generateURL(rangeSelectors);

				CSV_URL = url;
			}}
			type={ReportType.CSV}
		/>
	);
};

const WrapperPDFomponent = ({
	children,
	...otherProps
}: {
	children?: React.ReactNode;
	[key: string]: any;
}) => (
	<WrapperComponent
		alertMessage={
			sub(
				Liferay.Language.get(
					'the-x-file-is-being-generated-and-your-download-will-start-soon'
				),
				['PDF']
			) as string
		}
		infoMessage={Liferay.Language.get(
			'the-dashboard-will-be-downloaded-exactly-as-it-is-displayed-on-your-screen.-please-verify-if-the-desired-tabs-and-filters-are-selected-before-proceeding'
		)}
		onSubmit={jest.fn()}
		type={ReportType.PDF}
		{...otherProps}
	>
		{children}
	</WrapperComponent>
);

interface IWrapperComponent {
	alertMessage: string;
	children?: React.ReactNode;
	infoMessage: string;
	onSubmit: (rangeSelectors?: RangeSelectors) => void;
	type: ReportType;
}

const WrapperComponent: React.FC<IWrapperComponent> = ({
	children,
	infoMessage,
	onSubmit,
	type,
	...otherProps
}) => {
	const [visible, setVisible] = useState(false);
	const {observer} = useModal({onClose: () => setVisible(false)});

	return (
		<>
			{visible && (
				<ApolloProvider client={client}>
					<MemoryRouter>
						<MockedProvider
							mocks={[mockTimeRangeReq(), mockPreferenceReq()]}
						>
							<Provider store={mockStore()}>
								<DownloadReportModal
									{...otherProps}
									infoMessage={infoMessage}
									observer={observer}
									onClose={jest.fn()}
									onSubmit={onSubmit}
									type={type}
								>
									{children}
								</DownloadReportModal>
							</Provider>
						</MockedProvider>
					</MemoryRouter>
				</ApolloProvider>
			)}

			<DownloadReportButton
				disabled={false}
				onClick={() => setVisible(true)}
			/>
		</>
	);
};

const generateCSVURL = (type: CSVType) => {
	const {getByRole, getByTestId} = render(
		<WrapperCSVComponent type={type} />
	);

	fireEvent.click(
		getByRole('button', {
			name: /download report/i,
		})
	);

	act(() => {
		jest.runAllTimers();
	});

	const submitButton = getByTestId('submit');

	fireEvent.click(submitButton);
};

describe('DownloadReportModal CSV', () => {
	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeAll(() => {
		CSV_URL = '';

		jest.useFakeTimers();

		// @ts-ignore
		ReactDOM.createPortal = jest.fn((element) => element);
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('renders component', async () => {
		const {container, getByRole, getByTestId, getByText} = render(
			<WrapperCSVComponent type={CSVType.Blog} />
		);

		fireEvent.click(
			getByRole('button', {
				name: /download report/i,
			})
		);

		act(() => {
			jest.runAllTimers();
		});

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Select Date Range')).toBeInTheDocument();

		expect(
			getByRole('heading', {
				name: /download report/i,
			})
		).toBeInTheDocument();

		expect(
			getByText(
				'The Individuals list will be downloaded respecting the current ordering, filter, and search results. Please verify if the desired changes are applied.'
			)
		);

		expect(getByText('Date Range')).toBeInTheDocument();

		expect(getByTestId('cancel')).toBeInTheDocument();
		expect(getByTestId('submit')).toBeInTheDocument();
	});

	it.each`
		name
		${CSVType.Blog}
		${CSVType.Document}
		${CSVType.Event}
		${CSVType.Form}
		${CSVType.Individual}
		${CSVType.Journal}
		${CSVType.Page}
	`('generate a link to download CSV report for type $name', ({name}) => {
		generateCSVURL(name);

		expect(CSV_URL).toEqual(
			`/o/faro/main/2000/reports/export/csv/${name}?channelId=456&rangeKey=30&assetId=123&assetType=myAssetType`
		);
	});
});

describe('DownloadReportModal PDF', () => {
	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeAll(() => {
		jest.useFakeTimers();

		// @ts-ignore
		ReactDOM.createPortal = jest.fn((element) => element);
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('renders component', async () => {
		const containers = [
			ReportContainer.AcquisitionsCard,
			ReportContainer.ActiveIndividualsCard,
			ReportContainer.AssetAppearsOnCard,
			ReportContainer.AudienceCard,
			ReportContainer.CohortAnalysisCard,
			ReportContainer.CurrentTotalsCard,
			ReportContainer.DistributionBreakdownCard,
			ReportContainer.DownloadsByLocationCard,
			ReportContainer.DownloadsByTechnologyCard,
			ReportContainer.EnrichedProfilesCard,
			ReportContainer.InterestsCard,
			ReportContainer.SearchTermsCard,
			ReportContainer.SegmentCompositionCard,
			ReportContainer.SegmentCriteriaCard,
			ReportContainer.SegmentMembershipCard,
			ReportContainer.SessionsByLocationCard,
			ReportContainer.SessionTechnologyCard,
			ReportContainer.SiteActivityCard,
			ReportContainer.SubmissionsByLocationCard,
			ReportContainer.SubmissionsByTechnologyCard,
			ReportContainer.TopInterestsAsOfYesterdayCard,
			ReportContainer.TopInterestsCard,
			ReportContainer.TopPagesCard,
			ReportContainer.ViewsByLocationCard,
			ReportContainer.ViewsByTechnologyCard,
			ReportContainer.VisitorsBehaviorCard,
			ReportContainer.VisitorsByTimeCard,
		];

		const {container, getByRole, getByTestId, getByText} = render(
			<WrapperPDFomponent>
				<ClayForm.Group>
					<label>{Liferay.Language.get('dashboard-reports')}</label>

					{Object.values(formattedContainers(containers)).map(
						({id, label}) => (
							<Checkbox
								key={id}
								label={label}
								onChange={jest.fn()}
							/>
						)
					)}
				</ClayForm.Group>
			</WrapperPDFomponent>
		);

		fireEvent.click(
			getByRole('button', {
				name: /download report/i,
			})
		);

		act(() => {
			jest.runAllTimers();
		});

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Select Date Range')).toBeInTheDocument();

		expect(
			getByRole('heading', {
				name: /download report/i,
			})
		).toBeInTheDocument();

		expect(
			getByText(
				'The dashboard will be downloaded exactly as it is displayed on your screen. Please verify if the desired tabs and filters are selected before proceeding.'
			)
		);

		expect(
			getByText(
				'Only select a date range if you want to modify the current date filter.'
			)
		).toBeInTheDocument();

		expect(getByText('Date Range')).toBeInTheDocument();

		expect(getByTestId('cancel')).toBeInTheDocument();
		expect(getByTestId('submit')).toBeInTheDocument();
	});
});
