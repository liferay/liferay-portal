import ClayModal from '@clayui/modal';
import ClayIcon from 'shared/components/Icon';
import React from 'react';
import {IHelpWidgetScreenProps} from './types';
import {Text} from '@clayui/core';

const IssueSubmitted: React.FC<IHelpWidgetScreenProps> = () => (
	<>
		<ClayModal.Header>
			{Liferay.Language.get('issue-submitted')}
		</ClayModal.Header>

		<ClayModal.Body className="d-flex flex-column align-items-center">
			<ClayIcon
				className="icon-root icon-size-xxxl my-5"
				symbol="ac_no_sites"
			/>

			<p>
				<Text size={6} weight="bold">
					{Liferay.Language.get('message-received')}
				</Text>
			</p>

			<p className="text-secondary">
				{Liferay.Language.get(
					'thanks-for-your-contribution-well-look-in-to-this-as-soon-as-possible'
				)}
			</p>
		</ClayModal.Body>
	</>
);

export default IssueSubmitted;
