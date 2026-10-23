import ClayIcon from 'shared/components/Icon';
import ClayModal, {useModal} from '@clayui/modal';
import Loading from 'shared/components/Loading';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';

interface ILoadingModalProps {
	className?: string;
	icon?: string;
	message?: string;
	observer: Observer;
	title?: string;
}

const LoadingModal: React.FC<ILoadingModalProps> = ({
	className,
	icon,
	message = Liferay.Language.get('loading'),
	observer,
	title,
}) => (
	<ClayModal
		className={className}
		disableAutoClose
		observer={observer}
		size="sm"
	>
		<ClayModal.Body className="align-items-center d-flex flex-column">
			{title && <h1 className="h4 my-3 text-secondary">{title}</h1>}

			<div className="icon-container">
				{icon ? (
					<ClayIcon
						className="icon-root icon-size-xl text-secondary"
						symbol={icon}
					/>
				) : (
					<Loading />
				)}
			</div>

			{message && <p className="text-center">{message}</p>}
		</ClayModal.Body>
	</ClayModal>
);

/**
 * A loading modal that is open for as long as it is mounted, for the class
 * components that cannot use the `useModal` hook.
 */
export const LoadingDialog: React.FC<Omit<ILoadingModalProps, 'observer'>> = (
	props
) => {
	const {observer} = useModal({defaultOpen: true});

	return <LoadingModal {...props} observer={observer} />;
};

export default LoadingModal;
