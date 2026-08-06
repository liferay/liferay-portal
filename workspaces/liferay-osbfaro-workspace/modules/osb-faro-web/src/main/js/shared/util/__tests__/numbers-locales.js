import {
	formatPercent,
	toCurrency,
	toFixedPoint,
	toLocale,
	toRounded,
	toThousands,
} from '../numbers';

describe('Numbers Locales', () => {
	describe('en-US', () => {
		const locale = 'en-US';

		it('toLocale should format correctly', () => {
			expect(toLocale(1234.56, locale)).toBe('1,234.56');
		});

		it('toFixedPoint should format correctly', () => {
			expect(toFixedPoint(1234.56, locale)).toBe('1,235');
		});

		it('toRounded should format correctly', () => {
			expect(toRounded(1234.56, 1, locale)).toBe('1,234.6');
			expect(toRounded(1234, 1, locale)).toBe('1,234');
			expect(toRounded(0.300001, 2, locale)).toBe('0.30');
		});

		it('toThousands should format correctly', () => {
			expect(toThousands(4042239, locale)).toBe('4M');
		});

		it('formatPercent should not insert a space before the sign', () => {
			expect(formatPercent(7.3, 1, locale)).toBe('7.3%');
		});

		it('toCurrency should format correctly', () => {
			expect(toCurrency(1234.5, 'USD', locale)).toBe('$1,234.50');
		});
	});

	describe('pt-BR', () => {
		const locale = 'pt-BR';

		it('toLocale should format correctly', () => {
			expect(toLocale(1234.56, locale)).toBe('1.234,56');
		});

		it('toFixedPoint should format correctly', () => {
			expect(toFixedPoint(1234.56, locale)).toBe('1.235');
		});

		it('toRounded should format correctly', () => {
			expect(toRounded(1234.56, 1, locale)).toBe('1.234,6');
			expect(toRounded(1234, 1, locale)).toBe('1.234');
			expect(toRounded(0.300001, 2, locale)).toBe('0,30');
		});

		it('toThousands should format correctly', () => {
			expect(toThousands(4042239, locale)).toBe('4M');
			expect(toThousands(4500000, locale)).toBe('4,5M');
		});

		it('formatPercent should use a comma decimal separator, no space before the sign', () => {
			expect(formatPercent(7.3, 1, locale)).toBe('7,3%');
		});

		it('toCurrency should format correctly', () => {
			expect(toCurrency(1234.5, 'BRL', locale)).toBe('R$\u00A01.234,50');
		});
	});

	describe('ja-JP', () => {
		const locale = 'ja-JP';

		it('toLocale should format correctly', () => {
			expect(toLocale(1234.56, locale)).toBe('1,234.56');
		});

		it('toFixedPoint should format correctly', () => {
			expect(toFixedPoint(1234.56, locale)).toBe('1,235');
		});

		it('toRounded should format correctly', () => {
			expect(toRounded(1234.56, 1, locale)).toBe('1,234.6');
			expect(toRounded(1234, 1, locale)).toBe('1,234');
			expect(toRounded(0.300001, 2, locale)).toBe('0.30');
		});

		it('toThousands should abbreviate at 10,000 with 万/億, grouping every 4 digits', () => {
			expect(toThousands(1300, locale)).toBe('1,300');
			expect(toThousands(4042239, locale)).toBe('404.2万');
			expect(toThousands(100000000, locale)).toBe('1億');
		});

		it('formatPercent should format correctly', () => {
			expect(formatPercent(7.3, 1, locale)).toBe('7.3%');
		});

		it('toCurrency should use the fullwidth yen sign', () => {
			expect(toCurrency(1234, 'JPY', locale)).toBe('￥1,234');
		});
	});

	describe('es-ES', () => {
		const locale = 'es-ES';

		it('toThousands should use a comma decimal separator', () => {
			expect(toThousands(1300, locale)).toBe('1,3K');
			expect(toThousands(4042239, locale)).toBe('4M');
			expect(toThousands(4500000, locale)).toBe('4,5M');
		});

		it('toCurrency should insert a non-breaking space before the currency sign', () => {
			expect(toCurrency(1234.5, 'USD', locale)).toBe('1.234,50\u00A0US$');
		});

		it('formatPercent should insert a non-breaking space before the sign', () => {
			expect(formatPercent(7.3, 1, locale)).toBe('7,3\u00A0%');
		});
	});

	describe('zh-CN', () => {
		const locale = 'zh-CN';

		it('toLocale should format correctly', () => {
			expect(toLocale(1234.56, locale)).toBe('1,234.56');
		});

		it('toFixedPoint should format correctly', () => {
			expect(toFixedPoint(1234.56, locale)).toBe('1,235');
		});

		it('toRounded should format correctly', () => {
			expect(toRounded(1234.56, 1, locale)).toBe('1,234.6');
			expect(toRounded(1234, 1, locale)).toBe('1,234');
			expect(toRounded(0.300001, 2, locale)).toBe('0.30');
		});
	});
});
