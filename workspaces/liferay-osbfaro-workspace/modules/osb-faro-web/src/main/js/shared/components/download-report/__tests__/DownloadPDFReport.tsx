import {
	expandReportContent,
	fetchSprite,
	fitInlineInputs,
	getProductName,
	inlineSVGIcons,
	isToolbar,
	resetSpriteCache,
} from '../DownloadPDFReport';

const SPRITE_SVG =
	'<svg xmlns="http://www.w3.org/2000/svg">' +
	'<symbol id="icon-home" viewBox="0 0 16 16">' +
	'<path d="M8 1l7 7H1z"></path>' +
	'</symbol>' +
	'</svg>';

const mockFetch = (text: string) => {
	global.fetch = jest.fn().mockResolvedValue({
		text: jest.fn().mockResolvedValue(text),
	}) as any;
};

describe('getProductName', () => {
	it('returns the Liferay Data Platform name for LDP plans', () => {
		expect(getProductName(true)).toBe('Liferay Data Platform');
	});

	it('returns the Analytics Cloud name for non LDP plans', () => {
		expect(getProductName(false)).toBe('Analytics Cloud');
	});
});

describe('fetchSprite', () => {
	let querySelectorSpy: jest.SpyInstance;

	beforeEach(() => {
		resetSpriteCache();
		querySelectorSpy = jest.spyOn(document, 'querySelector');
	});

	afterEach(() => {
		querySelectorSpy.mockRestore();
	});

	it('returns null when no svg use element exists', async () => {
		querySelectorSpy.mockReturnValue(null);

		expect(await fetchSprite()).toBeNull();
	});

	it('returns null when href has no sprite URL (fragment only)', async () => {
		const useEl = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'use'
		);

		useEl.setAttribute('href', '#icon-home');
		querySelectorSpy.mockReturnValue(useEl);

		expect(await fetchSprite()).toBeNull();
	});

	it('fetches the sprite URL and returns the parsed SVG element', async () => {
		const useEl = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'use'
		);

		useEl.setAttribute('href', '/o/frontend-icons/icons.svg#icon-home');
		querySelectorSpy.mockReturnValue(useEl);
		mockFetch(SPRITE_SVG);

		const result = await fetchSprite();

		expect(result).not.toBeNull();
		expect(result!.tagName.toLowerCase()).toBe('svg');
		expect(global.fetch).toHaveBeenCalledWith(
			'/o/frontend-icons/icons.svg'
		);
	});

	it('reads xlink:href as fallback when href is absent', async () => {
		const useEl = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'use'
		);

		useEl.setAttribute(
			'xlink:href',
			'/o/frontend-icons/icons.svg#icon-home'
		);
		querySelectorSpy.mockReturnValue(useEl);
		mockFetch(SPRITE_SVG);

		const result = await fetchSprite();

		expect(result).not.toBeNull();
		expect(global.fetch).toHaveBeenCalledWith(
			'/o/frontend-icons/icons.svg'
		);
	});

	it('caches the fetched sprite so fetch is called only once', async () => {
		const useEl = document.createElementNS(
			'http://www.w3.org/2000/svg',
			'use'
		);

		useEl.setAttribute('href', '/o/frontend-icons/icons.svg#icon-home');
		querySelectorSpy.mockReturnValue(useEl);
		mockFetch(SPRITE_SVG);

		await fetchSprite();
		await fetchSprite();

		expect(global.fetch).toHaveBeenCalledTimes(1);
	});
});

