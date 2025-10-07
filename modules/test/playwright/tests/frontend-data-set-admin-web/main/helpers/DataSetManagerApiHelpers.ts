/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import {
	API_ENDPOINT_PATH,
	DEFAULT_LABEL,
	OBJECT_RELATIONSHIP,
} from '../utils/constants';
import getDataSetResourceURL from '../utils/getDataSetResourceURL';
import {
	EActionType,
	EAsyncActionMethod,
	ECreationActionTarget,
	EItemActionTarget,
	EModalActionVariant,
} from '../utils/types';

const DEFAULT_DATA_SET_ERC = 'sampleDataSetERC';
export class DataSetManagerApiHelpers extends ApiHelpers {
	async createDataSet({
		additionalAPIURLParameters,
		defaultItemsPerPage = 20,
		defaultVisualizationMode,
		description = 'Sample description',
		erc = 'sampleDataSetERC',
		label = DEFAULT_LABEL.DATA_SET,
		listOfItemsPerPage = '4, 8, 20, 40, 60',
		restApplication = `${API_ENDPOINT_PATH}/table-sections`,
		restEndpoint = '/',
		restSchema = 'DataSetTableSection',
	}: {
		additionalAPIURLParameters?: string;
		defaultItemsPerPage?: number;
		defaultVisualizationMode?: string;
		description?: string;
		erc?: string;
		label?: string;
		listOfItemsPerPage?: string;
		restApplication?: string;
		restEndpoint?: string;
		restSchema?: string;
	}) {
		const url = getDataSetResourceURL({});

		const data = {
			additionalAPIURLParameters,
			defaultItemsPerPage,
			defaultVisualizationMode,
			description,
			externalReferenceCode: erc,
			label,
			listOfItemsPerPage,
			restApplication,
			restEndpoint,
			restSchema,
		};

		return this.post(url, {data});
	}

	async createDataSetCardsSection({
		dataSetERC = DEFAULT_DATA_SET_ERC,
		fieldName = 'fieldName',
		name = 'title',
	}: {
		dataSetERC?: string;
		fieldName?: string;
		name?: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS,
		});

		const data = {
			fieldName,
			name,
		};

