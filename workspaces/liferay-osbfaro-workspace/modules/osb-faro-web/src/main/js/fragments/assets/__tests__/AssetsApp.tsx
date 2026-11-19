jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {Asset, AssetAccounts, AssetKnownIndividuals} from '../AssetsApp';

jest.unmock('react-dom');

describe('AssetsApp', () => {
	it.each([
		[
			{
				assetId: '1',
				assetType: 'blog',
				title: 'My Blog',
				touchpoint: 'https://liferay.com',
				type: '',
			},
			'assets/blog/1/page/https%3A%2F%2Fliferay.com/My%20Blog',
		],
		[
			{
				assetId: '1',
				assetType: 'blog',
				title: '',
				touchpoint: 'https://liferay.com',
				type: '',
			},
			'assets/blog/1/page/https%3A%2F%2Fliferay.com',
		],
		[
			{
				assetId: '1',
				assetType: 'blog',
				title: 'My Blog',
				touchpoint: 'https://liferay.com',
				type: 'blog-posting',
			},
			'assets/blog/1/page/https%3A%2F%2Fliferay.com/My%20Blog/blog-posting',
		],
	])('renders the route of Asset with %j', (params, path) => {
		render(<Asset channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of AssetAccounts', () => {
		render(
			<AssetAccounts
				channelId="321"
				groupId="23"
				params={{
					assetId: '1',
					assetType: 'blog',
					title: 'My Blog',
					touchpoint: 'https://liferay.com',
					type: '',
				}}
			/>
		);

		expect(
			screen.getByText(
				'assets/blog/1/accounts/https%3A%2F%2Fliferay.com/My%20Blog'
			)
		).toBeInTheDocument();
	});

	it('renders the route of AssetKnownIndividuals', () => {
		render(
			<AssetKnownIndividuals
				channelId="321"
				groupId="23"
				params={{
					assetId: '1',
					assetType: 'blog',
					title: 'My Blog',
					touchpoint: 'https://liferay.com',
					type: '',
				}}
			/>
		);

		expect(
			screen.getByText(
				'assets/blog/1/known-individuals/https%3A%2F%2Fliferay.com/My%20Blog'
			)
		).toBeInTheDocument();
	});
});
