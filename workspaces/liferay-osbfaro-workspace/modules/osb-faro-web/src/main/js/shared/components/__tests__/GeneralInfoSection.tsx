import React from 'react';
import {GeneralInfoSection} from '../GeneralInfoSection';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('GeneralInfoSection', () => {
	const mockConfig = [
		{
			columnClass: 'col-md-12',
			items: [
				{className: 'name-class', icon: 'user', key: 'userName'},
				{className: 'email-class', icon: 'mail', key: 'userEmail'},
			],
			title: 'User Profile',
		},
	];

	const mockLanguageMap = {
		userEmail: 'Email Address',
		userName: 'Full Name',
	};

	it('should render', () => {
		const {container} = render(
			<GeneralInfoSection
				config={mockConfig}
				getValue={() => 'test'}
				languageMap={mockLanguageMap}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should display values returned by the getValue function', () => {
		const mockGetValue = jest.fn((key) => {
			if (key === 'userName') return 'John Doe';
			return 'john@example.com';
		});

		const {getByText} = render(
			<GeneralInfoSection
				config={mockConfig}
				getValue={mockGetValue}
				languageMap={mockLanguageMap}
			/>
		);

		expect(getByText('John Doe')).toBeTruthy();
		expect(getByText('john@example.com')).toBeTruthy();

		expect(mockGetValue).toHaveBeenCalledWith('userName');
		expect(mockGetValue).toHaveBeenCalledWith('userEmail');
	});

	it.each([null, undefined, ''])(
		'should display no data when getValue returns %p',
		(value) => {
			const mockGetValue = jest.fn((key) => {
				if (key === 'userName') return 'John Doe';
				return value as string | undefined;
			});

			const {getByText} = render(
				<GeneralInfoSection
					config={mockConfig}
					getValue={mockGetValue}
					languageMap={mockLanguageMap}
				/>
			);

			expect(getByText('John Doe')).toBeTruthy();
			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([0, '0'])(
		'should display a zero when getValue returns %p',
		(value) => {
			const mockGetValue = jest.fn((key) => {
				if (key === 'userName') return 'John Doe';
				return value;
			});

			const {getByText, queryByText} = render(
				<GeneralInfoSection
					config={mockConfig}
					getValue={mockGetValue}
					languageMap={mockLanguageMap}
				/>
			);

			expect(getByText('0')).toBeTruthy();
			expect(queryByText('–')).toBeNull();
		}
	);

	it('should not link a missing value', () => {
		const {container} = render(
			<GeneralInfoSection
				config={mockConfig}
				getHref={() => '/foo'}
				getValue={() => undefined}
				languageMap={mockLanguageMap}
			/>
		);

		expect(container.querySelector('a')).toBeNull();
	});

	it('should render the correct section title', () => {
		const {getByText} = render(
			<GeneralInfoSection
				config={mockConfig}
				getValue={() => 'test'}
				languageMap={mockLanguageMap}
			/>
		);

		expect(getByText('User Profile')).toBeTruthy();
	});

	it('should render multiple sections with their respective items', () => {
		const multiSectionConfig = [
			{
				columnClass: 'section-1-column',
				items: [{className: 'item-1', icon: 'user', key: 'name'}],
				title: 'Primary Info',
			},
			{
				columnClass: 'section-2-column',
				items: [{className: 'item-2', icon: 'phone', key: 'phone'}],
				title: 'Secondary Info',
			},
		];

		const languageMap = {
			name: 'Name Label',
			phone: 'Phone Label',
		};

		const mockGetValue = jest.fn((key: string) => {
			const data = {name: 'Alice', phone: '555-0123'};
			return data[key as keyof typeof data];
		});

		const {container} = render(
			<GeneralInfoSection
				config={multiSectionConfig}
				getValue={mockGetValue}
				languageMap={languageMap}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
