/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import EventEmitter from './events/EventEmitter';
import throttle from './throttle.es';
import fetch from './util/fetch.es';
import setCSPNonce from './util/set_csp_nonce';

const SCRIPT_URL = document.currentScript.src;

/**
 * Options
 *
 * @property {String|Number}  breakpoint   The window width that defines the desktop size.
 * @property {String}         content      The class or ID of the content container.
 * @property {String}         container    The class or ID of the sidenav container.
 * @property {String|Number}  gutter       The space between the sidenav-slider and the sidenav-content.
 * @property {String}         navigation   The class or ID of the navigation container.
 * @property {String}         position     The position of the sidenav-slider. Possible values: left, right
 * @property {String}         type         The type of sidenav in desktop. Possible values: relative, fixed, fixed-push
 * @property {String}         typeMobile   The type of sidenav in mobile. Possible values: relative, fixed, fixed-push
 * @property {String|Object}  url          The URL to fetch the content to inject into .sidebar-body
 * @property {String|Number}  width        The width of the side navigation.
 */
const DEFAULTS = {
	breakpoint: 576,
	content: '.sidenav-content',
	gutter: '12px',
	loadingIndicatorTPL:
		'<div class="loading-animation loading-animation-md"></div>',
	navigation: '.sidenav-menu-slider',
	position: 'left',
	type: 'relative',
	typeMobile: 'relative',
	url: null,
	width: '225px',
};

/**
 * Map from toggler DOM nodes to sidenav instances.
 */
const INSTANCE_MAP = new WeakMap();

/**
 * Utility function that strips off a possible jQuery and Metal
 * component wrappers from a DOM element.
 */
function getElement(element) {

	// Remove jQuery wrapper, if any.

	if (element && element.jquery) {
		if (element.length > 1) {
			throw new Error(
				`getElement(): Expected at most one element, got ${element.length}`
			);
		}
		element = element.get(0);
	}

	// Remove Metal wrapper, if any.

	if (element && !(element instanceof HTMLElement)) {
		element = element.element;
	}

	return element;
}

function getInstance(element) {
	element = getElement(element);

	const instance = INSTANCE_MAP.get(element);

	return instance;
}

/**
 * A list of attributes that can contribute to the "identity" of an element, for
 * the purposes of delegated event handling.
 */
const IDENTITY_ATTRIBUTES = [/^aria-/, /^data-/, /^type$/];

/**
 * Returns a unique selector for the supplied element. Ideally we'd just use
 * the "id" attribute (assigning one if necessary), but the use of Metal
 * components means that any "id" we assign will be blown away on the next state
 * change.
 */
function getUniqueSelector(element) {
	element = getElement(element);

	if (element.id) {
		return `#${element.id}`;
	}

	let ancestorWithId = element.parentNode;

	while (ancestorWithId) {
		if (ancestorWithId.id) {
			break;
		}

		ancestorWithId = ancestorWithId.parentNode;
	}

	const attributes = Array.from(element.attributes)
		.map(({name, value}) => {
			const isIdentifying = IDENTITY_ATTRIBUTES.some((regExp) => {
				return regExp.test(name);
			});

			return isIdentifying ? `[${name}=${JSON.stringify(value)}]` : null;
		})
		.filter(Boolean)
		.sort();

	return [
		ancestorWithId ? `#${ancestorWithId.id} ` : '',
		element.tagName.toLowerCase(),
		...attributes,
	].join('');
}

function dispatchCustomEvent(element, eventName, detail = null) {
	const customEvent = new CustomEvent(eventName, {detail});

	element.dispatchEvent(customEvent);
}

function addClass(element, className) {
	setClasses(element, {
		[className]: true,
	});
}

function removeClass(element, className) {
	setClasses(element, {
		[className]: false,
	});
}

function setClasses(element, classes) {
	element = getElement(element);

	if (element) {

		// One at a time because IE 11: https://caniuse.com/#feat=classlist

		Object.entries(classes).forEach(([className, present]) => {

			// Some callers use multiple space-separated classNames for
			// `openClass`/`data-open-class`. (Looking at you,
			// product-navigation-simulation-web...)

			className.split(/\s+/).forEach((name) => {
				if (present) {
					element.classList.add(name);
				}
				else {
					element.classList.remove(name);
				}
			});
		});
	}
}

