import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/BasePage';
import getCN from 'classnames';
import React from 'react';
import SelectDataSource from '../components/SelectDataSource';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose, withAdminPermission} from 'shared/hoc';
import {connect} from 'react-redux';
import {ENABLE_CSVFILE} from 'shared/util/constants';
import {ENABLE_SALESFORCE} from 'shared/util/constants';
import {Routes, toRoute} from 'shared/util/router';

interface IAddDataSourceProps extends React.HTMLAttributes<HTMLDivElement> {
	close: () => void;
	groupId: string;
	open: (
		modalType: string,
		{groupId, onClose}: {groupId: string; onClose: () => void}
	) => void;
}

export const AddDataSource: React.FC<IAddDataSourceProps> = ({
	className,
	close,
	groupId,
	open
}) => {
	const getDataSourceSections = () => [
		{
			dataSources: [
				{
					iconName: 'liferay_logo',
					iconSize: 'xxxl',
					name: Liferay.Language.get('liferay-dxp'),
					onClick: () => {
						open(modalTypes.CONNECT_DXP_MODAL, {
							groupId,
							onClose: close
						});
					}
				},
				ENABLE_SALESFORCE && {
					iconName: 'salesforce_logo',
					iconSize: 'xxl',
					name: Liferay.Language.get('salesforce'),
					subtitle: Liferay.Language.get(
						'import-contacts-accounts-&-leads'
					),
					url: toRoute(Routes.SETTINGS_SALESFORCE_ADD, {groupId})
				},
				ENABLE_CSVFILE && {
					iconName: 'csv_logo',
					iconSize: 'xl',
					name: Liferay.Language.get('csv-file'),
					subtitle: Liferay.Language.get(
						'import-contacts-from-a-csv'
					),
					url: toRoute(Routes.SETTINGS_CSV_UPLOAD, {groupId})
				}
			].filter(Boolean) as [],
			title: Liferay.Language.get('choose-source')
		}
	];

	return (
		<BasePage
			breadcrumbItems={[
				breadcrumbs.getDataSources({groupId}),
				{
					active: true,
					label: Liferay.Language.get('add-data-source')
				}
			]}
			className={getCN('add-data-source-root', className)}
			groupId={groupId}
			pageDescription={
				ENABLE_CSVFILE
					? Liferay.Language.get('add-a-data-source-description')
					: Liferay.Language.get(
							'add-a-data-source-description-no-csv'
					  )
			}
			pageTitle={Liferay.Language.get('add-data-source')}
		>
			<SelectDataSource sections={getDataSourceSections()} />
		</BasePage>
	);
};

export default compose(
	withAdminPermission,
	connect(null, {close, open})
)(AddDataSource);
