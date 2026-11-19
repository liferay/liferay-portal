jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {
	ConfigureCSV,
	DataPrivacy,
	DataSource,
	DataSourceOnboarding,
	DeleteDataSource,
	EditDataSource,
	EditRecommendation,
	EventAttribute,
	EventAttributes,
	EventDefinition,
	EventDefinitions,
	Property,
	Recommendation,
} from '../SettingsApp';

jest.unmock('react-dom');

describe('SettingsApp', () => {
	it('renders the route of ConfigureCSV', () => {
		render(
			<ConfigureCSV channelId="321" groupId="23" params={{id: '1'}} />
		);

		expect(
			screen.getByText('settings/data-source/1/edit')
		).toBeInTheDocument();
	});

	it.each([
		[{}, 'settings/data-privacy'],
		[{view: 'requests'}, 'settings/data-privacy/requests'],
	])('renders the route of DataPrivacy with %j', (params, path) => {
		render(<DataPrivacy channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of DataSource', () => {
		render(<DataSource channelId="321" groupId="23" params={{id: '1'}} />);

		expect(screen.getByText('settings/data-source/1')).toBeInTheDocument();
	});

	it('renders the route of DataSourceOnboarding', () => {
		render(
			<DataSourceOnboarding
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText('settings/data-source/1/onboarding')
		).toBeInTheDocument();
	});

	it('renders the route of DeleteDataSource', () => {
		render(
			<DeleteDataSource channelId="321" groupId="23" params={{id: '1'}} />
		);

		expect(
			screen.getByText('settings/data-source/1/delete')
		).toBeInTheDocument();
	});

	it('renders the route of EditDataSource', () => {
		render(
			<EditDataSource channelId="321" groupId="23" params={{id: '1'}} />
		);

		expect(
			screen.getByText('settings/data-source/1/edit')
		).toBeInTheDocument();
	});

	it.each([
		[{}, 'settings/recommendations/create-item-similarity-model'],
		[{jobId: '1'}, 'settings/recommendations/1/edit'],
	])('renders the route of EditRecommendation with %j', (params, path) => {
		render(
			<EditRecommendation channelId="321" groupId="23" params={params} />
		);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of EventAttribute', () => {
		render(
			<EventAttribute
				channelId="321"
				groupId="23"
				params={{attributeId: '1'}}
			/>
		);

		expect(
			screen.getByText('settings/definitions/event-attributes/1')
		).toBeInTheDocument();
	});

	it.each([
		[{}, 'settings/definitions/event-attributes/global'],
		[{tab: 'local'}, 'settings/definitions/event-attributes/local'],
	])('renders the route of EventAttributes with %j', (params, path) => {
		render(
			<EventAttributes channelId="321" groupId="23" params={params} />
		);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of EventDefinition', () => {
		render(
			<EventDefinition
				channelId="321"
				groupId="23"
				params={{eventId: '1'}}
			/>
		);

		expect(
			screen.getByText('settings/definitions/events/1')
		).toBeInTheDocument();
	});

	it.each([
		[{}, 'settings/definitions/events/default'],
		[{tab: 'custom'}, 'settings/definitions/events/custom'],
	])('renders the route of EventDefinitions with %j', (params, path) => {
		render(
			<EventDefinitions channelId="321" groupId="23" params={params} />
		);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of Property', () => {
		render(<Property channelId="321" groupId="23" params={{id: '1'}} />);

		expect(screen.getByText('settings/properties/1')).toBeInTheDocument();
	});

	it('renders the route of Recommendation', () => {
		render(
			<Recommendation
				channelId="321"
				groupId="23"
				params={{jobId: '1'}}
			/>
		);

		expect(
			screen.getByText('settings/recommendations/1')
		).toBeInTheDocument();
	});
});