function hasClass(element, className) {
	element = getElement(element);

	// Again, product-navigation-simulation-web passes multiple classNames.

	return className.split(/\s+/).every((name) => {
		return element.classList.contains(name);
	});
}

function setStyles(element, styles) {
	element = getElement(element);

	if (element) {
		Object.entries(styles).forEach(([property, value]) => {
			element.style[property] = value;
		});
	}
}

/**
 * For compatibility with jQuery, which will treat "100" as "100px".
 */
function px(dimension) {
	if (typeof dimension === 'number') {
		return dimension + 'px';
	}
	else if (
		typeof dimension === 'string' &&
		dimension.match(/^\s*\d+\s*$/)
	) {
		return dimension.trim() + 'px';
	}
	else {
		return dimension;
	}
}

/**
 * Replacement for jQuery's `offset().left`.
 *
 * @see: https://github.com/jquery/jquery/blob/438b1a3e8a52/src/offset.js#L94-L100
 */
function offsetLeft(element) {
	const elementLeft = element.getBoundingClientRect().left;

	const documentOffset = element.ownerDocument.defaultView.pageOffsetX || 0;

	return elementLeft + documentOffset;
}

/**
 * Keys are event names (eg. "click").
 * Values are objects mapping selectors to EventEmitters.
 */
const eventNamesToSelectors = {};

function handleEvent(eventName, event) {
	Object.keys(eventNamesToSelectors[eventName]).forEach((selector) => {
		let matches = false;
		let target = event.target;

		while (target) {

			// In IE11 SVG elements have no `parentElement`, only a
			// `parentNode`, so we have to search up the DOM using
			// the latter. This in turn requires us to check for the
			// existence of `target.matches` before using it.
			//
			// See: https://stackoverflow.com/a/36270354/2103996

			matches = target.matches && target.matches(selector);

			if (matches) {
				break;
			}

			target = target.parentNode;
		}

		if (matches) {
			const emitter = eventNamesToSelectors[eventName][selector];
			emitter.emit('click', event);
		}
	});
}

let handleWindowResize;

function setupResizeListener() {
	if (!handleWindowResize) {
		handleWindowResize = throttle(() => {
			dispatchCustomEvent(document, 'screenChange.lexicon.sidenav');
		}, 150);

		window.addEventListener('resize', handleWindowResize);
	}
}

/**
 * Creates a delegated event listener for `eventName` events on
 * `elementOrSelector`.
 */
function subscribe(elementOrSelector, eventName, handler) {
	if (elementOrSelector) {

		// Add only one listener per `eventName`.

		if (!eventNamesToSelectors[eventName]) {
			eventNamesToSelectors[eventName] = {};

			document.addEventListener(eventName, (event) =>
				handleEvent(eventName, event)
			);
		}

		const emitters = eventNamesToSelectors[eventName];
		const selector =
			typeof elementOrSelector === 'string'
				? elementOrSelector
				: getUniqueSelector(elementOrSelector);

		if (!emitters[selector]) {
			emitters[selector] = new EventEmitter();
		}

		const emitter = emitters[selector];
		const subscription = emitter.on(eventName, (event) => {
			if (!event.defaultPrevented) {
				handler(event);
			}
		});

		return {
			dispose() {
				subscription.dispose();
			},
		};
	}

	return null;
}

function toInt(str) {
	return parseInt(str, 10) || 0;
}

function SideNavigation(toggler, options) {
	toggler = getElement(toggler);
	this.init(toggler, options);
}

SideNavigation.TRANSITION_DURATION = 500;