describe('inlineSVGIcons', () => {
	let sprite: Element;

	beforeEach(() => {
		const doc = new DOMParser().parseFromString(
			SPRITE_SVG,
			'image/svg+xml'
		);

		sprite = doc.documentElement;
	});

	const buildDoc = (svgInner: string): Document => {
		const clonedDoc = document.implementation.createHTMLDocument();

		clonedDoc.body.innerHTML = `<svg>${svgInner}</svg>`;

		return clonedDoc;
	};

	it('replaces use element with the matching symbol innerHTML', () => {
		const clonedDoc = buildDoc('<use href="/icons.svg#icon-home"></use>');

		inlineSVGIcons(clonedDoc, sprite);

		const svg = clonedDoc.querySelector('svg')!;

		expect(svg.querySelector('use')).toBeNull();
		expect(svg.querySelector('path')).not.toBeNull();
	});

	it('sets viewBox from the symbol onto the parent svg element', () => {
		const clonedDoc = buildDoc('<use href="/icons.svg#icon-home"></use>');

		inlineSVGIcons(clonedDoc, sprite);

		expect(clonedDoc.querySelector('svg')!.getAttribute('viewBox')).toBe(
			'0 0 16 16'
		);
	});

	it('leaves use element unchanged when href has no symbol fragment', () => {
		const clonedDoc = buildDoc('<use href="/icons.svg"></use>');

		inlineSVGIcons(clonedDoc, sprite);

		expect(clonedDoc.querySelector('use')).not.toBeNull();
	});

	it('leaves use element unchanged when symbol is not found in sprite', () => {
		const clonedDoc = buildDoc(
			'<use href="/icons.svg#icon-nonexistent"></use>'
		);

		inlineSVGIcons(clonedDoc, sprite);

		expect(clonedDoc.querySelector('use')).not.toBeNull();
	});

	it('does not set viewBox when symbol has no viewBox attribute', () => {
		const spriteDoc = new DOMParser().parseFromString(
			'<svg xmlns="http://www.w3.org/2000/svg">' +
				'<symbol id="icon-plain"><circle cx="8" cy="8" r="4"></circle></symbol>' +
				'</svg>',
			'image/svg+xml'
		);
		const spriteWithoutViewBox = spriteDoc.documentElement;
		const clonedDoc = buildDoc('<use href="/icons.svg#icon-plain"></use>');

		inlineSVGIcons(clonedDoc, spriteWithoutViewBox);

		expect(
			clonedDoc.querySelector('svg')!.getAttribute('viewBox')
		).toBeNull();
	});

	it('handles xlink:href when href attribute is absent', () => {
		const clonedDoc = document.implementation.createHTMLDocument();

		clonedDoc.body.innerHTML =
			'<svg><use xlink:href="/icons.svg#icon-home"></use></svg>';

		inlineSVGIcons(clonedDoc, sprite);

		const svg = clonedDoc.querySelector('svg')!;

		expect(svg.querySelector('use')).toBeNull();
		expect(svg.querySelector('path')).not.toBeNull();
	});
});

describe('expandReportContent', () => {
	it('removes the height limits and clipping of the marked elements', () => {
		document.body.innerHTML =
			'<div data-report-expand style="max-height: 100vh; overflow: auto; position: sticky"></div>' +
			'<div id="unmarked" style="overflow: auto"></div>';

		expandReportContent(document);

		const marked = document.querySelector<HTMLElement>(
			'[data-report-expand]'
		)!;

		expect(marked.style.height).toBe('auto');
		expect(marked.style.maxHeight).toBe('none');
		expect(marked.style.overflow).toBe('visible');
		expect(marked.style.position).toBe('static');
		expect(document.getElementById('unmarked')!.style.overflow).toBe(
			'auto'
		);
	});
});

describe('fitInlineInputs', () => {
	it('lets inline inputs grow to fit their text', () => {
		document.body.innerHTML =
			'<input class="form-control form-control-inline" style="height: 40px">' +
			'<input class="form-control" id="regular" style="height: 40px">';

		fitInlineInputs(document);

		const inlineInput = document.querySelector<HTMLElement>(
			'.form-control-inline'
		)!;

		expect(inlineInput.style.height).toBe('auto');
		expect(inlineInput.style.lineHeight).toBe('normal');
		expect(document.getElementById('regular')!.style.height).toBe('40px');
	});
});

describe('isToolbar', () => {
	it('leaves the page toolbar out of the report', () => {
		document.body.innerHTML =
			'<nav class="sticky-top toolbar-root tbar"></nav>' +
			'<aside class="base-edit-page-sidebar"></aside>';

		expect(isToolbar(document.querySelector('nav')!)).toBe(true);
		expect(isToolbar(document.querySelector('aside')!)).toBe(false);
	});
});
