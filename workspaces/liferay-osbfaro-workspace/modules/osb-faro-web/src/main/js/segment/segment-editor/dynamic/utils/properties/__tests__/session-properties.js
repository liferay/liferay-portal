jest.unmock('react-dom');

import SESSION_PROPERTIES, {
	CHANNEL_OPTIONS,
	DEFAULT_UTM_PARAMETER_OPTIONS,
	getUtmParameterLabel,
	getUtmParameterLabelByFieldName,
} from '../session-properties';
import {
	ACQUISITION_PARAMETER_PROPERTY_PREFIX,
	PropertyTypes,
} from '../../constants';

describe('session-properties', () => {
	describe('CHANNEL_OPTIONS', () => {
		it('should offer the six acquisition channels', () => {
			expect(CHANNEL_OPTIONS).toEqual([
				{label: 'Direct', value: 'direct'},
				{label: 'Organic', value: 'organic'},
				{label: 'Search', value: 'search'},
				{label: 'Paid', value: 'paid'},
				{label: 'Referral', value: 'referral'},
				{label: 'Social', value: 'social'},
			]);
		});

		it('should keep every value lowercase to match Session.acquisitionChannel', () => {
			CHANNEL_OPTIONS.forEach(({value}) => {
				expect(value).toBe(value.toLowerCase());
			});
		});
	});

	describe('DEFAULT_UTM_PARAMETER_OPTIONS', () => {
		it('should map every standard UTM parameter to its backend field', () => {
			expect(DEFAULT_UTM_PARAMETER_OPTIONS).toEqual([
				{fieldName: 'context/acquisitionSource', name: 'utm_source'},
				{fieldName: 'context/acquisitionMedium', name: 'utm_medium'},
				{
					fieldName: 'context/acquisitionCampaign',
					name: 'utm_campaign',
				},
				{fieldName: 'context/acquisitionTerm', name: 'utm_term'},
				{fieldName: 'context/acquisitionContent', name: 'utm_content'},
			]);
		});

		it('should carry the property prefix so callers never compose it again', () => {
			DEFAULT_UTM_PARAMETER_OPTIONS.forEach(({fieldName}) => {
				expect(fieldName).toStartWith(
					ACQUISITION_PARAMETER_PROPERTY_PREFIX
				);

				expect(fieldName).not.toContain(
					`${ACQUISITION_PARAMETER_PROPERTY_PREFIX}${ACQUISITION_PARAMETER_PROPERTY_PREFIX}`
				);
			});
		});
	});

	describe('getUtmParameterLabel', () => {
		it('should translate every standard UTM parameter', () => {
			expect(getUtmParameterLabel('utm_source')).toBe('UTM Source');
			expect(getUtmParameterLabel('utm_medium')).toBe('UTM Medium');
			expect(getUtmParameterLabel('utm_campaign')).toBe('UTM Campaign');
			expect(getUtmParameterLabel('utm_term')).toBe('UTM Term');
			expect(getUtmParameterLabel('utm_content')).toBe('UTM Content');
		});

		it('should fall back to the raw name for a custom UTM parameter', () => {
			expect(getUtmParameterLabel('utm_cid')).toBe('utm_cid');
		});
	});

	describe('getUtmParameterLabelByFieldName', () => {
		it('should translate every standard UTM parameter field name', () => {
			expect(
				getUtmParameterLabelByFieldName('context/acquisitionSource')
			).toBe('UTM Source');
			expect(
				getUtmParameterLabelByFieldName('context/acquisitionMedium')
			).toBe('UTM Medium');
			expect(
				getUtmParameterLabelByFieldName('context/acquisitionCampaign')
			).toBe('UTM Campaign');
			expect(
				getUtmParameterLabelByFieldName('context/acquisitionTerm')
			).toBe('UTM Term');
			expect(
				getUtmParameterLabelByFieldName('context/acquisitionContent')
			).toBe('UTM Content');
		});

		it('should strip the prefix from a custom UTM parameter field name', () => {
			expect(getUtmParameterLabelByFieldName('context/utm_cid')).toBe(
				'utm_cid'
			);
		});

		it('should leave an unprefixed field name alone', () => {
			expect(getUtmParameterLabelByFieldName('utm_cid')).toBe('utm_cid');
		});
	});

	describe('SESSION_PROPERTIES', () => {
		it('should filter Channel on the field the backend maps to Session.acquisitionChannel', () => {
			const channel = SESSION_PROPERTIES.find(
				(property) => property.type === PropertyTypes.SessionChannel
			);

			expect(channel.name).toBe('context/channel');
			expect(channel.options).toEqual(CHANNEL_OPTIONS);
		});

		it('should point UTM Parameter at a sentinel name that is never sent to the backend', () => {
			const utmParameter = SESSION_PROPERTIES.find(
				(property) =>
					property.type === PropertyTypes.SessionUtmParameter
			);

			expect(utmParameter.name).not.toStartWith(
				ACQUISITION_PARAMETER_PROPERTY_PREFIX
			);
		});
	});
});
