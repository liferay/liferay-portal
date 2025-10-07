/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type TCustomField = {
	fieldName: string;
	fieldType:
		| 'boolean'
		| 'checkbox'
		| 'date'
		| 'dropdown'
		| 'geolocation'
		| 'inputField'
		| 'radio'
		| 'textArea';
	fieldValues?:
		| TBoolean
		| TCheckbox
		| TDate
		| TDropDown
		| TGeolocation
		| TInputField
		| TRadio
		| TTextArea;
	hidden?: boolean;
	localizeFieldName?: boolean;
	resource: TResourceType;
	searchable?: boolean;
	visibleWithUpdate?: boolean;
};

export type TBoolean = {
	defaultValue?: boolean;
};

export type TCheckbox = {
	asKeywordOrText?: 'keyword' | 'text';
	dataType?: 'decimal' | 'integer' | 'text';
	precision?: '32-bit' | '64-bit';
	values: string;
};

export type TDate = {
	defaultValue?: Date;
};

export type TDropDown = {
	asKeywordOrText?: 'keyword' | 'text';
	dataType?: 'decimal' | 'integer' | 'text';
	precision?: '32-bit' | '64-bit';
	values: string;
};

export type TGeolocation = {};

export type TInputField = {
	asKeywordOrText?: 'keyword' | 'text';
	dataType?: 'decimal' | 'integer' | 'text';
	localizable?: boolean;
	precision?: '32-bit' | '64-bit';
	secret?: boolean;
	startingValue?: string;
	width?: number;
};

export type TRadio = {
	asKeywordOrText?: 'keyword' | 'text';
	dataType?: 'decimal' | 'integer' | 'text';
	precision?: '32-bit' | '64-bit';
	values: string;
};

export type TResourceType =
	| 'Account Entry'
	| 'Account Group'
	| 'Blogs Entry'
	| 'Bookmarks Entry'
	| 'Bookmarks Folder'
	| 'Calendar Event'
	| 'Discount'
	| 'Document'
	| 'Documents Folder'
	| 'Knowledge Base Article'
	| 'Knowledge Base Folder'
	| 'Message Boards Category'
	| 'Message Boards Message'
	| 'Order'
	| 'Order Item'
	| 'Organization'
	| 'Page'
	| 'Price List'
	| 'Price Modifier'
	| 'Product'
	| 'Product Attachment'
	| 'Product Configuration List'
	| 'Product Group'
	| 'Product Link'
	| 'Product Option'
	| 'Product Option Relation'
	| 'Product Option Value'
	| 'Product Option Value Relation'
	| 'Product SKU'
	| 'Product Specification'
	| 'Product Specification Value'
	| 'Role'
	| 'Shipment'
	| 'Site'
	| 'Site Navigation Menu Item'
	| 'User'
	| 'User Group'
	| 'Warehouse'
	| 'Web Content Article'
	| 'Web Content Folder'
	| 'Wiki Page';

export type TTextArea = {
	asKeywordOrText?: 'keyword' | 'text';
	height?: number;
	localizable?: boolean;
	startingValue?: string;
	width?: number;
};
