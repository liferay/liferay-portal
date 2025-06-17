import Avatar from '../Avatar';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {mockIndividual, mockSegment} from 'test/data';

jest.unmock('react-dom');

describe('Avatar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Avatar entity={mockIndividual()} />);
		expect(container).toMatchSnapshot();
	});

	it('should render with a first name initial', () => {
		const {getByText} = render(
			<Avatar
				entity={mockIndividual(0, {
					familyName: undefined,
					givenName: 'foo'
				})}
			/>
		);

		expect(getByText('F')).toBeTruthy();
	});

	it('should render with a first and last name', () => {
		const {getByText} = render(
			<Avatar
				entity={mockIndividual(0, {
					familyName: 'bar',
					givenName: 'foo'
				})}
			/>
		);

		expect(getByText('FB')).toBeTruthy();
	});

	it('should render if null is used as the first name', () => {
		const {container} = render(
			<Avatar
				entity={mockIndividual(0, {familyName: null, givenName: null})}
			/>
		);
		expect(container).toBeTruthy();
	});

	it('should render with a specific size', () => {
		const {container} = render(
			<Avatar entity={mockIndividual()} size='xl' />
		);
		expect(container.querySelector('.sticker-xl')).toBeTruthy();
	});

	it('should render with a specific color id', () => {
		const {container} = render(<Avatar entity={mockIndividual(4)} />);
		expect(container.querySelector('.sticker-chartOrange')).toBeTruthy();
	});

	it('should render with an image', () => {
		const {container} = render(
			<Avatar
				entity={mockIndividual(0, {
					image: 'http://i.imgur.com/G5pfP.jpg'
				})}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render an icon if a segment is passed', () => {
		const {container} = render(<Avatar entity={mockSegment()} />);
		expect(
			container.querySelector('.lexicon-icon-faro_contacts_segments')
		).toBeTruthy();
	});
});
