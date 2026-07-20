import 'test/mock-modal';

import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import SelectPageAssetInput, {
	getAssetTypeLabel,
	getCompatibleAssetTypes
} from '../SelectPageAssetInput';
import {act, cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {close, open} from 'shared/actions/modals';
import {OrderedMap} from 'immutable';
import {Provider} from 'react-redux';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	activities: {
		searchAssets: jest.fn(() => Promise.resolve({items: [], totalCount: 0}))
	},
	assets: {
		searchTypes: jest.fn(() =>
			Promise.resolve({
				items: [
					{id: 'CMSBasicWebContent', name: 'CMSBasicWebContent'},
					{
						id: 'cms-basic-web-content',
						name: 'cms-basic-web-content'
					},
					{id: 'document', name: 'document'},
					{id: 'webContent', name: 'webContent'}
				],
				totalCount: 4
			})
		)
	}
}));

// Object-definition types come only from LDP plans, so the component gates the
// asset-summary-types request on it; default to enabled and override per test.

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn(() => true)
}));

const defaultProps = {
	channelId: 'ch-1',
	groupId: 'gp-1'
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<SelectPageAssetInput {...defaultProps} {...props} />
	</Provider>
);

describe('getAssetTypeLabel', () => {
	it('should map predefined DXP type ids to their localized label', () => {
		expect(getAssetTypeLabel('blogs')).toBe('Blogs');
		expect(getAssetTypeLabel('document')).toBe('Documents and Media');
		expect(getAssetTypeLabel('forms')).toBe('Forms');
		expect(getAssetTypeLabel('webContent')).toBe('Web Content');
	});

	it('should return the raw name for non-predefined types (object definitions)', () => {
		expect(getAssetTypeLabel('CMSBasicDocument', 'CMSBasicDocument')).toBe(
			'CMSBasicDocument'
		);
		expect(getAssetTypeLabel('C_MyNewStructure')).toBe('C_MyNewStructure');
	});
});

describe('getCompatibleAssetTypes (asset types the picker offers)', () => {
	// The request (asset-summary-types) returns only ObjectEntry object-
	// definition names here (no DXP types), to prove the DXP types the picker
	// offers are fixed by the mapping, not read from the request.

	const OBJECT_DEFINITION_TYPES = [
		{id: 'CMSBasicDocument', name: 'CMSBasicDocument'},
		{id: 'C_MyNewStructure', name: 'C_MyNewStructure'}
	];

	it('should offer every DXP type plus the object definitions, independent of the event', () => {
		expect(
			getCompatibleAssetTypes(OBJECT_DEFINITION_TYPES).map(({id}) => id)
		).toEqual([
			'blogs',
			'document',
			'forms',
			'webContent',
			'CMSBasicDocument',
			'C_MyNewStructure'
		]);
	});

	it('should offer the fixed DXP types when no object definitions are available', () => {
		expect(getCompatibleAssetTypes([]).map(({id}) => id)).toEqual([
			'blogs',
			'document',
			'forms',
			'webContent'
		]);
	});

	it('should include only the asset-summary types that resolve to ObjectEntry', () => {
		// DXP-typed asset-summary items are already covered by the fixed types,
		// so they are not duplicated from the request; only object definitions
		// (which resolve to ObjectEntry) are appended.

		const withDxpNoise = [
			{id: 'blog', name: 'blog'},
			{id: 'document', name: 'document'},
			{id: 'C_MyNewStructure', name: 'C_MyNewStructure'}
		];

		expect(getCompatibleAssetTypes(withDxpNoise).map(({id}) => id)).toEqual([
			'blogs',
			'document',
			'forms',
			'webContent',
			'C_MyNewStructure'
		]);
	});
});

