import AssetsListCard from '../AssetsListCard';
import BasePage from 'shared/components/base-page';
import client from 'shared/apollo/client';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {BrowserRouter} from 'react-router-dom';
import {render} from '@testing-library/react';

const items = [
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	},
	{
		assetId: '01234',
		assetType: 'journal',
		interactions: 1,
		title: '231494203',
		type: 'Web Content'
	}
];

describe('AssetsListCard', () => {
	const MOCK_CONTEXT = {
		router: {
			params: {
				channelId: 123,
				groupId: '2000',
				touchpoint: 'www.liferay.com'
			},
			query: {
				rangeKey: '30'
			}
		}
	};

	const WrappedComponent = props => (
		<ApolloProvider client={client}>
			<BasePage.Context.Provider value={MOCK_CONTEXT}>
				<BrowserRouter>
					<AssetsListCard
						{...props}
						rangeSelectors={{rangeKey: '30'}}
					/>
				</BrowserRouter>
			</BasePage.Context.Provider>
		</ApolloProvider>
	);

	it('should render component', () => {
		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render component with items', () => {
		const {container} = render(<WrappedComponent items={items} />);

		expect(container).toMatchSnapshot();
	});
});
