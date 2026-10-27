import * as data from 'test/data';
import React from 'react';
import SearchableTableModal from '../SearchableTableModal';
import {cleanup, render} from '@testing-library/react';
import {createOrderIOMap} from 'shared/util/pagination';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {noop} from 'lodash';
import {Routes} from 'shared/util/router';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const COLUMNS = [
	{
		accessor: 'name',
		className: 'table-cell-expand',
		label: 'name'
	},
	{
		accessor: 'email',
		label: 'email'
	}
];

const defaultProps = {
	columns: COLUMNS,
	dataSourceFn: () => Promise.resolve(),
	groupId: '23',
	initialDelta: 5,
	initialOrderIOMap: createOrderIOMap('name'),
	onClose: noop
};

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <SearchableTableModal observer={observer} {...props} />;
};

const DefaultComponent = props => (
	<MemoryRouter initialEntries={['/workspace/23/settings/data-source']}>
		<RouterRoutes>
			<Route
				element={
					<TestModal {...defaultProps} {...props} />
				}
				path={`${Routes.SETTINGS_DATA_SOURCE_LIST}/*`}
			/>
		</RouterRoutes>
	</MemoryRouter>
);

describe('SearchableTableModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		render(<DefaultComponent />);

		await waitForModal();

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(document.body);

		expect(document.body).toMatchSnapshot();
	});

	it('should render with a custom title', async () => {
		render(<DefaultComponent title='Custom Title' />);

		await waitForModal();

		jest.runAllTimers();

		expect(document.querySelector('.modal-title')).toHaveTextContent(
			'Custom Title'
		);
	});

	it('should render with a custom submit button message', async () => {
		render(
			<DefaultComponent submitMessage='Custom Submit Message' />
		);

		await waitForModal();

		jest.runAllTimers();

		expect(document.querySelector('.btn-primary')).toHaveTextContent(
			'Custom Submit Message'
		);
	});

	it('should render with preselected items', async () => {
		render(
			<DefaultComponent
				dataSourceFn={() =>
					Promise.resolve(
						data.mockSearch(data.mockSegment, 1, {id: 'foo'})
					)
				}
				initialSelectedItems={[{id: 'foo', name: 'fooSegmentName'}]}
				submitMessage='Custom Submit Message'
			/>
		);

		await waitForModal();

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(document.body);

		expect(
			document.querySelector(
				'.table > tbody:nth-of-type(1) > tr .custom-checkbox input:checked'
			).checked
		).toBeTrue();
	});
});
