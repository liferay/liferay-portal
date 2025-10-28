import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import Loading, {Align} from 'shared/components/Loading';
import Modal from 'shared/components/modal';
import React, {useState} from 'react';
import Table from 'shared/components/table';
import {
	ActionType,
	useUnassignedSegmentsContext
} from 'shared/context/unassignedSegments';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {Option, Picker} from '@clayui/core';
import {partition} from 'lodash';
import {Segment} from 'shared/util/records';
import {sequence} from 'shared/util/promise';
import {useChannelContext} from 'shared/context/channel';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

const DELETE_OPTION = {
	label: Liferay.Language.get('delete'),
	value: 'DELETE'
};

const UNASSIGNED_OPTION = {
	label: Liferay.Language.get('unassigned'),
	value: ''
};

const channelMappingsToArray = (
	obj: IChannelMappings
): Array<{channelId: string; id: string}> => {
	const arr = [];

	for (const id in obj) {
		if ({}.hasOwnProperty.call(obj, id)) {
			arr.push({channelId: obj[id], id});
		}
	}

	return arr;
};

interface IChannelMappings {
	[segmentId: string]: string;
}

interface IAssignSegmentsProps {
	groupId?: string;
	onClose: () => void;
}

const AssignSegments: React.FC<IAssignSegmentsProps> = ({groupId, onClose}) => {
	const {onOrderIOMapChange, orderIOMap} = useStatefulPagination(null, {
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {channels} = useChannelContext();

	const {
		unassignedSegments,
		unassignedSegmentsDispatch
	} = useUnassignedSegmentsContext();

	const [channelMappings, setChannelMappings] = useState(
		unassignedSegments.reduce(
			(p: IChannelMappings, {id}: Segment): IChannelMappings => ({
				...p,
				[id]: ''
			}),
			{}
		)
	);

	const [isSubmitting, setIsSubmitting] = useState(false);

	const selectOptions = [
		UNASSIGNED_OPTION,
		DELETE_OPTION,
		...channels.map(({id, name}) => ({
			label: name,
			value: id
		}))
	];

	const updateSegment = (segmentId: string, value: string) => {
		setChannelMappings({...channelMappings, [segmentId]: value});
	};

	const ChannelSelect = ({data: {id}, options}) => (
		<td>
			<Picker
				data-testid={`select-${id}`}
				items={options as {label: string; value: string}[]}
				onSelectionChange={selectedValue =>
					updateSegment(id, selectedValue)
				}
				required
				selectedKey={channelMappings[id]}
			>
				{({label, value}) => <Option key={value}>{label}</Option>}
			</Picker>
		</td>
	);

	const handleSubmit = async () => {
		setIsSubmitting(true);

		const segments = channelMappingsToArray(channelMappings);

		const [toUpdate, toKeep] = partition(
			segments,
			({channelId}) => !!channelId
		);

		const segmentsFn = toUpdate.map(({channelId, id}) => {
			if (channelId === DELETE_OPTION.value) {
				return () => API.individualSegment.delete({groupId, ids: [id]});
			}

			return () =>
				API.individualSegment.updateChannel({
					channelId,
					groupId,
					id
				});
		});

		try {
			await sequence(segmentsFn)();
		} finally {
			const segmentIdsToKeep = toKeep.map(({id}) => id);

			unassignedSegmentsDispatch({
				payload: unassignedSegments.filter(({id}) =>
					segmentIdsToKeep.includes(id)
				),
				type: ActionType.setSegments
			});

			setIsSubmitting(false);

			onClose();
		}
	};

	const isValid = () => {
		for (const id in channelMappings) {
			if (
				{}.hasOwnProperty.call(channelMappings, id) &&
				channelMappings[id]
			) {
				return true;
			}
		}

		return false;
	};

	return (
		<div className='assign-segments'>
			<Modal.Body className='d-flex flex-column align-items-center'>
				<h2>{Liferay.Language.get('assign-existing-segments')}</h2>

				<span className='subtitle'>
					{Liferay.Language.get(
						'your-existing-segments-will-be-hidden-until-they-have-been-assigned.-a-segment-can-only-belong-to-a-single-property'
					)}
				</span>

				<div className='assign-segments-table-wrapper'>
					<Table
						columns={[
							{
								accessor: 'name',
								label: Liferay.Language.get('segment-name')
							},
							{
								accessor: 'userName',
								label: Liferay.Language.get('created-by')
							},
							{
								accessor: 'selectChannel',
								cellRenderer: ChannelSelect,
								cellRendererProps: {
									options: selectOptions
								},
								sortable: false
							}
						]}
						internalSort
						items={unassignedSegments}
						onOrderIOMapChange={onOrderIOMapChange}
						orderIOMap={orderIOMap}
						rowIdentifier='id'
					/>
				</div>
			</Modal.Body>

			<Modal.Footer className='d-flex justify-content-end'>
				<ClayButton
					disabled={isSubmitting}
					displayType='secondary'
					onClick={onClose}
				>
					{Liferay.Language.get('skip-for-now')}
				</ClayButton>

				<ClayButton
					data-testid='submit-button'
					disabled={isSubmitting || !isValid()}
					displayType='primary'
					onClick={handleSubmit}
				>
					{isSubmitting && <Loading align={Align.Left} />}

					{isSubmitting
						? Liferay.Language.get('saving')
						: Liferay.Language.get('done')}
				</ClayButton>
			</Modal.Footer>
		</div>
	);
};

export default AssignSegments;
