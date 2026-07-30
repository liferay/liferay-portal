import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {Formik} from 'formik';
import {modalTypes} from 'shared/actions/modals';
import {SegmentCategories} from 'shared/util/constants';
import {StaticRouter} from 'react-router';
import {Toolbar} from '../Toolbar';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('Toolbar', () => {
	afterEach(() => {
		jest.clearAllMocks();

		cleanup();
	});

	it('should render', () => {
		const {container} = render(
			<StaticRouter>
				<Formik>
					<Toolbar
						channelId='321'
						criteria={data.mockNewCriteria(1, {valid: false})}
						groupId='123'
						segmentType='BATCH'
					/>
				</Formik>
			</StaticRouter>
		);
		expect(container).toMatchSnapshot();
	});

	it('should open the accounts modal for account segments', async () => {
		API.accounts.searchByFilter.mockReturnValue(
			Promise.resolve({items: [], totalCount: 1})
		);

		const open = jest.fn();

		const {container, getByTestId} = render(
			<StaticRouter>
				<Formik>
					<Toolbar
						channelId='321'
						criteria={data.mockNewCriteria(1, {valid: true})}
						groupId='123'
						open={open}
						segmentCategory={SegmentCategories.Account}
						segmentType='BATCH'
					/>
				</Formik>
			</StaticRouter>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(getByTestId('preview-criteria-button'));

		expect(open).toHaveBeenCalledWith(
			modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL,
			expect.objectContaining({
				entityLabel: 'Accounts',
				title: 'Segment Accounts'
			})
		);
	});

	it('should render w/ preview button disabled if criteria is valid and total members count is equal to 0', () => {
		const {getByTestId} = render(
			<StaticRouter>
				<Formik>
					<Toolbar
						channelId='321'
						criteria={data.mockNewCriteria(1, {valid: true})}
						groupId='123'
						segmentType='BATCH'
					/>
				</Formik>
			</StaticRouter>
		);

		expect(getByTestId('preview-criteria-button')).toBeDisabled();
	});

	it('should render w/ preview button disabled if criteria is not valid', () => {
		const {getByTestId} = render(
			<StaticRouter>
				<Formik>
					<Toolbar
						channelId='321'
						criteria={data.mockNewCriteria(1, {valid: false})}
						groupId='123'
						segmentType='BATCH'
					/>
				</Formik>
			</StaticRouter>
		);

		expect(getByTestId('preview-criteria-button')).toBeDisabled();
	});

	it('should render w/ preview button enabled if total members count is bigger thant 0', async () => {
		API.individuals.search.mockReturnValue(Promise.resolve({total: 1}));

		const {container, getByTestId} = render(
			<StaticRouter>
				<Formik>
					<Toolbar
						channelId='321'
						criteria={data.mockNewCriteria(1, {valid: true})}
						groupId='123'
						segmentType='BATCH'
					/>
				</Formik>
			</StaticRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByTestId('preview-criteria-button')).toBeEnabled();
	});
});
