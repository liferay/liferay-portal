/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare module Liferay {
	namespace Address {

		/* Returns a list of countries */
		export function getCountries(callback?: () => void): Promise<any>;

		/* Returns a list of regions by country */
		export function getRegions(
			callback?: () => void,
			selectKey?: string
		): Promise<any>;
	}

	namespace Browser {
		export function isChrome(): boolean;

		export function isFirefox(): boolean;

		export function isMobile(): boolean;

		export function isMac(): boolean;
	}

	namespace CustomDialogs {
		export const enabled: boolean;
	}

	namespace DOMTaskRunner {
		export function addTask(task: object): void;

		export function addTaskState(state: object): void;

		export function reset(): void;

		export function runTasks(node: any): void;
	}

	namespace Language {
		type Direction = 'ltr' | 'rtl';

		type Locale =
			| 'ar_SA'
			| 'ca_ES'
			| 'de_DE'
			| 'en_US'
			| 'es_ES'
			| 'fi_FI'
			| 'fr_FR'
			| 'hu_HU'
			| 'nl_NL'
			| 'ja_JP'
			| 'pt_BR'
			| 'sv_SE'
			| 'zh_CN'
			| 'zh_Hans_CN'
			| 'zh_Hant_TW'
			| 'zh_TW';

		type FullyLocalizedValue<T> = {[key in Locale]: T};
		type LocalizedValue<T> = Partial<FullyLocalizedValue<T>>;

		export const available: FullyLocalizedValue<string>;

		export const direction: LocalizedValue<Direction>;

		export function get(key: string): string;
	}

	namespace Loader {
		export function require(...args: any[]): void;
	}

	namespace Portal {
		namespace Tabs {

			/**
			 * Prepares and fires the an event that will show a tab
			 */
			export function show(
				id: string,
				names: string[],
				namespace: string,
				callback?: () => void
			): void;
		}
	}

	namespace Portlet {
		export function add(options: object): void;

		export function addHTML(options: object): void;

		export function close(
			portlet: any,
			skipConfirm: boolean,
			options?: object
		): void;

		export function destroy(portlet: any, options?: object): void;

		export function onLoad(options: object): void;

		/**
		 * Minimizes portlet
		 */
		export function minimize(
			portletSelector: string,
			trigger: HTMLElement,
			options?: Object
		): void;

		export function refresh(
			portlet: any,
			data?: object,
			mergeWithRefreshURLData?: boolean
		): void;

		export function registerStatic(portletId: any): void;
	}

	namespace PortletKeys {
		export const DOCUMENT_LIBRARY: string;
		export const DYNAMIC_DATA_MAPPING: string;
		export const INSTANCE_SETTINGS: string;
		export const ITEM_SELECTOR: string;
	}

	namespace PropsValues {
		export const UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE: number;
	}

	namespace Service {
		export function bind(...args: any[]): void;

		export function del(httpMethodName: string): void;

		export function get(httpMethodName: string): void;

		export function invoke(
			payload: Object,
			ioConfig: Object
		): Promise<void>;

		export function post(httpMethodName: string): void;

		export function put(httpMethodName: string): void;

		export function parseInvokeArgs(payload: Object, ...args: any[]): void;

		export function parseIOConfig(payload: Object, ...args: any[]): void;

		export function parseIOFormConfig(
			ioConfig: Object,
			form: HTMLFormElement,
			...args: any[]
		): void;

		export function parseStringPayload(...args: any[]): Object;

		export function update(httpMethodName: string): void;
	}

	namespace State {
		type Primitive =
			| bigint
			| boolean
			| null
			| number
			| string
			| symbol
			| undefined;

		type Builtin = Date | Error | Function | Primitive | RegExp;

		/**
		 * A local "DeepReadonly" until TypeScript bundles one out of the box.
		 *
		 * See: https://github.com/microsoft/TypeScript/issues/13923
		 */
		export type Immutable<T> = T extends Builtin
			? T
			: T extends Map<infer K, infer V>
				? ReadonlyMap<Immutable<K>, Immutable<V>>
				: T extends ReadonlyMap<infer K, infer V>
					? ReadonlyMap<Immutable<K>, Immutable<V>>
					: T extends WeakMap<infer K, infer V>
						? WeakMap<Immutable<K>, Immutable<V>>
						: T extends Set<infer U>
							? ReadonlySet<Immutable<U>>
							: T extends ReadonlySet<infer U>
								? ReadonlySet<Immutable<U>>
								: T extends WeakSet<infer U>
									? WeakSet<Immutable<U>>
									: T extends Promise<infer U>
										? Promise<Immutable<U>>
										: T extends {}
											? {
													readonly [K in keyof T]: Immutable<
														T[K]
													>;
												}
											: Readonly<T>;

		const ATOM = 'Liferay.State.ATOM';
		const SELECTOR = 'Liferay.State.SELECTOR';

		type Atom<T> = Immutable<{
			[ATOM]: true;
			default: T;
			key: string;
		}>;

		interface Getter {
			<T>(atomOrSelector: Atom<T> | Selector<T>): Immutable<T>;
		}

		type Selector<T> = Immutable<{
			[SELECTOR]: true;
			deriveValue: (get: Getter) => T;
			key: string;
		}>;

		export function atom<T>(key: string, value: T): Atom<T>;

		export function read<T>(
			atomOrSelector: Atom<T> | Selector<T>
		): Immutable<T>;

		export function readAtom<T>(atom: Atom<T>): Immutable<T>;

		export function readSelector<T>(selector: Selector<T>): Immutable<T>;

		export function selector<T>(
			key: string,
			deriveValue: (get: Getter) => T
		): Selector<T>;

		export function subscribe<T extends any>(
			atomOrSelector: Atom<T> | Selector<T>,
			callback: (value: Immutable<T>) => void
		): {dispose: () => void};

		export function write<T>(
			atomOrSelector: Atom<T> | Selector<T>,
			value: T
		): void;

		export function writeAtom<T>(atom: Atom<T>, value: T): void;
	}

	namespace ThemeDisplay {
		export function getBCP47LanguageId(): string;
		export function getCompanyId(): string;
		export function getDefaultLanguageId(): Language.Locale;
		export function getLanguageId(): Language.Locale;
		export function getLayoutRelativeControlPanelURL(): string;
		export function getPathContext(): string;
		export function getPathFriendlyURLPublic(): string;
		export function getPathMain(): string;
		export function getPathThemeImages(): string;
		export function getPathThemeSpritemap(): string;
		export function getPlid(): number;
		export function getPortalURL(): string;
		export function getRealUserId(): string;
		export function getScopeGroupId(): number;
		export function getSiteGroupId(): number;
		export function getTimeZone(): string;
		export function getUserId(): string;
		export function isControlPanel(): boolean;
		export function isImpersonated(): boolean;
		export function isSignedIn(): boolean;
	}

	namespace Util {
		namespace Cookie {

			/**
			 * Object with cookie consent types as keys, for use in {@link Cookie.set}
			 */
			export const TYPES: {[key: string]: TYPE_VALUES};

			export type TYPE_VALUES =
				| 'CONSENT_TYPE_FUNCTIONAL'
				| 'CONSENT_TYPE_NECESSARY'
				| 'CONSENT_TYPE_PERFORMANCE'
				| 'CONSENT_TYPE_PERSONALIZATION';

			/* Returns the stored value of a cookie, undefined if not present */
			export function get(
				name: string,
				type: TYPE_VALUES
			): string | undefined;

			/* Sets a cookie of a specific type if user has consented */
			export function set(
				name: string,
				value: string,
				type: TYPE_VALUES,
				options?: {
					'domain'?: string;
					'expires'?: string;
					'max-age'?: string;
					'path'?: string;
					'samesite'?: string;
					'secure'?: boolean;
				}
			): boolean;

			/* Removes a cookie by expiring it */
			export function remove(name: string): void;
		}

		namespace PortletURL {

			/* Returns an action portlet URL in form of a URL object by setting the lifecycle parameter */
			export function createActionURL(
				basePortletURL: string,
				parameters?: Object
			): URL;

			/* Returns a portlet URL in form of a URL Object */
			export function createPortletURL(
				basePortletURL: string,
				parameters?: Object
			): URL;

			/* Returns a render portlet URL in form of a URL object by setting the lifecycle parameter */
			export function createRenderURL(
				basePortletURL: string,
				parameters?: Object
			): URL;

			/* Returns a resource portlet URL in form of a URL object by setting the lifecycle parameter */
			export function createResourceURL(
				basePortletURL: string,
				parameters?: Object
			): URL;
		}

		namespace Session {

			/**
			 * Gets the Store utility fetch value for given key
			 */
			export function get(
				key: string,
				options?: {useHttpSession: boolean; [key: string]: any}
			): Promise<any>;

			/**
			 * Sets the Store utility fetch value
			 */
			export function set(
				key: string,
				value: Object | string,
				options?: {useHttpSession: boolean; [key: string]: any}
			): Promise<any>;
		}

		namespace LocalStorage {

			/* Removes all entries in localStorage */
			export function clear(): void;

			/* Object with consent types as keys and corresponding cookie names as values */
			export const TYPES: {[key: string]: TYPE_VALUES};

			export type TYPE_VALUES =
				| 'CONSENT_TYPE_FUNCTIONAL'
				| 'CONSENT_TYPE_NECESSARY'
				| 'CONSENT_TYPE_PERFORMANCE'
				| 'CONSENT_TYPE_PERSONALIZATION';

			/* Returns the value in localStorage for the corresponding key if user has consented to the type of storage */
			export function getItem(
				key: string,
				type: TYPE_VALUES
			): string | undefined;

			/*  Returns the key of the n-th entry in localStorage */
			export function key(index: number): string | undefined;

			/* Removes the value for the corresponding key in localStorage regardless of consent */
			export function removeItem(key: string): void;

			/* Sets the key-value pair in localStorage if user has consented to the type of storage */
			export function setItem(
				key: string,
				value: string,
				type: TYPE_VALUES,
				options?: {
					'domain'?: string;
					'expires'?: string;
					'max-age'?: string;
					'path'?: string;
					'samesite'?: string;
					'secure'?: boolean;
				}
			): boolean;

			/* Returns the number of items in localStorage */
			export const length: number;
		}

		namespace SessionStorage {

			/* Removes all entries in sessionStorage */
			export function clear(): void;

			/* Object with consent types as keys and corresponding cookie names as values */
			export const TYPES: {[key: string]: TYPE_VALUES};

			export type TYPE_VALUES =
				| 'CONSENT_TYPE_FUNCTIONAL'
				| 'CONSENT_TYPE_NECESSARY'
				| 'CONSENT_TYPE_PERFORMANCE'
				| 'CONSENT_TYPE_PERSONALIZATION';

			/* Returns the value in sessionStorage for the corresponding key if user has consented to the type of storage */
			export function getItem(
				key: string,
				type: TYPE_VALUES
			): string | undefined;

			/* Returns the key of the n-th entry in sessionStorage */
			export function key(index: number): string | undefined;

			/* Removes the value for the corresponding key in sessionStorage regardless of consent */
			export function removeItem(key: string): void;

			/* Sets the key-value pair in sessionStorage if user has consented to the type of storage */
			export function setItem(
				key: string,
				value: string,
				type: TYPE_VALUES,
				options?: {
					'domain'?: string;
					'expires'?: string;
					'max-age'?: string;
					'path'?: string;
					'samesite'?: string;
					'secure'?: boolean;
				}
			): boolean;

			/* Returns the number of items in sessionStorage */
			export const length: number;
		}

		/* Escapes HTML from the given string */
		export function escapeHTML(string: string): string;

		/**
		 * Fetches a resource. A thin wrapper around ES6 Fetch API, with standardized
		 * default configuration.
		 */
		export function fetch(
			resource: string | Request,
			init?: Object
		): Promise<any>;

		/* Returns storage number formatted as a String */
		export function formatStorage(size: number, options?: Object): string;

		/* Returns a formatted XML */
		export function formatXML(content: string, options?: Object): string;

		export function getCheckedCheckboxes(
			form: HTMLFormElement,
			except: string,
			name?: string
		): Array<number> | '';

		export function getDOM(arg: any): HTMLElement | null;

		export function getUncheckedCheckboxes(
			form: HTMLFormElement,
			except: string,
			name?: string
		): Array<number> | '';

		/**
		 * Returns dimensions and coordinates representing a cropped region
		 */
		export function getCropRegion(
			imagePreview: HTMLImageElement,
			region: {
				height: number;
				width: number;
				x: number;
				y: number;
			}
		): {
			height: number;
			width: number;
			x: number;
			y: number;
		};

		/**
		 * Returns a DOM element or elements in a form.
		 */
		export function getFormElement(
			form: HTMLFormElement,
			elementName: string
		): Element | NodeList | null;

		export function getLexiconIcon(
			icon: string,
			cssClass?: string
		): HTMLElement;

		export function getLexiconIconTpl(
			icon: string,
			cssClass?: string
		): string;

		export function getOpener(): any;

		/**
		 * Returns the portlet namespace with underscores prepended and appended to it
		 */
		export function getPortletNamespace(portletId: string): string;

		export function getTop(): Window;

		export function getURLWithSessionId(url: string): string;

		export function getWindow(windowId?: string): Window;

		export function getSelectedOptionValues(
			select: HTMLSelectElement,
			delimiter?: string
		): string;

		/**
		 * Performs navigation to the given url. If SPA is enabled, it will route the
		 * request through the SPA engine. If not, it will simple change the document
		 * location.
		 */
		export function navigate(url: string | URL, listeners?: Object): void;

		/* Returns a namespaced string taking into account the optional parameters inside the provided object */
		export function ns(namespace: string, object?: Object): string | Object;

		/* Returns a FormData containing serialized object. */
		export function objectToFormData(
			object: Object,
			formData?: FormData,
			namespace?: string
		): FormData;

		export function objectToURLSearchParams(
			object: Object
		): URLSearchParams;

		/**
		 * Submits the form, with optional setting of form elements.
		 */
		export function postForm(
			form: HTMLFormElement | string,
			options?: {data: Object; url: string}
		): void;

		export function removeEntitySelection(
			entityIdString: string,
			entityNameString: string,
			removeEntityButton: string | HTMLElement,
			namespace: string
		): void;

		export function selectFolder(
			folderData: {
				idString: string;
				idValue: string;
				nameString: string;
				nameValue: string;
			},
			namespace: string
		): void;

		/**
		 * Sets the form elements to given values.
		 */
		export function setFormValues(
			form: HTMLFormElement,
			data: Object
		): void;

		export function showCapsLock(
			event: KeyboardEvent,
			elementId: string
		): void;

		export function sub(
			string: string,
			data:
				| string
				| number
				| string[]
				| number[]
				| Array<string>
				| Array<number>,
			...args: string[] | number[]
		): string;

		/**
		 * Get character code at the start of the given string.
		 */
		export function toCharCode(name: string): string;

		export function toggleBoxes(
			checkBoxId: string,
			toggleBoxId: string,
			displayWhenUnchecked?: boolean,
			toggleChildCheckboxes?: boolean
		): void;

		export function toggleRadio(
			radioId: string,
			showBoxIds: string | string[],
			hideBoxIds?: string | string[]
		): void;

		export function toggleSelectBox(
			selectBoxId: string,
			value: any,
			toggleBoxId: string
		): void;

		/**
		 * Unescapes HTML from the given string.
		 */
		export function unescapeHTML(string: string): string;
	}

	/**
	 * Registers a component and retrieves its instance from the global registry.
	 */
	export function component(
		id: string,
		value?: Object,
		componentConfig?: Object
	): Object;

	/**
	 * Retrieves a list of component instances after they've been registered.
	 */
	export function componentReady(...componentIds: string[]): Promise<any>;

	/**
	 * Destroys the component registered by the provided component ID. This invokes
	 * the component's own destroy lifecycle methods (destroy or dispose) and
	 * deletes the internal references to the component in the component registry.
	 */
	export function destroyComponent(componentId: string): void;

	/**
	 * Destroys registered components matching the provided filter function. If no
	 * filter function is provided, it destroys all registered components.
	 */
	export function destroyComponents(
		filterFn?: (component: any, componentConfigs: any) => boolean
	): void;

	/**
	 * Clears the component promises map to make sure pending promises don't get
	 * accidentally resolved at a later stage if a component with the same ID
	 * appears, causing stale code to run.
	 */
	export function destroyUnfulfilledPromises(): void;

	/**
	 * Retrieves a registered component's cached state.
	 */
	export function getComponentCache(componentId: string): void;

	/**
	 * Initializes the component cache mechanism.
	 */
	export function initComponentCache(): void;

	export function lazyLoad(): void;

	export function namespace(object: Object, path: string): Object;

	export function SideNavigation(
		toggler: HTMLElement,
		options?: Object
	): void;
}

interface ThemeDisplay {
	isSignedIn(): boolean;
	isStatePopUp(): boolean;
}

interface Window {
	cancelIdleCallback(handle: number): void;

	requestIdleCallback(callback: Function): any;

	themeDisplay: ThemeDisplay;
}
