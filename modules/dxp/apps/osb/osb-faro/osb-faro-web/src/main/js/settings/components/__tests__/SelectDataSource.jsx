import React from 'react';
import SelectDataSource from '../SelectDataSource';
import {cleanup, render} from '@testing-library/react';
import {noop} from 'lodash';
import {StaticRouter} from 'react-router-dom';

jest.unmock('react-dom');

const DATA_SOURCE_ARRAY = [
	{
		iconName: 'csv_logo',
		iconSize: 'xl',
		name: 'test',
		url: '#'
	},
	{
		iconName: 'liferay_logo',
		iconSize: 'xxl',
		name: 'test1',
		url: '#'
	}
];

const mockSections = [
	{
		dataSources: DATA_SOURCE_ARRAY,
		title: 'section 1 title'
	},
	{
		dataSources: [
			{
				iconName: 'salesforce_logo',
				iconSize: 'xxl',
				name: 'test1',
				onClick: noop
			}
		],
		title: 'section two title'
	}
];

describe('SelectDataSource', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<StaticRouter>
				<SelectDataSource sections={mockSections} />
			</StaticRouter>
		);
		expect(container).toMatchSnapshot();
	});
});
