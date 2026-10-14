import {getMaxTimeInStageLabel, getStageCountLabel} from '../lifecycleLabels';

describe('getStageCountLabel', () => {
	it('reads a single stage in the singular', () => {
		expect(getStageCountLabel(1)).toMatch(/^1 stage$/i);
	});

	it('reads several stages in the plural', () => {
		expect(getStageCountLabel(5)).toMatch(/^5 stages$/i);
	});

	it('reads no stages in the plural', () => {
		expect(getStageCountLabel(0)).toMatch(/^0 stages$/i);
	});
});

describe('getMaxTimeInStageLabel', () => {
	it('reads a single day in the singular', () => {
		expect(getMaxTimeInStageLabel(1)).toMatch(/^enabled, 1 day$/i);
	});

	it('reads several days in the plural', () => {
		expect(getMaxTimeInStageLabel(90)).toMatch(/^enabled, 90 days$/i);
	});

	it('reads a stage without a maximum as disabled', () => {
		expect(getMaxTimeInStageLabel(null)).toMatch(/^disabled$/i);
	});
});
