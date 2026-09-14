import BlogDevicesCard from 'assets/blog/hocs/DevicesCard';
import BlogLocationsCard from 'assets/blog/hocs/LocationsCard';
import DocumentDevicesCard from 'assets/document-and-media/hocs/DevicesCard';
import DocumentLocationsCard from 'assets/document-and-media/hocs/LocationsCard';
import FormAbandonmentCard from 'assets/form/hocs/FormAbandonmentCard';
import FormDevicesCard from 'assets/form/hocs/DevicesCard';
import FormLocationsCard from 'assets/form/hocs/LocationsCard';
import JournalDevicesCard from 'assets/web-content/hocs/DevicesCard';
import JournalLocationsCard from 'assets/web-content/hocs/LocationsCard';
import ObjectEntryDevicesCard from 'assets/object-entry/hocs/DevicesCard';
import ObjectEntryLocationsCard from 'assets/object-entry/hocs/LocationsCard';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	AbandonmentsMetric,
	CommentsMetric,
	CompletionTimeMetric,
	DownloadsMetric,
	ImpressionMadeMetric,
	RatingsMetric,
	ReadingTimeMetric,
	SubmissionsMetric,
	ViewsMetric,
} from 'shared/components/metric-card/metrics';
import {
	Accessor,
	EmptyStateLink,
	EmptyStateText,
} from 'assets/components/AssetAppearsOnCard';
import {AssetTypes} from 'shared/util/constants';
import {ENABLE_FORM_ABANDONMENT} from 'shared/util/feature-flags';
import {IAssetMetricCardProps} from 'assets/components/AssetMetricCard';
import {MetricName} from 'shared/types/MetricName';
import {Name} from 'shared/components/audience-report/types';

/**
 * Everything the asset dashboard renders differently from one asset type to
 * the next. Identity and routing live in `assets/descriptors`; this is the
 * presentation half, kept apart so the asset list does not pull the dashboard
 * cards into its own bundle.
 *
 * `DevicesCard` and `LocationsCard` stay per asset type because their GraphQL
 * documents genuinely differ — each selects a different set of scalar fields
 * alongside the metric, and object entries query without `channelId` and
 * `title`.
 */
export interface AssetOverviewConfig {
	appearsOn: {
		accessors: Accessor[];
		assetType: AssetTypes;
		emptyStateLink: EmptyStateLink;
		emptyStateText: EmptyStateText;
	};
	audienceReport: {
		knownIndividualsTitle: string;
		metricName: MetricName;
		name: Name;
		segmentsTitle?: string;
		uniqueVisitorsTitle: string;
	};

	// The devices and locations cards are untyped JavaScript HOCs.

	DevicesCard: React.ComponentType<any>;
	devicesLabel: string;
	LocationsCard: React.ComponentType<any>;
	locationsLabel: string;
	metricCard: Omit<IAssetMetricCardProps, 'label'>;

	/**
	 * Passed to both the locations and the technology card by the asset types
	 * whose metric is not views.
	 */
	metricLabel?: string;

	renderExtraCards?: () => React.ReactNode;
}

