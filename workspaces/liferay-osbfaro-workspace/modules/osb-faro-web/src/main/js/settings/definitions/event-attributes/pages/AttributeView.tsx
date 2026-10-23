import BasePage from 'settings/components/base-page/BasePage';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import EditAttributeEventModal from 'shared/components/modals/EditAttributeEventModal';
import EVENT_ATTRIBUTE_DEFINITION_QUERY, {
	EVENT_ATTRIBUTE_DEFINITION_WITH_RECENT_VALUES_QUERY,
	EventAttributeDefinitionData,
	EventAttributeDefinitionVariables,
	UPDATE_EVENT_ATTRIBUTE_DEFINITION,
} from 'event-analysis/queries/EventAttributeDefinitionQuery';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {Attribute} from 'event-analysis/utils/types';
import {capitalize} from 'lodash';
import {DateCell} from 'shared/components/table/cell-components';
import {getDefinitions, getEventAttributes} from 'shared/util/breadcrumbs';
import {getSafeDisplayValue} from 'shared/util/util';
import {SafeResults} from 'shared/hoc/util';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

interface IAttributeViewProps extends React.HTMLAttributes<HTMLElement> {
	attributeId: string;
	groupId: string;
}

const AttributeView: React.FC<IAttributeViewProps> = ({
	attributeId,
	groupId,
}) => {
	const result = useQuery<
		EventAttributeDefinitionData,
		EventAttributeDefinitionVariables
	>(EVENT_ATTRIBUTE_DEFINITION_WITH_RECENT_VALUES_QUERY, {
		variables: {id: attributeId},
	});

	const editModal = useModal();

	const viewAttributePageActions = [
		{
			label: Liferay.Language.get('edit'),
			onClick: () => editModal.onOpenChange(true),
		},
	];

	return (
		<>
			<SafeResults {...result}>
				{({
					eventAttributeDefinition: {
						dataType,
						description,
						displayName,
						name,
						recentValues,
					},
				}: {
					eventAttributeDefinition: Attribute;
				}) => (
					<BasePage
						breadcrumbItems={[
							getDefinitions({groupId}),
							getEventAttributes({groupId}),
							{active: true, label: name},
						]}
						pageActions={viewAttributePageActions}
						pageDescription={
							<>
								{description ? (
									<div>{description}</div>
								) : (
									<div className="no-description">
										{Liferay.Language.get('no-description')}
									</div>
								)}

								<ClayLabel displayType="secondary" inverse>
									{capitalize(dataType)}
								</ClayLabel>
							</>
						}
						pageTitle={name}
						subTitle={displayName}
					>
						<StatesRenderer empty={!recentValues?.length}>
							<StatesRenderer.Empty
								className="bg-white rounded"
								description={
									<>
										{Liferay.Language.get(
											'you-can-come-back-later-and-check-if-there-is-any-data-received-from-your-events'
										)}

										<ClayLink
											className="d-block mb-3"
											href={
												URLConstants.EventAttributesDocumentation
											}
											key="DOCUMENTATION"
											target="_blank"
										>
											{Liferay.Language.get(
												'learn-more-about-event-tracking'
											)}
										</ClayLink>
									</>
								}
								spacer
								title={Liferay.Language.get(
									'no-sample-data-found'
								)}
							/>

							<StatesRenderer.Success>
								<Table
									columns={[
										{
											accessor: 'value',
											className:
												'table-cell-expand-small text-truncate',
											dataFormatter: (value) =>
												getSafeDisplayValue(value),
											label: Liferay.Language.get(
												'sample-raw-data'
											),
											sortable: false,
										},
										{
											accessor: 'lastSeenDate',
											cellRenderer: ({data}) => (
												<DateCell
													className="table-column-text-end"
													data={data}
													datePath="lastSeenDate"
												/>
											),
											className: 'table-column-text-end',
											label: Liferay.Language.get(
												'last-seen'
											),
											sortable: false,
										},
									]}
									items={recentValues ?? []}
									rowIdentifier="value"
								/>
							</StatesRenderer.Success>
						</StatesRenderer>
					</BasePage>
				)}
			</SafeResults>

			{editModal.open && (
				<EditAttributeEventModal
					id={attributeId}
					mutation={UPDATE_EVENT_ATTRIBUTE_DEFINITION}
					observer={editModal.observer}
					onClose={() => editModal.onOpenChange(false)}
					query={EVENT_ATTRIBUTE_DEFINITION_QUERY}
					showTypecast
				/>
			)}
		</>
	);
};

export default AttributeView;
