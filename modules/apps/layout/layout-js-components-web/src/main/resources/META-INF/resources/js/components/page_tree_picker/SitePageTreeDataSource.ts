/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {
	PageTreePickerDataSource,
	PageTreePickerItem,
	PageTreePickerPage,
	PageTreePickerSelectionEntry,
} from '../../types/PageTreePicker';

export interface SitePage {
	externalReferenceCode: string;
	name_i18n?: Record<string, string>;
	parentSitePageExternalReferenceCode?: string | null;
	type?: string;
}

export interface SitePageTreeSelection {
	all?: boolean;
	excludedItems?: string[];
	excludedSubtrees?: string[];
	items?: string[];
	privateLayout: boolean;
	subtrees?: string[];
}

const FIELDS =
	'externalReferenceCode,name_i18n,parentSitePageExternalReferenceCode,type';

export const ROOT_ITEM_ID = 'liferay-page-tree-picker-root';

async function fetchPage(
	requestURL: URL
): Promise<{items: SitePage[]; totalCount: number}> {
	const response = await fetch(requestURL.toString(), {
		headers: {Accept: 'application/json'},
	});

	if (!response.ok) {
		throw new Error(
			`Request to ${requestURL.pathname} failed with status ${response.status}`
		);
	}

	const {items = [], totalCount = 0} = (await response.json()) as {
		items?: SitePage[];
		totalCount?: number;
	};

	return {items, totalCount};
}

function getCachedPromise<T>(
	promises: Map<string, Promise<T>>,
	externalReferenceCode: string,
	createPromise: () => Promise<T>
): Promise<T> {
	let promise = promises.get(externalReferenceCode);

	if (!promise) {
		promise = createPromise();

		promise.catch(() => {
			promises.delete(externalReferenceCode);
		});

		promises.set(externalReferenceCode, promise);
	}

	return promise;
}

function getChildSitePagesPath(externalReferenceCode: string): string {
	return `site-pages/${encodeURIComponent(externalReferenceCode)}/site-pages`;
}

function getIcon(sitePage: SitePage): string {
	if (sitePage.type === 'ContentPage') {
		return 'page';
	}

	if (
		sitePage.type === 'LinkToPagePage' ||
		sitePage.type === 'LinkToURLPage'
	) {
		return 'link';
	}

	return 'page-template';
}

function getName(sitePage: SitePage): string {
	const nameI18n = sitePage.name_i18n ?? {};

	return (
		nameI18n[Liferay.ThemeDisplay.getBCP47LanguageId()] ??
		Object.values(nameI18n)[0] ??
		''
	);
}

function toAncestorItems(
	ancestorSitePagesList: SitePage[][]
): Array<PageTreePickerItem<SitePage | null>> {
	const ancestorItems = new Map<
		string,
		PageTreePickerItem<SitePage | null>
	>();

	ancestorSitePagesList.forEach((ancestorSitePages) =>
		ancestorSitePages.forEach((ancestorSitePage) =>
			ancestorItems.set(
				ancestorSitePage.externalReferenceCode,
				toItem(ancestorSitePage, true)
			)
		)
	);

	return Array.from(ancestorItems.values());
}

function toItem(
	sitePage: SitePage,
	hasChildren: boolean
): PageTreePickerItem<SitePage | null> {
	return {
		hasChildren,
		icon: getIcon(sitePage),
		id: sitePage.externalReferenceCode,
		label: getName(sitePage),
		page: sitePage,
		parentId: sitePage.parentSitePageExternalReferenceCode ?? ROOT_ITEM_ID,
	};
}

function toStubEntries(
	externalReferenceCodes: string[] | undefined,
	excluded: boolean,
	includeDescendants: boolean
): Array<PageTreePickerSelectionEntry<SitePage | null>> {
	return (externalReferenceCodes ?? []).map((externalReferenceCode) => ({
		excluded,
		includeDescendants,
		item: {
			hasChildren: includeDescendants,
			id: externalReferenceCode,
			label: externalReferenceCode,
			page: {externalReferenceCode},
			parentId: ROOT_ITEM_ID,
		},
	}));
}

