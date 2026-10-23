import PropertyConfirmationModal, {
	IPropertyConfirmationModalProps,
} from './PropertyConfirmationModal';
import React from 'react';
import {sub} from 'shared/util/lang';

interface IDeletePropertyModalProps extends IPropertyConfirmationModalProps {
	name: string;
}

const DeletePropertyModal: React.FC<IDeletePropertyModalProps> = ({
	name,
	...otherProps
}) => (
	<PropertyConfirmationModal
		{...otherProps}
		confirmationText={
			sub(Liferay.Language.get('delete-x'), [name]) as string
		}
		deleteButtonLabel={Liferay.Language.get('delete')}
		instruction={
			sub(
				Liferay.Language.get(
					'to-delete-x,-copy-the-sentence-below-to-confirm-your-intention-to-delete-property'
				),
				[name]
			) as string
		}
		title={sub(Liferay.Language.get('delete-x?'), [name]) as string}
	/>
);

export default DeletePropertyModal;
