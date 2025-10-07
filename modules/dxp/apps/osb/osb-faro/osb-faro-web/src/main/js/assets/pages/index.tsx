import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import ClayLink from '@clayui/link';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {CSVType} from 'shared/components/download-report/utils';
import {getMatchedRoute, Routes, toRoute} from 'shared/util/router';
import {Router} from 'shared/types';
import {Switch, useParams} from 'react-router-dom';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSource} from 'shared/hooks/useDataSource';
import {User} from 'shared/util/records';

const BlogsList = lazy(
	() => import(/* webpackChunkName: "BlogsList" */ './BlogsList')
);

const DocumentsAndMediaList = lazy(
	() =>
		import(
			/* webpackChunkName: "DocumentsAndMediaList" */ './DocumentsAndMediaList'
		)
);

const FormsList = lazy(
	() => import(/* webpackChunkName: "FormsList" */ './FormsList')
);

const WebContentList = lazy(
	() => import(/* webpackChunkName: "WebContentList" */ './WebContentList')
);

const NAV_ITEMS = [
	{
		exact: true,
		label: Liferay.Language.get('blogs'),
		route: Routes.ASSETS_BLOGS
	},
	{
		exact: true,
		label: Liferay.Language.get('documents-and-media'),
		route: Routes.ASSETS_DOCUMENTS_AND_MEDIA
	},
	{
		exact: true,
		label: Liferay.Language.get('forms'),
		route: Routes.ASSETS_FORMS
	},
	{
		exact: true,
		label: Liferay.Language.get('web-content'),
		route: Routes.ASSETS_WEB_CONTENT
	}
];

interface IAssetsProps extends React.HTMLAttributes<HTMLElement> {
	currentUser: User;
	router: Router;
}

const Assets: React.FC<IAssetsProps> = ({className, router}) => {
	const {channelId, groupId} = useParams();
	const dataSourceStates = useDataSource();
	const {selectedChannel} = useChannelContext();
	const currentUser = useCurrentUser();

	const authorized = currentUser.isAdmin();

	return (
		<BasePage
			className={className}
			documentTitle={Liferay.Language.get('assets')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel?.name
					})
				]}
				groupId={groupId}
			>
				<BasePage.Header.TitleSection
					title={Liferay.Language.get('assets')}
				/>

				<BasePage.Header.NavBar
					items={NAV_ITEMS}
					routeParams={{channelId, groupId}}
				/>
			</BasePage.Header>
			{getMatchedRoute(NAV_ITEMS) === Routes.ASSETS_BLOGS && (
				<BasePage.SubHeader>
					<div className='d-flex justify-content-end w-100'>
						<DownloadCSVReport
							disabled={dataSourceStates.empty}
							type={CSVType.Blog}
							typeLang={Liferay.Language.get('blogs')}
						/>
					</div>
				</BasePage.SubHeader>
			)}
			{getMatchedRoute(NAV_ITEMS) ===
				Routes.ASSETS_DOCUMENTS_AND_MEDIA && (
				<BasePage.SubHeader>
					<div className='d-flex justify-content-end w-100'>
						<DownloadCSVReport
							disabled={dataSourceStates.empty}
							type={CSVType.Document}
							typeLang={Liferay.Language.get(
								'documents-and-media'
							)}
						/>
					</div>
				</BasePage.SubHeader>
			)}
			{getMatchedRoute(NAV_ITEMS) === Routes.ASSETS_FORMS && (
				<BasePage.SubHeader>
					<div className='d-flex justify-content-end w-100'>
						<DownloadCSVReport
							disabled={dataSourceStates.empty}
							type={CSVType.Form}
							typeLang={Liferay.Language.get('forms')}
						/>
					</div>
				</BasePage.SubHeader>
			)}
			{getMatchedRoute(NAV_ITEMS) === Routes.ASSETS_WEB_CONTENT && (
				<BasePage.SubHeader>
					<div className='d-flex justify-content-end w-100'>
						<DownloadCSVReport
							disabled={dataSourceStates.empty}
							type={CSVType.Journal}
							typeLang={Liferay.Language.get('web-content')}
						/>
					</div>
				</BasePage.SubHeader>
			)}
			<BasePage.Body>
				<BasePage.Context.Provider
					value={{
						filters: {},
						router
					}}
				>
					<Suspense fallback={<Loading />}>
						<StatesRenderer {...dataSourceStates}>
							<StatesRenderer.Empty
								description={
									<>
										{authorized
											? Liferay.Language.get(
													'connect-a-data-source-with-sites-data'
											  )
											: Liferay.Language.get(
													'please-contact-your-workspace-administrator-to-add-data-sources'
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

										{authorized && (
											<ClayLink
												button
												className='button-root'
												displayType='primary'
												href={toRoute(
													Routes.SETTINGS_ADD_DATA_SOURCE,
													{
														groupId
													}
												)}
											>
												{Liferay.Language.get(
													'connect-data-source'
												)}
											</ClayLink>
										)}
									</>
								}
								displayCard
								title={Liferay.Language.get(
									'no-sites-synced-from-data-sources'
								)}
							/>

							<StatesRenderer.Success>
								<Switch>
									<BundleRouter
										data={BlogsList}
										destructured={false}
										exact
										path={Routes.ASSETS_BLOGS}
									/>

									<BundleRouter
										data={DocumentsAndMediaList}
										destructured={false}
										exact
										path={Routes.ASSETS_DOCUMENTS_AND_MEDIA}
									/>

									<BundleRouter
										data={FormsList}
										destructured={false}
										exact
										path={Routes.ASSETS_FORMS}
									/>

									<BundleRouter
										data={WebContentList}
										destructured={false}
										exact
										path={Routes.ASSETS_WEB_CONTENT}
									/>

									<RouteNotFound />
								</Switch>
							</StatesRenderer.Success>
						</StatesRenderer>
					</Suspense>
				</BasePage.Context.Provider>
			</BasePage.Body>
		</BasePage>
	);
};

export default Assets;
