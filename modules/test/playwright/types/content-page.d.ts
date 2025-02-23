/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type AssetDisplayPageEntry = {
	assetDisplayPageEntryId: string;
	groupId: string;
};

type AssetListEntry = {
	assetListEntryId: string;
	groupId: string;
};

type CollectionConfig = {
	collectionReference: {
		className?: string;
		classPK?: number;
	};
	collectionType?: 'Collection' | 'CollectionProvider';
};

type CollectionViewport = {
	collectionViewportDefinition: {
		numberOfColumns: number;
	};
	id: 'landscapeMobile' | 'portraitMobile' | 'tablet';
};

type FragmentField = {
	id?: string;
	value?: {
		fragmentLink?: Record<string, string>;
		text?: {
			mapping?: {
				fieldKey: string;
				itemReference: {
					contextSource: string;
				};
			};
			value_i18n?: {
				en_US: string;
			};
		};
	};
};

type FormConfig = {
	formReference: {
		className: string;
		classType: number;
	};
	formType: 'multistep' | 'simple';
	numberOfSteps: number;
};

type Layout = {
	companyId: string;
	friendlyURL: string;
	friendlyUrlPath: string;
	groupId: string;
	hidden: boolean;
	id: string;
	layoutId: string;
	nameCurrentValue: string;
	parentPlid: string;
	plid: string;
	privateLayout: boolean;
	status: number;
	system: boolean;
	themeId: string;
	titleCurrentValue: string;
	type: string;
	uuid: string;
};

type LayoutPageTemplateEntry = {
	groupId: string;
	layoutPageTemplateEntryId: string;
	plid: string;
};

type LayoutPageTemplateCollection = {
	groupId: string;
	layoutPageTemplateCollectionId: string;
};

type PageDefinition = {
	pageElement: PageElement;
};

type PageElement = {
	definition?: {
		collectionConfig?: CollectionConfig;
		collectionViewports?: Array<CollectionViewport>;
		cssClasses?: string[];
		formConfig?: FormConfig;
		fragment?: {
			key: string;
		};
		fragmentConfig?: Record<string, any>;
		fragmentDropZoneId?: string;
		fragmentFields?: FragmentField[];
		fragmentStyle?: Record<string, string>;
		gutters?: boolean;
		layout?: {};
		listStyle?: string;
		numberOfColumns?: number;
		numberOfItems?: number;
		size?: number;
		widgetInstance?: {
			widgetConfig?: Record<string, any>;
			widgetName: string;
		};
	};
	id: string;
	pageElements?: PageElement[];
	type:
		| 'Collection'
		| 'CollectionItem'
		| 'Column'
		| 'DropZone'
		| 'Form'
		| 'FormStep'
		| 'FormStepContainer'
		| 'Fragment'
		| 'FragmentDropZone'
		| 'Root'
		| 'Row'
		| 'Section'
		| 'Widget';
};

type PagePermission = {
	actionKeys: PermissionActionKeys[];
	roleKey: 'Owner' | 'Site Member' | 'Guest';
};

type PermissionActionKeys =
	| 'ADD_DISCUSSION'
	| 'ADD_LAYOUT'
	| 'CONFIGURE_PORTLETS'
	| 'CUSTOMIZE'
	| 'DELETE'
	| 'DELETE_DISCUSSION'
	| 'LAYOUT_RULE_BUILDER'
	| 'PERMISSIONS'
	| 'PREVIEW_DRAFT'
	| 'UPDATE'
	| 'UPDATE_DISCUSSION'
	| 'UPDATE_LAYOUT_ADVANCED_OPTIONS'
	| 'UPDATE_LAYOUT_BASIC'
	| 'UPDATE_LAYOUT_CONTENT'
	| 'UPDATE_LAYOUT_LIMITED'
	| 'VIEW';

type SpacingType =
	| 'Margin Bottom'
	| 'Margin Left'
	| 'Margin Right'
	| 'Margin Top'
	| 'Padding Bottom'
	| 'Padding Left'
	| 'Padding Right'
	| 'Padding Top';

type StyleUnit = 'px' | '%' | 'em' | 'rem' | 'vw' | 'vh' | 'custom';
