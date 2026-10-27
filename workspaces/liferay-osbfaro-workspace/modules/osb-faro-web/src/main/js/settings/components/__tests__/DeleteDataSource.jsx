import * as data from 'test/data';
import * as pedantic from 'test/pedantic';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
	within
} from '@testing-library/react';
import {DataSource} from 'shared/util/records';
import {DeleteDataSource} from '../DeleteDataSource';
import {EntityTypes} from 'shared/util/constants';
import {fromJS} from 'immutable';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

let mockEntitiesModalProps;

jest.mock('shared/components/modals/SearchableEntitiesTableModal', () => ({
	__esModule: true,
	default: props => {
		mockEntitiesModalProps = props;

		return <div data-testid='searchable-entities-table-modal' />;
	}
}));

describe('DeleteDataSource', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	const dataSource = data.getImmutableMock(
		DataSource,
		data.mockLiferayDataSource
	);

	const entitiesCount = {
		[EntityTypes.Account]: 123,
		[EntityTypes.Asset]: 23,
		[EntityTypes.Individual]: 1,
		[EntityTypes.IndividualsSegment]: 12,
		[EntityTypes.Page]: 1234
	};

	const groupId = '23';

	const store = mockStore(
		fromJS({
			projects: {
				[groupId]: {
					data: {
						timeZone: {
							timeZoneId: 'UTC'
						}
					}
				}
			}
		})
	);

	const DefaultComponent = props => (
		<Provider store={store}>
			<MemoryRouter>
				<DeleteDataSource
					actionRequestFn={jest.fn()}
					dataSource={dataSource}
					deleteMessage='Test delete message'
					deletePhrase={Liferay.Language.get('remove-x')}
					entitiesCount={entitiesCount}
					groupId={groupId}
					id='123'
					pageActionConfirmationText='Confirm?'
					pageActionText={Liferay.Language.get('delete-data-source')}
					{...props}
				/>
			</MemoryRouter>
		</Provider>
	);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should successfully validate if the user typed in the full string to delete the datasource', async () => {
		render(<DefaultComponent />);

		const deleteButton = screen
			.getByText('Delete Data Source')
			.closest('button');
		const confirmationInput = screen.getByTestId('confirmation-input');

		fireEvent.change(confirmationInput, {
			target: {value: `remove ${dataSource.name}`}
		});

		await waitFor(() => expect(deleteButton).not.toBeDisabled());
	});

	it('should not enable delete button if validation is unsuccessful', async () => {
		render(<DefaultComponent />);

		const deleteButton = screen
			.getByText('Delete Data Source')
			.closest('button');
		const confirmationInput = screen.getByTestId('confirmation-input');

		fireEvent.change(confirmationInput, {
			target: {value: 'wrong string'}
		});

		fireEvent.blur(confirmationInput);

		// Wait for Formik validation to settle
		await waitFor(() => expect(deleteButton).toBeDisabled());
	});

	it('should ask for a confirmation before deleting the data source', async () => {
		const actionRequestFn = jest.fn();

		render(<DefaultComponent actionRequestFn={actionRequestFn} />);

		fireEvent.change(screen.getByTestId('confirmation-input'), {
			target: {value: `remove ${dataSource.name}`}
		});

		const deleteButton = screen
			.getByText('Delete Data Source')
			.closest('button');

		await waitFor(() => expect(deleteButton).not.toBeDisabled());

		expect(screen.queryByRole('dialog')).toBeNull();

		fireEvent.click(deleteButton);

		const dialog = await screen.findByRole('dialog');

		expect(await within(dialog).findByText('Confirm?')).toBeInTheDocument();

		expect(actionRequestFn).not.toHaveBeenCalled();

		fireEvent.click(
			within(dialog).getByText('Delete Data Source', {selector: 'button'})
		);

		expect(actionRequestFn).toHaveBeenCalledTimes(1);

		await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
	});

	it('should open the data source entities modal', async () => {
		render(<DefaultComponent />);

		expect(
			screen.queryByTestId('searchable-entities-table-modal')
		).toBeNull();

		fireEvent.click(screen.getByText('12 Segments'));

		expect(
			screen.getByTestId('searchable-entities-table-modal')
		).toBeInTheDocument();

		expect(mockEntitiesModalProps).toEqual(
			expect.objectContaining({
				dataSourceParams: {dataSourceId: '123', groupId: '23'},
				rowIdentifier: 'id'
			})
		);
	});
});
