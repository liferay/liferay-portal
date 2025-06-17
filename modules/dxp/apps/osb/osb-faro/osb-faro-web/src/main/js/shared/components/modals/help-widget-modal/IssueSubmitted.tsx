import ClayIcon from '@clayui/icon';
import Modal from 'shared/components/modal';
import React from 'react';
import {IHelpWidgetScreenProps} from './types';

const IssueSubmitted: React.FC<IHelpWidgetScreenProps> = ({onClose}) => (
	<>
		<Modal.Header
			onClose={onClose}
			title={Liferay.Language.get('issue-submitted')}
		/>

		<Modal.Body className='d-flex flex-column align-items-center'>
			<ClayIcon
				className='icon-root icon-size-xxxl my-5'
				symbol='ac_no_sites'
			/>

			<h3>{Liferay.Language.get('message-received')}</h3>

			<p className='description'>
				{Liferay.Language.get(
					'thanks-for-your-contribution-well-look-in-to-this-as-soon-as-possible'
				)}
			</p>
		</Modal.Body>
	</>
);

export default IssueSubmitted;
