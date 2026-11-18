import * as data from 'test/data';
import CriteriaCard from '../index';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Segment} from 'shared/util/records';

jest.unmock('react-dom');

describe('CriteriaCard', () => {
	const innerHeight = window.innerHeight;

	afterEach(() => {
		cleanup();

		window.innerHeight = innerHeight;
	});

	const mockSegment = data.getImmutableMock(Segment, data.mockSegment, 0, {
		referencedObjects: {
			fieldMappings: {
				individual: {
					demographics: {
						firstName: {
							context: 'demographics',
							id: null,
							name: 'firstName',
							ownerType: 'individual',
							propertyKey: '',
							rawType: 'text',
							type: 'text'
						}
					}
				}
			}
		}
	});

	it('should render', () => {
		const {container} = render(
			<ReferencedObjectsProvider segment={mockSegment}>
				<CriteriaCard
					criteriaString="demographics/firstName/value eq 'Test'"
					segment={mockSegment}
				/>
			</ReferencedObjectsProvider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ anonymous label', () => {
		const {queryByText} = render(
			<ReferencedObjectsProvider segment={mockSegment}>
				<CriteriaCard
					criteriaString="demographics/name/value eq 'Test'"
					includeAnonymousUsers
					segment={mockSegment}
				/>
			</ReferencedObjectsProvider>
		);

		expect(queryByText(/^includes anonymous individuals$/i)).toBeTruthy();
	});
});
