import * as API from 'shared/api';
import * as data from 'test/data';
import BaseFieldMappingView from '../BaseFieldMappingView';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource, User} from 'shared/util/records';
import {FieldContexts} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<StaticRouter>
			<BaseFieldMappingView
				context={FieldContexts.Demographics}
				currentUser={data.getImmutableMock(User, data.mockUser)}
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockSalesforceDataSource
				)}
				groupId='23'
				id='123'
				{...props}
			/>
		</StaticRouter>
	</Provider>
);

describe('BaseFieldMappingView', () => {
	it('should render', async () => {
		const {container} = render(<WrappedComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render w/o loading', async () => {
		const {container} = render(<WrappedComponent />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.loading-root')).toBeNull();
	});

	it('should render w/ error display', async () => {
		API.dataSource.fetchMappingsLite.mockReturnValueOnce(
			Promise.reject({})
		);

		const {container, getByText} = render(
			<WrappedComponent title='This is a title' />
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('An unexpected error occurred.')).toBeTruthy();
	});

	it('should render w/ details', () => {
		const details = 'This is the details';

		const {getByText} = render(<WrappedComponent details={details} />);

		jest.runAllTimers();

		expect(getByText(details)).toBeTruthy();
	});

	it('should render w/ title', () => {
		const title = 'This is a title';

		const {container, getByText} = render(
			<WrappedComponent title={title} />
		);

		jest.runAllTimers();

		expect(container.querySelector('.h4')).toBeTruthy();
		expect(getByText(title)).toBeTruthy();
	});
});
