import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/BasePage';
import DeleteDataSource from 'settings/components/DeleteDataSource';
import getCN from 'classnames';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {
	compose,
	withAdminPermission,
	withDataSource,
	withHistory,
	withRequest,
	withSheet
} from 'shared/hoc';
import {connect} from 'react-redux';
import {DataSource} from 'shared/util/records';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {truncate} from 'lodash';

const WrappedDeleteDataSource = compose<any>(
	withSheet(),
	withRequest(
		API.dataSource.fetchDeletePreview,
		data => ({entitiesCount: data}),
		{
			page: false
		}
	)
)(DeleteDataSource);

type History = {
	push: (path: string) => void;
};

interface IClearDataProps extends React.HTMLAttributes<HTMLElement> {
	addAlert: Alert.AddAlert;
	dataSource: DataSource;
	entitiesCount: number;
	groupId: string;
	history: History;
	id: string;
}

export const ClearData: React.FC<IClearDataProps> = ({
	addAlert,
	className,
	dataSource,
	entitiesCount,
	groupId,
	history,
	id
}) => (
	<BasePage
		breadcrumbItems={[
			breadcrumbs.getDataSources({groupId}),
			breadcrumbs.getDataSourceName({
				groupId,
				href: toRoute(Routes.SETTINGS_DATA_SOURCE, {
					groupId,
					id
				}),
				id,
				label: dataSource.name
			}),
			{
				active: true,
				label: Liferay.Language.get('clear-data')
			}
		]}
		className={getCN('data-source-clear-data-root', className)}
		documentTitle={
			sub(Liferay.Language.get('confirm-clearing-of-all-data-from-x'), [
				dataSource.name
			]) as string
		}
		pageDescription={Liferay.Language.get(
			'the-following-data-will-be-impacted-and-can-yield-unexpected-results.-this-action-will-not-remove-the-data-source'
		)}
		pageTitle={sub(
			Liferay.Language.get('confirm-clearing-of-all-data-from-x'),
			[dataSource.name]
		)}
	>
		<div className='page-container'>
			<WrappedDeleteDataSource
				actionRequestFn={() =>
					API.dataSource
						.clearData({
							groupId,
							id
						})
						.then(() => {
							addAlert({
								alertType: Alert.Types.Default,
								message: sub(
									Liferay.Language.get(
										'data-from-x-is-currently-being-removed-from-analytics-cloud'
									),
									[truncate(dataSource.name, {length: 50})]
								) as string
							});

							history.push(
								toRoute(Routes.SETTINGS_DATA_SOURCE, {
									groupId,
									id
								})
							);
						})
						.catch(() => {
							addAlert({
								alertType: Alert.Types.Error,
								message: Liferay.Language.get('error'),
								timeout: false
							});
						})
				}
				dataSource={dataSource}
				deleteMessage={sub(
					Liferay.Language.get(
						'to-complete,-copy-the-following-sentence-to-confirm-your-intention-and-click-x.-once-you-have-x,-you-can-not-undo-this-operation'
					),
					[
						Liferay.Language.get('clear-data').toLowerCase(),
						Liferay.Language.get('deleted-this-data-fragment')
					]
				)}
				deletePhrase={Liferay.Language.get('clear-x-data')}
				entitiesCount={entitiesCount}
				groupId={groupId}
				id={id}
				pageActionConfirmationText={sub(
					Liferay.Language.get(
						'are-you-sure-you-want-to-clear-data-from-x'
					),
					[dataSource.name]
				)}
				pageActionText={Liferay.Language.get('clear-data')}
			/>
		</div>
	</BasePage>
);

export default compose(
	withHistory,
	withAdminPermission,
	withDataSource,
	connect(null, {addAlert})
)(ClearData);