describe('SelectPageAssetInput', () => {
	beforeAll(() => {
		close.mockReturnValue({type: 'close'});
	});

	afterEach(() => {
		cleanup();
		open.mockClear();
		close.mockClear();
		API.activities.searchAssets.mockClear();
		API.assets.searchTypes.mockClear();
		useLDPEnabled.mockReturnValue(true);
	});

	const openPicker = (getByRole, name) =>
		fireEvent.click(getByRole('combobox', {name}));

	describe('default asset type', () => {
		it('should show the placeholder and disable add-assets until a type is chosen', async () => {
			const {getAllByRole, getByRole, getByText} = render(
				<DefaultComponent action='view' />
			);

			// Once the asset types load, no type is preselected: the picker shows
			// the "Select a Type" placeholder and add-assets stays disabled.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			expect(getAllByRole('combobox')).toHaveLength(2);
			expect(getByText('Select a Type')).toBeTruthy();
			expect(getByRole('button', {name: /add assets/i})).toBeDisabled();
		});

		it('should not emit anything on mount', () => {
			const onSelectionsChange = jest.fn();

			render(
				<DefaultComponent
					action='view'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			expect(onSelectionsChange).not.toHaveBeenCalled();
		});
	});

	describe('page selector', () => {
		it('should reveal the add-pages button and emit Page after choosing Page', () => {
			const onSelectionsChange = jest.fn();

			const {getByRole, getByText} = render(
				<DefaultComponent
					action='view'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));

			expect(getByText(/add pages/i)).toBeTruthy();
			expect(onSelectionsChange).toHaveBeenCalledWith({
				applicationId: 'Page',
				eventId: 'pageViewed',
				selections: []
			});
		});

		it('should open the modal with the select-page config', () => {
			const {getByRole, getByText} = render(
				<DefaultComponent action='view' />
			);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));
			fireEvent.click(getByText(/add pages/i));

			expect(open).toHaveBeenCalled();

			const config = open.mock.calls[0][1];

			expect(config.title).toBe('Select Page');
			expect(config.rowIdentifier).toBe('id');
			expect(config.submitMessage).toBe('Select');
			expect(typeof config.dataSourceFn).toBe('function');
		});
	});

	describe('asset type selector', () => {
		it('should emit the type and enable add-assets after choosing a type', async () => {
			const onSelectionsChange = jest.fn();

			const {getByRole, getByText} = render(
				<DefaultComponent
					action='view'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			// Wait for the picker to finish loading, then choose a type.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			openPicker(getByRole, 'Asset Type');
			fireEvent.click(getByText('Web Content'));

			expect(getByRole('button', {name: /add assets/i})).toBeEnabled();
			expect(onSelectionsChange).toHaveBeenCalledWith({
				applicationId: 'WebContent',
				eventId: 'webContentViewed',
				selections: []
			});
		});

		it('should open the modal with the select-asset config', async () => {
			const {getByRole, getByText} = render(
				<DefaultComponent action='view' />
			);

			// Choose a type so the add-assets button becomes available.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			openPicker(getByRole, 'Asset Type');
			fireEvent.click(getByText('Web Content'));

			fireEvent.click(getByRole('button', {name: /add assets/i}));

			const config = open.mock.calls[0][1];

			expect(config.title).toBe('Select Asset');
			expect(config.rowIdentifier).toBe('id');
			expect(config.submitMessage).toBe('Select');
			expect(typeof config.dataSourceFn).toBe('function');
			expect(config.columns.length).toBeGreaterThan(0);
		});
	});

	describe('selection', () => {
		it('should render a removable chip per selected item (controlled)', () => {
			const {getByText} = render(
				<DefaultComponent
					action='download'
					selectedItems={[
						{id: 'p-1', name: 'Home'},
						{id: 'p-2', name: 'About'}
					]}
				/>
			);

			expect(getByText('Home')).toBeTruthy();
			expect(getByText('About')).toBeTruthy();
		});

		it('should emit every selected item as a selection on modal submit', () => {
			const onSelectionsChange = jest.fn();

			const {getByRole, getByText} = render(
				<DefaultComponent
					action='view'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));
			fireEvent.click(getByText(/add pages/i));

			const {onSubmit} = open.mock.calls[0][1];

			act(() => {
				onSubmit(
					new OrderedMap({
						'p-1': {id: 'p-1', name: 'Home'},
						'p-2': {id: 'p-2', name: 'About'}
					})
				);
			});

			expect(onSelectionsChange).toHaveBeenLastCalledWith({
				applicationId: 'Page',
				eventId: 'pageViewed',
				selections: [
					{
						activityKey: 'Page#pageViewed#p-1',
						id: 'p-1',
						name: 'Home'
					},
					{
						activityKey: 'Page#pageViewed#p-2',
						id: 'p-2',
						name: 'About'
					}
				]
			});
			expect(close).toHaveBeenCalled();
		});

		it('should emit the remaining selections when a chip is removed', () => {
			const onSelectionsChange = jest.fn();

			const {getAllByLabelText} = render(
				<DefaultComponent
					action='download'
					applicationId='Document'
					onSelectionsChange={onSelectionsChange}
					selectedItems={[
						{
							activityKey: 'Document#documentDownloaded#p-1',
							id: 'p-1',
							name: 'Home'
						},
						{
							activityKey: 'Document#documentDownloaded#p-2',
							id: 'p-2',
							name: 'About'
						}
					]}
				/>
			);

			fireEvent.click(getAllByLabelText('Close')[0]);

			// The removed chip drops out; the surviving selection keeps its
			// already-built activityKey.

			expect(onSelectionsChange).toHaveBeenCalledWith(
				expect.objectContaining({
					selections: [
						{
							activityKey: 'Document#documentDownloaded#p-2',
							id: 'p-2',
							name: 'About'
						}
					]
				})
			);
		});

		it('should emit an empty selection when the selector type changes', () => {
			const onSelectionsChange = jest.fn();

			const {getByRole, getByText} = render(
				<DefaultComponent
					action='view'
					onSelectionsChange={onSelectionsChange}
					selectedItems={[{id: 'p-1', name: 'Home'}]}
				/>
			);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));

			expect(onSelectionsChange).toHaveBeenCalledWith(
				expect.objectContaining({selections: []})
			);
		});
	});

	describe('asset-only action', () => {
		it('should render Asset Type as static text (no page option) when the action is asset-only', () => {
			const {getAllByRole, getByText, queryByText} = render(
				<DefaultComponent action='download' />
			);

			// Asset-only actions show the "Asset Type" label as static text and
			// offer no page option; only the asset-type picker is a dropdown.

			expect(getByText('Asset Type')).toBeTruthy();
			expect(queryByText(/add pages/i)).toBeNull();
			expect(getAllByRole('combobox')).toHaveLength(1);
		});

		it('should show the placeholder and disable add-assets until a type is chosen', async () => {
			const {getByRole, getByText} = render(
				<DefaultComponent action='click' />
			);

			// Asset-only actions still start with no type: the picker shows the
			// placeholder and add-assets stays disabled until a type is chosen.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			expect(getByText('Select a Type')).toBeTruthy();
			expect(getByRole('button', {name: /add assets/i})).toBeDisabled();
		});

		it('should build a valid activityKey from the chosen click type', async () => {
			const onSelectionsChange = jest.fn();

			const {getByRole, getByText} = render(
				<DefaultComponent
					action='click'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			// Choose the Blog type, then pick a specific asset for it.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			openPicker(getByRole, 'Asset Type');
			fireEvent.click(getByText('Blogs'));

			fireEvent.click(getByRole('button', {name: /add assets/i}));

			const {onSubmit} = open.mock.calls[0][1];

			act(() => {
				onSubmit(
					new OrderedMap({'a-1': {id: 'a-1', name: 'My Asset'}})
				);
			});

			expect(onSelectionsChange).toHaveBeenLastCalledWith({
				applicationId: 'Blog',
				eventId: 'blogClicked',
				selections: [
					{
						activityKey: 'Blog#blogClicked#a-1',
						id: 'a-1',
						name: 'My Asset'
					}
				]
			});
		});
	});

	describe('applicationId inference (reload)', () => {
		it('should default to the asset-type selector when no applicationId is provided', async () => {
			const {getAllByRole, getByRole, getByText} = render(
				<DefaultComponent action='view' />
			);

			// Asset Type mode (the default): once loaded, the Page | Asset Type
			// picker + the asset-type picker, showing the placeholder.

			await waitFor(() =>
				expect(
					getByRole('combobox', {name: 'Asset Type'})
				).toBeEnabled()
			);

			expect(getAllByRole('combobox')).toHaveLength(2);
			expect(getByText('Select a Type')).toBeTruthy();
		});

		it('should use the page selector for a Page applicationId', () => {
			const {getAllByRole} = render(
				<DefaultComponent applicationId='Page' />
			);

			expect(getAllByRole('combobox')).toHaveLength(1);
		});

		it('should infer the asset-type selector from a non-Page applicationId', () => {
			const {getAllByRole} = render(
				<DefaultComponent applicationId='WebContent' />
			);

			// Asset Type mode: Page | Asset Type picker + the asset-type picker.

			expect(getAllByRole('combobox')).toHaveLength(2);
		});
	});

	describe('asset type filtering', () => {
		it('should query activity/asset with the applicationId resolved from the selected type', async () => {
			const {getByRole} = render(
				<DefaultComponent action='download' applicationId='Document' />
			);

			// The reloaded Document type is preselected; wait for loading, then
			// open the asset modal.

			await waitFor(() =>
				expect(
					getByRole('button', {name: /add assets/i})
				).toBeEnabled()
			);

			fireEvent.click(getByRole('button', {name: /add assets/i}));

			const {dataSourceFn} = open.mock.calls[0][1];

			await dataSourceFn({delta: 20, page: 1, query: ''});

			expect(API.activities.searchAssets).toHaveBeenCalledWith(
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentDownloaded'
				})
			);

			// A DXP type is identified by its applicationId, so no
			// objectDefinitionName is sent.

			expect(
				API.activities.searchAssets.mock.calls[0][0]
			).not.toHaveProperty('objectDefinitionName');
		});

		it('should pass the object definition name for an ObjectEntry type', async () => {
			// Reloading an ObjectEntry criterion defaults the picker to the
			// matching object-definition type (CMSBasicWebContent, from the mock).

			const {getByText} = render(
				<DefaultComponent
					action='download'
					applicationId='ObjectEntry'
				/>
			);

			await waitFor(() =>
				expect(getByText('CMSBasicWebContent')).toBeTruthy()
			);

			fireEvent.click(getByText(/add assets/i));

			const {dataSourceFn} = open.mock.calls[0][1];

			await dataSourceFn({delta: 20, page: 1, query: ''});

			// ObjectEntry is generic, so the selected object definition name is
			// sent for the backend to narrow the listing.

			expect(API.activities.searchAssets).toHaveBeenCalledWith(
				expect.objectContaining({
					applicationId: 'ObjectEntry',
					eventId: 'objectEntryDownloaded',
					objectDefinitionName: 'CMSBasicWebContent'
				})
			);
		});

		it('should build activityKeys (applicationId#eventId#assetId) on submit', async () => {
			const onSelectionsChange = jest.fn();

			const {getByRole} = render(
				<DefaultComponent
					action='download'
					applicationId='Document'
					onSelectionsChange={onSelectionsChange}
				/>
			);

			// The reloaded Document type is preselected; wait for loading, then
			// open the asset modal.

			await waitFor(() =>
				expect(
					getByRole('button', {name: /add assets/i})
				).toBeEnabled()
			);

			fireEvent.click(getByRole('button', {name: /add assets/i}));

			const {onSubmit} = open.mock.calls[0][1];

			act(() => {
				onSubmit(
					new OrderedMap({'a-1': {id: 'a-1', name: 'My Asset'}})
				);
			});

			// The default Document type -> documentDownloaded (analytics-client-js).

			expect(onSelectionsChange).toHaveBeenLastCalledWith({
				applicationId: 'Document',
				eventId: 'documentDownloaded',
				selections: [
					{
						activityKey: 'Document#documentDownloaded#a-1',
						id: 'a-1',
						name: 'My Asset'
					}
				]
			});
		});
	});

	describe('asset-summary-types request (LDP gating)', () => {
		// useRequest fires through a zero-delay debounce, so flush timers before
		// asserting whether the request went out.

		const renderAndFlush = props => {
			jest.useFakeTimers();

			render(<DefaultComponent {...props} />);

			act(() => jest.runAllTimers());

			jest.useRealTimers();
		};

		it('should not request asset-summary-types when the plan is not LDP', () => {
			useLDPEnabled.mockReturnValue(false);

			// A non-LDP plan has no object-definition types, so the request is
			// skipped and only the fixed DXP types are offered.

			renderAndFlush({action: 'download'});

			expect(API.assets.searchTypes).not.toHaveBeenCalled();
		});

		it('should request asset-summary-types on an LDP plan', () => {
			renderAndFlush({action: 'download'});

			expect(API.assets.searchTypes).toHaveBeenCalled();
		});

		it('should request asset-summary-types on an LDP plan even for an event with no ObjectEntry support', () => {
			// The request no longer depends on the event: comment maps only to
			// Blog, yet the object definitions are still fetched on LDP.

			renderAndFlush({action: 'comment'});

			expect(API.assets.searchTypes).toHaveBeenCalled();
		});

		it('should request asset-summary-types with a null rangeKey so recently tracked object definitions still list', () => {
			// A null rangeKey lifts the time-range restriction (LPD-97671):
			// CMS objects whose only activity is recent (e.g. the last 24
			// hours) must still surface as selectable asset types.

			renderAndFlush({action: 'download'});

			expect(API.assets.searchTypes).toHaveBeenCalledWith(
				expect.objectContaining({rangeKey: null})
			);
		});
	});

	describe('asset type loading', () => {
		// Drive the request with jest fake timers (like the LDP-gating tests) so
		// the flush is deterministic and unaffected by fake/real timer toggling
		// elsewhere in the file.

		beforeEach(() => {
			jest.useFakeTimers();
		});

		afterEach(() => {
			jest.useRealTimers();
		});

		it('should show a spinner inside the disabled picker while the LDP request is in flight', async () => {
			const {container, getByRole} = render(
				<DefaultComponent action='view' />
			);

			// While loading: the picker is present but disabled, showing the
			// placeholder and a spinner inside (all-or-nothing, no partial list).

			const trigger = getByRole('combobox', {name: 'Asset Type'});

			expect(trigger).toBeDisabled();
			expect(trigger).toHaveTextContent('Select a Type');
			expect(container.querySelector('.loading-root')).toBeTruthy();

			// Flush the request (debounce timer + resolved-promise microtask).

			await act(async () => {
				jest.runAllTimers();
			});

			// Once it resolves, the picker is enabled and the spinner clears.

			expect(getByRole('combobox', {name: 'Asset Type'})).toBeEnabled();
			expect(container.querySelector('.loading-root')).toBeNull();
		});

		it('should render the asset types immediately without loading on a non-LDP plan', () => {
			useLDPEnabled.mockReturnValue(false);

			const {container, getByRole} = render(
				<DefaultComponent action='view' />
			);

			// No request is made, so there is no loading — the fixed DXP types
			// are available at once.

			expect(container.querySelector('.loading-root')).toBeNull();
			expect(getByRole('combobox', {name: 'Asset Type'})).toBeTruthy();
		});
	});

	describe('modal columns', () => {
		it('should render the base page columns (name/url, views, data source)', () => {
			const {getByRole, getByText} = render(<DefaultComponent />);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));
			fireEvent.click(getByText(/add pages/i));

			const config = open.mock.calls[0][1];
			const byAccessor = Object.fromEntries(
				config.columns.map(column => [column.accessor, column])
			);

			// name + url (nameUrl), views count, and data source, mirroring the
			// base SelectEntityFromModal listing.

			expect(
				typeof byAccessor.name.cellRendererProps.renderSecondaryInfo
			).toBe('function');
			expect(byAccessor.count).toBeTruthy();
			expect(byAccessor.dataSourceName).toBeTruthy();

			expect(config.noResultsIcon).toBe('web-content');
			expect(config.orderByOptions).toHaveLength(1);
			expect(config.rowIdentifier).toBe('id');
		});

		it('should title the count column with the selected event label', () => {
			const {getByRole, getByText} = render(
				<DefaultComponent action='view' actionLabel='View' />
			);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));
			fireEvent.click(getByText(/add pages/i));

			const config = open.mock.calls[0][1];
			const countColumn = config.columns.find(
				(column) => column.accessor === 'count'
			);

			expect(countColumn.label).toBe('View');
			expect(config.orderByOptions[0].label).toBe('View');
		});

		it('should preserve the id, url, data source and total in the mapped page rows', async () => {
			API.activities.searchAssets.mockResolvedValueOnce({
				items: [
					{
						count: 42,
						dataSourceAssetPK: 'http://foo.com/home',
						dataSourceName: 'Liferay (3)',
						id: 'hash-1',
						name: 'Home'
					}
				],
				total: 7
			});

			const {getByRole, getByText} = render(<DefaultComponent />);

			openPicker(getByRole, 'Page or Asset Type');
			fireEvent.click(getByText('Page'));
			fireEvent.click(getByText(/add pages/i));

			const {dataSourceFn} = open.mock.calls[0][1];

			const {items, total} = await dataSourceFn({
				delta: 10,
				page: 1,
				query: ''
			});

			// The real id (analytics hash) is preserved so the activityKey and
			// the modal row selection stay correct.

			expect(items[0]).toEqual(
				expect.objectContaining({
					count: 42,
					dataSourceAssetPK: 'http://foo.com/home',
					dataSourceName: 'Liferay (3)',
					id: 'hash-1',
					name: 'Home'
				})
			);

			// total (not totalCount) so the "select all" checkbox is enabled.

			expect(total).toBe(7);
		});
	});
});
