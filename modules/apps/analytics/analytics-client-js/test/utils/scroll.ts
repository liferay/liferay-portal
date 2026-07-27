/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Analytics} from '../../src/types';
import {
	ScrollTracker,
	isPartiallyInViewport,
	isVisible,
} from '../../src/utils/scroll';
import {mockVisibleRect} from '../helpers';

const blogElement = `<div data-analytics-asset-id="1" data-analytics-asset-type="blog" id="blog">
	<h3>The standard Lorem Ipsum passage, used since the 1500s</h3><p>"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."</p><h3>Section 1.10.32 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC</h3><p>"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"</p>
	<p>"But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?"</p>
	<h3>Section 1.10.33 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC</h3>
	<p>"At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."</p>
</div>`;

const divElement = `<div>
	<h3>The standard Lorem Ipsum passage, used since the 1500s</h3><p>"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."</p><h3>Section 1.10.32 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC</h3><p>"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"</p>
	<p>"But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?"</p>
	<h3>Section 1.10.33 of "de Finibus Bonorum et Malorum", written by Cicero in 45 BC</h3>
	<p>"At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."</p>
</div>`;

const getPage = (): HTMLDivElement => {
	document.body.innerHTML = '';

	const page: HTMLDivElement = document.createElement('div');

	page.style.width = '600px';

	page.innerHTML =
		divElement + divElement + blogElement + divElement + divElement;

	document.body.appendChild(page);

	return page;
};

describe('ScrollTracker', () => {
	describe('getDepth() from an element', () => {
		beforeEach(() => {

			// Avoid: "Error: Not implemented: window.scrollTo."

			// @ts-ignore

			window.scrollTo = (_x, y) => {
				window.pageYOffset = y;
			};

			Object.defineProperty(document.body, 'clientHeight', {
				value: 4000,
			});
		});

		afterEach(() => {
			jest.restoreAllMocks();
		});

		it('returns the depth number from a element when the element has not yet been seen', () => {
			const page = getPage();
			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;
			const scroll = new ScrollTracker();

			jest.spyOn(
				blogElementNode,
				'getBoundingClientRect'
			).mockImplementation(() => {
				return {
					bottom: 1600,
					height: 500,
					top: 1100,
				} as DOMRect;
			});

			expect(scroll.getDepth(blogElementNode)).toBe(0);

			page.innerHTML = '';

			document.body.removeChild(page);
		});

		it('returns the depth number from a element when the element was completely viewed', () => {
			const page = getPage();

			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;

			const scroll = new ScrollTracker();

			window.scrollTo(0, 5000);

			jest.spyOn(
				blogElementNode,
				'getBoundingClientRect'
			).mockImplementation(() => {
				return {
					bottom: -1100,
					height: 500,
					top: -1600,
				} as DOMRect;
			});

			expect(scroll.getDepth(blogElementNode)).toBe(100);

			page.innerHTML = '';

			document.body.removeChild(page);
		});

		it('returns the depth number from a element when it is fully visible on the screen', () => {
			const page = getPage();

			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;

			const scroll = new ScrollTracker();

			window.scrollTo(0, 2000);

			jest.spyOn(
				blogElementNode,
				'getBoundingClientRect'
			).mockImplementation(() => {
				return {
					bottom: 900,
					height: 1000,
					top: -100,
				} as DOMRect;
			});

			const {bottom, top} = blogElementNode.getBoundingClientRect();

			expect(top < 0 && bottom > 0).toBe(true);

			expect(
				scroll.getDepth(blogElementNode) > 0 &&
					scroll.getDepth(blogElementNode) < 100
			).toBe(true);

			page.innerHTML = '';

			document.body.removeChild(page);
		});
	});

	describe('getDepth() from a page', () => {
		it('returns the depth number from page when the element was completely viewed', () => {
			const page = getPage();

			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;

			const scroll = new ScrollTracker();

			window.scrollTo(0, 5000);

			expect(scroll.getDepth(blogElementNode)).toBe(100);
		});
	});

	describe('isVisible', () => {
		afterEach(() => {
			jest.restoreAllMocks();
		});

		it('uses the native checkVisibility when available', () => {
			const element = document.createElement('div');

			const checkVisibility = jest.fn(() => true);

			element.checkVisibility = checkVisibility;

			expect(isVisible(element)).toBe(true);

			expect(checkVisibility).toHaveBeenCalledWith(
				expect.objectContaining({
					checkOpacity: true,
					checkVisibilityCSS: true,
					opacityProperty: true,
					visibilityProperty: true,
				})
			);
		});

		it('returns false when native checkVisibility reports hidden', () => {
			const element = document.createElement('div');

			element.checkVisibility = jest.fn(() => false);

			expect(isVisible(element)).toBe(false);
		});

		it('returns false in the fallback when the element has no layout box', () => {
			const element = document.createElement('div');

			document.body.appendChild(element);

			mockVisibleRect(element, {height: 0, width: 0});

			expect(isVisible(element)).toBe(false);

			document.body.removeChild(element);
		});

		it('returns false in the fallback when visibility is hidden', () => {
			const element = document.createElement('div');

			element.style.visibility = 'hidden';

			document.body.appendChild(element);

			mockVisibleRect(element);

			expect(isVisible(element)).toBe(false);

			document.body.removeChild(element);
		});

		it('returns false in the fallback when an ancestor has opacity 0', () => {
			const parent = document.createElement('div');

			parent.style.opacity = '0';

			const element = document.createElement('div');

			parent.appendChild(element);

			document.body.appendChild(parent);

			mockVisibleRect(element);

			expect(isVisible(element)).toBe(false);

			document.body.removeChild(parent);
		});

		it('returns true in the fallback when the element is rendered and opaque', () => {
			const element = document.createElement('div');

			document.body.appendChild(element);

			mockVisibleRect(element);

			expect(isVisible(element)).toBe(true);

			document.body.removeChild(element);
		});
	});

	describe('isPartiallyInViewport', () => {
		it('returns false when element is outside of viewport', () => {
			const page = getPage();
			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;

			jest.spyOn(
				blogElementNode,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 1500,
						height: 500,
						left: 0,
						right: 500,
						top: 1000,
						width: 500,
					}) as DOMRect
			);

			expect(isPartiallyInViewport(blogElementNode)).toBe(false);
		});

		it('returns true when element is outside of viewport', () => {
			const page = getPage();

			const blogElementNode = page.querySelector(
				'#blog'
			) as Analytics.HTMLElement;

			jest.spyOn(
				blogElementNode,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 500,
						height: 500,
						left: 0,
						right: 500,
						top: 0,
						width: 500,
					}) as DOMRect
			);

			expect(isPartiallyInViewport(blogElementNode)).toBe(true);
		});
	});
});
