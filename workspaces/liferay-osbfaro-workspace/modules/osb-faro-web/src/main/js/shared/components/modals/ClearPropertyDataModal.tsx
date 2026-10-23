import PropertyConfirmationModal, {
	IPropertyConfirmationModalProps,
} from './PropertyConfirmationModal';
import React from 'react';
import {sub} from 'shared/util/lang';

interface IClearPropertyDataModalProps extends IPropertyConfirmationModalProps {
	name: string;
}

const ClearPropertyDataModal: React.FC<IClearPropertyDataModalProps> = ({
	name,
	...otherProps
}) => (
	<PropertyConfirmationModal
		{...otherProps}
		confirmationText={
			sub(Liferay.Language.get('clear-x'), [name]) as string
		}
		deleteButtonLabel={Liferay.Language.get('clear-data')}
		instruction={
			sub(
				Liferay.Language.get(
					'to-clear-data-from-x,-copy-the-sentence-below-to-confirm-your-intention-to-clear-data-from-this-property'
				),
				[name]
			) as string
		}
		title={sub(Liferay.Language.get('clear-x-data?'), [name]) as string}
	/>
);

export default ClearPropertyDataModal;
