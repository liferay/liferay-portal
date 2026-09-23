import {DEFAULT_LOCALE, setLocale} from 'shared/util/locale';
import {getFileSizeLabel} from '../FileDropTarget';

describe('getFileSizeLabel', () => {
	afterEach(() => {
		setLocale(DEFAULT_LOCALE);
	});

	it('labels sizes under 1000 KB in KB', () => {
		expect(getFileSizeLabel(1536)).toBe('1.5 KB');
	});

	it('labels larger sizes in MB', () => {
		expect(getFileSizeLabel(2.5 * 1024 * 1024)).toBe('2.5 MB');
	});

	it('formats the size in the current locale', () => {
		setLocale('de-DE');

		expect(getFileSizeLabel(1536)).toBe('1,5 KB');
		expect(getFileSizeLabel(2.5 * 1024 * 1024)).toBe('2,5 MB');
	});
});
