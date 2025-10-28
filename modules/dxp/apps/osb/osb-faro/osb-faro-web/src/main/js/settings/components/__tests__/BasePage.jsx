import BasePage from '../BasePage';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const mockBreadcrumbItems = [
	{
		active: true,
		href: 'test123',
		label: 'testLabelBreadcrumbItems'
	}
];

const mockPageActions = [
	{
		actions: [{label: 'Test Action'}],
		label: 'testLabelPageActions '
	}
];

describe('BasePage', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage />
				</StaticRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with a description', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage pageDescription='testPageDescription' />
				</StaticRouter>
			</Provider>
		);

		expect(getByText('testPageDescription')).toBeTruthy();
	});

	it('should render with a breadcrumb', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage breadcrumbItems={mockBreadcrumbItems} />
				</StaticRouter>
			</Provider>
		);

		expect(getByText('testLabelBreadcrumbItems')).toBeTruthy();
	});

	it('should render with a page action', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage groupId='23' pageActions={mockPageActions} />
				</StaticRouter>
			</Provider>
		);

		expect(getByText('testLabelPageActions')).toBeTruthy();
	});

	it('should render with a title', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage groupId='23' pageTitle='testPageTitle' />
				</StaticRouter>
			</Provider>
		);

		expect(getByText('testPageTitle')).toBeTruthy();
	});

	it('should render with an inline title with subtitle', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage
						groupId='23'
						pageTitle='testPageTitle'
						subTitle='mysubtitle'
					/>
				</StaticRouter>
			</Provider>
		);

		expect(getByText('testPageTitle').parentElement).toHaveClass(
			'title-text'
		);
		expect(getByText('mysubtitle').parentElement).toBeTruthy();
	});
});
