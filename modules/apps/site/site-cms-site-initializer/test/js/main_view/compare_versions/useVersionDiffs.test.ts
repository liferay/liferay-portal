/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, renderHook, waitFor} from '@testing-library/react';
import {fetch} from 'frontend-js-web';

import {
	injectContentDiffs,
	useVersionDiffs,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/compare_versions/useVersionDiffs';

const mockFetch = fetch as jest.Mock;

function createIframe(bodyHTML: string): HTMLIFrameElement {
	const iframe = document.createElement('iframe');

	document.body.appendChild(iframe);

	const iframeDocument = iframe.contentDocument!;

	iframeDocument.body.innerHTML = bodyHTML;

	defineInnerText(iframe.contentWindow as Window & typeof globalThis);

	return iframe;
}

// JSDOM does not implement innerText; approximate the piece the code relies
// on: hidden elements contribute no visible text.

function defineInnerText(iframeWindow: Window & typeof globalThis) {
	Object.defineProperty(iframeWindow.HTMLElement.prototype, 'innerText', {
		configurable: true,
		get(this: HTMLElement): string {
			if (this.style.display === 'none') {
				return '';
			}

			return Array.from(this.childNodes)
				.map((node) => {
					if (node.nodeType === Node.TEXT_NODE) {
						return node.textContent;
					}

					if (node instanceof iframeWindow.HTMLElement) {
						return node.innerText;
					}

					return '';
				})
				.join('');
		},
	});
}

function createFieldHTML(
	fieldName: string,
	controlHTML = '<input class="form-control" />'
) {
	return `
		<div data-field-name="${fieldName}">
			<div class="form-group">${controlHTML}</div>
		</div>
	`;
}

function mockDiffsResponse(diffs: object) {
	mockFetch.mockResolvedValueOnce({
		json: async () => ({diffs}),
		ok: true,
		status: 200,
	} as Response);
}

describe('injectContentDiffs', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('overlays the diff inside the changed field, styled like the control it replaces', () => {
		const iframe = createIframe(
			createFieldHTML(
				'ObjectField_title',
				'<input class="form-control form-control-inline" />'
			)
		);

		injectContentDiffs({title: 'Old Title'}, 'removals', iframe);

		const box = iframe.contentDocument!.querySelector(
			'[data-field-name="ObjectField_title"] .form-group .cms-compare-versions-diff'
		);

		expect(box).toHaveTextContent('Old Title');
		expect(box).toHaveClass('form-control', 'form-control-inline');
	});

	it('falls back to the ObjectEntry wrapper and to a plain form-control box', () => {
		const iframe = createIframe(
			`<div data-field-name="ObjectEntry_objectEntryFriendlyURL">
				<div class="form-group"></div>
			</div>`
		);

		injectContentDiffs(
			{objectEntryFriendlyURL: 'old-url'},
			'removals',
			iframe
		);

		const box = iframe.contentDocument!.querySelector(
			'.cms-compare-versions-diff'
		);

		expect(box).toHaveTextContent('old-url');
		expect(box).toHaveClass('form-control');
	});

	it('ignores diffs whose field is not on the page', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_title'));

		injectContentDiffs({unknownField: 'value'}, 'removals', iframe);

		expect(
			iframe.contentDocument!.querySelector('.cms-compare-versions-diff')
		).toBeNull();
	});

	it('marks the diff type on the body', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_title'));

		injectContentDiffs({title: 'Old Title'}, 'additions', iframe);

		expect(iframe.contentDocument!.body).toHaveClass(
			'cms-compare-versions-additions'
		);
	});

	it('does not duplicate overlays when injecting again', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_title'));

		injectContentDiffs({title: 'First'}, 'removals', iframe);
		injectContentDiffs({title: 'Second'}, 'removals', iframe);

		const boxes = iframe.contentDocument!.querySelectorAll(
			'.cms-compare-versions-diff'
		);

		expect(boxes).toHaveLength(1);
		expect(boxes[0]).toHaveTextContent('Second');
	});

	it('removes every trace of the previous comparison when there is no diff', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_upload'));

		injectContentDiffs(
			{
				upload: '<img class="cms-compare-versions-attachment" src="a.png" /> a.png',
			},
			'removals',
			iframe
		);
		injectContentDiffs(null, 'removals', iframe);

		const iframeDocument = iframe.contentDocument!;

		expect(
			iframeDocument.querySelector('.cms-compare-versions-diff')
		).toBeNull();
		expect(
			iframeDocument.querySelector('.cms-compare-versions-attachment')
		).toBeNull();
		expect(iframeDocument.body).not.toHaveClass(
			'cms-compare-versions-removals'
		);
	});

	it('moves the attachment image above the diff box, framed with the pane color', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_upload'));

		injectContentDiffs(
			{
				upload: '<span class="diff-html-added"><img class="cms-compare-versions-attachment" src="a.png" /></span><span class="diff-html-added">a.png</span>',
			},
			'additions',
			iframe
		);

		const formGroup = iframe.contentDocument!.querySelector(
			'[data-field-name="ObjectField_upload"] .form-group'
		)!;

		const image = formGroup.querySelector(
			'.cms-compare-versions-attachment'
		)!;

		expect(image).toHaveClass('border-success');

		const box = formGroup.querySelector('.cms-compare-versions-diff')!;

		expect(
			image.compareDocumentPosition(box) &
				Node.DOCUMENT_POSITION_FOLLOWING
		).toBeTruthy();
	});

	it('frames the attachment image with the removals color on the removals pane', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_upload'));

		injectContentDiffs(
			{
				upload: '<span class="diff-html-removed"><img class="cms-compare-versions-attachment" src="a.png" /></span>',
			},
			'removals',
			iframe
		);

		expect(
			iframe.contentDocument!.querySelector(
				'.cms-compare-versions-attachment'
			)
		).toHaveClass('border-danger');
	});

	it('hides the structural elements the hidden content leaves empty, unless they hold media', () => {
		const iframe = createIframe(createFieldHTML('ObjectField_content'));

		injectContentDiffs(
			{
				content:
					'<ul id="ghost"><li><span class="diff-html-removed" style="display: none">Gone</span></li></ul>' +
					'<ul id="kept"><li>Visible text</li></ul>' +
					'<figure id="media"><img src="a.png" /></figure>',
			},
			'additions',
			iframe
		);

		const iframeDocument = iframe.contentDocument!;

		const getDisplay = (id: string) =>
			(iframeDocument.getElementById(id) as HTMLElement).style.display;

		expect(getDisplay('ghost')).toBe('none');
		expect(getDisplay('kept')).not.toBe('none');
		expect(getDisplay('media')).not.toBe('none');
	});
});

