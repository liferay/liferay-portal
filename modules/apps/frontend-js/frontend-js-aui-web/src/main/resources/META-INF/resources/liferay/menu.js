/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-menu',
	(A) => {
		const Util = Liferay.Util;

		const ARIA_ATTR_ROLE = 'role';

		const ATTR_CLASS_NAME = 'className';

		const AUTO = 'auto';

		const CSS_BTN_PRIMARY = 'btn-primary';

		const CSS_EXTENDED = 'lfr-extended';

		const CSS_OPEN = 'open';

		const CSS_PORTLET = '.portlet';

		const DEFAULT_ALIGN_POINTS = ['tl', 'bl'];

		const EVENT_CLICK = 'click';

		const EVENT_KEYDOWN = 'keydown';

		const PARENT_NODE = 'parentNode';

		const STR_BOTTOM = 'b';

		const STR_LEFT = 'l';

		const STR_LTR = 'ltr';

		const STR_RIGHT = 'r';

		const STR_RTL = 'rtl';

		const STR_TOP = 't';

		const MAP_ALIGN_DOWN = {
			downleft: ['tr', 'br'],
			downright: DEFAULT_ALIGN_POINTS,
		};

		const MAP_ALIGN_HORIZONTAL_OVERLAY = {
			left: STR_RIGHT,
			right: STR_LEFT,
		};

		const MAP_ALIGN_HORIZONTAL_OVERLAY_RTL = {
			left: STR_LEFT,
			right: STR_RIGHT,
		};

		const MAP_ALIGN_HORIZONTAL_TRIGGER = {
			left: STR_LEFT,
			right: STR_RIGHT,
		};

		const MAP_ALIGN_HORIZONTAL_TRIGGER_RTL = {
			left: STR_RIGHT,
			right: STR_LEFT,
		};

		const MAP_ALIGN_VERTICAL_OVERLAY = {
			down: STR_TOP,
			up: STR_BOTTOM,
		};

		const MAP_ALIGN_VERTICAL_TRIGGER = {
			down: STR_BOTTOM,
			up: STR_TOP,
		};

		const MAP_LIVE_SEARCH = {};

		const REGEX_DIRECTION =
			/\bdirection-(downleft|downright|down|left|right|up)\b/;

		const REGEX_MAX_DISPLAY_ITEMS = /max-display-items-(\d+)/;

		const SELECTOR_ANCHOR = 'a';

		const SELECTOR_LIST_ITEM = 'li';

		const SELECTOR_SEARCH_CONTAINER = '.lfr-menu-list-search-container';

		const TPL_MENU = '<div class="open" />';

		const Menu = function () {
			const instance = this;

			instance._handles = [];

			if (!Menu._INSTANCE) {
				Menu._INSTANCE = instance;
			}
		};

		Menu.prototype = {
			_closeActiveMenu() {
				const instance = this;

				const menu = instance._activeMenu;

				if (menu) {
					const handles = instance._handles;

					A.Array.invoke(handles, 'detach');

					handles.length = 0;

					const trigger = instance._activeTrigger;

					const overlay = instance._overlayMap.get(
						trigger.generateID()
					);

					if (overlay) {
						overlay.destroy();

						instance._overlayMap.clear();
					}

					instance._activeMenu = null;
					instance._activeTrigger = null;

					trigger.attr({
						'aria-expanded': false,
					});

					if (trigger.hasClass(CSS_EXTENDED)) {
						trigger.removeClass(CSS_BTN_PRIMARY);
					}
					else {
						trigger.get(PARENT_NODE).removeClass(CSS_OPEN);

						const portlet = trigger.ancestor(CSS_PORTLET);

						if (portlet) {
							portlet.removeClass(CSS_OPEN);
						}
					}
				}
			},

			_getAlignPoints: A.cached((cssClass) => {
				let alignPoints = DEFAULT_ALIGN_POINTS;

				let defaultOverlayHorizontalAlign = STR_RIGHT;

				let defaultTriggerHorizontalAlign = STR_LEFT;

				let mapAlignHorizontalOverlay = MAP_ALIGN_HORIZONTAL_OVERLAY;

				let mapAlignHorizontalTrigger = MAP_ALIGN_HORIZONTAL_TRIGGER;

				const langDir =
					Liferay.Language.direction[themeDisplay.getLanguageId()] ||
					STR_LTR;

				if (langDir === STR_RTL) {
					defaultOverlayHorizontalAlign = STR_LEFT;
					defaultTriggerHorizontalAlign = STR_RIGHT;

					mapAlignHorizontalOverlay =
						MAP_ALIGN_HORIZONTAL_OVERLAY_RTL;
					mapAlignHorizontalTrigger =
						MAP_ALIGN_HORIZONTAL_TRIGGER_RTL;
				}

				if (cssClass.indexOf(AUTO) === -1) {
					const directionMatch = cssClass.match(REGEX_DIRECTION);

					const direction =
						(directionMatch && directionMatch[1]) || AUTO;

					if (direction.startsWith('down')) {
						alignPoints =
							MAP_ALIGN_DOWN[direction] ||
							MAP_ALIGN_DOWN.downright;
					}
					else {
						const overlayHorizontal =
							mapAlignHorizontalOverlay[direction] ||
							defaultOverlayHorizontalAlign;
						const overlayVertical =
							MAP_ALIGN_VERTICAL_OVERLAY[direction] || STR_TOP;

						const triggerHorizontal =
							mapAlignHorizontalTrigger[direction] ||
							defaultTriggerHorizontalAlign;
						const triggerVertical =
							MAP_ALIGN_VERTICAL_TRIGGER[direction] || STR_TOP;

						alignPoints = [
							overlayVertical + overlayHorizontal,
							triggerVertical + triggerHorizontal,
						];
					}
				}

				return alignPoints;
			}),

			_getMenu(trigger) {
				const instance = this;

				if (!instance._overlayMap) {
					instance._overlayMap = new Map();
				}

				instance._trigger = trigger;

				let overlay = instance._overlayMap.get(trigger.generateID());

				if (!overlay) {
					const MenuOverlay = A.Component.create({
						AUGMENTS: [
							A.WidgetCssClass,
							A.WidgetPosition,
							A.WidgetStdMod,
							A.WidgetModality,
							A.WidgetPositionAlign,
							A.WidgetPositionConstrain,
							A.WidgetStack,
						],

						CSS_PREFIX: 'overlay',

						EXTENDS: A.Widget,

						NAME: 'overlay',
					});

					overlay = new MenuOverlay({
						align: {
							node: trigger,
							points: DEFAULT_ALIGN_POINTS,
						},
						constrain: true,
						hideClass: false,
						modal: Util.isPhone() || Util.isTablet(),
						preventOverlap: true,
						zIndex: Liferay.zIndex.MENU,
					}).render();

					Liferay.once('beforeScreenFlip', () => {
						overlay.destroy();

						instance._overlayMap.clear();
					});

					instance._overlayMap.set(trigger.generateID(), overlay);
				}
				else {
					overlay.set('align.node', trigger);
				}

				let listContainer = trigger.getData('menuListContainer');
				let menu = trigger.getData('menu');
				let menuHeight = trigger.getData('menuHeight');

				const liveSearch = menu && MAP_LIVE_SEARCH[menu.guid()];

				if (liveSearch) {
					liveSearch.reset();
				}

				let listItems;

				if (!menu || !listContainer) {
					listContainer = trigger.next('ul');

					listItems = listContainer.all(SELECTOR_LIST_ITEM);

					menu = A.Node.create(TPL_MENU);

					listContainer.placeBefore(menu);

					listItems.last().addClass('last');

					menu.append(listContainer);

					trigger.setData('menuListContainer', listContainer);
					trigger.setData('menu', menu);

					instance._setARIARoles(trigger, menu, listContainer);

					if (trigger.hasClass('select')) {
						listContainer.delegate(
							'click',
							(event) => {
								const selectedListItem = event.currentTarget;

								const selectedListItemIcon =
									selectedListItem.one('i');

								const triggerIcon = trigger.one('i');

								if (selectedListItemIcon && triggerIcon) {
									const selectedListItemIconClass =
										selectedListItemIcon.attr('class');

									triggerIcon.attr(
										'class',
										selectedListItemIconClass
									);
								}

								const selectedListItemMessage =
									selectedListItem.one('.lfr-icon-menu-text');

								const triggerMessage = trigger.one(
									'.lfr-icon-menu-text'
								);

								if (selectedListItemMessage && triggerMessage) {
									triggerMessage.setContent(
										selectedListItemMessage.text()
									);
								}
							},
							SELECTOR_LIST_ITEM
						);
					}
				}

				overlay.setStdModContent(A.WidgetStdMod.BODY, menu);

				if (!menuHeight) {
					menuHeight = instance._getMenuHeight(
						trigger,
						menu,
						listItems || listContainer.all(SELECTOR_LIST_ITEM)
					);

					trigger.setData('menuHeight', menuHeight);

					if (menuHeight !== AUTO) {
						listContainer.setStyle('maxHeight', menuHeight);
					}
				}

				instance._getFocusManager();

				return menu;
			},

			_getMenuHeight(trigger, menu, listItems) {
				const instance = this;

				const cssClass = trigger.attr(ATTR_CLASS_NAME);

				let height = AUTO;

				if (cssClass.indexOf('lfr-menu-expanded') === -1) {
					const params = REGEX_MAX_DISPLAY_ITEMS.exec(cssClass);

					const maxDisplayItems = params && parseInt(params[1], 10);

					if (maxDisplayItems && listItems.size() > maxDisplayItems) {
						instance._getLiveSearch(
							trigger,
							trigger.getData('menu')
						);

						height = 0;

						const heights = listItems
							.slice(0, maxDisplayItems)
							.get('offsetHeight');

						for (let i = heights.length - 1; i >= 0; i--) {
							height += heights[i];
						}
					}
				}

				return height;
			},

			_positionActiveMenu() {
				const instance = this;

				const menu = instance._activeMenu;
				const trigger = instance._activeTrigger;

				if (menu) {
					const cssClass = trigger.attr(ATTR_CLASS_NAME);

					const overlay = instance._overlayMap.get(
						trigger.generateID()
					);

					const align = overlay.get('align');

					const listNode = menu.one('ul');

					overlay.show();

					const listNodeHeight = listNode.get('offsetHeight');
					const listNodeWidth = listNode.get('offsetWidth');

					align.points = instance._getAlignPoints(cssClass);

					menu.addClass('lfr-icon-menu-open');

					overlay.setAttrs({
						align,
						centered: false,
						height: listNodeHeight,
						modal: Util.isPhone() || Util.isTablet(),
						width: listNodeWidth,
					});

					if (!Util.isPhone() && !Util.isTablet()) {
						const focusManager = overlay.bodyNode.focusManager;

						if (focusManager) {
							focusManager.focus(0);
						}
					}

					if (cssClass.indexOf(CSS_EXTENDED) > -1) {
						trigger.addClass(CSS_BTN_PRIMARY);
					}
					else {
						trigger.get(PARENT_NODE).addClass(CSS_OPEN);

						const portlet = trigger.ancestor(CSS_PORTLET);

						if (portlet) {
							portlet.addClass(CSS_OPEN);
						}
					}
				}
			},

			_setARIARoles(trigger, menu) {
				const links = menu
					.all(SELECTOR_ANCHOR)
					.filter(':not([aria-haspopup="dialog"]');

				const searchContainer = menu.one(SELECTOR_SEARCH_CONTAINER);

				const listNode = menu.one('ul');

				let ariaLinksAttr = 'menuitem';
				let ariaListNodeAttr = 'menu';

				if (searchContainer) {
					ariaLinksAttr = 'option';
					ariaListNodeAttr = 'listbox';
				}

				if (links.size() > 0) {
					listNode.setAttribute(ARIA_ATTR_ROLE, ariaListNodeAttr);
					links.set(ARIA_ATTR_ROLE, ariaLinksAttr);
				}

				trigger.attr({
					'aria-haspopup': true,
				});

				if (!trigger.hasClass('input-localized-trigger')) {
					listNode.setAttribute('aria-labelledby', trigger.guid());
				}
			},
		};

		Menu.handleFocus = function (id) {
			const node = A.one(id);

			if (node) {
				node.delegate(
					'mouseenter',
					A.rbind(Menu._targetLink, node, 'focus'),
					SELECTOR_LIST_ITEM
				);
				node.delegate(
					'mouseleave',
					A.rbind(Menu._targetLink, node, 'blur'),
					SELECTOR_LIST_ITEM
				);
			}
		};

		const buffer = [];

		Menu.register = function (id) {
			const menuNode = document.getElementById(id);

			if (menuNode) {
				if (!Menu._INSTANCE) {
					new Menu();
				}

				buffer.push(menuNode);

				Menu._registerTask();
			}
		};

		Menu._registerTask = A.debounce(() => {
			if (buffer.length) {
				const nodes = A.all(buffer);

				nodes.on(
					[EVENT_CLICK, EVENT_KEYDOWN],
					A.bind('_registerMenu', Menu)
				);

				buffer.length = 0;
			}
		}, 100);

		Menu._targetLink = function (event, action) {
			const anchor = event.currentTarget.one(SELECTOR_ANCHOR);

			if (anchor) {
				anchor[action]();
			}
		};

		Liferay.provide(
			Menu,
			'_getFocusManager',
			() => {
				const menuInstance = Menu._INSTANCE;

				const trigger = menuInstance._trigger;

				if (!menuInstance._focusManagerMap) {
					menuInstance._focusManagerMap = new Map();
				}

				let focusManager = menuInstance._focusManagerMap.get(
					trigger.generateID()
				);

				if (!focusManager) {
					const bodyNode = menuInstance._overlayMap.get(
						trigger.generateID()
					).bodyNode;

					bodyNode.plug(A.Plugin.NodeFocusManager, {
						circular: true,
						descendants: 'li:not(.hide) a,input',
						focusClass: 'focus',
						keys: {
							next: 'down:40',
							previous: 'down:38',
						},
					});

					bodyNode.on(
						'key',
						() => {
							const activeTrigger = menuInstance._activeTrigger;

							if (activeTrigger) {
								menuInstance._closeActiveMenu();

								activeTrigger.focus();
							}
						},
						'down:27,9'
					);

					focusManager = bodyNode.focusManager;

					bodyNode.delegate(
						'mouseenter',
						(event) => {
							if (focusManager.get('focused')) {
								focusManager.focus(
									event.currentTarget.one(SELECTOR_ANCHOR)
								);
							}
						},
						SELECTOR_LIST_ITEM
					);

					focusManager.after('activeDescendantChange', (event) => {
						const descendants = focusManager.get('descendants');

						const selectedItem = descendants.item(event.newVal);

						if (selectedItem) {
							const overlayList = bodyNode.one('ul');

							if (overlayList) {
								overlayList.setAttribute(
									'aria-activedescendant',
									selectedItem.guid()
								);
							}
						}
					});

					menuInstance._focusManagerMap.set(
						trigger.generateID(),
						focusManager
					);

					Liferay.once('beforeScreenFlip', () => {
						menuInstance._focusManagerMap = null;

						menuInstance._trigger = null;
					});
				}

				focusManager.refresh();
			},
			['node-focusmanager'],
			true
		);

		Liferay.provide(
			Menu,
			'_getLiveSearch',
			(_trigger, menu) => {
				const id = menu.guid();

				let liveSearch = MAP_LIVE_SEARCH[id];

				if (!liveSearch) {
					const listNode = menu.one('ul');

					const results = [];

					listNode.all('li').each((node) => {
						results.push({
							name: node.one('.taglib-text-icon').text().trim(),
							node,
						});
					});

					liveSearch = new Liferay.MenuFilter({
						content: listNode,
						menu: Menu._INSTANCE,
						minQueryLength: 0,
						queryDelay: 0,
						resultFilters: 'phraseMatch',
						resultTextLocator: 'name',
						source: results,
					});

					liveSearch.get('inputNode').swallowEvent('click');

					MAP_LIVE_SEARCH[id] = liveSearch;
				}
			},
			['liferay-menu-filter'],
			true
		);

		Liferay.provide(
			Menu,
			'_registerMenu',
			(event) => {
				const key = event.key || event.keyCode;

				if (
					event.type === EVENT_KEYDOWN &&
					key !== A.Event.KeyMap.SPACE
				) {
					return;
				}

				const menuInstance = Menu._INSTANCE;

				const handles = menuInstance._handles;

				const trigger = event.currentTarget;

				const activeTrigger = menuInstance._activeTrigger;

				if (activeTrigger) {
					if (activeTrigger !== trigger) {
						activeTrigger.removeClass(CSS_BTN_PRIMARY);

						activeTrigger.get(PARENT_NODE).removeClass(CSS_OPEN);

						const portlet = activeTrigger.ancestor(CSS_PORTLET);

						if (portlet) {
							portlet.removeClass(CSS_OPEN);
						}
						menuInstance._closeActiveMenu();
					}
					else {
						menuInstance._closeActiveMenu();

						return;
					}
				}

				if (!trigger.hasClass('disabled')) {
					const menu = menuInstance._getMenu(trigger);

					menuInstance._activeMenu = menu;
					menuInstance._activeTrigger = trigger;

					trigger.attr({
						'aria-expanded': true,
					});

					if (!handles.length) {
						const listContainer =
							trigger.getData('menuListContainer');

						A.Event.defineOutside('touchend');

						handles.push(
							A.getWin().on(
								'resize',
								A.debounce(
									menuInstance._positionActiveMenu,
									200,
									menuInstance
								)
							),
							A.getDoc().on(
								EVENT_CLICK,
								menuInstance._closeActiveMenu,
								menuInstance
							),
							listContainer.on(
								'touchendoutside',
								(event) => {
									event.preventDefault();

									menuInstance._closeActiveMenu();
								},
								menuInstance
							),
							Liferay.on('dropdownShow', (event) => {
								if (event.src !== 'LiferayMenu') {
									menuInstance._closeActiveMenu();
								}
							})
						);

						const DDM = A.DD && A.DD.DDM;

						if (DDM) {
							handles.push(
								DDM.on(
									'ddm:start',
									menuInstance._closeActiveMenu,
									menuInstance
								)
							);
						}
					}

					menuInstance._positionActiveMenu();

					Liferay.fire('dropdownShow', {
						src: 'LiferayMenu',
					});

					event.halt();
				}
			},
			[
				'aui-widget-cssclass',
				'event-outside',
				'event-touch',
				'widget',
				'widget-modality',
				'widget-position',
				'widget-position-align',
				'widget-position-constrain',
				'widget-stack',
				'widget-stdmod',
			]
		);

		Liferay.Menu = Menu;
	},
	'',
	{
		requires: ['aui-component', 'array-invoke', 'aui-debounce', 'aui-node'],
	}
);
