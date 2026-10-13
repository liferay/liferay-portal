import BaseEditPage from 'shared/components/base-edit-page';
import ClayButton from '@clayui/button';
import React from 'react';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';

interface IToolbarProps {
	channelId: string;
	groupId: string;
	id: string;
	onDeleteSegment?: () => void;
	valid: boolean;
}

export const Toolbar: React.FC<IToolbarProps> = ({
	channelId,
	groupId,
	id,
	onDeleteSegment,
	valid,
}) => {
	const cancelURL = id
		? toRoute(Routes.CONTACTS_SEGMENT, {channelId, groupId, id})
		: toRoute(Routes.CONTACTS_LIST_SEGMENT, {
				channelId,
				groupId,
				type: SEGMENTS,
			});

	return (
		<BaseEditPage.Toolbar
			backURL={cancelURL}
			title={
				id
					? Liferay.Language.get('edit-segment')
					: Liferay.Language.get('new-segment')
			}
		>
			{id && onDeleteSegment && (
				<>
					<BaseEditPage.Toolbar.Item>
						<ClayButton
							borderless
							className="rounded-lg"
							displayType="secondary"
							onClick={onDeleteSegment}
							size="sm"
							type="button"
						>
							{Liferay.Language.get('delete-segment')}
						</ClayButton>
					</BaseEditPage.Toolbar.Item>

					<BaseEditPage.Toolbar.Divider />
				</>
			)}

			<BaseEditPage.Toolbar.Cancel href={cancelURL} />

			<BaseEditPage.Toolbar.Save
				disabled={!valid}
				label={Liferay.Language.get('save-segment')}
				type="submit"
			/>
		</BaseEditPage.Toolbar>
	);
};

export default Toolbar;
