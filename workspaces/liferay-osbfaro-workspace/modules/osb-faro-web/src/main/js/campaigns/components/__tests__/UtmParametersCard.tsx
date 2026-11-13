import React from 'react';
import UtmParametersCard from '../UtmParametersCard';
import {fireEvent, render, screen, within} from '@testing-library/react';
import {UtmParameters} from 'campaigns/utils/types';

jest.unmock('react-dom');

const UTM_PARAMETERS: UtmParameters = {
	utmCampaign: ['q3-manufacturing-webinar'],
	utmContent: ['hero-banner', 'footer-cta'],
	utmMedium: ['email'],
	utmSource: ['google'],
};

const renderCard = (utmParameters: UtmParameters = UTM_PARAMETERS) =>
	render(<UtmParametersCard utmParameters={utmParameters} />);

const expand = () =>
	fireEvent.click(screen.getByRole('button', {name: /UTM Parameters/}));

const getValueCell = (parameter: string) =>
	screen.getByText(parameter).closest('tr')!.lastElementChild as HTMLElement;

describe('UtmParametersCard', () => {
	it('should start collapsed and expand from its title', () => {
		renderCard();

		const toggle = screen.getByRole('button', {name: /UTM Parameters/});

		expect(toggle).toHaveAttribute('aria-expanded', 'false');

		expand();

		expect(toggle).toHaveAttribute('aria-expanded', 'true');
	});

	it('should list the default parameters in URL order', () => {
		const {container} = renderCard();

		expand();

		const parameters = Array.from(
			container.querySelectorAll('tbody tr'),
			(row) => row.querySelector('th, td')!.textContent
		);

		expect(parameters).toEqual([
			'utmSource',
			'utmMedium',
			'utmCampaign',
			'utmContent',
			'utmTerm',
		]);
	});

	it('should render a label for every distinct value', () => {
		renderCard({
			...UTM_PARAMETERS,
			utmContent: ['hero-banner', 'footer-cta', 'hero-banner'],
		});

		expand();

		const labels = within(getValueCell('utmContent')).getAllByText(
			/hero-banner|footer-cta/
		);

		expect(labels.map((label) => label.textContent)).toEqual([
			'hero-banner',
			'footer-cta',
		]);
	});

	it('should render a dash for a parameter without values', () => {
		renderCard({...UTM_PARAMETERS, utmMedium: []});

		expand();

		expect(getValueCell('utmTerm')).toHaveTextContent(/^-$/);
		expect(getValueCell('utmMedium')).toHaveTextContent(/^-$/);
	});

	it('should render custom UTM parameters after the default ones', () => {
		const {container} = render(
			<UtmParametersCard
				customParameters={[{name: 'utm_region', values: ['emea']}]}
				utmParameters={UTM_PARAMETERS}
			/>
		);

		expand();

		const parameters = Array.from(
			container.querySelectorAll('tbody tr'),
			(row) => row.querySelector('th, td')!.textContent
		);

		expect(parameters).toEqual([
			'utmSource',
			'utmMedium',
			'utmCampaign',
			'utmContent',
			'utmTerm',
			'utm_region',
		]);
		expect(
			within(getValueCell('utm_region')).getByText('emea')
		).toBeInTheDocument();
	});

	it('should not render the empty state when only a custom parameter has a value', () => {
		render(
			<UtmParametersCard
				customParameters={[{name: 'utm_region', values: ['emea']}]}
				utmParameters={{}}
			/>
		);

		expand();

		expect(
			screen.queryByText('No UTM Parameters Were Found')
		).not.toBeInTheDocument();
		expect(getValueCell('utmSource')).toHaveTextContent(/^-$/);
	});

	it('should render the empty state when no parameter has a value', () => {
		const {container} = renderCard({utmSource: []});

		expand();

		expect(screen.getByText('No UTM Parameters Were Found')).toBeTruthy();
		expect(
			screen.getByText(
				'This campaign has no UTM parameters associated with it yet.'
			)
		).toBeTruthy();
		expect(container.querySelector('table')).toBeNull();
	});
});
