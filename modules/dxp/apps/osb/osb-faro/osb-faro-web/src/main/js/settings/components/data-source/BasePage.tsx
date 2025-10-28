import BasePage from 'settings/components/BasePage';
import DataSourceStatus from './DataSourceStatus';
import getCN from 'classnames';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert, Modal} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {DataSource, User} from 'shared/util/records';
import {deleteDataSource} from 'shared/actions/data-sources';
import {ENABLE_DELETE_DATA_SOURCE_BUTTON} from 'shared/util/constants';
import {
	getDataSourceDisplayObject,
	hasLegacyDXPConnection
} from 'shared/util/data-sources';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {truncate} from 'lodash';
import {withHistory} from 'shared/hoc';

const getPageDescription = dataSource =>
	dataSource
		? [dataSource.name, dataSource.url]
				.filter(item => item)
				.map(item => truncate(item, {length: 50}))
				.join(' - ')
		: '';

interface IBaseDataSourcePageProps extends React.HTMLAttributes<HTMLElement> {
	addAlert: Alert.AddAlert;
	close: Modal.close;
	currentUser: User;
	documentTitle: string;
	dataSource: DataSource;
	deleteDataSource: ({
		groupId,
		id
	}: {
		groupId: string;
		id: string;
	}) => Promise<void>;
	groupId: string;
	history: {
		push: (path: string) => void;
	};
	id: string;
	open: Modal.open;
	pageDescription: string;
	pageTitle: React.ReactNode;
	passedChildren: React.ReactNode;
	showDelete: boolean;
}

const BaseDataSourcePage: React.FC<IBaseDataSourcePageProps> = ({
	addAlert,
	className = '',
	close,
	currentUser,
	dataSource,
	deleteDataSource,
	documentTitle = Liferay.Language.get('configure-data-source'),
	groupId,
	history,
	id,
	open,
	pageDescription,
	pageTitle = Liferay.Language.get('configure-data-source'),
	passedChildren,
	showDelete = false,
	...otherProps
}) => {
	const name = dataSource ? dataSource.name : '';

	const handleDeleteClick = () => {
		open(modalTypes.DELETE_CONFIRMATION_MODAL, {
			children: (
				<p>
					<strong>
						{sub(
							Liferay.Language.get(
								'to-delete-x,-copy-the-sentence-below-to-confirm-your-intention-to-delete-data-source'
							),
							[name]
						)}
					</strong>
				</p>
			),
			deleteConfirmationText: sub(Liferay.Language.get('delete-x'), [
				name
			]),
			onClose: close,
			onSubmit: () => {
				deleteDataSource({groupId, id})
					.then(() => {
						addAlert({
							alertType: Alert.Types.Default,
							message: sub(
								Liferay.Language.get(
									'x-is-currently-being-removed-from-analytics-cloud'
								),
								[truncate(name, {length: 50})]
							) as string
						});

						close();

						history.push(
							toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
								groupId
							})
						);
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get('error'),
							timeout: false
						});
					});
			},
			title: sub(Liferay.Language.get('delete-x'), [name])
		});
	};

	return (
		<BasePage
			{...otherProps}
			className={getCN('data-source-base-page-root', className)}
			documentTitle={`${
				documentTitle || pageTitle
			} - ${Liferay.Language.get('data-sources')}`}
			pageActions={
				ENABLE_DELETE_DATA_SOURCE_BUTTON &&
				id &&
				showDelete &&
				currentUser.isAdmin()
					? [
							{
								displayType: 'secondary',
								label: Liferay.Language.get(
									'delete-data-source'
								),
								onClick: handleDeleteClick
							}
					  ]
					: []
			}
			pageDescription={pageDescription || getPageDescription(dataSource)}
			pageTitle={pageTitle}
		>
			<div className='page-container'>
				<div className='content-main'>{passedChildren}</div>

				<div className='content-side'>
					{((dataSource && !hasLegacyDXPConnection(dataSource)) ||
						!dataSource) && (
						<DataSourceStatus
							{...getDataSourceDisplayObject(dataSource)}
						/>
					)}
				</div>
			</div>
		</BasePage>
	);
};

const getOwnChildren = (store, ownProps) => ({
	passedChildren: ownProps.children
});

export default compose<any>(
	withHistory,
	connect(getOwnChildren, {addAlert, close, deleteDataSource, open})
)(BaseDataSourcePage);
