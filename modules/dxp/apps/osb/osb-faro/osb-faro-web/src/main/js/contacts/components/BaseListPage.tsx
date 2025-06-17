import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import EmbeddedAlertList, {
	IEmbeddedAlertListProps
} from 'shared/components/EmbeddedAlertList';
import NoResultsDisplay, {
	INoResultsDisplayProps
} from 'shared/components/NoResultsDisplay';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {FetchSegmentsParams} from 'segment/pages/List';
import {FilterOptionType} from 'shared/types';
import {get} from 'lodash';
import {NAME} from 'shared/util/pagination';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useChannelContext} from 'shared/context/channel';
import {useDataSource} from 'shared/hooks/useDataSource';
import {useParams} from 'react-router-dom';
import {User} from 'shared/util/records';

interface IBaseListPageProps {
	alerts?: IEmbeddedAlertListProps[];
	className?: string;
	columns: {
		accessor: string;
		label: any;
	}[];
	currentUser: User;
	dataSourceFn: (params: FetchSegmentsParams) => any;
	delta?: number;
	emptyStateTitle?: string;
	entityLabel?: string;
	filterBy?: object;
	filterByOptions?: FilterOptionType[];
	forwardedRef?: React.Ref<any>;
	hideNav?: boolean;
	noResultsConfig?: INoResultsDisplayProps;
	orderByOptions?: {label: string; value: string}[];
	orderIOMap?: object;
	page?: number;
	pageActions?: any[];
	pageActionsLabel?: string;
	query?: string;
	ref?: React.RefObject<SearchableEntityTable>;
	renderRowActions?: any;
	renderSelectedAction?: (checkedItemsISet) => any;
	rowIdentifier?: string;
	showCheckbox?: boolean;
}

const BaseListPage: React.FC<IBaseListPageProps> = ({
	alerts = [],
	className,
	columns,
	currentUser,
	dataSourceFn,
	delta,
	emptyStateTitle = Liferay.Language.get('no-data-sources-connected'),
	entityLabel,
	filterBy,
	filterByOptions,
	forwardedRef,
	noResultsConfig,
	orderByOptions = [
		{
			label: Liferay.Language.get('name'),
			value: NAME
		}
	],
	orderIOMap,
	page,
	pageActions,
	pageActionsLabel,
	query,
	rowIdentifier = 'id',
	showCheckbox = false,
	...otherProps
}) => {
	const {selectedChannel} = useChannelContext();
	const {channelId, groupId} = useParams();
	const authorized = currentUser.isAdmin();

	const dataSourceStates = useDataSource();
	const {empty, error, loading} = dataSourceStates;

	const ConnectDataSourceButton = () => (
		<ClayLink
			button
			className='button-root'
			displayType='primary'
			href={toRoute(Routes.SETTINGS_ADD_DATA_SOURCE, {
				channelId,
				groupId
			})}
		>
			{Liferay.Language.get('connect-data-source')}
		</ClayLink>
	);

	const renderNoResults = (query, activeFilters) => {
		if (query || activeFilters) {
			return (
				<NoResultsDisplay
					description={Liferay.Language.get(
						'please-try-a-different-search-term'
					)}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_no_results_found'
					}}
					title={Liferay.Language.get('there-are-no-results-found')}
				/>
			);
		} else {
			return (
				<NoResultsDisplay
					description={get(noResultsConfig, 'description')}
					icon={get(noResultsConfig, 'icon')}
					primary
					title={get(noResultsConfig, 'title')}
				/>
			);
		}
	};

	return (
		<BasePage className={className} documentTitle={entityLabel}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name
					})
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection title={entityLabel} />

					<BasePage.Header.Section>
						<BasePage.Header.PageActions
							actions={pageActions}
							disabled={empty || error || loading}
							label={pageActionsLabel}
						/>
					</BasePage.Header.Section>
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.Body>
				<EmbeddedAlertList alerts={alerts} />

				<Card pageDisplay>
					<Card.Body noPadding>
						<StatesRenderer {...dataSourceStates}>
							<StatesRenderer.Loading />

							<StatesRenderer.Empty
								description={
									authorized ? (
										<>
											{Liferay.Language.get(
												'connect-a-data-source-to-get-started'
											)}

											<ClayLink
												className='d-block mb-3'
												href={
													URLConstants.DataSourceConnection
												}
												key='DOCUMENTATION'
												target='_blank'
											>
												{Liferay.Language.get(
													'access-our-documentation-to-learn-more'
												)}
											</ClayLink>

											<ConnectDataSourceButton />
										</>
									) : (
										Liferay.Language.get(
											'please-contact-your-site-administrator-to-add-people-data-sources'
										)
									)
								}
								displayCard
								title={emptyStateTitle}
							/>

							<StatesRenderer.Success>
								<SearchableEntityTable
									{...otherProps}
									columns={columns}
									dataSourceFn={dataSourceFn}
									dataSourceParams={{channelId, groupId}}
									delta={delta}
									entityLabel={entityLabel}
									filterBy={filterBy}
									filterByOptions={filterByOptions}
									noResultsRenderer={renderNoResults}
									orderByOptions={orderByOptions}
									orderIOMap={orderIOMap}
									page={page}
									query={query}
									ref={forwardedRef}
									rowIdentifier={rowIdentifier}
									showCheckbox={showCheckbox}
								/>
							</StatesRenderer.Success>
						</StatesRenderer>
					</Card.Body>
				</Card>
			</BasePage.Body>
		</BasePage>
	);
};

export default React.forwardRef<HTMLDivElement, IBaseListPageProps>(
	(props, ref) => (
		<BaseListPage {...props} forwardedRef={ref}>
			{props.children}
		</BaseListPage>
	)
);
