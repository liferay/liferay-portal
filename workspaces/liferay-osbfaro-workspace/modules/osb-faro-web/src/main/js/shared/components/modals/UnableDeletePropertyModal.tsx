import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import Modal from 'shared/components/modal';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {sub} from 'shared/util/lang';

const UnableDeletePropertyModal: React.FC<{
	onClose: () => void;
}> = ({onClose}) => (
	<Modal type="danger">
		<Modal.Header
			onClose={onClose}
			title={Liferay.Language.get('unable-to-delete-property')}
		/>

		<Modal.Body inlineScroller>
			<p className="text-secondary">
				{sub(
					Liferay.Language.get(
						'ensure-no-sites-are-assigned-to-it-before-deleting-a-property'
					),
					[
						<ClayLink
							href={URLConstants.DeletePropertyDocumentation}
							key="URL"
							target="_blank"
						>
							{Liferay.Language.get(
								'access-our-documentation-to-learn-more'
							)}
						</ClayLink>,
					],
					false
				)}
			</p>
		</Modal.Body>

		<Modal.Footer>
			<ClayButton
				className="button-root"
				displayType="danger"
				onClick={onClose}
			>
				{Liferay.Language.get('done')}
			</ClayButton>
		</Modal.Footer>
	</Modal>
);

export default UnableDeletePropertyModal;