describe('useVersionDiffs', () => {
	const DEFAULT_INPUT = {
		languageId: 'en_US',
		objectEntryId: 42,
		sourceVersion: 2,
		targetVersion: 1,
	};

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('does not request anything while either version is unselected', () => {
		renderHook(() =>
			useVersionDiffs({...DEFAULT_INPUT, sourceVersion: null})
		);
		renderHook(() =>
			useVersionDiffs({...DEFAULT_INPUT, targetVersion: null})
		);

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('clears the previous diffs while the next comparison is in flight', async () => {
		mockDiffsResponse({source: {}, target: {title: 'first'}});

		const {rerender, result} = renderHook(
			(input) => useVersionDiffs(input),
			{initialProps: DEFAULT_INPUT}
		);

		await waitFor(() => expect(result.current).not.toBeNull());

		mockFetch.mockImplementationOnce(() => new Promise<Response>(() => {}));

		rerender({...DEFAULT_INPUT, targetVersion: 3});

		expect(result.current).toBeNull();
	});

	it('requests the diff once and exposes the source and target diffs', async () => {
		const diffs = {source: {title: 'old'}, target: {title: 'new'}};

		mockDiffsResponse(diffs);

		const {result} = renderHook(() => useVersionDiffs(DEFAULT_INPUT));

		await waitFor(() => expect(result.current).toEqual(diffs));

		expect(mockFetch).toHaveBeenCalledTimes(1);

		const [url, options] = mockFetch.mock.calls[0];

		expect(url).toBe('/o/cms/compare-versions');
		expect(JSON.parse(options.body)).toEqual({
			languageId: 'en_US',
			objectEntryId: 42,
			sourceVersion: 2,
			targetVersion: 1,
		});
	});

	it('clears the diffs when the request fails', async () => {
		mockDiffsResponse({source: {}, target: {}});

		const {rerender, result} = renderHook(
			(input) => useVersionDiffs(input),
			{initialProps: DEFAULT_INPUT}
		);

		await waitFor(() => expect(result.current).not.toBeNull());

		mockFetch.mockResolvedValueOnce({
			json: async () => ({title: 'Server error'}),
			ok: false,
			status: 500,
		} as Response);

		rerender({...DEFAULT_INPUT, targetVersion: 3});

		await waitFor(() => expect(result.current).toBeNull());
	});

	it('refetches when the comparison language changes', async () => {
		mockDiffsResponse({source: {}, target: {}});

		const {rerender} = renderHook((input) => useVersionDiffs(input), {
			initialProps: DEFAULT_INPUT,
		});

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		mockDiffsResponse({source: {}, target: {}});

		rerender({...DEFAULT_INPUT, languageId: 'es_ES'});

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(JSON.parse(mockFetch.mock.calls[1][1].body).languageId).toBe(
			'es_ES'
		);
	});

	it('discards a stale response that resolves after the versions changed', async () => {
		let resolveFirst: (response: Response) => void;

		mockFetch.mockImplementationOnce(
			() =>
				new Promise<Response>((resolve) => {
					resolveFirst = resolve;
				})
		);

		const {rerender, result} = renderHook(
			(input) => useVersionDiffs(input),
			{initialProps: DEFAULT_INPUT}
		);

		const freshDiffs = {source: {}, target: {title: 'fresh'}};

		mockDiffsResponse(freshDiffs);

		rerender({...DEFAULT_INPUT, targetVersion: 3});

		await waitFor(() => expect(result.current).toEqual(freshDiffs));

		await act(async () => {
			resolveFirst!({
				json: async () => ({
					diffs: {source: {}, target: {title: 'stale'}},
				}),
				ok: true,
				status: 200,
			} as Response);
		});

		expect(result.current).toEqual(freshDiffs);
		expect(mockFetch).toHaveBeenCalledTimes(2);
	});
});
