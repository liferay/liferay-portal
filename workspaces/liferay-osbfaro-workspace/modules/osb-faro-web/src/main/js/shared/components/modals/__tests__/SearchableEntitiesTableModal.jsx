import React from 'react';
import SearchableEntitiesTableModal from '../SearchableEntitiesTableModal';
import {cleanup, render} from '@testing-library/react';
import {createOrderIOMap, EMAIL_ADDRESS} from 'shared/util/pagination';
import {noop} from 'lodash';
import {OrderByDirections} from 'shared/util/constants';
import {MemoryRouter} from 'react-router';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const ITEMS = [
	{emailAddress: 'foo@liferay.com', name: 'Foo'},
	{emailAddress: 'bar@liferay.com', name: 'Bar'}
];

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <SearchableEntitiesTableModal observer={observer} {...props} />;
};

const DefaultComponent = props => (
	<MemoryRouter>
		<TestModal
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

	it('should render loading state without ghost table', async () => {
		const {getByText} = render(
			<DefaultComponent dataSourceFn={() => new Promise(() => {})} />
		);

		await waitForModal();

		expect(getByText('entities')).toBeTruthy();
		expect(document.querySelector('.loading-root')).toBeTruthy();
		expect(document.querySelector('table')).toBeFalsy();
	});

	it('should render w/ defaultParams', async () => {
		render(
			<DefaultComponent
				initialOrderIOMap={createOrderIOMap(
					EMAIL_ADDRESS,
					OrderByDirections.Descending
				)}
			/>
		);

		await waitForModal();

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(document.body);

		const emailHeaderButton = document.querySelectorAll(
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
