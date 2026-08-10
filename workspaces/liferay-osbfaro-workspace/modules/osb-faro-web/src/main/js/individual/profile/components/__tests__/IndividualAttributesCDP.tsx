import IndividualAttributesCDP from '../IndividualAttributesCDP';
import React from 'react';
import {fromJS} from 'immutable';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('IndividualAttributesCDP', () => {
	const mockProperties = {
		birthDate: '2020-01-01T00:00:00.000Z',
		email: 'test@example.com',
		familyName: 'Bar',
		givenName: 'Foo',
		jobTitle: 'Developer',
		languageId: 'en_US',
		middleName: 'Baz',
		prefix: 'Mr.',
		screenName: 'foobar',
		suffix: 'Jr.',
		userId: '123',
		uuid: 'uuid-123',
	};

	it('should render', () => {
		const {container} = render(
			<IndividualAttributesCDP propertiesData={fromJS(mockProperties)} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the empty state when showEmptyState is true', () => {
		const {getByText, queryByText} = render(
			<IndividualAttributesCDP
				propertiesData={fromJS(mockProperties)}
				showEmptyState
			>
				<div>{'empty state rendered'}</div>
			</IndividualAttributesCDP>
		);

		expect(getByText('empty state rendered')).toBeTruthy();
		expect(queryByText('screenName')).toBeNull();
	});

	it('should correctly format the birth date', () => {
		const {getByText} = render(
			<IndividualAttributesCDP propertiesData={fromJS(mockProperties)} />
		);

		expect(getByText('Jan 1, 2020')).toBeTruthy();
	});

	it('should display the fallback dash for missing values', () => {
		const {getAllByText} = render(
			<IndividualAttributesCDP propertiesData={fromJS({})} />
		);

		const dashes = getAllByText('-');
		expect(dashes.length).toBeGreaterThan(0);
	});
});
