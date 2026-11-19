jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {
	Page,
	PageAccounts,
	PageKnownIndividuals,
	PagePath,
	SiteInterest,
} from '../SitesApp';

jest.unmock('react-dom');

describe('SitesApp', () => {
	it.each([
		[
			{title: 'Home', touchpoint: 'https://liferay.com'},
			'sites/pages/overview/https%3A%2F%2Fliferay.com/Home',
		],
		[
			{title: '', touchpoint: 'https://liferay.com'},
			'sites/pages/overview/https%3A%2F%2Fliferay.com',
		],
	])('renders the route of Page with %j', (params, path) => {
		render(<Page channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of PageAccounts', () => {
		render(
			<PageAccounts
				channelId="321"
				groupId="23"
				params={{title: 'Home', touchpoint: 'https://liferay.com'}}
			/>
		);

		expect(
			screen.getByText(
				'sites/pages/accounts/https%3A%2F%2Fliferay.com/Home'
			)
		).toBeInTheDocument();
	});

	it('renders the route of PageKnownIndividuals', () => {
		render(
			<PageKnownIndividuals
				channelId="321"
				groupId="23"
				params={{title: 'Home', touchpoint: 'https://liferay.com'}}
			/>
		);

		expect(
			screen.getByText(
				'sites/pages/known-individuals/https%3A%2F%2Fliferay.com/Home'
			)
		).toBeInTheDocument();
	});

	it('renders the route of PagePath', () => {
		render(
			<PagePath
				channelId="321"
				groupId="23"
				params={{title: 'Home', touchpoint: 'https://liferay.com'}}
			/>
		);

		expect(
			screen.getByText('sites/pages/path/https%3A%2F%2Fliferay.com/Home')
		).toBeInTheDocument();
	});

	it('renders the route of SiteInterest', () => {
		render(
			<SiteInterest
				channelId="321"
				groupId="23"
				params={{interestId: 'Liferay DXP'}}
			/>
		);

		expect(
			screen.getByText('sites/interests/Liferay%20DXP')
		).toBeInTheDocument();
	});
});
