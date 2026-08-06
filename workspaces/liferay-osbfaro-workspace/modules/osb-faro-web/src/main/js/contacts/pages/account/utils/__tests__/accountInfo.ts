import {getAccountInfoDisplayValues} from '../accountInfo';

describe('getAccountInfoDisplayValues', () => {
	describe('revenue', () => {
		it('should abbreviate millions', () => {
			expect(
				getAccountInfoDisplayValues({annualRevenue: 120000000}).revenue
			).toBe('120M');
		});

		it('should abbreviate billions', () => {
			expect(
				getAccountInfoDisplayValues({annualRevenue: 11359000000})
					.revenue
			).toBe('11.3B');
		});

		it('should render a missing revenue blank', () => {
			expect(getAccountInfoDisplayValues({}).revenue).toBe('');
		});

		it('should render a zeroed revenue blank', () => {
			expect(
				getAccountInfoDisplayValues({annualRevenue: 0}).revenue
			).toBe('');
		});
	});

	describe('lifecycle stage', () => {
		it('should resolve a mapped stage to its label and display type', () => {
			expect(
				getAccountInfoDisplayValues({lifecycleStage: 'ENGAGED'})
					.lifecycleStage
			).toEqual({displayType: 'warning', label: 'Engaged'});
		});

		it('should resolve every mapped stage', () => {
			expect(
				getAccountInfoDisplayValues({lifecycleStage: 'AT_RISK'})
					.lifecycleStage
			).toEqual({displayType: 'danger', label: 'At Risk'});
			expect(
				getAccountInfoDisplayValues({lifecycleStage: 'ESTABLISHED'})
					.lifecycleStage
			).toEqual({displayType: 'success', label: 'Established'});
		});

		it('should keep the raw name of an unmapped stage', () => {
			expect(
				getAccountInfoDisplayValues({lifecycleStage: 'NOT_A_STAGE'})
					.lifecycleStage
			).toEqual({displayType: 'secondary', label: 'NOT_A_STAGE'});
		});

		it('should leave a missing stage undefined', () => {
			expect(
				getAccountInfoDisplayValues({}).lifecycleStage
			).toBeUndefined();
		});

		it('should leave a null stage undefined', () => {
			expect(
				getAccountInfoDisplayValues({lifecycleStage: null})
					.lifecycleStage
			).toBeUndefined();
		});
	});
});
