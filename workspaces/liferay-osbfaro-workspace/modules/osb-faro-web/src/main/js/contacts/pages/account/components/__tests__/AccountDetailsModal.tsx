import AccountDetailsModal from '../AccountDetailsModal';
import React from 'react';
import ReactDOM from 'react-dom';
import {act, cleanup, render, screen} from '@testing-library/react';
import {useRequest} from 'shared/hooks/useRequest';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

jest.unmock('react-dom');

let lastCustomDataRenderers: any;
let lastOnItemsPropSearch: ((item: any, query: string) => boolean) | undefined;
let lastViews: any[] | undefined;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({
		customDataRenderers,
		id,
		items,
		onItemsPropSearch,
		views,
	}: {
		customDataRenderers: any;
		id: string;
		items: any[];
		onItemsPropSearch?: (item: any, query: string) => boolean;
		views?: any[];
	}) => {
		lastCustomDataRenderers = customDataRenderers;
		lastOnItemsPropSearch = onItemsPropSearch;
		lastViews = views;

		return (
			<div data-testid="fds-component" id={id}>
				{(items ?? []).map((item: any) => (
					<div data-testid="fds-item" key={item.name}>
						{item.name}
					</div>
				))}
			</div>
		);
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '23'}),
}));

const mockedUseRequest = useRequest as jest.Mock;

beforeAll(warmFrontendDataSet);

const mockFields = [
	{
		dataSourceId: 'ds-1',
		dataSourceName: 'Salesforce',
		lastModified: '2024-11-20T08:30:00.000Z',
		name: 'website',
		sourceName: 'Web',
		value: 'https://acme.com',
	},
	{
		dataSourceId: 'ds-1',
		dataSourceName: 'Salesforce',
		lastModified: '2024-10-15T10:00:00.000Z',
		name: 'industry',
		sourceName: 'CRM',
		value: 'Technology',
	},
];

const renderModal = (
	overrides: Partial<{
		accountId: string;
		accountName: string;
		onClose: () => void;
	}> = {}
) => {
	const result = render(
		<AccountDetailsModal
			accountId="abc"
			accountName="Acme Corp"
			onClose={jest.fn()}
			{...overrides}
		/>
	);

	act(() => {
		jest.runAllTimers();
	});

	return result;
};

describe('AccountDetailsModal', () => {
	beforeAll(() => {

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => element);
	});

	beforeEach(() => {
		jest.clearAllMocks();
		lastOnItemsPropSearch = undefined;
		mockedUseRequest.mockReturnValue({data: {items: mockFields}});
	});

	afterEach(cleanup);

	it("should render the modal title with the account name's possessive form", () => {
		renderModal();

		expect(screen.getByText("Acme Corp's Attributes")).toBeInTheDocument();
	});

	it('should call useRequest with the account, channel, and group ids', () => {
		renderModal();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				variables: {accountId: 'abc', channelId: '456', groupId: '23'},
			})
		);
	});

	it('should render a row in the data set for each field returned by the request', () => {
		renderModal();

		expect(screen.getByTestId('fds-component')).toBeInTheDocument();

		const items = screen.getAllByTestId('fds-item');

		expect(items).toHaveLength(2);
		expect(items[0]).toHaveTextContent('website');
		expect(items[1]).toHaveTextContent('industry');
	});

	it('should render no rows when the request has no data yet', () => {
		mockedUseRequest.mockReturnValue({data: undefined});

		renderModal();

		expect(screen.queryAllByTestId('fds-item')).toHaveLength(0);
	});

	it('should render a loading indicator instead of the data set while the request is loading', () => {
		mockedUseRequest.mockReturnValue({data: undefined, loading: true});

		const {container} = renderModal();

		expect(screen.queryByTestId('fds-component')).not.toBeInTheDocument();
		expect(container.querySelector('.loading-root')).toBeInTheDocument();
	});

	it('should not offer any column as sortable since the data set is fed a static items array', () => {
		renderModal();

		const fields = lastViews![0].schema.fields;

		expect(fields.every((field: any) => !field.sortable)).toBe(true);
	});

	it('should render a missing attribute value as no data', () => {
		renderModal();

		render(
			lastCustomDataRenderers.attributeNameAndValueRenderer({
				itemData: {},
				value: 'fax',
			})
		);

		expect(screen.getByText('fax')).toBeInTheDocument();
		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

	it('should render a missing last modified date as no data', () => {
		renderModal();

		render(
			lastCustomDataRenderers.lastModifiedRenderer({value: undefined})
		);

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

	it('should match items by name when the data set search has a query', () => {
		renderModal();

		const matches = mockFields.filter((field) =>
			lastOnItemsPropSearch!(field, 'industry')
		);

		expect(matches).toHaveLength(1);
		expect(matches[0].name).toBe('industry');
	});

	it('should match the name filter case-insensitively', () => {
		renderModal();

		const matches = mockFields.filter((field) =>
			lastOnItemsPropSearch!(field, 'WEB')
		);

		expect(matches).toHaveLength(1);
		expect(matches[0].name).toBe('website');
	});
});
