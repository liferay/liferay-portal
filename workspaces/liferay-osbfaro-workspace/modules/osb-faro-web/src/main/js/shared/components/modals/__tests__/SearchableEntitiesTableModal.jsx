import React from 'react';
import SearchableEntitiesTableModal from '../SearchableEntitiesTableModal';
import {cleanup, render} from '@testing-library/react';
import {createOrderIOMap, EMAIL_ADDRESS} from 'shared/util/pagination';
import {noop} from 'lodash';
import {OrderByDirections} from 'shared/util/constants';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const ITEMS = [
	{emailAddress: 'foo@liferay.com', name: 'Foo'},
	{emailAddress: 'bar@liferay.com', name: 'Bar'}
];

const DefaultComponent = props => (
	<MemoryRouter>
		<SearchableEntitiesTableModal
			columns={[
				{
					accessor: 'name',
					className: 'table-cell-expand',
					label: 'name'
				},
				{
					accessor: 'emailAddress',
					label: 'email'
				}
			]}
			dataSourceFn={() =>
				Promise.resolve({items: ITEMS, total: ITEMS.length})
			}
			groupId='23'
			onClose={noop}
			rowIdentifier='emailAddress'
			{...props}
		/>
	</MemoryRouter>
);

describe('SearchableEntitiesTableModal', () => {
	afterEach(cleanup);

	it('should render loading state without ghost table', () => {
		const {container, getByText} = render(
			<DefaultComponent dataSourceFn={() => new Promise(() => {})} />
		);

		expect(getByText('entities')).toBeTruthy();
		expect(container.querySelector('.loading-root')).toBeTruthy();
		expect(container.querySelector('table')).toBeFalsy();
	});

	it('should render w/ defaultParams', async () => {
		const {container} = render(
			<DefaultComponent
				initialOrderIOMap={createOrderIOMap(
					EMAIL_ADDRESS,
					OrderByDirections.Descending
				)}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		const emailHeaderButton = container.querySelectorAll(
			'.table-head-title > button'
		)[1];

		expect(emailHeaderButton).toHaveTextContent('email');
		expect(
			emailHeaderButton.querySelector(
				'.lexicon-icon-order_arrow_descending'
			)
		).toBeTruthy();
	});
});