const ASSET_OVERVIEW_CONFIGS: Record<string, AssetOverviewConfig> = {
	blogs: {
		appearsOn: {
			accessors: [Accessor.ViewsMetric],
			assetType: AssetTypes.Blog,
			emptyStateLink: EmptyStateLink.Blog,
			emptyStateText: EmptyStateText.Blog,
		},
		audienceReport: {
			knownIndividualsTitle: Liferay.Language.get('segmented-views'),
			metricName: MetricName.Views,
			name: Name.Blog,
			uniqueVisitorsTitle: Liferay.Language.get('views'),
		},
		DevicesCard: BlogDevicesCard,
		devicesLabel: Liferay.Language.get('views-by-technology'),
		LocationsCard: BlogLocationsCard,
		locationsLabel: Liferay.Language.get('views-by-location'),
		metricCard: {
			documentationURL: URLConstants.VisitorBehaviorBlogsLink,
			metrics: [
				ViewsMetric,
				ReadingTimeMetric,
				CommentsMetric,
				RatingsMetric,
			],
			name: 'blog',
		},
	},
	'documents-and-media': {
		appearsOn: {
			accessors: [
				Accessor.DownloadsMetric,
				Accessor.ImpressionMadeMetric,
			],
			assetType: AssetTypes.Document,
			emptyStateLink: EmptyStateLink.Document,
			emptyStateText: EmptyStateText.Document,
		},
		audienceReport: {
			knownIndividualsTitle: Liferay.Language.get('segmented-downloads'),
			metricName: MetricName.Downloads,
			name: Name.Document,
			segmentsTitle: Liferay.Language.get('downloaded-segments'),
			uniqueVisitorsTitle: Liferay.Language.get('downloads'),
		},
		DevicesCard: DocumentDevicesCard,
		devicesLabel: Liferay.Language.get('downloads-by-technology'),
		LocationsCard: DocumentLocationsCard,
		locationsLabel: Liferay.Language.get('downloads-by-location'),
		metricCard: {
			documentationURL: URLConstants.VisitorBehaviorDocumentsAndMediaLink,
			metrics: [
				DownloadsMetric,
				ImpressionMadeMetric,
				CommentsMetric,
				RatingsMetric,
			],
			name: 'document',
		},
		metricLabel: Liferay.Language.get('downloads'),
	},
	forms: {
		appearsOn: {
			accessors: [Accessor.SubmissionsMetric, Accessor.ViewsMetric],
			assetType: AssetTypes.Form,
			emptyStateLink: EmptyStateLink.Form,
			emptyStateText: EmptyStateText.Form,
		},
		audienceReport: {
			knownIndividualsTitle: Liferay.Language.get(
				'segmented-submissions'
			),
			metricName: MetricName.Submissions,
			name: Name.Form,
			segmentsTitle: Liferay.Language.get('submitter-segments'),
			uniqueVisitorsTitle: Liferay.Language.get('submissions'),
		},
		DevicesCard: FormDevicesCard,
		devicesLabel: Liferay.Language.get('submissions-by-technology'),
		LocationsCard: FormLocationsCard,
		locationsLabel: Liferay.Language.get('submissions-by-location'),
		metricCard: {
			documentationURL: URLConstants.VisitorBehaviorFormsLink,
			legacyDropdownRangeKey: false,
			metrics: [
				SubmissionsMetric,
				ViewsMetric,
				AbandonmentsMetric,
				CompletionTimeMetric,
			],
			name: 'form',
		},
		metricLabel: Liferay.Language.get('submissions'),
		renderExtraCards: () =>
			ENABLE_FORM_ABANDONMENT && (
				<div className="row">
					<div className="col-sm-12">
						<FormAbandonmentCard
							label={Liferay.Language.get('form-abandonment')}
							legacyDropdownRangeKey={false}
						/>
					</div>
				</div>
			),
	},
	'object-entry': {
		appearsOn: {
			accessors: [
				Accessor.ImpressionMadeMetric,
				Accessor.ViewsMetric,
				Accessor.DownloadsMetric,
			],
			assetType: AssetTypes.ObjectEntry,
			emptyStateLink: EmptyStateLink.ObjectEntry,
			emptyStateText: EmptyStateText.ObjectEntry,
		},
		audienceReport: {
			knownIndividualsTitle: Liferay.Language.get('segmented-views'),
			metricName: MetricName.Views,
			name: Name.ObjectEntry,
			uniqueVisitorsTitle: Liferay.Language.get('views'),
		},
		DevicesCard: ObjectEntryDevicesCard,
		devicesLabel: Liferay.Language.get('views-by-technology'),
		LocationsCard: ObjectEntryLocationsCard,
		locationsLabel: Liferay.Language.get('views-by-location'),
		metricCard: {
			documentationURL: URLConstants.VisitorBehaviorWebContentLink,
			metrics: [ImpressionMadeMetric, ViewsMetric, DownloadsMetric],
			name: 'objectEntry',
			variableType: 'objectEntry',
		},
	},
	'web-content': {
		appearsOn: {
			accessors: [Accessor.ViewsMetric],
			assetType: AssetTypes.Journal,
			emptyStateLink: EmptyStateLink.Journal,
			emptyStateText: EmptyStateText.Journal,
		},
		audienceReport: {
			knownIndividualsTitle: Liferay.Language.get('segmented-views'),
			metricName: MetricName.Views,
			name: Name.Journal,
			uniqueVisitorsTitle: Liferay.Language.get('views'),
		},
		DevicesCard: JournalDevicesCard,
		devicesLabel: Liferay.Language.get('views-by-technology'),
		LocationsCard: JournalLocationsCard,
		locationsLabel: Liferay.Language.get('views-by-location'),
		metricCard: {
			documentationURL: URLConstants.VisitorBehaviorWebContentLink,
			metrics: [ViewsMetric],
			name: 'journal',
		},
	},
};

export const getAssetOverviewConfig = (slug: string): AssetOverviewConfig =>
	ASSET_OVERVIEW_CONFIGS[slug];