		return this.post(url, {data});
	}

	async createDataSetClientExtensionFilter({
		active,
		clientExtensionEntryERC,
		dataSetERC,
		fieldName,
		label_i18n = {en_US: 'Title'},
	}: {
		active?: boolean;
		clientExtensionEntryERC: string;
		dataSetERC: string;
		fieldName: string;
		label_i18n?: {[key: string]: string};
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_CLIENT_EXTENSION_FILTERS,
		});

		const data = {
			active,
			clientExtensionEntryERC,
			fieldName,
			label_i18n,
		};

		return this.post(url, {data});
	}

	async createDataSetCreationAction({
		active,
		dataSetERC = DEFAULT_DATA_SET_ERC,
		icon,
		label_i18n = {en_US: 'Default Creation Action'},
		modalSize = EModalActionVariant.FULL_SCREEN,
		permissionKey,
		title_i18n,
		target = ECreationActionTarget.LINK,
		url = liferayConfig.environment.baseUrl,
	}: {
		active?: boolean;
		dataSetERC?: string;
		icon?: string;
		label_i18n?: {[key: string]: string};
		modalSize?: EModalActionVariant;
		permissionKey?: string;
		target?: ECreationActionTarget;
		title_i18n?: {[key: string]: string};
		url?: string;
	}) {
		const endpointURL = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_ACTIONS,
		});

		const data = {
			active,
			icon,
			label_i18n,
			modalSize,
			permissionKey,
			target,
			title_i18n,
			type: EActionType.CREATION,
			url,
		};

		return this.post(endpointURL, {data});
	}

	async createDataSetTableSection({
		dataSetERC = DEFAULT_DATA_SET_ERC,
		extraBodyParams = {},
		fieldName = 'title',
		label_i18n = {en_US: 'Title'},
		renderer = 'default',
		rendererType = 'internal',
		sortable = false,
		type = 'string',
	}: {
		dataSetERC?: string;
		extraBodyParams?: any;
		fieldName?: string;
		label_i18n?: {[key: string]: string};
		renderer?: string;
		rendererType?: string;
		sortable?: boolean;
		type?: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS,
		});

		const data = {
			fieldName,
			label_i18n,
			renderer,
			rendererType,
			sortable,
			type,
			...extraBodyParams,
		};

		return this.post(url, {data});
	}

	async createDataSetDateFilter({
		active,
		dataSetERC = DEFAULT_DATA_SET_ERC,
		fieldName,
		from = '',
		label_i18n = {en_US: 'Title'},
		to = '',
		type,
	}: {
		active?: boolean;
		dataSetERC?: string;
		fieldName: string;
		from?: string;
		label_i18n?: {[key: string]: string};
		to?: string;
		type: 'date' | 'date-time';
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_DATE_FILTERS,
		});

		const data = {
			active,
			fieldName,
			from,
			label_i18n,
			to,
			type,
		};

		return this.post(url, {data});
	}

	async createDataSetSelectionFilter({
		active,
		dataSetERC = DEFAULT_DATA_SET_ERC,
		fieldName,
		include = true,
		itemKey,
		itemLabel,
		label_i18n,
		multiple = false,
		preselectedValues = '[]',
		source,
		sourceType,
	}: {
		active?: boolean;
		dataSetERC?: string;
		fieldName: string;
		include?: boolean;
		itemKey?: string;
		itemLabel?: string;
		label_i18n?: {[key: string]: string};
		multiple?: boolean;
		preselectedValues?: string;
		source: string;
		sourceType: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_SELECTION_FILTERS,
		});

		const data = {
			active,
			fieldName,
			include,
			itemKey,
			itemLabel,
			label_i18n,
			multiple,
			preselectedValues,
			source,
			sourceType,
		};

		return this.post(url, {data});
	}

	async createDataSetItemAction({
		active,
		confirmationMessage_i18n,
		confirmationMessageType,
		dataSetERC = DEFAULT_DATA_SET_ERC,
		errorMessage_i18n,
		icon,
		label_i18n = {en_US: 'Default Item Action'},
		method,
		modalSize = EModalActionVariant.FULL_SCREEN,
		permissionKey,
		requestBody = '{}',
		successMessage_i18n,
		title_i18n,
		target = EItemActionTarget.LINK,
		url = liferayConfig.environment.baseUrl,
	}: {
		active?: boolean;
		confirmationMessageType?: string;
		confirmationMessage_i18n?: {[key: string]: string};
		dataSetERC?: string;
		errorMessage_i18n?: {[key: string]: string};
		icon?: string;
		label_i18n?: {[key: string]: string};
		method?: EAsyncActionMethod;
		modalSize?: EModalActionVariant;
		permissionKey?: string;
		requestBody?: string;
		successMessage_i18n?: {[key: string]: string};
		target?: EItemActionTarget;
		title_i18n?: {[key: string]: string};
		url?: string;
	}) {
		const endpointURL = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_ACTIONS,
		});

		const data = {
			active,
			confirmationMessage_i18n,
			confirmationMessageType,
			errorMessage_i18n,
			icon,
			label_i18n,
			method,
			modalSize,
			permissionKey,
			requestBody,
			successMessage_i18n,
			target,
			title_i18n,
			type: EActionType.ITEM,
			url,
		};

		return this.post(endpointURL, {data});
	}

	async createDataSetSort({
		active,
		dataSetERC = DEFAULT_DATA_SET_ERC,
		defaultValue = false,
		fieldName = 'dateCreated',
		label_i18n = {en_US: 'Date Created'},
		orderType = 'asc',
	}: {
		active?: boolean;
		dataSetERC?: string;
		defaultValue?: boolean;
		fieldName?: string;
		label_i18n?: {[key: string]: string};
		orderType?: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_SORTS,
		});

		const data = {
			active,
			default: defaultValue,
			fieldName,
			label_i18n,
			orderType,
		};

		return this.post(url, {data});
	}

	async createDataSetListSection({
		dataSetERC = DEFAULT_DATA_SET_ERC,
		fieldName = 'fieldName',
		name = 'title',
	}: {
		dataSetERC?: string;
		fieldName?: string;
		name?: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS,
		});

		const data = {
			fieldName,
			name,
		};

		return this.post(url, {data});
	}

	async deleteDataSet({erc = DEFAULT_DATA_SET_ERC}: {erc?: string}) {
		const url = getDataSetResourceURL({
			dataSetERC: erc,
		});

		return this.delete(url);
	}

	async updateDataSet({
		additionalAPIURLParameters,
		defaultItemsPerPage,
		defaultVisualizationMode,
		erc = DEFAULT_DATA_SET_ERC,
		filtersOrder,
		label,
		listOfItemsPerPage,
	}: {
		additionalAPIURLParameters?: string;
		defaultItemsPerPage?: number;
		defaultVisualizationMode?: string;
		erc?: string;
		filtersOrder?: string;
		label?: string;
		listOfItemsPerPage?: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC: erc,
		});

		const data = {
			additionalAPIURLParameters,
			defaultItemsPerPage,
			defaultVisualizationMode,
			filtersOrder,
			label,
			listOfItemsPerPage,
		};

		return this.patch(url, data);
	}

	async updateDataSetSelectionFilter({
		active,
		dataSetERC,
		fieldName,
		include,
		itemKey,
		itemLabel,
		label_i18n,
		multiple,
		preselectedValues,
		selectionFilterERC,
	}: {
		active?: boolean;
		dataSetERC: string;
		fieldName?: string;
		include?: boolean;
		itemKey?: string;
		itemLabel?: string;
		label_i18n?: {[key: string]: string};
		multiple?: boolean;
		preselectedValues?: string;
		selectionFilterERC: string;
	}) {
		const url = getDataSetResourceURL({
			dataSetERC,
			relatedResourceERC: selectionFilterERC,
			relationship: OBJECT_RELATIONSHIP.DATA_SET_SELECTION_FILTERS,
		});

		const data = {
			active,
			fieldName,
			include,
			itemKey,
			itemLabel,
			label_i18n,
			multiple,
			preselectedValues,
		};

		return this.patch(url, data);
	}
}
