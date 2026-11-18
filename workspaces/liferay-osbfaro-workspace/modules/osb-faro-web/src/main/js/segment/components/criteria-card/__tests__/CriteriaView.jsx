import * as data from 'test/data';
import CriteriaView from '../CriteriaView';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {withReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';

jest.unmock('react-dom');

const FIELD_MAPPINGS = {
	individual: {
		demographics: {
			firstName: {
				context: 'demographics',
				id: null,
				name: 'firstName',
				ownerType: 'individual',
				rawType: 'Text',
				type: 'Text'
			}
		}
	}
};

const buildSegment = () =>
	data.getImmutableMock(Segment, data.mockSegment, 0, {
		referencedObjects: {fieldMappings: FIELD_MAPPINGS}
	});

describe('CriteriaView', () => {
	afterEach(cleanup);

	it('should render', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteria(1, {
					propertyName: 'demographics/firstName/value'
				})}
				segment={data.getImmutableMock(Segment, data.mockSegment, 0, {
					referencedObjects: {
						fieldMappings: {
							individual: {
								demographics: {
									firstName: {
										context: 'demographics',
										id: null,
										name: 'firstName',
										ownerType: 'individual',
										rawType: 'Text',
										type: 'Text'
									}
								}
							}
						}
					}
				})}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with multiple values', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteria(2, {
					propertyName: 'demographics/firstName/value',
					value: '["A", "B"]'
				})}
				segment={data.getImmutableMock(Segment, data.mockSegment, 0, {
					referencedObjects: {
						fieldMappings: {
							individual: {
								demographics: {
									firstName: {
										context: 'demographics',
										id: null,
										name: 'firstName',
										ownerType: 'individual',
										rawType: 'Text',
										type: 'Text'
									}
								}
							}
						}
					}
				})}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with nested criterias', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteriaNested()}
				segment={data.getImmutableMock(Segment, data.mockSegment, 0, {
					referencedObjects: {
						fieldMappings: {
							individual: {
								demographics: {
									firstName: {
										context: 'demographics',
										id: null,
										name: 'firstName',
										ownerType: 'individual',
										rawType: 'Text',
										type: 'Text'
									}
								}
							}
						}
					}
				})}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ Non-Existent Property message', () => {
		const {queryByText} = render(
			<CriteriaView criteria={data.mockNewCriteria()} />
		);

		expect(queryByText(/^attribute no longer exists\.$/i)).toBeTruthy();
	});

	it('should render numbered steps and "then" conjunction when real time + sequential', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container, queryByText} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteria(2, {
					propertyName: 'demographics/firstName/value'
				})}
				segment={buildSegment()}
				segmentType={SegmentTypes.RealTime}
				sequential
			/>
		);

		const stepNumbers = container.querySelectorAll('.criteria-step-number');

		expect(stepNumbers).toHaveLength(2);
		expect(stepNumbers[0].textContent).toBe('1');
		expect(stepNumbers[1].textContent).toBe('2');
		expect(queryByText(/^then$/i)).toBeTruthy();
		expect(queryByText(/^and$/i)).toBeFalsy();
	});

	it('should not wrap items in criteria-step when sequential is false', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container, queryByText} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteria(2, {
					propertyName: 'demographics/firstName/value'
				})}
				segment={buildSegment()}
				segmentType={SegmentTypes.RealTime}
				sequential={false}
			/>
		);

		expect(container.querySelector('.criteria-step')).toBeNull();
		expect(container.querySelector('.criteria-step-number')).toBeNull();
		expect(queryByText(/^then$/i)).toBeFalsy();
	});

	it('should not render numbered steps when segment type is batch even if sequential', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container, queryByText} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteria(2, {
					propertyName: 'demographics/firstName/value'
				})}
				segment={buildSegment()}
				segmentType={SegmentTypes.Batch}
				sequential
			/>
		);

		expect(container.querySelector('.criteria-step')).toBeNull();
		expect(container.querySelector('.criteria-step-number')).toBeNull();
		expect(queryByText(/^then$/i)).toBeFalsy();
	});

	it('should render numbered steps only at the top level when nested', () => {
		const WrappedCriteriaView = withReferencedObjectsProvider(CriteriaView);
		const {container} = render(
			<WrappedCriteriaView
				criteria={data.mockNewCriteriaNested()}
				segment={buildSegment()}
				segmentType={SegmentTypes.RealTime}
				sequential
			/>
		);

		const stepNumbers = container.querySelectorAll('.criteria-step-number');

		expect(stepNumbers).toHaveLength(2);
		expect(stepNumbers[0].textContent).toBe('1');
		expect(stepNumbers[1].textContent).toBe('2');
	});
});
