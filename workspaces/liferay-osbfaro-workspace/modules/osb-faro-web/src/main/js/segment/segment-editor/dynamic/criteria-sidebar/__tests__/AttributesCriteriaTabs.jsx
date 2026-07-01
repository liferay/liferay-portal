import AttributesCriteriaTabs from '../AttributesCriteriaTabs';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';

jest.unmock('react-dom');

const mockLiferayLanguage = key => {
	const messages = {
		custom: 'Custom',
		default: 'Default'
	};

	return messages[key] || key;
};

global.Liferay = {
	Language: {
		get: mockLiferayLanguage
	}
};

const makeProperty = (label, name) =>
	new Property({
		label,
		name,
		propertyKey: 'individual',
		type: PropertyTypes.Text
	});

const defaultProperties = new List([
	makeProperty('First Name', 'firstName'),
	makeProperty('Last Name', 'lastName')
]);

const customProperties = new List([
	makeProperty('Loyalty Tier', 'loyaltyTier'),
	makeProperty('Favorite Store', 'favoriteStore')
]);

const renderTabs = (props = {}) =>
	render(
		<DndProvider backend={HTML5Backend}>
			<AttributesCriteriaTabs
				customProperties={customProperties}
				defaultProperties={defaultProperties}
				searchValue=''
				{...props}
			/>
		</DndProvider>
	);

describe('AttributesCriteriaTabs', () => {
	afterEach(cleanup);

	it('renders both the Default and Custom tabs', () => {
		renderTabs();

		expect(screen.getByText('Default')).toBeInTheDocument();
		expect(screen.getByText('Custom')).toBeInTheDocument();
	});

	it('opens on the Default tab and lists the default properties', () => {
		renderTabs();

		expect(screen.getByText('First Name')).toBeInTheDocument();
		expect(screen.getByText('Last Name')).toBeInTheDocument();

		expect(screen.queryByText('Loyalty Tier')).not.toBeInTheDocument();
	});

	it('lists the custom DXP properties when the Custom tab is selected', () => {
		renderTabs();

		fireEvent.click(screen.getByText('Custom'));

		expect(screen.getByText('Loyalty Tier')).toBeInTheDocument();
		expect(screen.getByText('Favorite Store')).toBeInTheDocument();

		expect(screen.queryByText('First Name')).not.toBeInTheDocument();
	});

	it('filters the active tab client-side by the search keyword', () => {
		renderTabs({searchValue: 'First'});

		expect(screen.getByText('First Name')).toBeInTheDocument();
		expect(screen.queryByText('Last Name')).not.toBeInTheDocument();
	});

	it('renders the criteria items inside the shared property-subgroups-list container', () => {
		const {container} = renderTabs();

		const propertySubgroupsList = container.querySelector(
			'.property-subgroups-list'
		);

		expect(propertySubgroupsList).toBeInTheDocument();
		expect(
			propertySubgroupsList.querySelector('.criteria-sidebar-item-root')
		).toBeInTheDocument();
	});

	it('shows the no-entries empty state when there are no custom properties', () => {
		renderTabs({customProperties: new List()});

		fireEvent.click(screen.getByText('Custom'));

		expect(screen.getByText('no-custom-fields-yet')).toBeInTheDocument();
		expect(
			screen.getByText('learn-more-about-fields').closest('a')
		).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/security-and-administration/administration/configuring-liferay/adding-custom-fields'
		);
	});
});
