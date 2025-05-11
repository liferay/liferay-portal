/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay, LiferayStorage} from '../liferay/liferay';

export enum CONSENT_TYPE {
	FUNCTIONAL = 'CONSENT_TYPE_FUNCTIONAL',
	NECESSARY = 'CONSENT_TYPE_NECESSARY',
	PERFORMANCE = 'CONSENT_TYPE_PERFORMANCE',
	PERSONALIZATION = 'CONSENT_TYPE_PERSONALIZATION',
}

export enum STORAGE_KEYS {
	LIST_VIEW_COLUMNS = '@marketplace/listview-columns-',
	SWR_CACHE = '@marketplace/swr-cache',
}

export type StorageType = 'persisted' | 'temporary';

class Storage {
	private storage: LiferayStorage;

	constructor(storage: LiferayStorage) {
		this.storage = storage;
	}

	getItem(
		key: STORAGE_KEYS,
		consentType: CONSENT_TYPE = CONSENT_TYPE.NECESSARY
	): string | null {
		return this.storage.getItem(key, consentType);
	}

	removeItem(key: string): void {
		return this.storage.removeItem(key);
	}

	setItem(
		key: string,
		value: string,
		consentType: CONSENT_TYPE = CONSENT_TYPE.NECESSARY
	): void {
		return this.storage.setItem(key, value, consentType);
	}
}

class MarketplaceStorage {
	private LocalStorage = new Storage(Liferay.Util.LocalStorage);
	private SessionStorage = new Storage(Liferay.Util.SessionStorage);
	private static instance: MarketplaceStorage;
	static KEYS = STORAGE_KEYS;

	private constructor() {}

	public static getInstance(): MarketplaceStorage {
		if (!MarketplaceStorage.instance) {
			MarketplaceStorage.instance = new MarketplaceStorage();
		}

		return MarketplaceStorage.instance;
	}

	getStorage(storage: StorageType) {
		if (storage === 'persisted') {
			return this.LocalStorage;
		}

		return this.SessionStorage;
	}
}

export default MarketplaceStorage;
