import EntityList from '../EntityList';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {mockIndividual, mockSegment} from 'test/data';
import {withStaticRouter} from 'test/mock-router';

jest.unmock('react-dom');

const items = [
	mockIndividual(0, {total: 123}),
	mockIndividual(1, {total: 123}),
	mockIndividual(2, {total: 123})
];

const segments = [
	mockSegment(0, {segmentType: 'STATIC'}),
	mockSegment(1, {segmentType: 'DYNAMIC'}),
	mockSegment(2, {segmentType: 'STATIC'})
];

const WrappedComponent = withStaticRouter(EntityList);

describe('EntityList', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<WrappedComponent groupId='23' items={items} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render noItemsContent', () => {
		const noItemsContent = <h1>{'No Items!'}</h1>;
		const {queryByText} = render(
			<WrappedComponent
				groupId='23'
				noItemsContent={noItemsContent}
				total={0}
			/>
		);

		expect(queryByText('No Items!')).toBeTruthy();
	});

	it('should render a list of segments with segment icons', () => {
		const {container} = render(
			<WrappedComponent groupId='23' header='foo bar' items={segments} />
		);

		expect(
			container.querySelectorAll(
				'.lexicon-icon-individual_static_segment'
			).length
		).toBe(2);

		expect(
			container.querySelectorAll(
				'.lexicon-icon-individual_dynamic_segment'
			).length
		).toBe(1);
	});
});