export default class SitePageTreeDataSource
	implements PageTreePickerDataSource<SitePage | null>
{
	private readonly _descendantCountPromises = new Map<
		string,
		Promise<number>
	>();
	private readonly _hasChildrenPromises = new Map<string, Promise<boolean>>();
	private readonly _pageSize: number;
	private readonly _privateLayout: boolean;
	private readonly _sitePagePromises = new Map<
		string,
		Promise<SitePage | null>
	>();
	private _sitePagesCountPromise: Promise<number> | null = null;
	private readonly _siteURL: string;

	constructor({
		pageSize,
		privateLayout,
		siteExternalReferenceCode,
	}: {
		pageSize: number;
		privateLayout: boolean;
		siteExternalReferenceCode: string;
	}) {
		this._pageSize = pageSize;
		this._privateLayout = privateLayout;
		this._siteURL = `/o/headless-admin-site/v1.0/sites/${encodeURIComponent(
			siteExternalReferenceCode
		)}`;
	}

	async getChildren(
		parentItem: PageTreePickerItem<SitePage | null> | null,
		page: number
	): Promise<PageTreePickerPage<SitePage | null>> {
		if (!parentItem) {
			return {items: [this.getRootItem()], totalCount: 1};
		}

		const searchParams = {
			fields: FIELDS,
			page: String(page),
			pageSize: String(this._pageSize),
		};

		const requestURL =
			parentItem.id === ROOT_ITEM_ID
				? this._getURL('site-pages', {
						...searchParams,
						privateLayout: String(this._privateLayout),
						sort: 'pageSettings/priority:asc',
					})
				: this._getURL(
						getChildSitePagesPath(parentItem.id),
						searchParams
					);

		const {items, totalCount} = await fetchPage(requestURL);

		items.forEach((sitePage) => this._registerSitePage(sitePage));

		return {items: await this._toItems(items), totalCount};
	}

	getRootItem(): PageTreePickerItem<SitePage | null> {
		return {
			alwaysIncludeDescendants: true,
			hasChildren: true,
			icon: 'home',
			id: ROOT_ITEM_ID,
			label: this._privateLayout
				? Liferay.Language.get('private-pages')
				: Liferay.Language.get('public-pages'),
			page: null,
			parentId: null,
		};
	}

	getSubtreeCount(
		item: PageTreePickerItem<SitePage | null>
	): Promise<number> {
		if (item.id === ROOT_ITEM_ID) {
			return this._getSitePagesCount();
		}

		return getCachedPromise(this._descendantCountPromises, item.id, () =>
			fetchPage(
				this._getURL(getChildSitePagesPath(item.id), {
					fields: 'externalReferenceCode',
					flatten: 'true',
					pageSize: '1',
				})
			).then(({totalCount}) => totalCount)
		);
	}

	async resolveItems(
		items: Array<PageTreePickerItem<SitePage | null>>
	): Promise<Array<PageTreePickerItem<SitePage | null>>> {
		const sitePages = (
			await Promise.all(
				Array.from(
					new Set(
						items
							.filter((item) => item.id !== ROOT_ITEM_ID)
							.map((item) => item.id)
					)
				).map((externalReferenceCode) =>
					this._fetchSitePage(externalReferenceCode)
				)
			)
		).filter((sitePage): sitePage is SitePage => sitePage !== null);

		const [ancestorSitePagesList, resolvedItems] = await Promise.all([
			Promise.all(
				sitePages.map((sitePage) =>
					this._getAncestorSitePages(sitePage)
				)
			),
			this._toItems(sitePages),
		]);

		return [...toAncestorItems(ancestorSitePagesList), ...resolvedItems];
	}

	async search(
		query: string,
		page: number
	): Promise<PageTreePickerPage<SitePage | null>> {
		const {items: sitePages, totalCount} = await fetchPage(
			this._getURL('site-pages', {
				fields: FIELDS,
				flatten: 'true',
				page: String(page),
				pageSize: String(this._pageSize),
				privateLayout: String(this._privateLayout),
				search: query,
			})
		);

		sitePages.forEach((sitePage) => this._registerSitePage(sitePage));

		const [ancestorSitePagesList, items] = await Promise.all([
			Promise.all(
				sitePages.map((sitePage) =>
					this._getAncestorSitePages(sitePage)
				)
			),
			this._toItems(sitePages),
		]);

		return {
			ancestors: toAncestorItems(ancestorSitePagesList),
			items: items.map((item, index) => ({
				...item,
				path: ancestorSitePagesList[index].map(getName),
			})),
			totalCount,
		};
	}

	toEntries(
		selection: SitePageTreeSelection | null | undefined
	): Array<PageTreePickerSelectionEntry<SitePage | null>> {
		if (!selection) {
			return [];
		}

		return [
			...(selection.all
				? [
						{
							excluded: false,
							includeDescendants: true,
							item: this.getRootItem(),
						},
					]
				: []),
			...toStubEntries(selection.items, false, false),
			...toStubEntries(selection.subtrees, false, true),
			...toStubEntries(selection.excludedItems, true, false),
			...toStubEntries(selection.excludedSubtrees, true, true),
		];
	}

	toSelection(
		entries: Array<PageTreePickerSelectionEntry<SitePage | null>>
	): SitePageTreeSelection | null {
		const all = entries.some(
			(entry) => entry.item.id === ROOT_ITEM_ID && !entry.excluded
		);

		const excludedItems: string[] = [];
		const excludedSubtrees: string[] = [];
		const items: string[] = [];
		const subtrees: string[] = [];

		entries.forEach((entry) => {
			if (!entry.item.page) {
				return;
			}

			if (entry.excluded) {
				if (entry.includeDescendants) {
					excludedSubtrees.push(entry.item.id);
				}
				else {
					excludedItems.push(entry.item.id);
				}
			}
			else if (entry.includeDescendants) {
				subtrees.push(entry.item.id);
			}
			else {
				items.push(entry.item.id);
			}
		});

		if (!all && !items.length && !subtrees.length) {
			return null;
		}

		return {
			...(all && {all: true}),
			...(excludedItems.length && {excludedItems}),
			...(excludedSubtrees.length && {excludedSubtrees}),
			...(items.length && {items}),
			...(subtrees.length && {subtrees}),
			privateLayout: this._privateLayout,
		};
	}

	private _fetchSitePage(
		externalReferenceCode: string
	): Promise<SitePage | null> {
		return getCachedPromise(
			this._sitePagePromises,
			externalReferenceCode,
			async () => {
				const requestURL = this._getURL(
					`site-pages/${encodeURIComponent(externalReferenceCode)}`,
					{fields: FIELDS}
				);

				const response = await fetch(requestURL.toString(), {
					headers: {Accept: 'application/json'},
				});

				if (response.status === 404) {
					return null;
				}

				if (!response.ok) {
					throw new Error(
						`Request to ${requestURL.pathname} failed with status ${response.status}`
					);
				}

				return (await response.json()) as SitePage;
			}
		);
	}

	private async _getAncestorSitePages(
		sitePage: SitePage
	): Promise<SitePage[]> {
		const ancestorSitePages: SitePage[] = [];

		let parentSitePageExternalReferenceCode =
			sitePage.parentSitePageExternalReferenceCode;

		while (parentSitePageExternalReferenceCode) {
			const parentSitePage: SitePage | null = await this._fetchSitePage(
				parentSitePageExternalReferenceCode
			);

			if (!parentSitePage) {
				break;
			}

			ancestorSitePages.unshift(parentSitePage);

			parentSitePageExternalReferenceCode =
				parentSitePage.parentSitePageExternalReferenceCode;
		}

		return ancestorSitePages;
	}

	private _getSitePagesCount(): Promise<number> {
		if (!this._sitePagesCountPromise) {
			this._sitePagesCountPromise = fetchPage(
				this._getURL('site-pages', {
					fields: 'externalReferenceCode',
					flatten: 'true',
					pageSize: '1',
					privateLayout: String(this._privateLayout),
				})
			).then(({totalCount}) => totalCount);

			this._sitePagesCountPromise.catch(() => {
				this._sitePagesCountPromise = null;
			});
		}

		return this._sitePagesCountPromise;
	}

	private _getURL(path: string, searchParams: Record<string, string>): URL {
		const requestURL = new URL(
			`${this._siteURL}/${path}`,
			window.location.origin
		);

		Object.entries(searchParams).forEach(([name, value]) =>
			requestURL.searchParams.set(name, value)
		);

		return requestURL;
	}

	private _hasChildSitePages(
		externalReferenceCode: string
	): Promise<boolean> {
		return getCachedPromise(
			this._hasChildrenPromises,
			externalReferenceCode,
			() =>
				fetchPage(
					this._getURL(getChildSitePagesPath(externalReferenceCode), {
						fields: 'externalReferenceCode',
						pageSize: '1',
					})
				).then(({totalCount}) => totalCount > 0)
		);
	}

	private _registerSitePage(sitePage: SitePage) {
		this._sitePagePromises.set(
			sitePage.externalReferenceCode,
			Promise.resolve(sitePage)
		);
	}

	private _toItems(
		sitePages: SitePage[]
	): Promise<Array<PageTreePickerItem<SitePage | null>>> {
		return Promise.all(
			sitePages.map(async (sitePage) =>
				toItem(
					sitePage,
					await this._hasChildSitePages(
						sitePage.externalReferenceCode
					)
				)
			)
		);
	}
}
