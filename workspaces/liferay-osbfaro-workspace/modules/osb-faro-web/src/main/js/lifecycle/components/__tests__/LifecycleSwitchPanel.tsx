import * as API from 'shared/api';
import LifecycleSwitchPanel, {
	LifecyclePanelView,
} from '../LifecycleSwitchPanel';
import mockStore from 'test/mock-store';
import BasePage from 'shared/components/base-page';
import React, {useState} from 'react';
import {
	act,
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useRequest} from 'shared/hooks/useRequest';
import {setBodyWidth} from 'test/body-width';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23', lifecycleId: 'erc-1'}),
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const STAGE = {
	description: '',
	externalReferenceCode: 'stage',
	id: 'stage',
	maxDuration: null,
	priority: 1,
	stageName: 'Aware',
};

const LIFECYCLES: ILifecycleDetail[] = [
	{
		authorName: 'Anneta Smith',
		defaultAccountLifecycle: true,
		description: 'Unifies acquisition and expansion.',
		externalReferenceCode: 'erc-1',
		id: '1',
		modifiedDate: Date.UTC(2026, 9, 2, 17, 53),
		name: 'Growth and Retention Hub',
		stages: [STAGE, {...STAGE, id: 'stage-2'}],
	},
	{
		defaultAccountLifecycle: false,
		externalReferenceCode: 'erc-2',
		id: '2',
		modifiedDate: Date.UTC(2026, 9, 1, 9, 5),
		name: 'Partner Lifecycle',
	},
	{
		authorName: 'Marcus Reed',
		defaultAccountLifecycle: false,
		externalReferenceCode: 'erc-3',
		id: '3',
		name: 'Expansion Lifecycle',
		stages: [STAGE],
	},
];

const Panel = ({
	initialView = {type: 'switch'},
	onDefaultChange = jest.fn(),
	openLifecycle = LIFECYCLES[0],
}: {
	initialView?: LifecyclePanelView | null;
	onDefaultChange?: (lifecycleId: string) => void;
	openLifecycle?: ILifecycleDetail;
}) => {
	const [view, setView] = useState<LifecyclePanelView | null>(initialView);

	return (
		<BasePage documentTitle="Test">
			<span data-testid="view">{JSON.stringify(view)}</span>

			<LifecycleSwitchPanel
				defaultChangeCount={0}
				onDefaultChange={onDefaultChange}
				onViewChange={setView}
				openLifecycle={openLifecycle}
				view={view}
			/>
		</BasePage>
	);
};

const mockLifecyclesPage = (items: ILifecycleDetail[], totalCount = 3) =>
	mockedUseRequest.mockImplementation(
		({
			dataSourceFn,
			variables,
		}: {
			dataSourceFn: unknown;
			variables: {lifecycleId?: string};
		}) => ({
			data:
				dataSourceFn === API.lifecycle.fetchLifecyclesPage
					? {
							items,
							lastPage: 1,
							page: 1,
							pageSize: 20,
							totalCount,
						}
					: LIFECYCLES.find(({id}) => id === variables.lifecycleId),
			error: false,
			loading: false,
			refetch: jest.fn(),
		})
	);

const getPageRequests = () =>
	mockedUseRequest.mock.calls
		.map(([options]) => options)
		.filter(
			({dataSourceFn}) =>
				dataSourceFn === API.lifecycle.fetchLifecyclesPage
		)
		.map(({variables}) => variables);

const renderPanel = (props: React.ComponentProps<typeof Panel> = {}) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<Panel {...props} />
			</MemoryRouter>
		</Provider>
	);

const getRow = (name: string) =>
	screen.getByRole('link', {name}).closest('li') as HTMLElement;

const getRowMenu = (name: string) =>
	within(getRow(name)).getByRole('button', {name: /actions for/i});

const openRowMenu = (name: string) => fireEvent.click(getRowMenu(name));

