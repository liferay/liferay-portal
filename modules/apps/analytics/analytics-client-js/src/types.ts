/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export namespace Analytics {
	export enum ApplicationId {
		Blog = 'Blog',
		Custom = 'Custom',
		CustomEvent = 'CustomEvent',
		Document = 'Document',
		Form = 'Form',
		ObjectEntry = 'ObjectEntry',
		Page = 'Page',
		WebContent = 'WebContent',
	}

	export enum EventId {
		AssetClicked = 'assetClicked',
		AssetDepthReached = 'assetDepthReached',
		AssetDownloaded = 'assetDownloaded',
		AssetSubmitted = 'assetSubmitted',
		AssetViewed = 'assetViewed',
		BlogClicked = 'blogClicked',
		BlogDepthReached = 'blogDepthReached',
		BlogImpressionMade = 'blogImpressionMade',
		BlogViewed = 'blogViewed',
		DocumentDownloaded = 'documentDownloaded',
		DocumentImpressionMade = 'documentImpressionMade',
		DocumentPreviewed = 'documentPreviewed',
		FieldBlurred = 'fieldBlurred',
		FieldFocused = 'fieldFocused',
		FormSubmitted = 'formSubmitted',
		FormViewed = 'formViewed',
		ObjectEntryDownloaded = 'objectEntryDownloaded',
		ObjectEntryImpressionMade = 'objectEntryImpressionMade',
		ObjectEntryViewed = 'objectEntryViewed',
		PageDepthReached = 'pageDepthReached',
		PageLoaded = 'pageLoaded',
		PageRead = 'pageRead',
		PageUnloaded = 'pageUnloaded',
		PageViewed = 'pageViewed',
		TabBlurred = 'tabBlurred',
		TabFocused = 'tabFocused',
		WebContentClicked = 'webContentClicked',
		WebContentImpressionMade = 'webContentImpressionMade',
		WebContentViewed = 'webContentViewed',
	}

	export type Config = {
		channelId: string;
		dataSourceId: string;
		endpointUrl: string;
		flushInterval: number;
		identity: {
			emailAddressHashed: string;
		};
		identityEndpoint: string;
		projectId: string;
		userId: string;
	};

	export enum ElementAction {
		View = 'view',
		Impression = 'impression',
		Download = 'download',
	}

	export enum ElementType {
		Blog = 'blog',
		BlogsEntry = 'com.liferay.blogs.model.BlogsEntry',
		Custom = 'custom',
		Document = 'document',
		FileEntry = 'com.liferay.portal.kernel.repository.model.FileEntry',
		JournalArticle = 'com.liferay.journal.model.JournalArticle',
		ObjectEntry = 'object-entry',
		WebContent = 'web-content',
	}

	export interface HTMLElement extends Element {
		dataset: {
			[Analytics.DataSetList
				.AnalyticsAssetAction]?: Analytics.ElementAction;
			[Analytics.DataSetList.AnalyticsAssetCategory]?: string;
			[Analytics.DataSetList.AnalyticsAssetId]: string;
			[Analytics.DataSetList.AnalyticsAssetSubtype]?: string;
			[Analytics.DataSetList.AnalyticsAssetTitle]?: string;
			[Analytics.DataSetList.AnalyticsAssetType]?: Analytics.ElementType;
			[Analytics.DataSetList.AnalyticsAssetVersion]?: string;
			[Analytics.DataSetList.AnalyticsExternalReferenceCode]?: string;
			[Analytics.DataSetList.AnalyticsWebContentResourcePk]?: string;
		};
		innerText: string;
	}

	export interface ObjectEntryHTMLElement extends Element {
		dataset: {
			[Analytics.DataSetList
				.AnalyticsAssetAction]: Analytics.ElementAction;
			[Analytics.DataSetList.AnalyticsAssetType]: Analytics.ElementType;
			[Analytics.DataSetList.AnalyticsExternalReferenceCode]: string;
			[Analytics.DataSetList.AnalyticsObjectDefinitionName]: string;
		};
		innerText: string;
	}

	export enum DataSetList {
		AnalyticsAssetAction = 'analyticsAssetAction',
		AnalyticsAssetCategory = 'analyticsAssetCategory',
		AnalyticsExternalReferenceCode = 'analyticsExternalReferenceCode',
		AnalyticsAssetId = 'analyticsAssetId',
		AnalyticsAssetSubtype = 'analyticsAssetSubtype',
		AnalyticsAssetTitle = 'analyticsAssetTitle',
		AnalyticsAssetType = 'analyticsAssetType',
		AnalyticsAssetVersion = 'analyticsAssetVersion',
		AnalyticsObjectDefinitionName = 'analyticsObjectDefinitionName',
		AnalyticsWebContentResourcePk = 'analyticsWebContentResourcePk',
	}

	export type Event = {
		applicationId: Analytics.ApplicationId;
		contextHash: string;
		eventDate: string;
		eventId: Analytics.EventId;
		eventLocalDate: string;
		properties: Analytics.EventProps;
	};

	export type EventProps = {
		[key: string]: boolean | number | string;
	} & {
		assetType?: Analytics.ApplicationId;
	};

	export type Identity = {
		channelId: string;
		dataSourceId: string;
		emailAddressHashed: string;
		id: string;
		userId: string;
	};

	export type Context = {
		channelId: string;
		dataSourceId: string;
		[key: string]: string | number | boolean;
	};

	export type Message = {
		channelId: string;
		context: {
			[key: string]: string;
		};
		dataSourceId: string;
		emailAddressHashed: string;
		events: Event[];
		id: string;
		userId: string;
	};

	export type Middleware = (request: {context: Analytics.Context}) => {
		context: Analytics.Context;
	};

	export enum Keys {
		ChannelId = 'ac_client_channel_id',
		Contexts = 'ac_client_context',
		DisableTracking = 'ac_client_disable_tracking',
		Identity = 'ac_client_identity',
		PrevEmailAddressHash = 'ac_client_previous_email_address_hash',
		StorageVersion = 'ac_client_storage_version',
		UserId = 'ac_client_user_id',
	}

	export enum Queues {
		Events = 'ac_client_batch',
		IdentityMessage = 'ac_message_queue_identity',
		Messages = 'ac_message_queue',
	}

	export type FlushResult = {
		status: string;
		value: {events: Analytics.Event[]};
	};
}