SideNavigation.prototype = {
	_bindUI() {
		this._subscribeClickTrigger();

		this._subscribeReducedMotion();

		this._subscribeClickSidenavClose();
	},

	_emit(event) {
		this._emitter.emit(event, this);
	},

	_focusNavigation() {
		const container = document.querySelector(this.options.container);

		if (!container) {
			return;
		}

		const navigation = container.querySelector(this.options.navigation);

		if (!navigation) {
			container.focus();

			return;
		}

		navigation.focus();
	},

	_focusTrigger() {
		const toggler = this.toggler;

		if (
			!toggler ||
			!document.activeElement.classList.contains('sidenav-close')
		) {
			return;
		}

		toggler.focus();
	},

	_getSidenavWidth() {
		const options = this.options;

		const widthOriginal = options.widthOriginal;

		let width = widthOriginal;
		const winWidth = window.innerWidth;

		if (winWidth < widthOriginal + 40) {
			width = winWidth - 40;
		}

		return width;
	},

	_getSimpleSidenavType() {
		const type = this._getType();

		if (this._isDesktop() && type === 'fixed-push') {
			return 'desktop-fixed-push';
		}
		else if (!this._isDesktop() && type === 'fixed-push') {
			return 'mobile-fixed-push';
		}

		return 'fixed';
	},

	_getType() {
		return this._isDesktop() ? this.options.type : this.options.typeMobile;
	},

	_isDesktop() {
		return window.innerWidth >= this.options.breakpoint;
	},

	_isSidenavRight() {
		const options = this.options;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		const isSidenavRight = hasClass(container, 'sidenav-right');

		return isSidenavRight;
	},

	_isSimpleSidenavClosed() {
		const options = this.options;

		const openClass = options.openClass;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		return !hasClass(container, openClass);
	},

	_loadUrl(element, url) {
		const instance = this;

		const sidebar = element.querySelector('.sidebar-body');

		if (!instance._fetchPromise && sidebar) {
			while (sidebar.firstChild) {
				sidebar.removeChild(sidebar.firstChild);
			}

			const loading = document.createElement('div');
			addClass(loading, 'sidenav-loading');
			loading.innerHTML = instance.options.loadingIndicatorTPL;

			sidebar.appendChild(loading);
			instance._fetchPromise = fetch(url);

			instance._fetchPromise
				.then((response) => {
					if (!response.ok) {
						throw new Error(`Failed to fetch ${url}`);
					}

					return response.text();
				})
				.then((text) => {
					text = setCSPNonce(text);

					const range = document.createRange();

					range.selectNode(sidebar);

					// Unlike `.innerHTML`, this will eval scripts.

					const fragment = range.createContextualFragment(text);

					sidebar.removeChild(loading);

					sidebar.appendChild(fragment);

					instance.setHeight();
				})
				.catch((error) => {
					console.error(error);
				});
		}
	},

	_onClosed() {
		const options = this.options;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		if (!this._handleClosed) {
			this._handleClosed = () => {
				const type = this._getType();

				if (type === 'relative' && hasClass(container, 'open')) {
					removeClass(container, 'sidenav-transition');
				}
			};

			document.addEventListener(
				'closed.lexicon.sidenav',
				this._handleClosed
			);
		}
	},

	_onClosedStart() {
		const options = this.options;

		const container = document.querySelector(options.container);
		const content = document.querySelector(options.content);

		if (!container || !content) {
			return;
		}

		if (!this._handleClosedStart) {
			this._handleClosedStart = () => {
				const type = this._getType();

				if (
					type === 'relative' &&
					hasClass(container, 'open') &&
					content.closest('.page-maximized')
				) {
					let contentMargin =
						document.body.scrollWidth -
						content.getBoundingClientRect().right;

					let paddingRight = options.gutter + options.width;

					const contentMaxWidth =
						getComputedStyle(content).maxWidth ||
						getComputedStyle(content).width;

					if (/px$/.test(contentMaxWidth)) {
						contentMargin =
							(document.body.scrollWidth -
								toInt(contentMaxWidth)) /
							2;

						if (contentMargin > options.width) {
							paddingRight = '';
						}
						else if (
							contentMargin > 0 &&
							contentMargin < options.width
						) {
							paddingRight =
								options.gutter + options.width - contentMargin;
						}
					}

					if (!this.isReducedMotion()) {
						addClass(container, 'sidenav-transition');
					}

					setStyles(content, {
						'padding-right': px(paddingRight),
					});
				}
			};

			document.addEventListener(
				'closedStart.lexicon.sidenav',
				this._handleClosedStart
			);
		}
	},

	_onOpen() {
		const options = this.options;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		if (!this._handleOpen) {
			this._handleOpen = () => {
				const type = this._getType();

				if (type === 'relative' && hasClass(container, 'open')) {
					removeClass(container, 'sidenav-transition');
				}
			};

			document.addEventListener('open.lexicon.sidenav', this._handleOpen);
		}
	},

	_onOpenStart() {
		const options = this.options;

		const container = document.querySelector(options.container);
		const content = document.querySelector(options.content);

		if (!container || !content) {
			return;
		}

		if (!this._handleOpenStart) {
			this._handleOpenStart = (event) => {
				const type = this._getType();

				if (
					type === 'relative' &&
					hasClass(container, 'open') &&
					content.closest('.page-maximized')
				) {
					const otherMenu = document.querySelector(
						event.detail.options.container + ' .sidenav-menu'
					);

					if (!otherMenu) {
						return;
					}

					const otherMenuWidth =
						otherMenu.getBoundingClientRect().width;

					const contentMargin =
						document.body.scrollWidth -
						content.getBoundingClientRect().right -
						otherMenuWidth / 2;

					const paddingRight =
						contentMargin > 0
							? options.width + options.gutter - contentMargin
							: options.width + options.gutter;

					if (!this.isReducedMotion()) {
						addClass(container, 'sidenav-transition');
					}

					setStyles(content, {
						'padding-right': px(paddingRight),
					});
				}
			};

			document.addEventListener(
				'openStart.lexicon.sidenav',
				this._handleOpenStart
			);
		}
	},

	_onScreenChange() {
		const options = this.options;

		const container = document.querySelector(options.container);
		const content = document.querySelector(options.content);

		if (!container || !content) {
			return;
		}

		let originalIsDesktop = this._isDesktop();

		if (!this._handleOnScreenChange) {
			this._handleOnScreenChange = () => {
				const type = this._getType();

				if (type === 'relative' && hasClass(container, 'open')) {
					this.setHeight();
					this.setWidth();
				}

				if (this._isDesktop() !== originalIsDesktop) {
					if (type !== 'relative') {
						addClass(container, 'sidenav-fixed');

						content.style.paddingRight = '';
						content.style.minHeight = '';
					}
					else {
						removeClass(container, 'sidenav-fixed');
					}

					originalIsDesktop = this._isDesktop();
				}
			};

			document.addEventListener(
				'screenChange.lexicon.sidenav',
				this._handleOnScreenChange
			);
		}
	},

	_renderNav() {
		const options = this.options;

		const container = document.querySelector(options.container);
		const navigation = container.querySelector(options.navigation);

		if (!container || !navigation) {
			return;
		}

		const menu = navigation.querySelector('.sidenav-menu');

		const closed = hasClass(container, 'closed');
		const sidenavRight = this._isSidenavRight();
		const width = this._getSidenavWidth();

		if (closed) {
			setStyles(menu, {
				width: px(width),
			});

			if (sidenavRight) {
				const positionDirection = options.rtl ? 'left' : 'right';

				setStyles(menu, {
					[positionDirection]: px(width),
				});
			}
		}
		else {
			this.showSidenav();
			this.setHeight();
		}
	},

	_renderUI() {
		const options = this.options;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		const toggler = this.toggler;

		const type = this._getType();

		if (!this.useDataAttribute) {
			setupResizeListener();

			this._onClosedStart();
			this._onClosed();
			this._onOpenStart();
			this._onOpen();
			this._onScreenChange();

			if (!this._isDesktop()) {
				setClasses(container, {
					closed: true,
					open: false,
				});

				setClasses(toggler, {
					active: false,
					open: false,
				});
			}

			if (options.position === 'right') {
				addClass(container, 'sidenav-right');
			}

			if (type !== 'relative') {
				addClass(container, 'sidenav-fixed');
			}

			this._renderNav();
		}

		// Force Reflow for IE11 Browser Bug

		setStyles(container, {
			display: '',
		});
	},

	_subscribeClickSidenavClose() {
		const instance = this;

		const options = instance.options;

		const containerSelector = options.container;

		if (!instance._sidenavCloseSubscription) {
			const closeButtonSelector = `${containerSelector} .sidenav-close`;
			instance._sidenavCloseSubscription = subscribe(
				closeButtonSelector,
				'click',
				function handleSidenavClose(event) {
					event.preventDefault();
					instance.toggle();
				}
			);
		}
	},

	_subscribeClickTrigger() {
		const instance = this;

		if (!instance._togglerSubscription) {
			const toggler = instance.toggler;

			instance._togglerSubscription = subscribe(
				toggler,
				'click',
				function handleTogglerClick(event) {
					instance.toggle();

					event.preventDefault();
				}
			);
		}
	},

	_subscribeReducedMotion() {
		const instance = this;

		import(
			Liferay.FrontendESM.buildURL(
				SCRIPT_URL,
				'accessibility-settings-state-web',
				'index'
			)
		).then(({isReducedMotion}) => {
			instance.isReducedMotion = isReducedMotion;
		});
	},

	_subscribeSidenavTransitionEnd(element, fn) {
		if (this.isReducedMotion()) {
			removeClass(element, 'sidenav-transition');

			fn();
		}
		else {
			setTimeout(() => {
				removeClass(element, 'sidenav-transition');

				fn();
			}, SideNavigation.TRANSITION_DURATION);
		}
	},

	clearHeight() {
		const options = this.options;
		const container = document.querySelector(options.container);

		if (container) {
			const content = container.querySelector(options.content);
			const navigation = container.querySelector(options.navigation);
			const menu = container.querySelector('.sidenav-menu');

			[content, navigation, menu].forEach((element) => {
				setStyles(element, {
					'height': '',
					'min-height': '',
				});
			});
		}
	},

	destroy() {
		const instance = this;

		if (instance._sidenavCloseSubscription) {
			instance._sidenavCloseSubscription.dispose();
			instance._sidenavCloseSubscription = null;
		}

		if (instance._togglerSubscription) {
			instance._togglerSubscription.dispose();
			instance._togglerSubscription = null;
		}

		INSTANCE_MAP.delete(instance.toggler);

		document.removeEventListener(
			'closedStart.lexicon.sidenav',
			instance._handleClosedStart
		);

		document.removeEventListener(
			'closed.lexicon.sidenav',
			instance._handleClosed
		);

		document.removeEventListener(
			'openStart.lexicon.sidenav',
			instance._handleOpenStart
		);

		document.removeEventListener(
			'open.lexicon.sidenav',
			instance._handleOpen
		);

		document.removeEventListener(
			'screenChange.lexicon.sidenav',
			instance._handleOnScreenChange
		);

		if (handleWindowResize) {
			window.removeEventListener('resize', handleWindowResize);

			handleWindowResize = null;
		}
	},

	hide() {
		if (this.useDataAttribute) {
			this.hideSimpleSidenav();
		}
		else {
			this.toggleNavigation(false);
		}
	},

	hideSidenav() {
		const instance = this;
		const options = instance.options;

		const container = document.querySelector(options.container);

		if (container) {
			const content = container.querySelector(options.content);
			const navigation = container.querySelector(options.navigation);
			const menu = navigation.querySelector('.sidenav-menu');

			const sidenavRight = instance._isSidenavRight();

			let positionDirection = options.rtl ? 'right' : 'left';

			if (sidenavRight) {
				positionDirection = options.rtl ? 'left' : 'right';
			}

			const paddingDirection = 'padding-' + positionDirection;

			setStyles(content, {
				[paddingDirection]: '',
				[positionDirection]: '',
			});

			setStyles(navigation, {
				width: '',
			});

			if (sidenavRight) {
				setStyles(menu, {
					[positionDirection]: px(instance._getSidenavWidth()),
				});
			}

			instance._subscribeSidenavTransitionEnd(menu, () => {
				instance._focusTrigger();
			});
		}
	},

	hideSimpleSidenav() {
		const instance = this;

		const options = instance.options;

		const simpleSidenavClosed = instance._isSimpleSidenavClosed();

		if (!simpleSidenavClosed) {
			const container = document.querySelector(options.container);
			const content = document.querySelector(options.content);

			if (!container || !content) {
				return;
			}

			const closedClass = options.closedClass;
			const openClass = options.openClass;

			const toggler = instance.toggler;

			const target =
				toggler.dataset.target || toggler.getAttribute('href');

			instance._emit('closedStart.lexicon.sidenav');

			dispatchCustomEvent(
				document,
				'closedStart.lexicon.sidenav',
				instance
			);

			instance._subscribeSidenavTransitionEnd(content, () => {
				removeClass(container, 'sidenav-transition');
				removeClass(toggler, 'sidenav-transition');

				instance._emit('closed.lexicon.sidenav');

				dispatchCustomEvent(
					document,
					'closed.lexicon.sidenav',
					instance
				);

				instance._focusTrigger();
			});

			const isReducedMotion = instance.isReducedMotion();

			if (hasClass(content, openClass)) {
				setClasses(content, {
					[closedClass]: true,
					[openClass]: false,
					'sidenav-transition': !isReducedMotion,
				});
			}

			if (!isReducedMotion) {
				addClass(container, 'sidenav-transition');
				addClass(toggler, 'sidenav-transition');
			}

			setClasses(container, {
				[closedClass]: true,
				[openClass]: false,
			});

			const nodes = document.querySelectorAll(
				`[data-target="${target}"], [href="${target}"]`
			);

			Array.from(nodes).forEach((node) => {
				setClasses(node, {
					active: false,
					[openClass]: false,
				});
				setClasses(node, {
					active: false,
					[openClass]: false,
				});
			});
		}
	},

	init(toggler, options) {

		/**
		 * For compatibility, we use a data-toggle attribute of
		 * "liferay-sidenav" to distinguish our internal uses from
		 * possible external uses of the old jQuery plugin (which used
		 * "sidenav').
		 */
		const useDataAttribute = toggler.dataset.toggle === 'liferay-sidenav';

		options = {...DEFAULTS, ...options};

		options.breakpoint = toInt(options.breakpoint);
		options.container =
			options.container ||
			toggler.dataset.target ||
			toggler.getAttribute('href');
		options.gutter = toInt(options.gutter);
		options.rtl = document.dir === 'rtl';
		options.width = toInt(options.width);
		options.widthOriginal = options.width;

		// instantiate using data attribute

		if (useDataAttribute) {
			options.closedClass = toggler.dataset.closedClass || 'closed';
			options.content = toggler.dataset.content;
			options.loadingIndicatorTPL =
				toggler.dataset.loadingIndicatorTpl ||
				options.loadingIndicatorTPL;
			options.openClass = toggler.dataset.openClass || 'open';
			options.type = toggler.dataset.type;
			options.typeMobile = toggler.dataset.typeMobile;
			options.url = toggler.dataset.url;
			options.width = '';
		}

		this.toggler = toggler;
		this.options = options;
		this.useDataAttribute = useDataAttribute;

		this._emitter = new EventEmitter();

		this._bindUI();
		this._renderUI();
	},

	on(event, listener) {
		return this._emitter.on(event, listener);
	},

	setHeight() {
		const options = this.options;

		const container = document.querySelector(options.container);

		if (!container) {
			return;
		}

		const type = this._getType();

		if (type !== 'fixed' && type !== 'fixed-push') {
			const content = container.querySelector(options.content);
			const navigation = container.querySelector(options.navigation);
			const menu = container.querySelector('.sidenav-menu');

			const contentHeight = content.closest('.page-maximized')
				? window.innerHeight - menu.getBoundingClientRect().top
				: content.getBoundingClientRect().height;
			const navigationHeight = navigation.getBoundingClientRect().height;

			const tallest = px(Math.max(contentHeight, navigationHeight));

			setStyles(content, {
				'min-height': tallest,
			});

			setStyles(navigation, {
				'height': '100%',
				'min-height': tallest,
			});

			setStyles(menu, {
				'height': '100%',
				'min-height': tallest,
			});
		}
	},

	setWidth() {
		const options = this.options;

		const container = document.querySelector(options.container);
		const content = container.querySelector(options.content);
		const navigation = container.querySelector(options.navigation);

		if (!container || !content || !navigation) {
			return;
		}

		const menu = navigation.querySelector('.sidenav-menu');

		const sidenavRight = this._isSidenavRight();
		const width = this._getSidenavWidth();

		const offset = width + options.gutter;

		const url = options.url;

		if (url) {
			this._loadUrl(menu, url);
		}

		setStyles(navigation, {
			width: px(width),
		});

		setStyles(menu, {
			width: px(width),
		});

		let positionDirection = options.rtl ? 'right' : 'left';

		if (sidenavRight) {
			positionDirection = options.rtl ? 'left' : 'right';
		}

		const paddingDirection = 'padding-' + positionDirection;

		const pushContentCssProperty = this._isDesktop()
			? paddingDirection
			: positionDirection;
		const type = this._getType();

		if (type !== 'relative') {
			addClass(container, 'sidenav-fixed');
		}

		if (type !== 'fixed') {
			let navigationStartX = hasClass(container, 'open')
				? offsetLeft(navigation) - options.gutter
				: offsetLeft(navigation) - offset;

			const contentStartX = offsetLeft(content);
			const contentWidth = toInt(getComputedStyle(content).width);

			let padding = '';

			if (
				(options.rtl && sidenavRight) ||
				(!options.rtl && options.position === 'left')
			) {
				navigationStartX = offsetLeft(navigation) + offset;

				if (navigationStartX > contentStartX) {
					padding = navigationStartX - contentStartX;
				}
			}
			else if (
				(options.rtl && options.position === 'left') ||
				(!options.rtl && sidenavRight)
			) {
				if (navigationStartX < contentStartX + contentWidth) {
					padding = contentStartX + contentWidth - navigationStartX;

					if (padding >= offset) {
						padding = offset;
					}
				}
			}

			setStyles(content, {
				[pushContentCssProperty]: px(padding),
			});
		}
	},

	show() {
		if (this.useDataAttribute) {
			this.showSimpleSidenav();
		}
		else {
			this.toggleNavigation(true);
		}
	},

	showSidenav() {
		const instance = this;
		const options = instance.options;

		const container = document.querySelector(options.container);
		const navigation = container.querySelector(options.navigation);

		if (!container || !navigation) {
			return;
		}

		const menu = navigation.querySelector('.sidenav-menu');

		const url = options.url;

		if (url) {
			instance._loadUrl(menu, url);
		}

		instance.setWidth();

		instance._subscribeSidenavTransitionEnd(menu, () => {
			instance._focusNavigation();
		});
	},

	showSimpleSidenav() {
		const instance = this;

		const options = instance.options;

		const simpleSidenavClosed = instance._isSimpleSidenavClosed();

		if (simpleSidenavClosed) {
			const container = document.querySelector(options.container);
			const content = document.querySelector(options.content);

			if (!container || !content) {
				return;
			}

			const closedClass = options.closedClass;
			const openClass = options.openClass;

			const toggler = instance.toggler;

			const url = toggler.dataset.url;

			if (url) {
				instance._loadUrl(container, url);
			}

			instance._emit('openStart.lexicon.sidenav');

			dispatchCustomEvent(
				document,
				'openStart.lexicon.sidenav',
				instance
			);

			const isReducedMotion = instance.isReducedMotion();

			setClasses(content, {
				[closedClass]: false,
				[openClass]: true,
				'sidenav-transition': !isReducedMotion,
			});
			setClasses(container, {
				[closedClass]: false,
				[openClass]: true,
				'sidenav-transition': !isReducedMotion,
			});
			setClasses(toggler, {
				'active': true,
				[openClass]: true,
				'sidenav-transition': !isReducedMotion,
			});

			instance._subscribeSidenavTransitionEnd(content, () => {
				if (!isReducedMotion) {
					removeClass(container, 'sidenav-transition');
					removeClass(toggler, 'sidenav-transition');
				}

				instance._emit('open.lexicon.sidenav');

				dispatchCustomEvent(document, 'open.lexicon.sidenav', instance);

				this._focusNavigation();
			});
		}
	},

	toggle() {
		if (this.useDataAttribute) {
			this.toggleSimpleSidenav();
		}
		else {
			this.toggleNavigation();
		}
	},

	toggleNavigation(force) {
		const instance = this;

		const options = instance.options;
		const type = instance._getType();

		const container = document.querySelector(options.container);
		const menu = container.querySelector('.sidenav-menu');

		if (!container || !menu) {
			return;
		}

		const toggler = instance.toggler;

		const width = options.width;

		const closed =
			typeof force === 'boolean' ? force : hasClass(container, 'closed');
		const sidenavRight = instance._isSidenavRight();

		if (closed) {
			instance._emit('openStart.lexicon.sidenav');

			dispatchCustomEvent(
				document,
				'openStart.lexicon.sidenav',
				instance
			);
		}
		else {
			instance._emit('closedStart.lexicon.sidenav');

			dispatchCustomEvent(
				document,
				'closedStart.lexicon.sidenav',
				instance
			);
		}

		instance._subscribeSidenavTransitionEnd(container, () => {
			const menu = container.querySelector('.sidenav-menu');

			if (hasClass(container, 'closed')) {
				instance.clearHeight();

				setClasses(toggler, {
					'open': false,
					'sidenav-transition': false,
				});

				instance._emit('closed.lexicon.sidenav');

				dispatchCustomEvent(
					document,
					'closed.lexicon.sidenav',
					instance
				);
			}
			else {
				setClasses(toggler, {
					'open': true,
					'sidenav-transition': false,
				});

				instance._emit('open.lexicon.sidenav');

				dispatchCustomEvent(document, 'open.lexicon.sidenav', instance);
			}

			if (!instance._isDesktop()) {

				// ios 8 fixed element disappears when trying to scroll

				menu.focus();
			}
		});

		if (closed) {
			if (type === 'relative') {
				instance.setHeight();
			}

			setStyles(menu, {
				width: px(width),
			});

			const positionDirection = options.rtl ? 'left' : 'right';

			if (sidenavRight) {
				setStyles(menu, {
					[positionDirection]: '',
				});
			}
		}

		if (!instance.isReducedMotion()) {
			addClass(container, 'sidenav-transition');
			addClass(toggler, 'sidenav-transition');
		}

		if (closed) {
			instance.showSidenav();
		}
		else {
			instance.hideSidenav();
		}

		setClasses(container, {
			closed: !closed,
			open: closed,
		});

		setClasses(toggler, {
			active: closed,
			open: closed,
		});
	},

	toggleSimpleSidenav() {
		const simpleSidenavClosed = this._isSimpleSidenavClosed();

		if (simpleSidenavClosed) {
			this.showSimpleSidenav();
		}
		else {
			this.hideSimpleSidenav();
		}
	},

	visible() {
		let closed;

		if (this.useDataAttribute) {
			closed = this._isSimpleSidenavClosed();
		}
		else {
			const container = document.querySelector(this.options.container);

			if (!container) {
				return;
			}

			closed = hasClass(container, 'sidenav-transition')
				? !hasClass(container, 'closed')
				: hasClass(container, 'closed');
		}

		return !closed;
	},
};

SideNavigation.destroy = function destroy(element) {
	const instance = getInstance(element);

	if (instance) {
		instance.destroy();
	}
};

SideNavigation.hide = function hide(element) {
	const instance = getInstance(element);

	if (instance) {
		instance.hide();
	}
};

SideNavigation.initialize = function initialize(toggler, options = {}) {
	toggler = getElement(toggler);

	let instance = INSTANCE_MAP.get(toggler);

	if (!instance) {
		instance = new SideNavigation(toggler, options);

		INSTANCE_MAP.set(toggler, instance);
	}

	return instance;
};

SideNavigation.instance = getInstance;

function onReady() {
	const togglers = document.querySelectorAll(
		'[data-toggle="liferay-sidenav"]'
	);

	Array.from(togglers).forEach(SideNavigation.initialize);
}

if (document.readyState !== 'loading') {

	// readyState is "interactive" or "complete".

	onReady();
}
else {
	document.addEventListener('DOMContentLoaded', () => {
		onReady();
	});
}

export default SideNavigation;