describe('LifecycleSwitchPanel', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		setBodyWidth(1440);

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});

		mockLifecyclesPage(LIFECYCLES);
	});

	afterEach(cleanup);

	it('lists every lifecycle linking to its dashboard by external reference code', () => {
		renderPanel();

		expect(
			screen.getByRole('link', {name: 'Partner Lifecycle'})
		).toHaveAttribute(
			'href',
			toRoute(Routes.LIFECYCLE_DASHBOARD, {
				channelId: '123',
				groupId: '23',
				lifecycleId: 'erc-2',
			})
		);
		expect(document.querySelectorAll('.list-group-item-flex')).toHaveLength(
			3
		);
	});

	it('pins only the default and keeps the other rows aligned', () => {
		renderPanel();

		expect(
			within(getRow('Growth and Retention Hub')).getByRole('img', {
				name: /default/i,
			})
		).toBeInTheDocument();
		expect(
			within(getRow('Partner Lifecycle')).queryByRole('img', {
				name: /default/i,
			})
		).toBeNull();
		expect(
			getRow('Partner Lifecycle').querySelector('.sticker.invisible')
		).toBeInTheDocument();
	});

	it('highlights the open lifecycle', () => {
		renderPanel();

		expect(getRow('Growth and Retention Hub')).toHaveClass('active');
		expect(getRow('Partner Lifecycle')).not.toHaveClass('active');
		expect(getRow('Expansion Lifecycle')).not.toHaveClass('active');
	});

	it('highlights the open lifecycle by its external reference code across versions', () => {
		renderPanel({openLifecycle: {...LIFECYCLES[0], id: '10'}});

		expect(getRow('Growth and Retention Hub')).toHaveClass('active');
	});

	it('shows the modified date and its author when known', () => {
		renderPanel();

		expect(
			within(getRow('Growth and Retention Hub')).getByText(
				/^Oct 2, 2026, 05:53\sp\.m\. by Anneta Smith$/
			)
		).toBeInTheDocument();
		expect(
			within(getRow('Partner Lifecycle')).getByText(
				/^Oct 1, 2026, 09:05\sa\.m\.$/
			)
		).toBeInTheDocument();
		expect(
			within(getRow('Expansion Lifecycle')).queryByText(/marcus reed/i)
		).toBeNull();
	});

	it('labels the stage count only when stages are present', () => {
		renderPanel();

		expect(
			within(getRow('Growth and Retention Hub')).getByText(/^2 stages$/i)
		).toBeInTheDocument();
		expect(
			within(getRow('Partner Lifecycle')).queryByText(/stage/i)
		).toBeNull();
		expect(
			within(getRow('Expansion Lifecycle')).getByText(/^1 stage$/i)
		).toBeInTheDocument();
	});

	it('requests the first page of 20 for the open channel', () => {
		renderPanel();

		expect(getPageRequests()[0]).toEqual(
			expect.objectContaining({
				channelId: '123',
				groupId: '23',
				page: 1,
				pageSize: 20,
				search: '',
			})
		);
	});

	it('searches by name once typing pauses, from the first page', () => {
		mockLifecyclesPage(LIFECYCLES, 45);

		renderPanel();

		fireEvent.click(screen.getByText('2', {selector: '.page-link'}));

		expect(getPageRequests().pop()).toEqual(
			expect.objectContaining({page: 2, search: ''})
		);

		const requestsBeforeTyping = getPageRequests().length;

		fireEvent.change(screen.getByRole('textbox', {name: /search/i}), {
			target: {value: 'partner'},
		});

		act(() => {
			jest.advanceTimersByTime(250);
		});

		const newRequests = new Set(
			getPageRequests()
				.slice(requestsBeforeTyping)
				.map(({page, search}) => JSON.stringify({page, search}))
		);

		newRequests.delete(JSON.stringify({page: 2, search: ''}));

		expect(Array.from(newRequests)).toEqual([
			JSON.stringify({page: 1, search: 'partner'}),
		]);
	});

	it('summarizes the entries shown under the list', () => {
		renderPanel();

		expect(
			screen.getByText(/showing 1 to 3 of 3 entries/i)
		).toBeInTheDocument();
	});

	it('shows the description of each lifecycle', () => {
		renderPanel();

		expect(
			within(getRow('Growth and Retention Hub')).getByText(
				'Unifies acquisition and expansion.'
			)
		).toBeInTheDocument();
	});

	it('names the menu of every row after its lifecycle', () => {
		renderPanel();

		expect(getRowMenu('Partner Lifecycle')).toHaveAccessibleName(
			/actions for partner lifecycle/i
		);
		expect(getRowMenu('Expansion Lifecycle')).toHaveAccessibleName(
			/actions for expansion lifecycle/i
		);
	});

	it('marks the open lifecycle as the current page', () => {
		renderPanel();

		expect(
			screen.getByRole('link', {name: 'Growth and Retention Hub'})
		).toHaveAttribute('aria-current', 'page');
		expect(
			screen.getByRole('link', {name: 'Partner Lifecycle'})
		).not.toHaveAttribute('aria-current');
	});

	it('shows only the loading state while loading', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: false,
			loading: true,
		});

		renderPanel();

		expect(document.querySelector('.list-group-item')).toBeNull();
		expect(screen.queryByText(/showing/i)).toBeNull();
	});

	it('shows no results for an empty page', () => {
		mockLifecyclesPage([], 0);

		renderPanel();

		expect(screen.getByText(/no results found/i)).toBeInTheDocument();
		expect(document.querySelector('.list-group-item')).toBeNull();
		expect(screen.queryByText(/showing/i)).toBeNull();
	});

	it('hides View on the open lifecycle row', () => {
		renderPanel();

		openRowMenu('Growth and Retention Hub');

		expect(screen.queryByRole('menuitem', {name: /^view$/i})).toBeNull();
	});

	it('offers View on the other rows', () => {
		renderPanel();

		openRowMenu('Partner Lifecycle');

		expect(
			screen.getByRole('menuitem', {name: /^view$/i})
		).toBeInTheDocument();
	});

	it('hides View in the details of the open lifecycle', () => {
		renderPanel({
			initialView: {lifecycleId: '1', parent: 'switch', type: 'details'},
		});

		fireEvent.click(screen.getByRole('button', {name: /^menu$/i}));

		expect(screen.queryByRole('menuitem', {name: /^view$/i})).toBeNull();
	});

	it('offers View in the details of another lifecycle', () => {
		renderPanel({
			initialView: {lifecycleId: '2', parent: 'switch', type: 'details'},
		});

		fireEvent.click(screen.getByRole('button', {name: /^menu$/i}));

		expect(
			screen.getByRole('menuitem', {name: /^view$/i})
		).toBeInTheDocument();
	});

	it('drills down into the details and back to the list', () => {
		renderPanel();

		openRowMenu('Partner Lifecycle');

		fireEvent.click(screen.getByRole('menuitem', {name: /show.details/i}));

		expect(screen.getByTestId('view')).toHaveTextContent(
			JSON.stringify({
				lifecycleId: '2',
				parent: 'switch',
				type: 'details',
			})
		);
		expect(
			screen.getByRole('tab', {name: /^details$/i})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: /^back$/i}));

		expect(screen.getByTestId('view')).toHaveTextContent(
			JSON.stringify({type: 'switch'})
		);
		expect(
			screen.getByRole('link', {name: 'Partner Lifecycle'})
		).toBeInTheDocument();
	});

	it('reports a new default set from a row', async () => {
		const onDefaultChange = jest.fn();

		renderPanel({onDefaultChange});

		openRowMenu('Partner Lifecycle');

		fireEvent.click(screen.getByRole('menuitem', {name: /set.default/i}));

		expect(API.lifecycle.setDefaultLifecycle).toHaveBeenCalledWith({
			groupId: '23',
			lifecycleId: '2',
		});

		await screen.findByRole('link', {name: 'Partner Lifecycle'});

		expect(onDefaultChange).toHaveBeenCalledWith('2');
	});

	it('closes from the title link when it overlays the page', () => {
		setBodyWidth(1024);

		renderPanel();

		fireEvent.click(screen.getByRole('link', {name: 'Partner Lifecycle'}));

		expect(screen.getByTestId('view')).toHaveTextContent('null');
	});

	it('stays open from the title link when it pushes the page', () => {
		renderPanel();

		fireEvent.click(screen.getByRole('link', {name: 'Partner Lifecycle'}));

		expect(screen.getByTestId('view')).toHaveTextContent(
			JSON.stringify({type: 'switch'})
		);
	});
});
