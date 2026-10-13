import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SegmentMembership from '../SegmentMembership';
import {cleanup, fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Formik} from 'formik';
import {modalTypes} from 'shared/actions/modals';
import {Provider} from 'react-redux';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';

jest.unmock('react-dom');

const renderMembership = (props = {}, store = mockStore()) => {
	let formik;

	const result = render(
		<Provider store={store}>
			<Formik
				initialValues={{includeAnonymousUsers: false}}
				onSubmit={jest.fn()}
			>
				{(formikProps) => {
					formik = formikProps;

					return (
						<SegmentMembership
							channelId='321'
							criteria={data.mockNewCriteria(1, {valid: false})}
							criteriaString=''
							groupId='123'
							includeAnonymousUsers={false}
							segmentCategory={SegmentCategories.Individual}
							segmentType={SegmentTypes.Batch}
							{...props}
						/>
					);
				}}
			</Formik>
		</Provider>
	);

	return {...result, getFormik: () => formik};
};

describe('SegmentMembership', () => {
	afterEach(() => {
		jest.clearAllMocks();

		cleanup();
	});

	it('should render the anonymous toggle, the total members and an icon only preview button for batch segments', () => {
		renderMembership();

		expect(
			screen.getByRole('switch', {name: /include anonymous/i})
		).toBeInTheDocument();
		expect(screen.getByText(/total members/i)).toBeInTheDocument();

		const previewButton = screen.getByTestId('preview-criteria-button');

		expect(previewButton).toHaveAccessibleName('View Members');
		expect(previewButton).toHaveAttribute('aria-label', 'View Members');
		expect(previewButton).toHaveClass('btn-monospaced');
		expect(previewButton).not.toHaveTextContent('View Members');
		expect(previewButton.firstElementChild).toHaveClass('lexicon-icon-view');
	});

	it('should update the include anonymous field when it is toggled', async () => {
		const {getFormik} = renderMembership();

		fireEvent.click(screen.getByRole('switch', {name: /include anonymous/i}));

		await waitFor(() =>
			expect(getFormik().values.includeAnonymousUsers).toBe(true)
		);
	});

	it('should show the include anonymous label in gray only when the toggle is off', async () => {
		renderMembership();

		const toggle = screen.getByRole('switch', {name: /include anonymous/i});
		const container = toggle.closest('label');

		expect(container).toHaveClass('text-secondary');

		fireEvent.click(toggle);

		await waitFor(() => expect(container).not.toHaveClass('text-secondary'));
	});

	it('should mark the include anonymous field as touched when it loses focus', async () => {
		const {getFormik} = renderMembership();

		fireEvent.blur(screen.getByRole('switch', {name: /include anonymous/i}));

		await waitFor(() =>
			expect(getFormik().touched.includeAnonymousUsers).toBe(true)
		);
	});

	it('should not use the field name as the include anonymous toggle id', () => {
		renderMembership();

		expect(
			screen.getByRole('switch', {name: /include anonymous/i})
		).not.toHaveAttribute('id', 'includeAnonymousUsers');
	});

	it('should explain the anonymous exclusion through a keyboard reachable info popover', () => {
		renderMembership();

		const infoPopover = screen.getByRole('button', {
			name: 'More Information'
		});

		expect(infoPopover).toHaveAttribute('tabIndex', '0');

		fireEvent.focus(infoPopover);

		expect(
			screen.getByText(
				'Criteria containing individual or account attributes excludes anonymous individuals.'
			)
		).toBeInTheDocument();
	});

	it('should announce the total members to screen readers', () => {
		renderMembership();

		expect(screen.getByText(/total members/i).closest('[aria-live]')).toHaveAttribute(
			'aria-live',
			'polite'
		);
	});

	it('should render a preview button w/ the eye icon followed by the view members label for real-time segments', () => {
		renderMembership({segmentType: SegmentTypes.RealTime});

		expect(screen.queryByRole('switch')).not.toBeInTheDocument();
		expect(screen.queryByText(/total members/i)).not.toBeInTheDocument();

		const previewButton = screen.getByTestId('preview-criteria-button');

		expect(previewButton).toHaveAccessibleName('View Members');
		expect(previewButton).not.toHaveAttribute('aria-label');
		expect(previewButton).not.toHaveClass('btn-monospaced');
		expect(previewButton).toHaveTextContent('View Members');
		expect(previewButton.firstElementChild).toHaveClass(
			'lexicon-icon-view',
			'mr-2'
		);
	});

	it('should label the preview button with view accounts for real-time account segments', () => {
		renderMembership({
			segmentCategory: SegmentCategories.Account,
			segmentType: SegmentTypes.RealTime
		});

		expect(screen.getByTestId('preview-criteria-button')).toHaveTextContent(
			'View Accounts'
		);
	});

	it.each([SegmentTypes.Batch, SegmentTypes.RealTime])(
		'should tell only screen readers about invalid criteria on the preview button for %s segments',
		(segmentType) => {
			renderMembership({segmentType});

			const previewButton = screen.getByTestId('preview-criteria-button');

			expect(previewButton).toBeDisabled();
			expect(previewButton).toHaveAttribute('title', 'View Members');
			expect(previewButton).toHaveAccessibleDescription(
				'Some of your criteria are incomplete or invalid.'
			);
			expect(
				screen.getByText(
					'Some of your criteria are incomplete or invalid.'
				)
			).toHaveClass('sr-only');
		}
	);

	it.each([SegmentTypes.Batch, SegmentTypes.RealTime])(
		'should show the action name as the preview button tooltip for valid %s segments',
		async (segmentType) => {
			API.individuals.search.mockReturnValue(Promise.resolve({total: 1}));

			renderMembership({
				criteria: data.mockNewCriteria(1, {valid: true}),
				segmentType
			});

			const previewButton = screen.getByTestId('preview-criteria-button');

			await waitFor(() => expect(previewButton).toBeEnabled());

			expect(previewButton).toHaveAttribute('title', 'View Members');
			expect(previewButton).not.toHaveAccessibleDescription(
				'Some of your criteria are incomplete or invalid.'
			);
			expect(
				screen.queryByText(
					'Some of your criteria are incomplete or invalid.'
				)
			).not.toBeInTheDocument();
		}
	);

	it('should open the accounts modal for account segments', async () => {
		API.accounts.searchByFilter.mockReturnValue(
			Promise.resolve({items: [], totalCount: 1})
		);

		const store = mockStore();

		const dispatchSpy = jest.spyOn(store, 'dispatch');

		renderMembership(
			{
				criteria: data.mockNewCriteria(1, {valid: true}),
				segmentCategory: SegmentCategories.Account
			},
			store
		);

		const previewButton = screen.getByTestId('preview-criteria-button');

		await waitFor(() => expect(previewButton).toBeEnabled());

		fireEvent.click(previewButton);

		expect(dispatchSpy).toHaveBeenCalledWith(
			expect.objectContaining({
				payload: expect.objectContaining({
					props: expect.objectContaining({
						entityLabel: 'Accounts',
						title: 'Segment Accounts'
					}),
					type: modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL
				})
			})
		);
	});

	it('should open the segment membership modal w/ anonymous individuals and their accounts for individual segments', async () => {
		API.individuals.search.mockReturnValue(
			Promise.resolve({items: [], total: 1})
		);

		const store = mockStore();

		const dispatchSpy = jest.spyOn(store, 'dispatch');

		renderMembership(
			{
				criteria: data.mockNewCriteria(1, {valid: true}),
				criteriaString: 'filter',
				includeAnonymousUsers: true
			},
			store
		);

		const previewButton = screen.getByTestId('preview-criteria-button');

		await waitFor(() => expect(previewButton).toBeEnabled());

		fireEvent.click(previewButton);

		expect(dispatchSpy).toHaveBeenCalledWith(
			expect.objectContaining({
				payload: expect.objectContaining({
					props: expect.objectContaining({
						columns: [
							expect.objectContaining({
								accessor: 'name',
								className: 'w-50'
							}),
							expect.objectContaining({
								accessor: 'accountName',
								className: 'w-50',
								label: 'Account Name'
							})
						],
						entityLabel: 'Individuals',
						title: 'Segment Membership'
					}),
					type: modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL
				})
			})
		);

		const {dataSourceFn} = dispatchSpy.mock.calls.find(
			([action]) =>
				action.payload?.type === modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL
		)[0].payload.props;

		API.individuals.search.mockClear();

		dataSourceFn({delta: 10, page: 1});

		expect(API.individuals.search).toHaveBeenCalledWith(
			expect.objectContaining({
				channelId: '321',
				delta: 10,
				filter: 'filter',
				groupId: '123',
				includeAnonymousUsers: true,
				page: 1
			})
		);
	});

	it('should render w/ preview button disabled if criteria is valid and total members count is equal to 0', async () => {
		API.individuals.search.mockReturnValue(Promise.resolve({total: 0}));

		renderMembership({criteria: data.mockNewCriteria(1, {valid: true})});

		await waitFor(() => expect(API.individuals.search).toHaveBeenCalled());

		expect(screen.getByTestId('preview-criteria-button')).toBeDisabled();
	});

	it('should render w/ preview button enabled if total members count is bigger than 0', async () => {
		API.individuals.search.mockReturnValue(Promise.resolve({total: 1}));

		renderMembership({criteria: data.mockNewCriteria(1, {valid: true})});

		await waitFor(() =>
			expect(screen.getByTestId('preview-criteria-button')).toBeEnabled()
		);
	});
});
