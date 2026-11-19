import BundleRouter from 'route-middleware/BundleRouter';
import React from 'react';
import SectionApp from '../SectionApp';
import SettingsPage from 'settings/pages/Settings';
import {RouteObject} from 'react-router-dom';

interface ISettingsAppProps {
	channelId: string;
	groupId: string;
}

interface IConfigureCSVAppProps extends ISettingsAppProps {
	params: {fileVersionId?: string; id: string; name?: string};
}

interface IDataPrivacyAppProps extends ISettingsAppProps {
	params: {statuses?: string; types?: string; view?: string};
}

interface IDataSourceAppProps extends ISettingsAppProps {
	params: {id: string};
}

interface IDataSourceOnboardingAppProps extends ISettingsAppProps {
	params: {dataSourceId?: string; id: string; stepIndex?: string};
}

interface IEditRecommendationAppProps extends ISettingsAppProps {
	params: {jobId?: string};
}

interface IEventAttributeAppProps extends ISettingsAppProps {
	params: {attributeId: string};
}

interface IEventDefinitionAppProps extends ISettingsAppProps {
	params: {eventId: string};
}

interface IEventTabAppProps extends ISettingsAppProps {
	params: {tab?: string};
}

interface IPropertyAppProps extends ISettingsAppProps {
	params: {id: string};
}

interface IRecommendationAppProps extends ISettingsAppProps {
	params: {jobId: string};
}

const SETTINGS_ROUTES: RouteObject[] = [
	{
		element: <BundleRouter data={SettingsPage} />,
		path: 'settings/*',
	},
];

const AccessRequestsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/users/requests"
		routes={SETTINGS_ROUTES}
	/>
);

const ApiTokensApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/apis/tokens"
		routes={SETTINGS_ROUTES}
	/>
);

const BehaviorsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/definitions/behaviors"
		routes={SETTINGS_ROUTES}
	/>
);

const ConfigureCSVApp: React.FC<IConfigureCSVAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/data-source/${params.id}/edit`}
		routes={SETTINGS_ROUTES}
	/>
);

const DataPrivacyApp: React.FC<IDataPrivacyAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={
			params.view
				? `settings/data-privacy/${params.view}`
				: 'settings/data-privacy'
		}
		routes={SETTINGS_ROUTES}
	/>
);

const DataSourceApp: React.FC<IDataSourceAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/data-source/${params.id}`}
		routes={SETTINGS_ROUTES}
	/>
);

const DataSourceOnboardingApp: React.FC<IDataSourceOnboardingAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/data-source/${params.id}/onboarding`}
		routes={SETTINGS_ROUTES}
	/>
);

const DataSourcesApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/data-source"
		routes={SETTINGS_ROUTES}
	/>
);

const DefinitionsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/definitions"
		routes={SETTINGS_ROUTES}
	/>
);

const DefinitionsSearchApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/definitions/search"
		routes={SETTINGS_ROUTES}
	/>
);

const DeleteDataSourceApp: React.FC<IDataSourceAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/data-source/${params.id}/delete`}
		routes={SETTINGS_ROUTES}
	/>
);

const EditDataSourceApp: React.FC<IDataSourceAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/data-source/${params.id}/edit`}
		routes={SETTINGS_ROUTES}
	/>
);

const EditRecommendationApp: React.FC<IEditRecommendationAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={
			params.jobId
				? `settings/recommendations/${params.jobId}/edit`
				: 'settings/recommendations/create-item-similarity-model'
		}
		routes={SETTINGS_ROUTES}
	/>
);

const EventAttributeApp: React.FC<IEventAttributeAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/definitions/event-attributes/${params.attributeId}`}
		routes={SETTINGS_ROUTES}
	/>
);

const EventAttributesApp: React.FC<IEventTabAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/definitions/event-attributes/${params.tab || 'global'}`}
		routes={SETTINGS_ROUTES}
	/>
);

const EventDefinitionApp: React.FC<IEventDefinitionAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/definitions/events/${params.eventId}`}
		routes={SETTINGS_ROUTES}
	/>
);

const EventDefinitionsApp: React.FC<IEventTabAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/definitions/events/${params.tab || 'default'}`}
		routes={SETTINGS_ROUTES}
	/>
);

const FeatureFlagsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/feature-flags"
		routes={SETTINGS_ROUTES}
	/>
);

const IndividualAttributesApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/definitions/individual-attributes"
		routes={SETTINGS_ROUTES}
	/>
);

const InterestTopicsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/definitions/interest-topics"
		routes={SETTINGS_ROUTES}
	/>
);

const PropertiesApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/properties"
		routes={SETTINGS_ROUTES}
	/>
);

const PropertyApp: React.FC<IPropertyAppProps> = ({params, ...props}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/properties/${params.id}`}
		routes={SETTINGS_ROUTES}
	/>
);

const RecommendationApp: React.FC<IRecommendationAppProps> = ({
	params,
	...props
}) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path={`settings/recommendations/${params.jobId}`}
		routes={SETTINGS_ROUTES}
	/>
);

const RecommendationsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/recommendations"
		routes={SETTINGS_ROUTES}
	/>
);

const SettingsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/data-source"
		routes={SETTINGS_ROUTES}
	/>
);

const UsageApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/usage"
		routes={SETTINGS_ROUTES}
	/>
);

const UsersApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/users"
		routes={SETTINGS_ROUTES}
	/>
);

const WorkspaceSettingsApp: React.FC<ISettingsAppProps> = (props) => (
	<SectionApp
		{...props}
		channelScoped={false}
		path="settings/workspace"
		routes={SETTINGS_ROUTES}
	/>
);

export {
	AccessRequestsApp as AccessRequests,
	ApiTokensApp as ApiTokens,
	BehaviorsApp as Behaviors,
	ConfigureCSVApp as ConfigureCSV,
	DataPrivacyApp as DataPrivacy,
	DataSourceApp as DataSource,
	DataSourceOnboardingApp as DataSourceOnboarding,
	DataSourcesApp as DataSources,
	DefinitionsApp as Definitions,
	DefinitionsSearchApp as DefinitionsSearch,
	DeleteDataSourceApp as DeleteDataSource,
	EditDataSourceApp as EditDataSource,
	EditRecommendationApp as EditRecommendation,
	EventAttributeApp as EventAttribute,
	EventAttributesApp as EventAttributes,
	EventDefinitionApp as EventDefinition,
	EventDefinitionsApp as EventDefinitions,
	FeatureFlagsApp as FeatureFlags,
	IndividualAttributesApp as IndividualAttributes,
	InterestTopicsApp as InterestTopics,
	PropertiesApp as Properties,
	PropertyApp as Property,
	RecommendationApp as Recommendation,
	RecommendationsApp as Recommendations,
	SettingsApp as Settings,
	UsageApp as Usage,
	UsersApp as Users,
	WorkspaceSettingsApp as WorkspaceSettings,
};
