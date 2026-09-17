import React from 'react';
import withAction from '../WithAction';
import {compose} from 'redux';
import {RemoteData} from '../../util/records';
import {renderWithStore} from 'test/mock-store';
import {withStaticRouter} from 'test/mock-router';

jest.unmock('react-dom');

describe('withAction', () => {
	const action = () => ({type: 'NO_OP'});
	const mapStateToRemoteData = () => new RemoteData({loading: false});

	it('should return a new Component', () => {
		const WrappedComponent = withAction(
			action,
			mapStateToRemoteData
		)(jest.fn());

		expect(WrappedComponent).toBeInstanceOf(Object);
	});

	it('should render the wrapped component', () => {
		const WrappedComponent = withAction(
			action,
			mapStateToRemoteData
		)(() => <div>{'foo'}</div>);

		const {container} = renderWithStore(WrappedComponent);

		expect(container).toMatchSnapshot();
	});

	it('should render loading if the RemoteData is loading and data is null', () => {
		const WrappedComponent = withAction(
			action,
			() => new RemoteData()
		)(jest.fn());

		const {container} = renderWithStore(WrappedComponent);

		expect(container).toMatchSnapshot();
	});

	it('should render error if the RemoteData has error', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withAction(action, () => new RemoteData({error: true}))
		)(jest.fn());

		const {container} = renderWithStore(WrappedComponent);

		expect(container).toMatchSnapshot();
	});

	it('should render a permission error instead of the 404 when the request failed with a 403', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withAction(
				action,
				() => new RemoteData({error: true, errorStatus: 403})
			)
		)(jest.fn());

		const {container} = renderWithStore(WrappedComponent);

		expect(container.textContent).toContain(
			'You do not have permission to view this resource.'
		);
		expect(container.textContent).not.toContain(
			'The page you are looking for does not exist.'
		);
	});

	it('should still render the 404 when the failure carries no status', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withAction(action, () => new RemoteData({error: true}))
		)(jest.fn());

		const {container} = renderWithStore(WrappedComponent);

		expect(container.textContent).toContain(
			'The page you are looking for does not exist.'
		);
	});

	it('should render a custom error message', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withAction(action, () => new RemoteData({error: true}), {
				errorPageProps: {
					message: 'my fancy message, oh so fancy'
				}
			})
		)(jest.fn());

		const {container} = renderWithStore(WrappedComponent);

		expect(container).toMatchSnapshot();
	});

	it('should render the wrapped component if bypassErrorPage is true even if the RemoteData has an error', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withAction(
				action,
				() => new RemoteData({data: {test: 'test'}, error: true}),
				{bypassErrorPage: true}
			)
		)(() => <div>{'foo'}</div>);

		const {container} = renderWithStore(WrappedComponent);

		expect(container).toMatchSnapshot();
	});
});
