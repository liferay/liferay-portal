import {groupUtmParameters} from '../utmParameters';

const parameter = (name: string, standard: boolean, value: string) => ({
	fieldName: null,
	name,
	standard,
	value,
});

describe('groupUtmParameters', () => {
	it('should group the values by name in the order the API sends them', () => {
		expect(
			groupUtmParameters([
				parameter('utm_campaign', true, 'q3'),
				parameter('utm_region', false, 'emea'),
				parameter('utm_source', true, 'google'),
				parameter('utm_source', true, 'bing'),
			])
		).toEqual({
			customParameters: [{name: 'utm_region', values: ['emea']}],
			utmParameters: {
				utmCampaign: ['q3'],
				utmSource: ['google', 'bing'],
			},
		});
	});

	it('should group the values of a custom parameter under its own name', () => {
		expect(
			groupUtmParameters([
				parameter('utm_partner', false, 'acme'),
				parameter('utm_partner', false, 'globex'),
				parameter('utm_region', false, 'emea'),
			]).customParameters
		).toEqual([
			{name: 'utm_partner', values: ['acme', 'globex']},
			{name: 'utm_region', values: ['emea']},
		]);
	});

	it('should return nothing to list for an empty response', () => {
		expect(groupUtmParameters([])).toEqual({
			customParameters: [],
			utmParameters: {},
		});
	});
});
