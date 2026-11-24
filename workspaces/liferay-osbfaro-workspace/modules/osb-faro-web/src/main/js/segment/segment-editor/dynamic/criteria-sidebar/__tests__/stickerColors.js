import {getStickerStyle} from '../stickerColors';

describe('getStickerStyle', () => {
	it.each([
		['campaign', 'orange-l5', 'orange'],
		['event', 'teal-l5', 'teal'],
		['individual', 'indigo-l5', 'indigo'],
		['tag', 'yellow-l5', 'yellow-d4'],
		['vocabulary', 'blue-l5', 'blue'],
	])(
		'uses the %s colours from the design',
		(propertyKey, background, color) => {
			expect(getStickerStyle(propertyKey)).toEqual({
				'--criteria-sticker-bg': `var(--cadmin-${background})`,
				'--criteria-sticker-color': `var(--cadmin-${color})`,
			});
		}
	);

	it('falls back to indigo for an unknown condition type', () => {
		expect(getStickerStyle('unknown')).toEqual(getStickerStyle());
		expect(getStickerStyle()['--criteria-sticker-bg']).toBe(
			'var(--cadmin-indigo-l5)'
		);
	});
});
