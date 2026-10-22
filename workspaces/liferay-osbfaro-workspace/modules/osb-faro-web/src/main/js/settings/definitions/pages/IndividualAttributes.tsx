import * as API from 'shared/api';
import BasePage from 'settings/components/base-page/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import DataValue from 'shared/components/DataValue';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import IndividualAttributesModal from 'shared/components/modals/IndividualAttributesModal';
import React, {useState} from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import URLConstants from 'shared/util/url-constants';
import withStatefulPagination from 'shared/hoc/StatefulPagination';
import {applyTimeZone, formatRelativeTime} from 'shared/util/date';
import {createOrderIOMap} from 'shared/util/pagination';
import {getDefinitions} from 'shared/util/breadcrumbs';
import {omit} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useModal} from '@clayui/modal';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {User} from 'shared/util/records';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const SearchableEntityTableHOC = withStatefulPagination(
	SearchableEntityTable,
	{
		initialOrderIOMap: createOrderIOMap('fieldName'),
	},
	(props: {[key: string]: any}) => omit(props, 'onSearchValueChange')
);

interface IIndividualAttributesProps extends React.HTMLAttributes<HTMLElement> {
	currentUser: User;
	groupId: string;
}

const IndividualAttributes: React.FC<IIndividualAttributesProps> = ({
	groupId,
}) => {
	const currentUser = useCurrentUser();
	const {timeZoneId} = useTimeZone();

	const attributesModal = useModal();

	const [selectedAttribute, setSelectedAttribute] = useState<{
		dataSources: any;
		fieldName: any;
	}>();

	const openModal =
		({dataSources, fieldName}: {dataSources: any; fieldName: any}) =>
		() => {
			setSelectedAttribute({dataSources, fieldName});

			attributesModal.onOpenChange(true);
		};

	const authorized = currentUser.isAdmin();

	const FieldNameCell = ({
		data: {dataSources, fieldName},
	}: {
		data: {dataSources: any; fieldName: any};
	}) => (
		<td className="table-cell-expand">
			<div className="content-container">
				<ClayButton
					className="button-root"
					displayType="unstyled"
					onClick={openModal({dataSources, fieldName})}
				>
					{fieldName}
				</ClayButton>
			</div>
		</td>
	);

	return (
		<BasePage
			breadcrumbItems={[
				getDefinitions({groupId}),
				{
					active: true,
					label: Liferay.Language.get('individual-attributes'),
				},
			]}
			className="individual-attributes-root"
			pageDescription={Liferay.Language.get(
				'this-is-the-data-model-of-an-individual.-analytics-cloud-will-take-and-store-the-newest-data-from-all-your-sources'
			)}
			pageTitle={Liferay.Language.get('individual-attributes')}
		>
			<Card>
				<SearchableEntityTableHOC
					columns={[
						{
							accessor: 'fieldName',
							cellRenderer: FieldNameCell,
							className: 'table-cell-expand',
							label: Liferay.Language.get('attribute[noun]'),
						},
						{
							accessor: 'dataSources',
							className: 'pr-6',
							dataFormatter: (dataSources: any[]) =>
								dataSources.length > 1 ? (
									sub(Liferay.Language.get('x-sources'), [
										dataSources.length,
									])
								) : (
									<DataValue
										value={dataSources[0]?.dataSourceName}
									/>
								),
							label: Liferay.Language.get('sources'),
						},
						{
							accessor: 'dateModified',
							className: 'pr-5',
							dataFormatter: (dateModified: string) =>
								formatRelativeTime(
									applyTimeZone(dateModified, timeZoneId)
								),
							label: Liferay.Language.get('last-synced'),
							sortable: false,
						},
					]}
					dataSourceFn={API.definitions.searchIndividualAttributes}
					dataSourceParams={{groupId}}
					internalSort
					noResultsRenderer={() => (
						<NoResultsDisplay
							description={
								<>
									{Liferay.Language.get(
										'connect-a-data-source-with-people-data'
									)}

									<ClayLink
										className="d-block mb-3"
										href={URLConstants.DataSourceConnection}
										key="DOCUMENTATION"
										target="_blank"
									>
										{Liferay.Language.get(
											'access-our-documentation-to-learn-more'
										)}
									</ClayLink>

									{authorized && (
										<ClayLink
											button
											className="button-root"
											displayType="primary"
											href={toRoute(
												Routes.SETTINGS_DATA_SOURCE_LIST,
												{
													groupId,
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
							icon={{
								border: false,
								size: Sizes.XXXLarge,
								symbol: 'ac_satellite',
							}}
							spacer
							title={Liferay.Language.get(
								'no-individuals-synced-from-data-sources'
							)}
						/>
					)}
					rowIdentifier="fieldName"
					showFilterAndOrder={false}
					showPagination={false}
				/>
			</Card>

			{attributesModal.open && selectedAttribute && (
				<IndividualAttributesModal
					dataSources={selectedAttribute.dataSources}
					fieldName={selectedAttribute.fieldName}
					observer={attributesModal.observer}
				/>
			)}
		</BasePage>
	);
};

export default IndividualAttributes;
