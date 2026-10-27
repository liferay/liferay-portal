import LifecycleDetailsPanel from '../LifecycleDetailsPanel';
import BasePage from 'shared/components/base-page';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const LIFECYCLE: ILifecycleDetail = {
	authorName: 'Anneta Smith',
	defaultAccountLifecycle: true,
	description: 'Unifies acquisition and expansion.',
	externalReferenceCode: 'erc-1',
	id: '1',
	modifiedDate: Date.UTC(2025, 1, 25, 9, 0),
	name: 'Account Lifecycle',
	stages: [
		{
			accountLifecycleStageRule: {
				filterMetadata: null,
				filterString: '',
			},
			description: 'The buying committee is researching us.',
			externalReferenceCode: 'engaged',
			id: 'engaged',
			maxDuration: null,
			priority: 2,
			stageName: 'Engaged',
		},
		{
			accountLifecycleStageRule: {
				filterMetadata: JSON.stringify({
					conditions: [
						{
							conditionValue: 'Inquiry',
							field: 'salesforce/lead_Funnel_Stage__c',
							fieldDataCategory: 'Text',
							fieldDataType: 'STRING',
							operator: 'is',
						},
						{
							conditionValue: 'Known Prospect',
							field: 'salesforce/lead_Funnel_Stage__c',
							fieldDataCategory: 'Text',
							fieldDataType: 'STRING',
							operator: 'is',
						},
					],
					matchLogic: 'any',
				}),
				filterString: '',
			},
			description: 'Account identified via Demandbase.',
			externalReferenceCode: 'aware',
			id: 'aware',
			maxDuration: 90,
			priority: 1,
			stageName: 'Aware',
		},
	],
};

const mockLifecycle = (lifecycle: ILifecycleDetail | null, error = false) =>
	mockedUseRequest.mockReturnValue({
		data: lifecycle,
		error,
		loading: false,
		refetch: jest.fn(),
	});

const Panel = (
	props: Partial<React.ComponentProps<typeof LifecycleDetailsPanel>>
) => (
	<BasePage documentTitle="Test">
		<BasePage.Sidebar onOpenChange={jest.fn()} open>
			<LifecycleDetailsPanel lifecycleId="1" {...props} />
		</BasePage.Sidebar>
	</BasePage>
);

const renderPanel = (
	props: Partial<React.ComponentProps<typeof LifecycleDetailsPanel>> = {}
) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<Panel {...props} />
			</MemoryRouter>
		</Provider>
	);

const openStagesTab = () =>
	fireEvent.click(screen.getByRole('tab', {name: /^stages$/i}));

describe('LifecycleDetailsPanel', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});

		mockLifecycle(LIFECYCLE);
	});

	afterEach(cleanup);

	it('titles the panel with the lifecycle name and its sticker', () => {
		renderPanel();

		expect(screen.getByText('Account Lifecycle')).toBeInTheDocument();
		expect(screen.queryByRole('img', {name: /default/i})).toBeNull();
	});

	it('shows the back button only when drilled down', () => {
		const onBack = jest.fn();

		const {unmount} = renderPanel();

		expect(screen.queryByRole('button', {name: /^back$/i})).toBeNull();

		unmount();

		renderPanel({onBack});

		fireEvent.click(screen.getByRole('button', {name: /^back$/i}));

		expect(onBack).toHaveBeenCalled();
	});

	it('shows the lifecycle details', () => {
		renderPanel();

		expect(
			screen.getByText('Unifies acquisition and expansion.')
		).toBeInTheDocument();
		expect(screen.getByText(/last modified by/i)).toBeInTheDocument();
		expect(screen.getByText('Anneta Smith')).toBeInTheDocument();
		expect(
			screen.getByText(/^02\/25\/2025, 09:00\sa\.m\.$/)
		).toBeInTheDocument();
		expect(screen.getByText(/^2 stages$/i)).toBeInTheDocument();
	});

	it('reads a single stage and day in the singular', () => {
		mockLifecycle({
			...LIFECYCLE,
			stages: [{...LIFECYCLE.stages![1], maxDuration: 1}],
		});

		renderPanel();

		expect(screen.getByText(/^1 stage$/i)).toBeInTheDocument();

		openStagesTab();

		expect(screen.getByText(/^enabled, 1 day$/i)).toBeInTheDocument();
	});

	it('hides the details the API does not return', () => {
		mockLifecycle({...LIFECYCLE, authorName: null, modifiedDate: null});

		renderPanel();

		expect(screen.queryByText(/last modified by/i)).toBeNull();
		expect(screen.queryByText(/modified date/i)).toBeNull();
	});

	it('lists the stages in display order', () => {
		renderPanel();

		openStagesTab();

		expect(screen.getAllByText(/^stage info$/i)).toHaveLength(2);
		expect(screen.getByText('Aware')).toBeInTheDocument();
		expect(screen.getByText('Engaged')).toBeInTheDocument();
		expect(
			screen
				.getByText('Aware')
				.compareDocumentPosition(screen.getByText('Engaged')) &
				Node.DOCUMENT_POSITION_FOLLOWING
		).toBeTruthy();
	});

	it('shows the max time in stage', () => {
		renderPanel();

		openStagesTab();

		expect(screen.getByText(/^enabled, 90 days$/i)).toBeInTheDocument();
		expect(screen.getByText(/^disabled$/i)).toBeInTheDocument();
	});

	it('reads out the stage trigger with its connector', () => {
		renderPanel();

		openStagesTab();

		const awareCard = screen.getByText('Aware').closest('.card')!;

		expect(
			within(awareCard as HTMLElement).getByText(/^stage trigger$/i)
		).toBeInTheDocument();
		expect(
			within(awareCard as HTMLElement).getAllByText(
				'salesforce/lead_Funnel_Stage__c'
			)
		).toHaveLength(2);
		expect(
			within(awareCard as HTMLElement).getByText("'Inquiry'")
		).toBeInTheDocument();
		expect(
			within(awareCard as HTMLElement).getByText(/^or$/i)
		).toBeInTheDocument();
	});

	it('hides the trigger section of a stage without one', () => {
		renderPanel();

		openStagesTab();

		const engagedCard = screen.getByText('Engaged').closest('.card')!;

		expect(
			within(engagedCard as HTMLElement).queryByText(/^stage trigger$/i)
		).toBeNull();
	});

	it('offers the shared actions without Show Details', () => {
		renderPanel();

		fireEvent.click(screen.getByRole('button', {name: /^menu$/i}));

		expect(
			screen.getByRole('menuitem', {name: /^edit$/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('menuitem', {name: /manage.notifications/i})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('menuitem', {name: /show.details/i})
		).toBeNull();
	});

	it('shows a lifecycle it is given without requesting it', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: false,
			loading: true,
		});

		renderPanel({lifecycle: LIFECYCLE});

		expect(screen.getByText('Account Lifecycle')).toBeInTheDocument();
		expect(
			screen.getByRole('tab', {name: /^details$/i})
		).toBeInTheDocument();
		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({skipRequest: true})
		);
	});

	it('renders an error state when the lifecycle fails to load', () => {
		mockLifecycle(null, true);

		renderPanel();

		expect(screen.queryByRole('tab')).toBeNull();
	});
});
