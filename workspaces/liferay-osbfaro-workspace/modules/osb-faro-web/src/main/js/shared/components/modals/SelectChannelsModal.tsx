import * as API from 'shared/api';
import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import Modal from 'shared/components/modal';
import NoResultsDisplay from '../NoResultsDisplay';
import React, {useEffect, useRef, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {Sizes} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';
import {
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

export interface ISelectableChannel {
	commerceChannelsCount: number;
	groupsCount: number;
	id: string;
	name: string;
}

interface ISelectChannelsModalProps {

	/**
	 * Opt in preselection. When given, the channels matching it are selected
	 * on the first page of results. The modal itself has no opinion on which
	 * channels matter, so each caller decides whether to preselect anything.
	 */
	autoSelectFilter?: (channel: ISelectableChannel) => boolean;
	groupId: string;
	onClose: () => {};
	onSelect: (channels: string[]) => {};
	initialItems: string[];
}

const SelectChannelsModal: React.FC<ISelectChannelsModalProps> = ({
	autoSelectFilter,

	/**
	 * const {groupId} = useParams() doesn't work on Modals
	 */
	groupId,
	initialItems = [],
	onClose,
	onSelect,
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {
		delta,
		onDeltaChange,
		onOrderIOMapChange,
		onPageChange,
		onQueryChange,
		orderIOMap,
		page,
		query,
	} = useStatefulPagination(undefined, {
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data, error, loading} = useRequest({
		dataSourceFn: API.channels.search,
		variables: {
			cur: page,
			delta,
			groupId,
			orderIOMap,
			query,
		},
	});

	const [showAlert, setShowAlert] = useState(false);
	const hasAutoSelectedRef = useRef(false);

	useEffect(() => {
		if (selectedItems.keySeq().toArray().length) {
			setShowAlert(false);
		}
	}, [selectedItems]);

	useEffect(() => {
		if (initialItems.length) {
			selectionDispatch?.({
				payload: {items: initialItems.map((id) => ({id}))},
				type: 'add',
			});
		}
	}, [initialItems]);

	useEffect(() => {
		if (!autoSelectFilter || !data?.items || hasAutoSelectedRef.current) {
			return;
		}

		hasAutoSelectedRef.current = true;

		const autoSelectedChannels = data.items.filter(
			(channel: ISelectableChannel) =>
				autoSelectFilter(channel) && !initialItems.includes(channel.id)
		);

		if (autoSelectedChannels.length) {
			selectionDispatch?.({
				payload: {items: autoSelectedChannels},
				type: 'add',
			});
		}
	}, [data]);

	return (
		<Modal size="lg">
			<Modal.Header
				onClose={onClose}
				title={Liferay.Language.get('select-properties')}
			/>

			<ClayForm
				onSubmit={(event) => {
					event.preventDefault();

					if (selectedItems.isEmpty()) {
						setShowAlert(true);

						return;
					}

					onSelect(selectedItems.keySeq().toArray());

					onClose();
				}}
			>
				<Modal.Body className="p-0" inlineScroller>
					{showAlert && (
						<div className="px-4">
							<ClayAlert
								displayType="danger"
								onClose={() => setShowAlert(false)}
								title={Liferay.Language.get('error')}
							>
								{Liferay.Language.get(
									'unable-to-perform-this-action.-select-a-property-to-proceed'
								)}
							</ClayAlert>
						</div>
					)}

					<CrossPageSelect
						columns={[
							{
								accessor: NAME,
								className: 'table-cell-expand',
								label: Liferay.Language.get(
									'available-properties'
								),
								title: true,
							},
							{
								accessor: 'commerceChannelsCount',
								className: 'text-right',
								label: Liferay.Language.get(
									'dxp-commerce-channels'
								),
								sortable: false,
							},
							{
								accessor: 'groupsCount',
								className: 'text-right',
								label: Liferay.Language.get('sites'),
								sortable: false,
							},
						]}
						delta={delta}
						entityLabel={Liferay.Language.get('properties')}
						error={error}
						items={data?.items}
						loading={loading}
						noResultsRenderer={
							<NoResultsDisplay
								description={
									<>
										{Liferay.Language.get(
											'go-to-properties-under-workspace-settings-to-create-a-property'
										)}

										<ClayLink
											className="d-block mb-3"
											href={URLConstants.CreateProperty}
											key="DOCUMENTATION"
											target="_blank"
										>
											{Liferay.Language.get(
												'access-our-documentation-to-learn-more'
											)}
										</ClayLink>
									</>
								}
								icon={{
									border: false,
									size: Sizes.XXXLarge,
									symbol: 'ac_satellite',
								}}
								title={Liferay.Language.get(
									'no-properties-were-found'
								)}
							/>
						}
						onDeltaChange={onDeltaChange}
						onOrderIOMapChange={onOrderIOMapChange}
						onPageChange={onPageChange}
						onQueryChange={onQueryChange}
						orderByOptions={[
							{
								label: Liferay.Language.get('property-name'),
								value: NAME,
							},
						]}
						orderIOMap={orderIOMap}
						page={page}
						query={query}
						rowIdentifier="id"
						showCheckbox
						total={data?.total}
					/>
				</Modal.Body>

				<Modal.Footer>
					<ClayButton displayType="secondary" onClick={onClose}>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton displayType="primary" type="submit">
						{Liferay.Language.get('select')}
					</ClayButton>
				</Modal.Footer>
			</ClayForm>
		</Modal>
	);
};

export default withSelectionProvider(SelectChannelsModal);
