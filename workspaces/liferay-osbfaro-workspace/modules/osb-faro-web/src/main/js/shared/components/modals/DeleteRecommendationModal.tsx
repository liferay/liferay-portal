import ConfirmationModal from 'shared/components/ConfirmationModal';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {sub} from 'shared/util/lang';

interface IDeleteRecommendationModalProps {
	description: string;
	multiple?: boolean;
	name: string;
	observer: Observer;
	onClose: () => void;
	onSubmit: () => void;
}

const DeleteRecommendationModal: React.FC<IDeleteRecommendationModalProps> = ({
	description,
	multiple = false,
	name,
	observer,
	onClose,
	onSubmit,
}) => (
	<ConfirmationModal
		message={
			<div>
				<div className="h4 text-secondary">{description}</div>

				<p>
					{multiple
						? Liferay.Language.get(
								'components-using-these-models-will-need-to-be-reconfigured'
							)
						: Liferay.Language.get(
								'components-using-this-model-will-need-to-be-reconfigured'
							)}
				</p>
			</div>
		}
		observer={observer}
		onClose={onClose}
		onSubmit={onSubmit}
		submitButtonDisplay="warning"
		submitMessage={Liferay.Language.get('delete')}
		title={sub(Liferay.Language.get('deleting-x'), [name])}
		type="warning"
	/>
);

export default DeleteRecommendationModal;
